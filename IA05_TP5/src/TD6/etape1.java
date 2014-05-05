package TD6;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class etape1 {

	public static void test() {
		String query = "query/query.sparql"; // fichier contenant la requ�te
		Model model = ModelFactory.createDefaultModel();
		try {
			
			model.read(new FileInputStream("kb/foaf.n3"),null, "TURTLE");
			runExecQuery(query, model);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void runExecQuery(String qfilename, Model model) {
		Query query = QueryFactory.read(qfilename);
		System.out.println(query.toString());
		QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
		ResultSet r = queryExecution.execSelect();
		ResultSetFormatter.out(System.out,r);
		queryExecution.close();
	}
	
	public static void main(String[] args) {
		test();
	}

}
