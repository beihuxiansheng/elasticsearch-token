begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.mlt
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|mlt
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
name|com
operator|.
name|google
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
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Fieldable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|TransportActions
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
name|search
operator|.
name|SearchRequest
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
name|action
operator|.
name|search
operator|.
name|TransportSearchAction
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
name|support
operator|.
name|BaseAction
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
name|mapper
operator|.
name|DocumentMapper
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
name|mapper
operator|.
name|FieldMapper
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
name|mapper
operator|.
name|FieldMappers
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
name|mapper
operator|.
name|InternalMapper
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
name|json
operator|.
name|BoolJsonQueryBuilder
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
name|json
operator|.
name|MoreLikeThisFieldJsonQueryBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndicesService
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
name|BaseTransportRequestHandler
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
name|TransportChannel
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
name|TransportService
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Requests
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|json
operator|.
name|JsonQueryBuilders
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|builder
operator|.
name|SearchSourceBuilder
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TransportMoreLikeThisAction
specifier|public
class|class
name|TransportMoreLikeThisAction
extends|extends
name|BaseAction
argument_list|<
name|MoreLikeThisRequest
argument_list|,
name|SearchResponse
argument_list|>
block|{
DECL|field|searchAction
specifier|private
specifier|final
name|TransportSearchAction
name|searchAction
decl_stmt|;
DECL|field|getAction
specifier|private
specifier|final
name|TransportGetAction
name|getAction
decl_stmt|;
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
DECL|method|TransportMoreLikeThisAction
annotation|@
name|Inject
specifier|public
name|TransportMoreLikeThisAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportSearchAction
name|searchAction
parameter_list|,
name|TransportGetAction
name|getAction
parameter_list|,
name|IndicesService
name|indicesService
parameter_list|,
name|TransportService
name|transportService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|searchAction
operator|=
name|searchAction
expr_stmt|;
name|this
operator|.
name|getAction
operator|=
name|getAction
expr_stmt|;
name|this
operator|.
name|indicesService
operator|=
name|indicesService
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|TransportActions
operator|.
name|MORE_LIKE_THIS
argument_list|,
operator|new
name|TransportHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doExecute
annotation|@
name|Override
specifier|protected
name|void
name|doExecute
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
name|GetRequest
name|getRequest
init|=
name|getRequest
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|type
argument_list|(
name|request
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|id
argument_list|(
name|request
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|listenerThreaded
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|getAction
operator|.
name|execute
argument_list|(
name|getRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|GetResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|GetResponse
name|getResponse
parameter_list|)
block|{
if|if
condition|(
name|getResponse
operator|.
name|empty
argument_list|()
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|ElasticSearchException
argument_list|(
literal|"document missing"
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|BoolJsonQueryBuilder
name|boolBuilder
init|=
name|boolQuery
argument_list|()
decl_stmt|;
try|try
block|{
name|DocumentMapper
name|docMapper
init|=
name|indicesService
operator|.
name|indexServiceSafe
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|request
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|fields
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|field
range|:
name|request
operator|.
name|fields
argument_list|()
control|)
block|{
name|FieldMappers
name|fieldMappers
init|=
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartName
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldMappers
operator|!=
literal|null
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|fieldMappers
operator|.
name|mapper
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|docMapper
operator|.
name|parse
argument_list|(
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|id
argument_list|()
argument_list|,
name|getResponse
operator|.
name|source
argument_list|()
argument_list|,
operator|new
name|DocumentMapper
operator|.
name|ParseListenerAdapter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|beforeFieldAdded
parameter_list|(
name|FieldMapper
name|fieldMapper
parameter_list|,
name|Fieldable
name|field
parameter_list|,
name|Object
name|parseContext
parameter_list|)
block|{
if|if
condition|(
name|fieldMapper
operator|instanceof
name|InternalMapper
condition|)
block|{
return|return
literal|true
return|;
block|}
name|String
name|value
init|=
name|fieldMapper
operator|.
name|valueAsString
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|fields
operator|.
name|isEmpty
argument_list|()
operator|||
name|fields
operator|.
name|contains
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|addMoreLikeThis
argument_list|(
name|request
argument_list|,
name|boolBuilder
argument_list|,
name|fieldMapper
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// exclude myself
name|Term
name|uidTerm
init|=
name|docMapper
operator|.
name|uidMapper
argument_list|()
operator|.
name|term
argument_list|(
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
name|boolBuilder
operator|.
name|mustNot
argument_list|(
name|termQuery
argument_list|(
name|uidTerm
operator|.
name|field
argument_list|()
argument_list|,
name|uidTerm
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|SearchRequest
name|searchRequest
init|=
name|searchRequest
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|types
argument_list|(
name|request
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|listenerThreaded
argument_list|(
name|request
operator|.
name|listenerThreaded
argument_list|()
argument_list|)
operator|.
name|source
argument_list|(
name|searchSource
argument_list|()
operator|.
name|query
argument_list|(
name|boolBuilder
argument_list|)
argument_list|)
decl_stmt|;
name|searchAction
operator|.
name|execute
argument_list|(
name|searchRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|SearchResponse
name|response
parameter_list|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|addMoreLikeThis
specifier|private
name|void
name|addMoreLikeThis
parameter_list|(
name|MoreLikeThisRequest
name|request
parameter_list|,
name|BoolJsonQueryBuilder
name|boolBuilder
parameter_list|,
name|FieldMapper
name|fieldMapper
parameter_list|,
name|Fieldable
name|field
parameter_list|)
block|{
name|MoreLikeThisFieldJsonQueryBuilder
name|mlt
init|=
name|moreLikeThisFieldQuery
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|likeText
argument_list|(
name|fieldMapper
operator|.
name|valueAsString
argument_list|(
name|field
argument_list|)
argument_list|)
operator|.
name|percentTermsToMatch
argument_list|(
name|request
operator|.
name|percentTermsToMatch
argument_list|()
argument_list|)
operator|.
name|boostTerms
argument_list|(
name|request
operator|.
name|boostTerms
argument_list|()
argument_list|)
operator|.
name|boostTermsFactor
argument_list|(
name|request
operator|.
name|boostTermsFactor
argument_list|()
argument_list|)
operator|.
name|minDocFreq
argument_list|(
name|request
operator|.
name|minDocFreq
argument_list|()
argument_list|)
operator|.
name|maxDocFreq
argument_list|(
name|request
operator|.
name|maxDocFreq
argument_list|()
argument_list|)
operator|.
name|minWordLen
argument_list|(
name|request
operator|.
name|minWordLen
argument_list|()
argument_list|)
operator|.
name|maxWordLen
argument_list|(
name|request
operator|.
name|maxWordLen
argument_list|()
argument_list|)
operator|.
name|minTermFrequency
argument_list|(
name|request
operator|.
name|minTermFrequency
argument_list|()
argument_list|)
operator|.
name|maxQueryTerms
argument_list|(
name|request
operator|.
name|maxQueryTerms
argument_list|()
argument_list|)
operator|.
name|stopWords
argument_list|(
name|request
operator|.
name|stopWords
argument_list|()
argument_list|)
decl_stmt|;
name|boolBuilder
operator|.
name|should
argument_list|(
name|mlt
argument_list|)
expr_stmt|;
block|}
DECL|class|TransportHandler
specifier|private
class|class
name|TransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|MoreLikeThisRequest
argument_list|>
block|{
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|MoreLikeThisRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|MoreLikeThisRequest
argument_list|()
return|;
block|}
DECL|method|messageReceived
annotation|@
name|Override
specifier|public
name|void
name|messageReceived
parameter_list|(
name|MoreLikeThisRequest
name|request
parameter_list|,
specifier|final
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
comment|// no need to have a threaded listener since we just send back a response
name|request
operator|.
name|listenerThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
name|request
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|SearchResponse
name|result
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to send response for get"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|spawn
annotation|@
name|Override
specifier|public
name|boolean
name|spawn
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

