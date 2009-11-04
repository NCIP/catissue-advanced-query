/**
 *
 */
package edu.wustl.query.querymanager;

import java.sql.SQLException;
import java.util.List;

import edu.wustl.common.query.AbstractQuery;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.exceptions.SqlException;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.query.domain.Workflow;
import edu.wustl.query.util.querysuite.QueryModuleException;


/**
 * @author supriya_dankh
 * The UI layer will use this class for all query backend related operations.
 *  Specific implementations will handle application specific business logic
 *  like for CIDER adding additional predicates (like active UPI flag) into the IQuery object.
 *
 *  This class is also responsible for
 *   a.   Maintaining a list of currently running queries
 *   b.   Spawning new threads for new queries
 *   c.   Provide methods to get count, cancel/abort query threads etc.
 *
 */
public abstract class AbstractQueryManager
{

    /**
     * This method is used to execute the given <code>query</code> object.
     *
     * @param query
     *            The query object to be executed.
     * @return The Query Execution Id generated for the given <code>query</code>
     *         object.
     * @throws MultipleRootsException
     *             MultipleRootsException
     * @throws SqlException
     *             SqlException
     * @throws QueryModuleException
     *             QueryModuleException
     */
    abstract public Long execute(AbstractQuery query)
            throws MultipleRootsException, SqlException, QueryModuleException;

	/**
     * This method is used to execute a composite query.
     *
     * @param compositeQuery
     *            The Composite Query
     * @param dependendentQueryExecIds
     *            List containing the <code>query_Execution_ids</code> of the
     *            dependent queries.
     * @return The <code>Query_Exec_Id</code> of the composite query.
     * @throws MultipleRootsException
     * @throws SqlException
     * @throws QueryModuleException
     */
    abstract public Long execute(AbstractQuery compositeQuery,
            List<Long> dependendentQueryExecIds)
            throws MultipleRootsException, SqlException, QueryModuleException;

	/**
	 *
	 * @param query_excecution_id
	 * @return
	 * @throws DAOException
	 * @throws SQLException
	 */
	abstract public Count getQueryCount(Long query_excecution_id) throws DAOException, SQLException;

	/**
	 *
	 * @param query_excecution_id
	 * @return
	 */
	abstract public Count getWorkflowCount(int query_excecution_id);

	/**
	 *
	 * @param query_execution_id
	 * @throws SQLException
	 */
	abstract public void cancel(Long query_execution_id) throws SQLException;

}
