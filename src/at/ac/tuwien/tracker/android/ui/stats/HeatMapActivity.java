package at.ac.tuwien.tracker.android.ui.stats;

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
import android.os.AsyncTask;
import android.os.Bundle;
import at.ac.tuwien.tracker.android.R;
import at.ac.tuwien.tracker.android.common.AppSettings;
import at.ac.tuwien.tracker.android.common.Constants;
import at.ac.tuwien.tracker.android.serverConnection.dtos.LocationDTO;
import at.ac.tuwien.tracker.android.serverConnection.dtos.LocationListDTO;
import at.ac.tuwien.tracker.android.serverConnection.helpers.AbstractAsyncListActivity;
import at.ac.tuwien.tracker.android.serverConnection.helpers.AsyncActivity;
import at.ac.tuwien.tracker.android.ui.stats.overlay.HeatMapOverlay;
import at.ac.tuwien.tracker.android.ui.stats.overlay.HeatPoint;
import at.ac.tuwien.tracker.android.ui.stats.overlay.SimpleMapView;
import at.ac.tuwien.tracker.android.ui.stats.overlay.event.PanChangeListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;

public class HeatMapActivity extends MapActivity implements AsyncActivity{

	private HeatMapOverlay overlay;
	List<HeatPoint> heats;
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
//		this.helper = new MapExDBHelper(this);
		setContentView(R.layout.heat);
		new DownloadLocationListTask().execute();
	}
	private List<HeatPoint> loadPoints() {
			List<HeatPoint> heats = new ArrayList<HeatPoint>();
			float longitude = 13;
			float latitude = 42; 
			heats.add(new HeatPoint(42,13,100));
			heats.add(new HeatPoint((float) 42.1,(float)13.1,50));
			return heats;
	}
	
	
	public void showHeatMap(List<LocationDTO> locs){
		final SimpleMapView mapview = (SimpleMapView)findViewById(R.id.mapview);
		mapview.setBuiltInZoomControls(true);
		
		MapController mapController = mapview.getController();
		mapController.setZoom(10); 
		
		
		
		heats = new ArrayList<HeatPoint>();
		for(LocationDTO l : locs){
			float latitude = l.getLatitude().floatValue(); 
			float longitude = l.getLongitude().floatValue();
			//heatpoint lat/lon
			heats.add(new HeatPoint(latitude,longitude));
		}
		
		
		float longitude = locs.get(0).getLongitude().floatValue();
		float latitude = locs.get(0).getLatitude().floatValue(); 
		
		GeoPoint point = new GeoPoint((int)(latitude * 1E6), (int)(longitude * 1E6));

		mapController.setCenter(point);
		
		this.overlay = new HeatMapOverlay(20000, mapview);
//		overlay.updateHeat(loadPoints());
		
		mapview.getOverlays().add(overlay);
		final List<HeatPoint> points = heats;
		overlay.updateHeat(points);
		mapview.addPanChangeListener(new PanChangeListener() {
			
			public void onPan(GeoPoint old, GeoPoint current) {
				
				if(points.size() > 0){
					overlay.updateHeat(heats);
				}
				
			}

			
		});
	}
	
	protected static final String TAG = AbstractAsyncListActivity.class.getSimpleName();

	private ProgressDialog _progressDialog;
	
	private boolean _destroyed = false;

	
	public void refresh(List<LocationDTO> result) {
//		
//		String[] splits = result.split(";");
//		List<LocationDTO> locList = new ArrayList<LocationDTO>();
//		for(int i = 0; i < splits.length-2; i=i+2){
//			LocationDTO dto = new LocationDTO();
//			dto.setLatitude(Double.parseDouble(splits[i]));
//			dto.setLatitude(Double.parseDouble(splits[i+1]));
//			locList.add(dto);
//		}
		
		showHeatMap(result);
		
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
				
				
				final String url = getString(R.string.base_uri) + Constants.heatmap;

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

				// Populate the headers in an HttpEntity object to use for the request
				HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(credentials, requestHeaders);
														
				// Create a new RestTemplate instance
				RestTemplate restTemplate = new RestTemplate();

				// Perform the HTTP GET request
				ResponseEntity<LocationListDTO> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, LocationListDTO.class);
								
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
