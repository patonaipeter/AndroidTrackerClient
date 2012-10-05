package at.ac.tuwien.tracker.android.serverConnection.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RacingUserAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<String> users;
	private Context context;
	private List<Integer> colors;

	public RacingUserAdapter(Context context, List<String> users, List<Integer> colors) {
		this.mInflater = LayoutInflater.from(context);
		this.users = users;
		this.context = context;
		this.colors = colors;
	}

	public int getCount() {
		return users.size();
	}

	public String getItem(int arg0) {
		return users.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final String user = getItem(position);
		final Integer color = colors.get(position);
		View view = convertView;
		
		if (view == null)
		{
			view = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		}

		TextView t = (TextView) view.findViewById(android.R.id.text1); 
		t.setTextColor(color);
		t.setText(user);
		
		return view;
	}
}
