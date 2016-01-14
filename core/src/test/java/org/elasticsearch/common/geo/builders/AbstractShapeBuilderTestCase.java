begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.geo.builders
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|geo
operator|.
name|builders
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
name|io
operator|.
name|stream
operator|.
name|BytesStreamOutput
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
name|NamedWriteableAwareStreamInput
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
name|NamedWriteableRegistry
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
name|xcontent
operator|.
name|ToXContent
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
name|common
operator|.
name|xcontent
operator|.
name|XContentHelper
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
name|XContentType
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
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|not
import|;
end_import

begin_class
DECL|class|AbstractShapeBuilderTestCase
specifier|public
specifier|abstract
class|class
name|AbstractShapeBuilderTestCase
parameter_list|<
name|SB
extends|extends
name|ShapeBuilder
parameter_list|>
extends|extends
name|ESTestCase
block|{
DECL|field|NUMBER_OF_TESTBUILDERS
specifier|private
specifier|static
specifier|final
name|int
name|NUMBER_OF_TESTBUILDERS
init|=
literal|100
decl_stmt|;
DECL|field|namedWriteableRegistry
specifier|private
specifier|static
name|NamedWriteableRegistry
name|namedWriteableRegistry
decl_stmt|;
comment|/**      * setup for the whole base test class      */
annotation|@
name|BeforeClass
DECL|method|init
specifier|public
specifier|static
name|void
name|init
parameter_list|()
block|{
if|if
condition|(
name|namedWriteableRegistry
operator|==
literal|null
condition|)
block|{
name|namedWriteableRegistry
operator|=
operator|new
name|NamedWriteableRegistry
argument_list|()
expr_stmt|;
name|namedWriteableRegistry
operator|.
name|registerPrototype
argument_list|(
name|ShapeBuilder
operator|.
name|class
argument_list|,
name|PointBuilder
operator|.
name|PROTOTYPE
argument_list|)
expr_stmt|;
name|namedWriteableRegistry
operator|.
name|registerPrototype
argument_list|(
name|ShapeBuilder
operator|.
name|class
argument_list|,
name|CircleBuilder
operator|.
name|PROTOTYPE
argument_list|)
expr_stmt|;
name|namedWriteableRegistry
operator|.
name|registerPrototype
argument_list|(
name|ShapeBuilder
operator|.
name|class
argument_list|,
name|EnvelopeBuilder
operator|.
name|PROTOTYPE
argument_list|)
expr_stmt|;
name|namedWriteableRegistry
operator|.
name|registerPrototype
argument_list|(
name|ShapeBuilder
operator|.
name|class
argument_list|,
name|MultiPointBuilder
operator|.
name|PROTOTYPE
argument_list|)
expr_stmt|;
name|namedWriteableRegistry
operator|.
name|registerPrototype
argument_list|(
name|ShapeBuilder
operator|.
name|class
argument_list|,
name|LineStringBuilder
operator|.
name|PROTOTYPE
argument_list|)
expr_stmt|;
name|namedWriteableRegistry
operator|.
name|registerPrototype
argument_list|(
name|ShapeBuilder
operator|.
name|class
argument_list|,
name|MultiLineStringBuilder
operator|.
name|PROTOTYPE
argument_list|)
expr_stmt|;
name|namedWriteableRegistry
operator|.
name|registerPrototype
argument_list|(
name|ShapeBuilder
operator|.
name|class
argument_list|,
name|PolygonBuilder
operator|.
name|PROTOTYPE
argument_list|)
expr_stmt|;
name|namedWriteableRegistry
operator|.
name|registerPrototype
argument_list|(
name|ShapeBuilder
operator|.
name|class
argument_list|,
name|MultiPolygonBuilder
operator|.
name|PROTOTYPE
argument_list|)
expr_stmt|;
name|namedWriteableRegistry
operator|.
name|registerPrototype
argument_list|(
name|ShapeBuilder
operator|.
name|class
argument_list|,
name|GeometryCollectionBuilder
operator|.
name|PROTOTYPE
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|namedWriteableRegistry
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * create random shape that is put under test      */
DECL|method|createTestShapeBuilder
specifier|protected
specifier|abstract
name|SB
name|createTestShapeBuilder
parameter_list|()
function_decl|;
comment|/**      * mutate the given shape so the returned shape is different      */
DECL|method|createMutation
specifier|protected
specifier|abstract
name|SB
name|createMutation
parameter_list|(
name|SB
name|original
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Test that creates new shape from a random test shape and checks both for equality      */
DECL|method|testFromXContent
specifier|public
name|void
name|testFromXContent
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|runs
init|=
literal|0
init|;
name|runs
operator|<
name|NUMBER_OF_TESTBUILDERS
condition|;
name|runs
operator|++
control|)
block|{
name|SB
name|testShape
init|=
name|createTestShapeBuilder
argument_list|()
decl_stmt|;
name|XContentBuilder
name|contentBuilder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|contentBuilder
operator|.
name|prettyPrint
argument_list|()
expr_stmt|;
block|}
name|XContentBuilder
name|builder
init|=
name|testShape
operator|.
name|toXContent
argument_list|(
name|contentBuilder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
decl_stmt|;
name|XContentParser
name|shapeParser
init|=
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|shapeParser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|ShapeBuilder
name|parsedShape
init|=
name|ShapeBuilder
operator|.
name|parse
argument_list|(
name|shapeParser
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|testShape
argument_list|,
name|parsedShape
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testShape
argument_list|,
name|parsedShape
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testShape
operator|.
name|hashCode
argument_list|()
argument_list|,
name|parsedShape
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test serialization and deserialization of the test shape.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|runs
init|=
literal|0
init|;
name|runs
operator|<
name|NUMBER_OF_TESTBUILDERS
condition|;
name|runs
operator|++
control|)
block|{
name|SB
name|testShape
init|=
name|createTestShapeBuilder
argument_list|()
decl_stmt|;
name|SB
name|deserializedShape
init|=
operator|(
name|SB
operator|)
name|copyShape
argument_list|(
name|testShape
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|testShape
argument_list|,
name|deserializedShape
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testShape
operator|.
name|hashCode
argument_list|()
argument_list|,
name|deserializedShape
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|testShape
argument_list|,
name|deserializedShape
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test equality and hashCode properties      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testEqualsAndHashcode
specifier|public
name|void
name|testEqualsAndHashcode
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|runs
init|=
literal|0
init|;
name|runs
operator|<
name|NUMBER_OF_TESTBUILDERS
condition|;
name|runs
operator|++
control|)
block|{
name|SB
name|firstShape
init|=
name|createTestShapeBuilder
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"shape is equal to null"
argument_list|,
name|firstShape
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"shape is equal to incompatible type"
argument_list|,
name|firstShape
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"shape is not equal to self"
argument_list|,
name|firstShape
operator|.
name|equals
argument_list|(
name|firstShape
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"same shape's hashcode returns different values if called multiple times"
argument_list|,
name|firstShape
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|firstShape
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"different shapes should not be equal"
argument_list|,
name|createMutation
argument_list|(
name|firstShape
argument_list|)
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|firstShape
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|SB
name|secondShape
init|=
operator|(
name|SB
operator|)
name|copyShape
argument_list|(
name|firstShape
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"shape is not equal to self"
argument_list|,
name|secondShape
operator|.
name|equals
argument_list|(
name|secondShape
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"shape is not equal to its copy"
argument_list|,
name|firstShape
operator|.
name|equals
argument_list|(
name|secondShape
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"equals is not symmetric"
argument_list|,
name|secondShape
operator|.
name|equals
argument_list|(
name|firstShape
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"shape copy's hashcode is different from original hashcode"
argument_list|,
name|secondShape
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|firstShape
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|SB
name|thirdShape
init|=
operator|(
name|SB
operator|)
name|copyShape
argument_list|(
name|secondShape
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"shape is not equal to self"
argument_list|,
name|thirdShape
operator|.
name|equals
argument_list|(
name|thirdShape
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"shape is not equal to its copy"
argument_list|,
name|secondShape
operator|.
name|equals
argument_list|(
name|thirdShape
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"shape copy's hashcode is different from original hashcode"
argument_list|,
name|secondShape
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|thirdShape
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"equals is not transitive"
argument_list|,
name|firstShape
operator|.
name|equals
argument_list|(
name|thirdShape
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"shape copy's hashcode is different from original hashcode"
argument_list|,
name|firstShape
operator|.
name|hashCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|thirdShape
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"equals is not symmetric"
argument_list|,
name|thirdShape
operator|.
name|equals
argument_list|(
name|secondShape
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"equals is not symmetric"
argument_list|,
name|thirdShape
operator|.
name|equals
argument_list|(
name|firstShape
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|copyShape
specifier|static
name|ShapeBuilder
name|copyShape
parameter_list|(
name|ShapeBuilder
name|original
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|BytesStreamOutput
name|output
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|original
operator|.
name|writeTo
argument_list|(
name|output
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
operator|new
name|NamedWriteableAwareStreamInput
argument_list|(
name|StreamInput
operator|.
name|wrap
argument_list|(
name|output
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|,
name|namedWriteableRegistry
argument_list|)
init|)
block|{
name|ShapeBuilder
name|prototype
init|=
operator|(
name|ShapeBuilder
operator|)
name|namedWriteableRegistry
operator|.
name|getPrototype
argument_list|(
name|ShapeBuilder
operator|.
name|class
argument_list|,
name|original
operator|.
name|getWriteableName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|prototype
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

