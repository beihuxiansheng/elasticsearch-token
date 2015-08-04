begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.tasks
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|tasks
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
DECL|class|PendingTasksBlocksIT
specifier|public
class|class
name|PendingTasksBlocksIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Test
DECL|method|testPendingTasksWithBlocks
specifier|public
name|void
name|testPendingTasksWithBlocks
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
comment|// This test checks that the Pending Cluster Tasks operation is never blocked, even if an index is read only or whatever.
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
argument_list|,
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
name|PendingClusterTasksResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|preparePendingClusterTasks
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|response
operator|.
name|getPendingTasks
argument_list|()
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
try|try
block|{
name|setClusterReadOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PendingClusterTasksResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|preparePendingClusterTasks
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|response
operator|.
name|getPendingTasks
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

