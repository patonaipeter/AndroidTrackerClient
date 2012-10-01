package at.ac.tuwien.tracker.android.serverConnection.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.serverConnection.dtos.UserDTO;

public class FriendAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<UserDTO> users;
	private Context context;
	
	private ArrayList<UserDTO> selectedUsers = new ArrayList<UserDTO>();
	
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
		
		final TextView text = (TextView) view.findViewById(R.id.user_name);
		
		text.setText(user.getUsername());
		view.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {                   
                Toast toast = Toast.makeText(context, "Select Friend: "+user.getUsername(), Toast.LENGTH_SHORT);
                toast.show();
              
                
                if(selectedUsers.contains(user)){
                	selectedUsers.remove(user);
                	text.setTextColor(Color.BLACK);
                }else{
                	selectedUsers.add(user);
                	text.setTextColor(Color.GREEN);
                }
//                new PostMessageTask(user.getId()).execute();
                
            }
        });
		return view;
	}

	public ArrayList<UserDTO> getSelectedUsers() {
		return selectedUsers;
	}
	
	



}
