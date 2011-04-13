package com.fabula.android.timeline.sync;

import java.util.List;

import android.util.Log;

import com.fabula.android.timeline.models.BaseEvent;
import com.fabula.android.timeline.models.Event;
import com.fabula.android.timeline.models.EventItem;
import com.fabula.android.timeline.models.Experience;
import com.fabula.android.timeline.models.Experiences;
import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.SimplePicture;
import com.fabula.android.timeline.models.SimpleRecording;
import com.fabula.android.timeline.models.SimpleVideo;
import com.fabula.android.timeline.models.User;
import com.fabula.android.timeline.utilities.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * Handler for Google App Engine synchronization.
 * 
 * All actions to the Google App Engine is routed through this method.
 * 
 * 
 */
public class GAEHandler {
	private static final String TAG = "Google App Engine Handler";
	

	//ADDERS
	/**
	 * Sends an entire object to persist on server
	 * 
	 * @param object The object to send. Experiences, experience or event
	 */
	public static void persistTimelineObject(Object object){
		GsonBuilder gsonB = new GsonBuilder();
		gsonB.registerTypeAdapter(BaseEvent.class, new Serializers.EventSerializer());
		gsonB.registerTypeAdapter(Experiences.class, new Serializers.ExperiencesSerializer());
		
		Gson gson = gsonB.create();
		String jsonString ="";

		try {
			jsonString = gson.toJson(object, object.getClass());
		} catch (Exception e) {
			Log.e("save", e.getMessage());
		}
		
		    Log.i(TAG, "Saving TimelineObject-JSON to Google App Engine ");
		    Uploader.putToGAE(object, jsonString);
		    
		    Log.i(TAG, "Saving files on server");
		    storeFilesOnServer(object);
		  
	}
	
	public static void addGroupToServer(Group groupToAdd){
		Gson gson = new Gson();
		String jsonString ="";

		try {
			jsonString = gson.toJson(groupToAdd, Group.class);
		} catch (Exception e) {
			Log.e("save", e.getMessage());
		}
		
	    System.out.println();
	    Log.i(TAG, "Saving group-JSON on Google App Engine: "+jsonString);
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
		
		Log.i(TAG, "Saving user-JSON on Google App Engine: "+jsonString);
	    Uploader.putUserToGAE(jsonString);
	}
	
	public static void addUserToGroupOnServer(Group groupToGetNewMember, User userToAddToGroup) {
		Log.i(TAG,"Adding "+ userToAddToGroup +"  to "+groupToGetNewMember.getName()+" on Google App Engine");
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
	
	public static Experiences getAllSharedExperiences(User user){
		return Downloader.getAllSharedExperiencesFromServer(user);
	}
	
	public static List<User> getUsers(){
		return Downloader.getUsersFromServer().getUsers();
	}
	
	public static boolean IsUserRegistered(String username) {
		return Downloader.IsUserRegistered(username);
	}
	

	
	//HELPERS
	
	//Saving pictures to server. 
	//TODO: Any better way to do this than the almighty nesting going on here?
	private static void storeFilesOnServer(Object object) {
		if(object instanceof Experiences){
			if(((Experiences) object).getExperiences()!=null){
		    	for (Experience ex : ((Experiences) object).getExperiences()) {
		    		if(((Experience) ex).getEvents()!=null){
			    		for (BaseEvent baseEvent : ex.getEvents()) {
			    			if(baseEvent.getEventItems()!=null && baseEvent.isShared()){
					    		for (EventItem eventI : baseEvent.getEventItems()) {
							    	if(eventI instanceof SimplePicture){
							    		Uploader.uploadFile(Constants.IMAGE_STORAGE_FILEPATH+((SimplePicture)eventI).getPictureFilename(), 
							    				((SimplePicture)eventI).getPictureFilename());
							    	}else if(eventI instanceof SimpleVideo){
							    		Uploader.uploadFile(Constants.VIDEO_STORAGE_FILEPATH+((SimpleVideo)eventI).getVideoFilename(), 
							    				((SimpleVideo)eventI).getVideoFilename());
							    	}else if(eventI instanceof SimpleRecording){
							    		Uploader.uploadFile(Constants.RECORDING_STORAGE_FILEPATH+((SimpleRecording)eventI).getRecordingFilename(), 
							    				((SimpleRecording)eventI).getRecordingFilename());
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
		    		Uploader.uploadFile(Constants.IMAGE_STORAGE_FILEPATH+((SimplePicture)eventI).getPictureFilename(), 
		    				((SimplePicture)eventI).getPictureFilename());
		    	}else if(eventI instanceof SimpleVideo){
		    		Uploader.uploadFile(Constants.VIDEO_STORAGE_FILEPATH+((SimpleVideo)eventI).getVideoFilename(), 
		    				((SimpleVideo)eventI).getVideoFilename());
		    	}else if(eventI instanceof SimpleRecording){
		    		Uploader.uploadFile(Constants.RECORDING_STORAGE_FILEPATH+((SimpleRecording)eventI).getRecordingFilename(), 
		    				((SimpleRecording)eventI).getRecordingFilename());
		    	}
			}
		}
	}

}
