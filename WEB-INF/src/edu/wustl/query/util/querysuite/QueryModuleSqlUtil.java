
package edu.wustl.query.util.querysuite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.dao.DAOFactory;
import edu.wustl.common.dao.JDBCDAO;
import edu.wustl.common.dao.QuerySessionData;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.query.util.global.Constants;

/**
 * @author santhoshkumar_c
 *
 */
final public class QueryModuleSqlUtil
{

	private QueryModuleSqlUtil()
	{
	}

	/**
	 * Executes the query and returns the results.
	 * @param selectSql sql to be executed
	 * @param sessionData sessiondata
	 * @param querySessionData
	 * @return list of results
	 * @throws ClassNotFoundException
	 * @throws DAOException
	 */
	public static List<List<String>> executeQuery(final SessionDataBean sessionData,
			final QuerySessionData querySessionData) throws ClassNotFoundException, DAOException
	{
		JDBCDAO dao = (JDBCDAO) DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
		List<List<String>> dataList = new ArrayList<List<String>>();
		try
		{
			dao.openSession(sessionData);
			dataList = dao.executeQuery(querySessionData.getSql(), sessionData, querySessionData
					.isSecureExecute(), querySessionData.isHasConditionOnIdentifiedField(),
					querySessionData.getQueryResultObjectDataMap());
			dao.commit();
		}
		finally
		{
			dao.closeSession();
		}
		return dataList;
	}

	/**
		 * Creates a new table in database. First the table is deleted if exist already.
		 * @param tableName name of the table to be deleted before creating new one.
		 * @param createTableSql sql to create table
		 * @param sessionData session data.
		 * @throws DAOException DAOException 
		 */
	public static void executeCreateTable(final String tableName, final String createTableSql,
			QueryDetails queryDetailsObj) throws DAOException
	{
		JDBCDAO jdbcDao = (JDBCDAO) DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);
		try
		{
			jdbcDao.openSession(queryDetailsObj.getSessionData());
			jdbcDao.delete(tableName);
			jdbcDao.executeUpdate(createTableSql);
			jdbcDao.commit();
		}
		catch (DAOException e)
		{
			Logger.out.error(e);
			//			e.printStackTrace();
			//			throw e;
		}
		finally
		{
			jdbcDao.closeSession();
		}
	}

	/**
		 * @param tableName
		 * @param columnNameIndexMap
		 * @return
		 */
	public static String getSQLForRootNode(final String tableName,
			Map<String, String> columnNameIndexMap)
	{
		//		Map<String,String> columnNameIndexMap = getColumnNamesForSelectpart(root,queryResulObjectDataMap,uniqueIdNodesMap2);
		String columnNames = columnNameIndexMap.get(Constants.COLUMN_NAMES);
		String indexStr = columnNameIndexMap.get(Constants.INDEX);
		int index = -1;
		if (indexStr != null && !Constants.NULL.equals(indexStr))
		{
			index = Integer.valueOf(indexStr);
		}
		String idColumnName = columnNames;
		if (columnNames.indexOf(',') != -1)
		{
			idColumnName = columnNames.substring(0, columnNames.indexOf(','));
		}
		//		String selectSql = "select distinct " + columnNames + " from " + tableName + " where "
		//				+ idColumnName + " is not null";
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("select distinct ").append(columnNames).append(" from ").append(tableName)
				.append(" where ").append(idColumnName).append(" is not null");
		selectSql = selectSql.append(Constants.NODE_SEPARATOR).append(index);
		//selectSql = selectSql + Constants.NODE_SEPARATOR + index;
		return selectSql.toString();
	}

}
