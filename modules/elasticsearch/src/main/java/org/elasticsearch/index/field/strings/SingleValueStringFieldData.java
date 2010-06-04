begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.field.strings
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|field
operator|.
name|strings
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|field
operator|.
name|FieldDataOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|Strings
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|SingleValueStringFieldData
specifier|public
class|class
name|SingleValueStringFieldData
extends|extends
name|StringFieldData
block|{
DECL|field|valuesCache
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|String
index|[]
argument_list|>
name|valuesCache
init|=
operator|new
name|ThreadLocal
argument_list|<
name|String
index|[]
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|String
index|[]
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|String
index|[
literal|1
index|]
return|;
block|}
block|}
decl_stmt|;
comment|// order with value 0 indicates no value
DECL|field|order
specifier|private
specifier|final
name|int
index|[]
name|order
decl_stmt|;
DECL|method|SingleValueStringFieldData
specifier|public
name|SingleValueStringFieldData
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|FieldDataOptions
name|options
parameter_list|,
name|int
index|[]
name|order
parameter_list|,
name|String
index|[]
name|values
parameter_list|,
name|int
index|[]
name|freqs
parameter_list|)
block|{
name|super
argument_list|(
name|fieldName
argument_list|,
name|options
argument_list|,
name|values
argument_list|,
name|freqs
argument_list|)
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
block|}
DECL|method|multiValued
annotation|@
name|Override
specifier|public
name|boolean
name|multiValued
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|hasValue
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
name|order
index|[
name|docId
index|]
operator|!=
literal|0
return|;
block|}
DECL|method|forEachValueInDoc
annotation|@
name|Override
specifier|public
name|void
name|forEachValueInDoc
parameter_list|(
name|int
name|docId
parameter_list|,
name|StringValueInDocProc
name|proc
parameter_list|)
block|{
name|int
name|loc
init|=
name|order
index|[
name|docId
index|]
decl_stmt|;
if|if
condition|(
name|loc
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|proc
operator|.
name|onValue
argument_list|(
name|values
index|[
name|loc
index|]
argument_list|,
name|docId
argument_list|)
expr_stmt|;
block|}
DECL|method|value
annotation|@
name|Override
specifier|public
name|String
name|value
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|values
index|[
name|order
index|[
name|docId
index|]
index|]
return|;
block|}
DECL|method|values
annotation|@
name|Override
specifier|public
name|String
index|[]
name|values
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|int
name|loc
init|=
name|order
index|[
name|docId
index|]
decl_stmt|;
if|if
condition|(
name|loc
operator|==
literal|0
condition|)
block|{
return|return
name|Strings
operator|.
name|EMPTY_ARRAY
return|;
block|}
name|String
index|[]
name|ret
init|=
name|valuesCache
operator|.
name|get
argument_list|()
decl_stmt|;
name|ret
index|[
literal|0
index|]
operator|=
name|values
index|[
name|loc
index|]
expr_stmt|;
return|return
name|ret
return|;
block|}
block|}
end_class

end_unit

