package demo.dummyagent;

import jade.android.ConnectionListener;
import jade.android.JadeGateway;
import jade.core.AID;
import jade.core.Profile;
import jade.imtp.leap.JICP.JICPProtocol;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.util.leap.Properties;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TabHost.TabSpec;

/**
 * This class implements the Activity associated with the DummyAgent application
 * This activity starts a Jade MicroRuntime with a GatewayAgent in it that replicates 
 * the capabilities of the <code>jade.tools.DummyAgent</code>
 * This class replicates on ANDROID SDK the capabilities of the GUI of DummyAgent
 * It provides support to send message to other jade agent running on the same Jade 
 * platform and show the received ones.
 * A notify implemented with the Tast mechanism is sent to the View when a new message 
 * has arrived     
 * 
 * @author Stefano Semeria  Reply Cluster
 * @author Tiziana Trucco Telecomitalia
 * @author Marco Ughetti Telecomitalia
 */

public class DummyAgentActivity extends Activity implements ConnectionListener{


	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());
	public static final String KEY_RECEIVER = "receiver";
	public static final String KEY_CONTENT = "content";
	
	//Keys for parameters to Message details activity
	public static final String KEY_INTENT_SENDER="key_sender";
	public static final String KEY_INTENT_RECEIVER_LIST="key_receiver_list";
	public static final String KEY_INTENT_COM_ACT="key_commAct";
	public static final String KEY_INTENT_CONTENT="key_content";
	
	
	
	//Subactivities result code
	private final int RESCODE_MSG_DETAILS =1;
	
	//Duration of error notification
	private final int ERROR_MSG_DURATION=1000;
	
	//keys to index the tabs
	private final String SEND_MSG_TAB_TAG="SendMsg";
	private final String RECV_MSG_TAB_TAG="RecvMsg";
	
	//Menu entries
	private final int CONTEXT_MENU_ITEM_ADD=Menu.FIRST+1;
	private final int CONTEXT_MENU_ITEM_EDIT=Menu.FIRST+2;
	private final int CONTEXT_MENU_ITEM_REMOVE=Menu.FIRST+3;
	private final int CONTEXT_MENU_ITEM_REMOVEALL=Menu.FIRST+4;
	
	//notification ID
	private final int STATUSBAR_NOTIFICATION= R.layout.send_message;
	
	//Codes for menu items
	private final int JADE_EXIT_ID = Menu.FIRST;

	private EditText contentText, senderText;
	private Spinner spn;
	private ListView lv;
	private ListView recvListView;
	private JadeGateway gateway;
    private Button sendButton;
    private Button clearButton;
	private NotificationManager nManager; 
	private TabHost tabHost;
	
	private GUIUpdater updater;
	
	private LinkedList<MessageInfo> messageList;
	private ArrayList<AID> receivers;
	private IconifiedTextListAdapter listAdapter;
	private AIDDialog addrDialog;
	

	protected void onCreate(Bundle icicle) {
	
		super.onCreate(icicle);
		myLogger.log(Logger.INFO, "SendMessageActivity.onCreate() : starting onCreate method");
		
		//Set the xml layout from resource
		setContentView(R.layout.send_message);
		
		//PREPARE TAB HOST
		tabHost = (TabHost) findViewById(R.id.tabs);
		tabHost.setup();
		//Send Message Tab
		TabSpec sendMsgSpecs = tabHost.newTabSpec(SEND_MSG_TAB_TAG);
		sendMsgSpecs.setIndicator(getText(R.string.send_msg_tab_indicator),getResources().getDrawable(R.drawable.msg));
		sendMsgSpecs.setContent(R.id.content1);
		tabHost.addTab(sendMsgSpecs);
		//MessageList Tab
		TabSpec recvMsgSpecs = tabHost.newTabSpec(RECV_MSG_TAB_TAG);
		recvMsgSpecs.setIndicator(getText(R.string.recv_msg_tab_indicator),getResources().getDrawable(R.drawable.contact_32));
		recvMsgSpecs.setContent(R.id.content2);
		tabHost.addTab(recvMsgSpecs);
		//Select default tab
		tabHost.setCurrentTab(0);
		
		//PREPARE THE CONTEXT MENU IN RECEIVER LIST
		recvListView = (ListView) findViewById(R.id.receiverList);
		//register this view for context menu
		registerForContextMenu(recvListView);
		
		//Draw the receiver list. 
		receivers = new ArrayList<AID>();
		updateReceiverList();
		
	
		//PREPARE THE NOTIFICTION MANAGER for Connection notification
		nManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		//PREPARE THE MESSAGE LIST
		messageList = new LinkedList<MessageInfo>();
		
		
		//SAVE GUI COMPONENTS TO BE USED
		senderText = (EditText) findViewById(R.id.sender);
		//receiverText = (EditText) findViewById(R.id.receiver);
		contentText = (EditText) findViewById(R.id.content);
		spn = (Spinner) findViewById(R.id.commAct);
		lv = (ListView) findViewById(R.id.messageList);
		
		
		//SPINNER: fill with data
		String[] performatives= ACLMessage.getAllPerformativeNames();
		ArrayAdapter<CharSequence> comActList = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item, performatives);
		comActList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spn.setAdapter(comActList);
		
		//MESSAGE LISTVIEW: handle click event
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			public void onItemClick(AdapterView parent, View v, int position, long id) {
				forwarding(MessageDetailsActivity.class, position);
			}
		});
		
		
		//SEND BUTTON: retrieve and handle click event 
		sendButton = (Button) findViewById(R.id.sendBtn);
		sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	myLogger.log(Logger.INFO,"onClick(): content: "+contentText.getText().toString());
            	if (sendButton.isEnabled())
            		sendMessage(contentText.getText().toString(), (String)spn.getSelectedItem() );
       
            }
        });
		
		
		
		//CLEAR BUTTON: retrieve and handle click event (Initially disabled)
		clearButton = (Button) findViewById(R.id.clearbtn);		
		clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {      	
            	if (clearButton.isEnabled()){
            		messageList.clear();           	
            	    listAdapter.clear();
            	    lv.setAdapter(listAdapter);
            	 }
            }
        });
		
        //CREATE THE AID DIALOG
		addrDialog = new AIDDialog(this);
        //CREATE MESSAGE LIST ADAPTER
        listAdapter = new IconifiedTextListAdapter(this);
      
        //CREATE THE UI UPDATER 
		updater = new GUIUpdater(this);
		
		//CREATE AND THE JADE PROPERTIES CLASS
		Properties props = new Properties();
		props.setProperty(Profile.MAIN_HOST, getResources().getString(R.string.host));
		props.setProperty(Profile.MAIN_PORT, getResources().getString(R.string.port));
		props.setProperty(JICPProtocol.MSISDN_KEY, getResources().getString(R.string.msisdn));
	
		
		try {
			JadeGateway.connect(DummyAgent.class.getName(), null, props, this, this);
		} catch (Exception e) {
			myLogger.log(Logger.SEVERE,e.getMessage(),e);
			Toast.makeText(this, e.getMessage(), 5000);
		}	
			
	}
	
	
	
	private void forwarding(Class nextActivity, int position) {
        Intent intent = new Intent();
        intent.setClass(this, nextActivity);
        ACLMessage msg =  messageList.get(position).getMessage();
        
        intent.putExtra(KEY_INTENT_SENDER, msg.getSender().getName());
        intent.putExtra(KEY_INTENT_COM_ACT, ACLMessage.getPerformative(msg.getPerformative()));
        intent.putExtra(KEY_INTENT_CONTENT, msg.getContent());

        jade.util.leap.Iterator it = msg.getAllReceiver();
        
        ArrayList<String> recList = new ArrayList<String>();
        
        while (it.hasNext()){
        	recList.add( ((AID)it.next()).getName() );
        }
        
        intent.putExtra(DummyAgentActivity.KEY_INTENT_RECEIVER_LIST, recList);
        startActivityForResult(intent,RESCODE_MSG_DETAILS);
	}
	
	
	private void sendMessage(String content, String comAct) {				
		
		ACLMessage msg = new ACLMessage(ACLMessage.getInteger(comAct));		
		msg.setContent(content);
		
		for (Iterator<AID> iterator = receivers.iterator(); iterator.hasNext();) {
			AID agAid = iterator.next();
			msg.addReceiver(agAid);
		}
		
		
		DummySenderBehaviour dsb = new DummySenderBehaviour(msg);

		try {
			gateway.execute(dsb);

			MessageInfo info = new MessageInfo(msg);
			addFirstMessage(info);

			IconifiedText IT = new IconifiedText (info.toString(), getResources().getDrawable(R.drawable.upmod));
			IT.setTextColor(getResources().getColor(R.color.listitem_sent_msg));
			listAdapter.addFirstItem(IT);
			lv.setAdapter(listAdapter);
		}
		
		catch (Exception e) {
			Log.e("jade.android.demo",e.getMessage(),e);
			Toast.makeText(this,e.getMessage(), ERROR_MSG_DURATION);
		}
	}

	
	public void onConnected(JadeGateway gw) {

		gateway = gw;		
		sendButton.setEnabled(true);
		try{
			gateway.execute(updater);

			AID id = new AID();
	        CharSequence localName = getResources().getText(R.string.msisdn);
	        id.setLocalName(localName.toString());
	        senderText.setText(id.getName());
			
			Notification notification = new Notification(R.drawable.dummyagent,getResources().getText(R.string.statusbar_msg_connected),System.currentTimeMillis());
			PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(Intent.ACTION_DEFAULT), PendingIntent.FLAG_ONE_SHOT);
			notification.setLatestEventInfo(this, "Connected to Jade Service", "", pi);
			
			nManager.notify(STATUSBAR_NOTIFICATION, notification);
		}catch(ConnectException ce){
			Log.e("jade.android", ce.getMessage(), ce);
			Toast.makeText(this,ce.getMessage(), ERROR_MSG_DURATION);
		}catch(Exception e1){
			Log.e("jade.android", e1.getMessage(), e1);
			Toast.makeText(this,e1.getMessage(), ERROR_MSG_DURATION);
		}
	}

	public void onDisconnected() {
		Log.v("jade.android.demo","OnDisconnected has been called!!!!");
		
	}	
		
	public void addFirstMessage(MessageInfo msg) { 
		messageList.addFirst(msg);
	}
	
	public final LinkedList<MessageInfo> getMessageList() {
		return messageList;
	}
	
	public  IconifiedTextListAdapter getListAdapter() {
		return listAdapter;
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, JADE_EXIT_ID, Menu.NONE, R.string.menu_item_exit);
		return true;
	}
	
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		
		switch(item.getItemId()) {
			case JADE_EXIT_ID:
				finish();
				break;
		}
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {         
		
		if (resultCode == Activity.RESULT_OK) {             
			 tabHost.setCurrentTab(0);
			 receivers.clear();
			 String senderName = data.getStringExtra(MessageDetailsActivity.SENDER_NAME);
			 addReceiver(new AID(senderName,AID.ISGUID));
			 contentText.setText("");
			 updateReceiverList();
		 }
	}

	protected void onDestroy() {
		super.onDestroy();
		addrDialog.dismiss();
		Log.v("jade.android.demo","SendMessageActivity.onDestroy() : calling onDestroy method");
		nManager.cancel(STATUSBAR_NOTIFICATION);
		try {
			if (gateway != null)
				gateway.shutdownJADE();
				
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), ERROR_MSG_DURATION);
		}
			if (gateway != null )
				gateway.disconnect(this);
	}
	

	
	protected void onResume() {
		super.onResume();
		myLogger.log(Logger.INFO,"onResume() : called");
	}
	
	protected void onPause() {
		super.onPause();
		myLogger.log(Logger.INFO,"onPause() : called");
	}
	
	protected void onStop() {
		super.onStop();
		myLogger.log(Logger.INFO,"onStop() : called");
	}
	
	protected void onRestart() {
		super.onRestart();
		myLogger.log(Logger.INFO,"onRestart() : called");
	}

	protected void onStart() {
		super.onStart();
		myLogger.log(Logger.INFO,"onStart() : called");
	}
			
	
	
	public void addReceiver(AID receiver){
		receivers.add(receiver);
	}
	
	public void removeReceiver(int recId){
		receivers.remove(recId);
	}
	
	
	public void editReceiver(int oldRecId, AID newAid){
		receivers.set(oldRecId, newAid);
	}
	
	public final AID getReceiver(int recv){
		return receivers.get(recv);
	}
	
	private List<String> receiverListToString(){
		
		List<String> tmpList = new ArrayList<String>();
		
		//FIXME: Find a way to avoid this check!! (I must add an empty entry in the list, otherwise no cntext menu!!!!)
		if (receivers.size() == 0){
			tmpList.add("");
		} else {
			for (Iterator<AID> iterator = receivers.iterator(); iterator.hasNext();) {
				AID recv =  iterator.next();
				tmpList.add(recv.getName());
			}
		}
		
		return tmpList;
	}
		
	public void updateReceiverList(){
		List<String> strList = receiverListToString();
		IconifiedTextListAdapter adapter = new IconifiedTextListAdapter(this);
		
		Drawable d = getResources().getDrawable(R.drawable.runtree);
		
		for (Iterator iterator = strList.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			IconifiedText txt=null;
			if (string.equals("")){
				txt = new IconifiedText(string,null);
			} else {
				txt = new IconifiedText(string,d);
				txt.setTextColor(getResources().getColor(R.color.listitem_receivers_text_color));
			}	
			adapter.addLastItem(txt);
		}
		
		recvListView.setAdapter(adapter);
		recvListView.invalidate();
	}
	
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		ListView myLv = (ListView) v;
		AdapterContextMenuInfo adInfo = (AdapterContextMenuInfo) menuInfo;
		myLv.setSelection(adInfo.position);
		
		menu.add(Menu.NONE, CONTEXT_MENU_ITEM_ADD, Menu.NONE, R.string.menu_item_add);
		menu.add(Menu.NONE, CONTEXT_MENU_ITEM_EDIT,Menu.NONE, R.string.menu_item_edit);
		menu.add(Menu.NONE, CONTEXT_MENU_ITEM_REMOVE,Menu.NONE, R.string.menu_item_remove);
		menu.add(Menu.NONE, CONTEXT_MENU_ITEM_REMOVEALL,Menu.NONE, R.string.menu_item_removeall);
	}

	public boolean onContextItemSelected(MenuItem item) {
	
		AdapterContextMenuInfo menuInfo = null;
		
		switch(item.getItemId()) {
		
			case CONTEXT_MENU_ITEM_ADD:
				addrDialog.clear();
				addrDialog.showAdd();
			break;
			
			case CONTEXT_MENU_ITEM_EDIT:
				menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
				addrDialog.showEdit(menuInfo.position);
			break;
			
			case CONTEXT_MENU_ITEM_REMOVE:
				menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
				removeReceiver(menuInfo.position);
				updateReceiverList();
			break;
			
			case CONTEXT_MENU_ITEM_REMOVEALL:
				receivers.clear();
				updateReceiverList();
			break;	
		}
		
	return false;
}
	
}
