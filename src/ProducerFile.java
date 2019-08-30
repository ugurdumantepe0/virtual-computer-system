import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class ProducerFile extends Thread {

    private ProducerConsumer scheduler;
    private ConsumerFile consumer;
    private Semaphore mtx;
    private Assembler assembler;
    private int sizeLimit;
    private volatile ArrayList<ProcessImage> fileQueue;
    private boolean isRunning;
    private Semaphore mtxMain;

    public void pause(){
        System.out.println("ProducerFile has paused");
        scheduler.pause();}
    public void work(){
        System.out.println("ProducerFile is working");
        scheduler.work();}


    public void setConsumerFile(ConsumerFile inputConsumerFile){
        consumer = inputConsumerFile;
    }

    public void startConsumer(){
        consumer.start();
    }



    public ProducerFile(Semaphore mutexInput,int size, ArrayList<ProcessImage> fileQueueInput,
                        Assembler assemblerInput,Semaphore mutexMain ){

        this.mtx = mutexInput;
        this.sizeLimit = size;
        this.fileQueue = fileQueueInput;
        this.assembler = assemblerInput;
        this.mtxMain = mutexMain;

        isRunning = true;
        scheduler = new ProducerConsumer();


    }


    @Override
    public void run(){


        System.out.println("ProducerFile is running");

        String processLine = "";
        BufferedReader br = null;
        try {
            br=new BufferedReader(new InputStreamReader(new FileInputStream("inputSequence.txt"), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        while(isRunning){

            try {
                if(((processLine = br.readLine()) == null) || (processLine.trim().isEmpty()==true)) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            System.out.println();
            int fileSize = 0;
            try {
                mtx.acquire();
                fileSize = fileQueue.size();
                mtx.release();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(fileSize>sizeLimit){
                pause();
            }



            String[] tokens = processLine.split(" ");
            String outputFile = tokens[0].substring(0, tokens[0].indexOf("."))+".bin";

            ProcessImage process = new ProcessImage();
            process.processName = outputFile;
            process.LR = assembler.createBinaryFile(tokens[0], outputFile);

            try {
                mtx.acquire();
                System.out.println(process.processName);
                fileQueue.add(process);
                consumer.work();
                mtx.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(Integer.parseInt(tokens[1]));
                System.out.println("ProducerFile is sleeping "+Integer.parseInt(tokens[1]));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

        System.out.println("ProducerFile exiting");
    }

    public void stopThread() {

        System.out.println("ProducerFile is stopped");

        isRunning = false;
    }

}
