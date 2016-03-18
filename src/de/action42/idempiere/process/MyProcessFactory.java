package de.action42.idempiere.process;

import java.util.logging.Level;

import org.adempiere.base.IProcessFactory;
import org.compiere.process.ProcessCall;
import org.compiere.util.CLogger;

public class MyProcessFactory implements IProcessFactory {

	private final static CLogger log = CLogger.getCLogger(MyProcessFactory.class);

	public MyProcessFactory() {
		log.log(Level.WARNING, "MyProcessFactory ");
		// TODO Auto-generated constructor stub
	}

	@Override
	public ProcessCall newProcessInstance(String className) {
		log.log(Level.WARNING, "MyProcessFactory.newProcessInstance ");
		ProcessCall process = null;
		if (className.equals(ExportDATEV.class.getName())) {
			className = "de.action42.idempiere.process.ExportDATEV";
			//Get Class
			Class<?> processClass = null;
			//use context classloader if available
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader != null)
			{
				try
				{
					processClass = classLoader.loadClass(className);
				}
				catch (ClassNotFoundException ex)
				{
					if (log.isLoggable(Level.FINE))log.log(Level.FINE, className, ex);
				}
			}
			if (processClass == null)
			{
				classLoader = this.getClass().getClassLoader();
				try
				{
					processClass = classLoader.loadClass(className);
				}
				catch (ClassNotFoundException ex)
				{
					log.log(Level.WARNING, className, ex);
					return null;
				}
			}

			if (processClass == null) {
				return null;
			}

			//Get Process
			try
			{
				process = (ProcessCall)processClass.newInstance();
			}
			catch (Exception ex)
			{
				log.log(Level.WARNING, "Instance for " + className, ex);
				return null;
			}
		}
		return process;
	}

}
