begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|index
operator|.
name|IndexRequestBuilder
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
name|search
operator|.
name|SearchResponse
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
name|bytes
operator|.
name|BytesArray
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
name|bytes
operator|.
name|BytesReference
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
name|xcontent
operator|.
name|XContentType
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
name|query
operator|.
name|QueryBuilders
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
name|ESIntegTestCase
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
name|hamcrest
operator|.
name|ElasticsearchAssertions
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
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
name|containsString
import|;
end_import

begin_class
DECL|class|IndexRequestBuilderIT
specifier|public
class|class
name|IndexRequestBuilderIT
extends|extends
name|ESIntegTestCase
block|{
DECL|method|testSetSource
specifier|public
name|void
name|testSetSource
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"test_field"
argument_list|,
literal|"foobar"
argument_list|)
expr_stmt|;
name|IndexRequestBuilder
index|[]
name|builders
init|=
operator|new
name|IndexRequestBuilder
index|[]
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|setSource
argument_list|(
operator|(
name|Object
operator|)
literal|"test_field"
argument_list|,
operator|(
name|Object
operator|)
literal|"foobar"
argument_list|)
block|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{\"test_field\" : \"foobar\"}"
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
block|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|setSource
argument_list|(
operator|new
name|BytesArray
argument_list|(
literal|"{\"test_field\" : \"foobar\"}"
argument_list|)
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
block|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|setSource
argument_list|(
operator|new
name|BytesArray
argument_list|(
literal|"{\"test_field\" : \"foobar\"}"
argument_list|)
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
block|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|setSource
argument_list|(
name|BytesReference
operator|.
name|toBytes
argument_list|(
operator|new
name|BytesArray
argument_list|(
literal|"{\"test_field\" : \"foobar\"}"
argument_list|)
argument_list|)
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
block|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|setSource
argument_list|(
name|map
argument_list|)
block|}
decl_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|builders
argument_list|)
expr_stmt|;
name|SearchResponse
name|searchResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"test_field"
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|ElasticsearchAssertions
operator|.
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
name|builders
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testOddNumberOfSourceObjects
specifier|public
name|void
name|testOddNumberOfSourceObjects
parameter_list|()
block|{
try|try
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"test_field"
argument_list|,
literal|"foobar"
argument_list|,
operator|new
name|Object
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"The number of object passed must be even but was [3]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

