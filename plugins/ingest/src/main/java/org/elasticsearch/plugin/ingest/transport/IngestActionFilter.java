begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.ingest.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|transport
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
name|ActionRequest
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
name|ActionResponse
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
name|ActionFilter
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
name|ActionFilterChain
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
name|component
operator|.
name|AbstractComponent
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
name|ingest
operator|.
name|Data
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|IngestPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
operator|.
name|PipelineExecutionService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_class
DECL|class|IngestActionFilter
specifier|public
class|class
name|IngestActionFilter
extends|extends
name|AbstractComponent
implements|implements
name|ActionFilter
block|{
DECL|field|executionService
specifier|private
specifier|final
name|PipelineExecutionService
name|executionService
decl_stmt|;
annotation|@
name|Inject
DECL|method|IngestActionFilter
specifier|public
name|IngestActionFilter
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|PipelineExecutionService
name|executionService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|executionService
operator|=
name|executionService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply
specifier|public
name|void
name|apply
parameter_list|(
name|String
name|action
parameter_list|,
name|ActionRequest
name|request
parameter_list|,
name|ActionListener
name|listener
parameter_list|,
name|ActionFilterChain
name|chain
parameter_list|)
block|{
name|String
name|pipelineId
init|=
name|request
operator|.
name|getFromContext
argument_list|(
name|IngestPlugin
operator|.
name|INGEST_CONTEXT_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|pipelineId
operator|==
literal|null
condition|)
block|{
name|pipelineId
operator|=
name|request
operator|.
name|getHeader
argument_list|(
name|IngestPlugin
operator|.
name|INGEST_PARAM
argument_list|)
expr_stmt|;
if|if
condition|(
name|pipelineId
operator|==
literal|null
condition|)
block|{
name|chain
operator|.
name|proceed
argument_list|(
name|action
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
if|if
condition|(
name|request
operator|instanceof
name|IndexRequest
condition|)
block|{
name|processIndexRequest
argument_list|(
name|action
argument_list|,
name|listener
argument_list|,
name|chain
argument_list|,
operator|(
name|IndexRequest
operator|)
name|request
argument_list|,
name|pipelineId
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|request
operator|instanceof
name|BulkRequest
condition|)
block|{
name|BulkRequest
name|bulkRequest
init|=
operator|(
name|BulkRequest
operator|)
name|request
decl_stmt|;
name|processBulkIndexRequest
argument_list|(
name|action
argument_list|,
name|listener
argument_list|,
name|chain
argument_list|,
name|bulkRequest
argument_list|,
name|pipelineId
argument_list|,
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|chain
operator|.
name|proceed
argument_list|(
name|action
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|apply
specifier|public
name|void
name|apply
parameter_list|(
name|String
name|action
parameter_list|,
name|ActionResponse
name|response
parameter_list|,
name|ActionListener
name|listener
parameter_list|,
name|ActionFilterChain
name|chain
parameter_list|)
block|{
name|chain
operator|.
name|proceed
argument_list|(
name|action
argument_list|,
name|response
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|processIndexRequest
name|void
name|processIndexRequest
parameter_list|(
name|String
name|action
parameter_list|,
name|ActionListener
name|listener
parameter_list|,
name|ActionFilterChain
name|chain
parameter_list|,
name|IndexRequest
name|indexRequest
parameter_list|,
name|String
name|pipelineId
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sourceAsMap
init|=
name|indexRequest
operator|.
name|sourceAsMap
argument_list|()
decl_stmt|;
name|Data
name|data
init|=
operator|new
name|Data
argument_list|(
name|indexRequest
operator|.
name|index
argument_list|()
argument_list|,
name|indexRequest
operator|.
name|type
argument_list|()
argument_list|,
name|indexRequest
operator|.
name|id
argument_list|()
argument_list|,
name|sourceAsMap
argument_list|)
decl_stmt|;
name|executionService
operator|.
name|execute
argument_list|(
name|data
argument_list|,
name|pipelineId
argument_list|,
operator|new
name|PipelineExecutionService
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|executed
parameter_list|(
name|Data
name|data
parameter_list|)
block|{
if|if
condition|(
name|data
operator|.
name|isModified
argument_list|()
condition|)
block|{
name|indexRequest
operator|.
name|source
argument_list|(
name|data
operator|.
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|chain
operator|.
name|proceed
argument_list|(
name|action
argument_list|,
name|indexRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|failed
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"failed to execute pipeline [{}]"
argument_list|,
name|e
argument_list|,
name|pipelineId
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|// TODO: rethink how to deal with bulk requests:
comment|// This doesn't scale very well for a single bulk requests, so it would be great if a bulk requests could be broken up into several chunks so that the ingesting can be paralized
comment|// on the other hand if there are many index/bulk requests then breaking up bulk requests isn't going to help much.
comment|// I think the execution service should be smart enough about when it should break things up in chunks based on the ingest threadpool usage,
comment|// this means that the contract of the execution service should change in order to accept multiple data instances.
DECL|method|processBulkIndexRequest
name|void
name|processBulkIndexRequest
parameter_list|(
name|String
name|action
parameter_list|,
name|ActionListener
name|listener
parameter_list|,
name|ActionFilterChain
name|chain
parameter_list|,
name|BulkRequest
name|bulkRequest
parameter_list|,
name|String
name|pipelineId
parameter_list|,
name|Iterator
argument_list|<
name|ActionRequest
argument_list|>
name|requests
parameter_list|)
block|{
if|if
condition|(
operator|!
name|requests
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|chain
operator|.
name|proceed
argument_list|(
name|action
argument_list|,
name|bulkRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
return|return;
block|}
name|ActionRequest
name|actionRequest
init|=
name|requests
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|actionRequest
operator|instanceof
name|IndexRequest
operator|)
condition|)
block|{
name|processBulkIndexRequest
argument_list|(
name|action
argument_list|,
name|listener
argument_list|,
name|chain
argument_list|,
name|bulkRequest
argument_list|,
name|pipelineId
argument_list|,
name|requests
argument_list|)
expr_stmt|;
return|return;
block|}
name|IndexRequest
name|indexRequest
init|=
operator|(
name|IndexRequest
operator|)
name|actionRequest
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sourceAsMap
init|=
name|indexRequest
operator|.
name|sourceAsMap
argument_list|()
decl_stmt|;
name|Data
name|data
init|=
operator|new
name|Data
argument_list|(
name|indexRequest
operator|.
name|index
argument_list|()
argument_list|,
name|indexRequest
operator|.
name|type
argument_list|()
argument_list|,
name|indexRequest
operator|.
name|id
argument_list|()
argument_list|,
name|sourceAsMap
argument_list|)
decl_stmt|;
name|executionService
operator|.
name|execute
argument_list|(
name|data
argument_list|,
name|pipelineId
argument_list|,
operator|new
name|PipelineExecutionService
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|executed
parameter_list|(
name|Data
name|data
parameter_list|)
block|{
if|if
condition|(
name|data
operator|.
name|isModified
argument_list|()
condition|)
block|{
name|indexRequest
operator|.
name|source
argument_list|(
name|data
operator|.
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|processBulkIndexRequest
argument_list|(
name|action
argument_list|,
name|listener
argument_list|,
name|chain
argument_list|,
name|bulkRequest
argument_list|,
name|pipelineId
argument_list|,
name|requests
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|failed
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"failed to execute pipeline [{}]"
argument_list|,
name|e
argument_list|,
name|pipelineId
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|order
specifier|public
name|int
name|order
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
block|}
end_class

end_unit

