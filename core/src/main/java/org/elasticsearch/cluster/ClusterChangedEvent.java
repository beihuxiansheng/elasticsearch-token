begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectCursor
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
name|ImmutableList
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
name|node
operator|.
name|DiscoveryNodes
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ClusterChangedEvent
specifier|public
class|class
name|ClusterChangedEvent
block|{
DECL|field|source
specifier|private
specifier|final
name|String
name|source
decl_stmt|;
DECL|field|previousState
specifier|private
specifier|final
name|ClusterState
name|previousState
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|ClusterState
name|state
decl_stmt|;
DECL|field|nodesDelta
specifier|private
specifier|final
name|DiscoveryNodes
operator|.
name|Delta
name|nodesDelta
decl_stmt|;
DECL|method|ClusterChangedEvent
specifier|public
name|ClusterChangedEvent
parameter_list|(
name|String
name|source
parameter_list|,
name|ClusterState
name|state
parameter_list|,
name|ClusterState
name|previousState
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|previousState
operator|=
name|previousState
expr_stmt|;
name|this
operator|.
name|nodesDelta
operator|=
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|delta
argument_list|(
name|previousState
operator|.
name|nodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * The source that caused this cluster event to be raised.      */
DECL|method|source
specifier|public
name|String
name|source
parameter_list|()
block|{
return|return
name|this
operator|.
name|source
return|;
block|}
DECL|method|state
specifier|public
name|ClusterState
name|state
parameter_list|()
block|{
return|return
name|this
operator|.
name|state
return|;
block|}
DECL|method|previousState
specifier|public
name|ClusterState
name|previousState
parameter_list|()
block|{
return|return
name|this
operator|.
name|previousState
return|;
block|}
DECL|method|routingTableChanged
specifier|public
name|boolean
name|routingTableChanged
parameter_list|()
block|{
return|return
name|state
operator|.
name|routingTable
argument_list|()
operator|!=
name|previousState
operator|.
name|routingTable
argument_list|()
return|;
block|}
DECL|method|indexRoutingTableChanged
specifier|public
name|boolean
name|indexRoutingTableChanged
parameter_list|(
name|String
name|index
parameter_list|)
block|{
if|if
condition|(
operator|!
name|state
operator|.
name|routingTable
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|index
argument_list|)
operator|&&
operator|!
name|previousState
operator|.
name|routingTable
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|index
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|state
operator|.
name|routingTable
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|index
argument_list|)
operator|&&
name|previousState
operator|.
name|routingTable
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|index
argument_list|)
condition|)
block|{
return|return
name|state
operator|.
name|routingTable
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
operator|!=
name|previousState
operator|.
name|routingTable
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Returns the indices created in this event      */
DECL|method|indicesCreated
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|indicesCreated
parameter_list|()
block|{
if|if
condition|(
name|previousState
operator|==
literal|null
condition|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|keys
argument_list|()
operator|.
name|toArray
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
name|metaDataChanged
argument_list|()
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|created
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ObjectCursor
argument_list|<
name|String
argument_list|>
name|cursor
range|:
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|keys
argument_list|()
control|)
block|{
name|String
name|index
init|=
name|cursor
operator|.
name|value
decl_stmt|;
if|if
condition|(
operator|!
name|previousState
operator|.
name|metaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|index
argument_list|)
condition|)
block|{
if|if
condition|(
name|created
operator|==
literal|null
condition|)
block|{
name|created
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|created
operator|.
name|add
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|created
operator|==
literal|null
condition|?
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
else|:
name|created
return|;
block|}
comment|/**      * Returns the indices deleted in this event      */
DECL|method|indicesDeleted
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|indicesDeleted
parameter_list|()
block|{
comment|// if the new cluster state has a new master then we cannot know if an index which is not in the cluster state
comment|// is actually supposed to be deleted or imported as dangling instead. for example a new master might not have
comment|// the index in its cluster state because it was started with an empty data folder and in this case we want to
comment|// import as dangling. we check here for new master too to be on the safe side in this case.
comment|// This means that under certain conditions deleted indices might be reimported if a master fails while the deletion
comment|// request is issued and a node receives the cluster state that would trigger the deletion from the new master.
comment|// See test MetaDataWriteDataNodesTests.testIndicesDeleted()
comment|// See discussion on https://github.com/elastic/elasticsearch/pull/9952 and
comment|// https://github.com/elastic/elasticsearch/issues/11665
if|if
condition|(
name|hasNewMaster
argument_list|()
operator|||
name|previousState
operator|==
literal|null
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
if|if
condition|(
operator|!
name|metaDataChanged
argument_list|()
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|deleted
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ObjectCursor
argument_list|<
name|String
argument_list|>
name|cursor
range|:
name|previousState
operator|.
name|metaData
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|keys
argument_list|()
control|)
block|{
name|String
name|index
init|=
name|cursor
operator|.
name|value
decl_stmt|;
if|if
condition|(
operator|!
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|index
argument_list|)
condition|)
block|{
if|if
condition|(
name|deleted
operator|==
literal|null
condition|)
block|{
name|deleted
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|deleted
operator|.
name|add
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|deleted
operator|==
literal|null
condition|?
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
else|:
name|deleted
return|;
block|}
DECL|method|metaDataChanged
specifier|public
name|boolean
name|metaDataChanged
parameter_list|()
block|{
return|return
name|state
operator|.
name|metaData
argument_list|()
operator|!=
name|previousState
operator|.
name|metaData
argument_list|()
return|;
block|}
DECL|method|indexMetaDataChanged
specifier|public
name|boolean
name|indexMetaDataChanged
parameter_list|(
name|IndexMetaData
name|current
parameter_list|)
block|{
name|MetaData
name|previousMetaData
init|=
name|previousState
operator|.
name|metaData
argument_list|()
decl_stmt|;
if|if
condition|(
name|previousMetaData
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
name|IndexMetaData
name|previousIndexMetaData
init|=
name|previousMetaData
operator|.
name|index
argument_list|(
name|current
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
comment|// no need to check on version, since disco modules will make sure to use the
comment|// same instance if its a version match
if|if
condition|(
name|previousIndexMetaData
operator|==
name|current
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|blocksChanged
specifier|public
name|boolean
name|blocksChanged
parameter_list|()
block|{
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|!=
name|previousState
operator|.
name|blocks
argument_list|()
return|;
block|}
DECL|method|localNodeMaster
specifier|public
name|boolean
name|localNodeMaster
parameter_list|()
block|{
return|return
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeMaster
argument_list|()
return|;
block|}
DECL|method|nodesDelta
specifier|public
name|DiscoveryNodes
operator|.
name|Delta
name|nodesDelta
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodesDelta
return|;
block|}
DECL|method|nodesRemoved
specifier|public
name|boolean
name|nodesRemoved
parameter_list|()
block|{
return|return
name|nodesDelta
operator|.
name|removed
argument_list|()
return|;
block|}
DECL|method|nodesAdded
specifier|public
name|boolean
name|nodesAdded
parameter_list|()
block|{
return|return
name|nodesDelta
operator|.
name|added
argument_list|()
return|;
block|}
DECL|method|nodesChanged
specifier|public
name|boolean
name|nodesChanged
parameter_list|()
block|{
return|return
name|nodesRemoved
argument_list|()
operator|||
name|nodesAdded
argument_list|()
return|;
block|}
comment|/**      * Checks if this cluster state comes from a different master than the previous one.      * This is a workaround for the scenario where a node misses a cluster state  that has either      * no master block or state not recovered flag set. In this case we must make sure that      * if an index is missing from the cluster state is not deleted immediately but instead imported      * as dangling. See discussion on https://github.com/elastic/elasticsearch/pull/9952      */
DECL|method|hasNewMaster
specifier|private
name|boolean
name|hasNewMaster
parameter_list|()
block|{
name|String
name|oldMaster
init|=
name|previousState
argument_list|()
operator|.
name|getNodes
argument_list|()
operator|.
name|masterNodeId
argument_list|()
decl_stmt|;
name|String
name|newMaster
init|=
name|state
argument_list|()
operator|.
name|getNodes
argument_list|()
operator|.
name|masterNodeId
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldMaster
operator|==
literal|null
operator|&&
name|newMaster
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|oldMaster
operator|==
literal|null
operator|&&
name|newMaster
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|oldMaster
operator|.
name|equals
argument_list|(
name|newMaster
argument_list|)
operator|==
literal|false
return|;
block|}
block|}
end_class

end_unit

