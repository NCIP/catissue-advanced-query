
package edu.wustl.query.bizlogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.beans.QueryResultObjectDataBean;
import edu.wustl.common.dao.QuerySessionData;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.tree.QueryTreeNodeData;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.query.util.global.Constants;
import edu.wustl.query.util.querysuite.QueryCSMUtil;
import edu.wustl.query.util.querysuite.QueryDetails;
import edu.wustl.query.util.querysuite.QueryModuleSqlUtil;
import edu.wustl.query.util.querysuite.QueryModuleUtil;

/**
 * Creates QueryOutputTree Object as per the data filled by the user on AddLimits section.
 * Creates QueryOutputTree Table.
 * @author deepti_shelar
 */
public class QueryOutputTreeBizLogic
{

	/**
	 * Creates new table which has the same structure and also same data , as the output tree structurs has.  
	 * @param String selectSql , from this sql , new table will be created .
	 * @param sessionData to be added to tablename to have unique table for each session user.
	 * @param String tableName 
	 * @throws Exception Exception
	 */
	//Siddharth Shah
//	public void createOutputTreeTable(String selectSql, QueryDetails queryDetailsObj)
//			throws DAOException
//	{
//		String tableName = Constants.TEMP_OUPUT_TREE_TABLE_NAME
//				+ queryDetailsObj.getSessionData().getUserId() + queryDetailsObj.getRandomNumber();
//		String createTableSql = Constants.CREATE_TABLE + tableName + " " + Constants.AS + " "
//				+ selectSql;
//		QueryModuleSqlUtil.executeCreateTable(tableName, createTableSql, queryDetailsObj);
//	}
	
	public void createOutputTreeTable(String selectSql, QueryDetails queryDetailsObj)
	throws DAOException
	
	{
		String tableName = Constants.TEMP_OUPUT_TREE_TABLE_NAME
		+ queryDetailsObj.getSessionData().getUserId() + queryDetailsObj.getRandomNumber();
		QueryModuleSqlUtil.executeCreateTable(tableName, selectSql, queryDetailsObj);	
	}

	/**
	 * This method creates first level(Default) output tree data.
	 * @param tableName name fo the table created
	 * @param query IQuery obj created out of user inputs to dag view.
	 * @param sessionData session data to get the user id.
	 * @param hasConditionOnIdentifiedField 
	 * @param mainEntityMap 
	 * @param uniqueIdNodesMap 
	 * @param nodeAttributeColumnNameMap map which strores all node ids  with their information like attributes and actual column names in database.
	 * @return Vector<QueryTreeNodeData> data structure to form tree out of it.
	 * @throws DAOException DAOException
	 * @throws ClassNotFoundException ClassNotFoundException
	 */
	public Vector<QueryTreeNodeData> createDefaultOutputTreeData(int treeNo,
			OutputTreeDataNode root, boolean hasConditionOnIdentifiedField,
			QueryDetails queryDetailsObj) throws DAOException, ClassNotFoundException
	{
		String tableName = Constants.TEMP_OUPUT_TREE_TABLE_NAME
				+ queryDetailsObj.getSessionData().getUserId() + queryDetailsObj.getRandomNumber();
		QueryResultObjectDataBean queryResulObjectDataBean = QueryCSMUtil
				.getQueryResulObjectDataBean(root, queryDetailsObj);
		Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap = new HashMap<Long, QueryResultObjectDataBean>();
		queryResultObjectDataBeanMap.put(root.getId(), queryResulObjectDataBean);
		String selectSql = QueryModuleSqlUtil.getSQLForRootNode(tableName, QueryModuleUtil
				.getColumnNamesForSelectpart(root, queryDetailsObj, queryResultObjectDataBeanMap
						.get(root.getId())));

		String[] sqlIndex = selectSql.split(Constants.NODE_SEPARATOR);
		selectSql = sqlIndex[0];
		int index = Integer.parseInt(sqlIndex[1]);

		QueryCsmBizLogic queryCsmBizLogic = new QueryCsmBizLogic();
		List dataList = queryCsmBizLogic.executeCSMQuery(selectSql, queryDetailsObj,
				queryResultObjectDataBeanMap, root, hasConditionOnIdentifiedField);
		Vector<QueryTreeNodeData> treeDataVector = new Vector<QueryTreeNodeData>();
		if (dataList != null && dataList.size() != 0)
		{
			QueryTreeNodeData treeNode = new QueryTreeNodeData();
			String name = root.getOutputEntity().getDynamicExtensionsEntity().getName();
			name = Utility.parseClassName(name);
			String displayName = Utility.getDisplayLabel(name) + " (" + dataList.size() + ")";
			String nodeId = createNodeId(treeNo, root);
			displayName = Constants.TREE_NODE_FONT + displayName + Constants.TREE_NODE_FONT_CLOSE;
			treeNode.setIdentifier(nodeId);
			treeNode.setObjectName(name);
			treeNode.setDisplayName(displayName);
			treeNode.setParentIdentifier(Constants.ZERO_ID);
			treeNode.setParentObjectName("");
			treeDataVector.add(treeNode);
			treeDataVector = addNodeToTree(index, dataList, treeNode, root, treeDataVector);
		}
		return treeDataVector;
	}

