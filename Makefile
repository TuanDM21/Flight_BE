# Makefile for Flight Management System Backend

# Variables
COMPOSE_FILE_PROD = docker-compose.yml
COMPOSE_FILE_DEV = docker-compose.dev.yml
ENV_FILE = .env

.PHONY: help run-dev run-prod build-dev build-prod stop-dev stop-prod down-dev down-prod clean-dev clean-prod logs-dev logs-prod status restart-dev restart-prod test mvn-clean mvn-compile mvn-test mvn-package mvn-install ssl-cert db-migrate-dev db-migrate-prod db-info-dev db-info-prod db-clean-dev db-clean-prod db-validate-dev db-validate-prod db-repair-dev db-repair-prod

# Default target
.DEFAULT_GOAL := help

help: ## Show this help message
	@echo "⚠️  WARNING: This will DROP ALL database objects in development environment!"
	@read -p "Are you sure? Type 'yes' to continue: " confirm && [ "$$confirm" = "yes" ] || exit 1
	@echo "🧹 Cleaning development database..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_DEV) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using development environment"; \
		docker compose -f $(COMPOSE_FILE_DEV) --profile migration run --rm migration clean; \
	else \
		echo "❌ No MariaDB container found for development environment. Please start it first with 'make run-dev'"; \
		exit 1; \
	fi
	@echo "✅ Development database cleaned" clean-prod logs-dev logs-prod status restart-dev restart-prod test mvn-clean mvn-compile mvn-test mvn-package mvn-install ssl-cert db-migrate-dev db-migrate-prod db-info-dev db-info-prod db-clean-dev db-clean-prod db-validate-dev db-validate-prod db-repair-dev db-repair-prod

# Default target
.DEFAULT_GOAL := help

help: ## Show this help message
	@echo "Flight Management System Backend - Available Commands"
	@echo ""
	@echo "🚀 Development Environment Commands:"
	@echo "  make run-dev     - Start development environment"
	@echo "  make build-dev   - Build development Docker images"
	@echo "  make stop-dev    - Stop development containers"
	@echo "  make down-dev    - Stop and remove development containers"
	@echo "  make restart-dev - Restart development containers"
	@echo "  make clean-dev   - Clean up development containers, images and volumes"
	@echo "  make logs-dev    - Show logs from development containers"
	@echo ""
	@echo "🚀 Production Environment Commands:"
	@echo "  make run-prod     - Start production environment"
	@echo "  make build-prod   - Build production Docker images"
	@echo "  make stop-prod    - Stop production containers"
	@echo "  make down-prod    - Stop and remove production containers"
	@echo "  make restart-prod - Restart production containers"
	@echo "  make clean-prod   - Clean up production containers, images and volumes"
	@echo "  make logs-prod    - Show logs from production containers"
	@echo "  make status       - Show status of all containers"
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
	@echo "  make db-migrate-dev  - Run Flyway migrations on development"
	@echo "  make db-info-dev     - Show development migration status"
	@echo "  make db-clean-dev    - Clean development database (DANGEROUS)"
	@echo "  make db-validate-dev - Validate development migrations"
	@echo "  make db-repair-dev   - Repair development Flyway schema history"
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

run-dev: ## Start development environment
	@echo "🚀 Starting Development Environment..."
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "❌ .env file not found! Please create one."; \
		exit 1; \
	fi
	@docker compose -f $(COMPOSE_FILE_DEV) up --build -d
	@echo "✅ Development environment started"
	@echo "🌐 Application: http://localhost:8080"
	@echo "🔄 LiveReload: http://localhost:35729"
	@echo "🗄️  MySQL: localhost:3306"

run-prod: ## Start production environment
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

build-dev: ## Build development Docker images
	@echo "🔨 Building development Docker images..."
	@docker compose -f $(COMPOSE_FILE_DEV) build
	@echo "✅ Development images built"

build-prod: ## Build production Docker images
	@echo "🔨 Building production Docker images..."
	@docker compose -f $(COMPOSE_FILE_PROD) build
	@echo "✅ Production images built"


