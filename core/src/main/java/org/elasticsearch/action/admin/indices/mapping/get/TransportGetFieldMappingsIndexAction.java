begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.mapping.get
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
name|mapping
operator|.
name|get
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
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|get
operator|.
name|GetFieldMappingsResponse
operator|.
name|FieldMappingMetaData
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
name|single
operator|.
name|shard
operator|.
name|TransportSingleShardAction
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
name|routing
operator|.
name|ShardsIterator
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
name|MapBuilder
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
name|ToXContent
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
name|XContentBuilder
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
name|XContentType
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
name|DocumentFieldMappers
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
name|FieldMapper
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
name|shard
operator|.
name|ShardId
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
name|TypeMissingException
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|singletonMap
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
name|util
operator|.
name|CollectionUtils
operator|.
name|newLinkedList
import|;
end_import

begin_comment
comment|/**  * Transport action used to retrieve the mappings related to fields that belong to a specific index  */
end_comment

begin_class
DECL|class|TransportGetFieldMappingsIndexAction
specifier|public
class|class
name|TransportGetFieldMappingsIndexAction
extends|extends
name|TransportSingleShardAction
argument_list|<
name|GetFieldMappingsIndexRequest
argument_list|,
name|GetFieldMappingsResponse
argument_list|>
block|{
DECL|field|ACTION_NAME
specifier|private
specifier|static
specifier|final
name|String
name|ACTION_NAME
init|=
name|GetFieldMappingsAction
operator|.
name|NAME
operator|+
literal|"[index]"
decl_stmt|;
DECL|field|clusterService
specifier|protected
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
annotation|@
name|Inject
DECL|method|TransportGetFieldMappingsIndexAction
specifier|public
name|TransportGetFieldMappingsIndexAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|IndicesService
name|indicesService
parameter_list|,
name|ThreadPool
name|threadPool
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
name|ACTION_NAME
argument_list|,
name|threadPool
argument_list|,
name|clusterService
argument_list|,
name|transportService
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|GetFieldMappingsIndexRequest
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|MANAGEMENT
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
annotation|@
name|Override
DECL|method|resolveIndex
specifier|protected
name|boolean
name|resolveIndex
parameter_list|(
name|GetFieldMappingsIndexRequest
name|request
parameter_list|)
block|{
comment|//internal action, index already resolved
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|shards
specifier|protected
name|ShardsIterator
name|shards
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|InternalRequest
name|request
parameter_list|)
block|{
comment|// Will balance requests between shards
return|return
name|state
operator|.
name|routingTable
argument_list|()
operator|.
name|index
argument_list|(
name|request
operator|.
name|concreteIndex
argument_list|()
argument_list|)
operator|.
name|randomAllActiveShardsIt
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|shardOperation
specifier|protected
name|GetFieldMappingsResponse
name|shardOperation
parameter_list|(
specifier|final
name|GetFieldMappingsIndexRequest
name|request
parameter_list|,
name|ShardId
name|shardId
parameter_list|)
block|{
assert|assert
name|shardId
operator|!=
literal|null
assert|;
name|IndexService
name|indexService
init|=
name|indicesService
operator|.
name|indexServiceSafe
argument_list|(
name|shardId
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|typeIntersection
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|types
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|typeIntersection
operator|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|types
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|typeIntersection
operator|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|types
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|type
lambda|->
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|request
operator|.
name|types
argument_list|()
argument_list|,
name|type
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toCollection
argument_list|(
name|ArrayList
operator|::
operator|new
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|typeIntersection
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|TypeMissingException
argument_list|(
name|shardId
operator|.
name|getIndex
argument_list|()
argument_list|,
name|request
operator|.
name|types
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FieldMappingMetaData
argument_list|>
argument_list|>
name|typeMappings
init|=
operator|new
name|MapBuilder
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|type
range|:
name|typeIntersection
control|)
block|{
name|DocumentMapper
name|documentMapper
init|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|FieldMappingMetaData
argument_list|>
name|fieldMapping
init|=
name|findFieldMappingsByType
argument_list|(
name|documentMapper
argument_list|,
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fieldMapping
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|typeMappings
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|fieldMapping
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|GetFieldMappingsResponse
argument_list|(
name|singletonMap
argument_list|(
name|shardId
operator|.
name|getIndexName
argument_list|()
argument_list|,
name|typeMappings
operator|.
name|immutableMap
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|GetFieldMappingsResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|GetFieldMappingsResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|checkRequestBlock
specifier|protected
name|ClusterBlockException
name|checkRequestBlock
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|InternalRequest
name|request
parameter_list|)
block|{
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|indexBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|METADATA_READ
argument_list|,
name|request
operator|.
name|concreteIndex
argument_list|()
argument_list|)
return|;
block|}
DECL|field|includeDefaultsParams
specifier|private
specifier|static
specifier|final
name|ToXContent
operator|.
name|Params
name|includeDefaultsParams
init|=
operator|new
name|ToXContent
operator|.
name|Params
argument_list|()
block|{
specifier|static
specifier|final
name|String
name|INCLUDE_DEFAULTS
init|=
literal|"include_defaults"
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|INCLUDE_DEFAULTS
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
literal|"true"
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|INCLUDE_DEFAULTS
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
literal|"true"
return|;
block|}
return|return
name|defaultValue
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|paramAsBoolean
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|INCLUDE_DEFAULTS
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|defaultValue
return|;
block|}
annotation|@
name|Override
specifier|public
name|Boolean
name|paramAsBoolean
parameter_list|(
name|String
name|key
parameter_list|,
name|Boolean
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|INCLUDE_DEFAULTS
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|defaultValue
return|;
block|}
block|}
decl_stmt|;
DECL|method|findFieldMappingsByType
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|FieldMappingMetaData
argument_list|>
name|findFieldMappingsByType
parameter_list|(
name|DocumentMapper
name|documentMapper
parameter_list|,
name|GetFieldMappingsIndexRequest
name|request
parameter_list|)
block|{
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|FieldMappingMetaData
argument_list|>
name|fieldMappings
init|=
operator|new
name|MapBuilder
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|DocumentFieldMappers
name|allFieldMappers
init|=
name|documentMapper
operator|.
name|mappers
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|request
operator|.
name|fields
argument_list|()
control|)
block|{
if|if
condition|(
name|Regex
operator|.
name|isMatchAllPattern
argument_list|(
name|field
argument_list|)
condition|)
block|{
for|for
control|(
name|FieldMapper
name|fieldMapper
range|:
name|allFieldMappers
control|)
block|{
name|addFieldMapper
argument_list|(
name|fieldMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|fieldMapper
argument_list|,
name|fieldMappings
argument_list|,
name|request
operator|.
name|includeDefaults
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|Regex
operator|.
name|isSimpleMatchPattern
argument_list|(
name|field
argument_list|)
condition|)
block|{
comment|// go through the field mappers 3 times, to make sure we give preference to the resolve order: full name, index name, name.
comment|// also make sure we only store each mapper once.
name|Collection
argument_list|<
name|FieldMapper
argument_list|>
name|remainingFieldMappers
init|=
name|newLinkedList
argument_list|(
name|allFieldMappers
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|FieldMapper
argument_list|>
name|it
init|=
name|remainingFieldMappers
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|FieldMapper
name|fieldMapper
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|field
argument_list|,
name|fieldMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|addFieldMapper
argument_list|(
name|fieldMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|fieldMapper
argument_list|,
name|fieldMappings
argument_list|,
name|request
operator|.
name|includeDefaults
argument_list|()
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|Iterator
argument_list|<
name|FieldMapper
argument_list|>
name|it
init|=
name|remainingFieldMappers
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|FieldMapper
name|fieldMapper
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|field
argument_list|,
name|fieldMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|addFieldMapper
argument_list|(
name|fieldMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|fieldMapper
argument_list|,
name|fieldMappings
argument_list|,
name|request
operator|.
name|includeDefaults
argument_list|()
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// not a pattern
name|FieldMapper
name|fieldMapper
init|=
name|allFieldMappers
operator|.
name|smartNameFieldMapper
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldMapper
operator|!=
literal|null
condition|)
block|{
name|addFieldMapper
argument_list|(
name|field
argument_list|,
name|fieldMapper
argument_list|,
name|fieldMappings
argument_list|,
name|request
operator|.
name|includeDefaults
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|probablySingleFieldRequest
argument_list|()
condition|)
block|{
name|fieldMappings
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|FieldMappingMetaData
operator|.
name|NULL
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|fieldMappings
operator|.
name|immutableMap
argument_list|()
return|;
block|}
DECL|method|addFieldMapper
specifier|private
name|void
name|addFieldMapper
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldMapper
name|fieldMapper
parameter_list|,
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|FieldMappingMetaData
argument_list|>
name|fieldMappings
parameter_list|,
name|boolean
name|includeDefaults
parameter_list|)
block|{
if|if
condition|(
name|fieldMappings
operator|.
name|containsKey
argument_list|(
name|field
argument_list|)
condition|)
block|{
return|return;
block|}
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|fieldMapper
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|includeDefaults
condition|?
name|includeDefaultsParams
else|:
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|fieldMappings
operator|.
name|put
argument_list|(
name|field
argument_list|,
operator|new
name|FieldMappingMetaData
argument_list|(
name|fieldMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|builder
operator|.
name|bytes
argument_list|()
argument_list|)
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
literal|"failed to serialize XContent of field ["
operator|+
name|field
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

