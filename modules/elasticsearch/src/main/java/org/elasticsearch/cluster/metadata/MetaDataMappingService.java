begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ClusterStateUpdateTask
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
name|ProcessedClusterStateUpdateTask
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
name|compress
operator|.
name|CompressedString
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
name|mapper
operator|.
name|DocumentMapper
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
name|index
operator|.
name|mapper
operator|.
name|MergeMappingException
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
name|InvalidTypeNameException
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
name|Map
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
name|*
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
name|*
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
name|*
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
name|collect
operator|.
name|Maps
operator|.
name|*
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
name|mapper
operator|.
name|DocumentMapper
operator|.
name|MergeFlags
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|MetaDataMappingService
specifier|public
class|class
name|MetaDataMappingService
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
DECL|method|MetaDataMappingService
annotation|@
name|Inject
specifier|public
name|MetaDataMappingService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|IndicesService
name|indicesService
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
block|}
DECL|method|updateMapping
specifier|public
name|void
name|updateMapping
parameter_list|(
specifier|final
name|String
name|index
parameter_list|,
specifier|final
name|String
name|type
parameter_list|,
specifier|final
name|CompressedString
name|mappingSource
parameter_list|)
throws|throws
name|IOException
block|{
name|updateMapping
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|mappingSource
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|updateMapping
specifier|public
name|void
name|updateMapping
parameter_list|(
specifier|final
name|String
name|index
parameter_list|,
specifier|final
name|String
name|type
parameter_list|,
specifier|final
name|String
name|mappingSource
parameter_list|)
block|{
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"update-mapping ["
operator|+
name|index
operator|+
literal|"]["
operator|+
name|type
operator|+
literal|"]"
argument_list|,
operator|new
name|ClusterStateUpdateTask
argument_list|()
block|{
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
try|try
block|{
name|IndexService
name|indexService
init|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexService
operator|==
literal|null
condition|)
block|{
comment|// we need to create the index here, and add the current mapping to it, so we can merge
specifier|final
name|IndexMetaData
name|indexMetaData
init|=
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
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
comment|// only add the current relevant mapping (if exists)
if|if
condition|(
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|add
argument_list|(
name|type
argument_list|,
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|.
name|source
argument_list|()
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|MapperService
name|mapperService
init|=
name|indexService
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|DocumentMapper
name|existingMapper
init|=
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|type
argument_list|)
decl_stmt|;
comment|// parse the updated one
name|DocumentMapper
name|updatedMapper
init|=
name|mapperService
operator|.
name|parse
argument_list|(
name|type
argument_list|,
name|mappingSource
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingMapper
operator|==
literal|null
condition|)
block|{
name|existingMapper
operator|=
name|updatedMapper
expr_stmt|;
block|}
else|else
block|{
comment|// merge from the updated into the existing, ignore conflicts (we know we have them, we just want the new ones)
name|existingMapper
operator|.
name|merge
argument_list|(
name|updatedMapper
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// build the updated mapping source
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
try|try
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}] update_mapping [{}] (dynamic) with source [{}]"
argument_list|,
name|index
argument_list|,
name|type
argument_list|,
name|existingMapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|string
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
comment|// ignore
block|}
block|}
elseif|else
if|if
condition|(
name|logger
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] update_mapping [{}] (dynamic)"
argument_list|,
name|index
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
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
name|IndexMetaData
name|indexMetaData
init|=
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|newIndexMetaDataBuilder
argument_list|(
name|indexMetaData
argument_list|)
operator|.
name|putMapping
argument_list|(
operator|new
name|MappingMetaData
argument_list|(
name|existingMapper
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
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
return|;
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
literal|"failed to dynamically update the mapping in cluster_state from shard"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|currentState
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|removeMapping
specifier|public
name|void
name|removeMapping
parameter_list|(
specifier|final
name|RemoveRequest
name|request
parameter_list|)
block|{
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"remove-mapping ["
operator|+
name|request
operator|.
name|mappingType
operator|+
literal|"]"
argument_list|,
operator|new
name|ProcessedClusterStateUpdateTask
argument_list|()
block|{
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
if|if
condition|(
name|request
operator|.
name|indices
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
literal|"_all"
argument_list|)
argument_list|)
throw|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] remove_mapping [{}]"
argument_list|,
name|request
operator|.
name|indices
argument_list|,
name|request
operator|.
name|mappingType
argument_list|)
expr_stmt|;
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
name|String
name|indexName
range|:
name|request
operator|.
name|indices
control|)
block|{
if|if
condition|(
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|indexName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|newIndexMetaDataBuilder
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|indexName
argument_list|)
argument_list|)
operator|.
name|removeMapping
argument_list|(
name|request
operator|.
name|mappingType
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ClusterState
operator|.
name|builder
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
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clusterStateProcessed
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
block|{
comment|// TODO add a listener here!
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|putMapping
specifier|public
name|void
name|putMapping
parameter_list|(
specifier|final
name|PutRequest
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
literal|"put-mapping ["
operator|+
name|request
operator|.
name|mappingType
operator|+
literal|"]"
argument_list|,
operator|new
name|ProcessedClusterStateUpdateTask
argument_list|()
block|{
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
try|try
block|{
if|if
condition|(
name|request
operator|.
name|indices
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
literal|"_all"
argument_list|)
argument_list|)
throw|;
block|}
for|for
control|(
name|String
name|index
range|:
name|request
operator|.
name|indices
control|)
block|{
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
name|index
argument_list|)
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// pre create indices here and add mappings to them so we can merge the mappings here if needed
for|for
control|(
name|String
name|index
range|:
name|request
operator|.
name|indices
control|)
block|{
if|if
condition|(
name|indicesService
operator|.
name|hasIndex
argument_list|(
name|index
argument_list|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|IndexMetaData
name|indexMetaData
init|=
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|IndexService
name|indexService
init|=
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
decl_stmt|;
comment|// only add the current relevant mapping (if exists)
if|if
condition|(
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|containsKey
argument_list|(
name|request
operator|.
name|mappingType
argument_list|)
condition|)
block|{
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|add
argument_list|(
name|request
operator|.
name|mappingType
argument_list|,
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|get
argument_list|(
name|request
operator|.
name|mappingType
argument_list|)
operator|.
name|source
argument_list|()
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|DocumentMapper
argument_list|>
name|newMappers
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|DocumentMapper
argument_list|>
name|existingMappers
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|request
operator|.
name|indices
control|)
block|{
name|IndexService
name|indexService
init|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexService
operator|!=
literal|null
condition|)
block|{
comment|// try and parse it (no need to add it here) so we can bail early in case of parsing exception
name|DocumentMapper
name|newMapper
init|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|parse
argument_list|(
name|request
operator|.
name|mappingType
argument_list|,
name|request
operator|.
name|mappingSource
argument_list|)
decl_stmt|;
name|newMappers
operator|.
name|put
argument_list|(
name|index
argument_list|,
name|newMapper
argument_list|)
expr_stmt|;
name|DocumentMapper
name|existingMapper
init|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|request
operator|.
name|mappingType
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingMapper
operator|!=
literal|null
condition|)
block|{
comment|// first, simulate
name|DocumentMapper
operator|.
name|MergeResult
name|mergeResult
init|=
name|existingMapper
operator|.
name|merge
argument_list|(
name|newMapper
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
comment|// if we have conflicts, and we are not supposed to ignore them, throw an exception
if|if
condition|(
operator|!
name|request
operator|.
name|ignoreConflicts
operator|&&
name|mergeResult
operator|.
name|hasConflicts
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|MergeMappingException
argument_list|(
name|mergeResult
operator|.
name|conflicts
argument_list|()
argument_list|)
throw|;
block|}
name|existingMappers
operator|.
name|put
argument_list|(
name|index
argument_list|,
name|existingMapper
argument_list|)
expr_stmt|;
block|}
block|}
else|else
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
block|}
name|String
name|mappingType
init|=
name|request
operator|.
name|mappingType
decl_stmt|;
if|if
condition|(
name|mappingType
operator|==
literal|null
condition|)
block|{
name|mappingType
operator|=
name|newMappers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|type
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|mappingType
operator|.
name|equals
argument_list|(
name|newMappers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|type
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidTypeNameException
argument_list|(
literal|"Type name provided does not match type name within mapping definition"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|MapperService
operator|.
name|DEFAULT_MAPPING
operator|.
name|equals
argument_list|(
name|mappingType
argument_list|)
operator|&&
name|mappingType
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'_'
condition|)
block|{
throw|throw
operator|new
name|InvalidTypeNameException
argument_list|(
literal|"Document mapping type name can't start with '_'"
argument_list|)
throw|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
name|mappings
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|DocumentMapper
argument_list|>
name|entry
range|:
name|newMappers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|index
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
comment|// do the actual merge here on the master, and update the mapping source
name|DocumentMapper
name|newMapper
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|existingMappers
operator|.
name|containsKey
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
comment|// we have an existing mapping, do the merge here (on the master), it will automatically update the mapping source
name|DocumentMapper
name|existingMapper
init|=
name|existingMappers
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|CompressedString
name|existingSource
init|=
name|existingMapper
operator|.
name|mappingSource
argument_list|()
decl_stmt|;
name|existingMapper
operator|.
name|merge
argument_list|(
name|newMapper
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|CompressedString
name|updatedSource
init|=
name|existingMapper
operator|.
name|mappingSource
argument_list|()
decl_stmt|;
if|if
condition|(
name|existingSource
operator|.
name|equals
argument_list|(
name|updatedSource
argument_list|)
condition|)
block|{
comment|// same source, no changes, ignore it
block|}
else|else
block|{
comment|// use the merged mapping source
name|mappings
operator|.
name|put
argument_list|(
name|index
argument_list|,
operator|new
name|MappingMetaData
argument_list|(
name|existingMapper
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}] update_mapping [{}] with source [{}]"
argument_list|,
name|index
argument_list|,
name|existingMapper
operator|.
name|type
argument_list|()
argument_list|,
name|updatedSource
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|logger
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] update_mapping [{}]"
argument_list|,
name|index
argument_list|,
name|existingMapper
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|CompressedString
name|newSource
init|=
name|newMapper
operator|.
name|mappingSource
argument_list|()
decl_stmt|;
name|mappings
operator|.
name|put
argument_list|(
name|index
argument_list|,
operator|new
name|MappingMetaData
argument_list|(
name|newMapper
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}] create_mapping [{}] with source [{}]"
argument_list|,
name|index
argument_list|,
name|newMapper
operator|.
name|type
argument_list|()
argument_list|,
name|newSource
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|logger
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] create_mapping [{}]"
argument_list|,
name|index
argument_list|,
name|newMapper
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|mappings
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// no changes, return
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
return|return
name|currentState
return|;
block|}
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
name|String
name|indexName
range|:
name|request
operator|.
name|indices
control|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|indexName
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
name|indexName
argument_list|)
argument_list|)
throw|;
block|}
name|MappingMetaData
name|mappingMd
init|=
name|mappings
operator|.
name|get
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
if|if
condition|(
name|mappingMd
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|newIndexMetaDataBuilder
argument_list|(
name|indexMetaData
argument_list|)
operator|.
name|putMapping
argument_list|(
name|mappingMd
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
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
return|;
block|}
catch|catch
parameter_list|(
name|Exception
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
return|return
name|currentState
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|clusterStateProcessed
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
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
DECL|class|RemoveRequest
specifier|public
specifier|static
class|class
name|RemoveRequest
block|{
DECL|field|indices
specifier|final
name|String
index|[]
name|indices
decl_stmt|;
DECL|field|mappingType
specifier|final
name|String
name|mappingType
decl_stmt|;
DECL|method|RemoveRequest
specifier|public
name|RemoveRequest
parameter_list|(
name|String
index|[]
name|indices
parameter_list|,
name|String
name|mappingType
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
name|this
operator|.
name|mappingType
operator|=
name|mappingType
expr_stmt|;
block|}
block|}
DECL|class|PutRequest
specifier|public
specifier|static
class|class
name|PutRequest
block|{
DECL|field|indices
specifier|final
name|String
index|[]
name|indices
decl_stmt|;
DECL|field|mappingType
specifier|final
name|String
name|mappingType
decl_stmt|;
DECL|field|mappingSource
specifier|final
name|String
name|mappingSource
decl_stmt|;
DECL|field|ignoreConflicts
name|boolean
name|ignoreConflicts
init|=
literal|false
decl_stmt|;
DECL|field|timeout
name|TimeValue
name|timeout
init|=
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|10
argument_list|)
decl_stmt|;
DECL|method|PutRequest
specifier|public
name|PutRequest
parameter_list|(
name|String
index|[]
name|indices
parameter_list|,
name|String
name|mappingType
parameter_list|,
name|String
name|mappingSource
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
name|this
operator|.
name|mappingType
operator|=
name|mappingType
expr_stmt|;
name|this
operator|.
name|mappingSource
operator|=
name|mappingSource
expr_stmt|;
block|}
DECL|method|ignoreConflicts
specifier|public
name|PutRequest
name|ignoreConflicts
parameter_list|(
name|boolean
name|ignoreConflicts
parameter_list|)
block|{
name|this
operator|.
name|ignoreConflicts
operator|=
name|ignoreConflicts
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|timeout
specifier|public
name|PutRequest
name|timeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
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
block|}
end_class

end_unit

