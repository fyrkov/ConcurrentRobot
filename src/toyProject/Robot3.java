package toyProject;


import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by anch0317 on 03.03.2017.
 */
public class Robot3 implements IRobot {

    List<Step> list = new LinkedList<>();
    volatile Lock lock = new ReentrantLock();
    AtomicInteger stepOrder = new AtomicInteger();
    AtomicBoolean stepDone = new AtomicBoolean(true);
    private volatile double distance;
    private AtomicInteger stepCounter;
    private volatile int legs;
    private volatile boolean isInterrupted;

    public Robot3(int legsQuantity, double distance) {
        legs = legsQuantity;
        this.distance = distance;
        stepCounter = new AtomicInteger();
        for (int i = 0; i < legs; i++) {
            list.add(new Step(i));
        }
    }

    public void setLegs(int legs) {
        this.legs = legs;
    }

    public void interrupt() {
        isInterrupted = true;
    }

    public void run() {

        list.forEach(s -> {
            s.setDaemon(true);
            s.start();
        });

        while (distance > 0 && !isInterrupted) {
            for (int i = 0; i < legs; i++) {
                lock.lock();
                System.out.println("main has the lock");
                stepDone.set(false);
                stepOrder.set(i);
                System.out.println("order set to i = "+i);
                lock.unlock();
                System.out.println("main release the lock");
                while (!stepDone.get()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                stepDone.set(true);
            }
        }
    }


    class Step extends Thread {

        int stepNumber;
        private int legNumber;

        Step(int legNumber) {
            this.legNumber = legNumber;
        }

        @Override
        public void run() {
            while (true) {
                if (stepOrder.get() == legNumber && !stepDone.get()) {
                    System.out.println("thread " + legNumber +" flied through if, params are "+stepOrder.get()+" "+!stepDone.get());
                    lock.lock();
                    System.out.println("thread " + legNumber +" has the lock");
                    distance -= (Math.random() + 0.5);
                    stepNumber = stepCounter.incrementAndGet();
                    DecimalFormat f = new DecimalFormat("#0.00");
                    String s = "Robot moved with leg " + legNumber + ", step " + stepNumber + ", distance is: " + f.format(distance) + "\n";
//                GUI.appendText(s);
                    System.out.print(s);
                    try {
                        sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    stepDone.set(true);
                    lock.unlock();
                    System.out.println("thread " + legNumber +" released the lock");
                }
            }
        }
    }

}




