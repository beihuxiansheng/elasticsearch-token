begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.completion
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
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
name|index
operator|.
name|query
operator|.
name|QueryParseContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
operator|.
name|context
operator|.
name|GeoQueryContext
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
name|Collections
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|GeoQueryContextTests
specifier|public
class|class
name|GeoQueryContextTests
extends|extends
name|QueryContextTestCase
argument_list|<
name|GeoQueryContext
argument_list|>
block|{
DECL|method|randomGeoQueryContext
specifier|public
specifier|static
name|GeoQueryContext
name|randomGeoQueryContext
parameter_list|()
block|{
specifier|final
name|GeoQueryContext
operator|.
name|Builder
name|builder
init|=
name|GeoQueryContext
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setGeoPoint
argument_list|(
operator|new
name|GeoPoint
argument_list|(
name|randomDouble
argument_list|()
argument_list|,
name|randomDouble
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|builder
operator|::
name|setBoost
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|maybeSet
argument_list|(
name|builder
operator|::
name|setPrecision
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|12
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|neighbours
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|12
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|neighbours
operator|.
name|add
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
name|maybeSet
argument_list|(
name|builder
operator|::
name|setNeighbours
argument_list|,
name|neighbours
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createTestModel
specifier|protected
name|GeoQueryContext
name|createTestModel
parameter_list|()
block|{
return|return
name|randomGeoQueryContext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fromXContent
specifier|protected
name|GeoQueryContext
name|fromXContent
parameter_list|(
name|QueryParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|GeoQueryContext
operator|.
name|fromXContent
argument_list|(
name|context
argument_list|)
return|;
block|}
DECL|method|testNullGeoPointIsIllegal
specifier|public
name|void
name|testNullGeoPointIsIllegal
parameter_list|()
block|{
specifier|final
name|GeoQueryContext
name|geoQueryContext
init|=
name|randomGeoQueryContext
argument_list|()
decl_stmt|;
specifier|final
name|GeoQueryContext
operator|.
name|Builder
name|builder
init|=
name|GeoQueryContext
operator|.
name|builder
argument_list|()
operator|.
name|setNeighbours
argument_list|(
name|geoQueryContext
operator|.
name|getNeighbours
argument_list|()
argument_list|)
operator|.
name|setPrecision
argument_list|(
name|geoQueryContext
operator|.
name|getPrecision
argument_list|()
argument_list|)
operator|.
name|setBoost
argument_list|(
name|geoQueryContext
operator|.
name|getBoost
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"null geo point is illegal"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
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
name|equalTo
argument_list|(
literal|"geoPoint must not be null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIllegalArguments
specifier|public
name|void
name|testIllegalArguments
parameter_list|()
block|{
specifier|final
name|GeoQueryContext
operator|.
name|Builder
name|builder
init|=
name|GeoQueryContext
operator|.
name|builder
argument_list|()
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|setGeoPoint
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"geoPoint must not be null"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"geoPoint must not be null"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|builder
operator|.
name|setBoost
argument_list|(
operator|-
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"boost must be positive"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"boost must be greater than 0"
argument_list|)
expr_stmt|;
block|}
name|int
name|precision
init|=
literal|0
decl_stmt|;
try|try
block|{
do|do
block|{
name|precision
operator|=
name|randomInt
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|precision
operator|>=
literal|1
operator|&&
name|precision
operator|<=
literal|12
condition|)
do|;
name|builder
operator|.
name|setPrecision
argument_list|(
name|precision
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"precision must be between 1 and 12"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|neighbours
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|neighbours
operator|.
name|add
argument_list|(
name|precision
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|11
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|neighbours
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|neighbours
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setNeighbours
argument_list|(
name|neighbours
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"neighbour value must be between 1 and 12"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

