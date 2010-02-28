begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.json.merge.test1
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|json
operator|.
name|merge
operator|.
name|test1
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|Index
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
name|analysis
operator|.
name|AnalysisService
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
name|MergeMappingException
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
name|json
operator|.
name|JsonDocumentMapper
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
name|json
operator|.
name|JsonDocumentMapperParser
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
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|Streams
operator|.
name|*
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
name|*
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
annotation|@
name|Test
DECL|class|Test1MergeMapperTests
specifier|public
class|class
name|Test1MergeMapperTests
block|{
DECL|method|test1Merge
annotation|@
name|Test
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
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/json/merge/test1/stage1.json"
argument_list|)
decl_stmt|;
name|JsonDocumentMapper
name|stage1
init|=
operator|(
name|JsonDocumentMapper
operator|)
operator|new
name|JsonDocumentMapperParser
argument_list|(
operator|new
name|AnalysisService
argument_list|(
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|parse
argument_list|(
name|stage1Mapping
argument_list|)
decl_stmt|;
name|String
name|stage2Mapping
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/json/merge/test1/stage2.json"
argument_list|)
decl_stmt|;
name|JsonDocumentMapper
name|stage2
init|=
operator|(
name|JsonDocumentMapper
operator|)
operator|new
name|JsonDocumentMapperParser
argument_list|(
operator|new
name|AnalysisService
argument_list|(
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|parse
argument_list|(
name|stage2Mapping
argument_list|)
decl_stmt|;
try|try
block|{
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
expr_stmt|;
assert|assert
literal|false
operator|:
literal|"can't change field from number to type"
assert|;
block|}
catch|catch
parameter_list|(
name|MergeMappingException
name|e
parameter_list|)
block|{
comment|// all is well
block|}
comment|// now, test with ignore duplicates
name|stage1
operator|.
name|merge
argument_list|(
name|stage2
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|ignoreDuplicates
argument_list|(
literal|true
argument_list|)
operator|.
name|simulate
argument_list|(
literal|true
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
comment|// now merge, ignore duplicates and don't simulate
name|stage1
operator|.
name|merge
argument_list|(
name|stage2
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|ignoreDuplicates
argument_list|(
literal|true
argument_list|)
operator|.
name|simulate
argument_list|(
literal|false
argument_list|)
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
literal|"age"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

