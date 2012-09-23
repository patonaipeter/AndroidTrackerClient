package at.ac.tuwien.tracker.android.serverConnection.dtos;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="userlistdto")
public class UserListDTO {

	@ElementList(inline=true)
	private List<UserDTO> userList;
	
	public UserListDTO() {
	}

	public UserListDTO(List<UserDTO> userList) {
		super();
		this.userList = userList;
	}

	public List<UserDTO> getUserList() {
		return userList;
	}

	public void setUserList(List<UserDTO> userList) {
		this.userList = userList;
	}
	
	
	
}
