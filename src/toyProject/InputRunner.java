package toyProject;

import java.util.Scanner;

/**
 * Created by anch0317 on 03.03.2017.
 */
public class InputRunner implements Runnable {

    Robot2 r;

    public InputRunner(Robot2 r) {
        this.r = r;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String s = sc.nextLine();
            try {
                int n = Integer.parseInt(s);
                r.setLegs(n);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
}
