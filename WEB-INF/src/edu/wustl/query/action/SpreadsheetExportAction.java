/**
 * <p>Title: SpreadsheetExportAction Class>
 * <p>Description:	This class exports the data of a spreadsheet to a file.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Aniruddha Phadnis
 * @version 1.00
 * Created on Oct 24, 2005
 */

package edu.wustl.query.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.query.actionForm.AdvanceSearchForm;
import edu.wustl.query.util.global.Constants;
import edu.wustl.query.util.global.Utility;
import edu.wustl.query.util.global.Variables;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.dao.QuerySessionData;
import edu.wustl.common.util.ExportReport;
import edu.wustl.common.util.SendFile;
import edu.wustl.common.util.logger.Logger;

/**
 * @author aniruddha_phadnis
 */
public class SpreadsheetExportAction  extends Action
{
    /**
     * This class exports the data of a spreadsheet to a file.
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
    	AdvanceSearchForm searchForm = (AdvanceSearchForm)form;
    	HttpSession session = request.getSession();
    	String path = Variables.applicationHome + System.getProperty("file.separator");
		String csvfileName = path + Constants.SEARCH_RESULT;
		String zipFileName = path + session.getId() + Constants.ZIP_FILE_EXTENTION;
		String fileName = path + session.getId() + Constants.CSV_FILE_EXTENTION;
    	String isCheckAllAcrossAllChecked = (String)request.getParameter(Constants.CHECK_ALL_ACROSS_ALL_PAGES);
    	
    	//Extracting map from formbean which gives the serial numbers of selected rows
    	Map map = searchForm.getValues();
    	Object [] obj = map.keySet().toArray();
    	
    	//Getting column data & grid data from session
    	List<String> columnList = (List<String>)session.getAttribute(Constants.SPREADSHEET_COLUMN_LIST);
    	/**
		 * Name: Deepti
		 * Description: Query performance issue. Instead of saving complete query results in session, resultd will be fetched for each result page navigation.
		 * object of class QuerySessionData will be saved in session, which will contain the required information for query execution while navigating through query result pages.
		 * 
		 * Here, as results are not stored in session, the sql is fired again to form the shopping cart list.  
		 */
    	String pageNo = (String)request.getParameter(Constants.PAGE_NUMBER);
	    String recordsPerPageStr = (String)session.getAttribute(Constants.RESULTS_PER_PAGE);//Integer.parseInt(XMLPropertyHandler.getValue(Constants.NO_OF_RECORDS_PER_PAGE));
	    if(pageNo != null)
	    {
	    	request.setAttribute(Constants.PAGE_NUMBER,pageNo);
	    }
    	int recordsPerPage = new Integer(recordsPerPageStr);
		int pageNum = new Integer(pageNo);
		if(isCheckAllAcrossAllChecked != null && isCheckAllAcrossAllChecked.equalsIgnoreCase("true"))
    	{
			Integer totalRecords = (Integer)session.getAttribute(Constants.TOTAL_RESULTS);
			recordsPerPage = totalRecords;
			pageNum = 1;
    	}
		
		QuerySessionData querySessionData = (QuerySessionData)session.getAttribute(edu.wustl.common.util.global.Constants.QUERY_SESSION_DATA);
        List dataList1 = Utility.getPaginationDataList(request, getSessionData(request), recordsPerPage, pageNum, querySessionData);
        List<List<String>> dataList = (List<List<String>>) session.getAttribute(Constants.EXPORT_DATA_LIST);
        if(dataList == null )
        	dataList = dataList1;
        //List<String> entityIdsList = (List<String>) session.getAttribute(Constants.ENTITY_IDS_LIST);
        Map<Integer, List<String>> entityIdsMap = (Map<Integer, List<String>>)session.getAttribute(Constants.ENTITY_IDS_MAP);
    	//Mandar 06-Apr-06 Bugid:1165 : Extra ID columns displayed.  start
    	
    	Logger.out.debug("---------------------------------------------------------------------------------");
       	Logger.out.debug("06-apr-06 : cl size :-"+columnList.size()  );
    	Logger.out.debug(columnList); 
    	Logger.out.debug("--");
    	Logger.out.debug("06-apr-06 : dl size :-"+dataList.size()  );
    	Logger.out.debug(dataList); 
    	
