package TD5;

import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.io.StringWriter;


public class AgentPropagateSparql extends Agent {

	private static final long serialVersionUID = 1L;
	protected ObjectMapper mapper = new ObjectMapper();

    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Agent");
        sd.setName("PropagateSparql");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        mapper = new ObjectMapper();
        addBehaviour(new ReceiveRequest());
    }


	/**
	 * R�ception d'un message de la plateforme Jade
	 * Envoie du type de requ�te et du fichier pour initialiser le model.
	 * @author AudreyB
	 *
	 */

	private class ReceiveRequest extends Behaviour{

		
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt); 
			if(message != null){
				Message msg = null;
				StringWriter sw = new StringWriter();
				InformRequest ir = new InformRequest();
				try {
					msg = mapper.readValue(message.getContent(), Message.class);
					ir.setModelFile(msg.getOntologie());
					ir.setRequestType(msg.getReqType());
					mapper.writeValue(sw, ir);
					
					message.addReceiver(getReceiver("Agent", "KB"));
					message.setPerformative(ACLMessage.INFORM);
					String cid = (myAgent.getAID()+""+message.getPostTimeStamp());
					message.setConversationId(cid);
					
					addBehaviour(new FormatRequestBehav(cid));
				    System.out.println(getReceiver("Agent", "KB"));
					send(message);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	/**
	 * Receive message from Jade plateform
	 * send a first Message with a content InformRequest with the conversationId created
	 * Create the Sparql Request with message information
	 * send a second message with a request with the same  conversationId
	 * @author AudreyB
	 *
	 */
	private class FormatRequestBehav extends Behaviour {
		
		private String conversId;
		
		public FormatRequestBehav(String cid){
			this.conversId = cid;
		}

		@Override
		public void action() {
			/**
			 * Pr�parer la requ�te (�criture dans un fichier type "query.sparql"
			 * Envoie du nom de fichier � l'agent KB pour l'ex�cution de la requ�te
			 */
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	private AID getReceiver(String type, String Name) {
        AID rec = null;
        DFAgentDescription template =new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        sd.setName(Name);
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            rec = result[0].getName();
        } catch(FIPAException fe) {
            System.err.println(fe);
        }
        return rec;
    }

}
