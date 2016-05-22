package elasticsearchlibrary;

import org.asynchttpclient.AsyncHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Param;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.asynchttpclient.util.Base64;

import elasticsearchlibrary.BulkRequestObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Tirth Shah on 10-05-2016.
 */

public class Appbase {

	AsyncHttpClient httpClient;
	private String baseURL, URL, password, userName, app;
	private ArrayList<Param> parameters = new ArrayList<Param>();

	private static final String SEPARATOR = "/";

	// Do not include a / anywhere.
	// URL is the base URL.
	// App is the app name.
	// userName is the userName for your app.
	// password which matches with the username.
	// use the setters to set the the URL, app, userName, password.
	/**
	 * new Appbase()
	 * 
	 * create a new Appbase() by passing in arguments:
	 * 
	 * @param URL
	 *            The base URL eg. "www.example.com" or
	 *            "http://scalr.api.appbase.io".
	 * @param appName
	 *            application name eg. "myFirstApp" or "jsfiddle-demo"
	 * @param userName
	 *            the user name provided for the app eg. "7eJWHfD4P"
	 * @param password
	 *            the password corresponding to the userName eg.
	 *            "431d9cea-5219-4dfb-b798-f897f3a02665"
	 * 
	 */
	public Appbase(String URL, String app, String userName, String password) {

		this.baseURL = URL;
		this.password = password;
		this.userName = userName;
		this.app = app;
		constructURL();
		httpClient = new DefaultAsyncHttpClient();
		Param stream = new Param("streamonly", "true");
		parameters.add(stream);

	}

	/**
	 * Returns the constructed URL based on the type argument.
	 * 
	 * @param type
	 * @return constructed URL with the given type
	 */

	public String getURL(String type) {
		return URL + SEPARATOR + type;
	}

	/**
	 * Returns the constructed URL based on the type and id.
	 * 
	 * @param type
	 * @param id
	 * @return constructed URL with type and id
	 */
	public String getURL(String type, String id) {
		return URL + SEPARATOR + type + SEPARATOR + id;
	}

	/**
	 * Getter for userName
	 * 
	 * @return userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Getter for password
	 * 
	 * @return password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * returns the constructed URL by adding a search term as its query
	 * parameter.
	 * 
	 * @param term
	 * @return Search URL with the term as a query parameter
	 */
	public String getSearchUrl(String term) {
		return URL + SEPARATOR + "_search?q=" + term;
	}

	/**
	 * returns the constructed URL for a type by adding the search term as its
	 * query parameter.
	 * 
	 * @param term
	 *            the term to be searched
	 * @return Search URL with the term as a query parameter
	 */
	public String getSearchUrl(String type, String term) {
		return URL + SEPARATOR + type + SEPARATOR + "_search?q=" + term;
	}

	/**
	 * If the Appbase object needs to be reused or if a wrong base URL is set,
	 * this can be used to reset it.
	 * 
	 * @param URL
	 */
	public void setURL(String URL) {
		this.baseURL = URL;
		constructURL();
	}

