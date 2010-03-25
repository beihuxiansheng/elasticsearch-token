begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|admin
operator|.
name|indices
operator|.
name|alias
operator|.
name|IndicesAliasesRequest
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
name|admin
operator|.
name|indices
operator|.
name|alias
operator|.
name|IndicesAliasesResponse
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
name|admin
operator|.
name|indices
operator|.
name|create
operator|.
name|CreateIndexRequest
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
name|admin
operator|.
name|indices
operator|.
name|create
operator|.
name|CreateIndexResponse
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
name|admin
operator|.
name|indices
operator|.
name|delete
operator|.
name|DeleteIndexRequest
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
name|admin
operator|.
name|indices
operator|.
name|delete
operator|.
name|DeleteIndexResponse
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
name|admin
operator|.
name|indices
operator|.
name|flush
operator|.
name|FlushRequest
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
name|admin
operator|.
name|indices
operator|.
name|flush
operator|.
name|FlushResponse
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
name|admin
operator|.
name|indices
operator|.
name|gateway
operator|.
name|snapshot
operator|.
name|GatewaySnapshotRequest
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
name|admin
operator|.
name|indices
operator|.
name|gateway
operator|.
name|snapshot
operator|.
name|GatewaySnapshotResponse
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
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|put
operator|.
name|PutMappingRequest
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
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|put
operator|.
name|PutMappingResponse
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
name|admin
operator|.
name|indices
operator|.
name|optimize
operator|.
name|OptimizeRequest
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
name|admin
operator|.
name|indices
operator|.
name|optimize
operator|.
name|OptimizeResponse
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
name|admin
operator|.
name|indices
operator|.
name|refresh
operator|.
name|RefreshRequest
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
name|admin
operator|.
name|indices
operator|.
name|refresh
operator|.
name|RefreshResponse
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
name|admin
operator|.
name|indices
operator|.
name|status
operator|.
name|IndicesStatusRequest
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
name|admin
operator|.
name|indices
operator|.
name|status
operator|.
name|IndicesStatusResponse
import|;
end_import

begin_comment
comment|/**  * Administrative actions/operations against indices.  *  * @author kimchy (shay.banon)  * @see AdminClient#indices()  */
end_comment

