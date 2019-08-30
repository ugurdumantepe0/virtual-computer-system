import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class ProducerInput extends Thread {

    private ProducerConsumer scheduler;
    private  boolean isRunning ;
    private volatile List<ProcessImage> blockedQueue;
    private ArrayList<Integer> consoleQueue;
    private Semaphore mtx;
    private int size;
    private ConsumerInput consumer;
    private Semaphore mtxMain;

    public void setConsumer(ConsumerInput consumerInput){
        consumer = consumerInput;
        consumer.setConsoleQueue(consoleQueue);
    }
    public ProducerInput(int sizeLimit,Semaphore mutex, List<ProcessImage> blockedQ,Semaphore mutexMain ){
         scheduler = new ProducerConsumer();
         isRunning = true;
         mtx = mutex;
         blockedQueue = blockedQ;
         consoleQueue = new ArrayList<>();
         size = sizeLimit;

        this.mtxMain = mutexMain;

    }



    @Override
    public void run() {
        int inputValue;

        ProcessImage lastProcess = new ProcessImage();
        System.out.println("ProducerInput is running");


        try {
            while (isRunning) {


                Scanner in = new Scanner(System.in);

                mtxMain.acquire();
                boolean isBlockedQueueEmpty = blockedQueue.isEmpty();
                ProcessImage process = null;
                if(!isBlockedQueueEmpty)
                    process = blockedQueue.get(0);
                mtxMain.release();

                if (!isBlockedQueueEmpty&&process!=lastProcess) {

                    lastProcess = process;
                    System.out.println("type a number: ");
                    inputValue = in.nextInt();


                    mtx.acquire();
                    int consoleSize = consoleQueue.size();
                    mtx.release();

                    if(consoleSize>size){
                        pause();
                    }

                    mtx.acquire();
                    consoleQueue.add(inputValue);
                    mtx.release();
                    consumer.work();

                }

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("ProducerInput exiting");

    }

    public void pause() {
        System.out.println("ProducerInput is paused");

        scheduler.pause();}

    public void work() {
        System.out.println("ProducerInput is working");
        scheduler.work();}

    public void stopThread() {
        System.out.println("ProducerInput is stopped");

        isRunning = false;
    }
}
