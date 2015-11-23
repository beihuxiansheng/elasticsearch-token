begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.ingest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|ingest
package|;
end_package

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
name|support
operator|.
name|LoggerMessageFormat
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
name|concurrent
operator|.
name|EsExecutors
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
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_class
DECL|class|PipelineExecutionService
specifier|public
class|class
name|PipelineExecutionService
block|{
DECL|field|THREAD_POOL_NAME
specifier|static
specifier|final
name|String
name|THREAD_POOL_NAME
init|=
name|IngestPlugin
operator|.
name|NAME
decl_stmt|;
DECL|field|store
specifier|private
specifier|final
name|PipelineStore
name|store
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
annotation|@
name|Inject
DECL|method|PipelineExecutionService
specifier|public
name|PipelineExecutionService
parameter_list|(
name|PipelineStore
name|store
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
block|}
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|IngestDocument
name|ingestDocument
parameter_list|,
name|String
name|pipelineId
parameter_list|,
name|Listener
name|listener
parameter_list|)
block|{
name|Pipeline
name|pipeline
init|=
name|store
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
name|listener
operator|.
name|failed
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
name|LoggerMessageFormat
operator|.
name|format
argument_list|(
literal|"pipeline with id [{}] does not exist"
argument_list|,
name|pipelineId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|threadPool
operator|.
name|executor
argument_list|(
name|THREAD_POOL_NAME
argument_list|)
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
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
name|listener
operator|.
name|executed
argument_list|(
name|ingestDocument
argument_list|)
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
name|failed
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|interface|Listener
specifier|public
interface|interface
name|Listener
block|{
DECL|method|executed
name|void
name|executed
parameter_list|(
name|IngestDocument
name|ingestDocument
parameter_list|)
function_decl|;
DECL|method|failed
name|void
name|failed
parameter_list|(
name|Exception
name|e
parameter_list|)
function_decl|;
block|}
DECL|method|additionalSettings
specifier|public
specifier|static
name|Settings
name|additionalSettings
parameter_list|(
name|Settings
name|nodeSettings
parameter_list|)
block|{
name|Settings
name|settings
init|=
name|nodeSettings
operator|.
name|getAsSettings
argument_list|(
literal|"threadpool."
operator|+
name|THREAD_POOL_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|settings
operator|.
name|names
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// the TP is already configured in the node settings
comment|// no need for additional settings
return|return
name|Settings
operator|.
name|EMPTY
return|;
block|}
name|int
name|availableProcessors
init|=
name|EsExecutors
operator|.
name|boundedNumberOfProcessors
argument_list|(
name|nodeSettings
argument_list|)
decl_stmt|;
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"threadpool."
operator|+
name|THREAD_POOL_NAME
operator|+
literal|".type"
argument_list|,
literal|"fixed"
argument_list|)
operator|.
name|put
argument_list|(
literal|"threadpool."
operator|+
name|THREAD_POOL_NAME
operator|+
literal|".size"
argument_list|,
name|availableProcessors
argument_list|)
operator|.
name|put
argument_list|(
literal|"threadpool."
operator|+
name|THREAD_POOL_NAME
operator|+
literal|".queue_size"
argument_list|,
literal|200
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

