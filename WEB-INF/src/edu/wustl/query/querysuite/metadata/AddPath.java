
package edu.wustl.query.querysuite.metadata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddPath
{

	static Map<String, List<String>> superClassAndSubClassesMap = new HashMap<String, List<String>>();
	static Map<String, List<String>> superClassAndAssociationsMap = new HashMap<String, List<String>>();
	static Map<String, List<String>> eventParametersSubClassesMap = new HashMap<String, List<String>>();
	int identifier = 0;

	public static void initData()
	{
//		List<String> subClassesList = new ArrayList<String>();
//		subClassesList.add("edu.wustl.catissuecore.domain.CellSpecimen");
//		subClassesList.add("edu.wustl.catissuecore.domain.FluidSpecimen");
//		subClassesList.add("edu.wustl.catissuecore.domain.MolecularSpecimen");
//		subClassesList.add("edu.wustl.catissuecore.domain.TissueSpecimen");
//		superClassAndSubClassesMap.put("edu.wustl.catissuecore.domain.Specimen", subClassesList);
//
//		List<String> associationsList = new ArrayList<String>();
//		associationsList.add("edu.wustl.catissuecore.domain.SpecimenRequirement");
//		associationsList.add("edu.wustl.catissuecore.domain.SpecimenPosition");
//		associationsList.add("edu.wustl.catissuecore.domain.SpecimenArrayContent");
//		associationsList.add("edu.wustl.catissuecore.domain.DistributedItem");
//		superClassAndAssociationsMap
//				.put("edu.wustl.catissuecore.domain.Specimen", associationsList);

		

	}

	public List<String> getInsertPathStatements(Statement stmt, Connection connection, boolean flag)
			throws SQLException
	{
		if (flag == false)
		{
			superClassAndSubClassesMap = new HashMap<String, List<String>>(
					eventParametersSubClassesMap);
			superClassAndAssociationsMap = new HashMap<String, List<String>>();

			List<String> associationsList = new ArrayList<String>();
			associationsList.add("edu.wustl.catissuecore.domain.Specimen");
			associationsList.add("edu.wustl.catissuecore.domain.CellSpecimen");
			associationsList.add("edu.wustl.catissuecore.domain.FluidSpecimen");
			associationsList.add("edu.wustl.catissuecore.domain.MolecularSpecimen");
			associationsList.add("edu.wustl.catissuecore.domain.TissueSpecimen");
			superClassAndAssociationsMap.put(
					"edu.wustl.catissuecore.domain.SpecimenEventParameters", associationsList);
		}
		List<String> insertPathSQL = new ArrayList<String>();
		ResultSet rs;
		stmt = connection.createStatement();

		rs = stmt.executeQuery("select max(PATH_ID) from path");
		if (rs.next())
		{
			identifier = rs.getInt(1) + 1;
		}
		String sql;
		String entityId = null;
		String associatedEntityId = null;

		Set<String> keySet = superClassAndAssociationsMap.keySet();
		Iterator<String> iterator = keySet.iterator();
		while (iterator.hasNext())
		{
			String key = iterator.next();
			sql = "Select IDENTIFIER from dyextn_abstract_metadata where NAME = '" + key + "'";
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next())
			{
				entityId = String.valueOf(rs.getLong(1));

				List<String> associationsList = superClassAndAssociationsMap.get(key);
				for (String associatedEntityName : associationsList)
				{
					sql = "Select IDENTIFIER from dyextn_abstract_metadata where NAME = '"
							+ associatedEntityName + "'";
					Statement stmt4 = connection.createStatement();
					rs = stmt4.executeQuery(sql);
					if (rs.next())
					{
						associatedEntityId = String.valueOf(rs.getLong(1));

						sql = "Select INTERMEDIATE_PATH from PATH where FIRST_ENTITY_ID = "
								+ entityId + " and LAST_ENTITY_ID = " + associatedEntityId;
						Statement stmt5 = connection.createStatement();
						rs = stmt5.executeQuery(sql);

						while (rs.next())
						{
							String intermediatePathId = rs.getString(1);
							List<String> subClassList = superClassAndSubClassesMap.get(key);
							for (String subClassEntity : subClassList)
							{
								String subClassEntityId;
								Statement stmt1 = connection.createStatement();
								sql = "Select IDENTIFIER from dyextn_abstract_metadata where NAME = '"
										+ subClassEntity + "'";
								ResultSet rs1 = stmt.executeQuery(sql);
								if (rs1.next())
								{
									subClassEntityId = String.valueOf(rs1.getLong(1));
									if (key.equals(associatedEntityName))
									{
										insertPathSQL.add("insert into path values(" + identifier++
												+ "," + subClassEntityId + "," + intermediatePathId
												+ "," + subClassEntityId + ")");
									}
									else
									{
										insertPathSQL.add("insert into path values(" + identifier++
												+ "," + subClassEntityId + "," + intermediatePathId
												+ "," + associatedEntityId + ")");
									}
								}
								stmt1.close();
							}
						}
						stmt5.close();
						if (!(key.equals(associatedEntityName)))
						{
							sql = "Select INTERMEDIATE_PATH from PATH where FIRST_ENTITY_ID = "
									+ associatedEntityId + " and LAST_ENTITY_ID = " + entityId;
							Statement stmt2 = connection.createStatement();
							rs = stmt2.executeQuery(sql);
							while (rs.next())
							{
								String intermediatePathId = rs.getString(1);
								List<String> subClassList = superClassAndSubClassesMap.get(key);
								for (String subClassEntity : subClassList)
								{
									String subClassEntityId;
									Statement stmt3 = connection.createStatement();
									sql = "Select IDENTIFIER from dyextn_abstract_metadata where NAME = '"
											+ subClassEntity + "'";
									ResultSet rs1 = stmt3.executeQuery(sql);
									if (rs1.next())
									{
										subClassEntityId = String.valueOf(rs1.getLong(1));
										insertPathSQL
												.add("insert into path values(" + identifier++
														+ "," + associatedEntityId + ","
														+ intermediatePathId + ","
														+ subClassEntityId + ")");
									}
									stmt3.close();
								}
							}
							stmt2.close();
						}
					}
					stmt4.close();
				}
			}
			else
			{
				System.out.println("Entity with name : " + key + " not found");
			}
		}
		stmt.close();
		return insertPathSQL;
	}
}
