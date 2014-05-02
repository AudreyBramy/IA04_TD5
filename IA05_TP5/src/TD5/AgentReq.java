package TD5;

import java.io.IOException;
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
		addBehaviour(new ReqBehav());
	}

	public class ReqBehav extends Behaviour {
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
						toSend.setContent(msg.getOntologie());
						toSend.addReceiver(getReceiver("Agent","KB"));
						send(toSend);
						onto = true;
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
