begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.optimize
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|optimize
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESIntegTestCase
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
name|ESIntegTestCase
operator|.
name|ClusterScope
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
name|Arrays
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
operator|.
name|*
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
name|assertBlocked
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
name|ESIntegTestCase
operator|.
name|Scope
operator|.
name|TEST
argument_list|)
DECL|class|OptimizeBlocksIT
specifier|public
class|class
name|OptimizeBlocksIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Test
DECL|method|testOptimizeWithBlocks
specifier|public
name|void
name|testOptimizeWithBlocks
parameter_list|()
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|NumShards
name|numShards
init|=
name|getNumShards
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|int
name|docs
init|=
name|between
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|docs
condition|;
name|i
operator|++
control|)
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"test"
argument_list|,
literal|"init"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
comment|// Request is not blocked
for|for
control|(
name|String
name|blockSetting
range|:
name|Arrays
operator|.
name|asList
argument_list|(
name|SETTING_BLOCKS_READ
argument_list|,
name|SETTING_BLOCKS_WRITE
argument_list|)
control|)
block|{
try|try
block|{
name|enableIndexBlock
argument_list|(
literal|"test"
argument_list|,
name|blockSetting
argument_list|)
expr_stmt|;
name|OptimizeResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOptimize
argument_list|(
literal|"test"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertNoFailures
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getSuccessfulShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|numShards
operator|.
name|totalNumShards
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|disableIndexBlock
argument_list|(
literal|"test"
argument_list|,
name|blockSetting
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Request is blocked
for|for
control|(
name|String
name|blockSetting
range|:
name|Arrays
operator|.
name|asList
argument_list|(
name|SETTING_READ_ONLY
argument_list|,
name|SETTING_BLOCKS_METADATA
argument_list|)
control|)
block|{
try|try
block|{
name|enableIndexBlock
argument_list|(
literal|"test"
argument_list|,
name|blockSetting
argument_list|)
expr_stmt|;
name|assertBlocked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOptimize
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|disableIndexBlock
argument_list|(
literal|"test"
argument_list|,
name|blockSetting
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Optimizing all indices is blocked when the cluster is read-only
try|try
block|{
name|OptimizeResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOptimize
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertNoFailures
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getSuccessfulShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|numShards
operator|.
name|totalNumShards
argument_list|)
argument_list|)
expr_stmt|;
name|setClusterReadOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertBlocked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareFlush
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|setClusterReadOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

