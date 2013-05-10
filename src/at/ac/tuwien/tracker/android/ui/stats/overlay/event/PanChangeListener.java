
package at.ac.tuwien.tracker.android.ui.stats.overlay.event;

import com.google.android.maps.GeoPoint;

public interface PanChangeListener {
	public void onPan(GeoPoint old, GeoPoint current);
}
