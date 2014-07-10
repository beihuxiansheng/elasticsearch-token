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
name|com
operator|.
name|carrotsearch
operator|.
name|ant
operator|.
name|tasks
operator|.
name|junit4
operator|.
name|dependencies
operator|.
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterators
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomPicks
import|;
end_import

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Collections2
import|;
end_import

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
name|IOUtils
import|;
end_import

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
name|client
operator|.
name|ClusterAdminClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|FilterClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|IndicesAdminClient
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
name|net
operator|.
name|InetSocketAddress
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/**  * A test cluster implementation that holds a fixed set of external nodes as well as a InternalTestCluster  * which is used to run mixed version clusters in tests like backwards compatibility tests.  * Note: this is an experimental API  */
end_comment

begin_class
DECL|class|CompositeTestCluster
specifier|public
class|class
name|CompositeTestCluster
extends|extends
name|TestCluster
block|{
DECL|field|cluster
specifier|private
specifier|final
name|InternalTestCluster
name|cluster
decl_stmt|;
DECL|field|externalNodes
specifier|private
specifier|final
name|ExternalNode
index|[]
name|externalNodes
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|ExternalClient
name|client
init|=
operator|new
name|ExternalClient
argument_list|()
decl_stmt|;
DECL|field|NODE_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|NODE_PREFIX
init|=
literal|"external_"
decl_stmt|;
DECL|method|CompositeTestCluster
specifier|public
name|CompositeTestCluster
parameter_list|(
name|InternalTestCluster
name|cluster
parameter_list|,
name|int
name|numExternalNodes
parameter_list|,
name|ExternalNode
name|externalNode
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|cluster
operator|=
name|cluster
expr_stmt|;
name|this
operator|.
name|externalNodes
operator|=
operator|new
name|ExternalNode
index|[
name|numExternalNodes
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|externalNodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|externalNodes
index|[
name|i
index|]
operator|=
name|externalNode
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|afterTest
specifier|public
specifier|synchronized
name|void
name|afterTest
parameter_list|()
throws|throws
name|IOException
block|{
name|cluster
operator|.
name|afterTest
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|beforeTest
specifier|public
specifier|synchronized
name|void
name|beforeTest
parameter_list|(
name|Random
name|random
parameter_list|,
name|double
name|transportClientRatio
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|beforeTest
argument_list|(
name|random
argument_list|,
name|transportClientRatio
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|beforeTest
argument_list|(
name|random
argument_list|,
name|transportClientRatio
argument_list|)
expr_stmt|;
name|Settings
name|defaultSettings
init|=
name|cluster
operator|.
name|getDefaultSettings
argument_list|()
decl_stmt|;
specifier|final
name|Client
name|client
init|=
name|cluster
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|?
name|cluster
operator|.
name|client
argument_list|()
else|:
name|cluster
operator|.
name|clientNodeClient
argument_list|()
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
name|externalNodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|externalNodes
index|[
name|i
index|]
operator|.
name|running
argument_list|()
condition|)
block|{
try|try
block|{
name|externalNodes
index|[
name|i
index|]
operator|=
name|externalNodes
index|[
name|i
index|]
operator|.
name|start
argument_list|(
name|client
argument_list|,
name|defaultSettings
argument_list|,
name|NODE_PREFIX
operator|+
name|i
argument_list|,
name|cluster
operator|.
name|getClusterName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|interrupted
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
name|externalNodes
index|[
name|i
index|]
operator|.
name|reset
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|client
argument_list|()
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
name|setWaitForNodes
argument_list|(
literal|">="
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|this
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|runningNodes
specifier|private
name|Collection
argument_list|<
name|ExternalNode
argument_list|>
name|runningNodes
parameter_list|()
block|{
return|return
name|Collections2
operator|.
name|filter
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|externalNodes
argument_list|)
argument_list|,
operator|new
name|Predicate
argument_list|<
name|ExternalNode
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|ExternalNode
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|running
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * Upgrades one external running node to a node from the version running the tests. Commonly this is used      * to move from a node with version N-1 to a node running version N. This works seamless since they will      * share the same data directory. This method will return<tt>true</tt> iff a node got upgraded otherwise if no      * external node is running it returns<tt>false</tt>      */
DECL|method|upgradeOneNode
specifier|public
specifier|synchronized
name|boolean
name|upgradeOneNode
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
return|return
name|upgradeOneNode
argument_list|(
name|ImmutableSettings
operator|.
name|EMPTY
argument_list|)
return|;
block|}
comment|/**      * Upgrades all external running nodes to a node from the version running the tests.      * All nodes are shut down before the first upgrade happens.      * @return<code>true</code> iff at least one node as upgraded.      */
DECL|method|upgradeAllNodes
specifier|public
specifier|synchronized
name|boolean
name|upgradeAllNodes
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
return|return
name|upgradeAllNodes
argument_list|(
name|ImmutableSettings
operator|.
name|EMPTY
argument_list|)
return|;
block|}
comment|/**      * Upgrades all external running nodes to a node from the version running the tests.      * All nodes are shut down before the first upgrade happens.      * @return<code>true</code> iff at least one node as upgraded.      * @param nodeSettings settings for the upgrade nodes      */
DECL|method|upgradeAllNodes
specifier|public
specifier|synchronized
name|boolean
name|upgradeAllNodes
parameter_list|(
name|Settings
name|nodeSettings
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|boolean
name|upgradedOneNode
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|upgradeOneNode
argument_list|(
name|nodeSettings
argument_list|)
condition|)
block|{
name|upgradedOneNode
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|upgradedOneNode
return|;
block|}
comment|/**      * Upgrades one external running node to a node from the version running the tests. Commonly this is used      * to move from a node with version N-1 to a node running version N. This works seamless since they will      * share the same data directory. This method will return<tt>true</tt> iff a node got upgraded otherwise if no      * external node is running it returns<tt>false</tt>      */
DECL|method|upgradeOneNode
specifier|public
specifier|synchronized
name|boolean
name|upgradeOneNode
parameter_list|(
name|Settings
name|nodeSettings
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|Collection
argument_list|<
name|ExternalNode
argument_list|>
name|runningNodes
init|=
name|runningNodes
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|runningNodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|Client
name|existingClient
init|=
name|cluster
operator|.
name|client
argument_list|()
decl_stmt|;
name|ExternalNode
name|externalNode
init|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|,
name|runningNodes
argument_list|)
decl_stmt|;
name|externalNode
operator|.
name|stop
argument_list|()
expr_stmt|;
name|String
name|s
init|=
name|cluster
operator|.
name|startNode
argument_list|(
name|nodeSettings
argument_list|)
decl_stmt|;
name|ExternalNode
operator|.
name|waitForNode
argument_list|(
name|existingClient
argument_list|,
name|s
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Returns the a simple pattern that matches all "new" nodes in the cluster.      */
DECL|method|newNodePattern
specifier|public
name|String
name|newNodePattern
parameter_list|()
block|{
return|return
name|cluster
operator|.
name|nodePrefix
argument_list|()
operator|+
literal|"*"
return|;
block|}
comment|/**      * Returns the a simple pattern that matches all "old" / "backwardss" nodes in the cluster.      */
DECL|method|backwardsNodePattern
specifier|public
name|String
name|backwardsNodePattern
parameter_list|()
block|{
return|return
name|NODE_PREFIX
operator|+
literal|"*"
return|;
block|}
comment|/**      * Allows allocation of shards of the given indices on all nodes in the cluster.      */
DECL|method|allowOnAllNodes
specifier|public
name|void
name|allowOnAllNodes
parameter_list|(
name|String
modifier|...
name|index
parameter_list|)
block|{
name|Settings
name|build
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.routing.allocation.exclude._name"
argument_list|,
literal|""
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
name|index
argument_list|)
operator|.
name|setSettings
argument_list|(
name|build
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
comment|/**      * Allows allocation of shards of the given indices only on "new" nodes in the cluster.      * Note: if a shard is allocated on an "old" node and can't be allocated on a "new" node it will only be removed it can      * be allocated on some other "new" node.      */
DECL|method|allowOnlyNewNodes
specifier|public
name|void
name|allowOnlyNewNodes
parameter_list|(
name|String
modifier|...
name|index
parameter_list|)
block|{
name|Settings
name|build
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.routing.allocation.exclude._name"
argument_list|,
name|backwardsNodePattern
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
name|index
argument_list|)
operator|.
name|setSettings
argument_list|(
name|build
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
comment|/**      * Starts a current version data node      */
DECL|method|startNewNode
specifier|public
name|void
name|startNewNode
parameter_list|()
block|{
name|cluster
operator|.
name|startNode
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|client
specifier|public
specifier|synchronized
name|Client
name|client
parameter_list|()
block|{
return|return
name|client
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
return|return
name|runningNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|+
name|cluster
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|numDataNodes
specifier|public
name|int
name|numDataNodes
parameter_list|()
block|{
return|return
name|runningNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|+
name|cluster
operator|.
name|numDataNodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|numBenchNodes
specifier|public
name|int
name|numBenchNodes
parameter_list|()
block|{
return|return
name|cluster
operator|.
name|numBenchNodes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|httpAddresses
specifier|public
name|InetSocketAddress
index|[]
name|httpAddresses
parameter_list|()
block|{
return|return
name|cluster
operator|.
name|httpAddresses
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|externalNodes
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|hasFilterCache
specifier|public
name|boolean
name|hasFilterCache
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
specifier|synchronized
name|Iterator
argument_list|<
name|Client
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Iterators
operator|.
name|singletonIterator
argument_list|(
name|client
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Delegates to {@link org.elasticsearch.test.InternalTestCluster#fullRestart()}      */
DECL|method|fullRestartInternalCluster
specifier|public
name|void
name|fullRestartInternalCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|fullRestart
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the number of current version data nodes in the cluster      */
DECL|method|numNewDataNodes
specifier|public
name|int
name|numNewDataNodes
parameter_list|()
block|{
return|return
name|cluster
operator|.
name|numDataNodes
argument_list|()
return|;
block|}
comment|/**      * Returns the number of former version data nodes in the cluster      */
DECL|method|numBackwardsDataNodes
specifier|public
name|int
name|numBackwardsDataNodes
parameter_list|()
block|{
return|return
name|runningNodes
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|internalClient
specifier|private
specifier|synchronized
name|Client
name|internalClient
parameter_list|()
block|{
name|Collection
argument_list|<
name|ExternalNode
argument_list|>
name|externalNodes
init|=
name|runningNodes
argument_list|()
decl_stmt|;
return|return
name|random
operator|.
name|nextBoolean
argument_list|()
operator|&&
operator|!
name|externalNodes
operator|.
name|isEmpty
argument_list|()
condition|?
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|,
name|externalNodes
argument_list|)
operator|.
name|getClient
argument_list|()
else|:
name|cluster
operator|.
name|client
argument_list|()
return|;
block|}
DECL|class|ExternalClient
specifier|private
specifier|final
class|class
name|ExternalClient
extends|extends
name|FilterClient
block|{
DECL|method|ExternalClient
specifier|public
name|ExternalClient
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|in
specifier|protected
name|Client
name|in
parameter_list|()
block|{
return|return
name|internalClient
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|cluster
specifier|public
name|ClusterAdminClient
name|cluster
parameter_list|()
block|{
return|return
operator|new
name|ClusterAdmin
argument_list|(
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|ClusterAdminClient
name|in
parameter_list|()
block|{
return|return
name|internalClient
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|indices
specifier|public
name|IndicesAdminClient
name|indices
parameter_list|()
block|{
return|return
operator|new
name|IndicesAdmin
argument_list|(
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|IndicesAdminClient
name|in
parameter_list|()
block|{
return|return
name|internalClient
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// never close this client
block|}
block|}
block|}
end_class

end_unit

