package at.ac.tuwien.tracker.android.serverConnection.dtos;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="userdto")
public class UserDTO {

	@Element(required=false)
	private Integer id;
	@Element(required=false)
	private String username;
	@Element(required=false)
	private String email;
	@Element(required=false)
	private Integer score;
	@Element(required=false)
	private Long register_date;
	@Element(required=false)
	private Long last_activity_date;
	@Element(required=false)
	private Integer numOfFriends;
	@Element(required=false)
	private Double lastLongitude;
	@Element(required=false)
	private Double lastLatitude;
	
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

	

	
}
