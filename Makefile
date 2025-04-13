# Variables
docker_compose_dev = docker compose -f docker-compose.dev.yml
docker_compose_prod = docker compose -f docker-compose.prod.yml
service_name_dev = app-dev
service_name_prod = app

# Targets
.PHONY: build-dev
build-dev:
	$(docker_compose_dev) build

.PHONY: build-prod
build-prod:
	$(docker_compose_prod) build

.PHONY: up-dev
up-dev:
	$(docker_compose_dev) up

.PHONY: up-prod
up-prod:
	$(docker_compose_prod) up

.PHONY: down-dev
down-dev:
	$(docker_compose_dev) down

.PHONY: down-prod
down-prod:
	$(docker_compose_prod) down

.PHONY: logs-dev
logs-dev:
	$(docker_compose_dev) logs -f

.PHONY: logs-prod
logs-prod:
	$(docker_compose_prod) logs -f

.PHONY: migrate-dev
migrate-dev:
	$(docker_compose_dev) exec $(service_name_dev) mvn flyway:migrate \
	-Dflyway.url=jdbc:mysql://mysql:3306/airportdb \
	-Dflyway.user=root \
	-Dflyway.password=rootpassword -e

.PHONY: migrate-prod
migrate-prod:
	$(docker_compose_prod) exec $(service_name_prod) mvn flyway:migrate \
	-Dflyway.url=jdbc:mysql://mysql:3306/airportdb \
	-Dflyway.user=root \
	-Dflyway.password=rootpassword -e

.PHONY: clean-dev
clean-dev:
	$(docker_compose_dev) down --volumes --remove-orphans

.PHONY: clean-prod
clean-prod:
	$(docker_compose_prod) down --volumes --remove-orphans

.PHONY: restart-dev
restart-dev:
	$(docker_compose_dev) down && $(docker_compose_dev) up -d

.PHONY: restart-prod
restart-prod:
	$(docker_compose_prod) down && $(docker_compose_prod) up -d
