package at.ac.tuwien.tracker.android.serverConnection.features;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.serverConnection.dtos.UserDTO;
import at.ac.tuwien.tracker.android.serverConnection.mapsutils.HelloItemizedOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class HelloGoogleMaps extends MapActivity{
	
	

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_maps);
	    
	    
	    Intent intent = getIntent();
		String userlocation = intent.getStringExtra("userlocation");
		
		if(userlocation != null){
			showOneUserLocation(userlocation);
		}
		
		List<UserDTO> userliste = intent.getParcelableArrayListExtra("user_list");
		if(userliste != null && !userliste.isEmpty()){
			showUserLocations(userliste);
		}
		
		
	}





	private void showUserLocations(List<UserDTO> userliste) {
		

		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.usericon);

		for(UserDTO user : userliste){

			double longitude = user.getLastLongitude();
			double latitude = user.getLastLatitude(); 
			
			HelloItemizedOverlay itemizedoverlay = new HelloItemizedOverlay(drawable, this);

			MapController mapController = mapView.getController();
			mapController.setZoom(14); 

			//		    GeoPoint point = new GeoPoint(19240000,-99120000);
			GeoPoint point = new GeoPoint((int)(longitude * 1E6), (int)(latitude * 1E6));

			mapController.setCenter(point);

			OverlayItem overlayitem = new OverlayItem(point, user.getUsername(), user.getEmail());

			itemizedoverlay.addOverlay(overlayitem);
			mapOverlays.add(itemizedoverlay);
		}
	}





	private void showOneUserLocation(String userlocation) {
		
		//locationstring is two coordinates separated by "," ... parsing shouldn't be done here
		String[] numbers =  userlocation.split(",");
	    Double longitude = new Double(0);
	    longitude = Double.parseDouble(numbers[0]);
	    Double latitude =  new Double(0);
	    latitude = Double.parseDouble(numbers[1]);
	    
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.usericon);
	    HelloItemizedOverlay itemizedoverlay = new HelloItemizedOverlay(drawable, this);
	    
	    MapController mapController = mapView.getController();
	    mapController.setZoom(12); 
	    
//	    GeoPoint point = new GeoPoint(19240000,-99120000);
	    GeoPoint point = new GeoPoint((int)(longitude * 1E6), (int)(latitude * 1E6));
	    
	    mapController.setCenter(point);
	    
	    OverlayItem overlayitem = new OverlayItem(point, "You were here", "");
	    
	    itemizedoverlay.addOverlay(overlayitem);
	    mapOverlays.add(itemizedoverlay);
	}
}
