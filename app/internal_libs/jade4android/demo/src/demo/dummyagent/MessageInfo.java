package demo.dummyagent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jade.lang.acl.ACLMessage;


/**
 * This class stores an <code>ACLMessage</code>
 * and it is called by the agent to update the GUI when a message is received
 * 
 * @author Stefano Semeria  Reply Cluster
 * @author Tiziana Trucco Telecomitalia
 */

public class MessageInfo {
	
	private ACLMessage message;
	private String strMsg;

	public MessageInfo(ACLMessage msg){
		message = msg;
		Date timestamp = new Date();
		String pattern = "dd/MM/yy HH.mm.ss :  ";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern,Locale.ITALIAN);
		strMsg = formatter.format(timestamp) +  ACLMessage.getPerformative(message.getPerformative()); 
	}
	
	public final ACLMessage getMessage() {
		return message;
	}
	
	public String toString() {
		
		return strMsg;
	}
}
