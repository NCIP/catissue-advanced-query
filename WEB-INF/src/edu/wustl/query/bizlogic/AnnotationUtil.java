/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author
 *@version 1.0
 */

package edu.wustl.query.bizlogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.Constants.AssociationDirection;
import edu.common.dynamicextensions.util.global.Constants.AssociationType;
import edu.common.dynamicextensions.util.global.Constants.Cardinality;
import edu.wustl.cab2b.server.path.PathConstants;
import edu.wustl.cab2b.server.path.PathFinder;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.dbManager.DBUtil;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.query.annotations.xmi.PathObject;

/**
 * @author vishvesh_mulay
 *
 */
public class AnnotationUtil
{

	/**
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws BizLogicException
	 */
	public static synchronized Long addAssociation(Long staticEntityId, Long dynamicEntityId,
			boolean isEntityFromXmi)
			throws //DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, DynamicExtensionsSystemException,
			BizLogicException
	{
		//
		//        // Get instance of static entity from entity cache maintained by caB2B code
		//        EntityInterface staticEntity = EntityCacheFactory.getInstance()
		//                .getEntityById(staticEntityId);
		//
		//        //Get dynamic Entity from entity Manger
		//        EntityInterface dynamicEntity = (entityManager
		//                .getContainerByIdentifier(dynamicEntityId.toString()))
		//                .getEntity();

		//This change is done because when the hierarchy of objects grow in dynamic extensions then NonUniqueObjectException is thrown.
		//So static entity and dynamic entity are brought in one session and then associated.

		Session session = null;
		try
		{
			session = DBUtil.currentSession();
		}
		catch (HibernateException e1)
		{
			// TODO Auto-generated catch block
			Logger.out.debug(e1.getMessage(),e1);
			throw new BizLogicException("", e1);
		}
		AssociationInterface association = null;
		EntityInterface staticEntity = null;
		EntityInterface dynamicEntity = null;
		try
		{
			staticEntity = (EntityInterface) session.load(Entity.class, staticEntityId);
			dynamicEntity = (EntityInterface) ((Container) session.load(Container.class,
					dynamicEntityId)).getAbstractEntity();
			//			Get entitygroup that is used by caB2B for path finder purpose.

			//			Commented this line since performance issue for Bug 6433
			//EntityGroupInterface entityGroupInterface = Utility.getEntityGroup(staticEntity);
			//List<EntityInterface> processedEntityList = new ArrayList<EntityInterface>();

			//addCatissueGroup(dynamicEntity, entityGroupInterface,processedEntityList);

			//Create source role and target role for the association
			String roleName = staticEntityId.toString().concat("_").concat(
					dynamicEntityId.toString());
			RoleInterface sourceRole = getRole(AssociationType.CONTAINTMENT, roleName,
					Cardinality.ZERO, Cardinality.ONE);
			RoleInterface targetRole = getRole(AssociationType.CONTAINTMENT, roleName,
					Cardinality.ZERO, Cardinality.MANY);

			//Create association with the created source and target roles.
			association = getAssociation(dynamicEntity, AssociationDirection.SRC_DESTINATION,
					roleName, sourceRole, targetRole);

			//Create constraint properties for the created association.
			ConstraintPropertiesInterface constraintProperties = getConstraintProperties(
					staticEntity, dynamicEntity);
			association.setConstraintProperties(constraintProperties);

			//Add association to the static entity and save it.
			staticEntity.addAssociation(association);
			Long start = Long.valueOf(System.currentTimeMillis());

			staticEntity = EntityManager.getInstance().persistEntityMetadataForAnnotation(
					staticEntity, true, false, association);

			Long end = Long.valueOf(System.currentTimeMillis());
			Logger.out.info("Time required to persist one entity is " + (end - start) / 1000 + "seconds");
			//Add the column related to the association to the entity table of the associated entities.
			EntityManager.getInstance().addAssociationColumn(association);

			//			Collection<AssociationInterface> staticEntityAssociation = staticEntity
			//					.getAssociationCollection();
			//			for (AssociationInterface tempAssociation : staticEntityAssociation)
			//			{
			//				if (tempAssociation.getName().equals(association.getName()))
			//				{
			//					association = tempAssociation;
			//					break;
			//				}
			//			}

			Set<PathObject> processedPathList = new HashSet<PathObject>();
			addQueryPathsForAllAssociatedEntities(dynamicEntity, staticEntity, association.getId(),
					staticEntity.getId(), processedPathList);

			addEntitiesToCache(isEntityFromXmi, dynamicEntity, staticEntity);
		}
		catch (HibernateException e1)
		{
			// TODO Auto-generated catch block
			Logger.out.debug(e1.getMessages(),e1);
			throw new BizLogicException("", e1);
		}
		finally
		{
			try
			{
				DBUtil.closeSession();
			}
			catch (HibernateException e)
			{
				// TODO Auto-generated catch block
				Logger.out.debug(e.getMessage(),e);
				throw new BizLogicException("", e);

			}
		}
		return association.getId();
	}