	public void setApp(String app) {
		this.app = app;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private void constructURL() {
		this.URL = this.baseURL + SEPARATOR + app;
	}

	public String getAuth() {
		String Auth = this.userName + ":" + this.password;
		return Base64.encode(Auth.getBytes());
	}

	// Main library methods

	// index()
	// update()
	// delete()
	// bulk()
	// get()
	// getTypes()
	// search()
	// getStream()
	// searchStream()
	// searchStreamToURL()

	/**
	 * To index a object easily by just inputing the parameters.
	 * 
	 * @param type
	 *            the type of the object
	 * @param id
	 *            the id at which it need to be inserted
	 * @param jsonDoc
	 *            the String which is the JSON for the object to be inserted
	 * @return the result after the operation. It contains the details of the
	 *         operations execution.
	 */
	public String index(String type, String id, String jsonDoc) {

		RequestBuilder builder = new RequestBuilder("PUT");
		Request request = builder.setUrl(getURL(type, id))
				.addHeader("Authorization", "Basic " + getAuth())
				.setBody(jsonDoc).build();
		ListenableFuture<Response> f = httpClient.executeRequest(request);
		try {
			Response r = f.get();
			return r.getResponseBody();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * To update a document. We can pass just the portion of the object to be
	 * updated. parameters is a list of parameters which are the name value
	 * pairs which will be added during the execution
	 * 
	 * 
	 * @param type
	 *            the type of the object
	 * @param id
	 *            the id at which it need to be inserted
	 * @param parameters
	 *            A list of all the parameters for a specific update
	 * @param jsonDoc
	 *            the String which is the JSON for the object to be inserted
	 * @return the result after the operation. It contains the details of the
	 *         operations execution.
	 */

	public String update(String type, String id, List<Param> parameters,
			String jsonDoc) {
		RequestBuilder builder = new RequestBuilder("POST");
		Request request = builder
				.setUrl(getURL(type, id) + SEPARATOR + "_update")
				.addHeader("Authorization", "Basic " + getAuth())
				.addQueryParams(parameters).setBody(jsonDoc).build();
		ListenableFuture<Response> f = httpClient.executeRequest(request);
		try {
			Response r = f.get();
			return r.getResponseBody();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * To delete a document
	 * 
	 * @param type
	 *            the type of the object
	 * @param id
	 *            the id at which it need to be inserted
	 * @return the result after the operation. It contains the details of the
	 *         operations execution.
	 */
	public String delete(String type, String id) {
		ListenableFuture<Response> f = httpClient
				.prepareDelete(getURL(type, id))
				.addHeader("Authorization", "Basic " + getAuth()).execute();
		try {
			Response r = f.get();
			return r.getResponseBody();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Implements multiple methods at once can be used for bulk indexing or
	 * deleting or updating.
	 * 
	 * 
	 * @param objects
	 *            array of BulkRequestObject
	 * @return returns the array list of the response bodies of the methods
	 *         executed
	 */
	public ArrayList<String> bulk(BulkRequestObject[] objects) {
		ArrayList<String> abc = new ArrayList<String>();
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] != null) {
				switch (objects[i].getMethod()) {
				case 0:
					abc.add(index(objects[i].type, objects[i].getId(),
							objects[i].getJsonDoc()));
					break;
				case 1:
					abc.add(delete(objects[i].type, objects[i].getId()));
					break;
				case 2:
					abc.add(update(objects[i].type, objects[i].getId(), null,
							objects[i].getJsonDoc()));
					break;
				default:
					abc.add(index(objects[i].type, objects[i].getId(),
							objects[i].getJsonDoc()));
					break;
				}
			}

		}
		return abc;
	}

	/**
	 * 
	 * Method to get the indexed objects by specifying type and id
	 * 
	 * @param type
	 *            type of the required object
	 * @param id
	 *            id of the required object
	 * @return the json String of the required object
	 */

	public String get(String type, String id) {
		ListenableFuture<Response> f = httpClient.prepareGet(getURL(type, id))
				.addHeader("Authorization", "Basic " + getAuth()).execute();
		try {
			return f.get().getResponseBody();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the mappings
	 * 
	 * @return returns the json document as String of the mappings
	 */
	public String getTypes() {
		ListenableFuture<Response> f = httpClient
				.prepareGet(this.URL + SEPARATOR + "_mapping")
				.addHeader("Authorization", "Basic " + getAuth()).execute();
		try {
			return f.get().getResponseBody();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Search by passing the query body
	 * 
	 * @param type
	 *            type in which the search must take place
	 * @param body
	 *            the query body eg. ( {"query":{"term":{ "price" : 5595}}} )
	 * @return returns the search result corresponding to the query
	 */
	public String search(String type, String body) {
		ListenableFuture<Response> f = httpClient
				.preparePost(getURL(type) + SEPARATOR + "_search")
				.addHeader("Authorization", "Basic " + getAuth()).setBody(body)
				.execute();
		try {
			return f.get().getResponseBody();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	
	/**
	 * 
	 * @param type type in which search must be done
	 * @param parameters parameters for searching
	 * @return
	 */
	public String searchUsingParameters(String type,
			java.util.List<Param> parameters) {
		ListenableFuture<Response> f = httpClient
				.preparePost(getURL(type) + SEPARATOR + "_search")
				.addHeader("Authorization", "Basic " + getAuth())
				.addQueryParams(parameters)
				.execute();
		try {
			return f.get().getResponseBody();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	/**
	 * Get the stream for a indexed object.
	 * If any changes happen, the user can state what to happen by overriding the onData method or overriding the onBodyPartRecieved.
	 * 
	 * @param type type of the object
	 * @param id id of the object
	 * @param asyncHandler a async handler object. 
	 * It is preferable to pass a Appbase Handler object as single body may come as multiple body parts which need to be managed which is implemented by Appbase Handler.
	 */
	public void getStream(String type, String id,
			AsyncHandler<String> asyncHandler) {
		ListenableFuture<String> f = httpClient.prepareGet(getURL(type, id))
				.addHeader("Authorization", "Basic " + getAuth())
				.setRequestTimeout(60000000).addQueryParams(parameters)
				.execute(asyncHandler);

	}
	/**
	 * Get the search stream for a indexed object.
	 * If any changes happen and the changes contain some part of the query, the user can state what to happen by overriding the onData() method or the onBodyPartRecieved() method.
	 * 
	 * @param type type of the object
	 * @param id id of the object
	 * @param asyncHandler a async handler object. 
	 * It is preferable to pass a Appbase Handler object as single body may come as multiple body parts which need to be managed which is implemented by Appbase Handler.
	 */
	public String searchStream(String type, String body,
			AsyncHandler<String> asyncHandler) {
		ListenableFuture<String> f = httpClient
				.prepareGet(getURL(type) + SEPARATOR + "_search")
				.setRequestTimeout(60000000)
				.addHeader("Authorization", "Basic " + getAuth()).setBody(body)
				.addQueryParams(parameters).execute(asyncHandler);
		try {
			return f.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public void searchStreamToURL() {

	}

	// Search Document
	public void searchDocument(String type, String id) {
		httpClient.prepareGet(getURL(type, id))
				.addHeader("Authorization", "Basic " + getAuth()).execute();

	}

	/**
	 * Search by term 
	 * @param term term to be searched
	 */
	public void searchUri(String term) {

		httpClient.prepareGet(getSearchUrl(term))
				.addHeader("Authorization", "Basic " + getAuth()).execute();

	}

	// Extremely doubtful.
	
	/**
	 * Index a document without providing the id. Id will be automatically created.
	 * 
	 * @param type type of the object
	 * @param jsonDoc the objectto be indexed
	 */
	public void indexAutoId(String type, String jsonDoc) {
		RequestBuilder builder = new RequestBuilder("PUT");
		Request request = builder.setUrl(getURL(type))
				.addHeader("Authorization", "Basic " + getAuth())
				.setBody(jsonDoc).build();
		httpClient.executeRequest(request);
	}
}

// debug links

/*
 * private String URL="http://scalr.api.appbase.io", app="Trial1796",
 * userName="vspynv5Dg", password="f54091f5-ff77-4c71-a14c-1c29ab93fd15";
 */