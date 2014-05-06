package TD5;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
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
		private ObjectMapper mapper = new ObjectMapper();
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage message = receive(mt);
		
			if(message != null){
				InformRequest ir = null;
				try {
					ir = mapper.readValue(message.getContent(), InformRequest.class);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String kb = ir.getRequestType();
				if( kb != null){
					if(kb.equals("jena")){
						addBehaviour(new RequestJenaBehav(message.getConversationId(), ir));
						System.out.println("type = Jena");
					} else if (kb.equals("sparql")){
						System.out.println(" Add RequestSparqlBehavior with cid : "+ message.getConversationId()+"\n");
						addBehaviour(new RequestSparqlBehav(message.getConversationId(), ir));
					}
				} else {
						System.err.println("aucune ontologie � initialiser");
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
	public class RequestJenaBehav extends Behaviour {

		private String conversId;
		private Model model;
		
		public RequestJenaBehav(String conversId, InformRequest ir){
			//Creation du model
			model = ModelFactory.createDefaultModel();
			model.read(ir.getModelFile(),null,"TURTLE");
			this.conversId = conversId;
		}
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchConversationId(conversId));
			ACLMessage message = receive(mt);
			
			if(message != null){
				// Execution de la requ�te
				Message msg = null;
				List<String> result = new ArrayList<String>();
				StringWriter sw = new StringWriter();
				
				try {
					msg = mapper.readValue(message.getContent(), Message.class);
					
					if(msg.getRequest().getRequestName().equals("getInfoById")){
						result = getInfoById(msg.getRequest().getRequestInfo(), "td5");
						
					} else if(msg.getRequest().getRequestName().equals("getInfoByName")){
						result = getInfoByName(msg.getRequest().getRequestInfo(), "td5");
						
					} else if(msg.getRequest().getRequestName().equals("getKnowsPerson")){
						result = getPersonKnowsSb(msg.getRequest().getRequestInfo());
						
					} 
					msg.setResult(result);
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

		/**
		 * Information about a person by an id
		 * 
		 * @param id : person id
		 * @param pns : namespace
		 * @return a list of statement
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
		/**
		 * Information about a person by an id
		 * @param id : a resource corresponding to a person id
		 * @return a list of statement
		 */
		private List<String> getInfoById(Resource id){
			List<String> reqresult= new ArrayList<String>();
			
			StmtIterator iterator = model.listStatements(new SimpleSelector(id,(Property)null,(Resource)null)) ; 
			while(iterator.hasNext()){
				Statement stmt = iterator.next();
				reqresult.add(stmt.asTriple().toString());
			}
			
			return reqresult;
			
		}
		/**
		 * Information about a person with the person firstname
		 * @param name : person firstname
		 * @param pns : namespace
		 * @return a list of statement
		 */
		private List<String> getInfoByName(String name, String pns){
			Resource id = null;
			List<String> reqresult= new ArrayList<String>();
			
			StmtIterator iterator = model.listStatements(new SimpleSelector((Resource)null,(Property)null,name));
			
			if(iterator.hasNext()){
				Statement stmt = iterator.next();
				id = stmt.getSubject();
				System.out.println("Find id = "+id.toString());
				reqresult = getInfoById(id);
			}
			return reqresult;
		}
		
		private List<String> getPersonKnowsSb(String id){
			List<String> result = new ArrayList<String>();
			String nsFoaf = model.getNsPrefixURI("foaf");
			String nsTd5 = model.getNsPrefixURI("td5");
			Property p = model.getProperty(nsFoaf+"knows");
			Resource i = model.getResource(nsTd5+id);
			if(isAPerson(id)){
				List<Statement> stmtList = model.listStatements(new SimpleSelector((Resource)null, p, i)).toList();
				for (Statement statement : stmtList) {
					result.add(statement.toString());
				}
			}
			return result;
		}
		
		private boolean isAPerson(String id){
			String nsRdf = model.getNsPrefixURI("rdf");
			String nsFoaf = model.getNsPrefixURI("foaf");
			String nsTd5 = model.getNsPrefixURI("td5");
			Resource i = model.getResource(nsTd5+id);
			Property p = model.getProperty(nsRdf+"type");
			Resource h = model.getResource(nsFoaf+"Person");
			return model.listStatements(i, p, h).hasNext();
		}
		
	}
	
	private class RequestSparqlBehav extends Behaviour {

		private String conversId;
		private Model model;
		public RequestSparqlBehav(String cid, InformRequest ir){
			model = ModelFactory.createDefaultModel();
            try {
                model.read(new FileInputStream("kb/foaf.n3"),null, "TURTLE");
            } catch (FileNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            this.conversId = cid;
        }
		@Override
		public void action() {

            MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchConversationId(conversId));
			ACLMessage message = receive(mt);
			if(message != null){
                System.out.println("--------------------RESULT------------------------\n");
                RequestSparql msg = null;
				try {
					msg = mapper.readValue(message.getContent(), RequestSparql.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
                // Add result to the message
                msg = runExecQuery(msg, model);
                FileWriter writer = null;
                try {
                    // Write result in a file
                    FileOutputStream fileresult = new FileOutputStream("query/result.txt");
                    ResultSetFormatter.out(fileresult, msg.sparqlResult);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
		public RequestSparql runExecQuery(RequestSparql msg, Model model) {
            Query query = QueryFactory.read(msg.getRequestFile());
			System.out.println(query.toString());
			QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
			msg.sparqlResult = queryExecution.execSelect();
            ResultSetFormatter.out(System.out, msg.sparqlResult);
			queryExecution.close();
            return msg;
		}
		
	}
	
}
