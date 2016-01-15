begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.reindex
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
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
name|BulkItemResponse
operator|.
name|Failure
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
name|search
operator|.
name|ShardSearchFailure
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
name|cluster
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
name|unit
operator|.
name|TimeValue
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
name|search
operator|.
name|SearchHit
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
name|List
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
name|ReindexResponse
argument_list|>
block|{
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
name|ReindexResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|validateAgainstAliases
argument_list|(
name|request
operator|.
name|getSource
argument_list|()
argument_list|,
name|request
operator|.
name|getDestination
argument_list|()
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|autoCreateIndex
argument_list|,
name|clusterService
operator|.
name|state
argument_list|()
argument_list|)
expr_stmt|;
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
name|scriptService
argument_list|,
name|client
argument_list|,
name|threadPool
argument_list|,
name|request
argument_list|,
name|listener
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
name|ReindexResponse
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
comment|/**      * Throws an ActionRequestValidationException if the request tries to index      * back into the same index or into an index that points to two indexes.      * This cannot be done during request validation because the cluster state      * isn't available then. Package private for testing.      */
DECL|method|validateAgainstAliases
specifier|static
name|String
name|validateAgainstAliases
parameter_list|(
name|SearchRequest
name|source
parameter_list|,
name|IndexRequest
name|destination
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
name|concreteIndices
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
name|concreteIndices
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
return|return
name|target
return|;
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
argument_list|,
name|ReindexResponse
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
name|ScriptService
name|scriptService
parameter_list|,
name|Client
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
name|ReindexResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|super
argument_list|(
name|task
argument_list|,
name|logger
argument_list|,
name|scriptService
argument_list|,
name|client
argument_list|,
name|threadPool
argument_list|,
name|request
argument_list|,
name|request
operator|.
name|getSource
argument_list|()
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|buildIndexRequest
specifier|protected
name|IndexRequest
name|buildIndexRequest
parameter_list|(
name|SearchHit
name|doc
parameter_list|)
block|{
name|IndexRequest
name|index
init|=
operator|new
name|IndexRequest
argument_list|(
name|mainRequest
operator|.
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
comment|// We want the index from the copied request, not the doc.
name|index
operator|.
name|id
argument_list|(
name|doc
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|.
name|type
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|/*                  * Default to doc's type if not specified in request so its easy                  * to do a scripted update.                  */
name|index
operator|.
name|type
argument_list|(
name|doc
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|index
operator|.
name|source
argument_list|(
name|doc
operator|.
name|sourceRef
argument_list|()
argument_list|)
expr_stmt|;
comment|/*              * Internal versioning can just use what we copied from the              * destionation request. Otherwise we assume we're using external              * versioning and use the doc's version.              */
if|if
condition|(
name|index
operator|.
name|versionType
argument_list|()
operator|!=
name|INTERNAL
condition|)
block|{
name|index
operator|.
name|version
argument_list|(
name|doc
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|index
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
name|IndexRequest
name|index
parameter_list|,
name|SearchHit
name|doc
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
name|index
argument_list|,
name|doc
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
name|index
argument_list|,
name|doc
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"discard"
case|:
name|index
operator|.
name|routing
argument_list|(
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
annotation|@
name|Override
DECL|method|buildResponse
specifier|protected
name|ReindexResponse
name|buildResponse
parameter_list|(
name|TimeValue
name|took
parameter_list|,
name|List
argument_list|<
name|Failure
argument_list|>
name|indexingFailures
parameter_list|,
name|List
argument_list|<
name|ShardSearchFailure
argument_list|>
name|searchFailures
parameter_list|)
block|{
return|return
operator|new
name|ReindexResponse
argument_list|(
name|took
argument_list|,
name|task
operator|.
name|getStatus
argument_list|()
argument_list|,
name|indexingFailures
argument_list|,
name|searchFailures
argument_list|)
return|;
block|}
comment|/*          * Methods below here handle script updating the index request. They try          * to be pretty liberal with regards to types because script are often          * dynamically typed.          */
annotation|@
name|Override
DECL|method|scriptChangedIndex
specifier|protected
name|void
name|scriptChangedIndex
parameter_list|(
name|IndexRequest
name|index
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
name|index
operator|.
name|index
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
name|IndexRequest
name|index
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
name|index
operator|.
name|type
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
name|IndexRequest
name|index
parameter_list|,
name|Object
name|to
parameter_list|)
block|{
name|index
operator|.
name|id
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
name|IndexRequest
name|index
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
name|index
operator|.
name|version
argument_list|(
name|Versions
operator|.
name|MATCH_ANY
argument_list|)
operator|.
name|versionType
argument_list|(
name|INTERNAL
argument_list|)
expr_stmt|;
return|return;
block|}
name|index
operator|.
name|version
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
annotation|@
name|Override
DECL|method|scriptChangedParent
specifier|protected
name|void
name|scriptChangedParent
parameter_list|(
name|IndexRequest
name|index
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
name|index
operator|.
name|parent
argument_list|(
name|routing
argument_list|)
operator|.
name|routing
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
name|IndexRequest
name|index
parameter_list|,
name|Object
name|to
parameter_list|)
block|{
name|index
operator|.
name|routing
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
name|IndexRequest
name|index
parameter_list|,
name|Object
name|to
parameter_list|)
block|{
name|index
operator|.
name|timestamp
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
name|IndexRequest
name|index
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
name|index
operator|.
name|ttl
argument_list|(
operator|(
name|TimeValue
operator|)
literal|null
argument_list|)
expr_stmt|;
return|return;
block|}
name|index
operator|.
name|ttl
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
comment|/*              * Stuffing a number into the map will have converted it to              * some Number.              */
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
end_class

end_unit

