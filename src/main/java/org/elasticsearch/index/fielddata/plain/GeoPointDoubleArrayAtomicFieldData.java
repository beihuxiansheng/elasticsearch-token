begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.plain
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|plain
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
name|FixedBitSet
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
name|RamUsageEstimator
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
name|geo
operator|.
name|GeoPoint
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
name|BigDoubleArrayList
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
name|AtomicGeoPointFieldData
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
name|GeoPointValues
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
name|ScriptDocValues
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

begin_class
DECL|class|GeoPointDoubleArrayAtomicFieldData
specifier|public
specifier|abstract
class|class
name|GeoPointDoubleArrayAtomicFieldData
extends|extends
name|AtomicGeoPointFieldData
argument_list|<
name|ScriptDocValues
argument_list|>
block|{
DECL|field|numDocs
specifier|private
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|field|size
specifier|protected
name|long
name|size
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|GeoPointDoubleArrayAtomicFieldData
specifier|public
name|GeoPointDoubleArrayAtomicFieldData
parameter_list|(
name|int
name|numDocs
parameter_list|)
block|{
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|getNumDocs
specifier|public
name|int
name|getNumDocs
parameter_list|()
block|{
return|return
name|numDocs
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
return|return
operator|new
name|ScriptDocValues
operator|.
name|GeoPoints
argument_list|(
name|getGeoPointValues
argument_list|()
argument_list|)
return|;
block|}
DECL|class|WithOrdinals
specifier|static
class|class
name|WithOrdinals
extends|extends
name|GeoPointDoubleArrayAtomicFieldData
block|{
DECL|field|lon
DECL|field|lat
specifier|private
specifier|final
name|BigDoubleArrayList
name|lon
decl_stmt|,
name|lat
decl_stmt|;
DECL|field|ordinals
specifier|private
specifier|final
name|Ordinals
name|ordinals
decl_stmt|;
DECL|method|WithOrdinals
specifier|public
name|WithOrdinals
parameter_list|(
name|BigDoubleArrayList
name|lon
parameter_list|,
name|BigDoubleArrayList
name|lat
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|Ordinals
name|ordinals
parameter_list|)
block|{
name|super
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
name|this
operator|.
name|lon
operator|=
name|lon
expr_stmt|;
name|this
operator|.
name|lat
operator|=
name|lat
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
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
name|ordinals
operator|.
name|isMultiValued
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumberUniqueValues
specifier|public
name|long
name|getNumberUniqueValues
parameter_list|()
block|{
return|return
name|ordinals
operator|.
name|getNumOrds
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMemorySizeInBytes
specifier|public
name|long
name|getMemorySizeInBytes
parameter_list|()
block|{
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
name|size
operator|=
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
comment|/*size*/
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
comment|/*numDocs*/
operator|+
name|lon
operator|.
name|sizeInBytes
argument_list|()
operator|+
name|lat
operator|.
name|sizeInBytes
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|getGeoPointValues
specifier|public
name|GeoPointValues
name|getGeoPointValues
parameter_list|()
block|{
return|return
operator|new
name|GeoPointValuesWithOrdinals
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|,
name|ordinals
operator|.
name|ordinals
argument_list|()
argument_list|)
return|;
block|}
DECL|class|GeoPointValuesWithOrdinals
specifier|public
specifier|static
class|class
name|GeoPointValuesWithOrdinals
extends|extends
name|GeoPointValues
block|{
DECL|field|lon
DECL|field|lat
specifier|private
specifier|final
name|BigDoubleArrayList
name|lon
decl_stmt|,
name|lat
decl_stmt|;
DECL|field|ordinals
specifier|private
specifier|final
name|Ordinals
operator|.
name|Docs
name|ordinals
decl_stmt|;
DECL|field|scratch
specifier|private
specifier|final
name|GeoPoint
name|scratch
init|=
operator|new
name|GeoPoint
argument_list|()
decl_stmt|;
DECL|method|GeoPointValuesWithOrdinals
name|GeoPointValuesWithOrdinals
parameter_list|(
name|BigDoubleArrayList
name|lon
parameter_list|,
name|BigDoubleArrayList
name|lat
parameter_list|,
name|Ordinals
operator|.
name|Docs
name|ordinals
parameter_list|)
block|{
name|super
argument_list|(
name|ordinals
operator|.
name|isMultiValued
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|lon
operator|=
name|lon
expr_stmt|;
name|this
operator|.
name|lat
operator|=
name|lat
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
DECL|method|nextValue
specifier|public
name|GeoPoint
name|nextValue
parameter_list|()
block|{
specifier|final
name|long
name|ord
init|=
name|ordinals
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
assert|assert
name|ord
operator|>
literal|0
assert|;
return|return
name|scratch
operator|.
name|reset
argument_list|(
name|lat
operator|.
name|get
argument_list|(
name|ord
argument_list|)
argument_list|,
name|lon
operator|.
name|get
argument_list|(
name|ord
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setDocument
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
name|ordinals
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
return|;
block|}
block|}
block|}
comment|/**      * Assumes unset values are marked in bitset, and docId is used as the index to the value array.      */
DECL|class|SingleFixedSet
specifier|public
specifier|static
class|class
name|SingleFixedSet
extends|extends
name|GeoPointDoubleArrayAtomicFieldData
block|{
DECL|field|lon
DECL|field|lat
specifier|private
specifier|final
name|BigDoubleArrayList
name|lon
decl_stmt|,
name|lat
decl_stmt|;
DECL|field|set
specifier|private
specifier|final
name|FixedBitSet
name|set
decl_stmt|;
DECL|field|numOrds
specifier|private
specifier|final
name|long
name|numOrds
decl_stmt|;
DECL|method|SingleFixedSet
specifier|public
name|SingleFixedSet
parameter_list|(
name|BigDoubleArrayList
name|lon
parameter_list|,
name|BigDoubleArrayList
name|lat
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|FixedBitSet
name|set
parameter_list|,
name|long
name|numOrds
parameter_list|)
block|{
name|super
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
name|this
operator|.
name|lon
operator|=
name|lon
expr_stmt|;
name|this
operator|.
name|lat
operator|=
name|lat
expr_stmt|;
name|this
operator|.
name|set
operator|=
name|set
expr_stmt|;
name|this
operator|.
name|numOrds
operator|=
name|numOrds
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getNumberUniqueValues
specifier|public
name|long
name|getNumberUniqueValues
parameter_list|()
block|{
return|return
name|numOrds
return|;
block|}
annotation|@
name|Override
DECL|method|getMemorySizeInBytes
specifier|public
name|long
name|getMemorySizeInBytes
parameter_list|()
block|{
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
name|size
operator|=
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
comment|/*size*/
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
comment|/*numDocs*/
operator|+
name|lon
operator|.
name|sizeInBytes
argument_list|()
operator|+
name|lat
operator|.
name|sizeInBytes
argument_list|()
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|set
operator|.
name|getBits
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|getGeoPointValues
specifier|public
name|GeoPointValues
name|getGeoPointValues
parameter_list|()
block|{
return|return
operator|new
name|GeoPointValuesSingleFixedSet
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|,
name|set
argument_list|)
return|;
block|}
DECL|class|GeoPointValuesSingleFixedSet
specifier|static
class|class
name|GeoPointValuesSingleFixedSet
extends|extends
name|GeoPointValues
block|{
DECL|field|lon
specifier|private
specifier|final
name|BigDoubleArrayList
name|lon
decl_stmt|;
DECL|field|lat
specifier|private
specifier|final
name|BigDoubleArrayList
name|lat
decl_stmt|;
DECL|field|set
specifier|private
specifier|final
name|FixedBitSet
name|set
decl_stmt|;
DECL|field|scratch
specifier|private
specifier|final
name|GeoPoint
name|scratch
init|=
operator|new
name|GeoPoint
argument_list|()
decl_stmt|;
DECL|method|GeoPointValuesSingleFixedSet
name|GeoPointValuesSingleFixedSet
parameter_list|(
name|BigDoubleArrayList
name|lon
parameter_list|,
name|BigDoubleArrayList
name|lat
parameter_list|,
name|FixedBitSet
name|set
parameter_list|)
block|{
name|super
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|lon
operator|=
name|lon
expr_stmt|;
name|this
operator|.
name|lat
operator|=
name|lat
expr_stmt|;
name|this
operator|.
name|set
operator|=
name|set
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDocument
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
name|set
operator|.
name|get
argument_list|(
name|docId
argument_list|)
condition|?
literal|1
else|:
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|nextValue
specifier|public
name|GeoPoint
name|nextValue
parameter_list|()
block|{
return|return
name|scratch
operator|.
name|reset
argument_list|(
name|lat
operator|.
name|get
argument_list|(
name|docId
argument_list|)
argument_list|,
name|lon
operator|.
name|get
argument_list|(
name|docId
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
comment|/**      * Assumes all the values are "set", and docId is used as the index to the value array.      */
DECL|class|Single
specifier|public
specifier|static
class|class
name|Single
extends|extends
name|GeoPointDoubleArrayAtomicFieldData
block|{
DECL|field|lon
DECL|field|lat
specifier|private
specifier|final
name|BigDoubleArrayList
name|lon
decl_stmt|,
name|lat
decl_stmt|;
DECL|field|numOrds
specifier|private
specifier|final
name|long
name|numOrds
decl_stmt|;
DECL|method|Single
specifier|public
name|Single
parameter_list|(
name|BigDoubleArrayList
name|lon
parameter_list|,
name|BigDoubleArrayList
name|lat
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|long
name|numOrds
parameter_list|)
block|{
name|super
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
name|this
operator|.
name|lon
operator|=
name|lon
expr_stmt|;
name|this
operator|.
name|lat
operator|=
name|lat
expr_stmt|;
name|this
operator|.
name|numOrds
operator|=
name|numOrds
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getNumberUniqueValues
specifier|public
name|long
name|getNumberUniqueValues
parameter_list|()
block|{
return|return
name|numOrds
return|;
block|}
annotation|@
name|Override
DECL|method|getMemorySizeInBytes
specifier|public
name|long
name|getMemorySizeInBytes
parameter_list|()
block|{
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
name|size
operator|=
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
comment|/*size*/
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
comment|/*numDocs*/
operator|+
operator|(
name|lon
operator|.
name|sizeInBytes
argument_list|()
operator|+
name|lat
operator|.
name|sizeInBytes
argument_list|()
operator|)
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|getGeoPointValues
specifier|public
name|GeoPointValues
name|getGeoPointValues
parameter_list|()
block|{
return|return
operator|new
name|GeoPointValuesSingle
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|)
return|;
block|}
DECL|class|GeoPointValuesSingle
specifier|static
class|class
name|GeoPointValuesSingle
extends|extends
name|GeoPointValues
block|{
DECL|field|lon
specifier|private
specifier|final
name|BigDoubleArrayList
name|lon
decl_stmt|;
DECL|field|lat
specifier|private
specifier|final
name|BigDoubleArrayList
name|lat
decl_stmt|;
DECL|field|scratch
specifier|private
specifier|final
name|GeoPoint
name|scratch
init|=
operator|new
name|GeoPoint
argument_list|()
decl_stmt|;
DECL|method|GeoPointValuesSingle
name|GeoPointValuesSingle
parameter_list|(
name|BigDoubleArrayList
name|lon
parameter_list|,
name|BigDoubleArrayList
name|lat
parameter_list|)
block|{
name|super
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|lon
operator|=
name|lon
expr_stmt|;
name|this
operator|.
name|lat
operator|=
name|lat
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDocument
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
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|nextValue
specifier|public
name|GeoPoint
name|nextValue
parameter_list|()
block|{
return|return
name|scratch
operator|.
name|reset
argument_list|(
name|lat
operator|.
name|get
argument_list|(
name|docId
argument_list|)
argument_list|,
name|lon
operator|.
name|get
argument_list|(
name|docId
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

