begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|AbstractRandomizedTest
import|;
end_import

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
name|IndexShardRoutingTable
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
name|ShardRouting
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
name|PathUtils
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
name|regex
operator|.
name|Regex
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
name|indices
operator|.
name|recovery
operator|.
name|RecoverySettings
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
name|junit
operator|.
name|annotations
operator|.
name|TestLogging
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
name|junit
operator|.
name|listeners
operator|.
name|LoggingListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|Transport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|netty
operator|.
name|NettyTransport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_comment
comment|/**  * Abstract base class for backwards compatibility tests. Subclasses of this class  * can run tests against a mixed version cluster. A subset of the nodes in the cluster  * are started in dedicated process running off a full fledged elasticsearch release.  * Nodes can be "upgraded" from the "backwards" node to an "new" node where "new" nodes  * version corresponds to current version.  * The purpose of this test class is to run tests in scenarios where clusters are in an  * intermediate state during a rolling upgrade as well as upgrade situations. The clients  * accessed via #client() are random clients to the nodes in the cluster which might  * execute requests on the "new" as well as the "old" nodes.  *<p>  *   Note: this base class is still experimental and might have bugs or leave external processes running behind.  *</p>  * Backwards compatibility tests are disabled by default via {@link org.apache.lucene.util.AbstractRandomizedTest.Backwards} annotation.  * The following system variables control the test execution:  *<ul>  *<li>  *<tt>{@value #TESTS_BACKWARDS_COMPATIBILITY}</tt> enables / disables  *          tests annotated with {@link org.apache.lucene.util.AbstractRandomizedTest.Backwards} (defaults to  *<tt>false</tt>)  *</li>  *<li>  *<tt>{@value #TESTS_BACKWARDS_COMPATIBILITY_VERSION}</tt>  *          sets the version to run the external nodes from formatted as<i>X.Y.Z</i>.  *          The tests class will try to locate a release folder<i>elasticsearch-X.Y.Z</i>  *          within path passed via {@value #TESTS_BACKWARDS_COMPATIBILITY_PATH}  *          depending on this system variable.  *</li>  *<li>  *<tt>{@value #TESTS_BACKWARDS_COMPATIBILITY_PATH}</tt> the path to the  *          elasticsearch releases to run backwards compatibility tests against.  *</li>  *</ul>  *  */
end_comment

begin_comment
comment|// the transportClientRatio is tricky here since we don't fully control the cluster nodes
end_comment

