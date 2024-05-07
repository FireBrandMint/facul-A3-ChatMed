package jade.android;

import jade.core.MicroRuntime;
import jade.core.NotFoundException;
import jade.imtp.leap.JICP.JICPProtocol;
import jade.util.Event;
import jade.util.Logger;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

/**
 * This class provides an implementation of the Service that is responsible for
 * the management of the Jade Microruntime life cycle
 * When an activity binds to this Service an implementation of JadeBinder is returned.
 * Note that this class SHALL NOT be directly used by the android application developer that
 * SHALL use the JadeGateway instead
 * 
 * 
 * @author Stefano Semeria Reply Cluster
 * @author Tiziana Trucco Telecomitalia
 * @author Marco Ughetti Telecomitalia 
 */
public class MicroRuntimeService extends Service {

	private String myAgentName;	
	private String gatewayClassName;
	private String[] gatewayAgentArgs;
	private Properties jadeProperties;
	
	private final IBinder mBinder = new JadeBinderImpl(); 
	
	//Instance of the Logger
	private static final Logger myLogger = Logger.getMyLogger(MicroRuntimeService.class.getName()); 

	
	public void onCreate() {
		myLogger.log(Logger.INFO, "onCreate(): called");	
	}


	public void onStart(Intent i, int startId) {
		myLogger.log(Logger.INFO, "onStart(): called");
		Bundle arguments = i.getBundleExtra(JadeGateway.PROPERTIES_BUNDLE);
		gatewayClassName = arguments.getString(JadeGateway.GATEWAY_CLASS_NAME);
		ArrayList<String> args = (ArrayList<String>)arguments.getSerializable(JadeGateway.GATEWAY_AGENT_ARGS);
		if (args != null){
			gatewayAgentArgs = new String[args.size()]; 
			args.toArray(gatewayAgentArgs);
			myLogger.log(Logger.INFO, "onStart(): AGENT ARGS: " + gatewayAgentArgs);
		}else
			gatewayAgentArgs = null;

		myLogger.log(Logger.INFO, "onStart(): properties: " + arguments.getSerializable(JadeGateway.PROPERTIES_BUNDLE).getClass().getName());	
		
		Serializable ser = arguments.getSerializable(JadeGateway.PROPERTIES_BUNDLE);
		HashMap props = (HashMap)ser;
		jadeProperties = new jade.util.leap.Properties();

		for(Iterator it = props.keySet().iterator(); it.hasNext(); ){
			String key = (String)it.next();
			jadeProperties.put(key, props.get(key));
		}
	}

	

	public void onDestroy() {
		// stop Jade
		myLogger.log(Logger.INFO, "onDestroy(): called");
		if (MicroRuntime.isRunning()) {
			MicroRuntime.stopJADE();
			myLogger.log(Logger.INFO, "onDestroy(): JADE stopped");
		}
	}


	private class JadeBinderImpl extends Binder implements JadeBinder {
		
		public JadeBinderImpl() {
			myLogger.log(Logger.FINE, "JadeBinder(): constructor");
		}

		public void execute(Object command) throws StaleProxyException,ControllerException,InterruptedException {
			execute(command,0);
		}

		
		public void checkJADE() throws Exception {
			myLogger.log(Logger.INFO, "checkJADE(): starting checkJade");
			if (!MicroRuntime.isRunning()){
				Properties props = (Properties)jadeProperties.clone();
				MicroRuntime.startJADE(props, null);
				myAgentName = (String)props.get(JICPProtocol.MEDIATOR_ID_KEY);
				
				myLogger.log(Logger.INFO, "checkJADE() : Agent name is " + myAgentName);
				MicroRuntime.startAgent(myAgentName, gatewayClassName, gatewayAgentArgs);
			}
		}

		public void execute(Object command, long timeout) throws StaleProxyException, ControllerException, InterruptedException {
			Event e = null;
			//incapsulate the command into an Event
			e = new Event(-1, command);
			AgentController agent = MicroRuntime.getAgent(myAgentName);
			agent.putO2AObject(e, AgentController.ASYNC);
			e.waitUntilProcessed(timeout);
		}

		public void shutdownJADE() {
			try {
				MicroRuntime.killAgent(myAgentName);
			} catch (NotFoundException e) {
				myLogger.log(Logger.SEVERE, "shutdownJADE(): agent not found.", e);
			}
			MicroRuntime.stopJADE();			
		}

		
		public String getAgentName() {
			return myAgentName;
		}
	}


	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
}
