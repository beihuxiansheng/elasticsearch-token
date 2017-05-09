begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.geobounds
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|metrics
operator|.
name|geobounds
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
name|Writeable
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
name|aggregations
operator|.
name|pipeline
operator|.
name|PipelineAggregator
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
name|InternalAggregationTestCase
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_class
DECL|class|InternalGeoBoundsTests
specifier|public
class|class
name|InternalGeoBoundsTests
extends|extends
name|InternalAggregationTestCase
argument_list|<
name|InternalGeoBounds
argument_list|>
block|{
DECL|field|GEOHASH_TOLERANCE
specifier|static
specifier|final
name|double
name|GEOHASH_TOLERANCE
init|=
literal|1E
operator|-
literal|5D
decl_stmt|;
annotation|@
name|Override
DECL|method|createTestInstance
specifier|protected
name|InternalGeoBounds
name|createTestInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
block|{
name|InternalGeoBounds
name|geo
init|=
operator|new
name|InternalGeoBounds
argument_list|(
name|name
argument_list|,
name|randomDouble
argument_list|()
argument_list|,
name|randomDouble
argument_list|()
argument_list|,
name|randomDouble
argument_list|()
argument_list|,
name|randomDouble
argument_list|()
argument_list|,
name|randomDouble
argument_list|()
argument_list|,
name|randomDouble
argument_list|()
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|pipelineAggregators
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|geo
return|;
block|}
annotation|@
name|Override
DECL|method|assertReduced
specifier|protected
name|void
name|assertReduced
parameter_list|(
name|InternalGeoBounds
name|reduced
parameter_list|,
name|List
argument_list|<
name|InternalGeoBounds
argument_list|>
name|inputs
parameter_list|)
block|{
name|double
name|top
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
name|double
name|bottom
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|posLeft
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|posRight
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
name|double
name|negLeft
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|negRight
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
for|for
control|(
name|InternalGeoBounds
name|bounds
range|:
name|inputs
control|)
block|{
if|if
condition|(
name|bounds
operator|.
name|top
operator|>
name|top
condition|)
block|{
name|top
operator|=
name|bounds
operator|.
name|top
expr_stmt|;
block|}
if|if
condition|(
name|bounds
operator|.
name|bottom
operator|<
name|bottom
condition|)
block|{
name|bottom
operator|=
name|bounds
operator|.
name|bottom
expr_stmt|;
block|}
if|if
condition|(
name|bounds
operator|.
name|posLeft
operator|<
name|posLeft
condition|)
block|{
name|posLeft
operator|=
name|bounds
operator|.
name|posLeft
expr_stmt|;
block|}
if|if
condition|(
name|bounds
operator|.
name|posRight
operator|>
name|posRight
condition|)
block|{
name|posRight
operator|=
name|bounds
operator|.
name|posRight
expr_stmt|;
block|}
if|if
condition|(
name|bounds
operator|.
name|negLeft
operator|<
name|negLeft
condition|)
block|{
name|negLeft
operator|=
name|bounds
operator|.
name|negLeft
expr_stmt|;
block|}
if|if
condition|(
name|bounds
operator|.
name|negRight
operator|>
name|negRight
condition|)
block|{
name|negRight
operator|=
name|bounds
operator|.
name|negRight
expr_stmt|;
block|}
block|}
name|assertThat
argument_list|(
name|reduced
operator|.
name|top
argument_list|,
name|closeTo
argument_list|(
name|top
argument_list|,
name|GEOHASH_TOLERANCE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reduced
operator|.
name|bottom
argument_list|,
name|closeTo
argument_list|(
name|bottom
argument_list|,
name|GEOHASH_TOLERANCE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reduced
operator|.
name|posLeft
argument_list|,
name|closeTo
argument_list|(
name|posLeft
argument_list|,
name|GEOHASH_TOLERANCE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reduced
operator|.
name|posRight
argument_list|,
name|closeTo
argument_list|(
name|posRight
argument_list|,
name|GEOHASH_TOLERANCE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reduced
operator|.
name|negLeft
argument_list|,
name|closeTo
argument_list|(
name|negLeft
argument_list|,
name|GEOHASH_TOLERANCE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reduced
operator|.
name|negRight
argument_list|,
name|closeTo
argument_list|(
name|negRight
argument_list|,
name|GEOHASH_TOLERANCE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|instanceReader
specifier|protected
name|Writeable
operator|.
name|Reader
argument_list|<
name|InternalGeoBounds
argument_list|>
name|instanceReader
parameter_list|()
block|{
return|return
name|InternalGeoBounds
operator|::
operator|new
return|;
block|}
block|}
end_class

end_unit

