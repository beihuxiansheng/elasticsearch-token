begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cache.recycler
package|package
name|org
operator|.
name|elasticsearch
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
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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
name|component
operator|.
name|AbstractComponent
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
name|recycler
operator|.
name|AbstractRecyclerC
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
name|recycler
operator|.
name|Recycler
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
name|concurrent
operator|.
name|EsExecutors
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|recycler
operator|.
name|Recyclers
operator|.
name|*
import|;
end_import

begin_comment
comment|/** A recycler of fixed-size pages. */
end_comment

begin_class
DECL|class|PageCacheRecycler
specifier|public
class|class
name|PageCacheRecycler
extends|extends
name|AbstractComponent
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"recycler.page.type"
decl_stmt|;
DECL|field|LIMIT_HEAP
specifier|public
specifier|static
specifier|final
name|String
name|LIMIT_HEAP
init|=
literal|"recycler.page.limit.heap"
decl_stmt|;
DECL|field|WEIGHT
specifier|public
specifier|static
specifier|final
name|String
name|WEIGHT
init|=
literal|"recycler.page.weight"
decl_stmt|;
DECL|field|bytePage
specifier|private
specifier|final
name|Recycler
argument_list|<
name|byte
index|[]
argument_list|>
name|bytePage
decl_stmt|;
DECL|field|intPage
specifier|private
specifier|final
name|Recycler
argument_list|<
name|int
index|[]
argument_list|>
name|intPage
decl_stmt|;
DECL|field|longPage
specifier|private
specifier|final
name|Recycler
argument_list|<
name|long
index|[]
argument_list|>
name|longPage
decl_stmt|;
DECL|field|objectPage
specifier|private
specifier|final
name|Recycler
argument_list|<
name|Object
index|[]
argument_list|>
name|objectPage
decl_stmt|;
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|bytePage
operator|.
name|close
argument_list|()
expr_stmt|;
name|intPage
operator|.
name|close
argument_list|()
expr_stmt|;
name|longPage
operator|.
name|close
argument_list|()
expr_stmt|;
name|objectPage
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|maximumSearchThreadPoolSize
specifier|private
specifier|static
name|int
name|maximumSearchThreadPoolSize
parameter_list|(
name|ThreadPool
name|threadPool
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
name|ThreadPool
operator|.
name|Info
name|searchThreadPool
init|=
name|threadPool
operator|.
name|info
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SEARCH
argument_list|)
decl_stmt|;
assert|assert
name|searchThreadPool
operator|!=
literal|null
assert|;
specifier|final
name|int
name|maxSize
init|=
name|searchThreadPool
operator|.
name|getMax
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxSize
operator|<=
literal|0
condition|)
block|{
comment|// happens with cached thread pools, let's assume there are at most 3x ${number of processors} threads
return|return
literal|3
operator|*
name|EsExecutors
operator|.
name|boundedNumberOfProcessors
argument_list|(
name|settings
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|maxSize
return|;
block|}
block|}
annotation|@
name|Inject
DECL|method|PageCacheRecycler
specifier|public
name|PageCacheRecycler
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
specifier|final
name|Type
name|type
init|=
name|Type
operator|.
name|parse
argument_list|(
name|settings
operator|.
name|get
argument_list|(
name|TYPE
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|long
name|limit
init|=
name|settings
operator|.
name|getAsMemory
argument_list|(
name|LIMIT_HEAP
argument_list|,
literal|"10%"
argument_list|)
operator|.
name|bytes
argument_list|()
decl_stmt|;
specifier|final
name|int
name|availableProcessors
init|=
name|EsExecutors
operator|.
name|boundedNumberOfProcessors
argument_list|(
name|settings
argument_list|)
decl_stmt|;
specifier|final
name|int
name|searchThreadPoolSize
init|=
name|maximumSearchThreadPoolSize
argument_list|(
name|threadPool
argument_list|,
name|settings
argument_list|)
decl_stmt|;
comment|// We have a global amount of memory that we need to divide across data types.
comment|// Since some types are more useful than other ones we give them different weights.
comment|// Trying to store all of them in a single stack would be problematic because eg.
comment|// a work load could fill the recycler with only byte[] pages and then another
comment|// workload that would work with double[] pages couldn't recycle them because there
comment|// is no space left in the stack/queue. LRU/LFU policies are not an option either
comment|// because they would make obtain/release too costly: we really need constant-time
comment|// operations.
comment|// Ultimately a better solution would be to only store one kind of data and have the
comment|// ability to intepret it either as a source of bytes, doubles, longs, etc. eg. thanks
comment|// to direct ByteBuffers or sun.misc.Unsafe on a byte[] but this would have other issues
comment|// that would need to be addressed such as garbage collection of native memory or safety
comment|// of Unsafe writes.
specifier|final
name|double
name|bytesWeight
init|=
name|settings
operator|.
name|getAsDouble
argument_list|(
name|WEIGHT
operator|+
literal|".bytes"
argument_list|,
literal|1d
argument_list|)
decl_stmt|;
specifier|final
name|double
name|intsWeight
init|=
name|settings
operator|.
name|getAsDouble
argument_list|(
name|WEIGHT
operator|+
literal|".ints"
argument_list|,
literal|1d
argument_list|)
decl_stmt|;
specifier|final
name|double
name|longsWeight
init|=
name|settings
operator|.
name|getAsDouble
argument_list|(
name|WEIGHT
operator|+
literal|".longs"
argument_list|,
literal|1d
argument_list|)
decl_stmt|;
comment|// object pages are less useful to us so we give them a lower weight by default
specifier|final
name|double
name|objectsWeight
init|=
name|settings
operator|.
name|getAsDouble
argument_list|(
name|WEIGHT
operator|+
literal|".objects"
argument_list|,
literal|0.1d
argument_list|)
decl_stmt|;
specifier|final
name|double
name|totalWeight
init|=
name|bytesWeight
operator|+
name|intsWeight
operator|+
name|longsWeight
operator|+
name|objectsWeight
decl_stmt|;
specifier|final
name|int
name|maxPageCount
init|=
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
name|limit
operator|/
name|BigArrays
operator|.
name|PAGE_SIZE_IN_BYTES
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxBytePageCount
init|=
call|(
name|int
call|)
argument_list|(
name|bytesWeight
operator|*
name|maxPageCount
operator|/
name|totalWeight
argument_list|)
decl_stmt|;
name|bytePage
operator|=
name|build
argument_list|(
name|type
argument_list|,
name|maxBytePageCount
argument_list|,
name|searchThreadPoolSize
argument_list|,
name|availableProcessors
argument_list|,
operator|new
name|AbstractRecyclerC
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|newInstance
parameter_list|(
name|int
name|sizing
parameter_list|)
block|{
return|return
operator|new
name|byte
index|[
name|BigArrays
operator|.
name|BYTE_PAGE_SIZE
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|recycle
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
block|{
comment|// nothing to do
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|int
name|maxIntPageCount
init|=
call|(
name|int
call|)
argument_list|(
name|intsWeight
operator|*
name|maxPageCount
operator|/
name|totalWeight
argument_list|)
decl_stmt|;
name|intPage
operator|=
name|build
argument_list|(
name|type
argument_list|,
name|maxIntPageCount
argument_list|,
name|searchThreadPoolSize
argument_list|,
name|availableProcessors
argument_list|,
operator|new
name|AbstractRecyclerC
argument_list|<
name|int
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
index|[]
name|newInstance
parameter_list|(
name|int
name|sizing
parameter_list|)
block|{
return|return
operator|new
name|int
index|[
name|BigArrays
operator|.
name|INT_PAGE_SIZE
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|recycle
parameter_list|(
name|int
index|[]
name|value
parameter_list|)
block|{
comment|// nothing to do
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|int
name|maxLongPageCount
init|=
call|(
name|int
call|)
argument_list|(
name|longsWeight
operator|*
name|maxPageCount
operator|/
name|totalWeight
argument_list|)
decl_stmt|;
name|longPage
operator|=
name|build
argument_list|(
name|type
argument_list|,
name|maxLongPageCount
argument_list|,
name|searchThreadPoolSize
argument_list|,
name|availableProcessors
argument_list|,
operator|new
name|AbstractRecyclerC
argument_list|<
name|long
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
index|[]
name|newInstance
parameter_list|(
name|int
name|sizing
parameter_list|)
block|{
return|return
operator|new
name|long
index|[
name|BigArrays
operator|.
name|LONG_PAGE_SIZE
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|recycle
parameter_list|(
name|long
index|[]
name|value
parameter_list|)
block|{
comment|// nothing to do
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|int
name|maxObjectPageCount
init|=
call|(
name|int
call|)
argument_list|(
name|objectsWeight
operator|*
name|maxPageCount
operator|/
name|totalWeight
argument_list|)
decl_stmt|;
name|objectPage
operator|=
name|build
argument_list|(
name|type
argument_list|,
name|maxObjectPageCount
argument_list|,
name|searchThreadPoolSize
argument_list|,
name|availableProcessors
argument_list|,
operator|new
name|AbstractRecyclerC
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
index|[]
name|newInstance
parameter_list|(
name|int
name|sizing
parameter_list|)
block|{
return|return
operator|new
name|Object
index|[
name|BigArrays
operator|.
name|OBJECT_PAGE_SIZE
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|recycle
parameter_list|(
name|Object
index|[]
name|value
parameter_list|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|value
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// we need to remove the strong refs on the objects stored in the array
block|}
block|}
argument_list|)
expr_stmt|;
assert|assert
name|BigArrays
operator|.
name|PAGE_SIZE_IN_BYTES
operator|*
operator|(
name|maxBytePageCount
operator|+
name|maxIntPageCount
operator|+
name|maxLongPageCount
operator|+
name|maxObjectPageCount
operator|)
operator|<=
name|limit
assert|;
block|}
DECL|method|bytePage
specifier|public
name|Recycler
operator|.
name|V
argument_list|<
name|byte
index|[]
argument_list|>
name|bytePage
parameter_list|(
name|boolean
name|clear
parameter_list|)
block|{
specifier|final
name|Recycler
operator|.
name|V
argument_list|<
name|byte
index|[]
argument_list|>
name|v
init|=
name|bytePage
operator|.
name|obtain
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|isRecycled
argument_list|()
operator|&&
name|clear
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|v
operator|.
name|v
argument_list|()
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|v
return|;
block|}
DECL|method|intPage
specifier|public
name|Recycler
operator|.
name|V
argument_list|<
name|int
index|[]
argument_list|>
name|intPage
parameter_list|(
name|boolean
name|clear
parameter_list|)
block|{
specifier|final
name|Recycler
operator|.
name|V
argument_list|<
name|int
index|[]
argument_list|>
name|v
init|=
name|intPage
operator|.
name|obtain
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|isRecycled
argument_list|()
operator|&&
name|clear
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|v
operator|.
name|v
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|v
return|;
block|}
DECL|method|longPage
specifier|public
name|Recycler
operator|.
name|V
argument_list|<
name|long
index|[]
argument_list|>
name|longPage
parameter_list|(
name|boolean
name|clear
parameter_list|)
block|{
specifier|final
name|Recycler
operator|.
name|V
argument_list|<
name|long
index|[]
argument_list|>
name|v
init|=
name|longPage
operator|.
name|obtain
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|isRecycled
argument_list|()
operator|&&
name|clear
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|v
operator|.
name|v
argument_list|()
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
return|return
name|v
return|;
block|}
DECL|method|objectPage
specifier|public
name|Recycler
operator|.
name|V
argument_list|<
name|Object
index|[]
argument_list|>
name|objectPage
parameter_list|()
block|{
comment|// object pages are cleared on release anyway
return|return
name|objectPage
operator|.
name|obtain
argument_list|()
return|;
block|}
DECL|method|build
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Recycler
argument_list|<
name|T
argument_list|>
name|build
parameter_list|(
name|Type
name|type
parameter_list|,
name|int
name|limit
parameter_list|,
name|int
name|estimatedThreadPoolSize
parameter_list|,
name|int
name|availableProcessors
parameter_list|,
name|Recycler
operator|.
name|C
argument_list|<
name|T
argument_list|>
name|c
parameter_list|)
block|{
specifier|final
name|Recycler
argument_list|<
name|T
argument_list|>
name|recycler
decl_stmt|;
if|if
condition|(
name|limit
operator|==
literal|0
condition|)
block|{
name|recycler
operator|=
name|none
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|recycler
operator|=
name|type
operator|.
name|build
argument_list|(
name|c
argument_list|,
name|limit
argument_list|,
name|estimatedThreadPoolSize
argument_list|,
name|availableProcessors
argument_list|)
expr_stmt|;
block|}
return|return
name|recycler
return|;
block|}
DECL|enum|Type
specifier|public
specifier|static
enum|enum
name|Type
block|{
DECL|enum constant|QUEUE
name|QUEUE
block|{
annotation|@
name|Override
argument_list|<
name|T
argument_list|>
name|Recycler
argument_list|<
name|T
argument_list|>
name|build
parameter_list|(
name|Recycler
operator|.
name|C
argument_list|<
name|T
argument_list|>
name|c
parameter_list|,
name|int
name|limit
parameter_list|,
name|int
name|estimatedThreadPoolSize
parameter_list|,
name|int
name|availableProcessors
parameter_list|)
block|{
return|return
name|concurrentDeque
argument_list|(
name|c
argument_list|,
name|limit
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|CONCURRENT
name|CONCURRENT
block|{
annotation|@
name|Override
argument_list|<
name|T
argument_list|>
name|Recycler
argument_list|<
name|T
argument_list|>
name|build
parameter_list|(
name|Recycler
operator|.
name|C
argument_list|<
name|T
argument_list|>
name|c
parameter_list|,
name|int
name|limit
parameter_list|,
name|int
name|estimatedThreadPoolSize
parameter_list|,
name|int
name|availableProcessors
parameter_list|)
block|{
return|return
name|concurrent
argument_list|(
name|dequeFactory
argument_list|(
name|c
argument_list|,
name|limit
operator|/
name|availableProcessors
argument_list|)
argument_list|,
name|availableProcessors
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|NONE
name|NONE
block|{
annotation|@
name|Override
argument_list|<
name|T
argument_list|>
name|Recycler
argument_list|<
name|T
argument_list|>
name|build
parameter_list|(
name|Recycler
operator|.
name|C
argument_list|<
name|T
argument_list|>
name|c
parameter_list|,
name|int
name|limit
parameter_list|,
name|int
name|estimatedThreadPoolSize
parameter_list|,
name|int
name|availableProcessors
parameter_list|)
block|{
return|return
name|none
argument_list|(
name|c
argument_list|)
return|;
block|}
block|}
block|;
DECL|method|parse
specifier|public
specifier|static
name|Type
name|parse
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|CONCURRENT
return|;
block|}
try|try
block|{
return|return
name|Type
operator|.
name|valueOf
argument_list|(
name|type
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"no type support ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|build
specifier|abstract
parameter_list|<
name|T
parameter_list|>
name|Recycler
argument_list|<
name|T
argument_list|>
name|build
parameter_list|(
name|Recycler
operator|.
name|C
argument_list|<
name|T
argument_list|>
name|c
parameter_list|,
name|int
name|limit
parameter_list|,
name|int
name|estimatedThreadPoolSize
parameter_list|,
name|int
name|availableProcessors
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

