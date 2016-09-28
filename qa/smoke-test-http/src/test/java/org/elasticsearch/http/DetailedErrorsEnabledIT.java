begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.http
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|http
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|util
operator|.
name|EntityUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|ResponseException
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_comment
comment|/**  * Tests that by default the error_trace parameter can be used to show stacktraces  */
end_comment

begin_class
DECL|class|DetailedErrorsEnabledIT
specifier|public
class|class
name|DetailedErrorsEnabledIT
extends|extends
name|HttpSmokeTestCase
block|{
DECL|method|testThatErrorTraceWorksByDefault
specifier|public
name|void
name|testThatErrorTraceWorksByDefault
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|getRestClient
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"DELETE"
argument_list|,
literal|"/"
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"error_trace"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"request should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResponseException
name|e
parameter_list|)
block|{
name|Response
name|response
init|=
name|e
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHeader
argument_list|(
literal|"Content-Type"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"application/json"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|EntityUtils
operator|.
name|toString
argument_list|(
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"\"stack_trace\":\"[Validation Failed: 1: index / indices is missing;]; "
operator|+
literal|"nested: ActionRequestValidationException[Validation Failed: 1:"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|getRestClient
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"DELETE"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"request should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResponseException
name|e
parameter_list|)
block|{
name|Response
name|response
init|=
name|e
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHeader
argument_list|(
literal|"Content-Type"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"application/json"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|EntityUtils
operator|.
name|toString
argument_list|(
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"\"stack_trace\":\"[Validation Failed: 1: index / indices is missing;]; "
operator|+
literal|"nested: ActionRequestValidationException[Validation Failed: 1:"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
