package demo.dummyagent;

import jade.lang.acl.ACLMessage;

public interface ACLMessageListener {

	public void onMessageReceived(ACLMessage msg); 
	
}
