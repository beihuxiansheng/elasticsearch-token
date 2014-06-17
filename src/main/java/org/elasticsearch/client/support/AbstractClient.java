begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|support
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
name|bench
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
name|bulk
operator|.
name|BulkAction
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
name|bulk
operator|.
name|BulkRequest
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
name|bulk
operator|.
name|BulkRequestBuilder
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
name|bulk
operator|.
name|BulkResponse
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
name|CountAction
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
name|CountRequestBuilder
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
name|delete
operator|.
name|DeleteAction
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
name|DeleteRequestBuilder
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
name|deletebyquery
operator|.
name|DeleteByQueryAction
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
name|DeleteByQueryRequestBuilder
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
name|explain
operator|.
name|ExplainAction
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
name|explain
operator|.
name|ExplainRequest
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
name|explain
operator|.
name|ExplainRequestBuilder
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
name|explain
operator|.
name|ExplainResponse
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
name|index
operator|.
name|IndexAction
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
name|mlt
operator|.
name|MoreLikeThisAction
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
name|MoreLikeThisRequestBuilder
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
name|percolate
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
name|suggest
operator|.
name|SuggestAction
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
name|suggest
operator|.
name|SuggestRequest
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
name|suggest
operator|.
name|SuggestRequestBuilder
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
name|suggest
operator|.
name|SuggestResponse
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
name|termvector
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
name|update
operator|.
name|UpdateAction
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
name|update
operator|.
name|UpdateRequest
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
name|update
operator|.
name|UpdateRequestBuilder
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
name|update
operator|.
name|UpdateResponse
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
name|Nullable
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AbstractClient
specifier|public
specifier|abstract
class|class
name|AbstractClient
implements|implements
name|Client
block|{
annotation|@
name|Override
DECL|method|prepareExecute
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
name|RequestBuilder
name|prepareExecute
parameter_list|(
specifier|final
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
parameter_list|)
block|{
return|return
name|action
operator|.
name|newRequestBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|index
specifier|public
name|ActionFuture
argument_list|<
name|IndexResponse
argument_list|>
name|index
parameter_list|(
specifier|final
name|IndexRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|IndexAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|index
specifier|public
name|void
name|index
parameter_list|(
specifier|final
name|IndexRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|IndexResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|IndexAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareIndex
specifier|public
name|IndexRequestBuilder
name|prepareIndex
parameter_list|()
block|{
return|return
operator|new
name|IndexRequestBuilder
argument_list|(
name|this
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|prepareIndex
specifier|public
name|IndexRequestBuilder
name|prepareIndex
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|)
block|{
return|return
name|prepareIndex
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|prepareIndex
specifier|public
name|IndexRequestBuilder
name|prepareIndex
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
annotation|@
name|Nullable
name|String
name|id
parameter_list|)
block|{
return|return
name|prepareIndex
argument_list|()
operator|.
name|setIndex
argument_list|(
name|index
argument_list|)
operator|.
name|setType
argument_list|(
name|type
argument_list|)
operator|.
name|setId
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|update
specifier|public
name|ActionFuture
argument_list|<
name|UpdateResponse
argument_list|>
name|update
parameter_list|(
specifier|final
name|UpdateRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|UpdateAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
specifier|final
name|UpdateRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|UpdateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|UpdateAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareUpdate
specifier|public
name|UpdateRequestBuilder
name|prepareUpdate
parameter_list|()
block|{
return|return
operator|new
name|UpdateRequestBuilder
argument_list|(
name|this
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|prepareUpdate
specifier|public
name|UpdateRequestBuilder
name|prepareUpdate
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
return|return
operator|new
name|UpdateRequestBuilder
argument_list|(
name|this
argument_list|,
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|ActionFuture
argument_list|<
name|DeleteResponse
argument_list|>
name|delete
parameter_list|(
specifier|final
name|DeleteRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|DeleteAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
specifier|final
name|DeleteRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|DeleteResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|DeleteAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareDelete
specifier|public
name|DeleteRequestBuilder
name|prepareDelete
parameter_list|()
block|{
return|return
operator|new
name|DeleteRequestBuilder
argument_list|(
name|this
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|prepareDelete
specifier|public
name|DeleteRequestBuilder
name|prepareDelete
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
return|return
name|prepareDelete
argument_list|()
operator|.
name|setIndex
argument_list|(
name|index
argument_list|)
operator|.
name|setType
argument_list|(
name|type
argument_list|)
operator|.
name|setId
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bulk
specifier|public
name|ActionFuture
argument_list|<
name|BulkResponse
argument_list|>
name|bulk
parameter_list|(
specifier|final
name|BulkRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|BulkAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bulk
specifier|public
name|void
name|bulk
parameter_list|(
specifier|final
name|BulkRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|BulkResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|BulkAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareBulk
specifier|public
name|BulkRequestBuilder
name|prepareBulk
parameter_list|()
block|{
return|return
operator|new
name|BulkRequestBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|deleteByQuery
specifier|public
name|ActionFuture
argument_list|<
name|DeleteByQueryResponse
argument_list|>
name|deleteByQuery
parameter_list|(
specifier|final
name|DeleteByQueryRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|DeleteByQueryAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|deleteByQuery
specifier|public
name|void
name|deleteByQuery
parameter_list|(
specifier|final
name|DeleteByQueryRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|DeleteByQueryResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|DeleteByQueryAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareDeleteByQuery
specifier|public
name|DeleteByQueryRequestBuilder
name|prepareDeleteByQuery
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|DeleteByQueryRequestBuilder
argument_list|(
name|this
argument_list|)
operator|.
name|setIndices
argument_list|(
name|indices
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|ActionFuture
argument_list|<
name|GetResponse
argument_list|>
name|get
parameter_list|(
specifier|final
name|GetRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|GetAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|void
name|get
parameter_list|(
specifier|final
name|GetRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|GetResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|GetAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareGet
specifier|public
name|GetRequestBuilder
name|prepareGet
parameter_list|()
block|{
return|return
operator|new
name|GetRequestBuilder
argument_list|(
name|this
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|prepareGet
specifier|public
name|GetRequestBuilder
name|prepareGet
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
return|return
name|prepareGet
argument_list|()
operator|.
name|setIndex
argument_list|(
name|index
argument_list|)
operator|.
name|setType
argument_list|(
name|type
argument_list|)
operator|.
name|setId
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|multiGet
specifier|public
name|ActionFuture
argument_list|<
name|MultiGetResponse
argument_list|>
name|multiGet
parameter_list|(
specifier|final
name|MultiGetRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|MultiGetAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|multiGet
specifier|public
name|void
name|multiGet
parameter_list|(
specifier|final
name|MultiGetRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|MultiGetResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|MultiGetAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareMultiGet
specifier|public
name|MultiGetRequestBuilder
name|prepareMultiGet
parameter_list|()
block|{
return|return
operator|new
name|MultiGetRequestBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|search
specifier|public
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|search
parameter_list|(
specifier|final
name|SearchRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|SearchAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
specifier|final
name|SearchRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|SearchAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareSearch
specifier|public
name|SearchRequestBuilder
name|prepareSearch
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|SearchRequestBuilder
argument_list|(
name|this
argument_list|)
operator|.
name|setIndices
argument_list|(
name|indices
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|searchScroll
specifier|public
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|searchScroll
parameter_list|(
specifier|final
name|SearchScrollRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|SearchScrollAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|searchScroll
specifier|public
name|void
name|searchScroll
parameter_list|(
specifier|final
name|SearchScrollRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|SearchScrollAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareSearchScroll
specifier|public
name|SearchScrollRequestBuilder
name|prepareSearchScroll
parameter_list|(
name|String
name|scrollId
parameter_list|)
block|{
return|return
operator|new
name|SearchScrollRequestBuilder
argument_list|(
name|this
argument_list|,
name|scrollId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|multiSearch
specifier|public
name|ActionFuture
argument_list|<
name|MultiSearchResponse
argument_list|>
name|multiSearch
parameter_list|(
name|MultiSearchRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|MultiSearchAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|multiSearch
specifier|public
name|void
name|multiSearch
parameter_list|(
name|MultiSearchRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|MultiSearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|MultiSearchAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareMultiSearch
specifier|public
name|MultiSearchRequestBuilder
name|prepareMultiSearch
parameter_list|()
block|{
return|return
operator|new
name|MultiSearchRequestBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|count
specifier|public
name|ActionFuture
argument_list|<
name|CountResponse
argument_list|>
name|count
parameter_list|(
specifier|final
name|CountRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|CountAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|count
specifier|public
name|void
name|count
parameter_list|(
specifier|final
name|CountRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|CountResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|CountAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareCount
specifier|public
name|CountRequestBuilder
name|prepareCount
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|CountRequestBuilder
argument_list|(
name|this
argument_list|)
operator|.
name|setIndices
argument_list|(
name|indices
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|suggest
specifier|public
name|ActionFuture
argument_list|<
name|SuggestResponse
argument_list|>
name|suggest
parameter_list|(
specifier|final
name|SuggestRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|SuggestAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|suggest
specifier|public
name|void
name|suggest
parameter_list|(
specifier|final
name|SuggestRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|SuggestResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|SuggestAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareSuggest
specifier|public
name|SuggestRequestBuilder
name|prepareSuggest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|SuggestRequestBuilder
argument_list|(
name|this
argument_list|)
operator|.
name|setIndices
argument_list|(
name|indices
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|moreLikeThis
specifier|public
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|moreLikeThis
parameter_list|(
specifier|final
name|MoreLikeThisRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|MoreLikeThisAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|moreLikeThis
specifier|public
name|void
name|moreLikeThis
parameter_list|(
specifier|final
name|MoreLikeThisRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|MoreLikeThisAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareMoreLikeThis
specifier|public
name|MoreLikeThisRequestBuilder
name|prepareMoreLikeThis
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
return|return
operator|new
name|MoreLikeThisRequestBuilder
argument_list|(
name|this
argument_list|,
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|termVector
specifier|public
name|ActionFuture
argument_list|<
name|TermVectorResponse
argument_list|>
name|termVector
parameter_list|(
specifier|final
name|TermVectorRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|TermVectorAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|termVector
specifier|public
name|void
name|termVector
parameter_list|(
specifier|final
name|TermVectorRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|TermVectorResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|TermVectorAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareTermVector
specifier|public
name|TermVectorRequestBuilder
name|prepareTermVector
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
return|return
operator|new
name|TermVectorRequestBuilder
argument_list|(
name|this
argument_list|,
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|multiTermVectors
specifier|public
name|ActionFuture
argument_list|<
name|MultiTermVectorsResponse
argument_list|>
name|multiTermVectors
parameter_list|(
specifier|final
name|MultiTermVectorsRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|MultiTermVectorsAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|multiTermVectors
specifier|public
name|void
name|multiTermVectors
parameter_list|(
specifier|final
name|MultiTermVectorsRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|MultiTermVectorsResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|MultiTermVectorsAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareMultiTermVectors
specifier|public
name|MultiTermVectorsRequestBuilder
name|prepareMultiTermVectors
parameter_list|()
block|{
return|return
operator|new
name|MultiTermVectorsRequestBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|percolate
specifier|public
name|ActionFuture
argument_list|<
name|PercolateResponse
argument_list|>
name|percolate
parameter_list|(
specifier|final
name|PercolateRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|PercolateAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|percolate
specifier|public
name|void
name|percolate
parameter_list|(
specifier|final
name|PercolateRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|PercolateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|PercolateAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|preparePercolate
specifier|public
name|PercolateRequestBuilder
name|preparePercolate
parameter_list|()
block|{
return|return
operator|new
name|PercolateRequestBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|prepareMultiPercolate
specifier|public
name|MultiPercolateRequestBuilder
name|prepareMultiPercolate
parameter_list|()
block|{
return|return
operator|new
name|MultiPercolateRequestBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|multiPercolate
specifier|public
name|void
name|multiPercolate
parameter_list|(
name|MultiPercolateRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|MultiPercolateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|MultiPercolateAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|multiPercolate
specifier|public
name|ActionFuture
argument_list|<
name|MultiPercolateResponse
argument_list|>
name|multiPercolate
parameter_list|(
name|MultiPercolateRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|MultiPercolateAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|prepareExplain
specifier|public
name|ExplainRequestBuilder
name|prepareExplain
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
return|return
operator|new
name|ExplainRequestBuilder
argument_list|(
name|this
argument_list|,
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|ActionFuture
argument_list|<
name|ExplainResponse
argument_list|>
name|explain
parameter_list|(
name|ExplainRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|ExplainAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|void
name|explain
parameter_list|(
name|ExplainRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ExplainResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|ExplainAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clearScroll
specifier|public
name|void
name|clearScroll
parameter_list|(
name|ClearScrollRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ClearScrollResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|ClearScrollAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clearScroll
specifier|public
name|ActionFuture
argument_list|<
name|ClearScrollResponse
argument_list|>
name|clearScroll
parameter_list|(
name|ClearScrollRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|ClearScrollAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|prepareClearScroll
specifier|public
name|ClearScrollRequestBuilder
name|prepareClearScroll
parameter_list|()
block|{
return|return
operator|new
name|ClearScrollRequestBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bench
specifier|public
name|void
name|bench
parameter_list|(
name|BenchmarkRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|BenchmarkResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|BenchmarkAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bench
specifier|public
name|ActionFuture
argument_list|<
name|BenchmarkResponse
argument_list|>
name|bench
parameter_list|(
name|BenchmarkRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|BenchmarkAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|prepareBench
specifier|public
name|BenchmarkRequestBuilder
name|prepareBench
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|BenchmarkRequestBuilder
argument_list|(
name|this
argument_list|,
name|indices
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|abortBench
specifier|public
name|void
name|abortBench
parameter_list|(
name|AbortBenchmarkRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|AbortBenchmarkResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|AbortBenchmarkAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareAbortBench
specifier|public
name|AbortBenchmarkRequestBuilder
name|prepareAbortBench
parameter_list|(
name|String
modifier|...
name|benchmarkNames
parameter_list|)
block|{
return|return
operator|new
name|AbortBenchmarkRequestBuilder
argument_list|(
name|this
argument_list|)
operator|.
name|setBenchmarkNames
argument_list|(
name|benchmarkNames
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|benchStatus
specifier|public
name|void
name|benchStatus
parameter_list|(
name|BenchmarkStatusRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|BenchmarkStatusResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|execute
argument_list|(
name|BenchmarkStatusAction
operator|.
name|INSTANCE
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareBenchStatus
specifier|public
name|BenchmarkStatusRequestBuilder
name|prepareBenchStatus
parameter_list|()
block|{
return|return
operator|new
name|BenchmarkStatusRequestBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

