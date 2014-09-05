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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|*
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
name|client
operator|.
name|ClusterAdminClient
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
name|FilterClient
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
name|IndicesAdminClient
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
name|component
operator|.
name|AbstractComponent
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
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Base handler for REST requests.  *  * This handler makes sure that the headers& context of the handled {@link RestRequest requests} are copied over to  * the transport requests executed by the associated client. While the context is fully copied over, not all the headers  * are copied, but a selected few. It is possible to control what header are copied over by registering them using  * {@link #addUsefulHeaders(String...)}  */
end_comment

begin_class
DECL|class|BaseRestHandler
specifier|public
specifier|abstract
class|class
name|BaseRestHandler
extends|extends
name|AbstractComponent
implements|implements
name|RestHandler
block|{
DECL|field|usefulHeaders
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|usefulHeaders
init|=
name|Sets
operator|.
name|newCopyOnWriteArraySet
argument_list|()
decl_stmt|;
comment|/**      * Controls which REST headers get copied over from a {@link org.elasticsearch.rest.RestRequest} to      * its corresponding {@link org.elasticsearch.transport.TransportRequest}(s).      *      * By default no headers get copied but it is possible to extend this behaviour via plugins by calling this method.      */
DECL|method|addUsefulHeaders
specifier|public
specifier|static
name|void
name|addUsefulHeaders
parameter_list|(
name|String
modifier|...
name|headers
parameter_list|)
block|{
name|Collections
operator|.
name|addAll
argument_list|(
name|usefulHeaders
argument_list|,
name|headers
argument_list|)
expr_stmt|;
block|}
DECL|method|usefulHeaders
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|usefulHeaders
parameter_list|()
block|{
return|return
name|usefulHeaders
return|;
block|}
DECL|field|client
specifier|private
specifier|final
name|Client
name|client
decl_stmt|;
DECL|method|BaseRestHandler
specifier|protected
name|BaseRestHandler
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Client
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleRequest
specifier|public
specifier|final
name|void
name|handleRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|handleRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|usefulHeaders
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|?
name|client
else|:
operator|new
name|HeadersAndContextCopyClient
argument_list|(
name|client
argument_list|,
name|request
argument_list|,
name|usefulHeaders
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|handleRequest
specifier|protected
specifier|abstract
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
throws|throws
name|Exception
function_decl|;
DECL|class|HeadersAndContextCopyClient
specifier|static
specifier|final
class|class
name|HeadersAndContextCopyClient
extends|extends
name|FilterClient
block|{
DECL|field|restRequest
specifier|private
specifier|final
name|RestRequest
name|restRequest
decl_stmt|;
DECL|field|indicesAdmin
specifier|private
specifier|final
name|IndicesAdmin
name|indicesAdmin
decl_stmt|;
DECL|field|clusterAdmin
specifier|private
specifier|final
name|ClusterAdmin
name|clusterAdmin
decl_stmt|;
DECL|field|headers
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|headers
decl_stmt|;
DECL|method|HeadersAndContextCopyClient
name|HeadersAndContextCopyClient
parameter_list|(
name|Client
name|in
parameter_list|,
name|RestRequest
name|restRequest
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|headers
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|restRequest
operator|=
name|restRequest
expr_stmt|;
name|this
operator|.
name|indicesAdmin
operator|=
operator|new
name|IndicesAdmin
argument_list|(
name|in
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
argument_list|,
name|restRequest
argument_list|,
name|headers
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterAdmin
operator|=
operator|new
name|ClusterAdmin
argument_list|(
name|in
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
argument_list|,
name|restRequest
argument_list|,
name|headers
argument_list|)
expr_stmt|;
name|this
operator|.
name|headers
operator|=
name|headers
expr_stmt|;
block|}
DECL|method|copyHeadersAndContext
specifier|private
specifier|static
name|void
name|copyHeadersAndContext
parameter_list|(
name|ActionRequest
name|actionRequest
parameter_list|,
name|RestRequest
name|restRequest
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|headers
parameter_list|)
block|{
for|for
control|(
name|String
name|usefulHeader
range|:
name|headers
control|)
block|{
name|String
name|headerValue
init|=
name|restRequest
operator|.
name|header
argument_list|(
name|usefulHeader
argument_list|)
decl_stmt|;
if|if
condition|(
name|headerValue
operator|!=
literal|null
condition|)
block|{
name|actionRequest
operator|.
name|putHeader
argument_list|(
name|usefulHeader
argument_list|,
name|headerValue
argument_list|)
expr_stmt|;
block|}
block|}
name|actionRequest
operator|.
name|copyContextFrom
argument_list|(
name|restRequest
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|,
name|RequestBuilder
extends|extends
name|ActionRequestBuilder
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|,
name|Client
argument_list|>
parameter_list|>
name|ActionFuture
argument_list|<
name|Response
argument_list|>
name|execute
parameter_list|(
name|Action
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|,
name|Client
argument_list|>
name|action
parameter_list|,
name|Request
name|request
parameter_list|)
block|{
name|copyHeadersAndContext
argument_list|(
name|request
argument_list|,
name|restRequest
argument_list|,
name|headers
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|execute
argument_list|(
name|action
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|,
name|RequestBuilder
extends|extends
name|ActionRequestBuilder
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|,
name|Client
argument_list|>
parameter_list|>
name|void
name|execute
parameter_list|(
name|Action
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|,
name|Client
argument_list|>
name|action
parameter_list|,
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
name|copyHeadersAndContext
argument_list|(
name|request
argument_list|,
name|restRequest
argument_list|,
name|headers
argument_list|)
expr_stmt|;
name|super
operator|.
name|execute
argument_list|(
name|action
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cluster
specifier|public
name|ClusterAdminClient
name|cluster
parameter_list|()
block|{
return|return
name|clusterAdmin
return|;
block|}
annotation|@
name|Override
DECL|method|indices
specifier|public
name|IndicesAdminClient
name|indices
parameter_list|()
block|{
return|return
name|indicesAdmin
return|;
block|}
DECL|class|ClusterAdmin
specifier|private
specifier|static
specifier|final
class|class
name|ClusterAdmin
extends|extends
name|FilterClient
operator|.
name|ClusterAdmin
block|{
DECL|field|restRequest
specifier|private
specifier|final
name|RestRequest
name|restRequest
decl_stmt|;
DECL|field|headers
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|headers
decl_stmt|;
DECL|method|ClusterAdmin
specifier|private
name|ClusterAdmin
parameter_list|(
name|ClusterAdminClient
name|in
parameter_list|,
name|RestRequest
name|restRequest
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|headers
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|restRequest
operator|=
name|restRequest
expr_stmt|;
name|this
operator|.
name|headers
operator|=
name|headers
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|,
name|RequestBuilder
extends|extends
name|ActionRequestBuilder
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|,
name|ClusterAdminClient
argument_list|>
parameter_list|>
name|ActionFuture
argument_list|<
name|Response
argument_list|>
name|execute
parameter_list|(
name|Action
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|,
name|ClusterAdminClient
argument_list|>
name|action
parameter_list|,
name|Request
name|request
parameter_list|)
block|{
name|copyHeadersAndContext
argument_list|(
name|request
argument_list|,
name|restRequest
argument_list|,
name|headers
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|execute
argument_list|(
name|action
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|,
name|RequestBuilder
extends|extends
name|ActionRequestBuilder
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|,
name|ClusterAdminClient
argument_list|>
parameter_list|>
name|void
name|execute
parameter_list|(
name|Action
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|,
name|ClusterAdminClient
argument_list|>
name|action
parameter_list|,
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
name|copyHeadersAndContext
argument_list|(
name|request
argument_list|,
name|restRequest
argument_list|,
name|headers
argument_list|)
expr_stmt|;
name|super
operator|.
name|execute
argument_list|(
name|action
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|IndicesAdmin
specifier|private
specifier|final
class|class
name|IndicesAdmin
extends|extends
name|FilterClient
operator|.
name|IndicesAdmin
block|{
DECL|field|restRequest
specifier|private
specifier|final
name|RestRequest
name|restRequest
decl_stmt|;
DECL|field|headers
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|headers
decl_stmt|;
DECL|method|IndicesAdmin
specifier|private
name|IndicesAdmin
parameter_list|(
name|IndicesAdminClient
name|in
parameter_list|,
name|RestRequest
name|restRequest
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|headers
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|restRequest
operator|=
name|restRequest
expr_stmt|;
name|this
operator|.
name|headers
operator|=
name|headers
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|,
name|RequestBuilder
extends|extends
name|ActionRequestBuilder
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|,
name|IndicesAdminClient
argument_list|>
parameter_list|>
name|ActionFuture
argument_list|<
name|Response
argument_list|>
name|execute
parameter_list|(
name|Action
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|,
name|IndicesAdminClient
argument_list|>
name|action
parameter_list|,
name|Request
name|request
parameter_list|)
block|{
name|copyHeadersAndContext
argument_list|(
name|request
argument_list|,
name|restRequest
argument_list|,
name|headers
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|execute
argument_list|(
name|action
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|,
name|RequestBuilder
extends|extends
name|ActionRequestBuilder
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|,
name|IndicesAdminClient
argument_list|>
parameter_list|>
name|void
name|execute
parameter_list|(
name|Action
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|,
name|IndicesAdminClient
argument_list|>
name|action
parameter_list|,
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
name|copyHeadersAndContext
argument_list|(
name|request
argument_list|,
name|restRequest
argument_list|,
name|headers
argument_list|)
expr_stmt|;
name|super
operator|.
name|execute
argument_list|(
name|action
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

