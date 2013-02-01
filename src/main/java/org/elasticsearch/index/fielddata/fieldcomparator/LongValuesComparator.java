begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|IndexNumericFieldData
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
name|LongValues
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
name|LongArrayRef
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
comment|/**  */
end_comment

begin_class
DECL|class|LongValuesComparator
specifier|public
class|class
name|LongValuesComparator
extends|extends
name|FieldComparator
argument_list|<
name|Long
argument_list|>
block|{
DECL|field|indexFieldData
specifier|private
specifier|final
name|IndexNumericFieldData
name|indexFieldData
decl_stmt|;
DECL|field|missingValue
specifier|private
specifier|final
name|long
name|missingValue
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
name|long
index|[]
name|values
decl_stmt|;
DECL|field|readerValues
specifier|private
name|LongValues
name|readerValues
decl_stmt|;
DECL|field|bottom
specifier|private
name|long
name|bottom
decl_stmt|;
DECL|method|LongValuesComparator
specifier|public
name|LongValuesComparator
parameter_list|(
name|IndexNumericFieldData
name|indexFieldData
parameter_list|,
name|long
name|missingValue
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
name|indexFieldData
operator|=
name|indexFieldData
expr_stmt|;
name|this
operator|.
name|missingValue
operator|=
name|missingValue
expr_stmt|;
name|this
operator|.
name|values
operator|=
operator|new
name|long
index|[
name|numHits
index|]
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
name|long
name|v1
init|=
name|values
index|[
name|slot1
index|]
decl_stmt|;
specifier|final
name|long
name|v2
init|=
name|values
index|[
name|slot2
index|]
decl_stmt|;
if|if
condition|(
name|v1
operator|>
name|v2
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|v1
operator|<
name|v2
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
name|this
operator|.
name|bottom
operator|=
name|values
index|[
name|slot
index|]
expr_stmt|;
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
name|long
name|v2
init|=
name|readerValues
operator|.
name|getValueMissing
argument_list|(
name|doc
argument_list|,
name|missingValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|bottom
operator|>
name|v2
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|bottom
operator|<
name|v2
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
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
name|values
index|[
name|slot
index|]
operator|=
name|readerValues
operator|.
name|getValueMissing
argument_list|(
name|doc
argument_list|,
name|missingValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|FieldComparator
argument_list|<
name|Long
argument_list|>
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|readerValues
operator|=
name|indexFieldData
operator|.
name|load
argument_list|(
name|context
argument_list|)
operator|.
name|getLongValues
argument_list|()
expr_stmt|;
if|if
condition|(
name|readerValues
operator|.
name|isMultiValued
argument_list|()
condition|)
block|{
name|readerValues
operator|=
operator|new
name|MultiValuedBytesWrapper
argument_list|(
name|readerValues
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
DECL|method|value
specifier|public
name|Long
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|values
index|[
name|slot
index|]
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
name|Long
name|valueObj
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|long
name|value
init|=
name|valueObj
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|long
name|docValue
init|=
name|readerValues
operator|.
name|getValueMissing
argument_list|(
name|doc
argument_list|,
name|missingValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|docValue
operator|<
name|value
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|docValue
operator|>
name|value
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
comment|// THIS SHOULD GO INTO the fielddata package
DECL|class|FilteredByteValues
specifier|public
specifier|static
class|class
name|FilteredByteValues
implements|implements
name|LongValues
block|{
DECL|field|delegate
specifier|protected
specifier|final
name|LongValues
name|delegate
decl_stmt|;
DECL|method|FilteredByteValues
specifier|public
name|FilteredByteValues
parameter_list|(
name|LongValues
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
name|long
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
DECL|method|getValueMissing
specifier|public
name|long
name|getValueMissing
parameter_list|(
name|int
name|docId
parameter_list|,
name|long
name|missingValue
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getValueMissing
argument_list|(
name|docId
argument_list|,
name|missingValue
argument_list|)
return|;
block|}
DECL|method|getValues
specifier|public
name|LongArrayRef
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
name|LongValues
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
DECL|method|getValueMissing
specifier|public
name|long
name|getValueMissing
parameter_list|(
name|int
name|docId
parameter_list|,
name|long
name|missing
parameter_list|)
block|{
name|LongValues
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
name|missing
return|;
block|}
name|long
name|currentVal
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|long
name|relevantVal
init|=
name|currentVal
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|reversed
condition|)
block|{
if|if
condition|(
name|currentVal
operator|>
name|relevantVal
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
name|currentVal
operator|<
name|relevantVal
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
comment|// If we have a method on readerValues that tells if the values emitted by Iter or ArrayRef are sorted per
comment|// document that we can do this or something similar:
comment|// (This is already possible, if values are loaded from index, but we just need a method that tells us this
comment|// For example a impl that read values from the _source field might not read values in order)
comment|/*if (reversed) {                 // Would be nice if there is a way to get highest value from LongValues. The values are sorted anyway.                 LongArrayRef ref = readerValues.getValues(doc);                 if (ref.isEmpty()) {                     return missing;                 } else {                     return ref.values[ref.end - 1]; // last element is the highest value.                 }             } else {                 return readerValues.getValueMissing(doc, missing); // returns lowest             }*/
block|}
block|}
block|}
end_class

end_unit

