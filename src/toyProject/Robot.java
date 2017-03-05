package toyProject;


import java.io.*;

/**
 * Created by anch0317 on 03.03.2017.
 */
public class Robot {

    private volatile double distance;
    private int stepCounter;
    private int legs;

    public Robot(int legsQuantity, double distance) {
        legs = legsQuantity;
        this.distance = distance;
        cleanFile();
    }

    int getStepCounter() {
        return stepCounter;
    }

    void setLegs(int legs) {
        this.legs = legs;
    }

    void startMoving() {

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
            distance -= (Math.random() + 0.5);
            stepCounter++;
            System.out.println("Robot moved with leg " + legNumber + ", step " + stepCounter + ", distance is: " + distance);
//            write("Robot moved with leg " + legNumber + ", step " + stepCounter+"\n");
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
}




