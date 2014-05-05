package TD6;

import java.io.IOException;

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

public class AgentPropagateGeoSparql extends Agent {
	
	protected ObjectMapper mapper;
	public void setup(){
		mapper = new ObjectMapper();
		addBehaviour(new RequestBehav());
	}
	
	/**
	 * Reception d'une requête provenant de l'utilisateur
	 * Pour execution sur end point distant
	 * @author AudreyB
	 *
	 */
	public class RequestBehav extends Behaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
			
			if(message != null){
				Message msg = null;
				try {
					msg = mapper.readValue(message.getContent(), Message.class);
					// En fonction dde msg.getRequest().getRequestName on rédige reqête dans .sparql
					// envoie du message à l'agent GeoDataAgent
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
