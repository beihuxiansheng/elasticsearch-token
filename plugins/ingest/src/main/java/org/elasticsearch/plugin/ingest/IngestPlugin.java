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
name|action
operator|.
name|ActionModule
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
name|transport
operator|.
name|TransportClient
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
name|LifecycleComponent
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
name|Module
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
name|network
operator|.
name|NetworkModule
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
name|ProcessorsModule
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
name|AppendProcessor
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
name|ConvertProcessor
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
name|DateProcessor
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
name|FailProcessor
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
name|GeoIpProcessor
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
name|GrokProcessor
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
name|GsubProcessor
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
name|JoinProcessor
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
name|LowercaseProcessor
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
name|RemoveProcessor
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
name|RenameProcessor
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
name|SetProcessor
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
name|SplitProcessor
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
name|TrimProcessor
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
name|UppercaseProcessor
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
name|rest
operator|.
name|RestDeletePipelineAction
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
name|rest
operator|.
name|RestGetPipelineAction
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
name|rest
operator|.
name|RestIngestDisabledAction
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
name|rest
operator|.
name|RestPutPipelineAction
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
name|rest
operator|.
name|RestSimulatePipelineAction
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
name|transport
operator|.
name|IngestActionFilter
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
name|transport
operator|.
name|IngestDisabledActionFilter
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
name|transport
operator|.
name|delete
operator|.
name|DeletePipelineAction
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
name|transport
operator|.
name|delete
operator|.
name|DeletePipelineTransportAction
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
name|transport
operator|.
name|get
operator|.
name|GetPipelineAction
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
name|transport
operator|.
name|get
operator|.
name|GetPipelineTransportAction
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
name|transport
operator|.
name|put
operator|.
name|PutPipelineAction
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
name|transport
operator|.
name|put
operator|.
name|PutPipelineTransportAction
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
name|transport
operator|.
name|simulate
operator|.
name|SimulatePipelineAction
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
name|transport
operator|.
name|simulate
operator|.
name|SimulatePipelineTransportAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
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
name|ScriptModule
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
operator|.
name|settingsBuilder
import|;
end_import

