
package edu.wustl.query.htmlprovider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domain.DateAttributeTypeInformation;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.AttributeTypeInformationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.wustl.cab2b.common.exception.CheckedException;
import edu.wustl.cab2b.common.util.AttributeInterfaceComparator;
import edu.wustl.cab2b.common.util.PermissibleValueComparator;
import edu.wustl.common.query.factory.PermissibleValueManagerFactory;
import edu.wustl.common.query.pvmanager.IPermissibleValueManager;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IParameter;
import edu.wustl.common.util.ParseXMLFile;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.query.util.global.Constants;

/**
 * This class generates UI for 'Add Limits' and 'Edit Limits' section.
 * @author deepti_shelar
 */
public class HtmlProvider
{
	/**
	 * Object which holds data operators for attributes.
	 */
	private static ParseXMLFile parseFile = null;
	/**
	 *
	 */
	private int expressionId = -1;

	/**
	 * list of attributes.
	 */
	private String attributesList = "";

	/**
	 *
	 */
	private GenerateHTMLDetails generateHTMLDetails;

	/**
	 * attribute details for each attribute.
	 */
	private AttributeDetails attributeDetails;
	/**
	 * 
	 */
	private EntityInterface entity;
	/**
	 * 
	 */
	Map<String,AttributeInterface> enumratedAttributeMap= new HashMap<String,AttributeInterface>();
	/**
	 * 
	 */
	/**
	 *	For page set to SAVE_QUERY/ADD_EDIT/EXECUTE_QUERY depending upon
	 *  which page the request has come from.
	 */
	private String forPage = "";
	/**
	 * @return the expressionId
	 */
	public int getExpressionId()
	{
		return expressionId;
	}

//	private String formName = "categorySearchForm";
	/**
	 * @param generateHTMLDetails the generateHTMLDetails to be set
	 */
	public HtmlProvider(GenerateHTMLDetails generateHTMLDetails)
	{
		this.generateHTMLDetails = new GenerateHTMLDetails();
		setGenerateHTMLDetails(generateHTMLDetails);
		if (parseFile == null)
		{
			try
			{
				parseFile = ParseXMLFile.getInstance(Constants.DYNAMIC_UI_XML);
			}
			catch (CheckedException e)
			{
				Logger.out.debug(e.getStackTrace());
			}
		}
	}
	/**
	 * @param generateHTMLDetails the GenerateHTMLDetails to be set
	 */
	private void setGenerateHTMLDetails(GenerateHTMLDetails generateHTMLDetails)
	{
		if(generateHTMLDetails==null)
		{
			this.generateHTMLDetails.setSearchString("");
			this.generateHTMLDetails.setAttributeChecked(false);
			this.generateHTMLDetails.setPermissibleValuesChecked(false);
		}
		else
		{
			this.generateHTMLDetails.setSearchString(generateHTMLDetails.getSearchString());
			this.generateHTMLDetails.setAttributeChecked(generateHTMLDetails.isAttributeChecked());
			this.generateHTMLDetails.
					setPermissibleValuesChecked
					(generateHTMLDetails.isPermissibleValuesChecked());
		}
	}

	/**
	 *
	 * @param expressionId the expressionId to set
	 */
	public void setExpressionId(int expressionId)
	{
		this.expressionId = expressionId;
	}
	/**
	 *
	 * @return
	 *//*
	public StringBuffer generateSaveQueryPreHTML()
	{
		StringBuffer generatedPreHTML = new StringBuffer(100);
		generatedPreHTML
				.append("<table border=\"0\" width=\"100%\"+
				 cellspacing=\"0\" cellpadding=\"0\">");
		return generatedPreHTML;
	}*/
	/**
	 * Generates component name for an attribute.
	 * @param attribute for which component name has to be generated
	 * @return component name.
	 */
	private String generateComponentName(AttributeInterface attribute)
	{
		StringBuffer componentId = new StringBuffer();
		String attributeName = "";
		if (getExpressionId() > -1)
		{
			componentId = componentId.append(getExpressionId() + "_");
		}
		else
		{
			attributeName = attribute.getName();
		}
		componentId = componentId.append(attributeName).append(attribute.getId().toString());
		return componentId.toString();

	}

