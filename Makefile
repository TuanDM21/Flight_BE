# Makefile for Flight Management System Backend

# Variables
COMPOSE_FILE_PROD = docker-compose.yml
COMPOSE_FILE_LOCAL = docker-compose.local.yml
ENV_FILE = .env

.PHONY: help up-local up run-local run-prod stop-local stop down-local down clean-local clean logs-local logs status restart-local restart test mvn-clean mvn-compile mvn-test mvn-package mvn-install ssl-cert db-migrate-local db-migrate db-info-local db-info db-clean-local db-clean db-validate-local db-validate db-repair-local db-repair

# Default target
.DEFAULT_GOAL := help

help: ## Show this help message
	@echo "Flight Management System Backend - Available Commands"
	@echo ""
	@echo "🚀 Development Environment Commands:"
	@echo "  make up-local      - Start development environment (with build)"
	@echo "  make stop-local    - Stop development containers"
	@echo "  make down-local    - Stop and remove development containers"
	@echo "  make restart-local - Restart development containers"
	@echo "  make clean-local   - Clean up development containers, images and volumes"
	@echo "  make logs-local    - Show logs from development containers"
	@echo ""
	@echo "🚀 Production Environment Commands:"
	@echo "  make up            - Start production environment (with build)"
	@echo "  make stop          - Stop production containers"
	@echo "  make down          - Stop and remove production containers"
	@echo "  make restart       - Restart production containers"
	@echo "  make clean         - Clean up production containers, images and volumes"
	@echo "  make logs          - Show logs from production containers"
	@echo "  make status        - Show status of all containers"
	@echo ""
	@echo "🛠️  Maven Commands:"
	@echo "  make mvn-clean   - Clean Maven build artifacts"
	@echo "  make mvn-compile - Compile the project"
	@echo "  make mvn-test    - Run unit tests"
	@echo "  make mvn-package - Package the application"
	@echo "  make mvn-install - Install dependencies and build"
	@echo "  make test        - Run tests (alias for mvn-test)"
	@echo ""
	@echo "🗄️  Development Database Commands:"
	@echo "  make db-migrate-local  - Run Flyway migrations on development"
	@echo "  make db-info-local     - Show development migration status"
	@echo "  make db-clean-local    - Clean development database (DANGEROUS)"
	@echo "  make db-validate-local - Validate development migrations"
	@echo "  make db-repair-local   - Repair development Flyway schema history"
	@echo ""
	@echo "🗄️  Production Database Commands:"
	@echo "  make db-migrate-prod  - Run Flyway migrations on production"
	@echo "  make db-info-prod     - Show production migration status"
	@echo "  make db-clean-prod    - Clean production database (DANGEROUS)"
	@echo "  make db-validate-prod - Validate production migrations"
	@echo "  make db-repair-prod   - Repair production Flyway schema history"
	@echo ""
	@echo "🔐 SSL Commands:"
	@echo "  make ssl-cert    - Generate SSL certificates"
	@echo ""

up-local: ## Start development environment
	@echo "🚀 Starting Development Environment..."
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "❌ .env file not found! Please create one."; \
		exit 1; \
	fi
	@docker compose -f $(COMPOSE_FILE_LOCAL) up --build -d
	@echo "✅ Development environment started"
	@echo "🌐 Application: http://localhost:8080"
	@echo "🔄 LiveReload: http://localhost:35729"
	@echo "🗄️  MySQL: localhost:3306"

up: ## Start production environment
	@echo "🚀 Starting Production Environment..."
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "❌ .env file not found! Please create one."; \
		exit 1; \
	fi
	@docker compose -f $(COMPOSE_FILE_PROD) up --build -d
	@echo "✅ Production environment started"
	@echo "🌐 Application: http://localhost:8080"
	@echo "🌐 HTTPS: https://localhost:8443"
	@echo "🗄️  MySQL: localhost:3306"


stop-local: ## Stop development containers
	@echo "🛑 Stopping development containers..."
	@docker compose -f $(COMPOSE_FILE_LOCAL) stop
	@echo "✅ Development containers stopped"

