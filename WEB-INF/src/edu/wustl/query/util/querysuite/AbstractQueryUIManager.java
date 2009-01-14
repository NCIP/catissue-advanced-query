package edu.wustl.query.util.querysuite;

/**
 * This class is base for all QueryUIManager classes.
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import edu.wustl.common.query.CiderQuery;
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
}
