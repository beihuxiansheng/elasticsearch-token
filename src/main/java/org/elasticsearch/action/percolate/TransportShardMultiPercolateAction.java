begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.percolate
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|percolate
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
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
name|ActionResponse
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
name|TransportActions
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
name|SingleShardOperationRequest
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
name|TransportShardSingleOperationAction
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
name|routing
operator|.
name|ShardIterator
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|text
operator|.
name|StringText
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
name|text
operator|.
name|Text
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|percolator
operator|.
name|PercolatorService
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
name|List
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|TransportShardMultiPercolateAction
specifier|public
class|class
name|TransportShardMultiPercolateAction
extends|extends
name|TransportShardSingleOperationAction
argument_list|<
name|TransportShardMultiPercolateAction
operator|.
name|Request
argument_list|,
name|TransportShardMultiPercolateAction
operator|.
name|Response
argument_list|>
block|{
DECL|field|percolatorService
specifier|private
specifier|final
name|PercolatorService
name|percolatorService
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportShardMultiPercolateAction
specifier|public
name|TransportShardMultiPercolateAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|PercolatorService
name|percolatorService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|,
name|clusterService
argument_list|,
name|transportService
argument_list|)
expr_stmt|;
name|this
operator|.
name|percolatorService
operator|=
name|percolatorService
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
literal|"mpercolate/shard"
return|;
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
name|PERCOLATE
return|;
block|}
annotation|@
name|Override
DECL|method|newRequest
specifier|protected
name|Request
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|Request
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|Response
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|Response
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|checkGlobalBlock
specifier|protected
name|ClusterBlockException
name|checkGlobalBlock
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|Request
name|request
parameter_list|)
block|{
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|globalBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|READ
argument_list|)
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
name|Request
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
name|READ
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shards
specifier|protected
name|ShardIterator
name|shards
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|Request
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|clusterService
operator|.
name|operationRouting
argument_list|()
operator|.
name|getShards
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|request
operator|.
name|shardId
argument_list|()
argument_list|,
name|request
operator|.
name|preference
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shardOperation
specifier|protected
name|Response
name|shardOperation
parameter_list|(
name|Request
name|request
parameter_list|,
name|int
name|shardId
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
comment|// TODO: Look into combining the shard req's docs into one in memory index.
name|Response
name|response
init|=
operator|new
name|Response
argument_list|()
decl_stmt|;
name|response
operator|.
name|items
operator|=
operator|new
name|ArrayList
argument_list|<
name|Response
operator|.
name|Item
argument_list|>
argument_list|(
name|request
operator|.
name|items
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Request
operator|.
name|Item
name|item
range|:
name|request
operator|.
name|items
control|)
block|{
name|Response
operator|.
name|Item
name|responseItem
decl_stmt|;
name|int
name|slot
init|=
name|item
operator|.
name|slot
decl_stmt|;
try|try
block|{
name|responseItem
operator|=
operator|new
name|Response
operator|.
name|Item
argument_list|(
name|slot
argument_list|,
name|percolatorService
operator|.
name|percolate
argument_list|(
name|item
operator|.
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}][{}] failed to multi percolate"
argument_list|,
name|e
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|request
operator|.
name|shardId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|TransportActions
operator|.
name|isShardNotAvailableException
argument_list|(
name|e
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchException
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
throw|;
block|}
else|else
block|{
name|responseItem
operator|=
operator|new
name|Response
operator|.
name|Item
argument_list|(
name|slot
argument_list|,
operator|new
name|StringText
argument_list|(
name|ExceptionsHelper
operator|.
name|detailedMessage
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|response
operator|.
name|items
operator|.
name|add
argument_list|(
name|responseItem
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
DECL|class|Request
specifier|public
specifier|static
class|class
name|Request
extends|extends
name|SingleShardOperationRequest
block|{
DECL|field|shardId
specifier|private
name|int
name|shardId
decl_stmt|;
DECL|field|preference
specifier|private
name|String
name|preference
decl_stmt|;
DECL|field|items
specifier|private
name|List
argument_list|<
name|Item
argument_list|>
name|items
decl_stmt|;
DECL|field|done
specifier|private
specifier|volatile
name|boolean
name|done
init|=
literal|false
decl_stmt|;
DECL|method|Request
specifier|public
name|Request
parameter_list|()
block|{         }
DECL|method|Request
specifier|public
name|Request
parameter_list|(
name|String
name|concreteIndex
parameter_list|,
name|int
name|shardId
parameter_list|,
name|String
name|preference
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|concreteIndex
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|preference
operator|=
name|preference
expr_stmt|;
name|this
operator|.
name|items
operator|=
operator|new
name|ArrayList
argument_list|<
name|Item
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|shardId
specifier|public
name|int
name|shardId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Item
name|item
parameter_list|)
block|{
name|items
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
DECL|method|items
specifier|public
name|List
argument_list|<
name|Item
argument_list|>
name|items
parameter_list|()
block|{
return|return
name|items
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|shardId
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|preference
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|items
operator|=
operator|new
name|ArrayList
argument_list|<
name|Item
argument_list|>
argument_list|(
name|size
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|int
name|slot
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|PercolateShardRequest
name|shardRequest
init|=
operator|new
name|PercolateShardRequest
argument_list|(
name|index
argument_list|()
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
name|shardRequest
operator|.
name|documentType
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|shardRequest
operator|.
name|source
argument_list|(
name|in
operator|.
name|readBytesReference
argument_list|()
argument_list|)
expr_stmt|;
name|shardRequest
operator|.
name|docSource
argument_list|(
name|in
operator|.
name|readBytesReference
argument_list|()
argument_list|)
expr_stmt|;
name|shardRequest
operator|.
name|onlyCount
argument_list|(
name|in
operator|.
name|readBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|Item
name|item
init|=
operator|new
name|Item
argument_list|(
name|slot
argument_list|,
name|shardRequest
argument_list|)
decl_stmt|;
name|items
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|preference
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|items
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Item
name|item
range|:
name|items
control|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|item
operator|.
name|slot
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|item
operator|.
name|request
operator|.
name|documentType
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytesReference
argument_list|(
name|item
operator|.
name|request
operator|.
name|source
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytesReference
argument_list|(
name|item
operator|.
name|request
operator|.
name|docSource
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|item
operator|.
name|request
operator|.
name|onlyCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Item
specifier|public
specifier|static
class|class
name|Item
block|{
DECL|field|slot
specifier|private
specifier|final
name|int
name|slot
decl_stmt|;
DECL|field|request
specifier|private
specifier|final
name|PercolateShardRequest
name|request
decl_stmt|;
DECL|method|Item
specifier|public
name|Item
parameter_list|(
name|int
name|slot
parameter_list|,
name|PercolateShardRequest
name|request
parameter_list|)
block|{
name|this
operator|.
name|slot
operator|=
name|slot
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
block|}
DECL|method|slot
specifier|public
name|int
name|slot
parameter_list|()
block|{
return|return
name|slot
return|;
block|}
DECL|method|request
specifier|public
name|PercolateShardRequest
name|request
parameter_list|()
block|{
return|return
name|request
return|;
block|}
block|}
block|}
DECL|class|Response
specifier|public
specifier|static
class|class
name|Response
extends|extends
name|ActionResponse
block|{
DECL|field|items
specifier|private
name|List
argument_list|<
name|Item
argument_list|>
name|items
decl_stmt|;
DECL|method|items
specifier|public
name|List
argument_list|<
name|Item
argument_list|>
name|items
parameter_list|()
block|{
return|return
name|items
return|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|items
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Item
name|item
range|:
name|items
control|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|item
operator|.
name|slot
argument_list|)
expr_stmt|;
if|if
condition|(
name|item
operator|.
name|response
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|item
operator|.
name|response
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeText
argument_list|(
name|item
operator|.
name|error
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|items
operator|=
operator|new
name|ArrayList
argument_list|<
name|Item
argument_list|>
argument_list|(
name|size
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|int
name|slot
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|PercolateShardResponse
name|shardResponse
init|=
operator|new
name|PercolateShardResponse
argument_list|()
decl_stmt|;
name|shardResponse
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|items
operator|.
name|add
argument_list|(
operator|new
name|Item
argument_list|(
name|slot
argument_list|,
name|shardResponse
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|items
operator|.
name|add
argument_list|(
operator|new
name|Item
argument_list|(
name|slot
argument_list|,
name|in
operator|.
name|readText
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|Item
specifier|public
specifier|static
class|class
name|Item
block|{
DECL|field|slot
specifier|private
specifier|final
name|int
name|slot
decl_stmt|;
DECL|field|response
specifier|private
specifier|final
name|PercolateShardResponse
name|response
decl_stmt|;
DECL|field|error
specifier|private
specifier|final
name|Text
name|error
decl_stmt|;
DECL|method|Item
specifier|public
name|Item
parameter_list|(
name|Integer
name|slot
parameter_list|,
name|PercolateShardResponse
name|response
parameter_list|)
block|{
name|this
operator|.
name|slot
operator|=
name|slot
expr_stmt|;
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
name|this
operator|.
name|error
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|Item
specifier|public
name|Item
parameter_list|(
name|Integer
name|slot
parameter_list|,
name|Text
name|error
parameter_list|)
block|{
name|this
operator|.
name|slot
operator|=
name|slot
expr_stmt|;
name|this
operator|.
name|error
operator|=
name|error
expr_stmt|;
name|this
operator|.
name|response
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|slot
specifier|public
name|int
name|slot
parameter_list|()
block|{
return|return
name|slot
return|;
block|}
DECL|method|response
specifier|public
name|PercolateShardResponse
name|response
parameter_list|()
block|{
return|return
name|response
return|;
block|}
DECL|method|error
specifier|public
name|Text
name|error
parameter_list|()
block|{
return|return
name|error
return|;
block|}
DECL|method|failed
specifier|public
name|boolean
name|failed
parameter_list|()
block|{
return|return
name|error
operator|!=
literal|null
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