	/**
	 * This method generates the html for Add Limits and Edit Limits section.
	 * This internally calls methods to generate other UI components like text, Calendar, Combobox etc.
	 * @param entity entity to be presented on UI.
	 * @param conditions List of conditions , These are required in case of edit limits,
	 * 		For adding linits this parameter is null
	 * @return String html generated for Add Limits section.
	 */
	public String generateHTML(EntityInterface entity, List<ICondition> conditions,GenerateHTMLDetails generateHTMLDetails)
	{
		boolean attributeChecked = this.generateHTMLDetails.isAttributeChecked();
		boolean permissibleValuesChecked = this.generateHTMLDetails.isPermissibleValuesChecked();
		this.entity=entity;
		Collection<AttributeInterface> attributeCollection = entity.getEntityAttributesForQuery();
		String nameOfTheEntity = entity.getName();
		String entityId = entity.getId().toString();
		StringBuffer entityName = new StringBuffer(Utility.parseClassName(nameOfTheEntity));
		entityName = new StringBuffer(Utility.getDisplayLabel(entityName.toString()));
		boolean isEditLimits = isEditLimits(conditions);
		StringBuffer generatedHTML = new StringBuffer();
		StringBuffer generatedPreHTML = new StringBuffer();
		String attributesStr = getAttributesString(attributeCollection);
		generatedPreHTML = GenerateHtml.getHtmlHeader
					(entityName.toString(),entityId,attributesStr,isEditLimits);
		generatedHTML = getHtmlAttributes(conditions, attributeChecked, permissibleValuesChecked,
				attributeCollection);
		if(generateHTMLDetails!=null)
		{
			generateHTMLDetails.setEnumratedAttributeMap(enumratedAttributeMap);
		}
		return generatedPreHTML.toString() + "####" + generatedHTML.toString();
	}

	/**
	 * Generates html for all the attributes of the entity.
	 * @param conditions list of conditions
	 * @param attributeChecked boolean
	 * @param permissibleValuesChecked boolean
	 * @param attributeCollection collection of attributes
	 * @return StringBuffer
	 */
	private StringBuffer getHtmlAttributes(List<ICondition> conditions, boolean attributeChecked,
			boolean permissibleValuesChecked, Collection<AttributeInterface> attributeCollection)
	{
		StringBuffer generatedHTML = new StringBuffer(Constants.MAX_SIZE);
		String space = " ";
		generatedHTML
				.append("<table valign='top' border=\"0\" width=\"100%\" " +
						"height=\"100%\" cellspacing=\"0\" cellpadding=\"0\" " +
						"class='rowBGWhiteColor'>");
		boolean isBGColor = false;
		GenerateHtml.getTags(generatedHTML);
		if (!attributeCollection.isEmpty())
		{
			List<AttributeInterface> attributes =
				new ArrayList<AttributeInterface>(attributeCollection);
			Collections.sort(attributes, new AttributeInterfaceComparator());
			for (int i = 0; i < attributes.size(); i++)
			{
				AttributeInterface attribute = (AttributeInterface) attributes.get(i);
				if(HtmlUtility.isAttrNotSearchable(attribute))
				{
					continue;
				}
				getAttributeDetails(attribute,conditions,null);
				StringBuffer attrLabel = new StringBuffer
					(Utility.getDisplayLabel(attributeDetails.getAttrName()));
				boolean isBold = checkAttributeBold(attributeChecked, permissibleValuesChecked,
						attribute, attributeDetails.getAttrName());
				if(isBold)
				{
					attrLabel = GenerateHtml.getBoldLabel(attrLabel.toString());
				}
				String componentId = generateComponentName(attribute);
				attributesList = attributesList + ";" + componentId;
				isBGColor = GenerateHtml.getAlternateCss(generatedHTML, isBGColor, componentId,isBold);
				generatedHTML.append(attrLabel).append(space);
				GenerateHtml.getDateFormat(generatedHTML, isBold, attribute);
				generatedHTML.append(":&nbsp;&nbsp;&nbsp;&nbsp;</td>\n");
				generateHTMLForConditions(generatedHTML,attribute);
				generatedHTML.append("\n</tr>");
			}
		}
		GenerateHtml.getTags(generatedHTML);
		generatedHTML.append("</table>");
		return generatedHTML;
	}
	/**
	 * Gets attribute details for each attribute.
	 * @param attribute details to be set of this attribute
	 * @param conditions list of conditions
	 * @param parameterList list of parameters
	 */
	private void getAttributeDetails(AttributeInterface attribute,
			List<ICondition> conditions,List<IParameter<?>> parameterList)
	{
		this.attributeDetails = new AttributeDetails();
		attributeDetails.setAttrName(attribute.getName());
		attributeDetails.setOperatorsList(HtmlUtility.getConditionsList(attribute,parseFile));
		if(!attributeDetails.getOperatorsList().isEmpty())
		{
			attributeDetails.setBetween(
					GenerateHtml.checkBetweenOperator(
							attributeDetails.getOperatorsList().get(0)));
		}
		attributeDetails.setConditions(conditions);
		attributeDetails.setDataType(attribute.getDataType());
		ICondition condition  = null;
		attributeDetails.setAttributeNameConditionMap(HtmlUtility.getMapOfConditions(conditions));
		if(attributeDetails.getAttributeNameConditionMap()!=null)
		{
			condition = attributeDetails.getAttributeNameConditionMap().
							get(attributeDetails.getAttrName());
		}
		attributeDetails.setEditValues(null);
		attributeDetails.setSelectedOperator(null);
		if(condition != null)
		{
			attributeDetails.setEditValues(condition.getValues());
			attributeDetails.setSelectedOperator
				(condition.getRelationalOperator().getStringRepresentation());
		}
		getParamaterizedCondition(parameterList, forPage);
	}

