package toyProject;

/**
 * Created by anch0317 on 06.03.2017.
 */
public interface IRobot extends Runnable{
    public void setLegs(int legs);
    public void interrupt();
}
