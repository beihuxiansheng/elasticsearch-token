begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.fieldcomparator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|fieldcomparator
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
name|AtomicReaderContext
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
name|search
operator|.
name|FieldComparator
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|IndexFieldData
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Sorts by field's natural Term sort order, using  * ordinals.  This is functionally equivalent to {@link  * org.apache.lucene.search.FieldComparator.TermValComparator}, but it first resolves the string  * to their relative ordinal positions (using the index  * returned by {@link org.apache.lucene.search.FieldCache#getTermsIndex}), and  * does most comparisons using the ordinals.  For medium  * to large results, this comparator will be much faster  * than {@link org.apache.lucene.search.FieldComparator.TermValComparator}.  For very small  * result sets it may be slower.  */
end_comment

begin_class
DECL|class|BytesRefOrdValComparator
specifier|public
specifier|final
class|class
name|BytesRefOrdValComparator
extends|extends
name|FieldComparator
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|indexFieldData
specifier|final
name|IndexFieldData
operator|.
name|WithOrdinals
name|indexFieldData
decl_stmt|;
comment|/* Ords for each slot.        @lucene.internal */
DECL|field|ords
specifier|final
name|int
index|[]
name|ords
decl_stmt|;
comment|/* Values for each slot.        @lucene.internal */
DECL|field|values
specifier|final
name|BytesRef
index|[]
name|values
decl_stmt|;
comment|/* Which reader last copied a value into the slot. When        we compare two slots, we just compare-by-ord if the        readerGen is the same; else we must compare the        values (slower).        @lucene.internal */
DECL|field|readerGen
specifier|final
name|int
index|[]
name|readerGen
decl_stmt|;
comment|/* Gen of current reader we are on.        @lucene.internal */
DECL|field|currentReaderGen
name|int
name|currentReaderGen
init|=
operator|-
literal|1
decl_stmt|;
comment|/* Current reader's doc ord/values.        @lucene.internal */
DECL|field|termsIndex
name|BytesValues
operator|.
name|WithOrdinals
name|termsIndex
decl_stmt|;
comment|/* Bottom slot, or -1 if queue isn't full yet        @lucene.internal */
DECL|field|bottomSlot
name|int
name|bottomSlot
init|=
operator|-
literal|1
decl_stmt|;
comment|/* Bottom ord (same as ords[bottomSlot] once bottomSlot        is set).  Cached for faster compares.        @lucene.internal */
DECL|field|bottomOrd
name|int
name|bottomOrd
decl_stmt|;
comment|/* True if current bottom slot matches the current        reader.        @lucene.internal */
DECL|field|bottomSameReader
name|boolean
name|bottomSameReader
decl_stmt|;
comment|/* Bottom value (same as values[bottomSlot] once        bottomSlot is set).  Cached for faster compares.       @lucene.internal */
DECL|field|bottomValue
name|BytesRef
name|bottomValue
decl_stmt|;
DECL|field|tempBR
specifier|final
name|BytesRef
name|tempBR
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|method|BytesRefOrdValComparator
specifier|public
name|BytesRefOrdValComparator
parameter_list|(
name|IndexFieldData
operator|.
name|WithOrdinals
name|indexFieldData
parameter_list|,
name|int
name|numHits
parameter_list|)
block|{
name|this
operator|.
name|indexFieldData
operator|=
name|indexFieldData
expr_stmt|;
name|ords
operator|=
operator|new
name|int
index|[
name|numHits
index|]
expr_stmt|;
name|values
operator|=
operator|new
name|BytesRef
index|[
name|numHits
index|]
expr_stmt|;
name|readerGen
operator|=
operator|new
name|int
index|[
name|numHits
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slot1
parameter_list|,
name|int
name|slot2
parameter_list|)
block|{
if|if
condition|(
name|readerGen
index|[
name|slot1
index|]
operator|==
name|readerGen
index|[
name|slot2
index|]
condition|)
block|{
return|return
name|ords
index|[
name|slot1
index|]
operator|-
name|ords
index|[
name|slot2
index|]
return|;
block|}
specifier|final
name|BytesRef
name|val1
init|=
name|values
index|[
name|slot1
index|]
decl_stmt|;
specifier|final
name|BytesRef
name|val2
init|=
name|values
index|[
name|slot2
index|]
decl_stmt|;
if|if
condition|(
name|val1
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|val2
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|val2
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
name|val1
operator|.
name|compareTo
argument_list|(
name|val2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|compareDocToValue
specifier|public
name|int
name|compareDocToValue
parameter_list|(
name|int
name|doc
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
name|BytesRef
name|docValue
init|=
name|termsIndex
operator|.
name|getValue
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|docValue
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
name|docValue
operator|.
name|compareTo
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|/**      * Base class for specialized (per bit width of the      * ords) per-segment comparator.  NOTE: this is messy;      * we do this only because hotspot can't reliably inline      * the underlying array access when looking up doc->ord      *      * @lucene.internal      */
DECL|class|PerSegmentComparator
specifier|abstract
class|class
name|PerSegmentComparator
extends|extends
name|FieldComparator
argument_list|<
name|BytesRef
argument_list|>
block|{
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|FieldComparator
argument_list|<
name|BytesRef
argument_list|>
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|BytesRefOrdValComparator
operator|.
name|this
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slot1
parameter_list|,
name|int
name|slot2
parameter_list|)
block|{
return|return
name|BytesRefOrdValComparator
operator|.
name|this
operator|.
name|compare
argument_list|(
name|slot1
argument_list|,
name|slot2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
specifier|final
name|int
name|bottom
parameter_list|)
block|{
name|BytesRefOrdValComparator
operator|.
name|this
operator|.
name|setBottom
argument_list|(
name|bottom
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|BytesRef
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|BytesRefOrdValComparator
operator|.
name|this
operator|.
name|value
argument_list|(
name|slot
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareValues
specifier|public
name|int
name|compareValues
parameter_list|(
name|BytesRef
name|val1
parameter_list|,
name|BytesRef
name|val2
parameter_list|)
block|{
if|if
condition|(
name|val1
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|val2
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|val2
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
name|val1
operator|.
name|compareTo
argument_list|(
name|val2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareDocToValue
specifier|public
name|int
name|compareDocToValue
parameter_list|(
name|int
name|doc
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
return|return
name|BytesRefOrdValComparator
operator|.
name|this
operator|.
name|compareDocToValue
argument_list|(
name|doc
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
comment|// Used per-segment when bit width of doc->ord is 8:
DECL|class|ByteOrdComparator
specifier|private
specifier|final
class|class
name|ByteOrdComparator
extends|extends
name|PerSegmentComparator
block|{
DECL|field|readerOrds
specifier|private
specifier|final
name|byte
index|[]
name|readerOrds
decl_stmt|;
DECL|field|termsIndex
specifier|private
specifier|final
name|BytesValues
operator|.
name|WithOrdinals
name|termsIndex
decl_stmt|;
DECL|field|docBase
specifier|private
specifier|final
name|int
name|docBase
decl_stmt|;
DECL|method|ByteOrdComparator
specifier|public
name|ByteOrdComparator
parameter_list|(
name|byte
index|[]
name|readerOrds
parameter_list|,
name|BytesValues
operator|.
name|WithOrdinals
name|termsIndex
parameter_list|,
name|int
name|docBase
parameter_list|)
block|{
name|this
operator|.
name|readerOrds
operator|=
name|readerOrds
expr_stmt|;
name|this
operator|.
name|termsIndex
operator|=
name|termsIndex
expr_stmt|;
name|this
operator|.
name|docBase
operator|=
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
assert|assert
name|bottomSlot
operator|!=
operator|-
literal|1
assert|;
specifier|final
name|int
name|docOrd
init|=
operator|(
name|readerOrds
index|[
name|doc
index|]
operator|&
literal|0xFF
operator|)
decl_stmt|;
if|if
condition|(
name|bottomSameReader
condition|)
block|{
comment|// ord is precisely comparable, even in the equal case
return|return
name|bottomOrd
operator|-
name|docOrd
return|;
block|}
elseif|else
if|if
condition|(
name|bottomOrd
operator|>=
name|docOrd
condition|)
block|{
comment|// the equals case always means bottom is> doc
comment|// (because we set bottomOrd to the lower bound in
comment|// setBottom):
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|readerOrds
index|[
name|doc
index|]
operator|&
literal|0xFF
decl_stmt|;
name|ords
index|[
name|slot
index|]
operator|=
name|ord
expr_stmt|;
if|if
condition|(
name|ord
operator|==
literal|0
condition|)
block|{
name|values
index|[
name|slot
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|ord
operator|>
literal|0
assert|;
if|if
condition|(
name|values
index|[
name|slot
index|]
operator|==
literal|null
condition|)
block|{
name|values
index|[
name|slot
index|]
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
name|termsIndex
operator|.
name|getValueScratchByOrd
argument_list|(
name|ord
argument_list|,
name|values
index|[
name|slot
index|]
argument_list|)
expr_stmt|;
block|}
name|readerGen
index|[
name|slot
index|]
operator|=
name|currentReaderGen
expr_stmt|;
block|}
block|}
comment|// Used per-segment when bit width of doc->ord is 16:
DECL|class|ShortOrdComparator
specifier|private
specifier|final
class|class
name|ShortOrdComparator
extends|extends
name|PerSegmentComparator
block|{
DECL|field|readerOrds
specifier|private
specifier|final
name|short
index|[]
name|readerOrds
decl_stmt|;
DECL|field|termsIndex
specifier|private
specifier|final
name|BytesValues
operator|.
name|WithOrdinals
name|termsIndex
decl_stmt|;
DECL|field|docBase
specifier|private
specifier|final
name|int
name|docBase
decl_stmt|;
DECL|method|ShortOrdComparator
specifier|public
name|ShortOrdComparator
parameter_list|(
name|short
index|[]
name|readerOrds
parameter_list|,
name|BytesValues
operator|.
name|WithOrdinals
name|termsIndex
parameter_list|,
name|int
name|docBase
parameter_list|)
block|{
name|this
operator|.
name|readerOrds
operator|=
name|readerOrds
expr_stmt|;
name|this
operator|.
name|termsIndex
operator|=
name|termsIndex
expr_stmt|;
name|this
operator|.
name|docBase
operator|=
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
assert|assert
name|bottomSlot
operator|!=
operator|-
literal|1
assert|;
specifier|final
name|int
name|docOrd
init|=
operator|(
name|readerOrds
index|[
name|doc
index|]
operator|&
literal|0xFFFF
operator|)
decl_stmt|;
if|if
condition|(
name|bottomSameReader
condition|)
block|{
comment|// ord is precisely comparable, even in the equal case
return|return
name|bottomOrd
operator|-
name|docOrd
return|;
block|}
elseif|else
if|if
condition|(
name|bottomOrd
operator|>=
name|docOrd
condition|)
block|{
comment|// the equals case always means bottom is> doc
comment|// (because we set bottomOrd to the lower bound in
comment|// setBottom):
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|readerOrds
index|[
name|doc
index|]
operator|&
literal|0xFFFF
decl_stmt|;
name|ords
index|[
name|slot
index|]
operator|=
name|ord
expr_stmt|;
if|if
condition|(
name|ord
operator|==
literal|0
condition|)
block|{
name|values
index|[
name|slot
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|ord
operator|>
literal|0
assert|;
if|if
condition|(
name|values
index|[
name|slot
index|]
operator|==
literal|null
condition|)
block|{
name|values
index|[
name|slot
index|]
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
name|termsIndex
operator|.
name|getValueScratchByOrd
argument_list|(
name|ord
argument_list|,
name|values
index|[
name|slot
index|]
argument_list|)
expr_stmt|;
block|}
name|readerGen
index|[
name|slot
index|]
operator|=
name|currentReaderGen
expr_stmt|;
block|}
block|}
comment|// Used per-segment when bit width of doc->ord is 32:
DECL|class|IntOrdComparator
specifier|private
specifier|final
class|class
name|IntOrdComparator
extends|extends
name|PerSegmentComparator
block|{
DECL|field|readerOrds
specifier|private
specifier|final
name|int
index|[]
name|readerOrds
decl_stmt|;
DECL|field|termsIndex
specifier|private
specifier|final
name|BytesValues
operator|.
name|WithOrdinals
name|termsIndex
decl_stmt|;
DECL|field|docBase
specifier|private
specifier|final
name|int
name|docBase
decl_stmt|;
DECL|method|IntOrdComparator
specifier|public
name|IntOrdComparator
parameter_list|(
name|int
index|[]
name|readerOrds
parameter_list|,
name|BytesValues
operator|.
name|WithOrdinals
name|termsIndex
parameter_list|,
name|int
name|docBase
parameter_list|)
block|{
name|this
operator|.
name|readerOrds
operator|=
name|readerOrds
expr_stmt|;
name|this
operator|.
name|termsIndex
operator|=
name|termsIndex
expr_stmt|;
name|this
operator|.
name|docBase
operator|=
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
assert|assert
name|bottomSlot
operator|!=
operator|-
literal|1
assert|;
specifier|final
name|int
name|docOrd
init|=
name|readerOrds
index|[
name|doc
index|]
decl_stmt|;
if|if
condition|(
name|bottomSameReader
condition|)
block|{
comment|// ord is precisely comparable, even in the equal case
return|return
name|bottomOrd
operator|-
name|docOrd
return|;
block|}
elseif|else
if|if
condition|(
name|bottomOrd
operator|>=
name|docOrd
condition|)
block|{
comment|// the equals case always means bottom is> doc
comment|// (because we set bottomOrd to the lower bound in
comment|// setBottom):
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|readerOrds
index|[
name|doc
index|]
decl_stmt|;
name|ords
index|[
name|slot
index|]
operator|=
name|ord
expr_stmt|;
if|if
condition|(
name|ord
operator|==
literal|0
condition|)
block|{
name|values
index|[
name|slot
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|ord
operator|>
literal|0
assert|;
if|if
condition|(
name|values
index|[
name|slot
index|]
operator|==
literal|null
condition|)
block|{
name|values
index|[
name|slot
index|]
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
name|termsIndex
operator|.
name|getValueScratchByOrd
argument_list|(
name|ord
argument_list|,
name|values
index|[
name|slot
index|]
argument_list|)
expr_stmt|;
block|}
name|readerGen
index|[
name|slot
index|]
operator|=
name|currentReaderGen
expr_stmt|;
block|}
block|}
comment|// Used per-segment when bit width is not a native array
comment|// size (8, 16, 32):
DECL|class|AnyOrdComparator
specifier|final
class|class
name|AnyOrdComparator
extends|extends
name|PerSegmentComparator
block|{
DECL|field|fieldData
specifier|private
specifier|final
name|IndexFieldData
name|fieldData
decl_stmt|;
DECL|field|readerOrds
specifier|private
specifier|final
name|Ordinals
operator|.
name|Docs
name|readerOrds
decl_stmt|;
DECL|field|termsIndex
specifier|private
specifier|final
name|BytesValues
operator|.
name|WithOrdinals
name|termsIndex
decl_stmt|;
DECL|field|docBase
specifier|private
specifier|final
name|int
name|docBase
decl_stmt|;
DECL|method|AnyOrdComparator
specifier|public
name|AnyOrdComparator
parameter_list|(
name|IndexFieldData
name|fieldData
parameter_list|,
name|BytesValues
operator|.
name|WithOrdinals
name|termsIndex
parameter_list|,
name|int
name|docBase
parameter_list|)
block|{
name|this
operator|.
name|fieldData
operator|=
name|fieldData
expr_stmt|;
name|this
operator|.
name|readerOrds
operator|=
name|termsIndex
operator|.
name|ordinals
argument_list|()
expr_stmt|;
name|this
operator|.
name|termsIndex
operator|=
name|termsIndex
expr_stmt|;
name|this
operator|.
name|docBase
operator|=
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareBottom
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
assert|assert
name|bottomSlot
operator|!=
operator|-
literal|1
assert|;
specifier|final
name|int
name|docOrd
init|=
name|readerOrds
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|bottomSameReader
condition|)
block|{
comment|// ord is precisely comparable, even in the equal case
return|return
name|bottomOrd
operator|-
name|docOrd
return|;
block|}
elseif|else
if|if
condition|(
name|bottomOrd
operator|>=
name|docOrd
condition|)
block|{
comment|// the equals case always means bottom is> doc
comment|// (because we set bottomOrd to the lower bound in
comment|// setBottom):
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|readerOrds
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|ords
index|[
name|slot
index|]
operator|=
name|ord
expr_stmt|;
if|if
condition|(
name|ord
operator|==
literal|0
condition|)
block|{
name|values
index|[
name|slot
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|ord
operator|>
literal|0
assert|;
if|if
condition|(
name|values
index|[
name|slot
index|]
operator|==
literal|null
condition|)
block|{
name|values
index|[
name|slot
index|]
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
name|termsIndex
operator|.
name|getValueScratchByOrd
argument_list|(
name|ord
argument_list|,
name|values
index|[
name|slot
index|]
argument_list|)
expr_stmt|;
block|}
name|readerGen
index|[
name|slot
index|]
operator|=
name|currentReaderGen
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|FieldComparator
argument_list|<
name|BytesRef
argument_list|>
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|docBase
init|=
name|context
operator|.
name|docBase
decl_stmt|;
name|termsIndex
operator|=
name|indexFieldData
operator|.
name|load
argument_list|(
name|context
argument_list|)
operator|.
name|getBytesValues
argument_list|()
expr_stmt|;
comment|// TODO, we should support sorting on multi valued field, take the best ascending value out of all the values
if|if
condition|(
name|termsIndex
operator|.
name|isMultiValued
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"can't sort on a multi valued field"
argument_list|)
throw|;
block|}
specifier|final
name|Ordinals
operator|.
name|Docs
name|docToOrd
init|=
name|termsIndex
operator|.
name|ordinals
argument_list|()
decl_stmt|;
name|Object
name|ordsStorage
init|=
name|docToOrd
operator|.
name|ordinals
argument_list|()
operator|.
name|getBackingStorage
argument_list|()
decl_stmt|;
name|FieldComparator
argument_list|<
name|BytesRef
argument_list|>
name|perSegComp
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|docToOrd
operator|.
name|ordinals
argument_list|()
operator|.
name|hasSingleArrayBackingStorage
argument_list|()
condition|)
block|{
if|if
condition|(
name|ordsStorage
operator|instanceof
name|byte
index|[]
condition|)
block|{
name|perSegComp
operator|=
operator|new
name|ByteOrdComparator
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|ordsStorage
argument_list|,
name|termsIndex
argument_list|,
name|docBase
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ordsStorage
operator|instanceof
name|short
index|[]
condition|)
block|{
name|perSegComp
operator|=
operator|new
name|ShortOrdComparator
argument_list|(
operator|(
name|short
index|[]
operator|)
name|ordsStorage
argument_list|,
name|termsIndex
argument_list|,
name|docBase
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ordsStorage
operator|instanceof
name|int
index|[]
condition|)
block|{
name|perSegComp
operator|=
operator|new
name|IntOrdComparator
argument_list|(
operator|(
name|int
index|[]
operator|)
name|ordsStorage
argument_list|,
name|termsIndex
argument_list|,
name|docBase
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Don't specialize the long[] case since it's not
comment|// possible, ie, worse case is MAX_INT-1 docs with
comment|// every one having a unique value.
comment|// TODO: ES - should we optimize for the PackedInts.Reader case as well?
if|if
condition|(
name|perSegComp
operator|==
literal|null
condition|)
block|{
name|perSegComp
operator|=
operator|new
name|AnyOrdComparator
argument_list|(
name|indexFieldData
argument_list|,
name|termsIndex
argument_list|,
name|docBase
argument_list|)
expr_stmt|;
block|}
name|currentReaderGen
operator|++
expr_stmt|;
if|if
condition|(
name|bottomSlot
operator|!=
operator|-
literal|1
condition|)
block|{
name|perSegComp
operator|.
name|setBottom
argument_list|(
name|bottomSlot
argument_list|)
expr_stmt|;
block|}
return|return
name|perSegComp
return|;
block|}
annotation|@
name|Override
DECL|method|setBottom
specifier|public
name|void
name|setBottom
parameter_list|(
specifier|final
name|int
name|bottom
parameter_list|)
block|{
name|bottomSlot
operator|=
name|bottom
expr_stmt|;
name|bottomValue
operator|=
name|values
index|[
name|bottomSlot
index|]
expr_stmt|;
if|if
condition|(
name|currentReaderGen
operator|==
name|readerGen
index|[
name|bottomSlot
index|]
condition|)
block|{
name|bottomOrd
operator|=
name|ords
index|[
name|bottomSlot
index|]
expr_stmt|;
name|bottomSameReader
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|bottomValue
operator|==
literal|null
condition|)
block|{
comment|// 0 ord is null for all segments
assert|assert
name|ords
index|[
name|bottomSlot
index|]
operator|==
literal|0
assert|;
name|bottomOrd
operator|=
literal|0
expr_stmt|;
name|bottomSameReader
operator|=
literal|true
expr_stmt|;
name|readerGen
index|[
name|bottomSlot
index|]
operator|=
name|currentReaderGen
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|index
init|=
name|binarySearch
argument_list|(
name|termsIndex
argument_list|,
name|bottomValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
name|bottomOrd
operator|=
operator|-
name|index
operator|-
literal|2
expr_stmt|;
name|bottomSameReader
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|bottomOrd
operator|=
name|index
expr_stmt|;
comment|// exact value match
name|bottomSameReader
operator|=
literal|true
expr_stmt|;
name|readerGen
index|[
name|bottomSlot
index|]
operator|=
name|currentReaderGen
expr_stmt|;
name|ords
index|[
name|bottomSlot
index|]
operator|=
name|bottomOrd
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|BytesRef
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|values
index|[
name|slot
index|]
return|;
block|}
DECL|method|binarySearch
specifier|final
specifier|protected
specifier|static
name|int
name|binarySearch
parameter_list|(
name|BytesValues
operator|.
name|WithOrdinals
name|a
parameter_list|,
name|BytesRef
name|key
parameter_list|)
block|{
return|return
name|binarySearch
argument_list|(
name|a
argument_list|,
name|key
argument_list|,
literal|1
argument_list|,
name|a
operator|.
name|ordinals
argument_list|()
operator|.
name|getNumDocs
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|method|binarySearch
specifier|final
specifier|protected
specifier|static
name|int
name|binarySearch
parameter_list|(
name|BytesValues
operator|.
name|WithOrdinals
name|a
parameter_list|,
name|BytesRef
name|key
parameter_list|,
name|int
name|low
parameter_list|,
name|int
name|high
parameter_list|)
block|{
while|while
condition|(
name|low
operator|<=
name|high
condition|)
block|{
name|int
name|mid
init|=
operator|(
name|low
operator|+
name|high
operator|)
operator|>>>
literal|1
decl_stmt|;
name|BytesRef
name|midVal
init|=
name|a
operator|.
name|getValueByOrd
argument_list|(
name|mid
argument_list|)
decl_stmt|;
name|int
name|cmp
decl_stmt|;
if|if
condition|(
name|midVal
operator|!=
literal|null
condition|)
block|{
name|cmp
operator|=
name|midVal
operator|.
name|compareTo
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cmp
operator|=
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
name|low
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
name|high
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
else|else
return|return
name|mid
return|;
block|}
return|return
operator|-
operator|(
name|low
operator|+
literal|1
operator|)
return|;
block|}
block|}
end_class

end_unit

