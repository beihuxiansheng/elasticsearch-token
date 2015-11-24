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
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Point
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
name|Term
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
name|queries
operator|.
name|TermsQuery
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
name|TermQuery
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
name|query
operator|.
name|GeohashCellQuery
operator|.
name|Builder
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
name|RandomShapeGenerator
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
name|containsString
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
name|is
import|;
end_import

begin_class
DECL|class|GeohashCellQueryBuilderTests
specifier|public
class|class
name|GeohashCellQueryBuilderTests
extends|extends
name|AbstractQueryTestCase
argument_list|<
name|Builder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|Builder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|GeohashCellQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|Builder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
name|randomGeohash
argument_list|(
literal|1
argument_list|,
literal|12
argument_list|)
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
name|neighbors
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
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|precision
argument_list|(
name|randomIntBetween
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
name|builder
operator|.
name|precision
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|1000000
argument_list|)
operator|+
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
argument_list|)
expr_stmt|;
block|}
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
name|Builder
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
if|if
condition|(
name|queryBuilder
operator|.
name|neighbors
argument_list|()
condition|)
block|{
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|TermsQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|TermQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|TermQuery
name|termQuery
init|=
operator|(
name|TermQuery
operator|)
name|query
decl_stmt|;
name|Term
name|term
init|=
name|termQuery
operator|.
name|getTerm
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|queryBuilder
operator|.
name|fieldName
argument_list|()
operator|+
name|GeoPointFieldMapper
operator|.
name|Names
operator|.
name|GEOHASH_SUFFIX
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|geohash
init|=
name|queryBuilder
operator|.
name|geohash
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryBuilder
operator|.
name|precision
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|int
name|len
init|=
name|Math
operator|.
name|min
argument_list|(
name|queryBuilder
operator|.
name|precision
argument_list|()
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
name|assertThat
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|geohash
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
DECL|method|testNullField
specifier|public
name|void
name|testNullField
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
name|Builder
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
name|Builder
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
DECL|method|testNullGeoPoint
specifier|public
name|void
name|testNullGeoPoint
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
name|Builder
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
name|Builder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
literal|""
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
literal|"geohash or point must be defined"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInvalidPrecision
specifier|public
name|void
name|testInvalidPrecision
parameter_list|()
block|{
name|GeohashCellQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|Builder
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
name|precision
argument_list|(
operator|-
literal|1
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
name|containsString
argument_list|(
literal|"precision must be greater than 0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testLocationParsing
specifier|public
name|void
name|testLocationParsing
parameter_list|()
throws|throws
name|IOException
block|{
name|Point
name|point
init|=
name|RandomShapeGenerator
operator|.
name|xRandomPoint
argument_list|(
name|getRandom
argument_list|()
argument_list|)
decl_stmt|;
name|Builder
name|pointTestBuilder
init|=
operator|new
name|GeohashCellQuery
operator|.
name|Builder
argument_list|(
literal|"pin"
argument_list|,
operator|new
name|GeoPoint
argument_list|(
name|point
operator|.
name|getY
argument_list|()
argument_list|,
name|point
operator|.
name|getX
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|pointTest1
init|=
literal|"{\"geohash_cell\": {\"pin\": {\"lat\": "
operator|+
name|point
operator|.
name|getY
argument_list|()
operator|+
literal|",\"lon\": "
operator|+
name|point
operator|.
name|getX
argument_list|()
operator|+
literal|"}}}"
decl_stmt|;
name|assertParsedQuery
argument_list|(
name|pointTest1
argument_list|,
name|pointTestBuilder
argument_list|)
expr_stmt|;
name|String
name|pointTest2
init|=
literal|"{\"geohash_cell\": {\"pin\": \""
operator|+
name|point
operator|.
name|getY
argument_list|()
operator|+
literal|","
operator|+
name|point
operator|.
name|getX
argument_list|()
operator|+
literal|"\"}}"
decl_stmt|;
name|assertParsedQuery
argument_list|(
name|pointTest2
argument_list|,
name|pointTestBuilder
argument_list|)
expr_stmt|;
name|String
name|pointTest3
init|=
literal|"{\"geohash_cell\": {\"pin\": ["
operator|+
name|point
operator|.
name|getX
argument_list|()
operator|+
literal|","
operator|+
name|point
operator|.
name|getY
argument_list|()
operator|+
literal|"]}}"
decl_stmt|;
name|assertParsedQuery
argument_list|(
name|pointTest3
argument_list|,
name|pointTestBuilder
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
literal|"  \"geohash_cell\" : {\n"
operator|+
literal|"    \"neighbors\" : true,\n"
operator|+
literal|"    \"precision\" : 3,\n"
operator|+
literal|"    \"pin\" : \"t4mk70fgk067\",\n"
operator|+
literal|"    \"boost\" : 1.0\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
decl_stmt|;
name|GeohashCellQuery
operator|.
name|Builder
name|parsed
init|=
operator|(
name|GeohashCellQuery
operator|.
name|Builder
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
literal|3
argument_list|,
name|parsed
operator|.
name|precision
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

