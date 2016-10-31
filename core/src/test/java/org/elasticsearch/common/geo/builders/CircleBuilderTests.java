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
name|common
operator|.
name|unit
operator|.
name|DistanceUnit
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

begin_class
DECL|class|CircleBuilderTests
specifier|public
class|class
name|CircleBuilderTests
extends|extends
name|AbstractShapeBuilderTestCase
argument_list|<
name|CircleBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createTestShapeBuilder
specifier|protected
name|CircleBuilder
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
name|CircleBuilder
name|createMutation
parameter_list|(
name|CircleBuilder
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
name|CircleBuilder
name|mutate
parameter_list|(
name|CircleBuilder
name|original
parameter_list|)
throws|throws
name|IOException
block|{
name|CircleBuilder
name|mutation
init|=
name|copyShape
argument_list|(
name|original
argument_list|)
decl_stmt|;
name|double
name|radius
init|=
name|original
operator|.
name|radius
argument_list|()
decl_stmt|;
name|DistanceUnit
name|unit
init|=
name|original
operator|.
name|unit
argument_list|()
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|original
operator|.
name|center
argument_list|()
operator|.
name|x
operator|>
literal|0.0
operator|||
name|original
operator|.
name|center
argument_list|()
operator|.
name|y
operator|>
literal|0.0
condition|)
block|{
name|mutation
operator|.
name|center
argument_list|(
operator|new
name|Coordinate
argument_list|(
name|original
operator|.
name|center
argument_list|()
operator|.
name|x
operator|/
literal|2
argument_list|,
name|original
operator|.
name|center
argument_list|()
operator|.
name|y
operator|/
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// original center was 0.0, 0.0
name|mutation
operator|.
name|center
argument_list|(
name|randomDouble
argument_list|()
operator|+
literal|0.1
argument_list|,
name|randomDouble
argument_list|()
operator|+
literal|0.1
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|radius
operator|>
literal|0
condition|)
block|{
name|radius
operator|=
name|radius
operator|/
literal|2
expr_stmt|;
block|}
else|else
block|{
name|radius
operator|=
name|randomDouble
argument_list|()
operator|+
literal|0.1
expr_stmt|;
block|}
block|}
else|else
block|{
name|DistanceUnit
name|newRandom
init|=
name|unit
decl_stmt|;
while|while
condition|(
name|newRandom
operator|==
name|unit
condition|)
block|{
name|newRandom
operator|=
name|randomFrom
argument_list|(
name|DistanceUnit
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
name|unit
operator|=
name|newRandom
expr_stmt|;
block|}
return|return
name|mutation
operator|.
name|radius
argument_list|(
name|radius
argument_list|,
name|unit
argument_list|)
return|;
block|}
DECL|method|createRandomShape
specifier|static
name|CircleBuilder
name|createRandomShape
parameter_list|()
block|{
name|CircleBuilder
name|circle
init|=
operator|new
name|CircleBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|frequently
argument_list|()
condition|)
block|{
name|double
name|centerX
init|=
name|randomDoubleBetween
argument_list|(
operator|-
literal|180
argument_list|,
literal|180
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|double
name|centerY
init|=
name|randomDoubleBetween
argument_list|(
operator|-
literal|90
argument_list|,
literal|90
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|circle
operator|.
name|center
argument_list|(
name|centerX
argument_list|,
name|centerY
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|circle
operator|.
name|radius
argument_list|(
name|randomDoubleBetween
argument_list|(
literal|0.1
argument_list|,
literal|10.0
argument_list|,
literal|false
argument_list|)
argument_list|,
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
return|return
name|circle
return|;
block|}
block|}
end_class

end_unit

