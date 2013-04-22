package at.ac.tuwien.tracker.android.ui.race.browse;

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
import at.ac.tuwien.tracker.android.serverConnection.RaceMainActivity;
import at.ac.tuwien.tracker.android.serverConnection.adapters.BrowseRaceAdapter;
import at.ac.tuwien.tracker.android.serverConnection.dtos.RaceDTO;
import at.ac.tuwien.tracker.android.serverConnection.dtos.RaceListDTO;
import at.ac.tuwien.tracker.android.serverConnection.dtos.UserDTO;
import at.ac.tuwien.tracker.android.serverConnection.helpers.AbstractAsyncListActivity;

public class SelectRaceActivity extends AbstractAsyncListActivity {
	
	private BrowseRaceAdapter adapter;

	//***************************************
    // Activity methods
    //***************************************
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_social);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		// when this activity starts, initiate an asynchronous HTTP GET request
		new DownloadRaceListTask().execute();
		
		
//		Button button = (Button) findViewById(R.id.button_start);
//		final TextView raceName = (TextView) findViewById(R.id.editText_racename);
//		
//		button.setOnClickListener(new OnClickListener() {
//			
//			public void onClick(View v) {
//				new InitRaceTask(adapter.getSelectedUsers(), raceName.getText().toString()).execute();
//				
//			}
//		});
		
	}
	

	
	
	//***************************************
    // Private methods
    //*************************************** 
	private void refresh(List<RaceDTO> races) 
	{	
		if (races == null) 
		{
			return;
		}
		
		adapter = new BrowseRaceAdapter(this, races);
		setListAdapter(adapter);
		
	}
	
	
	private void showRace(String result, ArrayList<UserDTO> selectedUsers) {
		
		
		
		Intent intent = new Intent(this.getApplicationContext(),RaceMainActivity.class);
		intent.putExtra("alone", false);
		intent.putParcelableArrayListExtra("users", selectedUsers);
		intent.putExtra("raceid", Integer.parseInt(result));
		
		startActivity(intent);
			
	}
	
	
	//***************************************
    // Private classes
    //***************************************
	private class DownloadRaceListTask extends AsyncTask<Void, Void, List<RaceDTO>> 
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
				
				
				final String url = getString(R.string.base_uri) + Constants.browseraces;

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
			refresh(result);
		}
	}

	
	private class InitRaceTask extends AsyncTask<Void, Void, String> 
	{	
		ArrayList<UserDTO> selectedUsers;
		String racename;
		
		public InitRaceTask(ArrayList<UserDTO> selectedUsers, String string) {
			this.selectedUsers = selectedUsers;
			this.racename = string;
		}

		@Override
		protected void onPreExecute() 
		{
			// before the network request begins, show a progress indicator
			showLoadingProgressDialog();
		}
		
		@Override
		protected String doInBackground(Void... params) 
		{
			try 
			{
				
				final String url = getString(R.string.base_uri) + Constants.initnewrace;

				// Set the Accept header for "application/json" or "application/xml"
				HttpHeaders requestHeaders = new HttpHeaders();
				List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
				acceptableMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
				requestHeaders.setAccept(acceptableMediaTypes);

				String username = AppSettings.getServer_username();
				String password = AppSettings.getServer_password();

				MultiValueMap<String, String> requestData = new LinkedMultiValueMap<String, String>();
				requestData.add("username", username);
				requestData.add("password", password);
				requestData.add("racename", racename);
				String useridstring = "";
				for(UserDTO user : selectedUsers){
					useridstring += ""+user.getId()+",";
				}
				useridstring.substring(0, useridstring.length()-1);
				requestData.add("useridstring", useridstring);

				// Populate the headers in an HttpEntity object to use for the request
				HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(requestData, requestHeaders);
														
				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate();

				// Perform the HTTP GET request
				ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
								
				// Return the state from the ResponseEntity
				return responseEntity.getBody();
				
	
			} 
			catch(Exception e) 
			{
				Log.e(TAG, e.getMessage(), e);
			} 
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) 
		{
			dismissProgressDialog();
			
			//start race Intent+Service
			
			showRace(result,selectedUsers);
		}

		
	}
	
}
