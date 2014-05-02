package TD5;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
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

public class AgentReq extends Agent {

	private static final long serialVersionUID = 1L;
	private ObjectMapper mapper;
	private boolean onto;
	@Override
	protected void setup(){
		mapper = new ObjectMapper();
		onto = false;
		addBehaviour(new AskReqBehav());
	}

	/**
	 * Receive request form plateform
	 * Send ontologie to init
	 * @author AudreyB
	 *
	 */
	public class AskReqBehav extends Behaviour {
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
	
			if(message != null){
				Message msg = null;
				ACLMessage toSend = new ACLMessage(ACLMessage.INFORM);
				try {
					msg = mapper.readValue(message.getContent(), Message.class);
					if(msg.getOntologie() != null){
						String conversid = msg.getOntologie()+message.getPostTimeStamp();
						toSend.setContent(msg.getOntologie());
						toSend.setConversationId(conversid);
						toSend.addReceiver(getReceiver("Agent","KB"));
						
						onto = true;
						addBehaviour(new ReqBehav(msg, conversid));
						
						send(toSend);
						
					} else {
						System.out.println("Ontologie null");
					}
				}  catch (IOException e) {
					System.err.println("AgentReq(ReqBehav) : error message content mapping");
					e.printStackTrace();
				}
				
			}
		}

		@Override
		public boolean done() {
			return false;
		}
		
	}
	
	/**
	 * Send a request
	 * And receive response
	 * @author AudreyB
	 *
	 */
	private class ReqBehav extends Behaviour {

		private Message message;
		private String conversId;
		private StringWriter sw;
		
		public ReqBehav(Message msg, String convers) {
			this.message = msg;
			this.conversId = convers;
			
			sw = new StringWriter();
			try {
				mapper.writeValue(sw, message);
			} catch (IOException e) {e.printStackTrace();}
			
			ACLMessage toSend = new ACLMessage(ACLMessage.REQUEST);
			toSend.setConversationId(conversId);
			toSend.addReceiver(getReceiver("Agent","KB"));
			toSend.setContent(sw.toString());
		
			send(toSend);
		}
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId(conversId));
			ACLMessage message = receive(mt);
			if(message != null){
				try {
					Message msg = mapper.readValue(message.getContent(), Message.class);
					List<String> result = msg.getResult();
					System.out.println("Résultat : \n");
					for (String string : result) {
						System.out.println(string + "\n");
					}
				} catch (IOException e) {e.printStackTrace();}
			} else block();
			
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
