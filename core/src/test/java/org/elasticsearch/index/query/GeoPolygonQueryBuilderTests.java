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
name|jts
operator|.
name|JtsGeometry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|vividsolutions
operator|.
name|jts
operator|.
name|geom
operator|.
name|Coordinate
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
name|GeoPointInPolygonQuery
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
name|geo
operator|.
name|builders
operator|.
name|ShapeBuilder
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
name|XContentFactory
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
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|geo
operator|.
name|RandomShapeGenerator
operator|.
name|ShapeType
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|StreamsUtils
operator|.
name|copyToStringFromClasspath
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
name|is
import|;
end_import

begin_class
DECL|class|GeoPolygonQueryBuilderTests
specifier|public
class|class
name|GeoPolygonQueryBuilderTests
extends|extends
name|AbstractQueryTestCase
argument_list|<
name|GeoPolygonQueryBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreateTestQueryBuilder
specifier|protected
name|GeoPolygonQueryBuilder
name|doCreateTestQueryBuilder
parameter_list|()
block|{
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|polygon
init|=
name|randomPolygon
argument_list|(
name|randomIntBetween
argument_list|(
literal|4
argument_list|,
literal|50
argument_list|)
argument_list|)
decl_stmt|;
name|GeoPolygonQueryBuilder
name|builder
init|=
operator|new
name|GeoPolygonQueryBuilder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
name|polygon
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
name|GeoPolygonQueryBuilder
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
name|GeoPolygonQueryBuilder
name|queryBuilder
parameter_list|,
name|Query
name|query
parameter_list|)
block|{
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|GeoPolygonQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|GeoPolygonQuery
name|geoQuery
init|=
operator|(
name|GeoPolygonQuery
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
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|queryBuilderPoints
init|=
name|queryBuilder
operator|.
name|points
argument_list|()
decl_stmt|;
name|GeoPoint
index|[]
name|queryPoints
init|=
name|geoQuery
operator|.
name|points
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|queryPoints
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
name|queryBuilderPoints
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|queryBuilderPoints
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|GeoPoint
name|queryBuilderPoint
init|=
name|queryBuilderPoints
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|GeoPoint
name|pointCopy
init|=
operator|new
name|GeoPoint
argument_list|(
name|queryBuilderPoint
argument_list|)
decl_stmt|;
name|GeoUtils
operator|.
name|normalizePoint
argument_list|(
name|pointCopy
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|queryPoints
index|[
name|i
index|]
argument_list|,
name|equalTo
argument_list|(
name|pointCopy
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
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
name|queryBuilderPoints
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|queryPoints
index|[
name|i
index|]
argument_list|,
name|equalTo
argument_list|(
name|queryBuilderPoints
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|assertGeoPointQuery
specifier|private
name|void
name|assertGeoPointQuery
parameter_list|(
name|GeoPolygonQueryBuilder
name|queryBuilder
parameter_list|,
name|Query
name|query
parameter_list|)
block|{
name|assertThat
argument_list|(
name|query
argument_list|,
name|instanceOf
argument_list|(
name|GeoPointInPolygonQuery
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|GeoPointInPolygonQuery
name|geoQuery
init|=
operator|(
name|GeoPointInPolygonQuery
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
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|queryBuilderPoints
init|=
name|queryBuilder
operator|.
name|points
argument_list|()
decl_stmt|;
name|double
index|[]
name|lats
init|=
name|geoQuery
operator|.
name|getLats
argument_list|()
decl_stmt|;
name|double
index|[]
name|lons
init|=
name|geoQuery
operator|.
name|getLons
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|lats
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
name|queryBuilderPoints
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lons
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
name|queryBuilderPoints
operator|.
name|size
argument_list|()
argument_list|)
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
name|queryBuilderPoints
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|GeoPoint
name|queryBuilderPoint
init|=
name|queryBuilderPoints
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|GeoPoint
name|pointCopy
init|=
operator|new
name|GeoPoint
argument_list|(
name|queryBuilderPoint
argument_list|)
decl_stmt|;
name|GeoUtils
operator|.
name|normalizePoint
argument_list|(
name|pointCopy
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lats
index|[
name|i
index|]
argument_list|,
name|closeTo
argument_list|(
name|pointCopy
operator|.
name|getLat
argument_list|()
argument_list|,
literal|1E
operator|-
literal|5D
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lons
index|[
name|i
index|]
argument_list|,
name|closeTo
argument_list|(
name|pointCopy
operator|.
name|getLon
argument_list|()
argument_list|,
literal|1E
operator|-
literal|5D
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
DECL|method|randomPolygon
specifier|public
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|randomPolygon
parameter_list|(
name|int
name|numPoints
parameter_list|)
block|{
name|ShapeBuilder
name|shapeBuilder
init|=
literal|null
decl_stmt|;
comment|// This is a temporary fix because sometimes the RandomShapeGenerator
comment|// returns null. This is if there is an error generating the polygon. So
comment|// in this case keep trying until we successfully generate one
while|while
condition|(
name|shapeBuilder
operator|==
literal|null
condition|)
block|{
name|shapeBuilder
operator|=
name|RandomShapeGenerator
operator|.
name|createShapeWithin
argument_list|(
name|getRandom
argument_list|()
argument_list|,
literal|null
argument_list|,
name|ShapeType
operator|.
name|POLYGON
argument_list|)
expr_stmt|;
block|}
name|JtsGeometry
name|shape
init|=
operator|(
name|JtsGeometry
operator|)
name|shapeBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|Coordinate
index|[]
name|coordinates
init|=
name|shape
operator|.
name|getGeom
argument_list|()
operator|.
name|getCoordinates
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|GeoPoint
argument_list|>
name|polygonPoints
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Coordinate
name|coord
range|:
name|coordinates
control|)
block|{
name|polygonPoints
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|coord
operator|.
name|y
argument_list|,
name|coord
operator|.
name|x
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|polygonPoints
return|;
block|}
DECL|method|testNullFieldName
specifier|public
name|void
name|testNullFieldName
parameter_list|()
block|{
try|try
block|{
operator|new
name|GeoPolygonQueryBuilder
argument_list|(
literal|null
argument_list|,
name|randomPolygon
argument_list|(
literal|5
argument_list|)
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
literal|"fieldName must not be null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEmptyPolygon
specifier|public
name|void
name|testEmptyPolygon
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
name|GeoPolygonQueryBuilder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|GeoPoint
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|new
name|GeoPolygonQueryBuilder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
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
literal|"polygon must not be null or empty"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInvalidClosedPolygon
specifier|public
name|void
name|testInvalidClosedPolygon
parameter_list|()
block|{
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|points
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|points
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
literal|0
argument_list|,
literal|90
argument_list|)
argument_list|)
expr_stmt|;
name|points
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
literal|90
argument_list|,
literal|90
argument_list|)
argument_list|)
expr_stmt|;
name|points
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
literal|0
argument_list|,
literal|90
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|GeoPolygonQueryBuilder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
name|points
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
literal|"too few points defined for geo_polygon query"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInvalidOpenPolygon
specifier|public
name|void
name|testInvalidOpenPolygon
parameter_list|()
block|{
name|List
argument_list|<
name|GeoPoint
argument_list|>
name|points
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|points
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
literal|0
argument_list|,
literal|90
argument_list|)
argument_list|)
expr_stmt|;
name|points
operator|.
name|add
argument_list|(
operator|new
name|GeoPoint
argument_list|(
literal|90
argument_list|,
literal|90
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|GeoPolygonQueryBuilder
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|,
name|points
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
literal|"too few points defined for geo_polygon query"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDeprecatedXContent
specifier|public
name|void
name|testDeprecatedXContent
parameter_list|()
throws|throws
name|IOException
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|prettyPrint
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"geo_polygon"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
literal|"points"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|value
argument_list|(
literal|"0,0"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|value
argument_list|(
literal|"0,90"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|value
argument_list|(
literal|"90,90"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|value
argument_list|(
literal|"90,0"
argument_list|)
expr_stmt|;
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
literal|"normalize"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// deprecated
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
try|try
block|{
name|parseQuery
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"normalize is deprecated"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Deprecated field [normalize] used, expected [coerce] instead"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParsingAndToQueryParsingExceptions
specifier|public
name|void
name|testParsingAndToQueryParsingExceptions
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|brokenFiles
init|=
operator|new
name|String
index|[]
block|{
literal|"/org/elasticsearch/index/query/geo_polygon_exception_1.json"
block|,
literal|"/org/elasticsearch/index/query/geo_polygon_exception_2.json"
block|,
literal|"/org/elasticsearch/index/query/geo_polygon_exception_3.json"
block|,
literal|"/org/elasticsearch/index/query/geo_polygon_exception_4.json"
block|,
literal|"/org/elasticsearch/index/query/geo_polygon_exception_5.json"
block|}
decl_stmt|;
for|for
control|(
name|String
name|brokenFile
range|:
name|brokenFiles
control|)
block|{
name|String
name|query
init|=
name|copyToStringFromClasspath
argument_list|(
name|brokenFile
argument_list|)
decl_stmt|;
try|try
block|{
name|parseQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"parsing a broken geo_polygon filter didn't fail as expected while parsing: "
operator|+
name|brokenFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParsingException
name|e
parameter_list|)
block|{
comment|// success!
block|}
block|}
block|}
DECL|method|testParsingAndToQuery1
specifier|public
name|void
name|testParsingAndToQuery1
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
name|String
name|query
init|=
literal|"{\n"
operator|+
literal|"    \"geo_polygon\":{\n"
operator|+
literal|"        \""
operator|+
name|GEO_POINT_FIELD_NAME
operator|+
literal|"\":{\n"
operator|+
literal|"            \"points\":[\n"
operator|+
literal|"                [-70, 40],\n"
operator|+
literal|"                [-80, 30],\n"
operator|+
literal|"                [-90, 20]\n"
operator|+
literal|"            ]\n"
operator|+
literal|"        }\n"
operator|+
literal|"    }\n"
operator|+
literal|"}\n"
decl_stmt|;
name|assertGeoPolygonQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
DECL|method|testParsingAndToQuery2
specifier|public
name|void
name|testParsingAndToQuery2
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
name|String
name|query
init|=
literal|"{\n"
operator|+
literal|"    \"geo_polygon\":{\n"
operator|+
literal|"        \""
operator|+
name|GEO_POINT_FIELD_NAME
operator|+
literal|"\":{\n"
operator|+
literal|"            \"points\":[\n"
operator|+
literal|"                {\n"
operator|+
literal|"                    \"lat\":40,\n"
operator|+
literal|"                    \"lon\":-70\n"
operator|+
literal|"                },\n"
operator|+
literal|"                {\n"
operator|+
literal|"                    \"lat\":30,\n"
operator|+
literal|"                    \"lon\":-80\n"
operator|+
literal|"                },\n"
operator|+
literal|"                {\n"
operator|+
literal|"                    \"lat\":20,\n"
operator|+
literal|"                    \"lon\":-90\n"
operator|+
literal|"                }\n"
operator|+
literal|"            ]\n"
operator|+
literal|"        }\n"
operator|+
literal|"    }\n"
operator|+
literal|"}\n"
decl_stmt|;
name|assertGeoPolygonQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
DECL|method|testParsingAndToQuery3
specifier|public
name|void
name|testParsingAndToQuery3
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
name|String
name|query
init|=
literal|"{\n"
operator|+
literal|"    \"geo_polygon\":{\n"
operator|+
literal|"        \""
operator|+
name|GEO_POINT_FIELD_NAME
operator|+
literal|"\":{\n"
operator|+
literal|"            \"points\":[\n"
operator|+
literal|"                \"40, -70\",\n"
operator|+
literal|"                \"30, -80\",\n"
operator|+
literal|"                \"20, -90\"\n"
operator|+
literal|"            ]\n"
operator|+
literal|"        }\n"
operator|+
literal|"    }\n"
operator|+
literal|"}\n"
decl_stmt|;
name|assertGeoPolygonQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
DECL|method|testParsingAndToQuery4
specifier|public
name|void
name|testParsingAndToQuery4
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
name|String
name|query
init|=
literal|"{\n"
operator|+
literal|"    \"geo_polygon\":{\n"
operator|+
literal|"        \""
operator|+
name|GEO_POINT_FIELD_NAME
operator|+
literal|"\":{\n"
operator|+
literal|"            \"points\":[\n"
operator|+
literal|"                \"drn5x1g8cu2y\",\n"
operator|+
literal|"                \"30, -80\",\n"
operator|+
literal|"                \"20, -90\"\n"
operator|+
literal|"            ]\n"
operator|+
literal|"        }\n"
operator|+
literal|"    }\n"
operator|+
literal|"}\n"
decl_stmt|;
name|assertGeoPolygonQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
DECL|method|assertGeoPolygonQuery
specifier|private
name|void
name|assertGeoPolygonQuery
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryShardContext
name|context
init|=
name|createShardContext
argument_list|()
decl_stmt|;
name|Version
name|version
init|=
name|context
operator|.
name|indexVersionCreated
argument_list|()
decl_stmt|;
name|Query
name|parsedQuery
init|=
name|parseQuery
argument_list|(
name|query
argument_list|)
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
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
name|GeoPolygonQuery
name|filter
init|=
operator|(
name|GeoPolygonQuery
operator|)
name|parsedQuery
decl_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|fieldName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|points
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|points
argument_list|()
index|[
literal|0
index|]
operator|.
name|lat
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|40
argument_list|,
literal|0.00001
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|points
argument_list|()
index|[
literal|0
index|]
operator|.
name|lon
argument_list|()
argument_list|,
name|closeTo
argument_list|(
operator|-
literal|70
argument_list|,
literal|0.00001
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|points
argument_list|()
index|[
literal|1
index|]
operator|.
name|lat
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|30
argument_list|,
literal|0.00001
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|points
argument_list|()
index|[
literal|1
index|]
operator|.
name|lon
argument_list|()
argument_list|,
name|closeTo
argument_list|(
operator|-
literal|80
argument_list|,
literal|0.00001
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|points
argument_list|()
index|[
literal|2
index|]
operator|.
name|lat
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|20
argument_list|,
literal|0.00001
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|points
argument_list|()
index|[
literal|2
index|]
operator|.
name|lon
argument_list|()
argument_list|,
name|closeTo
argument_list|(
operator|-
literal|90
argument_list|,
literal|0.00001
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|GeoPointInPolygonQuery
name|q
init|=
operator|(
name|GeoPointInPolygonQuery
operator|)
name|parsedQuery
decl_stmt|;
name|assertThat
argument_list|(
name|q
operator|.
name|getField
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|GEO_POINT_FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|double
index|[]
name|lats
init|=
name|q
operator|.
name|getLats
argument_list|()
decl_stmt|;
specifier|final
name|double
index|[]
name|lons
init|=
name|q
operator|.
name|getLons
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|lats
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lons
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lats
index|[
literal|0
index|]
argument_list|,
name|closeTo
argument_list|(
literal|40
argument_list|,
literal|1E
operator|-
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lons
index|[
literal|0
index|]
argument_list|,
name|closeTo
argument_list|(
operator|-
literal|70
argument_list|,
literal|1E
operator|-
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lats
index|[
literal|1
index|]
argument_list|,
name|closeTo
argument_list|(
literal|30
argument_list|,
literal|1E
operator|-
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lons
index|[
literal|1
index|]
argument_list|,
name|closeTo
argument_list|(
operator|-
literal|80
argument_list|,
literal|1E
operator|-
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lats
index|[
literal|2
index|]
argument_list|,
name|closeTo
argument_list|(
literal|20
argument_list|,
literal|1E
operator|-
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lons
index|[
literal|2
index|]
argument_list|,
name|closeTo
argument_list|(
operator|-
literal|90
argument_list|,
literal|1E
operator|-
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lats
index|[
literal|3
index|]
argument_list|,
name|equalTo
argument_list|(
name|lats
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lons
index|[
literal|3
index|]
argument_list|,
name|equalTo
argument_list|(
name|lons
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"  \"geo_polygon\" : {\n"
operator|+
literal|"    \"person.location\" : {\n"
operator|+
literal|"      \"points\" : [ [ -70.0, 40.0 ], [ -80.0, 30.0 ], [ -90.0, 20.0 ], [ -70.0, 40.0 ] ]\n"
operator|+
literal|"    },\n"
operator|+
literal|"    \"coerce\" : false,\n"
operator|+
literal|"    \"ignore_malformed\" : false,\n"
operator|+
literal|"    \"boost\" : 1.0\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
decl_stmt|;
name|GeoPolygonQueryBuilder
name|parsed
init|=
operator|(
name|GeoPolygonQueryBuilder
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
literal|4
argument_list|,
name|parsed
operator|.
name|points
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

