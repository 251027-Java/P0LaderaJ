maven := './mvnw'
jar_loc := 'target/battleship-1.0-0-dist.jar'
db_container := 'battleship-db'

# removes artifacts and temporary files
clean:
    rm -f dependency-reduced-pom.xml
    {{ maven }} clean

# compiles and runs the Main file
run:
    {{ maven }} compile exec:java

# runs the jar (if built)
runj:
    java -jar {{ jar_loc }}

# builds the jar
build:
    {{ maven }} package

# runs tests
test:
    {{ maven }} test

# style check
check:
    {{ maven }} spotless:check
    {{ maven }} checkstyle:check

# format code
format:
    {{ maven }} spotless:apply

# cleans and runs mvnw verify
all: clean
    {{ maven }} verify

# create the database container
make-db:
    docker run --name {{ db_container }} -e POSTGRES_PASSWORD=secret -d postgres

# initialize the database
init-db:
    docker cp scripts/init-db.sql {{ db_container }}:/tmp/init-db.sql
    docker exec {{ db_container }} psql -U postgres -f tmp/init-db.sql

# destroy the database container
destroy-db:
    docker rm -f {{ db_container }}
