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
name|elasticsearch
operator|.
name|ElasticsearchIllegalStateException
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
name|AtomicFieldData
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
name|BytesValues
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

begin_comment
comment|/**  * {@link AtomicFieldData} impl based on Lucene's {@link SortedSetDocValues}.  *<p><b>Implementation note</b>: Lucene's ordinal for unset values is -1 whereas Elasticsearch's is 0, this is why there are all  * these +1 to translate from Lucene's ordinals to ES's.  */
end_comment

begin_class
DECL|class|SortedSetDVAtomicFieldData
specifier|abstract
class|class
name|SortedSetDVAtomicFieldData
block|{
DECL|field|reader
specifier|private
specifier|final
name|AtomicReader
name|reader
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|multiValued
specifier|private
specifier|final
name|boolean
name|multiValued
decl_stmt|;
DECL|field|valueCount
specifier|private
specifier|final
name|long
name|valueCount
decl_stmt|;
DECL|method|SortedSetDVAtomicFieldData
name|SortedSetDVAtomicFieldData
parameter_list|(
name|AtomicReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|SortedSetDocValues
name|dv
init|=
name|getValuesNoException
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|this
operator|.
name|multiValued
operator|=
name|DocValues
operator|.
name|unwrapSingleton
argument_list|(
name|dv
argument_list|)
operator|==
literal|null
expr_stmt|;
name|this
operator|.
name|valueCount
operator|=
name|dv
operator|.
name|getValueCount
argument_list|()
expr_stmt|;
block|}
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
name|multiValued
return|;
block|}
DECL|method|getNumberUniqueValues
specifier|public
name|long
name|getNumberUniqueValues
parameter_list|()
block|{
return|return
name|valueCount
return|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
comment|// There is no API to access memory usage per-field and RamUsageEstimator can't help since there are often references
comment|// from a per-field instance to all other instances handled by the same format
return|return
operator|-
literal|1L
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// no-op
block|}
DECL|method|getBytesValues
specifier|public
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|BytesValues
operator|.
name|WithOrdinals
name|getBytesValues
parameter_list|()
block|{
specifier|final
name|SortedSetDocValues
name|values
init|=
name|getValuesNoException
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|instanceof
name|RandomAccessOrds
condition|)
block|{
return|return
operator|new
name|RandomAccessSortedSetValues
argument_list|(
operator|(
name|RandomAccessOrds
operator|)
name|values
argument_list|,
name|multiValued
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|SortedSetValues
argument_list|(
name|values
argument_list|,
name|multiValued
argument_list|)
return|;
block|}
block|}
DECL|method|getTermsEnum
specifier|public
name|TermsEnum
name|getTermsEnum
parameter_list|()
block|{
return|return
name|getValuesNoException
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
operator|.
name|termsEnum
argument_list|()
return|;
block|}
DECL|method|getValuesNoException
specifier|private
specifier|static
name|SortedSetDocValues
name|getValuesNoException
parameter_list|(
name|AtomicReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
block|{
try|try
block|{
return|return
name|DocValues
operator|.
name|getSortedSet
argument_list|(
name|reader
argument_list|,
name|field
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
name|ElasticsearchIllegalStateException
argument_list|(
literal|"Couldn't load doc values"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|class|RandomAccessSortedSetValues
specifier|private
specifier|final
specifier|static
class|class
name|RandomAccessSortedSetValues
extends|extends
name|BytesValues
operator|.
name|WithOrdinals
block|{
DECL|field|values
specifier|private
specifier|final
name|RandomAccessOrds
name|values
decl_stmt|;
DECL|field|index
specifier|private
name|int
name|index
init|=
literal|0
decl_stmt|;
DECL|method|RandomAccessSortedSetValues
name|RandomAccessSortedSetValues
parameter_list|(
name|RandomAccessOrds
name|values
parameter_list|,
name|boolean
name|multiValued
parameter_list|)
block|{
name|super
argument_list|(
name|multiValued
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMaxOrd
specifier|public
name|long
name|getMaxOrd
parameter_list|()
block|{
return|return
name|values
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
name|long
name|getOrd
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
return|return
name|values
operator|.
name|nextOrd
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextOrd
specifier|public
name|long
name|nextOrd
parameter_list|()
block|{
return|return
name|values
operator|.
name|ordAt
argument_list|(
name|index
operator|++
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueByOrd
specifier|public
name|BytesRef
name|getValueByOrd
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
return|return
name|values
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|int
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|index
operator|=
literal|0
expr_stmt|;
return|return
name|values
operator|.
name|cardinality
argument_list|()
return|;
block|}
block|}
DECL|class|SortedSetValues
specifier|private
specifier|final
specifier|static
class|class
name|SortedSetValues
extends|extends
name|BytesValues
operator|.
name|WithOrdinals
block|{
DECL|field|values
specifier|private
specifier|final
name|SortedSetDocValues
name|values
decl_stmt|;
DECL|field|ords
specifier|private
name|long
index|[]
name|ords
decl_stmt|;
DECL|field|ordIndex
specifier|private
name|int
name|ordIndex
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|method|SortedSetValues
name|SortedSetValues
parameter_list|(
name|SortedSetDocValues
name|values
parameter_list|,
name|boolean
name|multiValued
parameter_list|)
block|{
name|super
argument_list|(
name|multiValued
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|ords
operator|=
operator|new
name|long
index|[
literal|0
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMaxOrd
specifier|public
name|long
name|getMaxOrd
parameter_list|()
block|{
return|return
name|values
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
name|long
name|getOrd
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
return|return
name|values
operator|.
name|nextOrd
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextOrd
specifier|public
name|long
name|nextOrd
parameter_list|()
block|{
assert|assert
name|ordIndex
operator|<
name|ords
operator|.
name|length
assert|;
return|return
name|ords
index|[
name|ordIndex
operator|++
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|int
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
comment|// For now, we consume all ords and pass them to the iter instead of doing it in a streaming way because Lucene's
comment|// SORTED_SET doc values are cached per thread, you can't have a fully independent instance
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|long
name|ord
init|=
name|values
operator|.
name|nextOrd
argument_list|()
init|;
name|ord
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|;
name|ord
operator|=
name|values
operator|.
name|nextOrd
argument_list|()
control|)
block|{
name|ords
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|ords
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|ords
index|[
name|i
operator|++
index|]
operator|=
name|ord
expr_stmt|;
block|}
name|ordIndex
operator|=
literal|0
expr_stmt|;
return|return
name|i
return|;
block|}
annotation|@
name|Override
DECL|method|getValueByOrd
specifier|public
name|BytesRef
name|getValueByOrd
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
return|return
name|values
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

