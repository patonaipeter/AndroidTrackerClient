package at.ac.tuwien.tracker.android.serverConnection.dtos;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="racestatisticslistdto")
public class RaceStatisticsListDTO {

	@Element(required=false)
	private Integer raceId;
	@Element(required=false)
	private String raceName;
	@ElementList(inline=true)
	private List<RaceStatisticsDTO> stats;
	
	
	public RaceStatisticsListDTO() {
	}
	
	public RaceStatisticsListDTO(Integer raceId, String raceName,
			List<RaceStatisticsDTO> stats) {
		super();
		this.raceId = raceId;
		this.raceName = raceName;
		this.stats = stats;
	}

	public Integer getRaceId() {
		return raceId;
	}
	public void setRaceId(Integer raceId) {
		this.raceId = raceId;
	}
	public String getRaceName() {
		return raceName;
	}
	public void setRaceName(String raceName) {
		this.raceName = raceName;
	}
	public List<RaceStatisticsDTO> getStats() {
		return stats;
	}
	public void setStats(List<RaceStatisticsDTO> stats) {
		this.stats = stats;
	}
	
}
