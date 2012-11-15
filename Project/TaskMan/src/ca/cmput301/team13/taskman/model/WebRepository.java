package ca.cmput301.team13.taskman.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


public class WebRepository {
	
	private static String REPO_URL = "";
	private VirtualRepository vr;
	
	public WebRepository(VirtualRepository vr) {
        this.vr = vr;
    }
	
	/**
	 * Builds a JSON string from the response provided by the specified endpoint
	 * @param uri
	 * @return
	 */
	private String getJSON(String uri) {
		String line;
		StringBuilder jsonString = new StringBuilder();
		HttpClient webReader = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(REPO_URL);
		
		try {
			HttpResponse response = webReader.execute(getRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			//If the service was ready and we got a response, collect it
			if(statusCode == 200) {
				InputStream responseContent = response.getEntity().getContent();
				BufferedReader contentReader = new BufferedReader(new InputStreamReader(responseContent));
				//Build the JSON string from the response stream
				while((line = contentReader.readLine()) != null) {
					jsonString.append(line);
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return jsonString.toString();
	}
	
	/**
	 * Builds a JSON string from the response provided by the specified endpoint and argument list
	 * 		- Each Object in the arguments list should have a toString() method that prints out the
	 *        desired identifier that should be present in the request URL.
	 * @param uri			The endpoint to query
	 * @param arguments		The argument list
	 * @return				The JSON response from the server, if any. Empty string otherwise.
	 */
	public String getJSON(String uri, RequestArgument[] arguments) {
		StringBuilder getArguments = new StringBuilder();
		int numArguments = arguments.length;
		//Generate a string of arguments
		for(int i=0; i<numArguments; i++) {
			getArguments.append(arguments[i]);
			//If there are more arguments, concatenate them with "&"
			if(i < arguments.length - 1) getArguments.append("&");
		}
		System.out.println(getArguments);
		return getArguments.toString();
	}

}
