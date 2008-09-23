package edu.wustl.query.util.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import edu.wustl.query.util.global.Constants;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.dao.DAOFactory;
import edu.wustl.common.dao.JDBCDAO;
import edu.wustl.common.util.dbManager.DAOException;

public class QueryCoreSessionListener implements HttpSessionListener
{
	public void sessionCreated(HttpSessionEvent arg0) 
	{

	}
	
	public void sessionDestroyed(HttpSessionEvent arg0) 
	{
		HttpSession session = arg0.getSession();
		SessionDataBean sessionData= (SessionDataBean)session.getAttribute(Constants.SESSION_DATA);

		if(sessionData!=null)
			cleanUp(sessionData,(String)session.getAttribute(Constants.RANDOM_NUMBER));

		// To remove PrivilegeCache from the session, requires user LoginName
		// Singleton instance of PrivilegeManager
		
		/**
		 * Commented out by Baljeet 
		 */
		/*if(sessionData != null)
		{
			PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
			privilegeManager.removePrivilegeCache(sessionData.getUserName());
		}*/
	}
	
	private void cleanUp(SessionDataBean sessionData,String randomNumber)
	{
		//Delete Advance Query table if exists
		//Advance Query table name with userID attached
		String tempTableName = Constants.QUERY_RESULTS_TABLE+"_"+sessionData.getUserId();
		try
		{
			JDBCDAO jdbcDao = (JDBCDAO)DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
			jdbcDao.openSession(sessionData);
			jdbcDao.delete(tempTableName);
			jdbcDao.closeSession();
		}
		catch(DAOException ex)
		{
			ex.printStackTrace();
		}
		String tempTableNameForQuery = Constants.TEMP_OUPUT_TREE_TABLE_NAME + sessionData.getUserId()+randomNumber;
		try
		{
			JDBCDAO jdbcDao = (JDBCDAO)DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
			jdbcDao.openSession(sessionData);
			jdbcDao.delete(tempTableNameForQuery);
			jdbcDao.closeSession();
		}
		catch(DAOException ex)
		{
			ex.printStackTrace();
		}
	}
}



