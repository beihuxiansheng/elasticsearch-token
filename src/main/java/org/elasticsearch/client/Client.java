begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
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
name|exists
operator|.
name|ExistsRequest
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
name|exists
operator|.
name|ExistsRequestBuilder
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
name|exists
operator|.
name|ExistsResponse
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
name|fieldstats
operator|.
name|FieldStatsRequest
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
name|fieldstats
operator|.
name|FieldStatsRequestBuilder
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
name|fieldstats
operator|.
name|FieldStatsResponse
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
name|indexedscripts
operator|.
name|delete
operator|.
name|DeleteIndexedScriptRequest
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
name|indexedscripts
operator|.
name|delete
operator|.
name|DeleteIndexedScriptRequestBuilder
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
name|indexedscripts
operator|.
name|delete
operator|.
name|DeleteIndexedScriptResponse
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
name|indexedscripts
operator|.
name|get
operator|.
name|GetIndexedScriptRequest
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
name|indexedscripts
operator|.
name|get
operator|.
name|GetIndexedScriptRequestBuilder
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
name|indexedscripts
operator|.
name|get
operator|.
name|GetIndexedScriptResponse
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
name|indexedscripts
operator|.
name|put
operator|.
name|PutIndexedScriptRequest
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
name|indexedscripts
operator|.
name|put
operator|.
name|PutIndexedScriptRequestBuilder
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
name|indexedscripts
operator|.
name|put
operator|.
name|PutIndexedScriptResponse
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
name|termvectors
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
name|support
operator|.
name|Headers
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lease
operator|.
name|Releasable
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

begin_comment
comment|/**  * A client provides a one stop interface for performing actions/operations against the cluster.  *<p/>  *<p>All operations performed are asynchronous by nature. Each action/operation has two flavors, the first  * simply returns an {@link org.elasticsearch.action.ActionFuture}, while the second accepts an  * {@link org.elasticsearch.action.ActionListener}.  *<p/>  *<p>A client can either be retrieved from a {@link org.elasticsearch.node.Node} started, or connected remotely  * to one or more nodes using {@link org.elasticsearch.client.transport.TransportClient}.  *  * @see org.elasticsearch.node.Node#client()  * @see org.elasticsearch.client.transport.TransportClient  */
end_comment

