begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|CollectionUtil
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
name|List
import|;
end_import

begin_comment
comment|/**  * This class implements a compilation of {@link ShardIterator}s. Each {@link ShardIterator}  * iterated by this {@link Iterable} represents a group of shards.  * ShardsIterators are always returned in ascending order independently of their order at construction  * time. The incoming iterators are sorted to ensure consistent iteration behavior across Nodes / JVMs. */
end_comment

begin_class
DECL|class|GroupShardsIterator
specifier|public
specifier|final
class|class
name|GroupShardsIterator
parameter_list|<
name|ShardIt
extends|extends
name|ShardIterator
parameter_list|>
implements|implements
name|Iterable
argument_list|<
name|ShardIt
argument_list|>
block|{
DECL|field|iterators
specifier|private
specifier|final
name|List
argument_list|<
name|ShardIt
argument_list|>
name|iterators
decl_stmt|;
comment|/**      * Constructs a enw GroupShardsIterator from the given list.      */
DECL|method|GroupShardsIterator
specifier|public
name|GroupShardsIterator
parameter_list|(
name|List
argument_list|<
name|ShardIt
argument_list|>
name|iterators
parameter_list|)
block|{
name|CollectionUtil
operator|.
name|timSort
argument_list|(
name|iterators
argument_list|)
expr_stmt|;
name|this
operator|.
name|iterators
operator|=
name|iterators
expr_stmt|;
block|}
comment|/**      * Returns the total number of shards within all groups      * @return total number of shards      */
DECL|method|totalSize
specifier|public
name|int
name|totalSize
parameter_list|()
block|{
name|int
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ShardIterator
name|shard
range|:
name|iterators
control|)
block|{
name|size
operator|+=
name|shard
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
comment|/**      * Returns the total number of shards plus the number of empty groups      * @return number of shards and empty groups      */
DECL|method|totalSizeWith1ForEmpty
specifier|public
name|int
name|totalSizeWith1ForEmpty
parameter_list|()
block|{
name|int
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ShardIt
name|shard
range|:
name|iterators
control|)
block|{
name|size
operator|+=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|shard
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
comment|/**      * Return the number of groups      * @return number of groups      */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|iterators
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|ShardIt
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|iterators
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
end_class

end_unit

