import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Process {

    int id;
    int pc;
    States state;
    ArrayList<String> inst;

    static Scanner sc = new Scanner(System.in);

    Process(int id, ArrayList<String> inst){
        this.id = id;
        this.pc = 0;
        this.inst = inst;
        this.state = States.NEW;
    }

    public String toString(){
        return "id "+id+" State "+state+" PC " +pc;
    }

    public void execute() throws IOException{

        this.state = States.RUNNING;

        for (int i = 1; i<= OS.timeSlice && state==States.RUNNING; i++) {

            if (pc<this.inst.size()) {

                String curInst = this.inst.get(pc);
                StringTokenizer st = new StringTokenizer(curInst);
                String instName = st.nextToken();
                System.out.println(curInst+" Of "+this);
                System.out.println("---------------------------------------");

                if (instName.equals("print")) {
                    String toPrint = st.nextToken();
                    OS.print(toPrint,id);
                }
                else if (instName.equals("assign")) {
                    String var = st.nextToken();
                    String value = st.nextToken();
                    if(value.equals("input")){

                        System.out.println("Please enter an input for variable "+var);
                        System.out.println("---------------------------------------");

                        value = sc.next();
                        OS.assign(var,value,id);

                    }else if(value.equals("readFile")){

                        String var1 = st.nextToken();
                        value = OS.readFile(var1);

                        if(value==null){
                            System.out.println("There are no file with this variable");
                            return;
                        }
                        OS.assign(var,value,id);

                    }
                    else{
                        OS.assign(var,value,id);
                    }
                    System.out.println("new value for "+var+ " is "+value);
                }
                else if (instName.equals("writeFile")) {

                    String fileName = st.nextToken();
                    String data = st.nextToken();
                    OS.writeFile(data, fileName);

                }
                else if (instName.equals("readFile")) {

                    String fileName = st.nextToken();
                    OS.readFile(fileName);

                }
                else if (instName.equals("printFromTo")) {

                    String int1 = st.nextToken();
                    String int2 = st.nextToken();
                    int n1 = OS.memory.get(int1,this.id).intData;
                    int n2 = OS.memory.get(int2,this.id).intData;
                    OS.printFromTo(n1, n2);

                }
                else if (instName.equals("semWait")) {

                    String resource = st.nextToken();
                    OS.semWait(resource, this);

                }
                else if (instName.equals("semSignal")) {

                    String resource = st.nextToken();
                    OS.semSignal(resource, this);

                }

                pc++;
                // in case the instructions is finished the process state mush be changed
                if(pc == inst.size()){
                    this.state = States.FINISHED;
                }

                OS.time++;
                OS.add(); // initialise a new process (if any)

                System.out.println("Time"+OS.time);
                System.out.println("OS ready queue"+OS.readyQueue);
                System.out.println("Blocked Queue"+ Arrays.toString(OS.srcQueue));
            }
        }
    }

}
