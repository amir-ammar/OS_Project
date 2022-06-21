import java.io.IOException;

public class Dispatcher extends Thread{

    public Dispatcher(){

    }
    public void run() {
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Time "+OS.Time + " READY Queue in "+OS.readyQueue);
            System.out.println("---------------------------------------");
            if(OS.memo.getReadyQueue().isEmpty()==false){
                Process p = OS.readyQueue.poll();
                System.out.println("Current running process "+p);
                System.out.println("---------------------------------------");
                try {
                    p.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(p.getPcb().getState() != States.FINISHED && p.getPcb().getState()!=States.BLOCKED) {
                    p.getPcb().setState(States.READY);
                    OS.memo.getReadyQueue().add(p);
                }
            }
            else{
                OS.flag = false;
                break;
            }
        }
    }
}
