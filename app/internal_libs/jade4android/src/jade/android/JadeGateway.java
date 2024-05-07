package jade.android;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import jade.core.Profile;
import jade.util.Logger;
import jade.util.leap.Properties;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.GatewayAgent;

/**
 * This class provide a gateway between ANDROID applications and a JADE based multi agent system.
 * It maintains an internal JADE Agent of class <code>GatewayAgent</code> that acts as entry point
 * in the JADE based system. 
 * The activation/termination of this agent (and its underlying split container) are completely managed
 * by the JadeGateway class and the android application developers do not need to care about them.
 *
 * This class provides APIs very similar to the class <code>jade.wrapper.gateway.JadeGateway</code>
 * (refer to Jade documentation); the main difference is that this one uses the <code>jade.core.MicroRuntime</code> 
 * instead of <code>jade.core.Runtime</code> in order to start a container in a split mode that is better suitable 
 * in a mobile environment.  
 * 
 * The following are the main steps in order to use the JadeGateway in your Android Application:
 *	<ol>
 *	<li>	Create your  Jade Agent class extending <code>jade.wrapper.gateway.GatewayAgent</code> </li>
 *	<li>	Add your application specific behaviours to your Jade Agent and override the processCommand method 
 *			with your application specific implementation </li>
 *	<li>	Create an android activity that implements ConnectionListener </li>
 *	<li>	Initialize this  JadeGateway by calling its method <code>connect</code> in the onCreate method 
 *		of your activity passing all required arguments: </li>
 *		<ul> 
 *			<li> <code>agentClassName</code> is the fully qualified name of the class that extends <code>jade.android.GatewayAgent</code> 
 *				and implements the application specific jade agent </li> 
			<li> <code>agentArgs</code> are the args passed to the jade agent when the Jade MicroRuntime starts it. They can be null.</li>
			<li> <code>jadeProfile</code> is the set of jade properties to be passed to the Jade MicroRuntime. </li>
		 			The minimal set shall be passed to this method is:
		 			<ul>
						<li> <code>jade.core.Profile.MAIN_HOST</code> is the hostname or ip address of the Jade Platform Main Container. </li>
						<li> <code>jade.core.Profile.MAIN_PORT</code> is the port of the Jade Platform Main Container. </li>
						<li> <code>jade.imtp.leap.JICP.JICPProtocol.MSISDN_KEY</code> is the unique identifier  of the split Container and Jade agent 
								that are started on Android Emulator. Typically you can use the IMEI code. </li> 
	 				</ul>
	 	</ul>
	 	
 *	<li>	Implement the <code>onConnected(JadeGayeway gateway)</code> method of the ConnectionListener interface in order to store the 
 *			JadeGateway instance returned </li>
 *	<li>	Call the execute method of the JadeGateway instance whenever you shall send a command to the agent
 *			This method will cause the callback of the method <code>processCommand</code> of the application-specific agent.
 *	 		The method <code>execute</code> will return only after the method <code>GatewayAgent.releaseCommand(command)</code> has been called
 * 			by your application-specific agent. </li>
 *	<li>	Call the disconnect method in order to unbind to the service. </li>
 *	</ol>

 * The suggested way of using the JadeGateway class is creating proper behaviours that perform the commands 
 * that the external system must issue to the JADE based system and pass them as parameters to the <code>execute()</code> 
 * method. When the <code>execute()</code> method returns the internal agent of the JadeGateway as completely executed
 * the behaviour and outputs (if any) can be retrieved from the behaviour object using ad hoc methods 
 * as examplified below.
 * 
 * <p>
 <code>
 DoSomeActionBehaviour b = new DoSomeActionBehaviour(....);<br>
 JadeGateway.execute(b);<br>
 // At this point b has been completely executed --> we can get results<br>
 result = b.getResult();<br>
 </code>
 * <br>
 * 
 * 
 * @author Stefano Semeria Reply Cluster
 * @author Tiziana Trucco Telecomitalia
 * @author Marco Ughetti Telecomitalia
 *
 */

public class JadeGateway   {
	
	private static HashMap map = new HashMap();
	private  JadeBinder jadeBinder = null;
	
	//Instance of the Logger
	private static final Logger myLogger = Logger.getMyLogger(JadeGateway.class.getName()); 
	
	//Property variables for JADE

	private static Properties jadeProps;

	//Constants for keys inside the Bundle
	static final String GATEWAY_CLASS_NAME="GATEWAY_CLASS_NAME";
	static final String GATEWAY_AGENT_ARGS="GATEWAY_AGENT_ARGS";
	static final String PROPERTIES_BUNDLE="PROPERTIES_BUNDLE";
	static final String SERVICE_BUNDLE="SERVICE_BUNDLE";
	
