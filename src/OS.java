import java.io.*;
import java.util.*;


public class OS extends Thread{
    static int timeSlice = 2;
    static Memory memory; // main memory
    static String path; // path to all files
    static Scanner input; // name of file
    static Memory memo;
    static String programName;
    static Process[] src;
    static Queue<Process>[] srcQueue;
    static Queue<Process> readyQueue;
    static int Time =0;
    static int id =1;
    static boolean flag = true;
    public OS(){

    }



    /*public static void addNewProgram(String path){
        try {
            Process p = readProcess(path, id++);
            p.state = States.READY;
            readyQueue.add(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


    public static void add() {
        // this files path could be changed
        String firstProgramPath = "E:\\folder\\Semester6\\S6\\OS\\Project New\\OS_Project\\Program_1.txt";
        String secondProgramPath = "E:\\folder\\Semester6\\S6\\OS\\Project New\\OS_Project\\Program_2.txt";
        String thirdProgramPath = "E:\\folder\\Semester6\\S6\\OS\\Project New\\OS_Project\\Program_3.txt";
        if (Time == 0) {
            // read first program
            System.out.println("Time "+Time);
            System.out.println("first Program is added");
            System.out.println("---------------------------------------");
            // addNewProgram(firstProgramPath);
        }
        else if (Time == 1) {
            // read second program
            System.out.println("Time "+Time);
            System.out.println("second Program is added");
            System.out.println("---------------------------------------");
            // addNewProgram(secondProgramPath);
        }
        else if (Time == 4) {
            // read third program
            System.out.println("Time "+Time);
            System.out.println("third Program is added");
            System.out.println("---------------------------------------");
            // addNewProgram(thirdProgramPath);
        }
    }

    public static void printFromTo(int from, int to){
        for(int i = from; i <= to; i++){
            System.out.println(i);
        }
    }

    public static String readFile(String path) throws FileNotFoundException {
        try {
            File file = new File(path+".txt");
            Scanner read = new Scanner(file);
            String s = "";
            while(read.hasNextLine()) {
                s = s+read.nextLine();
            }
            return s;
        }
        catch (FileNotFoundException e){
            System.out.println("File not found");
        }
        return null;
    }

    public static void writeFile(String data, String path) throws IOException {
        try {
            FileOutputStream fos = new FileOutputStream(path+".txt");
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
        }
        catch (IOException e){
            System.out.println("File not found");
        }
    }

    public static void assign(String varName, String value,int id){
        if(memory.contains(varName)){
            System.out.println("Variable " + varName + " already exists in memory");
        }else{
            memory.add(varName, new Type(value),id);
        }
    }

    public static void print(String varName,int id){
        if(memory.contains(varName+id)){
            System.out.println(memory.get(varName,id));
        }
        else{
            System.out.println("Variable " + varName + " not found in memory");
        }
    }

    public static void semWait(String input,Process p){
        // userInput, userOutput or file
        if(input.equals("file")){
            if(memo.getSrc()[2]!=null){
                memo.getBlockedQueueFile().add(p);
                p.getPcb().setState(States.BLOCKED);
            }
            else{
                memo.getSrc()[2] = p;
            }
        }
        else if(input.equals("userInput")){
            if(memo.getSrc()[0]!=null){
                memo.getBlockedQueueUserInput().add(p);
                p.getPcb().setState(States.BLOCKED);
            }
            else{
                memo.getSrc()[0] = p;
            }
        }
        else{
            if(memo.getSrc()[1]!=null){
                memo.getBlockedQueueUserOutput().add(p);
                p.getPcb().setState(States.BLOCKED);
            }
            else{
                memo.getSrc()[1] = p;
            }
        }
    }

    public static void semSignal(String input,Process p){
        if(input.equals("file")){
            if(memo.getSrc()[2] != p) return;
            if(!memo.getBlockedQueueFile().isEmpty()){
                memo.getSrc()[2] = memo.getBlockedQueueFile().poll();
                memo.getSrc()[2].getPcb().setState(States.READY);
                memo.getReadyQueue().add(memo.getSrc()[2]);
            }
            else{
                memo.getSrc()[2] = null;
            }

        }
        else if(input.equals("userInput")){
            if(memo.getSrc()[0] != p) return;
            if(!memo.getBlockedQueueUserInput().isEmpty()){
                memo.getSrc()[0] = memo.getBlockedQueueUserInput().poll();
                memo.getSrc()[0].getPcb().setState(States.READY);
                memo.getReadyQueue().add(memo.getSrc()[0]);
            }
            else{
                memo.getSrc()[0] = null;
            }
        }
        else{
            if(memo.getSrc()[1] != p) return;
            if(!memo.getBlockedQueueUserInput().isEmpty()){
                memo.getSrc()[1] = memo.getBlockedQueueUserInput().poll();
                memo.getSrc()[1].getPcb().setState(States.READY);
                memo.getReadyQueue().add(memo.getSrc()[0]);
            }
            else{
                memo.getSrc()[1] = null;
            }
        }
    }

    public static void init() { // initialize system
        path = "\\\\wsl$\\Ubuntu\\home\\amir\\";
        input = new Scanner(System.in);
        memo = new Memory(40);
        OS os = new OS();
        os.add();
        Dispatcher dis = new Dispatcher();
        dis.start();
        System.out.println("Welcome to the system, please enter program name");
    }

    public static void main(String [] args) throws IOException {
        init();
    }
}
