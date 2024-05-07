package demo.dummyagent;


import jade.core.AID;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * This class extends <code>Dialog</code>
 * and shows the AID dialog the user
 * 
 * @author Stefano Semeria  Reply Cluster
 * @author Tiziana Trucco Telecomitalia
 */


public class AIDDialog extends Dialog {

	private EditText agentNameEdt;
	private CheckBox longNameFormat;
	private EditText addressEdt;
	private int selectedPosition;
	private boolean editing;
	private DummyAgentActivity myActivity;

	
	public AIDDialog(DummyAgentActivity ctn) {
		super(ctn);
		
		myActivity = ctn;
		
		setCancelable(true);
		setTitle(R.string.dialog_title);
		setContentView(R.layout.dialog_message);
		
		selectedPosition = -1;
		editing = false;
		
		agentNameEdt = (EditText) findViewById(R.id.agentNameEdit);
		longNameFormat =  (CheckBox) findViewById(R.id.checkFullName);
		addressEdt = (EditText) findViewById(R.id.addressEdit);
		
		
		Button okBtn = (Button) findViewById(R.id.okBtn);	
		//Handle ok button pressed
		okBtn.setOnClickListener(new View.OnClickListener(){
	
			public void onClick(View arg0) {
				Log.v("MSG2", "Thread: " + Thread.currentThread().getId());
				
				String agentTxtName = agentNameEdt.getText().toString(); 
				String address = addressEdt.getText().toString();
				
				
				if (agentTxtName.length() > 0)
				{
					AID agentAID = new AID(agentTxtName, !longNameFormat.isChecked());
					
					if (address.length() > 0) {
						agentAID.addAddresses(address);
					}
					
					if (!AIDDialog.this.editing)
						myActivity.addReceiver(agentAID);
					else 
						myActivity.editReceiver(selectedPosition, agentAID);
					
					myActivity.updateReceiverList();
					cancel();				
				} else {
					//agent name is empty
					AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(myActivity);
					dlgBuilder.setCancelable(false)
							  .setIcon(android.R.drawable.ic_dialog_alert)
							  .setMessage(myActivity.getText(R.string.error_msg_empty_aid))
							  .setTitle("Error")
							  .setPositiveButton("Ok", new OnClickListener(){

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									
								}
								  
							  });
					
					dlgBuilder.show();				
				}
				
			}
			
		});
		
		
		Button cancelBtn = (Button) findViewById(R.id.cancelBtn);	
		//Handle ok button pressed
		cancelBtn.setOnClickListener(new View.OnClickListener(){

			public void onClick(View arg0) {
				cancel();
			}
			
		});
	}
	
	public void showEdit(int selectedPos){
		editing = true;
		selectedPosition = selectedPos;
		
		AID agentAID = myActivity.getReceiver(selectedPos);
		
		String[] addresses = agentAID.getAddressesArray();
		
		if (addresses.length > 0)
			this.addressEdt.setText(addresses[0]);
		
		this.agentNameEdt.setText(agentAID.getName());
		this.longNameFormat.setChecked(false);
		
		show();
	}
	
	public void showAdd(){
		editing = false;
		show();
	}
	
	public void clear(){
		agentNameEdt.setText("");
		longNameFormat.setText("");
		addressEdt.setText("");
		longNameFormat.setChecked(false);
	}
}
