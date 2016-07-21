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
name|ElasticsearchException
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
name|client
operator|.
name|node
operator|.
name|NodeClient
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
name|RestChannel
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
name|RestController
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
name|HashMap
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
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|POST
import|;
end_import

begin_class
DECL|class|RestDeleteByQueryAction
specifier|public
class|class
name|RestDeleteByQueryAction
extends|extends
name|AbstractBulkByQueryRestHandler
argument_list|<
name|DeleteByQueryRequest
argument_list|,
name|DeleteByQueryAction
argument_list|>
block|{
annotation|@
name|Inject
DECL|method|RestDeleteByQueryAction
specifier|public
name|RestDeleteByQueryAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
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
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|indicesQueriesRegistry
argument_list|,
name|aggParsers
argument_list|,
name|suggesters
argument_list|,
name|clusterService
argument_list|,
name|DeleteByQueryAction
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/{index}/_delete_by_query"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/{index}/{type}/_delete_by_query"
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
name|NodeClient
name|client
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
literal|false
operator|==
name|request
operator|.
name|hasContent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"_delete_by_query requires a request body"
argument_list|)
throw|;
block|}
name|handleRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|client
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|buildRequest
specifier|protected
name|DeleteByQueryRequest
name|buildRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
comment|/*          * Passing the search request through DeleteByQueryRequest first allows          * it to set its own defaults which differ from SearchRequest's          * defaults. Then the parseInternalRequest can override them.          */
name|DeleteByQueryRequest
name|internal
init|=
operator|new
name|DeleteByQueryRequest
argument_list|(
operator|new
name|SearchRequest
argument_list|()
argument_list|)
decl_stmt|;
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
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|consumers
operator|.
name|put
argument_list|(
literal|"conflicts"
argument_list|,
name|o
lambda|->
name|internal
operator|.
name|setConflicts
argument_list|(
operator|(
name|String
operator|)
name|o
argument_list|)
argument_list|)
expr_stmt|;
name|parseInternalRequest
argument_list|(
name|internal
argument_list|,
name|request
argument_list|,
name|consumers
argument_list|)
expr_stmt|;
return|return
name|internal
return|;
block|}
block|}
end_class

end_unit

