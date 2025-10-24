import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.*;
import java.util.zip.*;

class Parser {
    String commandName;
    String[] args;
    String outputFile = null;
    boolean appendMode = false;

    public boolean parse(String input) {
        // tirm() remove the whitespaces before and after the string
        // split("\\s+") removes any whitespaces inside the string and split it after each whitespace
        String[] tokens = input.trim().split("\\s+");

        if (tokens.length == 0) // if the input is empty
            return false;

        commandName = tokens[0];
        args = new String[tokens.length - 1];// make size of args without the first word (command)
        outputFile = null;
        appendMode = false;
        int argCount = 0;

        // make i begains with 1, to take args only not first word (command) in tokens array
        for (int i = 1; i < tokens.length; i++) {
            if (tokens[i].equals(">") || tokens[i].equals(">>")) {
                appendMode = tokens[i].equals(">>");
                if (i + 1 < tokens.length) {
                    outputFile = tokens[i + 1];
                } else {
                    System.out.println("Error: Missing file name after " + tokens[i]);
                    return false;
                }
                break; // stop parsing after redirection
            } else {
                args[argCount++] = tokens[i];
            }
        }

        args = Arrays.copyOf(args, argCount);
        return true;
    }
    public String getOutputFile() {
        return outputFile;
    }

    public boolean isAppendMode() {
        return appendMode;
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

    public String ls() {
        // create file object points to current directory
        File file = currentdir;
        String[] files = file.list();

        if (files == null)
            return("Directory is empty or can't be accessed!");
        else {
            // Arrays is a core utility class in java
            Arrays.sort(files); //You don’t create an Arrays object — instead, you call its static methods directly
            // create stringBuilder to store the result string init 
            StringBuilder sb = new StringBuilder();
            for(String f: files){
                // appedn each name followed by a newline
                sb.append(f).append("\n");
            }
            //convert stirngbuilder to a string and return it 
            return sb.toString();
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
                    System.out.println("Directory deleted: " + dir.getParentFile());
                }else {
                    System.out.println("Failed to delete: " + dir.getAbsolutePath());
                }
            } else {
                System.out.println(dir.getName() + " is not empty");
            }
        }
    }

public void cp(File f1,File  f2){

    // if file paths are relative(short) paths resolve it to current directory
        if(!f1.isAbsolute())
            f1 = new File(currentdir, f1.getPath());

        if(!f2.isAbsolute())
            f2 = new File(currentdir, f2.getPath());
        
        // if source file doesn't exist show error
        if(!f1.exists()){
            System.out.println("Error: " + f1.getPath() + " does not exist");
        }

try{
    
BufferedReader source =new BufferedReader(new FileReader(f1));  // read data from  the source file
BufferedWriter dest =new BufferedWriter(new FileWriter(f2));   // dest file

String line;  // add variable to read the file line by line 

while ((line=source.readLine())!=null) {    // loop until the end of the file  to copy the first file to sec one 
    
     dest.write(line);
     dest.newLine();

   }
dest.flush();   // tells the stream: "Don't wait for the buffer to be full. Send whatever data you have in the buffer right now."
System.out.println("File copied successfully.");  // confirm message 
}  



catch(IOException e){  // catch any exceptions 
System.out.println("Error: " + e.getMessage());
}
}


public void cp_r(File dir1, File dir2) {
    // if directory paths are relative(short) paths resolve it to current directory
        if(!dir1.isAbsolute())
            dir1 = new File(currentdir, dir1.getPath());

        if(!dir2.isAbsolute())
            dir2 = new File(currentdir, dir2.getPath());
    
        if (!dir1.exists()) {    //check if the dir is exist
            System.out.println("Error: Source directory not found!");
            return;
        }

        if (!dir1.isDirectory()) {  //check if this file is a directory
            System.out.println("Error: Source is not a directory!");
            return;
        }

        if (!dir2.exists()) {
            dir2.mkdirs(); // edit
        }

        File[] files = dir1.listFiles();     //get each file in the dir in a array 
        if (files != null) {  //check if the file is empty or not
            for (File file : files) {
                File newDest = new File(dir2, file.getName());  
                if (file.isDirectory()) {
                    cp_r(file, newDest);   // transfer the file to  dir2
                } else {
                    cp(file, newDest);   
                }
            }
        }
        else{
            System.out.println("Source Directory is empty.");
        }
        System.out.println("Directory copied successfully!");
    }


