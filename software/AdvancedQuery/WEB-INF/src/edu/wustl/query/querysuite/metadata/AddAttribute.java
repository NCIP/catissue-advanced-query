//
//package edu.wustl.query.querysuite.metadata;
//
//import java.io.IOException;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//
//import edu.wustl.dao.JDBCDAO;
//import edu.wustl.dao.exception.DAOException;
//
//public class AddAttribute extends BaseMetadata
//{
//
//	//private Connection connection = null;
//	private JDBCDAO jdbcdao = null;
//
//	public void addAttribute() throws SQLException, IOException, DAOException
//	{
//		//Statement stmt = connection.createStatement();
//		Set<String> keySet = entityNameAttributeNameMap.keySet();
//		Iterator<String> iterator = keySet.iterator();
//		while (iterator.hasNext())
//		{
//			String entityName = iterator.next();
//			List<String> attributes = entityNameAttributeNameMap.get(entityName);
//			for (String attr : attributes)
//			{
//				//stmt = connection.createStatement();
//				String sql = "select max(identifier) from dyextn_abstract_metadata";
//				//ResultSet rs = stmt.executeQuery(sql);
//				ResultSet rs = jdbcdao.getQueryResultSet(sql);
//				int nextIdOfAbstractMetadata = 0;
//				if (rs.next())
//				{
//					int maxId = rs.getInt(1);
//					nextIdOfAbstractMetadata = maxId + 1;
//				}
//
//				int nextIdAttrTypeInfo = 0;
//				sql = "select max(identifier) from dyextn_attribute_type_info";
//				//rs = stmt.executeQuery(sql);
//				 rs = jdbcdao.getQueryResultSet(sql);
//				if (rs.next())
//				{
//					int maxId = rs.getInt(1);
//					nextIdAttrTypeInfo = maxId + 1;
//				}
//
//				int nextIdDatabaseproperties = 0;
//				sql = "select max(identifier) from dyextn_database_properties";
//				//rs = stmt.executeQuery(sql);
//				 rs = jdbcdao.getQueryResultSet(sql);
//				if (rs.next())
//				{
//					int maxId = rs.getInt(1);
//					nextIdDatabaseproperties = maxId + 1;
//				}
//
//				sql = "INSERT INTO dyextn_abstract_metadata values(" + nextIdOfAbstractMetadata
//						+ ",NULL,NULL,NULL,'" + attr + "',null)";
//				//UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());
//				UpdateMetadataUtil.executeInsertSQL(sql, jdbcdao);
//				sql = "INSERT INTO DYEXTN_BASE_ABSTRACT_ATTRIBUTE values("
//						+ nextIdOfAbstractMetadata + ")";
//				//UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());
//				UpdateMetadataUtil.executeInsertSQL(sql, jdbcdao);
//
////				int entityId = UpdateMetadataUtil.getEntityIdByName(entityName, connection
////						.createStatement());
//				int entityId= UpdateMetadataUtil.getEntityIdByName(entityName, jdbcdao);
//				sql = "INSERT INTO dyextn_attribute values (" + nextIdOfAbstractMetadata + ","
//						+ entityId + ")";
//				//UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());
//				UpdateMetadataUtil.executeInsertSQL(sql, jdbcdao);
//				String primaryKey = attributePrimarkeyMap.get(attr);
//				sql = "insert into dyextn_primitive_attribute (IDENTIFIER,IS_COLLECTION,IS_IDENTIFIED,IS_PRIMARY_KEY,IS_NULLABLE)"
//						+ " values (" + nextIdOfAbstractMetadata + ",0,NULL," + primaryKey + ",1)";
//				//UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());
//				UpdateMetadataUtil.executeInsertSQL(sql, jdbcdao);
//
//				sql = "insert into dyextn_attribute_type_info (IDENTIFIER,PRIMITIVE_ATTRIBUTE_ID) values ("
//						+ nextIdAttrTypeInfo + "," + nextIdOfAbstractMetadata + ")";
//				//UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());
//				UpdateMetadataUtil.executeInsertSQL(sql, jdbcdao);
//
//				String dataType = getDataTypeOfAttribute(attr, attributeDatatypeMap);
//
//				if (!dataType.equalsIgnoreCase("String") && !dataType.equalsIgnoreCase("date"))
//				{
//					sql = "insert into dyextn_numeric_type_info (IDENTIFIER,MEASUREMENT_UNITS,DECIMAL_PLACES,NO_DIGITS) values ("
//							+ nextIdAttrTypeInfo + ",NULL,0,NULL)";
//					//UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());
//					UpdateMetadataUtil.executeInsertSQL(sql, jdbcdao);
//				}
//
//				if (dataType.equalsIgnoreCase("string"))
//				{
//					sql = "insert into dyextn_string_type_info (IDENTIFIER) values ("
//							+ nextIdAttrTypeInfo + ")";
//				}
//				else if (dataType.equalsIgnoreCase("double"))
//				{
//					sql = "insert into dyextn_double_type_info (IDENTIFIER) values ("
//							+ nextIdAttrTypeInfo + ")";
//				}
//				else if (dataType.equalsIgnoreCase("int"))
//				{
//					sql = "insert into dyextn_integer_type_info (IDENTIFIER) values ("
//							+ nextIdAttrTypeInfo + ")";
//				}
//				else if (dataType.equalsIgnoreCase("long"))
//				{
//					sql = "insert into dyextn_long_type_info (IDENTIFIER) values ("
//							+ nextIdAttrTypeInfo + ")";
//				}
//				else if (dataType.equalsIgnoreCase("date"))
//				{
//					sql = "insert into dyextn_date_type_info (IDENTIFIER,FORMAT) values ("
//							+ nextIdAttrTypeInfo + ",'MM-dd-yyyy')";
//				}
//				//UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());
//				UpdateMetadataUtil.executeInsertSQL(sql, jdbcdao);
//
//				String columnName = getColumnNameOfAttribue(attr, attributeColumnNameMap);
//				sql = "insert into dyextn_database_properties (IDENTIFIER,NAME) values ("
//						+ nextIdDatabaseproperties + ",'" + columnName + "')";
//				//UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());
//				UpdateMetadataUtil.executeInsertSQL(sql, jdbcdao);
//
//				sql = "insert into dyextn_column_properties (IDENTIFIER,PRIMITIVE_ATTRIBUTE_ID) values ("
//						+ nextIdDatabaseproperties + "," + nextIdOfAbstractMetadata + ")";
//				//UpdateMetadataUtil.executeInsertSQL(sql, connection.createStatement());
//				UpdateMetadataUtil.executeInsertSQL(sql, jdbcdao);
//			}
//		}
//	}
//
//	private String getColumnNameOfAttribue(String attr,
//			HashMap<String, String> attributeColumnNameMap)
//	{
//		return attributeColumnNameMap.get(attr);
//	}
//
//	private String getDataTypeOfAttribute(String attr, HashMap<String, String> attributeDatatypeMap)
//	{
//		return attributeDatatypeMap.get(attr);
//	}
//
//	private void populateEntityAttributeMap()
//	{
//		List<String> attributes = new ArrayList<String>();
//		attributes.add("quantity");
//		entityNameAttributeNameMap.put(
//				"edu.wustl.catissuecore.domain.DistributionSpecimenRequirement", attributes);
//
//		attributes = new ArrayList<String>();
//		attributes.add("unsignedConsentDocumentURL");
//		entityNameAttributeNameMap.put("edu.wustl.catissuecore.domain.CollectionProtocol",
//				attributes);
//
//		attributes = new ArrayList<String>();
//		attributes.add("signedConsentDocumentURL");
//		attributes.add("consentSignatureDate");
//		//attributes.add("barcode");
//		entityNameAttributeNameMap.put(
//				"edu.wustl.catissuecore.domain.CollectionProtocolRegistration", attributes);
//
//		/*attributes = new ArrayList<String>();
//		attributes.add("barcode");
//		entityNameAttributeNameMap.put("edu.wustl.catissuecore.domain.SpecimenCollectionGroup",attributes);*/
//	}
//
//	private void populateAttributeColumnNameMap()
//	{
//		attributeColumnNameMap.put("quantity", "QUANTITY");
//		attributeColumnNameMap.put("unsignedConsentDocumentURL", "UNSIGNED_CONSENT_DOC_URL");
//		attributeColumnNameMap.put("signedConsentDocumentURL", "CONSENT_DOC_URL");
//		attributeColumnNameMap.put("consentSignatureDate", "CONSENT_SIGN_DATE");
//		//attributeColumnNameMap.put("barcode", "BARCODE");
//	}
//
//	private void populateAttributeDatatypeMap()
//	{
//		attributeDatatypeMap.put("quantity", "double");
//		attributeDatatypeMap.put("unsignedConsentDocumentURL", "string");
//		attributeDatatypeMap.put("signedConsentDocumentURL", "string");
//		attributeDatatypeMap.put("consentSignatureDate", "date");
//		//attributeDatatypeMap.put("barcode", "string");
//	}
//
//	private void populateAttributePrimaryKeyMap()
//	{
//		attributePrimarkeyMap.put("quantity", "0");
//		attributePrimarkeyMap.put("unsignedConsentDocumentURL", "0");
//		attributePrimarkeyMap.put("signedConsentDocumentURL", "0");
//		attributePrimarkeyMap.put("consentSignatureDate", "0");
//		//attributePrimarkeyMap.put("barcode", "0");
//	}
//
//	private void populateEntityList()
//	{
//		entityList.add("edu.wustl.catissuecore.domain.DistributionSpecimenRequirement");
//		entityList.add("edu.wustl.catissuecore.domain.CollectionProtocol");
//		entityList.add("edu.wustl.catissuecore.domain.CollectionProtocolRegistration");
//		//entityList.add("edu.wustl.catissuecore.domain.SpecimenCollectionGroup");
//	}
//
//	//public AddAttribute(Connection connection) throws SQLException
//	public AddAttribute(JDBCDAO jdbcdao) throws SQLException
//	{
//		//this.connection = connection;
//		this.jdbcdao = jdbcdao;
//		populateEntityList();
//		populateEntityAttributeMap();
//		populateAttributeColumnNameMap();
//		populateAttributeDatatypeMap();
//		populateAttributePrimaryKeyMap();
//	}
////
////	public AddAttribute(Connection connection,
////			HashMap<String, List<String>> entityNameAttributeNameMap,
////			HashMap<String, String> attributeColumnNameMap,
////			HashMap<String, String> attributeDatatypeMap,
////			HashMap<String, String> attributePrimarkeyMap, List<String> entityList)
//
//	public AddAttribute(JDBCDAO jdbcdao,
//			HashMap<String, List<String>> entityNameAttributeNameMap,
//			HashMap<String, String> attributeColumnNameMap,
//			HashMap<String, String> attributeDatatypeMap,
//			HashMap<String, String> attributePrimarkeyMap, List<String> entityList)
//	{
//		//this.connection = connection;
//		this.jdbcdao = jdbcdao;
//		this.entityNameAttributeNameMap = entityNameAttributeNameMap;
//		this.attributeColumnNameMap = attributeColumnNameMap;
//		this.attributeDatatypeMap = attributeDatatypeMap;
//		this.attributePrimarkeyMap = attributePrimarkeyMap;
//		this.entityList = entityList;
//	}
//}