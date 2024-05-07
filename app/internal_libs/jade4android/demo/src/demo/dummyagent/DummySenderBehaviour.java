package demo.dummyagent;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * This class implements a message sender behaviour that is executed by the
 * agent any time a message is sent.
 *  
 * @author Stefano Semeria  Reply Cluster
 * @author Tiziana Trucco Telecomitalia
 */

public class DummySenderBehaviour extends OneShotBehaviour {

	private ACLMessage theMsg;
	
	public DummySenderBehaviour(ACLMessage msg) {
		theMsg = msg;
	}
	
	public void action() {
		myAgent.send(theMsg);
	}

}