begin_class
DECL|class|IngestPlugin
specifier|public
class|class
name|IngestPlugin
extends|extends
name|Plugin
block|{
DECL|field|PIPELINE_ID_PARAM_CONTEXT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|PIPELINE_ID_PARAM_CONTEXT_KEY
init|=
literal|"__pipeline_id__"
decl_stmt|;
DECL|field|PIPELINE_ID_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|PIPELINE_ID_PARAM
init|=
literal|"pipeline"
decl_stmt|;
DECL|field|PIPELINE_ALREADY_PROCESSED
specifier|public
specifier|static
specifier|final
name|String
name|PIPELINE_ALREADY_PROCESSED
init|=
literal|"ingest_already_processed"
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"ingest"
decl_stmt|;
DECL|field|NODE_INGEST_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|NODE_INGEST_SETTING
init|=
literal|"node.ingest"
decl_stmt|;
DECL|field|nodeSettings
specifier|private
specifier|final
name|Settings
name|nodeSettings
decl_stmt|;
DECL|field|ingestEnabled
specifier|private
specifier|final
name|boolean
name|ingestEnabled
decl_stmt|;
DECL|field|transportClient
specifier|private
specifier|final
name|boolean
name|transportClient
decl_stmt|;
DECL|method|IngestPlugin
specifier|public
name|IngestPlugin
parameter_list|(
name|Settings
name|nodeSettings
parameter_list|)
block|{
name|this
operator|.
name|nodeSettings
operator|=
name|nodeSettings
expr_stmt|;
name|this
operator|.
name|ingestEnabled
operator|=
name|nodeSettings
operator|.
name|getAsBoolean
argument_list|(
name|NODE_INGEST_SETTING
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|transportClient
operator|=
name|TransportClient
operator|.
name|CLIENT_TYPE
operator|.
name|equals
argument_list|(
name|nodeSettings
operator|.
name|get
argument_list|(
name|Client
operator|.
name|CLIENT_TYPE_SETTING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"Plugin that allows to configure pipelines to preprocess documents before indexing"
return|;
block|}
annotation|@
name|Override
DECL|method|nodeModules
specifier|public
name|Collection
argument_list|<
name|Module
argument_list|>
name|nodeModules
parameter_list|()
block|{
if|if
condition|(
name|transportClient
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|IngestModule
argument_list|(
name|ingestEnabled
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|nodeServices
specifier|public
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|LifecycleComponent
argument_list|>
argument_list|>
name|nodeServices
parameter_list|()
block|{
if|if
condition|(
name|transportClient
operator|||
name|ingestEnabled
operator|==
literal|false
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|IngestBootstrapper
operator|.
name|class
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|additionalSettings
specifier|public
name|Settings
name|additionalSettings
parameter_list|()
block|{
return|return
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|PipelineExecutionService
operator|.
name|additionalSettings
argument_list|(
name|nodeSettings
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|ProcessorsModule
name|processorsModule
parameter_list|)
block|{
if|if
condition|(
name|ingestEnabled
condition|)
block|{
name|processorsModule
operator|.
name|addProcessor
argument_list|(
name|GeoIpProcessor
operator|.
name|TYPE
argument_list|,
parameter_list|(
name|environment
parameter_list|,
name|templateService
parameter_list|)
lambda|->
operator|new
name|GeoIpProcessor
operator|.
name|Factory
argument_list|(
name|environment
operator|.
name|configFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|processorsModule
operator|.
name|addProcessor
argument_list|(
name|GrokProcessor
operator|.
name|TYPE
argument_list|,
parameter_list|(
name|environment
parameter_list|,
name|templateService
parameter_list|)
lambda|->
operator|new
name|GrokProcessor
operator|.
name|Factory
argument_list|(
name|environment
operator|.
name|configFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|processorsModule
operator|.
name|addProcessor
argument_list|(
name|DateProcessor
operator|.
name|TYPE
argument_list|,
parameter_list|(
name|environment
parameter_list|,
name|templateService
parameter_list|)
lambda|->
operator|new
name|DateProcessor
operator|.
name|Factory
argument_list|()
argument_list|)
expr_stmt|;
name|processorsModule
operator|.
name|addProcessor
argument_list|(
name|SetProcessor
operator|.
name|TYPE
argument_list|,
parameter_list|(
name|environment
parameter_list|,
name|templateService
parameter_list|)
lambda|->
operator|new
name|SetProcessor
operator|.
name|Factory
argument_list|(
name|templateService
argument_list|)
argument_list|)
expr_stmt|;
name|processorsModule
operator|.
name|addProcessor
argument_list|(
name|AppendProcessor
operator|.
name|TYPE
argument_list|,
parameter_list|(
name|environment
parameter_list|,
name|templateService
parameter_list|)
lambda|->
operator|new
name|AppendProcessor
operator|.
name|Factory
argument_list|(
name|templateService
argument_list|)
argument_list|)
expr_stmt|;
name|processorsModule
operator|.
name|addProcessor
argument_list|(
name|RenameProcessor
operator|.
name|TYPE
argument_list|,
parameter_list|(
name|environment
parameter_list|,
name|templateService
parameter_list|)
lambda|->
operator|new
name|RenameProcessor
operator|.
name|Factory
argument_list|()
argument_list|)
expr_stmt|;
name|processorsModule
operator|.
name|addProcessor
argument_list|(
name|RemoveProcessor
operator|.
name|TYPE
argument_list|,
parameter_list|(
name|environment
parameter_list|,
name|templateService
parameter_list|)
lambda|->
operator|new
name|RemoveProcessor
operator|.
name|Factory
argument_list|(
name|templateService
argument_list|)
argument_list|)
expr_stmt|;
name|processorsModule
operator|.
name|addProcessor
argument_list|(
name|SplitProcessor
operator|.
name|TYPE
argument_list|,
parameter_list|(
name|environment
parameter_list|,
name|templateService
parameter_list|)
lambda|->
operator|new
name|SplitProcessor
operator|.
name|Factory
argument_list|()
argument_list|)
expr_stmt|;
name|processorsModule
operator|.
name|addProcessor
argument_list|(
name|JoinProcessor
operator|.
name|TYPE
argument_list|,
parameter_list|(
name|environment
parameter_list|,
name|templateService
parameter_list|)
lambda|->
operator|new
name|JoinProcessor
operator|.
name|Factory
argument_list|()
argument_list|)
expr_stmt|;
name|processorsModule
operator|.
name|addProcessor
argument_list|(
name|UppercaseProcessor
operator|.
name|TYPE
argument_list|,
parameter_list|(
name|environment
parameter_list|,
name|templateService
parameter_list|)
lambda|->
operator|new
name|UppercaseProcessor
operator|.
name|Factory
argument_list|()
argument_list|)
expr_stmt|;
name|processorsModule
operator|.
name|addProcessor
argument_list|(
name|LowercaseProcessor
operator|.
name|TYPE
argument_list|,
parameter_list|(
name|environment
parameter_list|,
name|templateService
parameter_list|)
lambda|->
operator|new
name|LowercaseProcessor
operator|.
name|Factory
argument_list|()
argument_list|)
expr_stmt|;
name|processorsModule
operator|.
name|addProcessor
argument_list|(
name|TrimProcessor
operator|.
name|TYPE
argument_list|,
parameter_list|(
name|environment
parameter_list|,
name|templateService
parameter_list|)
lambda|->
operator|new
name|TrimProcessor
operator|.
name|Factory
argument_list|()
argument_list|)
expr_stmt|;
name|processorsModule
operator|.
name|addProcessor
argument_list|(
name|ConvertProcessor
operator|.
name|TYPE
argument_list|,
parameter_list|(
name|environment
parameter_list|,
name|templateService
parameter_list|)
lambda|->
operator|new
name|ConvertProcessor
operator|.
name|Factory
argument_list|()
argument_list|)
expr_stmt|;
name|processorsModule
operator|.
name|addProcessor
argument_list|(
name|GsubProcessor
operator|.
name|TYPE
argument_list|,
parameter_list|(
name|environment
parameter_list|,
name|templateService
parameter_list|)
lambda|->
operator|new
name|GsubProcessor
operator|.
name|Factory
argument_list|()
argument_list|)
expr_stmt|;
name|processorsModule
operator|.
name|addProcessor
argument_list|(
name|FailProcessor
operator|.
name|TYPE
argument_list|,
parameter_list|(
name|environment
parameter_list|,
name|templateService
parameter_list|)
lambda|->
operator|new
name|FailProcessor
operator|.
name|Factory
argument_list|(
name|templateService
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|ActionModule
name|module
parameter_list|)
block|{
if|if
condition|(
name|transportClient
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|ingestEnabled
condition|)
block|{
name|module
operator|.
name|registerFilter
argument_list|(
name|IngestActionFilter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|module
operator|.
name|registerFilter
argument_list|(
name|IngestDisabledActionFilter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ingestEnabled
condition|)
block|{
name|module
operator|.
name|registerAction
argument_list|(
name|PutPipelineAction
operator|.
name|INSTANCE
argument_list|,
name|PutPipelineTransportAction
operator|.
name|class
argument_list|)
expr_stmt|;
name|module
operator|.
name|registerAction
argument_list|(
name|GetPipelineAction
operator|.
name|INSTANCE
argument_list|,
name|GetPipelineTransportAction
operator|.
name|class
argument_list|)
expr_stmt|;
name|module
operator|.
name|registerAction
argument_list|(
name|DeletePipelineAction
operator|.
name|INSTANCE
argument_list|,
name|DeletePipelineTransportAction
operator|.
name|class
argument_list|)
expr_stmt|;
name|module
operator|.
name|registerAction
argument_list|(
name|SimulatePipelineAction
operator|.
name|INSTANCE
argument_list|,
name|SimulatePipelineTransportAction
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|NetworkModule
name|networkModule
parameter_list|)
block|{
if|if
condition|(
name|transportClient
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|ingestEnabled
condition|)
block|{
name|networkModule
operator|.
name|registerRestHandler
argument_list|(
name|RestPutPipelineAction
operator|.
name|class
argument_list|)
expr_stmt|;
name|networkModule
operator|.
name|registerRestHandler
argument_list|(
name|RestGetPipelineAction
operator|.
name|class
argument_list|)
expr_stmt|;
name|networkModule
operator|.
name|registerRestHandler
argument_list|(
name|RestDeletePipelineAction
operator|.
name|class
argument_list|)
expr_stmt|;
name|networkModule
operator|.
name|registerRestHandler
argument_list|(
name|RestSimulatePipelineAction
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|networkModule
operator|.
name|registerRestHandler
argument_list|(
name|RestIngestDisabledAction
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|ScriptModule
name|module
parameter_list|)
block|{
name|module
operator|.
name|registerScriptContext
argument_list|(
name|InternalTemplateService
operator|.
name|INGEST_SCRIPT_CONTEXT
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

