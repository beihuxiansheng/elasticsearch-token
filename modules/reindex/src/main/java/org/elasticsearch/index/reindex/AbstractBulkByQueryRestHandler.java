begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.reindex
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
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
name|support
operator|.
name|TransportAction
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
name|cluster
operator|.
name|service
operator|.
name|ClusterService
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
name|bytes
operator|.
name|BytesReference
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
name|collect
operator|.
name|Tuple
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
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentFactory
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
name|xcontent
operator|.
name|XContentHelper
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
name|xcontent
operator|.
name|XContentType
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
name|query
operator|.
name|IndicesQueriesRegistry
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
name|RestRequest
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
name|search
operator|.
name|RestSearchAction
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
name|RestActions
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
name|aggregations
operator|.
name|AggregatorParsers
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
name|suggest
operator|.
name|Suggesters
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
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
name|reindex
operator|.
name|AbstractBulkByScrollRequest
operator|.
name|SIZE_ALL_MATCHES
import|;
end_import

begin_comment
comment|/**  * Rest handler for reindex actions that accepts a search request like Update-By-Query or Delete-By-Query  */
end_comment

begin_class
DECL|class|AbstractBulkByQueryRestHandler
specifier|public
specifier|abstract
class|class
name|AbstractBulkByQueryRestHandler
parameter_list|<
name|Request
extends|extends
name|AbstractBulkByScrollRequest
parameter_list|<
name|Request
parameter_list|>
parameter_list|,
name|TA
extends|extends
name|TransportAction
parameter_list|<
name|Request
parameter_list|,
name|BulkIndexByScrollResponse
parameter_list|>
parameter_list|>
extends|extends
name|AbstractBaseReindexRestHandler
argument_list|<
name|Request
argument_list|,
name|TA
argument_list|>
block|{
DECL|method|AbstractBulkByQueryRestHandler
specifier|protected
name|AbstractBulkByQueryRestHandler
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Client
name|client
parameter_list|,
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
parameter_list|,
name|AggregatorParsers
name|aggParsers
parameter_list|,
name|Suggesters
name|suggesters
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TA
name|action
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|client
argument_list|,
name|indicesQueriesRegistry
argument_list|,
name|aggParsers
argument_list|,
name|suggesters
argument_list|,
name|clusterService
argument_list|,
name|action
argument_list|)
expr_stmt|;
block|}
DECL|method|parseInternalRequest
specifier|protected
name|void
name|parseInternalRequest
parameter_list|(
name|Request
name|internal
parameter_list|,
name|RestRequest
name|restRequest
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Consumer
argument_list|<
name|Object
argument_list|>
argument_list|>
name|consumers
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|internal
operator|!=
literal|null
operator|:
literal|"Request should not be null"
assert|;
assert|assert
name|restRequest
operator|!=
literal|null
operator|:
literal|"RestRequest should not be null"
assert|;
name|SearchRequest
name|searchRequest
init|=
name|internal
operator|.
name|getSearchRequest
argument_list|()
decl_stmt|;
name|int
name|scrollSize
init|=
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|size
argument_list|(
name|SIZE_ALL_MATCHES
argument_list|)
expr_stmt|;
name|parseSearchRequest
argument_list|(
name|searchRequest
argument_list|,
name|restRequest
argument_list|,
name|consumers
argument_list|)
expr_stmt|;
name|internal
operator|.
name|setSize
argument_list|(
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|size
argument_list|(
name|restRequest
operator|.
name|paramAsInt
argument_list|(
literal|"scroll_size"
argument_list|,
name|scrollSize
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|conflicts
init|=
name|restRequest
operator|.
name|param
argument_list|(
literal|"conflicts"
argument_list|)
decl_stmt|;
if|if
condition|(
name|conflicts
operator|!=
literal|null
condition|)
block|{
name|internal
operator|.
name|setConflicts
argument_list|(
name|conflicts
argument_list|)
expr_stmt|;
block|}
comment|// Let the requester set search timeout. It is probably only going to be useful for testing but who knows.
if|if
condition|(
name|restRequest
operator|.
name|hasParam
argument_list|(
literal|"search_timeout"
argument_list|)
condition|)
block|{
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|timeout
argument_list|(
name|restRequest
operator|.
name|paramAsTime
argument_list|(
literal|"search_timeout"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|parseSearchRequest
specifier|protected
name|void
name|parseSearchRequest
parameter_list|(
name|SearchRequest
name|searchRequest
parameter_list|,
name|RestRequest
name|restRequest
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Consumer
argument_list|<
name|Object
argument_list|>
argument_list|>
name|consumers
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|searchRequest
operator|!=
literal|null
operator|:
literal|"SearchRequest should not be null"
assert|;
assert|assert
name|restRequest
operator|!=
literal|null
operator|:
literal|"RestRequest should not be null"
assert|;
comment|/*          * We can't send parseSearchRequest REST content that it doesn't support          * so we will have to remove the content that is valid in addition to          * what it supports from the content first. This is a temporary hack and          * should get better when SearchRequest has full ObjectParser support          * then we can delegate and stuff.          */
name|BytesReference
name|content
init|=
name|RestActions
operator|.
name|hasBodyContent
argument_list|(
name|restRequest
argument_list|)
condition|?
name|RestActions
operator|.
name|getRestContent
argument_list|(
name|restRequest
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
operator|(
name|content
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|consumers
operator|!=
literal|null
operator|&&
name|consumers
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|Tuple
argument_list|<
name|XContentType
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|body
init|=
name|XContentHelper
operator|.
name|convertToMap
argument_list|(
name|content
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|modified
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Consumer
argument_list|<
name|Object
argument_list|>
argument_list|>
name|consumer
range|:
name|consumers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|value
init|=
name|body
operator|.
name|v2
argument_list|()
operator|.
name|remove
argument_list|(
name|consumer
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|consumer
operator|.
name|getValue
argument_list|()
operator|.
name|accept
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|modified
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|modified
condition|)
block|{
try|try
init|(
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|body
operator|.
name|v1
argument_list|()
argument_list|)
init|)
block|{
name|content
operator|=
name|builder
operator|.
name|map
argument_list|(
name|body
operator|.
name|v2
argument_list|()
argument_list|)
operator|.
name|bytes
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|RestSearchAction
operator|.
name|parseSearchRequest
argument_list|(
name|searchRequest
argument_list|,
name|indicesQueriesRegistry
argument_list|,
name|restRequest
argument_list|,
name|parseFieldMatcher
argument_list|,
name|aggParsers
argument_list|,
name|suggesters
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

