begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.fieldcaps
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|fieldcaps
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
name|test
operator|.
name|AbstractWireSerializingTestCase
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
DECL|class|FieldCapabilitiesTests
specifier|public
class|class
name|FieldCapabilitiesTests
extends|extends
name|AbstractWireSerializingTestCase
argument_list|<
name|FieldCapabilities
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createTestInstance
specifier|protected
name|FieldCapabilities
name|createTestInstance
parameter_list|()
block|{
return|return
name|randomFieldCaps
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|instanceReader
specifier|protected
name|Writeable
operator|.
name|Reader
argument_list|<
name|FieldCapabilities
argument_list|>
name|instanceReader
parameter_list|()
block|{
return|return
name|FieldCapabilities
operator|::
operator|new
return|;
block|}
DECL|method|testBuilder
specifier|public
name|void
name|testBuilder
parameter_list|()
block|{
name|FieldCapabilities
operator|.
name|Builder
name|builder
init|=
operator|new
name|FieldCapabilities
operator|.
name|Builder
argument_list|(
literal|"field"
argument_list|,
literal|"type"
argument_list|)
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"index1"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"index2"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"index3"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|{
name|FieldCapabilities
name|cap1
init|=
name|builder
operator|.
name|build
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|cap1
operator|.
name|isSearchable
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cap1
operator|.
name|isAggregatable
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cap1
operator|.
name|indices
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cap1
operator|.
name|nonSearchableIndices
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cap1
operator|.
name|nonAggregatableIndices
argument_list|()
argument_list|)
expr_stmt|;
name|FieldCapabilities
name|cap2
init|=
name|builder
operator|.
name|build
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|cap2
operator|.
name|isSearchable
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cap2
operator|.
name|isAggregatable
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cap2
operator|.
name|indices
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cap2
operator|.
name|indices
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"index1"
block|,
literal|"index2"
block|,
literal|"index3"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cap2
operator|.
name|nonSearchableIndices
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cap2
operator|.
name|nonAggregatableIndices
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|=
operator|new
name|FieldCapabilities
operator|.
name|Builder
argument_list|(
literal|"field"
argument_list|,
literal|"type"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"index1"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"index2"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"index3"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|{
name|FieldCapabilities
name|cap1
init|=
name|builder
operator|.
name|build
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|cap1
operator|.
name|isSearchable
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cap1
operator|.
name|isAggregatable
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|cap1
operator|.
name|indices
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cap1
operator|.
name|nonSearchableIndices
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"index1"
block|,
literal|"index3"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cap1
operator|.
name|nonAggregatableIndices
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"index2"
block|,
literal|"index3"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|FieldCapabilities
name|cap2
init|=
name|builder
operator|.
name|build
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|cap2
operator|.
name|isSearchable
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cap2
operator|.
name|isAggregatable
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cap2
operator|.
name|indices
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cap2
operator|.
name|indices
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"index1"
block|,
literal|"index2"
block|,
literal|"index3"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cap1
operator|.
name|nonSearchableIndices
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"index1"
block|,
literal|"index3"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|cap1
operator|.
name|nonAggregatableIndices
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"index2"
block|,
literal|"index3"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|randomFieldCaps
specifier|static
name|FieldCapabilities
name|randomFieldCaps
parameter_list|()
block|{
name|String
index|[]
name|indices
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|indices
operator|=
operator|new
name|String
index|[
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
index|]
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
name|indices
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indices
index|[
name|i
index|]
operator|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|20
argument_list|)
expr_stmt|;
block|}
block|}
name|String
index|[]
name|nonSearchableIndices
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|nonSearchableIndices
operator|=
operator|new
name|String
index|[
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
index|]
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
name|nonSearchableIndices
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|nonSearchableIndices
index|[
name|i
index|]
operator|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|20
argument_list|)
expr_stmt|;
block|}
block|}
name|String
index|[]
name|nonAggregatableIndices
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|nonAggregatableIndices
operator|=
operator|new
name|String
index|[
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
index|]
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
name|nonAggregatableIndices
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|nonAggregatableIndices
index|[
name|i
index|]
operator|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|20
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|FieldCapabilities
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|20
argument_list|)
argument_list|,
name|randomAsciiOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|20
argument_list|)
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|indices
argument_list|,
name|nonSearchableIndices
argument_list|,
name|nonAggregatableIndices
argument_list|)
return|;
block|}
block|}
end_class

end_unit

