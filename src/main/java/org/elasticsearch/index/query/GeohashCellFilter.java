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
name|search
operator|.
name|QueryCachingPolicy
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
name|GeoHashUtils
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
name|lucene
operator|.
name|HashedBytesRef
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
name|core
operator|.
name|StringFieldMapper
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
name|List
import|;
end_import

begin_comment
comment|/**  * A geohash cell filter that filters {@link GeoPoint}s by their geohashes. Basically the a  * Geohash prefix is defined by the filter and all geohashes that are matching this  * prefix will be returned. The<code>neighbors</code> flag allows to filter  * geohashes that surround the given geohash. In general the neighborhood of a  * geohash is defined by its eight adjacent cells.<br />  * The structure of the {@link GeohashCellFilter} is defined as:  *<pre>  *&quot;geohash_bbox&quot; {  *&quot;field&quot;:&quot;location&quot;,  *&quot;geohash&quot;:&quot;u33d8u5dkx8k&quot;,  *&quot;neighbors&quot;:false  * }  *</pre>  */
end_comment

begin_class
DECL|class|GeohashCellFilter
specifier|public
class|class
name|GeohashCellFilter
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"geohash_cell"
decl_stmt|;
DECL|field|NEIGHBORS
specifier|public
specifier|static
specifier|final
name|String
name|NEIGHBORS
init|=
literal|"neighbors"
decl_stmt|;
DECL|field|PRECISION
specifier|public
specifier|static
specifier|final
name|String
name|PRECISION
init|=
literal|"precision"
decl_stmt|;
DECL|field|CACHE
specifier|public
specifier|static
specifier|final
name|String
name|CACHE
init|=
literal|"_cache"
decl_stmt|;
DECL|field|CACHE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|CACHE_KEY
init|=
literal|"_cache_key"
decl_stmt|;
comment|/**      * Create a new geohash filter for a given set of geohashes. In general this method      * returns a boolean filter combining the geohashes OR-wise.      *      * @param context     Context of the filter      * @param fieldMapper field mapper for geopoints      * @param geohash     mandatory geohash      * @param geohashes   optional array of additional geohashes      * @return a new GeoBoundinboxfilter      */
DECL|method|create
specifier|public
specifier|static
name|Filter
name|create
parameter_list|(
name|QueryParseContext
name|context
parameter_list|,
name|GeoPointFieldMapper
name|fieldMapper
parameter_list|,
name|String
name|geohash
parameter_list|,
annotation|@
name|Nullable
name|List
argument_list|<
name|CharSequence
argument_list|>
name|geohashes
parameter_list|)
block|{
if|if
condition|(
name|fieldMapper
operator|.
name|geoHashStringMapper
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"geohash filter needs geohash_prefix to be enabled"
argument_list|)
throw|;
block|}
name|StringFieldMapper
name|geoHashMapper
init|=
name|fieldMapper
operator|.
name|geoHashStringMapper
argument_list|()
decl_stmt|;
if|if
condition|(
name|geohashes
operator|==
literal|null
operator|||
name|geohashes
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|geoHashMapper
operator|.
name|termFilter
argument_list|(
name|geohash
argument_list|,
name|context
argument_list|)
return|;
block|}
else|else
block|{
name|geohashes
operator|.
name|add
argument_list|(
name|geohash
argument_list|)
expr_stmt|;
return|return
name|geoHashMapper
operator|.
name|termsFilter
argument_list|(
name|geohashes
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
comment|/**      * Builder for a geohashfilter. It needs the fields<code>fieldname</code> and      *<code>geohash</code> to be set. the default for a neighbor filteing is      *<code>false</code>.      */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|BaseFilterBuilder
block|{
comment|// we need to store the geohash rather than the corresponding point,
comment|// because a transformation from a geohash to a point an back to the
comment|// geohash will extend the accuracy of the hash to max precision
comment|// i.e. by filing up with z's.
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|geohash
specifier|private
name|String
name|geohash
decl_stmt|;
DECL|field|levels
specifier|private
name|int
name|levels
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|neighbors
specifier|private
name|boolean
name|neighbors
decl_stmt|;
DECL|field|cache
specifier|private
name|Boolean
name|cache
decl_stmt|;
DECL|field|cacheKey
specifier|private
name|String
name|cacheKey
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|field
parameter_list|,
name|GeoPoint
name|point
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|point
operator|.
name|geohash
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|geohash
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|geohash
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|geohash
parameter_list|,
name|boolean
name|neighbors
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|geohash
operator|=
name|geohash
expr_stmt|;
name|this
operator|.
name|neighbors
operator|=
name|neighbors
expr_stmt|;
block|}
DECL|method|point
specifier|public
name|Builder
name|point
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
block|{
name|this
operator|.
name|geohash
operator|=
name|point
operator|.
name|getGeohash
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|point
specifier|public
name|Builder
name|point
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|this
operator|.
name|geohash
operator|=
name|GeoHashUtils
operator|.
name|encode
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|geohash
specifier|public
name|Builder
name|geohash
parameter_list|(
name|String
name|geohash
parameter_list|)
block|{
name|this
operator|.
name|geohash
operator|=
name|geohash
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|precision
specifier|public
name|Builder
name|precision
parameter_list|(
name|int
name|levels
parameter_list|)
block|{
name|this
operator|.
name|levels
operator|=
name|levels
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|precision
specifier|public
name|Builder
name|precision
parameter_list|(
name|String
name|precision
parameter_list|)
block|{
name|double
name|meters
init|=
name|DistanceUnit
operator|.
name|parse
argument_list|(
name|precision
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|,
name|DistanceUnit
operator|.
name|METERS
argument_list|)
decl_stmt|;
return|return
name|precision
argument_list|(
name|GeoUtils
operator|.
name|geoHashLevelsForPrecision
argument_list|(
name|meters
argument_list|)
argument_list|)
return|;
block|}
DECL|method|neighbors
specifier|public
name|Builder
name|neighbors
parameter_list|(
name|boolean
name|neighbors
parameter_list|)
block|{
name|this
operator|.
name|neighbors
operator|=
name|neighbors
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|field
specifier|public
name|Builder
name|field
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Should the filter be cached or not. Defaults to<tt>false</tt>.          */
DECL|method|cache
specifier|public
name|Builder
name|cache
parameter_list|(
name|boolean
name|cache
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|cacheKey
specifier|public
name|Builder
name|cacheKey
parameter_list|(
name|String
name|cacheKey
parameter_list|)
block|{
name|this
operator|.
name|cacheKey
operator|=
name|cacheKey
expr_stmt|;
return|return
name|this
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
if|if
condition|(
name|neighbors
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|NEIGHBORS
argument_list|,
name|neighbors
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|levels
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|PRECISION
argument_list|,
name|levels
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|CACHE
argument_list|,
name|cache
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cacheKey
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|CACHE_KEY
argument_list|,
name|cacheKey
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
name|field
argument_list|,
name|geohash
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Parser
specifier|public
specifier|static
class|class
name|Parser
implements|implements
name|FilterParser
block|{
annotation|@
name|Inject
DECL|method|Parser
specifier|public
name|Parser
parameter_list|()
block|{         }
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
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|NAME
argument_list|)
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Filter
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
name|String
name|geohash
init|=
literal|null
decl_stmt|;
name|int
name|levels
init|=
operator|-
literal|1
decl_stmt|;
name|boolean
name|neighbors
init|=
literal|false
decl_stmt|;
name|QueryCachingPolicy
name|cache
init|=
name|parseContext
operator|.
name|autoFilterCachePolicy
argument_list|()
decl_stmt|;
name|HashedBytesRef
name|cacheKey
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
if|if
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|currentToken
argument_list|()
operator|)
operator|!=
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
name|NAME
operator|+
literal|" must be an object"
argument_list|)
throw|;
block|}
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
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|String
name|field
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
name|PRECISION
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|==
name|Token
operator|.
name|VALUE_NUMBER
condition|)
block|{
name|levels
operator|=
name|parser
operator|.
name|intValue
argument_list|()
expr_stmt|;
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
name|double
name|meters
init|=
name|DistanceUnit
operator|.
name|parse
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|,
name|DistanceUnit
operator|.
name|METERS
argument_list|)
decl_stmt|;
name|levels
operator|=
name|GeoUtils
operator|.
name|geoHashLevelsForPrecision
argument_list|(
name|meters
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|NEIGHBORS
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|neighbors
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
name|CACHE
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|cache
operator|=
name|parseContext
operator|.
name|parseFilterCachePolicy
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|CACHE_KEY
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|cacheKey
operator|=
operator|new
name|HashedBytesRef
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
name|fieldName
operator|=
name|field
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
name|token
operator|==
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
comment|// A string indicates either a gehash or a lat/lon string
name|String
name|location
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
name|location
operator|.
name|indexOf
argument_list|(
literal|","
argument_list|)
operator|>
literal|0
condition|)
block|{
name|geohash
operator|=
name|GeoUtils
operator|.
name|parseGeoPoint
argument_list|(
name|parser
argument_list|)
operator|.
name|geohash
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|geohash
operator|=
name|location
expr_stmt|;
block|}
block|}
else|else
block|{
name|geohash
operator|=
name|GeoUtils
operator|.
name|parseGeoPoint
argument_list|(
name|parser
argument_list|)
operator|.
name|geohash
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"unexpected token ["
operator|+
name|token
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|geohash
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"no geohash value provided to geohash_cell filter"
argument_list|)
throw|;
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
if|if
condition|(
operator|!
name|geoMapper
operator|.
name|isEnableGeohashPrefix
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"can't execute geohash_cell on field ["
operator|+
name|fieldName
operator|+
literal|"], geohash_prefix is not enabled"
argument_list|)
throw|;
block|}
if|if
condition|(
name|levels
operator|>
literal|0
condition|)
block|{
name|int
name|len
init|=
name|Math
operator|.
name|min
argument_list|(
name|levels
argument_list|,
name|geohash
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|geohash
operator|=
name|geohash
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|Filter
name|filter
decl_stmt|;
if|if
condition|(
name|neighbors
condition|)
block|{
name|filter
operator|=
name|create
argument_list|(
name|parseContext
argument_list|,
name|geoMapper
argument_list|,
name|geohash
argument_list|,
name|GeoHashUtils
operator|.
name|addNeighbors
argument_list|(
name|geohash
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|CharSequence
argument_list|>
argument_list|(
literal|8
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|filter
operator|=
name|create
argument_list|(
name|parseContext
argument_list|,
name|geoMapper
argument_list|,
name|geohash
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
name|filter
operator|=
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|filter
argument_list|,
name|cacheKey
argument_list|,
name|cache
argument_list|)
expr_stmt|;
block|}
return|return
name|filter
return|;
block|}
block|}
block|}
end_class

end_unit

