#!/bin/bash
set -e

echo "🔨 Building Shopizer..."
mvn clean install -DskipTests

echo "🚀 Starting Shopizer with H2 database..."
cd sm-shop
mvn spring-boot:run -Dspring-boot.run.profiles=h2