stop-dev: ## Stop development containers
	@echo "🛑 Stopping development containers..."
	@docker compose -f $(COMPOSE_FILE_DEV) stop
	@echo "✅ Development containers stopped"

stop-prod: ## Stop production containers
	@echo "🛑 Stopping production containers..."
	@docker compose -f $(COMPOSE_FILE_PROD) stop
	@echo "✅ Production containers stopped"

down-dev: ## Stop and remove development containers
	@echo "⬇️  Stopping and removing development containers..."
	@docker compose -f $(COMPOSE_FILE_DEV) down --remove-orphans
	@echo "✅ Development containers stopped and removed"

down-prod: ## Stop and remove production containers
	@echo "⬇️  Stopping and removing production containers..."
	@docker compose -f $(COMPOSE_FILE_PROD) down --remove-orphans
	@echo "✅ Production containers stopped and removed"

restart-dev: ## Restart development containers
	@echo "🔄 Restarting development containers..."
	@$(MAKE) stop-dev
	@$(MAKE) run-dev
	@echo "✅ Development containers restarted"

restart-prod: ## Restart production containers
	@echo "🔄 Restarting production containers..."
	@$(MAKE) stop-prod
	@$(MAKE) run-prod
	@echo "✅ Production containers restarted"

clean-dev: ## Clean up development containers, images and volumes
	@echo "🧹 Cleaning up development environment..."
	@docker compose -f $(COMPOSE_FILE_DEV) down -v --remove-orphans
	@docker system prune -f
	@docker volume prune -f
	@echo "✅ Development cleanup completed"

clean-prod: ## Clean up production containers, images and volumes
	@echo "🧹 Cleaning up production environment..."
	@docker compose -f $(COMPOSE_FILE_PROD) down -v --remove-orphans
	@docker image rm flight-mnm-backend:latest 2>/dev/null || true
	@docker system prune -f
	@docker volume prune -f
	@echo "✅ Production cleanup completed"

logs-dev: ## Show logs from development containers
	@echo "📋 Showing development container logs..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_DEV) ps -q 2>/dev/null)" ]; then \
		docker compose -f $(COMPOSE_FILE_DEV) logs -f --tail=100; \
	else \
		echo "❌ No running development containers found"; \
	fi

logs-prod: ## Show logs from production containers
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
	@docker compose -f $(COMPOSE_FILE_DEV) ps

# Maven Commands
mvn-clean: ## Clean Maven build artifacts
	@echo "🧹 Cleaning Maven artifacts..."
	@./mvnw clean
	@echo "✅ Maven clean completed"

mvn-compile: ## Compile the project
	@echo "🔨 Compiling project..."
	@./mvnw compile
	@echo "✅ Compilation completed"

mvn-test: ## Run unit tests
	@echo "🧪 Running tests..."
	@./mvnw test
	@echo "✅ Tests completed"

test: mvn-test ## Run tests (alias)

mvn-package: ## Package the application
	@echo "📦 Packaging application..."
	@./mvnw package -DskipTests
	@echo "✅ Packaging completed"

mvn-install: ## Install dependencies and build
	@echo "⬇️  Installing dependencies and building..."
	@./mvnw clean install
	@echo "✅ Install completed"

# SSL Commands
ssl-cert: ## Generate SSL certificates
	@echo "🔐 Generating SSL certificates..."
	@chmod +x scripts/generate-ssl.sh
	@./scripts/generate-ssl.sh
	@echo "✅ SSL certificates generated"

# Database Migration Commands
db-migrate-dev: ## Run Flyway migrations on development environment
	@echo "🗄️  Running database migrations on development environment..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_DEV) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using development environment"; \
		docker compose -f $(COMPOSE_FILE_DEV) --profile migration run --rm migration migrate; \
	else \
		echo "❌ No MariaDB container found for development environment. Please start it first with 'make run-dev'"; \
		exit 1; \
	fi
	@echo "✅ Development migrations completed"

