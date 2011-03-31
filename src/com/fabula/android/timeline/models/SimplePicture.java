package com.fabula.android.timeline.models;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.fabula.android.timeline.Utilities;
import com.fabula.android.timeline.providers.PictureProvider;

public class SimplePicture extends EventItem {

	private transient Uri pictureUri;
	
	public SimplePicture(){}
	
	public SimplePicture(Context c) {
		super(c);
		className = "SimplePicture";
	}
	
	public SimplePicture(String id, Uri uri, Account u, String pictureFilename) {
		super(id, u);
		className = "SimplePicture";
		this.pictureUri = uri;
		this.filename = pictureFilename;
	}
	
	public SimplePicture(String id, Account u, String pictureFilename) {
		super(id, u);
		className = "SimplePicture";
		File file = Utilities.DownloadFromUrl(pictureFilename, Utilities.IMAGE_STORAGE_FILEPATH+pictureFilename);
		this.pictureUri = Uri.fromFile(file);
		this.filename = pictureFilename;
	}
	

	public Uri getPictureUri() {
		return pictureUri;
	}
	public void setPictureUri(Uri pictureUri, String pictureFilename) {
		this.pictureUri = pictureUri;
		this.filename = pictureFilename;
	}
	
	public String getPictureFilename() {
		return filename;
	}

	public void setPictureFilename(String pictureFilename) {
		this.filename = pictureFilename;
	}
	
	
	
	//For GSON serializing
	public String getClassName() {
		return className;
	}

	@Override
	public View getView(Context context) {
        ImageView image = new ImageView(context);
       // image.setImageURI(getPictureUri());
        Bitmap bm = null;
        try {
			bm = MediaStore.Images.Media.getBitmap(context.getContentResolver(), getPictureUri());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        image.setImageBitmap(getThumbnail(bm));
        bm.recycle();
        image.setTag(this);

        image.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.TOP));
        
        return image;
	}
	@Override
	public Intent getIntent(){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(getPictureUri(), "image/*");
		
		return intent;
	}
	
	public Bitmap getThumbnail(Bitmap bm){
        //actual width of the image (img is a Bitmap object)
        int width = bm.getWidth();
        int height = bm.getHeight();
     
        //new width
        int newWidth = 400;

        // calculate the scale
        float scaleWidth = (float)((float)newWidth /(float)width);

        //new height
        int newHeight = (int) ((int)height*scaleWidth);


        // recreate the new Bitmap and set it back
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
        return resizedBitmap;
	}
	
	@Override
	public Uri getUri() {
		return PictureColumns.CONTENT_URI;
	}
	
	public static final class PictureColumns implements BaseColumns {
		
		private PictureColumns(){
		}
		
		public static final Uri CONTENT_URI = Uri.parse("content://"+PictureProvider.AUTHORITY+"/pictures");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.fabula.pictures";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.fabula.pictures";

        public static final String DEFAULT_SORT_ORDER = "created DESC";

        public static final String TITLE = "title";
        
        public static final String URI_PATH = "uri_path";
        
        public static final String FILENAME = "filename";
        
        public static final String DESCRIPTION = "description";
        
        public static final String CREATED_DATE = "created";
        

	}


}
