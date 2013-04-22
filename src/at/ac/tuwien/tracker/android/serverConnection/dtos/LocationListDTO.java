package at.ac.tuwien.tracker.android.serverConnection.dtos;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
@Root(name="locatiolistdto")
public class LocationListDTO {
	@ElementList(inline=true)
	private List<LocationDTO> locationList;
	
	

	public LocationListDTO() {
	}

	public LocationListDTO(List<LocationDTO> locationList) {
		super();
		this.locationList = locationList;
	}


	public List<LocationDTO> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<LocationDTO> locationList) {
		this.locationList = locationList;
	}
	
	
}
