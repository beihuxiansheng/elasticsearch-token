begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|index
operator|.
name|fielddata
operator|.
name|AtomicNumericFieldData
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
name|BytesValues
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
name|DoubleValues
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
name|LongValues
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
DECL|class|FloatArrayAtomicFieldData
specifier|public
specifier|abstract
class|class
name|FloatArrayAtomicFieldData
extends|extends
name|AtomicNumericFieldData
block|{
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|FloatArrayAtomicFieldData
name|EMPTY
init|=
operator|new
name|Empty
argument_list|()
decl_stmt|;
DECL|field|values
specifier|protected
specifier|final
name|float
index|[]
name|values
decl_stmt|;
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
DECL|method|FloatArrayAtomicFieldData
specifier|public
name|FloatArrayAtomicFieldData
parameter_list|(
name|float
index|[]
name|values
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
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
DECL|class|Empty
specifier|static
class|class
name|Empty
extends|extends
name|FloatArrayAtomicFieldData
block|{
DECL|method|Empty
name|Empty
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLongValues
specifier|public
name|LongValues
name|getLongValues
parameter_list|()
block|{
return|return
name|LongValues
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|Override
DECL|method|getDoubleValues
specifier|public
name|DoubleValues
name|getDoubleValues
parameter_list|()
block|{
return|return
name|DoubleValues
operator|.
name|EMPTY
return|;
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
DECL|method|getMemorySizeInBytes
specifier|public
name|long
name|getMemorySizeInBytes
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|getBytesValues
specifier|public
name|BytesValues
name|getBytesValues
parameter_list|()
block|{
return|return
name|BytesValues
operator|.
name|EMPTY
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
name|ScriptDocValues
operator|.
name|EMPTY
return|;
block|}
block|}
DECL|class|WithOrdinals
specifier|public
specifier|static
class|class
name|WithOrdinals
extends|extends
name|FloatArrayAtomicFieldData
block|{
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
name|float
index|[]
name|values
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
name|values
argument_list|,
name|numDocs
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
DECL|method|isValuesOrdered
specifier|public
name|boolean
name|isValuesOrdered
parameter_list|()
block|{
return|return
literal|true
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
name|RamUsage
operator|.
name|NUM_BYTES_INT
comment|/*size*/
operator|+
name|RamUsage
operator|.
name|NUM_BYTES_INT
comment|/*numDocs*/
operator|+
name|RamUsage
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|values
operator|.
name|length
operator|*
name|RamUsage
operator|.
name|NUM_BYTES_FLOAT
operator|)
operator|+
name|ordinals
operator|.
name|getMemorySizeInBytes
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|getLongValues
specifier|public
name|LongValues
name|getLongValues
parameter_list|()
block|{
return|return
operator|new
name|LongValues
argument_list|(
name|values
argument_list|,
name|ordinals
operator|.
name|ordinals
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDoubleValues
specifier|public
name|DoubleValues
name|getDoubleValues
parameter_list|()
block|{
return|return
operator|new
name|DoubleValues
argument_list|(
name|values
argument_list|,
name|ordinals
operator|.
name|ordinals
argument_list|()
argument_list|)
return|;
block|}
DECL|class|LongValues
specifier|static
class|class
name|LongValues
extends|extends
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|LongValues
operator|.
name|OrdBasedLongValues
block|{
DECL|field|values
specifier|private
specifier|final
name|float
index|[]
name|values
decl_stmt|;
DECL|method|LongValues
name|LongValues
parameter_list|(
name|float
index|[]
name|values
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
DECL|method|getByOrd
specifier|public
name|long
name|getByOrd
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|values
index|[
name|ord
index|]
return|;
block|}
block|}
DECL|class|DoubleValues
specifier|static
class|class
name|DoubleValues
extends|extends
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|DoubleValues
operator|.
name|OrdBasedDoubleValues
block|{
DECL|field|values
specifier|private
specifier|final
name|float
index|[]
name|values
decl_stmt|;
DECL|method|DoubleValues
name|DoubleValues
parameter_list|(
name|float
index|[]
name|values
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
DECL|method|getByOrd
specifier|protected
name|double
name|getByOrd
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
return|return
name|values
index|[
name|ord
index|]
return|;
block|}
block|}
block|}
comment|/**      * A single valued case, where not all values are "set", so we have a FixedBitSet that      * indicates which values have an actual value.      */
DECL|class|SingleFixedSet
specifier|public
specifier|static
class|class
name|SingleFixedSet
extends|extends
name|FloatArrayAtomicFieldData
block|{
DECL|field|set
specifier|private
specifier|final
name|FixedBitSet
name|set
decl_stmt|;
DECL|method|SingleFixedSet
specifier|public
name|SingleFixedSet
parameter_list|(
name|float
index|[]
name|values
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|FixedBitSet
name|set
parameter_list|)
block|{
name|super
argument_list|(
name|values
argument_list|,
name|numDocs
argument_list|)
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
name|RamUsage
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|values
operator|.
name|length
operator|*
name|RamUsage
operator|.
name|NUM_BYTES_FLOAT
operator|)
operator|+
operator|(
name|set
operator|.
name|getBits
argument_list|()
operator|.
name|length
operator|*
name|RamUsage
operator|.
name|NUM_BYTES_LONG
operator|)
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|getLongValues
specifier|public
name|LongValues
name|getLongValues
parameter_list|()
block|{
return|return
operator|new
name|LongValues
argument_list|(
name|values
argument_list|,
name|set
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDoubleValues
specifier|public
name|DoubleValues
name|getDoubleValues
parameter_list|()
block|{
return|return
operator|new
name|DoubleValues
argument_list|(
name|values
argument_list|,
name|set
argument_list|)
return|;
block|}
DECL|class|LongValues
specifier|static
class|class
name|LongValues
extends|extends
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|LongValues
block|{
DECL|field|values
specifier|private
specifier|final
name|float
index|[]
name|values
decl_stmt|;
DECL|field|set
specifier|private
specifier|final
name|FixedBitSet
name|set
decl_stmt|;
DECL|method|LongValues
name|LongValues
parameter_list|(
name|float
index|[]
name|values
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
name|values
operator|=
name|values
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
name|set
operator|.
name|get
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|long
name|getValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|values
index|[
name|docId
index|]
return|;
block|}
block|}
DECL|class|DoubleValues
specifier|static
class|class
name|DoubleValues
extends|extends
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|DoubleValues
block|{
DECL|field|values
specifier|private
specifier|final
name|float
index|[]
name|values
decl_stmt|;
DECL|field|set
specifier|private
specifier|final
name|FixedBitSet
name|set
decl_stmt|;
DECL|method|DoubleValues
name|DoubleValues
parameter_list|(
name|float
index|[]
name|values
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
name|values
operator|=
name|values
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
name|set
operator|.
name|get
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|double
name|getValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|values
index|[
name|docId
index|]
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
name|FloatArrayAtomicFieldData
block|{
comment|/**          * Note, here, we assume that there is no offset by 1 from docId, so position 0          * is the value for docId 0.          */
DECL|method|Single
specifier|public
name|Single
parameter_list|(
name|float
index|[]
name|values
parameter_list|,
name|int
name|numDocs
parameter_list|)
block|{
name|super
argument_list|(
name|values
argument_list|,
name|numDocs
argument_list|)
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
name|RamUsage
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|values
operator|.
name|length
operator|*
name|RamUsage
operator|.
name|NUM_BYTES_FLOAT
operator|)
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|getLongValues
specifier|public
name|LongValues
name|getLongValues
parameter_list|()
block|{
return|return
operator|new
name|LongValues
argument_list|(
name|values
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDoubleValues
specifier|public
name|DoubleValues
name|getDoubleValues
parameter_list|()
block|{
return|return
operator|new
name|DoubleValues
argument_list|(
name|values
argument_list|)
return|;
block|}
DECL|class|LongValues
specifier|static
class|class
name|LongValues
extends|extends
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|LongValues
operator|.
name|DenseLongValues
block|{
DECL|field|values
specifier|private
specifier|final
name|float
index|[]
name|values
decl_stmt|;
DECL|method|LongValues
name|LongValues
parameter_list|(
name|float
index|[]
name|values
parameter_list|)
block|{
name|super
argument_list|(
literal|false
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
DECL|method|getValue
specifier|public
name|long
name|getValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|values
index|[
name|docId
index|]
return|;
block|}
block|}
DECL|class|DoubleValues
specifier|static
class|class
name|DoubleValues
extends|extends
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|DoubleValues
operator|.
name|DenseDoubleValues
block|{
DECL|field|values
specifier|private
specifier|final
name|float
index|[]
name|values
decl_stmt|;
DECL|method|DoubleValues
name|DoubleValues
parameter_list|(
name|float
index|[]
name|values
parameter_list|)
block|{
name|super
argument_list|(
literal|false
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
DECL|method|getValue
specifier|public
name|double
name|getValue
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|values
index|[
name|docId
index|]
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

