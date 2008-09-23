package edu.wustl.query.querysuite.metadata;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddAbstractSpecimenMetaData extends BaseMetadata
{
	private Connection connection = null;
	private Statement stmt = null;
	
	public void addAbstractPositionMetaData() throws SQLException, IOException
	{
		populateEntityList();
		populateEntityAttributeMap();
		populateAttributeColumnNameMap();
		populateAttributeDatatypeMap();
		populateAttributePrimaryKeyMap();
		
		stmt = connection.createStatement();
		AddEntity addEntity = new AddEntity(connection);
		addEntity.addEntity(entityList, "CATISSUE_ABSTRACT_SPECIMEN", "NULL", 3,1);
		AddAttribute addAttribute = new AddAttribute(connection,entityNameAttributeNameMap,attributeColumnNameMap,attributeDatatypeMap,attributePrimarkeyMap,entityList);
		addAttribute.addAttribute();
		
		String entityName = "edu.wustl.catissuecore.domain.AbstractSpecimen";
		String targetEntityName = "edu.wustl.catissuecore.domain.SpecimenEventParameters";
		
		AddAssociations addAssociations = new AddAssociations(connection);
		addAssociations.addAssociation(entityName,targetEntityName,"abstractSpecimen_SpecimenEventParameters","CONTAINTMENT","specimenEventCollection",true,"abstractSpecimen","SPECIMEN_ID",null,2,1,"BI_DIRECTIONAL");
		addAssociations.addAssociation(targetEntityName,entityName,"SpecimenEventParameters_abstractSpecimen","ASSOCIATION","abstractSpecimen",false,"","SPECIMEN_ID",null,2,1,"BI_DIRECTIONAL");
	}		
		private void populateEntityAttributeMap() 
		{
			List<String> attributes = new ArrayList<String>();
			
			attributes.add("id");
		
			entityNameAttributeNameMap.put("edu.wustl.catissuecore.domain.AbstractSpecimen",attributes);
		}
		
		private void populateAttributeColumnNameMap()
		{
			attributeColumnNameMap.put("id", "IDENTIFIER");
		}
		
		private void populateAttributeDatatypeMap()
		{
			attributeDatatypeMap.put("id", "long");
		}
		private void populateAttributePrimaryKeyMap() 
		{
			attributePrimarkeyMap.put("id", "1");
		}
		private void populateEntityList()
		{
			entityList.add("edu.wustl.catissuecore.domain.AbstractSpecimen");
		}
		public AddAbstractSpecimenMetaData(Connection connection) throws SQLException
		{
			this.connection = connection;
			this.stmt = connection.createStatement();
		}
	}
