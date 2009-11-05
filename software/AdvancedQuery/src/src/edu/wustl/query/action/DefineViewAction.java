
package edu.wustl.query.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.query.actionforms.CategorySearchForm;
import edu.wustl.query.bizlogic.DefineGridViewBizLogic;
import edu.wustl.query.util.global.Constants;
import edu.wustl.query.util.querysuite.QueryDetails;

public class DefineViewAction extends AbstractQueryBaseAction
{

	protected ActionForward executeBaseAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		CategorySearchForm categorysearchform = (CategorySearchForm) form;
		String[] selectedColumnNames = categorysearchform.getSelectedColumnNames();
		categorysearchform.setSelectedColumnNames(selectedColumnNames);
		HttpSession session = request.getSession();
		String isworkflow = request.getParameter(Constants.IS_WORKFLOW);
		String pageOf = request.getParameter(Constants.PAGE_OF);
		request.setAttribute(Constants.IS_WORKFLOW, isworkflow);
		request.setAttribute(Constants.PAGE_OF, pageOf);
		QueryDetails queryDetails = new QueryDetails(session);
		SelectedColumnsMetadata selectedColumnsMetadata = new SelectedColumnsMetadata();
		DefineGridViewBizLogic defineGridViewBizLogic = new DefineGridViewBizLogic();
		defineGridViewBizLogic.getSelectedColumnsMetadata(categorysearchform
				.getSelectedColumnNames(), queryDetails, selectedColumnsMetadata);
		session.setAttribute(Constants.SELECTED_COLUMN_META_DATA, selectedColumnsMetadata);
		return mapping.findForward(Constants.SUCCESS);
	}
}
