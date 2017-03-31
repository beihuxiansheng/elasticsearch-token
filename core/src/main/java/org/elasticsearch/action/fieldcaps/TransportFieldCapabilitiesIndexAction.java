begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.fieldcaps
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|fieldcaps
package|;
end_package

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
name|mapper
operator|.
name|MappedFieldType
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

begin_class
DECL|class|TransportFieldCapabilitiesIndexAction
specifier|public
class|class
name|TransportFieldCapabilitiesIndexAction
extends|extends
name|TransportSingleShardAction
argument_list|<
name|FieldCapabilitiesIndexRequest
argument_list|,
name|FieldCapabilitiesIndexResponse
argument_list|>
block|{
DECL|field|ACTION_NAME
specifier|private
specifier|static
specifier|final
name|String
name|ACTION_NAME
init|=
name|FieldCapabilitiesAction
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
DECL|method|TransportFieldCapabilitiesIndexAction
specifier|public
name|TransportFieldCapabilitiesIndexAction
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
name|FieldCapabilitiesIndexRequest
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
name|FieldCapabilitiesIndexRequest
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
comment|// Resolve patterns and deduplicate
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
name|FieldCapabilitiesIndexResponse
name|shardOperation
parameter_list|(
specifier|final
name|FieldCapabilitiesIndexRequest
name|request
parameter_list|,
name|ShardId
name|shardId
parameter_list|)
block|{
name|MapperService
name|mapperService
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
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
operator|new
name|HashSet
argument_list|<>
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
name|fieldNames
operator|.
name|addAll
argument_list|(
name|mapperService
operator|.
name|simpleMatchToIndexNames
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|FieldCapabilities
argument_list|>
name|responseMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fieldNames
control|)
block|{
name|MappedFieldType
name|ft
init|=
name|mapperService
operator|.
name|fullName
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|FieldCapabilities
name|fieldCap
init|=
operator|new
name|FieldCapabilities
argument_list|(
name|field
argument_list|,
name|ft
operator|.
name|typeName
argument_list|()
argument_list|,
name|ft
operator|.
name|isSearchable
argument_list|()
argument_list|,
name|ft
operator|.
name|isAggregatable
argument_list|()
argument_list|)
decl_stmt|;
name|responseMap
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|fieldCap
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FieldCapabilitiesIndexResponse
argument_list|(
name|shardId
operator|.
name|getIndexName
argument_list|()
argument_list|,
name|responseMap
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|FieldCapabilitiesIndexResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|FieldCapabilitiesIndexResponse
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
block|}
end_class

end_unit

