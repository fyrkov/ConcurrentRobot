package toyProject;


import java.io.*;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by anch0317 on 03.03.2017.
 */
public class Robot2 {

    private volatile double distance;
    private AtomicInteger stepCounter;
    private int legs;

    int getStepCounter() {
        return stepCounter.get();
    }

    synchronized void decrementDistance(double decrement) {
        this.distance -= decrement;
    }

    public void setLegs(int legs) {
        this.legs = legs;
    }

    public Robot2(int legsQuantity, double distance) {
        legs = legsQuantity;
        this.distance = distance;
        stepCounter = new AtomicInteger(0);
        cleanFile();
    }

    public void startMoving() {

        while (distance > 0) {
            for (int i = 0; i < legs; i++) {
                Step s = new Step(i + 1);
                s.start();
                try {
                    s.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (distance <= 0) break;
            }
        }

    }


    class Step extends Thread {

        private int legNumber;

        Step(int legNumber) {
            this.legNumber = legNumber;
        }

        @Override
        public void run() {
//            distance -= (Math.random() + 0.5);
            decrementDistance(Math.random() + 0.5);
            stepCounter.incrementAndGet();
//            System.out.println("Robot moved with leg " + legNumber + ", step " + stepCounter.get());
            write("Robot moved with leg " + legNumber + ", step " + stepCounter.get()+"\n");
            try {
                sleep((long) ((Math.random() * 700)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void write(String s) {
        try  (PrintWriter writer = new PrintWriter(
                new FileWriter("out.txt", true))){
            writer.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void cleanFile() {
        try  (PrintWriter writer = new PrintWriter(
                new FileWriter("out.txt"))){
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




