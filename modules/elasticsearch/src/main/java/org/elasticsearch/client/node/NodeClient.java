begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.node
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|node
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
name|ActionFuture
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
name|ActionListener
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
name|count
operator|.
name|CountRequest
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
name|count
operator|.
name|CountResponse
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
name|count
operator|.
name|TransportCountAction
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
name|delete
operator|.
name|DeleteRequest
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
name|delete
operator|.
name|DeleteResponse
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
name|delete
operator|.
name|TransportDeleteAction
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
name|deletebyquery
operator|.
name|DeleteByQueryRequest
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
name|deletebyquery
operator|.
name|DeleteByQueryResponse
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
name|deletebyquery
operator|.
name|TransportDeleteByQueryAction
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
name|action
operator|.
name|get
operator|.
name|TransportGetAction
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
name|index
operator|.
name|IndexRequest
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
name|index
operator|.
name|IndexResponse
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
name|index
operator|.
name|TransportIndexAction
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
name|mlt
operator|.
name|MoreLikeThisRequest
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
name|mlt
operator|.
name|TransportMoreLikeThisAction
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
name|*
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
name|terms
operator|.
name|TermsRequest
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
name|terms
operator|.
name|TermsResponse
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
name|terms
operator|.
name|TransportTermsAction
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
name|AdminClient
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
name|internal
operator|.
name|InternalClient
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
name|support
operator|.
name|AbstractClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|NodeClient
specifier|public
class|class
name|NodeClient
extends|extends
name|AbstractClient
implements|implements
name|InternalClient
block|{
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|admin
specifier|private
specifier|final
name|NodeAdminClient
name|admin
decl_stmt|;
DECL|field|indexAction
specifier|private
specifier|final
name|TransportIndexAction
name|indexAction
decl_stmt|;
DECL|field|deleteAction
specifier|private
specifier|final
name|TransportDeleteAction
name|deleteAction
decl_stmt|;
DECL|field|deleteByQueryAction
specifier|private
specifier|final
name|TransportDeleteByQueryAction
name|deleteByQueryAction
decl_stmt|;
DECL|field|getAction
specifier|private
specifier|final
name|TransportGetAction
name|getAction
decl_stmt|;
DECL|field|countAction
specifier|private
specifier|final
name|TransportCountAction
name|countAction
decl_stmt|;
DECL|field|searchAction
specifier|private
specifier|final
name|TransportSearchAction
name|searchAction
decl_stmt|;
DECL|field|searchScrollAction
specifier|private
specifier|final
name|TransportSearchScrollAction
name|searchScrollAction
decl_stmt|;
DECL|field|termsAction
specifier|private
specifier|final
name|TransportTermsAction
name|termsAction
decl_stmt|;
DECL|field|moreLikeThisAction
specifier|private
specifier|final
name|TransportMoreLikeThisAction
name|moreLikeThisAction
decl_stmt|;
DECL|method|NodeClient
annotation|@
name|Inject
specifier|public
name|NodeClient
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|NodeAdminClient
name|admin
parameter_list|,
name|TransportIndexAction
name|indexAction
parameter_list|,
name|TransportDeleteAction
name|deleteAction
parameter_list|,
name|TransportDeleteByQueryAction
name|deleteByQueryAction
parameter_list|,
name|TransportGetAction
name|getAction
parameter_list|,
name|TransportCountAction
name|countAction
parameter_list|,
name|TransportSearchAction
name|searchAction
parameter_list|,
name|TransportSearchScrollAction
name|searchScrollAction
parameter_list|,
name|TransportTermsAction
name|termsAction
parameter_list|,
name|TransportMoreLikeThisAction
name|moreLikeThisAction
parameter_list|)
block|{
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|admin
operator|=
name|admin
expr_stmt|;
name|this
operator|.
name|indexAction
operator|=
name|indexAction
expr_stmt|;
name|this
operator|.
name|deleteAction
operator|=
name|deleteAction
expr_stmt|;
name|this
operator|.
name|deleteByQueryAction
operator|=
name|deleteByQueryAction
expr_stmt|;
name|this
operator|.
name|getAction
operator|=
name|getAction
expr_stmt|;
name|this
operator|.
name|countAction
operator|=
name|countAction
expr_stmt|;
name|this
operator|.
name|searchAction
operator|=
name|searchAction
expr_stmt|;
name|this
operator|.
name|searchScrollAction
operator|=
name|searchScrollAction
expr_stmt|;
name|this
operator|.
name|termsAction
operator|=
name|termsAction
expr_stmt|;
name|this
operator|.
name|moreLikeThisAction
operator|=
name|moreLikeThisAction
expr_stmt|;
block|}
DECL|method|threadPool
annotation|@
name|Override
specifier|public
name|ThreadPool
name|threadPool
parameter_list|()
block|{
return|return
name|this
operator|.
name|threadPool
return|;
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// nothing really to do
block|}
DECL|method|admin
annotation|@
name|Override
specifier|public
name|AdminClient
name|admin
parameter_list|()
block|{
return|return
name|this
operator|.
name|admin
return|;
block|}
DECL|method|index
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|IndexResponse
argument_list|>
name|index
parameter_list|(
name|IndexRequest
name|request
parameter_list|)
block|{
return|return
name|indexAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|index
annotation|@
name|Override
specifier|public
name|void
name|index
parameter_list|(
name|IndexRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|IndexResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|indexAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|delete
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|DeleteResponse
argument_list|>
name|delete
parameter_list|(
name|DeleteRequest
name|request
parameter_list|)
block|{
return|return
name|deleteAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|delete
annotation|@
name|Override
specifier|public
name|void
name|delete
parameter_list|(
name|DeleteRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|DeleteResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|deleteAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteByQuery
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|DeleteByQueryResponse
argument_list|>
name|deleteByQuery
parameter_list|(
name|DeleteByQueryRequest
name|request
parameter_list|)
block|{
return|return
name|deleteByQueryAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|deleteByQuery
annotation|@
name|Override
specifier|public
name|void
name|deleteByQuery
parameter_list|(
name|DeleteByQueryRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|DeleteByQueryResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|deleteByQueryAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|get
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|GetResponse
argument_list|>
name|get
parameter_list|(
name|GetRequest
name|request
parameter_list|)
block|{
return|return
name|getAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|get
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|GetRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|GetResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|getAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|count
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|CountResponse
argument_list|>
name|count
parameter_list|(
name|CountRequest
name|request
parameter_list|)
block|{
return|return
name|countAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|count
annotation|@
name|Override
specifier|public
name|void
name|count
parameter_list|(
name|CountRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|CountResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|countAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|search
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|search
parameter_list|(
name|SearchRequest
name|request
parameter_list|)
block|{
return|return
name|searchAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|search
annotation|@
name|Override
specifier|public
name|void
name|search
parameter_list|(
name|SearchRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|searchAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|searchScroll
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|searchScroll
parameter_list|(
name|SearchScrollRequest
name|request
parameter_list|)
block|{
return|return
name|searchScrollAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|searchScroll
annotation|@
name|Override
specifier|public
name|void
name|searchScroll
parameter_list|(
name|SearchScrollRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|searchScrollAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|terms
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|TermsResponse
argument_list|>
name|terms
parameter_list|(
name|TermsRequest
name|request
parameter_list|)
block|{
return|return
name|termsAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|terms
annotation|@
name|Override
specifier|public
name|void
name|terms
parameter_list|(
name|TermsRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|TermsResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|termsAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|moreLikeThis
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|moreLikeThis
parameter_list|(
name|MoreLikeThisRequest
name|request
parameter_list|)
block|{
return|return
name|moreLikeThisAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|moreLikeThis
annotation|@
name|Override
specifier|public
name|void
name|moreLikeThis
parameter_list|(
name|MoreLikeThisRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|moreLikeThisAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