	private JadeGateway(JadeBinder cmd){
		jadeBinder = cmd;
	}
	
	/** Searches for the property with the specified key in the JADE Platform Profile. 
	 *	The method returns the default value argument if the property is not found. 
	 * @param key - the property key. 
	 * @param defaultValue - a default value
	 * @return the value with the specified key value
	 * @see java.util.Properties#getProperty(String, String)
	 **/
	public final static String getProfileProperty(String key, String defaultValue) {
		return jadeProps.getProperty(key, defaultValue);
	}
	
	/**
	 * execute a command. 
	 * This method first check if the executor Agent is alive (if not it
	 * creates container and agent), then it forwards the execution
	 * request to the agent, finally it blocks waiting until the command
	 * has been executed (i.e. the method <code>releaseCommand</code> 
	 * is called by the executor agent)
	 * 	<p> 
	 * <b>WARNING: if the main container of jade platform is not reachable (because the main container is not running or 
	 * the connection parameters are wrong) the call to execute shall take a very long time to complete and shall fail in the end, blocking 
	 * the Android application using it. Please always launch the platform BEFORE using execute().</b>
	 * 
	 * @throws StaleProxyException if the method was not able to execute the Command
	 * @throws ControllerException if agent is not reachable
	 * @throws InterruptedException if the timeout expires or the Thread
	 * executing this method is interrupted.
	 * @throws Exception for any other problem reaching the JADE platform
	 * @see jade.wrapper.AgentController#putO2AObject(Object, boolean)
	 **/
	public final void execute(Object command) throws StaleProxyException,ControllerException,InterruptedException, Exception {
		execute(command, 0);
	}
	
	/**
	 * Execute a command specifying a timeout. 
	 * This method first check if the executor Agent is alive (if not it
	 * creates container and agent), then it forwards the execution
	 * request to the agent, finally it blocks waiting until the command
	 * has been executed. In case the command is a behaviour this method blocks 
	 * until the behaviour has been completely executed. 
	 * 	<p> 
	 * <b>WARNING: if the main container of jade platform is not reachable (because the main container is not running or 
	 * the connection parameters are wrong) the call to execute shall take a very long time to complete and shall fail in the end, blocking 
	 * the Android application using it. Please always launch the platform BEFORE using execute().</b>
	 * 
	 * @throws InterruptedException if the timeout expires or the Thread
	 * executing this method is interrupted.
	 * @throws StaleProxyException if the method was not able to execute the Command
	 * @throws ControllerException if agent is not reachable
	 * @throws Exception for any other problem reaching the JADE platform
	 * @see jade.wrapper.AgentController#putO2AObject(Object, boolean)
	 **/
	public final void execute(Object command, long timeout) throws StaleProxyException,ControllerException,InterruptedException, ConnectException, Exception {
	
		if (jadeBinder != null)
			jadeBinder.checkJADE();
		else 
			throw new ConnectException("Not connected to MicroRuntimeService!!");
		
		try {
			jadeBinder.execute(command,timeout);
		} 
		catch (StaleProxyException exc)  
		{
			exc.printStackTrace();
			// in case an exception was thrown, restart JADE
			// and then re-execute the command
			restartAfterFailure(command, timeout);
		}
		catch (ControllerException exc) 
		{
			exc.printStackTrace();
			// in case an exception was thrown, restart JADE
			// and then reexecute the command
			restartAfterFailure(command, timeout);
		}
	}
	
	
	
	/**
	 * Initialize this gateway by passing the proper configuration parameters
	 * It connects to the MicroRuntime service and set the jade properties.
	 * @param agentClassName is the fully-qualified class name of the JadeGateway internal agent. If null is passed
	 * the default class will be used.
	 * @param agentArgs is the list of string agent arguments 
	 * @param jadeProfile the properties that contain all parameters for running JADE (see jade.core.Profile).
	 * Typically these properties will have to be read from a JADE configuration file.
	 * If jadeProfile is null, then a JADE container attaching to a main on the local host is launched
	 * @param ctn the current Application context (usually the activity using this method)
	 * @param list a listener for the connection to the service. Once the connection is completed the listener "onConnected" shall be called 
	 * @throws Exception   if the jade properties are wrong or connection to the MicroRuntimeService is not possible.
	 * 
	 **/
	public final static void connect(String agentClassName, String[] agentArgs, Properties jadeProfile, Context ctn, ConnectionListener list) throws Exception{
		myLogger.log(Logger.FINE,"connect(): called");
	
		String agentType = agentClassName;
		if (agentType == null) {
			agentType = GatewayAgent.class.getName();
		}

		Properties jadeProps = jadeProfile;
		if (jadeProps != null) {
			// Since we will create a non-main container --> force the "main" property to be false
			jadeProps.setProperty(Profile.MAIN, "false");
		}else{
			myLogger.log(Logger.SEVERE,"connect(): jade properties cannot be null.");
			throw new Exception("Jade properties cannot be null.");
		}
		
		MicroRuntimeServiceConnection sConn = new MicroRuntimeServiceConnection(list);
		map.put(ctn, sConn);
		Bundle b = prepareBundle(agentType,agentArgs,jadeProps);
		Intent i = new Intent(ctn, MicroRuntimeService.class);
		i.putExtra(PROPERTIES_BUNDLE, b);
		
		ctn.startService(i);
		ctn.bindService(i, sConn,Context.BIND_AUTO_CREATE);
	
	}
	
