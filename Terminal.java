import java.util.Arrays;
import java.util.Scanner;
import java.io.File;

class Parser {
    String commandName;
    String[] args;

    public boolean parse(String input) {
        // tirm() remove the whitespaces before and after the string
        // split("\\s+") removes any whitespaces inside the string and split it after each whitespace
        String[] tokens = input.trim().split("\\s+");

        if (tokens.length == 0) // if the input is empty
            return false;

        commandName = tokens[0];
        args = new String[tokens.length - 1];// make size of args without the first word (command)

        // make i begains with 1, to take args only not first word (command) in tokens array
        for (int i = 1; i < tokens.length; i++)
            args[i - 1] = tokens[i];

        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}

public class Terminal {
    Parser parser = new Parser();
    File currentdir = new File(System.getProperty("user.dir")); // get current working directory

    public String pwd() {
        return currentdir.getAbsolutePath();
    }

    public void cd() {
        // No arguments go to home directory
        if (parser.args.length == 0) {
            currentdir = new File(System.getProperty("user.home"));
            System.out.println(currentdir);
        }
        // cd.. go to the previous(parent) directory
        else if (parser.args[0].equals("..")) {
            File parent = currentdir.getParentFile();
            if (parent != null) {
                currentdir = parent;
                System.out.println(currentdir.getAbsolutePath());
            } else
                System.out.println("No parent directory, you are at the root");
        }
        // argument is full path or the relative (short) path
        else {
            File path = new File(parser.args[0]);
            // if the input path is relative (short) path, connect it to current directory
            if (!path.isAbsolute()) {
                // create a new file object by resolve it with current directory to get its absolute path
                path = new File(currentdir, parser.args[0]);
            }

            //check if the resloved path exists and if it's a directory
            if (path.exists() && path.isDirectory()) {
                currentdir = path; // update current directory
                System.out.println(currentdir.getAbsolutePath()); // print absolute path
            } else
                System.out.println("No such directory " + parser.args[0]);
        }
    }

    public void ls() {
        // create file object points to current directory
        File file = currentdir;
        String[] files = file.list();

        if (files == null)
            System.out.println("Directory is empty or can't be accessed!");
        else {
            // Arrays is a core utility class in java
            Arrays.sort(files); //You don’t create an Arrays object — instead, you call its static methods directly

            for (int i = 0; i < files.length; i++) {
                System.out.println(files[i]);

                //OR "use range based for loop"
                // for(String file : files)
                // System.out.println(file);
            }
        }
    }

    public void mkdir() {
        // if the user didn't enter directory name
        if (parser.args.length == 0) {
            System.out.println("Directory name is missing, try again!");
            return;
        }
        // if the user enter 1 or more arguments
        for (String dir : parser.args) {
            //create file object points on where folder will be created
            File newdir = new File(dir);
            // check if the argument is absolute(full) or relative path
            if (!newdir.isAbsolute()) {
                // if it's a relative path, make file object(newdir) points to the new path as its path connected to absolute path
                newdir = new File(currentdir, dir);
            }
            //check if the directory is created or not
            if (newdir.mkdirs())
                // if the directory is successfully created
                System.out.println("Directory created: " + newdir.getAbsolutePath());
            else
                // if the directory is already created or failed to be created
                System.out.println("Failed to create: " + newdir.getAbsolutePath());
        }
    }

    public void chooseCommandAction() {
        String command = parser.getCommandName();
        String[] args = parser.getArgs();

        switch (command) {
            case "pwd":
                System.out.println(pwd()); // printing the string returned from the method
                break;

            case "cd":
                cd();
                break;

            case "ls":
                ls();
                break;

            case "mkdir":
                mkdir();
                break;

            case "exit":
                System.out.println("Exiting...");
                System.exit(0);
            default:
                System.out.println("There is no such command, please try again!");
                break;
        }
    }

    public static void main(String[] args) {
        Terminal terminal = new Terminal();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter command: ");
            String command = scanner.nextLine();

            if (terminal.parser.parse(command))
                terminal.chooseCommandAction();
            else
                System.out.println("Invalid command");
        }

    }
}

