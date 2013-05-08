/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.ac.tuwien.tracker.android.serverConnection.dtos;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name="statisticsdto")
public class StatisticsDTO 
{
	@Element
	private String name;
	@Element
	private Double distance;
	@Element
	private Double elevation;
	@Element
	private Double avgSpeed;
	@Element
	private Integer numberOfRaces;
	@Element
	private Double distanceInRaceMode;
	@Element
	private Double avgSpeedInRaceMode;
	@Element
	private Integer raceWins;
	@Element
	private Integer score;
	
	public StatisticsDTO() { }
	
	public StatisticsDTO(String name, Double distance, Double elevation, Double avgSpeed, Integer numberOfRaces, Double distanceInRaceMode, Double avgSpeedInRaceMode, Integer raceWins, Integer score)
	{
		this.name = name;
		this.distance = distance;
		this.elevation = elevation;
		this.avgSpeed = avgSpeed;
		this.numberOfRaces = numberOfRaces;
		this.distanceInRaceMode = distanceInRaceMode;
		this.avgSpeedInRaceMode = avgSpeedInRaceMode;
		this.raceWins = raceWins;
		this.score = score;
	}

	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}

	
	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}
	

	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}

	public Double getAvgSpeed() {
		return avgSpeed;
	}

	public void setAvgSpeed(Double avgSpeed) {
		this.avgSpeed = avgSpeed;
	}

	public Integer getNumberOfRaces() {
		return numberOfRaces;
	}

	public void setNumberOfRaces(Integer numberOfRaces) {
		this.numberOfRaces = numberOfRaces;
	}

	public Double getDistanceInRaceMode() {
		return distanceInRaceMode;
	}

	public void setDistanceInRaceMode(Double distanceInRaceMode) {
		this.distanceInRaceMode = distanceInRaceMode;
	}

	public Double getAvgSpeedInRaceMode() {
		return avgSpeedInRaceMode;
	}

	public void setAvgSpeedInRaceMode(Double avgSpeedInRaceMode) {
		this.avgSpeedInRaceMode = avgSpeedInRaceMode;
	}

	public Integer getRaceWins() {
		return raceWins;
	}

	public void setRaceWins(Integer raceWins) {
		this.raceWins = raceWins;
	}

	public String getFormattedName()
	{
		return this.getName() + "Distance - (" + this.getDistance() + ")" + " AvgSpeed (" + this.getAvgSpeed() + ")" + "Overall Elevation (" + this.getElevation() + ")"
				+ "Distance In Races (" + this.getDistanceInRaceMode() + ")" + "NumOfRaces (" + this.getNumberOfRaces() + ")";
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
	
}
