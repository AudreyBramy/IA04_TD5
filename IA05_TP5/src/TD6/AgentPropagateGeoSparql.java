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

public class AgentPropagateGeoSparql extends Agent {
	
	protected ObjectMapper mapper;
	public void setup(){
		mapper = new ObjectMapper();
		addBehaviour(new RequestBehav());
	}
	
	/**
	 * Reception d'une requ�te provenant de l'utilisateur
	 * Pour execution sur end point distant
	 * @author MDH
	 *
	 */
	public class RequestBehav extends Behaviour {

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

                    message.addReceiver(getReceiver("Agent", "Geodata"));
                    message.setPerformative(ACLMessage.REQUEST);
                    send(message);

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
