# Makefile for Flight Management System Backend

# Variables
ENV ?= dev
COMPOSE_FILE_PROD = docker-compose.yml
COMPOSE_FILE_DEV = docker-compose.dev.yml
COMPOSE_FILE = $(if $(filter prod,$(ENV)),$(COMPOSE_FILE_PROD),$(COMPOSE_FILE_DEV))
ENV_FILE = .env

.PHONY: help build run stop down clean logs status restart test mvn-clean mvn-compile mvn-test mvn-package mvn-install ssl-cert db-migrate db-info db-clean db-validate db-repair

# Default target
.DEFAULT_GOAL := help

help: ## Show this help message
	@echo "Flight Management System Backend - Available Commands"
	@echo ""
	@echo "Usage: make <command> ENV=<dev|prod>"
	@echo "Default ENV=dev if not specified"
	@echo ""
	@echo "🚀 Docker Commands:"
	@echo "  make build ENV=dev   - Build development Docker images"
	@echo "  make build ENV=prod  - Build production Docker images"
	@echo "  make run ENV=dev     - Start development environment"
	@echo "  make run ENV=prod    - Start production environment"
	@echo "  make stop ENV=dev    - Stop development containers"
	@echo "  make stop ENV=prod   - Stop production containers"
	@echo "  make down ENV=dev    - Stop and remove development containers"
	@echo "  make down ENV=prod   - Stop and remove production containers"
	@echo "  make restart ENV=dev - Restart development containers"
	@echo "  make restart ENV=prod- Restart production containers"
	@echo "  make clean ENV=dev   - Clean up development containers, images and volumes"
	@echo "  make clean ENV=prod  - Clean up production containers, images and volumes"
	@echo "  make logs ENV=dev    - Show logs from development containers"
	@echo "  make logs ENV=prod   - Show logs from production containers"
	@echo "  make status          - Show status of all containers"
	@echo ""
	@echo "🛠️  Maven Commands:"
	@echo "  make mvn-clean   - Clean Maven build artifacts"
	@echo "  make mvn-compile - Compile the project"
	@echo "  make mvn-test    - Run unit tests"
	@echo "  make mvn-package - Package the application"
	@echo "  make mvn-install - Install dependencies and build"
	@echo "  make test        - Run tests (alias for mvn-test)"
	@echo ""
	@echo "� CI/CD Commands:"
	@echo "  make run-ci ENV=prod     - Start with database wait for CI/CD"
	@echo "  make deploy-ci ENV=prod  - Full CI/CD deployment with migration"
	@echo ""
	@echo "�🗄️  Database Migration Commands:"
	@echo "  make db-migrate ENV=dev  - Run Flyway migrations on development"
	@echo "  make db-migrate ENV=prod - Run Flyway migrations on production"
	@echo "  make db-info ENV=dev     - Show development migration status"
	@echo "  make db-info ENV=prod    - Show production migration status"
	@echo "  make db-clean ENV=dev    - Clean development database (DANGEROUS)"
	@echo "  make db-clean ENV=prod   - Clean production database (DANGEROUS)"
	@echo "  make db-validate ENV=dev - Validate development migrations"
	@echo "  make db-validate ENV=prod- Validate production migrations"
	@echo "  make db-repair ENV=dev   - Repair development Flyway schema history"
	@echo "  make db-repair ENV=prod  - Repair production Flyway schema history"
	@echo ""
	@echo "🔐 SSL Commands:"
	@echo "  make ssl-cert    - Generate SSL certificates"
	@echo ""

# Docker Commands
build: ## Build Docker images based on ENV
ifeq ($(ENV),prod)
	@echo "🔨 Building production Docker images..."
	@DOCKER_BUILDKIT=1 docker build --network=host --progress=plain -f Dockerfile -t flight-mnm-backend:latest .
	@echo "✅ Production images built"
else
	@echo "🔨 Building development Docker images..."
	@docker compose -f $(COMPOSE_FILE) build
	@echo "✅ Development images built"
endif

run: ## Start environment based on ENV
	@echo "🚀 Starting $(ENV) Environment..."
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "❌ .env file not found! Please create one."; \
		exit 1; \
	fi
	@docker compose -f $(COMPOSE_FILE) up -d
ifeq ($(ENV),prod)
	@echo "✅ Production environment started"
	@echo "🌐 Application: http://localhost:8080"
	@echo "🌐 HTTPS: https://localhost:8443"
	@echo "🗄️  MySQL: localhost:3306"
