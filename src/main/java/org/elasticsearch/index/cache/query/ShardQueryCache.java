begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
operator|.
name|query
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
name|cache
operator|.
name|RemovalListener
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
name|cache
operator|.
name|RemovalNotification
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSet
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
name|lucene
operator|.
name|docset
operator|.
name|DocIdSets
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
name|metrics
operator|.
name|CounterMetric
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
name|filter
operator|.
name|weighted
operator|.
name|WeightedFilterCache
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
name|settings
operator|.
name|IndexSettings
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
name|AbstractIndexShardComponent
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
name|cache
operator|.
name|query
operator|.
name|IndicesQueryCache
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ShardQueryCache
specifier|public
class|class
name|ShardQueryCache
extends|extends
name|AbstractIndexShardComponent
implements|implements
name|RemovalListener
argument_list|<
name|IndicesQueryCache
operator|.
name|Key
argument_list|,
name|BytesReference
argument_list|>
block|{
DECL|field|evictionsMetric
specifier|final
name|CounterMetric
name|evictionsMetric
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|field|totalMetric
specifier|final
name|CounterMetric
name|totalMetric
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|ShardQueryCache
specifier|public
name|ShardQueryCache
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
DECL|method|stats
specifier|public
name|QueryCacheStats
name|stats
parameter_list|()
block|{
return|return
operator|new
name|QueryCacheStats
argument_list|(
name|totalMetric
operator|.
name|count
argument_list|()
argument_list|,
name|evictionsMetric
operator|.
name|count
argument_list|()
argument_list|)
return|;
block|}
DECL|method|onCached
specifier|public
name|void
name|onCached
parameter_list|(
name|IndicesQueryCache
operator|.
name|Key
name|key
parameter_list|,
name|BytesReference
name|value
parameter_list|)
block|{
name|totalMetric
operator|.
name|inc
argument_list|(
name|key
operator|.
name|ramBytesUsed
argument_list|()
operator|+
name|value
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onRemoval
specifier|public
name|void
name|onRemoval
parameter_list|(
name|RemovalNotification
argument_list|<
name|IndicesQueryCache
operator|.
name|Key
argument_list|,
name|BytesReference
argument_list|>
name|removalNotification
parameter_list|)
block|{
if|if
condition|(
name|removalNotification
operator|.
name|wasEvicted
argument_list|()
condition|)
block|{
name|evictionsMetric
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
name|long
name|dec
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|removalNotification
operator|.
name|getKey
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|dec
operator|+=
name|removalNotification
operator|.
name|getKey
argument_list|()
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|removalNotification
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|dec
operator|+=
name|removalNotification
operator|.
name|getValue
argument_list|()
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|totalMetric
operator|.
name|dec
argument_list|(
name|dec
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

