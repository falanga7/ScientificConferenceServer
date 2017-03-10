package it.unisa.mobileprogramming;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//Sets the path to base URL + /users
@Path("/participant")
public class Server {

	private String connectionToScopus(String url) throws IOException{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");

		//add the proper request header
		con.setRequestProperty("X-ELS-APIKey", "4b20356edf3c991101b8f439a1b1da05");
		con.setRequestProperty("Accept", "application/json");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null)
			response.append(inputLine);
		in.close();

		return response.toString();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String scopusDataMining ( @Context HttpHeaders header, @Context HttpServletResponse responseHttp,	
			@QueryParam("name") String nameURL,	@QueryParam("surname") String surnameURL) throws IOException{
		
		responseHttp.setHeader("Access-Control-Allow-Origin", "*");
		String scopusJson = connectionToScopus("http://api.elsevier.com/content/search/author?query=AUTHLASTNAME%28"+surnameURL+
				"%29&query=AUTHFIRST%28"+nameURL+"%29&field=preferred-name,affiliation-current,subject-area,dc:identifier"
				+ "&count=10");

		JSONObject response = new JSONObject();
		JSONArray responseValue = new JSONArray();		

		try {
			JSONObject root = new JSONObject(scopusJson);
			JSONObject searchresultsArray = root.getJSONObject("search-results");

			//	get the detail for each participant:
			JSONArray entryArray = searchresultsArray.getJSONArray("entry");			
			for (int i=0;i<entryArray.length();i++){
				String name,surname,apiScopusLink,affiliation,publicScopusLink,subjectArea,hIndex;
				apiScopusLink = entryArray.getJSONObject(i).getString("prism:url");
				String id = entryArray.getJSONObject(i).getString("dc:identifier").substring(10);
				publicScopusLink = "https://www.scopus.com/authid/detail.uri?authorId=" + id;

				//	getting name and surname:
				surname = entryArray.getJSONObject(i).getJSONObject("preferred-name").getString("surname");
				name = entryArray.getJSONObject(i).getJSONObject("preferred-name").getString("given-name");

				//	getting the research area:
				subjectArea = "";
				try{
					JSONArray subjectAreaArray = entryArray.getJSONObject(i).getJSONArray("subject-area");
					for (int j=0;j<subjectAreaArray.length();j++){
						String temp = subjectAreaArray.getJSONObject(j).getString("$");
						if (temp.contains(" (all)"))
							temp = temp.replace(" (all)","");
						subjectArea = subjectArea.concat(temp+", ");
					}
					subjectArea = subjectArea.substring(0, subjectArea.length()-2);
				} catch (NumberFormatException | JSONException e) {
					subjectArea = "";
				}

				//	getting the affiliation:
				affiliation = "";
				try{
					affiliation = entryArray.getJSONObject(i).getJSONObject("affiliation-current").getString("affiliation-name")+
							" ("+entryArray.getJSONObject(i).getJSONObject("affiliation-current").getString("affiliation-country")+")";
				} catch (NumberFormatException | JSONException e) {
					affiliation = "";
				}

				//	getting the h-index:
				hIndex = "";
				try{
					String participantJson = connectionToScopus(apiScopusLink+"?view=metrics&field=h-index");
					JSONObject participantRoot = new JSONObject(participantJson);
					hIndex = participantRoot.getJSONArray("author-retrieval-response").getJSONObject(0).getString("h-index");
				} catch (NumberFormatException | JSONException e) {}

				//	creating the Participant with info collected, and putting in the result's array:
				Participant newParticipant = new Participant(name, surname, publicScopusLink, hIndex, affiliation, subjectArea);
				JSONObject temp = new JSONObject(newParticipant);
				responseValue.put(temp);
			}

			//	creating the json object to return, using the result's array as value:
			response.put("results", responseValue);

		} catch (NumberFormatException | JSONException e) {
			e.printStackTrace();
		}


		//		return scopusJson;
		return response.toString();
	}

}