package ml.iamwhatiam.tao.domain;

public class Response<T> extends Taichi {

	private static final long serialVersionUID = 4556562401560111738L;
	
	private long refId;
	
	private T response;

	public long getRefId() {
		return refId;
	}

	public void setRefId(long refId) {
		this.refId = refId;
	}

	public T getResponse() {
		return response;
	}

	public void setResponse(T response) {
		this.response = response;
	}

	@Override
	public String toString() {
		return "{\"refId\": " + refId + ", \"response\": " + response + "}";
	}

}
