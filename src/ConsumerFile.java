import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsumerFile extends Thread {
    private ProducerConsumer scheduler;
    private Semaphore mtx;
    private Semaphore mtxMain;

    private Assembler assembler;
    private Memory memory;
    private ProducerFile producer;
    private volatile ArrayList<ProcessImage> fileQueue;
    private List<ProcessImage> readyQueue;
    private boolean isRunning;
    String bitmap = "";
    private int sizeLimit;


    public ConsumerFile(ProducerFile producerInput, Semaphore mutexInput,
                        ArrayList<ProcessImage> fileQueueInput, Assembler assemblerInput,
                        List<ProcessImage> readyQueueInput, Memory memoryInput,Semaphore mutexMain) {

        this.mtx = mutexInput;
        this.sizeLimit = memoryInput.getMemorySize();
        this.fileQueue = fileQueueInput;
        this.assembler = assemblerInput;
        this.producer = producerInput;
        this.readyQueue = readyQueueInput;
        this.memory = memoryInput;
        this.mtxMain = mutexMain;
        scheduler = new ProducerConsumer();

        isRunning = true;

        this.bitmap =  new String(new char[sizeLimit]).replace('\0', '0');

        System.out.println(bitmap);

    }

    public void deallocateProcess(ProcessImage process){

        StringBuilder newBitmap = new StringBuilder(bitmap);

        for(int i = process.BR;i<process.BR+process.LR;++i){
            newBitmap.setCharAt(i,'0');
        }
        bitmap = newBitmap.toString();

        System.out.println("Deallocated : ");
        System.out.println(bitmap);
    }
    @Override
    public void run() {

        System.out.println("ConsumerFile is running");
        int allocated = 0;
        int fileSize = 0;
        ProcessImage process;

        while (isRunning) {
            try {

                mtx.acquire();
                fileSize = fileQueue.size();
                mtx.release();


                if (fileSize == 0) {
                    pause();
                    continue;
                }


                process = fileQueue.get(0);
                fileQueue.remove(0);
                producer.work();
                while (true) {
                    //System.out.println(process.LR);
                    allocated = allocate(process.LR);
                    if (allocated == -1) {
                        Thread.sleep(2000);
                        continue;
                    } else {

                        char[] toMemory = assembler.readBinaryFile(process.LR, process.processName);

                        try {
                            mtxMain.acquire();
                            readyQueue.add(new ProcessImage(process.processName, allocated, process.LR));
                            mtxMain.release();
                            memory.addInstructions(toMemory, process.LR, allocated); // memory e koydu.
                            System.out.println("Process is loaded !\n");

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        break;

                    }

                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

        System.out.println("ConsumerFile exiting");

    }


    int allocate(int size) {

        String token;
        Pattern pattern = Pattern.compile("[" + '0' + "]{" + size + "}");
        Matcher matcher = pattern.matcher(bitmap);


        if (matcher.find()) {
            token = matcher.group(0);
            int index = bitmap.indexOf(token);
            StringBuilder newBitmap = new StringBuilder(bitmap);

            for (int i = index; i < index + size; ++i)
                newBitmap.setCharAt(i, '1');

            bitmap = newBitmap.toString();
            System.out.println("Allocated : ");
            System.out.println(bitmap);
            return index;
        } else {
            return -1;
        }


    }

    public void stopThread() {
        System.out.println("ConsumerFile is stopped");

        scheduler.work();
        isRunning = false; }

    public void pause() {
        System.out.println("ConsumerFile has paused");

        scheduler.pause();}

    public void work() {
        System.out.println("ConsumerFile is working");
        scheduler.work();}
}
