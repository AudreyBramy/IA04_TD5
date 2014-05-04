package TD6;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class etape2 {

	public static void runExecQuery(String qFile){
		Query q = QueryFactory.read(qFile);
		Query query = QueryFactory.create(q);
		//System.setProperty("http.proxyHost","proxyweb.utc.fr");
		//System.setProperty("http.proxyPort","3128");
		System.out.println("Query sent");
		QueryExecution qexec = QueryExecutionFactory.sparqlService( "http://linkedgeodata.org/sparql", query );
		ResultSet concepts = qexec.execSelect();
		ResultSetFormatter.out(concepts);
		qexec.close();
	}
	public static void main(String[] args) {
		String query = "query2.sparql"; // fichier contenant la requête
		runExecQuery(query);
	}

}
