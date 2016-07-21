begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.termvectors
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvectors
package|;
end_package

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
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Terms
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
name|TermsEnum
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
name|admin
operator|.
name|indices
operator|.
name|alias
operator|.
name|Alias
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
name|lucene
operator|.
name|uid
operator|.
name|Versions
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
name|settings
operator|.
name|Settings
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
name|IndexNotFoundException
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
name|engine
operator|.
name|VersionConflictEngineException
import|;
end_import

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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertAcked
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|instanceOf
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
name|notNullValue
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
name|nullValue
import|;
end_import

begin_class
DECL|class|MultiTermVectorsIT
specifier|public
class|class
name|MultiTermVectorsIT
extends|extends
name|AbstractTermVectorsTestCase
block|{
DECL|method|testDuelESLucene
specifier|public
name|void
name|testDuelESLucene
parameter_list|()
throws|throws
name|Exception
block|{
name|AbstractTermVectorsTestCase
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
literal|"test"
argument_list|,
literal|"alias"
argument_list|,
name|testFieldSettings
argument_list|)
expr_stmt|;
comment|//we generate as many docs as many shards we have
name|TestDoc
index|[]
name|testDocs
init|=
name|generateTestDocs
argument_list|(
literal|"test"
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
name|AbstractTermVectorsTestCase
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
name|AbstractTermVectorsTestCase
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
name|getCause
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
block|}
DECL|method|testMissingIndexThrowsMissingIndex
specifier|public
name|void
name|testMissingIndexThrowsMissingIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|TermVectorsRequestBuilder
name|requestBuilder
init|=
name|client
argument_list|()
operator|.
name|prepareTermVectors
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
name|client
argument_list|()
operator|.
name|prepareMultiTermVectors
argument_list|()
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
name|getCause
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|IndexNotFoundException
operator|.
name|class
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
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"no such index"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiTermVectorsWithVersion
specifier|public
name|void
name|testMultiTermVectorsWithVersion
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addAlias
argument_list|(
operator|new
name|Alias
argument_list|(
literal|"alias"
argument_list|)
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.refresh_interval"
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|MultiTermVectorsResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareMultiTermVectors
argument_list|()
operator|.
name|add
argument_list|(
name|indexOrAlias
argument_list|()
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|get
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
name|getResponse
argument_list|()
operator|.
name|isExists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
operator|+
name|i
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
comment|// Version from translog
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareMultiTermVectors
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|TermVectorsRequest
argument_list|(
name|indexOrAlias
argument_list|()
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|selectedFields
argument_list|(
literal|"field"
argument_list|)
operator|.
name|version
argument_list|(
name|Versions
operator|.
name|MATCH_ANY
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|TermVectorsRequest
argument_list|(
name|indexOrAlias
argument_list|()
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|selectedFields
argument_list|(
literal|"field"
argument_list|)
operator|.
name|version
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|TermVectorsRequest
argument_list|(
name|indexOrAlias
argument_list|()
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|selectedFields
argument_list|(
literal|"field"
argument_list|)
operator|.
name|version
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
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
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// [0] version doesn't matter, which is the default
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
argument_list|,
name|nullValue
argument_list|()
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
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
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
name|getIndex
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test"
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
name|getResponse
argument_list|()
operator|.
name|isExists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|checkTermTexts
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|0
index|]
operator|.
name|getResponse
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|terms
argument_list|(
literal|"field"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"value1"
block|}
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|1
index|]
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
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
literal|1
index|]
operator|.
name|getIndex
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test"
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
literal|1
index|]
operator|.
name|getFailure
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|1
index|]
operator|.
name|getResponse
argument_list|()
operator|.
name|isExists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|checkTermTexts
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|1
index|]
operator|.
name|getResponse
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|terms
argument_list|(
literal|"field"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"value1"
block|}
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|2
index|]
operator|.
name|getFailure
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|2
index|]
operator|.
name|getFailure
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
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
literal|2
index|]
operator|.
name|getFailure
argument_list|()
operator|.
name|getCause
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|VersionConflictEngineException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|//Version from Lucene index
name|refresh
argument_list|()
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareMultiTermVectors
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|TermVectorsRequest
argument_list|(
name|indexOrAlias
argument_list|()
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|selectedFields
argument_list|(
literal|"field"
argument_list|)
operator|.
name|version
argument_list|(
name|Versions
operator|.
name|MATCH_ANY
argument_list|)
operator|.
name|realtime
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|TermVectorsRequest
argument_list|(
name|indexOrAlias
argument_list|()
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|selectedFields
argument_list|(
literal|"field"
argument_list|)
operator|.
name|version
argument_list|(
literal|1
argument_list|)
operator|.
name|realtime
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|TermVectorsRequest
argument_list|(
name|indexOrAlias
argument_list|()
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|selectedFields
argument_list|(
literal|"field"
argument_list|)
operator|.
name|version
argument_list|(
literal|2
argument_list|)
operator|.
name|realtime
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
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
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// [0] version doesn't matter, which is the default
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
argument_list|,
name|nullValue
argument_list|()
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
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
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
name|getResponse
argument_list|()
operator|.
name|isExists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|checkTermTexts
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|0
index|]
operator|.
name|getResponse
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|terms
argument_list|(
literal|"field"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"value1"
block|}
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|1
index|]
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
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
literal|1
index|]
operator|.
name|getFailure
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|1
index|]
operator|.
name|getResponse
argument_list|()
operator|.
name|isExists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|checkTermTexts
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|1
index|]
operator|.
name|getResponse
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|terms
argument_list|(
literal|"field"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"value1"
block|}
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|2
index|]
operator|.
name|getFailure
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|2
index|]
operator|.
name|getFailure
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
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
literal|2
index|]
operator|.
name|getFailure
argument_list|()
operator|.
name|getCause
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|VersionConflictEngineException
operator|.
name|class
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
operator|+
name|i
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
comment|// Version from translog
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareMultiTermVectors
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|TermVectorsRequest
argument_list|(
name|indexOrAlias
argument_list|()
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|selectedFields
argument_list|(
literal|"field"
argument_list|)
operator|.
name|version
argument_list|(
name|Versions
operator|.
name|MATCH_ANY
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|TermVectorsRequest
argument_list|(
name|indexOrAlias
argument_list|()
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|selectedFields
argument_list|(
literal|"field"
argument_list|)
operator|.
name|version
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|TermVectorsRequest
argument_list|(
name|indexOrAlias
argument_list|()
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|selectedFields
argument_list|(
literal|"field"
argument_list|)
operator|.
name|version
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
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
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// [0] version doesn't matter, which is the default
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
argument_list|,
name|nullValue
argument_list|()
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
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
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
name|getIndex
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test"
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
name|getResponse
argument_list|()
operator|.
name|isExists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|checkTermTexts
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|0
index|]
operator|.
name|getResponse
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|terms
argument_list|(
literal|"field"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"value2"
block|}
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|1
index|]
operator|.
name|getFailure
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|1
index|]
operator|.
name|getFailure
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
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
literal|1
index|]
operator|.
name|getIndex
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test"
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
literal|1
index|]
operator|.
name|getFailure
argument_list|()
operator|.
name|getCause
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|VersionConflictEngineException
operator|.
name|class
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
literal|2
index|]
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
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
literal|2
index|]
operator|.
name|getIndex
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test"
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
literal|2
index|]
operator|.
name|getFailure
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|2
index|]
operator|.
name|getResponse
argument_list|()
operator|.
name|isExists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|checkTermTexts
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|2
index|]
operator|.
name|getResponse
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|terms
argument_list|(
literal|"field"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"value2"
block|}
argument_list|)
expr_stmt|;
comment|//Version from Lucene index
name|refresh
argument_list|()
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareMultiTermVectors
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|TermVectorsRequest
argument_list|(
name|indexOrAlias
argument_list|()
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|selectedFields
argument_list|(
literal|"field"
argument_list|)
operator|.
name|version
argument_list|(
name|Versions
operator|.
name|MATCH_ANY
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|TermVectorsRequest
argument_list|(
name|indexOrAlias
argument_list|()
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|selectedFields
argument_list|(
literal|"field"
argument_list|)
operator|.
name|version
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|TermVectorsRequest
argument_list|(
name|indexOrAlias
argument_list|()
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|selectedFields
argument_list|(
literal|"field"
argument_list|)
operator|.
name|version
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
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
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// [0] version doesn't matter, which is the default
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
argument_list|,
name|nullValue
argument_list|()
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
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
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
name|getIndex
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test"
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
name|getResponse
argument_list|()
operator|.
name|isExists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|checkTermTexts
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|0
index|]
operator|.
name|getResponse
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|terms
argument_list|(
literal|"field"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"value2"
block|}
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|1
index|]
operator|.
name|getFailure
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|1
index|]
operator|.
name|getFailure
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
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
literal|1
index|]
operator|.
name|getIndex
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test"
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
literal|1
index|]
operator|.
name|getFailure
argument_list|()
operator|.
name|getCause
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|VersionConflictEngineException
operator|.
name|class
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
literal|2
index|]
operator|.
name|getId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"2"
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
literal|2
index|]
operator|.
name|getIndex
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test"
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
literal|2
index|]
operator|.
name|getFailure
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|2
index|]
operator|.
name|getResponse
argument_list|()
operator|.
name|isExists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|checkTermTexts
argument_list|(
name|response
operator|.
name|getResponses
argument_list|()
index|[
literal|2
index|]
operator|.
name|getResponse
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|terms
argument_list|(
literal|"field"
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"value2"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|indexOrAlias
specifier|private
specifier|static
name|String
name|indexOrAlias
parameter_list|()
block|{
return|return
name|randomBoolean
argument_list|()
condition|?
literal|"test"
else|:
literal|"alias"
return|;
block|}
DECL|method|checkTermTexts
specifier|private
name|void
name|checkTermTexts
parameter_list|(
name|Terms
name|terms
parameter_list|,
name|String
index|[]
name|expectedTexts
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|expectedText
range|:
name|expectedTexts
control|)
block|{
name|assertThat
argument_list|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedText
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

