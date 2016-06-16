package elasticsearchlibrary.handlers;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.asynchttpclient.AsyncHandler;
import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.HttpResponseHeaders;
import org.asynchttpclient.HttpResponseStatus;
import org.asynchttpclient.Response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AppbaseHandler<T> implements AsyncHandler<T>  {
	private Response.ResponseBuilder builder;
	JsonArray jsonArray;
	String halfBody = "";
	JsonParser jsParser;
	private Class<T> type;
	private T returnObject =null;
	public AppbaseHandler(Class<T> type) {
		if(Response.class==type){
			builder = new Response.ResponseBuilder();
		}
		jsonArray = new JsonArray();
		jsParser = new JsonParser();
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see elasticsearchlibrary.handlers.AppbaseHandler#onStatusReceived(org.asynchttpclient.HttpResponseStatus)
	 */
	public State onStatusReceived(HttpResponseStatus status) throws Exception {
		if(Response.class==type){
			builder.accumulate(status);
		}
		if (status.getStatusCode() > 500)
			return State.ABORT;

		return State.CONTINUE;
	}

	/* (non-Javadoc)
	 * @see elasticsearchlibrary.handlers.AppbaseHandler#onHeadersReceived(org.asynchttpclient.HttpResponseHeaders)
	 */
	public State onHeadersReceived(HttpResponseHeaders arg0) throws Exception {
		if(Response.class==type){
			builder.accumulate(arg0);
		}
		return State.CONTINUE;
	}

	/* (non-Javadoc)
	 * @see elasticsearchlibrary.handlers.AppbaseHandler#onCompleted()
	 */
	public T onCompleted() throws Exception {
		if(type==Response.class){
			return (T)builder.build();
		}
		else {
			return returnObject;
		}
	}

	/* (non-Javadoc)
	 * @see elasticsearchlibrary.handlers.AppbaseHandler#onBodyPartReceived(org.asynchttpclient.HttpResponseBodyPart)
	 */
	@SuppressWarnings("unchecked")
	public State onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
		halfBody += new String(bodyPart.getBodyPartBytes());
		JsonObject object;
		try {
			JsonElement element = jsParser.parse(halfBody);
			object = element.getAsJsonObject();
			halfBody = "";
			Gson gson = new Gson();
			object=formatResponse(object);
			if(type==Response.class){
				builder.accumulate(bodyPart);
			}
			if (type == String.class) {
				returnObject=((T) object.toString());
			} else if (type == JsonElement.class || JsonElement.class.isAssignableFrom(type)) {
				returnObject=((T) object);
			} else {
				T newReceived = gson.fromJson(object, type);
				returnObject=(newReceived);
			}

		} catch (Exception e) {

		}
		return State.CONTINUE;
	}


	/* (non-Javadoc)
	 * @see elasticsearchlibrary.handlers.AppbaseHandler#onThrowable(java.lang.Throwable)
	 */
	public void onThrowable(Throwable arg0) {
		System.out.println(arg0.getMessage());
	}

	/* (non-Javadoc)
	 * @see elasticsearchlibrary.handlers.AppbaseHandler#formatResponse(com.google.gson.JsonObject)
	 */
	public JsonObject formatResponse(JsonObject response) {
		JsonObject formatted = new JsonObject();
		formatted = response.remove("_source").getAsJsonObject();
		Set<Entry<String, JsonElement>> entrySet = response.entrySet();
		for (Map.Entry<String, JsonElement> entry : entrySet) {
			if (entry.getKey().startsWith("_")) {
				formatted.add(entry.getKey(), entry.getValue());
			}
		}
		return formatted;
	}
}