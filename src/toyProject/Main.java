package toyProject;

/**
 * Created by user on 09.03.2017.
 */
public class Main {
    public static void main(String[] args) {

        IRobot r = new Robot3(3, 50);
        new Thread(r).start();

    }
}
