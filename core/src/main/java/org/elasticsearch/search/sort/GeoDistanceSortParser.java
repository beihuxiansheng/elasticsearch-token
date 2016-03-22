begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.sort
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
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
name|LeafReaderContext
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
name|NumericDocValues
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
name|DocIdSetIterator
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
name|Query
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
operator|.
name|BitSetProducer
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
name|BitSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|GeoDistance
operator|.
name|FixedSourceDistance
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
name|geo
operator|.
name|GeoUtils
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
name|search
operator|.
name|Queries
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
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
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
name|IndexFieldData
operator|.
name|XFieldComparatorSource
operator|.
name|Nested
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
name|fielddata
operator|.
name|NumericDoubleValues
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
name|SortedNumericDoubleValues
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
name|MappedFieldType
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
name|query
operator|.
name|QueryShardContext
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
name|query
operator|.
name|support
operator|.
name|NestedInnerQueryParseSupport
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
name|MultiValueMode
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|GeoDistanceSortParser
specifier|public
class|class
name|GeoDistanceSortParser
implements|implements
name|SortParser
block|{
annotation|@
name|Override
DECL|method|names
specifier|public
name|String
index|[]
name|names
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"_geo_distance"
block|,
literal|"_geoDistance"
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|SortField
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|geoPoints
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|DistanceUnit
name|unit
init|=
name|DistanceUnit
operator|.
name|DEFAULT
decl_stmt|;
name|GeoDistance
name|geoDistance
init|=
name|GeoDistance
operator|.
name|DEFAULT
decl_stmt|;
name|boolean
name|reverse
init|=
literal|false
decl_stmt|;
name|MultiValueMode
name|sortMode
init|=
literal|null
decl_stmt|;
name|NestedInnerQueryParseSupport
name|nestedHelper
init|=
literal|null
decl_stmt|;
specifier|final
name|boolean
name|indexCreatedBeforeV2_0
init|=
name|context
operator|.
name|indexVersionCreated
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_2_0_0
argument_list|)
decl_stmt|;
name|boolean
name|coerce
init|=
name|GeoDistanceSortBuilder
operator|.
name|DEFAULT_COERCE
decl_stmt|;
name|boolean
name|ignoreMalformed
init|=
name|GeoDistanceSortBuilder
operator|.
name|DEFAULT_IGNORE_MALFORMED
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
name|GeoDistanceSortBuilder
operator|.
name|parseGeoPoints
argument_list|(
name|parser
argument_list|,
name|geoPoints
argument_list|)
expr_stmt|;
name|fieldName
operator|=
name|currentName
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
comment|// the json in the format of -> field : { lat : 30, lon : 12 }
if|if
condition|(
literal|"nested_filter"
operator|.
name|equals
argument_list|(
name|currentName
argument_list|)
operator|||
literal|"nestedFilter"
operator|.
name|equals
argument_list|(
name|currentName
argument_list|)
condition|)
block|{
if|if
condition|(
name|nestedHelper
operator|==
literal|null
condition|)
block|{
name|nestedHelper
operator|=
operator|new
name|NestedInnerQueryParseSupport
argument_list|(
name|parser
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
name|nestedHelper
operator|.
name|filter
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|fieldName
operator|=
name|currentName
expr_stmt|;
name|GeoPoint
name|point
init|=
operator|new
name|GeoPoint
argument_list|()
decl_stmt|;
name|GeoUtils
operator|.
name|parseGeoPoint
argument_list|(
name|parser
argument_list|,
name|point
argument_list|)
expr_stmt|;
name|geoPoints
operator|.
name|add
argument_list|(
name|point
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"reverse"
operator|.
name|equals
argument_list|(
name|currentName
argument_list|)
condition|)
block|{
name|reverse
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"order"
operator|.
name|equals
argument_list|(
name|currentName
argument_list|)
condition|)
block|{
name|reverse
operator|=
literal|"desc"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentName
operator|.
name|equals
argument_list|(
literal|"unit"
argument_list|)
condition|)
block|{
name|unit
operator|=
name|DistanceUnit
operator|.
name|fromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentName
operator|.
name|equals
argument_list|(
literal|"distance_type"
argument_list|)
operator|||
name|currentName
operator|.
name|equals
argument_list|(
literal|"distanceType"
argument_list|)
condition|)
block|{
name|geoDistance
operator|=
name|GeoDistance
operator|.
name|fromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"coerce"
operator|.
name|equals
argument_list|(
name|currentName
argument_list|)
operator|||
operator|(
name|indexCreatedBeforeV2_0
operator|&&
literal|"normalize"
operator|.
name|equals
argument_list|(
name|currentName
argument_list|)
operator|)
condition|)
block|{
name|coerce
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|coerce
operator|==
literal|true
condition|)
block|{
name|ignoreMalformed
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"ignore_malformed"
operator|.
name|equals
argument_list|(
name|currentName
argument_list|)
condition|)
block|{
name|boolean
name|ignoreMalformedFlag
init|=
name|parser
operator|.
name|booleanValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|coerce
operator|==
literal|false
condition|)
block|{
name|ignoreMalformed
operator|=
name|ignoreMalformedFlag
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"sort_mode"
operator|.
name|equals
argument_list|(
name|currentName
argument_list|)
operator|||
literal|"sortMode"
operator|.
name|equals
argument_list|(
name|currentName
argument_list|)
operator|||
literal|"mode"
operator|.
name|equals
argument_list|(
name|currentName
argument_list|)
condition|)
block|{
name|sortMode
operator|=
name|MultiValueMode
operator|.
name|fromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"nested_path"
operator|.
name|equals
argument_list|(
name|currentName
argument_list|)
operator|||
literal|"nestedPath"
operator|.
name|equals
argument_list|(
name|currentName
argument_list|)
condition|)
block|{
if|if
condition|(
name|nestedHelper
operator|==
literal|null
condition|)
block|{
name|nestedHelper
operator|=
operator|new
name|NestedInnerQueryParseSupport
argument_list|(
name|parser
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
name|nestedHelper
operator|.
name|setPath
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|GeoPoint
name|point
init|=
operator|new
name|GeoPoint
argument_list|()
decl_stmt|;
name|point
operator|.
name|resetFromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
name|geoPoints
operator|.
name|add
argument_list|(
name|point
argument_list|)
expr_stmt|;
name|fieldName
operator|=
name|currentName
expr_stmt|;
block|}
block|}
block|}
comment|// validation was not available prior to 2.x, so to support bwc percolation queries we only ignore_malformed on 2.x created indexes
if|if
condition|(
operator|!
name|indexCreatedBeforeV2_0
operator|&&
operator|!
name|ignoreMalformed
condition|)
block|{
for|for
control|(
name|GeoPoint
name|point
range|:
name|geoPoints
control|)
block|{
if|if
condition|(
name|point
operator|.
name|lat
argument_list|()
operator|>
literal|90.0
operator|||
name|point
operator|.
name|lat
argument_list|()
operator|<
operator|-
literal|90.0
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"illegal latitude value [{}] for [GeoDistanceSort]"
argument_list|,
name|point
operator|.
name|lat
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|point
operator|.
name|lon
argument_list|()
operator|>
literal|180.0
operator|||
name|point
operator|.
name|lon
argument_list|()
operator|<
operator|-
literal|180
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"illegal longitude value [{}] for [GeoDistanceSort]"
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|coerce
condition|)
block|{
for|for
control|(
name|GeoPoint
name|point
range|:
name|geoPoints
control|)
block|{
name|GeoUtils
operator|.
name|normalizePoint
argument_list|(
name|point
argument_list|,
name|coerce
argument_list|,
name|coerce
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sortMode
operator|==
literal|null
condition|)
block|{
name|sortMode
operator|=
name|reverse
condition|?
name|MultiValueMode
operator|.
name|MAX
else|:
name|MultiValueMode
operator|.
name|MIN
expr_stmt|;
block|}
if|if
condition|(
name|sortMode
operator|==
name|MultiValueMode
operator|.
name|SUM
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"sort_mode [sum] isn't supported for sorting by geo distance"
argument_list|)
throw|;
block|}
name|MappedFieldType
name|fieldType
init|=
name|context
operator|.
name|fieldMapper
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"failed to find mapper for ["
operator|+
name|fieldName
operator|+
literal|"] for geo distance based sort"
argument_list|)
throw|;
block|}
specifier|final
name|MultiValueMode
name|finalSortMode
init|=
name|sortMode
decl_stmt|;
comment|// final reference for use in the anonymous class
specifier|final
name|IndexGeoPointFieldData
name|geoIndexFieldData
init|=
name|context
operator|.
name|getForField
argument_list|(
name|fieldType
argument_list|)
decl_stmt|;
specifier|final
name|FixedSourceDistance
index|[]
name|distances
init|=
operator|new
name|FixedSourceDistance
index|[
name|geoPoints
operator|.
name|size
argument_list|()
index|]
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
name|geoPoints
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|distances
index|[
name|i
index|]
operator|=
name|geoDistance
operator|.
name|fixedSourceDistance
argument_list|(
name|geoPoints
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|lat
argument_list|()
argument_list|,
name|geoPoints
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|lon
argument_list|()
argument_list|,
name|unit
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Nested
name|nested
decl_stmt|;
if|if
condition|(
name|nestedHelper
operator|!=
literal|null
operator|&&
name|nestedHelper
operator|.
name|getPath
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|BitSetProducer
name|rootDocumentsFilter
init|=
name|context
operator|.
name|bitsetFilter
argument_list|(
name|Queries
operator|.
name|newNonNestedFilter
argument_list|()
argument_list|)
decl_stmt|;
name|Query
name|innerDocumentsQuery
decl_stmt|;
if|if
condition|(
name|nestedHelper
operator|.
name|filterFound
argument_list|()
condition|)
block|{
comment|// TODO: use queries instead
name|innerDocumentsQuery
operator|=
name|nestedHelper
operator|.
name|getInnerFilter
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|innerDocumentsQuery
operator|=
name|nestedHelper
operator|.
name|getNestedObjectMapper
argument_list|()
operator|.
name|nestedTypeFilter
argument_list|()
expr_stmt|;
block|}
name|nested
operator|=
operator|new
name|Nested
argument_list|(
name|rootDocumentsFilter
argument_list|,
name|innerDocumentsQuery
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nested
operator|=
literal|null
expr_stmt|;
block|}
name|IndexFieldData
operator|.
name|XFieldComparatorSource
name|geoDistanceComparatorSource
init|=
operator|new
name|IndexFieldData
operator|.
name|XFieldComparatorSource
argument_list|()
block|{
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|FieldComparator
argument_list|<
name|?
argument_list|>
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
name|FieldComparator
operator|.
name|DoubleComparator
argument_list|(
name|numHits
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|MultiGeoPointValues
name|geoPointValues
init|=
name|geoIndexFieldData
operator|.
name|load
argument_list|(
name|context
argument_list|)
operator|.
name|getGeoPointValues
argument_list|()
decl_stmt|;
specifier|final
name|SortedNumericDoubleValues
name|distanceValues
init|=
name|GeoDistance
operator|.
name|distanceValues
argument_list|(
name|geoPointValues
argument_list|,
name|distances
argument_list|)
decl_stmt|;
specifier|final
name|NumericDoubleValues
name|selectedValues
decl_stmt|;
if|if
condition|(
name|nested
operator|==
literal|null
condition|)
block|{
name|selectedValues
operator|=
name|finalSortMode
operator|.
name|select
argument_list|(
name|distanceValues
argument_list|,
name|Double
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|BitSet
name|rootDocs
init|=
name|nested
operator|.
name|rootDocs
argument_list|(
name|context
argument_list|)
decl_stmt|;
specifier|final
name|DocIdSetIterator
name|innerDocs
init|=
name|nested
operator|.
name|innerDocs
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|selectedValues
operator|=
name|finalSortMode
operator|.
name|select
argument_list|(
name|distanceValues
argument_list|,
name|Double
operator|.
name|MAX_VALUE
argument_list|,
name|rootDocs
argument_list|,
name|innerDocs
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|selectedValues
operator|.
name|getRawDoubleValues
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|SortField
argument_list|(
name|fieldName
argument_list|,
name|geoDistanceComparatorSource
argument_list|,
name|reverse
argument_list|)
return|;
block|}
block|}
end_class

end_unit

