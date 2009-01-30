
package edu.wustl.common.query.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.querysuite.exceptions.CyclicException;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.factory.QueryObjectFactory;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.ArithmeticOperator;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IConnector;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IExpressionOperand;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.querysuite.queryobject.IOperand;
import edu.wustl.common.querysuite.queryobject.IOutputAttribute;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.IQueryEntity;
import edu.wustl.common.querysuite.queryobject.IRule;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.queryobject.impl.Expression;
import edu.wustl.query.util.querysuite.QueryModuleError;
import edu.wustl.query.util.querysuite.QueryModuleException;

/**
 * Mock to create XQuery Objects.
 * 
 * @author ravindra_jain, juber patel
 * @created 4th December, 2008
 * 
 */
public class IQueryBuilder
{

	public static XQueryEntityManagerMock entityManager = new XQueryEntityManagerMock();

	/**
	 * simple IQuery for Person with no additional conditions
	 * 	 * 
	 * @param expression The Expression reference created by function
	 * @return The corresponding join Graph reference.
	 * @throws DynamicExtensionsSystemException 
	 * @throws CyclicException 
	 * @throws DynamicExtensionsApplicationException 
	 */
	public static IQuery skeletalPersonQuery() throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException, CyclicException
	{
		IQuery query = null;
		query = QueryObjectFactory.createQuery();
		IConstraints constraints = QueryObjectFactory.createConstraints();
		query.setConstraints(constraints);

		IExpression person = createExpression(constraints, null, XQueryEntityManagerMock.PERSON);
		addBasicPersonConditions(person, "N");

		return query;
	}

	/**
	 *  simple IQuery for Person & Demographics
	 *  
	 * @param expression The Expression reference created by function
	 * @return The corresponding join Graph reference.
	 * @throws DynamicExtensionsSystemException 
	 * @throws CyclicException 
	 * @throws DynamicExtensionsApplicationException 
	 * @throws MultipleRootsException 
	 */
	public static IQuery skeletalDemograpihcsQuery() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, CyclicException, MultipleRootsException
	{
		IQuery query = skeletalPersonQuery();
		IConstraints constraints = query.getConstraints();
		IJoinGraph joinGraph = constraints.getJoinGraph();

		IExpression person = findExpression(XQueryEntityManagerMock.PERSON, joinGraph.getRoot(),
				joinGraph);
		IExpression demographics = createExpression(constraints, person,
				XQueryEntityManagerMock.DEMOGRAPHICS);
		addBasicDemographicsConditions(demographics);

		return query;
	}

	/**
	 * IQuery for person, demographics, race & gender with no conditions
	 * 
	 * @param expression The Expression reference created by function
	 * @return The corresponding join Graph reference.
	 * @throws MultipleRootsException 
	 * @throws CyclicException 
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 */
	public static IQuery skeletalRaceGenderQuery() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, CyclicException, MultipleRootsException
	{
		IQuery query = skeletalDemograpihcsQuery();
		IConstraints constraints = query.getConstraints();
		IJoinGraph joinGraph = constraints.getJoinGraph();

		IExpression demographics = findExpression(XQueryEntityManagerMock.DEMOGRAPHICS, joinGraph
				.getRoot(), joinGraph);
		IExpression race = createExpression(constraints, demographics, XQueryEntityManagerMock.RACE);
		createExpression(constraints, race, XQueryEntityManagerMock.GENDER);
		
		return query;
	}

	public static IQuery skeletalRaceGenderAddressQuery() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, CyclicException, MultipleRootsException
	{
		IQuery query = skeletalRaceGenderQuery();
		IConstraints constraints = query.getConstraints();
		IJoinGraph joinGraph = constraints.getJoinGraph();

		IExpression demographics = findExpression(XQueryEntityManagerMock.ADDRESS, joinGraph
				.getRoot(), joinGraph);
		createExpression(constraints, demographics, XQueryEntityManagerMock.ADDRESS);
		
		return query;
	}

	public static IQuery skeletalLabProcedureQuery() throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException, CyclicException
	{
		IQuery query = null;
		query = QueryObjectFactory.createQuery();
		IConstraints constraints = QueryObjectFactory.createConstraints();
		query.setConstraints(constraints);

		createExpression(constraints, null, XQueryEntityManagerMock.LABORATORY_PROCEDURE);
		return query;
	}

	public static IQuery skeletalLabProcedureDetailsQuery()
			throws DynamicExtensionsSystemException, MultipleRootsException,
			DynamicExtensionsApplicationException, CyclicException
	{
		IQuery query = skeletalLabProcedureQuery();
		IConstraints constraints = query.getConstraints();
		IJoinGraph joinGraph = constraints.getJoinGraph();

		IExpression labProcedure = findExpression(XQueryEntityManagerMock.LABORATORY_PROCEDURE,
				joinGraph.getRoot(), joinGraph);
		createExpression(constraints, labProcedure,
				XQueryEntityManagerMock.LABORATORY_PROCEDURE_DETAILS);
		
		return query;

	}