	/**
	 * get details for parameterized query.
	 * @param parameterList list of parameters
	 * @param forPage String
	 */
	private void getParamaterizedCondition(List<IParameter<?>> parameterList, String forPage)
	{
		IParameter<?> paramater=null;
		attributeDetails.setParameterList(parameterList);
		boolean isPresentInMap =
				attributeDetails.getAttributeNameConditionMap()!=null &&
				attributeDetails.getAttributeNameConditionMap().
								get(attributeDetails.getAttrName())!=null;
		if (forPage!=null && forPage.equalsIgnoreCase(Constants.EXECUTE_QUERY_PAGE) && isPresentInMap)
		{
			paramater = HtmlUtility.isParameterized(attributeDetails.getAttributeNameConditionMap().
					get(attributeDetails.getAttrName()),parameterList);
			attributeDetails.setParamater(paramater);
			attributeDetails.setParameterizedCondition(
					attributeDetails.getAttributeNameConditionMap().
					containsKey(attributeDetails.getAttrName())&& paramater!=null);
		}
	}

	/**
	 * check if attribute label is bold.
	 * @param attributeChecked boolean
	 * @param permissibleValuesChecked boolean
	 * @param attribute AttributeInterface
	 * @param attrName name of attribute
	 * @return boolean
	 */
	private boolean checkAttributeBold(boolean attributeChecked, boolean permissibleValuesChecked,
			AttributeInterface attribute, String attrName)
	{
		boolean isBold = false;
		if(attributeChecked)
		{
			isBold = isAttributeBold(attrName.toLowerCase());
		}
		if(!isBold && permissibleValuesChecked)
		{
			isBold = isPerValueAttributeBold(HtmlUtility.getPermissibleValuesList(attribute));
		}
		return isBold;
	}

    /**
     *
     * @param permissibleValuesList list of permissible values
     * @return boolean
     */
	private boolean isPerValueAttributeBold(
			List<PermissibleValueInterface> permissibleValuesList)
	{
		boolean isBold = false;
		if (permissibleValuesList != null && !permissibleValuesList.isEmpty())
		{
			for (int i = 0; i < permissibleValuesList.size(); i++)
			{
				PermissibleValueInterface perValue =
					(PermissibleValueInterface) permissibleValuesList.get(i);
				String value = perValue.getValueAsObject().toString();
				if (isAttributeBold(value.toLowerCase()))
				{
					isBold = true;
					break;
				}
			}
		}
		return isBold;
	}

	/**
	 * @param attrName name of attribute
	 * @return boolean
	 */
	private boolean isAttributeBold(String attrName)
	{
		boolean isBold = false;
		for(String searchString : this.generateHTMLDetails.getSearcStrings())
		{
			if(attrName.indexOf(searchString)>=0)
			{
				isBold = true;
				break;
			}
		}
		return isBold;
	}