	/**
	 * @param dynamicEntity
	 * @param staticEntity
	 */

	/*
	 *Commented out by Baljeet 
	 */
	/*public static void addCatissueGroup(EntityInterface dynamicEntity, EntityGroupInterface entityGroupInterface,List<EntityInterface> processedEntityList)
	{
		if (processedEntityList.contains(dynamicEntity))
		{
			return;
		}
		else
		{
			processedEntityList.add(dynamicEntity);
		}

		//Add the entity group to the dynamic entity and all it's associated entities.
		if (!checkBaseEntityGroup(dynamicEntity.getEntityGroup()))
		{
			dynamicEntity.setEntityGroup(entityGroupInterface);
		}
		Collection<AssociationInterface> associationCollection = dynamicEntity
				.getAssociationCollection();

		for (AssociationInterface associationInteface : associationCollection)
		{
			addCatissueGroup(associationInteface.getTargetEntity(), entityGroupInterface, processedEntityList);
			//associationInteface.getTargetEntity().addEntityGroupInterface(entityGroupInterface);
		}
	}*/

	/**
	 * @param entityGroupColl
	 * @return
	 */

	/*
	 * Commented out By Baljeet
	 */
	/*public static boolean checkBaseEntityGroup(EntityGroupInterface entityGroup)
	{
		if (entityGroup.getId().intValue() == Constants.CATISSUE_ENTITY_GROUP)
		{
			return true;
		}

		return false;
	}*/

	/**
	 * @param dynamicEntity
	 * @param staticEntity
	 * @param associationId
	 * @param isEntityFromXmi
	 * @throws BizLogicException
	 */
	public static void addQueryPathsForAllAssociatedEntities(EntityInterface dynamicEntity,
			EntityInterface staticEntity, Long associationId, Long staticEntityId,
			Set<PathObject> processedPathList) throws BizLogicException
	{
		if (staticEntity != null)
		{
			PathObject pathObject = new PathObject();
			pathObject.setSourceEntity(staticEntity);
			pathObject.setTargetEntity(dynamicEntity);

			if (processedPathList.contains(pathObject))
			{
				return;
			}
			else
			{
				processedPathList.add(pathObject);
			}

			AnnotationUtil.addPathsForQuery(staticEntity.getId(), dynamicEntity.getId(),
					staticEntityId, associationId);
		}

		Collection<AssociationInterface> associationCollection = dynamicEntity
				.getAssociationCollection();
		for (AssociationInterface associationInteface : associationCollection)
		{
			addQueryPathsForAllAssociatedEntities(associationInteface.getTargetEntity(),
					dynamicEntity, associationInteface.getId(), staticEntityId, processedPathList);

			//			AnnotationUtil.addPathsForQuery(dynamicEntity.getId(), associationInteface
			//					.getTargetEntity().getId(), associationInteface.getId());
		}
	}

