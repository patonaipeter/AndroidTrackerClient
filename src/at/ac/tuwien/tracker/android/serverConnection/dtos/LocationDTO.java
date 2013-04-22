package at.ac.tuwien.tracker.android.serverConnection.dtos;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.os.Parcel;
import android.os.Parcelable;

@Root(name="locationdto")
public class LocationDTO {
	
	@Element(required=false)
	private Integer id;
	@Element(required=false)
	private Long date;
	@Element(required=false)
	private Double longitude;
	@Element(required=false)
	private Double latitude;
	@Element(required=false)
	private Double altitude;
	
	
	
	public LocationDTO() {
		
	}
	
	public LocationDTO(Integer id, Long date, Double longitude,
			Double latitude, Double altitude) {
		super();
		this.id = id;
		this.date = date;
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getAltitude() {
		return altitude;
	}
	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}
	

	 public LocationDTO(Parcel source){
         /*
          * Reconstruct from the Parcel
          */
         id = source.readInt();
         date = source.readLong();
         longitude = source.readDouble();
         latitude = source.readDouble();
         altitude = source.readDouble();
         
   }

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeLong(date);
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
		dest.writeDouble(altitude);
	}

	public static final Parcelable.Creator<LocationDTO> CREATOR = new
			  Parcelable.Creator<LocationDTO>() {
			      public LocationDTO createFromParcel(Parcel in) {
			              return new LocationDTO(in);
			      }

			      public LocationDTO[] newArray(int size) {
			              return new LocationDTO[size];
			      }
			  };

	
	
}
