package at.ac.tuwien.tracker.android.listmenu;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.serverConnection.AddFriendActivity;
import at.ac.tuwien.tracker.android.serverConnection.FriendRequestsActivity;

public class SocialActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        
  		
		String[] options = getResources().getStringArray(R.array.social_options);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options);
		setListAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_social, menu);
        return true;
    }
    
  //***************************************
    // ListActivity methods
    //***************************************
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		Intent intent = new Intent();
		
		switch(position) 
		{
			case 0:
				intent.setClass(this, AddFriendActivity.class);
				startActivity(intent);
		      	break;
	      	case 1:
//	      		intent.setClass(this, GpsMainActivity.class);
//				startActivity(intent);
		      	break;
//			    intent.setClass(this, HttpGetActivity.class);
//			    startActivity(intent);
//	      		break;
	      	case 2:
	      		intent.setClass(this, FriendRequestsActivity.class);
			    startActivity(intent);
	      		break;
	      	default:
	      		break;
		}
	}
}
