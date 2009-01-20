package edu.wustl.query.util.querysuite;

/**
 * This class is base for all QueryUIManager classes.
 */
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.query.querymanager.Count;
import edu.wustl.query.util.global.Constants;

public abstract class AbstractQueryUIManager {
	
	protected HttpServletRequest request;
	protected HttpSession session;
	protected boolean isSavedQuery;
	protected QueryDetails queryDetailsObj;

	/**
	 * This method extracts query object and forms results .
	 * 
	 * @param option
	 * @return status
	 */
	public int searchQuery(String option) throws QueryModuleException {
		session.removeAttribute(Constants.HYPERLINK_COLUMN_MAP);
		QueryModuleError status = QueryModuleError.SUCCESS;
		int query_exec_id = 0;
		

			if (queryDetailsObj.getSessionData() != null) {
				query_exec_id = processQuery();
			}
		
		return query_exec_id;
	}

	/**
	 * This method Processes the Query by calling execute method of QueryManager.
	 * 
	 * @throws QueryModuleException
	 */
	public abstract int processQuery() throws QueryModuleException ;
	
	public abstract Count getCount(int query_execution_id) throws QueryModuleException;
	
	/**
	 * This method gets the required objects (incase of Cider objects will be Projects)
	 * based on which query results can be filtered.
	 * @param userId get Objects based on user id. 
	 * @return collection of required objects.
	 * @throws QueryModuleException
	 */
	abstract public List<NameValueBean> getObjects(Long userId) throws QueryModuleException;
	
	/**
	 * This method updates the query object with default conditions
	 * @throws QueryModuleException
	 */
	abstract public void updateQuery() throws QueryModuleException;
}
