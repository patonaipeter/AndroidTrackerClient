package at.ac.tuwien.tracker.android.serverConnection;

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
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.serverConnection.dtos.StatisticsDTO;
import at.ac.tuwien.tracker.android.serverConnection.helpers.AbstractAsyncActivity;

public class BasicStatisticsActivity extends AbstractAsyncActivity {

	//TODO create a layout: update button and form
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_text);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        new DownloadStateTask().execute(MediaType.APPLICATION_XML);
        
    	
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_register, menu);
//        return true;
//    }

    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

  
	//***************************************
    // Private classes
    //***************************************
	private class DownloadStateTask extends AsyncTask<MediaType, Void, StatisticsDTO> 
	{	
//		private String _abbreviation;
		
		@Override
		protected void onPreExecute() 
		{
			// before the network request begins, show a progress indicator
			showLoadingProgressDialog();
//			
//			// retrieve the abbreviation from the EditText field
//			EditText editText = (EditText) findViewById(R.id.edit_text_abbreviation);
//			
//			_abbreviation = editText.getText().toString();
		}
		
		@Override
		protected StatisticsDTO doInBackground(MediaType... params) 
		{
			try 
			{
				if (params.length <= 0)
				{
					return null;
				}
				
				MediaType mediaType = params[0];
				
				// The URL for making the GET request
				final String url = getString(R.string.base_uri) + "/statistics";
				
				// Set the Accept header for "application/json" or "application/xml"
				HttpHeaders requestHeaders = new HttpHeaders();
				List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
				acceptableMediaTypes.add(mediaType);
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
				ResponseEntity<StatisticsDTO> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, StatisticsDTO.class);
								
				// Return the state from the ResponseEntity
				return responseEntity.getBody();
			} 
			catch(Exception e) 
			{
//				Log.e(TAG, e.getMessage(), e);
			} 
			
			return null;
		}
		
		@Override
		protected void onPostExecute(StatisticsDTO stats) 
		{
			// hide the progress indicator when the network request is complete
			dismissProgressDialog();
			
			// return the list of states
			showStats(stats);
		}

	
	}
	
	private void showStats(StatisticsDTO stats) {

		final TextView textHeader = (TextView) findViewById(R.id.textView1);
		final TextView text2 = (TextView) findViewById(R.id.textView2);
		final TextView text3 = (TextView) findViewById(R.id.textView3);
		final TextView text4 = (TextView) findViewById(R.id.textView4);
		
		
		textHeader.setText(stats.getName());
		text2.setText("Num Of Races: "+stats.getNumberOfRaces());
		text3.setText("Avg Speed"+ stats.getAvgSpeed());
		text4.setText("Avg Speed In Race mode: "+ stats.getAvgSpeedInRaceMode() + " Distance: " + stats.getDistance() + " Distance in Race mode: "+ stats.getDistanceInRaceMode() + " " +
				"Elevation: " + stats.getElevation());
		
		
	}
    
  
}
