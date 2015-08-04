begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|SearchPhaseExecutionException
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
name|ShardSearchFailure
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
name|query
operator|.
name|TestQueryParsingException
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
name|SearchShardTarget
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
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|FakeRestRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|RemoteTransportException
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
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|contains
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|not
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|BytesRestResponseTests
specifier|public
class|class
name|BytesRestResponseTests
extends|extends
name|ESTestCase
block|{
annotation|@
name|Test
DECL|method|testWithHeaders
specifier|public
name|void
name|testWithHeaders
parameter_list|()
throws|throws
name|Exception
block|{
name|RestRequest
name|request
init|=
operator|new
name|FakeRestRequest
argument_list|()
decl_stmt|;
name|RestChannel
name|channel
init|=
name|randomBoolean
argument_list|()
condition|?
operator|new
name|DetailedExceptionRestChannel
argument_list|(
name|request
argument_list|)
else|:
operator|new
name|SimpleExceptionRestChannel
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|BytesRestResponse
name|response
init|=
operator|new
name|BytesRestResponse
argument_list|(
name|channel
argument_list|,
operator|new
name|WithHeadersException
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
literal|"n1"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
literal|"n1"
argument_list|)
argument_list|,
name|contains
argument_list|(
literal|"v11"
argument_list|,
literal|"v12"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
literal|"n2"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
literal|"n2"
argument_list|)
argument_list|,
name|contains
argument_list|(
literal|"v21"
argument_list|,
literal|"v22"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleExceptionMessage
specifier|public
name|void
name|testSimpleExceptionMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|RestRequest
name|request
init|=
operator|new
name|FakeRestRequest
argument_list|()
decl_stmt|;
name|RestChannel
name|channel
init|=
operator|new
name|SimpleExceptionRestChannel
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Throwable
name|t
init|=
operator|new
name|ElasticsearchException
argument_list|(
literal|"an error occurred reading data"
argument_list|,
operator|new
name|FileNotFoundException
argument_list|(
literal|"/foo/bar"
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRestResponse
name|response
init|=
operator|new
name|BytesRestResponse
argument_list|(
name|channel
argument_list|,
name|t
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|response
operator|.
name|content
argument_list|()
operator|.
name|toUtf8
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|containsString
argument_list|(
literal|"ElasticsearchException[an error occurred reading data]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"FileNotFoundException"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"/foo/bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"error_trace"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDetailedExceptionMessage
specifier|public
name|void
name|testDetailedExceptionMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|RestRequest
name|request
init|=
operator|new
name|FakeRestRequest
argument_list|()
decl_stmt|;
name|RestChannel
name|channel
init|=
operator|new
name|DetailedExceptionRestChannel
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Throwable
name|t
init|=
operator|new
name|ElasticsearchException
argument_list|(
literal|"an error occurred reading data"
argument_list|,
operator|new
name|FileNotFoundException
argument_list|(
literal|"/foo/bar"
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRestResponse
name|response
init|=
operator|new
name|BytesRestResponse
argument_list|(
name|channel
argument_list|,
name|t
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|response
operator|.
name|content
argument_list|()
operator|.
name|toUtf8
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|containsString
argument_list|(
literal|"{\"type\":\"exception\",\"reason\":\"an error occurred reading data\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|containsString
argument_list|(
literal|"{\"type\":\"file_not_found_exception\",\"reason\":\"/foo/bar\"}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNonElasticsearchExceptionIsNotShownAsSimpleMessage
specifier|public
name|void
name|testNonElasticsearchExceptionIsNotShownAsSimpleMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|RestRequest
name|request
init|=
operator|new
name|FakeRestRequest
argument_list|()
decl_stmt|;
name|RestChannel
name|channel
init|=
operator|new
name|SimpleExceptionRestChannel
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Throwable
name|t
init|=
operator|new
name|Throwable
argument_list|(
literal|"an error occurred reading data"
argument_list|,
operator|new
name|FileNotFoundException
argument_list|(
literal|"/foo/bar"
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRestResponse
name|response
init|=
operator|new
name|BytesRestResponse
argument_list|(
name|channel
argument_list|,
name|t
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|response
operator|.
name|content
argument_list|()
operator|.
name|toUtf8
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"Throwable[an error occurred reading data]"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"FileNotFoundException[/foo/bar]"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"error_trace"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|containsString
argument_list|(
literal|"\"error\":\"No ElasticsearchException found\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testErrorTrace
specifier|public
name|void
name|testErrorTrace
parameter_list|()
throws|throws
name|Exception
block|{
name|RestRequest
name|request
init|=
operator|new
name|FakeRestRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|params
argument_list|()
operator|.
name|put
argument_list|(
literal|"error_trace"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|RestChannel
name|channel
init|=
operator|new
name|DetailedExceptionRestChannel
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Throwable
name|t
init|=
operator|new
name|Throwable
argument_list|(
literal|"an error occurred reading data"
argument_list|,
operator|new
name|FileNotFoundException
argument_list|(
literal|"/foo/bar"
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRestResponse
name|response
init|=
operator|new
name|BytesRestResponse
argument_list|(
name|channel
argument_list|,
name|t
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|response
operator|.
name|content
argument_list|()
operator|.
name|toUtf8
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|containsString
argument_list|(
literal|"\"type\":\"throwable\",\"reason\":\"an error occurred reading data\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|containsString
argument_list|(
literal|"{\"type\":\"file_not_found_exception\",\"reason\":\"/foo/bar\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|containsString
argument_list|(
literal|"\"error_trace\":{\"message\":\"an error occurred reading data\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testGuessRootCause
specifier|public
name|void
name|testGuessRootCause
parameter_list|()
throws|throws
name|IOException
block|{
name|RestRequest
name|request
init|=
operator|new
name|FakeRestRequest
argument_list|()
decl_stmt|;
name|RestChannel
name|channel
init|=
operator|new
name|DetailedExceptionRestChannel
argument_list|(
name|request
argument_list|)
decl_stmt|;
block|{
name|Throwable
name|t
init|=
operator|new
name|ElasticsearchException
argument_list|(
literal|"an error occurred reading data"
argument_list|,
operator|new
name|FileNotFoundException
argument_list|(
literal|"/foo/bar"
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRestResponse
name|response
init|=
operator|new
name|BytesRestResponse
argument_list|(
name|channel
argument_list|,
name|t
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|response
operator|.
name|content
argument_list|()
operator|.
name|toUtf8
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|containsString
argument_list|(
literal|"{\"root_cause\":[{\"type\":\"exception\",\"reason\":\"an error occurred reading data\"}]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|Throwable
name|t
init|=
operator|new
name|FileNotFoundException
argument_list|(
literal|"/foo/bar"
argument_list|)
decl_stmt|;
name|BytesRestResponse
name|response
init|=
operator|new
name|BytesRestResponse
argument_list|(
name|channel
argument_list|,
name|t
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|response
operator|.
name|content
argument_list|()
operator|.
name|toUtf8
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|containsString
argument_list|(
literal|"{\"root_cause\":[{\"type\":\"file_not_found_exception\",\"reason\":\"/foo/bar\"}]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNullThrowable
specifier|public
name|void
name|testNullThrowable
parameter_list|()
throws|throws
name|Exception
block|{
name|RestRequest
name|request
init|=
operator|new
name|FakeRestRequest
argument_list|()
decl_stmt|;
name|RestChannel
name|channel
init|=
operator|new
name|SimpleExceptionRestChannel
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|BytesRestResponse
name|response
init|=
operator|new
name|BytesRestResponse
argument_list|(
name|channel
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|response
operator|.
name|content
argument_list|()
operator|.
name|toUtf8
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|containsString
argument_list|(
literal|"\"error\":\"unknown\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"error_trace"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvert
specifier|public
name|void
name|testConvert
parameter_list|()
throws|throws
name|IOException
block|{
name|RestRequest
name|request
init|=
operator|new
name|FakeRestRequest
argument_list|()
decl_stmt|;
name|RestChannel
name|channel
init|=
operator|new
name|DetailedExceptionRestChannel
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|ShardSearchFailure
name|failure
init|=
operator|new
name|ShardSearchFailure
argument_list|(
operator|new
name|TestQueryParsingException
argument_list|(
operator|new
name|Index
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"foobar"
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|new
name|SearchShardTarget
argument_list|(
literal|"node_1"
argument_list|,
literal|"foo"
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|ShardSearchFailure
name|failure1
init|=
operator|new
name|ShardSearchFailure
argument_list|(
operator|new
name|TestQueryParsingException
argument_list|(
operator|new
name|Index
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|"foobar"
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|new
name|SearchShardTarget
argument_list|(
literal|"node_1"
argument_list|,
literal|"foo"
argument_list|,
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|SearchPhaseExecutionException
name|ex
init|=
operator|new
name|SearchPhaseExecutionException
argument_list|(
literal|"search"
argument_list|,
literal|"all shards failed"
argument_list|,
operator|new
name|ShardSearchFailure
index|[]
block|{
name|failure
block|,
name|failure1
block|}
argument_list|)
decl_stmt|;
name|BytesRestResponse
name|response
init|=
operator|new
name|BytesRestResponse
argument_list|(
name|channel
argument_list|,
operator|new
name|RemoteTransportException
argument_list|(
literal|"foo"
argument_list|,
name|ex
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|response
operator|.
name|content
argument_list|()
operator|.
name|toUtf8
argument_list|()
decl_stmt|;
name|String
name|expected
init|=
literal|"{\"error\":{\"root_cause\":[{\"type\":\"test_query_parsing_exception\",\"reason\":\"foobar\",\"index\":\"foo\"}],\"type\":\"search_phase_execution_exception\",\"reason\":\"all shards failed\",\"phase\":\"search\",\"grouped\":true,\"failed_shards\":[{\"shard\":1,\"index\":\"foo\",\"node\":\"node_1\",\"reason\":{\"type\":\"test_query_parsing_exception\",\"reason\":\"foobar\",\"index\":\"foo\"}}]},\"status\":400}"
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|trim
argument_list|()
argument_list|,
name|text
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|WithHeadersException
specifier|public
specifier|static
class|class
name|WithHeadersException
extends|extends
name|ElasticsearchException
block|{
DECL|method|WithHeadersException
name|WithHeadersException
parameter_list|()
block|{
name|super
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|this
operator|.
name|addHeader
argument_list|(
literal|"n1"
argument_list|,
literal|"v11"
argument_list|,
literal|"v12"
argument_list|)
expr_stmt|;
name|this
operator|.
name|addHeader
argument_list|(
literal|"n2"
argument_list|,
literal|"v21"
argument_list|,
literal|"v22"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SimpleExceptionRestChannel
specifier|private
specifier|static
class|class
name|SimpleExceptionRestChannel
extends|extends
name|RestChannel
block|{
DECL|method|SimpleExceptionRestChannel
name|SimpleExceptionRestChannel
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendResponse
specifier|public
name|void
name|sendResponse
parameter_list|(
name|RestResponse
name|response
parameter_list|)
block|{         }
block|}
DECL|class|DetailedExceptionRestChannel
specifier|private
specifier|static
class|class
name|DetailedExceptionRestChannel
extends|extends
name|RestChannel
block|{
DECL|method|DetailedExceptionRestChannel
name|DetailedExceptionRestChannel
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendResponse
specifier|public
name|void
name|sendResponse
parameter_list|(
name|RestResponse
name|response
parameter_list|)
block|{         }
block|}
block|}
end_class

end_unit

