begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.cache.recycler
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|cache
operator|.
name|recycler
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|SeedUtils
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
name|base
operator|.
name|Predicate
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
name|collect
operator|.
name|Maps
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
name|collect
operator|.
name|Sets
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
name|BytesRef
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
name|common
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticsearchTestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|ConcurrentHashMap
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
name|ConcurrentMap
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_class
DECL|class|MockBigArrays
specifier|public
class|class
name|MockBigArrays
extends|extends
name|BigArrays
block|{
comment|/**      * Tracking allocations is useful when debugging a leak but shouldn't be enabled by default as this would also be very costly      * since it creates a new Exception every time a new array is created.      */
DECL|field|TRACK_ALLOCATIONS
specifier|private
specifier|static
specifier|final
name|boolean
name|TRACK_ALLOCATIONS
init|=
literal|false
decl_stmt|;
DECL|field|INSTANCES
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|BigArrays
argument_list|>
name|INSTANCES
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|WeakHashMap
argument_list|<
name|BigArrays
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|ACQUIRED_ARRAYS
specifier|private
specifier|static
specifier|final
name|ConcurrentMap
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|ACQUIRED_ARRAYS
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|ensureAllArraysAreReleased
specifier|public
specifier|static
name|void
name|ensureAllArraysAreReleased
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|masterCopy
init|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|ACQUIRED_ARRAYS
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|masterCopy
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// not empty, we might be executing on a shared cluster that keeps on obtaining
comment|// and releasing arrays, lets make sure that after a reasonable timeout, all master
comment|// copy (snapshot) have been released
name|boolean
name|success
init|=
name|ElasticsearchTestCase
operator|.
name|awaitBusy
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Object
name|input
parameter_list|)
block|{
return|return
name|Sets
operator|.
name|intersection
argument_list|(
name|masterCopy
operator|.
name|keySet
argument_list|()
argument_list|,
name|ACQUIRED_ARRAYS
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|masterCopy
operator|.
name|keySet
argument_list|()
operator|.
name|retainAll
argument_list|(
name|ACQUIRED_ARRAYS
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|ACQUIRED_ARRAYS
operator|.
name|keySet
argument_list|()
operator|.
name|removeAll
argument_list|(
name|masterCopy
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove all existing master copy we will report on
if|if
condition|(
operator|!
name|masterCopy
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|Object
name|cause
init|=
name|masterCopy
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|masterCopy
operator|.
name|size
argument_list|()
operator|+
literal|" arrays have not been released"
argument_list|,
name|cause
operator|instanceof
name|Throwable
condition|?
operator|(
name|Throwable
operator|)
name|cause
else|:
literal|null
argument_list|)
throw|;
block|}
block|}
block|}
for|for
control|(
specifier|final
name|BigArrays
name|bigArrays
range|:
name|INSTANCES
control|)
block|{
comment|// BigArrays are used on the network layer and the cluster is shared across tests so nodes might still be talking to
comment|// each other a bit after the test finished, wait a bit for things to stabilize if so
specifier|final
name|boolean
name|sizeIsZero
init|=
name|ElasticsearchTestCase
operator|.
name|awaitBusy
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Object
name|input
parameter_list|)
block|{
return|return
name|bigArrays
operator|.
name|sizeInBytes
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|sizeIsZero
condition|)
block|{
specifier|final
name|long
name|sizeInBytes
init|=
name|bigArrays
operator|.
name|sizeInBytes
argument_list|()
decl_stmt|;
if|if
condition|(
name|sizeInBytes
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Expected 0 bytes, got "
operator|+
name|sizeInBytes
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
annotation|@
name|Inject
DECL|method|MockBigArrays
specifier|public
name|MockBigArrays
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|PageCacheRecycler
name|recycler
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|recycler
argument_list|)
expr_stmt|;
name|long
name|seed
decl_stmt|;
try|try
block|{
name|seed
operator|=
name|SeedUtils
operator|.
name|parseSeed
argument_list|(
name|RandomizedContext
operator|.
name|current
argument_list|()
operator|.
name|getRunnerSeedAsString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// rest tests don't run randomized and have no context
name|seed
operator|=
literal|0
expr_stmt|;
block|}
name|random
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|INSTANCES
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newByteArray
specifier|public
name|ByteArray
name|newByteArray
parameter_list|(
name|long
name|size
parameter_list|,
name|boolean
name|clearOnResize
parameter_list|)
block|{
specifier|final
name|ByteArrayWrapper
name|array
init|=
operator|new
name|ByteArrayWrapper
argument_list|(
name|super
operator|.
name|newByteArray
argument_list|(
name|size
argument_list|,
name|clearOnResize
argument_list|)
argument_list|,
name|clearOnResize
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|clearOnResize
condition|)
block|{
name|array
operator|.
name|randomizeContent
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
return|return
name|array
return|;
block|}
annotation|@
name|Override
DECL|method|resize
specifier|public
name|ByteArray
name|resize
parameter_list|(
name|ByteArray
name|array
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|ByteArrayWrapper
name|arr
init|=
operator|(
name|ByteArrayWrapper
operator|)
name|array
decl_stmt|;
specifier|final
name|long
name|originalSize
init|=
name|arr
operator|.
name|size
argument_list|()
decl_stmt|;
name|array
operator|=
name|super
operator|.
name|resize
argument_list|(
name|arr
operator|.
name|in
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|ACQUIRED_ARRAYS
operator|.
name|remove
argument_list|(
name|arr
argument_list|)
expr_stmt|;
if|if
condition|(
name|array
operator|instanceof
name|ByteArrayWrapper
condition|)
block|{
name|arr
operator|=
operator|(
name|ByteArrayWrapper
operator|)
name|array
expr_stmt|;
block|}
else|else
block|{
name|arr
operator|=
operator|new
name|ByteArrayWrapper
argument_list|(
name|array
argument_list|,
name|arr
operator|.
name|clearOnResize
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|arr
operator|.
name|clearOnResize
condition|)
block|{
name|arr
operator|.
name|randomizeContent
argument_list|(
name|originalSize
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
return|return
name|arr
return|;
block|}
annotation|@
name|Override
DECL|method|newIntArray
specifier|public
name|IntArray
name|newIntArray
parameter_list|(
name|long
name|size
parameter_list|,
name|boolean
name|clearOnResize
parameter_list|)
block|{
specifier|final
name|IntArrayWrapper
name|array
init|=
operator|new
name|IntArrayWrapper
argument_list|(
name|super
operator|.
name|newIntArray
argument_list|(
name|size
argument_list|,
name|clearOnResize
argument_list|)
argument_list|,
name|clearOnResize
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|clearOnResize
condition|)
block|{
name|array
operator|.
name|randomizeContent
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
return|return
name|array
return|;
block|}
annotation|@
name|Override
DECL|method|resize
specifier|public
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
name|IntArrayWrapper
name|arr
init|=
operator|(
name|IntArrayWrapper
operator|)
name|array
decl_stmt|;
specifier|final
name|long
name|originalSize
init|=
name|arr
operator|.
name|size
argument_list|()
decl_stmt|;
name|array
operator|=
name|super
operator|.
name|resize
argument_list|(
name|arr
operator|.
name|in
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|ACQUIRED_ARRAYS
operator|.
name|remove
argument_list|(
name|arr
argument_list|)
expr_stmt|;
if|if
condition|(
name|array
operator|instanceof
name|IntArrayWrapper
condition|)
block|{
name|arr
operator|=
operator|(
name|IntArrayWrapper
operator|)
name|array
expr_stmt|;
block|}
else|else
block|{
name|arr
operator|=
operator|new
name|IntArrayWrapper
argument_list|(
name|array
argument_list|,
name|arr
operator|.
name|clearOnResize
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|arr
operator|.
name|clearOnResize
condition|)
block|{
name|arr
operator|.
name|randomizeContent
argument_list|(
name|originalSize
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
return|return
name|arr
return|;
block|}
annotation|@
name|Override
DECL|method|newLongArray
specifier|public
name|LongArray
name|newLongArray
parameter_list|(
name|long
name|size
parameter_list|,
name|boolean
name|clearOnResize
parameter_list|)
block|{
specifier|final
name|LongArrayWrapper
name|array
init|=
operator|new
name|LongArrayWrapper
argument_list|(
name|super
operator|.
name|newLongArray
argument_list|(
name|size
argument_list|,
name|clearOnResize
argument_list|)
argument_list|,
name|clearOnResize
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|clearOnResize
condition|)
block|{
name|array
operator|.
name|randomizeContent
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
return|return
name|array
return|;
block|}
annotation|@
name|Override
DECL|method|resize
specifier|public
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
name|LongArrayWrapper
name|arr
init|=
operator|(
name|LongArrayWrapper
operator|)
name|array
decl_stmt|;
specifier|final
name|long
name|originalSize
init|=
name|arr
operator|.
name|size
argument_list|()
decl_stmt|;
name|array
operator|=
name|super
operator|.
name|resize
argument_list|(
name|arr
operator|.
name|in
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|ACQUIRED_ARRAYS
operator|.
name|remove
argument_list|(
name|arr
argument_list|)
expr_stmt|;
if|if
condition|(
name|array
operator|instanceof
name|LongArrayWrapper
condition|)
block|{
name|arr
operator|=
operator|(
name|LongArrayWrapper
operator|)
name|array
expr_stmt|;
block|}
else|else
block|{
name|arr
operator|=
operator|new
name|LongArrayWrapper
argument_list|(
name|array
argument_list|,
name|arr
operator|.
name|clearOnResize
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|arr
operator|.
name|clearOnResize
condition|)
block|{
name|arr
operator|.
name|randomizeContent
argument_list|(
name|originalSize
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
return|return
name|arr
return|;
block|}
annotation|@
name|Override
DECL|method|newFloatArray
specifier|public
name|FloatArray
name|newFloatArray
parameter_list|(
name|long
name|size
parameter_list|,
name|boolean
name|clearOnResize
parameter_list|)
block|{
specifier|final
name|FloatArrayWrapper
name|array
init|=
operator|new
name|FloatArrayWrapper
argument_list|(
name|super
operator|.
name|newFloatArray
argument_list|(
name|size
argument_list|,
name|clearOnResize
argument_list|)
argument_list|,
name|clearOnResize
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|clearOnResize
condition|)
block|{
name|array
operator|.
name|randomizeContent
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
return|return
name|array
return|;
block|}
annotation|@
name|Override
DECL|method|resize
specifier|public
name|FloatArray
name|resize
parameter_list|(
name|FloatArray
name|array
parameter_list|,
name|long
name|size
parameter_list|)
block|{
name|FloatArrayWrapper
name|arr
init|=
operator|(
name|FloatArrayWrapper
operator|)
name|array
decl_stmt|;
specifier|final
name|long
name|originalSize
init|=
name|arr
operator|.
name|size
argument_list|()
decl_stmt|;
name|array
operator|=
name|super
operator|.
name|resize
argument_list|(
name|arr
operator|.
name|in
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|ACQUIRED_ARRAYS
operator|.
name|remove
argument_list|(
name|arr
argument_list|)
expr_stmt|;
if|if
condition|(
name|array
operator|instanceof
name|FloatArrayWrapper
condition|)
block|{
name|arr
operator|=
operator|(
name|FloatArrayWrapper
operator|)
name|array
expr_stmt|;
block|}
else|else
block|{
name|arr
operator|=
operator|new
name|FloatArrayWrapper
argument_list|(
name|array
argument_list|,
name|arr
operator|.
name|clearOnResize
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|arr
operator|.
name|clearOnResize
condition|)
block|{
name|arr
operator|.
name|randomizeContent
argument_list|(
name|originalSize
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
return|return
name|arr
return|;
block|}
annotation|@
name|Override
DECL|method|newDoubleArray
specifier|public
name|DoubleArray
name|newDoubleArray
parameter_list|(
name|long
name|size
parameter_list|,
name|boolean
name|clearOnResize
parameter_list|)
block|{
specifier|final
name|DoubleArrayWrapper
name|array
init|=
operator|new
name|DoubleArrayWrapper
argument_list|(
name|super
operator|.
name|newDoubleArray
argument_list|(
name|size
argument_list|,
name|clearOnResize
argument_list|)
argument_list|,
name|clearOnResize
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|clearOnResize
condition|)
block|{
name|array
operator|.
name|randomizeContent
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
return|return
name|array
return|;
block|}
annotation|@
name|Override
DECL|method|resize
specifier|public
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
name|DoubleArrayWrapper
name|arr
init|=
operator|(
name|DoubleArrayWrapper
operator|)
name|array
decl_stmt|;
specifier|final
name|long
name|originalSize
init|=
name|arr
operator|.
name|size
argument_list|()
decl_stmt|;
name|array
operator|=
name|super
operator|.
name|resize
argument_list|(
name|arr
operator|.
name|in
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|ACQUIRED_ARRAYS
operator|.
name|remove
argument_list|(
name|arr
argument_list|)
expr_stmt|;
if|if
condition|(
name|array
operator|instanceof
name|DoubleArrayWrapper
condition|)
block|{
name|arr
operator|=
operator|(
name|DoubleArrayWrapper
operator|)
name|array
expr_stmt|;
block|}
else|else
block|{
name|arr
operator|=
operator|new
name|DoubleArrayWrapper
argument_list|(
name|array
argument_list|,
name|arr
operator|.
name|clearOnResize
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|arr
operator|.
name|clearOnResize
condition|)
block|{
name|arr
operator|.
name|randomizeContent
argument_list|(
name|originalSize
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
return|return
name|arr
return|;
block|}
annotation|@
name|Override
DECL|method|newObjectArray
specifier|public
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
return|return
operator|new
name|ObjectArrayWrapper
argument_list|<>
argument_list|(
name|super
operator|.
expr|<
name|T
operator|>
name|newObjectArray
argument_list|(
name|size
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|resize
specifier|public
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
name|ObjectArrayWrapper
argument_list|<
name|T
argument_list|>
name|arr
init|=
operator|(
name|ObjectArrayWrapper
argument_list|<
name|T
argument_list|>
operator|)
name|array
decl_stmt|;
name|array
operator|=
name|super
operator|.
name|resize
argument_list|(
name|arr
operator|.
name|in
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|ACQUIRED_ARRAYS
operator|.
name|remove
argument_list|(
name|arr
argument_list|)
expr_stmt|;
if|if
condition|(
name|array
operator|instanceof
name|ObjectArrayWrapper
condition|)
block|{
name|arr
operator|=
operator|(
name|ObjectArrayWrapper
argument_list|<
name|T
argument_list|>
operator|)
name|array
expr_stmt|;
block|}
else|else
block|{
name|arr
operator|=
operator|new
name|ObjectArrayWrapper
argument_list|<>
argument_list|(
name|array
argument_list|)
expr_stmt|;
block|}
return|return
name|arr
return|;
block|}
DECL|class|AbstractArrayWrapper
specifier|private
specifier|static
specifier|abstract
class|class
name|AbstractArrayWrapper
block|{
DECL|field|in
specifier|final
name|BigArray
name|in
decl_stmt|;
DECL|field|clearOnResize
name|boolean
name|clearOnResize
decl_stmt|;
DECL|field|released
name|AtomicBoolean
name|released
decl_stmt|;
DECL|method|AbstractArrayWrapper
name|AbstractArrayWrapper
parameter_list|(
name|BigArray
name|in
parameter_list|,
name|boolean
name|clearOnResize
parameter_list|)
block|{
name|ACQUIRED_ARRAYS
operator|.
name|put
argument_list|(
name|this
argument_list|,
name|TRACK_ALLOCATIONS
condition|?
operator|new
name|RuntimeException
argument_list|()
else|:
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|clearOnResize
operator|=
name|clearOnResize
expr_stmt|;
name|released
operator|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|getDelegate
specifier|protected
specifier|abstract
name|BigArray
name|getDelegate
parameter_list|()
function_decl|;
DECL|method|randomizeContent
specifier|protected
specifier|abstract
name|void
name|randomizeContent
parameter_list|(
name|long
name|from
parameter_list|,
name|long
name|to
parameter_list|)
function_decl|;
DECL|method|size
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|getDelegate
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|sizeInBytes
specifier|public
name|long
name|sizeInBytes
parameter_list|()
block|{
return|return
name|in
operator|.
name|sizeInBytes
argument_list|()
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
operator|!
name|released
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Double release"
argument_list|)
throw|;
block|}
name|ACQUIRED_ARRAYS
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|randomizeContent
argument_list|(
literal|0
argument_list|,
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|getDelegate
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ByteArrayWrapper
specifier|private
class|class
name|ByteArrayWrapper
extends|extends
name|AbstractArrayWrapper
implements|implements
name|ByteArray
block|{
DECL|field|in
specifier|private
specifier|final
name|ByteArray
name|in
decl_stmt|;
DECL|method|ByteArrayWrapper
name|ByteArrayWrapper
parameter_list|(
name|ByteArray
name|in
parameter_list|,
name|boolean
name|clearOnResize
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|,
name|clearOnResize
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDelegate
specifier|protected
name|BigArray
name|getDelegate
parameter_list|()
block|{
return|return
name|in
return|;
block|}
annotation|@
name|Override
DECL|method|randomizeContent
specifier|protected
name|void
name|randomizeContent
parameter_list|(
name|long
name|from
parameter_list|,
name|long
name|to
parameter_list|)
block|{
name|fill
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
operator|(
name|byte
operator|)
name|random
operator|.
name|nextInt
argument_list|(
literal|1
operator|<<
literal|8
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|byte
name|get
parameter_list|(
name|long
name|index
parameter_list|)
block|{
return|return
name|in
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|byte
name|set
parameter_list|(
name|long
name|index
parameter_list|,
name|byte
name|value
parameter_list|)
block|{
return|return
name|in
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|long
name|index
parameter_list|,
name|int
name|len
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
block|{
return|return
name|in
operator|.
name|get
argument_list|(
name|index
argument_list|,
name|len
argument_list|,
name|ref
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|long
name|index
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|in
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|buf
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
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
name|byte
name|value
parameter_list|)
block|{
name|in
operator|.
name|fill
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|IntArrayWrapper
specifier|private
class|class
name|IntArrayWrapper
extends|extends
name|AbstractArrayWrapper
implements|implements
name|IntArray
block|{
DECL|field|in
specifier|private
specifier|final
name|IntArray
name|in
decl_stmt|;
DECL|method|IntArrayWrapper
name|IntArrayWrapper
parameter_list|(
name|IntArray
name|in
parameter_list|,
name|boolean
name|clearOnResize
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|,
name|clearOnResize
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDelegate
specifier|protected
name|BigArray
name|getDelegate
parameter_list|()
block|{
return|return
name|in
return|;
block|}
annotation|@
name|Override
DECL|method|randomizeContent
specifier|protected
name|void
name|randomizeContent
parameter_list|(
name|long
name|from
parameter_list|,
name|long
name|to
parameter_list|)
block|{
name|fill
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|random
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
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
return|return
name|in
operator|.
name|get
argument_list|(
name|index
argument_list|)
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
return|return
name|in
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|value
argument_list|)
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
return|return
name|in
operator|.
name|increment
argument_list|(
name|index
argument_list|,
name|inc
argument_list|)
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
name|int
name|value
parameter_list|)
block|{
name|in
operator|.
name|fill
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|LongArrayWrapper
specifier|private
class|class
name|LongArrayWrapper
extends|extends
name|AbstractArrayWrapper
implements|implements
name|LongArray
block|{
DECL|field|in
specifier|private
specifier|final
name|LongArray
name|in
decl_stmt|;
DECL|method|LongArrayWrapper
name|LongArrayWrapper
parameter_list|(
name|LongArray
name|in
parameter_list|,
name|boolean
name|clearOnResize
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|,
name|clearOnResize
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDelegate
specifier|protected
name|BigArray
name|getDelegate
parameter_list|()
block|{
return|return
name|in
return|;
block|}
annotation|@
name|Override
DECL|method|randomizeContent
specifier|protected
name|void
name|randomizeContent
parameter_list|(
name|long
name|from
parameter_list|,
name|long
name|to
parameter_list|)
block|{
name|fill
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
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
return|return
name|in
operator|.
name|get
argument_list|(
name|index
argument_list|)
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
return|return
name|in
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|value
argument_list|)
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
return|return
name|in
operator|.
name|increment
argument_list|(
name|index
argument_list|,
name|inc
argument_list|)
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
name|long
name|value
parameter_list|)
block|{
name|in
operator|.
name|fill
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FloatArrayWrapper
specifier|private
class|class
name|FloatArrayWrapper
extends|extends
name|AbstractArrayWrapper
implements|implements
name|FloatArray
block|{
DECL|field|in
specifier|private
specifier|final
name|FloatArray
name|in
decl_stmt|;
DECL|method|FloatArrayWrapper
name|FloatArrayWrapper
parameter_list|(
name|FloatArray
name|in
parameter_list|,
name|boolean
name|clearOnResize
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|,
name|clearOnResize
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDelegate
specifier|protected
name|BigArray
name|getDelegate
parameter_list|()
block|{
return|return
name|in
return|;
block|}
annotation|@
name|Override
DECL|method|randomizeContent
specifier|protected
name|void
name|randomizeContent
parameter_list|(
name|long
name|from
parameter_list|,
name|long
name|to
parameter_list|)
block|{
name|fill
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
operator|(
name|random
operator|.
name|nextFloat
argument_list|()
operator|-
literal|0.5f
operator|)
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|float
name|get
parameter_list|(
name|long
name|index
parameter_list|)
block|{
return|return
name|in
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|float
name|set
parameter_list|(
name|long
name|index
parameter_list|,
name|float
name|value
parameter_list|)
block|{
return|return
name|in
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|increment
specifier|public
name|float
name|increment
parameter_list|(
name|long
name|index
parameter_list|,
name|float
name|inc
parameter_list|)
block|{
return|return
name|in
operator|.
name|increment
argument_list|(
name|index
argument_list|,
name|inc
argument_list|)
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
name|float
name|value
parameter_list|)
block|{
name|in
operator|.
name|fill
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|DoubleArrayWrapper
specifier|private
class|class
name|DoubleArrayWrapper
extends|extends
name|AbstractArrayWrapper
implements|implements
name|DoubleArray
block|{
DECL|field|in
specifier|private
specifier|final
name|DoubleArray
name|in
decl_stmt|;
DECL|method|DoubleArrayWrapper
name|DoubleArrayWrapper
parameter_list|(
name|DoubleArray
name|in
parameter_list|,
name|boolean
name|clearOnResize
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|,
name|clearOnResize
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDelegate
specifier|protected
name|BigArray
name|getDelegate
parameter_list|()
block|{
return|return
name|in
return|;
block|}
annotation|@
name|Override
DECL|method|randomizeContent
specifier|protected
name|void
name|randomizeContent
parameter_list|(
name|long
name|from
parameter_list|,
name|long
name|to
parameter_list|)
block|{
name|fill
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
operator|(
name|random
operator|.
name|nextDouble
argument_list|()
operator|-
literal|0.5
operator|)
operator|*
literal|1000
argument_list|)
expr_stmt|;
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
return|return
name|in
operator|.
name|get
argument_list|(
name|index
argument_list|)
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
return|return
name|in
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|value
argument_list|)
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
return|return
name|in
operator|.
name|increment
argument_list|(
name|index
argument_list|,
name|inc
argument_list|)
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
name|in
operator|.
name|fill
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ObjectArrayWrapper
specifier|private
class|class
name|ObjectArrayWrapper
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractArrayWrapper
implements|implements
name|ObjectArray
argument_list|<
name|T
argument_list|>
block|{
DECL|field|in
specifier|private
specifier|final
name|ObjectArray
argument_list|<
name|T
argument_list|>
name|in
decl_stmt|;
DECL|method|ObjectArrayWrapper
name|ObjectArrayWrapper
parameter_list|(
name|ObjectArray
argument_list|<
name|T
argument_list|>
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDelegate
specifier|protected
name|BigArray
name|getDelegate
parameter_list|()
block|{
return|return
name|in
return|;
block|}
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
return|return
name|in
operator|.
name|get
argument_list|(
name|index
argument_list|)
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
return|return
name|in
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|randomizeContent
specifier|protected
name|void
name|randomizeContent
parameter_list|(
name|long
name|from
parameter_list|,
name|long
name|to
parameter_list|)
block|{
comment|// will be cleared anyway
block|}
block|}
block|}
end_class

end_unit

