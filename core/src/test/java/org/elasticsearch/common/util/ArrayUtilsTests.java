begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
package|;
end_package

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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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
name|is
import|;
end_import

begin_class
DECL|class|ArrayUtilsTests
specifier|public
class|class
name|ArrayUtilsTests
extends|extends
name|ESTestCase
block|{
DECL|method|testBinarySearch
specifier|public
name|void
name|testBinarySearch
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|int
name|index
init|=
name|Math
operator|.
name|min
argument_list|(
name|randomInt
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|9
argument_list|)
decl_stmt|;
name|double
name|tolerance
init|=
name|Math
operator|.
name|random
argument_list|()
operator|*
literal|0.01
decl_stmt|;
name|double
name|lookForValue
init|=
name|randomFreq
argument_list|(
literal|0.9
argument_list|)
condition|?
operator|-
literal|1
else|:
name|Double
operator|.
name|NaN
decl_stmt|;
comment|// sometimes we'll look for NaN
name|double
index|[]
name|array
init|=
operator|new
name|double
index|[
literal|10
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
name|array
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|double
name|value
decl_stmt|;
if|if
condition|(
name|randomFreq
argument_list|(
literal|0.9
argument_list|)
condition|)
block|{
name|value
operator|=
name|Math
operator|.
name|random
argument_list|()
operator|*
literal|10
expr_stmt|;
name|array
index|[
name|i
index|]
operator|=
name|value
operator|+
operator|(
operator|(
name|randomFreq
argument_list|(
literal|0.5
argument_list|)
condition|?
literal|1
else|:
operator|-
literal|1
operator|)
operator|*
name|Math
operator|.
name|random
argument_list|()
operator|*
name|tolerance
operator|)
expr_stmt|;
block|}
else|else
block|{
comment|// sometimes we'll have NaN in the array
name|value
operator|=
name|Double
operator|.
name|NaN
expr_stmt|;
name|array
index|[
name|i
index|]
operator|=
name|value
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|==
name|index
operator|&&
name|lookForValue
operator|<
literal|0
condition|)
block|{
name|lookForValue
operator|=
name|value
expr_stmt|;
block|}
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|array
argument_list|)
expr_stmt|;
comment|// pick up all the indices that fall within the range of [lookForValue - tolerance, lookForValue + tolerance]
comment|// we need to do this, since we choose the values randomly and we might end up having multiple values in the
comment|// array that will match the looked for value with the random tolerance. In such cases, the binary search will
comment|// return the first one that will match.
name|BitSet
name|bitSet
init|=
operator|new
name|BitSet
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
name|array
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Double
operator|.
name|isNaN
argument_list|(
name|lookForValue
argument_list|)
operator|&&
name|Double
operator|.
name|isNaN
argument_list|(
name|array
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|array
index|[
name|i
index|]
operator|>=
name|lookForValue
operator|-
name|tolerance
operator|)
operator|&&
operator|(
name|array
index|[
name|i
index|]
operator|<=
name|lookForValue
operator|+
name|tolerance
operator|)
condition|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|foundIndex
init|=
name|ArrayUtils
operator|.
name|binarySearch
argument_list|(
name|array
argument_list|,
name|lookForValue
argument_list|,
name|tolerance
argument_list|)
decl_stmt|;
if|if
condition|(
name|bitSet
operator|.
name|cardinality
argument_list|()
operator|==
literal|0
condition|)
block|{
name|assertThat
argument_list|(
name|foundIndex
argument_list|,
name|is
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|bitSet
operator|.
name|get
argument_list|(
name|foundIndex
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|randomFreq
specifier|private
name|boolean
name|randomFreq
parameter_list|(
name|double
name|freq
parameter_list|)
block|{
return|return
name|Math
operator|.
name|random
argument_list|()
operator|<
name|freq
return|;
block|}
DECL|method|randomInt
specifier|private
name|int
name|randomInt
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|int
name|delta
init|=
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|random
argument_list|()
operator|*
operator|(
name|max
operator|-
name|min
operator|)
argument_list|)
decl_stmt|;
return|return
name|min
operator|+
name|delta
return|;
block|}
DECL|method|testConcat
specifier|public
name|void
name|testConcat
parameter_list|()
block|{
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|,
literal|"d"
block|}
argument_list|,
name|ArrayUtils
operator|.
name|concat
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"c"
block|,
literal|"d"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|firstSize
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
index|[]
name|first
init|=
operator|new
name|String
index|[
name|firstSize
index|]
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|sourceOfTruth
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
name|firstSize
condition|;
name|i
operator|++
control|)
block|{
name|first
index|[
name|i
index|]
operator|=
name|randomRealisticUnicodeOfCodepointLengthBetween
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|sourceOfTruth
operator|.
name|add
argument_list|(
name|first
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|int
name|secondSize
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|String
index|[]
name|second
init|=
operator|new
name|String
index|[
name|secondSize
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
name|secondSize
condition|;
name|i
operator|++
control|)
block|{
name|second
index|[
name|i
index|]
operator|=
name|randomRealisticUnicodeOfCodepointLengthBetween
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|sourceOfTruth
operator|.
name|add
argument_list|(
name|second
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertArrayEquals
argument_list|(
name|sourceOfTruth
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|ArrayUtils
operator|.
name|concat
argument_list|(
name|first
argument_list|,
name|second
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

