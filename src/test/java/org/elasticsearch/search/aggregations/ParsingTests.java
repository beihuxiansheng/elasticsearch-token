begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
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
name|SearchPhaseExecutionException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|json
operator|.
name|JsonXContent
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

begin_class
DECL|class|ParsingTests
specifier|public
class|class
name|ParsingTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SearchPhaseExecutionException
operator|.
name|class
argument_list|)
DECL|method|testTwoTypes
specifier|public
name|void
name|testTwoTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"idx"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|setAggregations
argument_list|(
name|JsonXContent
operator|.
name|contentBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"in_stock"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"filter"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"range"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"stock"
argument_list|)
operator|.
name|field
argument_list|(
literal|"gt"
argument_list|,
literal|0
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"terms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"stock"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