public void wc(File f){
// resolve relative (short) path using current directory
if(!f.isAbsolute())
    f = new File(currentdir, f.getPath());
    
if(!f.exists()){  //check if the file is exist
    System.out.println("Error: the file not found!");
    return;
}

int line_count=0;
int word_count=0;
int char_count=0;

try(BufferedReader myfile =new BufferedReader(new FileReader(f))){
    
 String line_;

while ((line_=myfile.readLine())!=null) {
   line_count ++;
   char_count+=line_.length()+1;
   String[] words = line_.trim().split("\\s+");
   if (!line_.trim().isEmpty()) {
       word_count += words.length;
    }
   }
   System.out.println(line_count+" "+word_count+" "+char_count+" "+f.getName());

}
catch(IOException e ){
System.out.println("Error: "+e.getMessage());

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
        
        // check if path is relative(short) path 
        if( !file.isAbsolute() ){
            file = new File(currentdir, path);
        }

        File parent = file.getParentFile();
        //if no path given use the current working directory
        if ( parent == null ) {
            parent = currentdir;
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
    
    public void handleRedirection(String output) {
        String outputFile = parser.getOutputFile();
        boolean append = parser.isAppendMode();

        if (outputFile == null) {
            System.out.print(output); // print normally to console
        } else {
            File file = new File(currentdir, outputFile);
            try (FileWriter fw = new FileWriter(file, append);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(output);
            } catch (IOException e) {
                System.out.println("Redirection error: " + e.getMessage());
            }
        }
    }

    public void zip(String[] args) {
        // If the user didn’t type a file name, show an error
        if (args == null || args.length == 0) {
            System.out.println("Error: please enter a file name to zip");
            return;
        }
        String sourceFileName = args[0];
        String zipFileName = sourceFileName + ".zip";

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

    public void zip_r(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("Error: please enter a folder name to zip");
            return;
        }

        String sourceName = args[0];
        String zipFileName = sourceName + ".zip";
        File sourceDir = new File(currentdir, sourceName);

        //Check if the directory exists
        if (!sourceDir.exists()) {
            System.out.println("Error: Folder not found - " + sourceDir.getAbsolutePath());
            return;
        }

        //Ensure it's a directory (not a file)
        if (!sourceDir.isDirectory()) {
            System.out.println("Error: The provided name is not a directory");
            return;
        }

        try (
                FileOutputStream fos = new FileOutputStream(new File(currentdir, zipFileName));
                ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
            byte[] buffer = new byte[1024];

            //Walk recursively through directory and add files
            Files.walk(sourceDir.toPath())
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile())) {
                            // Preserve folder structure inside zip
                            String zipEntryName = sourceDir.toPath().relativize(path).toString().replace("\\", "/");
                            ZipEntry entry = new ZipEntry(zipEntryName);
                            zos.putNextEntry(entry);

                            int len;
                            while ((len = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, len);
                            }

                            zos.closeEntry();
                        } catch (IOException e) {
                            System.out.println("Error adding file: " + path + " -> " + e.getMessage());
                        }
                    });

            System.out.println("Folder zipped successfully to: " + zipFileName);

        } catch (IOException e) {
            System.out.println("Error while zipping: " + e.getMessage());
        }
    }

    public void unzip(String[] args) {
        File zipFile = null;

        // Case 1: no parameters -> unzip first .zip file in current directory
        if (args.length == 0) {
            File[] zipFiles = currentdir.listFiles((dir, name) -> name.toLowerCase().endsWith(".zip"));

            if (zipFiles == null || zipFiles.length == 0) {
                System.out.println("No .zip files found in current directory: " + currentdir.getAbsolutePath());
                return;
            }

            zipFile = zipFiles[0]; // pick the first zip file found
        }
        // Case 2: unzip <filename>
        else {
            String zipFileName = args[0];
            if (!zipFileName.toLowerCase().endsWith(".zip")) {
                zipFileName += ".zip";
            }

            zipFile = new File(currentdir, zipFileName);

            if (!zipFile.exists()) {
                System.out.println("Error: File not found - " + zipFile.getAbsolutePath());
                return;
            }
        }

        // --- Unzipping process ---
        System.out.println("Unzipping: " + zipFile.getName());
        File destDir = currentdir; // Unzip into current directory

        // Unzip safely
        try (FileInputStream fis = new FileInputStream(zipFile);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ZipInputStream zis = new ZipInputStream(bis)) {

            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = new File(destDir, zipEntry.getName());



                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        System.out.println("Failed to create directory " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.exists() && !parent.mkdirs()) {
                        System.out.println("Failed to create directory for file " + newFile);
                        continue;
                    }
                }

                zis.closeEntry();
            }

            System.out.println("Successfully unzipped: " + zipFile.getName());
        } catch (IOException e) {
            System.out.println("Error while unzipping: " + e.getMessage());
            return;
        }


        // --- Delete zip file safely ---
        if (zipFile.delete()) {
            System.out.println("Deleted zip file: " + zipFile.getName());
        } else
              {
                System.out.println("Could not delete zip file: " + zipFile.getAbsolutePath());
            }

    }

    public void chooseCommandAction() {
        String command = parser.getCommandName();
        String[] args = parser.getArgs();
        String output = ""; // to check if there > or >> or not

        switch (command) {
            case "pwd":
                System.out.println(pwd()); // printing the string returned from the method
                break;

            case "cp":
                    if (args.length == 2) {
                        cp(new File(args[0]), new File(args[1]));
                    } else if (args.length == 3 && args[0].equals("-r")) {
                        cp_r(new File(args[1]), new File(args[2]));
                    } else {
                        System.out.println("Usage: cp [ -r ] <source> <destination>");
                    }
                    break;    

            case "cp_r":
                   if (args.length == 2) {
                       cp_r(new File(args[0]), new File(args[1]));
                   }   
                   else {
                       System.out.println("Usage: cp_r <sourceDirectory> <destinationDirectory>");
                   }
                   break;    

                case "wc":
                    if (args.length == 1) {
                        wc(new File(args[0]));
                    } else {
                        System.out.println("Usage: wc <filename>");
                    }
                    break;     

            case "cd":
                cd();
                break;

            case "ls":
                output = ls();
                break;

            case "mkdir":
                mkdir();
                break;
                
            case "rmdir":
                rmdir(args);
                break;
                
            case "rm":
                rm(args);
                break;
                
            case "touch":
                touch(args);
                break;

            case "cat":  
                cat(args);
                break;

            case "zip":
                zip(args);
                break;

            case "unzip":
                unzip(args);
                break;

            case "exit":
                System.out.println("Exiting...");
                System.exit(0);    
                
            default:
                System.out.println("There is no such command, please try again!");
                break;
        }
         handleRedirection(output);

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
    


