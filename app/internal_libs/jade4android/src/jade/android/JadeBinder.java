package jade.android;

import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
/**
 * This interface is the one to be implemented by the Binder returned by 
 * the MicroRuntimeService after a binding
 *  
 * @author Tiziana Trucco TelecomItalia
 * @author Stefano Semeria Reply Cluster
 *
 */
public interface JadeBinder {
	public void execute(Object command) throws StaleProxyException,ControllerException, InterruptedException;
	public void execute(Object command, long timeout) throws StaleProxyException,ControllerException, InterruptedException;
	public void checkJADE() throws StaleProxyException,ControllerException,Exception;
	public void shutdownJADE();
	public String getAgentName();
	
}