	public static ICondition addCondition(IExpression expression, String attributeName,
			RelationalOperator unaryOperator)
	{
		AttributeInterface attribute = findAttribute(expression.getQueryEntity()
				.getDynamicExtensionsEntity(), attributeName);
		ICondition condition = QueryObjectFactory.createCondition();
		condition.setAttribute(attribute);
		condition.setRelationalOperator(unaryOperator);
		getRule(expression).addCondition(condition);
		return condition;
	}

	public static void addCondition(IExpression expression, String attributeName,
			RelationalOperator binaryOperator, String value)
	{
		ICondition condition = addCondition(expression, attributeName, binaryOperator);
		condition.setValue(value);

	}

	public static void addCondition(IExpression expression, String attributeName,
			RelationalOperator betweenOperator, List<String> values)
	{
		AttributeInterface attribute = findAttribute(expression.getQueryEntity()
				.getDynamicExtensionsEntity(), attributeName);

		ICondition condition = QueryObjectFactory.createCondition(attribute, betweenOperator,
				values);
		getRule(expression).addCondition(condition);

	}

	public static void addCondition(IExpression expression, String attributeName,
			RelationalOperator betweenOperator, String... values)
	{
		AttributeInterface attribute = findAttribute(expression.getQueryEntity()
				.getDynamicExtensionsEntity(), attributeName);

		ICondition condition = QueryObjectFactory.createCondition(attribute, betweenOperator,
				Arrays.asList(values));
		getRule(expression).addCondition(condition);
	}

	public static IExpression createExpression(IConstraints constraints, IExpression parent, String expressionType)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException, CyclicException
	{
		EntityInterface entity = entityManager.getEntityByName(expressionType);
		IQueryEntity queryEntity = QueryObjectFactory.createQueryEntity(entity);
		IExpression expression = constraints.addExpression(queryEntity);
		expression.setInView(true);

		List<ICondition> conditions = new ArrayList<ICondition>();
		IRule rule = QueryObjectFactory.createRule(conditions);
		expression.addOperand(rule);
		
		if(parent != null)
		{
			connectExpressions(parent, expression, LogicalOperator.And, constraints.getJoinGraph());
		}
		
		return expression;
	}

	private static IRule getRule(IExpression expression)
	{
		Iterator<IExpressionOperand> iterator = expression.iterator();
		while (iterator.hasNext())
		{
			IExpressionOperand operand = iterator.next();
			if (operand instanceof IRule)
			{
				return (IRule) operand;
			}
		}

		return null;
	}

	public static void connectExpressions(IExpression source, IExpression target,
			LogicalOperator logicalOperator, IJoinGraph joinGraph)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			CyclicException
	{
		IConnector<LogicalOperator> connector = QueryObjectFactory
				.createLogicalConnector(logicalOperator);

		source.addOperand(connector, target);

		// Add association to Join graph.
		AssociationInterface association = getAssociation(entityManager.getAssociations(source
				.getQueryEntity().getDynamicExtensionsEntity().getId(), target.getQueryEntity()
				.getDynamicExtensionsEntity().getId()), target.getQueryEntity()
				.getDynamicExtensionsEntity());
		IIntraModelAssociation imAssociation = QueryObjectFactory
				.createIntraModelAssociation(association);
		joinGraph.putAssociation(source, target, imAssociation);

	}

	public static AssociationInterface getAssociation(
			Collection<AssociationInterface> associations, EntityInterface targetEntity)
	{
		for (AssociationInterface theAssociation : associations)
		{
			if (theAssociation == null || theAssociation.getTargetEntity() == null)
			{
				System.out.println("FFOOOOO");
			}
			if (theAssociation.getTargetEntity().equals(targetEntity))
			{
				return theAssociation;
			}
		}

		return null;
	}

	public static IExpression findExpression(String entityName, IExpression root,
			IJoinGraph joinGraph)
	{
		if (root.getQueryEntity().getDynamicExtensionsEntity().getName().equals(entityName))
		{
			return root;
		}

		for (IExpression child : joinGraph.getChildrenList(root))
		{
			IExpression wantedExpression = findExpression(entityName, child, joinGraph);
			if (wantedExpression != null)
			{
				return wantedExpression;
			}
		}

		return null;
	}

	/**
	 * To search attribute in the Entity.
	 * 
	 * @param entity The Dynamic Extension Entity
	 * @param attributeName The name of the attribute to search.
	 * @return The corresponding attribute.
	 */
	private static AttributeInterface findAttribute(EntityInterface entity, String attributeName)
	{
		Collection<AttributeInterface> attributes = entity.getEntityAttributesForQuery();
		for (AttributeInterface attribute : attributes)
		{
			if (attribute.getName().equals(attributeName))
				return attribute;
		}

		return null;
	}

	private static void addBasicPersonConditions(IExpression personExpression, String researchOptOut)
	{
		addCondition(personExpression, "personUpi", RelationalOperator.IsNotNull);
		addCondition(personExpression, "activeUpiFlag", RelationalOperator.Equals, "A");
		addCondition(personExpression, "researchOptOut", RelationalOperator.In, researchOptOut);
	}

