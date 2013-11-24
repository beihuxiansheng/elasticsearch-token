begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ArrayUtil
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
name|util
operator|.
name|RamUsageEstimator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/** Utility class to work with arrays. */
end_comment

begin_enum
DECL|enum|BigArrays
specifier|public
enum|enum
name|BigArrays
block|{     ;
comment|/** Page size in bytes: 16KB */
DECL|field|PAGE_SIZE_IN_BYTES
specifier|public
specifier|static
specifier|final
name|int
name|PAGE_SIZE_IN_BYTES
init|=
literal|1
operator|<<
literal|14
decl_stmt|;
comment|/** Returns the next size to grow when working with parallel arrays that may have different page sizes or number of bytes per element. */
DECL|method|overSize
specifier|public
specifier|static
name|long
name|overSize
parameter_list|(
name|long
name|minTargetSize
parameter_list|)
block|{
return|return
name|overSize
argument_list|(
name|minTargetSize
argument_list|,
name|PAGE_SIZE_IN_BYTES
operator|/
literal|8
argument_list|,
literal|1
argument_list|)
return|;
block|}
comment|/** Return the next size to grow to that is&gt;=<code>minTargetSize</code>.      *  Inspired from {@link ArrayUtil#oversize(int, int)} and adapted to play nicely with paging. */
DECL|method|overSize
specifier|public
specifier|static
name|long
name|overSize
parameter_list|(
name|long
name|minTargetSize
parameter_list|,
name|int
name|pageSize
parameter_list|,
name|int
name|bytesPerElement
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|minTargetSize
operator|>=
literal|0
argument_list|,
literal|"minTargetSize must be>= 0"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|pageSize
operator|>=
literal|0
argument_list|,
literal|"pageSize must be> 0"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|bytesPerElement
operator|>
literal|0
argument_list|,
literal|"bytesPerElement must be> 0"
argument_list|)
expr_stmt|;
name|long
name|newSize
decl_stmt|;
if|if
condition|(
name|minTargetSize
operator|<
name|pageSize
condition|)
block|{
name|newSize
operator|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
operator|(
name|int
operator|)
name|minTargetSize
argument_list|,
name|bytesPerElement
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newSize
operator|=
name|minTargetSize
operator|+
operator|(
name|minTargetSize
operator|>>>
literal|3
operator|)
expr_stmt|;
block|}
if|if
condition|(
name|newSize
operator|>
name|pageSize
condition|)
block|{
comment|// round to a multiple of pageSize
name|newSize
operator|=
name|newSize
operator|-
operator|(
name|newSize
operator|%
name|pageSize
operator|)
operator|+
name|pageSize
expr_stmt|;
assert|assert
name|newSize
operator|%
name|pageSize
operator|==
literal|0
assert|;
block|}
return|return
name|newSize
return|;
block|}
DECL|method|indexIsInt
specifier|static
name|boolean
name|indexIsInt
parameter_list|(
name|long
name|index
parameter_list|)
block|{
return|return
name|index
operator|==
operator|(
name|int
operator|)
name|index
return|;
block|}
DECL|class|IntArrayWrapper
specifier|private
specifier|static
class|class
name|IntArrayWrapper
implements|implements
name|IntArray
block|{
DECL|field|array
specifier|private
specifier|final
name|int
index|[]
name|array
decl_stmt|;
DECL|method|IntArrayWrapper
name|IntArrayWrapper
parameter_list|(
name|int
index|[]
name|array
parameter_list|)
block|{
name|this
operator|.
name|array
operator|=
name|array
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|array
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|int
name|get
parameter_list|(
name|long
name|index
parameter_list|)
block|{
assert|assert
name|indexIsInt
argument_list|(
name|index
argument_list|)
assert|;
return|return
name|array
index|[
operator|(
name|int
operator|)
name|index
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|int
name|set
parameter_list|(
name|long
name|index
parameter_list|,
name|int
name|value
parameter_list|)
block|{
assert|assert
name|indexIsInt
argument_list|(
name|index
argument_list|)
assert|;
specifier|final
name|int
name|ret
init|=
name|array
index|[
operator|(
name|int
operator|)
name|index
index|]
decl_stmt|;
name|array
index|[
operator|(
name|int
operator|)
name|index
index|]
operator|=
name|value
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|increment
specifier|public
name|int
name|increment
parameter_list|(
name|long
name|index
parameter_list|,
name|int
name|inc
parameter_list|)
block|{
assert|assert
name|indexIsInt
argument_list|(
name|index
argument_list|)
assert|;
return|return
name|array
index|[
operator|(
name|int
operator|)
name|index
index|]
operator|+=
name|inc
return|;
block|}
block|}
DECL|class|LongArrayWrapper
specifier|private
specifier|static
class|class
name|LongArrayWrapper
implements|implements
name|LongArray
block|{
DECL|field|array
specifier|private
specifier|final
name|long
index|[]
name|array
decl_stmt|;
DECL|method|LongArrayWrapper
name|LongArrayWrapper
parameter_list|(
name|long
index|[]
name|array
parameter_list|)
block|{
name|this
operator|.
name|array
operator|=
name|array
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|array
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|long
name|get
parameter_list|(
name|long
name|index
parameter_list|)
block|{
assert|assert
name|indexIsInt
argument_list|(
name|index
argument_list|)
assert|;
return|return
name|array
index|[
operator|(
name|int
operator|)
name|index
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|long
name|set
parameter_list|(
name|long
name|index
parameter_list|,
name|long
name|value
parameter_list|)
block|{
assert|assert
name|indexIsInt
argument_list|(
name|index
argument_list|)
assert|;
specifier|final
name|long
name|ret
init|=
name|array
index|[
operator|(
name|int
operator|)
name|index
index|]
decl_stmt|;
name|array
index|[
operator|(
name|int
operator|)
name|index
index|]
operator|=
name|value
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|increment
specifier|public
name|long
name|increment
parameter_list|(
name|long
name|index
parameter_list|,
name|long
name|inc
parameter_list|)
block|{
assert|assert
name|indexIsInt
argument_list|(
name|index
argument_list|)
assert|;
return|return
name|array
index|[
operator|(
name|int
operator|)
name|index
index|]
operator|+=
name|inc
return|;
block|}
block|}
DECL|class|DoubleArrayWrapper
specifier|private
specifier|static
class|class
name|DoubleArrayWrapper
implements|implements
name|DoubleArray
block|{
DECL|field|array
specifier|private
specifier|final
name|double
index|[]
name|array
decl_stmt|;
DECL|method|DoubleArrayWrapper
name|DoubleArrayWrapper
parameter_list|(
name|double
index|[]
name|array
parameter_list|)
block|{
name|this
operator|.
name|array
operator|=
name|array
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|array
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|double
name|get
parameter_list|(
name|long
name|index
parameter_list|)
block|{
assert|assert
name|indexIsInt
argument_list|(
name|index
argument_list|)
assert|;
return|return
name|array
index|[
operator|(
name|int
operator|)
name|index
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|double
name|set
parameter_list|(
name|long
name|index
parameter_list|,
name|double
name|value
parameter_list|)
block|{
assert|assert
name|indexIsInt
argument_list|(
name|index
argument_list|)
assert|;
name|double
name|ret
init|=
name|array
index|[
operator|(
name|int
operator|)
name|index
index|]
decl_stmt|;
name|array
index|[
operator|(
name|int
operator|)
name|index
index|]
operator|=
name|value
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|increment
specifier|public
name|double
name|increment
parameter_list|(
name|long
name|index
parameter_list|,
name|double
name|inc
parameter_list|)
block|{
assert|assert
name|indexIsInt
argument_list|(
name|index
argument_list|)
assert|;
return|return
name|array
index|[
operator|(
name|int
operator|)
name|index
index|]
operator|+=
name|inc
return|;
block|}
annotation|@
name|Override
DECL|method|fill
specifier|public
name|void
name|fill
parameter_list|(
name|long
name|fromIndex
parameter_list|,
name|long
name|toIndex
parameter_list|,
name|double
name|value
parameter_list|)
block|{
assert|assert
name|indexIsInt
argument_list|(
name|fromIndex
argument_list|)
assert|;
assert|assert
name|indexIsInt
argument_list|(
name|toIndex
argument_list|)
assert|;
name|Arrays
operator|.
name|fill
argument_list|(
name|array
argument_list|,
operator|(
name|int
operator|)
name|fromIndex
argument_list|,
operator|(
name|int
operator|)
name|toIndex
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ObjectArrayWrapper
specifier|private
specifier|static
class|class
name|ObjectArrayWrapper
parameter_list|<
name|T
parameter_list|>
implements|implements
name|ObjectArray
argument_list|<
name|T
argument_list|>
block|{
DECL|field|array
specifier|private
specifier|final
name|Object
index|[]
name|array
decl_stmt|;
DECL|method|ObjectArrayWrapper
name|ObjectArrayWrapper
parameter_list|(
name|Object
index|[]
name|array
parameter_list|)
block|{
name|this
operator|.
name|array
operator|=
name|array
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|array
operator|.
name|length
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|get
specifier|public
name|T
name|get
parameter_list|(
name|long
name|index
parameter_list|)
block|{
assert|assert
name|indexIsInt
argument_list|(
name|index
argument_list|)
assert|;
return|return
operator|(
name|T
operator|)
name|array
index|[
operator|(
name|int
operator|)
name|index
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|T
name|set
parameter_list|(
name|long
name|index
parameter_list|,
name|T
name|value
parameter_list|)
block|{
assert|assert
name|indexIsInt
argument_list|(
name|index
argument_list|)
assert|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|T
name|ret
init|=
operator|(
name|T
operator|)
name|array
index|[
operator|(
name|int
operator|)
name|index
index|]
decl_stmt|;
name|array
index|[
operator|(
name|int
operator|)
name|index
index|]
operator|=
name|value
expr_stmt|;
return|return
name|ret
return|;
block|}
block|}
comment|/** Allocate a new {@link IntArray} of the given capacity. */
DECL|method|newIntArray
specifier|public
specifier|static
name|IntArray
name|newIntArray
parameter_list|(
name|long
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<=
name|BigIntArray
operator|.
name|PAGE_SIZE
condition|)
block|{
return|return
operator|new
name|IntArrayWrapper
argument_list|(
operator|new
name|int
index|[
operator|(
name|int
operator|)
name|size
index|]
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|BigIntArray
argument_list|(
name|size
argument_list|)
return|;
block|}
block|}
comment|/** Resize the array to the exact provided size. */
DECL|method|resize
specifier|public
specifier|static
name|IntArray
name|resize
parameter_list|(
name|IntArray
name|array
parameter_list|,
name|long
name|size
parameter_list|)
block|{
if|if
condition|(
name|array
operator|instanceof
name|BigIntArray
condition|)
block|{
operator|(
operator|(
name|BigIntArray
operator|)
name|array
operator|)
operator|.
name|resize
argument_list|(
name|size
argument_list|)
expr_stmt|;
return|return
name|array
return|;
block|}
else|else
block|{
specifier|final
name|IntArray
name|newArray
init|=
name|newIntArray
argument_list|(
name|size
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
operator|,
name|end
operator|=
name|Math
operator|.
name|min
argument_list|(
name|size
argument_list|,
name|array
operator|.
name|size
argument_list|()
argument_list|)
condition|;
name|i
operator|<
name|end
incr|;
control|++i)
block|{
name|newArray
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|array
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|newArray
return|;
block|}
block|}
comment|/** Grow an array to a size that is larger than<code>minSize</code>, preserving content, and potentially reusing part of the provided array. */
DECL|method|grow
specifier|public
specifier|static
name|IntArray
name|grow
parameter_list|(
name|IntArray
name|array
parameter_list|,
name|long
name|minSize
parameter_list|)
block|{
if|if
condition|(
name|minSize
operator|<=
name|array
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
name|array
return|;
block|}
specifier|final
name|long
name|newSize
init|=
name|overSize
argument_list|(
name|minSize
argument_list|,
name|BigIntArray
operator|.
name|PAGE_SIZE
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
decl_stmt|;
return|return
name|resize
argument_list|(
name|array
argument_list|,
name|newSize
argument_list|)
return|;
block|}
comment|/** Allocate a new {@link LongArray} of the given capacity. */
DECL|method|newLongArray
specifier|public
specifier|static
name|LongArray
name|newLongArray
parameter_list|(
name|long
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<=
name|BigLongArray
operator|.
name|PAGE_SIZE
condition|)
block|{
return|return
operator|new
name|LongArrayWrapper
argument_list|(
operator|new
name|long
index|[
operator|(
name|int
operator|)
name|size
index|]
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|BigLongArray
argument_list|(
name|size
argument_list|)
return|;
block|}
block|}
comment|/** Resize the array to the exact provided size. */
DECL|method|resize
specifier|public
specifier|static
name|LongArray
name|resize
parameter_list|(
name|LongArray
name|array
parameter_list|,
name|long
name|size
parameter_list|)
block|{
if|if
condition|(
name|array
operator|instanceof
name|BigLongArray
condition|)
block|{
operator|(
operator|(
name|BigLongArray
operator|)
name|array
operator|)
operator|.
name|resize
argument_list|(
name|size
argument_list|)
expr_stmt|;
return|return
name|array
return|;
block|}
else|else
block|{
specifier|final
name|LongArray
name|newArray
init|=
name|newLongArray
argument_list|(
name|size
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
operator|,
name|end
operator|=
name|Math
operator|.
name|min
argument_list|(
name|size
argument_list|,
name|array
operator|.
name|size
argument_list|()
argument_list|)
condition|;
name|i
operator|<
name|end
incr|;
control|++i)
block|{
name|newArray
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|array
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|newArray
return|;
block|}
block|}
comment|/** Grow an array to a size that is larger than<code>minSize</code>, preserving content, and potentially reusing part of the provided array. */
DECL|method|grow
specifier|public
specifier|static
name|LongArray
name|grow
parameter_list|(
name|LongArray
name|array
parameter_list|,
name|long
name|minSize
parameter_list|)
block|{
if|if
condition|(
name|minSize
operator|<=
name|array
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
name|array
return|;
block|}
specifier|final
name|long
name|newSize
init|=
name|overSize
argument_list|(
name|minSize
argument_list|,
name|BigLongArray
operator|.
name|PAGE_SIZE
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
argument_list|)
decl_stmt|;
return|return
name|resize
argument_list|(
name|array
argument_list|,
name|newSize
argument_list|)
return|;
block|}
comment|/** Allocate a new {@link LongArray} of the given capacity. */
DECL|method|newDoubleArray
specifier|public
specifier|static
name|DoubleArray
name|newDoubleArray
parameter_list|(
name|long
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<=
name|BigLongArray
operator|.
name|PAGE_SIZE
condition|)
block|{
return|return
operator|new
name|DoubleArrayWrapper
argument_list|(
operator|new
name|double
index|[
operator|(
name|int
operator|)
name|size
index|]
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|BigDoubleArray
argument_list|(
name|size
argument_list|)
return|;
block|}
block|}
comment|/** Resize the array to the exact provided size. */
DECL|method|resize
specifier|public
specifier|static
name|DoubleArray
name|resize
parameter_list|(
name|DoubleArray
name|array
parameter_list|,
name|long
name|size
parameter_list|)
block|{
if|if
condition|(
name|array
operator|instanceof
name|BigDoubleArray
condition|)
block|{
operator|(
operator|(
name|BigDoubleArray
operator|)
name|array
operator|)
operator|.
name|resize
argument_list|(
name|size
argument_list|)
expr_stmt|;
return|return
name|array
return|;
block|}
else|else
block|{
specifier|final
name|DoubleArray
name|newArray
init|=
name|newDoubleArray
argument_list|(
name|size
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
operator|,
name|end
operator|=
name|Math
operator|.
name|min
argument_list|(
name|size
argument_list|,
name|array
operator|.
name|size
argument_list|()
argument_list|)
condition|;
name|i
operator|<
name|end
incr|;
control|++i)
block|{
name|newArray
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|array
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|newArray
return|;
block|}
block|}
comment|/** Grow an array to a size that is larger than<code>minSize</code>, preserving content, and potentially reusing part of the provided array. */
DECL|method|grow
specifier|public
specifier|static
name|DoubleArray
name|grow
parameter_list|(
name|DoubleArray
name|array
parameter_list|,
name|long
name|minSize
parameter_list|)
block|{
if|if
condition|(
name|minSize
operator|<=
name|array
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
name|array
return|;
block|}
specifier|final
name|long
name|newSize
init|=
name|overSize
argument_list|(
name|minSize
argument_list|,
name|BigDoubleArray
operator|.
name|PAGE_SIZE
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_DOUBLE
argument_list|)
decl_stmt|;
return|return
name|resize
argument_list|(
name|array
argument_list|,
name|newSize
argument_list|)
return|;
block|}
comment|/** Allocate a new {@link LongArray} of the given capacity. */
DECL|method|newObjectArray
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|ObjectArray
argument_list|<
name|T
argument_list|>
name|newObjectArray
parameter_list|(
name|long
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<=
name|BigLongArray
operator|.
name|PAGE_SIZE
condition|)
block|{
return|return
operator|new
name|ObjectArrayWrapper
argument_list|<
name|T
argument_list|>
argument_list|(
operator|new
name|Object
index|[
operator|(
name|int
operator|)
name|size
index|]
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|BigObjectArray
argument_list|<
name|T
argument_list|>
argument_list|(
name|size
argument_list|)
return|;
block|}
block|}
comment|/** Resize the array to the exact provided size. */
DECL|method|resize
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|ObjectArray
argument_list|<
name|T
argument_list|>
name|resize
parameter_list|(
name|ObjectArray
argument_list|<
name|T
argument_list|>
name|array
parameter_list|,
name|long
name|size
parameter_list|)
block|{
if|if
condition|(
name|array
operator|instanceof
name|BigObjectArray
condition|)
block|{
operator|(
operator|(
name|BigObjectArray
argument_list|<
name|?
argument_list|>
operator|)
name|array
operator|)
operator|.
name|resize
argument_list|(
name|size
argument_list|)
expr_stmt|;
return|return
name|array
return|;
block|}
else|else
block|{
specifier|final
name|ObjectArray
argument_list|<
name|T
argument_list|>
name|newArray
init|=
name|newObjectArray
argument_list|(
name|size
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
operator|,
name|end
operator|=
name|Math
operator|.
name|min
argument_list|(
name|size
argument_list|,
name|array
operator|.
name|size
argument_list|()
argument_list|)
condition|;
name|i
operator|<
name|end
incr|;
control|++i)
block|{
name|newArray
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|array
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|newArray
return|;
block|}
block|}
comment|/** Grow an array to a size that is larger than<code>minSize</code>, preserving content, and potentially reusing part of the provided array. */
DECL|method|grow
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|ObjectArray
argument_list|<
name|T
argument_list|>
name|grow
parameter_list|(
name|ObjectArray
argument_list|<
name|T
argument_list|>
name|array
parameter_list|,
name|long
name|minSize
parameter_list|)
block|{
if|if
condition|(
name|minSize
operator|<=
name|array
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
name|array
return|;
block|}
specifier|final
name|long
name|newSize
init|=
name|overSize
argument_list|(
name|minSize
argument_list|,
name|BigObjectArray
operator|.
name|PAGE_SIZE
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
decl_stmt|;
return|return
name|resize
argument_list|(
name|array
argument_list|,
name|newSize
argument_list|)
return|;
block|}
block|}
end_enum

end_unit

