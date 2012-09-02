package at.ac.tuwien.tracker.android;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import at.ac.tuwien.tracker.android.serverConnection.RegisterActivity;

import com.example.androidtrackerclient.R;

public class MainActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
	
		
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
	      		intent.setClass(this, RegisterActivity.class);
				startActivity(intent);
		      	break;
//			    intent.setClass(this, HttpGetActivity.class);
//			    startActivity(intent);
//	      		break;
	      	case 2:
//	      		intent.setClass(this, HttpGetParametersActivity.class);
//			    startActivity(intent);
	      		break;
	      	default:
	      		break;
		}
	}
}
