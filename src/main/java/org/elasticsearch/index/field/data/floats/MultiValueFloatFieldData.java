begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.field.data.floats
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
name|floats
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadLocals
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
name|field
operator|.
name|data
operator|.
name|doubles
operator|.
name|DoubleFieldData
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|MultiValueFloatFieldData
specifier|public
class|class
name|MultiValueFloatFieldData
extends|extends
name|FloatFieldData
block|{
DECL|field|VALUE_CACHE_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|VALUE_CACHE_SIZE
init|=
literal|10
decl_stmt|;
DECL|field|doublesValuesCache
specifier|private
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|double
index|[]
index|[]
argument_list|>
argument_list|>
name|doublesValuesCache
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
index|[]
argument_list|>
name|initialValue
parameter_list|()
block|{
name|double
index|[]
index|[]
name|value
init|=
operator|new
name|double
index|[
name|VALUE_CACHE_SIZE
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
name|value
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|value
index|[
name|i
index|]
operator|=
operator|new
name|double
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
operator|new
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|double
index|[]
index|[]
argument_list|>
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|valuesCache
specifier|private
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|float
index|[]
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
name|float
index|[]
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
name|float
index|[]
index|[]
argument_list|>
name|initialValue
parameter_list|()
block|{
name|float
index|[]
index|[]
name|value
init|=
operator|new
name|float
index|[
name|VALUE_CACHE_SIZE
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
name|value
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|value
index|[
name|i
index|]
operator|=
operator|new
name|float
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
operator|new
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|float
index|[]
index|[]
argument_list|>
argument_list|(
name|value
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
index|[]
name|ordinals
decl_stmt|;
DECL|method|MultiValueFloatFieldData
specifier|public
name|MultiValueFloatFieldData
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
index|[]
index|[]
name|ordinals
parameter_list|,
name|float
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
annotation|@
name|Override
DECL|method|computeSizeInBytes
specifier|protected
name|long
name|computeSizeInBytes
parameter_list|()
block|{
name|long
name|size
init|=
name|super
operator|.
name|computeSizeInBytes
argument_list|()
decl_stmt|;
name|size
operator|+=
name|RamUsage
operator|.
name|NUM_BYTES_ARRAY_HEADER
expr_stmt|;
comment|// for the top level array
for|for
control|(
name|int
index|[]
name|ordinal
range|:
name|ordinals
control|)
block|{
name|size
operator|+=
name|RamUsage
operator|.
name|NUM_BYTES_INT
operator|*
name|ordinal
operator|.
name|length
operator|+
name|RamUsage
operator|.
name|NUM_BYTES_ARRAY_HEADER
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|multiValued
specifier|public
name|boolean
name|multiValued
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hasValue
specifier|public
name|boolean
name|hasValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
for|for
control|(
name|int
index|[]
name|ordinal
range|:
name|ordinals
control|)
block|{
if|if
condition|(
name|ordinal
index|[
name|docId
index|]
operator|!=
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|forEachValueInDoc
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ordinals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|loc
init|=
name|ordinals
index|[
name|i
index|]
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
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|proc
operator|.
name|onMissing
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
name|proc
operator|.
name|onValue
argument_list|(
name|docId
argument_list|,
operator|new
name|BytesRef
argument_list|(
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
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|forEachValueInDoc
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
for|for
control|(
name|int
index|[]
name|ordinal
range|:
name|ordinals
control|)
block|{
name|int
name|loc
init|=
name|ordinal
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
break|break;
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
block|}
annotation|@
name|Override
DECL|method|forEachValueInDoc
specifier|public
name|void
name|forEachValueInDoc
parameter_list|(
name|int
name|docId
parameter_list|,
name|LongValueInDocProc
name|proc
parameter_list|)
block|{
for|for
control|(
name|int
index|[]
name|ordinal
range|:
name|ordinals
control|)
block|{
name|int
name|loc
init|=
name|ordinal
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
break|break;
block|}
name|proc
operator|.
name|onValue
argument_list|(
name|docId
argument_list|,
operator|(
name|long
operator|)
name|values
index|[
name|loc
index|]
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|forEachValueInDoc
specifier|public
name|void
name|forEachValueInDoc
parameter_list|(
name|int
name|docId
parameter_list|,
name|MissingDoubleValueInDocProc
name|proc
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ordinals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|loc
init|=
name|ordinals
index|[
name|i
index|]
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
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|proc
operator|.
name|onMissing
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
break|break;
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
block|}
annotation|@
name|Override
DECL|method|forEachValueInDoc
specifier|public
name|void
name|forEachValueInDoc
parameter_list|(
name|int
name|docId
parameter_list|,
name|MissingLongValueInDocProc
name|proc
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ordinals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|loc
init|=
name|ordinals
index|[
name|i
index|]
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
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|proc
operator|.
name|onMissing
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
name|proc
operator|.
name|onValue
argument_list|(
name|docId
argument_list|,
operator|(
name|long
operator|)
name|values
index|[
name|loc
index|]
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ordinals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|loc
init|=
name|ordinals
index|[
name|i
index|]
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
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|proc
operator|.
name|onMissing
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
break|break;
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
block|}
annotation|@
name|Override
DECL|method|forEachOrdinalInDoc
specifier|public
name|void
name|forEachOrdinalInDoc
parameter_list|(
name|int
name|docId
parameter_list|,
name|OrdinalInDocProc
name|proc
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ordinals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|loc
init|=
name|ordinals
index|[
name|i
index|]
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
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|proc
operator|.
name|onOrdinal
argument_list|(
name|docId
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
name|proc
operator|.
name|onOrdinal
argument_list|(
name|docId
argument_list|,
name|loc
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doubleValues
specifier|public
name|double
index|[]
name|doubleValues
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|int
name|length
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
index|[]
name|ordinal
range|:
name|ordinals
control|)
block|{
if|if
condition|(
name|ordinal
index|[
name|docId
index|]
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|length
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|DoubleFieldData
operator|.
name|EMPTY_DOUBLE_ARRAY
return|;
block|}
name|double
index|[]
name|doubles
decl_stmt|;
if|if
condition|(
name|length
operator|<
name|VALUE_CACHE_SIZE
condition|)
block|{
name|doubles
operator|=
name|doublesValuesCache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
index|[
name|length
index|]
expr_stmt|;
block|}
else|else
block|{
name|doubles
operator|=
operator|new
name|double
index|[
name|length
index|]
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|doubles
index|[
name|i
index|]
operator|=
name|values
index|[
name|ordinals
index|[
name|i
index|]
index|[
name|docId
index|]
index|]
expr_stmt|;
block|}
return|return
name|doubles
return|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|float
name|value
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
for|for
control|(
name|int
index|[]
name|ordinal
range|:
name|ordinals
control|)
block|{
name|int
name|loc
init|=
name|ordinal
index|[
name|docId
index|]
decl_stmt|;
if|if
condition|(
name|loc
operator|!=
literal|0
condition|)
block|{
return|return
name|values
index|[
name|loc
index|]
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|values
specifier|public
name|float
index|[]
name|values
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|int
name|length
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
index|[]
name|ordinal
range|:
name|ordinals
control|)
block|{
if|if
condition|(
name|ordinal
index|[
name|docId
index|]
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|length
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|EMPTY_FLOAT_ARRAY
return|;
block|}
name|float
index|[]
name|floats
decl_stmt|;
if|if
condition|(
name|length
operator|<
name|VALUE_CACHE_SIZE
condition|)
block|{
name|floats
operator|=
name|valuesCache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
index|[
name|length
index|]
expr_stmt|;
block|}
else|else
block|{
name|floats
operator|=
operator|new
name|float
index|[
name|length
index|]
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|floats
index|[
name|i
index|]
operator|=
name|values
index|[
name|ordinals
index|[
name|i
index|]
index|[
name|docId
index|]
index|]
expr_stmt|;
block|}
return|return
name|floats
return|;
block|}
block|}
end_class

end_unit

