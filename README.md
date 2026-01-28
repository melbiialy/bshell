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

