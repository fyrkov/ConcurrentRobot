package toyProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by anch0317 on 03.03.2017.
 */
public class InputRunner extends Thread {

    IRobot r;

    InputRunner(IRobot r) {
        this.r = r;
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String s;
            System.out.print("Type in legs number: ");
            try {
                while (!br.ready()) {
                    Thread.sleep(20);
                }
                s = br.readLine();
                try {
                    int n = Integer.parseInt(s);
                    r.setLegs(n);
                    System.out.println(n+" legs set");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                System.out.println("Input task cancelled");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
