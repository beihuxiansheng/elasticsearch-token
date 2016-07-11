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
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * A thread safe ordinals abstraction. Ordinals can only be positive integers.  */
end_comment

begin_class
DECL|class|Ordinals
specifier|public
specifier|abstract
class|class
name|Ordinals
implements|implements
name|Accountable
block|{
DECL|field|NO_VALUES
specifier|public
specifier|static
specifier|final
name|ValuesHolder
name|NO_VALUES
init|=
operator|new
name|ValuesHolder
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|BytesRef
name|lookupOrd
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
comment|/**      * The memory size this ordinals take.      */
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
specifier|abstract
name|long
name|ramBytesUsed
parameter_list|()
function_decl|;
DECL|method|ordinals
specifier|public
specifier|abstract
name|RandomAccessOrds
name|ordinals
parameter_list|(
name|ValuesHolder
name|values
parameter_list|)
function_decl|;
DECL|method|ordinals
specifier|public
specifier|final
name|RandomAccessOrds
name|ordinals
parameter_list|()
block|{
return|return
name|ordinals
argument_list|(
name|NO_VALUES
argument_list|)
return|;
block|}
DECL|interface|ValuesHolder
specifier|public
interface|interface
name|ValuesHolder
block|{
DECL|method|lookupOrd
name|BytesRef
name|lookupOrd
parameter_list|(
name|long
name|ord
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