begin_interface
DECL|interface|Client
specifier|public
interface|interface
name|Client
extends|extends
name|ElasticsearchClient
extends|,
name|Releasable
block|{
DECL|field|CLIENT_TYPE_SETTING
name|String
name|CLIENT_TYPE_SETTING
init|=
literal|"client.type"
decl_stmt|;
comment|/**      * The admin client that can be used to perform administrative operations.      */
DECL|method|admin
name|AdminClient
name|admin
parameter_list|()
function_decl|;
comment|/**      * Index a JSON source associated with a given index and type.      *<p/>      *<p>The id is optional, if it is not provided, one will be generated automatically.      *      * @param request The index request      * @return The result future      * @see Requests#indexRequest(String)      */
DECL|method|index
name|ActionFuture
argument_list|<
name|IndexResponse
argument_list|>
name|index
parameter_list|(
name|IndexRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Index a document associated with a given index and type.      *<p/>      *<p>The id is optional, if it is not provided, one will be generated automatically.      *      * @param request  The index request      * @param listener A listener to be notified with a result      * @see Requests#indexRequest(String)      */
DECL|method|index
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
function_decl|;
comment|/**      * Index a document associated with a given index and type.      *<p/>      *<p>The id is optional, if it is not provided, one will be generated automatically.      */
DECL|method|prepareIndex
name|IndexRequestBuilder
name|prepareIndex
parameter_list|()
function_decl|;
comment|/**      * Updates a document based on a script.      *      * @param request The update request      * @return The result future      */
DECL|method|update
name|ActionFuture
argument_list|<
name|UpdateResponse
argument_list|>
name|update
parameter_list|(
name|UpdateRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Updates a document based on a script.      *      * @param request  The update request      * @param listener A listener to be notified with a result      */
DECL|method|update
name|void
name|update
parameter_list|(
name|UpdateRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|UpdateResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Updates a document based on a script.      */
DECL|method|prepareUpdate
name|UpdateRequestBuilder
name|prepareUpdate
parameter_list|()
function_decl|;
comment|/**      * Updates a document based on a script.      */
DECL|method|prepareUpdate
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
function_decl|;
comment|/**      * Index a document associated with a given index and type.      *<p/>      *<p>The id is optional, if it is not provided, one will be generated automatically.      *      * @param index The index to index the document to      * @param type  The type to index the document to      */
DECL|method|prepareIndex
name|IndexRequestBuilder
name|prepareIndex
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|)
function_decl|;
comment|/**      * Index a document associated with a given index and type.      *<p/>      *<p>The id is optional, if it is not provided, one will be generated automatically.      *      * @param index The index to index the document to      * @param type  The type to index the document to      * @param id    The id of the document      */
DECL|method|prepareIndex
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
function_decl|;
comment|/**      * Deletes a document from the index based on the index, type and id.      *      * @param request The delete request      * @return The result future      * @see Requests#deleteRequest(String)      */
DECL|method|delete
name|ActionFuture
argument_list|<
name|DeleteResponse
argument_list|>
name|delete
parameter_list|(
name|DeleteRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Deletes a document from the index based on the index, type and id.      *      * @param request  The delete request      * @param listener A listener to be notified with a result      * @see Requests#deleteRequest(String)      */
DECL|method|delete
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
function_decl|;
comment|/**      * Deletes a document from the index based on the index, type and id.      */
DECL|method|prepareDelete
name|DeleteRequestBuilder
name|prepareDelete
parameter_list|()
function_decl|;
comment|/**      * Deletes a document from the index based on the index, type and id.      *      * @param index The index to delete the document from      * @param type  The type of the document to delete      * @param id    The id of the document to delete      */
DECL|method|prepareDelete
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
function_decl|;
comment|/**      * Executes a bulk of index / delete operations.      *      * @param request The bulk request      * @return The result future      * @see org.elasticsearch.client.Requests#bulkRequest()      */
DECL|method|bulk
name|ActionFuture
argument_list|<
name|BulkResponse
argument_list|>
name|bulk
parameter_list|(
name|BulkRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Executes a bulk of index / delete operations.      *      * @param request  The bulk request      * @param listener A listener to be notified with a result      * @see org.elasticsearch.client.Requests#bulkRequest()      */
DECL|method|bulk
name|void
name|bulk
parameter_list|(
name|BulkRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|BulkResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Executes a bulk of index / delete operations.      */
DECL|method|prepareBulk
name|BulkRequestBuilder
name|prepareBulk
parameter_list|()
function_decl|;
comment|/**      * Gets the document that was indexed from an index with a type and id.      *      * @param request The get request      * @return The result future      * @see Requests#getRequest(String)      */
DECL|method|get
name|ActionFuture
argument_list|<
name|GetResponse
argument_list|>
name|get
parameter_list|(
name|GetRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Gets the document that was indexed from an index with a type and id.      *      * @param request  The get request      * @param listener A listener to be notified with a result      * @see Requests#getRequest(String)      */
DECL|method|get
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
function_decl|;
comment|/**      * Gets the document that was indexed from an index with a type and id.      */
DECL|method|prepareGet
name|GetRequestBuilder
name|prepareGet
parameter_list|()
function_decl|;
comment|/**      * Gets the document that was indexed from an index with a type (optional) and id.      */
DECL|method|prepareGet
name|GetRequestBuilder
name|prepareGet
parameter_list|(
name|String
name|index
parameter_list|,
annotation|@
name|Nullable
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
function_decl|;
comment|/**      * Put an indexed script      */
DECL|method|preparePutIndexedScript
name|PutIndexedScriptRequestBuilder
name|preparePutIndexedScript
parameter_list|()
function_decl|;
comment|/**      * Put the indexed script      */
DECL|method|preparePutIndexedScript
name|PutIndexedScriptRequestBuilder
name|preparePutIndexedScript
parameter_list|(
annotation|@
name|Nullable
name|String
name|scriptLang
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|source
parameter_list|)
function_decl|;
comment|/**      * delete an indexed script      */
DECL|method|deleteIndexedScript
name|void
name|deleteIndexedScript
parameter_list|(
name|DeleteIndexedScriptRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|DeleteIndexedScriptResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Delete an indexed script      *      * @param request The put request      * @return The result future      */
DECL|method|deleteIndexedScript
name|ActionFuture
argument_list|<
name|DeleteIndexedScriptResponse
argument_list|>
name|deleteIndexedScript
parameter_list|(
name|DeleteIndexedScriptRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Delete an indexed script      */
DECL|method|prepareDeleteIndexedScript
name|DeleteIndexedScriptRequestBuilder
name|prepareDeleteIndexedScript
parameter_list|()
function_decl|;
comment|/**      * Delete an indexed script      */
DECL|method|prepareDeleteIndexedScript
name|DeleteIndexedScriptRequestBuilder
name|prepareDeleteIndexedScript
parameter_list|(
annotation|@
name|Nullable
name|String
name|scriptLang
parameter_list|,
name|String
name|id
parameter_list|)
function_decl|;
comment|/**      * Put an indexed script      */
DECL|method|putIndexedScript
name|void
name|putIndexedScript
parameter_list|(
name|PutIndexedScriptRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|PutIndexedScriptResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Put an indexed script      *      * @param request The put request      * @return The result future      */
DECL|method|putIndexedScript
name|ActionFuture
argument_list|<
name|PutIndexedScriptResponse
argument_list|>
name|putIndexedScript
parameter_list|(
name|PutIndexedScriptRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Get an indexed script      */
DECL|method|prepareGetIndexedScript
name|GetIndexedScriptRequestBuilder
name|prepareGetIndexedScript
parameter_list|()
function_decl|;
comment|/**      * Get the indexed script      */
DECL|method|prepareGetIndexedScript
name|GetIndexedScriptRequestBuilder
name|prepareGetIndexedScript
parameter_list|(
annotation|@
name|Nullable
name|String
name|scriptLang
parameter_list|,
name|String
name|id
parameter_list|)
function_decl|;
comment|/**      * Get an indexed script      */
DECL|method|getIndexedScript
name|void
name|getIndexedScript
parameter_list|(
name|GetIndexedScriptRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|GetIndexedScriptResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Gets the document that was indexed from an index with a type and id.      *      * @param request The get request      * @return The result future      * @see Requests#getRequest(String)      */
DECL|method|getIndexedScript
name|ActionFuture
argument_list|<
name|GetIndexedScriptResponse
argument_list|>
name|getIndexedScript
parameter_list|(
name|GetIndexedScriptRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Multi get documents.      */
DECL|method|multiGet
name|ActionFuture
argument_list|<
name|MultiGetResponse
argument_list|>
name|multiGet
parameter_list|(
name|MultiGetRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Multi get documents.      */
DECL|method|multiGet
name|void
name|multiGet
parameter_list|(
name|MultiGetRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|MultiGetResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Multi get documents.      */
DECL|method|prepareMultiGet
name|MultiGetRequestBuilder
name|prepareMultiGet
parameter_list|()
function_decl|;
comment|/**      * A count of all the documents matching a specific query.      *      * @param request The count request      * @return The result future      * @see Requests#countRequest(String...)      */
DECL|method|count
name|ActionFuture
argument_list|<
name|CountResponse
argument_list|>
name|count
parameter_list|(
name|CountRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * A count of all the documents matching a specific query.      *      * @param request  The count request      * @param listener A listener to be notified of the result      * @see Requests#countRequest(String...)      */
DECL|method|count
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
function_decl|;
comment|/**      * A count of all the documents matching a specific query.      */
DECL|method|prepareCount
name|CountRequestBuilder
name|prepareCount
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
function_decl|;
comment|/**      * Checks existence of any documents matching a specific query.      *      * @param request The exists request      * @return The result future      * @see Requests#existsRequest(String...)      */
DECL|method|exists
name|ActionFuture
argument_list|<
name|ExistsResponse
argument_list|>
name|exists
parameter_list|(
name|ExistsRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Checks existence of any documents matching a specific query.      *      * @param request The exists request      * @param listener A listener to be notified of the result      * @see Requests#existsRequest(String...)      */
DECL|method|exists
name|void
name|exists
parameter_list|(
name|ExistsRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ExistsResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Checks existence of any documents matching a specific query.      */
DECL|method|prepareExists
name|ExistsRequestBuilder
name|prepareExists
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
function_decl|;
comment|/**      * Suggestion matching a specific phrase.      *      * @param request The suggest request      * @return The result future      * @see Requests#suggestRequest(String...)      */
DECL|method|suggest
name|ActionFuture
argument_list|<
name|SuggestResponse
argument_list|>
name|suggest
parameter_list|(
name|SuggestRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Suggestions matching a specific phrase.      *      * @param request  The suggest request      * @param listener A listener to be notified of the result      * @see Requests#suggestRequest(String...)      */
DECL|method|suggest
name|void
name|suggest
parameter_list|(
name|SuggestRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|SuggestResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Suggestions matching a specific phrase.      */
DECL|method|prepareSuggest
name|SuggestRequestBuilder
name|prepareSuggest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
function_decl|;
comment|/**      * Search across one or more indices and one or more types with a query.      *      * @param request The search request      * @return The result future      * @see Requests#searchRequest(String...)      */
DECL|method|search
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|search
parameter_list|(
name|SearchRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Search across one or more indices and one or more types with a query.      *      * @param request  The search request      * @param listener A listener to be notified of the result      * @see Requests#searchRequest(String...)      */
DECL|method|search
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
function_decl|;
comment|/**      * Search across one or more indices and one or more types with a query.      */
DECL|method|prepareSearch
name|SearchRequestBuilder
name|prepareSearch
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
function_decl|;
comment|/**      * A search scroll request to continue searching a previous scrollable search request.      *      * @param request The search scroll request      * @return The result future      * @see Requests#searchScrollRequest(String)      */
DECL|method|searchScroll
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|searchScroll
parameter_list|(
name|SearchScrollRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * A search scroll request to continue searching a previous scrollable search request.      *      * @param request  The search scroll request      * @param listener A listener to be notified of the result      * @see Requests#searchScrollRequest(String)      */
DECL|method|searchScroll
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
function_decl|;
comment|/**      * A search scroll request to continue searching a previous scrollable search request.      */
DECL|method|prepareSearchScroll
name|SearchScrollRequestBuilder
name|prepareSearchScroll
parameter_list|(
name|String
name|scrollId
parameter_list|)
function_decl|;
comment|/**      * Performs multiple search requests.      */
DECL|method|multiSearch
name|ActionFuture
argument_list|<
name|MultiSearchResponse
argument_list|>
name|multiSearch
parameter_list|(
name|MultiSearchRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Performs multiple search requests.      */
DECL|method|multiSearch
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
function_decl|;
comment|/**      * Performs multiple search requests.      */
DECL|method|prepareMultiSearch
name|MultiSearchRequestBuilder
name|prepareMultiSearch
parameter_list|()
function_decl|;
comment|/**      * A more like this action to search for documents that are "like" a specific document.      *      * @param request The more like this request      * @return The response future      */
DECL|method|moreLikeThis
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|moreLikeThis
parameter_list|(
name|MoreLikeThisRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * A more like this action to search for documents that are "like" a specific document.      *      * @param request  The more like this request      * @param listener A listener to be notified of the result      */
DECL|method|moreLikeThis
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
function_decl|;
comment|/**      * A more like this action to search for documents that are "like" a specific document.      *      * @param index The index to load the document from      * @param type  The type of the document      * @param id    The id of the document      */
DECL|method|prepareMoreLikeThis
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
function_decl|;
comment|/**      * An action that returns the term vectors for a specific document.      *      * @param request The term vector request      * @return The response future      */
DECL|method|termVectors
name|ActionFuture
argument_list|<
name|TermVectorsResponse
argument_list|>
name|termVectors
parameter_list|(
name|TermVectorsRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * An action that returns the term vectors for a specific document.      *      * @param request The term vector request      * @return The response future      */
DECL|method|termVectors
name|void
name|termVectors
parameter_list|(
name|TermVectorsRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|TermVectorsResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Builder for the term vector request.      */
DECL|method|prepareTermVectors
name|TermVectorsRequestBuilder
name|prepareTermVectors
parameter_list|()
function_decl|;
comment|/**      * Builder for the term vector request.      *      * @param index The index to load the document from      * @param type  The type of the document      * @param id    The id of the document      */
DECL|method|prepareTermVectors
name|TermVectorsRequestBuilder
name|prepareTermVectors
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
function_decl|;
comment|/**      * An action that returns the term vectors for a specific document.      *      * @param request The term vector request      * @return The response future      */
annotation|@
name|Deprecated
DECL|method|termVector
name|ActionFuture
argument_list|<
name|TermVectorsResponse
argument_list|>
name|termVector
parameter_list|(
name|TermVectorsRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * An action that returns the term vectors for a specific document.      *      * @param request The term vector request      * @return The response future      */
annotation|@
name|Deprecated
DECL|method|termVector
name|void
name|termVector
parameter_list|(
name|TermVectorsRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|TermVectorsResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Builder for the term vector request.      */
annotation|@
name|Deprecated
DECL|method|prepareTermVector
name|TermVectorsRequestBuilder
name|prepareTermVector
parameter_list|()
function_decl|;
comment|/**      * Builder for the term vector request.      *      * @param index The index to load the document from      * @param type  The type of the document      * @param id    The id of the document      */
annotation|@
name|Deprecated
DECL|method|prepareTermVector
name|TermVectorsRequestBuilder
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
function_decl|;
comment|/**      * Multi get term vectors.      */
DECL|method|multiTermVectors
name|ActionFuture
argument_list|<
name|MultiTermVectorsResponse
argument_list|>
name|multiTermVectors
parameter_list|(
name|MultiTermVectorsRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Multi get term vectors.      */
DECL|method|multiTermVectors
name|void
name|multiTermVectors
parameter_list|(
name|MultiTermVectorsRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|MultiTermVectorsResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Multi get term vectors.      */
DECL|method|prepareMultiTermVectors
name|MultiTermVectorsRequestBuilder
name|prepareMultiTermVectors
parameter_list|()
function_decl|;
comment|/**      * Percolates a request returning the matches documents.      */
DECL|method|percolate
name|ActionFuture
argument_list|<
name|PercolateResponse
argument_list|>
name|percolate
parameter_list|(
name|PercolateRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Percolates a request returning the matches documents.      */
DECL|method|percolate
name|void
name|percolate
parameter_list|(
name|PercolateRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|PercolateResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Percolates a request returning the matches documents.      */
DECL|method|preparePercolate
name|PercolateRequestBuilder
name|preparePercolate
parameter_list|()
function_decl|;
comment|/**      * Performs multiple percolate requests.      */
DECL|method|multiPercolate
name|ActionFuture
argument_list|<
name|MultiPercolateResponse
argument_list|>
name|multiPercolate
parameter_list|(
name|MultiPercolateRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Performs multiple percolate requests.      */
DECL|method|multiPercolate
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
function_decl|;
comment|/**      * Performs multiple percolate requests.      */
DECL|method|prepareMultiPercolate
name|MultiPercolateRequestBuilder
name|prepareMultiPercolate
parameter_list|()
function_decl|;
comment|/**      * Computes a score explanation for the specified request.      *      * @param index The index this explain is targeted for      * @param type  The type this explain is targeted for      * @param id    The document identifier this explain is targeted for      */
DECL|method|prepareExplain
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
function_decl|;
comment|/**      * Computes a score explanation for the specified request.      *      * @param request The request encapsulating the query and document identifier to compute a score explanation for      */
DECL|method|explain
name|ActionFuture
argument_list|<
name|ExplainResponse
argument_list|>
name|explain
parameter_list|(
name|ExplainRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Computes a score explanation for the specified request.      *      * @param request  The request encapsulating the query and document identifier to compute a score explanation for      * @param listener A listener to be notified of the result      */
DECL|method|explain
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
function_decl|;
comment|/**      * Clears the search contexts associated with specified scroll ids.      */
DECL|method|prepareClearScroll
name|ClearScrollRequestBuilder
name|prepareClearScroll
parameter_list|()
function_decl|;
comment|/**      * Clears the search contexts associated with specified scroll ids.      */
DECL|method|clearScroll
name|ActionFuture
argument_list|<
name|ClearScrollResponse
argument_list|>
name|clearScroll
parameter_list|(
name|ClearScrollRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Clears the search contexts associated with specified scroll ids.      */
DECL|method|clearScroll
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
function_decl|;
DECL|method|prepareFieldStats
name|FieldStatsRequestBuilder
name|prepareFieldStats
parameter_list|()
function_decl|;
DECL|method|fieldStats
name|ActionFuture
argument_list|<
name|FieldStatsResponse
argument_list|>
name|fieldStats
parameter_list|(
name|FieldStatsRequest
name|request
parameter_list|)
function_decl|;
DECL|method|fieldStats
name|void
name|fieldStats
parameter_list|(
name|FieldStatsRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|FieldStatsResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Returns this clients settings      */
DECL|method|settings
name|Settings
name|settings
parameter_list|()
function_decl|;
DECL|method|headers
name|Headers
name|headers
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

