begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugins
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
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
name|hasItem
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
name|hasItems
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
name|hasSize
import|;
end_import

begin_class
DECL|class|ProgressInputStreamTests
specifier|public
class|class
name|ProgressInputStreamTests
extends|extends
name|ESTestCase
block|{
DECL|field|progresses
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|progresses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|testThatProgressListenerIsCalled
specifier|public
name|void
name|testThatProgressListenerIsCalled
parameter_list|()
throws|throws
name|Exception
block|{
name|ProgressInputStream
name|is
init|=
name|newProgressInputStream
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|is
operator|.
name|checkProgress
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|progresses
argument_list|,
name|hasSize
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|progresses
argument_list|,
name|hasItems
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatProgressListenerIsCalledOnUnexpectedCompletion
specifier|public
name|void
name|testThatProgressListenerIsCalledOnUnexpectedCompletion
parameter_list|()
throws|throws
name|Exception
block|{
name|ProgressInputStream
name|is
init|=
name|newProgressInputStream
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|is
operator|.
name|checkProgress
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|progresses
argument_list|,
name|hasItems
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatProgressListenerReturnsMaxValueOnWrongExpectedSize
specifier|public
name|void
name|testThatProgressListenerReturnsMaxValueOnWrongExpectedSize
parameter_list|()
throws|throws
name|Exception
block|{
name|ProgressInputStream
name|is
init|=
name|newProgressInputStream
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|is
operator|.
name|checkProgress
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|progresses
argument_list|,
name|hasItems
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|.
name|checkProgress
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|progresses
argument_list|,
name|hasItems
argument_list|(
literal|50
argument_list|,
literal|99
argument_list|)
argument_list|)
expr_stmt|;
name|is
operator|.
name|checkProgress
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|progresses
argument_list|,
name|hasItems
argument_list|(
literal|50
argument_list|,
literal|99
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneByte
specifier|public
name|void
name|testOneByte
parameter_list|()
throws|throws
name|Exception
block|{
name|ProgressInputStream
name|is
init|=
name|newProgressInputStream
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|is
operator|.
name|checkProgress
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|is
operator|.
name|checkProgress
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|progresses
argument_list|,
name|hasItems
argument_list|(
literal|99
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOddBytes
specifier|public
name|void
name|testOddBytes
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|odd
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
operator|*
literal|2
operator|+
literal|1
decl_stmt|;
name|ProgressInputStream
name|is
init|=
name|newProgressInputStream
argument_list|(
name|odd
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
name|odd
condition|;
name|i
operator|++
control|)
block|{
name|is
operator|.
name|checkProgress
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|checkProgress
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|progresses
argument_list|,
name|hasSize
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|odd
operator|+
literal|1
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|progresses
argument_list|,
name|hasItem
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEvenBytes
specifier|public
name|void
name|testEvenBytes
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|even
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
operator|*
literal|2
decl_stmt|;
name|ProgressInputStream
name|is
init|=
name|newProgressInputStream
argument_list|(
name|even
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
name|even
condition|;
name|i
operator|++
control|)
block|{
name|is
operator|.
name|checkProgress
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|checkProgress
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|progresses
argument_list|,
name|hasSize
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|even
operator|+
literal|1
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|progresses
argument_list|,
name|hasItem
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOnProgressCannotBeCalledMoreThanOncePerPercent
specifier|public
name|void
name|testOnProgressCannotBeCalledMoreThanOncePerPercent
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|count
init|=
name|randomIntBetween
argument_list|(
literal|150
argument_list|,
literal|300
argument_list|)
decl_stmt|;
name|ProgressInputStream
name|is
init|=
name|newProgressInputStream
argument_list|(
name|count
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|is
operator|.
name|checkProgress
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|checkProgress
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|progresses
argument_list|,
name|hasSize
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|newProgressInputStream
specifier|private
name|ProgressInputStream
name|newProgressInputStream
parameter_list|(
name|int
name|expectedSize
parameter_list|)
block|{
return|return
operator|new
name|ProgressInputStream
argument_list|(
literal|null
argument_list|,
name|expectedSize
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|onProgress
parameter_list|(
name|int
name|percent
parameter_list|)
block|{
name|progresses
operator|.
name|add
argument_list|(
name|percent
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

