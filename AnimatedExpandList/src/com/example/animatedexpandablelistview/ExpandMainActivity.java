package com.example.animatedexpandablelistview;

import java.util.ArrayList;
import java.util.List;

import com.idunnololz.widgets.AnimatedExpandableListView;
import com.idunnololz.widgets.AnimatedExpandableListView.AnimatedExpandableListAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

/**
 * This is an example usage of the AnimatedExpandableListView class.
 * 
 * It is an activity that holds a listview which is populated with 100 groups
 * where each group has from 1 to 100 children (so the first group will have one
 * child, the second will have two children and so on...).
 */
public class ExpandMainActivity extends Activity implements SwipeActionListener {
    private AnimatedExpandableListView listView;
    private SwipeActionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expand_activity_main);
        
        List<GroupItem> items = new ArrayList<GroupItem>();
        
        // Populate our list with groups and it's children
        for(int i = 1; i < 100; i++) {
            GroupItem item = new GroupItem();
            
            item.title = "Group " + i;
            
            for(int j = 0; j < i; j++) {
                ChildItem child = new ChildItem();
                child.title = "Awesome item " + j;
                child.hint = "Too awesome";
                
                item.items.add(child);
            }
            
            items.add(item);
        }
        listView = (AnimatedExpandableListView) findViewById(R.id.listView);
        adapter = new SwipeActionAdapter(this);
        adapter.setData(items);
        adapter.addBackground(SwipeDirections.DIRECTION_FAR_LEFT,R.layout.row_bg_left_far)
        .addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT,R.layout.row_bg_left)
        .addBackground(SwipeDirections.DIRECTION_FAR_RIGHT,R.layout.row_bg_right_far)
        .addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT,R.layout.row_bg_right);
        adapter.setSwipeActionListener(this)
        .setListView(listView);
      
        listView.setAdapter(adapter);
        
        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        listView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group 
                // expansion/collapse.
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
            
        });
    }
    
    private static class GroupItem {
        String title;
        List<ChildItem> items = new ArrayList<ChildItem>();
    }
    
    private static class ChildItem {
        String title;
        String hint;
    }
    
    private static class ChildHolder {
        TextView title;
        TextView hint;
    }
    
    private static class GroupHolder {
        TextView title;
    }
    
    /**
     * Adapter for our list of {@link GroupItem}s.
     */
    private class ExampleAdapter extends AnimatedExpandableListAdapter {
        private LayoutInflater inflater;
        
        private List<GroupItem> items;
        
        public ExampleAdapter(Context context) {
             inflater = LayoutInflater.from(context);
        }

        public void setData(List<GroupItem> items) {
            this.items = items;
        }

        @Override
        public ChildItem getChild(int groupPosition, int childPosition) {
            return items.get(groupPosition).items.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder holder;
            ChildItem item = getChild(groupPosition, childPosition);
            if (convertView == null) {
                holder = new ChildHolder();
                convertView = inflater.inflate(R.layout.expand_list_item, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.textTitle);
                holder.hint = (TextView) convertView.findViewById(R.id.textHint);
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }
            
            holder.title.setText(item.title);
            holder.hint.setText(item.hint);
            
            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return items.get(groupPosition).items.size();
        }

        @Override
        public GroupItem getGroup(int groupPosition) {
            return items.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return items.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder holder;
            GroupItem item = getGroup(groupPosition);
            if (convertView == null) {
                holder = new GroupHolder();
                convertView = inflater.inflate(R.layout.expand_group_item, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.textTitle);
                convertView.setTag(holder);
            } else {
                holder = (GroupHolder) convertView.getTag();
            }
            
            holder.title.setText(item.title);
            
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int arg0, int arg1) {
            return true;
        }
        
    }


    
      public class SwipeActionAdapter extends AnimatedExpandableListAdapter implements
    SwipeActionTouchListener.ActionCallbacks
{
private ListView mListView;
private SwipeActionTouchListener mTouchListener;
protected SwipeActionListener mSwipeActionListener;
private boolean mFadeOut = false;
private boolean mFixedBackgrounds = false;
private float mFarSwipeFraction = 0.5f;
private LayoutInflater inflater;

private List<GroupItem> items;
protected SparseIntArray mBackgroundResIds = new SparseIntArray();

public SwipeActionAdapter(Context context){
	 inflater = LayoutInflater.from(context);
}

/*@Override
public View getView(final int position, final View convertView, final ViewGroup parent){
    SwipeViewGroup output = (SwipeViewGroup)convertView;

    if(output == null) {
        output = new SwipeViewGroup(parent.getContext());
        for (int i = 0; i < mBackgroundResIds.size(); i++) {
          output.addBackground(View.inflate(parent.getContext(),mBackgroundResIds.valueAt(i), null),mBackgroundResIds.keyAt(i));
        }
        output.setSwipeTouchListener(mTouchListener);
    }

  //  output.setContentView(super.getView(position,output.getContentView(),output));

    return output;
}*/
public void setData(List<GroupItem> items) {
    this.items = items;
}

@Override
public ChildItem getChild(int groupPosition, int childPosition) {
    return items.get(groupPosition).items.get(childPosition);
}

@Override
public long getChildId(int groupPosition, int childPosition) {
    return childPosition;
}

@Override
public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
    ChildHolder holder;
    ChildItem item = getChild(groupPosition, childPosition);
    if (convertView == null) {
        holder = new ChildHolder();
        convertView = inflater.inflate(R.layout.expand_list_item, parent, false);
        holder.title = (TextView) convertView.findViewById(R.id.textTitle);
        holder.hint = (TextView) convertView.findViewById(R.id.textHint);
        convertView.setTag(holder);
    } else {
        holder = (ChildHolder) convertView.getTag();
    }
    
    holder.title.setText(item.title);
    holder.hint.setText(item.hint);
    
    return convertView;
}

