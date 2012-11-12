begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search.geo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
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
name|index
operator|.
name|IndexReader
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
name|search
operator|.
name|SortField
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
name|cache
operator|.
name|field
operator|.
name|data
operator|.
name|FieldDataCache
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
name|FieldDataType
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
name|mapper
operator|.
name|FieldMapper
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
name|mapper
operator|.
name|MapperService
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
name|mapper
operator|.
name|geo
operator|.
name|GeoPointFieldData
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
name|mapper
operator|.
name|geo
operator|.
name|GeoPointFieldDataType
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

begin_comment
comment|// LUCENE MONITOR: Monitor against FieldComparator.Double
end_comment

begin_class
DECL|class|GeoDistanceDataComparator
specifier|public
class|class
name|GeoDistanceDataComparator
extends|extends
name|FieldComparator
argument_list|<
name|Double
argument_list|>
block|{
DECL|method|comparatorSource
specifier|public
specifier|static
name|FieldDataType
operator|.
name|ExtendedFieldComparatorSource
name|comparatorSource
parameter_list|(
name|String
name|fieldName
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
name|FieldDataCache
name|fieldDataCache
parameter_list|,
name|MapperService
name|mapperService
parameter_list|)
block|{
return|return
operator|new
name|InnerSource
argument_list|(
name|fieldName
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|unit
argument_list|,
name|geoDistance
argument_list|,
name|fieldDataCache
argument_list|,
name|mapperService
argument_list|)
return|;
block|}
DECL|class|InnerSource
specifier|static
class|class
name|InnerSource
extends|extends
name|FieldDataType
operator|.
name|ExtendedFieldComparatorSource
block|{
DECL|field|fieldName
specifier|protected
specifier|final
name|String
name|fieldName
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
DECL|field|fieldDataCache
specifier|protected
specifier|final
name|FieldDataCache
name|fieldDataCache
decl_stmt|;
DECL|field|mapperService
specifier|private
specifier|final
name|MapperService
name|mapperService
decl_stmt|;
DECL|method|InnerSource
specifier|private
name|InnerSource
parameter_list|(
name|String
name|fieldName
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
name|FieldDataCache
name|fieldDataCache
parameter_list|,
name|MapperService
name|mapperService
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
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
name|fieldDataCache
operator|=
name|fieldDataCache
expr_stmt|;
name|this
operator|.
name|mapperService
operator|=
name|mapperService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newComparator
specifier|public
name|FieldComparator
name|newComparator
parameter_list|(
name|String
name|fieldname
parameter_list|,
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|,
name|boolean
name|reversed
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|GeoDistanceDataComparator
argument_list|(
name|numHits
argument_list|,
name|fieldname
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|unit
argument_list|,
name|geoDistance
argument_list|,
name|fieldDataCache
argument_list|,
name|mapperService
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|reducedType
specifier|public
name|SortField
operator|.
name|Type
name|reducedType
parameter_list|()
block|{
return|return
name|SortField
operator|.
name|Type
operator|.
name|DOUBLE
return|;
block|}
block|}
DECL|field|fieldName
specifier|protected
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|indexFieldName
specifier|protected
specifier|final
name|String
name|indexFieldName
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
DECL|field|fieldDataCache
specifier|protected
specifier|final
name|FieldDataCache
name|fieldDataCache
decl_stmt|;
DECL|field|fieldData
specifier|protected
name|GeoPointFieldData
name|fieldData
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
DECL|method|GeoDistanceDataComparator
specifier|public
name|GeoDistanceDataComparator
parameter_list|(
name|int
name|numHits
parameter_list|,
name|String
name|fieldName
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
name|FieldDataCache
name|fieldDataCache
parameter_list|,
name|MapperService
name|mapperService
parameter_list|)
block|{
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
name|fieldName
operator|=
name|fieldName
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
name|fieldDataCache
operator|=
name|fieldDataCache
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
name|FieldMapper
name|mapper
init|=
name|mapperService
operator|.
name|smartNameFieldMapper
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapper
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No mapping found for field ["
operator|+
name|fieldName
operator|+
literal|"] for geo distance sort"
argument_list|)
throw|;
block|}
if|if
condition|(
name|mapper
operator|.
name|fieldDataType
argument_list|()
operator|!=
name|GeoPointFieldDataType
operator|.
name|TYPE
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"field ["
operator|+
name|fieldName
operator|+
literal|"] is not a geo_point field"
argument_list|)
throw|;
block|}
name|this
operator|.
name|indexFieldName
operator|=
name|mapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|GeoDistanceDataComparator
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldData
operator|=
operator|(
name|GeoPointFieldData
operator|)
name|fieldDataCache
operator|.
name|cache
argument_list|(
name|GeoPointFieldDataType
operator|.
name|TYPE
argument_list|,
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|indexFieldName
argument_list|)
expr_stmt|;
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
specifier|final
name|double
name|v1
init|=
name|values
index|[
name|slot1
index|]
decl_stmt|;
specifier|final
name|double
name|v2
init|=
name|values
index|[
name|slot2
index|]
decl_stmt|;
if|if
condition|(
name|v1
operator|>
name|v2
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|v1
operator|<
name|v2
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
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
name|double
name|distance
decl_stmt|;
if|if
condition|(
operator|!
name|fieldData
operator|.
name|hasValue
argument_list|(
name|doc
argument_list|)
condition|)
block|{
comment|// is this true? push this to the "end"
name|distance
operator|=
name|Double
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|distance
operator|=
name|fixedSourceDistance
operator|.
name|calculate
argument_list|(
name|fieldData
operator|.
name|latValue
argument_list|(
name|doc
argument_list|)
argument_list|,
name|fieldData
operator|.
name|lonValue
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|double
name|v2
init|=
name|distance
decl_stmt|;
if|if
condition|(
name|bottom
operator|>
name|v2
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|bottom
operator|<
name|v2
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
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
decl_stmt|;
if|if
condition|(
operator|!
name|fieldData
operator|.
name|hasValue
argument_list|(
name|doc
argument_list|)
condition|)
block|{
comment|// is this true? push this to the "end"
name|distance1
operator|=
name|Double
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|distance1
operator|=
name|fixedSourceDistance
operator|.
name|calculate
argument_list|(
name|fieldData
operator|.
name|latValue
argument_list|(
name|doc
argument_list|)
argument_list|,
name|fieldData
operator|.
name|lonValue
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
call|(
name|int
call|)
argument_list|(
name|distance1
operator|-
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
name|double
name|distance
decl_stmt|;
if|if
condition|(
operator|!
name|fieldData
operator|.
name|hasValue
argument_list|(
name|doc
argument_list|)
condition|)
block|{
comment|// is this true? push this to the "end"
name|distance
operator|=
name|Double
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|distance
operator|=
name|fixedSourceDistance
operator|.
name|calculate
argument_list|(
name|fieldData
operator|.
name|latValue
argument_list|(
name|doc
argument_list|)
argument_list|,
name|fieldData
operator|.
name|lonValue
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|values
index|[
name|slot
index|]
operator|=
name|distance
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
block|}
end_class

end_unit

