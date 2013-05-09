package at.ac.tuwien.tracker.android.ui.stats;

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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.common.Constants;
import at.ac.tuwien.tracker.android.serverConnection.adapters.BrowseFriendAdapter;
import at.ac.tuwien.tracker.android.serverConnection.adapters.ToplistUserAdapter;
import at.ac.tuwien.tracker.android.serverConnection.dtos.UserDTO;
import at.ac.tuwien.tracker.android.serverConnection.dtos.UserListDTO;
import at.ac.tuwien.tracker.android.serverConnection.helpers.AbstractAsyncListActivity;

public class ToplistActivity extends AbstractAsyncListActivity {
	
	private ToplistUserAdapter adapter;
	private Integer raceId;

	//***************************************
    // Activity methods
    //***************************************
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social);
		
		Intent intent = getIntent();
        raceId = intent.getIntExtra("raceid", -1);
		
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		new ToplistRetrieval().execute();
		
		
	}
	

	
	
	//***************************************
    // Private methods
    //*************************************** 
	private void refresh(List<UserDTO> users) 
	{	
		if (users == null) 
		{
			return;
		}
		
		adapter = new ToplistUserAdapter(this, users);
		adapter.setRaceId(raceId);
		setListAdapter(adapter);
		
	}
	
	
	
	
	//***************************************
    // Private classes
    //***************************************
	private class ToplistRetrieval extends AsyncTask<Void, Void, List<UserDTO>> 
	{	
		@Override
		protected void onPreExecute() 
		{
			// before the network request begins, show a progress indicator
			showLoadingProgressDialog();
		}
		
		@Override
		protected List<UserDTO> doInBackground(Void... params) 
		{
			try 
			{
				
				
				final String url = getString(R.string.base_uri) + Constants.toplist;

				// Set the Accept header for "application/json" or "application/xml"
				HttpHeaders requestHeaders = new HttpHeaders();
				List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
				acceptableMediaTypes.add(MediaType.APPLICATION_XML);
				requestHeaders.setAccept(acceptableMediaTypes);

				MultiValueMap<String, String> credentials = new LinkedMultiValueMap<String, String>();

				// Populate the headers in an HttpEntity object to use for the request
				HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(credentials, requestHeaders);
														
				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate();

				// Perform the HTTP GET request
				ResponseEntity<UserListDTO> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, UserListDTO.class);
								
				// Return the state from the ResponseEntity
				UserListDTO liste = responseEntity.getBody();
				return liste.getUserList();
				
	
			} 
			catch(Exception e) 
			{
				Log.e(TAG, e.getMessage(), e);
			} 
			
			return null;
		}
		
		@Override
		protected void onPostExecute(List<UserDTO> result) 
		{
			// hide the progress indicator when the network request is complete
			dismissProgressDialog();
			
			// return the list of states
			refresh(result);
		}
	}

	
	
	
}
