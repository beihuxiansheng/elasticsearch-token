begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.metadata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
package|;
end_package

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
name|Lists
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
name|Maps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|MasterNodeOperationRequest
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
name|TimeoutClusterStateUpdateTask
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
name|action
operator|.
name|index
operator|.
name|NodeAliasesUpdatedAction
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
name|Priority
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
name|Strings
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
name|component
operator|.
name|AbstractComponent
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
name|common
operator|.
name|unit
operator|.
name|TimeValue
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
name|XContentFactory
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
name|XContentParser
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
name|index
operator|.
name|query
operator|.
name|IndexQueryParserService
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
name|service
operator|.
name|IndexService
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
name|indices
operator|.
name|IndicesService
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
name|InvalidAliasNameException
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
name|Map
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|ClusterState
operator|.
name|newClusterStateBuilder
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
name|newIndexMetaDataBuilder
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
name|MetaData
operator|.
name|newMetaDataBuilder
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|MetaDataIndexAliasesService
specifier|public
class|class
name|MetaDataIndexAliasesService
extends|extends
name|AbstractComponent
block|{
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
DECL|field|aliasOperationPerformedAction
specifier|private
specifier|final
name|NodeAliasesUpdatedAction
name|aliasOperationPerformedAction
decl_stmt|;
annotation|@
name|Inject
DECL|method|MetaDataIndexAliasesService
specifier|public
name|MetaDataIndexAliasesService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|IndicesService
name|indicesService
parameter_list|,
name|NodeAliasesUpdatedAction
name|aliasOperationPerformedAction
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|indicesService
operator|=
name|indicesService
expr_stmt|;
name|this
operator|.
name|aliasOperationPerformedAction
operator|=
name|aliasOperationPerformedAction
expr_stmt|;
block|}
DECL|method|indicesAliases
specifier|public
name|void
name|indicesAliases
parameter_list|(
specifier|final
name|Request
name|request
parameter_list|,
specifier|final
name|Listener
name|listener
parameter_list|)
block|{
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"index-aliases"
argument_list|,
name|Priority
operator|.
name|URGENT
argument_list|,
operator|new
name|TimeoutClusterStateUpdateTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TimeValue
name|timeout
parameter_list|()
block|{
return|return
name|request
operator|.
name|masterTimeout
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
name|listener
operator|.
name|onFailure
argument_list|(
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
specifier|final
name|ClusterState
name|currentState
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|indicesToClose
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|IndexService
argument_list|>
name|indices
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|AliasAction
name|aliasAction
range|:
name|request
operator|.
name|actions
control|)
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|hasText
argument_list|(
name|aliasAction
operator|.
name|alias
argument_list|()
argument_list|)
operator|||
operator|!
name|Strings
operator|.
name|hasText
argument_list|(
name|aliasAction
operator|.
name|index
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Index name and alias name are required"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|aliasAction
operator|.
name|index
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
name|aliasAction
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|aliasAction
operator|.
name|alias
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidAliasNameException
argument_list|(
operator|new
name|Index
argument_list|(
name|aliasAction
operator|.
name|index
argument_list|()
argument_list|)
argument_list|,
name|aliasAction
operator|.
name|alias
argument_list|()
argument_list|,
literal|"an index exists with the same name as the alias"
argument_list|)
throw|;
block|}
if|if
condition|(
name|aliasAction
operator|.
name|indexRouting
argument_list|()
operator|!=
literal|null
operator|&&
name|aliasAction
operator|.
name|indexRouting
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"alias ["
operator|+
name|aliasAction
operator|.
name|alias
argument_list|()
operator|+
literal|"] has several routing values associated with it"
argument_list|)
throw|;
block|}
block|}
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
name|MetaData
operator|.
name|Builder
name|builder
init|=
name|newMetaDataBuilder
argument_list|()
operator|.
name|metaData
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|AliasAction
name|aliasAction
range|:
name|request
operator|.
name|actions
control|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|builder
operator|.
name|get
argument_list|(
name|aliasAction
operator|.
name|index
argument_list|()
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
name|aliasAction
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
comment|// TODO: not copy (putAll)
name|IndexMetaData
operator|.
name|Builder
name|indexMetaDataBuilder
init|=
name|newIndexMetaDataBuilder
argument_list|(
name|indexMetaData
argument_list|)
decl_stmt|;
if|if
condition|(
name|aliasAction
operator|.
name|actionType
argument_list|()
operator|==
name|AliasAction
operator|.
name|Type
operator|.
name|ADD
condition|)
block|{
name|String
name|filter
init|=
name|aliasAction
operator|.
name|filter
argument_list|()
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|filter
argument_list|)
condition|)
block|{
comment|// parse the filter, in order to validate it
name|IndexService
name|indexService
init|=
name|indices
operator|.
name|get
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexService
operator|==
literal|null
condition|)
block|{
name|indexService
operator|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexService
operator|==
literal|null
condition|)
block|{
comment|// temporarily create the index so we have can parse the filter
try|try
block|{
name|indexService
operator|=
name|indicesService
operator|.
name|createIndex
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|,
name|indexMetaData
operator|.
name|settings
argument_list|()
argument_list|,
name|currentState
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[{}] failed to temporary create in order to apply alias action"
argument_list|,
name|e
argument_list|,
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|indicesToClose
operator|.
name|add
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|indices
operator|.
name|put
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|,
name|indexService
argument_list|)
expr_stmt|;
block|}
comment|// now, parse the filter
name|IndexQueryParserService
name|indexQueryParser
init|=
name|indexService
operator|.
name|queryParserService
argument_list|()
decl_stmt|;
try|try
block|{
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|filter
argument_list|)
operator|.
name|createParser
argument_list|(
name|filter
argument_list|)
decl_stmt|;
try|try
block|{
name|indexQueryParser
operator|.
name|parseInnerFilter
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"failed to parse filter for ["
operator|+
name|aliasAction
operator|.
name|alias
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|AliasMetaData
name|newAliasMd
init|=
name|AliasMetaData
operator|.
name|newAliasMetaDataBuilder
argument_list|(
name|aliasAction
operator|.
name|alias
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|filter
argument_list|)
operator|.
name|indexRouting
argument_list|(
name|aliasAction
operator|.
name|indexRouting
argument_list|()
argument_list|)
operator|.
name|searchRouting
argument_list|(
name|aliasAction
operator|.
name|searchRouting
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Check if this alias already exists
name|AliasMetaData
name|aliasMd
init|=
name|indexMetaData
operator|.
name|aliases
argument_list|()
operator|.
name|get
argument_list|(
name|aliasAction
operator|.
name|alias
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|aliasMd
operator|!=
literal|null
operator|&&
name|aliasMd
operator|.
name|equals
argument_list|(
name|newAliasMd
argument_list|)
condition|)
block|{
comment|// It's the same alias - ignore it
continue|continue;
block|}
name|indexMetaDataBuilder
operator|.
name|putAlias
argument_list|(
name|newAliasMd
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|aliasAction
operator|.
name|actionType
argument_list|()
operator|==
name|AliasAction
operator|.
name|Type
operator|.
name|REMOVE
condition|)
block|{
if|if
condition|(
operator|!
name|indexMetaData
operator|.
name|aliases
argument_list|()
operator|.
name|containsKey
argument_list|(
name|aliasAction
operator|.
name|alias
argument_list|()
argument_list|)
condition|)
block|{
comment|// This alias doesn't exist - ignore
continue|continue;
block|}
name|indexMetaDataBuilder
operator|.
name|removerAlias
argument_list|(
name|aliasAction
operator|.
name|alias
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|changed
operator|=
literal|true
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|indexMetaDataBuilder
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|changed
condition|)
block|{
name|ClusterState
name|updatedState
init|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|metaData
argument_list|(
name|builder
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// even though changes happened, they resulted in 0 actual changes to metadata
comment|// i.e. remove and add the same alias to the same index
if|if
condition|(
name|updatedState
operator|.
name|metaData
argument_list|()
operator|.
name|aliases
argument_list|()
operator|.
name|equals
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|aliases
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|currentState
return|;
block|}
comment|// wait for responses from other nodes if needed
name|int
name|responseCount
init|=
name|updatedState
operator|.
name|nodes
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|long
name|version
init|=
name|updatedState
operator|.
name|version
argument_list|()
operator|+
literal|1
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"waiting for [{}] notifications with version [{}]"
argument_list|,
name|responseCount
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|aliasOperationPerformedAction
operator|.
name|add
argument_list|(
operator|new
name|CountDownListener
argument_list|(
name|responseCount
argument_list|,
name|listener
argument_list|,
name|version
argument_list|)
argument_list|,
name|request
operator|.
name|timeout
argument_list|)
expr_stmt|;
return|return
name|updatedState
return|;
block|}
else|else
block|{
comment|// Nothing to do
return|return
name|currentState
return|;
block|}
block|}
finally|finally
block|{
for|for
control|(
name|String
name|index
range|:
name|indicesToClose
control|)
block|{
name|indicesService
operator|.
name|removeIndex
argument_list|(
name|index
argument_list|,
literal|"created for alias processing"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|clusterStateProcessed
parameter_list|(
name|String
name|source
parameter_list|,
name|ClusterState
name|oldState
parameter_list|,
name|ClusterState
name|newState
parameter_list|)
block|{
if|if
condition|(
name|oldState
operator|==
name|newState
condition|)
block|{
comment|// we didn't do anything, callback
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|Response
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|interface|Listener
specifier|public
specifier|static
interface|interface
name|Listener
block|{
DECL|method|onResponse
name|void
name|onResponse
parameter_list|(
name|Response
name|response
parameter_list|)
function_decl|;
DECL|method|onFailure
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
function_decl|;
block|}
DECL|class|Request
specifier|public
specifier|static
class|class
name|Request
block|{
DECL|field|actions
specifier|final
name|AliasAction
index|[]
name|actions
decl_stmt|;
DECL|field|timeout
specifier|final
name|TimeValue
name|timeout
decl_stmt|;
DECL|field|masterTimeout
name|TimeValue
name|masterTimeout
init|=
name|MasterNodeOperationRequest
operator|.
name|DEFAULT_MASTER_NODE_TIMEOUT
decl_stmt|;
DECL|method|Request
specifier|public
name|Request
parameter_list|(
name|AliasAction
index|[]
name|actions
parameter_list|,
name|TimeValue
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|actions
operator|=
name|actions
expr_stmt|;
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
block|}
DECL|method|masterTimeout
specifier|public
name|Request
name|masterTimeout
parameter_list|(
name|TimeValue
name|masterTimeout
parameter_list|)
block|{
name|this
operator|.
name|masterTimeout
operator|=
name|masterTimeout
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
DECL|class|Response
specifier|public
specifier|static
class|class
name|Response
block|{
DECL|field|acknowledged
specifier|private
specifier|final
name|boolean
name|acknowledged
decl_stmt|;
DECL|method|Response
specifier|public
name|Response
parameter_list|(
name|boolean
name|acknowledged
parameter_list|)
block|{
name|this
operator|.
name|acknowledged
operator|=
name|acknowledged
expr_stmt|;
block|}
DECL|method|acknowledged
specifier|public
name|boolean
name|acknowledged
parameter_list|()
block|{
return|return
name|acknowledged
return|;
block|}
block|}
DECL|class|CountDownListener
specifier|private
class|class
name|CountDownListener
implements|implements
name|NodeAliasesUpdatedAction
operator|.
name|Listener
block|{
DECL|field|notified
specifier|private
specifier|final
name|AtomicBoolean
name|notified
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|field|countDown
specifier|private
specifier|final
name|AtomicInteger
name|countDown
decl_stmt|;
DECL|field|listener
specifier|private
specifier|final
name|Listener
name|listener
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|long
name|version
decl_stmt|;
DECL|method|CountDownListener
specifier|public
name|CountDownListener
parameter_list|(
name|int
name|countDown
parameter_list|,
name|Listener
name|listener
parameter_list|,
name|long
name|version
parameter_list|)
block|{
name|this
operator|.
name|countDown
operator|=
operator|new
name|AtomicInteger
argument_list|(
name|countDown
argument_list|)
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onAliasesUpdated
specifier|public
name|void
name|onAliasesUpdated
parameter_list|(
name|NodeAliasesUpdatedAction
operator|.
name|NodeAliasesUpdatedResponse
name|response
parameter_list|)
block|{
if|if
condition|(
name|version
operator|<=
name|response
operator|.
name|version
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Received NodeAliasesUpdatedResponse with version [{}] from [{}]"
argument_list|,
name|response
operator|.
name|version
argument_list|()
argument_list|,
name|response
operator|.
name|nodeId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|countDown
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|aliasOperationPerformedAction
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|notified
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|Response
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onTimeout
specifier|public
name|void
name|onTimeout
parameter_list|()
block|{
name|aliasOperationPerformedAction
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|notified
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|Response
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

