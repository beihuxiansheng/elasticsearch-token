begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|SearchResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticsearchIntegrationTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertNoFailures
import|;
end_import

begin_comment
comment|/**  * Various tests for Groovy scripting  */
end_comment

begin_class
DECL|class|GroovyScriptTests
specifier|public
class|class
name|GroovyScriptTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|testGroovyBigDecimalTransformation
specifier|public
name|void
name|testGroovyBigDecimalTransformation
parameter_list|()
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"doc"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|5
argument_list|)
operator|.
name|setRefresh
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// Test that something that would usually be a BigDecimal is transformed into a Double
name|assertScript
argument_list|(
literal|"def n = 1.23; assert n instanceof Double;"
argument_list|)
expr_stmt|;
name|assertScript
argument_list|(
literal|"def n = 1.23G; assert n instanceof Double;"
argument_list|)
expr_stmt|;
name|assertScript
argument_list|(
literal|"def n = BigDecimal.ONE; assert n instanceof BigDecimal;"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertScript
specifier|public
name|void
name|assertScript
parameter_list|(
name|String
name|script
parameter_list|)
block|{
name|SearchResponse
name|resp
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"query\": {\"match_all\": {}},"
operator|+
literal|"\"sort\":{\"_script\": {\"script\": \""
operator|+
name|script
operator|+
literal|"; 1\", \"type\": \"number\", \"lang\": \"groovy\"}}}"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNoFailures
argument_list|(
name|resp
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

