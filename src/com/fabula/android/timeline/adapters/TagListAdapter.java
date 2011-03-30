package com.fabula.android.timeline.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.fabula.android.timeline.MyGroupsActivity;
import com.fabula.android.timeline.R;
import com.fabula.android.timeline.models.Group;
import com.fabula.android.timeline.models.User;

public class TagListAdapter extends ArrayAdapter<String> {

	
	private Context mContext;
	private List <String> tags, checkedTags;
	private LayoutInflater mInflater;

	public TagListAdapter(Context context, List<String> allTags, List<String> checkedTags) {
		 super(context, 0);
		 this.mContext = context;
		 this.tags = allTags;
		 this.checkedTags = checkedTags;
		 mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 setNotifyOnChange(true);
	 }

	public int getCount() {
		return tags.size();
	}

	public String getItem(int position) {
		return tags.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public List<String> getCheckedTags() {
		return checkedTags;
	}

	public void setCheckedTags(List<String> checkedTags) {
		this.checkedTags = checkedTags;
	}

	public int getAmountOfSelectedTags() {
		return checkedTags.size();
	}
	
	

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
		notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		
		if (convertView == null) {
			 convertView = mInflater.inflate(R.layout.list_tags_view, null);
			 holder = new ViewHolder();
			 holder.text = (TextView) convertView.findViewById(R.id.taglist_text);
			 holder.checkBox = (CheckBox) convertView.findViewById(R.id.tag_list_checkbox);
			 holder.checkBox.setTag(position);
			 
				OnClickListener l= new OnClickListener() {
					
					public void onClick(View v) {
						listClickAction(v);

					}

				};
			 
  			holder.checkBox.setOnClickListener(l);
  			convertView.setOnClickListener(l);
			convertView.setTag(holder);
			 
		 } else {
			 holder = (ViewHolder) convertView.getTag();
			 holder.checkBox.setTag(position);
		 }
			 
			 holder.text.setText(tags.get(position).toString());
//			 holder.checkBox.setChecked(isAlreadyPartOfGroup(users.get(position), group));
			holder.checkBox.setChecked(checkedTags.contains(tags.get(position)));
			
			 return convertView;
	 }
	

	

	
	private void listClickAction(View v) {
		boolean checked = false;
		if(v instanceof RelativeLayout){
			v = ((ViewHolder)v.getTag()).checkBox;
			checked = !((CheckBox)v).isChecked();
		}else{
			checked = ((CheckBox)v).isChecked();
		}
		Integer myPosition = (Integer)v.getTag();
		
		if(checked){
			checkedTags.add(getItem(myPosition));
		}else{
			checkedTags.remove(getItem(myPosition));
		}
		((CheckBox)v).setChecked(checked);
	}
 
	
	
//	private boolean isAlreadyPartOfGroup(User user, Group group) {
//	
//			for (User u : group.getMembers()) {
//				if(user.getUserName().equals(u.getUserName())) {
//					return true;
//				}
//			}
//		return false;
//	}
	
}	 

