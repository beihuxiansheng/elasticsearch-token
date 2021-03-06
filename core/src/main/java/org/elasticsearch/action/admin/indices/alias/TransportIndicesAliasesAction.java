begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.alias
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
name|alias
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
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionListener
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
name|admin
operator|.
name|indices
operator|.
name|alias
operator|.
name|IndicesAliasesRequest
operator|.
name|AliasActions
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
name|ActionFilters
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
name|master
operator|.
name|TransportMasterNodeAction
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
name|ack
operator|.
name|ClusterStateUpdateResponse
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
name|block
operator|.
name|ClusterBlockException
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
name|block
operator|.
name|ClusterBlockLevel
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
name|AliasAction
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
name|AliasMetaData
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
name|metadata
operator|.
name|MetaDataIndexAliasesService
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
name|service
operator|.
name|ClusterService
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
name|collect
operator|.
name|ImmutableOpenMap
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
name|inject
operator|.
name|Inject
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
name|rest
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|AliasesNotFoundException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|unmodifiableList
import|;
end_import

begin_comment
comment|/**  * Add/remove aliases action  */
end_comment

begin_class
DECL|class|TransportIndicesAliasesAction
specifier|public
class|class
name|TransportIndicesAliasesAction
extends|extends
name|TransportMasterNodeAction
argument_list|<
name|IndicesAliasesRequest
argument_list|,
name|IndicesAliasesResponse
argument_list|>
block|{
DECL|field|indexAliasesService
specifier|private
specifier|final
name|MetaDataIndexAliasesService
name|indexAliasesService
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportIndicesAliasesAction
specifier|public
name|TransportIndicesAliasesAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|MetaDataIndexAliasesService
name|indexAliasesService
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|IndicesAliasesAction
operator|.
name|NAME
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|IndicesAliasesRequest
operator|::
operator|new
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexAliasesService
operator|=
name|indexAliasesService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|protected
name|String
name|executor
parameter_list|()
block|{
comment|// we go async right away...
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|IndicesAliasesResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|IndicesAliasesResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|checkBlock
specifier|protected
name|ClusterBlockException
name|checkBlock
parameter_list|(
name|IndicesAliasesRequest
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|indices
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AliasActions
name|aliasAction
range|:
name|request
operator|.
name|aliasActions
argument_list|()
control|)
block|{
name|Collections
operator|.
name|addAll
argument_list|(
name|indices
argument_list|,
name|aliasAction
operator|.
name|indices
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|indicesBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|METADATA_WRITE
argument_list|,
name|indices
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|indices
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|masterOperation
specifier|protected
name|void
name|masterOperation
parameter_list|(
specifier|final
name|IndicesAliasesRequest
name|request
parameter_list|,
specifier|final
name|ClusterState
name|state
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|IndicesAliasesResponse
argument_list|>
name|listener
parameter_list|)
block|{
comment|//Expand the indices names
name|List
argument_list|<
name|AliasActions
argument_list|>
name|actions
init|=
name|request
operator|.
name|aliasActions
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AliasAction
argument_list|>
name|finalActions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Resolve all the AliasActions into AliasAction instances and gather all the aliases
name|Set
argument_list|<
name|String
argument_list|>
name|aliases
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AliasActions
name|action
range|:
name|actions
control|)
block|{
name|String
index|[]
name|concreteIndices
init|=
name|indexNameExpressionResolver
operator|.
name|concreteIndexNames
argument_list|(
name|state
argument_list|,
name|request
operator|.
name|indicesOptions
argument_list|()
argument_list|,
name|action
operator|.
name|indices
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|aliases
argument_list|,
name|action
operator|.
name|aliases
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|index
range|:
name|concreteIndices
control|)
block|{
switch|switch
condition|(
name|action
operator|.
name|actionType
argument_list|()
condition|)
block|{
case|case
name|ADD
case|:
for|for
control|(
name|String
name|alias
range|:
name|concreteAliases
argument_list|(
name|action
argument_list|,
name|state
operator|.
name|metaData
argument_list|()
argument_list|,
name|index
argument_list|)
control|)
block|{
name|finalActions
operator|.
name|add
argument_list|(
operator|new
name|AliasAction
operator|.
name|Add
argument_list|(
name|index
argument_list|,
name|alias
argument_list|,
name|action
operator|.
name|filter
argument_list|()
argument_list|,
name|action
operator|.
name|indexRouting
argument_list|()
argument_list|,
name|action
operator|.
name|searchRouting
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|REMOVE
case|:
for|for
control|(
name|String
name|alias
range|:
name|concreteAliases
argument_list|(
name|action
argument_list|,
name|state
operator|.
name|metaData
argument_list|()
argument_list|,
name|index
argument_list|)
control|)
block|{
name|finalActions
operator|.
name|add
argument_list|(
operator|new
name|AliasAction
operator|.
name|Remove
argument_list|(
name|index
argument_list|,
name|alias
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|REMOVE_INDEX
case|:
name|finalActions
operator|.
name|add
argument_list|(
operator|new
name|AliasAction
operator|.
name|RemoveIndex
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported action ["
operator|+
name|action
operator|.
name|actionType
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|finalActions
operator|.
name|isEmpty
argument_list|()
operator|&&
literal|false
operator|==
name|actions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AliasesNotFoundException
argument_list|(
name|aliases
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|aliases
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
throw|;
block|}
name|request
operator|.
name|aliasActions
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|IndicesAliasesClusterStateUpdateRequest
name|updateRequest
init|=
operator|new
name|IndicesAliasesClusterStateUpdateRequest
argument_list|(
name|unmodifiableList
argument_list|(
name|finalActions
argument_list|)
argument_list|)
operator|.
name|ackTimeout
argument_list|(
name|request
operator|.
name|timeout
argument_list|()
argument_list|)
operator|.
name|masterNodeTimeout
argument_list|(
name|request
operator|.
name|masterNodeTimeout
argument_list|()
argument_list|)
decl_stmt|;
name|indexAliasesService
operator|.
name|indicesAliases
argument_list|(
name|updateRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|ClusterStateUpdateResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|ClusterStateUpdateResponse
name|response
parameter_list|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|IndicesAliasesResponse
argument_list|(
name|response
operator|.
name|isAcknowledged
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Exception
name|t
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to perform aliases"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onFailure
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|concreteAliases
specifier|private
specifier|static
name|String
index|[]
name|concreteAliases
parameter_list|(
name|AliasActions
name|action
parameter_list|,
name|MetaData
name|metaData
parameter_list|,
name|String
name|concreteIndex
parameter_list|)
block|{
if|if
condition|(
name|action
operator|.
name|expandAliasesWildcards
argument_list|()
condition|)
block|{
comment|//for DELETE we expand the aliases
name|String
index|[]
name|indexAsArray
init|=
block|{
name|concreteIndex
block|}
decl_stmt|;
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|aliasMetaData
init|=
name|metaData
operator|.
name|findAliases
argument_list|(
name|action
operator|.
name|aliases
argument_list|()
argument_list|,
name|indexAsArray
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|finalAliases
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ObjectCursor
argument_list|<
name|List
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|curAliases
range|:
name|aliasMetaData
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|AliasMetaData
name|aliasMeta
range|:
name|curAliases
operator|.
name|value
control|)
block|{
name|finalAliases
operator|.
name|add
argument_list|(
name|aliasMeta
operator|.
name|alias
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|finalAliases
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|finalAliases
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
else|else
block|{
comment|//for ADD and REMOVE_INDEX we just return the current aliases
return|return
name|action
operator|.
name|aliases
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

