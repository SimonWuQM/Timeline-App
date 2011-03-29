package com.fabula.android.timeline.sync;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.fabula.android.timeline.Utilities;
import com.fabula.android.timeline.models.BaseEvent;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.EventItem;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.Experiences;
import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.MoodEvent;
import com.fabula.android.timeline.models.SimplePicture;
import com.fabula.android.timeline.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * 
 * Handler for Google App Engine synchronization.
 * 
 */
public class GAEHandler {
	

	//ADDERS
	/**
	 * Sends an entire object to persist on server
	 * 
	 * @param object The object to send. Experiences, experience or event
	 */
	public static void persistTimelineObject(Object object){
		GsonBuilder gsonB = new GsonBuilder();
		gsonB.registerTypeAdapter(MoodEvent.class, new EventSerializer());
		gsonB.registerTypeAdapter(Event.class, new EventSerializer());
		gsonB.registerTypeAdapter(Experiences.class, new ExperiencesSerializer());
		
		Gson gson = gsonB.create();
		String jsonString ="";

		try {
			jsonString = gson.toJson(object, object.getClass());
		} catch (Exception e) {
			Log.e("save", e.getMessage());
		}
		
		    //Saving json to server
		    System.out.println("Lagrer JSON p� Google App Engine: "+jsonString);
		    Uploader.putToGAE(object, jsonString);
		    
		    //Saving pictures to server. Any better way to do this than the almighty nesting going on here?
		    System.out.println("Lagrer bilder p� server");
		    if(object instanceof Experiences){
		    	if(((Experiences) object).getExperiences()!=null){
			    	for (Experience ex : ((Experiences) object).getExperiences()) {
			    		if(((Experience) ex).getEvents()!=null){
				    		for (BaseEvent baseEvent : ex.getEvents()) {
				    			if(baseEvent.getEventItems()!=null && baseEvent.isShared()){
						    		for (EventItem eventI : baseEvent.getEventItems()) {
								    	if(eventI instanceof SimplePicture){
								    		Uploader.uploadFile(Utilities.IMAGE_STORAGE_FILEPATH+((SimplePicture)eventI).getPictureFilename(), ((SimplePicture)eventI).getPictureFilename());
								    	}
									}
				    			}
							}
			    		}
					} 
		    	}
		    	
		    }else if(object instanceof Event){
		    	for (EventItem eventI : ((Event)object).getEventItems()) {
			    	if(eventI instanceof SimplePicture){
			    		Uploader.uploadFile(Utilities.IMAGE_STORAGE_FILEPATH+((SimplePicture)eventI).getPictureFilename(), ((SimplePicture)eventI).getPictureFilename());
			    	}
				}
		    }
		  
	}
	
	
	public static void addGroupToServer(Group groupToAdd){
		Gson gson = new Gson();
		String jsonString ="";

		try {
			jsonString = gson.toJson(groupToAdd, Group.class);
		} catch (Exception e) {
			Log.e("save", e.getMessage());
		}
		
	    System.out.println("Lagrer JSON p� Google App Engine: "+jsonString);
	    Uploader.putGroupToGAE(jsonString);
	    
	}
	

	public static void addUserToServer(User userToAdd){
		Gson gson = new Gson();
		String jsonString ="";

		try {
			jsonString = gson.toJson(userToAdd, User.class);
		} catch (Exception e) {
			Log.e("save", e.getMessage());
		}
		
	    System.out.println("Lagrer JSON p� Google App Engine: "+jsonString);
	    Uploader.putUserToGAE(jsonString);
	}
	
	public static void addUserToGroupOnServer(Group groupToGetNewMember, User userToAddToGroup) {
		System.out.println("Legger til "+ userToAddToGroup +"  til "+groupToGetNewMember.getName()+" p� Google App Engine");
		Uploader.putUserToGroupToGAE(groupToGetNewMember, userToAddToGroup);
	}
	
	
	//REMOVERS
	public static void removeUserFromGroupOnServer(Group groupToRemoveMember, User userToRemoveFromGroup) {
		Uploader.deleteUserFromGroupToGAE(groupToRemoveMember, userToRemoveFromGroup);
	}
	
