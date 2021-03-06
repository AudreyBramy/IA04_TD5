package TD6;

import java.io.*;

import TD5.InformRequest;
import TD5.Message;
import TD5.AgentKB.InitModelBehav;

import TD5.RequestSparql;
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
        sd.setName("Geodata");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        
		mapper = new ObjectMapper();
		addBehaviour(new RequestGeodataBehav());
	}

	private class RequestGeodataBehav extends Behaviour {
        @Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage message = receive(mt);
			if(message != null){
                try {
                    System.out.println("--------------------RESULT------------------------\n");
                    RequestSparql msg = null;

                    msg = mapper.readValue(message.getContent(), RequestSparql.class);

                    // Add result to the message
                    msg = runExecQuery(msg);
                    FileWriter writer = null;

                    // Write result in a file
                    FileOutputStream fileresult = new FileOutputStream("query/Georesult.txt");
                    ResultSetFormatter.out(fileresult, msg.sparqlResult);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
			}
			
		}

		@Override
		public boolean done() {
			return false;
		}

        public RequestSparql runExecQuery(RequestSparql msg) {
            System.setProperty("http.proxyHost", "proxyweb.utc.fr");
            System.setProperty("http.proxyPort","3128");
            Query query = QueryFactory.read(msg.getRequestFile());
            System.out.println(query.toString());
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://linkedgeodata.org/sparql", query);
            msg.sparqlResult = queryExecution.execSelect();
            ResultSetFormatter.out(System.out, msg.sparqlResult);
            queryExecution.close();
            return msg;
        }
		
	}
}
