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
name|joda
operator|.
name|time
operator|.
name|DateTime
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|instanceOf
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
name|notNullValue
import|;
end_import

begin_class
DECL|class|GeoDistanceRangeQueryTests
specifier|public
class|class
name|GeoDistanceRangeQueryTests
extends|extends
name|BaseQueryTestCase
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
name|GeoDistanceRangeQueryBuilder
name|builder
init|=
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
name|GEO_FIELD_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|geohash
argument_list|(
name|randomGeohash
argument_list|(
literal|1
argument_list|,
literal|12
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|double
name|lat
init|=
name|randomDouble
argument_list|()
operator|*
literal|180
operator|-
literal|90
decl_stmt|;
name|double
name|lon
init|=
name|randomDouble
argument_list|()
operator|*
literal|360
operator|-
literal|180
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|point
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|point
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|fromValue
init|=
name|randomInt
argument_list|(
literal|1000000
argument_list|)
decl_stmt|;
name|int
name|toValue
init|=
name|randomIntBetween
argument_list|(
name|fromValue
argument_list|,
literal|1000000
argument_list|)
decl_stmt|;
name|String
name|fromToUnits
init|=
name|randomFrom
argument_list|(
name|DistanceUnit
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
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
argument_list|)
expr_stmt|;
name|builder
operator|.
name|to
argument_list|(
name|toValue
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
name|fromToUnits
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
name|fromToUnits
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
name|fromToUnits
argument_list|)
expr_stmt|;
name|builder
operator|.
name|to
argument_list|(
name|toValue
operator|+
name|fromToUnits
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
condition|)
block|{
name|builder
operator|.
name|unit
argument_list|(
name|randomFrom
argument_list|(
name|DistanceUnit
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
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|coerce
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
name|ignoreMalformed
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
name|assertThat
argument_list|(
name|geoQuery
operator|.
name|lat
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|point
argument_list|()
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
name|queryBuilder
operator|.
name|point
argument_list|()
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
name|Math
operator|.
name|abs
argument_list|(
name|fromValue
argument_list|)
operator|/
literal|1000
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
name|Math
operator|.
name|abs
argument_list|(
name|toValue
argument_list|)
operator|/
literal|1000
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
annotation|@
name|Test
DECL|method|testNullFieldName
specifier|public
name|void
name|testNullFieldName
parameter_list|()
block|{
name|GeoDistanceRangeQueryBuilder
name|builder
init|=
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|builder
operator|.
name|geohash
argument_list|(
name|randomGeohash
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|from
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|QueryValidationException
name|exception
init|=
name|builder
operator|.
name|validate
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|exception
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"["
operator|+
name|GeoDistanceRangeQueryBuilder
operator|.
name|NAME
operator|+
literal|"] fieldName must not be null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoPoint
specifier|public
name|void
name|testNoPoint
parameter_list|()
block|{
name|GeoDistanceRangeQueryBuilder
name|builder
init|=
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
name|GEO_FIELD_NAME
argument_list|)
decl_stmt|;
name|builder
operator|.
name|from
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|QueryValidationException
name|exception
init|=
name|builder
operator|.
name|validate
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|exception
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"["
operator|+
name|GeoDistanceRangeQueryBuilder
operator|.
name|NAME
operator|+
literal|"] point must not be null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoFromOrTo
specifier|public
name|void
name|testNoFromOrTo
parameter_list|()
block|{
name|GeoDistanceRangeQueryBuilder
name|builder
init|=
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
name|GEO_FIELD_NAME
argument_list|)
decl_stmt|;
name|String
name|geohash
init|=
name|randomGeohash
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|builder
operator|.
name|geohash
argument_list|(
name|geohash
argument_list|)
expr_stmt|;
name|QueryValidationException
name|exception
init|=
name|builder
operator|.
name|validate
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|exception
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"["
operator|+
name|GeoDistanceRangeQueryBuilder
operator|.
name|NAME
operator|+
literal|"] Must define at least one parameter from [from, to]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
name|GEO_FIELD_NAME
argument_list|)
decl_stmt|;
name|String
name|geohash
init|=
name|randomGeohash
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|builder
operator|.
name|geohash
argument_list|(
name|geohash
argument_list|)
expr_stmt|;
name|builder
operator|.
name|from
argument_list|(
operator|new
name|DateTime
argument_list|()
argument_list|)
expr_stmt|;
name|QueryValidationException
name|exception
init|=
name|builder
operator|.
name|validate
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|exception
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"["
operator|+
name|GeoDistanceRangeQueryBuilder
operator|.
name|NAME
operator|+
literal|"] from must either be a number or a string. Found ["
operator|+
name|DateTime
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
name|GEO_FIELD_NAME
argument_list|)
decl_stmt|;
name|String
name|geohash
init|=
name|randomGeohash
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|builder
operator|.
name|geohash
argument_list|(
name|geohash
argument_list|)
expr_stmt|;
name|builder
operator|.
name|to
argument_list|(
operator|new
name|DateTime
argument_list|()
argument_list|)
expr_stmt|;
name|QueryValidationException
name|exception
init|=
name|builder
operator|.
name|validate
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|exception
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"["
operator|+
name|GeoDistanceRangeQueryBuilder
operator|.
name|NAME
operator|+
literal|"] to must either be a number or a string. Found ["
operator|+
name|DateTime
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
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
name|GEO_FIELD_NAME
argument_list|)
decl_stmt|;
name|String
name|geohash
init|=
name|randomGeohash
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|builder
operator|.
name|geohash
argument_list|(
name|geohash
argument_list|)
expr_stmt|;
name|builder
operator|.
name|from
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|builder
operator|.
name|optimizeBbox
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|QueryValidationException
name|exception
init|=
name|builder
operator|.
name|validate
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|exception
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"["
operator|+
name|GeoDistanceRangeQueryBuilder
operator|.
name|NAME
operator|+
literal|"] optimizeBbox must be one of [none, memory, indexed]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleValidationErrors
specifier|public
name|void
name|testMultipleValidationErrors
parameter_list|()
block|{
name|GeoDistanceRangeQueryBuilder
name|builder
init|=
operator|new
name|GeoDistanceRangeQueryBuilder
argument_list|(
name|GEO_FIELD_NAME
argument_list|)
decl_stmt|;
name|double
name|lat
init|=
name|randomDouble
argument_list|()
operator|*
literal|360
operator|-
literal|180
decl_stmt|;
name|double
name|lon
init|=
name|randomDouble
argument_list|()
operator|*
literal|360
operator|-
literal|180
decl_stmt|;
name|builder
operator|.
name|point
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|)
expr_stmt|;
name|builder
operator|.
name|from
argument_list|(
operator|new
name|DateTime
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|to
argument_list|(
operator|new
name|DateTime
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|optimizeBbox
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|QueryValidationException
name|exception
init|=
name|builder
operator|.
name|validate
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|exception
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|exception
operator|.
name|validationErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
