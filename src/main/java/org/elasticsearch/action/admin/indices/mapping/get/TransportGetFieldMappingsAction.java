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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|Sets
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
name|master
operator|.
name|info
operator|.
name|TransportClusterInfoAction
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
name|IndicesService
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
name|Collection
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|TransportGetFieldMappingsAction
specifier|public
class|class
name|TransportGetFieldMappingsAction
extends|extends
name|TransportClusterInfoAction
argument_list|<
name|GetFieldMappingsRequest
argument_list|,
name|GetFieldMappingsResponse
argument_list|>
block|{
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportGetFieldMappingsAction
specifier|public
name|TransportGetFieldMappingsAction
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
name|IndicesService
name|indicesService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|)
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
DECL|method|transportAction
specifier|protected
name|String
name|transportAction
parameter_list|()
block|{
return|return
name|GetFieldMappingsAction
operator|.
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|newRequest
specifier|protected
name|GetFieldMappingsRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|GetFieldMappingsRequest
argument_list|()
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
DECL|method|doMasterOperation
specifier|protected
name|void
name|doMasterOperation
parameter_list|(
specifier|final
name|GetFieldMappingsRequest
name|request
parameter_list|,
specifier|final
name|ClusterState
name|state
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|GetFieldMappingsResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticsearchException
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|GetFieldMappingsResponse
argument_list|(
name|findMappings
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|,
name|request
operator|.
name|types
argument_list|()
argument_list|,
name|request
operator|.
name|fields
argument_list|()
argument_list|,
name|request
operator|.
name|includeDefaults
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|findMappings
specifier|private
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|FieldMappingMetaData
argument_list|>
argument_list|>
argument_list|>
name|findMappings
parameter_list|(
name|String
index|[]
name|concreteIndices
parameter_list|,
specifier|final
name|String
index|[]
name|types
parameter_list|,
specifier|final
name|String
index|[]
name|fields
parameter_list|,
name|boolean
name|includeDefaults
parameter_list|)
block|{
assert|assert
name|types
operator|!=
literal|null
assert|;
assert|assert
name|concreteIndices
operator|!=
literal|null
assert|;
if|if
condition|(
name|concreteIndices
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|()
return|;
block|}
name|boolean
name|isProbablySingleFieldRequest
init|=
name|concreteIndices
operator|.
name|length
operator|==
literal|1
operator|&&
name|types
operator|.
name|length
operator|==
literal|1
operator|&&
name|fields
operator|.
name|length
operator|==
literal|1
decl_stmt|;
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|FieldMappingMetaData
argument_list|>
argument_list|>
argument_list|>
name|indexMapBuilder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Sets
operator|.
name|SetView
argument_list|<
name|String
argument_list|>
name|intersection
init|=
name|Sets
operator|.
name|intersection
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|concreteIndices
argument_list|)
argument_list|,
name|indicesService
operator|.
name|indices
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|intersection
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
name|Collection
argument_list|<
name|String
argument_list|>
name|typeIntersection
decl_stmt|;
if|if
condition|(
name|types
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
name|Collections2
operator|.
name|filter
argument_list|(
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|types
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|types
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|ImmutableMap
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
argument_list|<
name|String
argument_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|FieldMappingMetaData
argument_list|>
argument_list|>
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
name|ImmutableMap
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
name|fields
argument_list|,
name|includeDefaults
argument_list|,
name|isProbablySingleFieldRequest
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
if|if
condition|(
operator|!
name|typeMappings
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|indexMapBuilder
operator|.
name|put
argument_list|(
name|index
argument_list|,
name|typeMappings
operator|.
name|immutableMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|indexMapBuilder
operator|.
name|build
argument_list|()
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
specifier|final
specifier|static
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
annotation|@
name|Override
annotation|@
name|Deprecated
specifier|public
name|Boolean
name|paramAsBooleanOptional
parameter_list|(
name|String
name|key
parameter_list|,
name|Boolean
name|defaultValue
parameter_list|)
block|{
return|return
name|paramAsBoolean
argument_list|(
name|key
argument_list|,
name|defaultValue
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|findFieldMappingsByType
specifier|private
name|ImmutableMap
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
name|String
index|[]
name|fields
parameter_list|,
name|boolean
name|includeDefaults
parameter_list|,
name|boolean
name|isProbablySingleFieldRequest
parameter_list|)
throws|throws
name|ElasticsearchException
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
argument_list|<
name|String
argument_list|,
name|FieldMappingMetaData
argument_list|>
argument_list|()
decl_stmt|;
name|ImmutableList
argument_list|<
name|FieldMapper
argument_list|>
name|allFieldMappers
init|=
name|documentMapper
operator|.
name|mappers
argument_list|()
operator|.
name|mappers
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
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
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
argument_list|,
name|fieldMapper
argument_list|,
name|fieldMappings
argument_list|,
name|includeDefaults
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
name|boolean
index|[]
name|resolved
init|=
operator|new
name|boolean
index|[
name|allFieldMappers
operator|.
name|size
argument_list|()
index|]
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
name|allFieldMappers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldMapper
name|fieldMapper
init|=
name|allFieldMappers
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
argument_list|)
condition|)
block|{
name|addFieldMapper
argument_list|(
name|fieldMapper
operator|.
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
argument_list|,
name|fieldMapper
argument_list|,
name|fieldMappings
argument_list|,
name|includeDefaults
argument_list|)
expr_stmt|;
name|resolved
index|[
name|i
index|]
operator|=
literal|true
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|allFieldMappers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|resolved
index|[
name|i
index|]
condition|)
block|{
continue|continue;
block|}
name|FieldMapper
name|fieldMapper
init|=
name|allFieldMappers
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
condition|)
block|{
name|addFieldMapper
argument_list|(
name|fieldMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|fieldMapper
argument_list|,
name|fieldMappings
argument_list|,
name|includeDefaults
argument_list|)
expr_stmt|;
name|resolved
index|[
name|i
index|]
operator|=
literal|true
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|allFieldMappers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|resolved
index|[
name|i
index|]
condition|)
block|{
continue|continue;
block|}
name|FieldMapper
name|fieldMapper
init|=
name|allFieldMappers
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
name|names
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
name|names
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|fieldMapper
argument_list|,
name|fieldMappings
argument_list|,
name|includeDefaults
argument_list|)
expr_stmt|;
name|resolved
index|[
name|i
index|]
operator|=
literal|true
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
name|documentMapper
operator|.
name|mappers
argument_list|()
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
name|includeDefaults
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isProbablySingleFieldRequest
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
name|names
argument_list|()
operator|.
name|fullName
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

