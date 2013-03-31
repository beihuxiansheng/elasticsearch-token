begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.index.fielddata.ordinals
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|ordinals
package|;
end_package

begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

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
name|IntsRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|RamUsage
import|;
end_import

begin_comment
comment|/**  * An efficient store for positive integer slices. This pool uses multiple  * sliced arrays to hold integers in int array pages rather than an object based  * datastructures.  */
end_comment

begin_class
DECL|class|PositiveIntPool
specifier|final
class|class
name|PositiveIntPool
block|{
comment|// TODO it might be useful to store the size of the slices in a sep
comment|// datastructure rather than useing a negative value to donate this.
DECL|field|blockShift
specifier|private
specifier|final
name|int
name|blockShift
decl_stmt|;
DECL|field|blockMask
specifier|private
specifier|final
name|int
name|blockMask
decl_stmt|;
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
decl_stmt|;
comment|/**      * array of buffers currently used in the pool. Buffers are allocated if      * needed don't modify this outside of this class      */
DECL|field|buffers
specifier|private
name|int
index|[]
index|[]
name|buffers
init|=
operator|new
name|int
index|[
literal|10
index|]
index|[]
decl_stmt|;
comment|/**      * index into the buffers array pointing to the current buffer used as the      * head      */
DECL|field|bufferUpto
specifier|private
name|int
name|bufferUpto
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Pointer to the current position in head buffer */
DECL|field|intUpto
specifier|private
name|int
name|intUpto
decl_stmt|;
comment|/** Current head buffer */
DECL|field|buffer
specifier|private
name|int
index|[]
name|buffer
decl_stmt|;
comment|/** Current head offset */
DECL|field|intOffset
specifier|private
name|int
name|intOffset
decl_stmt|;
comment|/**      * Creates a new {@link PositiveIntPool} with the given blockShift.      *       * @param blockShift      *            the n-the power of two indicating the size of each block in      *            the paged datastructure. BlockSize = 1<< blockShift      */
DECL|method|PositiveIntPool
specifier|public
name|PositiveIntPool
parameter_list|(
name|int
name|blockShift
parameter_list|)
block|{
name|this
operator|.
name|blockShift
operator|=
name|blockShift
expr_stmt|;
name|this
operator|.
name|blockSize
operator|=
literal|1
operator|<<
name|blockShift
expr_stmt|;
name|this
operator|.
name|blockMask
operator|=
name|blockSize
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|intUpto
operator|=
name|blockSize
expr_stmt|;
name|this
operator|.
name|intOffset
operator|=
operator|-
name|blockSize
expr_stmt|;
block|}
comment|/**      * Adds all integers in the given slices and returns the positive offset      * into the datastructure to retrive this slice.      */
DECL|method|put
specifier|public
name|int
name|put
parameter_list|(
name|IntsRef
name|slice
parameter_list|)
block|{
if|if
condition|(
name|slice
operator|.
name|length
operator|>
name|blockSize
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Can not store slices greater or equal to: "
operator|+
name|blockSize
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|intUpto
operator|+
name|slice
operator|.
name|length
operator|)
operator|>
name|blockSize
condition|)
block|{
name|nextBuffer
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|relativeOffset
init|=
name|intUpto
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|slice
operator|.
name|ints
argument_list|,
name|slice
operator|.
name|offset
argument_list|,
name|buffer
argument_list|,
name|relativeOffset
argument_list|,
name|slice
operator|.
name|length
argument_list|)
expr_stmt|;
name|intUpto
operator|+=
name|slice
operator|.
name|length
expr_stmt|;
name|buffer
index|[
name|intUpto
operator|-
literal|1
index|]
operator|*=
operator|-
literal|1
expr_stmt|;
comment|// mark as end
return|return
name|relativeOffset
operator|+
name|intOffset
return|;
block|}
comment|/**      * Returns the first value of the slice stored at the given offset.      *<p>      * Note: the slice length must be greater than one otherwise the returned      * value is the negative complement of the actual value      *</p>      */
DECL|method|getFirstFromOffset
specifier|public
name|int
name|getFirstFromOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
specifier|final
name|int
name|blockOffset
init|=
name|offset
operator|>>
name|blockShift
decl_stmt|;
specifier|final
name|int
name|relativeOffset
init|=
name|offset
operator|&
name|blockMask
decl_stmt|;
specifier|final
name|int
index|[]
name|currentBuffer
init|=
name|buffers
index|[
name|blockOffset
index|]
decl_stmt|;
assert|assert
name|currentBuffer
index|[
name|relativeOffset
index|]
operator|>=
literal|0
assert|;
return|return
name|currentBuffer
index|[
name|relativeOffset
index|]
return|;
block|}
comment|/**      * Retrieves a previously stored slice from the pool.      *       * @param slice the slice to fill      * @param offset the offset where the slice is stored      */
DECL|method|fill
specifier|public
name|void
name|fill
parameter_list|(
name|IntsRef
name|slice
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
specifier|final
name|int
name|blockOffset
init|=
name|offset
operator|>>
name|blockShift
decl_stmt|;
specifier|final
name|int
name|relativeOffset
init|=
name|offset
operator|&
name|blockMask
decl_stmt|;
specifier|final
name|int
index|[]
name|currentBuffer
init|=
name|buffers
index|[
name|blockOffset
index|]
decl_stmt|;
name|slice
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|slice
operator|.
name|length
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|relativeOffset
init|;
name|i
operator|<
name|currentBuffer
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|slice
operator|.
name|length
operator|++
expr_stmt|;
if|if
condition|(
name|currentBuffer
index|[
name|i
index|]
operator|<
literal|0
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|slice
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|slice
operator|.
name|ints
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|slice
operator|.
name|ints
argument_list|,
name|slice
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|currentBuffer
argument_list|,
name|relativeOffset
argument_list|,
name|slice
operator|.
name|ints
argument_list|,
literal|0
argument_list|,
name|slice
operator|.
name|length
argument_list|)
expr_stmt|;
name|slice
operator|.
name|ints
index|[
name|slice
operator|.
name|length
operator|-
literal|1
index|]
operator|*=
operator|-
literal|1
expr_stmt|;
block|}
block|}
DECL|method|getMemorySizeInBytes
specifier|public
name|long
name|getMemorySizeInBytes
parameter_list|()
block|{
return|return
operator|(
operator|(
name|bufferUpto
operator|+
literal|1
operator|)
operator|*
name|blockSize
operator|*
name|RamUsage
operator|.
name|NUM_BYTES_INT
operator|)
operator|+
operator|(
operator|(
name|bufferUpto
operator|+
literal|1
operator|)
operator|*
name|RamUsage
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|)
return|;
block|}
DECL|method|nextBuffer
specifier|private
name|void
name|nextBuffer
parameter_list|()
block|{
if|if
condition|(
literal|1
operator|+
name|bufferUpto
operator|==
name|buffers
operator|.
name|length
condition|)
block|{
name|int
index|[]
index|[]
name|newBuffers
init|=
operator|new
name|int
index|[
call|(
name|int
call|)
argument_list|(
name|buffers
operator|.
name|length
operator|*
literal|1.5
argument_list|)
index|]
index|[]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffers
argument_list|,
literal|0
argument_list|,
name|newBuffers
argument_list|,
literal|0
argument_list|,
name|buffers
operator|.
name|length
argument_list|)
expr_stmt|;
name|buffers
operator|=
name|newBuffers
expr_stmt|;
block|}
name|buffer
operator|=
name|buffers
index|[
literal|1
operator|+
name|bufferUpto
index|]
operator|=
operator|new
name|int
index|[
name|blockSize
index|]
expr_stmt|;
name|bufferUpto
operator|++
expr_stmt|;
name|intUpto
operator|=
literal|0
expr_stmt|;
name|intOffset
operator|+=
name|blockSize
expr_stmt|;
block|}
block|}
end_class

end_unit

