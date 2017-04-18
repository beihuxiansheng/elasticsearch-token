begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.range
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|range
package|;
end_package

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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|util
operator|.
name|BytesRef
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
name|util
operator|.
name|TestUtil
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
name|fielddata
operator|.
name|AbstractSortedSetDocValues
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
name|fielddata
operator|.
name|SortedBinaryDocValues
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
name|LeafBucketCollector
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
name|bucket
operator|.
name|range
operator|.
name|BinaryRangeAggregator
operator|.
name|SortedBinaryRangeLeafCollector
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
name|bucket
operator|.
name|range
operator|.
name|BinaryRangeAggregator
operator|.
name|SortedSetRangeLeafCollector
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
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|LongHashSet
import|;
end_import

begin_class
DECL|class|BinaryRangeAggregatorTests
specifier|public
class|class
name|BinaryRangeAggregatorTests
extends|extends
name|ESTestCase
block|{
DECL|class|FakeSortedSetDocValues
specifier|private
specifier|static
class|class
name|FakeSortedSetDocValues
extends|extends
name|AbstractSortedSetDocValues
block|{
DECL|field|terms
specifier|private
specifier|final
name|BytesRef
index|[]
name|terms
decl_stmt|;
DECL|field|ords
name|long
index|[]
name|ords
decl_stmt|;
DECL|field|i
specifier|private
name|int
name|i
decl_stmt|;
DECL|method|FakeSortedSetDocValues
name|FakeSortedSetDocValues
parameter_list|(
name|BytesRef
index|[]
name|terms
parameter_list|)
block|{
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|advanceExact
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|i
operator|=
literal|0
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|nextOrd
specifier|public
name|long
name|nextOrd
parameter_list|()
block|{
if|if
condition|(
name|i
operator|==
name|ords
operator|.
name|length
condition|)
block|{
return|return
name|NO_MORE_ORDS
return|;
block|}
return|return
name|ords
index|[
name|i
operator|++
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|BytesRef
name|lookupOrd
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
return|return
name|terms
index|[
operator|(
name|int
operator|)
name|ord
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|long
name|getValueCount
parameter_list|()
block|{
return|return
name|terms
operator|.
name|length
return|;
block|}
block|}
DECL|method|doTestSortedSetRangeLeafCollector
specifier|private
name|void
name|doTestSortedSetRangeLeafCollector
parameter_list|(
name|int
name|maxNumValuesPerDoc
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|termSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numTerms
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|maxNumValuesPerDoc
argument_list|,
literal|100
argument_list|)
decl_stmt|;
while|while
condition|(
name|termSet
operator|.
name|size
argument_list|()
operator|<
name|numTerms
condition|)
block|{
name|termSet
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
name|randomInt
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BytesRef
index|[]
name|terms
init|=
name|termSet
operator|.
name|toArray
argument_list|(
operator|new
name|BytesRef
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|terms
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numRanges
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|BinaryRangeAggregator
operator|.
name|Range
index|[]
name|ranges
init|=
operator|new
name|BinaryRangeAggregator
operator|.
name|Range
index|[
name|numRanges
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
name|numRanges
condition|;
operator|++
name|i
control|)
block|{
name|ranges
index|[
name|i
index|]
operator|=
operator|new
name|BinaryRangeAggregator
operator|.
name|Range
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
operator|new
name|BytesRef
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
name|randomInt
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
operator|new
name|BytesRef
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
name|randomInt
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|ranges
argument_list|,
name|BinaryRangeAggregator
operator|.
name|RANGE_COMPARATOR
argument_list|)
expr_stmt|;
name|FakeSortedSetDocValues
name|values
init|=
operator|new
name|FakeSortedSetDocValues
argument_list|(
name|terms
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|counts
init|=
operator|new
name|int
index|[
name|ranges
operator|.
name|length
index|]
decl_stmt|;
name|SortedSetRangeLeafCollector
name|collector
init|=
operator|new
name|SortedSetRangeLeafCollector
argument_list|(
name|values
argument_list|,
name|ranges
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|doCollect
parameter_list|(
name|LeafBucketCollector
name|sub
parameter_list|,
name|int
name|doc
parameter_list|,
name|long
name|bucket
parameter_list|)
throws|throws
name|IOException
block|{
name|counts
index|[
operator|(
name|int
operator|)
name|bucket
index|]
operator|++
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|final
name|int
index|[]
name|expectedCounts
init|=
operator|new
name|int
index|[
name|ranges
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|randomIntBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
literal|0
init|;
name|doc
operator|<
name|maxDoc
condition|;
operator|++
name|doc
control|)
block|{
name|LongHashSet
name|ordinalSet
init|=
operator|new
name|LongHashSet
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numValues
init|=
name|randomInt
argument_list|(
name|maxNumValuesPerDoc
argument_list|)
decl_stmt|;
while|while
condition|(
name|ordinalSet
operator|.
name|size
argument_list|()
operator|<
name|numValues
condition|)
block|{
name|ordinalSet
operator|.
name|add
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|terms
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
index|[]
name|ords
init|=
name|ordinalSet
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|ords
argument_list|)
expr_stmt|;
name|values
operator|.
name|ords
operator|=
name|ords
expr_stmt|;
comment|// simulate aggregation
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// now do it the naive way
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ranges
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
for|for
control|(
name|long
name|ord
range|:
name|ords
control|)
block|{
name|BytesRef
name|term
init|=
name|terms
index|[
operator|(
name|int
operator|)
name|ord
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|ranges
index|[
name|i
index|]
operator|.
name|from
operator|==
literal|null
operator|||
name|ranges
index|[
name|i
index|]
operator|.
name|from
operator|.
name|compareTo
argument_list|(
name|term
argument_list|)
operator|<=
literal|0
operator|)
operator|&&
operator|(
name|ranges
index|[
name|i
index|]
operator|.
name|to
operator|==
literal|null
operator|||
name|ranges
index|[
name|i
index|]
operator|.
name|to
operator|.
name|compareTo
argument_list|(
name|term
argument_list|)
operator|>
literal|0
operator|)
condition|)
block|{
name|expectedCounts
index|[
name|i
index|]
operator|++
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
name|assertArrayEquals
argument_list|(
name|expectedCounts
argument_list|,
name|counts
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortedSetRangeLeafCollectorSingleValued
specifier|public
name|void
name|testSortedSetRangeLeafCollectorSingleValued
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|iters
init|=
name|randomInt
argument_list|(
literal|10
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
name|iters
condition|;
operator|++
name|i
control|)
block|{
name|doTestSortedSetRangeLeafCollector
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSortedSetRangeLeafCollectorMultiValued
specifier|public
name|void
name|testSortedSetRangeLeafCollectorMultiValued
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|iters
init|=
name|randomInt
argument_list|(
literal|10
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
name|iters
condition|;
operator|++
name|i
control|)
block|{
name|doTestSortedSetRangeLeafCollector
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FakeSortedBinaryDocValues
specifier|private
specifier|static
class|class
name|FakeSortedBinaryDocValues
extends|extends
name|SortedBinaryDocValues
block|{
DECL|field|terms
specifier|private
specifier|final
name|BytesRef
index|[]
name|terms
decl_stmt|;
DECL|field|i
name|int
name|i
decl_stmt|;
DECL|field|ords
name|long
index|[]
name|ords
decl_stmt|;
DECL|method|FakeSortedBinaryDocValues
name|FakeSortedBinaryDocValues
parameter_list|(
name|BytesRef
index|[]
name|terms
parameter_list|)
block|{
name|this
operator|.
name|terms
operator|=
name|terms
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|advanceExact
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|i
operator|=
literal|0
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|docValueCount
specifier|public
name|int
name|docValueCount
parameter_list|()
block|{
return|return
name|ords
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|nextValue
specifier|public
name|BytesRef
name|nextValue
parameter_list|()
block|{
return|return
name|terms
index|[
operator|(
name|int
operator|)
name|ords
index|[
name|i
operator|++
index|]
index|]
return|;
block|}
block|}
DECL|method|doTestSortedBinaryRangeLeafCollector
specifier|private
name|void
name|doTestSortedBinaryRangeLeafCollector
parameter_list|(
name|int
name|maxNumValuesPerDoc
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|termSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numTerms
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|maxNumValuesPerDoc
argument_list|,
literal|100
argument_list|)
decl_stmt|;
while|while
condition|(
name|termSet
operator|.
name|size
argument_list|()
operator|<
name|numTerms
condition|)
block|{
name|termSet
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
name|randomInt
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BytesRef
index|[]
name|terms
init|=
name|termSet
operator|.
name|toArray
argument_list|(
operator|new
name|BytesRef
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|terms
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numRanges
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|BinaryRangeAggregator
operator|.
name|Range
index|[]
name|ranges
init|=
operator|new
name|BinaryRangeAggregator
operator|.
name|Range
index|[
name|numRanges
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
name|numRanges
condition|;
operator|++
name|i
control|)
block|{
name|ranges
index|[
name|i
index|]
operator|=
operator|new
name|BinaryRangeAggregator
operator|.
name|Range
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
operator|new
name|BytesRef
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
name|randomInt
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
operator|new
name|BytesRef
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
name|randomInt
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|ranges
argument_list|,
name|BinaryRangeAggregator
operator|.
name|RANGE_COMPARATOR
argument_list|)
expr_stmt|;
name|FakeSortedBinaryDocValues
name|values
init|=
operator|new
name|FakeSortedBinaryDocValues
argument_list|(
name|terms
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|counts
init|=
operator|new
name|int
index|[
name|ranges
operator|.
name|length
index|]
decl_stmt|;
name|SortedBinaryRangeLeafCollector
name|collector
init|=
operator|new
name|SortedBinaryRangeLeafCollector
argument_list|(
name|values
argument_list|,
name|ranges
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|doCollect
parameter_list|(
name|LeafBucketCollector
name|sub
parameter_list|,
name|int
name|doc
parameter_list|,
name|long
name|bucket
parameter_list|)
throws|throws
name|IOException
block|{
name|counts
index|[
operator|(
name|int
operator|)
name|bucket
index|]
operator|++
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|final
name|int
index|[]
name|expectedCounts
init|=
operator|new
name|int
index|[
name|ranges
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|randomIntBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
literal|0
init|;
name|doc
operator|<
name|maxDoc
condition|;
operator|++
name|doc
control|)
block|{
name|LongHashSet
name|ordinalSet
init|=
operator|new
name|LongHashSet
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numValues
init|=
name|randomInt
argument_list|(
name|maxNumValuesPerDoc
argument_list|)
decl_stmt|;
while|while
condition|(
name|ordinalSet
operator|.
name|size
argument_list|()
operator|<
name|numValues
condition|)
block|{
name|ordinalSet
operator|.
name|add
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|terms
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
index|[]
name|ords
init|=
name|ordinalSet
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|ords
argument_list|)
expr_stmt|;
name|values
operator|.
name|ords
operator|=
name|ords
expr_stmt|;
comment|// simulate aggregation
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// now do it the naive way
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ranges
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
for|for
control|(
name|long
name|ord
range|:
name|ords
control|)
block|{
name|BytesRef
name|term
init|=
name|terms
index|[
operator|(
name|int
operator|)
name|ord
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|ranges
index|[
name|i
index|]
operator|.
name|from
operator|==
literal|null
operator|||
name|ranges
index|[
name|i
index|]
operator|.
name|from
operator|.
name|compareTo
argument_list|(
name|term
argument_list|)
operator|<=
literal|0
operator|)
operator|&&
operator|(
name|ranges
index|[
name|i
index|]
operator|.
name|to
operator|==
literal|null
operator|||
name|ranges
index|[
name|i
index|]
operator|.
name|to
operator|.
name|compareTo
argument_list|(
name|term
argument_list|)
operator|>
literal|0
operator|)
condition|)
block|{
name|expectedCounts
index|[
name|i
index|]
operator|++
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
name|assertArrayEquals
argument_list|(
name|expectedCounts
argument_list|,
name|counts
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortedBinaryRangeLeafCollectorSingleValued
specifier|public
name|void
name|testSortedBinaryRangeLeafCollectorSingleValued
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|iters
init|=
name|randomInt
argument_list|(
literal|10
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
name|iters
condition|;
operator|++
name|i
control|)
block|{
name|doTestSortedBinaryRangeLeafCollector
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSortedBinaryRangeLeafCollectorMultiValued
specifier|public
name|void
name|testSortedBinaryRangeLeafCollectorMultiValued
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|iters
init|=
name|randomInt
argument_list|(
literal|10
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
name|iters
condition|;
operator|++
name|i
control|)
block|{
name|doTestSortedBinaryRangeLeafCollector
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

