begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.field.data.doubles
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|field
operator|.
name|data
operator|.
name|doubles
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|RamUsage
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
name|thread
operator|.
name|ThreadLocals
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|SingleValueDoubleFieldData
specifier|public
class|class
name|SingleValueDoubleFieldData
extends|extends
name|DoubleFieldData
block|{
DECL|field|valuesCache
specifier|private
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|double
index|[]
argument_list|>
argument_list|>
name|valuesCache
init|=
operator|new
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|double
index|[]
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|double
index|[]
argument_list|>
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|double
index|[]
argument_list|>
argument_list|(
operator|new
name|double
index|[
literal|1
index|]
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|// order with value 0 indicates no value
DECL|field|ordinals
specifier|private
specifier|final
name|int
index|[]
name|ordinals
decl_stmt|;
DECL|method|SingleValueDoubleFieldData
specifier|public
name|SingleValueDoubleFieldData
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
index|[]
name|ordinals
parameter_list|,
name|double
index|[]
name|values
parameter_list|)
block|{
name|super
argument_list|(
name|fieldName
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|this
operator|.
name|ordinals
operator|=
name|ordinals
expr_stmt|;
block|}
DECL|method|computeSizeInBytes
annotation|@
name|Override
specifier|protected
name|long
name|computeSizeInBytes
parameter_list|()
block|{
return|return
name|super
operator|.
name|computeSizeInBytes
argument_list|()
operator|+
name|RamUsage
operator|.
name|NUM_BYTES_INT
operator|*
name|ordinals
operator|.
name|length
operator|+
name|RamUsage
operator|.
name|NUM_BYTES_ARRAY_HEADER
return|;
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
name|ordinals
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
name|ordinals
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
name|docId
argument_list|,
name|Double
operator|.
name|toString
argument_list|(
name|values
index|[
name|loc
index|]
argument_list|)
argument_list|)
expr_stmt|;
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
name|DoubleValueInDocProc
name|proc
parameter_list|)
block|{
name|int
name|loc
init|=
name|ordinals
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
name|docId
argument_list|,
name|values
index|[
name|loc
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|doubleValues
annotation|@
name|Override
specifier|public
name|double
index|[]
name|doubleValues
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|values
argument_list|(
name|docId
argument_list|)
return|;
block|}
DECL|method|value
annotation|@
name|Override
specifier|public
name|double
name|value
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|values
index|[
name|ordinals
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
name|double
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
name|ordinals
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
name|EMPTY_DOUBLE_ARRAY
return|;
block|}
name|double
index|[]
name|ret
init|=
name|valuesCache
operator|.
name|get
argument_list|()
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

