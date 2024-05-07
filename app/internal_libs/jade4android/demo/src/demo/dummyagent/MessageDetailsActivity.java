package demo.dummyagent;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Iterator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * This Activity is shown when the user selects a message into the message list in <code>DummyAgentActivity</code>
 * It provides controls to show the message details (Sender, Receiver, Communicative Act and Content) 
 * and provides a Reply button to perform automatic reply.
 * It is launched as a subactivity from <code>DummyAgentActivity</code> and returns the Sender field to its parent.
 * 
 * @author Cristina Cuce'  University of Reggio Calabria 
 * @author Stefano Semeria  Reply Cluster
 * @author Tiziana Trucco Telecomitalia
 * 
 */



public class MessageDetailsActivity extends Activity {

	private EditText sender;
	public static final String SENDER_NAME = "SENDER_NAME";
	
	protected void onCreate(Bundle icicle) {
		
		super.onCreate(icicle);
		setContentView(R.layout.message_details);
		
		Intent it =getIntent();
		String senderName = (String) it.getSerializableExtra(DummyAgentActivity.KEY_INTENT_SENDER);

		sender = (EditText) this.findViewById(R.id.senderDet);
		sender.setText( senderName );
		
		
		ListView receiverList = (ListView) this.findViewById(R.id.receiverListDet);
		ArrayList<String> strList = (ArrayList<String>) it.getSerializableExtra(DummyAgentActivity.KEY_INTENT_RECEIVER_LIST);
		
		IconifiedTextListAdapter ita = new IconifiedTextListAdapter(this);
		
		Drawable icon = getResources().getDrawable(R.drawable.runtree);
		
		for (java.util.Iterator<String> iterator = strList.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			IconifiedText txt = new IconifiedText(string,icon);
			txt.setTextColor(getResources().getColor(R.color.listitem_receivers_text_color));
			ita.addLastItem(txt);
		}
		
		receiverList.setAdapter(ita);
		
		EditText comAct = (EditText) this.findViewById(R.id.comActDet);
		String comActName = (String) it.getSerializableExtra(DummyAgentActivity.KEY_INTENT_COM_ACT);
		comAct.setText(comActName);
		
		
		EditText content = (EditText) this.findViewById(R.id.contentDet);
		String contentName = (String) it.getSerializableExtra(DummyAgentActivity.KEY_INTENT_CONTENT);
		content.setText( contentName );
	
		
	
		
		Button reply = (Button) this.findViewById(R.id.replyBtnDet);
		reply.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				String snd = sender.getText().toString();
				Intent data = new Intent();
				data.putExtra(SENDER_NAME, snd);
				setResult(Activity.RESULT_OK, data);
				finish();
			}
				
		});
	
	}	
	
	
	protected void onDestroy(){
		super.onDestroy();
	}
}
