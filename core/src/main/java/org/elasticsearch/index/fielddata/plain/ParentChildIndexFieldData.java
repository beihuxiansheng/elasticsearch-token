begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.plain
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|plain
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
name|DirectoryReader
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
name|LeafReader
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
name|LeafReaderContext
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
name|SortedDocValues
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
name|BytesRef
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
name|LongValues
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
name|ElasticsearchException
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
name|Nullable
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
name|Index
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
name|AtomicParentChildFieldData
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
name|IndexFieldData
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
name|IndexFieldData
operator|.
name|XFieldComparatorSource
operator|.
name|Nested
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
name|IndexFieldDataCache
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
name|IndexParentChildFieldData
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
name|fieldcomparator
operator|.
name|BytesRefFieldComparatorSource
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
name|mapper
operator|.
name|DocumentMapper
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
name|mapper
operator|.
name|MappedFieldType
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
name|mapper
operator|.
name|MapperService
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
name|mapper
operator|.
name|ParentFieldMapper
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|MultiValueMode
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
name|ArrayList
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
comment|/**  * ParentChildIndexFieldData is responsible for loading the id cache mapping  * needed for has_child and has_parent queries into memory.  */
end_comment

begin_class
DECL|class|ParentChildIndexFieldData
specifier|public
class|class
name|ParentChildIndexFieldData
extends|extends
name|AbstractIndexFieldData
argument_list|<
name|AtomicParentChildFieldData
argument_list|>
implements|implements
name|IndexParentChildFieldData
block|{
DECL|field|parentTypes
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|parentTypes
decl_stmt|;
DECL|field|breakerService
specifier|private
specifier|final
name|CircuitBreakerService
name|breakerService
decl_stmt|;
DECL|method|ParentChildIndexFieldData
specifier|public
name|ParentChildIndexFieldData
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|IndexFieldDataCache
name|cache
parameter_list|,
name|MapperService
name|mapperService
parameter_list|,
name|CircuitBreakerService
name|breakerService
parameter_list|)
block|{
name|super
argument_list|(
name|indexSettings
argument_list|,
name|fieldName
argument_list|,
name|cache
argument_list|)
expr_stmt|;
name|this
operator|.
name|breakerService
operator|=
name|breakerService
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|parentTypes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DocumentMapper
name|mapper
range|:
name|mapperService
operator|.
name|docMappers
argument_list|(
literal|false
argument_list|)
control|)
block|{
name|ParentFieldMapper
name|parentFieldMapper
init|=
name|mapper
operator|.
name|parentFieldMapper
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentFieldMapper
operator|.
name|active
argument_list|()
condition|)
block|{
name|parentTypes
operator|.
name|add
argument_list|(
name|parentFieldMapper
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|parentTypes
operator|=
name|parentTypes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|comparatorSource
specifier|public
name|XFieldComparatorSource
name|comparatorSource
parameter_list|(
annotation|@
name|Nullable
name|Object
name|missingValue
parameter_list|,
name|MultiValueMode
name|sortMode
parameter_list|,
name|Nested
name|nested
parameter_list|)
block|{
return|return
operator|new
name|BytesRefFieldComparatorSource
argument_list|(
name|this
argument_list|,
name|missingValue
argument_list|,
name|sortMode
argument_list|,
name|nested
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|AtomicParentChildFieldData
name|load
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
block|{
specifier|final
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
return|return
operator|new
name|AbstractAtomicParentChildFieldData
argument_list|()
block|{
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|types
parameter_list|()
block|{
return|return
name|parentTypes
return|;
block|}
annotation|@
name|Override
specifier|public
name|SortedDocValues
name|getOrdinalsValues
parameter_list|(
name|String
name|type
parameter_list|)
block|{
try|try
block|{
return|return
name|DocValues
operator|.
name|getSorted
argument_list|(
name|reader
argument_list|,
name|ParentFieldMapper
operator|.
name|joinField
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"cannot load join doc values field for type ["
operator|+
name|type
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
comment|// unknown
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
throws|throws
name|ElasticsearchException
block|{             }
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|loadDirect
specifier|public
name|AbstractAtomicParentChildFieldData
name|loadDirect
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|empty
specifier|protected
name|AtomicParentChildFieldData
name|empty
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
return|return
name|AbstractAtomicParentChildFieldData
operator|.
name|empty
argument_list|()
return|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
implements|implements
name|IndexFieldData
operator|.
name|Builder
block|{
annotation|@
name|Override
DECL|method|build
specifier|public
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|build
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
name|MappedFieldType
name|fieldType
parameter_list|,
name|IndexFieldDataCache
name|cache
parameter_list|,
name|CircuitBreakerService
name|breakerService
parameter_list|,
name|MapperService
name|mapperService
parameter_list|)
block|{
return|return
operator|new
name|ParentChildIndexFieldData
argument_list|(
name|indexSettings
argument_list|,
name|fieldType
operator|.
name|name
argument_list|()
argument_list|,
name|cache
argument_list|,
name|mapperService
argument_list|,
name|breakerService
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|loadGlobal
specifier|public
name|IndexParentChildFieldData
name|loadGlobal
parameter_list|(
name|DirectoryReader
name|indexReader
parameter_list|)
block|{
if|if
condition|(
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
operator|<=
literal|1
condition|)
block|{
comment|// ordinals are already global
return|return
name|this
return|;
block|}
try|try
block|{
return|return
name|cache
operator|.
name|load
argument_list|(
name|indexReader
argument_list|,
name|this
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|ElasticsearchException
condition|)
block|{
throw|throw
operator|(
name|ElasticsearchException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|buildOrdinalMap
specifier|private
specifier|static
name|OrdinalMap
name|buildOrdinalMap
parameter_list|(
name|AtomicParentChildFieldData
index|[]
name|atomicFD
parameter_list|,
name|String
name|parentType
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SortedDocValues
index|[]
name|ordinals
init|=
operator|new
name|SortedDocValues
index|[
name|atomicFD
operator|.
name|length
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
name|ordinals
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|ordinals
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
argument_list|(
name|parentType
argument_list|)
expr_stmt|;
block|}
return|return
name|OrdinalMap
operator|.
name|build
argument_list|(
literal|null
argument_list|,
name|ordinals
argument_list|,
name|PackedInts
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
DECL|class|OrdinalMapAndAtomicFieldData
specifier|private
specifier|static
class|class
name|OrdinalMapAndAtomicFieldData
block|{
DECL|field|ordMap
specifier|final
name|OrdinalMap
name|ordMap
decl_stmt|;
DECL|field|fieldData
specifier|final
name|AtomicParentChildFieldData
index|[]
name|fieldData
decl_stmt|;
DECL|method|OrdinalMapAndAtomicFieldData
name|OrdinalMapAndAtomicFieldData
parameter_list|(
name|OrdinalMap
name|ordMap
parameter_list|,
name|AtomicParentChildFieldData
index|[]
name|fieldData
parameter_list|)
block|{
name|this
operator|.
name|ordMap
operator|=
name|ordMap
expr_stmt|;
name|this
operator|.
name|fieldData
operator|=
name|fieldData
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|localGlobalDirect
specifier|public
name|IndexParentChildFieldData
name|localGlobalDirect
parameter_list|(
name|DirectoryReader
name|indexReader
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|long
name|ramBytesUsed
init|=
literal|0
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OrdinalMapAndAtomicFieldData
argument_list|>
name|perType
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|type
range|:
name|parentTypes
control|)
block|{
specifier|final
name|AtomicParentChildFieldData
index|[]
name|fieldData
init|=
operator|new
name|AtomicParentChildFieldData
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
name|LeafReaderContext
name|context
range|:
name|indexReader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|fieldData
index|[
name|context
operator|.
name|ord
index|]
operator|=
name|load
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|final
name|OrdinalMap
name|ordMap
init|=
name|buildOrdinalMap
argument_list|(
name|fieldData
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|ramBytesUsed
operator|+=
name|ordMap
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
name|perType
operator|.
name|put
argument_list|(
name|type
argument_list|,
operator|new
name|OrdinalMapAndAtomicFieldData
argument_list|(
name|ordMap
argument_list|,
name|fieldData
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AtomicParentChildFieldData
index|[]
name|fielddata
init|=
operator|new
name|AtomicParentChildFieldData
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
name|fielddata
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|fielddata
index|[
name|i
index|]
operator|=
operator|new
name|GlobalAtomicFieldData
argument_list|(
name|parentTypes
argument_list|,
name|perType
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
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
name|ramBytesUsed
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
literal|"global-ordinals [_parent] took [{}]"
argument_list|,
operator|new
name|TimeValue
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
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
name|GlobalFieldData
argument_list|(
name|indexReader
argument_list|,
name|fielddata
argument_list|,
name|ramBytesUsed
argument_list|,
name|perType
argument_list|)
return|;
block|}
DECL|class|GlobalAtomicFieldData
specifier|private
specifier|static
class|class
name|GlobalAtomicFieldData
extends|extends
name|AbstractAtomicParentChildFieldData
block|{
DECL|field|types
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|types
decl_stmt|;
DECL|field|atomicFD
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OrdinalMapAndAtomicFieldData
argument_list|>
name|atomicFD
decl_stmt|;
DECL|field|segmentIndex
specifier|private
specifier|final
name|int
name|segmentIndex
decl_stmt|;
DECL|method|GlobalAtomicFieldData
name|GlobalAtomicFieldData
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|types
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|OrdinalMapAndAtomicFieldData
argument_list|>
name|atomicFD
parameter_list|,
name|int
name|segmentIndex
parameter_list|)
block|{
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
name|this
operator|.
name|atomicFD
operator|=
name|atomicFD
expr_stmt|;
name|this
operator|.
name|segmentIndex
operator|=
name|segmentIndex
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|types
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|types
parameter_list|()
block|{
return|return
name|types
return|;
block|}
annotation|@
name|Override
DECL|method|getOrdinalsValues
specifier|public
name|SortedDocValues
name|getOrdinalsValues
parameter_list|(
name|String
name|type
parameter_list|)
block|{
specifier|final
name|OrdinalMapAndAtomicFieldData
name|atomicFD
init|=
name|this
operator|.
name|atomicFD
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|atomicFD
operator|==
literal|null
condition|)
block|{
return|return
name|DocValues
operator|.
name|emptySorted
argument_list|()
return|;
block|}
specifier|final
name|OrdinalMap
name|ordMap
init|=
name|atomicFD
operator|.
name|ordMap
decl_stmt|;
specifier|final
name|SortedDocValues
index|[]
name|allSegmentValues
init|=
operator|new
name|SortedDocValues
index|[
name|atomicFD
operator|.
name|fieldData
operator|.
name|length
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
name|allSegmentValues
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|allSegmentValues
index|[
name|i
index|]
operator|=
name|atomicFD
operator|.
name|fieldData
index|[
name|i
index|]
operator|.
name|getOrdinalsValues
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SortedDocValues
name|segmentValues
init|=
name|allSegmentValues
index|[
name|segmentIndex
index|]
decl_stmt|;
if|if
condition|(
name|segmentValues
operator|.
name|getValueCount
argument_list|()
operator|==
name|ordMap
operator|.
name|getValueCount
argument_list|()
condition|)
block|{
comment|// ords are already global
return|return
name|segmentValues
return|;
block|}
specifier|final
name|LongValues
name|globalOrds
init|=
name|ordMap
operator|.
name|getGlobalOrds
argument_list|(
name|segmentIndex
argument_list|)
decl_stmt|;
return|return
operator|new
name|SortedDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|BytesRef
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
specifier|final
name|int
name|segmentIndex
init|=
name|ordMap
operator|.
name|getFirstSegmentNumber
argument_list|(
name|ord
argument_list|)
decl_stmt|;
specifier|final
name|int
name|segmentOrd
init|=
operator|(
name|int
operator|)
name|ordMap
operator|.
name|getFirstSegmentOrd
argument_list|(
name|ord
argument_list|)
decl_stmt|;
return|return
name|allSegmentValues
index|[
name|segmentIndex
index|]
operator|.
name|lookupOrd
argument_list|(
name|segmentOrd
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|ordMap
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
specifier|final
name|int
name|segmentOrd
init|=
name|segmentValues
operator|.
name|getOrd
argument_list|(
name|docID
argument_list|)
decl_stmt|;
comment|// TODO: is there a way we can get rid of this branch?
if|if
condition|(
name|segmentOrd
operator|>=
literal|0
condition|)
block|{
return|return
operator|(
name|int
operator|)
name|globalOrds
operator|.
name|get
argument_list|(
name|segmentOrd
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|segmentOrd
return|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
comment|// this class does not take memory on its own, the index-level field data does
comment|// it through the use of ordinal maps
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
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
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|List
argument_list|<
name|Releasable
argument_list|>
name|closeables
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|OrdinalMapAndAtomicFieldData
name|fds
range|:
name|atomicFD
operator|.
name|values
argument_list|()
control|)
block|{
name|closeables
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|fds
operator|.
name|fieldData
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Releasables
operator|.
name|close
argument_list|(
name|closeables
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|GlobalFieldData
specifier|public
class|class
name|GlobalFieldData
implements|implements
name|IndexParentChildFieldData
implements|,
name|Accountable
block|{
DECL|field|coreCacheKey
specifier|private
specifier|final
name|Object
name|coreCacheKey
decl_stmt|;
DECL|field|leaves
specifier|private
specifier|final
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
decl_stmt|;
DECL|field|fielddata
specifier|private
specifier|final
name|AtomicParentChildFieldData
index|[]
name|fielddata
decl_stmt|;
DECL|field|ramBytesUsed
specifier|private
specifier|final
name|long
name|ramBytesUsed
decl_stmt|;
DECL|field|ordinalMapPerType
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OrdinalMapAndAtomicFieldData
argument_list|>
name|ordinalMapPerType
decl_stmt|;
DECL|method|GlobalFieldData
name|GlobalFieldData
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|AtomicParentChildFieldData
index|[]
name|fielddata
parameter_list|,
name|long
name|ramBytesUsed
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|OrdinalMapAndAtomicFieldData
argument_list|>
name|ordinalMapPerType
parameter_list|)
block|{
name|this
operator|.
name|coreCacheKey
operator|=
name|reader
operator|.
name|getCoreCacheKey
argument_list|()
expr_stmt|;
name|this
operator|.
name|leaves
operator|=
name|reader
operator|.
name|leaves
argument_list|()
expr_stmt|;
name|this
operator|.
name|ramBytesUsed
operator|=
name|ramBytesUsed
expr_stmt|;
name|this
operator|.
name|fielddata
operator|=
name|fielddata
expr_stmt|;
name|this
operator|.
name|ordinalMapPerType
operator|=
name|ordinalMapPerType
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFieldName
specifier|public
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|ParentChildIndexFieldData
operator|.
name|this
operator|.
name|getFieldName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|AtomicParentChildFieldData
name|load
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
block|{
assert|assert
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
operator|==
name|leaves
operator|.
name|get
argument_list|(
name|context
operator|.
name|ord
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
assert|;
return|return
name|fielddata
index|[
name|context
operator|.
name|ord
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|loadDirect
specifier|public
name|AtomicParentChildFieldData
name|loadDirect
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|load
argument_list|(
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|comparatorSource
specifier|public
name|XFieldComparatorSource
name|comparatorSource
parameter_list|(
name|Object
name|missingValue
parameter_list|,
name|MultiValueMode
name|sortMode
parameter_list|,
name|Nested
name|nested
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"No sorting on global ords"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|ParentChildIndexFieldData
operator|.
name|this
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|index
specifier|public
name|Index
name|index
parameter_list|()
block|{
return|return
name|ParentChildIndexFieldData
operator|.
name|this
operator|.
name|index
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|ramBytesUsed
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
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
DECL|method|loadGlobal
specifier|public
name|IndexParentChildFieldData
name|loadGlobal
parameter_list|(
name|DirectoryReader
name|indexReader
parameter_list|)
block|{
if|if
condition|(
name|indexReader
operator|.
name|getCoreCacheKey
argument_list|()
operator|==
name|coreCacheKey
condition|)
block|{
return|return
name|this
return|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|localGlobalDirect
specifier|public
name|IndexParentChildFieldData
name|localGlobalDirect
parameter_list|(
name|DirectoryReader
name|indexReader
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|loadGlobal
argument_list|(
name|indexReader
argument_list|)
return|;
block|}
block|}
comment|/**      * Returns the global ordinal map for the specified type      */
comment|// TODO: OrdinalMap isn't expose in the field data framework, because it is an implementation detail.
comment|// However the JoinUtil works directly with OrdinalMap, so this is a hack to get access to OrdinalMap
comment|// I don't think we should expose OrdinalMap in IndexFieldData, because only parent/child relies on it and for the
comment|// rest of the code OrdinalMap is an implementation detail, but maybe we can expose it in IndexParentChildFieldData interface?
DECL|method|getOrdinalMap
specifier|public
specifier|static
name|MultiDocValues
operator|.
name|OrdinalMap
name|getOrdinalMap
parameter_list|(
name|IndexParentChildFieldData
name|indexParentChildFieldData
parameter_list|,
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
name|indexParentChildFieldData
operator|instanceof
name|ParentChildIndexFieldData
operator|.
name|GlobalFieldData
condition|)
block|{
return|return
operator|(
operator|(
name|GlobalFieldData
operator|)
name|indexParentChildFieldData
operator|)
operator|.
name|ordinalMapPerType
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|.
name|ordMap
return|;
block|}
else|else
block|{
comment|// one segment, local ordinals are global
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

