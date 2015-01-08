begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.warmer.put
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
name|warmer
operator|.
name|put
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|search
operator|.
name|SearchRequest
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
name|search
operator|.
name|SearchResponse
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
name|search
operator|.
name|TransportSearchAction
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
name|TransportMasterNodeOperationAction
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
name|AckedClusterStateUpdateTask
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
name|ClusterService
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
name|common
operator|.
name|bytes
operator|.
name|BytesReference
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
name|index
operator|.
name|Index
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
name|IndexMissingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|warmer
operator|.
name|IndexWarmersMetaData
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
comment|/**  * Internal Actions executed on the master associating a warmer with a name in the cluster state metadata.  *  * Note: this is an internal API and should not be used / called by any client code.  */
end_comment

begin_class
DECL|class|TransportPutWarmerAction
specifier|public
class|class
name|TransportPutWarmerAction
extends|extends
name|TransportMasterNodeOperationAction
argument_list|<
name|PutWarmerRequest
argument_list|,
name|PutWarmerResponse
argument_list|>
block|{
DECL|field|searchAction
specifier|private
specifier|final
name|TransportSearchAction
name|searchAction
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportPutWarmerAction
specifier|public
name|TransportPutWarmerAction
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
name|TransportSearchAction
name|searchAction
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|PutWarmerAction
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|searchAction
operator|=
name|searchAction
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
DECL|method|newRequest
specifier|protected
name|PutWarmerRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|PutWarmerRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|PutWarmerResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|PutWarmerResponse
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
name|PutWarmerRequest
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
name|String
index|[]
name|concreteIndices
init|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|concreteIndices
argument_list|(
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|indicesOptions
argument_list|()
argument_list|,
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|indices
argument_list|()
argument_list|)
decl_stmt|;
name|ClusterBlockException
name|status
init|=
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
name|concreteIndices
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|!=
literal|null
condition|)
block|{
return|return
name|status
return|;
block|}
comment|// PutWarmer executes a SearchQuery before adding the new warmer to the cluster state,
comment|// so we need to check the same block as TransportSearchTypeAction here
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
name|READ
argument_list|,
name|concreteIndices
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
name|PutWarmerRequest
name|request
parameter_list|,
specifier|final
name|ClusterState
name|state
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|PutWarmerResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticsearchException
block|{
comment|// first execute the search request, see that its ok...
name|SearchRequest
name|searchRequest
init|=
operator|new
name|SearchRequest
argument_list|(
name|request
operator|.
name|searchRequest
argument_list|()
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|searchAction
operator|.
name|execute
argument_list|(
name|searchRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|SearchResponse
name|searchResponse
parameter_list|)
block|{
if|if
condition|(
name|searchResponse
operator|.
name|getFailedShards
argument_list|()
operator|>
literal|0
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|ElasticsearchException
argument_list|(
literal|"search failed with failed shards: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|searchResponse
operator|.
name|getShardFailures
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"put_warmer ["
operator|+
name|request
operator|.
name|name
argument_list|()
operator|+
literal|"]"
argument_list|,
operator|new
name|AckedClusterStateUpdateTask
argument_list|<
name|PutWarmerResponse
argument_list|>
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|PutWarmerResponse
name|newResponse
parameter_list|(
name|boolean
name|acknowledged
parameter_list|)
block|{
return|return
operator|new
name|PutWarmerResponse
argument_list|(
name|acknowledged
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|String
name|source
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to put warmer [{}] on indices [{}]"
argument_list|,
name|t
argument_list|,
name|request
operator|.
name|name
argument_list|()
argument_list|,
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|indices
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|onFailure
argument_list|(
name|source
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ClusterState
name|execute
parameter_list|(
name|ClusterState
name|currentState
parameter_list|)
block|{
name|MetaData
name|metaData
init|=
name|currentState
operator|.
name|metaData
argument_list|()
decl_stmt|;
name|String
index|[]
name|concreteIndices
init|=
name|metaData
operator|.
name|concreteIndices
argument_list|(
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|indicesOptions
argument_list|()
argument_list|,
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|indices
argument_list|()
argument_list|)
decl_stmt|;
name|BytesReference
name|source
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|source
argument_list|()
operator|!=
literal|null
operator|&&
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|source
operator|=
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|source
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|extraSource
argument_list|()
operator|!=
literal|null
operator|&&
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|extraSource
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|source
operator|=
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|extraSource
argument_list|()
expr_stmt|;
block|}
comment|// now replace it on the metadata
name|MetaData
operator|.
name|Builder
name|mdBuilder
init|=
name|MetaData
operator|.
name|builder
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|concreteIndices
control|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|metaData
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMetaData
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|)
throw|;
block|}
name|IndexWarmersMetaData
name|warmers
init|=
name|indexMetaData
operator|.
name|custom
argument_list|(
name|IndexWarmersMetaData
operator|.
name|TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|warmers
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] putting warmer [{}]"
argument_list|,
name|index
argument_list|,
name|request
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|warmers
operator|=
operator|new
name|IndexWarmersMetaData
argument_list|(
operator|new
name|IndexWarmersMetaData
operator|.
name|Entry
argument_list|(
name|request
operator|.
name|name
argument_list|()
argument_list|,
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|types
argument_list|()
argument_list|,
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|queryCache
argument_list|()
argument_list|,
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|IndexWarmersMetaData
operator|.
name|Entry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|warmers
operator|.
name|entries
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|IndexWarmersMetaData
operator|.
name|Entry
name|entry
range|:
name|warmers
operator|.
name|entries
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|request
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
name|entries
operator|.
name|add
argument_list|(
operator|new
name|IndexWarmersMetaData
operator|.
name|Entry
argument_list|(
name|request
operator|.
name|name
argument_list|()
argument_list|,
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|types
argument_list|()
argument_list|,
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|queryCache
argument_list|()
argument_list|,
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|entries
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] put warmer [{}]"
argument_list|,
name|index
argument_list|,
name|request
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|entries
operator|.
name|add
argument_list|(
operator|new
name|IndexWarmersMetaData
operator|.
name|Entry
argument_list|(
name|request
operator|.
name|name
argument_list|()
argument_list|,
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|types
argument_list|()
argument_list|,
name|request
operator|.
name|searchRequest
argument_list|()
operator|.
name|queryCache
argument_list|()
argument_list|,
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] update warmer [{}]"
argument_list|,
name|index
argument_list|,
name|request
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|warmers
operator|=
operator|new
name|IndexWarmersMetaData
argument_list|(
name|entries
operator|.
name|toArray
argument_list|(
operator|new
name|IndexWarmersMetaData
operator|.
name|Entry
index|[
name|entries
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|IndexMetaData
operator|.
name|Builder
name|indexBuilder
init|=
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|indexMetaData
argument_list|)
operator|.
name|putCustom
argument_list|(
name|IndexWarmersMetaData
operator|.
name|TYPE
argument_list|,
name|warmers
argument_list|)
decl_stmt|;
name|mdBuilder
operator|.
name|put
argument_list|(
name|indexBuilder
argument_list|)
expr_stmt|;
block|}
return|return
name|ClusterState
operator|.
name|builder
argument_list|(
name|currentState
argument_list|)
operator|.
name|metaData
argument_list|(
name|mdBuilder
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