	/**
	 * Retrieve the agent name (usually provided together with JADE connection properties)
	 * @return name of the agent
	 */
	
	public String getAgentName() throws ConnectException {
		if (jadeBinder != null){
			return jadeBinder.getAgentName();
		} else {
			throw new ConnectException("Not connected to MicroRuntimeService!!");
		}
	}
	
	
	/**
	 * Initialize this gateway by passing the proper configuration parameters
	 * It connects to the MicroRuntime service and set the jade properties.
	 * @param agentClassName is the fully-qualified class name of the JadeGateway internal agent. If null is passed
	 * the default class will be used.
	 * @param jadeProfile the properties that contain all parameters for running JADE (see jade.core.Profile).
	 * Typically these properties will have to be read from a JADE configuration file.
	 * If jadeProfile is null, then a JADE container attaching to a main on the local host is launched
	 **/
	public final static void connect(String agentClassName, Properties jadeProfile, Context ctn, ConnectionListener list) throws Exception{
		connect(agentClassName, null, jadeProfile, ctn, list);
	}
	
	/**
	 * Shutdown the local container on Android platform.
	 **/
	public final void shutdownJADE() throws ConnectException {
		if (jadeBinder != null)
			jadeBinder.shutdownJADE();
		else 
			throw new ConnectException("Not connected to MicroRuntimeService!!");
		
	}
	
	
	/**
	 * Disconnect from the local MicroRuntimeService, without stopping the JADE agent.
	 * @param ctn the current application context
	 **/
	public final void disconnect(Context ctn) {
		myLogger.log(Logger.FINE, "disconnect(): disconnecting from service");
		ctn.unbindService((ServiceConnection)map.get(ctn));
		jadeBinder = null;
		map.remove(ctn);
	}
	
	
	//utility method
	
	private static Bundle prepareBundle(String agentClassName, String[] agentArgs, Properties jadeProfile){
		Bundle b = new Bundle();
		
		b.putString(GATEWAY_CLASS_NAME, agentClassName);
		
		if (agentArgs != null){
			ArrayList<String> aArgs =  new ArrayList<String>(Arrays.asList(agentArgs));
			b.putSerializable(GATEWAY_AGENT_ARGS, aArgs);
		}
		//since there are some serialization problems, we create an hashmap 
		//to store the properties that be to be put into to bundle 
		HashMap theProps = new HashMap();
		for(Enumeration e = jadeProfile.keys(); e.hasMoreElements();){
			String key = (String)e.nextElement();
			theProps.put(key, jadeProfile.get(key));
		}
		
		b.putSerializable(PROPERTIES_BUNDLE, theProps);
		return b;
	}

	private void restartAfterFailure(Object command, long timeout) throws StaleProxyException, ControllerException, InterruptedException, Exception {
		jadeBinder.shutdownJADE();
		jadeBinder.checkJADE();
		jadeBinder.execute(command,timeout);
	}
	private static class MicroRuntimeServiceConnection implements ServiceConnection {

		private ConnectionListener conListener;

		public MicroRuntimeServiceConnection(ConnectionListener listener) {
			this.conListener = listener;
		}

		public void onServiceConnected(ComponentName className, IBinder service) {
			myLogger.log(Logger.FINE,"onServiceConnected(): called");
			if(conListener != null) {
				conListener.onConnected(new JadeGateway((JadeBinder)service));
			}
		}

		//is called only when the connection to the service fails. 
		//i.e. crash of the service process.
		public void onServiceDisconnected(ComponentName className){
			myLogger.log(Logger.FINE,"onServiceDisconnected(): called");
			if(conListener != null) {
				conListener.onDisconnected();
			}
		}
	}
}