	/**
	 * @param isEntityFromXmi
	 * @param dynamicEntity
	 * @param staticEntity
	 * @throws BizLogicException
	 */
	public static void addEntitiesToCache(boolean isEntityFromXmi, EntityInterface dynamicEntity,
			EntityInterface staticEntity) throws BizLogicException
	{
		if (!isEntityFromXmi)
		{
			Connection conn = null;
			try
			{
				InitialContext ctx = new InitialContext();
				String DATASOURCE_JNDI_NAME = "java:/catissuecore";
				DataSource dataSource = (DataSource) ctx.lookup(DATASOURCE_JNDI_NAME);
				conn = dataSource.getConnection();
				PathFinder.getInstance().refreshCache(conn, true);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				Logger.out.debug(e.getMessage(),e);
			}
			finally
			{
				try
				{
					if (conn != null)
					{
						conn.close();
					}
				}
				catch (HibernateException e)
				{
					// TODO Auto-generated catch block
					Logger.out.debug(e.getMessage(),e);
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					Logger.out.debug(e.getMessage(),e);
				}
			}

		}
	}

	/**
	 * @param staticEntity
	 * @param dynamicEntity
	 * @return
	 */
	private static ConstraintPropertiesInterface getConstraintProperties(
			EntityInterface staticEntity, EntityInterface dynamicEntity)
	{
		ConstraintPropertiesInterface constrProp = DomainObjectFactory.getInstance()
				.createConstraintProperties();
		constrProp.setName(dynamicEntity.getTableProperties().getName());
		constrProp.setTargetEntityKey("DYEXTN_AS_" + staticEntity.getId().toString() + "_"
				+ dynamicEntity.getId().toString());
		constrProp.setSourceEntityKey(null);
		return constrProp;
	}

	/**
	 * @param targetEntity
	 * @param associationDirection
	 * @param assoName
	 * @param sourceRole
	 * @param targetRole
	 * @return
	 */
	private static AssociationInterface getAssociation(EntityInterface targetEntity,
			AssociationDirection associationDirection, String assoName, RoleInterface sourceRole,
			RoleInterface targetRole)
	{
		AssociationInterface association = DomainObjectFactory.getInstance().createAssociation();
		association.setTargetEntity(targetEntity);
		association.setAssociationDirection(associationDirection);
		association.setName(assoName);
		association.setSourceRole(sourceRole);
		association.setTargetRole(targetRole);
		return association;
	}

	/**
	 * @param associationType associationType
	 * @param name name
	 * @param minCard  minCard
	 * @param maxCard maxCard
	 * @return  RoleInterface
	 */
	private static RoleInterface getRole(AssociationType associationType, String name,
			Cardinality minCard, Cardinality maxCard)
	{
		RoleInterface role = DomainObjectFactory.getInstance().createRole();
		role.setAssociationsType(associationType);
		role.setName(name);
		role.setMinimumCardinality(minCard);
		role.setMaximumCardinality(maxCard);
		return role;
	}

	/**
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param deAssociationID
	 */
	public static void addPathsForQuery(Long staticEntityId, Long dynamicEntityId,
			Long hookEntityId, Long deAssociationID)
	{
		Long maxPathId = getMaxId("path_id", "path");
		maxPathId += 1;
		insertNewPaths(maxPathId, staticEntityId, dynamicEntityId, deAssociationID);
		if (hookEntityId != null && staticEntityId != hookEntityId)
		{
			maxPathId += 1;
			addPathFromStaticEntity(maxPathId, hookEntityId, staticEntityId, dynamicEntityId,
					deAssociationID);
		}
	}

