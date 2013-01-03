begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.index.mapper.merge
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
operator|.
name|index
operator|.
name|mapper
operator|.
name|merge
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
name|xcontent
operator|.
name|XContentFactory
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
name|mapper
operator|.
name|DocumentMapper
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
name|unit
operator|.
name|index
operator|.
name|mapper
operator|.
name|MapperTests
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
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
name|index
operator|.
name|mapper
operator|.
name|DocumentMapper
operator|.
name|MergeFlags
operator|.
name|mergeFlags
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
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
annotation|@
name|Test
DECL|class|TestMergeMapperTests
specifier|public
class|class
name|TestMergeMapperTests
block|{
annotation|@
name|Test
DECL|method|test1Merge
specifier|public
name|void
name|test1Merge
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|stage1Mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"person"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"name"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|stage1
init|=
name|MapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|stage1Mapping
argument_list|)
decl_stmt|;
name|String
name|stage2Mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"person"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"name"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"age"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"integer"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"obj1"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"prop1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"integer"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|stage2
init|=
name|MapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|stage2Mapping
argument_list|)
decl_stmt|;
name|DocumentMapper
operator|.
name|MergeResult
name|mergeResult
init|=
name|stage1
operator|.
name|merge
argument_list|(
name|stage2
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|mergeResult
operator|.
name|hasConflicts
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// since we are simulating, we should not have the age mapping
name|assertThat
argument_list|(
name|stage1
operator|.
name|mappers
argument_list|()
operator|.
name|smartName
argument_list|(
literal|"age"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stage1
operator|.
name|mappers
argument_list|()
operator|.
name|smartName
argument_list|(
literal|"obj1.prop1"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// now merge, don't simulate
name|mergeResult
operator|=
name|stage1
operator|.
name|merge
argument_list|(
name|stage2
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// there is still merge failures
name|assertThat
argument_list|(
name|mergeResult
operator|.
name|hasConflicts
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// but we have the age in
name|assertThat
argument_list|(
name|stage1
operator|.
name|mappers
argument_list|()
operator|.
name|smartName
argument_list|(
literal|"age"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stage1
operator|.
name|mappers
argument_list|()
operator|.
name|smartName
argument_list|(
literal|"obj1.prop1"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMergeObjectAndNested
specifier|public
name|void
name|testMergeObjectAndNested
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|objectMapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"obj"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"object"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|objectMapper
init|=
name|MapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|objectMapping
argument_list|)
decl_stmt|;
name|String
name|nestedMapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"obj"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"nested"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|nestedMapper
init|=
name|MapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|nestedMapping
argument_list|)
decl_stmt|;
name|DocumentMapper
operator|.
name|MergeResult
name|mergeResult
init|=
name|objectMapper
operator|.
name|merge
argument_list|(
name|nestedMapper
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|mergeResult
operator|.
name|hasConflicts
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
name|mergeResult
operator|.
name|conflicts
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mergeResult
operator|.
name|conflicts
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
literal|"object mapping [obj] can't be changed from non-nested to nested"
argument_list|)
argument_list|)
expr_stmt|;
name|mergeResult
operator|=
name|nestedMapper
operator|.
name|merge
argument_list|(
name|objectMapper
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mergeResult
operator|.
name|conflicts
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|mergeResult
operator|.
name|conflicts
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
literal|"object mapping [obj] can't be changed from nested to non-nested"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

