begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.geo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|geo
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|geo
operator|.
name|GeoHashUtils
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SingleValueGeoPointFieldData
specifier|public
class|class
name|SingleValueGeoPointFieldData
extends|extends
name|GeoPointFieldData
block|{
DECL|field|valuesArrayCache
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|GeoPoint
index|[]
argument_list|>
argument_list|>
name|valuesArrayCache
init|=
operator|new
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|GeoPoint
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
name|GeoPoint
index|[]
argument_list|>
name|initialValue
parameter_list|()
block|{
name|GeoPoint
index|[]
name|value
init|=
operator|new
name|GeoPoint
index|[
literal|1
index|]
decl_stmt|;
name|value
index|[
literal|0
index|]
operator|=
operator|new
name|GeoPoint
argument_list|()
expr_stmt|;
return|return
operator|new
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|GeoPoint
index|[]
argument_list|>
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|valuesLatCache
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
name|valuesLatCache
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
DECL|field|valuesLonCache
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
name|valuesLonCache
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
DECL|method|SingleValueGeoPointFieldData
specifier|public
name|SingleValueGeoPointFieldData
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
name|lat
parameter_list|,
name|double
index|[]
name|lon
parameter_list|)
block|{
name|super
argument_list|(
name|fieldName
argument_list|,
name|lat
argument_list|,
name|lon
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
annotation|@
name|Override
DECL|method|multiValued
specifier|public
name|boolean
name|multiValued
parameter_list|()
block|{
return|return
literal|false
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
return|return
name|ordinals
index|[
name|docId
index|]
operator|!=
literal|0
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
name|proc
operator|.
name|onMissing
argument_list|(
name|docId
argument_list|)
expr_stmt|;
return|return;
block|}
comment|//LUCENE 4 UPGRADE: Make GeoHashUtils works with BytesRef instead of String
name|proc
operator|.
name|onValue
argument_list|(
name|docId
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|GeoHashUtils
operator|.
name|encode
argument_list|(
name|lat
index|[
name|loc
index|]
argument_list|,
name|lon
index|[
name|loc
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
name|proc
operator|.
name|onOrdinal
argument_list|(
name|docId
argument_list|,
name|ordinals
index|[
name|docId
index|]
argument_list|)
expr_stmt|;
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
name|lat
index|[
name|loc
index|]
argument_list|,
name|lon
index|[
name|loc
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|GeoPoint
name|value
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
literal|null
return|;
block|}
name|GeoPoint
name|point
init|=
name|valuesCache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|point
operator|.
name|latlon
argument_list|(
name|lat
index|[
name|loc
index|]
argument_list|,
name|lon
index|[
name|loc
index|]
argument_list|)
expr_stmt|;
return|return
name|point
return|;
block|}
annotation|@
name|Override
DECL|method|values
specifier|public
name|GeoPoint
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
name|EMPTY_ARRAY
return|;
block|}
name|GeoPoint
index|[]
name|ret
init|=
name|valuesArrayCache
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
operator|.
name|latlon
argument_list|(
name|lat
index|[
name|loc
index|]
argument_list|,
name|lon
index|[
name|loc
index|]
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|latValue
specifier|public
name|double
name|latValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|lat
index|[
name|ordinals
index|[
name|docId
index|]
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|lonValue
specifier|public
name|double
name|lonValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|lon
index|[
name|ordinals
index|[
name|docId
index|]
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|latValues
specifier|public
name|double
index|[]
name|latValues
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
name|DoubleFieldData
operator|.
name|EMPTY_DOUBLE_ARRAY
return|;
block|}
name|double
index|[]
name|ret
init|=
name|valuesLatCache
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
name|lat
index|[
name|loc
index|]
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|lonValues
specifier|public
name|double
index|[]
name|lonValues
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
name|DoubleFieldData
operator|.
name|EMPTY_DOUBLE_ARRAY
return|;
block|}
name|double
index|[]
name|ret
init|=
name|valuesLonCache
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
name|lon
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