	public static void removeGroupFromDatabase(Group selectedGroup) {
		Uploader.deleteUserFromGroupToGAE(selectedGroup);
	}
	
	//GETTERS
	
	public static int getAverageMoodForExperience(Experience experience){
		return Downloader.getAverageMoodForExperience(experience);
	}
	
	//
	
	/**
	 * Custom serializer for {@link Gson} to remove empty lists, which Google App Engine can't handle right. Not good coding!
	 * 
	 */
	private static class ExperiencesSerializer implements JsonSerializer<Experiences> {
		  public JsonElement serialize(Experiences src, Type typeOfSrc, JsonSerializationContext context) {
			  if(src.getExperiences().size()==0)
					 src.setExperiences(null);
			  else{
				  for (Experience ex: src.getExperiences()) {
					 if(ex.getEvents().size()==0)
						 ex.setEvents(null);
					  else{
						  List<BaseEvent> baseEvents = new ArrayList<BaseEvent>();
						  try {
							  for (BaseEvent baseEvent : ex.getEvents()) {
								  BaseEvent bEvent = null;
									if(baseEvent instanceof Event){
										bEvent = convertEventToBaseEvent(baseEvent);
										baseEvents.add(bEvent);
									}else if (baseEvent instanceof MoodEvent){
										bEvent = convertMoodEventToBaseEvent(baseEvent);
										baseEvents.add(bEvent);
									}
							}
							  ex.setEvents(baseEvents);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					  }
				  }
			  }
			 
			Gson gson = new Gson();
		    return new JsonParser().parse(gson.toJson(src));
		  }
		}
	
	private static class EventSerializer implements JsonSerializer<BaseEvent> {
		  public JsonElement serialize(BaseEvent baseEvent, Type typeOfSrc, JsonSerializationContext context) {
			  BaseEvent bEvent = null;
			  if(baseEvent instanceof Event){
				bEvent = convertEventToBaseEvent(baseEvent);
				
			}else if (baseEvent instanceof MoodEvent){
				bEvent = convertMoodEventToBaseEvent(baseEvent);
			}
			 
			Gson gson = new Gson();
		    return new JsonParser().parse(gson.toJson(bEvent));
		  }

	}
	
	private static BaseEvent convertEventToBaseEvent(BaseEvent baseEvent) {
		BaseEvent bEvent = new BaseEvent(baseEvent.getId(), baseEvent.getExperienceid(), 
				baseEvent.getDatetime(), baseEvent.getLocation(), baseEvent.getUser());
		
		bEvent.setClassName(baseEvent.getClassName());
		if(((Event)baseEvent).getEmotionList().size()==0)
			bEvent.setEmotionList(null);
		else
			bEvent.setEmotionList(baseEvent.getEmotionList());
		
		if(((Event)baseEvent).getEventItems().size()==0)
			bEvent.setEventItems(null);
		else
			bEvent.setEventItems(((Event)baseEvent).getEventItems());
		
		if(((Event)baseEvent).getTags().size()==0)
			bEvent.setTags(null);
		else
			bEvent.setTags(((Event)baseEvent).getTags());
		
		bEvent.setShared(((Event)baseEvent).isShared());
		
		return bEvent;
	}

	private static BaseEvent convertMoodEventToBaseEvent(BaseEvent baseEvent) {
		BaseEvent moodBaseEvent = new BaseEvent(baseEvent.getId(), baseEvent.getExperienceid(), 
				baseEvent.getDatetime(), baseEvent.getLocation(), baseEvent.getUser());
		moodBaseEvent.setClassName(((MoodEvent)baseEvent).getClassName());
		moodBaseEvent.setMoodInt(((MoodEvent)baseEvent).getMood().getMoodInt());
		moodBaseEvent.setShared(((MoodEvent)baseEvent).isShared());
		moodBaseEvent.setAverage(((MoodEvent)baseEvent).isAverage());
		return moodBaseEvent;
	}


}
