begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|*
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
name|PagedBytes
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
name|GrowableWriter
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
name|ElasticSearchException
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
name|fielddata
operator|.
name|ordinals
operator|.
name|MultiFlatArrayOrdinals
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
name|SingleArrayOrdinals
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|settings
operator|.
name|IndexSettings
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|PagedBytesIndexFieldData
specifier|public
class|class
name|PagedBytesIndexFieldData
extends|extends
name|AbstractIndexFieldData
argument_list|<
name|PagedBytesAtomicFieldData
argument_list|>
implements|implements
name|IndexOrdinalFieldData
argument_list|<
name|PagedBytesAtomicFieldData
argument_list|>
block|{
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
name|build
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|FieldMapper
operator|.
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|type
parameter_list|,
name|IndexFieldDataCache
name|cache
parameter_list|)
block|{
return|return
operator|new
name|PagedBytesIndexFieldData
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|fieldNames
argument_list|,
name|type
argument_list|,
name|cache
argument_list|)
return|;
block|}
block|}
DECL|method|PagedBytesIndexFieldData
specifier|public
name|PagedBytesIndexFieldData
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|FieldMapper
operator|.
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|,
name|IndexFieldDataCache
name|cache
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|fieldNames
argument_list|,
name|fieldDataType
argument_list|,
name|cache
argument_list|)
expr_stmt|;
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
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|PagedBytesAtomicFieldData
name|load
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
block|{
try|try
block|{
return|return
name|cache
operator|.
name|load
argument_list|(
name|context
argument_list|,
name|this
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|ElasticSearchException
condition|)
block|{
throw|throw
operator|(
name|ElasticSearchException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|loadDirect
specifier|public
name|PagedBytesAtomicFieldData
name|loadDirect
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|AtomicReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|(
name|getFieldNames
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
name|PagedBytesAtomicFieldData
operator|.
name|empty
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
specifier|final
name|PagedBytes
name|bytes
init|=
operator|new
name|PagedBytes
argument_list|(
literal|15
argument_list|)
decl_stmt|;
name|int
name|startBytesBPV
decl_stmt|;
name|int
name|startTermsBPV
decl_stmt|;
name|int
name|startNumUniqueTerms
decl_stmt|;
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|int
name|termCountHardLimit
decl_stmt|;
if|if
condition|(
name|maxDoc
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|termCountHardLimit
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|termCountHardLimit
operator|=
name|maxDoc
operator|+
literal|1
expr_stmt|;
block|}
comment|// Try for coarse estimate for number of bits; this
comment|// should be an underestimate most of the time, which
comment|// is fine -- GrowableWriter will reallocate as needed
name|long
name|numUniqueTerms
init|=
name|terms
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|numUniqueTerms
operator|!=
operator|-
literal|1L
condition|)
block|{
if|if
condition|(
name|numUniqueTerms
operator|>
name|termCountHardLimit
condition|)
block|{
comment|// app is misusing the API (there is more than
comment|// one term per doc); in this case we make best
comment|// effort to load what we can (see LUCENE-2142)
name|numUniqueTerms
operator|=
name|termCountHardLimit
expr_stmt|;
block|}
name|startBytesBPV
operator|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|numUniqueTerms
operator|*
literal|4
argument_list|)
expr_stmt|;
name|startTermsBPV
operator|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|numUniqueTerms
argument_list|)
expr_stmt|;
name|startNumUniqueTerms
operator|=
operator|(
name|int
operator|)
name|numUniqueTerms
expr_stmt|;
block|}
else|else
block|{
name|startBytesBPV
operator|=
literal|1
expr_stmt|;
name|startTermsBPV
operator|=
literal|1
expr_stmt|;
name|startNumUniqueTerms
operator|=
literal|1
expr_stmt|;
block|}
comment|// TODO: expose this as an option..., have a nice parser for it...
name|float
name|acceptableOverheadRatio
init|=
name|PackedInts
operator|.
name|FAST
decl_stmt|;
name|GrowableWriter
name|termOrdToBytesOffset
init|=
operator|new
name|GrowableWriter
argument_list|(
name|startBytesBPV
argument_list|,
literal|1
operator|+
name|startNumUniqueTerms
argument_list|,
name|acceptableOverheadRatio
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|int
index|[]
argument_list|>
name|ordinals
init|=
operator|new
name|ArrayList
argument_list|<
name|int
index|[]
argument_list|>
argument_list|()
decl_stmt|;
name|int
index|[]
name|idx
init|=
operator|new
name|int
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
decl_stmt|;
name|ordinals
operator|.
name|add
argument_list|(
operator|new
name|int
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
argument_list|)
expr_stmt|;
comment|// 0 is reserved for "unset"
name|bytes
operator|.
name|copyUsingLengthPrefix
argument_list|(
operator|new
name|BytesRef
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|termOrd
init|=
literal|1
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|DocsEnum
name|docsEnum
init|=
literal|null
decl_stmt|;
for|for
control|(
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|next
argument_list|()
init|;
name|term
operator|!=
literal|null
condition|;
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
control|)
block|{
if|if
condition|(
name|termOrd
operator|==
name|termOrdToBytesOffset
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// NOTE: this code only runs if the incoming
comment|// reader impl doesn't implement
comment|// size (which should be uncommon)
name|termOrdToBytesOffset
operator|=
name|termOrdToBytesOffset
operator|.
name|resize
argument_list|(
name|ArrayUtil
operator|.
name|oversize
argument_list|(
literal|1
operator|+
name|termOrd
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|termOrdToBytesOffset
operator|.
name|set
argument_list|(
name|termOrd
argument_list|,
name|bytes
operator|.
name|copyUsingLengthPrefix
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|,
name|docsEnum
argument_list|,
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|docId
init|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
init|;
name|docId
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|;
name|docId
operator|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|int
index|[]
name|ordinal
decl_stmt|;
if|if
condition|(
name|idx
index|[
name|docId
index|]
operator|>=
name|ordinals
operator|.
name|size
argument_list|()
condition|)
block|{
name|ordinal
operator|=
operator|new
name|int
index|[
name|reader
operator|.
name|maxDoc
argument_list|()
index|]
expr_stmt|;
name|ordinals
operator|.
name|add
argument_list|(
name|ordinal
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ordinal
operator|=
name|ordinals
operator|.
name|get
argument_list|(
name|idx
index|[
name|docId
index|]
argument_list|)
expr_stmt|;
block|}
name|ordinal
index|[
name|docId
index|]
operator|=
name|termOrd
expr_stmt|;
name|idx
index|[
name|docId
index|]
operator|++
expr_stmt|;
block|}
name|termOrd
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"StopFillCacheException"
argument_list|)
condition|)
block|{
comment|// all is well, in case numeric parsers are used.
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
name|PagedBytes
operator|.
name|Reader
name|bytesReader
init|=
name|bytes
operator|.
name|freeze
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|PackedInts
operator|.
name|Reader
name|termOrdToBytesOffsetReader
init|=
name|termOrdToBytesOffset
operator|.
name|getMutable
argument_list|()
decl_stmt|;
if|if
condition|(
name|ordinals
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
operator|new
name|PagedBytesAtomicFieldData
argument_list|(
name|bytesReader
argument_list|,
name|termOrdToBytesOffsetReader
argument_list|,
operator|new
name|SingleArrayOrdinals
argument_list|(
name|ordinals
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|termOrd
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
name|int
index|[]
index|[]
name|nativeOrdinals
init|=
operator|new
name|int
index|[
name|ordinals
operator|.
name|size
argument_list|()
index|]
index|[]
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
name|nativeOrdinals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|nativeOrdinals
index|[
name|i
index|]
operator|=
name|ordinals
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|PagedBytesAtomicFieldData
argument_list|(
name|bytesReader
argument_list|,
name|termOrdToBytesOffsetReader
argument_list|,
operator|new
name|MultiFlatArrayOrdinals
argument_list|(
name|nativeOrdinals
argument_list|,
name|termOrd
argument_list|)
argument_list|)
return|;
block|}
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
parameter_list|)
block|{
comment|// TODO support "missingValue" for sortMissingValue options here...
return|return
operator|new
name|BytesRefFieldComparatorSource
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

