package TD6;

import java.io.IOException;
import java.io.StringWriter;

import TD5.InformRequest;
import TD5.Message;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentPropagateSparql extends Agent {

	private static final long serialVersionUID = 1L;
	protected ObjectMapper mapper = new ObjectMapper();
	
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
					
					message.addReceiver(r);
					message.setConversationId(myAgent.getAID()+DateFormat);
					
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
	 * Create a conversationId 
	 * send a first Message with a content InformRequest with the conversationId created
	 * Create the Sparql Request with message information
	 * send a second message with a request with the same  conversationId
	 * @author AudreyB
	 *
	 */
	private class FormatRequestBehav extends Behaviour {

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}

}
