package TD5;

import com.hp.hpl.jena.query.ResultSet;

public class RequestSparql {
	private String requestFile;
    public ResultSet sparqlResult;




    public RequestSparql(){}
	
	public String getRequestFile() {
		return requestFile;
	}

	public void setRequestFile(String requestFile) {
		this.requestFile = requestFile;
	}
	

}
