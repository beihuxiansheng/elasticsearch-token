begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.ingest.transport.simulate
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
operator|.
name|simulate
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
name|ingest
operator|.
name|IngestDocument
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
name|ingest
operator|.
name|processor
operator|.
name|Processor
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_class
DECL|class|SimulateExecutionService
specifier|public
class|class
name|SimulateExecutionService
block|{
DECL|field|THREAD_POOL_NAME
specifier|private
specifier|static
specifier|final
name|String
name|THREAD_POOL_NAME
init|=
name|ThreadPool
operator|.
name|Names
operator|.
name|MANAGEMENT
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
annotation|@
name|Inject
DECL|method|SimulateExecutionService
specifier|public
name|SimulateExecutionService
parameter_list|(
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
block|}
DECL|method|executeDocument
name|SimulateDocumentResult
name|executeDocument
parameter_list|(
name|Pipeline
name|pipeline
parameter_list|,
name|IngestDocument
name|ingestDocument
parameter_list|,
name|boolean
name|verbose
parameter_list|)
block|{
if|if
condition|(
name|verbose
condition|)
block|{
name|List
argument_list|<
name|SimulateProcessorResult
argument_list|>
name|processorResultList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|IngestDocument
name|currentIngestDocument
init|=
operator|new
name|IngestDocument
argument_list|(
name|ingestDocument
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pipeline
operator|.
name|getProcessors
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Processor
name|processor
init|=
name|pipeline
operator|.
name|getProcessors
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|processorId
init|=
literal|"processor["
operator|+
name|processor
operator|.
name|getType
argument_list|()
operator|+
literal|"]-"
operator|+
name|i
decl_stmt|;
try|try
block|{
name|processor
operator|.
name|execute
argument_list|(
name|currentIngestDocument
argument_list|)
expr_stmt|;
name|processorResultList
operator|.
name|add
argument_list|(
operator|new
name|SimulateProcessorResult
argument_list|(
name|processorId
argument_list|,
name|currentIngestDocument
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|processorResultList
operator|.
name|add
argument_list|(
operator|new
name|SimulateProcessorResult
argument_list|(
name|processorId
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|currentIngestDocument
operator|=
operator|new
name|IngestDocument
argument_list|(
name|currentIngestDocument
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SimulateDocumentVerboseResult
argument_list|(
name|processorResultList
argument_list|)
return|;
block|}
else|else
block|{
try|try
block|{
name|pipeline
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
return|return
operator|new
name|SimulateDocumentSimpleResult
argument_list|(
name|ingestDocument
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
operator|new
name|SimulateDocumentSimpleResult
argument_list|(
name|e
argument_list|)
return|;
block|}
block|}
block|}
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|SimulatePipelineRequest
operator|.
name|Parsed
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|SimulatePipelineResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|THREAD_POOL_NAME
argument_list|)
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{
name|List
argument_list|<
name|SimulateDocumentResult
argument_list|>
name|responses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|IngestDocument
name|ingestDocument
range|:
name|request
operator|.
name|getDocuments
argument_list|()
control|)
block|{
name|responses
operator|.
name|add
argument_list|(
name|executeDocument
argument_list|(
name|request
operator|.
name|getPipeline
argument_list|()
argument_list|,
name|ingestDocument
argument_list|,
name|request
operator|.
name|isVerbose
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|SimulatePipelineResponse
argument_list|(
name|request
operator|.
name|getPipeline
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|request
operator|.
name|isVerbose
argument_list|()
argument_list|,
name|responses
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

