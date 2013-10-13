begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ElasticSearchTestCase
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
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
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

begin_comment
comment|/**  * Tests for {@link SlicedDoubleList}  */
end_comment

begin_class
DECL|class|SlicedDoubleListTests
specifier|public
class|class
name|SlicedDoubleListTests
extends|extends
name|ElasticSearchTestCase
block|{
annotation|@
name|Test
DECL|method|testCapacity
specifier|public
name|void
name|testCapacity
parameter_list|()
block|{
name|SlicedDoubleList
name|list
init|=
operator|new
name|SlicedDoubleList
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|list
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|list
operator|.
name|offset
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|list
operator|.
name|values
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|=
operator|new
name|SlicedDoubleList
argument_list|(
operator|new
name|double
index|[
literal|10
index|]
argument_list|,
literal|5
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|list
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|list
operator|.
name|offset
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|list
operator|.
name|values
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGrow
specifier|public
name|void
name|testGrow
parameter_list|()
block|{
name|SlicedDoubleList
name|list
init|=
operator|new
name|SlicedDoubleList
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|list
operator|.
name|length
operator|=
literal|1000
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|grow
argument_list|(
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|list
operator|.
name|values
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|double
operator|)
name|i
operator|)
expr_stmt|;
block|}
name|int
name|expected
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Double
name|d
range|:
name|list
control|)
block|{
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|expected
operator|++
argument_list|,
name|equalTo
argument_list|(
name|d
argument_list|)
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
name|list
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|i
argument_list|,
name|equalTo
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|list
operator|.
name|offset
init|;
name|i
operator|<
name|list
operator|.
name|offset
operator|+
name|list
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|count
operator|++
argument_list|,
name|equalTo
argument_list|(
name|list
operator|.
name|values
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testIndexOf
specifier|public
name|void
name|testIndexOf
parameter_list|()
block|{
name|SlicedDoubleList
name|list
init|=
operator|new
name|SlicedDoubleList
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|list
operator|.
name|length
operator|=
literal|1000
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|grow
argument_list|(
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|list
operator|.
name|values
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|double
operator|)
name|i
operator|%
literal|100
operator|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
literal|999
argument_list|,
name|equalTo
argument_list|(
name|list
operator|.
name|lastIndexOf
argument_list|(
literal|99.0d
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|99
argument_list|,
name|equalTo
argument_list|(
name|list
operator|.
name|indexOf
argument_list|(
literal|99.0d
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|-
literal|1
argument_list|,
name|equalTo
argument_list|(
name|list
operator|.
name|lastIndexOf
argument_list|(
literal|100.0d
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|-
literal|1
argument_list|,
name|equalTo
argument_list|(
name|list
operator|.
name|indexOf
argument_list|(
literal|100.0d
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIsEmpty
specifier|public
name|void
name|testIsEmpty
parameter_list|()
block|{
name|SlicedDoubleList
name|list
init|=
operator|new
name|SlicedDoubleList
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|false
argument_list|,
name|equalTo
argument_list|(
name|list
operator|.
name|isEmpty
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|assertThat
argument_list|(
literal|true
argument_list|,
name|equalTo
argument_list|(
name|list
operator|.
name|isEmpty
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSet
specifier|public
name|void
name|testSet
parameter_list|()
block|{
name|SlicedDoubleList
name|list
init|=
operator|new
name|SlicedDoubleList
argument_list|(
literal|5
argument_list|)
decl_stmt|;
try|try
block|{
name|list
operator|.
name|set
argument_list|(
literal|0
argument_list|,
operator|(
name|double
operator|)
literal|4
argument_list|)
expr_stmt|;
assert|assert
literal|false
assert|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{         }
try|try
block|{
name|list
operator|.
name|add
argument_list|(
operator|(
name|double
operator|)
literal|4
argument_list|)
expr_stmt|;
assert|assert
literal|false
assert|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{         }
block|}
annotation|@
name|Test
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|SlicedDoubleList
name|list
init|=
operator|new
name|SlicedDoubleList
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"[0.0, 0.0, 0.0, 0.0, 0.0]"
argument_list|,
name|equalTo
argument_list|(
name|list
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|grow
argument_list|(
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|list
operator|.
name|values
index|[
name|i
index|]
operator|=
operator|(
operator|(
name|double
operator|)
name|i
operator|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
literal|"[0.0, 1.0, 2.0, 3.0, 4.0]"
argument_list|,
name|equalTo
argument_list|(
name|list
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

