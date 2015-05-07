begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.reducers.moving.avg
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|reducers
operator|.
name|moving
operator|.
name|avg
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|EvictingQueue
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
name|reducers
operator|.
name|movavg
operator|.
name|models
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
name|test
operator|.
name|ElasticsearchTestCase
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
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|MovAvgUnitTests
specifier|public
class|class
name|MovAvgUnitTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testSimpleMovAvgModel
specifier|public
name|void
name|testSimpleMovAvgModel
parameter_list|()
block|{
name|MovAvgModel
name|model
init|=
operator|new
name|SimpleModel
argument_list|()
decl_stmt|;
name|int
name|numValues
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|int
name|windowSize
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|EvictingQueue
argument_list|<
name|Double
argument_list|>
name|window
init|=
name|EvictingQueue
operator|.
name|create
argument_list|(
name|windowSize
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
name|i
operator|++
control|)
block|{
name|double
name|randValue
init|=
name|randomDouble
argument_list|()
decl_stmt|;
name|double
name|expected
init|=
literal|0
decl_stmt|;
name|window
operator|.
name|offer
argument_list|(
name|randValue
argument_list|)
expr_stmt|;
for|for
control|(
name|double
name|value
range|:
name|window
control|)
block|{
name|expected
operator|+=
name|value
expr_stmt|;
block|}
name|expected
operator|/=
name|window
operator|.
name|size
argument_list|()
expr_stmt|;
name|double
name|actual
init|=
name|model
operator|.
name|next
argument_list|(
name|window
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|Double
operator|.
name|compare
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSimplePredictionModel
specifier|public
name|void
name|testSimplePredictionModel
parameter_list|()
block|{
name|MovAvgModel
name|model
init|=
operator|new
name|SimpleModel
argument_list|()
decl_stmt|;
name|int
name|windowSize
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|int
name|numPredictions
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|EvictingQueue
argument_list|<
name|Double
argument_list|>
name|window
init|=
name|EvictingQueue
operator|.
name|create
argument_list|(
name|windowSize
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
name|windowSize
condition|;
name|i
operator|++
control|)
block|{
name|window
operator|.
name|offer
argument_list|(
name|randomDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|double
name|actual
index|[]
init|=
name|model
operator|.
name|predict
argument_list|(
name|window
argument_list|,
name|numPredictions
argument_list|)
decl_stmt|;
name|double
name|expected
index|[]
init|=
operator|new
name|double
index|[
name|numPredictions
index|]
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
name|numPredictions
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|double
name|value
range|:
name|window
control|)
block|{
name|expected
index|[
name|i
index|]
operator|+=
name|value
expr_stmt|;
block|}
name|expected
index|[
name|i
index|]
operator|/=
name|window
operator|.
name|size
argument_list|()
expr_stmt|;
name|window
operator|.
name|offer
argument_list|(
name|expected
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numPredictions
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|Double
operator|.
name|compare
argument_list|(
name|expected
index|[
name|i
index|]
argument_list|,
name|actual
index|[
name|i
index|]
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testLinearMovAvgModel
specifier|public
name|void
name|testLinearMovAvgModel
parameter_list|()
block|{
name|MovAvgModel
name|model
init|=
operator|new
name|LinearModel
argument_list|()
decl_stmt|;
name|int
name|numValues
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|int
name|windowSize
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|EvictingQueue
argument_list|<
name|Double
argument_list|>
name|window
init|=
name|EvictingQueue
operator|.
name|create
argument_list|(
name|windowSize
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
name|i
operator|++
control|)
block|{
name|double
name|randValue
init|=
name|randomDouble
argument_list|()
decl_stmt|;
name|window
operator|.
name|offer
argument_list|(
name|randValue
argument_list|)
expr_stmt|;
name|double
name|avg
init|=
literal|0
decl_stmt|;
name|long
name|totalWeight
init|=
literal|1
decl_stmt|;
name|long
name|current
init|=
literal|1
decl_stmt|;
for|for
control|(
name|double
name|value
range|:
name|window
control|)
block|{
name|avg
operator|+=
name|value
operator|*
name|current
expr_stmt|;
name|totalWeight
operator|+=
name|current
expr_stmt|;
name|current
operator|+=
literal|1
expr_stmt|;
block|}
name|double
name|expected
init|=
name|avg
operator|/
name|totalWeight
decl_stmt|;
name|double
name|actual
init|=
name|model
operator|.
name|next
argument_list|(
name|window
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|Double
operator|.
name|compare
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testLinearPredictionModel
specifier|public
name|void
name|testLinearPredictionModel
parameter_list|()
block|{
name|MovAvgModel
name|model
init|=
operator|new
name|LinearModel
argument_list|()
decl_stmt|;
name|int
name|windowSize
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|int
name|numPredictions
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|EvictingQueue
argument_list|<
name|Double
argument_list|>
name|window
init|=
name|EvictingQueue
operator|.
name|create
argument_list|(
name|windowSize
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
name|windowSize
condition|;
name|i
operator|++
control|)
block|{
name|window
operator|.
name|offer
argument_list|(
name|randomDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|double
name|actual
index|[]
init|=
name|model
operator|.
name|predict
argument_list|(
name|window
argument_list|,
name|numPredictions
argument_list|)
decl_stmt|;
name|double
name|expected
index|[]
init|=
operator|new
name|double
index|[
name|numPredictions
index|]
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
name|numPredictions
condition|;
name|i
operator|++
control|)
block|{
name|double
name|avg
init|=
literal|0
decl_stmt|;
name|long
name|totalWeight
init|=
literal|1
decl_stmt|;
name|long
name|current
init|=
literal|1
decl_stmt|;
for|for
control|(
name|double
name|value
range|:
name|window
control|)
block|{
name|avg
operator|+=
name|value
operator|*
name|current
expr_stmt|;
name|totalWeight
operator|+=
name|current
expr_stmt|;
name|current
operator|+=
literal|1
expr_stmt|;
block|}
name|expected
index|[
name|i
index|]
operator|=
name|avg
operator|/
name|totalWeight
expr_stmt|;
name|window
operator|.
name|offer
argument_list|(
name|expected
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numPredictions
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|Double
operator|.
name|compare
argument_list|(
name|expected
index|[
name|i
index|]
argument_list|,
name|actual
index|[
name|i
index|]
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testEWMAMovAvgModel
specifier|public
name|void
name|testEWMAMovAvgModel
parameter_list|()
block|{
name|double
name|alpha
init|=
name|randomDouble
argument_list|()
decl_stmt|;
name|MovAvgModel
name|model
init|=
operator|new
name|EwmaModel
argument_list|(
name|alpha
argument_list|)
decl_stmt|;
name|int
name|numValues
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|int
name|windowSize
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|EvictingQueue
argument_list|<
name|Double
argument_list|>
name|window
init|=
name|EvictingQueue
operator|.
name|create
argument_list|(
name|windowSize
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
name|i
operator|++
control|)
block|{
name|double
name|randValue
init|=
name|randomDouble
argument_list|()
decl_stmt|;
name|window
operator|.
name|offer
argument_list|(
name|randValue
argument_list|)
expr_stmt|;
name|double
name|avg
init|=
literal|0
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|double
name|value
range|:
name|window
control|)
block|{
if|if
condition|(
name|first
condition|)
block|{
name|avg
operator|=
name|value
expr_stmt|;
name|first
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|avg
operator|=
operator|(
name|value
operator|*
name|alpha
operator|)
operator|+
operator|(
name|avg
operator|*
operator|(
literal|1
operator|-
name|alpha
operator|)
operator|)
expr_stmt|;
block|}
block|}
name|double
name|expected
init|=
name|avg
decl_stmt|;
name|double
name|actual
init|=
name|model
operator|.
name|next
argument_list|(
name|window
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|Double
operator|.
name|compare
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testEWMAPredictionModel
specifier|public
name|void
name|testEWMAPredictionModel
parameter_list|()
block|{
name|double
name|alpha
init|=
name|randomDouble
argument_list|()
decl_stmt|;
name|MovAvgModel
name|model
init|=
operator|new
name|EwmaModel
argument_list|(
name|alpha
argument_list|)
decl_stmt|;
name|int
name|windowSize
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|int
name|numPredictions
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|EvictingQueue
argument_list|<
name|Double
argument_list|>
name|window
init|=
name|EvictingQueue
operator|.
name|create
argument_list|(
name|windowSize
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
name|windowSize
condition|;
name|i
operator|++
control|)
block|{
name|window
operator|.
name|offer
argument_list|(
name|randomDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|double
name|actual
index|[]
init|=
name|model
operator|.
name|predict
argument_list|(
name|window
argument_list|,
name|numPredictions
argument_list|)
decl_stmt|;
name|double
name|expected
index|[]
init|=
operator|new
name|double
index|[
name|numPredictions
index|]
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
name|numPredictions
condition|;
name|i
operator|++
control|)
block|{
name|double
name|avg
init|=
literal|0
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|double
name|value
range|:
name|window
control|)
block|{
if|if
condition|(
name|first
condition|)
block|{
name|avg
operator|=
name|value
expr_stmt|;
name|first
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|avg
operator|=
operator|(
name|value
operator|*
name|alpha
operator|)
operator|+
operator|(
name|avg
operator|*
operator|(
literal|1
operator|-
name|alpha
operator|)
operator|)
expr_stmt|;
block|}
block|}
name|expected
index|[
name|i
index|]
operator|=
name|avg
expr_stmt|;
name|window
operator|.
name|offer
argument_list|(
name|expected
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numPredictions
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|Double
operator|.
name|compare
argument_list|(
name|expected
index|[
name|i
index|]
argument_list|,
name|actual
index|[
name|i
index|]
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testHoltLinearMovAvgModel
specifier|public
name|void
name|testHoltLinearMovAvgModel
parameter_list|()
block|{
name|double
name|alpha
init|=
name|randomDouble
argument_list|()
decl_stmt|;
name|double
name|beta
init|=
name|randomDouble
argument_list|()
decl_stmt|;
name|MovAvgModel
name|model
init|=
operator|new
name|HoltLinearModel
argument_list|(
name|alpha
argument_list|,
name|beta
argument_list|)
decl_stmt|;
name|int
name|numValues
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|int
name|windowSize
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|EvictingQueue
argument_list|<
name|Double
argument_list|>
name|window
init|=
name|EvictingQueue
operator|.
name|create
argument_list|(
name|windowSize
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
name|i
operator|++
control|)
block|{
name|double
name|randValue
init|=
name|randomDouble
argument_list|()
decl_stmt|;
name|window
operator|.
name|offer
argument_list|(
name|randValue
argument_list|)
expr_stmt|;
name|double
name|s
init|=
literal|0
decl_stmt|;
name|double
name|last_s
init|=
literal|0
decl_stmt|;
comment|// Trend value
name|double
name|b
init|=
literal|0
decl_stmt|;
name|double
name|last_b
init|=
literal|0
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
name|double
name|last
decl_stmt|;
for|for
control|(
name|double
name|value
range|:
name|window
control|)
block|{
name|last
operator|=
name|value
expr_stmt|;
if|if
condition|(
name|counter
operator|==
literal|1
condition|)
block|{
name|s
operator|=
name|value
expr_stmt|;
name|b
operator|=
name|value
operator|-
name|last
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
name|alpha
operator|*
name|value
operator|+
operator|(
literal|1.0d
operator|-
name|alpha
operator|)
operator|*
operator|(
name|last_s
operator|+
name|last_b
operator|)
expr_stmt|;
name|b
operator|=
name|beta
operator|*
operator|(
name|s
operator|-
name|last_s
operator|)
operator|+
operator|(
literal|1
operator|-
name|beta
operator|)
operator|*
name|last_b
expr_stmt|;
block|}
name|counter
operator|+=
literal|1
expr_stmt|;
name|last_s
operator|=
name|s
expr_stmt|;
name|last_b
operator|=
name|b
expr_stmt|;
block|}
name|double
name|expected
init|=
name|s
operator|+
operator|(
literal|0
operator|*
name|b
operator|)
decl_stmt|;
name|double
name|actual
init|=
name|model
operator|.
name|next
argument_list|(
name|window
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|Double
operator|.
name|compare
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testHoltLinearPredictionModel
specifier|public
name|void
name|testHoltLinearPredictionModel
parameter_list|()
block|{
name|double
name|alpha
init|=
name|randomDouble
argument_list|()
decl_stmt|;
name|double
name|beta
init|=
name|randomDouble
argument_list|()
decl_stmt|;
name|MovAvgModel
name|model
init|=
operator|new
name|HoltLinearModel
argument_list|(
name|alpha
argument_list|,
name|beta
argument_list|)
decl_stmt|;
name|int
name|windowSize
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|int
name|numPredictions
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|EvictingQueue
argument_list|<
name|Double
argument_list|>
name|window
init|=
name|EvictingQueue
operator|.
name|create
argument_list|(
name|windowSize
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
name|windowSize
condition|;
name|i
operator|++
control|)
block|{
name|window
operator|.
name|offer
argument_list|(
name|randomDouble
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|double
name|actual
index|[]
init|=
name|model
operator|.
name|predict
argument_list|(
name|window
argument_list|,
name|numPredictions
argument_list|)
decl_stmt|;
name|double
name|expected
index|[]
init|=
operator|new
name|double
index|[
name|numPredictions
index|]
decl_stmt|;
name|double
name|s
init|=
literal|0
decl_stmt|;
name|double
name|last_s
init|=
literal|0
decl_stmt|;
comment|// Trend value
name|double
name|b
init|=
literal|0
decl_stmt|;
name|double
name|last_b
init|=
literal|0
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
name|double
name|last
decl_stmt|;
for|for
control|(
name|double
name|value
range|:
name|window
control|)
block|{
name|last
operator|=
name|value
expr_stmt|;
if|if
condition|(
name|counter
operator|==
literal|1
condition|)
block|{
name|s
operator|=
name|value
expr_stmt|;
name|b
operator|=
name|value
operator|-
name|last
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
name|alpha
operator|*
name|value
operator|+
operator|(
literal|1.0d
operator|-
name|alpha
operator|)
operator|*
operator|(
name|last_s
operator|+
name|last_b
operator|)
expr_stmt|;
name|b
operator|=
name|beta
operator|*
operator|(
name|s
operator|-
name|last_s
operator|)
operator|+
operator|(
literal|1
operator|-
name|beta
operator|)
operator|*
name|last_b
expr_stmt|;
block|}
name|counter
operator|+=
literal|1
expr_stmt|;
name|last_s
operator|=
name|s
expr_stmt|;
name|last_b
operator|=
name|b
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numPredictions
condition|;
name|i
operator|++
control|)
block|{
name|expected
index|[
name|i
index|]
operator|=
name|s
operator|+
operator|(
name|i
operator|*
name|b
operator|)
expr_stmt|;
name|assertThat
argument_list|(
name|Double
operator|.
name|compare
argument_list|(
name|expected
index|[
name|i
index|]
argument_list|,
name|actual
index|[
name|i
index|]
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

