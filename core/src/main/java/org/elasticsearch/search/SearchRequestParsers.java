begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
package|;
end_package

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
name|QueryParseContext
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

begin_comment
comment|/**  * A container for all parsers used to parse  * {@link org.elasticsearch.action.search.SearchRequest} objects from a rest request.  */
end_comment

begin_class
DECL|class|SearchRequestParsers
specifier|public
class|class
name|SearchRequestParsers
block|{
comment|// TODO: this class should be renamed to SearchRequestParser, and all the parse
comment|// methods split across RestSearchAction and SearchSourceBuilder should be moved here
comment|// TODO: IndicesQueriesRegistry should be removed and just have the map of query parsers here
comment|/**      * Query parsers that may be used in search requests.      * @see org.elasticsearch.index.query.QueryParseContext      * @see org.elasticsearch.search.builder.SearchSourceBuilder#fromXContent(QueryParseContext, AggregatorParsers, Suggesters)      */
DECL|field|queryParsers
specifier|public
specifier|final
name|IndicesQueriesRegistry
name|queryParsers
decl_stmt|;
comment|// TODO: AggregatorParsers should be removed and the underlying maps of agg
comment|// and pipeline agg parsers should be here
comment|/**      * Agg and pipeline agg parsers that may be used in search requests.      * @see org.elasticsearch.search.builder.SearchSourceBuilder#fromXContent(QueryParseContext, AggregatorParsers, Suggesters)      */
DECL|field|aggParsers
specifier|public
specifier|final
name|AggregatorParsers
name|aggParsers
decl_stmt|;
comment|// TODO: Suggesters should be removed and the underlying map moved here
comment|/**      * Suggesters that may be used in search requests.      * @see org.elasticsearch.search.builder.SearchSourceBuilder#fromXContent(QueryParseContext, AggregatorParsers, Suggesters)      */
DECL|field|suggesters
specifier|public
specifier|final
name|Suggesters
name|suggesters
decl_stmt|;
DECL|method|SearchRequestParsers
specifier|public
name|SearchRequestParsers
parameter_list|(
name|IndicesQueriesRegistry
name|queryParsers
parameter_list|,
name|AggregatorParsers
name|aggParsers
parameter_list|,
name|Suggesters
name|suggesters
parameter_list|)
block|{
name|this
operator|.
name|queryParsers
operator|=
name|queryParsers
expr_stmt|;
name|this
operator|.
name|aggParsers
operator|=
name|aggParsers
expr_stmt|;
name|this
operator|.
name|suggesters
operator|=
name|suggesters
expr_stmt|;
block|}
block|}
end_class

end_unit

