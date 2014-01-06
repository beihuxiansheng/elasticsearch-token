begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.stresstest.gcbehavior
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|stresstest
operator|.
name|gcbehavior
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
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
name|settings
operator|.
name|ImmutableSettings
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|NodeBuilder
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|FilterBuilders
operator|.
name|rangeFilter
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|filteredQuery
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|matchAllQuery
import|;
end_import

begin_class
DECL|class|FilterCacheGcStress
specifier|public
class|class
name|FilterCacheGcStress
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|Settings
name|settings
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"gateway.type"
argument_list|,
literal|"none"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Node
name|node
init|=
name|NodeBuilder
operator|.
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|node
argument_list|()
decl_stmt|;
specifier|final
name|Client
name|client
init|=
name|node
operator|.
name|client
argument_list|()
decl_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForYellowStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
specifier|final
name|AtomicBoolean
name|stop
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|Thread
name|indexingThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|stop
operator|.
name|get
argument_list|()
condition|)
block|{
name|client
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
name|System
operator|.
name|currentTimeMillis
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
block|}
decl_stmt|;
name|indexingThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
name|searchThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|stop
operator|.
name|get
argument_list|()
condition|)
block|{
name|client
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|filteredQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|,
name|rangeFilter
argument_list|(
literal|"field"
argument_list|)
operator|.
name|from
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
literal|1000000
argument_list|)
argument_list|)
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
block|}
decl_stmt|;
name|searchThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

