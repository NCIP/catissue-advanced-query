
package edu.wustl.query.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.client.metadatasearch.MetadataSearch;
import edu.wustl.cab2b.common.beans.MatchedClass;
import edu.wustl.cab2b.common.util.Constants;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.query.actionForm.CategorySearchForm;
import edu.wustl.query.util.querysuite.EntityCacheFactory;
import edu.wustl.query.util.querysuite.QueryModuleUtil;

/**
 * This class loads screen for categorySearch.
 * When search button is clicked it checks for the input : Text , checkbox , radiobutton etc. And depending upon the selections made by user,
 * the list of entities is populated. This list is kept in session.
 * @author deepti_shelar
 */

public class CategorySearchAction extends Action
{

	/**
	 * This method loads the data required for categorySearch.jsp
	 * @param mapping mapping
	 * @param form form
	 * @param request request
	 * @param response response
	 * @throws Exception Exception
	 * @return ActionForward actionForward
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String isQuery = request.getParameter("isQuery");
		if (isQuery != null)
		{
			request.setAttribute("isQuery", isQuery);
		}
		else
		{
			request.setAttribute("isQuery", "false");
		}
		CategorySearchForm searchForm = (CategorySearchForm) form;
		String currentPage = searchForm.getCurrentPage();
		if (currentPage != null && currentPage.equalsIgnoreCase("resultsView"))
		{
			searchForm = QueryModuleUtil.setDefaultSelections(searchForm);
			return mapping.findForward(edu.wustl.query.util.global.Constants.SUCCESS);
		}
		String textfieldValue = searchForm.getTextField();
		if (currentPage != null && currentPage.equalsIgnoreCase("prevToAddLimits"))
		{
			textfieldValue = "";
		}
		if (textfieldValue != null && !textfieldValue.equals(""))
		{
			int[] searchTarget = prepareSearchTarget(searchForm);

			/* 
			 * Bug #5131 : Disabling function call and supplying value directly
			 * until the Concept-Code search is fixed
			 */
			// int basedOn = prepareBaseOn(searchForm.getSelected());
			int basedOn = Constants.BASED_ON_TEXT;
			Set<EntityInterface> entityCollection = new HashSet<EntityInterface>();
			String[] searchString = null;
			searchString = prepareSearchString(textfieldValue);
			String attributeChecked = searchForm.getAttributeChecked();
			String permissibleValuesChecked = searchForm.getPermissibleValuesChecked();
			String entitiesString = "";
			EntityCache cache = EntityCacheFactory.getInstance();
			MetadataSearch advancedSearch = new MetadataSearch(cache);
			MatchedClass matchedClass = advancedSearch.search(searchTarget, searchString, basedOn);
			entityCollection = matchedClass.getEntityCollection();
			List resultList = new ArrayList(entityCollection);
			for (int i = 0; i < resultList.size(); i++)
			{
				EntityInterface entity = (EntityInterface) resultList.get(i);
				String fullyQualifiedEntityName = entity.getName();
				String entityName = Utility.parseClassName(fullyQualifiedEntityName);
				entityName = Utility.getDisplayLabel(entityName);
				String entityId = entity.getId().toString();
				String description = entity.getDescription();
				entitiesString = entitiesString
						+ edu.wustl.query.util.global.Constants.ENTITY_SEPARATOR + entityName
						+ edu.wustl.query.util.global.Constants.ATTRIBUTE_SEPARATOR + entityId
						+ edu.wustl.query.util.global.Constants.ATTRIBUTE_SEPARATOR + description;
			}