    	List tmpColumnList = new ArrayList();
    	int idColCount=0;
    	// count no. of ID columns
    	for(int cnt=0;cnt<columnList.size();cnt++  )
    	{
    		String columnName = (String)columnList.get(cnt );
    		Logger.out.debug(columnName + " : " + columnName.length()    );
    		if(columnName.trim().equalsIgnoreCase("ID") )
    		{
    			idColCount++; 
    		}
    	}
    	// remove ID columns
    	for(int cnt=0;cnt<(columnList.size()-idColCount) ;cnt++  )
    	{
    		tmpColumnList.add(columnList.get(cnt)  ); 
    	}
    	
    	// datalist filtration for ID data.
    	List tmpDataList = new ArrayList();
    	for(int dataListCnt=0; dataListCnt<dataList.size(); dataListCnt++    )
    	{
    		List tmpList = (List) dataList.get(dataListCnt);
    		List tmpNewList = new ArrayList();
        	for(int cnt=0;cnt<(tmpList.size()-idColCount) ;cnt++  )
        	{
        		tmpNewList.add(tmpList.get(cnt)  ); 
        	}
        	tmpDataList.add(tmpNewList ); 
    	}
    	
    	Logger.out.debug("--");
    	Logger.out.debug("tmpcollist :" + tmpColumnList.size() ); 
    	Logger.out.debug(tmpColumnList); 
    	Logger.out.debug("--");
    	Logger.out.debug("tmpdatalist :" + tmpDataList.size() ); 
    	Logger.out.debug(tmpDataList); 

    	Logger.out.debug("---------------------------------------------------------------------------------");
    	columnList = tmpColumnList ;
    	dataList = tmpDataList ;
    	//    	Mandar 06-Apr-06 Bugid:1165 : Extra ID columns end  
    	
    	List<List<String>> exportList = new ArrayList();
    	
    	//Adding first row(column names) to exportData
    	exportList.add(columnList);
    	List<String> idIndexList = new ArrayList<String>();
    	int columnsSize = columnList.size();
    	if(isCheckAllAcrossAllChecked != null && isCheckAllAcrossAllChecked.equalsIgnoreCase("true"))
    	{
    		for(int i=0;i<dataList.size();i++)
        	{
        		List<String> list = dataList.get(i);
        		List<String> subList = list.subList(0,columnsSize);
				exportList.add(subList);
				if(entityIdsMap != null && !entityIdsMap.isEmpty())
		    	{
					List<String> entityIdList = entityIdsMap.get(i);
					idIndexList.addAll(entityIdList);
	    		}
        	}
    	}
    	else
    	{
	    	for(int i=0;i<obj.length;i++)
	    	{
	    		int indexOf = obj[i].toString().indexOf("_") + 1;
	    		int index = Integer.parseInt(obj[i].toString().substring(indexOf));
	    		List<String> list = dataList.get(index);
	    		List<String> subList = list.subList(0,columnsSize);
	    		if(entityIdsMap != null && !entityIdsMap.isEmpty())
	    		{
		    		List<String> entityIdList = entityIdsMap.get(index);
		    		idIndexList.addAll(entityIdList);
	    		}
	    		exportList.add(subList);
	    	}
    	}
    	String delimiter = Constants.DELIMETER; 
    	ExportReport report = null;
    	//Exporting the data to the given file & sending it to user
    	if (entityIdsMap != null && !entityIdsMap.isEmpty())
    	{
    		report = new ExportReport(path,csvfileName,zipFileName);
    		report.writeDataToZip(exportList,delimiter,idIndexList);
    		SendFile.sendFileToClient(response,zipFileName,Constants.EXPORT_ZIP_NAME,"application/download");
    	}
    	else
    	{
    		report = new ExportReport(fileName);
    		report.writeData(exportList,delimiter);   	
    		report.closeFile();
        	SendFile.sendFileToClient(response,fileName,Constants.SEARCH_RESULT,"application/download");
    	}    	
    	
    	return null;
    }
    
   
   
    //This method is added By Baljeet to get session data
    
    /**
     * This method returns the session data bean
     * @param request
     * @return
     */
    private SessionDataBean getSessionData(HttpServletRequest request)
	{
		Object obj = request.getSession().getAttribute(Constants.SESSION_DATA);
		SessionDataBean sessionData = null;
		if (obj != null)
		{
			sessionData = (SessionDataBean) obj;
			return sessionData;
		}
		return sessionData;
	}
}