maven := './mvnw'
jar_loc := 'target/battleship-1.0-0-dist.jar'

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
