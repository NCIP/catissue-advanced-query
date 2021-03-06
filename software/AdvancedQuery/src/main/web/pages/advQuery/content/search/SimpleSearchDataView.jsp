<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/PagenationTag.tld" prefix="custom"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Hashtable"%>
<%@ page import="edu.wustl.query.actionForm.QueryAdvanceSearchForm"%>
<%@ page import="edu.wustl.query.util.global.AQConstants"%>
<%@ page import="edu.wustl.query.util.global.Utility"%>
<%@ page import="edu.wustl.query.util.global.Variables"%>
<%@ page language="java" isELIgnored="false"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script src="jss/advQuery/script.js"></script>
<script type="text/javascript" src="jss/advQuery/ajax.js"></script>
<style>
.active-column-0 {
	width: 30px
}

tr#hiddenCombo {
	display: none;
}
</style>
<head>
<%
	int pageNum = Integer.parseInt((String) request
			.getAttribute(AQConstants.PAGE_NUMBER));

	int totalResults = ((Integer) session
			.getAttribute(AQConstants.TOTAL_RESULTS)).intValue();
	int numResultsPerPage = Integer.parseInt((String) session
			.getAttribute(AQConstants.RESULTS_PER_PAGE));
	String pageName = "SpreadsheetView.do";
	String checkAllPages = (String) session
			.getAttribute("checkAllPages");
	QueryAdvanceSearchForm form = (QueryAdvanceSearchForm) session
			.getAttribute("advanceSearchForm");
	List columnList = (List) session
			.getAttribute(AQConstants.SPREADSHEET_COLUMN_LIST);
	if (columnList == null)
		columnList = (List) request
				.getAttribute(AQConstants.SPREADSHEET_COLUMN_LIST);

	columnList.add(0, " ");
	List dataList = (List) request
			.getAttribute(AQConstants.PAGINATION_DATA_LIST);

	String pageOf = (String) request.getAttribute(AQConstants.PAGEOF);
	String title = pageOf + ".searchResultTitle";
	boolean isSpecimenData = false;
	int IDCount = 0;
%>


