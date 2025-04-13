# Makefile for managing the airport-control-system project

# Variables
docker_compose = docker compose
service_name = app-dev

# Targets
.PHONY: build
build:
	$(docker_compose) build

.PHONY: up
up:
	$(docker_compose) up -d

.PHONY: down
down:
	$(docker_compose) down

.PHONY: logs
logs:
	$(docker_compose) logs -f

.PHONY: migrate
migrate:
	$(docker_compose) exec $(service_name) mvn flyway:migrate \
	-Dflyway.url=jdbc:mysql://mysql:3306/airportdb \
	-Dflyway.user=root \
	-Dflyway.password=rootpassword -e

.PHONY: test
test:
	$(docker_compose) exec $(service_name) mvn test

.PHONY: clean
clean:
	$(docker_compose) down --volumes --remove-orphans

.PHONY: restart
restart:
	$(docker_compose) down && $(docker_compose) up -d

# Run the application
.PHONY: run
run:
	$(docker_compose) up --build
