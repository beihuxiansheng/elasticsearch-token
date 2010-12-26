begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.field.data.longs
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
name|longs
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
name|joda
operator|.
name|time
operator|.
name|DateTimeZone
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
name|joda
operator|.
name|time
operator|.
name|MutableDateTime
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|MultiValueLongFieldData
specifier|public
class|class
name|MultiValueLongFieldData
extends|extends
name|LongFieldData
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
DECL|field|dateTimesCache
specifier|private
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|MutableDateTime
index|[]
index|[]
argument_list|>
argument_list|>
name|dateTimesCache
init|=
operator|new
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|MutableDateTime
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
name|MutableDateTime
index|[]
index|[]
argument_list|>
name|initialValue
parameter_list|()
block|{
name|MutableDateTime
index|[]
index|[]
name|value
init|=
operator|new
name|MutableDateTime
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
name|MutableDateTime
index|[
name|i
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|i
condition|;
name|j
operator|++
control|)
block|{
name|value
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
operator|new
name|MutableDateTime
argument_list|(
name|DateTimeZone
operator|.
name|UTC
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|MutableDateTime
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
name|long
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
name|long
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
name|long
index|[]
index|[]
argument_list|>
name|initialValue
parameter_list|()
block|{
name|long
index|[]
index|[]
name|value
init|=
operator|new
name|long
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
name|long
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
name|long
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
DECL|method|MultiValueLongFieldData
specifier|public
name|MultiValueLongFieldData
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
index|[]
index|[]
name|ordinals
parameter_list|,
name|long
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
DECL|method|multiValued
annotation|@
name|Override
specifier|public
name|boolean
name|multiValued
parameter_list|()
block|{
return|return
literal|true
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
literal|null
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
index|[]
name|docOrders
init|=
name|ordinals
index|[
name|docId
index|]
decl_stmt|;
if|if
condition|(
name|docOrders
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|int
name|docOrder
range|:
name|docOrders
control|)
block|{
name|proc
operator|.
name|onValue
argument_list|(
name|docId
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|values
index|[
name|docOrder
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
index|[]
name|docOrders
init|=
name|ordinals
index|[
name|docId
index|]
decl_stmt|;
if|if
condition|(
name|docOrders
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|int
name|docOrder
range|:
name|docOrders
control|)
block|{
name|proc
operator|.
name|onValue
argument_list|(
name|docId
argument_list|,
name|values
index|[
name|docOrder
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|dates
annotation|@
name|Override
specifier|public
name|MutableDateTime
index|[]
name|dates
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|int
index|[]
name|docOrders
init|=
name|ordinals
index|[
name|docId
index|]
decl_stmt|;
if|if
condition|(
name|docOrders
operator|==
literal|null
condition|)
block|{
return|return
name|EMPTY_DATETIME_ARRAY
return|;
block|}
name|MutableDateTime
index|[]
name|dates
decl_stmt|;
if|if
condition|(
name|docOrders
operator|.
name|length
operator|<
name|VALUE_CACHE_SIZE
condition|)
block|{
name|dates
operator|=
name|dateTimesCache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
index|[
name|docOrders
operator|.
name|length
index|]
expr_stmt|;
block|}
else|else
block|{
name|dates
operator|=
operator|new
name|MutableDateTime
index|[
name|docOrders
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dates
index|[
name|i
index|]
operator|=
operator|new
name|MutableDateTime
argument_list|()
expr_stmt|;
block|}
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
name|docOrders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dates
index|[
name|i
index|]
operator|.
name|setMillis
argument_list|(
name|values
index|[
name|docOrders
index|[
name|i
index|]
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|dates
return|;
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
name|int
index|[]
name|docOrders
init|=
name|ordinals
index|[
name|docId
index|]
decl_stmt|;
if|if
condition|(
name|docOrders
operator|==
literal|null
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
name|docOrders
operator|.
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
name|docOrders
operator|.
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
name|docOrders
operator|.
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
name|docOrders
operator|.
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
name|docOrders
index|[
name|i
index|]
index|]
expr_stmt|;
block|}
return|return
name|doubles
return|;
block|}
DECL|method|value
annotation|@
name|Override
specifier|public
name|long
name|value
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|int
index|[]
name|docOrders
init|=
name|ordinals
index|[
name|docId
index|]
decl_stmt|;
if|if
condition|(
name|docOrders
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|values
index|[
name|docOrders
index|[
literal|0
index|]
index|]
return|;
block|}
DECL|method|values
annotation|@
name|Override
specifier|public
name|long
index|[]
name|values
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|int
index|[]
name|docOrders
init|=
name|ordinals
index|[
name|docId
index|]
decl_stmt|;
if|if
condition|(
name|docOrders
operator|==
literal|null
condition|)
block|{
return|return
name|EMPTY_LONG_ARRAY
return|;
block|}
name|long
index|[]
name|longs
decl_stmt|;
if|if
condition|(
name|docOrders
operator|.
name|length
operator|<
name|VALUE_CACHE_SIZE
condition|)
block|{
name|longs
operator|=
name|valuesCache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
index|[
name|docOrders
operator|.
name|length
index|]
expr_stmt|;
block|}
else|else
block|{
name|longs
operator|=
operator|new
name|long
index|[
name|docOrders
operator|.
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
name|docOrders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|longs
index|[
name|i
index|]
operator|=
name|values
index|[
name|docOrders
index|[
name|i
index|]
index|]
expr_stmt|;
block|}
return|return
name|longs
return|;
block|}
block|}
end_class

end_unit

