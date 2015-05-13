begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|search
operator|.
name|Query
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
name|inject
operator|.
name|Inject
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
name|GeoPointFieldMapper
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
name|InMemoryGeoBoundingBoxQuery
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
name|IndexedGeoBoundingBoxQuery
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
DECL|class|GeoBoundingBoxQueryParser
specifier|public
class|class
name|GeoBoundingBoxQueryParser
implements|implements
name|QueryParser
block|{
DECL|field|TOP
specifier|public
specifier|static
specifier|final
name|String
name|TOP
init|=
literal|"top"
decl_stmt|;
DECL|field|LEFT
specifier|public
specifier|static
specifier|final
name|String
name|LEFT
init|=
literal|"left"
decl_stmt|;
DECL|field|RIGHT
specifier|public
specifier|static
specifier|final
name|String
name|RIGHT
init|=
literal|"right"
decl_stmt|;
DECL|field|BOTTOM
specifier|public
specifier|static
specifier|final
name|String
name|BOTTOM
init|=
literal|"bottom"
decl_stmt|;
DECL|field|TOP_LEFT
specifier|public
specifier|static
specifier|final
name|String
name|TOP_LEFT
init|=
name|TOP
operator|+
literal|"_"
operator|+
name|LEFT
decl_stmt|;
DECL|field|TOP_RIGHT
specifier|public
specifier|static
specifier|final
name|String
name|TOP_RIGHT
init|=
name|TOP
operator|+
literal|"_"
operator|+
name|RIGHT
decl_stmt|;
DECL|field|BOTTOM_LEFT
specifier|public
specifier|static
specifier|final
name|String
name|BOTTOM_LEFT
init|=
name|BOTTOM
operator|+
literal|"_"
operator|+
name|LEFT
decl_stmt|;
DECL|field|BOTTOM_RIGHT
specifier|public
specifier|static
specifier|final
name|String
name|BOTTOM_RIGHT
init|=
name|BOTTOM
operator|+
literal|"_"
operator|+
name|RIGHT
decl_stmt|;
DECL|field|TOPLEFT
specifier|public
specifier|static
specifier|final
name|String
name|TOPLEFT
init|=
literal|"topLeft"
decl_stmt|;
DECL|field|TOPRIGHT
specifier|public
specifier|static
specifier|final
name|String
name|TOPRIGHT
init|=
literal|"topRight"
decl_stmt|;
DECL|field|BOTTOMLEFT
specifier|public
specifier|static
specifier|final
name|String
name|BOTTOMLEFT
init|=
literal|"bottomLeft"
decl_stmt|;
DECL|field|BOTTOMRIGHT
specifier|public
specifier|static
specifier|final
name|String
name|BOTTOMRIGHT
init|=
literal|"bottomRight"
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"geo_bbox"
decl_stmt|;
DECL|field|FIELD
specifier|public
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"field"
decl_stmt|;
annotation|@
name|Inject
DECL|method|GeoBoundingBoxQueryParser
specifier|public
name|GeoBoundingBoxQueryParser
parameter_list|()
block|{     }
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
name|NAME
block|,
literal|"geoBbox"
block|,
literal|"geo_bounding_box"
block|,
literal|"geoBoundingBox"
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
name|double
name|top
init|=
name|Double
operator|.
name|NaN
decl_stmt|;
name|double
name|bottom
init|=
name|Double
operator|.
name|NaN
decl_stmt|;
name|double
name|left
init|=
name|Double
operator|.
name|NaN
decl_stmt|;
name|double
name|right
init|=
name|Double
operator|.
name|NaN
decl_stmt|;
name|String
name|queryName
init|=
literal|null
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|boolean
name|normalize
init|=
literal|true
decl_stmt|;
name|GeoPoint
name|sparse
init|=
operator|new
name|GeoPoint
argument_list|()
decl_stmt|;
name|String
name|type
init|=
literal|"memory"
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
name|currentFieldName
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
name|START_OBJECT
condition|)
block|{
name|fieldName
operator|=
name|currentFieldName
expr_stmt|;
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
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|parseContext
operator|.
name|isDeprecatedSetting
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
comment|// skip
block|}
elseif|else
if|if
condition|(
name|FIELD
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|fieldName
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|TOP
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|top
operator|=
name|parser
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|BOTTOM
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|bottom
operator|=
name|parser
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|LEFT
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|left
operator|=
name|parser
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|RIGHT
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|right
operator|=
name|parser
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|TOP_LEFT
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
name|TOPLEFT
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|GeoUtils
operator|.
name|parseGeoPoint
argument_list|(
name|parser
argument_list|,
name|sparse
argument_list|)
expr_stmt|;
name|top
operator|=
name|sparse
operator|.
name|getLat
argument_list|()
expr_stmt|;
name|left
operator|=
name|sparse
operator|.
name|getLon
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|BOTTOM_RIGHT
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
name|BOTTOMRIGHT
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|GeoUtils
operator|.
name|parseGeoPoint
argument_list|(
name|parser
argument_list|,
name|sparse
argument_list|)
expr_stmt|;
name|bottom
operator|=
name|sparse
operator|.
name|getLat
argument_list|()
expr_stmt|;
name|right
operator|=
name|sparse
operator|.
name|getLon
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|TOP_RIGHT
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
name|TOPRIGHT
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|GeoUtils
operator|.
name|parseGeoPoint
argument_list|(
name|parser
argument_list|,
name|sparse
argument_list|)
expr_stmt|;
name|top
operator|=
name|sparse
operator|.
name|getLat
argument_list|()
expr_stmt|;
name|right
operator|=
name|sparse
operator|.
name|getLon
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|BOTTOM_LEFT
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
name|BOTTOMLEFT
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|GeoUtils
operator|.
name|parseGeoPoint
argument_list|(
name|parser
argument_list|,
name|sparse
argument_list|)
expr_stmt|;
name|bottom
operator|=
name|sparse
operator|.
name|getLat
argument_list|()
expr_stmt|;
name|left
operator|=
name|sparse
operator|.
name|getLon
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Unexpected field ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"fieldname expected but ["
operator|+
name|token
operator|+
literal|"] found"
argument_list|)
throw|;
block|}
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
literal|"_name"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|queryName
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"normalize"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|normalize
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
literal|"type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|type
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[geo_bbox] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
specifier|final
name|GeoPoint
name|topLeft
init|=
name|sparse
operator|.
name|reset
argument_list|(
name|top
argument_list|,
name|left
argument_list|)
decl_stmt|;
comment|//just keep the object
specifier|final
name|GeoPoint
name|bottomRight
init|=
operator|new
name|GeoPoint
argument_list|(
name|bottom
argument_list|,
name|right
argument_list|)
decl_stmt|;
if|if
condition|(
name|normalize
condition|)
block|{
comment|// Special case: if the difference bettween the left and right is 360 and the right is greater than the left, we are asking for
comment|// the complete longitude range so need to set longitude to the complete longditude range
name|boolean
name|completeLonRange
init|=
operator|(
operator|(
name|right
operator|-
name|left
operator|)
operator|%
literal|360
operator|==
literal|0
operator|&&
name|right
operator|>
name|left
operator|)
decl_stmt|;
name|GeoUtils
operator|.
name|normalizePoint
argument_list|(
name|topLeft
argument_list|,
literal|true
argument_list|,
operator|!
name|completeLonRange
argument_list|)
expr_stmt|;
name|GeoUtils
operator|.
name|normalizePoint
argument_list|(
name|bottomRight
argument_list|,
literal|true
argument_list|,
operator|!
name|completeLonRange
argument_list|)
expr_stmt|;
if|if
condition|(
name|completeLonRange
condition|)
block|{
name|topLeft
operator|.
name|resetLon
argument_list|(
operator|-
literal|180
argument_list|)
expr_stmt|;
name|bottomRight
operator|.
name|resetLon
argument_list|(
literal|180
argument_list|)
expr_stmt|;
block|}
block|}
name|MapperService
operator|.
name|SmartNameFieldMappers
name|smartMappers
init|=
name|parseContext
operator|.
name|smartFieldMappers
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|smartMappers
operator|==
literal|null
operator|||
operator|!
name|smartMappers
operator|.
name|hasMapper
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"failed to find geo_point field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|mapper
init|=
name|smartMappers
operator|.
name|mapper
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|mapper
operator|instanceof
name|GeoPointFieldMapper
operator|)
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"field ["
operator|+
name|fieldName
operator|+
literal|"] is not a geo_point field"
argument_list|)
throw|;
block|}
name|GeoPointFieldMapper
name|geoMapper
init|=
operator|(
operator|(
name|GeoPointFieldMapper
operator|)
name|mapper
operator|)
decl_stmt|;
name|Query
name|filter
decl_stmt|;
if|if
condition|(
literal|"indexed"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|filter
operator|=
name|IndexedGeoBoundingBoxQuery
operator|.
name|create
argument_list|(
name|topLeft
argument_list|,
name|bottomRight
argument_list|,
name|geoMapper
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"memory"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|IndexGeoPointFieldData
name|indexFieldData
init|=
name|parseContext
operator|.
name|getForField
argument_list|(
name|mapper
argument_list|)
decl_stmt|;
name|filter
operator|=
operator|new
name|InMemoryGeoBoundingBoxQuery
argument_list|(
name|topLeft
argument_list|,
name|bottomRight
argument_list|,
name|indexFieldData
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"geo bounding box type ["
operator|+
name|type
operator|+
literal|"] not supported, either 'indexed' or 'memory' are allowed"
argument_list|)
throw|;
block|}
if|if
condition|(
name|queryName
operator|!=
literal|null
condition|)
block|{
name|parseContext
operator|.
name|addNamedQuery
argument_list|(
name|queryName
argument_list|,
name|filter
argument_list|)
expr_stmt|;
block|}
return|return
name|filter
return|;
block|}
block|}
end_class

end_unit

