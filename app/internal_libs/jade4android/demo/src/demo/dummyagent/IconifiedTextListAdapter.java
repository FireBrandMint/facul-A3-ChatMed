package demo.dummyagent;

import java.util.LinkedList;

import android.content.Context; 
import android.view.View; 
import android.view.ViewGroup; 
import android.widget.BaseAdapter; 

/**
 * This class provides an Adapter used to store IconifiedText objects and to
 * display iconified items in a list
 * 
 * @author Stefano Semeria  Reply Cluster
 * @author Tiziana Trucco Telecomitalia
 */


public class IconifiedTextListAdapter extends BaseAdapter { 

     /** Remember our context so we can use it when constructing views. */ 
     private Context mContext; 

     private LinkedList<IconifiedText> mItems = new LinkedList<IconifiedText>(); 

     public IconifiedTextListAdapter(Context context) { 
          mContext = context; 
     } 

     public void addFirstItem(IconifiedText it) { mItems.addFirst(it); } 
     public void addLastItem(IconifiedText it) { mItems.addLast(it); }
     
     public void setListItems(LinkedList<IconifiedText> lit) { mItems = lit; } 

     /** @return The number of items in the */ 
     public int getCount() { return mItems.size(); } 

     public Object getItem(int position) { return mItems.get(position);} 


     /** Use the array index as a unique id. */ 
     public long getItemId(int position) { 
          return position; 
     } 

     public void clear(){
    	 mItems.clear();
     }
     
     /** @param convertView The old view to overwrite, if one is passed 
      * @return a IconifiedTextView that holds wraps around an IconifiedText */ 
     public View getView(int position, View convertView, ViewGroup parent) { 
          IconifiedTextView btv; 
          if (convertView == null) { 
               btv = new IconifiedTextView(mContext, mItems.get(position)); 
          } else { // Reuse/Overwrite the View passed 
               // We are assuming(!) that it is castable! 
               btv = (IconifiedTextView) convertView; 
               btv.setText(mItems.get(position).getText());
               btv.setTextColor(mItems.get(position).getTextColor());
               btv.setIcon(mItems.get(position).getIcon()); 
          } 
          return btv; 
     } 
}