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
specifier|final
class|class
name|BucketUtils
block|{
DECL|method|BucketUtils
specifier|private
name|BucketUtils
parameter_list|()
block|{}
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
if|if
condition|(
name|finalSize
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"size must be positive, got "
operator|+
name|finalSize
argument_list|)
throw|;
block|}
if|if
condition|(
name|numberOfShards
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"number of shards must be positive, got "
operator|+
name|numberOfShards
argument_list|)
throw|;
block|}
if|if
condition|(
name|numberOfShards
operator|==
literal|1
condition|)
block|{
comment|// In the case of a single shard, we do not need to over-request
return|return
name|finalSize
return|;
block|}
comment|// Request 50% more buckets on the shards in order to improve accuracy
comment|// as well as a small constant that should help with small values of 'size'
specifier|final
name|long
name|shardSampleSize
init|=
call|(
name|long
call|)
argument_list|(
name|finalSize
operator|*
literal|1.5
operator|+
literal|10
argument_list|)
decl_stmt|;
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
name|shardSampleSize
argument_list|)
return|;
block|}
block|}
end_class

end_unit

