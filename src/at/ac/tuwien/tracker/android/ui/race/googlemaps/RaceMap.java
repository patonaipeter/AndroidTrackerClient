package at.ac.tuwien.tracker.android.ui.race.googlemaps;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.common.Constants;
import at.ac.tuwien.tracker.android.serverConnection.dtos.LocationDTO;
import at.ac.tuwien.tracker.android.serverConnection.dtos.LocationListDTO;
import at.ac.tuwien.tracker.android.serverConnection.dtos.UserDTO;
import at.ac.tuwien.tracker.android.serverConnection.helpers.AbstractAsyncListActivity;
import at.ac.tuwien.tracker.android.serverConnection.helpers.AsyncActivity;
import at.ac.tuwien.tracker.android.serverConnection.mapsutils.HelloItemizedOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class RaceMap extends MapActivity implements AsyncActivity{
	
	
	Integer raceId;
	Integer userId;

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_maps);
	    
	    
	    Intent intent = getIntent();
	    
	    raceId = intent.getIntExtra("raceid",-1);
	    userId = intent.getIntExtra("userid", -1);
	    
	    
//	    refresh(getRacePoints());
	    
		new DownloadLocationListTask().execute();
	
		
	}





	private List<LocationDTO> getRacePoints() {
		final String url = getString(R.string.base_uri) + Constants.getracepoints;

		// Set the Accept header for "application/json" or "application/xml"
		HttpHeaders requestHeaders = new HttpHeaders();
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_XML);
		requestHeaders.setAccept(acceptableMediaTypes);

		String username = AppSettings.getServer_username();
		String password = AppSettings.getServer_password();

		MultiValueMap<String, String> credentials = new LinkedMultiValueMap<String, String>();
		credentials.add("username", username);
		credentials.add("password", password);
		credentials.add("raceid",""+raceId);
		credentials.add("userid",""+userId);

		// Populate the headers in an HttpEntity object to use for the request
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(credentials, requestHeaders);
												
		// Create a new RestTemplate instance
		RestTemplate restTemplate = new RestTemplate();

		// Perform the HTTP GET request
		ResponseEntity<LocationListDTO> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, LocationListDTO.class);
						
		// Return the state from the ResponseEntity
		LocationListDTO liste = responseEntity.getBody();
		return liste.getLocationList();
		
	}





	private void showUserLocations(List<LocationDTO> locs) {
		

		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.location2);

		for(LocationDTO loc : locs){

			double longitude = loc.getLongitude();
			double latitude = loc.getLatitude(); 
			
			HelloItemizedOverlay itemizedoverlay = new HelloItemizedOverlay(drawable, this);

			MapController mapController = mapView.getController();
			mapController.setZoom(14); 

			//		    GeoPoint point = new GeoPoint(19240000,-99120000);
			GeoPoint point = new GeoPoint((int)(longitude * 1E6), (int)(latitude * 1E6));

			mapController.setCenter(point);

			OverlayItem overlayitem = new OverlayItem(point, "", "");

			itemizedoverlay.addOverlay(overlayitem);
			mapOverlays.add(itemizedoverlay);
		}
	}





//	private void showOneUserLocation(String userlocation) {
//		
//		//locationstring is two coordinates separated by "," ... parsing shouldn't be done here
//		String[] numbers =  userlocation.split(",");
//	    Double longitude = new Double(0);
//	    longitude = Double.parseDouble(numbers[0]);
//	    Double latitude =  new Double(0);
//	    latitude = Double.parseDouble(numbers[1]);
//	    
//	    MapView mapView = (MapView) findViewById(R.id.mapview);
//	    mapView.setBuiltInZoomControls(true);
//	    
//	    List<Overlay> mapOverlays = mapView.getOverlays();
//	    Drawable drawable = this.getResources().getDrawable(R.drawable.usericon);
//	    HelloItemizedOverlay itemizedoverlay = new HelloItemizedOverlay(drawable, this);
//	    
//	    MapController mapController = mapView.getController();
//	    mapController.setZoom(12); 
//	    
////	    GeoPoint point = new GeoPoint(19240000,-99120000);
//	    GeoPoint point = new GeoPoint((int)(longitude * 1E6), (int)(latitude * 1E6));
//	    
//	    mapController.setCenter(point);
//	    
//	    OverlayItem overlayitem = new OverlayItem(point, "You were here", "");
//	    
//	    itemizedoverlay.addOverlay(overlayitem);
//	    mapOverlays.add(itemizedoverlay);
//	}
//	
	
	public void refresh(List<LocationDTO> result) {
		showUserLocations(result);
		
	}
	
	
	//***************************************
    // Private classes
    //***************************************
	private class DownloadLocationListTask extends AsyncTask<Void, Void, List<LocationDTO>> 
	{	
		@Override
		protected void onPreExecute() 
		{
			// before the network request begins, show a progress indicator
			showLoadingProgressDialog();
		}
		
		@Override
		protected List<LocationDTO> doInBackground(Void... params) 
		{
			try 
			{
				
				
				final String url = getString(R.string.base_uri) + Constants.getracepoints;

				// Set the Accept header for "application/json" or "application/xml"
				HttpHeaders requestHeaders = new HttpHeaders();
				List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
				acceptableMediaTypes.add(MediaType.APPLICATION_XML);
				requestHeaders.setAccept(acceptableMediaTypes);

				String username = AppSettings.getServer_username();
				String password = AppSettings.getServer_password();

				MultiValueMap<String, String> credentials = new LinkedMultiValueMap<String, String>();
				credentials.add("username", username);
				credentials.add("password", password);
				credentials.add("raceid",""+raceId);
				credentials.add("userid",""+userId);

				// Populate the headers in an HttpEntity object to use for the request
				HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(credentials, requestHeaders);
														
				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate();

				// Perform the HTTP GET request
				ResponseEntity<LocationListDTO> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, LocationListDTO.class);
								
				// Return the state from the ResponseEntity
				LocationListDTO liste = responseEntity.getBody();
				return liste.getLocationList();
				
	
			} 
			catch(Exception e) 
			{
//				Log.e(TAG, e.getMessage(), e);
			} 
			
			return null;
		}
		
		@Override
		protected void onPostExecute(List<LocationDTO> result) 
		{
			// hide the progress indicator when the network request is complete
			dismissProgressDialog();
			
			// return the list of states
			refresh(result);
		}
	}


	protected static final String TAG = AbstractAsyncListActivity.class.getSimpleName();

	private ProgressDialog _progressDialog;
	
	private boolean _destroyed = false;

	
//	//***************************************
//    // Activity methods
//    //***************************************
//	@Override
//	public MainApplication getApplicationContext()
//	{
//		return (MainApplication) super.getApplicationContext();
//	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		_destroyed = true;
	}
		
	
	//***************************************
    // Public methods
    //***************************************
	public void showLoadingProgressDialog() 
	{
		this.showProgressDialog("Loading. Please wait...");
	}
	
	public void showProgressDialog(CharSequence message) 
	{
		if (_progressDialog == null)
		{
			_progressDialog = new ProgressDialog(this);
			_progressDialog.setIndeterminate(true);
		}
		
		_progressDialog.setMessage(message);
		_progressDialog.show();
	}
		
	public void dismissProgressDialog() 
	{
		if (_progressDialog != null && !_destroyed) 
		{
			_progressDialog.dismiss();
		}
	}

	
	
}
