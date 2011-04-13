package com.fabula.android.timeline.models;

import java.util.ArrayList;
import java.util.List;



/**
 * Wrapper class for JSON-serialization
 * 
 * 
 * @author andekr
 *
 */
public class Experiences {

	/**
	 * No-args for Gson  
	 */
	public Experiences() {}

	private List<Experience> experiences;
	
	
	public Experiences(List<Experience> experiences) {
		this.experiences = experiences;
	}

	public List<Experience> getExperiences() {
		return experiences;
	}

	public void setExperiences(ArrayList<Experience> experiences) {
		this.experiences = experiences;
	}
	
	
	
}