	/**
	 * This function generates the HTML for enumerated values.
	 *
	 * @param componentId
	 *            id of component
	 * @param permissibleValues
	 *            list of permissible values
	 * @param editLimitPermissibleValues
	 *            values list in case of edit limits
	 * @return String html for enumerated value dropdown
	 */
	private String generateHTMLForEnumeratedValues(String  componentId,
			List<PermissibleValueInterface> permissibleValues, List<String> editLimitPermissibleValues)
	{
		StringBuffer html = new StringBuffer(Constants.MAX_SIZE);
		//String attributeName = attribute.getName();
		//String componentId = generateComponentName(attribute);
		String format ="\n<td width='70%' valign='centre' colspan='4' >";
		if (permissibleValues != null && !permissibleValues.isEmpty())
		{
            html.append(format);

            // Bug #3700. Derestricting the list width & increasing the
            // height
            String temp = "\n<select style=\"display:block;\" MULTIPLE styleId='country' "
        		+ "size ='5' name=\"" + componentId
                + "_enumeratedvaluescombobox\"\">";
            html.append(temp);
			List<PermissibleValueInterface> values =
				new ArrayList<PermissibleValueInterface>(permissibleValues);
			Collections.sort(values, new PermissibleValueComparator());
			for (int i = 0; i < values.size(); i++)
			{
				PermissibleValueInterface perValue = (PermissibleValueInterface) values.get(i);
				getHtmlEnumValues(editLimitPermissibleValues, html, perValue);
			}
			html.append("\n</select>\n</td>");
			//html.append("\n</td>");
		}
		return html.toString();

	}
	/**
	 *	Get html for enumerated values.
	 * @param editLimitPermissibleValues values list in case of edit limits
	 * @param html generated html
	 * @param perValue permissible value
	 */
	private void getHtmlEnumValues(List<String> editLimitPermissibleValues, StringBuffer html,
			PermissibleValueInterface perValue)
	{
		String value = perValue.getValueAsObject().toString();
		if (editLimitPermissibleValues != null
				&& editLimitPermissibleValues.contains(value)
				|| isAttributeBold(value.toLowerCase()))
		{
			html.append("\n<option class=\"PermissibleValuesQuery\" title=\"" + value
					+ "\" value=\"" + value + "\" SELECTED>" + value + "</option>");
		}
		else
		{
			html.append("\n<option class=\"PermissibleValuesQuery\" title=\"" + value
					+ "\" value=\"" + value + "\">" + value + "</option>");
		}
	}

	/**
	 * Gets string from collection of attributes.
	 * @param attributeCollection collection of attributes
	 * @return String
	 */
	private String getAttributesString(Collection<AttributeInterface> attributeCollection)
	{
		StringBuffer attributesList = new StringBuffer();
		StringBuffer semicolon = new StringBuffer(";");
		if (!attributeCollection.isEmpty())
		{
			List<AttributeInterface> attributes =
				new ArrayList<AttributeInterface>(attributeCollection);
			Collections.sort(attributes, new AttributeInterfaceComparator());
			for (int i = 0; i < attributes.size(); i++)
			{
				AttributeInterface attribute = (AttributeInterface) attributes.get(i);
				if(HtmlUtility.isAttrNotSearchable(attribute))
				{
					continue;
				}
				//String attrName = attribute.getName();
				String componentId = generateComponentName(attribute);
				attributesList.append(semicolon).append(componentId);
			}
		}
		return attributesList.toString();
	}

	/**
	 * Method to generate HTML for condition NULL.
	 * @param generatedHTML generated html
	 * @param attribute AttributeInterface
	 * @param attributeDetails details of attribute
	 */
	private void generateHTMLForConditionNull(StringBuffer generatedHTML,
			AttributeInterface attribute,AttributeDetails attributeDetails)
	{
		List<PermissibleValueInterface> permissibleValues =getPermissibleValuesList(attribute,entity);
		String componentId = generateComponentName(attribute);
		boolean isDate = false;
		AttributeTypeInformationInterface attrTypeInfo = attribute
		.getAttributeTypeInformation();
		if (attrTypeInfo instanceof DateAttributeTypeInformation)
		{
			isDate = true;
		}
		generatedHTML.append(Constants.NEWLINE).append(
				GenerateHtml.generateHTMLForOperators(componentId,isDate,attributeDetails));
		if (showListBoxForPV(attribute))//!permissibleValues.isEmpty() && permissibleValues.size() < Constants.MAX_PV_SIZE)
		{
			generatedHTML.append(Constants.NEWLINE).append(
					generateHTMLForEnumeratedValues(componentId, permissibleValues,
							attributeDetails.getEditValues()));
		}
		else
		{
			if (attribute.getDataType().equalsIgnoreCase(Constants.DATATYPE_BOOLEAN))
			{
				generatedHTML
						.append(Constants.NEWLINE).append(
								GenerateHtml.generateHTMLForRadioButton(
								componentId, attributeDetails.getEditValues()));
			}
			else
			{
				generatedHTML.append(Constants.NEWLINE).append(
						GenerateHtml.generateHTMLForTextBox(
								componentId,attributeDetails));
			}
			
			IPermissibleValueManager permissibleValueManager = PermissibleValueManagerFactory.getPermissibleValueManager();
			if(permissibleValueManager.isEnumerated(attribute,entity))
				{
					showEnumeratedAttibutesWithIcon(generatedHTML,attribute);
				}
			
		}
	}

