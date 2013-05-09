package at.ac.tuwien.tracker.android.serverConnection.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.serverConnection.dtos.UserDTO;
import at.ac.tuwien.tracker.android.ui.race.googlemaps.RaceMap;

public class ToplistUserAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<UserDTO> users;
	private Context context;
	private Integer raceId;
	
	
	public ToplistUserAdapter(Context context, List<UserDTO> users) {
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
		
		final TextView text = (TextView) view.findViewById(R.id.user_name);
		
		text.setText(user.getUsername() +"---"+ user.getScore());
		
		return view;
	}

	

	public void setRaceId(Integer raceId) {
		this.raceId = raceId;
		
	}
	
	public Integer getRaceId(){
		return this.raceId;
	}
	



}
