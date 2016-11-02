begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|admin
operator|.
name|indices
operator|.
name|alias
operator|.
name|IndicesAliasesClusterStateUpdateRequest
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
name|metadata
operator|.
name|AliasAction
operator|.
name|NewAliasValidator
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
name|IndexNotFoundException
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
name|IndexService
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
name|mapper
operator|.
name|MapperService
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
name|HashMap
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
name|Map
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
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
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

begin_comment
comment|/**  * Service responsible for submitting add and remove aliases requests  */
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
DECL|field|aliasValidator
specifier|private
specifier|final
name|AliasValidator
name|aliasValidator
decl_stmt|;
DECL|field|deleteIndexService
specifier|private
specifier|final
name|MetaDataDeleteIndexService
name|deleteIndexService
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
name|AliasValidator
name|aliasValidator
parameter_list|,
name|MetaDataDeleteIndexService
name|deleteIndexService
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
name|aliasValidator
operator|=
name|aliasValidator
expr_stmt|;
name|this
operator|.
name|deleteIndexService
operator|=
name|deleteIndexService
expr_stmt|;
block|}
DECL|method|indicesAliases
specifier|public
name|void
name|indicesAliases
parameter_list|(
specifier|final
name|IndicesAliasesClusterStateUpdateRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|ClusterStateUpdateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"index-aliases"
argument_list|,
operator|new
name|AckedClusterStateUpdateTask
argument_list|<
name|ClusterStateUpdateResponse
argument_list|>
argument_list|(
name|Priority
operator|.
name|URGENT
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|ClusterStateUpdateResponse
name|newResponse
parameter_list|(
name|boolean
name|acknowledged
parameter_list|)
block|{
return|return
operator|new
name|ClusterStateUpdateResponse
argument_list|(
name|acknowledged
argument_list|)
return|;
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
return|return
name|innerExecute
argument_list|(
name|currentState
argument_list|,
name|request
operator|.
name|actions
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|innerExecute
name|ClusterState
name|innerExecute
parameter_list|(
name|ClusterState
name|currentState
parameter_list|,
name|Iterable
argument_list|<
name|AliasAction
argument_list|>
name|actions
parameter_list|)
block|{
name|List
argument_list|<
name|Index
argument_list|>
name|indicesToClose
init|=
operator|new
name|ArrayList
argument_list|<>
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
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
comment|// Gather all the indexes that must be removed first so:
comment|// 1. We don't cause error when attempting to replace an index with a alias of the same name.
comment|// 2. We don't allow removal of aliases from indexes that we're just going to delete anyway. That'd be silly.
name|Set
argument_list|<
name|Index
argument_list|>
name|indicesToDelete
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AliasAction
name|action
range|:
name|actions
control|)
block|{
if|if
condition|(
name|action
operator|.
name|removeIndex
argument_list|()
condition|)
block|{
name|IndexMetaData
name|index
init|=
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|getIndices
argument_list|()
operator|.
name|get
argument_list|(
name|action
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexNotFoundException
argument_list|(
name|action
operator|.
name|getIndex
argument_list|()
argument_list|)
throw|;
block|}
name|indicesToDelete
operator|.
name|add
argument_list|(
name|index
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|// Remove the indexes if there are any to remove
if|if
condition|(
name|changed
condition|)
block|{
name|currentState
operator|=
name|deleteIndexService
operator|.
name|deleteIndices
argument_list|(
name|currentState
argument_list|,
name|indicesToDelete
argument_list|)
expr_stmt|;
block|}
name|MetaData
operator|.
name|Builder
name|metadata
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
comment|// Run the remaining alias actions
for|for
control|(
name|AliasAction
name|action
range|:
name|actions
control|)
block|{
if|if
condition|(
name|action
operator|.
name|removeIndex
argument_list|()
condition|)
block|{
comment|// Handled above
continue|continue;
block|}
name|IndexMetaData
name|index
init|=
name|metadata
operator|.
name|get
argument_list|(
name|action
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexNotFoundException
argument_list|(
name|action
operator|.
name|getIndex
argument_list|()
argument_list|)
throw|;
block|}
name|NewAliasValidator
name|newAliasValidator
init|=
parameter_list|(
name|alias
parameter_list|,
name|indexRouting
parameter_list|,
name|filter
parameter_list|)
lambda|->
block|{
comment|/* It is important that we look up the index using the metadata builder we are modifying so we can remove an                      * index and replace it with an alias. */
name|Function
argument_list|<
name|String
argument_list|,
name|IndexMetaData
argument_list|>
name|indexLookup
init|=
name|name
lambda|->
name|metadata
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|aliasValidator
operator|.
name|validateAlias
argument_list|(
name|alias
argument_list|,
name|action
operator|.
name|getIndex
argument_list|()
argument_list|,
name|indexRouting
argument_list|,
name|indexLookup
argument_list|)
expr_stmt|;
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
name|IndexService
name|indexService
init|=
name|indices
operator|.
name|get
argument_list|(
name|index
operator|.
name|getIndex
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
name|index
operator|.
name|getIndex
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
comment|// temporarily create the index and add mappings so we can parse the filter
try|try
block|{
name|indexService
operator|=
name|indicesService
operator|.
name|createIndex
argument_list|(
name|index
argument_list|,
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Failed to create temporary index for parsing the alias"
argument_list|,
name|e
argument_list|)
throw|;
block|}
for|for
control|(
name|ObjectCursor
argument_list|<
name|MappingMetaData
argument_list|>
name|cursor
range|:
name|index
operator|.
name|getMappings
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|MappingMetaData
name|mappingMetaData
init|=
name|cursor
operator|.
name|value
decl_stmt|;
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|merge
argument_list|(
name|mappingMetaData
operator|.
name|type
argument_list|()
argument_list|,
name|mappingMetaData
operator|.
name|source
argument_list|()
argument_list|,
name|MapperService
operator|.
name|MergeReason
operator|.
name|MAPPING_RECOVERY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|indicesToClose
operator|.
name|add
argument_list|(
name|index
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|indices
operator|.
name|put
argument_list|(
name|action
operator|.
name|getIndex
argument_list|()
argument_list|,
name|indexService
argument_list|)
expr_stmt|;
block|}
comment|// the context is only used for validation so it's fine to pass fake values for the shard id and the current
comment|// timestamp
name|aliasValidator
operator|.
name|validateAliasFilter
argument_list|(
name|alias
argument_list|,
name|filter
argument_list|,
name|indexService
operator|.
name|newQueryShardContext
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|,
parameter_list|()
lambda|->
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|changed
operator||=
name|action
operator|.
name|apply
argument_list|(
name|newAliasValidator
argument_list|,
name|metadata
argument_list|,
name|index
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
name|ClusterState
operator|.
name|builder
argument_list|(
name|currentState
argument_list|)
operator|.
name|metaData
argument_list|(
name|metadata
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// even though changes happened, they resulted in 0 actual changes to metadata
comment|// i.e. remove and add the same alias to the same index
if|if
condition|(
operator|!
name|updatedState
operator|.
name|metaData
argument_list|()
operator|.
name|equalsAliases
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|updatedState
return|;
block|}
block|}
return|return
name|currentState
return|;
block|}
finally|finally
block|{
for|for
control|(
name|Index
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
block|}
end_class

end_unit