	/**
	 *
	 * @param hookEntityId
	 */
	private static void addPathFromStaticEntity(Long maxPathId, Long hookEntityId,
			Long previousDynamicEntity, Long dynamicEntityId, Long deAssociationId)
	{
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		String query = "";
		Connection conn = null;
		try
		{
			conn = DBUtil.getConnection();
			query = "select ASSOCIATION_ID from INTRA_MODEL_ASSOCIATION where DE_ASSOCIATION_ID="
					+ deAssociationId;
			statement = conn.prepareStatement(query);
			resultSet = statement.executeQuery();
			resultSet.next();
			Long intraModelAssociationId = resultSet.getLong(1);

			query = "select INTERMEDIATE_PATH from path where FIRST_ENTITY_ID=" + hookEntityId
					+ " and LAST_ENTITY_ID=" + previousDynamicEntity;
			statement = conn.prepareStatement(query);
			resultSet = statement.executeQuery();
			resultSet.next();
			String path = resultSet.getString(1);
			path = path.concat("_").concat(intraModelAssociationId.toString());

			query = "insert into path (PATH_ID, FIRST_ENTITY_ID,INTERMEDIATE_PATH, LAST_ENTITY_ID) values (?,?,?,?)";
			statement = conn.prepareStatement(query);

			statement.setLong(1, maxPathId);
			statement.setLong(2, hookEntityId);
			statement.setString(3, path);
			statement.setLong(4, dynamicEntityId);
			statement.execute();

			conn.commit();
		}
		catch (SQLException e)
		{
			Logger.out.debug(e.getMessage(),e);
		}
		finally
		{
			try
			{
				resultSet.close();
				statement.close();
				DBUtil.closeConnection();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				Logger.out.debug(e.getMessage(),e);
			}

		}
	}

	/**
	 * @param maxPathId
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param deAssociationID
	 */
	private static void insertNewPaths(Long maxPathId, Long staticEntityId, Long dynamicEntityId,
			Long deAssociationID)
	{
		// StringBuffer query = new StringBuffer();
		Long intraModelAssociationId = getMaxId("ASSOCIATION_ID", "ASSOCIATION");
		intraModelAssociationId += 1;
		Connection conn = null;
		try
		{
			conn = DBUtil.getConnection();

			String associationQuery = "insert into ASSOCIATION (ASSOCIATION_ID, ASSOCIATION_TYPE) values ("
					+ intraModelAssociationId
					+ ","
					+ PathConstants.AssociationType.INTRA_MODEL_ASSOCIATION.getValue() + ")";
			String intraModelQuery = "insert into INTRA_MODEL_ASSOCIATION (ASSOCIATION_ID, DE_ASSOCIATION_ID) values ("
					+ intraModelAssociationId + "," + deAssociationID + ")";
			String directPathQuery = "insert into PATH (PATH_ID, FIRST_ENTITY_ID,INTERMEDIATE_PATH, LAST_ENTITY_ID) values ("
					+ maxPathId
					+ ","
					+ staticEntityId
					+ ",'"
					+ intraModelAssociationId
					+ "',"
					+ dynamicEntityId + ")";

			List<String> list = new ArrayList<String>();
			list.add(associationQuery);
			list.add(intraModelQuery);
			list.add(directPathQuery);

			executeQuery(conn, list);

			//addIndirectPaths(maxPathId, staticEntityId, dynamicEntityId, intraModelAssociationId,
			//conn);
			conn.commit();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			Logger.out.debug(e.getMessage(),e);
		}
		finally
		{
			try
			{
				DBUtil.closeConnection();
			}
			catch (HibernateException e)
			{
				// TODO Auto-generated catch block
				Logger.out.debug(e.getMessage(),e);
			}
		}
	}

