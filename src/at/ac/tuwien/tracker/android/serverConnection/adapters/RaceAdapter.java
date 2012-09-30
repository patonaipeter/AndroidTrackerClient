package at.ac.tuwien.tracker.android.serverConnection.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.serverConnection.dtos.RaceDTO;

public class RaceAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<RaceDTO> races;
	private Context context;

	public RaceAdapter(Context context, List<RaceDTO> users) {
		this.mInflater = LayoutInflater.from(context);
		this.races = users;
		this.context = context;
	}

	public int getCount() {
		return races.size();
	}

	public RaceDTO getItem(int arg0) {
		return races.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final RaceDTO race = getItem(position);
		
		View view = convertView;
		
		if (view == null)
		{
			view = mInflater.inflate(R.layout.activity_add_friend, parent, false);
		}

		TextView t = (TextView) view.findViewById(R.id.user_name); 
		t.setText(race.getName());
		view.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {                   
                Toast t = Toast.makeText(context, "Sending Friend Request to User: "+race.getName(), Toast.LENGTH_SHORT);
                t.show();
                
                //TODO
                //save race id
                //start race service
                
            }
        });
		return view;
	}
}
