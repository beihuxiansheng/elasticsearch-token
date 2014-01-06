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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cache
operator|.
name|recycler
operator|.
name|PageCacheRecycler
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
name|Releasable
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|BigArrays
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
name|util
operator|.
name|LongArray
import|;
end_import

begin_comment
comment|/**  * Base implementation for {@link BytesRefHash} and {@link LongHash}.  */
end_comment

begin_comment
comment|// IDs are internally stored as id + 1 so that 0 encodes for an empty slot
end_comment

begin_class
DECL|class|AbstractHash
specifier|abstract
class|class
name|AbstractHash
implements|implements
name|Releasable
block|{
comment|// Open addressing typically requires having smaller load factors compared to linked lists because
comment|// collisions may result into worse lookup performance.
DECL|field|DEFAULT_MAX_LOAD_FACTOR
specifier|static
specifier|final
name|float
name|DEFAULT_MAX_LOAD_FACTOR
init|=
literal|0.6f
decl_stmt|;
DECL|field|maxLoadFactor
specifier|final
name|float
name|maxLoadFactor
decl_stmt|;
DECL|field|size
DECL|field|maxSize
name|long
name|size
decl_stmt|,
name|maxSize
decl_stmt|;
DECL|field|ids
name|LongArray
name|ids
decl_stmt|;
DECL|field|mask
name|long
name|mask
decl_stmt|;
DECL|method|AbstractHash
name|AbstractHash
parameter_list|(
name|long
name|capacity
parameter_list|,
name|float
name|maxLoadFactor
parameter_list|,
name|PageCacheRecycler
name|recycler
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|capacity
operator|>=
literal|0
argument_list|,
literal|"capacity must be>= 0"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|maxLoadFactor
operator|>
literal|0
operator|&&
name|maxLoadFactor
operator|<
literal|1
argument_list|,
literal|"maxLoadFactor must be> 0 and< 1"
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxLoadFactor
operator|=
name|maxLoadFactor
expr_stmt|;
name|long
name|buckets
init|=
literal|1L
operator|+
call|(
name|long
call|)
argument_list|(
name|capacity
operator|/
name|maxLoadFactor
argument_list|)
decl_stmt|;
name|buckets
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|Long
operator|.
name|highestOneBit
argument_list|(
name|buckets
operator|-
literal|1
argument_list|)
operator|<<
literal|1
argument_list|)
expr_stmt|;
comment|// next power of two
assert|assert
name|buckets
operator|==
name|Long
operator|.
name|highestOneBit
argument_list|(
name|buckets
argument_list|)
assert|;
name|maxSize
operator|=
call|(
name|long
call|)
argument_list|(
name|buckets
operator|*
name|maxLoadFactor
argument_list|)
expr_stmt|;
assert|assert
name|maxSize
operator|>=
name|capacity
assert|;
name|size
operator|=
literal|0
expr_stmt|;
name|ids
operator|=
name|BigArrays
operator|.
name|newLongArray
argument_list|(
name|buckets
argument_list|,
name|recycler
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|mask
operator|=
name|buckets
operator|-
literal|1
expr_stmt|;
block|}
comment|/**      * Return the number of allocated slots to store this hash table.      */
DECL|method|capacity
specifier|public
name|long
name|capacity
parameter_list|()
block|{
return|return
name|ids
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * Return the number of longs in this hash table.      */
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|slot
specifier|static
name|long
name|slot
parameter_list|(
name|long
name|hash
parameter_list|,
name|long
name|mask
parameter_list|)
block|{
return|return
name|hash
operator|&
name|mask
return|;
block|}
DECL|method|nextSlot
specifier|static
name|long
name|nextSlot
parameter_list|(
name|long
name|curSlot
parameter_list|,
name|long
name|mask
parameter_list|)
block|{
return|return
operator|(
name|curSlot
operator|+
literal|1
operator|)
operator|&
name|mask
return|;
comment|// linear probing
block|}
comment|/**      * Get the id associated with key at<code>0&lte; index&lte; capacity()</code> or -1 if this slot is unused.      */
DECL|method|id
specifier|public
name|long
name|id
parameter_list|(
name|long
name|index
parameter_list|)
block|{
return|return
name|ids
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|-
literal|1
return|;
block|}
DECL|method|id
specifier|protected
specifier|final
name|long
name|id
parameter_list|(
name|long
name|index
parameter_list|,
name|long
name|id
parameter_list|)
block|{
return|return
name|ids
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|id
operator|+
literal|1
argument_list|)
operator|-
literal|1
return|;
block|}
comment|/** Resize keys to the given capacity. */
DECL|method|resizeKeys
specifier|protected
name|void
name|resizeKeys
parameter_list|(
name|long
name|capacity
parameter_list|)
block|{}
comment|/** Remove key at the given index and  */
DECL|method|removeAndAdd
specifier|protected
specifier|abstract
name|void
name|removeAndAdd
parameter_list|(
name|long
name|index
parameter_list|,
name|long
name|id
parameter_list|)
function_decl|;
DECL|method|grow
specifier|protected
specifier|final
name|void
name|grow
parameter_list|()
block|{
comment|// The difference of this implementation of grow() compared to standard hash tables is that we are growing in-place, which makes
comment|// the re-mapping of keys to slots a bit more tricky.
assert|assert
name|size
operator|==
name|maxSize
assert|;
specifier|final
name|long
name|prevSize
init|=
name|size
decl_stmt|;
specifier|final
name|long
name|buckets
init|=
name|capacity
argument_list|()
decl_stmt|;
comment|// Resize arrays
specifier|final
name|long
name|newBuckets
init|=
name|buckets
operator|<<
literal|1
decl_stmt|;
assert|assert
name|newBuckets
operator|==
name|Long
operator|.
name|highestOneBit
argument_list|(
name|newBuckets
argument_list|)
operator|:
name|newBuckets
assert|;
comment|// power of 2
name|resizeKeys
argument_list|(
name|newBuckets
argument_list|)
expr_stmt|;
name|ids
operator|=
name|BigArrays
operator|.
name|resize
argument_list|(
name|ids
argument_list|,
name|newBuckets
argument_list|)
expr_stmt|;
name|mask
operator|=
name|newBuckets
operator|-
literal|1
expr_stmt|;
comment|// First let's remap in-place: most data will be put in its final position directly
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|buckets
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|id
init|=
name|id
argument_list|(
name|i
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
operator|-
literal|1
condition|)
block|{
name|removeAndAdd
argument_list|(
name|i
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
comment|// The only entries which have not been put in their final position in the previous loop are those that were stored in a slot that
comment|// is< slot(key, mask). This only happens when slot(key, mask) returned a slot that was close to the end of the array and colision
comment|// resolution has put it back in the first slots. This time, collision resolution will have put them at the beginning of the newly
comment|// allocated slots. Let's re-add them to make sure they are in the right slot. This 2nd loop will typically exit very early.
for|for
control|(
name|long
name|i
init|=
name|buckets
init|;
name|i
operator|<
name|newBuckets
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|id
init|=
name|id
argument_list|(
name|i
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
operator|-
literal|1
condition|)
block|{
name|removeAndAdd
argument_list|(
name|i
argument_list|,
name|id
argument_list|)
expr_stmt|;
comment|// add it back
block|}
else|else
block|{
break|break;
block|}
block|}
assert|assert
name|size
operator|==
name|prevSize
assert|;
name|maxSize
operator|=
call|(
name|long
call|)
argument_list|(
name|newBuckets
operator|*
name|maxLoadFactor
argument_list|)
expr_stmt|;
assert|assert
name|size
operator|<
name|maxSize
assert|;
block|}
annotation|@
name|Override
DECL|method|release
specifier|public
name|boolean
name|release
parameter_list|()
block|{
name|Releasables
operator|.
name|release
argument_list|(
name|ids
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

