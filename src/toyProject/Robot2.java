package toyProject;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by anch0317 on 03.03.2017.
 */
public class Robot2 {

    private AtomicBoolean condition[];
    private volatile double distance;
    private volatile int stepCounter;
    private int legs;

    public Robot2(int legsQuantity, double distance) {
        legs = legsQuantity;
        this.distance = distance;
        condition = new AtomicBoolean[legsQuantity];
        cleanFile();
    }

    int getStepCounter() {
        return stepCounter;
    }

    //TODO legs addition
    void setLegs(int legs) {
        this.legs = legs;
    }

    void startMoving() {

        Thread s[] = new Step[legs];
        for (int i = 0; i < legs; i++) {
            condition[i] = new AtomicBoolean();
            s[i] = new Step(i);
            s[i].setDaemon(true);
            s[i].start();
        }

        while (distance > 0) {
            for (int i = 0; i < legs; i++) {
                synchronized (s[i]) {
                    condition[i].set(true);
                    s[i].notify();
//                    System.out.println("Main thread reports");
                    while (condition[i].get()) {
                        try {
                            s[i].wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (distance <= 0) break;
                }
            }
        }
    }

    void write(String s) {
        try (PrintWriter writer = new PrintWriter(
                new FileWriter("out.txt", true))) {
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
                while (condition[legNumber].get()) {
                    synchronized (this) {
                        distance -= (Math.random() + 0.5);
                        stepCounter++;
                        condition[legNumber].set(false);
//                        System.out.println("Robot moved with leg " + (legNumber + 1) + ", step " + stepCounter + ", distance is: " + distance);
                        write("Robot moved with leg " + (legNumber+1) + ", step " + stepCounter+", distance is: " + distance+"\n");
                        notify();
                        while (!condition[legNumber].get()) {
                            try {
                                sleep(250);
                                wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}




