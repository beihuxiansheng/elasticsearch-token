begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|search
operator|.
name|DocIdSet
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
name|Filter
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
name|Bits
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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
name|Nullable
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
name|lucene
operator|.
name|docset
operator|.
name|AndDocIdSet
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
name|lucene
operator|.
name|docset
operator|.
name|DocIdSets
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
name|lucene
operator|.
name|docset
operator|.
name|MatchDocIdSet
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
name|MultiGeoPointValues
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
name|GeoPointFieldMapper
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
DECL|class|GeoDistanceFilter
specifier|public
class|class
name|GeoDistanceFilter
extends|extends
name|Filter
block|{
DECL|field|lat
specifier|private
specifier|final
name|double
name|lat
decl_stmt|;
DECL|field|lon
specifier|private
specifier|final
name|double
name|lon
decl_stmt|;
DECL|field|distance
specifier|private
specifier|final
name|double
name|distance
decl_stmt|;
comment|// in miles
DECL|field|geoDistance
specifier|private
specifier|final
name|GeoDistance
name|geoDistance
decl_stmt|;
DECL|field|indexFieldData
specifier|private
specifier|final
name|IndexGeoPointFieldData
name|indexFieldData
decl_stmt|;
DECL|field|fixedSourceDistance
specifier|private
specifier|final
name|GeoDistance
operator|.
name|FixedSourceDistance
name|fixedSourceDistance
decl_stmt|;
DECL|field|distanceBoundingCheck
specifier|private
specifier|final
name|GeoDistance
operator|.
name|DistanceBoundingCheck
name|distanceBoundingCheck
decl_stmt|;
DECL|field|boundingBoxFilter
specifier|private
specifier|final
name|Filter
name|boundingBoxFilter
decl_stmt|;
DECL|method|GeoDistanceFilter
specifier|public
name|GeoDistanceFilter
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|,
name|double
name|distance
parameter_list|,
name|GeoDistance
name|geoDistance
parameter_list|,
name|IndexGeoPointFieldData
name|indexFieldData
parameter_list|,
name|GeoPointFieldMapper
name|mapper
parameter_list|,
name|String
name|optimizeBbox
parameter_list|)
block|{
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
name|distance
operator|=
name|distance
expr_stmt|;
name|this
operator|.
name|geoDistance
operator|=
name|geoDistance
expr_stmt|;
name|this
operator|.
name|indexFieldData
operator|=
name|indexFieldData
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
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|GeoDistance
operator|.
name|DistanceBoundingCheck
name|distanceBoundingCheck
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|optimizeBbox
operator|!=
literal|null
operator|&&
operator|!
literal|"none"
operator|.
name|equals
argument_list|(
name|optimizeBbox
argument_list|)
condition|)
block|{
name|distanceBoundingCheck
operator|=
name|GeoDistance
operator|.
name|distanceBoundingCheck
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|distance
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"memory"
operator|.
name|equals
argument_list|(
name|optimizeBbox
argument_list|)
condition|)
block|{
name|boundingBoxFilter
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"indexed"
operator|.
name|equals
argument_list|(
name|optimizeBbox
argument_list|)
condition|)
block|{
name|boundingBoxFilter
operator|=
name|IndexedGeoBoundingBoxFilter
operator|.
name|create
argument_list|(
name|distanceBoundingCheck
operator|.
name|topLeft
argument_list|()
argument_list|,
name|distanceBoundingCheck
operator|.
name|bottomRight
argument_list|()
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
name|distanceBoundingCheck
operator|=
name|GeoDistance
operator|.
name|ALWAYS_INSTANCE
expr_stmt|;
comment|// fine, we do the bounding box check using the filter
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"type ["
operator|+
name|optimizeBbox
operator|+
literal|"] for bounding box optimization not supported"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|distanceBoundingCheck
operator|=
name|GeoDistance
operator|.
name|ALWAYS_INSTANCE
expr_stmt|;
name|boundingBoxFilter
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|distanceBoundingCheck
operator|=
name|distanceBoundingCheck
expr_stmt|;
block|}
DECL|method|lat
specifier|public
name|double
name|lat
parameter_list|()
block|{
return|return
name|lat
return|;
block|}
DECL|method|lon
specifier|public
name|double
name|lon
parameter_list|()
block|{
return|return
name|lon
return|;
block|}
DECL|method|distance
specifier|public
name|double
name|distance
parameter_list|()
block|{
return|return
name|distance
return|;
block|}
DECL|method|geoDistance
specifier|public
name|GeoDistance
name|geoDistance
parameter_list|()
block|{
return|return
name|geoDistance
return|;
block|}
DECL|method|fieldName
specifier|public
name|String
name|fieldName
parameter_list|()
block|{
return|return
name|indexFieldData
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptedDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|DocIdSet
name|boundingBoxDocSet
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|boundingBoxFilter
operator|!=
literal|null
condition|)
block|{
name|boundingBoxDocSet
operator|=
name|boundingBoxFilter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|acceptedDocs
argument_list|)
expr_stmt|;
if|if
condition|(
name|DocIdSets
operator|.
name|isEmpty
argument_list|(
name|boundingBoxDocSet
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|final
name|MultiGeoPointValues
name|values
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
name|GeoDistanceDocSet
name|distDocSet
init|=
operator|new
name|GeoDistanceDocSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|acceptedDocs
argument_list|,
name|values
argument_list|,
name|fixedSourceDistance
argument_list|,
name|distanceBoundingCheck
argument_list|,
name|distance
argument_list|)
decl_stmt|;
if|if
condition|(
name|boundingBoxDocSet
operator|==
literal|null
condition|)
block|{
return|return
name|distDocSet
return|;
block|}
else|else
block|{
return|return
operator|new
name|AndDocIdSet
argument_list|(
operator|new
name|DocIdSet
index|[]
block|{
name|boundingBoxDocSet
block|,
name|distDocSet
block|}
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|GeoDistanceFilter
name|filter
init|=
operator|(
name|GeoDistanceFilter
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|filter
operator|.
name|distance
argument_list|,
name|distance
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|filter
operator|.
name|lat
argument_list|,
name|lat
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|filter
operator|.
name|lon
argument_list|,
name|lon
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|indexFieldData
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexName
argument_list|()
operator|.
name|equals
argument_list|(
name|filter
operator|.
name|indexFieldData
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|geoDistance
operator|!=
name|filter
operator|.
name|geoDistance
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"GeoDistanceFilter("
operator|+
name|indexFieldData
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexName
argument_list|()
operator|+
literal|", "
operator|+
name|geoDistance
operator|+
literal|", "
operator|+
name|distance
operator|+
literal|", "
operator|+
name|lat
operator|+
literal|", "
operator|+
name|lon
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
decl_stmt|;
name|long
name|temp
decl_stmt|;
name|temp
operator|=
name|lat
operator|!=
operator|+
literal|0.0d
condition|?
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|lat
argument_list|)
else|:
literal|0L
expr_stmt|;
name|result
operator|=
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|lon
operator|!=
operator|+
literal|0.0d
condition|?
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|lon
argument_list|)
else|:
literal|0L
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|distance
operator|!=
operator|+
literal|0.0d
condition|?
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|distance
argument_list|)
else|:
literal|0L
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|geoDistance
operator|!=
literal|null
condition|?
name|geoDistance
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|indexFieldData
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexName
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|class|GeoDistanceDocSet
specifier|public
specifier|static
class|class
name|GeoDistanceDocSet
extends|extends
name|MatchDocIdSet
block|{
DECL|field|distance
specifier|private
specifier|final
name|double
name|distance
decl_stmt|;
comment|// in miles
DECL|field|values
specifier|private
specifier|final
name|MultiGeoPointValues
name|values
decl_stmt|;
DECL|field|fixedSourceDistance
specifier|private
specifier|final
name|GeoDistance
operator|.
name|FixedSourceDistance
name|fixedSourceDistance
decl_stmt|;
DECL|field|distanceBoundingCheck
specifier|private
specifier|final
name|GeoDistance
operator|.
name|DistanceBoundingCheck
name|distanceBoundingCheck
decl_stmt|;
DECL|method|GeoDistanceDocSet
specifier|public
name|GeoDistanceDocSet
parameter_list|(
name|int
name|maxDoc
parameter_list|,
annotation|@
name|Nullable
name|Bits
name|acceptDocs
parameter_list|,
name|MultiGeoPointValues
name|values
parameter_list|,
name|GeoDistance
operator|.
name|FixedSourceDistance
name|fixedSourceDistance
parameter_list|,
name|GeoDistance
operator|.
name|DistanceBoundingCheck
name|distanceBoundingCheck
parameter_list|,
name|double
name|distance
parameter_list|)
block|{
name|super
argument_list|(
name|maxDoc
argument_list|,
name|acceptDocs
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
name|fixedSourceDistance
operator|=
name|fixedSourceDistance
expr_stmt|;
name|this
operator|.
name|distanceBoundingCheck
operator|=
name|distanceBoundingCheck
expr_stmt|;
name|this
operator|.
name|distance
operator|=
name|distance
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|matchDoc
specifier|protected
name|boolean
name|matchDoc
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|int
name|length
init|=
name|values
operator|.
name|count
argument_list|()
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
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|distanceBoundingCheck
operator|.
name|isWithin
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
condition|)
block|{
name|double
name|d
init|=
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
decl_stmt|;
if|if
condition|(
name|d
operator|<
name|distance
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

