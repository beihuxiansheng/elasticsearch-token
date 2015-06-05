begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
package|;
end_package

begin_comment
comment|/**  * Helper functions for common Bucketing functions  */
end_comment

begin_class
DECL|class|BucketUtils
specifier|public
class|class
name|BucketUtils
block|{
comment|/**      * Heuristic used to determine the size of shard-side PriorityQueues when      * selecting the top N terms from a distributed index.      *       * @param finalSize      *            The number of terms required in the final reduce phase.      * @param numberOfShards      *            The number of shards being queried.      * @return A suggested default for the size of any shard-side PriorityQueues      */
DECL|method|suggestShardSideQueueSize
specifier|public
specifier|static
name|int
name|suggestShardSideQueueSize
parameter_list|(
name|int
name|finalSize
parameter_list|,
name|int
name|numberOfShards
parameter_list|)
block|{
assert|assert
name|numberOfShards
operator|>=
literal|1
assert|;
if|if
condition|(
name|numberOfShards
operator|==
literal|1
condition|)
block|{
return|return
name|finalSize
return|;
block|}
comment|//Cap the multiplier used for shards to avoid excessive data transfer
specifier|final
name|long
name|shardSampleSize
init|=
operator|(
name|long
operator|)
name|finalSize
operator|*
name|Math
operator|.
name|min
argument_list|(
literal|10
argument_list|,
name|numberOfShards
argument_list|)
decl_stmt|;
comment|// When finalSize is very small e.g. 1 and there is a low number of
comment|// shards then we need to ensure we still gather a reasonable sample of statistics from each
comment|// shard (at low cost) to improve the chances of the final result being accurate.
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|Math
operator|.
name|max
argument_list|(
literal|10
argument_list|,
name|shardSampleSize
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

