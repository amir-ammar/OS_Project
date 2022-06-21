import java.io.*;
import java.util.*;

public class Memory {

    // 0, 1, 2, 3, 4 -> for the PCBs
    // 5 -> for the ready queue
    // 6 -> for the waiting queue (user input)
    // 7 -> for the waiting queue (user output)
    // 8 -> for the waiting queue (file)
    // 9 -> resources array
    // 10 -> for the start of the memory
    public void addToReadyQueue(Process p){
        ((Queue<Process>)memory[5]).add(p);
    }
    public void addToBlockedQueue(String src,Process p){
        if(src.equals("userInput")){
            ((Queue<Process>)memory[6]).add(p);
            ((Process[])memory[9])[0] = p;
        }else if(src.equals("userOutput")){
            ((Queue<Process>)memory[7]).add(p);
            ((Process[])memory[9])[1] = p;
        }else{
            ((Queue<Process>)memory[8]).add(p);
            ((Process[])memory[9])[2] = p;
        }
    }

    private Object [] memory;
    public int mp; // memory pointer (starts from 9)
    public int processPointer;
    public int swapPointer;
    public HashMap<Integer, Integer> processMap; //  start address -> process id
    public Process[] getSrc(){
        return (Process[]) memory[9];
    }
    public Queue<Process> getReadyQueue(){
        return (Queue<Process>)memory[5];
    }
    public Queue<Process> getBlockedQueueUserInput(){
        return (Queue<Process>)memory[6];
    }
    public Queue<Process> getBlockedQueueUserOutput(){
        return (Queue<Process>)memory[7];
    }
    public Queue<Process> getBlockedQueueFile(){
        return (Queue<Process>)memory[8];
    }
    public String getInstruction(int pc,Process p){
        return (String)memory[p.getPcb().getStart()+pc];
    }
    public Memory(int size) {
        memory = new Object[size];
        memory[5] = new LinkedList<Process>();
        memory[6] = new LinkedList<Process>();
        memory[7] = new LinkedList<Process>();
        memory[8] = new LinkedList<Process>();
        memory[9] = new Process[3];
        mp = 9;
    }

    public void set(int index, Object value) {
        memory[index] = value;
    }

    public Object get(int index) {
        return memory[index];
    }

    public void readProcess(String path, int id) {

        if (processPointer == 5) {
            // not enough space in the kernel
            return;
        }
            int size = countFileLines(path, id);
            // need to swap another process
            if (40 - mp < size) {
                swap(path, id, true);
            } else {
                Process newProcess = new Process(new PCB(id, States.NEW, 0, mp, mp + size + 2, Location.Memory));
                memory[processPointer++] = newProcess;
                newProcess.getPcb().setState(States.READY);
                addToReadyQueue(newProcess);
                processMap.put(mp, id);
                writeToMemory(mp, mp + size + 2, id, 0, path);
                mp += size + 3;
        }
    }

    public void compactData(int start, int end) {
        if (end + 1 == mp) return;

        // to modify the information of the pcb's in the kernal
        for (int i = start + 1; i < mp; i++) {
            if (processMap.containsKey(i)) {
                Process process = getProcess(processMap.get(i));
                int size = process.getPcb().getSize();
                process.getPcb().setStart(start);
                process.getPcb().setEnd(start + size - 1);
                processMap.remove(i);
                processMap.put(start, process.getPcb().getID());
                start += size;
            }
        }

        // to move the data
        for (int i = end + 1; i < mp; i++) {
            if (memory[i] != null) {
                memory[start++] = memory[i];
            }
        }

        // to modify the mp
        mp = start;

        // nullify the memory from mp to the end
        for (int i = mp; i < 40; i++) {
            memory[i] = null;
        }


    }

    public void compactKernal(int index) {

        for (int i = index; i < processPointer - 1; i++) {
            memory[i] = memory[i + 1];
        }

        memory[processPointer - 1] = null;
        processPointer--;
    }

    public Process getProcess(int id) {
        for (int i = 0; i < processPointer; i++) {
            Process process = (Process)memory[i];
            if (process.getPcb().getID() == id) {
                return (Process) memory[i];
            }
        }
        return null;
    }

    public void removeProcess(int id) {
        for (int i = 0; i < processPointer; i++) {
            Process process = (Process)memory[i];
            if (process.getPcb().getID() == id) {
                compactKernal(i);
                compactData(process.getPcb().getStart(), process.getPcb().getEnd());
            }
        }
    }





    public void swap(String path, int id, boolean isNew) {


        int size = countFileLines(path, id) + 3;
        int start = 0;
        boolean f = false;
        int skip = 0;

        while (size > skip) {

            if (memory[swapPointer] != null) {
                Process process = (Process)memory[swapPointer];
                if (size - skip > process.getPcb().getSize()) {

                    if (!f) {start = process.getPcb().getStart(); f = true;}

                    writeToDisk(process.getPcb().getStart(), process.getPcb().getEnd(), process.getPcb().getID(), path);
                    writeToMemory(process.getPcb().getStart(), process.getPcb().getEnd(), id, skip, path);

                } else if (size - skip < process.getPcb().getSize()){

                    writeToDisk(process.getPcb().getStart(), process.getPcb().getEnd(), process.getPcb().getID(), path);
                    writeToMemory(process.getPcb().getStart(), process.getPcb().getStart() + (size - skip) - 1, id, skip, path);
                    compactData(process.getPcb().getStart() + size, process.getPcb().getEnd());

                } else {

                    writeToDisk(process.getPcb().getStart(), process.getPcb().getEnd(), process.getPcb().getID(), path);
                    writeToMemory(process.getPcb().getStart(), process.getPcb().getEnd(), id, skip, path);

                }

                processMap.remove(process.getPcb().getStart());
                process.getPcb().setLocation(Location.Disk);
                skip += process.getPcb().getSize();
            }

            swapPointer = (swapPointer + 1) % 5;
        }

        if (isNew) {
            Process newProcess = new Process(new PCB(id, States.NEW, 0, start, start + size - 1, Location.Memory));
            memory[processPointer++] = newProcess;
            processMap.put(start, id);
        } else {
            Process process = getProcess(id);
            process.getPcb().setLocation(Location.Memory);
            processMap.remove(process.getPcb().getStart());
            process.getPcb().setStart(start);
            process.getPcb().setEnd(start + size - 1);
            processMap.put(start, id);
        }
    }


    public void writeToDisk(int start, int end, int id, String path) {

        File file = new File(path + "_" + id + ".txt");

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (int i = start; i <= end; i++) {
                bw.write((String)memory[i]);
                bw.newLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void writeToMemory(int start, int end, int id, int skip, String path) {

        File file = new File(path + "_" + id + ".txt");
        int index = 0;
        int var = 0;
        try {

            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            while (index < skip) {
                br.readLine();
                index++;
            }

            for (int i = start; i <= end; i++) {
                String line = br.readLine();
                if (line != null) memory[i] = br.readLine();
                else memory[i] = new StringBuilder("" + (char)('a' + var++) + " ");
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public int countFileLines(String fileName, int id) {
        int count = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName + "_" + id + ".txt"));
            while (br.readLine() != null) {
                count++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }



}
