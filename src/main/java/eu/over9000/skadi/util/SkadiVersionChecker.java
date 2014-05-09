package eu.over9000.skadi.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class SkadiVersionChecker {
	private static final HttpClient httpClient = HttpClients.createMinimal();
	
	private static final JsonParser parser = new JsonParser();
	
	private static String getAPIResponse(final String api_url) throws URISyntaxException, IOException {
		final URI URL = new URI(api_url);
		final HttpResponse response = SkadiVersionChecker.httpClient.execute(new HttpGet(URL));
		
		final String responseString = new BasicResponseHandler().handleResponse(response);
		return responseString;
		
	}
	
	public static String[] getLatestTag() {
		
		try {
			final String response = SkadiVersionChecker
			        .getAPIResponse("https://api.github.com/repos/s1mpl3x/skadi/tags");
			
			final JsonArray tagsArray = SkadiVersionChecker.parser.parse(response).getAsJsonArray();
			final String name = tagsArray.get(0).getAsJsonObject().get("name").getAsString();
			final String commit = tagsArray.get(0).getAsJsonObject().get("commit").getAsJsonObject().get("sha")
			        .getAsString();
			
			return new String[] { commit, name };
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		return new String[] { "", "" };
	}
}