<h1 align="center">Battleship</h1>

<!-- TODO add section on how to start game. probably include database initialization -->

## Usage

Maven can be used in this project via `./mvnw` in command line. In addition, [just](https://just.systems/man/en/) is 
supported if available. See [Just Commands](#just-commands) for more information.

> [!NOTE]
> If using Command Prompt or a similar shell, 
`./mvnw.cmd` may need to be used instead. See [Windows Usage](#windows-usage) for more information.

### Windows usage

For some shells on Windows, such as Command Prompt, `./mvnw.cmd` should be used instead if `./mvnw` cannot be run.
Any commands found here that use `./mvnw` should work if replaced with `./mvnw.cmd`. 

If using a bash emulator or similar on Windows, such as Git Bash, `./mvnw` can be used. In addition, commands found in
the [justfile](./justfile) can be run if [just](https://just.systems/man/en/) is installed. See 
[Just Commands](#just-commands) for more information on `just` commands.

## `just` commands

Aside from manually entering Maven goals, [just](https://just.systems/man/en/) can also be used if it's installed and
added to the path. This is entirely **OPTIONAL**. It is not necessary to use `just` to run the project
but can help with development and re-running commands.

> [!WARNING]
> The current justfile assumes a unix-like shell and may not work properly with shells such as PowerShell and Command
> Prompt. See the official [docs](https://just.systems/man/en/prerequisites.html#windows) if you want to try to support
> these shells (not recommended).

**Recipe examples**

| Recipe  | Description                |
| ------- | -------------------------- |
| `run`   | Compiles and runs the game |
| `build` | Builds the jar             |
| `test`  | Runs all tests             |

To view all recipes, use the following command:

```sh
just -l
```

## Database initialization

Running a local PostgreSQL database in a [Docker](https://www.docker.com/) container is the intended way of creating and
utilizing the database for this game.

### Standard initialization

Create a new container and set up the database with the necessary tables and initial data:

```sh
docker run --name battleship-db -e POSTGRES_PASSWORD=secret -d postgres
docker cp scripts/init-db.sql battleship-db:/tmp/dbdata
docker exec battleship-db psql -U postgres -f tmp/dbdata
```

<details>
<summary>Just</summary>

```sh
just db-create
```

</details>

### Database exportation

If you've ran the game and want to transfer the data to another system, you may want to export the database.

To export the database to a file called `battle.data`:

```sh
docker exec battleship-db pg_dump -U postgres -F p postgres > battle.data
```

<details>
<summary>Just</summary>

```sh
just db-export
```

</details>

### Initialization with existing data

You can create a new database with existing data assuming your data file is called `battle.data`:

```sh
docker run --name battleship-db -e POSTGRES_PASSWORD=secret -d postgres
docker cp battle.data battleship-db:/tmp/dbdata
docker exec battleship-db psql -U postgres -f tmp/dbdata
```

<details>
<summary>Just</summary>

```sh
just db-create-with battle.data
```

</details>

### Deletion of the database

If you need to re-initialize a database, you need to delete it first.

Delete the database:

```sh
docker rm -f battleship-db
```

<details>
<summary>Just</summary>

```sh
just db-destroy
```

</details>

## Running the game

Once you have the database made and running, you can start running the game:

```sh
./mvnw compile exec:java
```

<details>
<summary>Just</summary>

```sh
just run
```

</details>

Alternatively, if you've built the jar, you can use:

```sh
java -jar target/battleship-1.0.0-dist.jar
```

<details>
<summary>Just</summary>

```sh
just runj
```

</details>

## Testing

To run all tests, use the following command:

```sh
./mvnw test
```

<details>
<summary>Just</summary>

```sh
just test
```

</details>

## Building

Build the jar to distribute:

```sh
./mvnw package
```

<details>
<summary>Just</summary>

```sh
just build
```

</details>

The jar will be generated in the `target` directory with the name `battleship-1.0-0-dist.jar`.

