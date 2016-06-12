package elasticsearchlibrary.handlers;

import java.util.ArrayList;

import org.asynchttpclient.AsyncHandler;
import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.HttpResponseHeaders;
import org.asynchttpclient.HttpResponseStatus;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by Tirth Shah on 14-05-2016.
 */

public abstract class AppbaseHandlerSaveStream<T> implements AsyncHandler<ArrayList<T>>{
	private boolean getResult;
	JsonArray jsonArray;
	String halfBody = "";
	JsonParser jsParser;
	private Class<T> type;
	ArrayList<T> objectsRecieved;

	public AppbaseHandlerSaveStream(boolean getResult, Class<T> type) {
		this.getResult = getResult;
		jsonArray = new JsonArray();
		jsParser = new JsonParser();
		this.type = type;
		if (getResult)
			objectsRecieved = new ArrayList<T>();
	}

	public boolean getResult() {
		return getResult;
	}

	public State onStatusReceived(HttpResponseStatus arg0) throws Exception {
		if (arg0.getStatusCode() > 500)
			return State.ABORT;

		return State.CONTINUE;
	}

	public State onHeadersReceived(HttpResponseHeaders arg0) throws Exception {
		return State.CONTINUE;
	}

	public ArrayList<T> onCompleted() throws Exception {
		return null;
	}

	public State onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {

		halfBody += new String(bodyPart.getBodyPartBytes());
		JsonObject object;

		try {
			JsonElement element = jsParser.parse(halfBody);
			object = element.getAsJsonObject();
			if (getResult) {
				jsonArray.add(object);
			}
			halfBody = "";
			Gson gson = new Gson();
			T newReceived = gson.fromJson(object, type);
			onData(newReceived);
			if (getResult) {
				objectsRecieved.add(newReceived);
			}
		} catch (Exception e) {
		}
		return State.CONTINUE;
	}
	
	public abstract void onData(T data);

	public void onThrowable(Throwable arg0) {
		System.out.println();
	}

	public void encodeToJsonObject(String data) {

	}

	public void decodeFromJsonObject() {

	}

}