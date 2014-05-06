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
                RequestSparql msg = null;
                StringWriter sw = new StringWriter();
                RequestSparql sparqlReq = new RequestSparql();


                    sparqlReq.setRequestFile("query/query2.sparql");
                    try {
                        mapper.writeValue(sw, sparqlReq);
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                    ACLMessage toSend = new ACLMessage(ACLMessage.REQUEST);
                    toSend.addReceiver(getReceiver("Agent", "Geodata"));
                    toSend.setPerformative(ACLMessage.REQUEST);
                    toSend.setContent(sw.toString());

                    send(toSend);

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
