package at.ac.tuwien.tracker.android.serverConnection.dtos;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.os.Parcel;
import android.os.Parcelable;

@Root(name="userdto")
public class UserDTO implements Parcelable {

	@Element(required=false)
	private Integer id = 0;
	@Element(required=false)
	private String username;
	@Element(required=false)
	private String email;
	@Element(required=false)
	private Integer score = 0;
	@Element(required=false)
	private Long register_date = new Long(0);
	@Element(required=false)
	private Long last_activity_date = new Long(0);
	@Element(required=false)
	private Integer numOfFriends = 0;
	@Element(required=false)
	private Double lastLongitude = new Double(0);
	@Element(required=false)
	private Double lastLatitude = new Double(0);
	
	
	
	public UserDTO() {
		super();
	}
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Long getRegister_date() {
		return register_date;
	}

	public void setRegister_date(Long register_date) {
		this.register_date = register_date;
	}

	public Long getLast_activity_date() {
		return last_activity_date;
	}

	public void setLast_activity_date(Long last_activity_date) {
		this.last_activity_date = last_activity_date;
	}

	public Integer getNumOfFriends() {
		return numOfFriends;
	}

	public void setNumOfFriends(Integer numOfFriends) {
		this.numOfFriends = numOfFriends;
	}

	public Double getLastLongitude() {
		return lastLongitude;
	}

	public void setLastLongitude(Double lastLongitude) {
		this.lastLongitude = lastLongitude;
	}

	public Double getLastLatitude() {
		return lastLatitude;
	}

	public void setLastLatitude(Double lastLatitude) {
		this.lastLatitude = lastLatitude;
	}
	
	
	 public UserDTO(Parcel source){
         /*
          * Reconstruct from the Parcel
          */
         id = source.readInt();
         username = source.readString();
         email = source.readString();
         score = source.readInt();
         numOfFriends = source.readInt();
         register_date = source.readLong();
         last_activity_date = source.readLong();
         lastLongitude = source.readDouble();
         lastLatitude = source.readDouble();
         
   }

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(username);
		dest.writeString(email);
		dest.writeInt(score);
		dest.writeInt(numOfFriends);
		dest.writeLong(register_date);
		dest.writeLong(last_activity_date);
		dest.writeDouble(lastLongitude);
		dest.writeDouble(lastLatitude);
		
	}

	public static final Parcelable.Creator<UserDTO> CREATOR = new
			  Parcelable.Creator<UserDTO>() {
			      public UserDTO createFromParcel(Parcel in) {
			              return new UserDTO(in);
			      }

			      public UserDTO[] newArray(int size) {
			              return new UserDTO[size];
			      }
			  };

	
}
