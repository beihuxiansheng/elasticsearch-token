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
name|MatchNoDocsQuery
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
name|spatial
operator|.
name|geopoint
operator|.
name|search
operator|.
name|XGeoPointDistanceRangeQuery
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
name|util
operator|.
name|GeoDistanceUtils
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
name|NumericUtils
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
name|compress
operator|.
name|CompressedXContent
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
name|search
operator|.
name|geo
operator|.
name|GeoDistanceRangeQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|geo
operator|.
name|RandomGeoGenerator
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|containsString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|instanceOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|notNullValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|closeTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|is
import|;
end_import

begin_class
DECL|class|GeoDistanceRangeQueryTests
specifier|public
class|class
name|GeoDistanceRangeQueryTests
extends|extends
name|AbstractQueryTestCase
argument_list|<
name|GeoDistanceRangeQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|GeoDistanceRangeQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|Version
name|version
init|=
name|queryShardContext
argument_list|()
operator|.
name|indexVersionCreated
argument_list|()
decl_stmt|;
name|GeoDistanceRangeQueryBuilder
name|builder
decl_stmt|;
name|GeoPoint
name|randomPoint
init|=
name|RandomGeoGenerator
operator|.
name|randomPointIn
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|180.0
argument_list|,
operator|-
literal|89.9
argument_list|,
literal|180.0
argument_list|,
literal|89.9
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|=
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
name|randomPoint
operator|.
name|geohash
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|=
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
name|randomPoint
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|=
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
name|randomPoint
operator|.
name|lat
argument_list|()
argument_list|,
name|randomPoint
operator|.
name|lon
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|GeoPoint
name|point
init|=
name|builder
operator|.
name|point
argument_list|()
decl_stmt|;
specifier|final
name|double
name|maxRadius
init|=
name|GeoDistanceUtils
operator|.
name|maxRadialDistanceMeters
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
specifier|final
name|int
name|fromValueMeters
init|=
name|randomInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|maxRadius
operator|*
literal|0.5
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|toValueMeters
init|=
name|randomIntBetween
argument_list|(
name|fromValueMeters
operator|+
literal|1
argument_list|,
operator|(
name|int
operator|)
name|maxRadius
argument_list|)
decl_stmt|;
name|DistanceUnit
name|fromToUnits
init|=
name|randomFrom
argument_list|(
name|DistanceUnit
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|fromToUnitsStr
init|=
name|fromToUnits
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|double
name|fromValue
init|=
name|DistanceUnit
operator|.
name|convert
argument_list|(
name|fromValueMeters
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|,
name|fromToUnits
argument_list|)
decl_stmt|;
specifier|final
name|double
name|toValue
init|=
name|DistanceUnit
operator|.
name|convert
argument_list|(
name|toValueMeters
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|,
name|fromToUnits
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|int
name|branch
init|=
name|randomInt
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|fromToUnits
operator|=
name|DistanceUnit
operator|.
name|DEFAULT
expr_stmt|;
switch|switch
condition|(
name|branch
condition|)
block|{
case|case
literal|0
case|:
name|builder
operator|.
name|from
argument_list|(
name|fromValueMeters
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|builder
operator|.
name|to
argument_list|(
name|toValueMeters
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|builder
operator|.
name|from
argument_list|(
name|fromValueMeters
argument_list|)
expr_stmt|;
name|builder
operator|.
name|to
argument_list|(
name|toValueMeters
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
name|int
name|branch
init|=
name|randomInt
argument_list|(
literal|2
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|branch
condition|)
block|{
case|case
literal|0
case|:
name|builder
operator|.
name|from
argument_list|(
name|fromValue
operator|+
name|fromToUnitsStr
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|builder
operator|.
name|to
argument_list|(
name|toValue
operator|+
name|fromToUnitsStr
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|builder
operator|.
name|from
argument_list|(
name|fromValue
operator|+
name|fromToUnitsStr
argument_list|)
expr_stmt|;
name|builder
operator|.
name|to
argument_list|(
name|toValue
operator|+
name|fromToUnitsStr
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|includeLower
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|includeUpper
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|geoDistance
argument_list|(
name|randomFrom
argument_list|(
name|GeoDistance
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
operator|&&
name|version
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_2_2_0
argument_list|)
condition|)
block|{
name|builder
operator|.
name|optimizeBbox
argument_list|(
name|randomFrom
argument_list|(
literal|"none"
argument_list|,
literal|"memory"
argument_list|,
literal|"indexed"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|unit
argument_list|(
name|fromToUnits
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|setValidationMethod
argument_list|(
name|randomFrom
argument_list|(
name|GeoValidationMethod
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|ignoreUnmapped
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|doAssertLuceneQuery
specifier|protected
name|void
name|doAssertLuceneQuery
parameter_list|(
name|GeoDistanceRangeQueryBuilder
name|queryBuilder
parameter_list|,
name|Query
name|query
parameter_list|,
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Version
name|version
init|=
name|context
operator|.
name|indexVersionCreated
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_2_2_0
argument_list|)
condition|)
block|{
name|assertLegacyQuery
argument_list|(
name|queryBuilder
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertGeoPointQuery
argument_list|(
name|queryBuilder
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertLegacyQuery
specifier|private
name|void
name|assertLegacyQuery
parameter_list|(
name|GeoDistanceRangeQueryBuilder
name|queryBuilder
parameter_list|,
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|GeoDistanceRangeQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|GeoDistanceRangeQuery
name|geoQuery
init|=
operator|(
name|GeoDistanceRangeQuery
operator|)
name|query
decl_stmt|;
name|assertThat
argument_list|(
name|geoQuery
operator|.
name|fieldName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|fieldName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryBuilder
operator|.
name|point
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|GeoPoint
name|expectedPoint
init|=
operator|new
name|GeoPoint
argument_list|(
name|queryBuilder
operator|.
name|point
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|GeoValidationMethod
operator|.
name|isCoerce
argument_list|(
name|queryBuilder
operator|.
name|getValidationMethod
argument_list|()
argument_list|)
condition|)
block|{
name|GeoUtils
operator|.
name|normalizePoint
argument_list|(
name|expectedPoint
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|geoQuery
operator|.
name|lat
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedPoint
operator|.
name|lat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|geoQuery
operator|.
name|lon
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedPoint
operator|.
name|lon
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|geoQuery
operator|.
name|geoDistance
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|geoDistance
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryBuilder
operator|.
name|from
argument_list|()
operator|!=
literal|null
operator|&&
name|queryBuilder
operator|.
name|from
argument_list|()
operator|instanceof
name|Number
condition|)
block|{
name|double
name|fromValue
init|=
operator|(
operator|(
name|Number
operator|)
name|queryBuilder
operator|.
name|from
argument_list|()
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryBuilder
operator|.
name|unit
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|fromValue
operator|=
name|queryBuilder
operator|.
name|unit
argument_list|()
operator|.
name|toMeters
argument_list|(
name|fromValue
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryBuilder
operator|.
name|geoDistance
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|fromValue
operator|=
name|queryBuilder
operator|.
name|geoDistance
argument_list|()
operator|.
name|normalize
argument_list|(
name|fromValue
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
name|double
name|fromSlop
init|=
name|Math
operator|.
name|abs
argument_list|(
name|fromValue
argument_list|)
operator|/
literal|1000
decl_stmt|;
if|if
condition|(
name|queryBuilder
operator|.
name|includeLower
argument_list|()
operator|==
literal|false
condition|)
block|{
name|fromSlop
operator|=
name|NumericUtils
operator|.
name|sortableLongToDouble
argument_list|(
operator|(
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|fromValue
argument_list|)
argument_list|)
operator|+
literal|1L
operator|)
argument_list|)
operator|/
literal|1000.0
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|geoQuery
operator|.
name|minInclusiveDistance
argument_list|()
argument_list|,
name|closeTo
argument_list|(
name|fromValue
argument_list|,
name|fromSlop
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryBuilder
operator|.
name|to
argument_list|()
operator|!=
literal|null
operator|&&
name|queryBuilder
operator|.
name|to
argument_list|()
operator|instanceof
name|Number
condition|)
block|{
name|double
name|toValue
init|=
operator|(
operator|(
name|Number
operator|)
name|queryBuilder
operator|.
name|to
argument_list|()
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryBuilder
operator|.
name|unit
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|toValue
operator|=
name|queryBuilder
operator|.
name|unit
argument_list|()
operator|.
name|toMeters
argument_list|(
name|toValue
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryBuilder
operator|.
name|geoDistance
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|toValue
operator|=
name|queryBuilder
operator|.
name|geoDistance
argument_list|()
operator|.
name|normalize
argument_list|(
name|toValue
argument_list|,
name|DistanceUnit
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
name|double
name|toSlop
init|=
name|Math
operator|.
name|abs
argument_list|(
name|toValue
argument_list|)
operator|/
literal|1000
decl_stmt|;
if|if
condition|(
name|queryBuilder
operator|.
name|includeUpper
argument_list|()
operator|==
literal|false
condition|)
block|{
name|toSlop
operator|=
name|NumericUtils
operator|.
name|sortableLongToDouble
argument_list|(
operator|(
name|NumericUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|toValue
argument_list|)
argument_list|)
operator|-
literal|1L
operator|)
argument_list|)
operator|/
literal|1000.0
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|geoQuery
operator|.
name|maxInclusiveDistance
argument_list|()
argument_list|,
name|closeTo
argument_list|(
name|toValue
argument_list|,
name|toSlop
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertGeoPointQuery
specifier|private
name|void
name|assertGeoPointQuery
parameter_list|(
name|GeoDistanceRangeQueryBuilder
name|queryBuilder
parameter_list|,
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|XGeoPointDistanceRangeQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|XGeoPointDistanceRangeQuery
name|geoQuery
init|=
operator|(
name|XGeoPointDistanceRangeQuery
operator|)
name|query
decl_stmt|;
name|assertThat
argument_list|(
name|geoQuery
operator|.
name|getField
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|fieldName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryBuilder
operator|.
name|point
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|GeoPoint
name|expectedPoint
init|=
operator|new
name|GeoPoint
argument_list|(
name|queryBuilder
operator|.
name|point
argument_list|()
argument_list|)
decl_stmt|;
name|GeoUtils
operator|.
name|normalizePoint
argument_list|(
name|expectedPoint
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|geoQuery
operator|.
name|getCenterLat
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedPoint
operator|.
name|lat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|geoQuery
operator|.
name|getCenterLon
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedPoint
operator|.
name|lon
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryBuilder
operator|.
name|from
argument_list|()
operator|!=
literal|null
operator|&&
name|queryBuilder
operator|.
name|from
argument_list|()
operator|instanceof
name|Number
condition|)
block|{
name|double
name|fromValue
init|=
operator|(
operator|(
name|Number
operator|)
name|queryBuilder
operator|.
name|from
argument_list|()
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryBuilder
operator|.
name|unit
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|fromValue
operator|=
name|queryBuilder
operator|.
name|unit
argument_list|()
operator|.
name|toMeters
argument_list|(
name|fromValue
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|geoQuery
operator|.
name|getMinRadiusMeters
argument_list|()
argument_list|,
name|closeTo
argument_list|(
name|fromValue
argument_list|,
literal|1E
operator|-
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryBuilder
operator|.
name|to
argument_list|()
operator|!=
literal|null
operator|&&
name|queryBuilder
operator|.
name|to
argument_list|()
operator|instanceof
name|Number
condition|)
block|{
name|double
name|toValue
init|=
operator|(
operator|(
name|Number
operator|)
name|queryBuilder
operator|.
name|to
argument_list|()
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryBuilder
operator|.
name|unit
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|toValue
operator|=
name|queryBuilder
operator|.
name|unit
argument_list|()
operator|.
name|toMeters
argument_list|(
name|toValue
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|geoQuery
operator|.
name|getMaxRadiusMeters
argument_list|()
argument_list|,
name|closeTo
argument_list|(
name|toValue
argument_list|,
literal|1E
operator|-
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Overridden here to ensure the test is only run if at least one type is      * present in the mappings. Geo queries do not execute if the field is not      * explicitly mapped      */
annotation|@
name|Override
DECL|method|testToQuery
specifier|public
name|void
name|testToQuery
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeTrue
argument_list|(
literal|"test runs only when at least a type is registered"
argument_list|,
name|getCurrentTypes
argument_list|()
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|super
operator|.
name|testToQuery
argument_list|()
expr_stmt|;
block|}
DECL|method|testNullFieldName
specifier|public
name|void
name|testNullFieldName
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
literal|null
argument_list|,
operator|new
name|GeoPoint
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
literal|""
argument_list|,
operator|new
name|GeoPoint
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"fieldName must not be null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNoPoint
specifier|public
name|void
name|testNoPoint
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
operator|(
name|GeoPoint
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"point must not be null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInvalidFrom
specifier|public
name|void
name|testInvalidFrom
parameter_list|()
block|{
name|GeoDistanceRangeQueryBuilder
name|builder
init|=
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
operator|new
name|GeoPoint
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|from
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|from
argument_list|(
operator|(
name|Number
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"[from] must not be null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInvalidTo
specifier|public
name|void
name|testInvalidTo
parameter_list|()
block|{
name|GeoDistanceRangeQueryBuilder
name|builder
init|=
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
operator|new
name|GeoPoint
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|to
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|to
argument_list|(
operator|(
name|Number
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"[to] must not be null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInvalidOptimizeBBox
specifier|public
name|void
name|testInvalidOptimizeBBox
parameter_list|()
block|{
name|GeoDistanceRangeQueryBuilder
name|builder
init|=
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
operator|new
name|GeoPoint
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
try|try
block|{
name|builder
operator|.
name|optimizeBbox
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"optimizeBbox must not be null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
block|{
name|builder
operator|.
name|optimizeBbox
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"optimizeBbox must be one of [none, memory, indexed]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testInvalidGeoDistance
specifier|public
name|void
name|testInvalidGeoDistance
parameter_list|()
block|{
name|GeoDistanceRangeQueryBuilder
name|builder
init|=
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
operator|new
name|GeoPoint
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|geoDistance
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"geoDistance calculation mode must not be null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInvalidDistanceUnit
specifier|public
name|void
name|testInvalidDistanceUnit
parameter_list|()
block|{
name|GeoDistanceRangeQueryBuilder
name|builder
init|=
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
operator|new
name|GeoPoint
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|unit
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"distance unit must not be null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNestedRangeQuery
specifier|public
name|void
name|testNestedRangeQuery
parameter_list|()
throws|throws
name|IOException
block|{
comment|// create a nested geo_point type with a subfield named "geohash" (explicit testing for ISSUE #15179)
name|MapperService
name|mapperService
init|=
name|queryShardContext
argument_list|()
operator|.
name|getMapperService
argument_list|()
decl_stmt|;
name|String
name|nestedMapping
init|=
literal|"{\"nested_doc\" : {\"properties\" : {"
operator|+
literal|"\"locations\": {\"properties\": {"
operator|+
literal|"\"geohash\": {\"type\": \"geo_point\"}},"
operator|+
literal|"\"type\": \"nested\"}"
operator|+
literal|"}}}"
decl_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
literal|"nested_doc"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|nestedMapping
argument_list|)
argument_list|,
name|MapperService
operator|.
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// create a range query on the nested locations.geohash sub-field
name|String
name|queryJson
init|=
literal|"{\n"
operator|+
literal|"  \"nested\": {\n"
operator|+
literal|"    \"path\": \"locations\",\n"
operator|+
literal|"    \"query\": {\n"
operator|+
literal|"      \"geo_distance_range\": {\n"
operator|+
literal|"        \"from\": \"0.0km\",\n"
operator|+
literal|"        \"to\" : \"200.0km\",\n"
operator|+
literal|"        \"locations.geohash\": \"s7ws01wyd7ws\"\n"
operator|+
literal|"      }\n"
operator|+
literal|"    }\n"
operator|+
literal|"  }\n"
operator|+
literal|"}\n"
decl_stmt|;
name|NestedQueryBuilder
name|builder
init|=
operator|(
name|NestedQueryBuilder
operator|)
name|parseQuery
argument_list|(
name|queryJson
argument_list|)
decl_stmt|;
name|QueryShardContext
name|context
init|=
name|createShardContext
argument_list|()
decl_stmt|;
name|builder
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|testFromJson
specifier|public
name|void
name|testFromJson
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|json
init|=
literal|"{\n"
operator|+
literal|"  \"geo_distance_range\" : {\n"
operator|+
literal|"    \"pin.location\" : [ -70.0, 40.0 ],\n"
operator|+
literal|"    \"from\" : \"200km\",\n"
operator|+
literal|"    \"to\" : \"400km\",\n"
operator|+
literal|"    \"include_lower\" : true,\n"
operator|+
literal|"    \"include_upper\" : true,\n"
operator|+
literal|"    \"unit\" : \"m\",\n"
operator|+
literal|"    \"distance_type\" : \"sloppy_arc\",\n"
operator|+
literal|"    \"optimize_bbox\" : \"memory\",\n"
operator|+
literal|"    \"validation_method\" : \"STRICT\",\n"
operator|+
literal|"    \"ignore_unmapped\" : false,\n"
operator|+
literal|"    \"boost\" : 1.0\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
decl_stmt|;
name|GeoDistanceRangeQueryBuilder
name|parsed
init|=
operator|(
name|GeoDistanceRangeQueryBuilder
operator|)
name|parseQuery
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|checkGeneratedJson
argument_list|(
name|json
argument_list|,
name|parsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
argument_list|,
operator|-
literal|70.0
argument_list|,
name|parsed
operator|.
name|point
argument_list|()
operator|.
name|lon
argument_list|()
argument_list|,
literal|0.0001
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testMustRewrite
specifier|public
name|void
name|testMustRewrite
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeTrue
argument_list|(
literal|"test runs only when at least a type is registered"
argument_list|,
name|getCurrentTypes
argument_list|()
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|super
operator|.
name|testMustRewrite
argument_list|()
expr_stmt|;
block|}
DECL|method|testIgnoreUnmapped
specifier|public
name|void
name|testIgnoreUnmapped
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|GeoDistanceRangeQueryBuilder
name|queryBuilder
init|=
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
literal|"unmapped"
argument_list|,
operator|new
name|GeoPoint
argument_list|(
literal|0.0
argument_list|,
literal|0.0
argument_list|)
argument_list|)
operator|.
name|from
argument_list|(
literal|"20m"
argument_list|)
decl_stmt|;
name|queryBuilder
operator|.
name|ignoreUnmapped
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|queryBuilder
operator|.
name|toQuery
argument_list|(
name|queryShardContext
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|MatchNoDocsQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|GeoDistanceRangeQueryBuilder
name|failingQueryBuilder
init|=
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
literal|"unmapped"
argument_list|,
operator|new
name|GeoPoint
argument_list|(
literal|0.0
argument_list|,
literal|0.0
argument_list|)
argument_list|)
operator|.
name|from
argument_list|(
literal|"20m"
argument_list|)
decl_stmt|;
name|failingQueryBuilder
operator|.
name|ignoreUnmapped
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|QueryShardException
name|e
init|=
name|expectThrows
argument_list|(
name|QueryShardException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|failingQueryBuilder
operator|.
name|toQuery
argument_list|(
name|queryShardContext
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"failed to find geo_point field [unmapped]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

