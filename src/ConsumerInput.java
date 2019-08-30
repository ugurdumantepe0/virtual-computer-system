import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;





public class ConsumerInput extends Thread {
    private ProducerConsumer scheduler;
    private  boolean isRunning ;
    private volatile List<ProcessImage> blockedQueue;
    private volatile List<ProcessImage> readyQueue;

    private ArrayList<Integer> consoleQueue;
    private Semaphore mtx;
    private Semaphore mtxMain;
    private ProducerInput producer;
    public ConsumerInput(ProducerInput producerInput,Semaphore mutex,
                         List<ProcessImage> readyInput,List<ProcessImage> blockedQ,Semaphore mutexMain ){

        isRunning = true;
        producer = producerInput;
        mtx = mutex;
        readyQueue = readyInput;
        blockedQueue = blockedQ;
        scheduler = new ProducerConsumer();
        this.mtxMain = mutexMain;

    }


    public void setConsoleQueue(ArrayList<Integer> consoleInput){
        consoleQueue = consoleInput;
    }

    @Override
    public void run() {

        System.out.println("ConsumerInput is running");
        int size, inputValue;

        try {
            while (isRunning) {


                mtx.acquire();
                size = consoleQueue.size();
                mtx.release();

                while(isRunning&&size == 0) {
                    pause();

                    mtx.acquire();
                    size = consoleQueue.size();
                    mtx.release();
                }
                if(isRunning!=true)
                    break;


                mtx.acquire();

                inputValue = consoleQueue.get(0);
                consoleQueue.remove(0);
                producer.work();
                ;
                boolean isBlockedQueueEmpty = blockedQueue.isEmpty();

                mtx.release();


                if (!isBlockedQueueEmpty) {

                    System.out.println("blocked isnt empty");

                    mtxMain.acquire();
                    ProcessImage p = blockedQueue.get(0);
                    blockedQueue.remove(0);
                    p.V = inputValue;
                    readyQueue.add(p);
                    mtxMain.release();


                }
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ConsumerInput exiting");

    }


    public void pause() {
        System.out.println("ConsumerInput is paused");

        scheduler.pause();}

    public void work() {
        System.out.println("ConsumerInput is working");
        scheduler.work();}

    public void stopThread() {
        System.out.println("ConsumerInput is stopped");
        scheduler.work();

        isRunning = false;
    }
}
