begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.percolator.stats
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|percolator
operator|.
name|stats
package|;
end_package

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
name|metrics
operator|.
name|MeanMetric
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ShardPercolateService
specifier|public
class|class
name|ShardPercolateService
extends|extends
name|AbstractIndexShardComponent
block|{
annotation|@
name|Inject
DECL|method|ShardPercolateService
specifier|public
name|ShardPercolateService
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
DECL|field|percolateMetric
specifier|private
specifier|final
name|MeanMetric
name|percolateMetric
init|=
operator|new
name|MeanMetric
argument_list|()
decl_stmt|;
DECL|field|currentMetric
specifier|private
specifier|final
name|CounterMetric
name|currentMetric
init|=
operator|new
name|CounterMetric
argument_list|()
decl_stmt|;
DECL|method|prePercolate
specifier|public
name|void
name|prePercolate
parameter_list|()
block|{
name|currentMetric
operator|.
name|inc
argument_list|()
expr_stmt|;
block|}
DECL|method|postPercolate
specifier|public
name|void
name|postPercolate
parameter_list|(
name|long
name|tookInNanos
parameter_list|)
block|{
name|currentMetric
operator|.
name|dec
argument_list|()
expr_stmt|;
name|percolateMetric
operator|.
name|inc
argument_list|(
name|tookInNanos
argument_list|)
expr_stmt|;
block|}
DECL|method|stats
specifier|public
name|PercolateStats
name|stats
parameter_list|()
block|{
return|return
operator|new
name|PercolateStats
argument_list|(
name|percolateMetric
operator|.
name|count
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|percolateMetric
operator|.
name|sum
argument_list|()
argument_list|)
argument_list|,
name|currentMetric
operator|.
name|count
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

