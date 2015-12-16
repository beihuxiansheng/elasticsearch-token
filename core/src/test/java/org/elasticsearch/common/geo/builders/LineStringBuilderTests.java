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

begin_class
DECL|class|LineStringBuilderTests
specifier|public
class|class
name|LineStringBuilderTests
extends|extends
name|AbstractShapeBuilderTestCase
argument_list|<
name|LineStringBuilder
argument_list|>
block|{
DECL|method|testInvalidConstructorArgs
specifier|public
name|void
name|testInvalidConstructorArgs
parameter_list|()
block|{
try|try
block|{
operator|new
name|LineStringBuilder
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception expected"
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
literal|"cannot create point collection with empty set of points"
argument_list|,
name|equalTo
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|LineStringBuilder
argument_list|(
operator|new
name|PointListBuilder
argument_list|()
operator|.
name|list
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception expected"
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
literal|"cannot create point collection with empty set of points"
argument_list|,
name|equalTo
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|LineStringBuilder
argument_list|(
operator|new
name|PointListBuilder
argument_list|()
operator|.
name|point
argument_list|(
literal|0.0
argument_list|,
literal|0.0
argument_list|)
operator|.
name|list
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception expected"
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
literal|"invalid number of points in LineString (found [1] - must be>= 2)"
argument_list|,
name|equalTo
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createTestShapeBuilder
specifier|protected
name|LineStringBuilder
name|createTestShapeBuilder
parameter_list|()
block|{
return|return
name|createRandomShape
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createMutation
specifier|protected
name|LineStringBuilder
name|createMutation
parameter_list|(
name|LineStringBuilder
name|original
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|mutate
argument_list|(
name|original
argument_list|)
return|;
block|}
DECL|method|mutate
specifier|static
name|LineStringBuilder
name|mutate
parameter_list|(
name|LineStringBuilder
name|original
parameter_list|)
throws|throws
name|IOException
block|{
name|LineStringBuilder
name|mutation
init|=
operator|(
name|LineStringBuilder
operator|)
name|copyShape
argument_list|(
name|original
argument_list|)
decl_stmt|;
name|Coordinate
index|[]
name|coordinates
init|=
name|original
operator|.
name|coordinates
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Coordinate
name|coordinate
init|=
name|randomFrom
argument_list|(
name|coordinates
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|coordinate
operator|.
name|x
operator|!=
literal|0.0
condition|)
block|{
name|coordinate
operator|.
name|x
operator|=
name|coordinate
operator|.
name|x
operator|/
literal|2
expr_stmt|;
block|}
else|else
block|{
name|coordinate
operator|.
name|x
operator|=
name|randomDoubleBetween
argument_list|(
operator|-
literal|180.0
argument_list|,
literal|180.0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|coordinate
operator|.
name|y
operator|!=
literal|0.0
condition|)
block|{
name|coordinate
operator|.
name|y
operator|=
name|coordinate
operator|.
name|y
operator|/
literal|2
expr_stmt|;
block|}
else|else
block|{
name|coordinate
operator|.
name|y
operator|=
name|randomDoubleBetween
argument_list|(
operator|-
literal|90.0
argument_list|,
literal|90.0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|mutation
operator|.
name|points
argument_list|(
name|coordinates
argument_list|)
return|;
block|}
DECL|method|createRandomShape
specifier|static
name|LineStringBuilder
name|createRandomShape
parameter_list|()
block|{
name|LineStringBuilder
name|lsb
init|=
operator|(
name|LineStringBuilder
operator|)
name|RandomShapeGenerator
operator|.
name|createShape
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|ShapeType
operator|.
name|LINESTRING
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|lsb
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|lsb
return|;
block|}
block|}
end_class

end_unit