	/**
	 * @param maxPathId
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @param intraModelAssociationId
	 * @param conn
	 * @throws SQLException
	 */
	/*	private static void addIndirectPaths(Long maxPathId, Long staticEntityId, Long dynamicEntityId,
				Long intraModelAssociationId, Connection conn)

		{
			ResultSet resultSet = null;
			PreparedStatement statement = null;
			String query = "";
			try
			{
				//resultSet = getIndirectPaths(conn, staticEntityId);
				query = "select FIRST_ENTITY_ID,INTERMEDIATE_PATH from path where LAST_ENTITY_ID="
						+ staticEntityId;
				statement = conn.prepareStatement(query);
				resultSet = statement.executeQuery();


				query = "insert into path (PATH_ID, FIRST_ENTITY_ID,INTERMEDIATE_PATH, LAST_ENTITY_ID) values (?,?,?,?)";
				statement = conn.prepareStatement(query);
				while (resultSet.next())
				{

					Long firstEntityId = resultSet.getLong(1);
					String path = resultSet.getString(2);
					path = path.concat("_").concat(intraModelAssociationId.toString());

					statement.setLong(1, maxPathId);
					maxPathId++;
					statement.setLong(2, firstEntityId);
					statement.setString(3, path);
					statement.setLong(4, dynamicEntityId);
					statement.execute();
					statement.clearParameters();
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					resultSet.close();
					statement.close();
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	*/
	//	/**
	//	 * @param conn
	//	 * @param staticEntityId
	//	 * @return
	//	 * @throws SQLException
	//	 */
	//	private static ResultSet getIndirectPaths(Connection conn, Long staticEntityId)
	//	{
	//		String query = "select FIRST_ENTITY_ID,INTERMEDIATE_PATH from path where LAST_ENTITY_ID="
	//				+ staticEntityId;
	//		java.sql.PreparedStatement statement = null;
	//		ResultSet resultSet = null;
	//		try
	//		{
	//			statement = conn.prepareStatement(query);
	//			resultSet = statement.executeQuery();
	//		}
	//		catch (SQLException e)
	//		{
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//
	//		finally
	//		{
	//			try
	//			{
	//
	//				statement.close();
	//			}
	//			catch (SQLException e)
	//			{
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//
	//		}
	//		return resultSet;
	//	}
	/**
	 * @param conn
	 * @param queryList
	 * @throws SQLException
	 */
	private static void executeQuery(Connection conn, List<String> queryList)
	{
		Statement statement = null;
		try
		{
			statement = conn.createStatement();
			for (String query : queryList)
			{
				statement.execute(query);
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			Logger.out.debug(e.getMessage(),e);
		}

		finally
		{
			try
			{
				statement.close();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				Logger.out.debug(e.getMessage(),e);
			}
		}
	}

	/**
	 * @param columnName
	 * @param tableName
	 * @return
	 */
	private static Long getMaxId(String columnName, String tableName)
	{
		String query = "select max(" + columnName + ") from " + tableName;
		//		HibernateDAO hibernateDAO = (HibernateDAO) DAOFactory.getInstance().getDAO(
		//				Constants.HIBERNATE_DAO);
		java.sql.PreparedStatement statement = null;
		ResultSet resultSet = null;
		Connection conn = null;
		try
		{
			conn = DBUtil.getConnection();
			statement = conn.prepareStatement(query);
			resultSet = statement.executeQuery();
			resultSet.next();
			Long maxId = resultSet.getLong(1);

			return maxId;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			Logger.out.debug(e.getMessage(),e);
		}
		finally
		{
			try
			{
				resultSet.close();
				statement.close();
				DBUtil.closeConnection();

			}
			catch (HibernateException e)
			{
				// TODO Auto-generated catch block
				Logger.out.debug(e.getMessage(),e);
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				Logger.out.debug(e.getMessage(),e);
			}
		}
		return null;
	}

	/**
	 * @param entity_name_participant
	 * @return
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 */
	public static Long getEntityId(String entityName) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		if (entityName != null)
		{
			EntityManagerInterface entityManager = EntityManager.getInstance();
			EntityInterface entity;
			entity = entityManager.getEntityByName(entityName);
			if (entity != null)
			{
				return entity.getId();
			}
		}
		return Long.valueOf(0);
	}

}