			if (entitiesString.equals(""))
			{
				entitiesString = ApplicationProperties.getValue("query.noResultFoundMessage");
			}
			else
			{
				String KeySeparator = edu.wustl.query.util.global.Constants.KEY_CODE;
				String key = request.getParameter(KeySeparator);
				entitiesString = entitiesString
						+ edu.wustl.query.util.global.Constants.KEY_SEPARATOR + key;
				entitiesString = entitiesString
						+ edu.wustl.query.util.global.Constants.KEY_SEPARATOR + textfieldValue;
				entitiesString = entitiesString
						+ edu.wustl.query.util.global.Constants.KEY_SEPARATOR + attributeChecked;
				entitiesString = entitiesString
						+ edu.wustl.query.util.global.Constants.KEY_SEPARATOR
						+ permissibleValuesChecked;
			}
			response.setContentType("text/html");
			response.getWriter().write(entitiesString);
			return null;
		}
		return mapping.findForward(edu.wustl.query.util.global.Constants.SUCCESS);
	}

	/**
	 * Prepares a String to be sent to AdvancedSearch logic.
	 * @param textfieldValue String
	 * @return String[] array of strings , taken from user. 
	 */
	private String[] prepareSearchString(String textfieldValue)
	{
		int counter = 0;
		StringTokenizer tokenizer = new StringTokenizer(textfieldValue);
		String[] searchString = new String[tokenizer.countTokens()];
		while (tokenizer.hasMoreTokens())
		{
			searchString[counter] = tokenizer.nextToken();
			counter++;
		}
		return searchString;
	}

	/**
	 * Returns a int constant for radil option selected by user which represents Based on.
	 * @param basedOnStr String
	 * @return int basedOn
	 */
	private int prepareBaseOn(String basedOnStr)
	{
		int basedOn = Constants.BASED_ON_TEXT;
		if (basedOnStr != null)
		{
			if (basedOnStr.equalsIgnoreCase("conceptCode_radioButton"))
			{
				basedOn = Constants.BASED_ON_CONCEPT_CODE;
			}
		}
		return basedOn;
	}

	/**
	 * Prepares the int [] for search targets from the checkbox values selected by user.
	 * @param searchForm action form
	 * @return int[] Integer array of selections made by user.
	 */
	private int[] prepareSearchTarget(CategorySearchForm searchForm)
	{
		String classCheckBoxChecked = searchForm.getClassChecked();
		String attributeCheckBoxChecked = searchForm.getAttributeChecked();
		String permissiblevaluesCheckBoxChecked = searchForm.getPermissibleValuesChecked();
		String includeDescriptionChecked = searchForm.getIncludeDescriptionChecked();
		List<Integer> target = new ArrayList<Integer>();
		if (classCheckBoxChecked != null
				&& (classCheckBoxChecked.equalsIgnoreCase(edu.wustl.query.util.global.Constants.ON) || classCheckBoxChecked
						.equalsIgnoreCase("true")))
		{
			if (includeDescriptionChecked != null
					&& (includeDescriptionChecked.equalsIgnoreCase(edu.wustl.query.util.global.Constants.ON) || includeDescriptionChecked
							.equalsIgnoreCase("true")))
			{
				target.add(Integer.valueOf((Constants.CLASS_WITH_DESCRIPTION)));
			}
			else
			{
				target.add(Integer.valueOf((Constants.CLASS)));
			}
		}
		if (attributeCheckBoxChecked != null
				&& (attributeCheckBoxChecked.equalsIgnoreCase(edu.wustl.query.util.global.Constants.ON) || attributeCheckBoxChecked
						.equalsIgnoreCase("true")))
		{
			if (includeDescriptionChecked != null
					&& (includeDescriptionChecked.equalsIgnoreCase(edu.wustl.query.util.global.Constants.ON) || includeDescriptionChecked
							.equalsIgnoreCase("true")))
			{
				target.add(Integer.valueOf((Constants.ATTRIBUTE_WITH_DESCRIPTION)));
			}
			else
			{
				target.add(Integer.valueOf((Constants.ATTRIBUTE)));
			}
		}
		if (permissiblevaluesCheckBoxChecked != null
				&& (permissiblevaluesCheckBoxChecked.equalsIgnoreCase(edu.wustl.query.util.global.Constants.ON) || permissiblevaluesCheckBoxChecked
						.equalsIgnoreCase("true")))
		{
			target.add(Integer.valueOf((Constants.PV)));
		}
		int[] searchTarget = new int[target.size()];
		for (int i = 0; i < target.size(); i++)
		{
			searchTarget[i] = ((target.get(i))).intValue();
		}
		return searchTarget;
	}

}