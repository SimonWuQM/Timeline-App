package com.fabula.android.timeline.models;

import java.util.Date;

import android.accounts.Account;
import android.location.Location;

import com.fabula.android.timeline.R;

public class MoodEvent extends BaseEvent{
	
	private MoodEnum mood;
	private boolean average;
	
	public MoodEvent(){
		className = this.getClass().getSimpleName();
		setShared(true);
		setAverage(false);
	}

	public MoodEvent(String experienceID, Location location, MoodEnum mood, Account user) {
		super(experienceID, location, user);
		this.mood = mood;
		className = this.getClass().getSimpleName();
		setShared(true);
		setAverage(false);
		setMoodX(mood.getMoodX());
		setMoodY(mood.getMoodY());
	}
	
	public MoodEvent(String id, String experienceID, Date dateTime, Location location, MoodEnum mood, Account user) {
		super(id, experienceID, dateTime, location, user);
		this.mood = mood;
		className = this.getClass().getSimpleName();
		setShared(true);
		setAverage(false);
		setMoodX(mood.getMoodX());
		setMoodY(mood.getMoodY());
	}

	public MoodEnum getMood() {
		return mood;
	}

	public void setMood(MoodEnum mood) {
		this.mood = mood;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}
	
	public boolean isAverage() {
		return average;
	}

	public void setAverage(boolean average) {
		this.average = average;
	}
	
	@Override
	public String toString() {
		
		return "Valence: "+ getMoodX()+ "   Arousal: "+ getMoodY()+ "";
	}

	public enum MoodEnum {
		VERY_HAPPY(1,1), HAPPY(0,1), SAD(1,0), VERY_SAD(0,0);

		private double x,y;
		
		MoodEnum(double x, double y){
			this.x = x;
			this.y = y;
		}
		
		public int getIcon(){
			
			switch (this) {
			case VERY_HAPPY:
				 return R.drawable.mood_very_happy;
			case HAPPY:
				return R.drawable.mood_happy;
//			case LIKEWISE:
//				 return R.drawable.mood_likewise;
			case SAD:
				 return R.drawable.mood_sad;
			case VERY_SAD:
				return R.drawable.mood_very_sad;
			default:
				return R.drawable.mood_happy;
			}
		}
		
//		public int getMoodInt() {
//			return type;
//		}
		
		public double getMoodX() {
			return x;
		}
		
		public double getMoodY() {
			return y;
		}
		
		public String getName(){
			return name();
		}
		
		public static MoodEnum getType(double x, double y) {
			
			if(x >= 0.5 && y >= 0.5) {
				
				MoodEnum m = MoodEnum.VERY_HAPPY;
				m.x = x;
				m.y = y;
				return m;
			}
			else if (x >= 0.5 && y <= 0.5 ) {
				MoodEnum m = MoodEnum.SAD;
				m.x = x;
				m.y = y;
				return m;
			}
			else if(x <= 0.5 && y <= 0.5) {
				MoodEnum m = MoodEnum.VERY_SAD;
				m.x = x;
				m.y = y;
				return m;
			}
			else if(x <= 0.5 && y >= 0.5) {
				MoodEnum m = MoodEnum.HAPPY;
				m.x = x;
				m.y = y;
				return m;
			}
			else {
				
				MoodEnum m = MoodEnum.HAPPY;
				m.x = x;
				m.y = y;
				return m;
			}
//			switch (type) {
//			case 2:
//				return MoodEnum.VERY_HAPPY;
//			case 1:
//				return MoodEnum.HAPPY;
//			case 0: 
////				return MoodEnum.LIKEWISE;
////			case -1:
//				return MoodEnum.SAD;
//			case -2:
//				return MoodEnum.VERY_SAD;
//			default:
//				return MoodEnum.VERY_HAPPY;
//			}
		}
		
	}
	
}
