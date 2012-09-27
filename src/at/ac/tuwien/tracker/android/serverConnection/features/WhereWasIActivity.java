package at.ac.tuwien.tracker.android.serverConnection.features;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.ErrorManager;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.common.Constants;
import at.ac.tuwien.tracker.android.common.Session;
import at.ac.tuwien.tracker.android.serverConnection.dtos.UserListDTO;
import at.ac.tuwien.tracker.android.serverConnection.helpers.AbstractAsyncActivity;

public class WhereWasIActivity extends AbstractAsyncActivity{
	
	
    DateFormat formatDateTime = DateFormat.getDateTimeInstance();
    Calendar dateTime = Calendar.getInstance();
    private TextView timeLabel;
	private OnDateSetListener date;
	private OnTimeSetListener time;
	private Button submit; 
     
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wherewasi);
        timeLabel=(TextView)findViewById(R.id.timeTxt);
        final Button timeBtn = (Button) findViewById(R.id.timeBtn); 
        final Button dateBtn = (Button) findViewById(R.id.dateBtn); 
        submit = (Button) findViewById(R.id.submit);
        
        updateLabel();
        

        WhereWasIActivity.this.date = new OnDateSetListener() {

        	public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
        		dateTime.set(Calendar.YEAR,year);
        		dateTime.set(Calendar.MONTH, monthOfYear);
        		dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        		updateLabel();
        	}
        };
        
        WhereWasIActivity.this.time = new OnTimeSetListener() {
			
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        		dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        		dateTime.set(Calendar.MINUTE,minute);

        		updateLabel();
        	}
		};
        
        dateBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				 WhereWasIActivity.this.chooseDate();
			}
		});
        
        timeBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				chooseTime();
			}
		});
        
        
        
        submit.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//TODO check if date set 
				//make call
				//give the userlist to google maps
				
//				showUsers(null);
				new PostMessageTask().execute();
			}
		});
        
        
    }
     
     
    public void chooseDate(){
        new DatePickerDialog(WhereWasIActivity.this, date, dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }
     
    public void chooseTime(){
        new TimePickerDialog(WhereWasIActivity.this, time, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), true).show();
    }
     
     
   
     
    private void updateLabel() {
        timeLabel.setText(formatDateTime.format(dateTime.getTime()));
    }
    
    
    //*++++++++++++++++++++++++++++++++//
    
    
	//***************************************
    // Private classes
    //***************************************
	private class PostMessageTask extends AsyncTask<Void, Void, String> 
	{	
		String errorMsg = "";
		@Override
		protected void onPreExecute() 
		{
		
		}
		
		@Override
		protected String doInBackground(Void... params) 
		{
			
			try 
			{
				
				
				// The URL for making the GET request
				final String url = getString(R.string.base_uri) + Constants.wherewasi;
				
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
				requestData.add("time", ""+WhereWasIActivity.this.dateTime.getTimeInMillis());
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
				errorMsg = "Sorry I am not working...";
			} 
			
			return null;
		}
		
	

		@Override
		protected void onPostExecute(String loc) 
		{
		
			
			// return the list of states
			showUsers(loc,errorMsg);
		}

		
	
	}
	
	protected void showUsers(String loc, String error) {
		
		if(loc == null){
			Toast.makeText(WhereWasIActivity.this, error + "Something went wrong", Toast.LENGTH_LONG).show();
		}else if(loc.equals("error")){
			Toast.makeText(WhereWasIActivity.this, error + "Nothing found", Toast.LENGTH_LONG).show();
		}else{
			
			Intent intent = new Intent(getApplicationContext(), HelloGoogleMaps.class);
			intent.putExtra("userlocation", loc);
			startActivity(intent);
			
		}
	}
	
	
	
	

}

