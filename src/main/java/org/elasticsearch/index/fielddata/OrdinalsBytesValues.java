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
name|Ordinals
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_interface
DECL|interface|OrdinalsBytesValues
specifier|public
interface|interface
name|OrdinalsBytesValues
extends|extends
name|BytesValues
block|{
DECL|method|ordinals
name|Ordinals
operator|.
name|Docs
name|ordinals
parameter_list|()
function_decl|;
DECL|method|getValueByOrd
name|BytesRef
name|getValueByOrd
parameter_list|(
name|int
name|ord
parameter_list|)
function_decl|;
comment|/**      * Returns the bytes value for the docId, with the provided "ret" which will be filled with the      * result which will also be returned. If there is no value for this docId, the length will be 0.      * Note, the bytes are not "safe".      */
DECL|method|getValueScratchByOrd
name|BytesRef
name|getValueScratchByOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|ret
parameter_list|)
function_decl|;
DECL|method|getSafeValueByOrd
name|BytesRef
name|getSafeValueByOrd
parameter_list|(
name|int
name|ord
parameter_list|)
function_decl|;
DECL|class|StringBased
specifier|public
specifier|static
class|class
name|StringBased
extends|extends
name|BytesValues
operator|.
name|StringBased
implements|implements
name|OrdinalsBytesValues
block|{
DECL|field|values
specifier|private
specifier|final
name|OrdinalsStringValues
name|values
decl_stmt|;
DECL|method|StringBased
specifier|public
name|StringBased
parameter_list|(
name|OrdinalsStringValues
name|values
parameter_list|)
block|{
name|super
argument_list|(
name|values
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
DECL|method|ordinals
specifier|public
name|Ordinals
operator|.
name|Docs
name|ordinals
parameter_list|()
block|{
return|return
name|values
operator|.
name|ordinals
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getValueByOrd
specifier|public
name|BytesRef
name|getValueByOrd
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
name|scratch
operator|.
name|copyChars
argument_list|(
name|values
operator|.
name|getValueByOrd
argument_list|(
name|ord
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|scratch
return|;
block|}
annotation|@
name|Override
DECL|method|getValueScratchByOrd
specifier|public
name|BytesRef
name|getValueScratchByOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|ret
parameter_list|)
block|{
name|ret
operator|.
name|copyChars
argument_list|(
name|values
operator|.
name|getValueByOrd
argument_list|(
name|ord
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|getSafeValueByOrd
specifier|public
name|BytesRef
name|getSafeValueByOrd
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
return|return
operator|new
name|BytesRef
argument_list|(
name|values
operator|.
name|getValueByOrd
argument_list|(
name|ord
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_interface

end_unit

