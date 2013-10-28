begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.action.termvector
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvector
package|;
end_package

begin_comment
comment|/*  * Licensed to ElasticSearch under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DirectoryReader
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
name|index
operator|.
name|Fields
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvector
operator|.
name|MultiTermVectorsItemResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvector
operator|.
name|MultiTermVectorsRequestBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvector
operator|.
name|MultiTermVectorsResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvector
operator|.
name|TermVectorRequestBuilder
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
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|MultiTermVectorsTests
specifier|public
class|class
name|MultiTermVectorsTests
extends|extends
name|AbstractTermVectorTests
block|{
annotation|@
name|Test
DECL|method|testDuelESLucene
specifier|public
name|void
name|testDuelESLucene
parameter_list|()
throws|throws
name|Exception
block|{
name|AbstractTermVectorTests
operator|.
name|TestFieldSetting
index|[]
name|testFieldSettings
init|=
name|getFieldSettings
argument_list|()
decl_stmt|;
name|createIndexBasedOnFieldSettings
argument_list|(
name|testFieldSettings
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|AbstractTermVectorTests
operator|.
name|TestDoc
index|[]
name|testDocs
init|=
name|generateTestDocs
argument_list|(
literal|5
argument_list|,
name|testFieldSettings
argument_list|)
decl_stmt|;
name|DirectoryReader
name|directoryReader
init|=
name|indexDocsWithLucene
argument_list|(
name|testDocs
argument_list|)
decl_stmt|;
name|AbstractTermVectorTests
operator|.
name|TestConfig
index|[]
name|testConfigs
init|=
name|generateTestConfigs
argument_list|(
literal|20
argument_list|,
name|testDocs
argument_list|,
name|testFieldSettings
argument_list|)
decl_stmt|;
name|MultiTermVectorsRequestBuilder
name|requestBuilder
init|=
name|client
argument_list|()
operator|.
name|prepareMultiTermVectors
argument_list|()
decl_stmt|;
for|for
control|(
name|AbstractTermVectorTests
operator|.
name|TestConfig
name|test
range|:
name|testConfigs
control|)
block|{
name|requestBuilder
operator|.
name|add
argument_list|(
name|getRequestForConfig
argument_list|(
name|test
argument_list|)
operator|.
name|request
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|MultiTermVectorsItemResponse
index|[]
name|responseItems
init|=
name|requestBuilder
operator|.
name|get
argument_list|()
operator|.
name|getResponses
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
name|testConfigs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TestConfig
name|test
init|=
name|testConfigs
index|[
name|i
index|]
decl_stmt|;
try|try
block|{
name|MultiTermVectorsItemResponse
name|item
init|=
name|responseItems
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|expectedException
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|item
operator|.
name|isFailed
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
elseif|else
if|if
condition|(
name|item
operator|.
name|isFailed
argument_list|()
condition|)
block|{
name|fail
argument_list|(
name|item
operator|.
name|getFailure
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Fields
name|luceneTermVectors
init|=
name|getTermVectorsFromLucene
argument_list|(
name|directoryReader
argument_list|,
name|test
operator|.
name|doc
argument_list|)
decl_stmt|;
name|validateResponse
argument_list|(
name|item
operator|.
name|getResponse
argument_list|()
argument_list|,
name|luceneTermVectors
argument_list|,
name|test
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Test exception while running "
operator|+
name|test
operator|.
name|toString
argument_list|()
argument_list|,
name|t
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|testMissingIndexThrowsMissingIndex
specifier|public
name|void
name|testMissingIndexThrowsMissingIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|TermVectorRequestBuilder
name|requestBuilder
init|=
name|client
argument_list|()
operator|.
name|prepareTermVector
argument_list|(
literal|"testX"
argument_list|,
literal|"typeX"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|MultiTermVectorsRequestBuilder
name|mtvBuilder
init|=
operator|new
name|MultiTermVectorsRequestBuilder
argument_list|(
name|client
argument_list|()
argument_list|)
decl_stmt|;
name|mtvBuilder
operator|.
name|add
argument_list|(
name|requestBuilder
operator|.
name|request
argument_list|()
argument_list|)
expr_stmt|;
name|MultiTermVectorsResponse
name|response
init|=
name|mtvBuilder
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResponses
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
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|0
index|]
operator|.
name|getFailure
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"["
operator|+
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|0
index|]
operator|.
name|getIndex
argument_list|()
operator|+
literal|"] missing"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

