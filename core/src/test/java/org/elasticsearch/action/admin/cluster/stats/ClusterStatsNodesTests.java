begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.stats
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
name|stats
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
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|info
operator|.
name|NodeInfo
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
name|node
operator|.
name|DiscoveryNode
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
name|network
operator|.
name|NetworkModule
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
name|common
operator|.
name|xcontent
operator|.
name|XContentType
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentHelper
operator|.
name|toXContent
import|;
end_import

begin_class
DECL|class|ClusterStatsNodesTests
specifier|public
class|class
name|ClusterStatsNodesTests
extends|extends
name|ESTestCase
block|{
comment|/**      * Test that empty transport/http types are not printed out as part      * of the cluster stats xcontent output.      */
DECL|method|testNetworkTypesToXContent
specifier|public
name|void
name|testNetworkTypesToXContent
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterStatsNodes
operator|.
name|NetworkTypes
name|stats
init|=
operator|new
name|ClusterStatsNodes
operator|.
name|NetworkTypes
argument_list|(
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\"transport_types\":{},\"http_types\":{}}"
argument_list|,
name|toXContent
argument_list|(
name|stats
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|NodeInfo
argument_list|>
name|nodeInfos
init|=
name|singletonList
argument_list|(
name|createNodeInfo
argument_list|(
literal|"node_0"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|stats
operator|=
operator|new
name|ClusterStatsNodes
operator|.
name|NetworkTypes
argument_list|(
name|nodeInfos
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\"transport_types\":{},\"http_types\":{}}"
argument_list|,
name|toXContent
argument_list|(
name|stats
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|nodeInfos
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|createNodeInfo
argument_list|(
literal|"node_1"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
argument_list|,
name|createNodeInfo
argument_list|(
literal|"node_2"
argument_list|,
literal|"custom"
argument_list|,
literal|"custom"
argument_list|)
argument_list|,
name|createNodeInfo
argument_list|(
literal|"node_3"
argument_list|,
literal|null
argument_list|,
literal|"custom"
argument_list|)
argument_list|)
expr_stmt|;
name|stats
operator|=
operator|new
name|ClusterStatsNodes
operator|.
name|NetworkTypes
argument_list|(
name|nodeInfos
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{"
operator|+
literal|"\"transport_types\":{\"custom\":1},"
operator|+
literal|"\"http_types\":{\"custom\":2}"
operator|+
literal|"}"
argument_list|,
name|toXContent
argument_list|(
name|stats
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createNodeInfo
specifier|private
specifier|static
name|NodeInfo
name|createNodeInfo
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|String
name|transportType
parameter_list|,
name|String
name|httpType
parameter_list|)
block|{
name|Settings
operator|.
name|Builder
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|transportType
operator|!=
literal|null
condition|)
block|{
name|settings
operator|.
name|put
argument_list|(
name|randomFrom
argument_list|(
name|NetworkModule
operator|.
name|TRANSPORT_TYPE_KEY
argument_list|,
name|NetworkModule
operator|.
name|TRANSPORT_TYPE_DEFAULT_KEY
argument_list|)
argument_list|,
name|transportType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|httpType
operator|!=
literal|null
condition|)
block|{
name|settings
operator|.
name|put
argument_list|(
name|randomFrom
argument_list|(
name|NetworkModule
operator|.
name|HTTP_TYPE_KEY
argument_list|,
name|NetworkModule
operator|.
name|HTTP_TYPE_DEFAULT_KEY
argument_list|)
argument_list|,
name|httpType
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|NodeInfo
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|DiscoveryNode
argument_list|(
name|nodeId
argument_list|,
name|buildNewFakeTransportAddress
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|,
name|settings
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