else
	@echo "✅ Development environment started"
	@echo "🌐 Application: http://localhost:8080"
	@echo "🔄 LiveReload: http://localhost:35729"
	@echo "🗄️  MySQL: localhost:3306"
endif


stop: ## Stop containers based on ENV
	@echo "🛑 Stopping $(ENV) containers..."
	@docker compose -f $(COMPOSE_FILE) stop
	@echo "✅ $(ENV) containers stopped"

down: ## Stop and remove containers based on ENV
	@echo "⬇️  Stopping and removing $(ENV) containers..."
	@docker compose -f $(COMPOSE_FILE) down --remove-orphans
	@echo "✅ $(ENV) containers stopped and removed"

restart: ## Restart containers based on ENV
	@echo "🔄 Restarting $(ENV) containers..."
	@$(MAKE) stop ENV=$(ENV)
	@$(MAKE) run ENV=$(ENV)
	@echo "✅ $(ENV) containers restarted"

clean: ## Clean up containers, images and volumes for specific ENV
	@echo "🧹 Cleaning up $(ENV) environment..."
	@docker compose -f $(COMPOSE_FILE) down -v --remove-orphans
ifeq ($(ENV),prod)
	@docker image rm flight-mnm-backend:latest 2>/dev/null || true
endif
	@docker system prune -f
	@docker volume prune -f
	@echo "✅ $(ENV) cleanup completed"

logs: ## Show logs from containers based on ENV
	@echo "📋 Showing $(ENV) container logs..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE) ps -q 2>/dev/null)" ]; then \
		docker compose -f $(COMPOSE_FILE) logs -f --tail=100; \
	else \
		echo "❌ No running $(ENV) containers found"; \
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
db-migrate: ## Run Flyway migrations based on ENV
	@echo "🗄️  Running database migrations on $(ENV) environment..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using $(ENV) environment"; \
		docker compose -f $(COMPOSE_FILE) --profile migration run --rm migration; \
	else \
		echo "❌ No MariaDB container found for $(ENV) environment. Please start it first with 'make run ENV=$(ENV)'"; \
		exit 1; \
	fi
	@echo "✅ $(ENV) migrations completed"

db-info: ## Show migration status based on ENV
	@echo "📊 Database migration status for $(ENV) environment:"
	@if [ "$(shell docker compose -f $(COMPOSE_FILE) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using $(ENV) environment"; \
		docker compose -f $(COMPOSE_FILE) --profile migration run --rm migration info; \
	else \
		echo "❌ No MariaDB container found for $(ENV) environment. Please start it first with 'make run ENV=$(ENV)'"; \
		exit 1; \
	fi

db-clean: ## Clean database (DANGEROUS - will drop all objects) based on ENV
	@echo "⚠️  WARNING: This will DROP ALL database objects in $(ENV) environment!"
	@read -p "Are you sure? Type 'yes' to continue: " confirm && [ "$$confirm" = "yes" ] || exit 1
	@echo "🧹 Cleaning $(ENV) database..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using $(ENV) environment"; \
		docker compose -f $(COMPOSE_FILE) --profile migration run --rm migration mvn flyway:clean; \
	else \
		echo "❌ No MariaDB container found for $(ENV) environment. Please start it first"; \
		exit 1; \
	fi
	@echo "✅ $(ENV) database cleaned"

db-validate: ## Validate migrations based on ENV
	@echo "✅ Validating $(ENV) migrations..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using $(ENV) environment"; \
		docker compose -f $(COMPOSE_FILE) --profile migration run --rm migration mvn flyway:validate; \
	else \
		echo "❌ No MariaDB container found for $(ENV) environment. Please start it first"; \
		exit 1; \
	fi
	@echo "✅ $(ENV) validation completed"

db-repair: ## Repair Flyway schema history based on ENV
	@echo "🔧 Repairing Flyway schema history for $(ENV) environment..."
	@if [ "$(shell docker compose -f $(COMPOSE_FILE) ps -q mariadb 2>/dev/null)" ]; then \
		echo "📍 Using $(ENV) environment"; \
		docker compose -f $(COMPOSE_FILE) --profile migration run --rm migration repair; \
	else \
		echo "❌ No MariaDB container found for $(ENV) environment. Please start it first"; \
		exit 1; \
	fi
	@echo "✅ $(ENV) repair completed"
