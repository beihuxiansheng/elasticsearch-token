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
name|test
operator|.
name|ElasticsearchTestCase
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
name|ElasticsearchTestCase
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
name|ExceptionWithHeaders
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
literal|"ElasticsearchException[an error occurred reading data]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|containsString
argument_list|(
literal|"FileNotFoundException[/foo/bar]"
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
literal|"\"error\":\"Throwable[an error occurred reading data]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|text
argument_list|,
name|containsString
argument_list|(
literal|"FileNotFoundException[/foo/bar]"
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
literal|"\"error\":\"Unknown\""
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
DECL|class|ExceptionWithHeaders
specifier|private
specifier|static
class|class
name|ExceptionWithHeaders
extends|extends
name|ElasticsearchException
operator|.
name|WithRestHeaders
block|{
DECL|method|ExceptionWithHeaders
name|ExceptionWithHeaders
parameter_list|()
block|{
name|super
argument_list|(
literal|""
argument_list|,
name|header
argument_list|(
literal|"n1"
argument_list|,
literal|"v11"
argument_list|,
literal|"v12"
argument_list|)
argument_list|,
name|header
argument_list|(
literal|"n2"
argument_list|,
literal|"v21"
argument_list|,
literal|"v22"
argument_list|)
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

