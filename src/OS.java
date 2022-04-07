import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;


public class OS {


    static Memory memory; // main memory
    static String path; // path to all files
    static Scanner input; // name of file
    static String programName;

    public static void printFromTo(int from, int to){
        for(int i = from; i <= to; i++){
            System.out.println(i);
        }
    }

    public static void readFile(String path) throws FileNotFoundException {
        try {
            File file = new File(path);
            Scanner read = new Scanner(file);
            while(read.hasNextLine()) {
                System.out.println(read.nextLine());
            }
        }
        catch (FileNotFoundException e){
            System.out.println("File not found");
        }

    }

    public static void writeFile(String data, String path) throws IOException {
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(data);
            writer.close();
        }
        catch (IOException e){
            System.out.println("File not found");
        }
    }

    public static void assign(String varName, String value){
        if(memory.contains(varName)){
            System.out.println("Variable " + varName + " already exists in memory");
        }else{
            try {
                int x = Integer.parseInt(value);
                memory.add(varName, new Type(x));
            }
            catch (NumberFormatException e){
                memory.add(varName, new Type(value));
            }
        }
    }

    public static void print(String varName){
        if(memory.contains(varName)){
            System.out.println(memory.get(varName));
        }
        else{
            System.out.println("Variable " + varName + " not found in memory");
        }
    }

    public static void semWait(){
        // TODO: implement semWait method here
    }

    public static void semSignal(){
        // TODO: implement semSignal method here
    }

    public static void init() { // initialize system
        memory = new Memory();
        path = "\\\\wsl$\\Ubuntu\\home\\amir\\";
        input = new Scanner(System.in);
        System.out.println("Welcome to the system, please enter program name");

    }

    public static void execute() throws IOException { // execute program
        programName = input.next(); // get program name
        File file = new File(path + programName + ".txt");
        Scanner sc = new Scanner(file);

        while(sc.hasNextLine()) {
            String line = sc.nextLine(); // read a line from the file (instruction)
            StringTokenizer st = new StringTokenizer(line);
            String command = st.nextToken();


            if(command.equals("printFromTo")) {
                int from = Integer.parseInt(st.nextToken());
                int to = Integer.parseInt(st.nextToken());
                printFromTo(from, to);
            }
            else if(command.equals("readFile")){
                readFile(path + st.nextToken() + ".txt");
            }
            else if(command.equals("writeFile")){
                String fileName = st.nextToken();
                String data = st.nextToken();
                writeFile(data, path + fileName + ".txt");
            }
            else if(command.equals("assign")){
                String varName = st.nextToken();
                String value = st.nextToken();
                assign(varName, value);
            }
            else if(command.equals("print")){
                print(st.nextToken());
            }
            else if(command.equals("semWait")){
                semWait();
            }
            else if(command.equals("semSignal")){
                semSignal();
            }
            else {
                System.out.println("Invalid command");
            }
        }
    }


    public static void main(String [] args) throws IOException {
        init();
        execute();
    }

}
