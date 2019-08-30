public class ProducerConsumer {


    public synchronized void pause(){

            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

    }


    public synchronized void work(){
                notify();
    }
}
