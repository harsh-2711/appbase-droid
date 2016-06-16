package elasticsearchlibrary;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PipedInputStream;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.HttpResponseHeaders;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import elasticsearchlibrary.handlers.AppbaseStreamHandler;
import elasticsearchlibrary.handlers.AppbaseHandlerSaveStream;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppbaseMainTest {
	static JsonParser parser;
	static String randomId = null;
	static AppbaseClient appbase;
	static final String user = "7eJWHfD4P", pass = "431d9cea-5219-4dfb-b798-f897f3a02665",
			URL = "http://scalr.api.appbase.io", appName = "jsfiddle-demo",
			jsonDoc = "{\"department_id\": 1,\"department_name\": \"Books\",\"name\": \"A Fake Book on Network Routing\",\"price\": 5595}";
	static Random r = new Random();
	String type = "product", id = "1";

	public static String generateId() {
		if (r == null) {
			r = new Random();
		}
		int n = r.nextInt(5) + 5;
		String id = "";
		for (int i = 0; i < n + 1; i++) {
			id += (char) (r.nextInt(25) + 97) + "";
		}
		return id;
	}

	@BeforeClass
	public static void setup() {
		/**
		 * SETUP new Appbase() Pass the parameters URL (base url eg.
		 * http://scalr.api.appbase.io) appName eg. jsfiddle-demo user name. The
		 * one provided for that particular app(eg. 7eJWHfD4P ) password
		 * corresponding to the userName(eg.
		 * 431d9cea-5219-4dfb-b798-f897f3a02665 )
		 */

		appbase = new AppbaseClient(URL, appName, user, pass);
		randomId = generateId();
		parser = new JsonParser();
	}

	@Test
	public void AindexTest() {

		/**
		 * Index String result = appbase.index(type, id, jsonDoc); type and id
		 * of the object you need to insert. jsonDoc is the entire body we need
		 * to insert.
		 *
		 */

		// There will be tests for checking index for a new object and for one
		// which already exists
		String result = appbase.index(type, randomId, jsonDoc);
		assertNotNull(result);

		JsonObject object = parser.parse(result).getAsJsonObject();
		String created = object.get("created").getAsString();

		assertNotNull(created);
		assertEquals("true", created);

		result = appbase.index(type, randomId, jsonDoc);
		assertNotNull(result);
		object = parser.parse(result).getAsJsonObject();
		created = object.get("created").getAsString();

		assertNotNull(created);
		assertEquals("false", created);

	}

	@Test
	public void BupdateTest() {
		int generatedPrice = 5;
		String jsonDoc = "{doc: {\"price\": " + generatedPrice + "}}";
		String result = appbase.update(type, randomId, null, jsonDoc);
		JsonObject object = parser.parse(result).getAsJsonObject();
		assertEquals(object.getAsJsonObject("_shards").get("successful"),
				object.getAsJsonObject("_shards").get("total"));
		assertNotEquals(object.getAsJsonObject("_shards").get("successful").getAsInt(), 0);

		result = appbase.update(type, randomId, null, jsonDoc);
		object = parser.parse(result).getAsJsonObject();
		assertEquals(object.getAsJsonObject("_shards").get("successful"),
				object.getAsJsonObject("_shards").get("total"));
		assertEquals(object.getAsJsonObject("_shards").get("successful").getAsInt(), 0);

	}

	@Test
	public void CdeleteTest() {
		String result = appbase.delete(type, randomId);
		JsonObject object = parser.parse(result).getAsJsonObject();
		assertEquals(object.get("found").getAsBoolean(), true);
		assertEquals(object.getAsJsonObject("_shards").get("failed").getAsInt(), 0);

		result = appbase.delete(type, randomId);
		object = parser.parse(result).getAsJsonObject();
		assertEquals(object.get("found").getAsBoolean(), false);
		assertEquals(object.getAsJsonObject("_shards").get("failed").getAsInt(), 0);
	}

	@Test
	public void EgetTest() {

		appbase.index(type, randomId, jsonDoc);
		String result = appbase.get(type, randomId);
		JsonObject object = parser.parse(result).getAsJsonObject();
		assertEquals(object.get("found").getAsBoolean(), true);
		appbase.delete(type, randomId);
		result = appbase.get(type, randomId);
		object = parser.parse(result).getAsJsonObject();
		assertEquals(object.get("found").getAsBoolean(), false);

	}

	@Test
	public void FgetTypesTest() {
		String result = appbase.getTypes();
		JsonObject object = parser.parse(result).getAsJsonObject();
		assertEquals(object.isJsonObject(), true);
	}

	@Test
	public void GsearchTest() {
		String body = "{\"query\":{\"term\":{ \"price\" : 5595}}}";
		String result = appbase.search(type, body);
		JsonObject object = parser.parse(result).getAsJsonObject();
		assertEquals(object.isJsonObject(), true);
		assertNotEquals(object.getAsJsonObject("hits").get("total").getAsInt(), 0);
	}

	public void AHgetStreamTest() {
		appbase.index(type, randomId, jsonDoc);
		appbase.getStream(type, randomId, new AppbaseStreamHandler<String>(String.class) {
			int i = 1;

			@Override
			public org.asynchttpclient.AsyncHandler.State onHeadersReceived(HttpResponseHeaders arg0) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(arg0.getHeaders());
				return super.onHeadersReceived(arg0);
			}

			@Override
			public void onData(String data) {

			}

			@Override
			public org.asynchttpclient.AsyncHandler.State onBodyPartReceived(HttpResponseBodyPart bodyPart)
					throws Exception {
				if (i == 1) {
					appbase.update(type, randomId, null, "{doc: {\"price\": " + 2 + "}}");
					i++;
				} else if (i > 1) {
					String result = new String(bodyPart.getBodyPartBytes());
					JsonObject object = parser.parse(result).getAsJsonObject();
					System.out.println(result);
					assertEquals(object.getAsJsonObject("_source").get("price").getAsInt(), 2);
					return State.ABORT;
				}

				return State.CONTINUE;

			}
		});

		System.out.println("abc");
	}

	@Test
	public void IsearchStreamTest() {
		appbase.searchStream(type, "{\"query\":{\"term\":{ \"price\" : 5595}}}", new AppbaseStreamHandler<String>(String.class){

			@Override
			public void onData(String data) {
				// TODO Auto-generated method stub
				
			}

			
		});

	}

	@Test
	public void JsearchStreamToURLTest() {
	}

	public void testIndex() {

		String user = "7eJWHfD4P", pass = "431d9cea-5219-4dfb-b798-f897f3a02665", URL = "http://scalr.api.appbase.io",
				appName = "jsfiddle-demo",
				jsonDoc = "{\"department_id\": 1,\"department_name\": \"Books\",\"name\": \"A Fake Book on Network Routing\",\"price\": 5595}";

		appbase = new AppbaseClient(URL, appName, user, pass);
		// Index 1
		ListenableFuture<Response> f = appbase.prepareIndex(type, id, jsonDoc).execute();
		try {
			Response r = f.get();
			System.out.println(r.getResponseBody());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		// Index 2
		appbase.prepareIndex(URL, appName, jsonDoc).execute(new AppbaseHandlerSaveStream<String>(false, String.class) {

			@Override
			public void onData(String data) {
				// TODO Auto-generated method stub

			}

		});
		// Index 3

		ListenableFuture<Response> k = appbase.prepareIndex(type, id, jsonDoc).addQueryParam("same as add", "field")
				.addHeader("any", "header").execute();

		try {
			Response r = k.get();
			String a = r.getResponseBody();
			System.out.println(a);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void testSearch() {
		String user = "7eJWHfD4P", pass = "431d9cea-5219-4dfb-b798-f897f3a02665", URL = "http://scalr.api.appbase.io",
				appName = "jsfiddle-demo";

		appbase = new AppbaseClient(URL, appName, user, pass);

		// Search 1

		String body = "{\"query\":{\"term\":{ \"price\" : 5595}}}";
		ListenableFuture<Response> f = appbase.prepareSearch(type, body).addHeader("any", "header")
				.addQueryParam("set", "parameter").execute();

		try {
			Response r = f.get();
			String a = r.getResponseBody();
			System.out.println(a);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void AAtryStream() {
		PipedInputStream input = null;
		try {
			input = appbase.getPipedStream(type, "1");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			int data = input.read();
			while (data != -1) {
				System.out.print((char) data);
				data = input.read();
			}
			System.out.println();
		} catch (IOException e) {
			System.out.println();
			System.out.println("exception");
		}
	}
}
