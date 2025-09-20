# Makefile for Flight Management System Backend

# Variables
COMPOSE_FILE_PROD = docker-compose.yml
COMPOSE_FILE_DEV = docker-compose.dev.yml
ENV_FILE = .env

.PHONY: help build run-prod run-dev stop down clean logs status restart test mvn-clean mvn-compile mvn-test mvn-package mvn-install ssl-cert db-migrate db-info db-clean db-validate db-repair

# Default target
.DEFAULT_GOAL := help

help: ## Show this help message
	@echo "Flight Management System Backend - Available Commands"
	@echo ""
	@echo "🚀 Docker Commands:"
	@echo "  make build       - Build all Docker images"
	@echo "  make run-prod    - Start production environment"
	@echo "  make run-dev     - Start development environment"
	@echo "  make stop        - Stop all containers"
	@echo "  make down        - Stop and remove containers"
	@echo "  make restart     - Restart all containers"
	@echo "  make clean       - Clean up containers, images and volumes"
	@echo "  make logs        - Show logs from all containers"
	@echo "  make status      - Show status of all containers"
	@echo ""
	@echo "🛠️  Maven Commands:"
	@echo "  make mvn-clean   - Clean Maven build artifacts"
	@echo "  make mvn-compile - Compile the project"
	@echo "  make mvn-test    - Run unit tests"
	@echo "  make mvn-package - Package the application"
	@echo "  make mvn-install - Install dependencies and build"
	@echo "  make test        - Run tests (alias for mvn-test)"
	@echo ""
	@echo "🗄️  Database Migration Commands:"
	@echo "  make db-migrate  - Run Flyway migrations"
	@echo "  make db-info     - Show migration status"
	@echo "  make db-clean    - Clean database (DANGEROUS)"
	@echo "  make db-validate - Validate migrations"
	@echo "  make db-repair   - Repair Flyway schema history"
	@echo ""
	@echo "🔐 SSL Commands:"
	@echo "  make ssl-cert    - Generate SSL certificates"
	@echo ""
	@echo "🚀 CI/CD Commands:"
	@echo "  make setup-ssh   - Generate SSH keys for deployment"
	@echo "  make test-cicd   - Test CI/CD pipeline locally"
	@echo "  make deploy-prod - Deploy to production locally"
	@echo "  make backup-db   - Create database backup"
	@echo "  make health-check- Check application health"
	@echo "  make logs-prod   - Show production logs"
	@echo "  make restart-prod- Restart production services"
	@echo "  make stop-prod   - Stop production services"
	@echo "  make clean-prod  - Clean production environment"
	@echo ""

# Docker Commands
build: ## Build all Docker images
	@echo "🔨 Building Docker images..."
	@docker compose -f $(COMPOSE_FILE_PROD) build
	@docker compose -f $(COMPOSE_FILE_DEV) build
	@echo "✅ Docker images built successfully"

run-prod: ## Start production environment
	@echo "🚀 Starting Production Environment..."
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "❌ .env file not found! Please create one."; \
		exit 1; \
	fi
	@docker compose -f $(COMPOSE_FILE_PROD) up -d
	@echo "✅ Production environment started"
	@echo "🌐 Application: http://localhost:8080"
	@echo "🌐 HTTPS: https://localhost:8443"
	@echo "🗄️  MySQL: localhost:3306"

run-dev: ## Start development environment
	@echo "🛠️  Starting Development Environment..."
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "❌ .env file not found! Please create one."; \
		exit 1; \
	fi
	@docker compose -f $(COMPOSE_FILE_DEV) up -d
	@echo "✅ Development environment started"
	@echo "🌐 Application: http://localhost:8080"
	@echo "🔄 LiveReload: http://localhost:35729"
	@echo "🗄️  MySQL: localhost:3306"

stop: ## Stop all containers
	@echo "🛑 Stopping all containers..."
	@docker compose -f $(COMPOSE_FILE_PROD) down
	@docker compose -f $(COMPOSE_FILE_DEV) down
	@echo "✅ All containers stopped"

down: ## Stop and remove containers
	@echo "⬇️  Stopping and removing containers..."
	@docker compose -f $(COMPOSE_FILE_PROD) down --remove-orphans
	@docker compose -f $(COMPOSE_FILE_DEV) down --remove-orphans
	@echo "✅ Containers stopped and removed"

restart: ## Restart all containers
	@echo "🔄 Restarting containers..."
	@make stop
	@make run-dev
	@echo "✅ Containers restarted"

clean: ## Clean up containers, images and volumes
	@echo "🧹 Cleaning up..."
	@docker compose -f $(COMPOSE_FILE_PROD) down -v --remove-orphans
	@docker compose -f $(COMPOSE_FILE_DEV) down -v --remove-orphans
	@docker system prune -f
	@docker volume prune -f
	@echo "✅ Cleanup completed"