@Override
public int getRealChildrenCount(int groupPosition) {
    return items.get(groupPosition).items.size();
}

@Override
public GroupItem getGroup(int groupPosition) {
    return items.get(groupPosition);
}

@Override
public int getGroupCount() {
    return items.size();
}

@Override
public long getGroupId(int groupPosition) {
    return groupPosition;
}

@SuppressWarnings("unused")
@Override
public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
    GroupHolder holder;
    GroupItem item = getGroup(groupPosition);
   /* if (convertView == null) {
        holder = new GroupHolder();
        convertView = inflater.inflate(R.layout.expand_group_item, parent, false);
        holder.title = (TextView) convertView.findViewById(R.id.textTitle);
        convertView.setTag(holder);
    } else {
        holder = (GroupHolder) convertView.getTag();
    }
    
    holder.title.setText(item.title);*/
	//  convertView = inflater.inflate(R.layout.expand_group_item, parent, false);
   View convertView1 = inflater.inflate(R.layout.expand_group_item, parent, false);
	 SwipeViewGroup output = (SwipeViewGroup)convertView;

	    if(output == null) {
	        output = new SwipeViewGroup(parent.getContext());
	        for (int i = 0; i < mBackgroundResIds.size(); i++) {
	          output.addBackground(View.inflate(parent.getContext(),mBackgroundResIds.valueAt(i), null),mBackgroundResIds.keyAt(i));
	        }
	        output.setSwipeTouchListener(mTouchListener);
	    }
	    TextView title = (TextView) convertView1.findViewById(R.id.textTitle);
	    title.setText(item.title);
	   output.setContentView(convertView1);

	    return output;
    //return convertView;
}

@Override
public boolean hasStableIds() {
    return true;
}

@Override
public boolean isChildSelectable(int arg0, int arg1) {
    return true;
}
/**
 * SwipeActionTouchListener.ActionCallbacks callback
 * We just link it through to our own interface
 *
 * @param position the position of the item that was swiped
 * @return boolean indicating whether the item has actions
 */
@Override
public boolean hasActions(int position){
    return mSwipeActionListener != null && mSwipeActionListener.hasActions(position);
}

/**
 * SwipeActionTouchListener.ActionCallbacks callback
 * We just link it through to our own interface
 *
 * @param listView The originating {@link ListView}.
 * @param position The position to perform the action on, sorted in descending  order
 *                 for convenience.
 * @param direction The type of swipe that triggered the action
 * @return boolean that indicates whether the list item should be dismissed or shown again.
 */
@Override
public boolean onPreAction(ListView listView, int position, int direction){
    return mSwipeActionListener != null && mSwipeActionListener.shouldDismiss(position, direction);
}

/**
 * SwipeActionTouchListener.ActionCallbacks callback
 * We just link it through to our own interface
 *
 * @param listView The originating {@link ListView}.
 * @param position The positions to perform the action on, sorted in descending  order
 *                 for convenience.
 * @param direction The type of swipe that triggered the action.
 */
@Override
public void onAction(ListView listView, int[] position, int[] direction){
    if(mSwipeActionListener != null) mSwipeActionListener.onSwipe(position,direction);
}

/**
 * Set whether items should have a fadeOut animation
 *
 * @param mFadeOut true makes items fade out with a swipe (opacity -> 0)
 * @return A reference to the current instance so that commands can be chained
 */
