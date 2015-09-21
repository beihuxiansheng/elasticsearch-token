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
comment|/**  *<pre>  * {  *     "pin.location" : {  *         "points" : [  *              { "lat" : 12, "lon" : 40},  *              {}  *         ]  *     }  * }  *</pre>  */
end_comment

begin_class
DECL|class|GeoPolygonQueryParser
specifier|public
class|class
name|GeoPolygonQueryParser
implements|implements
name|QueryParser
argument_list|<
name|GeoPolygonQueryBuilder
argument_list|>
block|{
DECL|field|COERCE_FIELD
specifier|public
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
specifier|public
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
specifier|public
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
specifier|public
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
name|GeoPolygonQueryBuilder
operator|.
name|NAME
block|,
literal|"geoPolygon"
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
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
DECL|method|getBuilderPrototype
specifier|public
name|GeoPolygonQueryBuilder
name|getBuilderPrototype
parameter_list|()
block|{
return|return
name|GeoPolygonQueryBuilder
operator|.
name|PROTOTYPE
return|;
block|}
block|}
end_class

end_unit

