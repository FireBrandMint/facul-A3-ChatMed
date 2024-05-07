package demo.dummyagent;


import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.wrapper.gateway.GatewayAgent;


/**
 * This class implements a jade Agent able to process Command in the shape of Jade
 * Behaviours with the mechanism inherited by <code>jade.wrapper.gateway.GatewayAgent</code>
 * A cyclic behaviour is provided in order to manage ACLMessage from other agents. 
 * Moreover it receives with the same mechanism an object called updater that implements the
 * <code>ACLMessageListener</code> interface and it calls its callback method <code>onMessageReceived</code>
 * when a message is received
 * 
 * @author Stefano Semeria Reply Cluster
 * @author Tiziana Trucco Telecomitalia
 *
 */
public class DummyAgent extends GatewayAgent {

	private final Logger myLogger = Logger.getMyLogger(this.getClass().getName());
	private ACLMessageListener updater;
	
	protected void setup() {
		super.setup();
		addBehaviour(new MessageReceiverBehaviour());
	}
	
	protected void processCommand(final Object command) {
		if (command instanceof Behaviour) {
			SequentialBehaviour sb = new SequentialBehaviour(this);
			sb.addSubBehaviour((Behaviour) command);
			sb.addSubBehaviour(new OneShotBehaviour(this) {
				public void action() {
					DummyAgent.this.releaseCommand(command);
				}
			});
			addBehaviour(sb);
		} else if (command instanceof ACLMessageListener) {
			myLogger.log(Logger.INFO, "processCommand(): New GUI updater received and registered!");
			
			ACLMessageListener listener =(ACLMessageListener) command;
			
			updater = listener;
			releaseCommand(command);
		}
		
		else {
			myLogger.log(Logger.WARNING, "processCommand().Unknown command "+command);
		}
	}

	
	private class MessageReceiverBehaviour extends CyclicBehaviour{

		public void action() {
			ACLMessage msg = myAgent.receive();
			myLogger.log(Logger.INFO, "MessageReceiverBehaviour().Message received: " + this.hashCode());
			
			//if a message is available and a listener is available
			if (msg != null && updater != null){
				//callback the interface update function
				updater.onMessageReceived(msg);				
			} else {
				block();
			}
		}
	
	}

}