	/**
	 * Method for generating HTML depending on condition.
	 * @param generatedHTML generated html
	 * @param attribute AttributeInterface
	 */
	private void generateHTMLForConditions(StringBuffer generatedHTML,
			AttributeInterface attribute)
	{
		List<ICondition> conditions = attributeDetails.getConditions();
		if (conditions != null)
		{
			getHtmlConditionNotNull(generatedHTML,attribute, forPage);
		}
		if (conditions == null || (attributeDetails.getAttributeNameConditionMap()!=null
				&& !attributeDetails.getAttributeNameConditionMap().
				containsKey(attributeDetails.getAttrName())))
		{
			generateHTMLForConditionNull(generatedHTML, attribute,this.attributeDetails);
		}
	}

	/**
	 *
	 * @param generatedHTML generated html
	 * @param attribute AttributeInterface
	 * @param forPage String
	 */
	private void getHtmlConditionNotNull(StringBuffer generatedHTML,
			AttributeInterface attribute, String forPage)
	{
		if (attributeDetails.getAttributeNameConditionMap()!=null &&
				attributeDetails.getAttributeNameConditionMap().
				containsKey(attributeDetails.getAttrName()))
		{
			IParameter<?> parameter = attributeDetails.getParamater();
			if (forPage.equalsIgnoreCase(Constants.EXECUTE_QUERY_PAGE)
					&& parameter==null)
			{
				return;
			}

			generateHTMLForConditionNull(generatedHTML,attribute,this.attributeDetails);
		}
	}
	/**
	 * Method generates html for each entity of saved query.
	 * @param expressionID expression id
	 * @param entity entity for which html tobe generated
	 * @param conditions list of conditions
	 * @param isShowAll boolean
	 * @param entityList list of entities
	 * @param parameterList list of parameters
	 * @return generated html
	 */
	private StringBuffer getSaveQueryPageHtml(int expressionID, EntityInterface entity,
			List<ICondition> conditions, boolean isShowAll, Map<EntityInterface,
			List<Integer>> entityList,List<IParameter<?>> parameterList)
	{
		setExpressionId(expressionID);
		StringBuffer generatedHTML = new StringBuffer();
		StringBuffer generatedPreHTML = new StringBuffer();
		Collection<AttributeInterface> attributeCollection = entity.getEntityAttributesForQuery();
		Collection<AttributeInterface> collection = new ArrayList<AttributeInterface>();
		boolean isBGColor = false;
		boolean isEditLimits = isEditLimits(conditions);
		if (!attributeCollection.isEmpty())
		{
			// get the list of dag ids for the corresponding entity
			String dagNodeId = getDagNodeId(expressionID, entity, entityList);
			List<AttributeInterface> attributes =
				new ArrayList<AttributeInterface>(attributeCollection);
			Collections.sort(attributes, new AttributeInterfaceComparator());
			GenerateHtml.getHtmlAddEditPage(forPage, generatedHTML);
			for(AttributeInterface attribute : attributes)
			{
				if(HtmlUtility.isAttrNotSearchable(attribute))
				{
					continue;
				}
				getAttributeDetails(attribute, conditions, parameterList);
				String attrName = attributeDetails.getAttrName();
				Map<String, ICondition> attributeNameConditionMap =
					attributeDetails.getAttributeNameConditionMap();
				if (checkAtrributeCondition(isShowAll, attrName, attributeNameConditionMap))
				{
					continue;
				}
				collection.add(attribute);
				String componentId = generateComponentName(attribute);
				isBGColor = getAlternateHtmlForSavedQuery(
						generatedHTML, isBGColor,componentId);
				generatedHTML.append(getHtmlAttributeSavedQuery(
						entity, dagNodeId, attribute));

			}

			generatedPreHTML.append(getHtml(entity, generatedHTML,collection,isEditLimits));
		}
		generatedHTML = getAddEditPageHtml(generatedHTML, generatedPreHTML);
		return generatedHTML;
	}

