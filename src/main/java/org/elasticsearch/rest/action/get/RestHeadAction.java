begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.get
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|get
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
name|get
operator|.
name|GetRequest
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
name|get
operator|.
name|GetResponse
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
name|Strings
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|support
operator|.
name|RestResponseListener
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|HEAD
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
operator|.
name|NOT_FOUND
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
operator|.
name|OK
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RestHeadAction
specifier|public
class|class
name|RestHeadAction
extends|extends
name|BaseRestHandler
block|{
annotation|@
name|Inject
DECL|method|RestHeadAction
specifier|public
name|RestHeadAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
parameter_list|,
name|RestClientFactory
name|restClientFactory
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|restClientFactory
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|HEAD
argument_list|,
literal|"/{index}/{type}/{id}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|HEAD
argument_list|,
literal|"/{index}/{type}/{id}/_source"
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
specifier|final
name|RestRequest
name|request
parameter_list|,
specifier|final
name|RestChannel
name|channel
parameter_list|,
specifier|final
name|Client
name|client
parameter_list|)
block|{
specifier|final
name|GetRequest
name|getRequest
init|=
operator|new
name|GetRequest
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"index"
argument_list|)
argument_list|,
name|request
operator|.
name|param
argument_list|(
literal|"type"
argument_list|)
argument_list|,
name|request
operator|.
name|param
argument_list|(
literal|"id"
argument_list|)
argument_list|)
decl_stmt|;
name|getRequest
operator|.
name|listenerThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|getRequest
operator|.
name|operationThreaded
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|getRequest
operator|.
name|refresh
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"refresh"
argument_list|,
name|getRequest
operator|.
name|refresh
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|getRequest
operator|.
name|routing
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"routing"
argument_list|)
argument_list|)
expr_stmt|;
comment|// order is important, set it after routing, so it will set the routing
name|getRequest
operator|.
name|parent
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"parent"
argument_list|)
argument_list|)
expr_stmt|;
name|getRequest
operator|.
name|preference
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"preference"
argument_list|)
argument_list|)
expr_stmt|;
name|getRequest
operator|.
name|realtime
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"realtime"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// don't get any fields back...
name|getRequest
operator|.
name|fields
argument_list|(
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
comment|// TODO we can also just return the document size as Content-Length
name|client
operator|.
name|get
argument_list|(
name|getRequest
argument_list|,
operator|new
name|RestResponseListener
argument_list|<
name|GetResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|RestResponse
name|buildResponse
parameter_list|(
name|GetResponse
name|response
parameter_list|)
block|{
if|if
condition|(
operator|!
name|response
operator|.
name|isExists
argument_list|()
condition|)
block|{
return|return
operator|new
name|BytesRestResponse
argument_list|(
name|NOT_FOUND
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|BytesRestResponse
argument_list|(
name|OK
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

