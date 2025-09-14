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

# Docker Commands
build: ## Build all Docker images
	@echo "🔨 Building Docker images..."
	@docker-compose -f $(COMPOSE_FILE_PROD) build
	@docker-compose -f $(COMPOSE_FILE_DEV) build
	@echo "✅ Build completed"

run-prod: ## Start production environment
	@echo "🚀 Starting Production Environment..."
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "❌ .env file not found. Please create it first."; \
		exit 1; \
	fi
	@docker-compose -f $(COMPOSE_FILE_PROD) up -d
	@echo "✅ Production environment started"
	@echo "🌐 Application: http://localhost:8080"
	@echo "🌐 HTTPS: https://localhost:8443"
	@echo "🗄️  MySQL: localhost:3306"

run-dev: ## Start development environment
	@echo "🛠️  Starting Development Environment..."
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "❌ .env file not found. Please create it first."; \
		exit 1; \
	fi
	@docker-compose -f $(COMPOSE_FILE_DEV) up -d
	@echo "✅ Development environment started"
	@echo "🌐 Application: http://localhost:8080"
	@echo "🔄 LiveReload: http://localhost:35729"
	@echo "🗄️  MySQL: localhost:3306"

stop: ## Stop all containers
	@echo "🛑 Stopping all containers..."
	@docker-compose -f $(COMPOSE_FILE_PROD) down
	@docker-compose -f $(COMPOSE_FILE_DEV) down
	@echo "✅ All containers stopped"

down: ## Stop and remove containers
	@echo "⬇️  Stopping and removing containers..."
	@docker-compose -f $(COMPOSE_FILE_PROD) down --remove-orphans
	@docker-compose -f $(COMPOSE_FILE_DEV) down --remove-orphans
	@echo "✅ Containers stopped and removed"

restart: ## Restart all containers
	@echo "🔄 Restarting containers..."
	@make stop
	@make run-dev
	@echo "✅ Containers restarted"

clean: ## Clean up containers, images and volumes
	@echo "🧹 Cleaning up..."
	@docker-compose -f $(COMPOSE_FILE_PROD) down -v --remove-orphans
	@docker-compose -f $(COMPOSE_FILE_DEV) down -v --remove-orphans
	@docker system prune -f
	@docker volume prune -f
	@echo "✅ Cleanup completed"

logs: ## Show logs from all containers
	@echo "📋 Showing container logs..."
	@if [ "$(shell docker-compose -f $(COMPOSE_FILE_DEV) ps -q 2>/dev/null)" ]; then \
		docker-compose -f $(COMPOSE_FILE_DEV) logs -f --tail=100; \
	elif [ "$(shell docker-compose -f $(COMPOSE_FILE_PROD) ps -q 2>/dev/null)" ]; then \
		docker-compose -f $(COMPOSE_FILE_PROD) logs -f --tail=100; \
	else \
		echo "❌ No running containers found"; \
	fi

status: ## Show status of all containers
	@echo "📊 Container Status:"
	@echo ""
	@echo "Production Environment:"
	@docker-compose -f $(COMPOSE_FILE_PROD) ps
	@echo ""
	@echo "Development Environment:"
	@docker-compose -f $(COMPOSE_FILE_DEV) ps

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
	@SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/flight_db SPRING_DATASOURCE_USERNAME=root SPRING_DATASOURCE_PASSWORD=rootpassword123 ./mvnw flyway:migrate
	@echo "✅ Migrations completed"

db-info: ## Show migration status
	@echo "📊 Database migration status:"
	@SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/flight_db SPRING_DATASOURCE_USERNAME=root SPRING_DATASOURCE_PASSWORD=rootpassword123 ./mvnw flyway:info

db-clean: ## Clean database (DANGEROUS - will drop all objects)
	@echo "⚠️  WARNING: This will DROP ALL database objects!"
	@read -p "Are you sure? Type 'yes' to continue: " confirm && [ "$$confirm" = "yes" ] || exit 1
	@echo "🧹 Cleaning database..."
	@SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/flight_db SPRING_DATASOURCE_USERNAME=root SPRING_DATASOURCE_PASSWORD=rootpassword123 ./mvnw flyway:clean
	@echo "✅ Database cleaned"

db-validate: ## Validate migrations
	@echo "✅ Validating migrations..."
	@SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/flight_db SPRING_DATASOURCE_USERNAME=root SPRING_DATASOURCE_PASSWORD=rootpassword123 ./mvnw flyway:validate
	@echo "✅ Validation completed"

db-repair: ## Repair Flyway schema history
	@echo "🔧 Repairing Flyway schema history..."
	@SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/flight_db SPRING_DATASOURCE_USERNAME=root SPRING_DATASOURCE_PASSWORD=rootpassword123 ./mvnw flyway:repair
	@echo "✅ Repair completed"
