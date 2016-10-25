begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
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
name|util
operator|.
name|BigArrays
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|ParsedScrollId
operator|.
name|QUERY_AND_FETCH_TYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|ParsedScrollId
operator|.
name|QUERY_THEN_FETCH_TYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|TransportSearchHelper
operator|.
name|parseScrollId
import|;
end_import

begin_class
DECL|class|TransportSearchScrollAction
specifier|public
class|class
name|TransportSearchScrollAction
extends|extends
name|HandledTransportAction
argument_list|<
name|SearchScrollRequest
argument_list|,
name|SearchResponse
argument_list|>
block|{
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|searchTransportService
specifier|private
specifier|final
name|SearchTransportService
name|searchTransportService
decl_stmt|;
DECL|field|searchPhaseController
specifier|private
specifier|final
name|SearchPhaseController
name|searchPhaseController
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportSearchScrollAction
specifier|public
name|TransportSearchScrollAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|SearchScrollAction
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
name|SearchScrollRequest
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
name|searchTransportService
operator|=
operator|new
name|SearchTransportService
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|)
expr_stmt|;
name|this
operator|.
name|searchPhaseController
operator|=
operator|new
name|SearchPhaseController
argument_list|(
name|settings
argument_list|,
name|bigArrays
argument_list|,
name|scriptService
argument_list|,
name|clusterService
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
specifier|final
name|void
name|doExecute
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
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"the task parameter is required"
argument_list|)
throw|;
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
name|SearchScrollRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
try|try
block|{
name|ParsedScrollId
name|scrollId
init|=
name|parseScrollId
argument_list|(
name|request
operator|.
name|scrollId
argument_list|()
argument_list|)
decl_stmt|;
name|AbstractAsyncAction
name|action
decl_stmt|;
switch|switch
condition|(
name|scrollId
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|QUERY_THEN_FETCH_TYPE
case|:
name|action
operator|=
operator|new
name|SearchScrollQueryThenFetchAsyncAction
argument_list|(
name|logger
argument_list|,
name|clusterService
argument_list|,
name|searchTransportService
argument_list|,
name|searchPhaseController
argument_list|,
name|request
argument_list|,
operator|(
name|SearchTask
operator|)
name|task
argument_list|,
name|scrollId
argument_list|,
name|listener
argument_list|)
expr_stmt|;
break|break;
case|case
name|QUERY_AND_FETCH_TYPE
case|:
name|action
operator|=
operator|new
name|SearchScrollQueryAndFetchAsyncAction
argument_list|(
name|logger
argument_list|,
name|clusterService
argument_list|,
name|searchTransportService
argument_list|,
name|searchPhaseController
argument_list|,
name|request
argument_list|,
operator|(
name|SearchTask
operator|)
name|task
argument_list|,
name|scrollId
argument_list|,
name|listener
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Scroll id type ["
operator|+
name|scrollId
operator|.
name|getType
argument_list|()
operator|+
literal|"] unrecognized"
argument_list|)
throw|;
block|}
name|action
operator|.
name|start
argument_list|()
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
block|}
block|}
end_class

end_unit

