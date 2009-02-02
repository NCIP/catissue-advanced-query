package edu.wustl.query.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.query.pvmanager.impl.PVManagerException;
import edu.wustl.common.vocab.IConcept;
import edu.wustl.common.vocab.IVocabulary;
import edu.wustl.common.vocab.VocabularyException;
import edu.wustl.common.vocab.impl.Vocabulary;
import edu.wustl.common.vocab.utility.VocabUtil;
import edu.wustl.query.bizlogic.BizLogicFactory;
import edu.wustl.query.bizlogic.SearchPermissibleValueBizlogic;
import edu.wustl.query.util.global.Constants;

/**
 * @author amit_doshi
 * Action Class to show the UI for Vocabulary Interface and to handle the Ajax request
 */
public class SearchMappedPVsAction extends Action
{

	/**
	 * This method handles the various Ajax request for VI
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
		final String targetVocab = request.getParameter("selectedCheckBox");
		//get the id of the component on which user click to search for PVs
		String componentId = request.getParameter("componentId");
		if (componentId == null)
		{
			//need to save componetid into the session for next Ajax requests
			componentId = (String) request.getSession().getAttribute(Constants.COMPONENT_ID);
		}
		String entityName = (String) request.getSession().getAttribute(Constants.ENTITY_NAME);
		Entity entity = (Entity) EntityCache.getCache().getEntityById(Long.valueOf((entityName)));
		Map<String, AttributeInterface> enumAttributeMap = (HashMap<String, AttributeInterface>)
				request.getSession().getAttribute(Constants.ENUMRATED_ATTRIBUTE);
		AttributeInterface attribute = (AttributeInterface) enumAttributeMap
				.get(Constants.ATTRIBUTE_INTERFACE + componentId);

		if (targetVocab != null)
		{
			// AJAX Request handler for Getting Mapping data for source to target vocabulries
			String html = getMappedVocabDataAsHTML(targetVocab, attribute, entity);
			response.getWriter().write(html);
			return null;
		}
		
		generatePermValueHTMLForMED(attribute, entity, componentId, request);
		return mapping.findForward(edu.wustl.query.util.global.Constants.SUCCESS);
	}
		/**
	 * This method generate the HTML for the Source vocabulary  (MED 1.0)
	 * @param attribute
	 * @param entity
	 * @param componentId
	 * @param request
	 * @throws VocabularyException
	 * @throws PVManagerException
	 */
	private void generatePermValueHTMLForMED(AttributeInterface attribute, EntityInterface entity,
			String componentId, HttpServletRequest request) throws VocabularyException,
			PVManagerException
	{

		SearchPermissibleValueBizlogic bizLogic = (SearchPermissibleValueBizlogic) BizLogicFactory
				.getInstance().getBizLogic(Constants.SEARCH_PV_FROM_VOCAB_BILOGIC_ID);
		List<PermissibleValueInterface> premValueList = bizLogic.getPermissibleValueListFromDB(attribute, entity);
		int count=Integer.parseInt( VocabUtil.getVocabProperties().getProperty("pvs.to.show"));
		StringBuffer html = new StringBuffer();
		//if(premValueList!=null && premValueList.size()<=count)
		//{
			List<IConcept> pvList = bizLogic.getPermissibleValueList(attribute, entity);
			String vocabName = VocabUtil.getVocabProperties().getProperty("source.vocab.name");
			String vocabVer = VocabUtil.getVocabProperties().getProperty("source.vocab.version");
			String vocabDisName = getDisplayNameForVocab(vocabName, vocabVer);
			int displayPVCount=1;
			html.append(bizLogic.getRootVocabularyNodeHTML(vocabName, vocabVer, vocabDisName));
			for(IConcept concept:pvList)
			{
				if(displayPVCount<=count)// Need to show only specified number of Concepts on UI
				{
				String id = vocabName + "@" + vocabVer + ":" + concept.getCode();
				html.append(bizLogic.getMappedVocabularyPVChildAsHTML(vocabName, vocabVer, concept,id));
				displayPVCount++;
				}
				else
				{
					html.append(bizLogic.getMessage(count));
					break;
				}
				
			}
			html.append(bizLogic.getEndHTML());
			
		/*}
		else 
		{
			String vocabName = VocabUtil.getVocabProperties().getProperty("source.vocab.name");
			String vocabVer = VocabUtil.getVocabProperties().getProperty("source.vocab.version");
			String vocabDisName = getDisplayNameForVocab(vocabName, vocabVer);
			html.append(bizLogic.getRootVocabularyNodeHTML(vocabName, vocabVer, vocabDisName));
			html.append(bizLogic.getMessage(count));
			html.append(bizLogic.getEndHTML());
			
		}*/
		request.getSession().setAttribute(Constants.MED_PV_HTML, html.toString());
		request.getSession().setAttribute(Constants.VOCABULIRES, bizLogic.getVocabulries());
		if (componentId != null)
		{
			request.getSession().setAttribute(Constants.COMPONENT_ID, componentId);
		}
	}
	/**
	 * This method returns the display name for given vocabulary Name and vocabulary version
	 * @param vocabName
	 * @param vocabVer
	 * @return
	 * @throws VocabularyException
	 */
	private String getDisplayNameForVocab(String vocabName, String vocabVer)
			throws VocabularyException
	{
		SearchPermissibleValueBizlogic bizLogic = (SearchPermissibleValueBizlogic) BizLogicFactory
				.getInstance().getBizLogic(Constants.SEARCH_PV_FROM_VOCAB_BILOGIC_ID);
		List<IVocabulary> vocabularies = bizLogic.getVocabulries();
		String vocabDisName = "";
		for (IVocabulary vocabulary : vocabularies)
		{
			if (vocabulary.getName().equals(vocabName) && vocabulary.getVersion().equals(vocabVer))
			{
				vocabDisName = vocabulary.getDisplayName();
				break;
			}
		}
		if (vocabDisName.equals(""))
		{
			throw new VocabularyException("Could not find the vocabulary.");
		}
		return vocabDisName;
	}
	/**
	 * This method returns the data mapped vocabularies
	 * @param targetVocab
	 * @param attribute
	 * @param entity
	 * @return
	 * @throws VocabularyException
	 * @throws PVManagerException
	 */
	private String getMappedVocabDataAsHTML(String targetVocab, AttributeInterface attribute,
			EntityInterface entity) throws VocabularyException, PVManagerException
	{

		SearchPermissibleValueBizlogic bizLogic = (SearchPermissibleValueBizlogic) BizLogicFactory
				.getInstance().getBizLogic(Constants.SEARCH_PV_FROM_VOCAB_BILOGIC_ID);
		String sourceVocabulary = VocabUtil.getVocabProperties().getProperty("source.vocab.name");
		String sourceVocabVer = VocabUtil.getVocabProperties().getProperty("source.vocab.version");
		String targetVacbArray[] = targetVocab.split("#");
		String targetVocabName = targetVacbArray[0];
		String targetVocabVer = targetVacbArray[1];
		String targetVocabURN = targetVacbArray[2];
		IVocabulary targetVocabulary = new Vocabulary(targetVocabName, targetVocabVer,targetVocabURN);
		StringBuffer html = new StringBuffer();
		//List<PermissibleValueInterface> premValueList = bizLogic.getPermissibleValueListFromDB(attribute, entity);
		//int count=Integer.parseInt( VocabUtil.getVocabProperties().getProperty("pvs.to.show"));
		//if Pvs are geater then specified count then no need to show the mappings
		
		/*if(premValueList!=null && premValueList.size()<=count)
		{*/
			if (!sourceVocabulary.equalsIgnoreCase(targetVocabName)
					|| !sourceVocabVer.equalsIgnoreCase(targetVocabVer))
			{
				html.append(bizLogic.getRootVocabularyNodeHTML(targetVocabName, targetVocabVer,
						getDisplayNameForVocab(targetVocabName, targetVocabVer)));
				Map<String, List<IConcept>> vocabMappings = bizLogic.getMappedConcepts(attribute,
						targetVocabulary, entity);
				getMappingDataAsHTML(html, targetVocabName, targetVocabVer, vocabMappings);
	
			}
		/*}
		else
		{
			String vocabDisName = getDisplayNameForVocab(targetVocabName, targetVocabVer);
			html.append(bizLogic.getRootVocabularyNodeHTML(targetVocabName, targetVocabVer, vocabDisName));
			html.append(bizLogic.getMessage(count));
			html.append(bizLogic.getEndHTML());
		}*/

		return html.toString();

	}
	/**
	 * This method returns the mapping data as HTML
	 * @param html
	 * @param vocabName
	 * @param vocabversoin
	 * @param vocabMappings
	 * @throws VocabularyException 
	 * @throws NumberFormatException 
	 */
	private void getMappingDataAsHTML(StringBuffer html, String vocabName, String vocabversoin,
			Map<String, List<IConcept>> vocabMappings) throws NumberFormatException, VocabularyException
	{
		SearchPermissibleValueBizlogic bizLogic = (SearchPermissibleValueBizlogic) BizLogicFactory
				.getInstance().getBizLogic(Constants.SEARCH_PV_FROM_VOCAB_BILOGIC_ID);
		int displayPVCount=1;
		boolean isMsgDisplayed=false;
		int count=Integer.parseInt(VocabUtil.getVocabProperties().getProperty("pvs.to.show"));
		if (vocabMappings != null)
		{
			Set<String> keySet = vocabMappings.keySet();
			Iterator<String> iterator = keySet.iterator();
			while (iterator.hasNext())
			{
				String conceptCode = iterator.next();
				List<IConcept> mappingList = (ArrayList) vocabMappings.get(conceptCode);
				ListIterator<IConcept> mappingListItr = mappingList.listIterator();
				while (mappingListItr.hasNext())
				{
					
					IConcept concept = (IConcept) mappingListItr.next();
					if(displayPVCount<=count)// Need to show only specified number of Concepts on UI
					{
						//we need to use the MED Concept code with mapped values
					String checkboxId = vocabName + "@" + vocabversoin + ":" + conceptCode;
					html.append(bizLogic.getMappedVocabularyPVChildAsHTML(vocabName, vocabversoin,
							concept, checkboxId));
					displayPVCount++;
					}
					else
					{
						html.append(bizLogic.getMessage(count));
						isMsgDisplayed=true;
						break;//break inner loop
					}

				}
				if(isMsgDisplayed)
				{
					break; //break outer loop
				}
			}
		}
		else
		{
			html.append(bizLogic.getNoMappingFoundHTML());
		}
		html.append(bizLogic.getEndHTML());
	}

}
