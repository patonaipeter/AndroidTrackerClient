package at.ac.tuwien.tracker.android.serverConnection.features;

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
import android.widget.Toast;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.common.Constants;
import at.ac.tuwien.tracker.android.common.Session;
import at.ac.tuwien.tracker.android.serverConnection.dtos.UserDTO;
import at.ac.tuwien.tracker.android.serverConnection.dtos.UserListDTO;
import at.ac.tuwien.tracker.android.serverConnection.helpers.AbstractAsyncActivity;

public class ListNearUsersActivity extends AbstractAsyncActivity{
	
	
     
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
		new PostMessageTask().execute();
	
        
    }
     
     

//++++++++++++++++++++++++++//
    
    
	//***************************************
    // Private classes
    //***************************************
	private class PostMessageTask extends AsyncTask<Void, Void, UserListDTO> 
	{	
		String errorMsg = "";
		@Override
		protected void onPreExecute() 
		{
			showLoadingProgressDialog();
		}
		
		@Override
		protected UserListDTO doInBackground(Void... params) 
		{
			
			try 
			{
				
				
				// The URL for making the GET request
				final String url = getString(R.string.base_uri) + Constants.listnearusers;
				
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
				
				requestData.add("longitude", ""+Session.getCurrentLongitude());
				requestData.add("latitude", ""+Session.getCurrentLatitude());
				requestData.add("radius","0,0001"); //TODO make value changeable
				
				if(!Session.isStarted()){
					errorMsg = "GPSLogging Service is off! Server call cannot be made.";
				
					return null;
				}
				
				// Populate the headers in an HttpEntity object to use for the request
				HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(requestData, requestHeaders);
														
				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate();
				
				// Perform the HTTP GET request
				ResponseEntity<UserListDTO> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, UserListDTO.class);
								
				// Return the state from the ResponseEntity
				return responseEntity.getBody();
			} 
			catch(Exception e) 
			{
				errorMsg = "Sorry I am not working...";
			} 
			
			return null;
		}
		
	

		@Override
		protected void onPostExecute(UserListDTO locs) 
		{
		
			dismissProgressDialog();
			// return the list of states
			showUsers(locs,errorMsg);
		}

		
	
	}
	
	protected void showUsers(UserListDTO locs, String error) {
		
		if(locs == null){
			Toast.makeText(ListNearUsersActivity.this, error + "Something went wrong", Toast.LENGTH_LONG).show();
		}else{
			
			Intent intent = new Intent(getApplicationContext(), HelloGoogleMaps.class);
			ArrayList<UserDTO> dataList = new ArrayList<UserDTO>();
			dataList.addAll(locs.getUserList());
			intent.putParcelableArrayListExtra("user_list", dataList);
			
			startActivity(intent);
		}
	}
	
	
	
	

}

