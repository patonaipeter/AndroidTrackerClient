package at.ac.tuwien.tracker.android.serverConnection.dtos;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
@Root(name="racelistdto")
public class RaceListDTO {

	private List<RaceDTO> raceList;

	@ElementList(inline=true)
	public List<RaceDTO> getRaceList() {
		return raceList;
	}

	public void setRaceList(List<RaceDTO> raceList) {
		this.raceList = raceList;
	}
	
	
}
