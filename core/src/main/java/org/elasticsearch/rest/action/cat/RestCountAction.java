begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.cat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|cat
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
name|Table
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
name|BytesArray
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
name|index
operator|.
name|query
operator|.
name|QueryBuilder
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
name|rest
operator|.
name|RestResponse
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
name|RestTable
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
name|builder
operator|.
name|SearchSourceBuilder
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
name|GET
import|;
end_import

begin_class
DECL|class|RestCountAction
specifier|public
class|class
name|RestCountAction
extends|extends
name|AbstractCatAction
block|{
DECL|field|indicesQueriesRegistry
specifier|private
specifier|final
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
decl_stmt|;
annotation|@
name|Inject
DECL|method|RestCountAction
specifier|public
name|RestCountAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|restController
parameter_list|,
name|RestController
name|controller
parameter_list|,
name|Client
name|client
parameter_list|,
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
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
name|restController
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_cat/count"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|restController
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_cat/count/{index}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|indicesQueriesRegistry
operator|=
name|indicesQueriesRegistry
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|documentation
specifier|protected
name|void
name|documentation
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"/_cat/count\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"/_cat/count/{index}\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doRequest
specifier|public
name|void
name|doRequest
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
name|String
index|[]
name|indices
init|=
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"index"
argument_list|)
argument_list|)
decl_stmt|;
name|SearchRequest
name|countRequest
init|=
operator|new
name|SearchRequest
argument_list|(
name|indices
argument_list|)
decl_stmt|;
name|String
name|source
init|=
name|request
operator|.
name|param
argument_list|(
literal|"source"
argument_list|)
decl_stmt|;
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
operator|new
name|SearchSourceBuilder
argument_list|()
operator|.
name|size
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|countRequest
operator|.
name|source
argument_list|(
name|searchSourceBuilder
argument_list|)
expr_stmt|;
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
name|searchSourceBuilder
operator|.
name|query
argument_list|(
name|RestActions
operator|.
name|getQueryContent
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|source
argument_list|)
argument_list|,
name|indicesQueriesRegistry
argument_list|,
name|parseFieldMatcher
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|QueryBuilder
name|queryBuilder
init|=
name|RestActions
operator|.
name|urlParamsToQueryBuilder
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryBuilder
operator|!=
literal|null
condition|)
block|{
name|searchSourceBuilder
operator|.
name|query
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
block|}
block|}
name|client
operator|.
name|search
argument_list|(
name|countRequest
argument_list|,
operator|new
name|RestResponseListener
argument_list|<
name|SearchResponse
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
name|SearchResponse
name|countResponse
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|RestTable
operator|.
name|buildResponse
argument_list|(
name|buildTable
argument_list|(
name|request
argument_list|,
name|countResponse
argument_list|)
argument_list|,
name|channel
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTableWithHeader
specifier|protected
name|Table
name|getTableWithHeader
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|)
block|{
name|Table
name|table
init|=
operator|new
name|Table
argument_list|()
decl_stmt|;
name|table
operator|.
name|startHeadersWithTimestamp
argument_list|()
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"count"
argument_list|,
literal|"alias:dc,docs.count,docsCount;desc:the document count"
argument_list|)
expr_stmt|;
name|table
operator|.
name|endHeaders
argument_list|()
expr_stmt|;
return|return
name|table
return|;
block|}
DECL|method|buildTable
specifier|private
name|Table
name|buildTable
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|SearchResponse
name|response
parameter_list|)
block|{
name|Table
name|table
init|=
name|getTableWithHeader
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|table
operator|.
name|startRow
argument_list|()
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|endRow
argument_list|()
expr_stmt|;
return|return
name|table
return|;
block|}
block|}
end_class

end_unit