begin_class
annotation|@
name|AbstractRandomizedTest
operator|.
name|Backwards
annotation|@
name|ElasticsearchIntegrationTest
operator|.
name|ClusterScope
argument_list|(
name|minNumDataNodes
operator|=
literal|0
argument_list|,
name|maxNumDataNodes
operator|=
literal|2
argument_list|,
name|scope
operator|=
name|ElasticsearchIntegrationTest
operator|.
name|Scope
operator|.
name|SUITE
argument_list|,
name|numClientNodes
operator|=
literal|0
argument_list|,
name|transportClientRatio
operator|=
literal|0.0
argument_list|)
annotation|@
name|Ignore
DECL|class|ElasticsearchBackwardsCompatIntegrationTest
specifier|public
specifier|abstract
class|class
name|ElasticsearchBackwardsCompatIntegrationTest
extends|extends
name|ElasticsearchIntegrationTest
block|{
DECL|method|backwardsCompatibilityPath
specifier|private
specifier|static
name|Path
name|backwardsCompatibilityPath
parameter_list|()
block|{
name|String
name|path
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|TESTS_BACKWARDS_COMPATIBILITY_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
operator|||
name|path
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Must specify backwards test path with property "
operator|+
name|TESTS_BACKWARDS_COMPATIBILITY_PATH
argument_list|)
throw|;
block|}
name|String
name|version
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|TESTS_BACKWARDS_COMPATIBILITY_VERSION
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|==
literal|null
operator|||
name|version
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Must specify backwards test version with property "
operator|+
name|TESTS_BACKWARDS_COMPATIBILITY_VERSION
argument_list|)
throw|;
block|}
if|if
condition|(
name|Version
operator|.
name|fromString
argument_list|(
name|version
argument_list|)
operator|.
name|before
argument_list|(
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Backcompat elasticsearch version must be same major version as current. "
operator|+
literal|"backcompat: "
operator|+
name|version
operator|+
literal|", current: "
operator|+
name|Version
operator|.
name|CURRENT
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|Path
name|file
init|=
name|PathUtils
operator|.
name|get
argument_list|(
name|path
argument_list|,
literal|"elasticsearch-"
operator|+
name|version
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|file
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Backwards tests location is missing: "
operator|+
name|file
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|Files
operator|.
name|isDirectory
argument_list|(
name|file
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Backwards tests location is not a directory: "
operator|+
name|file
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|file
return|;
block|}
DECL|method|backwardsCluster
specifier|public
name|CompositeTestCluster
name|backwardsCluster
parameter_list|()
block|{
return|return
operator|(
name|CompositeTestCluster
operator|)
name|cluster
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|buildTestCluster
specifier|protected
name|TestCluster
name|buildTestCluster
parameter_list|(
name|Scope
name|scope
parameter_list|,
name|long
name|seed
parameter_list|)
throws|throws
name|IOException
block|{
name|TestCluster
name|cluster
init|=
name|super
operator|.
name|buildTestCluster
argument_list|(
name|scope
argument_list|,
name|seed
argument_list|)
decl_stmt|;
name|ExternalNode
name|externalNode
init|=
operator|new
name|ExternalNode
argument_list|(
name|backwardsCompatibilityPath
argument_list|()
argument_list|,
name|randomLong
argument_list|()
argument_list|,
operator|new
name|SettingsSource
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Settings
name|node
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
return|return
name|externalNodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Settings
name|transportClient
parameter_list|()
block|{
return|return
name|transportClientSettings
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
operator|new
name|CompositeTestCluster
argument_list|(
operator|(
name|InternalTestCluster
operator|)
name|cluster
argument_list|,
name|between
argument_list|(
name|minExternalNodes
argument_list|()
argument_list|,
name|maxExternalNodes
argument_list|()
argument_list|)
argument_list|,
name|externalNode
argument_list|)
return|;
block|}
DECL|method|addLoggerSettings
specifier|private
name|Settings
name|addLoggerSettings
parameter_list|(
name|Settings
name|externalNodesSettings
parameter_list|)
block|{
name|TestLogging
name|logging
init|=
name|getClass
argument_list|()
operator|.
name|getAnnotation
argument_list|(
name|TestLogging
operator|.
name|class
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|loggingLevels
init|=
name|LoggingListener
operator|.
name|getLoggersAndLevelsFromAnnotation
argument_list|(
name|logging
argument_list|)
decl_stmt|;
name|ImmutableSettings
operator|.
name|Builder
name|finalSettings
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|loggingLevels
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|level
range|:
name|loggingLevels
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|finalSettings
operator|.
name|put
argument_list|(
literal|"logger."
operator|+
name|level
operator|.
name|getKey
argument_list|()
argument_list|,
name|level
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|finalSettings
operator|.
name|put
argument_list|(
name|externalNodesSettings
argument_list|)
expr_stmt|;
return|return
name|finalSettings
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|minExternalNodes
specifier|protected
name|int
name|minExternalNodes
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
DECL|method|maxExternalNodes
specifier|protected
name|int
name|maxExternalNodes
parameter_list|()
block|{
return|return
literal|2
return|;
block|}
annotation|@
name|Override
DECL|method|maximumNumberOfReplicas
specifier|protected
name|int
name|maximumNumberOfReplicas
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
DECL|method|requiredSettings
specifier|protected
name|Settings
name|requiredSettings
parameter_list|()
block|{
return|return
name|ExternalNode
operator|.
name|REQUIRED_SETTINGS
return|;
block|}
annotation|@
name|Override
DECL|method|nodeSettings
specifier|protected
name|Settings
name|nodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
return|return
name|commonNodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
return|;
block|}
DECL|method|assertAllShardsOnNodes
specifier|public
name|void
name|assertAllShardsOnNodes
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|pattern
parameter_list|)
block|{
name|ClusterState
name|clusterState
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
name|prepareState
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getState
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexRoutingTable
name|indexRoutingTable
range|:
name|clusterState
operator|.
name|routingTable
argument_list|()
control|)
block|{
for|for
control|(
name|IndexShardRoutingTable
name|indexShardRoutingTable
range|:
name|indexRoutingTable
control|)
block|{
for|for
control|(
name|ShardRouting
name|shardRouting
range|:
name|indexShardRoutingTable
control|)
block|{
if|if
condition|(
name|shardRouting
operator|.
name|currentNodeId
argument_list|()
operator|!=
literal|null
operator|&&
name|index
operator|.
name|equals
argument_list|(
name|shardRouting
operator|.
name|getIndex
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|name
init|=
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|get
argument_list|(
name|shardRouting
operator|.
name|currentNodeId
argument_list|()
argument_list|)
operator|.
name|name
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"Allocated on new node: "
operator|+
name|name
argument_list|,
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|pattern
argument_list|,
name|name
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|commonNodeSettings
specifier|protected
name|Settings
name|commonNodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
name|ImmutableSettings
operator|.
name|Builder
name|builder
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|requiredSettings
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|TransportModule
operator|.
name|TRANSPORT_TYPE_KEY
argument_list|,
name|NettyTransport
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
comment|// run same transport  / disco as external
operator|.
name|put
argument_list|(
name|TransportModule
operator|.
name|TRANSPORT_SERVICE_TYPE_KEY
argument_list|,
name|TransportService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|compatibilityVersion
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_1_3_2
argument_list|)
condition|)
block|{
comment|// if we test against nodes before 1.3.2 we disable all the compression due to a known bug
comment|// see #7210
name|builder
operator|.
name|put
argument_list|(
name|Transport
operator|.
name|TransportSettings
operator|.
name|TRANSPORT_TCP_COMPRESS
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
name|RecoverySettings
operator|.
name|INDICES_RECOVERY_COMPRESS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|externalNodeSettings
specifier|protected
name|Settings
name|externalNodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
return|return
name|addLoggerSettings
argument_list|(
name|commonNodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

