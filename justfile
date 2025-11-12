maven := './mvnw'
jar_loc := 'target/P0LaderaJ-1.0-SNAPSHOT-shaded.jar'

clean:
    {{ maven }} clean

run:
    {{ maven }} compile exec:java

runj:
    java -jar {{ jar_loc }}

build:
    {{ maven }} package

test:
    {{ maven }} test
