package at.ac.tuwien.tracker.android.serverConnection.race;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.common.Constants;
import at.ac.tuwien.tracker.android.serverConnection.adapters.RaceAdapter;
import at.ac.tuwien.tracker.android.serverConnection.dtos.RaceDTO;
import at.ac.tuwien.tracker.android.serverConnection.dtos.RaceListDTO;
import at.ac.tuwien.tracker.android.serverConnection.helpers.AbstractAsyncListActivity;

public class SelectJoinableRaceActivity extends AbstractAsyncListActivity {

	//***************************************
    // Activity methods
    //***************************************
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		// when this activity starts, initiate an asynchronous HTTP GET request
		new DownloadRacesTask().execute();
	}
	
	
	//***************************************
    // Private methods
    //*************************************** 
	private void refreshListe(List<RaceDTO> users) 
	{	
		if (users == null) 
		{
			return;
		}
		
		RaceAdapter adapter = new RaceAdapter(this, users);
		setListAdapter(adapter);
	}
	
	
	//***************************************
    // Private classes
    //***************************************
	private class DownloadRacesTask extends AsyncTask<Void, Void, List<RaceDTO>> 
	{	
		@Override
		protected void onPreExecute() 
		{
			// before the network request begins, show a progress indicator
			showLoadingProgressDialog();
		}
		
		@Override
		protected List<RaceDTO> doInBackground(Void... params) 
		{
			try 
			{
				
				
				final String url = getString(R.string.base_uri) + Constants.listjoinableraces;

				// Set the Accept header for "application/json" or "application/xml"
				HttpHeaders requestHeaders = new HttpHeaders();
				List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
				acceptableMediaTypes.add(MediaType.APPLICATION_XML);
				requestHeaders.setAccept(acceptableMediaTypes);

				String username = AppSettings.getServer_username();
				String password = AppSettings.getServer_password();

				MultiValueMap<String, String> credentials = new LinkedMultiValueMap<String, String>();
				credentials.add("username", username);
				credentials.add("password", password);

				// Populate the headers in an HttpEntity object to use for the request
				HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(credentials, requestHeaders);
														
				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate();

				// Perform the HTTP GET request
				ResponseEntity<RaceListDTO> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, RaceListDTO.class);
								
				// Return the state from the ResponseEntity
				RaceListDTO liste = responseEntity.getBody();
				return liste.getRaceList();
				
	
			} 
			catch(Exception e) 
			{
				Log.e(TAG, e.getMessage(), e);
			} 
			
			return null;
		}
		
		@Override
		protected void onPostExecute(List<RaceDTO> result) 
		{
			// hide the progress indicator when the network request is complete
			dismissProgressDialog();
			
			// return the list of states
			refreshListe(result);
		}
	}


}
