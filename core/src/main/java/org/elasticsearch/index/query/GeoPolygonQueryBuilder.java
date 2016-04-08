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
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geopoint
operator|.
name|document
operator|.
name|GeoPointField
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
name|spatial
operator|.
name|geopoint
operator|.
name|search
operator|.
name|GeoPointInPolygonQuery
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
name|ParseField
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
name|ParsingException
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
name|Strings
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|XContentBuilder
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
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
operator|.
name|Token
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
name|mapper
operator|.
name|geo
operator|.
name|BaseGeoPointFieldMapper
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
name|GeoPolygonQuery
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_class
DECL|class|GeoPolygonQueryBuilder
specifier|public
class|class
name|GeoPolygonQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|GeoPolygonQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"geo_polygon"
decl_stmt|;
DECL|field|QUERY_NAME_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|QUERY_NAME_FIELD
init|=
operator|new
name|ParseField
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
DECL|field|COERCE_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|COERCE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"coerce"
argument_list|,
literal|"normalize"
argument_list|)
decl_stmt|;
DECL|field|IGNORE_MALFORMED_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|IGNORE_MALFORMED_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"ignore_malformed"
argument_list|)
decl_stmt|;
DECL|field|VALIDATION_METHOD
specifier|private
specifier|static
specifier|final
name|ParseField
name|VALIDATION_METHOD
init|=
operator|new
name|ParseField
argument_list|(
literal|"validation_method"
argument_list|)
decl_stmt|;
DECL|field|POINTS_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|POINTS_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"points"
argument_list|)
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|shell
specifier|private
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|shell
decl_stmt|;
DECL|field|validationMethod
specifier|private
name|GeoValidationMethod
name|validationMethod
init|=
name|GeoValidationMethod
operator|.
name|DEFAULT
decl_stmt|;
DECL|method|GeoPolygonQueryBuilder
specifier|public
name|GeoPolygonQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|points
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isEmpty
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fieldName must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|points
operator|==
literal|null
operator|||
name|points
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polygon must not be null or empty"
argument_list|)
throw|;
block|}
else|else
block|{
name|GeoPoint
name|start
init|=
name|points
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|start
operator|.
name|equals
argument_list|(
name|points
operator|.
name|get
argument_list|(
name|points
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
condition|)
block|{
if|if
condition|(
name|points
operator|.
name|size
argument_list|()
operator|<
literal|4
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"too few points defined for geo_polygon query"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|points
operator|.
name|size
argument_list|()
operator|<
literal|3
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"too few points defined for geo_polygon query"
argument_list|)
throw|;
block|}
block|}
block|}
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|shell
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|points
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|shell
operator|.
name|get
argument_list|(
name|shell
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|equals
argument_list|(
name|shell
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
name|shell
operator|.
name|add
argument_list|(
name|shell
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Read from a stream.      */
DECL|method|GeoPolygonQueryBuilder
specifier|public
name|GeoPolygonQueryBuilder
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|fieldName
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|shell
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|size
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|shell
operator|.
name|add
argument_list|(
name|in
operator|.
name|readGeoPoint
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|validationMethod
operator|=
name|GeoValidationMethod
operator|.
name|readFromStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|protected
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeString
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|shell
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|GeoPoint
name|point
range|:
name|shell
control|)
block|{
name|out
operator|.
name|writeGeoPoint
argument_list|(
name|point
argument_list|)
expr_stmt|;
block|}
name|validationMethod
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|fieldName
specifier|public
name|String
name|fieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
DECL|method|points
specifier|public
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|points
parameter_list|()
block|{
return|return
name|shell
return|;
block|}
comment|/** Sets the validation method to use for geo coordinates. */
DECL|method|setValidationMethod
specifier|public
name|GeoPolygonQueryBuilder
name|setValidationMethod
parameter_list|(
name|GeoValidationMethod
name|method
parameter_list|)
block|{
name|this
operator|.
name|validationMethod
operator|=
name|method
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns the validation method to use for geo coordinates. */
DECL|method|getValidationMethod
specifier|public
name|GeoValidationMethod
name|getValidationMethod
parameter_list|()
block|{
return|return
name|this
operator|.
name|validationMethod
return|;
block|}
annotation|@
name|Override
DECL|method|doToQuery
specifier|protected
name|Query
name|doToQuery
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
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
name|QueryShardException
argument_list|(
name|context
argument_list|,
literal|"failed to find geo_point field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|fieldType
operator|instanceof
name|BaseGeoPointFieldMapper
operator|.
name|GeoPointFieldType
operator|)
condition|)
block|{
throw|throw
operator|new
name|QueryShardException
argument_list|(
name|context
argument_list|,
literal|"field ["
operator|+
name|fieldName
operator|+
literal|"] is not a geo_point field"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|shell
init|=
operator|new
name|ArrayList
argument_list|<
name|GeoPoint
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|GeoPoint
name|geoPoint
range|:
name|this
operator|.
name|shell
control|)
block|{
name|shell
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|geoPoint
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|shellSize
init|=
name|shell
operator|.
name|size
argument_list|()
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
comment|// validation was not available prior to 2.x, so to support bwc
comment|// percolation queries we only ignore_malformed on 2.x created indexes
if|if
condition|(
operator|!
name|indexCreatedBeforeV2_0
operator|&&
operator|!
name|GeoValidationMethod
operator|.
name|isIgnoreMalformed
argument_list|(
name|validationMethod
argument_list|)
condition|)
block|{
for|for
control|(
name|GeoPoint
name|point
range|:
name|shell
control|)
block|{
if|if
condition|(
operator|!
name|GeoUtils
operator|.
name|isValidLatitude
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|QueryShardException
argument_list|(
name|context
argument_list|,
literal|"illegal latitude value [{}] for [{}]"
argument_list|,
name|point
operator|.
name|lat
argument_list|()
argument_list|,
name|GeoPolygonQueryBuilder
operator|.
name|NAME
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|GeoUtils
operator|.
name|isValidLongitude
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|QueryShardException
argument_list|(
name|context
argument_list|,
literal|"illegal longitude value [{}] for [{}]"
argument_list|,
name|point
operator|.
name|lon
argument_list|()
argument_list|,
name|GeoPolygonQueryBuilder
operator|.
name|NAME
argument_list|)
throw|;
block|}
block|}
block|}
specifier|final
name|Version
name|indexVersionCreated
init|=
name|context
operator|.
name|indexVersionCreated
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexVersionCreated
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_2_2_0
argument_list|)
operator|||
name|GeoValidationMethod
operator|.
name|isCoerce
argument_list|(
name|validationMethod
argument_list|)
condition|)
block|{
for|for
control|(
name|GeoPoint
name|point
range|:
name|shell
control|)
block|{
name|GeoUtils
operator|.
name|normalizePoint
argument_list|(
name|point
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|indexVersionCreated
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_2_2_0
argument_list|)
condition|)
block|{
name|IndexGeoPointFieldData
name|indexFieldData
init|=
name|context
operator|.
name|getForField
argument_list|(
name|fieldType
argument_list|)
decl_stmt|;
return|return
operator|new
name|GeoPolygonQuery
argument_list|(
name|indexFieldData
argument_list|,
name|shell
operator|.
name|toArray
argument_list|(
operator|new
name|GeoPoint
index|[
name|shellSize
index|]
argument_list|)
argument_list|)
return|;
block|}
name|double
index|[]
name|lats
init|=
operator|new
name|double
index|[
name|shellSize
index|]
decl_stmt|;
name|double
index|[]
name|lons
init|=
operator|new
name|double
index|[
name|shellSize
index|]
decl_stmt|;
name|GeoPoint
name|p
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
name|shellSize
condition|;
operator|++
name|i
control|)
block|{
name|p
operator|=
operator|new
name|GeoPoint
argument_list|(
name|shell
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|lats
index|[
name|i
index|]
operator|=
name|p
operator|.
name|lat
argument_list|()
expr_stmt|;
name|lons
index|[
name|i
index|]
operator|=
name|p
operator|.
name|lon
argument_list|()
expr_stmt|;
block|}
comment|// if index created V_2_2 use (soon to be legacy) numeric encoding postings format
comment|// if index created V_2_3> use prefix encoded postings format
specifier|final
name|GeoPointField
operator|.
name|TermEncoding
name|encoding
init|=
operator|(
name|indexVersionCreated
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_2_3_0
argument_list|)
operator|)
condition|?
name|GeoPointField
operator|.
name|TermEncoding
operator|.
name|NUMERIC
else|:
name|GeoPointField
operator|.
name|TermEncoding
operator|.
name|PREFIX
decl_stmt|;
return|return
operator|new
name|GeoPointInPolygonQuery
argument_list|(
name|fieldType
operator|.
name|name
argument_list|()
argument_list|,
name|encoding
argument_list|,
name|lats
argument_list|,
name|lons
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doXContent
specifier|protected
name|void
name|doXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
name|POINTS_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|GeoPoint
name|point
range|:
name|shell
control|)
block|{
name|builder
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
name|point
operator|.
name|lon
argument_list|()
argument_list|)
operator|.
name|value
argument_list|(
name|point
operator|.
name|lat
argument_list|()
argument_list|)
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|COERCE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|GeoValidationMethod
operator|.
name|isCoerce
argument_list|(
name|validationMethod
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|IGNORE_MALFORMED_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|GeoValidationMethod
operator|.
name|isIgnoreMalformed
argument_list|(
name|validationMethod
argument_list|)
argument_list|)
expr_stmt|;
name|printBoostAndQueryName
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|GeoPolygonQueryBuilder
name|fromXContent
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
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
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|shell
init|=
literal|null
decl_stmt|;
name|Float
name|boost
init|=
literal|null
decl_stmt|;
name|boolean
name|coerce
init|=
name|GeoValidationMethod
operator|.
name|DEFAULT_LENIENT_PARSING
decl_stmt|;
name|boolean
name|ignoreMalformed
init|=
name|GeoValidationMethod
operator|.
name|DEFAULT_LENIENT_PARSING
decl_stmt|;
name|GeoValidationMethod
name|validationMethod
init|=
literal|null
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
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|POINTS_FIELD
argument_list|)
condition|)
block|{
name|shell
operator|=
operator|new
name|ArrayList
argument_list|<
name|GeoPoint
argument_list|>
argument_list|()
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
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|shell
operator|.
name|add
argument_list|(
name|GeoUtils
operator|.
name|parseGeoPoint
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"[geo_polygon] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"[geo_polygon] query does not support token type ["
operator|+
name|token
operator|.
name|name
argument_list|()
operator|+
literal|"] under ["
operator|+
name|currentFieldName
operator|+
literal|"]"
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
literal|"boost"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|boost
operator|=
name|parser
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|COERCE_FIELD
argument_list|)
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
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|IGNORE_MALFORMED_FIELD
argument_list|)
condition|)
block|{
name|ignoreMalformed
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
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|VALIDATION_METHOD
argument_list|)
condition|)
block|{
name|validationMethod
operator|=
name|GeoValidationMethod
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
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"[geo_polygon] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"[geo_polygon] unexpected token type ["
operator|+
name|token
operator|.
name|name
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
name|GeoPolygonQueryBuilder
name|builder
init|=
operator|new
name|GeoPolygonQueryBuilder
argument_list|(
name|fieldName
argument_list|,
name|shell
argument_list|)
decl_stmt|;
if|if
condition|(
name|validationMethod
operator|!=
literal|null
condition|)
block|{
comment|// if GeoValidationMethod was explicitly set ignore deprecated coerce and ignoreMalformed settings
name|builder
operator|.
name|setValidationMethod
argument_list|(
name|validationMethod
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|setValidationMethod
argument_list|(
name|GeoValidationMethod
operator|.
name|infer
argument_list|(
name|coerce
argument_list|,
name|ignoreMalformed
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|queryName
argument_list|(
name|queryName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|boost
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|GeoPolygonQueryBuilder
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|validationMethod
argument_list|,
name|other
operator|.
name|validationMethod
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|,
name|other
operator|.
name|fieldName
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|shell
argument_list|,
name|other
operator|.
name|shell
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doHashCode
specifier|protected
name|int
name|doHashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|validationMethod
argument_list|,
name|fieldName
argument_list|,
name|shell
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
block|}
end_class

end_unit

