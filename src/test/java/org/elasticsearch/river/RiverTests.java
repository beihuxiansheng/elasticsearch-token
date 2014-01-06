begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.river
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|river
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|get
operator|.
name|GetResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|index
operator|.
name|IndexResponse
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|ElasticsearchIntegrationTest
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_class
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|Scope
operator|.
name|TEST
argument_list|)
DECL|class|RiverTests
specifier|public
class|class
name|RiverTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|testRiverStart
specifier|public
name|void
name|testRiverStart
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|riverName
init|=
literal|"test_river"
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"-->  creating river [{}]"
argument_list|,
name|riverName
argument_list|)
expr_stmt|;
name|IndexResponse
name|indexResponse
init|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
name|RiverIndexName
operator|.
name|Conf
operator|.
name|DEFAULT_INDEX_NAME
argument_list|,
name|riverName
argument_list|,
literal|"_meta"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"type"
argument_list|,
name|TestRiverModule
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|indexResponse
operator|.
name|isCreated
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"-->  checking that river [{}] was created"
argument_list|,
name|riverName
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|awaitBusy
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|GetResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
name|RiverIndexName
operator|.
name|Conf
operator|.
name|DEFAULT_INDEX_NAME
argument_list|,
name|riverName
argument_list|,
literal|"_status"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|response
operator|.
name|isExists
argument_list|()
return|;
block|}
block|}
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

