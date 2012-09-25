package at.ac.tuwien.tracker.android.serverConnection.dtos;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="msgdto")
public class MsgDTO {

	@Element(required=false)
	private Integer id;
	@Element(required=false)
	private String text;
	@Element(required=false)
	private String sender;
	@Element(required=false)
	private Integer senderId;
	@Element(required=false)
	private Long sentDate;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public Integer getSenderId() {
		return senderId;
	}
	public void setSenderId(Integer senderId) {
		this.senderId = senderId;
	}
	public Long getSentDate() {
		return sentDate;
	}
	public void setSentDate(Long sentDate) {
		this.sentDate = sentDate;
	}
	
	
}
