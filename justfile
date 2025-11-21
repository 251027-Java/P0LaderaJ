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
    {{ maven }} jacoco:report

# style check
[group('style')]
check:
    {{ maven }} spotless:check
    {{ maven }} checkstyle:check

# format all code
[group('style')]
format:
    {{ maven }} spotless:apply
    docker run --rm -v /$(pwd):/work backplane/pgformatter -i scripts/*.sql

# cleans and runs mvnw verify
all: clean
    {{ maven }} verify

_db-create:
    docker run --name {{ db_container }} -e POSTGRES_PASSWORD=secret -p 5432:5432 -d postgres

_db-init filepath:
    docker cp {{ filepath }} {{ db_container }}:/tmp/dbdata
    @sleep 2
    docker exec {{ db_container }} psql -U postgres -v ON_ERROR_STOP=1 -q -f tmp/dbdata

# create the container and set up the database
[group('db')]
db-create: _db-create (_db-init 'scripts/init-db.sql')

# destroy the database container
[group('db')]
db-destroy:
    docker rm -f {{ db_container }}

# start the database container
[group('db')]
db-start:
    docker start {{ db_container }}

# stop the database container
[group('db')]
db-stop:
    docker stop {{ db_container }}

# export the database
[group('db')]
db-export filepath='battle.data':
    docker exec {{ db_container }} pg_dump -U postgres -F p postgres > {{ filepath }}

# create container with a given data file
[group('db')]
db-create-with filepath: _db-create (_db-init filepath)
