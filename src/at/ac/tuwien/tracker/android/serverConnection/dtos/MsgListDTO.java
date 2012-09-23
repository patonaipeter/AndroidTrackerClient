package at.ac.tuwien.tracker.android.serverConnection.dtos;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
@Root(name="msglistdto")
public class MsgListDTO {

	private List<MsgDTO> msgList;

	@ElementList(inline=true)
	public List<MsgDTO> getMsgList() {
		return msgList;
	}

	public void setMsgList(List<MsgDTO> msgList) {
		this.msgList = msgList;
	}
	
	
}
