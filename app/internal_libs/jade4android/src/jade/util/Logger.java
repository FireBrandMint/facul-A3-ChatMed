package jade.util;

import java.util.Hashtable;

import jade.util.leap.Properties;
import jade.util.leap.Serializable;
import android.util.Log;


/**
 * This class provides an implementation of the JADE logging API 
 * to print out messages to a log file on the Android emulator.
 * 
 * According to java logging philosophy, the JADE logging API allows to set several logging levels.
 * The levels in descending order are: <p>
 * SEVERE (highest value) <br>
 * WARNING <br>
 * INFO <br>
 * CONFIG <br>
 * FINE <br>
 * FINER <br>
 * FINEST (lowest value)
 * 
 * Since the Android logging API defines different logging levels, the following 
 * mapping rules have been followed:
 * <p> SEVERE corresponds to <code>android.util.Log.ERROR</code><br>
 * <p> WARNING corresponds to <code>android.util.Log.WARN</code><br>
 * <p> INFO corresponds to <code>android.util.Log.INFO</code><br>
 * <p> CONFIG corresponds to <code>android.util.Log.VERBOSE</code><br>
 * <p> FINE, FINER and FINEST correspond to <code>android.util.Log.DEBUG</code><br>
 * 
 * <p> In addition, there is a level OFF that can be used to turn off logging, 
 * and a level ALL that can be used to enable logging of all messages.
 * <p>
 * <p>
 * For instance, in order to log the warning message  "Attention!", the
 * following code should be used: <br><br>
 *
 * <code>Logger logger = Logger.getMyLogger(this.getClass().getName());</code><br>
 * <code>if (logger.isLoggable(logger.WARNING)) </code>
 * <br> <code>logger.log(Logger.WARNING,"Attention!"); </code>
 * <p>
 * Notice that the test <code>isLoggable</code> allows just to improve performance, but
 * it has no side-effect.
 *
 * Following the Android logging style, the Logger class name is used as TAG and  the name 
 * of the Logger is added to the printed message.
 * 
 * @author Anna Maria Porcino - University of Reggio Calabria
 * @author Tiziana Trucco - Telecom Italia
 * 
 */

public class Logger implements Serializable{

	/**
	 * SEVERE is a message level indicating a serious failure.
	 */
	public static final int SEVERE	=	10;
	/**
	 * WARNING is a message level indicating a potential problem.
	 */
	public static final int WARNING	=	9;
	/**
	 * INFO is a message level for informational messages
	 */
	public static final int INFO	=	8;
	/**
	 * CONFIG is a message level for static configuration messages.
	 */
	public static final int CONFIG	=	7;
	/**
	 * FINE is a message level providing tracing information.
	 */
	public static final int FINE	=	5;
	/**
	 * FINER indicates a fairly detailed tracing message.
	 */
	public static final int FINER	=	4;
	/**
	 * FINEST indicates a highly detailed tracing message
	 */
	public static final int FINEST	=	3;
	/**
	 * ALL indicates that all messages should be logged. 
	 */
	public static final int ALL		=	-2147483648;
	/**
	 * Special level to be used to turn off logging
	 */
	public static final int OFF		=	2147483647;
	
	private static Hashtable logManager = new Hashtable();
	private String name;
	
	
	/**
	 * Private method to construct a logger for a named subsystem.
	 * @param name A name for the logger
	 */
	private Logger(String name){
		this.name = name;
	}

	public static void println(String log) {
		Log.println(Log.ASSERT, Logger.class.getName(), log);
	}
	
	/**
	 * Find or create a logger for a named subsystem.
	 * @param name The name of the logger.
	 * @return the instance of the Logger.
	 */
	public synchronized static Logger getMyLogger(String name){
		
		Object myLogger = logManager.get(name);
		if (myLogger == null){
			myLogger = new Logger(name);
			logManager.put(name, myLogger);
		}
		return (Logger)myLogger;
	}
	

	/**
	 * In this implementation this methods does nothing.
	 * @param pp
	 */
	public static void initialize(Properties pp) {

	}
	
	
	/**
	 * Checks if the current level is loggable
	 * @param level
	 * @return
	 */
	public boolean isLoggable(int level){
		//FIXME: there should be a SUPPRESS level in android but at the moment it's not accessible.
		if (level == OFF)
			return false;
		else if(level ==ALL)
			return true;
		else 
			return Log.isLoggable(this.getClass().getName(), getAndroidLogLevel(level));
	}
	
	/**
	 * Prints out a log message onto the emulator log file.
	 * @param level The desired log level
	 * @param msg the message to log
	 */
	
	public void log(int level, String msg) {
		//FIXME OFF level not considered
		Log.println(getAndroidLogLevel(level), this.getClass().getName(), name + ": " + msg);
	}
	
	public void log(int level, String msg, Throwable t) {
		//FIXME OFF and ALL level not considered  
		int ll = getAndroidLogLevel(level);
		switch(ll){
		case  Log.ERROR: 
			Log.e(this.getClass().getName(), name + ": " + msg, t);
			break;
		case Log.WARN:
			Log.w(this.getClass().getName(), name + ": " + msg, t);
			break;
		case Log.INFO:
			Log.i(this.getClass().getName(), name + ": " + msg, t);
			break;
		case Log.VERBOSE:
			Log.v(this.getClass().getName(), name + ": " + msg, t);
			break;
		case Log.DEBUG:
			Log.d(this.getClass().getName(), name + ": " + msg, t);
			break;
		}
		
	}
	
	private int getAndroidLogLevel(int level){
		int out = Log.INFO;
		switch (level) {
			case  SEVERE: 
				out = Log.ERROR;
				break;
			case WARNING:
				out = Log.WARN;
				break;
			case INFO:
				out = Log.INFO;
				break;
			case CONFIG:
				out = Log.VERBOSE;
				break;
			case FINE:
				out = Log.DEBUG;
				break;
			case FINER:
				out = Log.DEBUG;
				break;	
			case FINEST:
				out = Log.DEBUG;
				break;
		}
		return out;
	}
}
