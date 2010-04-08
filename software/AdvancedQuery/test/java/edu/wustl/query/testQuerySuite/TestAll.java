
package edu.wustl.query.testQuerySuite;

/**
 *
 */

import junit.framework.Test;
import junit.framework.TestSuite;
import edu.ustl.query.util.querysuite.QueryDetailsTestCase;
import edu.ustl.query.util.querysuite.QueryModuleSqlUtilTestCase;
import edu.ustl.query.util.querysuite.QueryModuleUtilTestCase;
import edu.ustl.query.util.querysuite.TemporalQueryUtilityTestCase;
import edu.wustl.common.query.factory.AbstractQueryGeneratorFactoryTestCase;
import edu.wustl.common.query.factory.CommonObjectFactoryTestCase;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadataTestCase;
import edu.wustl.common.query.queryobject.locator.QueryNodeLocatorTestCase;
import edu.wustl.common.query.queryobject.util.QueryObjectProcessorTestCase;
import edu.wustl.query.bizlogic.CommonQueryBizLogicTestCase;
import edu.wustl.query.bizlogic.CreateQueryObjectTestCase;
import edu.wustl.query.bizlogic.DashboardBizLogicTestCase;
import edu.wustl.query.bizlogic.DefaultQueryBizLogicTestCase;
import edu.wustl.query.bizlogic.DefineGridViewBizLogicTestCase;
import edu.wustl.query.bizlogic.ExportQueryBizLogicTestCase;
import edu.wustl.query.bizlogic.QueryCsmBizLogicTestCase;
import edu.wustl.query.bizlogic.QueryOutputTreeBizLogicTestCase;
import edu.wustl.query.bizlogic.SaveQueryBizLogicTestCase;
import edu.wustl.query.bizlogic.ShareQueryBizLogicTestCase;
import edu.wustl.query.generator.SqlGeneratorGenericTestCase;
import edu.wustl.query.htmlprovider.GenerateHtmlDetailsTestCase;
import edu.wustl.query.htmlprovider.GenerateHtmlTestCase;
import edu.wustl.query.htmlprovider.HtmlProviderTestCase;
import edu.wustl.query.htmlprovider.ParseXMLFileTestCase;
import edu.wustl.query.htmlprovider.SavedQueryHtmlProviderTestCase;
import edu.wustl.query.security.QueryCsmCacheManagerTestCase;
import edu.wustl.query.util.global.UtilityTestCase;

/**
 * @author prafull_kadam
 * Test Suite for testing all Query Interface related classes.
 */
public class TestAll
{
	public static void main(String[] args)
	{
		junit.swingui.TestRunner.run(TestAll.class);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test suite for Query Interface Classes");
		suite.addTestSuite(CreateQueryObjectTestCase.class);

		suite.addTestSuite(SqlGeneratorGenericTestCase.class);
		//suite.addTestSuite(MySqlQueryGenerator.class);
		suite.addTestSuite(HtmlProviderTestCase.class);
		suite.addTestSuite(DashboardBizLogicTestCase.class);
		suite.addTestSuite(GenerateHtmlDetailsTestCase.class);
		suite.addTestSuite(GenerateHtmlTestCase.class);
		suite.addTestSuite(ParseXMLFileTestCase.class);
		suite.addTestSuite(SavedQueryHtmlProviderTestCase.class);
		suite.addTestSuite(QueryCsmBizLogicTestCase.class);
		suite.addTestSuite(SelectedColumnsMetadataTestCase.class);
		suite.addTestSuite(QueryModuleSqlUtilTestCase.class);
		suite.addTestSuite(ExportQueryBizLogicTestCase.class);
//		suite.addTestSuite(SaveQueryBizLogicTestCase.class);
		suite.addTestSuite(CommonQueryBizLogicTestCase.class);
		suite.addTestSuite(TemporalQueryUtilityTestCase.class);
		suite.addTestSuite(QueryObjectProcessorTestCase.class);
		suite.addTestSuite(QueryNodeLocatorTestCase.class);
		suite.addTestSuite(AbstractQueryGeneratorFactoryTestCase.class);
		suite.addTestSuite(CommonObjectFactoryTestCase.class);
		suite.addTestSuite(DefaultQueryBizLogicTestCase.class);
		suite.addTestSuite(ShareQueryBizLogicTestCase.class);
		suite.addTestSuite(DefineGridViewBizLogicTestCase.class);
		suite.addTestSuite(QueryCsmCacheManagerTestCase.class);
		suite.addTestSuite(UtilityTestCase.class);
		suite.addTestSuite(QueryDetailsTestCase.class);
		suite.addTestSuite(QueryModuleUtilTestCase.class);
		suite.addTestSuite(QueryOutputTreeBizLogicTestCase.class);
		return suite;
	}
}
