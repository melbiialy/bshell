## BShell

`BShell` is a lightweight, POSIX-style command-line shell implemented in Java.  
It executes external commands, provides several built-in commands, maintains command history, and supports basic output redirection.  
The project is developed as a personal side project to deepen understanding of how Unix-like shells work internally.

## Features

- **Built-in commands**
  - `cd`, `pwd`, `echo`, `type`, `exit`
  - History-related commands: `history`, `history -w`, `history -a`, `history -r`
- **External command execution**
  - Executes programs available on the system `PATH`
- **Output redirection**
  - Redirects `stdout` and `stderr`, including append variants (`>`, `>>`, `2>`, `2>>`)
- **Command parsing**
  - Tokenization and parsing of simple shell commands
- **Interactive shell**
  - Read–eval–print loop with line editing support (via JLine)
- **Autocompletion**
  - Completion for built-in commands and commands discovered on `PATH`

## Technology Stack

- **Language**: Java  
- **Build tool**: Maven  
- **Libraries**:
  - JLine – line editing, history, and completion support

## Getting Started

- **Prerequisites**
  - Java 17 or later (or the version configured in `pom.xml`)
  - Maven (`mvn`)

- **Clone the repository**

```sh
git clone https://github.com/<your-username>/<your-repo-name>.git
cd <your-repo-name>
```

- **Build**

```sh
mvn clean package
```

- **Run**

Using the helper script:

```sh
./your_program.sh
```

Or by running the main class directly via Maven:

```sh
mvn exec:java -Dexec.mainClass="Main"
```

## Usage

After starting the shell, commands can be executed similarly to a standard POSIX shell.

- **External commands**

```sh
ls -la
grep "pattern" file.txt
```

- **Built-in commands**

```sh
cd /tmp
pwd
echo "Hello from BShell"
type cd
exit
```

- **History commands**

```sh
history        # display command history
history -w     # write history to file
history -a     # append new entries to history file
history -r     # reload history from file
```

- **Redirection examples**

```sh
echo "log line" > out.log
echo "another line" >> out.log
ls missing-file 2> errors.log
ls missing-file 2>> errors.log
```

## Project Structure

- `src/main/java/Main.java` – Application entry point.  
- `src/main/java/commandexecution/BShell.java` – Main shell loop and orchestration.  
- `src/main/java/commandexecution/CommandExecutor.java` – Process execution and I/O wiring.  
- `src/main/java/commandexecution/parser` – Command tokenization and parsing.  
- `src/main/java/builtincommands` – Implementations of built-in commands (`cd`, `echo`, `pwd`, `type`, `exit`, history commands, etc.).  
- `src/main/java/commandexecution/redirect` – Output redirection handling (`>`, `>>`, `2>`, `2>>`).  
- `src/main/java/history` – Command history management and persistence.  
- `src/main/java/exception` – Custom exception types for shell-specific errors.  

## Design Overview

- **Separation of concerns**: Parsing, execution, built-ins, redirection, and history are organized into dedicated packages.  
- **Extensibility**: New built-in commands can be added by implementing `BuiltInCommand` and registering them in `CommandRegistry`.  
- **Testability**: Core execution logic is separated from user input handling, simplifying testing of parsing and execution behavior.

## Roadmap

- Pipeline support (`cmd1 | cmd2`)  
- Background job execution (`cmd &`)  
- Environment variable expansion  
- Enhanced quoting and escaping rules  

## License

This is a personal side project.  
Add a `LICENSE` file (for example, MIT) if you intend to publish the project or accept external contributions.

## Java Shell – Side Project

This repository contains a small, POSIX‑style shell implemented in Java.  
It can execute external programs, handle several builtin commands, support command history, and perform basic output redirection.  
The project is intended as a learning/side project to better understand how Unix‑like shells work internally.

## Features

- **Builtin commands**
  - `cd`, `pwd`, `echo`, `type`, `exit`
  - History commands: `history`, `history -w`, `history -a`, `history -r`
- **External commands**
  - Executes programs available on the system `PATH`
- **Redirection**
  - Redirect `stdout` and `stderr`, including append variants (`>`, `>>`, `2>`, `2>>`)
- **Command parsing**
  - Tokenization and parsing of simple shell commands
- **Interactive shell**
  - Read–eval–print loop implemented in `BShell` with line editing support (via JLine)
- **Autocompletion**
  - Completion for builtin commands and commands discovered on `PATH`

## Tech Stack

- **Language**: Java
- **Build tool**: Maven
- **Libraries**:
  - JLine – line editing, history, and completion support

## Getting Started

- **Prerequisites**
  - Java 17+ (or the version configured in `pom.xml`)
  - Maven (`mvn`)

- **Clone the repository**

```sh
git clone https://github.com/<your-username>/<your-repo-name>.git
cd <your-repo-name>
```

- **Build**

```sh
mvn clean package
```

- **Run**

You can use the helper script:

```sh
./your_program.sh
```

or run the `Main` class directly via Maven:

```sh
mvn exec:java -Dexec.mainClass="Main"
```

## Usage

Once the shell starts, you can run commands much like in a regular POSIX shell.

- **Run external commands**

```sh
ls -la
grep pattern file.txt
```

- **Use builtin commands**

```sh
cd /tmp
pwd
echo "Hello from my Java shell"
type cd
exit
```

- **Work with history**

```sh
history        # show command history
history -w     # write history to file
history -a     # append new commands to history file
history -r     # reload history from file
```

- **Redirection examples**

```sh
echo "log line" > out.log
echo "another line" >> out.log
ls missing-file 2> errors.log
```

## Project Structure

- `src/main/java/Main.java` – Application entry point.
- `src/main/java/commandexecution/BShell.java` – Main shell loop and orchestration.
- `src/main/java/commandexecution/CommandExecutor.java` – Spawns processes and wires IO.
- `src/main/java/commandexecution/parser` – Tokenizer and parser for commands.
- `src/main/java/builtincommands` – Implementations of builtin commands (`cd`, `echo`, `pwd`, `type`, `exit`, history commands, etc.).
- `src/main/java/commandexecution/redirect` – Output redirection handling (`>`, `>>`, `2>`, `2>>`).
- `src/main/java/history` – History management and persistence.
- `src/main/java/exception` – Domain‑specific exceptions (e.g. unknown commands, invalid paths).

## Design Notes

- **Separation of concerns**
  - Parsing, execution, builtins, redirection, and history are split into dedicated packages.
- **Extensibility**
  - New builtin commands can be added by implementing `BuiltInCommand` and registering them in `CommandRegistry`.
- **Testability**
  - Core execution logic is isolated from user input handling, making it easier to test commands and parsing.

## Future Ideas

- Pipelines (`cmd1 | cmd2`)
- Background jobs (`cmd &`)
- Environment variable expansion
- More robust quoting and escaping rules

## License

You can choose any license you prefer for this side project (for example, MIT).  
Add a `LICENSE` file if you want others to reuse or contribute to the code.