<script language="javascript">
		var colZeroDir='ascending';

		function getData()
		{	//ajax call to get update data from server
			var request = newXMLHTTPReq();
			var handlerFunction = getReadyStateHandler(request,displayValidationMessage,true);
			request.onreadystatechange = handlerFunction;
			var actionURL = "updateSessionData=updateSessionData";
			var url = "ValidateQuery.do";
			request.open("POST",url,true);
			request.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
			request.send(actionURL);
		}

		function displayValidationMessage(message)
		{
			//var message contains space " " if the message is not to be shown.
			if (message != null && message == " ") 		// do not show popup
			{
				onAddToCart();
			}
			else
			{
				var isChecked = updateHiddenFields(); // if atleast one check box is checked.
				if (isChecked == "true")
				{
					var r=confirm(message);
					if (r==true)
					{
						onAddToCart();
					}
				}
				else
				{
					alert("Please select at least one checkbox");
				}
			}
		}

		function onAddToCart()
		{
			var isChecked = updateHiddenFields();
			var chkBox = document.getElementById('checkAll');
			var isCheckAllAcrossAllChecked = chkBox.checked;

		    if(isChecked == "true")
		    {
			     var pageNum = "<%=pageNum%>";
				 var action;
                 var isQueryModule = "<%=pageOf.equals(AQConstants.PAGEOF_QUERY_MODULE)%>";
                 <%if (pageOf.equals(AQConstants.PAGEOF_QUERY_MODULE)) {%>

				  action = "AddDeleteCart.do?operation=add&pageNum="+pageNum+"&isCheckAllAcrossAllChecked="+isCheckAllAcrossAllChecked;
				   document.forms[0].target = "gridFrame";
				<%} else {%>
			     action = "ShoppingCart.do?operation=add&pageNum="+pageNum+"&isCheckAllAcrossAllChecked="+isCheckAllAcrossAllChecked ;
				 document.forms[0].target = "myframe1";
				<%}%>
				document.forms[0].operation.value="add";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}

		function onExport()
		{
			var isChecked = updateHiddenFields();
			var pageNum = "<%=pageNum%>";
			var chkBox = document.getElementById('checkAll');
			var isCheckAllAcrossAllChecked = chkBox.checked;
		    if(isChecked == "true")
		    {
				var action = "SpreadsheetExport.do?pageNum="+pageNum+"&isCheckAllAcrossAllChecked="+isCheckAllAcrossAllChecked ;
				document.forms[0].operation.value="export";
				document.forms[0].action = action;
				document.forms[0].target = "_blank";
				document.forms[0].submit();
			}
			else
			{
				alert("Please select at least one checkbox");
			}
		}
		//function that is called on click of Define View button for the configuration of search results
		function onSimpleConfigure()
		{
				action="ConfigureSimpleQuery.do?pageOf=pageOfSimpleQueryInterface";
				document.forms[0].action = action;
				document.forms[0].target = "_parent";
				document.forms[0].submit();
		}

		function onAdvanceConfigure()
		{
				action="ConfigureAdvanceSearchView.do?pageOf=pageOfQueryResults";
				document.forms[0].action = action;
				document.forms[0].target = "myframe1";
				document.forms[0].submit();
		}
		function onQueryResultsConfigure()
		{
			 action="DefineQueryResultsView.do?pageOf=pageOfQueryModule";
			 document.forms[0].action = action;
			 document.forms[0].target = "<%=AQConstants.GRID_DATA_VIEW_FRAME%>";
			 document.forms[0].submit();
		}
		function onRedefineSimpleQuery()
		{
			action="SimpleQueryInterface.do?pageOf=pageOfSimpleQueryInterface&operation=redefine";
			document.forms[0].action = action;
			document.forms[0].target = "_parent";
			document.forms[0].submit();
		}
		function onRedefineAdvanceQuery()
		{
			action="AdvanceQueryInterface.do?pageOf=pageOfAdvanceQueryInterface&operation=redefine";
			document.forms[0].action = action;
			document.forms[0].target = "_parent";
			document.forms[0].submit();
		}
		function onRedefineDAGQuery()
		{
			waitCursor();
			document.forms[0].action='SearchCategory.do?currentPage=resultsView';
			document.forms[0].target = "_parent";
			document.forms[0].submit();
			hideCursor();
		}
		var selected;

		function addCheckBoxValuesToArray(checkBoxName)
		{
			var theForm = document.forms[0];
		    selected=new Array();

		    for(var i=0,j=0;i<theForm.elements.length;i++)
		    {
		 	  	if(theForm[i].type == 'checkbox' && theForm[i].checked==true)
			        selected[j++]=theForm[i].value;
			}
		}

        //Commented out By Baljeet...

		function callAction(action)
		{
			document.forms[0].action = action;
			document.forms[0].submit();
		}
		function setCheckBoxState()
		{
		   if(document.getElementById('checkAll'))
		   {
			var chkBox = document.getElementById('checkAll');
			var isCheckAllAcrossAllChecked = chkBox.checked;
		<%if (checkAllPages != null && checkAllPages.equals("true")) {%>
			chkBox.checked = true;
				rowCount = mygrid.getRowsNum();
				for(i=1;i<=rowCount;i++)
				{
					var cl = mygrid.cells(i,0);
					if(cl.isCheckbox())
					cl.setChecked(true);
				}
		<%}%>
		}
		}
//this function is called after executing ajax call from checkAllOnThisPage function.
function checkAllOnThisPageResponse()
{
}

//document.forms[0].checkAllPages.value = true;

	</script>
<%
	String configAction = new String();
	String redefineQueryAction = new String();
	if (pageOf.equals(AQConstants.PAGEOF_SIMPLE_QUERY_INTERFACE)) {
		configAction = "onSimpleConfigure()";
		redefineQueryAction = "onRedefineSimpleQuery()";
	} else if (pageOf.equals("pageOfQueryModule")) {
		configAction = "onQueryResultsConfigure()";
		redefineQueryAction = "onRedefineDAGQuery()";
	} else {
		configAction = "onAdvanceConfigure()";
		redefineQueryAction = "onRedefineAdvanceQuery()";
	}
	boolean mac = false;
	Object os = request.getHeader("user-agent");
	if (os != null && os.toString().toLowerCase().indexOf("mac") != -1) {
		mac = true;
	}
	String height = "100%";
	if (mac) {
		/* mac gives problem if the values aer specified in percentage*/
		height = "500";
	}
%>
<!-- Mandar : 434 : for tooltip -->
<script language="JavaScript" type="text/javascript"
	src="jss/advQuery/javaScript.js"></script>
</head>
<body onload="setCheckBoxState()">

<!-------new--->
<!--Prafull:Added errors tag inside the table-->
<table width="100%" border="0" cellpadding="0" cellspacing="0" class=""
	height="100%">
	<tr height="95%">
		<td class=""><logic:equal name="pageOf"
			value="<%=AQConstants.PAGEOF_SIMPLE_QUERY_INTERFACE%>">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class=""><span class=""> Simple Query </span></td>
					<td><img
						src="images/advQuery/uIEnhancementImages/table_title_corner2.gif"
						alt="Page Title - Search Results" width="31" height="24"
						hspace="0" vspace="0" /></td>
				</tr>
			</table>
		</logic:equal></td>
	</tr>
	<tr>
		<td class="">

		<table width="100%" border="0" cellpadding="0" cellspacing="0"
			class="" height="100%">

			<tr height="1%">
				<td align="left"><%@ include
					file="/pages/advQuery/content/common/ActionErrors.jsp"%></td>
			</tr>
			<!------new--->
			<!--<table summary="" cellpadding="0" cellspacing="0" border="0" width="99%" height="100%" style="overflow:auto;">
<tr>
	<td >

	</td>
</tr>-->
			<html:form action="QueryWizard.do" style="margin:0;padding:0;">
				<html:hidden property="checkAllPages" value="" />

				<%
					if (dataList == null
								&& pageOf.equals(AQConstants.PAGEOF_QUERY_RESULTS)) {
				%>
				<bean:message key="advanceQuery.noRecordsFound" />
				<%
					} else if (dataList != null && dataList.size() != 0) {
				%>
				<!--
			Patch ID: Bug#3090_28
			Description: The width of <td> are adjusted to fit into the iframe.
			These changes were made to remove the extra white space on the data view/spreadsheet view page.
		-->
				<%--<tr height="3%">
			 <td  class="formTitle" width="100%">
				<bean:message key="<%=title%>"/>
			 </td>
		</tr>--%>

				<tr>
					<td class="black_ar"><custom:test name=""
						pageNum="<%=pageNum%>" totalResults="<%=totalResults%>"
						numResultsPerPage="<%=numResultsPerPage%>"
						pageName="<%=pageName%>" showPageSizeCombo="<%=true%>"
						recordPerPageList="<%=AQConstants.RESULT_PERPAGE_OPTIONS%>" /> <html:hidden
						property="<%=AQConstants.PAGEOF%>" value="<%=pageOf%>" /> <html:hidden
						property="isPaging" value="true" /></td>
				</tr>
				<%
					if (pageOf.equals(AQConstants.PAGEOF_QUERY_RESULTS)) {
								String[] selectedColumns = form
										.getSelectedColumnNames();
				%>

				<tr id="hiddenCombo" rowspan="2" height="2%">
					<td class="black_new"><!-- Mandar : 434 : for tooltip --> <html:select
						property="selectedColumnNames" styleClass="selectedColumnNames"
						size="1" styleId="selectedColumnNames" multiple="true"
						onmouseover="showTip(this.id)" onmouseout="hideTip(this.id)">
						<%
							for (int j = 0; j < selectedColumns.length; j++) {
						%>
						<html:option value="<%=selectedColumns[j]%>"><%=selectedColumns[j]%></html:option>
						<%
							}
						%>
					</html:select></td>
				</tr>
				<%
					}
				%>

				<tr valign="top" width="100%" height="2%">
					<td width="100%" valign="top"><!--  **************  Code for New Grid  *********************** -->
					<script>
					/*
						to be used when you want to specify another javascript function for row selection.
						useDefaultRowClickHandler =1 | any value other than 1 indicates you want to use another row click handler.
						useFunction = "";  Function to be used.
					*/
					var useDefaultRowClickHandler =1;
					var useFunction = "search";
				</script> <%@ include file="/pages/advQuery/content/search/AdvanceGrid.jsp"%>

					<!--  **************  Code for New Grid  *********************** -->
					</td>
				</tr>

				<tr width="100%" valign="top" height="95%">

					<td width="90%">

					<table summary="" cellpadding="0" cellspacing="0" border="0"
						width="100%" valign="top" height="100%">
						<tr height="100%">
							<td width="5%" nowrap valign="top" class="black_ar"><input
								type='checkbox' name='checkAll2' id='checkAll2'
								onClick='checkAllOnThisPage(this)'> <span valign="top"
								class="black_ar"><bean:message
								key="buttons.checkAllOnThisPage" /></span> <input type='checkbox'
								name='checkAll' id='checkAll'
								onClick='checkAllAcrossAllPages(this)'> <span
								valign="top" class="black_ar"><bean:message
								key="buttons.checkAll" /></span></td>
							<%
								//Commented out By Baljeet....
										//Object obj = session.getAttribute(Constants.SPECIMENT_VIEW_ATTRIBUTE);
										//boolean isDefaultView = (obj!=null);
										boolean isDefaultView = true;
							%>
							<td width="5%" valign="top">
							<%
								if (pageOf.equals(AQConstants.PAGEOF_QUERY_RESULTS)) {
							%> <!--Commented out by Baljeet as it is catissuecore specific -->
							<!--<input type='checkbox' <%if (isDefaultView) {%>checked='checked' <%}%>name='checkDefaultSpecimenView' id='checkDefaultSpecimenView' onClick='setDefaultView(this)'>
						<span class="black_ar"><bean:message key="buttons.defaultSpecimenView" /></span>&nbsp; -->
							<%
								} else {
							%> &nbsp; <%
 	}
 %>
							</td>
							<td width="10%" align="right" valign="top">&nbsp;</td>
							<td width="5%" nowrap align="right" valign="top">
							<%
								if (Utility
												.checkFeatureUsage(AQConstants.FEATURE_ADD_TO_LIST)
												&& (pageOf.equals(AQConstants.PAGEOF_QUERY_RESULTS) || pageOf
														.equals(AQConstants.PAGEOF_QUERY_MODULE))) {
							%> <img src="images/advQuery/b_add_list.gif" id="addToListImgId"
								width="100" hspace="3" onclick="getData()" />&nbsp; <%
 	} else {
 %> &nbsp; <%
 	}
 %>
							</td>
							<td width="5%" nowrap align="right" valign="top"><img
								src="images/advQuery/b_exp.gif" id="exportImgId" width="57"
								hspace="3" onclick="onExport()" />&nbsp;</td>
							<td width="5%" nowrap align="right" valign="top"><img
								src="images/advQuery/b_def_view.gif" id="defineViewId"
								width="88" hspace="3" onclick="<%=configAction%>" />&nbsp;</td>
							<td width="5%" nowrap align="right" valign="top"><img
								src="images/advQuery/b_red_query.gif" id="redefineQueryId"
								width="107" hspace="3" onclick="<%=redefineQueryAction%>" />&nbsp;
							</td>
						</tr>
					</table>

					</td>
				</tr>
				<%
					}
				%>

				<tr>
					<td><html:hidden property="operation" value="" /></td>
				</tr>
				<input type="hidden" name="isQuery" value="true">
			</html:form>
		</table>
		</td>
	</tr>
</table>