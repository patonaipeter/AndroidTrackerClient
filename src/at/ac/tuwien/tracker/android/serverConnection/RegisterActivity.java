package at.ac.tuwien.tracker.android.serverConnection;

import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import at.ac.tuwien.tracker.android.R;

public class RegisterActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        
    	final Button buttonSubmit = (Button) findViewById(R.id.submit);
    	buttonSubmit.setOnClickListener(new View.OnClickListener() 
			{
            	public void onClick(View v) 
            	{
            		new PostMessageTask().execute();
            	}
			}
		);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_register, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    
    
  //***************************************
    // Private methods
    //***************************************	
	private void showResult(String result)
	{
		if (result != null)
		{
			// display a notification to the user with the response message
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		}
		else
		{
			Toast.makeText(this, "I got null, something happened!", Toast.LENGTH_LONG).show();
		}
	}
    
  //***************************************
    // Private classes
    //***************************************
	private class PostMessageTask extends AsyncTask<Void, Void, String> 
	{	
		private MultiValueMap<String, String> _message;
		
		@Override
		protected void onPreExecute() 
		{
			// before the network request begins, show a progress indicator
//			showLoadingProgressDialog();
			
			// assemble the map
			_message = new LinkedMultiValueMap<String, String>();
			
			EditText editText = (EditText) findViewById(R.id.usernameText);
			_message.add("username", editText.getText().toString());
			
			editText = (EditText) findViewById(R.id.passwordText);
			_message.add("password", editText.getText().toString());
			
			editText = (EditText) findViewById(R.id.emailText);
			_message.add("email", editText.getText().toString());
		}
		
		@Override
		protected String doInBackground(Void... params) 
		{
			try 
			{				
				// The URL for making the POST request
				final String url = getString(R.string.base_uri) + "/sendmessagemap";
				
				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate();
				
				// Make the network request, posting the message, expecting a String in response from the server
				ResponseEntity<String> response = restTemplate.postForEntity(url, _message, String.class);
								
				// Return the response body to display to the user
				return response.getBody();
			} 
			catch(Exception e) 
			{
//				Log.e(TAG, e.getMessage(), e);
			} 
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) 
		{
			// after the network request completes, hid the progress indicator
//			dismissProgressDialog();
			
			// return the response body to the calling class
			showResult(result);
		}
	}
}
