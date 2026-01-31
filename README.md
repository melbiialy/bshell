# BShell

<p align="center">
  <strong>A lightweight, POSIX-style shell written in Java</strong>
</p>

<p align="center">
  Built to explore how Unix shells work — from parsing and tokenization to process execution and redirection.
</p>

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Quick Start](#quick-start)
- [Usage](#usage)
- [Architecture](#architecture)
- [Development](#development)
- [Limitations & Roadmap](#limitations--roadmap)
- [Contributing](#contributing)

---

## Overview

**BShell** is a minimal but fully interactive command-line shell implemented in Java. It supports external program execution, pipelines, built-in commands, output redirection, full quoting and escaping, command history with persistence, and tab completion — all with a clean, modular codebase designed for learning and extension.

Whether you're curious how shells parse commands, spawn processes, or handle I/O redirection, BShell offers a readable reference implementation without the complexity of bash or zsh.

---

## Features

| Category | Capabilities |
|----------|--------------|
| **Built-ins** | `cd`, `pwd`, `echo`, `type`, `exit` |
| **History** | `history`, `history -w`, `history -a`, `history -r` with file persistence |
| **Execution** | Run any program on your `PATH` |
| **Pipelines** | Chain commands with `\|` (e.g. `ls \| grep foo`) |
| **Redirection** | `>`, `>>`, `2>`, `2>>` for stdout and stderr |
| **Quoting** | Full support for single quotes, double quotes, and escaping |
| **Interactivity** | Readline-style editing, history navigation, **tab completion** (built-ins + PATH commands) |
| **Parsing** | Tokenization and command structure parsing |

---

## Quick Start

**Prerequisites:** Java 23+ (or adjust `pom.xml`), Maven

```bash
# Clone and enter the project
git clone https://github.com/<your-username>/codecrafters-shell-java.git
cd codecrafters-shell-java

# Build
mvn clean package

# Run BShell
./bshell.sh
```

Or run via Maven:

```bash
mvn exec:java -Dexec.mainClass="Main"
```

---

## Usage

Once BShell is running, use it like a typical Unix shell:

**External commands**

```bash
ls -la
grep "pattern" file.txt
cat /etc/os-release
```

**Built-in commands**

```bash
cd /tmp
pwd
echo "Hello from BShell"
type cd          # shows whether cd is built-in or external
exit
```

**History (with file persistence)**

```bash
history          # show command history
history -w       # write history to file
history -a       # append new entries to history file
history -r       # reload history from file
```

**Pipelines**

```bash
ls -la | grep ".txt"
cat file.txt | wc -l
ps aux | head -5
```

**Output redirection**

```bash
echo "log line" > out.log
echo "another line" >> out.log
ls missing-file 2> errors.log
ls missing-file 2>> errors.log
```

**Quoting and escaping**

```bash
echo 'single quoted $var stays literal'
echo "double quoted $HOME expands"
echo "escape \"quotes\" and newlines\n"
```

---

## Architecture

```
src/main/java/
├── Main.java                    # Entry point
├── commandexecution/
│   ├── BShell.java              # Main REPL loop, orchestration
│   ├── CommandExecutor.java     # Process spawn & I/O wiring
│   ├── CommandRunner.java       # Execution orchestration
│   ├── Command.java             # Parsed command representation
│   ├── RedirectHandler.java     # Redirection setup
│   ├── parser/                  # Tokenizer, Parser
│   ├── redirect/                # >, >>, 2>, 2>> implementations
│   ├── autocompletion/          # Tab completion (builtins + PATH)
│   └── lineinputhandler/        # JLine-based input handling
├── builtincommands/             # cd, echo, pwd, type, exit, history variants
├── history/                     # HistoryManager, HistoryStorage
└── exception/                   # Shell-specific exceptions
```

**Design principles**

- **Separation of concerns** — Parsing, execution, built-ins, redirection, and history live in dedicated packages.
- **Extensibility** — Add built-ins by implementing `BuiltInCommand` and registering them in `CommandRegistry`.

---

## Development

**Build JAR with dependencies**

```bash
mvn clean package
# Produces target/codecrafters-shell.jar (or via assembly in /tmp)
```

**Debugging** — Add logging in `BShell` or `CommandExecutor` to trace command parsing and execution.

---

## Limitations & Roadmap

**Current limitations**

- No command chaining (`&&`, `||`, `;`)
- No background jobs (`&`), job control (`fg`, `bg`)
- No shell variables, conditionals, loops, or functions

**Possible next steps**

- [ ] Command chaining (`&&`, `||`, `;`)
- [ ] Configurable history file location and size
- [ ] Additional built-ins (`which`, `alias`, `unalias`, `set`)

---

## Contributing

Contributions, ideas, and bug reports are welcome.

**Reporting issues** — Please include:
- The exact command you ran
- What BShell did
- What you expected (e.g., compared to bash)

**Pull requests** — Keep changes focused.

---

## License

MIT License — see [LICENSE](LICENSE) for details.
