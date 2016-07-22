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
name|apache
operator|.
name|http
operator|.
name|HttpHost
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
name|ActionRequestValidationException
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
name|BackoffPolicy
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
name|ActionFilters
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
name|AutoCreateIndex
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
name|HandledTransportAction
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
name|ParentTaskAssigningClient
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
name|RestClient
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
name|ClusterState
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
name|metadata
operator|.
name|IndexNameExpressionResolver
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
name|logging
operator|.
name|ESLogger
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
name|lucene
operator|.
name|uid
operator|.
name|Versions
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
name|Setting
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
name|Setting
operator|.
name|Property
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
name|transport
operator|.
name|TransportAddress
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|HttpInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|HttpServer
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
name|internal
operator|.
name|TTLFieldMapper
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
name|internal
operator|.
name|VersionFieldMapper
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
name|reindex
operator|.
name|remote
operator|.
name|RemoteInfo
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
name|reindex
operator|.
name|remote
operator|.
name|RemoteScrollableHitSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Script
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|Task
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
name|transport
operator|.
name|TransportService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Objects
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
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BiFunction
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
name|Function
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
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
name|VersionType
operator|.
name|INTERNAL
import|;
end_import

begin_class
DECL|class|TransportReindexAction
specifier|public
class|class
name|TransportReindexAction
extends|extends
name|HandledTransportAction
argument_list|<
name|ReindexRequest
argument_list|,
name|BulkIndexByScrollResponse
argument_list|>
block|{
DECL|field|REMOTE_CLUSTER_WHITELIST
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|REMOTE_CLUSTER_WHITELIST
init|=
name|Setting
operator|.
name|listSetting
argument_list|(
literal|"reindex.remote.whitelist"
argument_list|,
name|emptyList
argument_list|()
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|scriptService
specifier|private
specifier|final
name|ScriptService
name|scriptService
decl_stmt|;
DECL|field|autoCreateIndex
specifier|private
specifier|final
name|AutoCreateIndex
name|autoCreateIndex
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|Client
name|client
decl_stmt|;
DECL|field|remoteWhitelist
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|remoteWhitelist
decl_stmt|;
DECL|field|httpServer
specifier|private
specifier|final
name|HttpServer
name|httpServer
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportReindexAction
specifier|public
name|TransportReindexAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|,
name|AutoCreateIndex
name|autoCreateIndex
parameter_list|,
name|Client
name|client
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
annotation|@
name|Nullable
name|HttpServer
name|httpServer
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|ReindexAction
operator|.
name|NAME
argument_list|,
name|threadPool
argument_list|,
name|transportService
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|ReindexRequest
operator|::
operator|new
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|scriptService
operator|=
name|scriptService
expr_stmt|;
name|this
operator|.
name|autoCreateIndex
operator|=
name|autoCreateIndex
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|remoteWhitelist
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|REMOTE_CLUSTER_WHITELIST
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|httpServer
operator|=
name|httpServer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|Task
name|task
parameter_list|,
name|ReindexRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|BulkIndexByScrollResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|checkRemoteWhitelist
argument_list|(
name|request
operator|.
name|getRemoteInfo
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterState
name|state
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
name|validateAgainstAliases
argument_list|(
name|request
operator|.
name|getSearchRequest
argument_list|()
argument_list|,
name|request
operator|.
name|getDestination
argument_list|()
argument_list|,
name|request
operator|.
name|getRemoteInfo
argument_list|()
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|autoCreateIndex
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|ParentTaskAssigningClient
name|client
init|=
operator|new
name|ParentTaskAssigningClient
argument_list|(
name|this
operator|.
name|client
argument_list|,
name|clusterService
operator|.
name|localNode
argument_list|()
argument_list|,
name|task
argument_list|)
decl_stmt|;
operator|new
name|AsyncIndexBySearchAction
argument_list|(
operator|(
name|BulkByScrollTask
operator|)
name|task
argument_list|,
name|logger
argument_list|,
name|client
argument_list|,
name|threadPool
argument_list|,
name|request
argument_list|,
name|listener
argument_list|,
name|scriptService
argument_list|,
name|state
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|ReindexRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|BulkIndexByScrollResponse
argument_list|>
name|listener
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"task required"
argument_list|)
throw|;
block|}
DECL|method|checkRemoteWhitelist
specifier|private
name|void
name|checkRemoteWhitelist
parameter_list|(
name|RemoteInfo
name|remoteInfo
parameter_list|)
block|{
name|TransportAddress
name|publishAddress
init|=
literal|null
decl_stmt|;
name|HttpInfo
name|httpInfo
init|=
name|httpServer
operator|==
literal|null
condition|?
literal|null
else|:
name|httpServer
operator|.
name|info
argument_list|()
decl_stmt|;
if|if
condition|(
name|httpInfo
operator|!=
literal|null
operator|&&
name|httpInfo
operator|.
name|getAddress
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|publishAddress
operator|=
name|httpInfo
operator|.
name|getAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
expr_stmt|;
block|}
name|checkRemoteWhitelist
argument_list|(
name|remoteWhitelist
argument_list|,
name|remoteInfo
argument_list|,
name|publishAddress
argument_list|)
expr_stmt|;
block|}
DECL|method|checkRemoteWhitelist
specifier|static
name|void
name|checkRemoteWhitelist
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|whitelist
parameter_list|,
name|RemoteInfo
name|remoteInfo
parameter_list|,
name|TransportAddress
name|publishAddress
parameter_list|)
block|{
if|if
condition|(
name|remoteInfo
operator|==
literal|null
condition|)
return|return;
name|String
name|check
init|=
name|remoteInfo
operator|.
name|getHost
argument_list|()
operator|+
literal|':'
operator|+
name|remoteInfo
operator|.
name|getPort
argument_list|()
decl_stmt|;
if|if
condition|(
name|whitelist
operator|.
name|contains
argument_list|(
name|check
argument_list|)
condition|)
return|return;
comment|/*          * For testing we support the key "myself" to allow connecting to the local node. We can't just change the setting to include the          * local node because it is intentionally not a dynamic setting for security purposes. We can't use something like "localhost:9200"          * because we don't know up front which port we'll get because the tests bind to port 0. Instead we try to resolve it here, taking          * "myself" to mean "my published http address".          */
if|if
condition|(
name|whitelist
operator|.
name|contains
argument_list|(
literal|"myself"
argument_list|)
operator|&&
name|publishAddress
operator|!=
literal|null
operator|&&
name|publishAddress
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|check
argument_list|)
condition|)
block|{
return|return;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|'['
operator|+
name|check
operator|+
literal|"] not whitelisted in "
operator|+
name|REMOTE_CLUSTER_WHITELIST
operator|.
name|getKey
argument_list|()
argument_list|)
throw|;
block|}
comment|/**      * Throws an ActionRequestValidationException if the request tries to index      * back into the same index or into an index that points to two indexes.      * This cannot be done during request validation because the cluster state      * isn't available then. Package private for testing.      */
DECL|method|validateAgainstAliases
specifier|static
name|void
name|validateAgainstAliases
parameter_list|(
name|SearchRequest
name|source
parameter_list|,
name|IndexRequest
name|destination
parameter_list|,
name|RemoteInfo
name|remoteInfo
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|,
name|AutoCreateIndex
name|autoCreateIndex
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|)
block|{
if|if
condition|(
name|remoteInfo
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|String
name|target
init|=
name|destination
operator|.
name|index
argument_list|()
decl_stmt|;
if|if
condition|(
literal|false
operator|==
name|autoCreateIndex
operator|.
name|shouldAutoCreate
argument_list|(
name|target
argument_list|,
name|clusterState
argument_list|)
condition|)
block|{
comment|/*              * If we're going to autocreate the index we don't need to resolve              * it. This is the same sort of dance that TransportIndexRequest              * uses to decide to autocreate the index.              */
name|target
operator|=
name|indexNameExpressionResolver
operator|.
name|concreteIndexNames
argument_list|(
name|clusterState
argument_list|,
name|destination
argument_list|)
index|[
literal|0
index|]
expr_stmt|;
block|}
for|for
control|(
name|String
name|sourceIndex
range|:
name|indexNameExpressionResolver
operator|.
name|concreteIndexNames
argument_list|(
name|clusterState
argument_list|,
name|source
argument_list|)
control|)
block|{
if|if
condition|(
name|sourceIndex
operator|.
name|equals
argument_list|(
name|target
argument_list|)
condition|)
block|{
name|ActionRequestValidationException
name|e
init|=
operator|new
name|ActionRequestValidationException
argument_list|()
decl_stmt|;
name|e
operator|.
name|addValidationError
argument_list|(
literal|"reindex cannot write into an index its reading from ["
operator|+
name|target
operator|+
literal|']'
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
comment|/**      * Simple implementation of reindex using scrolling and bulk. There are tons      * of optimizations that can be done on certain types of reindex requests      * but this makes no attempt to do any of them so it can be as simple      * possible.      */
DECL|class|AsyncIndexBySearchAction
specifier|static
class|class
name|AsyncIndexBySearchAction
extends|extends
name|AbstractAsyncBulkIndexByScrollAction
argument_list|<
name|ReindexRequest
argument_list|>
block|{
DECL|method|AsyncIndexBySearchAction
specifier|public
name|AsyncIndexBySearchAction
parameter_list|(
name|BulkByScrollTask
name|task
parameter_list|,
name|ESLogger
name|logger
parameter_list|,
name|ParentTaskAssigningClient
name|client
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ReindexRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|BulkIndexByScrollResponse
argument_list|>
name|listener
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|super
argument_list|(
name|task
argument_list|,
name|logger
argument_list|,
name|client
argument_list|,
name|threadPool
argument_list|,
name|request
argument_list|,
name|listener
argument_list|,
name|scriptService
argument_list|,
name|clusterState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|buildScrollableResultSource
specifier|protected
name|ScrollableHitSource
name|buildScrollableResultSource
parameter_list|(
name|BackoffPolicy
name|backoffPolicy
parameter_list|)
block|{
if|if
condition|(
name|mainRequest
operator|.
name|getRemoteInfo
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// NORELEASE track 500-level retries that are builtin to the client
name|RemoteInfo
name|remoteInfo
init|=
name|mainRequest
operator|.
name|getRemoteInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|remoteInfo
operator|.
name|getUsername
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// NORELEASE support auth
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Auth is unsupported"
argument_list|)
throw|;
block|}
name|RestClient
name|restClient
init|=
name|RestClient
operator|.
name|builder
argument_list|(
operator|new
name|HttpHost
argument_list|(
name|remoteInfo
operator|.
name|getHost
argument_list|()
argument_list|,
name|remoteInfo
operator|.
name|getPort
argument_list|()
argument_list|,
name|remoteInfo
operator|.
name|getScheme
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
operator|new
name|RemoteScrollableHitSource
argument_list|(
name|logger
argument_list|,
name|backoffPolicy
argument_list|,
name|threadPool
argument_list|,
name|task
operator|::
name|countSearchRetry
argument_list|,
name|this
operator|::
name|finishHim
argument_list|,
name|restClient
argument_list|,
name|remoteInfo
operator|.
name|getQuery
argument_list|()
argument_list|,
name|mainRequest
operator|.
name|getSearchRequest
argument_list|()
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|buildScrollableResultSource
argument_list|(
name|backoffPolicy
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|buildScriptApplier
specifier|protected
name|BiFunction
argument_list|<
name|RequestWrapper
argument_list|<
name|?
argument_list|>
argument_list|,
name|ScrollableHitSource
operator|.
name|Hit
argument_list|,
name|RequestWrapper
argument_list|<
name|?
argument_list|>
argument_list|>
name|buildScriptApplier
parameter_list|()
block|{
name|Script
name|script
init|=
name|mainRequest
operator|.
name|getScript
argument_list|()
decl_stmt|;
if|if
condition|(
name|script
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|ReindexScriptApplier
argument_list|(
name|task
argument_list|,
name|scriptService
argument_list|,
name|script
argument_list|,
name|script
operator|.
name|getParams
argument_list|()
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|buildScriptApplier
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|buildRequest
specifier|protected
name|RequestWrapper
argument_list|<
name|IndexRequest
argument_list|>
name|buildRequest
parameter_list|(
name|ScrollableHitSource
operator|.
name|Hit
name|doc
parameter_list|)
block|{
name|IndexRequest
name|index
init|=
operator|new
name|IndexRequest
argument_list|()
decl_stmt|;
comment|// Copy the index from the request so we always write where it asked to write
name|index
operator|.
name|index
argument_list|(
name|mainRequest
operator|.
name|getDestination
argument_list|()
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
comment|// If the request override's type then the user wants all documents in that type. Otherwise keep the doc's type.
if|if
condition|(
name|mainRequest
operator|.
name|getDestination
argument_list|()
operator|.
name|type
argument_list|()
operator|==
literal|null
condition|)
block|{
name|index
operator|.
name|type
argument_list|(
name|doc
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|index
operator|.
name|type
argument_list|(
name|mainRequest
operator|.
name|getDestination
argument_list|()
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*              * Internal versioning can just use what we copied from the destination request. Otherwise we assume we're using external              * versioning and use the doc's version.              */
name|index
operator|.
name|versionType
argument_list|(
name|mainRequest
operator|.
name|getDestination
argument_list|()
operator|.
name|versionType
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|.
name|versionType
argument_list|()
operator|==
name|INTERNAL
condition|)
block|{
name|index
operator|.
name|version
argument_list|(
name|mainRequest
operator|.
name|getDestination
argument_list|()
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|index
operator|.
name|version
argument_list|(
name|doc
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// id and source always come from the found doc. Scripts can change them but they operate on the index request.
name|index
operator|.
name|id
argument_list|(
name|doc
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|index
operator|.
name|source
argument_list|(
name|doc
operator|.
name|getSource
argument_list|()
argument_list|)
expr_stmt|;
comment|/*              * The rest of the index request just has to be copied from the template. It may be changed later from scripts or the superclass              * here on out operates on the index request rather than the template.              */
name|index
operator|.
name|routing
argument_list|(
name|mainRequest
operator|.
name|getDestination
argument_list|()
operator|.
name|routing
argument_list|()
argument_list|)
expr_stmt|;
name|index
operator|.
name|parent
argument_list|(
name|mainRequest
operator|.
name|getDestination
argument_list|()
operator|.
name|parent
argument_list|()
argument_list|)
expr_stmt|;
name|index
operator|.
name|timestamp
argument_list|(
name|mainRequest
operator|.
name|getDestination
argument_list|()
operator|.
name|timestamp
argument_list|()
argument_list|)
expr_stmt|;
name|index
operator|.
name|ttl
argument_list|(
name|mainRequest
operator|.
name|getDestination
argument_list|()
operator|.
name|ttl
argument_list|()
argument_list|)
expr_stmt|;
name|index
operator|.
name|contentType
argument_list|(
name|mainRequest
operator|.
name|getDestination
argument_list|()
operator|.
name|getContentType
argument_list|()
argument_list|)
expr_stmt|;
name|index
operator|.
name|setPipeline
argument_list|(
name|mainRequest
operator|.
name|getDestination
argument_list|()
operator|.
name|getPipeline
argument_list|()
argument_list|)
expr_stmt|;
comment|// OpType is synthesized from version so it is handled when we copy version above.
return|return
name|wrap
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**          * Override the simple copy behavior to allow more fine grained control.          */
annotation|@
name|Override
DECL|method|copyRouting
specifier|protected
name|void
name|copyRouting
parameter_list|(
name|RequestWrapper
argument_list|<
name|?
argument_list|>
name|request
parameter_list|,
name|String
name|routing
parameter_list|)
block|{
name|String
name|routingSpec
init|=
name|mainRequest
operator|.
name|getDestination
argument_list|()
operator|.
name|routing
argument_list|()
decl_stmt|;
if|if
condition|(
name|routingSpec
operator|==
literal|null
condition|)
block|{
name|super
operator|.
name|copyRouting
argument_list|(
name|request
argument_list|,
name|routing
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|routingSpec
operator|.
name|startsWith
argument_list|(
literal|"="
argument_list|)
condition|)
block|{
name|super
operator|.
name|copyRouting
argument_list|(
name|request
argument_list|,
name|mainRequest
operator|.
name|getDestination
argument_list|()
operator|.
name|routing
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
switch|switch
condition|(
name|routingSpec
condition|)
block|{
case|case
literal|"keep"
case|:
name|super
operator|.
name|copyRouting
argument_list|(
name|request
argument_list|,
name|routing
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"discard"
case|:
name|super
operator|.
name|copyRouting
argument_list|(
name|request
argument_list|,
literal|null
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported routing command"
argument_list|)
throw|;
block|}
block|}
DECL|class|ReindexScriptApplier
class|class
name|ReindexScriptApplier
extends|extends
name|ScriptApplier
block|{
DECL|method|ReindexScriptApplier
name|ReindexScriptApplier
parameter_list|(
name|BulkByScrollTask
name|task
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|,
name|Script
name|script
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
name|super
argument_list|(
name|task
argument_list|,
name|scriptService
argument_list|,
name|script
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
comment|/*              * Methods below here handle script updating the index request. They try              * to be pretty liberal with regards to types because script are often              * dynamically typed.              */
annotation|@
name|Override
DECL|method|scriptChangedIndex
specifier|protected
name|void
name|scriptChangedIndex
parameter_list|(
name|RequestWrapper
argument_list|<
name|?
argument_list|>
name|request
parameter_list|,
name|Object
name|to
parameter_list|)
block|{
name|requireNonNull
argument_list|(
name|to
argument_list|,
literal|"Can't reindex without a destination index!"
argument_list|)
expr_stmt|;
name|request
operator|.
name|setIndex
argument_list|(
name|to
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scriptChangedType
specifier|protected
name|void
name|scriptChangedType
parameter_list|(
name|RequestWrapper
argument_list|<
name|?
argument_list|>
name|request
parameter_list|,
name|Object
name|to
parameter_list|)
block|{
name|requireNonNull
argument_list|(
name|to
argument_list|,
literal|"Can't reindex without a destination type!"
argument_list|)
expr_stmt|;
name|request
operator|.
name|setType
argument_list|(
name|to
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scriptChangedId
specifier|protected
name|void
name|scriptChangedId
parameter_list|(
name|RequestWrapper
argument_list|<
name|?
argument_list|>
name|request
parameter_list|,
name|Object
name|to
parameter_list|)
block|{
name|request
operator|.
name|setId
argument_list|(
name|Objects
operator|.
name|toString
argument_list|(
name|to
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scriptChangedVersion
specifier|protected
name|void
name|scriptChangedVersion
parameter_list|(
name|RequestWrapper
argument_list|<
name|?
argument_list|>
name|request
parameter_list|,
name|Object
name|to
parameter_list|)
block|{
if|if
condition|(
name|to
operator|==
literal|null
condition|)
block|{
name|request
operator|.
name|setVersion
argument_list|(
name|Versions
operator|.
name|MATCH_ANY
argument_list|)
expr_stmt|;
name|request
operator|.
name|setVersionType
argument_list|(
name|INTERNAL
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|request
operator|.
name|setVersion
argument_list|(
name|asLong
argument_list|(
name|to
argument_list|,
name|VersionFieldMapper
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|scriptChangedParent
specifier|protected
name|void
name|scriptChangedParent
parameter_list|(
name|RequestWrapper
argument_list|<
name|?
argument_list|>
name|request
parameter_list|,
name|Object
name|to
parameter_list|)
block|{
comment|// Have to override routing with parent just in case its changed
name|String
name|routing
init|=
name|Objects
operator|.
name|toString
argument_list|(
name|to
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|request
operator|.
name|setParent
argument_list|(
name|routing
argument_list|)
expr_stmt|;
name|request
operator|.
name|setRouting
argument_list|(
name|routing
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scriptChangedRouting
specifier|protected
name|void
name|scriptChangedRouting
parameter_list|(
name|RequestWrapper
argument_list|<
name|?
argument_list|>
name|request
parameter_list|,
name|Object
name|to
parameter_list|)
block|{
name|request
operator|.
name|setRouting
argument_list|(
name|Objects
operator|.
name|toString
argument_list|(
name|to
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scriptChangedTimestamp
specifier|protected
name|void
name|scriptChangedTimestamp
parameter_list|(
name|RequestWrapper
argument_list|<
name|?
argument_list|>
name|request
parameter_list|,
name|Object
name|to
parameter_list|)
block|{
name|request
operator|.
name|setTimestamp
argument_list|(
name|Objects
operator|.
name|toString
argument_list|(
name|to
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scriptChangedTTL
specifier|protected
name|void
name|scriptChangedTTL
parameter_list|(
name|RequestWrapper
argument_list|<
name|?
argument_list|>
name|request
parameter_list|,
name|Object
name|to
parameter_list|)
block|{
if|if
condition|(
name|to
operator|==
literal|null
condition|)
block|{
name|request
operator|.
name|setTtl
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|request
operator|.
name|setTtl
argument_list|(
name|asLong
argument_list|(
name|to
argument_list|,
name|TTLFieldMapper
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|asLong
specifier|private
name|long
name|asLong
parameter_list|(
name|Object
name|from
parameter_list|,
name|String
name|name
parameter_list|)
block|{
comment|/*                  * Stuffing a number into the map will have converted it to                  * some Number.                  * */
name|Number
name|fromNumber
decl_stmt|;
try|try
block|{
name|fromNumber
operator|=
operator|(
name|Number
operator|)
name|from
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|name
operator|+
literal|" may only be set to an int or a long but was ["
operator|+
name|from
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|long
name|l
init|=
name|fromNumber
operator|.
name|longValue
argument_list|()
decl_stmt|;
comment|// Check that we didn't round when we fetched the value.
if|if
condition|(
name|fromNumber
operator|.
name|doubleValue
argument_list|()
operator|!=
name|l
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|name
operator|+
literal|" may only be set to an int or a long but was ["
operator|+
name|from
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|l
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

