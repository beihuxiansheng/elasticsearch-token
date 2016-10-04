begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.admin.indices
package|package
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
name|admin
operator|.
name|indices
operator|.
name|stats
operator|.
name|IndicesStatsRequest
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
name|stats
operator|.
name|IndicesStatsResponse
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
name|IndicesOptions
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
name|node
operator|.
name|NodeClient
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
name|rest
operator|.
name|BaseRestHandler
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
name|BytesRestResponse
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
name|RestController
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
name|RestRequest
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
name|RestResponse
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
name|RestBuilderListener
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
name|Collections
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
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|GET
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
operator|.
name|OK
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|RestActions
operator|.
name|buildBroadcastShardsHeader
import|;
end_import

begin_class
DECL|class|RestIndicesStatsAction
specifier|public
class|class
name|RestIndicesStatsAction
extends|extends
name|BaseRestHandler
block|{
annotation|@
name|Inject
DECL|method|RestIndicesStatsAction
specifier|public
name|RestIndicesStatsAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_stats"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_stats/{metric}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_stats/{metric}/{indexMetric}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/{index}/_stats"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/{index}/_stats/{metric}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareRequest
specifier|public
name|RestChannelConsumer
name|prepareRequest
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|,
specifier|final
name|NodeClient
name|client
parameter_list|)
throws|throws
name|IOException
block|{
name|IndicesStatsRequest
name|indicesStatsRequest
init|=
operator|new
name|IndicesStatsRequest
argument_list|()
decl_stmt|;
name|indicesStatsRequest
operator|.
name|indicesOptions
argument_list|(
name|IndicesOptions
operator|.
name|fromRequest
argument_list|(
name|request
argument_list|,
name|indicesStatsRequest
operator|.
name|indicesOptions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|indices
argument_list|(
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"index"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|types
argument_list|(
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"types"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|metrics
init|=
name|Strings
operator|.
name|splitStringByCommaToSet
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"metric"
argument_list|,
literal|"_all"
argument_list|)
argument_list|)
decl_stmt|;
comment|// short cut, if no metrics have been specified in URI
if|if
condition|(
name|metrics
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|metrics
operator|.
name|contains
argument_list|(
literal|"_all"
argument_list|)
condition|)
block|{
name|indicesStatsRequest
operator|.
name|all
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|indicesStatsRequest
operator|.
name|clear
argument_list|()
expr_stmt|;
name|indicesStatsRequest
operator|.
name|docs
argument_list|(
name|metrics
operator|.
name|contains
argument_list|(
literal|"docs"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|store
argument_list|(
name|metrics
operator|.
name|contains
argument_list|(
literal|"store"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|indexing
argument_list|(
name|metrics
operator|.
name|contains
argument_list|(
literal|"indexing"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|search
argument_list|(
name|metrics
operator|.
name|contains
argument_list|(
literal|"search"
argument_list|)
operator|||
name|metrics
operator|.
name|contains
argument_list|(
literal|"suggest"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|get
argument_list|(
name|metrics
operator|.
name|contains
argument_list|(
literal|"get"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|merge
argument_list|(
name|metrics
operator|.
name|contains
argument_list|(
literal|"merge"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|refresh
argument_list|(
name|metrics
operator|.
name|contains
argument_list|(
literal|"refresh"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|flush
argument_list|(
name|metrics
operator|.
name|contains
argument_list|(
literal|"flush"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|warmer
argument_list|(
name|metrics
operator|.
name|contains
argument_list|(
literal|"warmer"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|queryCache
argument_list|(
name|metrics
operator|.
name|contains
argument_list|(
literal|"query_cache"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|segments
argument_list|(
name|metrics
operator|.
name|contains
argument_list|(
literal|"segments"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|fieldData
argument_list|(
name|metrics
operator|.
name|contains
argument_list|(
literal|"fielddata"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|completion
argument_list|(
name|metrics
operator|.
name|contains
argument_list|(
literal|"completion"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|requestCache
argument_list|(
name|metrics
operator|.
name|contains
argument_list|(
literal|"request_cache"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|recovery
argument_list|(
name|metrics
operator|.
name|contains
argument_list|(
literal|"recovery"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesStatsRequest
operator|.
name|translog
argument_list|(
name|metrics
operator|.
name|contains
argument_list|(
literal|"translog"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|hasParam
argument_list|(
literal|"groups"
argument_list|)
condition|)
block|{
name|indicesStatsRequest
operator|.
name|groups
argument_list|(
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"groups"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|hasParam
argument_list|(
literal|"types"
argument_list|)
condition|)
block|{
name|indicesStatsRequest
operator|.
name|types
argument_list|(
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"types"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indicesStatsRequest
operator|.
name|completion
argument_list|()
operator|&&
operator|(
name|request
operator|.
name|hasParam
argument_list|(
literal|"fields"
argument_list|)
operator|||
name|request
operator|.
name|hasParam
argument_list|(
literal|"completion_fields"
argument_list|)
operator|)
condition|)
block|{
name|indicesStatsRequest
operator|.
name|completionFields
argument_list|(
name|request
operator|.
name|paramAsStringArray
argument_list|(
literal|"completion_fields"
argument_list|,
name|request
operator|.
name|paramAsStringArray
argument_list|(
literal|"fields"
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indicesStatsRequest
operator|.
name|fieldData
argument_list|()
operator|&&
operator|(
name|request
operator|.
name|hasParam
argument_list|(
literal|"fields"
argument_list|)
operator|||
name|request
operator|.
name|hasParam
argument_list|(
literal|"fielddata_fields"
argument_list|)
operator|)
condition|)
block|{
name|indicesStatsRequest
operator|.
name|fieldDataFields
argument_list|(
name|request
operator|.
name|paramAsStringArray
argument_list|(
literal|"fielddata_fields"
argument_list|,
name|request
operator|.
name|paramAsStringArray
argument_list|(
literal|"fields"
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indicesStatsRequest
operator|.
name|segments
argument_list|()
operator|&&
name|request
operator|.
name|hasParam
argument_list|(
literal|"include_segment_file_sizes"
argument_list|)
condition|)
block|{
name|indicesStatsRequest
operator|.
name|includeSegmentFileSizes
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|channel
lambda|->
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|stats
argument_list|(
name|indicesStatsRequest
argument_list|,
operator|new
name|RestBuilderListener
argument_list|<
name|IndicesStatsResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
block|{
block|@Override             public RestResponse buildResponse(IndicesStatsResponse response
operator|,
name|XContentBuilder
name|builder
block|)
throws|throws
name|Exception
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|buildBroadcastShardsHeader
argument_list|(
name|builder
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|response
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
operator|new
name|BytesRestResponse
argument_list|(
name|OK
argument_list|,
name|builder
argument_list|)
return|;
block|}
block|}
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_function
unit|}      @
name|Override
DECL|method|canTripCircuitBreaker
specifier|public
name|boolean
name|canTripCircuitBreaker
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
end_function

begin_decl_stmt
DECL|field|RESPONSE_PARAMS
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|RESPONSE_PARAMS
init|=
name|Collections
operator|.
name|singleton
argument_list|(
literal|"level"
argument_list|)
decl_stmt|;
end_decl_stmt

begin_function
annotation|@
name|Override
DECL|method|responseParams
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|responseParams
parameter_list|()
block|{
return|return
name|RESPONSE_PARAMS
return|;
block|}
end_function

unit|}
end_unit

