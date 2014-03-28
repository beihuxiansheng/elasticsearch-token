begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.cardinality
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
name|cardinality
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|IntOpenHashSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|hash
operator|.
name|MurmurHash3
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
name|util
operator|.
name|BigArrays
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
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
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
name|cardinality
operator|.
name|HyperLogLogPlusPlus
operator|.
name|MAX_PRECISION
import|;
end_import

begin_import
import|import static
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
name|cardinality
operator|.
name|HyperLogLogPlusPlus
operator|.
name|MIN_PRECISION
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
DECL|class|HyperLogLogPlusPlusTests
specifier|public
class|class
name|HyperLogLogPlusPlusTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|encodeDecode
specifier|public
name|void
name|encodeDecode
parameter_list|()
block|{
specifier|final
name|int
name|iters
init|=
name|scaledRandomIntBetween
argument_list|(
literal|100000
argument_list|,
literal|500000
argument_list|)
decl_stmt|;
comment|// random hashes
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iters
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|p1
init|=
name|randomIntBetween
argument_list|(
literal|4
argument_list|,
literal|24
argument_list|)
decl_stmt|;
specifier|final
name|long
name|hash
init|=
name|randomLong
argument_list|()
decl_stmt|;
name|testEncodeDecode
argument_list|(
name|p1
argument_list|,
name|hash
argument_list|)
expr_stmt|;
block|}
comment|// special cases
for|for
control|(
name|int
name|p1
init|=
name|MIN_PRECISION
init|;
name|p1
operator|<=
name|MAX_PRECISION
condition|;
operator|++
name|p1
control|)
block|{
name|testEncodeDecode
argument_list|(
name|p1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
name|p1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|testEncodeDecode
argument_list|(
name|p1
argument_list|,
operator|~
literal|0L
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEncodeDecode
specifier|private
name|void
name|testEncodeDecode
parameter_list|(
name|int
name|p1
parameter_list|,
name|long
name|hash
parameter_list|)
block|{
specifier|final
name|long
name|index
init|=
name|HyperLogLogPlusPlus
operator|.
name|index
argument_list|(
name|hash
argument_list|,
name|p1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|runLen
init|=
name|HyperLogLogPlusPlus
operator|.
name|runLen
argument_list|(
name|hash
argument_list|,
name|p1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|encoded
init|=
name|HyperLogLogPlusPlus
operator|.
name|encodeHash
argument_list|(
name|hash
argument_list|,
name|p1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|index
argument_list|,
name|HyperLogLogPlusPlus
operator|.
name|decodeIndex
argument_list|(
name|encoded
argument_list|,
name|p1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|runLen
argument_list|,
name|HyperLogLogPlusPlus
operator|.
name|decodeRunLen
argument_list|(
name|encoded
argument_list|,
name|p1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|accuracy
specifier|public
name|void
name|accuracy
parameter_list|()
block|{
specifier|final
name|long
name|bucket
init|=
name|randomInt
argument_list|(
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numValues
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxValue
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|randomBoolean
argument_list|()
condition|?
literal|1000
else|:
literal|100000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|p
init|=
name|randomIntBetween
argument_list|(
literal|14
argument_list|,
name|MAX_PRECISION
argument_list|)
decl_stmt|;
name|IntOpenHashSet
name|set
init|=
operator|new
name|IntOpenHashSet
argument_list|()
decl_stmt|;
name|HyperLogLogPlusPlus
name|e
init|=
operator|new
name|HyperLogLogPlusPlus
argument_list|(
name|p
argument_list|,
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
argument_list|,
literal|1
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
specifier|final
name|int
name|n
init|=
name|randomInt
argument_list|(
name|maxValue
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
specifier|final
name|long
name|hash
init|=
name|MurmurHash3
operator|.
name|hash
argument_list|(
operator|(
name|long
operator|)
name|n
argument_list|)
decl_stmt|;
name|e
operator|.
name|collect
argument_list|(
name|bucket
argument_list|,
name|hash
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
comment|//System.out.println(e.cardinality(bucket) + "<> " + set.size());
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|e
operator|.
name|cardinality
argument_list|(
name|bucket
argument_list|)
argument_list|,
name|closeTo
argument_list|(
name|set
operator|.
name|size
argument_list|()
argument_list|,
literal|0.1
operator|*
name|set
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|e
operator|.
name|cardinality
argument_list|(
name|bucket
argument_list|)
argument_list|,
name|closeTo
argument_list|(
name|set
operator|.
name|size
argument_list|()
argument_list|,
literal|0.1
operator|*
name|set
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|()
block|{
specifier|final
name|int
name|p
init|=
name|randomIntBetween
argument_list|(
name|MIN_PRECISION
argument_list|,
name|MAX_PRECISION
argument_list|)
decl_stmt|;
specifier|final
name|HyperLogLogPlusPlus
name|single
init|=
operator|new
name|HyperLogLogPlusPlus
argument_list|(
name|p
argument_list|,
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|HyperLogLogPlusPlus
index|[]
name|multi
init|=
operator|new
name|HyperLogLogPlusPlus
index|[
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|100
argument_list|)
index|]
decl_stmt|;
specifier|final
name|long
index|[]
name|bucketOrds
init|=
operator|new
name|long
index|[
name|multi
operator|.
name|length
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
name|multi
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|bucketOrds
index|[
name|i
index|]
operator|=
name|randomInt
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|multi
index|[
name|i
index|]
operator|=
operator|new
name|HyperLogLogPlusPlus
argument_list|(
name|p
argument_list|,
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|numValues
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxValue
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|randomBoolean
argument_list|()
condition|?
literal|1000
else|:
literal|1000000
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
specifier|final
name|int
name|n
init|=
name|randomInt
argument_list|(
name|maxValue
argument_list|)
decl_stmt|;
specifier|final
name|long
name|hash
init|=
name|MurmurHash3
operator|.
name|hash
argument_list|(
operator|(
name|long
operator|)
name|n
argument_list|)
decl_stmt|;
name|single
operator|.
name|collect
argument_list|(
literal|0
argument_list|,
name|hash
argument_list|)
expr_stmt|;
comment|// use a gaussian so that all instances don't collect as many hashes
specifier|final
name|int
name|index
init|=
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|pow
argument_list|(
name|randomDouble
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|multi
index|[
name|index
index|]
operator|.
name|collect
argument_list|(
name|bucketOrds
index|[
name|index
index|]
argument_list|,
name|hash
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomInt
argument_list|(
literal|100
argument_list|)
operator|==
literal|0
condition|)
block|{
name|HyperLogLogPlusPlus
name|merged
init|=
operator|new
name|HyperLogLogPlusPlus
argument_list|(
name|p
argument_list|,
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|multi
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|merged
operator|.
name|merge
argument_list|(
literal|0
argument_list|,
name|multi
index|[
name|j
index|]
argument_list|,
name|bucketOrds
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|single
operator|.
name|cardinality
argument_list|(
literal|0
argument_list|)
argument_list|,
name|merged
operator|.
name|cardinality
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|fakeHashes
specifier|public
name|void
name|fakeHashes
parameter_list|()
block|{
comment|// hashes with lots of leading zeros trigger different paths in the code that we try to go through here
specifier|final
name|int
name|p
init|=
name|randomIntBetween
argument_list|(
name|MIN_PRECISION
argument_list|,
name|MAX_PRECISION
argument_list|)
decl_stmt|;
specifier|final
name|HyperLogLogPlusPlus
name|counts
init|=
operator|new
name|HyperLogLogPlusPlus
argument_list|(
name|p
argument_list|,
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|counts
operator|.
name|collect
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|counts
operator|.
name|cardinality
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|counts
operator|.
name|collect
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|counts
operator|.
name|cardinality
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|counts
operator|.
name|upgradeToHll
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// all hashes felt into the same bucket so hll would expect a count of 1
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|counts
operator|.
name|cardinality
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|precisionFromThreshold
specifier|public
name|void
name|precisionFromThreshold
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|HyperLogLogPlusPlus
operator|.
name|precisionFromThreshold
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|HyperLogLogPlusPlus
operator|.
name|precisionFromThreshold
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|HyperLogLogPlusPlus
operator|.
name|precisionFromThreshold
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|13
argument_list|,
name|HyperLogLogPlusPlus
operator|.
name|precisionFromThreshold
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|HyperLogLogPlusPlus
operator|.
name|precisionFromThreshold
argument_list|(
literal|10000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|18
argument_list|,
name|HyperLogLogPlusPlus
operator|.
name|precisionFromThreshold
argument_list|(
literal|100000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|18
argument_list|,
name|HyperLogLogPlusPlus
operator|.
name|precisionFromThreshold
argument_list|(
literal|1000000
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

