package TD5;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

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
						addBehaviour(new RequestBehav(message.getConversationId(), kb));
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
	/**
	 * Receive a request to Execute
	 * @author AudreyB
	 *
	 */
	public class RequestBehav extends Behaviour {

		private String conversId;
		private Model model;
		
		public RequestBehav(String conversId, String m){
			//Creation du model
			model = ModelFactory.createDefaultModel();
			model.read(m);
			this.conversId = conversId;
		}
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchConversationId(conversId));
			ACLMessage message = receive(mt);
			
			if(message != null){
				// Execution de la requête
				Message msg = null;
				List<String> result = new ArrayList<String>();
				StringWriter sw = new StringWriter();
				
				try {
					msg = mapper.readValue(message.getContent(), Message.class);
					
					if(msg.getRequest().getRequestName().equals("getInfoById")){
						result = getInfoById(msg.getRequest().getRequestInfo(), "td5");
						msg.setResult(result);
					}
					mapper.writeValue(sw,msg);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				message = message.createReply();
				message.setPerformative(ACLMessage.INFORM);
				message.setContent(sw.toString());
				send(message);
			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
		/**
		 * REQUEST
		 */

		private List<String> getInfoById(String id, String pns){
			String ns = model.getNsPrefixURI(pns);
			List<String> reqresult= new ArrayList<String>();
			
			Resource h = model.getResource(ns + id);
			StmtIterator iterator = model.listStatements(new SimpleSelector(h,(Property)null,(Resource)null)) ; 
			while(iterator.hasNext()){
				Statement stmt = iterator.next();
				reqresult.add(stmt.asTriple().toString());
			}
			
			return reqresult;
			
		}
		
	}
	
}
