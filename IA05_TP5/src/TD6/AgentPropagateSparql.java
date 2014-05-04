package TD6;

import java.io.IOException;
import java.io.StringWriter;

import TD5.InformRequest;
import TD5.Message;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class AgentPropagateSparql extends Agent {

	private static final long serialVersionUID = 1L;
	protected ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * Réception d'un message de la plateforme Jade 
	 * Envoie du type de requête et du fichier pour initialiser le model.
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
			 * Préparer la requête (écriture dans un fichier type "query.sparql"
			 * Envoie du nom de fichier à l'agent KB pour l'exécution de la requête
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