	/**
	 * Modify html for Add/Edit page of Query.
	 * @param generatedHTML StringBuffer
	 * @param generatedPreHTML StringBuffer
	 * @return modified html
	 */
	private StringBuffer getAddEditPageHtml(StringBuffer generatedHTML,
			StringBuffer generatedPreHTML)
	{
		StringBuffer html = generatedHTML;
		if (forPage.equalsIgnoreCase(Constants.ADD_EDIT_PAGE))
		{
			generatedPreHTML.append("####");
			generatedPreHTML.append(generatedHTML);
			html = generatedPreHTML;
		}
		return html;
	}
	/**
	 * Edit Limit case if Conditions on attribute is not null.
	 * @param conditions list of conditions
	 * @return isEditLimits
	 */
	public static boolean isEditLimits(List<ICondition> conditions)
	{
		boolean isEditLimits=false;
		if (conditions != null)
		{
			isEditLimits = true;
		}
		return isEditLimits;
	}
	/**
	 * This method checks if an attribute has conditions.
	 * @param isShowAll boolean
	 * @param attrName name of attribute
	 * @param attributeNameConditionMap map containing attribute and its conditions.
	 * @return boolean
	 */
	private boolean checkAtrributeCondition(boolean isShowAll, String attrName,
			Map<String, ICondition> attributeNameConditionMap)
	{
		return attributeNameConditionMap != null
				&& !attributeNameConditionMap.containsKey(attrName) && !isShowAll;
	}

	/**
	 *
	 * @param entity for which html is to be generated
	 * @param generatedHTML generated html
	 * @param collection collection of attributes
	 * @param isEditLimits boolean
	 * @return StringBuffer
	 */
	private StringBuffer getHtml(EntityInterface entity, StringBuffer generatedHTML,
			Collection<AttributeInterface> collection, boolean isEditLimits)
	{
		StringBuffer generatedPreHTML = new StringBuffer();
		String nameOfTheEntity = entity.getName();
		Collection<AttributeInterface> attributeCollection = entity.getEntityAttributesForQuery();
		if (forPage.equalsIgnoreCase(Constants.SAVE_QUERY_PAGE)
				|| forPage.equalsIgnoreCase(Constants.EXECUTE_QUERY_PAGE))
		{
			generatedHTML.append(" <input type='hidden'  id='" + this.expressionId + ":"
					+ Utility.parseClassName(entity.getName()) + "_attributeList'" + "value="
					+ getAttributesString(collection) + " />  ");
		}
		else if (forPage.equalsIgnoreCase(Constants.ADD_EDIT_PAGE))
		{
			GenerateHtml.getTags(generatedHTML);
			generatedHTML.append("</table>");
			generatedPreHTML = GenerateHtml.generatePreHtml(
				getAttributesString(attributeCollection), nameOfTheEntity,isEditLimits);
		}
		return generatedPreHTML;
	}

	/**
	 * Method returns DagNode Id.
	 * @param expressionID expressionID
	 * @param entity EntityInterface
	 * @param entityList list of entities
	 * @return String
	 */
	private String getDagNodeId(int expressionID, EntityInterface entity,
			Map<EntityInterface, List<Integer>> entityList)
	{
		List<Integer> entityDagId = (List<Integer>)entityList.get(entity);
		String dagNodeId = "";	// Converting the dagId to string
		if (entityDagId.size() > 1)
		{
			// DAGNodeId / expressionID to be shown only in case
			//if there are more than one node of the same class
			dagNodeId = expressionID + Constants.QUERY_DOT;
		}
		return dagNodeId;
	}

	/**
	 * This method gets html for each attribute in saved query.
	 * @param entity EntityInterface
	 * @param dagNodeId String
	 * @param attribute AttributeInterface
	 * @return generated html
	 */
	private StringBuffer getHtmlAttributeSavedQuery(EntityInterface entity, String dagNodeId,
			AttributeInterface attribute)
	{
		StringBuffer generatedHTML = new StringBuffer(Constants.MAX_SIZE);
		String name = Utility.parseClassName(entity.getName());
		String componentId = generateComponentName(attribute);
		generatedHTML.append(getHtmlForPage(dagNodeId,componentId,name));
		GenerateHtml.getDateFormat(generatedHTML, false, attribute);
		if ((forPage.equalsIgnoreCase(Constants.EXECUTE_QUERY_PAGE)
		 	&& attributeDetails.isParameterizedCondition())
		 	|| !forPage.equalsIgnoreCase(Constants.EXECUTE_QUERY_PAGE))
		{
			generatedHTML.append("&nbsp;&nbsp;&nbsp;&nbsp;</b></td>\n");
		}
		generateHTMLForConditions(generatedHTML, attribute);
		generatedHTML.append("\n</tr>");
		return generatedHTML;
	}

