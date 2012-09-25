package at.ac.tuwien.tracker.android.serverConnection.adapters;

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

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.common.Constants;
import at.ac.tuwien.tracker.android.common.Utilities;
import at.ac.tuwien.tracker.android.serverConnection.dtos.UserDTO;

public class FriendAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<UserDTO> users;
	private Context context;

	public FriendAdapter(Context context, List<UserDTO> users) {
		this.mInflater = LayoutInflater.from(context);
		this.users = users;
		this.context = context;
	}

	public int getCount() {
		return users.size();
	}

	public UserDTO getItem(int arg0) {
		return users.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final UserDTO user = getItem(position);
		
		View view = convertView;
		
		if (view == null)
		{
			view = mInflater.inflate(R.layout.activity_add_friend, parent, false);
		}

		TextView t = (TextView) view.findViewById(R.id.user_name); 
		t.setText(user.getUsername());
		view.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {                   
                Toast t = Toast.makeText(context, "Sending Friend Request to User: "+user.getUsername(), Toast.LENGTH_SHORT);
                t.show();
                
                new PostMessageTask(user.getId()).execute();
                
            }
        });
		return view;
	}


	private class PostMessageTask extends AsyncTask<Void, Void, String> 
	{	
		private MultiValueMap<String, String> requestData;
		final String url = Constants.base_url + Constants.friendrequest;
		private Integer id;
		public PostMessageTask(Integer id) {
			this.id = id;
		}

		@Override
		protected void onPreExecute() 
		{
		

			String username = AppSettings.getServer_username();
			String password = AppSettings.getServer_password();

			requestData = new LinkedMultiValueMap<String, String>();
			requestData.add("username", username);
			requestData.add("password", password);
			requestData.add("friendid", ""+id);

		}
		
		@Override
		protected String doInBackground(Void... params) 
		{
			try 
			{				
				HttpHeaders requestHeaders = new HttpHeaders();
				List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
				acceptableMediaTypes.add(MediaType.MULTIPART_FORM_DATA);
				requestHeaders.setAccept(acceptableMediaTypes);

				HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(requestData, requestHeaders);

				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate();
				
				// Make the network request, posting the message, expecting a String in response from the server
				ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
								
				return response.getBody();
			} 
			catch(Exception e) 
			{
				Utilities.LogInfo(e.getMessage());
			} 
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) 
		{
			 
			if(result.equals("success")){
				Toast t = Toast.makeText(context, "FriendRequest Successful: ", Toast.LENGTH_SHORT);
				t.show();
			}else{
				Toast t = Toast.makeText(context, "FriendRequest Failed", Toast.LENGTH_SHORT);
				t.show();
			}
		}
	}
}
