# Battleship

<!-- TODO add section on how to start game. probably include database initialization -->

## Usage

Maven can be used in this project via `./mvnw` in command line. In addition, [just](https://just.systems/man/en/) is 
supported if available. See [Just Commands](#just-commands) for more information.

> [!NOTE]
> If using Command Prompt or a similar shell, 
`./mvnw.cmd` may need to be used instead. See [Windows Usage](#windows-usage) for more information.

### Running the game

<!-- need docker command somewhere -->
```sh
./mvnw compile exec:java
```

### Testing

To run all tests, use the following command:

```sh
./mvnw test
```

### Building

Build the jar to distribute:

```sh
./mvnw package
```

The jar will be generated in the `target` directory with the name `battleship-1.0-0-dist.jar`. From the root directory
of this repository, use the following command to run the jar:

```sh
java -jar target/battleship-1.0.0-dist.jar
```

### Windows Usage

For some shells on Windows, such as Command Prompt, `./mvnw.cmd` should be used instead if `./mvnw` cannot be run.
Any commands found here that use `./mvnw` should work if replaced with `./mvnw.cmd`. 

If using a bash emulator or similar on Windows, such as Git Bash, `./mvnw` can be used. In addition, commands found in
the [justfile](./justfile) can be run if [just](https://just.systems/man/en/) is installed. See 
[Just Commands](#just-commands) for more information on `just` commands.

### `just` Commands

Aside from manually entering Maven goals, [just](https://just.systems/man/en/) can also be used if it's installed and
added to the path. This is entirely **OPTIONAL**. It is not necessary to use `just` to run the project
but can help with development and rerunning commands.

> [!WARNING]
> The current justfile assumes a unix-like shell and may not work properly with shells such as PowerShell and Command
> Prompt. See the official [docs](https://just.systems/man/en/prerequisites.html#windows) if you want to try to support
> these shells (not recommended).

**Notable recipes**

| Recipe  | Description                |
| ------- | -------------------------- |
| `run`   | Compiles and runs the game |
| `build` | Builds the jar             |
| `test`  | Runs all tests             |

To view all recipes, use the following command:

```sh
just -l
```
