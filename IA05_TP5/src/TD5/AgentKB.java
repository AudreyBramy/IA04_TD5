package TD5;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentKB extends Agent {

	private static final long serialVersionUID = 1L;
	private Model model;
	private ObjectMapper mapper;

	protected void setup() {
		DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Agent");
        sd.setName("KB");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        
		//Creation du model
		model = ModelFactory.createDefaultModel();
		mapper = new ObjectMapper();
		addBehaviour(new InitModelBehav());
	}
	
	/**
	 * Initialisation du model avec la base de connaissance
	 * @author AudreyB
	 *
	 */
	public class InitModelBehav extends Behaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = receive(mt);
		
			if(message != null){
				String kb = message.getContent();
				if(kb != null){
						model.read(kb);
						System.out.println("Model init : " +kb);
				} else {
						System.err.println("aucune ontologie à initialiser");
				}
			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
