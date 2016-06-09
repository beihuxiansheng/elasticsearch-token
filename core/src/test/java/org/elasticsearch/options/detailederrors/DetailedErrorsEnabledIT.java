begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.options.detailederrors
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|options
operator|.
name|detailederrors
package|;
end_package

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
name|common
operator|.
name|network
operator|.
name|NetworkModule
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
name|ESIntegTestCase
operator|.
name|ClusterScope
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
operator|.
name|Scope
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
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|Scope
operator|.
name|TEST
argument_list|,
name|supportsDedicatedMasters
operator|=
literal|false
argument_list|,
name|numDataNodes
operator|=
literal|1
argument_list|)
DECL|class|DetailedErrorsEnabledIT
specifier|public
class|class
name|DetailedErrorsEnabledIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|nodeSettings
specifier|protected
name|Settings
name|nodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|super
operator|.
name|nodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|NetworkModule
operator|.
name|HTTP_ENABLED
operator|.
name|getKey
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|testThatErrorTraceWorksByDefault
specifier|public
name|void
name|testThatErrorTraceWorksByDefault
parameter_list|()
throws|throws
name|Exception
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
argument_list|,
literal|null
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
name|e
operator|.
name|getResponseBody
argument_list|()
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
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
literal|null
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
name|e
operator|.
name|getResponseBody
argument_list|()
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

