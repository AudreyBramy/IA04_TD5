package TD5;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
/**
 * Created with IntelliJ IDEA.
 * User: home
 * Date: 01/04/2014
 * Time: 10:26
 * To change this template use File | Settings | File Templates.
 */


public class Main {
    public static String MAIN_PROPERTIES_FILE = "boot";

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Runtime rt = Runtime.instance();
        Profile p = null;
        try{
            p = new ProfileImpl(MAIN_PROPERTIES_FILE);
            AgentContainer mc = rt.createMainContainer(p);
        }
        catch(Exception ex) {
            System.out.println("error");
        }
    }
}
