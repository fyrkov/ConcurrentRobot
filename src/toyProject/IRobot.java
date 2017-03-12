package toyProject;

/**
 * Created by anch0317 on 06.03.2017.
 */
public interface IRobot extends Runnable{
     void setParams(int legs, double distance);
     void interrupt();

}
