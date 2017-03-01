begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.percentiles.tdigest
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
name|percentiles
operator|.
name|tdigest
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
operator|.
name|Reader
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
name|DocValueFormat
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
name|InternalAggregationTestCase
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

begin_class
DECL|class|InternalTDigestPercentilesRanksTests
specifier|public
class|class
name|InternalTDigestPercentilesRanksTests
extends|extends
name|InternalAggregationTestCase
argument_list|<
name|InternalTDigestPercentileRanks
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createTestInstance
specifier|protected
name|InternalTDigestPercentileRanks
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
name|double
index|[]
name|cdfValues
init|=
operator|new
name|double
index|[]
block|{
literal|0.5
block|}
decl_stmt|;
name|TDigestState
name|state
init|=
operator|new
name|TDigestState
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|int
name|numValues
init|=
name|randomInt
argument_list|(
literal|100
argument_list|)
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
name|numValues
condition|;
operator|++
name|i
control|)
block|{
name|state
operator|.
name|add
argument_list|(
name|randomDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|boolean
name|keyed
init|=
literal|false
decl_stmt|;
name|DocValueFormat
name|format
init|=
name|DocValueFormat
operator|.
name|RAW
decl_stmt|;
return|return
operator|new
name|InternalTDigestPercentileRanks
argument_list|(
name|name
argument_list|,
name|cdfValues
argument_list|,
name|state
argument_list|,
name|keyed
argument_list|,
name|format
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|assertReduced
specifier|protected
name|void
name|assertReduced
parameter_list|(
name|InternalTDigestPercentileRanks
name|reduced
parameter_list|,
name|List
argument_list|<
name|InternalTDigestPercentileRanks
argument_list|>
name|inputs
parameter_list|)
block|{
comment|// it is hard to check the values due to the inaccuracy of the algorithm
comment|// the min/max values should be accurate due to the way the algo works so we can at least test those
name|double
name|min
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|max
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
name|long
name|totalCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|InternalTDigestPercentileRanks
name|ranks
range|:
name|inputs
control|)
block|{
if|if
condition|(
name|ranks
operator|.
name|state
operator|.
name|centroidCount
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// quantiles would return NaN
continue|continue;
block|}
name|totalCount
operator|+=
name|ranks
operator|.
name|state
operator|.
name|size
argument_list|()
expr_stmt|;
name|min
operator|=
name|Math
operator|.
name|min
argument_list|(
name|ranks
operator|.
name|state
operator|.
name|quantile
argument_list|(
literal|0
argument_list|)
argument_list|,
name|min
argument_list|)
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|ranks
operator|.
name|state
operator|.
name|quantile
argument_list|(
literal|1
argument_list|)
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|totalCount
argument_list|,
name|reduced
operator|.
name|state
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|totalCount
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|reduced
operator|.
name|state
operator|.
name|quantile
argument_list|(
literal|0
argument_list|)
argument_list|,
name|min
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|reduced
operator|.
name|state
operator|.
name|quantile
argument_list|(
literal|1
argument_list|)
argument_list|,
name|max
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|instanceReader
specifier|protected
name|Reader
argument_list|<
name|InternalTDigestPercentileRanks
argument_list|>
name|instanceReader
parameter_list|()
block|{
return|return
name|InternalTDigestPercentileRanks
operator|::
operator|new
return|;
block|}
block|}
end_class

end_unit

