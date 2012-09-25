package at.ac.tuwien.tracker.android.serverConnection.dtos;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
@Root(name="msglistdto")
public class MsgListDTO {

	@ElementList(inline=true)
	private List<MsgDTO> msgList;
	public MsgListDTO() {
		
	}

	public MsgListDTO(List<MsgDTO> msgList) {
		super();
		this.msgList = msgList;
	}

	public List<MsgDTO> getMsgList() {
		return msgList;
	}

	public void setMsgList(List<MsgDTO> msgList) {
		this.msgList = msgList;
	}
	
	
}
