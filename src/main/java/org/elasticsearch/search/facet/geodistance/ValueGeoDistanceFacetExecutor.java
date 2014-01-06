begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.geodistance
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|geodistance
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
name|IndexGeoPointFieldData
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
name|IndexNumericFieldData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SearchContext
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
comment|/**  *  */
end_comment

begin_class
DECL|class|ValueGeoDistanceFacetExecutor
specifier|public
class|class
name|ValueGeoDistanceFacetExecutor
extends|extends
name|GeoDistanceFacetExecutor
block|{
DECL|field|valueIndexFieldData
specifier|private
specifier|final
name|IndexNumericFieldData
name|valueIndexFieldData
decl_stmt|;
DECL|method|ValueGeoDistanceFacetExecutor
specifier|public
name|ValueGeoDistanceFacetExecutor
parameter_list|(
name|IndexGeoPointFieldData
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
name|GeoDistanceFacet
operator|.
name|Entry
index|[]
name|entries
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|IndexNumericFieldData
name|valueIndexFieldData
parameter_list|)
block|{
name|super
argument_list|(
name|indexFieldData
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|unit
argument_list|,
name|geoDistance
argument_list|,
name|entries
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|valueIndexFieldData
operator|=
name|valueIndexFieldData
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collector
specifier|public
name|Collector
name|collector
parameter_list|()
block|{
return|return
operator|new
name|Collector
argument_list|(
operator|new
name|Aggregator
argument_list|(
name|fixedSourceDistance
argument_list|,
name|entries
argument_list|)
argument_list|)
return|;
block|}
DECL|class|Collector
class|class
name|Collector
extends|extends
name|GeoDistanceFacetExecutor
operator|.
name|Collector
block|{
DECL|method|Collector
name|Collector
parameter_list|(
name|Aggregator
name|aggregator
parameter_list|)
block|{
name|super
argument_list|(
name|aggregator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Aggregator
operator|)
name|this
operator|.
name|aggregator
operator|)
operator|.
name|valueValues
operator|=
name|valueIndexFieldData
operator|.
name|load
argument_list|(
name|context
argument_list|)
operator|.
name|getDoubleValues
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Aggregator
specifier|public
specifier|static
class|class
name|Aggregator
extends|extends
name|GeoDistanceFacetExecutor
operator|.
name|Aggregator
block|{
DECL|field|valueValues
name|DoubleValues
name|valueValues
decl_stmt|;
DECL|method|Aggregator
specifier|public
name|Aggregator
parameter_list|(
name|GeoDistance
operator|.
name|FixedSourceDistance
name|fixedSourceDistance
parameter_list|,
name|GeoDistanceFacet
operator|.
name|Entry
index|[]
name|entries
parameter_list|)
block|{
name|super
argument_list|(
name|fixedSourceDistance
argument_list|,
name|entries
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collectGeoPoint
specifier|protected
name|void
name|collectGeoPoint
parameter_list|(
name|GeoDistanceFacet
operator|.
name|Entry
name|entry
parameter_list|,
name|int
name|docId
parameter_list|,
name|double
name|distance
parameter_list|)
block|{
name|entry
operator|.
name|foundInDoc
operator|=
literal|true
expr_stmt|;
name|entry
operator|.
name|count
operator|++
expr_stmt|;
name|int
name|seek
init|=
name|valueValues
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
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
name|seek
condition|;
name|i
operator|++
control|)
block|{
name|double
name|value
init|=
name|valueValues
operator|.
name|nextValue
argument_list|()
decl_stmt|;
name|entry
operator|.
name|totalCount
operator|++
expr_stmt|;
name|entry
operator|.
name|total
operator|+=
name|value
expr_stmt|;
if|if
condition|(
name|value
operator|<
name|entry
operator|.
name|min
condition|)
block|{
name|entry
operator|.
name|min
operator|=
name|value
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|>
name|entry
operator|.
name|max
condition|)
block|{
name|entry
operator|.
name|max
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

