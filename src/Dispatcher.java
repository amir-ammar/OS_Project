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

            System.out.println("Time " + OS.time + " READY Queue in "+ OS.readyQueue);
            System.out.println("---------------------------------------");

            if(!OS.readyQueue.isEmpty()){

                Process p = OS.readyQueue.poll();
                System.out.println("Current running process "+p);
                System.out.println("---------------------------------------");

                try {
                    p.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // if the process after executing 2 instructions not finished or blocked for some resource it should be returned to the ready queue
                if(p.state != States.FINISHED && p.state!=States.BLOCKED) {
                    p.state = States.READY;
                    OS.readyQueue.add(p);
                }

            }
            else{
                OS.flag = false;
                break;
            }
        }

    }
}
