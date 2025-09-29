#!/bin/bash
set -e

echo "🔧 Creating Flyway user..."
mariadb -u root -p"${MYSQL_ROOT_PASSWORD}" <<FLYWAY_EOF
CREATE USER IF NOT EXISTS '${FLYWAY_USER}'@'%' IDENTIFIED BY '${FLYWAY_PASSWORD}';
GRANT ALL PRIVILEGES ON \`${MYSQL_DATABASE}\`.* TO '${FLYWAY_USER}'@'%';
FLUSH PRIVILEGES;
FLYWAY_EOF

echo "✅ Flyway user '${FLYWAY_USER}' created successfully."

echo "🔧 Granting additional privileges for Spring Boot user..."
mariadb -u root -p"${MYSQL_ROOT_PASSWORD}" <<APP_EOF
GRANT ALL PRIVILEGES ON \`${MYSQL_DATABASE}\`.* TO '${MYSQL_USER}'@'%';
FLUSH PRIVILEGES;
APP_EOF

echo "✅ Spring Boot user '${MYSQL_USER}' configured successfully."
echo "🎉 Database initialization completed!"
