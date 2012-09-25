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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.common.Constants;
import at.ac.tuwien.tracker.android.common.Utilities;
import at.ac.tuwien.tracker.android.serverConnection.dtos.MsgDTO;

import com.example.androidtrackerclient.R;

public class MsgAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<MsgDTO> requests;
	private Context context;

	public MsgAdapter(Context context, List<MsgDTO> msgs) {
		this.mInflater = LayoutInflater.from(context);
		this.requests = msgs;
		this.context = context;
	}

	public int getCount() {
		return requests.size();
	}

	public MsgDTO getItem(int arg0) {
		return requests.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final MsgDTO message = getItem(position);
		
		View view = convertView;
		
		if (view == null)
		{
			view = mInflater.inflate(R.layout.friendrequests_layout, parent, false);
		}

		TextView t = (TextView) view.findViewById(R.id.msg_text); 
		t.setText("From: "+ message.getSender());
		
		Button accept = (Button) view.findViewById(R.id.button1);
		Button deny = (Button) view.findViewById(R.id.button2);
		
		accept.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {                   
                Toast t = Toast.makeText(context, "Accepting Friend Request to User: "+message.getSender(), Toast.LENGTH_SHORT);
                t.show();
            	
                new PostMessageTask(message.getId(),true,position).execute();
                
            }
        });
		
		deny.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {                   
                Toast t = Toast.makeText(context, "Denying Friend Request to User: "+message.getSender(), Toast.LENGTH_SHORT);
                t.show();
                
                new PostMessageTask(message.getId(),false,position).execute();
                
            }
        });
	
		return view;
	}


	private class PostMessageTask extends AsyncTask<Void, Void, String> 
	{	
		private MultiValueMap<String, String> requestData;
		String url = Constants.base_url + "/friendrequest";
		private Integer id;
		private int position;
		public PostMessageTask(Integer id, boolean accept, int position) {
			this.id = id;
			this.position = position;
			if(accept){
				url = Constants.base_url + Constants.acceptfriendrequest;
			}else{
				url = Constants.base_url + Constants.denyfriendrequest;
			}
		}

		@Override
		protected void onPreExecute() 
		{
		

			String username = AppSettings.getServer_username();
			String password = AppSettings.getServer_password();

			requestData = new LinkedMultiValueMap<String, String>();
			requestData.add("username", username);
			requestData.add("password", password);
			requestData.add("messageid", ""+id);

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
				Toast t = Toast.makeText(context, "Succesful: ", Toast.LENGTH_SHORT);
				t.show();
			
				//removing listview element
				requests.remove(position);
            	MsgAdapter.this.notifyDataSetChanged();
            	
			}else{
				Toast t = Toast.makeText(context, "Error", Toast.LENGTH_SHORT);
				t.show();
			}
		}
	}
}
