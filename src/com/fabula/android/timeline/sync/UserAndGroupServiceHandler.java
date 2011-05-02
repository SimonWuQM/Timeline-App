package com.fabula.android.timeline.sync;

import android.app.ProgressDialog;
import android.content.Context;

import com.fabula.android.timeline.ProgressDialogActivity;
import com.fabula.android.timeline.database.contentmanagers.UserGroupManager;
import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.Groups;
import com.fabula.android.timeline.models.User;
import com.fabula.android.timeline.models.Users;
import com.fabula.android.timeline.utilities.Utilities;

public class UserAndGroupServiceHandler {

	private Context mContext;
	private ProgressDialog progressDialog;
	private Runnable getUsersAndGroupsRunnable;
	private UserGroupManager uGManager;
	private ProgressDialogActivity activity;

	public UserAndGroupServiceHandler(ProgressDialogActivity activity, Context context) {
		this.mContext = context;
		this.activity = activity;
		
			uGManager = new UserGroupManager(mContext);
			getUsersAndGroupsRunnable = new Runnable(){
	            public void run() {
	            	getUsersAndGroupsAddToDatabase();
	            }
	        };
	}
    /**
     * Metoden som starter tr�den som henter serier fra serveren
     * Kan vises uten progressDialog, som er hendig n�r man trykker seg tilbake til startskjermen.
     * Evt. endring vil da reflekteres, men uten � hindre brukereren i se p� lista. 
	 * Call this method when you don't want to handle progressbar and threading yourself.
	 * 
     * 
     * @param showProgressDialog Gir mulighet til � vise/ikke vise progressbar ved lasting av nye elementer.
     */
	public void startDownloadUsersAndGroups() {
		progressDialog = new ProgressDialog(mContext);
		Thread thread =  new Thread(null, getUsersAndGroupsRunnable, "getUsersAndGroups");
        thread.start();
        progressDialog = ProgressDialog.show(mContext,    
              "", "", true);
	}
	
	private void getUsersAndGroupsAddToDatabase() {
		uGManager.truncateUserGroupDatabase();
		getUsersAndAddToDatabase();
    	getGroupsAndAddToDatabase();
    	activity.runOnUiThread(new Runnable() {
			public void run() {
				progressDialog.dismiss();
				activity.callBack();
			}
		});
	}
	
	private void getUsersAndAddToDatabase() {
		activity.runOnUiThread(new Runnable() {
			
			public void run() {
				progressDialog.setMessage("Loading all users ...");
			}
		});
		
		downloadUsers();	
	}
	private void downloadUsers() {
		Users selectableUsersFromServer = ServerDownloader.getUsersFromServer();
		if(selectableUsersFromServer!=null){
			for (User user : selectableUsersFromServer.getUsers()) {
				uGManager.addUserToUserDatabase(user);
			}
		}
	}	
	
	private void getGroupsAndAddToDatabase() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				progressDialog.setMessage("Loading my groups ...");
			}
		});
		downloadGroups();
	}
	
	
	private void downloadGroups() {
		Groups groupsUserIsConnectedToFromServer = ServerDownloader.getGroupsFromServer(new User(Utilities.getUserAccount(mContext).name));
		
		if(groupsUserIsConnectedToFromServer!=null){
			for (Group groupToAddToDatabase : groupsUserIsConnectedToFromServer.getGroups()) {
				uGManager.addGroupToGroupDatabase(groupToAddToDatabase);
				for (User user : groupToAddToDatabase.getMembers()) {
					uGManager.addUserToAGroupInTheDatabase(groupToAddToDatabase, user);
				}
			}
		}
	}
	
	/**
	 * Call this method when you handle progressbar and threading yourself.
	 * 
	 */
	public void downloadUsersAndGroups(){
		uGManager.truncateUserGroupDatabase();
		downloadUsers();
		downloadGroups();
	}
	
}