stop: ## Stop production containers
	@echo "🛑 Stopping production containers..."
	@docker compose -f $(COMPOSE_FILE_PROD) stop
	@echo "✅ Production containers stopped"

down-local: ## Stop and remove development containers
	@echo "⬇️  Stopping and removing development containers..."
	@docker compose -f $(COMPOSE_FILE_LOCAL) down --remove-orphans
	@echo "✅ Development containers stopped and removed"

down: ## Stop and remove production containers
	@echo "⬇️  Stopping and removing production containers..."
	@docker compose -f $(COMPOSE_FILE_PROD) down --remove-orphans
	@echo "✅ Production containers stopped and removed"

restart-local: ## Restart development containers
	@echo "🔄 Restarting development containers..."
	@$(MAKE) stop-local
	@$(MAKE) up-local
	@echo "✅ Development containers restarted"

restart: ## Restart production containers
	@echo "🔄 Restarting production containers..."
	@$(MAKE) stop
	@$(MAKE) up
	@echo "✅ Production containers restarted"

clean-local: ## Clean up development containers, images and volumes
	@echo "🧹 Cleaning up development environment..."
	@docker compose -f $(COMPOSE_FILE_LOCAL) down -v --remove-orphans
	@docker system prune -f
	@docker volume prune -f
	@echo "✅ Development cleanup completed"

clean: ## Clean up production containers, images and volumes
	@echo "🧹 Cleaning up production environment..."
	@docker compose -f $(COMPOSE_FILE_PROD) down -v --remove-orphans
	@docker image rm flight-mnm-backend:latest 2>/dev/null || true
	@docker system prune -f
	@docker volume prune -f
	@echo "✅ Production cleanup completed"

logs-local: ## Show logs from development containers
	@echo "📋 Showing development container logs..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_LOCAL) ps -q 2>/dev/null)" ]; then \
		docker compose -f $(COMPOSE_FILE_LOCAL) logs -f --tail=100; \
	else \
		echo "❌ No running development containers found"; \
	fi

logs: ## Show logs from production containers
	@echo "📋 Showing production container logs..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q 2>/dev/null)" ]; then \
		docker compose -f $(COMPOSE_FILE_PROD) logs -f --tail=100; \
	else \
		echo "❌ No running production containers found"; \
	fi

status: ## Show status of all containers
	@echo "📊 Container Status:"
	@echo ""
	@echo "Production Environment:"
	@docker compose -f $(COMPOSE_FILE_PROD) ps
	@echo ""
	@echo "Development Environment:"
	@docker compose -f $(COMPOSE_FILE_LOCAL) ps

# Maven Commands
mvn-clean: ## Clean Maven build artifacts
	@echo "🧹 Cleaning Maven artifacts..."
	@./mvnw clean
	@echo "✅ Maven clean completed"

mvn-compile: ## Compile the project
	@echo "🔨 Compiling project..."
	@./mvnw compile
	@echo "✅ Compilation completed"

# Database Migration Commands
db-migrate-local: ## Run Flyway migrations on development environment
	@echo "🗄️  Running database migrations on development environment..."
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "❌ .env file not found! Please create one (see .env.example)."; \
		exit 1; \
	fi
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_LOCAL) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using development environment"; \
		docker compose -f $(COMPOSE_FILE_LOCAL) --profile migration run --rm migration flyway migrate; \
	else \
		echo "❌ No MariaDB container found for development environment. Please start it first with 'make up-local'"; \
		exit 1; \
	fi
	@echo "✅ Development migrations completed"

db-migrate: ## Run Flyway migrations on production environment
	@echo "🗄️  Running database migrations on production environment..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using production environment"; \
		docker compose -f $(COMPOSE_FILE_PROD) --profile migration run --rm migration flyway migrate; \
	else \
		echo "❌ No MariaDB container found for production environment. Please start it first with 'make up'"; \
		exit 1; \
	fi
	@echo "✅ Production migrations completed"

