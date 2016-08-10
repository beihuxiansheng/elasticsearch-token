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
name|VersionType
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
name|IdFieldMapper
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
name|IndexFieldMapper
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
name|ParentFieldMapper
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
name|RoutingFieldMapper
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
name|TimestampFieldMapper
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
name|TypeFieldMapper
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
name|BiFunction
import|;
end_import

begin_class
DECL|class|TransportUpdateByQueryAction
specifier|public
class|class
name|TransportUpdateByQueryAction
extends|extends
name|HandledTransportAction
argument_list|<
name|UpdateByQueryRequest
argument_list|,
name|BulkIndexByScrollResponse
argument_list|>
block|{
DECL|field|client
specifier|private
specifier|final
name|Client
name|client
decl_stmt|;
DECL|field|scriptService
specifier|private
specifier|final
name|ScriptService
name|scriptService
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportUpdateByQueryAction
specifier|public
name|TransportUpdateByQueryAction
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
name|Client
name|client
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|UpdateByQueryAction
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
name|UpdateByQueryRequest
operator|::
operator|new
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|scriptService
operator|=
name|scriptService
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
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
name|UpdateByQueryRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|BulkIndexByScrollResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|ClusterState
name|state
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
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
name|UpdateByQueryRequest
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
comment|/**      * Simple implementation of update-by-query using scrolling and bulk.      */
DECL|class|AsyncIndexBySearchAction
specifier|static
class|class
name|AsyncIndexBySearchAction
extends|extends
name|AbstractAsyncBulkIndexByScrollAction
argument_list|<
name|UpdateByQueryRequest
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
name|UpdateByQueryRequest
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
DECL|method|needsSourceDocumentVersions
specifier|protected
name|boolean
name|needsSourceDocumentVersions
parameter_list|()
block|{
comment|/*              * We always need the version of the source document so we can report a version conflict if we try to delete it and it has been              * changed.              */
return|return
literal|true
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
name|UpdateByQueryScriptApplier
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
name|index
operator|.
name|index
argument_list|(
name|doc
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
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
name|index
operator|.
name|versionType
argument_list|(
name|VersionType
operator|.
name|INTERNAL
argument_list|)
expr_stmt|;
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
name|index
operator|.
name|setPipeline
argument_list|(
name|mainRequest
operator|.
name|getPipeline
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|wrap
argument_list|(
name|index
argument_list|)
return|;
block|}
DECL|class|UpdateByQueryScriptApplier
class|class
name|UpdateByQueryScriptApplier
extends|extends
name|ScriptApplier
block|{
DECL|method|UpdateByQueryScriptApplier
name|UpdateByQueryScriptApplier
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
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Modifying ["
operator|+
name|IndexFieldMapper
operator|.
name|NAME
operator|+
literal|"] not allowed"
argument_list|)
throw|;
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
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Modifying ["
operator|+
name|TypeFieldMapper
operator|.
name|NAME
operator|+
literal|"] not allowed"
argument_list|)
throw|;
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
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Modifying ["
operator|+
name|IdFieldMapper
operator|.
name|NAME
operator|+
literal|"] not allowed"
argument_list|)
throw|;
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
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Modifying [_version] not allowed"
argument_list|)
throw|;
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
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Modifying ["
operator|+
name|RoutingFieldMapper
operator|.
name|NAME
operator|+
literal|"] not allowed"
argument_list|)
throw|;
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
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Modifying ["
operator|+
name|ParentFieldMapper
operator|.
name|NAME
operator|+
literal|"] not allowed"
argument_list|)
throw|;
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
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Modifying ["
operator|+
name|TimestampFieldMapper
operator|.
name|NAME
operator|+
literal|"] not allowed"
argument_list|)
throw|;
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
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Modifying ["
operator|+
name|TTLFieldMapper
operator|.
name|NAME
operator|+
literal|"] not allowed"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

