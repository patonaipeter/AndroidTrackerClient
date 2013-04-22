package at.ac.tuwien.tracker.android.listmenu;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.serverConnection.race.SelectFriendsActivity;
import at.ac.tuwien.tracker.android.serverConnection.race.SelectJoinableRaceActivity;
import at.ac.tuwien.tracker.android.ui.race.browse.SelectRaceActivity;

public class RaceActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);
		
		String[] options = getResources().getStringArray(R.array.race_options);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options);
		setListAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_race, menu);
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
				intent.setClass(this, SelectRaceActivity.class);
				startActivity(intent);
		      	break;
	      	case 1:
			    intent.setClass(this, SelectFriendsActivity.class);
			    startActivity(intent);
	      		break;
	      	case 2:
	      		intent.setClass(this, SelectJoinableRaceActivity.class);
			    startActivity(intent);
	      		break;
	      	default:
	      		break;
		}
	}
}
