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
name|ingest
operator|.
name|Pipeline
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
name|PipelineStore
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

begin_class
DECL|class|IngestActionFilter
specifier|public
class|class
name|IngestActionFilter
extends|extends
name|ActionFilter
operator|.
name|Simple
block|{
DECL|field|pipelineStore
specifier|private
specifier|final
name|PipelineStore
name|pipelineStore
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
name|PipelineStore
name|pipelineStore
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|pipelineStore
operator|=
name|pipelineStore
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply
specifier|protected
name|boolean
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
name|INGEST_HTTP_PARAM
argument_list|)
expr_stmt|;
if|if
condition|(
name|pipelineId
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
name|Pipeline
name|pipeline
init|=
name|pipelineStore
operator|.
name|get
argument_list|(
name|pipelineId
argument_list|)
decl_stmt|;
if|if
condition|(
name|pipeline
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
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
operator|(
name|IndexRequest
operator|)
name|request
argument_list|,
name|pipeline
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
name|List
argument_list|<
name|ActionRequest
argument_list|>
name|actionRequests
init|=
name|bulkRequest
operator|.
name|requests
argument_list|()
decl_stmt|;
for|for
control|(
name|ActionRequest
name|actionRequest
range|:
name|actionRequests
control|)
block|{
if|if
condition|(
name|actionRequest
operator|instanceof
name|IndexRequest
condition|)
block|{
name|processIndexRequest
argument_list|(
operator|(
name|IndexRequest
operator|)
name|actionRequest
argument_list|,
name|pipeline
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|processIndexRequest
name|void
name|processIndexRequest
parameter_list|(
name|IndexRequest
name|indexRequest
parameter_list|,
name|Pipeline
name|pipeline
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
name|pipeline
operator|.
name|execute
argument_list|(
name|data
argument_list|)
expr_stmt|;
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
annotation|@
name|Override
DECL|method|apply
specifier|protected
name|boolean
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
parameter_list|)
block|{
return|return
literal|true
return|;
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

