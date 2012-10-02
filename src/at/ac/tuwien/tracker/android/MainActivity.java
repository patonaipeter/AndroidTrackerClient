package at.ac.tuwien.tracker.android;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.listmenu.RaceActivity;
import at.ac.tuwien.tracker.android.listmenu.SocialActivity;
import at.ac.tuwien.tracker.android.listmenu.StatisticsActivity;
import at.ac.tuwien.tracker.android.serverConnection.RegisterActivity;
import at.ac.tuwien.tracker.android.serverConnection.features.HelloGoogleMaps;
import at.ac.tuwien.tracker.android.serverConnection.features.WhereWasIActivity;

public class MainActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //TODO delete this in production
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        AppSettings.setServer_password(prefs.getString("server_password", "admin"));
        AppSettings.setServer_username(prefs.getString("server_username", "admin"));
        
		
		String[] options = getResources().getStringArray(R.array.main_options);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options);
		setListAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
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
				intent.setClass(this, RegisterActivity.class);
				startActivity(intent);
		      	break;
	      	case 1:
	      		intent.setClass(this, GpsMainActivity.class);
				startActivity(intent);
		      	break;
	      	case 2:
	      		intent.setClass(this, RaceActivity.class);
			    startActivity(intent);
	      		break;
	      	case 3:
	      		intent.setClass(this, StatisticsActivity.class);
			    startActivity(intent);
	      		break;
	      	case 4:
	      		intent.setClass(this, SocialActivity.class);
			    startActivity(intent);
	      		break;
	      	case 5:
	      		intent.setClass(this, WhereWasIActivity.class);
	      		startActivity(intent);
	      		break;
	      	default:
	      		break;
		}
	}
}
