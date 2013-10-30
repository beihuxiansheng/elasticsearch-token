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
name|elasticsearch
operator|.
name|common
operator|.
name|geo
operator|.
name|GeoDistance
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
name|unit
operator|.
name|DistanceUnit
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
name|IndexGeoPointFieldData
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
comment|/**  */
end_comment

begin_class
DECL|class|GeoDistanceComparator
specifier|public
class|class
name|GeoDistanceComparator
extends|extends
name|NumberComparatorBase
argument_list|<
name|Double
argument_list|>
block|{
DECL|field|indexFieldData
specifier|protected
specifier|final
name|IndexGeoPointFieldData
argument_list|<
name|?
argument_list|>
name|indexFieldData
decl_stmt|;
DECL|field|lat
specifier|protected
specifier|final
name|double
name|lat
decl_stmt|;
DECL|field|lon
specifier|protected
specifier|final
name|double
name|lon
decl_stmt|;
DECL|field|unit
specifier|protected
specifier|final
name|DistanceUnit
name|unit
decl_stmt|;
DECL|field|geoDistance
specifier|protected
specifier|final
name|GeoDistance
name|geoDistance
decl_stmt|;
DECL|field|fixedSourceDistance
specifier|protected
specifier|final
name|GeoDistance
operator|.
name|FixedSourceDistance
name|fixedSourceDistance
decl_stmt|;
DECL|field|sortMode
specifier|protected
specifier|final
name|SortMode
name|sortMode
decl_stmt|;
DECL|field|MISSING_VALUE
specifier|private
specifier|static
specifier|final
name|Double
name|MISSING_VALUE
init|=
name|Double
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|double
index|[]
name|values
decl_stmt|;
DECL|field|bottom
specifier|private
name|double
name|bottom
decl_stmt|;
DECL|field|geoDistanceValues
specifier|private
name|GeoDistanceValues
name|geoDistanceValues
decl_stmt|;
DECL|method|GeoDistanceComparator
specifier|public
name|GeoDistanceComparator
parameter_list|(
name|int
name|numHits
parameter_list|,
name|IndexGeoPointFieldData
argument_list|<
name|?
argument_list|>
name|indexFieldData
parameter_list|,
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|,
name|GeoDistance
name|geoDistance
parameter_list|,
name|SortMode
name|sortMode
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
operator|new
name|double
index|[
name|numHits
index|]
expr_stmt|;
name|this
operator|.
name|indexFieldData
operator|=
name|indexFieldData
expr_stmt|;
name|this
operator|.
name|lat
operator|=
name|lat
expr_stmt|;
name|this
operator|.
name|lon
operator|=
name|lon
expr_stmt|;
name|this
operator|.
name|unit
operator|=
name|unit
expr_stmt|;
name|this
operator|.
name|geoDistance
operator|=
name|geoDistance
expr_stmt|;
name|this
operator|.
name|fixedSourceDistance
operator|=
name|geoDistance
operator|.
name|fixedSourceDistance
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|unit
argument_list|)
expr_stmt|;
name|this
operator|.
name|sortMode
operator|=
name|sortMode
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|FieldComparator
argument_list|<
name|Double
argument_list|>
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|GeoPointValues
name|readerValues
init|=
name|indexFieldData
operator|.
name|load
argument_list|(
name|context
argument_list|)
operator|.
name|getGeoPointValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|readerValues
operator|.
name|isMultiValued
argument_list|()
condition|)
block|{
name|geoDistanceValues
operator|=
operator|new
name|MV
argument_list|(
name|readerValues
argument_list|,
name|fixedSourceDistance
argument_list|,
name|sortMode
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|geoDistanceValues
operator|=
operator|new
name|SV
argument_list|(
name|readerValues
argument_list|,
name|fixedSourceDistance
argument_list|)
expr_stmt|;
block|}
return|return
name|this
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
name|Double
operator|.
name|compare
argument_list|(
name|values
index|[
name|slot1
index|]
argument_list|,
name|values
index|[
name|slot2
index|]
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
specifier|final
name|double
name|v2
init|=
name|geoDistanceValues
operator|.
name|computeDistance
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|Double
operator|.
name|compare
argument_list|(
name|bottom
argument_list|,
name|v2
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
name|Double
name|distance2
parameter_list|)
throws|throws
name|IOException
block|{
name|double
name|distance1
init|=
name|geoDistanceValues
operator|.
name|computeDistance
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|Double
operator|.
name|compare
argument_list|(
name|distance1
argument_list|,
name|distance2
argument_list|)
return|;
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
name|values
index|[
name|slot
index|]
operator|=
name|geoDistanceValues
operator|.
name|computeDistance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
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
name|this
operator|.
name|bottom
operator|=
name|values
index|[
name|bottom
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|Double
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
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
name|values
index|[
name|slot
index|]
operator|+=
name|geoDistanceValues
operator|.
name|computeDistance
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|divide
specifier|public
name|void
name|divide
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|divisor
parameter_list|)
block|{
name|values
index|[
name|slot
index|]
operator|/=
name|divisor
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|missing
specifier|public
name|void
name|missing
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
name|values
index|[
name|slot
index|]
operator|=
name|MISSING_VALUE
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareBottomMissing
specifier|public
name|int
name|compareBottomMissing
parameter_list|()
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|bottom
argument_list|,
name|MISSING_VALUE
argument_list|)
return|;
block|}
comment|// Computes the distance based on geo points.
comment|// Due to this abstractions the geo distance comparator doesn't need to deal with whether fields have one
comment|// or multiple geo points per document.
DECL|class|GeoDistanceValues
specifier|private
specifier|static
specifier|abstract
class|class
name|GeoDistanceValues
block|{
DECL|field|readerValues
specifier|protected
specifier|final
name|GeoPointValues
name|readerValues
decl_stmt|;
DECL|field|fixedSourceDistance
specifier|protected
specifier|final
name|GeoDistance
operator|.
name|FixedSourceDistance
name|fixedSourceDistance
decl_stmt|;
DECL|method|GeoDistanceValues
specifier|protected
name|GeoDistanceValues
parameter_list|(
name|GeoPointValues
name|readerValues
parameter_list|,
name|GeoDistance
operator|.
name|FixedSourceDistance
name|fixedSourceDistance
parameter_list|)
block|{
name|this
operator|.
name|readerValues
operator|=
name|readerValues
expr_stmt|;
name|this
operator|.
name|fixedSourceDistance
operator|=
name|fixedSourceDistance
expr_stmt|;
block|}
DECL|method|computeDistance
specifier|public
specifier|abstract
name|double
name|computeDistance
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
block|}
comment|// Deals with one geo point per document
DECL|class|SV
specifier|private
specifier|static
specifier|final
class|class
name|SV
extends|extends
name|GeoDistanceValues
block|{
DECL|method|SV
name|SV
parameter_list|(
name|GeoPointValues
name|readerValues
parameter_list|,
name|GeoDistance
operator|.
name|FixedSourceDistance
name|fixedSourceDistance
parameter_list|)
block|{
name|super
argument_list|(
name|readerValues
argument_list|,
name|fixedSourceDistance
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeDistance
specifier|public
name|double
name|computeDistance
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|int
name|numValues
init|=
name|readerValues
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|double
name|result
init|=
name|MISSING_VALUE
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
name|numValues
condition|;
name|i
operator|++
control|)
block|{
name|GeoPoint
name|geoPoint
init|=
name|readerValues
operator|.
name|nextValue
argument_list|()
decl_stmt|;
return|return
name|fixedSourceDistance
operator|.
name|calculate
argument_list|(
name|geoPoint
operator|.
name|lat
argument_list|()
argument_list|,
name|geoPoint
operator|.
name|lon
argument_list|()
argument_list|)
return|;
block|}
return|return
name|MISSING_VALUE
return|;
block|}
block|}
comment|// Deals with more than one geo point per document
DECL|class|MV
specifier|private
specifier|static
specifier|final
class|class
name|MV
extends|extends
name|GeoDistanceValues
block|{
DECL|field|sortMode
specifier|private
specifier|final
name|SortMode
name|sortMode
decl_stmt|;
DECL|method|MV
name|MV
parameter_list|(
name|GeoPointValues
name|readerValues
parameter_list|,
name|GeoDistance
operator|.
name|FixedSourceDistance
name|fixedSourceDistance
parameter_list|,
name|SortMode
name|sortMode
parameter_list|)
block|{
name|super
argument_list|(
name|readerValues
argument_list|,
name|fixedSourceDistance
argument_list|)
expr_stmt|;
name|this
operator|.
name|sortMode
operator|=
name|sortMode
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeDistance
specifier|public
name|double
name|computeDistance
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|int
name|length
init|=
name|readerValues
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|double
name|distance
init|=
name|sortMode
operator|.
name|startDouble
argument_list|()
decl_stmt|;
name|double
name|result
init|=
name|MISSING_VALUE
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|GeoPoint
name|point
init|=
name|readerValues
operator|.
name|nextValue
argument_list|()
decl_stmt|;
name|result
operator|=
name|distance
operator|=
name|sortMode
operator|.
name|apply
argument_list|(
name|distance
argument_list|,
name|fixedSourceDistance
operator|.
name|calculate
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sortMode
operator|.
name|reduce
argument_list|(
name|result
argument_list|,
name|length
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

