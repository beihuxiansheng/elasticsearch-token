begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DocValues
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|MultiDocValues
operator|.
name|OrdinalMap
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
name|index
operator|.
name|RandomAccessOrds
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
name|Accountable
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
name|packed
operator|.
name|PackedInts
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
name|breaker
operator|.
name|CircuitBreaker
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
name|logging
operator|.
name|ESLogger
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
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|IndexSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|AtomicOrdinalsFieldData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|IndexOrdinalsFieldData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|plain
operator|.
name|AbstractAtomicOrdinalsFieldData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|breaker
operator|.
name|CircuitBreakerService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|Collections
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
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Utility class to build global ordinals.  */
end_comment

begin_enum
DECL|enum|GlobalOrdinalsBuilder
specifier|public
enum|enum
name|GlobalOrdinalsBuilder
block|{     ;
comment|/**      * Build global ordinals for the provided {@link IndexReader}.      */
DECL|method|build
specifier|public
specifier|static
name|IndexOrdinalsFieldData
name|build
parameter_list|(
specifier|final
name|IndexReader
name|indexReader
parameter_list|,
name|IndexOrdinalsFieldData
name|indexFieldData
parameter_list|,
name|IndexSettings
name|indexSettings
parameter_list|,
name|CircuitBreakerService
name|breakerService
parameter_list|,
name|ESLogger
name|logger
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|1
assert|;
name|long
name|startTimeNS
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
specifier|final
name|AtomicOrdinalsFieldData
index|[]
name|atomicFD
init|=
operator|new
name|AtomicOrdinalsFieldData
index|[
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
specifier|final
name|RandomAccessOrds
index|[]
name|subs
init|=
operator|new
name|RandomAccessOrds
index|[
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|atomicFD
index|[
name|i
index|]
operator|=
name|indexFieldData
operator|.
name|load
argument_list|(
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|subs
index|[
name|i
index|]
operator|=
name|atomicFD
index|[
name|i
index|]
operator|.
name|getOrdinalsValues
argument_list|()
expr_stmt|;
block|}
specifier|final
name|OrdinalMap
name|ordinalMap
init|=
name|OrdinalMap
operator|.
name|build
argument_list|(
literal|null
argument_list|,
name|subs
argument_list|,
name|PackedInts
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|long
name|memorySizeInBytes
init|=
name|ordinalMap
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
name|breakerService
operator|.
name|getBreaker
argument_list|(
name|CircuitBreaker
operator|.
name|FIELDDATA
argument_list|)
operator|.
name|addWithoutBreaking
argument_list|(
name|memorySizeInBytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"global-ordinals [{}][{}] took [{}]"
argument_list|,
name|indexFieldData
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|ordinalMap
operator|.
name|getValueCount
argument_list|()
argument_list|,
operator|new
name|TimeValue
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTimeNS
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|InternalGlobalOrdinalsIndexFieldData
argument_list|(
name|indexSettings
argument_list|,
name|indexFieldData
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|atomicFD
argument_list|,
name|ordinalMap
argument_list|,
name|memorySizeInBytes
argument_list|)
return|;
block|}
DECL|method|buildEmpty
specifier|public
specifier|static
name|IndexOrdinalsFieldData
name|buildEmpty
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
specifier|final
name|IndexReader
name|indexReader
parameter_list|,
name|IndexOrdinalsFieldData
name|indexFieldData
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|1
assert|;
specifier|final
name|AtomicOrdinalsFieldData
index|[]
name|atomicFD
init|=
operator|new
name|AtomicOrdinalsFieldData
index|[
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
specifier|final
name|RandomAccessOrds
index|[]
name|subs
init|=
operator|new
name|RandomAccessOrds
index|[
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|atomicFD
index|[
name|i
index|]
operator|=
operator|new
name|AbstractAtomicOrdinalsFieldData
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RandomAccessOrds
name|getOrdinalsValues
parameter_list|()
block|{
return|return
name|DocValues
operator|.
name|emptySortedSet
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{                 }
block|}
expr_stmt|;
name|subs
index|[
name|i
index|]
operator|=
name|atomicFD
index|[
name|i
index|]
operator|.
name|getOrdinalsValues
argument_list|()
expr_stmt|;
block|}
specifier|final
name|OrdinalMap
name|ordinalMap
init|=
name|OrdinalMap
operator|.
name|build
argument_list|(
literal|null
argument_list|,
name|subs
argument_list|,
name|PackedInts
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
return|return
operator|new
name|InternalGlobalOrdinalsIndexFieldData
argument_list|(
name|indexSettings
argument_list|,
name|indexFieldData
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|atomicFD
argument_list|,
name|ordinalMap
argument_list|,
literal|0
argument_list|)
return|;
block|}
block|}
end_enum

end_unit

