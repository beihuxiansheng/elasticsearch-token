begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.fieldcomparator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|fieldcomparator
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
name|search
operator|.
name|FieldComparator
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
name|index
operator|.
name|fielddata
operator|.
name|BytesValues
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
name|util
operator|.
name|BytesRefArrayRef
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
comment|/**  * Sorts by field's natural Term sort order.  All  * comparisons are done using BytesRef.compareTo, which is  * slow for medium to large result sets but possibly  * very fast for very small results sets.  */
end_comment

begin_class
DECL|class|BytesRefValComparator
specifier|public
specifier|final
class|class
name|BytesRefValComparator
extends|extends
name|FieldComparator
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|indexFieldData
specifier|private
specifier|final
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|indexFieldData
decl_stmt|;
DECL|field|reversed
specifier|private
specifier|final
name|boolean
name|reversed
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|BytesRef
index|[]
name|values
decl_stmt|;
DECL|field|bottom
specifier|private
name|BytesRef
name|bottom
decl_stmt|;
DECL|field|docTerms
specifier|private
name|BytesValues
name|docTerms
decl_stmt|;
DECL|method|BytesRefValComparator
name|BytesRefValComparator
parameter_list|(
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|indexFieldData
parameter_list|,
name|int
name|numHits
parameter_list|,
name|boolean
name|reversed
parameter_list|)
block|{
name|this
operator|.
name|reversed
operator|=
name|reversed
expr_stmt|;
name|values
operator|=
operator|new
name|BytesRef
index|[
name|numHits
index|]
expr_stmt|;
name|this
operator|.
name|indexFieldData
operator|=
name|indexFieldData
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slot1
parameter_list|,
name|int
name|slot2
parameter_list|)
block|{
specifier|final
name|BytesRef
name|val1
init|=
name|values
index|[
name|slot1
index|]
decl_stmt|;
specifier|final
name|BytesRef
name|val2
init|=
name|values
index|[
name|slot2
index|]
decl_stmt|;
return|return
name|compareValues
argument_list|(
name|val1
argument_list|,
name|val2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BytesRef
name|val2
init|=
name|docTerms
operator|.
name|getValue
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|compareValues
argument_list|(
name|bottom
argument_list|,
name|val2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|values
index|[
name|slot
index|]
operator|==
literal|null
condition|)
block|{
name|values
index|[
name|slot
index|]
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
name|docTerms
operator|.
name|getValueScratch
argument_list|(
name|doc
argument_list|,
name|values
index|[
name|slot
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|FieldComparator
argument_list|<
name|BytesRef
argument_list|>
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|docTerms
operator|=
name|indexFieldData
operator|.
name|load
argument_list|(
name|context
argument_list|)
operator|.
name|getBytesValues
argument_list|()
expr_stmt|;
if|if
condition|(
name|docTerms
operator|.
name|isMultiValued
argument_list|()
condition|)
block|{
name|docTerms
operator|=
operator|new
name|MultiValuedBytesWrapper
argument_list|(
name|docTerms
argument_list|,
name|reversed
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
specifier|final
name|int
name|bottom
parameter_list|)
block|{
name|this
operator|.
name|bottom
operator|=
name|values
index|[
name|bottom
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|BytesRef
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|values
index|[
name|slot
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|compareValues
specifier|public
name|int
name|compareValues
parameter_list|(
name|BytesRef
name|val1
parameter_list|,
name|BytesRef
name|val2
parameter_list|)
block|{
if|if
condition|(
name|val1
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|val2
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|val2
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
name|val1
operator|.
name|compareTo
argument_list|(
name|val2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareDocToValue
specifier|public
name|int
name|compareDocToValue
parameter_list|(
name|int
name|doc
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
return|return
name|docTerms
operator|.
name|getValue
argument_list|(
name|doc
argument_list|)
operator|.
name|compareTo
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|class|FilteredByteValues
specifier|public
specifier|static
class|class
name|FilteredByteValues
implements|implements
name|BytesValues
block|{
DECL|field|delegate
specifier|protected
specifier|final
name|BytesValues
name|delegate
decl_stmt|;
DECL|method|FilteredByteValues
specifier|public
name|FilteredByteValues
parameter_list|(
name|BytesValues
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|isMultiValued
argument_list|()
return|;
block|}
DECL|method|hasValue
specifier|public
name|boolean
name|hasValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|hasValue
argument_list|(
name|docId
argument_list|)
return|;
block|}
DECL|method|getValue
specifier|public
name|BytesRef
name|getValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getValue
argument_list|(
name|docId
argument_list|)
return|;
block|}
DECL|method|makeSafe
specifier|public
name|BytesRef
name|makeSafe
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|makeSafe
argument_list|(
name|bytes
argument_list|)
return|;
block|}
DECL|method|getValueScratch
specifier|public
name|BytesRef
name|getValueScratch
parameter_list|(
name|int
name|docId
parameter_list|,
name|BytesRef
name|ret
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getValueScratch
argument_list|(
name|docId
argument_list|,
name|ret
argument_list|)
return|;
block|}
DECL|method|getValues
specifier|public
name|BytesRefArrayRef
name|getValues
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getValues
argument_list|(
name|docId
argument_list|)
return|;
block|}
DECL|method|getIter
specifier|public
name|Iter
name|getIter
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getIter
argument_list|(
name|docId
argument_list|)
return|;
block|}
DECL|method|forEachValueInDoc
specifier|public
name|void
name|forEachValueInDoc
parameter_list|(
name|int
name|docId
parameter_list|,
name|ValueInDocProc
name|proc
parameter_list|)
block|{
name|delegate
operator|.
name|forEachValueInDoc
argument_list|(
name|docId
argument_list|,
name|proc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MultiValuedBytesWrapper
specifier|private
specifier|static
specifier|final
class|class
name|MultiValuedBytesWrapper
extends|extends
name|FilteredByteValues
block|{
DECL|field|reversed
specifier|private
specifier|final
name|boolean
name|reversed
decl_stmt|;
DECL|method|MultiValuedBytesWrapper
specifier|public
name|MultiValuedBytesWrapper
parameter_list|(
name|BytesValues
name|delegate
parameter_list|,
name|boolean
name|reversed
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
name|this
operator|.
name|reversed
operator|=
name|reversed
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueScratch
specifier|public
name|BytesRef
name|getValueScratch
parameter_list|(
name|int
name|docId
parameter_list|,
name|BytesRef
name|scratch
parameter_list|)
block|{
name|BytesValues
operator|.
name|Iter
name|iter
init|=
name|delegate
operator|.
name|getIter
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|BytesRef
name|currentVal
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|BytesRef
name|relevantVal
init|=
name|currentVal
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|cmp
init|=
name|currentVal
operator|.
name|compareTo
argument_list|(
name|relevantVal
argument_list|)
decl_stmt|;
if|if
condition|(
name|reversed
condition|)
block|{
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
name|relevantVal
operator|=
name|currentVal
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|relevantVal
operator|=
name|currentVal
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
break|break;
block|}
name|currentVal
operator|=
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|relevantVal
return|;
comment|/*if (reversed) {                 BytesRefArrayRef ref = readerValues.getValues(docId);                 if (ref.isEmpty()) {                     return null;                 } else {                     return ref.values[ref.end - 1]; // last element is the highest value.                 }             } else {                 return readerValues.getValue(docId); // returns the lowest value             }*/
block|}
block|}
block|}
end_class

end_unit