db-info-local: ## Show migration status for development environment
	@echo "📊 Database migration status for development environment:"
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "❌ .env file not found! Please create one (see .env.example)."; \
		exit 1; \
	fi
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_LOCAL) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using development environment"; \
		docker compose -f $(COMPOSE_FILE_LOCAL) --profile migration run --rm migration flyway info; \
	else \
		echo "❌ No MariaDB container found for development environment. Please start it first with 'make up-local'"; \
		exit 1; \
	fi

db-info: ## Show migration status for production environment
	@echo "📊 Database migration status for production environment:"
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using production environment"; \
		docker compose -f $(COMPOSE_FILE_PROD) --profile migration run --rm migration flyway info; \
	else \
		echo "❌ No MariaDB container found for production environment. Please start it first with 'make up'"; \
		exit 1; \
	fi

db-clean-local: ## Clean development database (DANGEROUS - will drop all objects)
	@echo "⚠️  WARNING: This will DROP ALL database objects in development environment!"
	@read -p "Are you sure? Type 'yes' to continue: " confirm && [ "$$confirm" = "yes" ] || exit 1
	@echo "🧹 Cleaning development database..."
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "❌ .env file not found! Please create one (see .env.example)."; \
		exit 1; \
	fi
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_LOCAL) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using development environment"; \
		docker compose -f $(COMPOSE_FILE_LOCAL) --profile migration run --rm migration flyway clean; \
	else \
		echo "❌ No MariaDB container found for development environment. Please start it first"; \
		exit 1; \
	fi
	@echo "✅ Development database cleaned"

db-clean: ## Clean production database (DANGEROUS - will drop all objects)
	@echo "⚠️  WARNING: This will DROP ALL database objects in production environment!"
	@read -p "Are you sure? Type 'yes' to continue: " confirm && [ "$$confirm" = "yes" ] || exit 1
	@echo "🧹 Cleaning production database..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using production environment"; \
		docker compose -f $(COMPOSE_FILE_PROD) --profile migration run --rm migration flyway clean; \
	else \
		echo "❌ No MariaDB container found for production environment. Please start it first"; \
		exit 1; \
	fi
	@echo "✅ Production database cleaned"

db-validate-local: ## Validate development migrations
	@echo "✅ Validating development migrations..."
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "❌ .env file not found! Please create one (see .env.example)."; \
		exit 1; \
	fi
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_LOCAL) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using development environment"; \
		docker compose -f $(COMPOSE_FILE_LOCAL) --profile migration run --rm migration flyway validate; \
	else \
		echo "❌ No MariaDB container found for development environment. Please start it first"; \
		exit 1; \
	fi
	@echo "✅ Development validation completed"

db-validate: ## Validate production migrations
	@echo "✅ Validating production migrations..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using production environment"; \
		docker compose -f $(COMPOSE_FILE_PROD) --profile migration run --rm migration flyway validate; \
	else \
		echo "❌ No MariaDB container found for production environment. Please start it first"; \
		exit 1; \
	fi
	@echo "✅ Production validation completed"

db-repair-local: ## Repair Flyway schema history for development environment
	@echo "🔧 Repairing Flyway schema history for development environment..."
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "❌ .env file not found! Please create one (see .env.example)."; \
		exit 1; \
	fi
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_LOCAL) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using development environment"; \
		docker compose -f $(COMPOSE_FILE_LOCAL) --profile migration run --rm migration flyway repair; \
	else \
		echo "❌ No MariaDB container found for development environment. Please start it first"; \
		exit 1; \
	fi
	@echo "✅ Development repair completed"

db-repair: ## Repair Flyway schema history for production environment
	@echo "🔧 Repairing Flyway schema history for production environment..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using production environment"; \
		docker compose -f $(COMPOSE_FILE_PROD) --profile migration run --rm migration flyway repair; \
	else \
		echo "❌ No MariaDB container found for production environment. Please start it first"; \
		exit 1; \
	fi
	@echo "✅ Production repair completed"
