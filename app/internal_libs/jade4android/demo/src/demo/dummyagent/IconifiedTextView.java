package demo.dummyagent;

import android.content.Context; 
import android.graphics.drawable.Drawable; 
import android.util.TypedValue;
import android.widget.ImageView; 
import android.widget.LinearLayout; 
import android.widget.TextView; 

/**
 * This class rapresents a single item in a iconified ListView
 * 
 * @author Stefano Semeria  Reply Cluster
 * @author Tiziana Trucco Telecomitalia
 */


public class IconifiedTextView extends LinearLayout { 
      
     private TextView mText; 
     private ImageView mIcon; 
     
     public IconifiedTextView(Context context, IconifiedText aIconifiedText) { 
          super(context); 

         
          this.setOrientation(HORIZONTAL); 

          
          if (aIconifiedText.hasIcon()) {
        	  mIcon = new ImageView(context); 
        	  mIcon.setImageDrawable(aIconifiedText.getIcon()); 
        	  // left, top, right, bottom 
        	  mIcon.setPadding(0, 2, 3, 0); // 5px to the right 
         
        	  addView(mIcon,  new LinearLayout.LayoutParams( 
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 
      
          }
          
          mText = new TextView(context); 
          mText.setText(aIconifiedText.getText());
          mText.setTextColor(aIconifiedText.getTextColor());
          mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mText.getTextSize());
          
          addView(mText, new LinearLayout.LayoutParams( 
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 
     } 

     
     public void setText(String words) { 
          mText.setText(words); 
     } 

     public void setTextColor(int color) {
    	 mText.setTextColor(color);
     }
    	 
     public void setIcon(Drawable bullet) { 
    	 if (mIcon != null)
    		 mIcon.setImageDrawable(bullet); 
     } 
}