begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.stats
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|stats
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
name|ObjectObjectOpenHashMap
import|;
end_import

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
name|ObjectObjectCursor
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
name|CommonStats
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|XContentBuilderString
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
name|cache
operator|.
name|filter
operator|.
name|FilterCacheStats
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
name|cache
operator|.
name|id
operator|.
name|IdCacheStats
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
name|engine
operator|.
name|SegmentsStats
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
name|fielddata
operator|.
name|FieldDataStats
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
name|percolator
operator|.
name|stats
operator|.
name|PercolateStats
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
name|DocsStats
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
name|store
operator|.
name|StoreStats
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
name|suggest
operator|.
name|completion
operator|.
name|CompletionStats
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

begin_class
DECL|class|ClusterStatsIndices
specifier|public
class|class
name|ClusterStatsIndices
implements|implements
name|ToXContent
implements|,
name|Streamable
block|{
DECL|field|indexCount
specifier|private
name|int
name|indexCount
decl_stmt|;
DECL|field|shards
specifier|private
name|ShardStats
name|shards
decl_stmt|;
DECL|field|docs
specifier|private
name|DocsStats
name|docs
decl_stmt|;
DECL|field|store
specifier|private
name|StoreStats
name|store
decl_stmt|;
DECL|field|fieldData
specifier|private
name|FieldDataStats
name|fieldData
decl_stmt|;
DECL|field|filterCache
specifier|private
name|FilterCacheStats
name|filterCache
decl_stmt|;
DECL|field|idCache
specifier|private
name|IdCacheStats
name|idCache
decl_stmt|;
DECL|field|completion
specifier|private
name|CompletionStats
name|completion
decl_stmt|;
DECL|field|segments
specifier|private
name|SegmentsStats
name|segments
decl_stmt|;
DECL|field|percolate
specifier|private
name|PercolateStats
name|percolate
decl_stmt|;
DECL|method|ClusterStatsIndices
specifier|private
name|ClusterStatsIndices
parameter_list|()
block|{     }
DECL|method|ClusterStatsIndices
specifier|public
name|ClusterStatsIndices
parameter_list|(
name|ClusterStatsNodeResponse
index|[]
name|nodeResponses
parameter_list|)
block|{
name|ObjectObjectOpenHashMap
argument_list|<
name|String
argument_list|,
name|ShardStats
argument_list|>
name|countsPerIndex
init|=
operator|new
name|ObjectObjectOpenHashMap
argument_list|<
name|String
argument_list|,
name|ShardStats
argument_list|>
argument_list|()
decl_stmt|;
name|this
operator|.
name|docs
operator|=
operator|new
name|DocsStats
argument_list|()
expr_stmt|;
name|this
operator|.
name|store
operator|=
operator|new
name|StoreStats
argument_list|()
expr_stmt|;
name|this
operator|.
name|fieldData
operator|=
operator|new
name|FieldDataStats
argument_list|()
expr_stmt|;
name|this
operator|.
name|filterCache
operator|=
operator|new
name|FilterCacheStats
argument_list|()
expr_stmt|;
name|this
operator|.
name|idCache
operator|=
operator|new
name|IdCacheStats
argument_list|()
expr_stmt|;
name|this
operator|.
name|completion
operator|=
operator|new
name|CompletionStats
argument_list|()
expr_stmt|;
name|this
operator|.
name|segments
operator|=
operator|new
name|SegmentsStats
argument_list|()
expr_stmt|;
name|this
operator|.
name|percolate
operator|=
operator|new
name|PercolateStats
argument_list|()
expr_stmt|;
for|for
control|(
name|ClusterStatsNodeResponse
name|r
range|:
name|nodeResponses
control|)
block|{
for|for
control|(
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
name|ShardStats
name|shardStats
range|:
name|r
operator|.
name|shardsStats
argument_list|()
control|)
block|{
name|ShardStats
name|indexShardStats
init|=
name|countsPerIndex
operator|.
name|get
argument_list|(
name|shardStats
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexShardStats
operator|==
literal|null
condition|)
block|{
name|indexShardStats
operator|=
operator|new
name|ShardStats
argument_list|()
expr_stmt|;
name|countsPerIndex
operator|.
name|put
argument_list|(
name|shardStats
operator|.
name|getIndex
argument_list|()
argument_list|,
name|indexShardStats
argument_list|)
expr_stmt|;
block|}
name|indexShardStats
operator|.
name|total
operator|++
expr_stmt|;
name|CommonStats
name|shardCommonStats
init|=
name|shardStats
operator|.
name|getStats
argument_list|()
decl_stmt|;
if|if
condition|(
name|shardStats
operator|.
name|getShardRouting
argument_list|()
operator|.
name|primary
argument_list|()
condition|)
block|{
name|indexShardStats
operator|.
name|primaries
operator|++
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|shardCommonStats
operator|.
name|docs
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|add
argument_list|(
name|shardCommonStats
operator|.
name|store
argument_list|)
expr_stmt|;
name|fieldData
operator|.
name|add
argument_list|(
name|shardCommonStats
operator|.
name|fieldData
argument_list|)
expr_stmt|;
name|filterCache
operator|.
name|add
argument_list|(
name|shardCommonStats
operator|.
name|filterCache
argument_list|)
expr_stmt|;
name|idCache
operator|.
name|add
argument_list|(
name|shardCommonStats
operator|.
name|idCache
argument_list|)
expr_stmt|;
name|completion
operator|.
name|add
argument_list|(
name|shardCommonStats
operator|.
name|completion
argument_list|)
expr_stmt|;
name|segments
operator|.
name|add
argument_list|(
name|shardCommonStats
operator|.
name|segments
argument_list|)
expr_stmt|;
name|percolate
operator|.
name|add
argument_list|(
name|shardCommonStats
operator|.
name|percolate
argument_list|)
expr_stmt|;
block|}
block|}
name|shards
operator|=
operator|new
name|ShardStats
argument_list|()
expr_stmt|;
name|indexCount
operator|=
name|countsPerIndex
operator|.
name|size
argument_list|()
expr_stmt|;
for|for
control|(
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|ShardStats
argument_list|>
name|indexCountsCursor
range|:
name|countsPerIndex
control|)
block|{
name|shards
operator|.
name|addIndexShardCount
argument_list|(
name|indexCountsCursor
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getIndexCount
specifier|public
name|int
name|getIndexCount
parameter_list|()
block|{
return|return
name|indexCount
return|;
block|}
DECL|method|getShards
specifier|public
name|ShardStats
name|getShards
parameter_list|()
block|{
return|return
name|this
operator|.
name|shards
return|;
block|}
DECL|method|getDocs
specifier|public
name|DocsStats
name|getDocs
parameter_list|()
block|{
return|return
name|docs
return|;
block|}
DECL|method|getStore
specifier|public
name|StoreStats
name|getStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
DECL|method|getFieldData
specifier|public
name|FieldDataStats
name|getFieldData
parameter_list|()
block|{
return|return
name|fieldData
return|;
block|}
DECL|method|getFilterCache
specifier|public
name|FilterCacheStats
name|getFilterCache
parameter_list|()
block|{
return|return
name|filterCache
return|;
block|}
DECL|method|getIdCache
specifier|public
name|IdCacheStats
name|getIdCache
parameter_list|()
block|{
return|return
name|idCache
return|;
block|}
DECL|method|getCompletion
specifier|public
name|CompletionStats
name|getCompletion
parameter_list|()
block|{
return|return
name|completion
return|;
block|}
DECL|method|getSegments
specifier|public
name|SegmentsStats
name|getSegments
parameter_list|()
block|{
return|return
name|segments
return|;
block|}
DECL|method|getPercolate
specifier|public
name|PercolateStats
name|getPercolate
parameter_list|()
block|{
return|return
name|percolate
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
name|indexCount
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|shards
operator|=
name|ShardStats
operator|.
name|readShardStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|docs
operator|=
name|DocsStats
operator|.
name|readDocStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|store
operator|=
name|StoreStats
operator|.
name|readStoreStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|fieldData
operator|=
name|FieldDataStats
operator|.
name|readFieldDataStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|filterCache
operator|=
name|FilterCacheStats
operator|.
name|readFilterCacheStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|idCache
operator|=
name|IdCacheStats
operator|.
name|readIdCacheStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|completion
operator|=
name|CompletionStats
operator|.
name|readCompletionStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|segments
operator|=
name|SegmentsStats
operator|.
name|readSegmentsStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|percolate
operator|=
name|PercolateStats
operator|.
name|readPercolateStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
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
name|out
operator|.
name|writeVInt
argument_list|(
name|indexCount
argument_list|)
expr_stmt|;
name|shards
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|docs
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|store
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|fieldData
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|filterCache
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|idCache
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|completion
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|segments
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|percolate
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|readIndicesStats
specifier|public
specifier|static
name|ClusterStatsIndices
name|readIndicesStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ClusterStatsIndices
name|indicesStats
init|=
operator|new
name|ClusterStatsIndices
argument_list|()
decl_stmt|;
name|indicesStats
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|indicesStats
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|COUNT
specifier|static
specifier|final
name|XContentBuilderString
name|COUNT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"count"
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|COUNT
argument_list|,
name|indexCount
argument_list|)
expr_stmt|;
name|shards
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|docs
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|store
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|fieldData
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|filterCache
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|idCache
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|completion
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|segments
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|percolate
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|class|ShardStats
specifier|public
specifier|static
class|class
name|ShardStats
implements|implements
name|ToXContent
implements|,
name|Streamable
block|{
DECL|field|indices
name|int
name|indices
decl_stmt|;
DECL|field|total
name|int
name|total
decl_stmt|;
DECL|field|primaries
name|int
name|primaries
decl_stmt|;
comment|// min/max
DECL|field|minIndexShards
name|int
name|minIndexShards
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|maxIndexShards
name|int
name|maxIndexShards
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|minIndexPrimaryShards
name|int
name|minIndexPrimaryShards
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|maxIndexPrimaryShards
name|int
name|maxIndexPrimaryShards
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|minIndexReplication
name|double
name|minIndexReplication
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|totalIndexReplication
name|double
name|totalIndexReplication
init|=
literal|0
decl_stmt|;
DECL|field|maxIndexReplication
name|double
name|maxIndexReplication
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|ShardStats
specifier|public
name|ShardStats
parameter_list|()
block|{         }
comment|/**          * number of indices in the cluster          */
DECL|method|getIndices
specifier|public
name|int
name|getIndices
parameter_list|()
block|{
return|return
name|this
operator|.
name|indices
return|;
block|}
comment|/**          * total number of shards in the cluster          */
DECL|method|getTotal
specifier|public
name|int
name|getTotal
parameter_list|()
block|{
return|return
name|this
operator|.
name|total
return|;
block|}
comment|/**          * total number of primary shards in the cluster          */
DECL|method|getPrimaries
specifier|public
name|int
name|getPrimaries
parameter_list|()
block|{
return|return
name|this
operator|.
name|primaries
return|;
block|}
comment|/**          * returns how many *redundant* copies of the data the cluster holds - running with no replicas will return 0          */
DECL|method|getReplication
specifier|public
name|double
name|getReplication
parameter_list|()
block|{
if|if
condition|(
name|primaries
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
operator|(
operator|(
call|(
name|double
call|)
argument_list|(
name|total
operator|-
name|primaries
argument_list|)
operator|)
operator|/
name|primaries
operator|)
return|;
block|}
comment|/**          * the maximum number of shards (primary+replicas) an index has          */
DECL|method|getMaxIndexShards
specifier|public
name|int
name|getMaxIndexShards
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxIndexShards
return|;
block|}
comment|/**          * the minimum number of shards (primary+replicas) an index has          */
DECL|method|getMinIndexShards
specifier|public
name|int
name|getMinIndexShards
parameter_list|()
block|{
return|return
name|this
operator|.
name|minIndexShards
return|;
block|}
comment|/**          * average number of shards (primary+replicas) across the indices          */
DECL|method|getAvgIndexShards
specifier|public
name|double
name|getAvgIndexShards
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|indices
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
operator|(
operator|(
name|double
operator|)
name|this
operator|.
name|total
operator|)
operator|/
name|this
operator|.
name|indices
return|;
block|}
comment|/**          * the maximum number of primary shards an index has          */
DECL|method|getMaxIndexPrimaryShards
specifier|public
name|int
name|getMaxIndexPrimaryShards
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxIndexPrimaryShards
return|;
block|}
comment|/**          * the minimum number of primary shards an index has          */
DECL|method|getMinIndexPrimaryShards
specifier|public
name|int
name|getMinIndexPrimaryShards
parameter_list|()
block|{
return|return
name|this
operator|.
name|minIndexPrimaryShards
return|;
block|}
comment|/**          * the average number primary shards across the indices          */
DECL|method|getAvgIndexPrimaryShards
specifier|public
name|double
name|getAvgIndexPrimaryShards
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|indices
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
operator|(
operator|(
name|double
operator|)
name|this
operator|.
name|primaries
operator|)
operator|/
name|this
operator|.
name|indices
return|;
block|}
comment|/**          * minimum replication factor across the indices. See {@link #getReplication}          */
DECL|method|getMinIndexReplication
specifier|public
name|double
name|getMinIndexReplication
parameter_list|()
block|{
return|return
name|this
operator|.
name|minIndexReplication
return|;
block|}
comment|/**          * average replication factor across the indices. See {@link #getReplication}          */
DECL|method|getAvgIndexReplication
specifier|public
name|double
name|getAvgIndexReplication
parameter_list|()
block|{
if|if
condition|(
name|indices
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|this
operator|.
name|totalIndexReplication
operator|/
name|this
operator|.
name|indices
return|;
block|}
comment|/**          * maximum replication factor across the indices. See {@link #getReplication          */
DECL|method|getMaxIndexReplication
specifier|public
name|double
name|getMaxIndexReplication
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxIndexReplication
return|;
block|}
DECL|method|addIndexShardCount
specifier|public
name|void
name|addIndexShardCount
parameter_list|(
name|ShardStats
name|indexShardCount
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|++
expr_stmt|;
name|this
operator|.
name|primaries
operator|+=
name|indexShardCount
operator|.
name|primaries
expr_stmt|;
name|this
operator|.
name|total
operator|+=
name|indexShardCount
operator|.
name|total
expr_stmt|;
name|this
operator|.
name|totalIndexReplication
operator|+=
name|indexShardCount
operator|.
name|getReplication
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|indices
operator|==
literal|1
condition|)
block|{
comment|// first index, uninitialized.
name|minIndexPrimaryShards
operator|=
name|indexShardCount
operator|.
name|primaries
expr_stmt|;
name|maxIndexPrimaryShards
operator|=
name|indexShardCount
operator|.
name|primaries
expr_stmt|;
name|minIndexShards
operator|=
name|indexShardCount
operator|.
name|total
expr_stmt|;
name|maxIndexShards
operator|=
name|indexShardCount
operator|.
name|total
expr_stmt|;
name|minIndexReplication
operator|=
name|indexShardCount
operator|.
name|getReplication
argument_list|()
expr_stmt|;
name|maxIndexReplication
operator|=
name|minIndexReplication
expr_stmt|;
block|}
else|else
block|{
name|minIndexShards
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minIndexShards
argument_list|,
name|indexShardCount
operator|.
name|total
argument_list|)
expr_stmt|;
name|minIndexPrimaryShards
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minIndexPrimaryShards
argument_list|,
name|indexShardCount
operator|.
name|primaries
argument_list|)
expr_stmt|;
name|minIndexReplication
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minIndexReplication
argument_list|,
name|indexShardCount
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|maxIndexShards
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxIndexShards
argument_list|,
name|indexShardCount
operator|.
name|total
argument_list|)
expr_stmt|;
name|maxIndexPrimaryShards
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxIndexPrimaryShards
argument_list|,
name|indexShardCount
operator|.
name|primaries
argument_list|)
expr_stmt|;
name|maxIndexReplication
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxIndexReplication
argument_list|,
name|indexShardCount
operator|.
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readShardStats
specifier|public
specifier|static
name|ShardStats
name|readShardStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ShardStats
name|c
init|=
operator|new
name|ShardStats
argument_list|()
decl_stmt|;
name|c
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|c
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
name|indices
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|total
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|primaries
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|minIndexShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|maxIndexShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|minIndexPrimaryShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|maxIndexPrimaryShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|minIndexReplication
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|totalIndexReplication
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|maxIndexReplication
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
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
name|out
operator|.
name|writeVInt
argument_list|(
name|indices
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|total
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|primaries
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|minIndexShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|maxIndexShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|minIndexPrimaryShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|maxIndexPrimaryShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|minIndexReplication
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|totalIndexReplication
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|maxIndexReplication
argument_list|)
expr_stmt|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|SHARDS
specifier|static
specifier|final
name|XContentBuilderString
name|SHARDS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"shards"
argument_list|)
decl_stmt|;
DECL|field|TOTAL
specifier|static
specifier|final
name|XContentBuilderString
name|TOTAL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"total"
argument_list|)
decl_stmt|;
DECL|field|PRIMARIES
specifier|static
specifier|final
name|XContentBuilderString
name|PRIMARIES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"primaries"
argument_list|)
decl_stmt|;
DECL|field|REPLICATION
specifier|static
specifier|final
name|XContentBuilderString
name|REPLICATION
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"replication"
argument_list|)
decl_stmt|;
DECL|field|MIN
specifier|static
specifier|final
name|XContentBuilderString
name|MIN
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"min"
argument_list|)
decl_stmt|;
DECL|field|MAX
specifier|static
specifier|final
name|XContentBuilderString
name|MAX
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"max"
argument_list|)
decl_stmt|;
DECL|field|AVG
specifier|static
specifier|final
name|XContentBuilderString
name|AVG
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"avg"
argument_list|)
decl_stmt|;
DECL|field|INDEX
specifier|static
specifier|final
name|XContentBuilderString
name|INDEX
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
block|}
DECL|method|addIntMinMax
specifier|private
name|void
name|addIntMinMax
parameter_list|(
name|XContentBuilderString
name|field
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|,
name|double
name|avg
parameter_list|,
name|XContentBuilder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MIN
argument_list|,
name|min
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MAX
argument_list|,
name|max
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|AVG
argument_list|,
name|avg
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
DECL|method|addDoubleMinMax
specifier|private
name|void
name|addDoubleMinMax
parameter_list|(
name|XContentBuilderString
name|field
parameter_list|,
name|double
name|min
parameter_list|,
name|double
name|max
parameter_list|,
name|double
name|avg
parameter_list|,
name|XContentBuilder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MIN
argument_list|,
name|min
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MAX
argument_list|,
name|max
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|AVG
argument_list|,
name|avg
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|SHARDS
argument_list|)
expr_stmt|;
if|if
condition|(
name|indices
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL
argument_list|,
name|total
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|PRIMARIES
argument_list|,
name|primaries
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|REPLICATION
argument_list|,
name|getReplication
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|INDEX
argument_list|)
expr_stmt|;
name|addIntMinMax
argument_list|(
name|Fields
operator|.
name|SHARDS
argument_list|,
name|minIndexShards
argument_list|,
name|maxIndexShards
argument_list|,
name|getAvgIndexShards
argument_list|()
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|addIntMinMax
argument_list|(
name|Fields
operator|.
name|PRIMARIES
argument_list|,
name|minIndexPrimaryShards
argument_list|,
name|maxIndexPrimaryShards
argument_list|,
name|getAvgIndexPrimaryShards
argument_list|()
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|addDoubleMinMax
argument_list|(
name|Fields
operator|.
name|REPLICATION
argument_list|,
name|minIndexReplication
argument_list|,
name|maxIndexReplication
argument_list|,
name|getAvgIndexReplication
argument_list|()
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"total ["
operator|+
name|total
operator|+
literal|"] primaries ["
operator|+
name|primaries
operator|+
literal|"]"
return|;
block|}
block|}
block|}
end_class

end_unit

