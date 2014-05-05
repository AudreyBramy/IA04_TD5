package TD6;

import java.io.IOException;

import TD5.InformRequest;
import TD5.Message;
import TD5.AgentKB.InitModelBehav;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
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

public class GeodataAgent extends Agent {
	
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
		addBehaviour(new RequestSparqlBehav());
	}

	private class RequestSparqlBehav extends Behaviour {
		
		@Override
		public void action() {
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
			
			if(message != null){
				Message msg = null;
				try {
					msg = mapper.readValue(message.getContent(), Message.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
				runExecQuery("query.sparql");
				// Add result to the message
				// Write result in a csv file
				
			}
			
		}

		@Override
		public boolean done() {
			return false;
		}
		
		/**
		 * Execution d'une requête sur un "end-point" distant 
		 * @param qfilename nom du fichier contenant la requête.
		 */
		public void runExecQuery(String qfilename) {
			Query query = QueryFactory.read(qfilename);
			System.out.println(query.toString());
			QueryExecution qexec = QueryExecutionFactory.sparqlService( "http://linkedgeodata.org/sparql", query );
			ResultSet concepts = qexec.execSelect();
			ResultSetFormatter.out(concepts);
			qexec.close();
		}
		
	}
}