	/**
	 * This method generates the alternate css for each attribute in saved query.
	 * @param generatedHTML generated html
	 * @param isBGColor boolean
	 * @param componentId component identifier
	 * @return boolean
	 */

	private boolean getAlternateHtmlForSavedQuery(StringBuffer generatedHTML,boolean isBGColor,
			 String componentId)
	{
		boolean bgColor = isBGColor;
		String styleSheetClass = GenerateHtml.CSS_BGWHITE;
		if ((forPage.equalsIgnoreCase(Constants.EXECUTE_QUERY_PAGE)
				&& attributeDetails.isParameterizedCondition())
				|| !forPage.equalsIgnoreCase(Constants.EXECUTE_QUERY_PAGE))
		{
			attributesList = attributesList + ";" + componentId;
		}
		if (isBGColor)
		{
			styleSheetClass = GenerateHtml.CSS_BGGREY;
		}
		else
		{
			styleSheetClass = GenerateHtml.CSS_BGWHITE;
		}
		bgColor ^= true; 	//BGColor = !BGColor
		String html = "\n<tr  class='"+styleSheetClass +"'" +
		"  id=\"componentId\" "+" >\n";
		
		generatedHTML.append(html);
		return bgColor;
	}

	/**
	 * Modifies html based on ForPage i.e SAVE_QUERY, EXECUTE_QUERY_PAGE.
	 * @param dagNodeId dag node id
	 * @param componentId id of component
	 * @param name of Entity
	 * @return StringBuffer
	 */
	private StringBuffer getHtmlForPage(String dagNodeId,String componentId,String name)
	{
		StringBuffer generatedHTML = new StringBuffer(Constants.MAX_SIZE);
		String attrLabel = Utility.getDisplayLabel(attributeDetails.getAttrName());
		String html="";
		if (forPage.equalsIgnoreCase(Constants.SAVE_QUERY_PAGE))
		{
		//	formName = "saveQueryForm";
			html = " " + GenerateHtml.generateCheckBox(componentId, false)
			+ "<td valign='top' align='left' class='standardTextQuery'>"
			+"<label for='" + componentId
			+ "_displayName' title='" + dagNodeId + name + "." + attrLabel + "'>"
			+ "<input type=\"textbox\"  class=\"formFieldSized20\"  name='"
			+ componentId + "_displayName'     id='" + componentId
			+ "_displayName' value='" + dagNodeId + name + "." + attrLabel
			+ "' disabled='true'> " + "</label></td>";
			generatedHTML.append(html);
		}
		if (!forPage.equalsIgnoreCase(Constants.EXECUTE_QUERY_PAGE))
		{
			html="<td valign='top' align='left' "
				+ "class='standardTextQuery' nowrap='nowrap' width=\"15%\">"
				+ attrLabel + " ";
			generatedHTML
					.append(html);
		}
		if (forPage.equalsIgnoreCase(Constants.EXECUTE_QUERY_PAGE)
				&& attributeDetails.isParameterizedCondition())
		{
		//	formName = "saveQueryForm";
			html="<td valign='top' align='left' class='standardTextQuery' "
				+ "nowrap='nowrap' width=\"15%\">"
				+ attributeDetails.getParamater().getName() + " ";
			generatedHTML
					.append(html);
		}
		return generatedHTML;
	}
	/**
	 * This method generates the html for Save Query section. This internally
	 * calls methods to generate other UI components like text, Calendar,
	 * Combobox etc. This method is same as the generateHTML except that this
	 * will generate html for selected conditions and will display only those
	 * conditions with their values set by user.
	 * @param expressionMap map which holds the list of all dag ids / expression ids for a particular entity
	 * @param isShowAll boolean
	 * @param forPage String
	 * @param parameterList list of parameters
	 * @return String html generated for Save Query section.
	 */
	public String getHtmlForSavedQuery(
			Map<Integer, Map<EntityInterface, List<ICondition>>>  expressionMap, boolean isShowAll,
			String forPage,List<IParameter<?>> parameterList)
	{
		this.forPage = forPage;
		StringBuffer generatedHTML = new StringBuffer(Constants.MAX_SIZE);
		attributesList = "";
		StringBuffer expressionEntityString = new StringBuffer();
		if (expressionMap.isEmpty())
		{
			generatedHTML.append("No record found.");
			//return generatedHTML.toString();
		}
		else
		{
			//get the map which holds the list of all dag ids / expression ids for a particular entity
			expressionEntityString = getMapsForEntity(
				expressionMap, isShowAll, parameterList, generatedHTML);
		}
		if (!expressionMap.isEmpty() && (forPage.equalsIgnoreCase(Constants.SAVE_QUERY_PAGE)
				|| forPage.equalsIgnoreCase(Constants.EXECUTE_QUERY_PAGE)))
		{
			String html = "<input type='hidden' id='totalentities' value='"
				+ expressionEntityString + "' />";
			generatedHTML.append(html);
			html = "<input type='hidden' id='attributesList' value='"
				+ attributesList + "' />";
			generatedHTML.append(html);
			generatedHTML
			.append("<input type='hidden' id='conditionList' name='conditionList' value='' />");
		}
		return generatedHTML.toString();
	}

