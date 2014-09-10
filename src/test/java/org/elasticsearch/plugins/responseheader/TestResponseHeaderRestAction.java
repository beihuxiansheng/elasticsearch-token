begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugins.responseheader
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|responseheader
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
name|Client
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
name|inject
operator|.
name|Inject
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
name|rest
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestResponseHeaderRestAction
specifier|public
class|class
name|TestResponseHeaderRestAction
extends|extends
name|BaseRestHandler
block|{
annotation|@
name|Inject
DECL|method|TestResponseHeaderRestAction
specifier|public
name|TestResponseHeaderRestAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
parameter_list|,
name|Client
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|controller
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|GET
argument_list|,
literal|"/_protected"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|,
name|Client
name|client
parameter_list|)
block|{
if|if
condition|(
literal|"password"
operator|.
name|equals
argument_list|(
name|request
operator|.
name|header
argument_list|(
literal|"Secret"
argument_list|)
argument_list|)
condition|)
block|{
name|RestResponse
name|response
init|=
operator|new
name|BytesRestResponse
argument_list|(
name|RestStatus
operator|.
name|OK
argument_list|,
literal|"Access granted"
argument_list|)
decl_stmt|;
name|response
operator|.
name|addHeader
argument_list|(
literal|"Secret"
argument_list|,
literal|"granted"
argument_list|)
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|RestResponse
name|response
init|=
operator|new
name|BytesRestResponse
argument_list|(
name|RestStatus
operator|.
name|UNAUTHORIZED
argument_list|,
literal|"Access denied"
argument_list|)
decl_stmt|;
name|response
operator|.
name|addHeader
argument_list|(
literal|"Secret"
argument_list|,
literal|"required"
argument_list|)
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

