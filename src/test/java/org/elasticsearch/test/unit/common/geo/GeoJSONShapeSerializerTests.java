begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.test.unit.common.geo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
operator|.
name|common
operator|.
name|geo
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
name|Shape
import|;
end_import

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
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|jts
operator|.
name|JtsPoint
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
name|*
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
name|GeoJSONShapeSerializer
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
name|testng
operator|.
name|annotations
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
name|testng
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Tests for {@link GeoJSONShapeSerializer}  */
end_comment

begin_class
DECL|class|GeoJSONShapeSerializerTests
specifier|public
class|class
name|GeoJSONShapeSerializerTests
block|{
DECL|field|GEOMETRY_FACTORY
specifier|private
specifier|static
specifier|final
name|GeometryFactory
name|GEOMETRY_FACTORY
init|=
operator|new
name|GeometryFactory
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testSerialize_simplePoint
specifier|public
name|void
name|testSerialize_simplePoint
parameter_list|()
throws|throws
name|IOException
block|{
name|XContentBuilder
name|expected
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"Point"
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"coordinates"
argument_list|)
operator|.
name|value
argument_list|(
literal|100.0
argument_list|)
operator|.
name|value
argument_list|(
literal|0.0
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|Point
name|point
init|=
name|GEOMETRY_FACTORY
operator|.
name|createPoint
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|100.0
argument_list|,
literal|0.0
argument_list|)
argument_list|)
decl_stmt|;
name|assertSerializationEquals
argument_list|(
name|expected
argument_list|,
operator|new
name|JtsPoint
argument_list|(
name|point
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSerialize_lineString
specifier|public
name|void
name|testSerialize_lineString
parameter_list|()
throws|throws
name|IOException
block|{
name|XContentBuilder
name|expected
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"LineString"
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"coordinates"
argument_list|)
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|100.0
argument_list|)
operator|.
name|value
argument_list|(
literal|0.0
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|101.0
argument_list|)
operator|.
name|value
argument_list|(
literal|1.0
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Coordinate
argument_list|>
name|lineCoordinates
init|=
operator|new
name|ArrayList
argument_list|<
name|Coordinate
argument_list|>
argument_list|()
decl_stmt|;
name|lineCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|100
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|lineCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|101
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|LineString
name|lineString
init|=
name|GEOMETRY_FACTORY
operator|.
name|createLineString
argument_list|(
name|lineCoordinates
operator|.
name|toArray
argument_list|(
operator|new
name|Coordinate
index|[
name|lineCoordinates
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|assertSerializationEquals
argument_list|(
name|expected
argument_list|,
operator|new
name|JtsGeometry
argument_list|(
name|lineString
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSerialize_polygonNoHoles
specifier|public
name|void
name|testSerialize_polygonNoHoles
parameter_list|()
throws|throws
name|IOException
block|{
name|XContentBuilder
name|expected
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"Polygon"
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"coordinates"
argument_list|)
operator|.
name|startArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|100.0
argument_list|)
operator|.
name|value
argument_list|(
literal|0.0
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|101.0
argument_list|)
operator|.
name|value
argument_list|(
literal|0.0
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|101.0
argument_list|)
operator|.
name|value
argument_list|(
literal|1.0
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|100.0
argument_list|)
operator|.
name|value
argument_list|(
literal|1.0
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|100.0
argument_list|)
operator|.
name|value
argument_list|(
literal|0.0
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Coordinate
argument_list|>
name|shellCoordinates
init|=
operator|new
name|ArrayList
argument_list|<
name|Coordinate
argument_list|>
argument_list|()
decl_stmt|;
name|shellCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|100
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|shellCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|101
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|shellCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|101
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|shellCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|100
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|shellCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|100
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|LinearRing
name|shell
init|=
name|GEOMETRY_FACTORY
operator|.
name|createLinearRing
argument_list|(
name|shellCoordinates
operator|.
name|toArray
argument_list|(
operator|new
name|Coordinate
index|[
name|shellCoordinates
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|Polygon
name|polygon
init|=
name|GEOMETRY_FACTORY
operator|.
name|createPolygon
argument_list|(
name|shell
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertSerializationEquals
argument_list|(
name|expected
argument_list|,
operator|new
name|JtsGeometry
argument_list|(
name|polygon
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSerialize_polygonWithHole
specifier|public
name|void
name|testSerialize_polygonWithHole
parameter_list|()
throws|throws
name|IOException
block|{
name|XContentBuilder
name|expected
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"Polygon"
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"coordinates"
argument_list|)
operator|.
name|startArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|100.0
argument_list|)
operator|.
name|value
argument_list|(
literal|0.0
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|101.0
argument_list|)
operator|.
name|value
argument_list|(
literal|0.0
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|101.0
argument_list|)
operator|.
name|value
argument_list|(
literal|1.0
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|100.0
argument_list|)
operator|.
name|value
argument_list|(
literal|1.0
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|100.0
argument_list|)
operator|.
name|value
argument_list|(
literal|0.0
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|100.2
argument_list|)
operator|.
name|value
argument_list|(
literal|0.2
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|100.8
argument_list|)
operator|.
name|value
argument_list|(
literal|0.2
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|100.8
argument_list|)
operator|.
name|value
argument_list|(
literal|0.8
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|100.2
argument_list|)
operator|.
name|value
argument_list|(
literal|0.8
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|100.2
argument_list|)
operator|.
name|value
argument_list|(
literal|0.2
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Coordinate
argument_list|>
name|shellCoordinates
init|=
operator|new
name|ArrayList
argument_list|<
name|Coordinate
argument_list|>
argument_list|()
decl_stmt|;
name|shellCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|100
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|shellCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|101
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|shellCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|101
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|shellCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|100
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|shellCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|100
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Coordinate
argument_list|>
name|holeCoordinates
init|=
operator|new
name|ArrayList
argument_list|<
name|Coordinate
argument_list|>
argument_list|()
decl_stmt|;
name|holeCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|100.2
argument_list|,
literal|0.2
argument_list|)
argument_list|)
expr_stmt|;
name|holeCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|100.8
argument_list|,
literal|0.2
argument_list|)
argument_list|)
expr_stmt|;
name|holeCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|100.8
argument_list|,
literal|0.8
argument_list|)
argument_list|)
expr_stmt|;
name|holeCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|100.2
argument_list|,
literal|0.8
argument_list|)
argument_list|)
expr_stmt|;
name|holeCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|100.2
argument_list|,
literal|0.2
argument_list|)
argument_list|)
expr_stmt|;
name|LinearRing
name|shell
init|=
name|GEOMETRY_FACTORY
operator|.
name|createLinearRing
argument_list|(
name|shellCoordinates
operator|.
name|toArray
argument_list|(
operator|new
name|Coordinate
index|[
name|shellCoordinates
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|LinearRing
index|[]
name|holes
init|=
operator|new
name|LinearRing
index|[
literal|1
index|]
decl_stmt|;
name|holes
index|[
literal|0
index|]
operator|=
name|GEOMETRY_FACTORY
operator|.
name|createLinearRing
argument_list|(
name|holeCoordinates
operator|.
name|toArray
argument_list|(
operator|new
name|Coordinate
index|[
name|holeCoordinates
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|Polygon
name|polygon
init|=
name|GEOMETRY_FACTORY
operator|.
name|createPolygon
argument_list|(
name|shell
argument_list|,
name|holes
argument_list|)
decl_stmt|;
name|assertSerializationEquals
argument_list|(
name|expected
argument_list|,
operator|new
name|JtsGeometry
argument_list|(
name|polygon
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSerialize_multiPoint
specifier|public
name|void
name|testSerialize_multiPoint
parameter_list|()
throws|throws
name|IOException
block|{
name|XContentBuilder
name|expected
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"MultiPoint"
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"coordinates"
argument_list|)
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|100.0
argument_list|)
operator|.
name|value
argument_list|(
literal|0.0
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|startArray
argument_list|()
operator|.
name|value
argument_list|(
literal|101.0
argument_list|)
operator|.
name|value
argument_list|(
literal|1.0
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Coordinate
argument_list|>
name|multiPointCoordinates
init|=
operator|new
name|ArrayList
argument_list|<
name|Coordinate
argument_list|>
argument_list|()
decl_stmt|;
name|multiPointCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|100
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|multiPointCoordinates
operator|.
name|add
argument_list|(
operator|new
name|Coordinate
argument_list|(
literal|101
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|MultiPoint
name|multiPoint
init|=
name|GEOMETRY_FACTORY
operator|.
name|createMultiPoint
argument_list|(
name|multiPointCoordinates
operator|.
name|toArray
argument_list|(
operator|new
name|Coordinate
index|[
name|multiPointCoordinates
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|assertSerializationEquals
argument_list|(
name|expected
argument_list|,
operator|new
name|JtsGeometry
argument_list|(
name|multiPoint
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertSerializationEquals
specifier|private
name|void
name|assertSerializationEquals
parameter_list|(
name|XContentBuilder
name|expected
parameter_list|,
name|Shape
name|shape
parameter_list|)
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
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|GeoJSONShapeSerializer
operator|.
name|serialize
argument_list|(
name|shape
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|string
argument_list|()
argument_list|,
name|builder
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

