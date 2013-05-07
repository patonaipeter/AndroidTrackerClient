package at.ac.tuwien.tracker.android.serverConnection.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.serverConnection.dtos.MsgDTO;

public class NotificationAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private List<MsgDTO> notification;
	private Context context;

	public NotificationAdapter(Context context, List<MsgDTO> msgs) {
		this.mInflater = LayoutInflater.from(context);
		this.notification = msgs;
		this.context = context;
	}

	public int getCount() {
		return notification.size();
	}

	public MsgDTO getItem(int arg0) {
		return notification.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final MsgDTO message = getItem(position);
		
		View view = convertView;
		
		if (view == null)
		{
			view = mInflater.inflate(R.layout.notification_layout, parent, false);
		}

		TextView header = (TextView) view.findViewById(R.id.msg_header); 
		TextView text = (TextView) view.findViewById(R.id.msg_text); 
		header.setText(""+ message.getSender()+": ");
		Date date = new Date(message.getSentDate());
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
        String dateText = df2.format(date);
		if(message.getMsgType().equals("FRIENDREQUEST")){
			text.setText(dateText + " - added you as friend - ");
		}else if(message.getMsgType().equals("NEARTOYOU")){
			text.setText(dateText + " - is near to you now - ");
		}else{
			
			text.setText(dateText + " - " +message.getText());
		}
		
		
		
	
	
		return view;
	}


	
}
