
package edu.wustl.query.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.action.BaseAction;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.QueryResultObjectDataBean;
import edu.wustl.common.dao.QuerySessionData;
import edu.wustl.common.query.queryobject.impl.OutputTreeDataNode;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.querysuite.queryobject.IOutputAttribute;
import edu.wustl.common.querysuite.queryobject.IOutputTerm;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.query.actionForm.CategorySearchForm;
import edu.wustl.query.bizlogic.DefineGridViewBizLogic;
import edu.wustl.query.bizlogic.QueryOutputSpreadsheetBizLogic;
import edu.wustl.query.util.global.Constants;
import edu.wustl.query.util.querysuite.QueryDetails;
import edu.wustl.query.util.querysuite.QueryModuleUtil;

/**
 * When user is done with selecting the columns to be shown in results, this action is invoked.
 * This action creates metadata for selected columns and keeps it in session for further reference.
 *  
 * @author deepti_shelar
 *
 */
public class ConfigureGridViewAction extends BaseAction
{
	/**
	 * creates metadata for selected columns and keeps it in session for further reference.
	 * @param mapping mapping
	 * @param form form
	 * @param request request
	 * @param response response
	 * @throws Exception Exception
	 * @return ActionForward actionForward
	 */
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception
	{ 
		CategorySearchForm categorySearchForm = (CategorySearchForm) form;
		HttpSession session = request.getSession();
		Map<String, IOutputTerm> outputTermsColumns = (Map<String, IOutputTerm>)session.getAttribute(Constants.OUTPUT_TERMS_COLUMNS);
		IQuery query = (IQuery)session.getAttribute(Constants.QUERY_OBJECT);
		QueryDetails queryDetailsObj = new QueryDetails(session);
		SelectedColumnsMetadata selectedColumnsMetadata = (SelectedColumnsMetadata)session.getAttribute(Constants.SELECTED_COLUMN_META_DATA);
		OutputTreeDataNode currentSelectedObject = selectedColumnsMetadata.getCurrentSelectedObject();
		QuerySessionData querySessionData = (QuerySessionData) session.getAttribute(Constants.QUERY_SESSION_DATA);
		String recordsPerPageStr = (String) session.getAttribute(Constants.RESULTS_PER_PAGE);
		int recordsPerPage =Integer.valueOf(recordsPerPageStr);
		
		Boolean hasConditionOnIdentifiedField = (Boolean)session.getAttribute(Constants.HAS_CONDITION_ON_IDENTIFIED_FIELD);
		//Map<EntityInterface ,List<EntityInterface>> mainEntityMap =(Map<EntityInterface ,List<EntityInterface>>)session.getAttribute(Constants.MAIN_ENTITY_MAP);

		//Map<String, OutputTreeDataNode> uniqueIdNodesMap = (Map<String, OutputTreeDataNode>) session.getAttribute(Constants.ID_NODES_MAP);
		DefineGridViewBizLogic defineGridViewBizLogic = new DefineGridViewBizLogic();
		QueryOutputSpreadsheetBizLogic queryOutputSpreadsheetBizLogic = new QueryOutputSpreadsheetBizLogic();

		String sql = querySessionData.getSql();
		session.removeAttribute(Constants.EXPORT_DATA_LIST);
		session.removeAttribute(Constants.ENTITY_IDS_MAP);

		Map spreadSheetDataMap = new HashMap();
		List<String> definedColumnsList = new ArrayList<String>();
		List<NameValueBean> selectedColumnNameValueBeanList = categorySearchForm.getSelectedColumnNameValueBeanList();
		String op = (String) request.getParameter(Constants.OPERATION);
		if (op==null)
		{
			op = Constants.FINISH;
		}
		
		if (op.equalsIgnoreCase(Constants.FINISH))
		{  
			selectedColumnsMetadata.setDefinedView(true);
			Map<Long, QueryResultObjectDataBean> queryResultObjecctDataMap = new HashMap<Long, QueryResultObjectDataBean>();			
			//defineGridViewBizLogic.getSelectedColumnsMetadata(categorySearchForm, queryDetailsObj,selectedColumnsMetadata);
			defineGridViewBizLogic.getSelectedColumnsMetadata(categorySearchForm, queryDetailsObj, selectedColumnsMetadata,query.getConstraints());
			StringBuffer selectedColumnNames = new StringBuffer();
			String nodeData = getClickedNodeData(sql);
			definedColumnsList = defineGridViewBizLogic.getSelectedColumnList(categorySearchForm,
					selectedColumnsMetadata.getSelectedAttributeMetaDataList(), selectedColumnNames,
						queryResultObjecctDataMap, queryDetailsObj, outputTermsColumns,nodeData);
			spreadSheetDataMap.put(Constants.SPREADSHEET_COLUMN_LIST, definedColumnsList);
			// gets the message and sets it in the session.
			//String message = QueryModuleUtil.getMessageIfIdNotPresentForOrderableEntities(selectedColumnsMetadata,cart);
			//session.setAttribute(Constants.VALIDATION_MESSAGE_FOR_ORDERING, message);
			String SqlForSelectedColumns = defineGridViewBizLogic.createSQLForSelectedColumn(selectedColumnNames.toString(), sql);
			querySessionData = queryOutputSpreadsheetBizLogic.getQuerySessionData(queryDetailsObj,
					recordsPerPage, 0, spreadSheetDataMap, SqlForSelectedColumns,
					queryResultObjecctDataMap, hasConditionOnIdentifiedField);
			selectedColumnNameValueBeanList = categorySearchForm.getSelectedColumnNameValueBeanList();
			session.setAttribute(Constants.DEFINE_VIEW_QUERY_REASULT_OBJECT_DATA_MAP, queryResultObjecctDataMap);
			spreadSheetDataMap.put(Constants.DEFINE_VIEW_QUERY_REASULT_OBJECT_DATA_MAP, queryResultObjecctDataMap);
			spreadSheetDataMap.put(Constants.QUERY_REASUL_OBJECT_DATA_MAP, session.getAttribute(Constants.QUERY_REASUL_OBJECT_DATA_MAP));
		}
		else if (op.equalsIgnoreCase(Constants.BACK))
		{ 
			Map<Long, QueryResultObjectDataBean> queryResultObjecctDataMap = (Map<Long, QueryResultObjectDataBean>)session.getAttribute(Constants.DEFINE_VIEW_QUERY_REASULT_OBJECT_DATA_MAP);
			spreadSheetDataMap.put(Constants.DEFINE_VIEW_QUERY_REASULT_OBJECT_DATA_MAP, queryResultObjecctDataMap);
			if(!selectedColumnsMetadata.isDefinedView())
			{
				selectedColumnsMetadata.setDefinedView(false);
				queryResultObjecctDataMap = (Map<Long, QueryResultObjectDataBean>)session.getAttribute(Constants.QUERY_REASUL_OBJECT_DATA_MAP);
				spreadSheetDataMap.put(Constants.QUERY_REASUL_OBJECT_DATA_MAP, queryResultObjecctDataMap);
			}
			selectedColumnNameValueBeanList = selectedColumnsMetadata.getSelectedColumnNameValueBeanList();
			categorySearchForm.setSelectedColumnNameValueBeanList(selectedColumnNameValueBeanList);
			definedColumnsList = (List<String>) session.getAttribute(Constants.SPREADSHEET_COLUMN_LIST);
			spreadSheetDataMap.put(Constants.SPREADSHEET_COLUMN_LIST, definedColumnsList);
			querySessionData = queryOutputSpreadsheetBizLogic.getQuerySessionData(queryDetailsObj,
					recordsPerPage, 0, spreadSheetDataMap, sql, queryResultObjecctDataMap,
					hasConditionOnIdentifiedField);
		}
		else if (op.equalsIgnoreCase(Constants.RESTORE))
		{ 
			Map<Long, QueryResultObjectDataBean> queryResultObjecctDataMap = new HashMap<Long, QueryResultObjectDataBean>();
			selectedColumnsMetadata.setDefinedView(false);
			defineGridViewBizLogic.getColumnsMetadataForSelectedNode(currentSelectedObject,selectedColumnsMetadata,query.getConstraints());
			StringBuffer selectedColumnNames = new StringBuffer();
			//Restoring to the default view.
			selectedColumnsMetadata.setSelectedOutputAttributeList(new ArrayList<IOutputAttribute>());
			String nodeData = getClickedNodeData(sql);
			definedColumnsList = defineGridViewBizLogic.getSelectedColumnList(categorySearchForm, 
					selectedColumnsMetadata.getSelectedAttributeMetaDataList(), selectedColumnNames,
					queryResultObjecctDataMap, queryDetailsObj,outputTermsColumns,nodeData);
			String SqlForSelectedColumns = defineGridViewBizLogic.createSQLForSelectedColumn(selectedColumnNames.toString(), sql);
			spreadSheetDataMap.put(Constants.SPREADSHEET_COLUMN_LIST, definedColumnsList);
			querySessionData = queryOutputSpreadsheetBizLogic.getQuerySessionData(queryDetailsObj,
					recordsPerPage, 0, spreadSheetDataMap, SqlForSelectedColumns,
					queryResultObjecctDataMap, hasConditionOnIdentifiedField);
			selectedColumnNameValueBeanList = null;
			//session.setAttribute(Constants.DEFINE_VIEW_QUERY_REASULT_OBJECT_DATA_MAP, queryResultObjecctDataMap);
			spreadSheetDataMap.put(Constants.DEFINE_VIEW_QUERY_REASULT_OBJECT_DATA_MAP, queryResultObjecctDataMap);
			spreadSheetDataMap.put(Constants.QUERY_REASUL_OBJECT_DATA_MAP, queryResultObjecctDataMap);
		}
		spreadSheetDataMap.put(Constants.SPREADSHEET_COLUMN_LIST, definedColumnsList);
		selectedColumnsMetadata.setSelectedColumnNameValueBeanList(selectedColumnNameValueBeanList);
		selectedColumnsMetadata.setCurrentSelectedObject(currentSelectedObject);
		spreadSheetDataMap.put(Constants.QUERY_SESSION_DATA, querySessionData);
		spreadSheetDataMap.put(Constants.SELECTED_COLUMN_META_DATA,selectedColumnsMetadata);
		spreadSheetDataMap.put(Constants.MAIN_ENTITY_MAP, queryDetailsObj.getMainEntityMap());
		QueryModuleUtil.setGridData(request, spreadSheetDataMap);
		return mapping.findForward(Constants.SUCCESS);
	}

	private String getClickedNodeData(String sql) {
		
		int index = sql.lastIndexOf('=');
		String clickedData = "";
		if(index != -1)
		{
			clickedData = sql.substring(index +1,sql.length());
		}
		return clickedData;
	}
}
