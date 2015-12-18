begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.completion.context
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
operator|.
name|context
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
name|document
operator|.
name|StringField
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
name|DocValuesType
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
name|IndexableField
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
name|GeoHashUtils
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
name|ParseContext
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
name|ParseContext
operator|.
name|Document
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Map
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A {@link ContextMapping} that uses a geo location/area as a  * criteria.  * The suggestions can be boosted and/or filtered depending on  * whether it falls within an area, represented by a query geo hash  * with a specified precision  *  * {@link GeoQueryContext} defines the options for constructing  * a unit of query context for this context type  */
end_comment

begin_class
DECL|class|GeoContextMapping
specifier|public
class|class
name|GeoContextMapping
extends|extends
name|ContextMapping
block|{
DECL|field|FIELD_PRECISION
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_PRECISION
init|=
literal|"precision"
decl_stmt|;
DECL|field|FIELD_FIELDNAME
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_FIELDNAME
init|=
literal|"path"
decl_stmt|;
DECL|field|DEFAULT_PRECISION
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_PRECISION
init|=
literal|6
decl_stmt|;
DECL|field|CONTEXT_VALUE
specifier|static
specifier|final
name|String
name|CONTEXT_VALUE
init|=
literal|"context"
decl_stmt|;
DECL|field|CONTEXT_BOOST
specifier|static
specifier|final
name|String
name|CONTEXT_BOOST
init|=
literal|"boost"
decl_stmt|;
DECL|field|CONTEXT_PRECISION
specifier|static
specifier|final
name|String
name|CONTEXT_PRECISION
init|=
literal|"precision"
decl_stmt|;
DECL|field|CONTEXT_NEIGHBOURS
specifier|static
specifier|final
name|String
name|CONTEXT_NEIGHBOURS
init|=
literal|"neighbours"
decl_stmt|;
DECL|field|precision
specifier|private
specifier|final
name|int
name|precision
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|method|GeoContextMapping
specifier|private
name|GeoContextMapping
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|int
name|precision
parameter_list|)
block|{
name|super
argument_list|(
name|Type
operator|.
name|GEO
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|precision
operator|=
name|precision
expr_stmt|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
block|}
DECL|method|getFieldName
specifier|public
name|String
name|getFieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
DECL|method|getPrecision
specifier|public
name|int
name|getPrecision
parameter_list|()
block|{
return|return
name|precision
return|;
block|}
DECL|method|load
specifier|protected
specifier|static
name|GeoContextMapping
name|load
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|)
block|{
specifier|final
name|GeoContextMapping
operator|.
name|Builder
name|builder
init|=
operator|new
name|GeoContextMapping
operator|.
name|Builder
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Object
name|configPrecision
init|=
name|config
operator|.
name|get
argument_list|(
name|FIELD_PRECISION
argument_list|)
decl_stmt|;
if|if
condition|(
name|configPrecision
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|configPrecision
operator|instanceof
name|Integer
condition|)
block|{
name|builder
operator|.
name|precision
argument_list|(
operator|(
name|Integer
operator|)
name|configPrecision
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|configPrecision
operator|instanceof
name|Long
condition|)
block|{
name|builder
operator|.
name|precision
argument_list|(
operator|(
name|Long
operator|)
name|configPrecision
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|configPrecision
operator|instanceof
name|Double
condition|)
block|{
name|builder
operator|.
name|precision
argument_list|(
operator|(
name|Double
operator|)
name|configPrecision
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|configPrecision
operator|instanceof
name|Float
condition|)
block|{
name|builder
operator|.
name|precision
argument_list|(
operator|(
name|Float
operator|)
name|configPrecision
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|precision
argument_list|(
name|configPrecision
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|remove
argument_list|(
name|FIELD_PRECISION
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Object
name|fieldName
init|=
name|config
operator|.
name|get
argument_list|(
name|FIELD_FIELDNAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|fieldName
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|remove
argument_list|(
name|FIELD_FIELDNAME
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toInnerXContent
specifier|protected
name|XContentBuilder
name|toInnerXContent
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
name|field
argument_list|(
name|FIELD_PRECISION
argument_list|,
name|precision
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|FIELD_FIELDNAME
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
comment|/**      * Parse a set of {@link CharSequence} contexts at index-time.      * Acceptable formats:      *      *<ul>      *<li>Array:<pre>[<i>&lt;GEO POINT&gt;</i>, ..]</pre></li>      *<li>String/Object/Array:<pre>&quot;GEO POINT&quot;</pre></li>      *</ul>      *      * see {@link GeoUtils#parseGeoPoint(String, GeoPoint)} for GEO POINT      */
annotation|@
name|Override
DECL|method|parseContext
specifier|public
name|Set
argument_list|<
name|CharSequence
argument_list|>
name|parseContext
parameter_list|(
name|ParseContext
name|parseContext
parameter_list|,
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
throws|,
name|ElasticsearchParseException
block|{
if|if
condition|(
name|fieldName
operator|!=
literal|null
condition|)
block|{
name|FieldMapper
name|mapper
init|=
name|parseContext
operator|.
name|docMapper
argument_list|()
operator|.
name|mappers
argument_list|()
operator|.
name|getMapper
argument_list|(
name|fieldName
argument_list|)
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
name|ElasticsearchParseException
argument_list|(
literal|"referenced field must be mapped to geo_point"
argument_list|)
throw|;
block|}
block|}
specifier|final
name|Set
argument_list|<
name|CharSequence
argument_list|>
name|contexts
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Token
name|token
init|=
name|parser
operator|.
name|currentToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
comment|// Test if value is a single point in<code>[lon, lat]</code> format
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|VALUE_NUMBER
condition|)
block|{
name|double
name|lon
init|=
name|parser
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|==
name|Token
operator|.
name|VALUE_NUMBER
condition|)
block|{
name|double
name|lat
init|=
name|parser
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|==
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|contexts
operator|.
name|add
argument_list|(
name|GeoHashUtils
operator|.
name|stringEncode
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|,
name|precision
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"only two values [lon, lat] expected"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"latitude must be a numeric value"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
while|while
condition|(
name|token
operator|!=
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|GeoPoint
name|point
init|=
name|GeoUtils
operator|.
name|parseGeoPoint
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|contexts
operator|.
name|add
argument_list|(
name|GeoHashUtils
operator|.
name|stringEncode
argument_list|(
name|point
operator|.
name|getLon
argument_list|()
argument_list|,
name|point
operator|.
name|getLat
argument_list|()
argument_list|,
name|precision
argument_list|)
argument_list|)
expr_stmt|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
specifier|final
name|String
name|geoHash
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
specifier|final
name|CharSequence
name|truncatedGeoHash
init|=
name|geoHash
operator|.
name|subSequence
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|geoHash
operator|.
name|length
argument_list|()
argument_list|,
name|precision
argument_list|)
argument_list|)
decl_stmt|;
name|contexts
operator|.
name|add
argument_list|(
name|truncatedGeoHash
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// or a single location
name|GeoPoint
name|point
init|=
name|GeoUtils
operator|.
name|parseGeoPoint
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|contexts
operator|.
name|add
argument_list|(
name|GeoHashUtils
operator|.
name|stringEncode
argument_list|(
name|point
operator|.
name|getLon
argument_list|()
argument_list|,
name|point
operator|.
name|getLat
argument_list|()
argument_list|,
name|precision
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|contexts
return|;
block|}
annotation|@
name|Override
DECL|method|parseContext
specifier|public
name|Set
argument_list|<
name|CharSequence
argument_list|>
name|parseContext
parameter_list|(
name|Document
name|document
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|CharSequence
argument_list|>
name|geohashes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldName
operator|!=
literal|null
condition|)
block|{
name|IndexableField
index|[]
name|fields
init|=
name|document
operator|.
name|getFields
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|GeoPoint
name|spare
init|=
operator|new
name|GeoPoint
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|IndexableField
index|[]
name|lonFields
init|=
name|document
operator|.
name|getFields
argument_list|(
name|fieldName
operator|+
literal|".lon"
argument_list|)
decl_stmt|;
name|IndexableField
index|[]
name|latFields
init|=
name|document
operator|.
name|getFields
argument_list|(
name|fieldName
operator|+
literal|".lat"
argument_list|)
decl_stmt|;
if|if
condition|(
name|lonFields
operator|.
name|length
operator|>
literal|0
operator|&&
name|latFields
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|lonFields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|IndexableField
name|lonField
init|=
name|lonFields
index|[
name|i
index|]
decl_stmt|;
name|IndexableField
name|latField
init|=
name|latFields
index|[
name|i
index|]
decl_stmt|;
assert|assert
name|lonField
operator|.
name|fieldType
argument_list|()
operator|.
name|docValuesType
argument_list|()
operator|==
name|latField
operator|.
name|fieldType
argument_list|()
operator|.
name|docValuesType
argument_list|()
assert|;
comment|// we write doc values fields differently: one field for all values, so we need to only care about indexed fields
if|if
condition|(
name|lonField
operator|.
name|fieldType
argument_list|()
operator|.
name|docValuesType
argument_list|()
operator|==
name|DocValuesType
operator|.
name|NONE
condition|)
block|{
name|spare
operator|.
name|reset
argument_list|(
name|latField
operator|.
name|numericValue
argument_list|()
operator|.
name|doubleValue
argument_list|()
argument_list|,
name|lonField
operator|.
name|numericValue
argument_list|()
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
name|geohashes
operator|.
name|add
argument_list|(
name|GeoHashUtils
operator|.
name|stringEncode
argument_list|(
name|spare
operator|.
name|getLon
argument_list|()
argument_list|,
name|spare
operator|.
name|getLat
argument_list|()
argument_list|,
name|precision
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
for|for
control|(
name|IndexableField
name|field
range|:
name|fields
control|)
block|{
if|if
condition|(
name|field
operator|instanceof
name|StringField
condition|)
block|{
name|spare
operator|.
name|resetFromString
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|spare
operator|.
name|resetFromIndexHash
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|geohashes
operator|.
name|add
argument_list|(
name|spare
operator|.
name|geohash
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Set
argument_list|<
name|CharSequence
argument_list|>
name|locations
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CharSequence
name|geohash
range|:
name|geohashes
control|)
block|{
name|int
name|precision
init|=
name|Math
operator|.
name|min
argument_list|(
name|this
operator|.
name|precision
argument_list|,
name|geohash
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|CharSequence
name|truncatedGeohash
init|=
name|geohash
operator|.
name|subSequence
argument_list|(
literal|0
argument_list|,
name|precision
argument_list|)
decl_stmt|;
name|locations
operator|.
name|add
argument_list|(
name|truncatedGeohash
argument_list|)
expr_stmt|;
block|}
return|return
name|locations
return|;
block|}
comment|/**      * Parse a list of {@link GeoQueryContext}      * using<code>parser</code>. A QueryContexts accepts one of the following forms:      *      *<ul>      *<li>Object: GeoQueryContext</li>      *<li>String: GeoQueryContext value with boost=1  precision=PRECISION neighbours=[PRECISION]</li>      *<li>Array:<pre>[GeoQueryContext, ..]</pre></li>      *</ul>      *      *  A GeoQueryContext has one of the following forms:      *<ul>      *<li>Object:      *<ul>      *<li><pre>GEO POINT</pre></li>      *<li><pre>{&quot;lat&quot;:<i>&lt;double&gt;</i>,&quot;lon&quot;:<i>&lt;double&gt;</i>,&quot;precision&quot;:<i>&lt;int&gt;</i>,&quot;neighbours&quot;:<i>&lt;[int, ..]&gt;</i>}</pre></li>      *<li><pre>{&quot;context&quot;:<i>&lt;string&gt;</i>,&quot;boost&quot;:<i>&lt;int&gt;</i>,&quot;precision&quot;:<i>&lt;int&gt;</i>,&quot;neighbours&quot;:<i>&lt;[int, ..]&gt;</i>}</pre></li>      *<li><pre>{&quot;context&quot;:<i>&lt;GEO POINT&gt;</i>,&quot;boost&quot;:<i>&lt;int&gt;</i>,&quot;precision&quot;:<i>&lt;int&gt;</i>,&quot;neighbours&quot;:<i>&lt;[int, ..]&gt;</i>}</pre></li>      *</ul>      *<li>String:<pre>GEO POINT</pre></li>      *</ul>      * see {@link GeoUtils#parseGeoPoint(String, GeoPoint)} for GEO POINT      */
annotation|@
name|Override
DECL|method|parseQueryContext
specifier|public
name|List
argument_list|<
name|QueryContext
argument_list|>
name|parseQueryContext
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
throws|,
name|ElasticsearchParseException
block|{
name|List
argument_list|<
name|GeoQueryContext
argument_list|>
name|queryContexts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|START_OBJECT
operator|||
name|token
operator|==
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
name|queryContexts
operator|.
name|add
argument_list|(
name|GeoQueryContext
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
while|while
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|queryContexts
operator|.
name|add
argument_list|(
name|GeoQueryContext
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|QueryContext
argument_list|>
name|queryContextList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|GeoQueryContext
name|queryContext
range|:
name|queryContexts
control|)
block|{
name|int
name|minPrecision
init|=
name|this
operator|.
name|precision
decl_stmt|;
if|if
condition|(
name|queryContext
operator|.
name|getPrecision
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|minPrecision
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minPrecision
argument_list|,
name|queryContext
operator|.
name|getPrecision
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|GeoPoint
name|point
init|=
name|queryContext
operator|.
name|getGeoPoint
argument_list|()
decl_stmt|;
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|locations
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|geoHash
init|=
name|GeoHashUtils
operator|.
name|stringEncode
argument_list|(
name|point
operator|.
name|getLon
argument_list|()
argument_list|,
name|point
operator|.
name|getLat
argument_list|()
argument_list|,
name|minPrecision
argument_list|)
decl_stmt|;
name|locations
operator|.
name|add
argument_list|(
name|geoHash
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryContext
operator|.
name|getNeighbours
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
name|geoHash
operator|.
name|length
argument_list|()
operator|==
name|this
operator|.
name|precision
condition|)
block|{
name|GeoHashUtils
operator|.
name|addNeighbors
argument_list|(
name|geoHash
argument_list|,
name|locations
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|queryContext
operator|.
name|getNeighbours
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
for|for
control|(
name|Integer
name|neighbourPrecision
range|:
name|queryContext
operator|.
name|getNeighbours
argument_list|()
control|)
block|{
if|if
condition|(
name|neighbourPrecision
operator|<
name|geoHash
operator|.
name|length
argument_list|()
condition|)
block|{
name|String
name|truncatedGeoHash
init|=
name|geoHash
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|neighbourPrecision
argument_list|)
decl_stmt|;
name|locations
operator|.
name|add
argument_list|(
name|truncatedGeoHash
argument_list|)
expr_stmt|;
name|GeoHashUtils
operator|.
name|addNeighbors
argument_list|(
name|truncatedGeoHash
argument_list|,
name|locations
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|String
name|location
range|:
name|locations
control|)
block|{
name|queryContextList
operator|.
name|add
argument_list|(
operator|new
name|QueryContext
argument_list|(
name|location
argument_list|,
name|queryContext
operator|.
name|getBoost
argument_list|()
argument_list|,
name|location
operator|.
name|length
argument_list|()
operator|<
name|this
operator|.
name|precision
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|queryContextList
return|;
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
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|GeoContextMapping
name|that
init|=
operator|(
name|GeoContextMapping
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|precision
operator|!=
name|that
operator|.
name|precision
condition|)
return|return
literal|false
return|;
return|return
operator|!
operator|(
name|fieldName
operator|!=
literal|null
condition|?
operator|!
name|fieldName
operator|.
name|equals
argument_list|(
name|that
operator|.
name|fieldName
argument_list|)
else|:
name|that
operator|.
name|fieldName
operator|!=
literal|null
operator|)
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
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|super
operator|.
name|hashCode
argument_list|()
argument_list|,
name|precision
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|ContextBuilder
argument_list|<
name|GeoContextMapping
argument_list|>
block|{
DECL|field|precision
specifier|private
name|int
name|precision
init|=
name|DEFAULT_PRECISION
decl_stmt|;
DECL|field|fieldName
specifier|private
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
DECL|method|Builder
specifier|protected
name|Builder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**          * Set the precision use o make suggestions          *          * @param precision          *            precision as distance with {@link DistanceUnit}. Default:          *            meters          * @return this          */
DECL|method|precision
specifier|public
name|Builder
name|precision
parameter_list|(
name|String
name|precision
parameter_list|)
block|{
return|return
name|precision
argument_list|(
name|DistanceUnit
operator|.
name|parse
argument_list|(
name|precision
argument_list|,
name|DistanceUnit
operator|.
name|METERS
argument_list|,
name|DistanceUnit
operator|.
name|METERS
argument_list|)
argument_list|)
return|;
block|}
comment|/**          * Set the precision use o make suggestions          *          * @param precision          *            precision value          * @param unit          *            {@link DistanceUnit} to use          * @return this          */
DECL|method|precision
specifier|public
name|Builder
name|precision
parameter_list|(
name|double
name|precision
parameter_list|,
name|DistanceUnit
name|unit
parameter_list|)
block|{
return|return
name|precision
argument_list|(
name|unit
operator|.
name|toMeters
argument_list|(
name|precision
argument_list|)
argument_list|)
return|;
block|}
comment|/**          * Set the precision use o make suggestions          *          * @param meters          *            precision as distance in meters          * @return this          */
DECL|method|precision
specifier|public
name|Builder
name|precision
parameter_list|(
name|double
name|meters
parameter_list|)
block|{
name|int
name|level
init|=
name|GeoUtils
operator|.
name|geoHashLevelsForPrecision
argument_list|(
name|meters
argument_list|)
decl_stmt|;
comment|// Ceiling precision: we might return more results
if|if
condition|(
name|GeoUtils
operator|.
name|geoHashCellSize
argument_list|(
name|level
argument_list|)
operator|<
name|meters
condition|)
block|{
name|level
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|level
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|precision
argument_list|(
name|level
argument_list|)
return|;
block|}
comment|/**          * Set the precision use o make suggestions          *          * @param level          *            maximum length of geohashes          * @return this          */
DECL|method|precision
specifier|public
name|Builder
name|precision
parameter_list|(
name|int
name|level
parameter_list|)
block|{
name|this
operator|.
name|precision
operator|=
name|level
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Set the name of the field containing a geolocation to use          * @param fieldName name of the field          * @return this          */
DECL|method|field
specifier|public
name|Builder
name|field
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|GeoContextMapping
name|build
parameter_list|()
block|{
return|return
operator|new
name|GeoContextMapping
argument_list|(
name|name
argument_list|,
name|fieldName
argument_list|,
name|precision
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

