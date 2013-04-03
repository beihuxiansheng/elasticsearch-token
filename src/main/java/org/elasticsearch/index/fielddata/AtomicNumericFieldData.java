begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|AtomicNumericFieldData
specifier|public
specifier|abstract
class|class
name|AtomicNumericFieldData
implements|implements
name|AtomicFieldData
argument_list|<
name|ScriptDocValues
argument_list|>
block|{
DECL|field|isFloat
specifier|private
name|boolean
name|isFloat
decl_stmt|;
DECL|method|AtomicNumericFieldData
specifier|public
name|AtomicNumericFieldData
parameter_list|(
name|boolean
name|isFloat
parameter_list|)
block|{
name|this
operator|.
name|isFloat
operator|=
name|isFloat
expr_stmt|;
block|}
DECL|method|getLongValues
specifier|public
specifier|abstract
name|LongValues
name|getLongValues
parameter_list|()
function_decl|;
DECL|method|getDoubleValues
specifier|public
specifier|abstract
name|DoubleValues
name|getDoubleValues
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|getScriptValues
specifier|public
name|ScriptDocValues
name|getScriptValues
parameter_list|()
block|{
if|if
condition|(
name|isFloat
condition|)
block|{
return|return
operator|new
name|ScriptDocValues
operator|.
name|NumericDouble
argument_list|(
name|getDoubleValues
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ScriptDocValues
operator|.
name|NumericLong
argument_list|(
name|getLongValues
argument_list|()
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getBytesValues
specifier|public
name|BytesValues
name|getBytesValues
parameter_list|()
block|{
if|if
condition|(
name|isFloat
condition|)
block|{
specifier|final
name|DoubleValues
name|values
init|=
name|getDoubleValues
argument_list|()
decl_stmt|;
return|return
operator|new
name|BytesValues
argument_list|(
name|values
operator|.
name|isMultiValued
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|values
operator|.
name|hasValue
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
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
if|if
condition|(
name|values
operator|.
name|hasValue
argument_list|(
name|docId
argument_list|)
condition|)
block|{
name|ret
operator|.
name|copyChars
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|values
operator|.
name|getValue
argument_list|(
name|docId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ret
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iter
name|getIter
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
specifier|final
name|DoubleValues
operator|.
name|Iter
name|iter
init|=
name|values
operator|.
name|getIter
argument_list|(
name|docId
argument_list|)
decl_stmt|;
return|return
operator|new
name|BytesValues
operator|.
name|Iter
argument_list|()
block|{
specifier|private
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
name|spare
operator|.
name|copyChars
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|spare
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hash
parameter_list|()
block|{
return|return
name|spare
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
else|else
block|{
specifier|final
name|LongValues
name|values
init|=
name|getLongValues
argument_list|()
decl_stmt|;
return|return
operator|new
name|BytesValues
argument_list|(
name|values
operator|.
name|isMultiValued
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|values
operator|.
name|hasValue
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
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
if|if
condition|(
name|values
operator|.
name|hasValue
argument_list|(
name|docId
argument_list|)
condition|)
block|{
name|ret
operator|.
name|copyChars
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|values
operator|.
name|getValue
argument_list|(
name|docId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ret
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iter
name|getIter
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
specifier|final
name|LongValues
operator|.
name|Iter
name|iter
init|=
name|values
operator|.
name|getIter
argument_list|(
name|docId
argument_list|)
decl_stmt|;
return|return
operator|new
name|BytesValues
operator|.
name|Iter
argument_list|()
block|{
specifier|private
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
name|spare
operator|.
name|copyChars
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|spare
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hash
parameter_list|()
block|{
return|return
name|spare
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getHashedBytesValues
specifier|public
name|BytesValues
name|getHashedBytesValues
parameter_list|()
block|{
return|return
name|getBytesValues
argument_list|()
return|;
block|}
block|}
end_class

end_unit