begin_interface
DECL|interface|IndicesAdminClient
specifier|public
interface|interface
name|IndicesAdminClient
block|{
comment|/**      * The status of one or more indices.      *      * @param request The indices status request      * @return The result future      * @see Requests#indicesStatus(String...)      */
DECL|method|status
name|ActionFuture
argument_list|<
name|IndicesStatusResponse
argument_list|>
name|status
parameter_list|(
name|IndicesStatusRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * The status of one or more indices.      *      * @param request  The indices status request      * @param listener A listener to be notified with a result      * @see Requests#indicesStatus(String...)      */
DECL|method|status
name|void
name|status
parameter_list|(
name|IndicesStatusRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|IndicesStatusResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Creates an index using an explicit request allowing to specify the settings of the index.      *      * @param request The create index request      * @return The result future      * @see org.elasticsearch.client.Requests#createIndexRequest(String)      */
DECL|method|create
name|ActionFuture
argument_list|<
name|CreateIndexResponse
argument_list|>
name|create
parameter_list|(
name|CreateIndexRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Creates an index using an explicit request allowing to specify the settings of the index.      *      * @param request  The create index request      * @param listener A listener to be notified with a result      * @see org.elasticsearch.client.Requests#createIndexRequest(String)      */
DECL|method|create
name|void
name|create
parameter_list|(
name|CreateIndexRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|CreateIndexResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Deletes an index based on the index name.      *      * @param request The delete index request      * @return The result future      * @see org.elasticsearch.client.Requests#deleteIndexRequest(String)      */
DECL|method|delete
name|ActionFuture
argument_list|<
name|DeleteIndexResponse
argument_list|>
name|delete
parameter_list|(
name|DeleteIndexRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Deletes an index based on the index name.      *      * @param request  The delete index request      * @param listener A listener to be notified with a result      * @see org.elasticsearch.client.Requests#deleteIndexRequest(String)      */
DECL|method|delete
name|void
name|delete
parameter_list|(
name|DeleteIndexRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|DeleteIndexResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Explicitly refresh one or more indices (making the content indexed since the last refresh searchable).      *      * @param request The refresh request      * @return The result future      * @see org.elasticsearch.client.Requests#refreshRequest(String...)      */
DECL|method|refresh
name|ActionFuture
argument_list|<
name|RefreshResponse
argument_list|>
name|refresh
parameter_list|(
name|RefreshRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Explicitly refresh one or more indices (making the content indexed since the last refresh searchable).      *      * @param request  The refresh request      * @param listener A listener to be notified with a result      * @see org.elasticsearch.client.Requests#refreshRequest(String...)      */
DECL|method|refresh
name|void
name|refresh
parameter_list|(
name|RefreshRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|RefreshResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Explicitly flush one or more indices (releasing memory from the node).      *      * @param request The flush request      * @return A result future      * @see org.elasticsearch.client.Requests#flushRequest(String...)      */
DECL|method|flush
name|ActionFuture
argument_list|<
name|FlushResponse
argument_list|>
name|flush
parameter_list|(
name|FlushRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Explicitly flush one or more indices (releasing memory from the node).      *      * @param request  The flush request      * @param listener A listener to be notified with a result      * @see org.elasticsearch.client.Requests#flushRequest(String...)      */
DECL|method|flush
name|void
name|flush
parameter_list|(
name|FlushRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|FlushResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Explicitly optimize one or more indices into a the number of segments.      *      * @param request The optimize request      * @return A result future      * @see org.elasticsearch.client.Requests#optimizeRequest(String...)      */
DECL|method|optimize
name|ActionFuture
argument_list|<
name|OptimizeResponse
argument_list|>
name|optimize
parameter_list|(
name|OptimizeRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Explicitly optimize one or more indices into a the number of segments.      *      * @param request  The optimize request      * @param listener A listener to be notified with a result      * @see org.elasticsearch.client.Requests#optimizeRequest(String...)      */
DECL|method|optimize
name|void
name|optimize
parameter_list|(
name|OptimizeRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|OptimizeResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Add mapping definition for a type into one or more indices.      *      * @param request The create mapping request      * @return A result future      * @see org.elasticsearch.client.Requests#putMappingRequest(String...)      */
DECL|method|putMapping
name|ActionFuture
argument_list|<
name|PutMappingResponse
argument_list|>
name|putMapping
parameter_list|(
name|PutMappingRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Add mapping definition for a type into one or more indices.      *      * @param request  The create mapping request      * @param listener A listener to be notified with a result      * @see org.elasticsearch.client.Requests#putMappingRequest(String...)      */
DECL|method|putMapping
name|void
name|putMapping
parameter_list|(
name|PutMappingRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|PutMappingResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Explicitly perform gateway snapshot for one or more indices.      *      * @param request The gateway snapshot request      * @return The result future      * @see org.elasticsearch.client.Requests#gatewaySnapshotRequest(String...)      */
DECL|method|gatewaySnapshot
name|ActionFuture
argument_list|<
name|GatewaySnapshotResponse
argument_list|>
name|gatewaySnapshot
parameter_list|(
name|GatewaySnapshotRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Explicitly perform gateway snapshot for one or more indices.      *      * @param request  The gateway snapshot request      * @param listener A listener to be notified with a result      * @see org.elasticsearch.client.Requests#gatewaySnapshotRequest(String...)      */
DECL|method|gatewaySnapshot
name|void
name|gatewaySnapshot
parameter_list|(
name|GatewaySnapshotRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|GatewaySnapshotResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**      * Allows to add/remove aliases from indices.      *      * @param request The index aliases request      * @return The result future      * @see Requests#indexAliasesRequest()      */
DECL|method|indicesAliases
name|ActionFuture
argument_list|<
name|IndicesAliasesResponse
argument_list|>
name|indicesAliases
parameter_list|(
name|IndicesAliasesRequest
name|request
parameter_list|)
function_decl|;
comment|/**      * Allows to add/remove aliases from indices.      *      * @param request  The index aliases request      * @param listener A listener to be notified with a result      * @see Requests#indexAliasesRequest()      */
DECL|method|aliases
name|void
name|aliases
parameter_list|(
name|IndicesAliasesRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|IndicesAliasesResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

