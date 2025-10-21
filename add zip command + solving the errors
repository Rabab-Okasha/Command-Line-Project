import java.util.Arrays;
import java.util.Scanner;
import java.io.*;
import java.util.zip.*;

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
    
    public void rmdir(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("Error: enter directory name");
            return;
        }

        String argument = args[0];

        if ( argument.equals("*") ) {

           File currentDir = currentdir;
            // puts all directories in current directory in  a list
            File[] subDirs = currentDir.listFiles(File::isDirectory);

            if (subDirs == null || subDirs.length == 0) {
                // checks if the list for subdirectories is empty
                System.out.println("No subdirectories found");
                return;
            }

            boolean deleted = false;
            for (File dir : subDirs) {
                // go through each subdirectory
                String[] contents = dir.list();
                // put them all in array
                if (contents != null && contents.length == 0) {
                    /* checks if there are any subdirectories in contents list to delete
                     and see if those subdirectories are empty */
                    if (dir.delete()) {
                        System.out.println("Deleted empty directory: " + dir.getName());
                        deleted = true;
                    }
                }
            }
            if (!deleted) System.out.println("No empty directories found");
            return;
        }

        File dir;
        // checks if argument given is full or relative(short) path
        if (new File(argument).isAbsolute()) {
            // given full path
            dir = new File(argument);
        } else {
            // if no path given (short path) consider it the current working path
            dir = new File(currentdir, argument);
        }

        // checks if directory exists
        if (!dir.exists()) {
            System.out.println("Directory not found: " + dir.getAbsolutePath());
        }
        // checks if it's a directory not a file
        else if (!dir.isDirectory()) {
            System.out.println("Error: Not a directory");
        }

        else {
            // puts them in a list called contents
            String[] contents = dir.list();
            if (contents != null && contents.length == 0) {
                /*  checks if there are directories to delete in contents list
                  and if they are empty directories then attempt to delete them */
                if (dir.delete()) {
                    System.out.println("Directory deleted: " + dir.getAbsolutePath());
                } else {
                    System.out.println("Failed to delete: " + dir.getAbsolutePath());
                }
            } else {
                System.out.println(dir.getName() + " is not empty");
            }
        }
    }
    
    public void touch(String[] args) {
        // create a file with full/relative path
        if ( args == null || args.length == 0) {
            System.out.println("Error: enter file name");
            return;
        }
        String path = args[0];
        File file = new File(path);

        File parent = file.getParentFile();
        //if no path given use the current working directory
        if ( parent == null ) {
            parent = new File(System.getProperty("user.dir"));
        }

        // if there is no directory with this name print error
        if( !parent.exists() ) {
            System.out.println("Error: Parent directory does not exist: " + parent.getPath());
            return;
        }

        // if file with same name already exists print error
        if ( file.exists() ) {
            System.out.println("Error: A file named " + path + " already exists");
            return;
        }
        try {
            boolean created = file.createNewFile();
            if (created) {
                System.out.println("File " + path +  " created successfully" );
            }
            else{
                System.out.println("File " + path +  " failed to be created" );
            }

        } catch (IOException e) {
            System.out.println("touch: I/O error: " + e.getMessage());
        }
    }

    public void rm(String[] args) {
        if (args == null || args.length !=1 ) {
            System.out.println("Error: enter file name");
            return;
        }
        String filename = args[0];
        File file = new File(currentdir, filename);
        // check if file exists to remove it
        if ( !file.exists() ){
            System.out.println("Error: " + filename + " file not found");
            return;
        }
        
        // checks if file is a directory not a file 
        if( file.isDirectory() ){
            System.out.println("Error: cannot remove: " + filename + " is a directory");
            return;
        }

        boolean deleted = file.delete();
        if (deleted){
            System.out.println("File deleted: " + filename );
        }
        else{
            System.out.println("Failed to delete file: " + filename );
        }
    }
    
    public void cat(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter at least one file name");
            return;
        }

        for (String filename : args) {
            File file = new File(pwd() + File.separator + filename);
            if (!file.exists()) {
                System.out.println("File does not exist: " + filename);
                continue;
            }
            try (Scanner s = new Scanner(file)) {
                while (s.hasNextLine()) {
                    System.out.println(s.nextLine());
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error opening file: " + filename);
            }
        }
    }

    public void zip() {
    // Get the class name and assume it's the current Java file
    String sourceFileName = "Terminal.java";
    String zipFileName = "Terminal.zip";

    File sourceFile = new File(currentdir, sourceFileName);

    if (!sourceFile.exists()) {
        System.out.println("Error: File not found - " + sourceFile.getAbsolutePath());
        return;
    }

    try (
        FileOutputStream fos = new FileOutputStream(new File(currentdir, zipFileName));
        ZipOutputStream zos = new ZipOutputStream(fos);
        FileInputStream fis = new FileInputStream(sourceFile)
    ) {
        ZipEntry zipEntry = new ZipEntry(sourceFile.getName());
        zos.putNextEntry(zipEntry);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, length);
        }

        zos.closeEntry();
        System.out.println("File zipped successfully to: " + zipFileName);

    } catch (IOException e) {
        System.out.println("Error while zipping: " + e.getMessage());
    }
}

    

   
      

    public void chooseCommandAction()
    {
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
                
            case "rmdir":
                rmdir();
                break;
                
            case "rm":
                rm();
                break;
                
            case "touch":
                touch();
                break;
                
            case "zip":
                 zip();
                 break;
                
            default:
                System.out.println("There is no such command, please try again!");
                break;
        }
    }


    public static void main(String[] args) {
        Terminal terminal = new Terminal();
        Scanner scanner = new Scanner(System.in);

        while (true)  
        {
            System.out.print("Enter command: ");
            String command = scanner.nextLine();

            if (terminal.parser.parse(command))
                terminal.chooseCommandAction();
            else
                System.out.println("Invalid command");
        }

    }

   }