logs: ## Show logs from all containers
	@echo "📋 Showing container logs..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_DEV) ps -q 2>/dev/null)" ]; then \
		docker compose -f $(COMPOSE_FILE_DEV) logs -f --tail=100; \
	elif [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q 2>/dev/null)" ]; then \
		docker compose -f $(COMPOSE_FILE_PROD) logs -f --tail=100; \
	else \
		echo "❌ No running containers found"; \
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
db-migrate: ## Run Flyway migrations
	@echo "🗄️  Running database migrations..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_DEV) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using development environment"; \
		docker compose -f $(COMPOSE_FILE_DEV) --profile migration run --rm migration mvn flyway:migrate; \
	elif [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using production environment"; \
		docker compose -f $(COMPOSE_FILE_PROD) --profile migration run --rm migration mvn flyway:migrate; \
	else \
		echo "❌ No MariaDB container found. Please start an environment first with 'make run-dev' or 'make run-prod'"; \
		exit 1; \
	fi
	@echo "✅ Migrations completed"

db-info: ## Show migration status
	@echo "📊 Database migration status:"
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_DEV) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using development environment"; \
		docker compose -f $(COMPOSE_FILE_DEV) --profile migration run --rm migration mvn flyway:info; \
	elif [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using production environment"; \
		docker compose -f $(COMPOSE_FILE_PROD) --profile migration run --rm migration mvn flyway:info; \
	else \
		echo "❌ No MariaDB container found. Please start an environment first with 'make run-dev' or 'make run-prod'"; \
		exit 1; \
	fi

db-clean: ## Clean database (DANGEROUS - will drop all objects)
	@echo "⚠️  WARNING: This will DROP ALL database objects!"
	@read -p "Are you sure? Type 'yes' to continue: " confirm && [ "$$confirm" = "yes" ] || exit 1
	@echo "🧹 Cleaning database..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_DEV) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using development environment"; \
		docker compose -f $(COMPOSE_FILE_DEV) --profile migration run --rm migration mvn flyway:clean; \
	elif [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using production environment"; \
		docker compose -f $(COMPOSE_FILE_PROD) --profile migration run --rm migration mvn flyway:clean; \
	else \
		echo "❌ No MariaDB container found. Please start an environment first"; \
		exit 1; \
	fi
	@echo "✅ Database cleaned"

db-validate: ## Validate migrations
	@echo "✅ Validating migrations..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_DEV) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using development environment"; \
		docker compose -f $(COMPOSE_FILE_DEV) --profile migration run --rm migration mvn flyway:validate; \
	elif [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using production environment"; \
		docker compose -f $(COMPOSE_FILE_PROD) --profile migration run --rm migration mvn flyway:validate; \
	else \
		echo "❌ No MariaDB container found. Please start an environment first"; \
		exit 1; \
	fi
	@echo "✅ Validation completed"

db-repair: ## Repair Flyway schema history
	@echo "🔧 Repairing Flyway schema history..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE_DEV) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using development environment"; \
		docker compose -f $(COMPOSE_FILE_DEV) --profile migration run --rm migration mvn flyway:repair; \
	elif [ "$(shell docker compose -f $(COMPOSE_FILE_PROD) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using production environment"; \
		docker compose -f $(COMPOSE_FILE_PROD) --profile migration run --rm migration mvn flyway:repair; \
	else \
		echo "❌ No MariaDB container found. Please start an environment first"; \
		exit 1; \
	fi
	@echo "✅ Repair completed"

# CI/CD Commands
setup-ssh: ## Generate SSH keys for CI/CD deployment
	@echo "🔐 Generating SSH keys for CI/CD..."
	@chmod +x scripts/generate-ssh-keys.sh
	@./scripts/generate-ssh-keys.sh

test-cicd: ## Test CI/CD pipeline locally
	@echo "🧪 Testing CI/CD pipeline locally..."
	@chmod +x scripts/test-cicd.sh
	@./scripts/test-cicd.sh

deploy-prod: ## Deploy to production (local testing)
	@echo "🚀 Deploying to production..."
	@if [ ! -f .env.production ]; then \
		echo "❌ .env.production file not found! Please create it."; \
		exit 1; \
	fi
	@cp .env.production .env
	@docker compose down
	@docker compose up -d --build
	@docker compose --profile migration run --rm migration mvn flyway:migrate
	@echo "✅ Production deployment completed"

backup-db: ## Create database backup
	@echo "💾 Creating database backup..."
	@TIMESTAMP=$(shell date +%Y%m%d_%H%M%S)
	@mkdir -p backups/$$TIMESTAMP
	@if [ "$(shell docker compose ps -q mariadb 2>/dev/null)" ]; then \
		docker compose exec mariadb mysqldump -u root -p"$$MYSQL_ROOT_PASSWORD" airport_db > backups/$$TIMESTAMP/backup.sql; \
		echo "✅ Database backup created: backups/$$TIMESTAMP/backup.sql"; \
	else \
		echo "❌ MariaDB container not running"; \
		exit 1; \
	fi

health-check: ## Check application health
	@echo "🏥 Checking application health..."
	@if curl -f http://localhost:8080/actuator/health 2>/dev/null; then \
		echo "✅ Application is healthy"; \
	else \
		echo "❌ Application is not responding"; \
		exit 1; \
	fi

logs-prod: ## Show production logs
	@echo "📋 Showing production logs..."
	@docker compose logs -f --tail=100

restart-prod: ## Restart production services
	@echo "🔄 Restarting production services..."
	@docker compose restart
	@echo "✅ Production services restarted"

stop-prod: ## Stop production services
	@echo "🛑 Stopping production services..."
	@docker compose down
	@echo "✅ Production services stopped"

clean-prod: ## Clean production environment
	@echo "🧹 Cleaning production environment..."
	@docker compose down -v --remove-orphans
	@docker system prune -f
	@docker volume prune -f
	@echo "✅ Production environment cleaned"
