package at.ac.tuwien.tracker.android.serverConnection.features;

import android.os.Bundle;
import at.ac.tuwien.tracker.android.R;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class HelloGoogleMaps extends MapActivity{

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_maps);
	    
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	}

}