db-migrate-prod: ## Run Flyway migrations on production environment
	@echo "🗄️  Running database migrations on production environment..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using production environment"; \
		docker compose -f $(COMPOSE_FILE_PROD) --profile migration run --rm migration migrate; \
	else \
		echo "❌ No MariaDB container found for production environment. Please start it first with 'make run-prod'"; \
		exit 1; \
	fi
	@echo "✅ Production migrations completed"

db-info-dev: ## Show migration status for development environment
	@echo "📊 Database migration status for development environment:"
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_DEV) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using development environment"; \
		docker compose -f $(COMPOSE_FILE_DEV) --profile migration run --rm migration info; \
	else \
		echo "❌ No MariaDB container found for development environment. Please start it first with 'make run-dev'"; \
		exit 1; \
	fi

db-info-prod: ## Show migration status for production environment
	@echo "📊 Database migration status for production environment:"
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using production environment"; \
		docker compose -f $(COMPOSE_FILE_PROD) --profile migration run --rm migration info; \
	else \
		echo "❌ No MariaDB container found for production environment. Please start it first with 'make run-prod'"; \
		exit 1; \
	fi

db-clean-dev: ## Clean development database (DANGEROUS - will drop all objects)
	@echo "⚠️  WARNING: This will DROP ALL database objects in development environment!"
	@read -p "Are you sure? Type 'yes' to continue: " confirm && [ "$$confirm" = "yes" ] || exit 1
	@echo "🧹 Cleaning development database..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_DEV) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using development environment"; \
		docker compose -f $(COMPOSE_FILE_DEV) --profile migration run --rm migration clean; \
	else \
		echo "❌ No MariaDB container found for development environment. Please start it first"; \
		exit 1; \
	fi
	@echo "✅ Development database cleaned"

db-clean-prod: ## Clean production database (DANGEROUS - will drop all objects)
	@echo "⚠️  WARNING: This will DROP ALL database objects in production environment!"
	@read -p "Are you sure? Type 'yes' to continue: " confirm && [ "$$confirm" = "yes" ] || exit 1
	@echo "🧹 Cleaning production database..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using production environment"; \
		docker compose -f $(COMPOSE_FILE_PROD) --profile migration run --rm migration clean; \
	else \
		echo "❌ No MariaDB container found for production environment. Please start it first"; \
		exit 1; \
	fi
	@echo "✅ Production database cleaned"

db-validate-dev: ## Validate development migrations
	@echo "✅ Validating development migrations..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_DEV) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using development environment"; \
		docker compose -f $(COMPOSE_FILE_DEV) --profile migration run --rm migration validate; \
	else \
		echo "❌ No MariaDB container found for development environment. Please start it first"; \
		exit 1; \
	fi
	@echo "✅ Development validation completed"

db-validate-prod: ## Validate production migrations
	@echo "✅ Validating production migrations..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using production environment"; \
		docker compose -f $(COMPOSE_FILE_PROD) --profile migration run --rm migration validate; \
	else \
		echo "❌ No MariaDB container found for production environment. Please start it first"; \
		exit 1; \
	fi
	@echo "✅ Production validation completed"

db-repair-dev: ## Repair Flyway schema history for development environment
	@echo "🔧 Repairing Flyway schema history for development environment..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_DEV) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using development environment"; \
		docker compose -f $(COMPOSE_FILE_DEV) --profile migration run --rm migration repair; \
	else \
		echo "❌ No MariaDB container found for development environment. Please start it first"; \
		exit 1; \
	fi
	@echo "✅ Development repair completed"

db-repair-prod: ## Repair Flyway schema history for production environment
	@echo "🔧 Repairing Flyway schema history for production environment..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using production environment"; \
		docker compose -f $(COMPOSE_FILE_PROD) --profile migration run --rm migration repair; \
	else \
		echo "❌ No MariaDB container found for production environment. Please start it first"; \
		exit 1; \
	fi
	@echo "✅ Production repair completed"
