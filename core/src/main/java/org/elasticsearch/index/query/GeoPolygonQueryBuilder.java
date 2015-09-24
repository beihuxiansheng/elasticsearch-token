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
name|Arrays
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
DECL|field|PROTO_SHAPE
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|PROTO_SHAPE
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|GeoPoint
index|[]
block|{
operator|new
name|GeoPoint
argument_list|(
literal|1.0
argument_list|,
literal|1.0
argument_list|)
block|,
operator|new
name|GeoPoint
argument_list|(
literal|1.0
argument_list|,
literal|2.0
argument_list|)
block|,
operator|new
name|GeoPoint
argument_list|(
literal|2.0
argument_list|,
literal|1.0
argument_list|)
block|}
argument_list|)
decl_stmt|;
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|GeoPolygonQueryBuilder
name|PROTOTYPE
init|=
operator|new
name|GeoPolygonQueryBuilder
argument_list|(
literal|"field"
argument_list|,
name|PROTO_SHAPE
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
DECL|field|coerce
specifier|private
name|boolean
name|coerce
init|=
literal|false
decl_stmt|;
DECL|field|ignoreMalformed
specifier|private
name|boolean
name|ignoreMalformed
init|=
literal|false
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
name|points
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
DECL|method|coerce
specifier|public
name|GeoPolygonQueryBuilder
name|coerce
parameter_list|(
name|boolean
name|coerce
parameter_list|)
block|{
if|if
condition|(
name|coerce
condition|)
block|{
name|this
operator|.
name|ignoreMalformed
operator|=
literal|true
expr_stmt|;
block|}
name|this
operator|.
name|coerce
operator|=
name|coerce
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|coerce
specifier|public
name|boolean
name|coerce
parameter_list|()
block|{
return|return
name|this
operator|.
name|coerce
return|;
block|}
DECL|method|ignoreMalformed
specifier|public
name|GeoPolygonQueryBuilder
name|ignoreMalformed
parameter_list|(
name|boolean
name|ignoreMalformed
parameter_list|)
block|{
if|if
condition|(
name|coerce
operator|==
literal|false
condition|)
block|{
name|this
operator|.
name|ignoreMalformed
operator|=
name|ignoreMalformed
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|ignoreMalformed
specifier|public
name|boolean
name|ignoreMalformed
parameter_list|()
block|{
return|return
name|ignoreMalformed
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
name|ignoreMalformed
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
name|shell
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
name|GeoPointFieldMapper
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
name|shell
operator|.
name|size
argument_list|()
index|]
argument_list|)
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
name|GeoPolygonQueryParser
operator|.
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
name|GeoPolygonQueryParser
operator|.
name|COERCE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|coerce
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|GeoPolygonQueryParser
operator|.
name|IGNORE_MALFORMED_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|ignoreMalformed
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
annotation|@
name|Override
DECL|method|doReadFrom
specifier|protected
name|GeoPolygonQueryBuilder
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fieldName
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|shell
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
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
name|GeoPoint
operator|.
name|readGeoPointFrom
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
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
name|builder
operator|.
name|coerce
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|builder
operator|.
name|ignoreMalformed
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
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
name|point
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|coerce
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|ignoreMalformed
argument_list|)
expr_stmt|;
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
name|coerce
argument_list|,
name|other
operator|.
name|coerce
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
name|ignoreMalformed
argument_list|,
name|other
operator|.
name|ignoreMalformed
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
name|coerce
argument_list|,
name|fieldName
argument_list|,
name|ignoreMalformed
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

