package at.ac.tuwien.tracker.android.serverConnection.dtos;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
@Root(name="racelistdto")
public class RaceListDTO {
	@ElementList(inline=true)
	private List<RaceDTO> raceList;
	
	

	public RaceListDTO() {
	}

	public RaceListDTO(List<RaceDTO> raceList) {
		super();
		this.raceList = raceList;
	}


	public List<RaceDTO> getRaceList() {
		return raceList;
	}

	public void setRaceList(List<RaceDTO> raceList) {
		this.raceList = raceList;
	}
	
	
}
