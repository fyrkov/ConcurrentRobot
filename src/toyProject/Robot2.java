package toyProject;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by anch0317 on 03.03.2017.
 */
public class Robot2 implements IRobot {

    private List<AtomicBoolean> condition;
    private volatile double distance;
    private volatile int stepCounter;
    private volatile int legs;
    private volatile List<Thread> s;
    private AtomicBoolean legSetFlag = new AtomicBoolean();

    public Robot2(int legsQuantity, double distance) {
        legs = legsQuantity;
        this.distance = distance;
        condition = new ArrayList<>();
        s = new ArrayList<>();
        for (int i = 0; i < legs; i++) {
            s.add(i, new Step(i));
            condition.add(i, new AtomicBoolean());
        }
        cleanFile();
    }

    int getStepCounter() {
        return stepCounter;
    }

    //TODO legs addition
    public void setLegs(int legs) {
        legSetFlag.set(true);
        synchronized (this) {
            System.out.println("Setter works");
            int delta = legs - this.legs;
            if (delta > 0) {
                for (int i = this.legs; i < legs; i++) {
                    s.add(i, new Step(i));
                    if (condition.size() > i) condition.set(i, new AtomicBoolean());
                    else condition.add(i, new AtomicBoolean());
                    s.get(i).setDaemon(true);
                    s.get(i).start();
                }
            } else if (delta < 0) {
                for (int i = this.legs - 1; i >= legs; i--) {
                    s.get(i).interrupt();
                    s.remove(i);
                }
            }
            this.legs = legs;
            legSetFlag.set(false);
            try {
                //timeout for proper step threads interruption, may be changed to check loop before leaving sync block
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            notify();
            System.out.println("Setter released the lock");
        }
    }

    void startMoving() {

        for (int i = 0; i < legs; i++) {
            s.get(i).setDaemon(true);
            s.get(i).start();
        }

        synchronized (this) {
            while (distance > 0) {
                for (int i = 0; i < legs; i++) {
                    synchronized (s.get(i)) {
                        condition.get(i).set(true);
                        s.get(i).notify();
                        while (condition.get(i).get()) {
                            try {
                                s.get(i).wait();
                            } catch (InterruptedException e) {
                            }
                        }
                        if (distance <= 0) break;
                    }
                    if (legSetFlag.get()) try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    void write(String s) {

        //TODO freezes when increasing legs number
        try (FileWriter writer = new FileWriter("out.txt", true)) {
            writer.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void cleanFile() {
        try (PrintWriter writer = new PrintWriter(
                new FileWriter("out.txt"))) {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Step extends Thread {

        private int legNumber;

        Step(int legNumber) {
            this.legNumber = legNumber;
        }

        @Override
        public void run() {
            while (true) {
                while (condition.get(legNumber).get()) {
                    synchronized (this) {
                        distance -= (Math.random() + 0.5);
                        stepCounter++;
                        condition.get(legNumber).set(false);
                        String s = "Robot moved with leg " + (legNumber + 1) + ", step " + stepCounter + ", distance is: " + distance + "\n";
                        System.out.print(s);
                        write(s);
                        notify();
                        while (!condition.get(legNumber).get()) {
                            try {
                                sleep(1000);
                                wait();
                            } catch (InterruptedException e) {
                                System.out.println("Leg task " + (legNumber + 1) + " cancelled");
                            }
                        }
                    }
                }
            }
        }
    }
}




