package TD5;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.query.*;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

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
            Query query = QueryFactory.read(msg.getRequestFile());
            System.setProperty("http.proxyHost", "proxyweb.utc.fr");
            System.setProperty("http.proxyPort","3128");
            System.out.println(query.toString());
            QueryExecution queryExecution = QueryExecutionFactory.sparqlService("http://linkedgeodata.org/sparql", query);
            msg.sparqlResult = queryExecution.execSelect();
            ResultSetFormatter.out(System.out, msg.sparqlResult);
            // Write result in a file
            FileOutputStream fileresult = null;
            try {
                fileresult = new FileOutputStream("query/Georesult.txt");
            } catch (FileNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            ResultSetFormatter.out(fileresult, msg.sparqlResult);
            queryExecution.close();
            return msg;
        }
		
	}
}
