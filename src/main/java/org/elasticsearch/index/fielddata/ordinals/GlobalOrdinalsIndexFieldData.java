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
name|AtomicReaderContext
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
name|TermsEnum
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
name|index
operator|.
name|AbstractIndexComponent
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
name|fielddata
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
name|index
operator|.
name|fielddata
operator|.
name|fieldcomparator
operator|.
name|SortMode
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
name|ordinals
operator|.
name|InternalGlobalOrdinalsBuilder
operator|.
name|OrdinalMappingSource
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
name|AtomicFieldDataWithOrdinalsTermsEnum
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
name|FieldMapper
import|;
end_import

begin_comment
comment|/**  * {@link IndexFieldData} impl based on global ordinals.  */
end_comment

begin_class
DECL|class|GlobalOrdinalsIndexFieldData
specifier|public
specifier|final
class|class
name|GlobalOrdinalsIndexFieldData
extends|extends
name|AbstractIndexComponent
implements|implements
name|IndexFieldData
operator|.
name|WithOrdinals
implements|,
name|RamUsage
block|{
DECL|field|fieldNames
specifier|private
specifier|final
name|FieldMapper
operator|.
name|Names
name|fieldNames
decl_stmt|;
DECL|field|fieldDataType
specifier|private
specifier|final
name|FieldDataType
name|fieldDataType
decl_stmt|;
DECL|field|atomicReaders
specifier|private
specifier|final
name|Atomic
index|[]
name|atomicReaders
decl_stmt|;
DECL|field|memorySizeInBytes
specifier|private
specifier|final
name|long
name|memorySizeInBytes
decl_stmt|;
DECL|method|GlobalOrdinalsIndexFieldData
specifier|public
name|GlobalOrdinalsIndexFieldData
parameter_list|(
name|Index
name|index
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|FieldMapper
operator|.
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|,
name|AtomicFieldData
operator|.
name|WithOrdinals
index|[]
name|segmentAfd
parameter_list|,
name|LongValues
name|globalOrdToFirstSegment
parameter_list|,
name|LongValues
name|globalOrdToFirstSegmentDelta
parameter_list|,
name|OrdinalMappingSource
index|[]
name|segmentOrdToGlobalOrds
parameter_list|,
name|long
name|memorySizeInBytes
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldNames
operator|=
name|fieldNames
expr_stmt|;
name|this
operator|.
name|fieldDataType
operator|=
name|fieldDataType
expr_stmt|;
name|this
operator|.
name|atomicReaders
operator|=
operator|new
name|Atomic
index|[
name|segmentAfd
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segmentAfd
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|atomicReaders
index|[
name|i
index|]
operator|=
operator|new
name|Atomic
argument_list|(
name|segmentAfd
index|[
name|i
index|]
argument_list|,
name|globalOrdToFirstSegment
argument_list|,
name|globalOrdToFirstSegmentDelta
argument_list|,
name|segmentOrdToGlobalOrds
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|memorySizeInBytes
operator|=
name|memorySizeInBytes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|AtomicFieldData
operator|.
name|WithOrdinals
name|load
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
return|return
name|atomicReaders
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
name|AtomicFieldData
operator|.
name|WithOrdinals
name|loadDirect
parameter_list|(
name|AtomicReaderContext
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
DECL|method|loadGlobal
specifier|public
name|WithOrdinals
name|loadGlobal
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|)
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|localGlobalDirect
specifier|public
name|WithOrdinals
name|localGlobalDirect
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldNames
specifier|public
name|FieldMapper
operator|.
name|Names
name|getFieldNames
parameter_list|()
block|{
return|return
name|fieldNames
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldDataType
specifier|public
name|FieldDataType
name|getFieldDataType
parameter_list|()
block|{
return|return
name|fieldDataType
return|;
block|}
annotation|@
name|Override
DECL|method|valuesOrdered
specifier|public
name|boolean
name|valuesOrdered
parameter_list|()
block|{
return|return
literal|false
return|;
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
name|SortMode
name|sortMode
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"no global ordinals sorting yet"
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
comment|// no need to clear, because this is cached and cleared in AbstractBytesIndexFieldData
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
comment|// no need to clear, because this is cached and cleared in AbstractBytesIndexFieldData
block|}
annotation|@
name|Override
DECL|method|getMemorySizeInBytes
specifier|public
name|long
name|getMemorySizeInBytes
parameter_list|()
block|{
return|return
name|memorySizeInBytes
return|;
block|}
DECL|class|Atomic
specifier|private
specifier|final
class|class
name|Atomic
implements|implements
name|AtomicFieldData
operator|.
name|WithOrdinals
block|{
DECL|field|afd
specifier|private
specifier|final
name|AtomicFieldData
operator|.
name|WithOrdinals
name|afd
decl_stmt|;
DECL|field|segmentOrdToGlobalOrdLookup
specifier|private
specifier|final
name|OrdinalMappingSource
name|segmentOrdToGlobalOrdLookup
decl_stmt|;
DECL|field|globalOrdToFirstSegment
specifier|private
specifier|final
name|LongValues
name|globalOrdToFirstSegment
decl_stmt|;
DECL|field|globalOrdToFirstSegmentDelta
specifier|private
specifier|final
name|LongValues
name|globalOrdToFirstSegmentDelta
decl_stmt|;
DECL|method|Atomic
specifier|private
name|Atomic
parameter_list|(
name|WithOrdinals
name|afd
parameter_list|,
name|LongValues
name|globalOrdToFirstSegment
parameter_list|,
name|LongValues
name|globalOrdToFirstSegmentDelta
parameter_list|,
name|OrdinalMappingSource
name|segmentOrdToGlobalOrdLookup
parameter_list|)
block|{
name|this
operator|.
name|afd
operator|=
name|afd
expr_stmt|;
name|this
operator|.
name|segmentOrdToGlobalOrdLookup
operator|=
name|segmentOrdToGlobalOrdLookup
expr_stmt|;
name|this
operator|.
name|globalOrdToFirstSegment
operator|=
name|globalOrdToFirstSegment
expr_stmt|;
name|this
operator|.
name|globalOrdToFirstSegmentDelta
operator|=
name|globalOrdToFirstSegmentDelta
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBytesValues
specifier|public
name|BytesValues
operator|.
name|WithOrdinals
name|getBytesValues
parameter_list|(
name|boolean
name|needsHashes
parameter_list|)
block|{
name|BytesValues
operator|.
name|WithOrdinals
name|values
init|=
name|afd
operator|.
name|getBytesValues
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Ordinals
operator|.
name|Docs
name|segmentOrdinals
init|=
name|values
operator|.
name|ordinals
argument_list|()
decl_stmt|;
name|Ordinals
operator|.
name|Docs
name|globalOrdinals
init|=
name|segmentOrdToGlobalOrdLookup
operator|.
name|globalOrdinals
argument_list|(
name|segmentOrdinals
argument_list|)
decl_stmt|;
specifier|final
name|BytesValues
operator|.
name|WithOrdinals
index|[]
name|bytesValues
init|=
operator|new
name|BytesValues
operator|.
name|WithOrdinals
index|[
name|atomicReaders
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
name|bytesValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bytesValues
index|[
name|i
index|]
operator|=
name|atomicReaders
index|[
name|i
index|]
operator|.
name|afd
operator|.
name|getBytesValues
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BytesValues
operator|.
name|WithOrdinals
argument_list|(
name|globalOrdinals
argument_list|)
block|{
name|int
name|readerIndex
decl_stmt|;
annotation|@
name|Override
specifier|public
name|BytesRef
name|getValueByOrd
parameter_list|(
name|long
name|globalOrd
parameter_list|)
block|{
specifier|final
name|long
name|segmentOrd
init|=
name|globalOrd
operator|-
name|globalOrdToFirstSegmentDelta
operator|.
name|get
argument_list|(
name|globalOrd
argument_list|)
decl_stmt|;
name|readerIndex
operator|=
operator|(
name|int
operator|)
name|globalOrdToFirstSegment
operator|.
name|get
argument_list|(
name|globalOrd
argument_list|)
expr_stmt|;
return|return
name|bytesValues
index|[
name|readerIndex
index|]
operator|.
name|getValueByOrd
argument_list|(
name|segmentOrd
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|copyShared
parameter_list|()
block|{
return|return
name|bytesValues
index|[
name|readerIndex
index|]
operator|.
name|copyShared
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|currentValueHash
parameter_list|()
block|{
return|return
name|bytesValues
index|[
name|readerIndex
index|]
operator|.
name|currentValueHash
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
name|afd
operator|.
name|isMultiValued
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumDocs
specifier|public
name|int
name|getNumDocs
parameter_list|()
block|{
return|return
name|afd
operator|.
name|getNumDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumberUniqueValues
specifier|public
name|long
name|getNumberUniqueValues
parameter_list|()
block|{
return|return
name|afd
operator|.
name|getNumberUniqueValues
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMemorySizeInBytes
specifier|public
name|long
name|getMemorySizeInBytes
parameter_list|()
block|{
return|return
name|afd
operator|.
name|getMemorySizeInBytes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getScriptValues
specifier|public
name|ScriptDocValues
name|getScriptValues
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Script values not supported on global ordinals"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getTermsEnum
specifier|public
name|TermsEnum
name|getTermsEnum
parameter_list|()
block|{
return|return
operator|new
name|AtomicFieldDataWithOrdinalsTermsEnum
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{         }
block|}
block|}
end_class

end_unit

