package TD5;

import com.hp.hpl.jena.query.ResultSet;

import java.util.List;

public class Message {

	private String ontologie;
	private String reqType; // jena ou 
	private RequestJena request;
	private List<String> result;



	public Message() {}
	
	public List<String> getResult() {
		return result;
	}

	public void setResult(List<String> result) {
		this.result = result;
	}
	
	public String getReqType() {
		return reqType;
	}

	public void setReqType(String reqType) {
		this.reqType = reqType;
	}
	
	public String getOntologie() {
		return ontologie;
	}
	public void setOntologie(String ontologie) {
		this.ontologie = ontologie;
	}
	public RequestJena getRequest() {
		return request;
	}
	public void setRequest(RequestJena request) {
		this.request = request;
	}
}