	/**
	 * Create a map which holds the list of all Expression(DAGNode) ids for a particular entity.
	 * @param expressionMap map of enpression ids for an entity
	 * @param isShowAll boolean
	 * @param parameterList list of parameters
	 * @param generatedHTML generated html
	 * @return expressionEntityString
	 */
	private StringBuffer getMapsForEntity(
			Map<Integer, Map<EntityInterface, List<ICondition>>> expressionMap, boolean isShowAll,
			List<IParameter<?>> parameterList, StringBuffer generatedHTML)
	{
		String colon = ":";
		StringBuffer expressionEntityString = new StringBuffer();
		Map<EntityInterface, List<ICondition>> entityConditionMap = null;
		Map<EntityInterface, List<Integer>> entityExpressionIdListMap =
					GenerateHtml.getEntityExpressionIdListMap(expressionMap);
		Iterator<Integer> iterator = expressionMap.keySet().iterator();
		while (iterator.hasNext())
		{
			Integer expressionId = (Integer) iterator.next();
			entityConditionMap = expressionMap.get(expressionId);
			if (entityConditionMap.isEmpty())
			{
				continue;
			}
			Iterator<EntityInterface> it2 = entityConditionMap.keySet().iterator();
			while (it2.hasNext())
			{
				EntityInterface entity = (EntityInterface) it2.next();
				List<ICondition> conditions = entityConditionMap.get(entity);
				generatedHTML.append(getSaveQueryPageHtml(expressionId.intValue(), entity,
				conditions, isShowAll, entityExpressionIdListMap,parameterList));
				expressionEntityString.append(expressionId.intValue()).append(colon)
						.append(Utility.parseClassName(
							entity.getName())).append(Constants.ENTITY_SEPARATOR);
			}
		}
		return expressionEntityString;
	}
	/**
	 * 
	 * @param generatedHTML
	 * @param attributeInterface
	 * added by amit_doshi for Vocabulary Interface
	 */
	private void showEnumeratedAttibutesWithIcon(StringBuffer generatedHTML, AttributeInterface attributeInterface) 
	{
		/* Need to get the attribute interface of of ID attribute because we have to set all the concept code to the
		 ID Attribute*/
		AttributeInterface attributeIDInterface=entity.getAttributeByName(Constants.ID);
		String componentIdOfID=generateComponentName(attributeIDInterface);
		String componentId = generateComponentName(attributeInterface);
		generatedHTML.append("\n<td valign='top' width='1%'><a onclick=\"openPermissibleValuesConfigWindow('" + componentId	+ "','"+entity.getName()+"','"+componentIdOfID+"')\" ><img src=\"images/advancequery/b_new_search.gif\"  /></a> </td>");
		enumratedAttributeMap.put(Constants.ATTRIBUTE_INTERFACE+componentId, attributeInterface);
		
	}
	/**
	 * 
	 * @param attribute
	 * @return boolean
	 * added by amit_doshi
	 */
	private boolean showListBoxForPV(AttributeInterface attribute)
	{
		IPermissibleValueManager permissibleValueManager = PermissibleValueManagerFactory.getPermissibleValueManager();
		boolean status=false;
		status= permissibleValueManager.showListBoxForPV(attribute,entity);
		return status;
		
	}
	/**
	 * returns PermissibleValuesList' list for attribute
	 * 
	 * @param attribute     AttributeInterface
	 * @return List of permissible values for the passed attribute
	 * added amit_doshi 
	 */
	private List<PermissibleValueInterface> getPermissibleValuesList(AttributeInterface attribute,EntityInterface entity)
	{
		IPermissibleValueManager permissibleValueManager = PermissibleValueManagerFactory.getPermissibleValueManager();
		List<PermissibleValueInterface> permissibleValues = null;
		permissibleValues = permissibleValueManager.getPermissibleValueList(attribute,entity);
		return permissibleValues;
	}

}
