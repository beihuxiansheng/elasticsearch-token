begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.health
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|health
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|support
operator|.
name|IndicesOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexNameExpressionResolver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|MetaData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|IndexRoutingTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|RoutingTable
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
name|io
operator|.
name|stream
operator|.
name|BytesStreamOutput
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|allOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
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
name|empty
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
name|greaterThanOrEqualTo
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
name|is
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
name|lessThanOrEqualTo
import|;
end_import

begin_class
DECL|class|ClusterStateHealthTests
specifier|public
class|class
name|ClusterStateHealthTests
extends|extends
name|ESTestCase
block|{
DECL|field|indexNameExpressionResolver
specifier|private
specifier|final
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
init|=
operator|new
name|IndexNameExpressionResolver
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
DECL|method|testClusterHealth
specifier|public
name|void
name|testClusterHealth
parameter_list|()
throws|throws
name|IOException
block|{
name|RoutingTableGenerator
name|routingTableGenerator
init|=
operator|new
name|RoutingTableGenerator
argument_list|()
decl_stmt|;
name|RoutingTableGenerator
operator|.
name|ShardCounter
name|counter
init|=
operator|new
name|RoutingTableGenerator
operator|.
name|ShardCounter
argument_list|()
decl_stmt|;
name|RoutingTable
operator|.
name|Builder
name|routingTable
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
decl_stmt|;
name|MetaData
operator|.
name|Builder
name|metaData
init|=
name|MetaData
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|randomInt
argument_list|(
literal|4
argument_list|)
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|int
name|numberOfShards
init|=
name|randomInt
argument_list|(
literal|3
argument_list|)
operator|+
literal|1
decl_stmt|;
name|int
name|numberOfReplicas
init|=
name|randomInt
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|IndexMetaData
name|indexMetaData
init|=
name|IndexMetaData
operator|.
name|builder
argument_list|(
literal|"test_"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|settings
argument_list|(
name|settings
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
operator|.
name|numberOfShards
argument_list|(
name|numberOfShards
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
name|numberOfReplicas
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IndexRoutingTable
name|indexRoutingTable
init|=
name|routingTableGenerator
operator|.
name|genIndexRoutingTable
argument_list|(
name|indexMetaData
argument_list|,
name|counter
argument_list|)
decl_stmt|;
name|metaData
operator|.
name|put
argument_list|(
name|indexMetaData
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|routingTable
operator|.
name|add
argument_list|(
name|indexRoutingTable
argument_list|)
expr_stmt|;
block|}
name|ClusterState
name|clusterState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|ClusterName
operator|.
name|DEFAULT
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
index|[]
name|concreteIndices
init|=
name|indexNameExpressionResolver
operator|.
name|concreteIndices
argument_list|(
name|clusterState
argument_list|,
name|IndicesOptions
operator|.
name|strictExpand
argument_list|()
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
name|ClusterStateHealth
name|clusterStateHealth
init|=
operator|new
name|ClusterStateHealth
argument_list|(
name|clusterState
argument_list|,
name|concreteIndices
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"cluster status: {}, expected {}"
argument_list|,
name|clusterStateHealth
operator|.
name|getStatus
argument_list|()
argument_list|,
name|counter
operator|.
name|status
argument_list|()
argument_list|)
expr_stmt|;
name|clusterStateHealth
operator|=
name|maybeSerialize
argument_list|(
name|clusterStateHealth
argument_list|)
expr_stmt|;
name|assertClusterHealth
argument_list|(
name|clusterStateHealth
argument_list|,
name|counter
argument_list|)
expr_stmt|;
block|}
DECL|method|testValidations
specifier|public
name|void
name|testValidations
parameter_list|()
throws|throws
name|IOException
block|{
name|RoutingTableGenerator
name|routingTableGenerator
init|=
operator|new
name|RoutingTableGenerator
argument_list|()
decl_stmt|;
name|IndexMetaData
name|indexMetaData
init|=
name|IndexMetaData
operator|.
name|builder
argument_list|(
literal|"test"
argument_list|)
operator|.
name|settings
argument_list|(
name|settings
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|2
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingTableGenerator
operator|.
name|ShardCounter
name|counter
init|=
operator|new
name|RoutingTableGenerator
operator|.
name|ShardCounter
argument_list|()
decl_stmt|;
name|IndexRoutingTable
name|indexRoutingTable
init|=
name|routingTableGenerator
operator|.
name|genIndexRoutingTable
argument_list|(
name|indexMetaData
argument_list|,
name|counter
argument_list|)
decl_stmt|;
name|indexMetaData
operator|=
name|IndexMetaData
operator|.
name|builder
argument_list|(
literal|"test"
argument_list|)
operator|.
name|settings
argument_list|(
name|settings
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|2
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|3
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|ClusterIndexHealth
name|indexHealth
init|=
operator|new
name|ClusterIndexHealth
argument_list|(
name|indexMetaData
argument_list|,
name|indexRoutingTable
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|indexHealth
operator|.
name|getValidationFailures
argument_list|()
argument_list|,
name|Matchers
operator|.
name|hasSize
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|RoutingTable
operator|.
name|Builder
name|routingTable
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
decl_stmt|;
name|MetaData
operator|.
name|Builder
name|metaData
init|=
name|MetaData
operator|.
name|builder
argument_list|()
decl_stmt|;
name|metaData
operator|.
name|put
argument_list|(
name|indexMetaData
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|routingTable
operator|.
name|add
argument_list|(
name|indexRoutingTable
argument_list|)
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|ClusterName
operator|.
name|DEFAULT
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
index|[]
name|concreteIndices
init|=
name|indexNameExpressionResolver
operator|.
name|concreteIndices
argument_list|(
name|clusterState
argument_list|,
name|IndicesOptions
operator|.
name|strictExpand
argument_list|()
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
name|ClusterStateHealth
name|clusterStateHealth
init|=
operator|new
name|ClusterStateHealth
argument_list|(
name|clusterState
argument_list|,
name|concreteIndices
argument_list|)
decl_stmt|;
name|clusterStateHealth
operator|=
name|maybeSerialize
argument_list|(
name|clusterStateHealth
argument_list|)
expr_stmt|;
comment|// currently we have no cluster level validation failures as index validation issues are reported per index.
name|assertThat
argument_list|(
name|clusterStateHealth
operator|.
name|getValidationFailures
argument_list|()
argument_list|,
name|Matchers
operator|.
name|hasSize
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|maybeSerialize
name|ClusterStateHealth
name|maybeSerialize
parameter_list|(
name|ClusterStateHealth
name|clusterStateHealth
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|clusterStateHealth
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|clusterStateHealth
operator|=
name|ClusterStateHealth
operator|.
name|readClusterHealth
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
return|return
name|clusterStateHealth
return|;
block|}
DECL|method|assertClusterHealth
specifier|private
name|void
name|assertClusterHealth
parameter_list|(
name|ClusterStateHealth
name|clusterStateHealth
parameter_list|,
name|RoutingTableGenerator
operator|.
name|ShardCounter
name|counter
parameter_list|)
block|{
name|assertThat
argument_list|(
name|clusterStateHealth
operator|.
name|getStatus
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|status
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterStateHealth
operator|.
name|getActiveShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|active
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterStateHealth
operator|.
name|getActivePrimaryShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|primaryActive
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterStateHealth
operator|.
name|getInitializingShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|initializing
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterStateHealth
operator|.
name|getRelocatingShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|relocating
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterStateHealth
operator|.
name|getUnassignedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|unassigned
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterStateHealth
operator|.
name|getValidationFailures
argument_list|()
argument_list|,
name|empty
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterStateHealth
operator|.
name|getActiveShardsPercent
argument_list|()
argument_list|,
name|is
argument_list|(
name|allOf
argument_list|(
name|greaterThanOrEqualTo
argument_list|(
literal|0.0
argument_list|)
argument_list|,
name|lessThanOrEqualTo
argument_list|(
literal|100.0
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

