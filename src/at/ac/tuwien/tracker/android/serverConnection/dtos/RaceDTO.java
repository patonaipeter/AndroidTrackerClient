package at.ac.tuwien.tracker.android.serverConnection.dtos;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="racedto")
public class RaceDTO {
	@Element(required=false)
	private Integer id;
	@Element(required=false)
	private String name;
	@Element(required=false)
	private Long date;
	@Element(required=false)
	private Double length;
	
	
	
	public RaceDTO() {
		
	}
	public RaceDTO(Integer id, String name, Long date, Double length) {
		this.id = id;
		this.name = name;
		this.date = date;
		this.length = length;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public Double getLength() {
		return length;
	}
	public void setLength(Double length) {
		this.length = length;
	}
	
	
}