	/**
	 * @param treeNo
	 * @param root
	 * @return
	 */
	private String createNodeId(int treeNo, OutputTreeDataNode root)
	{
		String nodeId = treeNo + "_" + Constants.NULL_ID + Constants.NODE_SEPARATOR
				+ root.getUniqueNodeId() + Constants.UNDERSCORE + Constants.LABEL_TREE_NODE;
		return nodeId;
	}

	/**
	 * Encrypts the id to be set for tree node
	 * @param id String original id
	 * @return encrypted id
	 */
	public String encryptId(String id)
	{
		String encryptedId = Constants.UNIQUE_ID_SEPARATOR + id;
		return encryptedId;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public String decryptId(String id)
	{
		int indexOfSeperator = id.indexOf(Constants.UNIQUE_ID_SEPARATOR);
		if (indexOfSeperator != -1)
		{
			String decryptedId = id.substring(indexOfSeperator
					+ Constants.UNIQUE_ID_SEPARATOR.length());
			return decryptedId;
		}
		return id;
	}

	/**
	 * This method adds the node to tree.The id for new node is set as 'Id of OutputTreeNode _id value of that node in newly created table'
	 * @param dataList all records in database satisfying the criteria.
	 * @param parentNode parent node of tree data
	 * @param node the node to be added of OutputTreeDataNode.
	 * @param treeDataVector  data structure to form tree out of it.
	 * @param nodeAttributeColumnNameMap map which strores all node ids  with their information like attributes and actual column names in database.
	 * @return treeDataVector  data structure to form tree out of it.
	 */
	private Vector<QueryTreeNodeData> addNodeToTree(int index, List dataList,
			QueryTreeNodeData parentNode, OutputTreeDataNode node,
			Vector<QueryTreeNodeData> treeDataVector)
	{
		Iterator dataListIterator = dataList.iterator();
		List rowList = new ArrayList();
		String uniqueNodeId = node.getUniqueNodeId();
		String parentNodeId = uniqueNodeId + Constants.UNDERSCORE + Constants.LABEL_TREE_NODE;

		List<String> primaryKeyList = edu.wustl.query.util.global.Utility.getPrimaryKey(node
				.getOutputEntity().getDynamicExtensionsEntity());
		int idColumnSize = primaryKeyList.size();

		while (dataListIterator.hasNext())
		{
			rowList = (List) dataListIterator.next();
			QueryTreeNodeData treeNode = null;
			String data = "";
			int i = 0;
			for (; i < idColumnSize; i++)
			{
				data = data + Constants.PRIMARY_KEY_ATTRIBUTE_SEPARATOR + (String) rowList.get(i);
			}
			if (data.startsWith(Constants.PRIMARY_KEY_ATTRIBUTE_SEPARATOR))
			{
				data = data.substring(Constants.PRIMARY_KEY_ATTRIBUTE_SEPARATOR.length());
			}
			String currentNodeId = uniqueNodeId + Constants.UNDERSCORE + data;
			if (data.contains("#"))
			{
				currentNodeId = uniqueNodeId + Constants.UNDERSCORE + Constants.HASHED_NODE_ID;
			}
			String nodeIdToSet = parentNodeId + Constants.NODE_SEPARATOR + currentNodeId;
			nodeIdToSet = encryptId(nodeIdToSet);

			treeNode = new QueryTreeNodeData();
			treeNode.setIdentifier(nodeIdToSet);
			EntityInterface dynExtEntity = node.getOutputEntity().getDynamicExtensionsEntity();
			String fullyQualifiedEntityName = dynExtEntity.getName();
			treeNode.setObjectName(fullyQualifiedEntityName);
			String displayName = formatDisplayData(data);
			if (index != -1)
			{
				displayName = (String) rowList.get(i);
				if (displayName.equals(""))
				{
					displayName = ApplicationProperties.getValue("query.tree.label.NA");;
				}
			}
			treeNode.setDisplayName(displayName);
			treeNode.setParentIdentifier(parentNode.getIdentifier().toString());
			treeNode.setParentObjectName(parentNode.getObjectName());
			treeDataVector.add(treeNode);
		}
		//		}
		return treeDataVector;
	}

	/**
	 * @param data input string
	 * @return formatted string for display name of node
	 */
	private String formatDisplayData(String data)
	{	
		return data.replaceAll(Constants.PRIMARY_KEY_ATTRIBUTE_SEPARATOR, Constants.UNDERSCORE);
	}

	/**
	 * Updates tree when user clicks on any of the nodes.
	 * @param id string id of the node 
	 * @param node node cicked
	 * @param idColumnMap map which strores all node ids  with their information like attributes and actual column names in database.
	 * @param parentNodeId string id of parent
	 * @param sessionData SessionDataBean to be sent for execute query
	 * @return string representing tree node structure
	 * @throws ClassNotFoundException ClassNotFoundException
	 * @throws DAOException DAOException
	 */
	public String updateTreeForDataNode(String id, OutputTreeDataNode node, String parentNodeId,
			QueryDetails queryDetailsObj) throws ClassNotFoundException, DAOException
	{
		String tableName = Constants.TEMP_OUPUT_TREE_TABLE_NAME
				+ queryDetailsObj.getSessionData().getUserId() + queryDetailsObj.getRandomNumber();
		String parentIdColumnName = QueryModuleUtil.getParentIdColumnName(node);
		List<OutputTreeDataNode> children = node.getChildren();
		String outputTreeStr = "";
		for (OutputTreeDataNode childNode : children)
		{
			String selectSql = getSql(parentNodeId, tableName, parentIdColumnName, childNode);
			String name = childNode.getOutputEntity().getDynamicExtensionsEntity().getName();
			name = Utility.parseClassName(name);
			List<List<String>> dataList = getTreeDataList(queryDetailsObj, selectSql, null, false);
			//List dataList = QueryModuleUtil.executeQuery(selectSql, sessionData);
			int size = dataList.size();
			if (size != 0)
			{
				String parId = id.substring(id.lastIndexOf(Constants.NODE_SEPARATOR) + 2, id
						.length());
				String childNodeId = childNode.getUniqueNodeId() + Constants.UNDERSCORE
						+ Constants.LABEL_TREE_NODE;
				String nodeId = Constants.UNIQUE_ID_SEPARATOR + parId + Constants.NODE_SEPARATOR
						+ childNodeId;
				String displayName = Utility.getDisplayLabel(name) + " (" + size + ")";
				displayName = Constants.TREE_NODE_FONT + displayName
						+ Constants.TREE_NODE_FONT_CLOSE;
				String objectName = name;
				String fullName = node.getOutputEntity().getDynamicExtensionsEntity().getName();
				String parentObjectName = Utility.parseClassName(fullName);
				outputTreeStr = outputTreeStr + "|" + nodeId + "," + displayName + "," + objectName
						+ "," + id + "," + parentObjectName;
			}
		}
		return outputTreeStr;
	}

	/**
	 * Returns the sql to be fired and to get data to update tree view.
	 * @param idColumnMap map which strores all node ids  with their information like attributes and actual column names in database.
	 * @param parentNodeId string id of parent
	 * @param tableName name of the table
	 * @param parentIdColumnName name of the column of parent node's id
	 * @param childNode child node 
	 * @return String sql to be fired to get data to update tree.
	 */
	private String getSql(String parentNodeId, String tableName, String parentIdColumnName,
			OutputTreeDataNode childNode)
	{
		String selectSql = Constants.SELECT_DISTINCT;
		String idColumnOfCurrentNode = "";
		List<QueryOutputTreeAttributeMetadata> attributes = childNode.getAttributes();
		String sqlColumnName = "";
		List<String> primaryKeyList = edu.wustl.query.util.global.Utility.getPrimaryKey(childNode
				.getOutputEntity().getDynamicExtensionsEntity());
		for (QueryOutputTreeAttributeMetadata attributeMetaData : attributes)
		{
			AttributeInterface attribute = attributeMetaData.getAttribute();
			sqlColumnName = attributeMetaData.getColumnName();
			if (primaryKeyList.contains(attribute.getName()))
			{
				idColumnOfCurrentNode = idColumnOfCurrentNode + "," + sqlColumnName;
			}

			if (!attribute.getAttributeTypeInformation().getDataType().equalsIgnoreCase(
					Constants.FILE_TYPE))
			{
				selectSql = selectSql + sqlColumnName + ",";
			}
		}
		if (idColumnOfCurrentNode.length() > 0 && idColumnOfCurrentNode.charAt(0) == ',')
		{
			idColumnOfCurrentNode = idColumnOfCurrentNode.substring(1, idColumnOfCurrentNode
					.length());
		}
		selectSql = selectSql.substring(0, selectSql.lastIndexOf(','));
		selectSql = edu.wustl.query.util.global.Utility.getSQLForNode(parentNodeId, tableName, parentIdColumnName, selectSql,
				idColumnOfCurrentNode);
		return selectSql;
	}

	

	/**
	 * This method is called when user clicks on a node present in a tree, to get all the child nodes added to tree. 
	 * @param nodeId id of the node clicked.
	 * @param idNodeMap map which stores id and nodes already added to tree.
	 * @param sessionData sessionData session data to get the user id.
	 * @param hasConditionOnIdentifiedField 
	 * @param mainEntityMap 
	 * @return String outputTreeStr which is then parsed and then sent to client to form tree. 
	 * String for one node is comma seperated for its id, display name, object name , parentId, parent Object name.
	 * Such string elements for child nodes are seperated by "|".
	 */
	public String updateTreeForLabelNode(String nodeId, QueryDetails queryDetailsObj,
			boolean hasConditionOnIdentifiedField) throws ClassNotFoundException, DAOException
	{
		String tableName = Constants.TEMP_OUPUT_TREE_TABLE_NAME
				+ queryDetailsObj.getSessionData().getUserId() + queryDetailsObj.getRandomNumber();
		String selectSql = "";
		int index = -1;

		
		
		String labelNode = nodeId.substring(nodeId.lastIndexOf(Constants.NODE_SEPARATOR) + 2,
				nodeId.length());
		String[] splitIds = labelNode.split(Constants.UNDERSCORE);
		String treeNo = splitIds[0];
		String treeNodeId = splitIds[1];
		String uniqueCurrentNodeId = treeNo + "_" + treeNodeId;
		String parentNodeId = nodeId.substring(0, nodeId.indexOf(Constants.NODE_SEPARATOR));
		String decryptedId = decryptId(parentNodeId);
		String[] nodeIds = decryptedId.split(Constants.UNDERSCORE);
		String parentId = nodeIds[1];
		String parentData = null;
		Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap = null;
		if (nodeIds.length == 3)
		{
			parentData = nodeIds[2];
		}
		String uniqueParentId = treeNo + "_" + parentId;
		OutputTreeDataNode parentNode = queryDetailsObj.getUniqueIdNodesMap().get(uniqueParentId);
		OutputTreeDataNode currentNode = queryDetailsObj.getUniqueIdNodesMap().get(
				uniqueCurrentNodeId);
		if (!parentNodeId.contains(Constants.NULL_ID))
		{
			String parentIdColumnName = QueryModuleUtil.getParentIdColumnName(parentNode);
			List<OutputTreeDataNode> children = parentNode.getChildren();
			if (children.isEmpty())
			{
				return "";
			}
			String columnNames = "";
			QueryResultObjectDataBean queryResulObjectDataBean = QueryCSMUtil
					.getQueryResulObjectDataBean(currentNode, queryDetailsObj);
			queryResultObjectDataBeanMap = new HashMap<Long, QueryResultObjectDataBean>();
			queryResultObjectDataBeanMap.put(currentNode.getId(), queryResulObjectDataBean);
			Map<String, String> columnNameIndexMap = QueryModuleUtil.getColumnNamesForSelectpart(
					currentNode, queryDetailsObj, queryResultObjectDataBeanMap.get(currentNode
							.getId()));
			columnNames = columnNameIndexMap.get(Constants.COLUMN_NAMES);
			String indexStr = columnNameIndexMap.get(Constants.INDEX);
			if ((indexStr != null) && (!indexStr.equalsIgnoreCase(Constants.NULL)))
			{
				index = Integer.valueOf(indexStr);
			}
			//columnNames = columnNames.substring(0, columnNames.lastIndexOf(";"));
			selectSql = "select distinct " + columnNames;
			String idColumnOfCurrentNode = columnNames;
			if (columnNames.indexOf(',') != -1)
			{
				idColumnOfCurrentNode = columnNames.substring(0, columnNames.indexOf(','));
			}
			selectSql =  edu.wustl.query.util.global.Utility.getSQLForNode(parentData, tableName, parentIdColumnName, selectSql,
					idColumnOfCurrentNode);
		}
		if (parentNodeId.contains(Constants.NULL_ID))
		{

			selectSql = QueryModuleSqlUtil.getSQLForRootNode(tableName, QueryModuleUtil
					.getColumnNamesForSelectpart(currentNode, queryDetailsObj,
							queryResultObjectDataBeanMap.get(currentNode.getId())));

			String indexStr = selectSql.substring(selectSql.indexOf(Constants.NODE_SEPARATOR) + 2,
					selectSql.length());
			if (!indexStr.equalsIgnoreCase(Constants.NULL))
			{
				index = Integer.valueOf(indexStr);
			}
			selectSql = selectSql.substring(0, selectSql.indexOf(Constants.NODE_SEPARATOR));
		}
		List<List<String>> dataList = getTreeDataList(queryDetailsObj, selectSql,
				queryResultObjectDataBeanMap, hasConditionOnIdentifiedField);

		//List dataList = QueryModuleUtil.executeQuery(selectSql, sessionData);
		String outputTreeStr = buildOutputTreeString(index, dataList, currentNode, nodeId,
				parentNode, queryDetailsObj);
		return outputTreeStr;
	}

	/**
	 * This method builds a string from the input data , based on this string tree will be formed.
	 * @param dataList List of result records
	 * @param children List<OutputTreeDataNode> child nodes 
	 * @param nodeId String id which will be parent id for the new nodes added to tree. 
	 * @param parentNode parent node 
	 * @param idNodeMap map which stores id and nodes already added to tree.
	 * @return String outputTreeStr which is then parsed and then sent to client to form tree. 
	 * String for one node is comma seperated for its id, display name, object name , parentId, parent Object name.
	 * Such string elements for child nodes are seperated by "|".
	 **/
	String buildOutputTreeString(int index, List dataList, OutputTreeDataNode currentNode,
			String parentNodeId, OutputTreeDataNode parentNode, QueryDetails queryDetailsObj)
	{
		Iterator dataListIterator = dataList.iterator();
		List<String> existingNodesList = new ArrayList<String>();
		List rowList = new ArrayList();
		String outputTreeStr = "";
		List<String> primaryKeyList = edu.wustl.query.util.global.Utility.getPrimaryKey(currentNode
				.getOutputEntity().getDynamicExtensionsEntity());
		int idColumnSize = primaryKeyList.size();
		while (dataListIterator.hasNext())
		{
			rowList = (List) dataListIterator.next();
			int i = 0;
			String data = "";
			for (; i < idColumnSize; i++)
			{
				data = data + Constants.PRIMARY_KEY_ATTRIBUTE_SEPARATOR + (String) rowList.get(i);
			}
			if (data.startsWith(Constants.PRIMARY_KEY_ATTRIBUTE_SEPARATOR))
			{
				data = data.substring(Constants.PRIMARY_KEY_ATTRIBUTE_SEPARATOR.length());
			}
			String fullyQualifiedEntityName = currentNode.getOutputEntity()
					.getDynamicExtensionsEntity().getName();
			String entityName = Utility.parseClassName(fullyQualifiedEntityName);
			String currentNodeId = currentNode.getUniqueNodeId() + Constants.UNDERSCORE + data;
			if (data.contains("#"))
			{
				currentNodeId = currentNode.getUniqueNodeId() + Constants.UNDERSCORE
						+ Constants.HASHED_NODE_ID;
			}
			String labelNode = parentNodeId.substring(parentNodeId
					.lastIndexOf(Constants.NODE_SEPARATOR) + 2, parentNodeId.length());
			String nodeIdToSet = Constants.UNIQUE_ID_SEPARATOR + labelNode
					+ Constants.NODE_SEPARATOR + currentNodeId;
			String displayName = entityName + Constants.UNDERSCORE + data;
			if (index != -1)
			{
				displayName = (String) rowList.get(i);
			}
			if (displayName.equalsIgnoreCase(""))
			{
				//displayName = entityName + Constants.UNDERSCORE + data;
				if (data.equals(""))
				{
					displayName = ApplicationProperties.getValue("query.tree.label.NA");;
				}
				else
				{
					displayName = data;
				}
			}
			else
			{
				displayName = formatDisplayData(displayName);
			}
			String objectname = fullyQualifiedEntityName;
			String parentObjectName = "";
			if (parentNode != null)
			{
				parentObjectName = parentNode.getOutputEntity().getDynamicExtensionsEntity()
						.getName();
			}
			if (!existingNodesList.contains(nodeIdToSet))
			{
				existingNodesList.add(nodeIdToSet);
				queryDetailsObj.getUniqueIdNodesMap().put(
						String.valueOf(currentNode.getUniqueNodeId()), currentNode);
				outputTreeStr = outputTreeStr + nodeIdToSet + "," + displayName + "," + objectname
						+ "," + parentNodeId + "," + parentObjectName + "|";
			}
		}
		return outputTreeStr;
	}

	/**
	 * @return
	 * @throws DAOException 
	 * @throws ClassNotFoundException 
	 */
	private List<List<String>> getTreeDataList(QueryDetails queryDetailsObj, String selectSql,
			Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap,
			boolean hasConditionOnIdentifiedField) throws ClassNotFoundException, DAOException
	{
		QuerySessionData querySessionData = new QuerySessionData();
		querySessionData.setSql(selectSql);
		querySessionData.setQueryResultObjectDataMap(queryResultObjectDataBeanMap);
		querySessionData.setSecureExecute(queryDetailsObj.getSessionData().isSecurityRequired());
		querySessionData.setHasConditionOnIdentifiedField(hasConditionOnIdentifiedField);
		List<List<String>> dataList = QueryModuleSqlUtil.executeQuery(queryDetailsObj
				.getSessionData(), querySessionData);

		querySessionData.setTotalNumberOfRecords(dataList.size());

		return dataList;
	}
}