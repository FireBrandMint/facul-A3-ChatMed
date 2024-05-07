package demo.dummyagent;

import jade.android.JadeGateway;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import android.os.Handler;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This class implements the ACLMessageListener interface
 * and it is called by the agent to update the GUI when a message is received
 * 
 * @author Stefano Semeria  Reply Cluster
 * @author Tiziana Trucco Telecomitalia
 */

class GUIUpdater implements ACLMessageListener {

	private Handler handl;
	private DummyAgentActivity act;	
	private static final Logger myLogger = Logger.getMyLogger(JadeGateway.class.getName());
	
	public GUIUpdater(DummyAgentActivity baseActivity) {
		
		handl = new Handler();
		act = baseActivity;
		
	}
	
	public void onMessageReceived(ACLMessage msg) {
		// TODO Auto-generated method stub
		myLogger.log(Logger.INFO, "onMessageReceived(): GuiUpdater has received message");
		Updater up = new Updater(act,msg);
		handl.post(up);
	}

	private class Updater implements Runnable {
		
		private DummyAgentActivity sendMsgAct;
		private ACLMessage message;
		
		
		public Updater(DummyAgentActivity sm, ACLMessage msg) { 
			sendMsgAct = sm;
			message = msg;
			
		}
		
		public void run() {
		
			MessageInfo mi = new MessageInfo(message);
			
			sendMsgAct.addFirstMessage(mi);
			IconifiedTextListAdapter adapter = sendMsgAct.getListAdapter();
			IconifiedText txt = new IconifiedText(mi.toString(),sendMsgAct.getResources().getDrawable(R.drawable.downmod));
			txt.setTextColor(sendMsgAct.getResources().getColor(R.color.listitem_received_msg));
			adapter.addFirstItem(txt);
			
			ListView lv = (ListView) sendMsgAct.findViewById(R.id.messageList);
			lv.setAdapter(adapter);
			Toast t = Toast.makeText(sendMsgAct, sendMsgAct.getResources().getText(R.string.notify_msg_received), 2000);
			t.show();
		    
		}
	}
}
	
