
package at.ac.tuwien.tracker.android.ui.stats.overlay;


public class HeatPoint {
	public HeatPoint(float lat, float lon, int intensity) {
		this.lat = lat;
		this.lon = lon;
		this.intensity = intensity;
	}

	public float lat;
	public float lon;
	public int intensity;
	
	public HeatPoint(){
		this(0f,0f,0);
	}
	
	public HeatPoint(float lat, float lon){
		this(lat,lon,1);
	}
	
}