	private static void addBasicDemographicsConditions(IExpression demographicsExpression)
	{
		addCondition(demographicsExpression, "effectiveEndTimeStamp",
				RelationalOperator.GreaterThan, "01/24/2009");
		addCondition(demographicsExpression, "effectiveStartTimeStamp",
				RelationalOperator.LessThan, "01/26/2009");
	}

	private static void addPersonUpiMembershipCondition(IExpression personExpression,
			RelationalOperator operator)
	{
		List<String> values = new ArrayList<String>();
		values.add("000000000000000008690923");
		values.add("000000000000000008690927");
		values.add("000000000000000008691120");
		values.add("000000000000000008690929");

		addCondition(personExpression, "personUpi", operator, values);

	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	/**
	 * To create IQuery for:
	 * Query on person & demographics & race & gender
	 * Query for Person: PersonUpi is Not NULL
	 * 					 DateOfBirth > someDate
	 * 					 RaceId = 3452
	 * 					 Gender = 1987
	 * @param expression The Expression reference created by function
	 * @return The corresponding join Graph reference.
	 */
	
	
	
	/**
	 * To create IQuery for:
	 * Query on person & demographics & race & gender
	 * Query for Person: PersonUpi is Not NULL
	 * 					 DateOfBirth > someDate
	 * 					 RaceId = 3452
	 * @param expression The Expression reference created by function
	 * @return The corresponding join Graph reference.
	 */
	
	
	
	/**
	 * To create IQuery for Person as:
	 * [Person Upi is not null]
	 * DateOfBirth < someDate
	 * 
	 * @return The corresponding IQuery
	 */
	
	
	
	
	/**
	 * To create IQuery for Person as:
	 * Query on person - using IN relational operator
	 * Query for Person: PersonUpi in values
	 * 					 {000000000000000008690923	000000000000000008691120
	 *					  000000000000000008690927  000000000000000008690929}
	 * 
	 * @return The corresponding IQuery
	 */


	
	
	/**
	 * To create IQuery for Person as:
	 * Query on person - using NOT IN relational operator
	 * Query for Person: PersonUpi NOT IN values
	 * 					 {000000000000000008690923	000000000000000008691120
	 *					  000000000000000008690927  000000000000000008690929}
	 * 
	 * @return The corresponding IQuery
	 */
	
	
	
	
	/**
	 * To create IQuery for Person as:
	 * Query on person - using CONTAINS relational operator
	 * Query for Person: PersonUpi CONTAINS values
	 * 					 {000000000000000008690923}
	 * 
	 * @return The corresponding IQuery
	 */
	
	
	
	
	/**
	 * To create IQuery for Query as:
	 * Query on person & demographics - using BETWEEN relational operator
	 * Query for Person: PersonUpi is NOT NULL &
	 * 			 Demographics : dateOfBirth between
	 * 							1912-01-01	and
	 *    						1912-04-01
	 * 
	 * @return The corresponding IQuery
	 */


	
	
	
	/**
	 * To create IQuery for Query as:
	 * Query on person & demographics - using STARTS WITH relational operator
	 * Query for Person: PersonUpi is NOT NULL &
	 * 			 Demographics : socialSecurityNumber starts with '0'
	 * 
	 * @return The corresponding IQuery
	 */
	
	
	
	/**
	 * To create IQuery for Query as:
	 * Query on Person & Lab
	 * Query for Person: PersonUpi is NOT NULL &
	 * 			 Lab : accessionNumber is NOT NULL
	 * 
	 * @return The corresponding IQuery
	 */
	
	
	

	/**
	 * 
	 * @param query
	 * @throws QueryModuleException
	 */
	private static void updateQueryWithSelectedAttributes(IParameterizedQuery query)
			throws QueryModuleException
	{
		List<IOutputAttribute> selectedOutputAttributeList = new ArrayList<IOutputAttribute>();
		IParameterizedQuery parameterizedQuery = (IParameterizedQuery) query;
		IConstraints constraints = parameterizedQuery.getConstraints();
		IExpression rootExpression;
		try
		{
			rootExpression = constraints.getRootExpression();

			Collection<AttributeInterface> allAttributes = rootExpression.getQueryEntity()
					.getDynamicExtensionsEntity().getPrimarykeyAttributeCollectionInSameEntity();
			for (AttributeInterface attributeInterface : allAttributes)
			{
				IOutputAttribute createOutputAttribute = QueryObjectFactory.createOutputAttribute(
						rootExpression, attributeInterface);
				selectedOutputAttributeList.add(createOutputAttribute);
			}
			parameterizedQuery.setOutputAttributeList(selectedOutputAttributeList);
		}
		catch (MultipleRootsException e)
		{
			QueryModuleException queryModuleException = new QueryModuleException(e.getMessage(),
					QueryModuleError.MULTIPLE_ROOT);
			throw queryModuleException;
		}
	}

}
