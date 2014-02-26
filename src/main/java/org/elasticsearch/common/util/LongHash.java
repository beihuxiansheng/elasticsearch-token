begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
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
name|hash
operator|.
name|MurmurHash3
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
name|lease
operator|.
name|Releasables
import|;
end_import

begin_comment
comment|/**  * Specialized hash table implementation similar to BytesRefHash that maps  *  long values to ids. Collisions are resolved with open addressing and linear  *  probing, growth is smooth thanks to {@link BigArrays} and capacity is always  *  a multiple of 2 for faster identification of buckets.  */
end_comment

begin_comment
comment|// IDs are internally stored as id + 1 so that 0 encodes for an empty slot
end_comment

begin_class
DECL|class|LongHash
specifier|public
specifier|final
class|class
name|LongHash
extends|extends
name|AbstractHash
block|{
DECL|field|keys
specifier|private
name|LongArray
name|keys
decl_stmt|;
comment|// Constructor with configurable capacity and default maximum load factor.
DECL|method|LongHash
specifier|public
name|LongHash
parameter_list|(
name|long
name|capacity
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|)
block|{
name|this
argument_list|(
name|capacity
argument_list|,
name|DEFAULT_MAX_LOAD_FACTOR
argument_list|,
name|bigArrays
argument_list|)
expr_stmt|;
block|}
comment|//Constructor with configurable capacity and load factor.
DECL|method|LongHash
specifier|public
name|LongHash
parameter_list|(
name|long
name|capacity
parameter_list|,
name|float
name|maxLoadFactor
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|)
block|{
name|super
argument_list|(
name|capacity
argument_list|,
name|maxLoadFactor
argument_list|,
name|bigArrays
argument_list|)
expr_stmt|;
name|keys
operator|=
name|bigArrays
operator|.
name|newLongArray
argument_list|(
name|capacity
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|hash
specifier|private
specifier|static
name|long
name|hash
parameter_list|(
name|long
name|value
parameter_list|)
block|{
comment|// Don't use the value directly. Under some cases eg dates, it could be that the low bits don't carry much value and we would like
comment|// all bits of the hash to carry as much value
return|return
name|MurmurHash3
operator|.
name|hash
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|/**      * Return the key at<code>0&lte; index&lte; capacity()</code>. The result is undefined if the slot is unused.      */
DECL|method|key
specifier|public
name|long
name|key
parameter_list|(
name|long
name|index
parameter_list|)
block|{
return|return
name|keys
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**      * Get the id associated with<code>key</code>      */
DECL|method|find
specifier|public
name|long
name|find
parameter_list|(
name|long
name|key
parameter_list|)
block|{
specifier|final
name|long
name|slot
init|=
name|slot
argument_list|(
name|hash
argument_list|(
name|key
argument_list|)
argument_list|,
name|mask
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|index
init|=
name|slot
init|;
condition|;
name|index
operator|=
name|nextSlot
argument_list|(
name|index
argument_list|,
name|mask
argument_list|)
control|)
block|{
specifier|final
name|long
name|id
init|=
name|id
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
operator|-
literal|1
operator|||
name|keys
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|==
name|key
condition|)
block|{
return|return
name|id
return|;
block|}
block|}
block|}
DECL|method|set
specifier|private
name|long
name|set
parameter_list|(
name|long
name|key
parameter_list|,
name|long
name|id
parameter_list|)
block|{
assert|assert
name|size
operator|<
name|maxSize
assert|;
specifier|final
name|long
name|slot
init|=
name|slot
argument_list|(
name|hash
argument_list|(
name|key
argument_list|)
argument_list|,
name|mask
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|index
init|=
name|slot
init|;
condition|;
name|index
operator|=
name|nextSlot
argument_list|(
name|index
argument_list|,
name|mask
argument_list|)
control|)
block|{
specifier|final
name|long
name|curId
init|=
name|id
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|curId
operator|==
operator|-
literal|1
condition|)
block|{
comment|// means unset
name|id
argument_list|(
name|index
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|keys
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|key
argument_list|)
expr_stmt|;
operator|++
name|size
expr_stmt|;
return|return
name|id
return|;
block|}
elseif|else
if|if
condition|(
name|keys
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|==
name|key
condition|)
block|{
return|return
operator|-
literal|1
operator|-
name|curId
return|;
block|}
block|}
block|}
DECL|method|reset
specifier|private
name|void
name|reset
parameter_list|(
name|long
name|key
parameter_list|,
name|long
name|id
parameter_list|)
block|{
specifier|final
name|long
name|slot
init|=
name|slot
argument_list|(
name|hash
argument_list|(
name|key
argument_list|)
argument_list|,
name|mask
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|index
init|=
name|slot
init|;
condition|;
name|index
operator|=
name|nextSlot
argument_list|(
name|index
argument_list|,
name|mask
argument_list|)
control|)
block|{
specifier|final
name|long
name|curId
init|=
name|id
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|curId
operator|==
operator|-
literal|1
condition|)
block|{
comment|// means unset
name|id
argument_list|(
name|index
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|keys
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|key
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
block|{
assert|assert
name|keys
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|!=
name|key
assert|;
block|}
block|}
block|}
comment|/**      * Try to add<code>key</code>. Return its newly allocated id if it wasn't in the hash table yet, or</code>-1-id</code>      * if it was already present in the hash table.      */
DECL|method|add
specifier|public
name|long
name|add
parameter_list|(
name|long
name|key
parameter_list|)
block|{
if|if
condition|(
name|size
operator|>=
name|maxSize
condition|)
block|{
assert|assert
name|size
operator|==
name|maxSize
assert|;
name|grow
argument_list|()
expr_stmt|;
block|}
assert|assert
name|size
operator|<
name|maxSize
assert|;
return|return
name|set
argument_list|(
name|key
argument_list|,
name|size
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|resizeKeys
specifier|protected
name|void
name|resizeKeys
parameter_list|(
name|long
name|capacity
parameter_list|)
block|{
name|keys
operator|=
name|bigArrays
operator|.
name|resize
argument_list|(
name|keys
argument_list|,
name|capacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeAndAdd
specifier|protected
name|void
name|removeAndAdd
parameter_list|(
name|long
name|index
parameter_list|,
name|long
name|id
parameter_list|)
block|{
specifier|final
name|long
name|key
init|=
name|keys
operator|.
name|set
argument_list|(
name|index
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|reset
argument_list|(
name|key
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|release
specifier|public
name|boolean
name|release
parameter_list|()
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|super
operator|.
name|release
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|Releasables
operator|.
name|release
argument_list|(
name|success
argument_list|,
name|keys
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

