# Command Line Project 
## 📘 Project Overview
This project implements a **Command Line Interpreter (CLI)** in **Java**, designed to simulate the functionality of a basic operating system terminal.  
The CLI accepts user input, parses it into commands and arguments, and executes actions directly on the file system.

The program continues running until the user types `exit`.

---

## ⚙️ Main Components
### 🧩 `Parser` Class
- Reads and parses user input.
- Separates the **command name** and **arguments**.
- Validates input format.

### ⚡ `Terminal` Class
- Executes all supported commands.
- Handles file and directory operations.
- Displays output and error messages.

---

## 🧠 Implemented Commands

| Command | Description |
|----------|-------------|
| **pwd** | Displays the current working directory. |
| **cd** | Changes the current directory. Supports:<br>• `cd` → home directory<br>• `cd ..` → previous directory<br>• `cd path` → move to the given path (absolute or relative). |
| **ls** | Lists the contents of the current directory (sorted alphabetically). |
| **mkdir** | Creates one or more new directories. |
| **rmdir** | Removes empty directories. Supports `rmdir *` to remove all empty directories in the current folder. |
| **touch** | Creates an empty file at the specified path. |
| **cp** | Copies a file to another file. |
| **cp -r** | Copies a directory and all its contents recursively. |
| **rm** | Deletes a file. |
| **cat** | Prints a file’s content or concatenates two files. |
| **wc** | Displays the number of lines, words, and characters in a file. |
| **>** | Redirects command output to a file (overwrites existing content). |
| **>>** | Redirects command output to a file (appends content if the file exists). |
| **zip / unzip** | Compresses or extracts files and directories. |
| **exit** | Exits the CLI program. |

---

## Contributors
- [Rabab Mohamed](https://github.com/Rabab-Okasha)
- [Malak Hassan](https://github.com/Malakhassan8)
- [Martina Waleed]
- [Nour Hassan](https://github.com/Nourhasann)
- [Nour Mohamed]
