begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|GroupShardsIterator
specifier|public
class|class
name|GroupShardsIterator
implements|implements
name|Iterable
argument_list|<
name|ShardIterator
argument_list|>
block|{
DECL|field|iterators
specifier|private
specifier|final
name|Collection
argument_list|<
name|ShardIterator
argument_list|>
name|iterators
decl_stmt|;
DECL|method|GroupShardsIterator
specifier|public
name|GroupShardsIterator
parameter_list|(
name|Collection
argument_list|<
name|ShardIterator
argument_list|>
name|iterators
parameter_list|)
block|{
name|this
operator|.
name|iterators
operator|=
name|iterators
expr_stmt|;
block|}
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
DECL|method|totalSizeActiveWith1ForEmpty
specifier|public
name|int
name|totalSizeActiveWith1ForEmpty
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
name|int
name|sizeActive
init|=
name|shard
operator|.
name|sizeActive
argument_list|()
decl_stmt|;
if|if
condition|(
name|sizeActive
operator|==
literal|0
condition|)
block|{
name|size
operator|+=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|size
operator|+=
name|sizeActive
expr_stmt|;
block|}
block|}
return|return
name|size
return|;
block|}
DECL|method|totalSizeActive
specifier|public
name|int
name|totalSizeActive
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
name|sizeActive
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
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
DECL|method|iterators
specifier|public
name|Collection
argument_list|<
name|ShardIterator
argument_list|>
name|iterators
parameter_list|()
block|{
return|return
name|iterators
return|;
block|}
DECL|method|iterator
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ShardIterator
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

