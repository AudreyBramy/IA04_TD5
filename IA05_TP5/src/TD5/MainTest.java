package TD5;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class MainTest {
	private static List<String> getInfoById(Model model,Resource id){
		List<String> reqresult= new ArrayList<String>();
		
		StmtIterator iterator = model.listStatements(new SimpleSelector(id,(Property)null,(Resource)null)) ; 
		while(iterator.hasNext()){
			Statement stmt = iterator.next();
			reqresult.add(stmt.asTriple().toString());
		}
		
		return reqresult;
		
	}
	private static boolean isAPerson(Model model,String id){
		String nsRdf = model.getNsPrefixURI("rdf");
		String nsFoaf = model.getNsPrefixURI("foaf");
		String nsTd5 = model.getNsPrefixURI("td5");
		Resource i = model.getResource(nsTd5+id);
		Property p = model.getProperty(nsRdf+"type");
		Resource h = model.getResource(nsFoaf+"Person");
		return model.listStatements(i, p, h).hasNext();
	}

	public static void main(String[] args) {

		Model model;
		model = ModelFactory.createDefaultModel();
		model.read("file:kb/index.rdf",null,"TURTLE");
		
		List<String> result = new ArrayList<String>();
		String nsFoaf = model.getNsPrefixURI("foaf");
		String nsTd5 = model.getNsPrefixURI("td5");
		Property p = model.getProperty(nsFoaf+"knows");
		Resource i = model.getResource(nsTd5+"jean");
		System.out.println(isAPerson(model, "jean"));
		if(isAPerson(model,"jean")){
			List<Statement> stmtList = model.listStatements(new SimpleSelector((Resource)null, p, i)).toList();
			System.out.println(stmtList.size());
		}

	}

}
