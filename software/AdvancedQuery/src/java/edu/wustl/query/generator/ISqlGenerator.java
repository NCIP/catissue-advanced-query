/**
 * 
 */

package edu.wustl.query.generator;

import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.exceptions.SqlException;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.dao.exception.DAOException;

/**
 * To Generate SQL for the given Query Object.
 * 
 * @author prafull_kadam
 * 
 */
public interface ISqlGenerator {

    /**
     * Generates SQL for the given Query Object.
     * 
     * @param query The Reference to Query Object.
     * @return the String representing SQL for the given Query object.
     * @throws MultipleRootsException When there are multpile roots present in a
     *             graph.
     * @throws SqlException When there is error in the passed IQuery object.
     * @throws DAOException 
     * @throws RuntimeException 
     */
    String generateSQL(IQuery query) throws MultipleRootsException, SqlException, RuntimeException, DAOException;

    List<OutputTreeDataNode> getRootOutputTreeNodeList();

    /**
     * @return map with key as sql's column name and value as the term which
     *         that column represents.
     */
    Map<String, IOutputTerm> getOutputTermsColumns();
    

    Map<AttributeInterface, String> getAttributeColumnNameMap();
    
    /**
     * @return boolean variable which determines if the query is 
     * on multiple entity groups.
     */
    boolean isQueryOnMultipleEntityGroups();
}