package TD5;

public class Message {

	private String ontologie;
	private Request request;

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
