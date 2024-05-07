package demo.dummyagent;
import android.graphics.drawable.Drawable; 

/**
 * This class stores an Icon (as a Drawable) and the Text to be shown in a 
 * ListView entry and it is called by the agent to update the GUI when a message is received
 * 
 * @author Stefano Semeria  Reply Cluster
 * @author Tiziana Trucco Telecomitalia
 */

public class IconifiedText implements Comparable<IconifiedText>{ 
    
     private String mText = ""; 
     private Drawable mIcon; 
     private int mTextColor;
	private int mTextSize;
     
     public IconifiedText(String text, Drawable bullet, int size) { 
          mIcon = bullet; 
          mText = text; 
          mTextSize = size;
     } 
      
     public IconifiedText(String text, Drawable bullet){
    	 this(text,bullet,15);
     }
     
     public String getText() { 
          return mText; 
     } 
      
     public void setText(String text) { 
          mText = text; 
     } 
      
     public void setIcon(Drawable icon) { 
          mIcon = icon; 
     } 
      
     public Drawable getIcon() { 
          return mIcon; 
     } 

     /** Make IconifiedText comparable by its name */ 

     public int compareTo(IconifiedText other) { 
          if(this.mText != null) 
               return this.mText.compareTo(other.getText()); 
          else 
               throw new IllegalArgumentException(); 
     }

	public int getTextColor() {
		return mTextColor;
	}
	
	
	public void setTextSize(int size) {
		mTextSize = size;
	}
	
	public int getTextSize(int size) {
		return mTextSize;
	}
	
	
	public boolean hasIcon(){
		return (mIcon != null);
	}
	
	public void setTextColor(int color) {
		mTextColor = color;
	} 

} 

