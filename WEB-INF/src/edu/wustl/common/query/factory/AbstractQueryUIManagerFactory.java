package edu.wustl.common.query.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.util.Utility;
import edu.wustl.query.util.global.Variables;
import edu.wustl.query.util.querysuite.AbstractQueryUIManager;
import edu.wustl.query.util.querysuite.QueryModuleError;
import edu.wustl.query.util.querysuite.QueryModuleException;

/**
 * Factory to return the AbstractQueryUIManager instance.
 * 
 * @author maninder_randhawa
 *
 */
public class AbstractQueryUIManagerFactory {
	/**
	 * Method to create instance of class AbstractQueryUIManager. 
	 * @return The reference of AbstractQueryUIManager. 
	 */
	public static AbstractQueryUIManager getDefaultAbstractUIQueryManager()
	{
		return (AbstractQueryUIManager) Utility.getObject(Variables.abstractQueryUIManagerClassName);
	}
	
	/**
	 * Method to create instance of class AbstractQueryUIManager. 
	 * @return The reference of AbstractQueryUIManager. 
	 */
	public static AbstractQueryUIManager configureDefaultAbstractUIQueryManager(Class className,HttpServletRequest request
			, IQuery iquery) throws QueryModuleException
	{
		
         String queryUIManagerClass = Variables.abstractQueryUIManagerClassName;
         QueryModuleException queryModuleException = null;
         AbstractQueryUIManager abstractQueryUIManager = null;
         if(queryUIManagerClass!=null)
         {
			try
			{
				className = Class.forName(queryUIManagerClass);
				if(className != null)
		         {
		         	Class[] parameterTypes = {HttpServletRequest.class,IQuery.class};
		         	Constructor declaredConstructor = className.getDeclaredConstructor(parameterTypes);
				   	abstractQueryUIManager =  (AbstractQueryUIManager)declaredConstructor.newInstance(request,iquery);
		         }
			}
			catch (ClassNotFoundException e)
			{
				queryModuleException = new QueryModuleException(e.getMessage(),QueryModuleError.CLASS_NOT_FOUND);
				throw queryModuleException;
			}
			catch (IllegalArgumentException e)
			{
				queryModuleException = new QueryModuleException(e.getMessage(),QueryModuleError.SQL_EXCEPTION);
				throw queryModuleException;
			}
			catch (InstantiationException e)
			{
				queryModuleException = new QueryModuleException(e.getMessage(),QueryModuleError.SQL_EXCEPTION);
				throw queryModuleException;
			}
			catch (IllegalAccessException e)
			{
				queryModuleException = new QueryModuleException(e.getMessage(),QueryModuleError.SQL_EXCEPTION);
				throw queryModuleException;
			}
			catch (InvocationTargetException e)
			{
				queryModuleException = new QueryModuleException(e.getMessage(),QueryModuleError.SQL_EXCEPTION);
				throw queryModuleException;
			}
			catch (NoSuchMethodException e)
			{
				queryModuleException = new QueryModuleException(e.getMessage(),QueryModuleError.SQL_EXCEPTION);
				throw queryModuleException;
			}
         }
		return abstractQueryUIManager;
	}
}