@SuppressWarnings("unused")
public SwipeActionAdapter setFadeOut(boolean mFadeOut){
    this.mFadeOut = mFadeOut;
    if(mListView != null) mTouchListener.setFadeOut(mFadeOut);
    return this;
}

/**
 * Set whether the backgrounds should be fixed or swipe in from the side
 * The default value for this property is false: backgrounds will swipe in
 *
 * @param mFixedBackgrounds true for fixed backgrounds, false for swipe in
 */
@SuppressWarnings("unused")
public SwipeActionAdapter setFixedBackgrounds(boolean mFixedBackgrounds){
    this.mFixedBackgrounds = mFixedBackgrounds;
    if(mListView != null) mTouchListener.setFixedBackgrounds(mFixedBackgrounds);
    return this;
}

/**
 * Set the fraction of the View Width that needs to be swiped before it is counted as a far swipe
 *
 * @param farSwipeFraction float between 0 and 1
 */
@SuppressWarnings("unused")
public SwipeActionAdapter setFarSwipeFraction(float farSwipeFraction) {
    if(farSwipeFraction < 0 || farSwipeFraction > 1) {
        throw new IllegalArgumentException("Must be a float between 0 and 1");
    }
    mFarSwipeFraction = farSwipeFraction;
    if(mListView != null) mTouchListener.setFarSwipeFraction(mFarSwipeFraction);
    return this;
}

/**
 * We need the ListView to be able to modify it's OnTouchListener
 *
 * @param mListView the ListView to which the adapter will be attached
 * @return A reference to the current instance so that commands can be chained
 */
public SwipeActionAdapter setListView(ListView mListView){
    this.mListView = mListView;
    mTouchListener = new SwipeActionTouchListener(mListView,this);
    this.mListView.setOnTouchListener(mTouchListener);
    this.mListView.setOnScrollListener(mTouchListener.makeScrollListener());
    this.mListView.setClipChildren(false);
    mTouchListener.setFadeOut(mFadeOut);
    mTouchListener.setFixedBackgrounds(mFixedBackgrounds);
    return this;
}

/**
 * Getter that is just here for completeness
 *
 * @return the current ListView
 */
@SuppressWarnings("unused")
public AbsListView getListView(){
    return mListView;
}

/**
 * Add a background image for a certain callback. The key for the background must be one of the
 * directions from the SwipeDirections class.
 *
 * @param key the identifier of the callback for which this resource should be shown
 * @param resId the resource Id of the background to add
 * @return A reference to the current instance so that commands can be chained
 */
public SwipeActionAdapter addBackground(int key, int resId){
    if(SwipeDirections.getAllDirections().contains(key)) mBackgroundResIds.put(key,resId);
    return this;
}

/**
 * Set the listener for swipe events
 *
 * @param mSwipeActionListener class listening to swipe events
 * @return A reference to the current instance so that commands can be chained
 */
public SwipeActionAdapter setSwipeActionListener(SwipeActionListener mSwipeActionListener){
    this.mSwipeActionListener = mSwipeActionListener;
    return this;
}


}

      @Override
      public boolean hasActions(int position){
          return true;
      }

      @Override
      public boolean shouldDismiss(int position, int direction){
          return direction == SwipeDirections.DIRECTION_NORMAL_LEFT;
      }

      @Override
      public void onSwipe(int[] positionList, int[] directionList){
          for(int i=0;i<positionList.length;i++) {
              int direction = directionList[i];
              int position = positionList[i];
              String dir = "";

              switch (direction) {
                  case SwipeDirections.DIRECTION_FAR_LEFT:
                      dir = "Far left";
                      break;
                  case SwipeDirections.DIRECTION_NORMAL_LEFT:
                      dir = "Left";
                      break;
                  case SwipeDirections.DIRECTION_FAR_RIGHT:
                      dir = "Far right";
                      break;
                  case SwipeDirections.DIRECTION_NORMAL_RIGHT:
                   //   AlertDialog.Builder builder = new AlertDialog.Builder(this);
                  //    builder.setTitle("Test Dialog").setMessage("You swiped right").create().show();
                      dir = "Right";
                      break;
              }
             
              adapter.notifyDataSetChanged();
          }
      }
    
    
  /*  *//**
     * Interface that listeners of swipe events should implement
     *//*
    public interface SwipeActionListener{
        public boolean hasActions(int position);
        public boolean shouldDismiss(int position, int direction);
        public void onSwipe(int[] position, int[] direction);
    }  
    
    
    */
    
    
}
