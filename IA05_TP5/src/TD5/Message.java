package TD5;

import java.util.List;

public class Message {

	private String ontologie;
	private Request request;
	private List<String> result;

	public List<String> getResult() {
		return result;
	}

	public void setResult(List<String> result) {
		this.result = result;
	}

	public Message() {}
	
	public String getOntologie() {
		return ontologie;
	}
	public void setOntologie(String ontologie) {
		this.ontologie = ontologie;
	}
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
}
