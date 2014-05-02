package TD5;
/**
 * Created with IntelliJ IDEA.
 * User: home
 * Date: 01/04/2014
 * Time: 10:27
 * To change this template use File | Settings | File Templates.
 */
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class SecondMain {

    public static String SECONDARY_PROPERTIES_FILE = "sndC";

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        Runtime rt = Runtime.instance();
        ProfileImpl p = null;
        try {
            p = new ProfileImpl(SECONDARY_PROPERTIES_FILE);
            ContainerController cc = rt.createAgentContainer(p);
            AgentController agentKb = cc.createNewAgent("KB", "TD5.AgentKB", null);
            agentKb.start();
            AgentController agentReq = cc.createNewAgent("Req", "TD5.AgentReq", null);
            agentReq.start();
          
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}