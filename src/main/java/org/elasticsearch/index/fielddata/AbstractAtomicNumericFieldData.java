begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
DECL|class|AbstractAtomicNumericFieldData
specifier|public
specifier|abstract
class|class
name|AbstractAtomicNumericFieldData
implements|implements
name|AtomicNumericFieldData
block|{
DECL|field|isFloat
specifier|private
name|boolean
name|isFloat
decl_stmt|;
DECL|method|AbstractAtomicNumericFieldData
specifier|public
name|AbstractAtomicNumericFieldData
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
annotation|@
name|Override
DECL|method|isValuesOrdered
specifier|public
name|boolean
name|isValuesOrdered
parameter_list|()
block|{
return|return
literal|false
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
if|if
condition|(
name|isFloat
condition|)
block|{
return|return
operator|new
name|ScriptDocValues
operator|.
name|Doubles
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
name|Longs
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
parameter_list|(
name|boolean
name|needsHashes
parameter_list|)
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
name|int
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|this
operator|.
name|docId
operator|=
name|docId
expr_stmt|;
return|return
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|nextValue
parameter_list|()
block|{
name|scratch
operator|.
name|copyChars
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|values
operator|.
name|nextValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|scratch
return|;
block|}
annotation|@
name|Override
specifier|public
name|Order
name|getOrder
parameter_list|()
block|{
return|return
name|values
operator|.
name|getOrder
argument_list|()
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
name|int
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|this
operator|.
name|docId
operator|=
name|docId
expr_stmt|;
return|return
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|nextValue
parameter_list|()
block|{
name|scratch
operator|.
name|copyChars
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|values
operator|.
name|nextValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|scratch
return|;
block|}
annotation|@
name|Override
specifier|public
name|Order
name|getOrder
parameter_list|()
block|{
return|return
name|values
operator|.
name|getOrder
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

