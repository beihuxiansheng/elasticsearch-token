begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.ingest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
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
name|Action
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
name|ElasticsearchClient
import|;
end_import

begin_class
DECL|class|SimulatePipelineAction
specifier|public
class|class
name|SimulatePipelineAction
extends|extends
name|Action
argument_list|<
name|SimulatePipelineRequest
argument_list|,
name|SimulatePipelineResponse
argument_list|,
name|SimulatePipelineRequestBuilder
argument_list|>
block|{
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|SimulatePipelineAction
name|INSTANCE
init|=
operator|new
name|SimulatePipelineAction
argument_list|()
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"cluster:admin/ingest/pipeline/simulate"
decl_stmt|;
DECL|method|SimulatePipelineAction
specifier|public
name|SimulatePipelineAction
parameter_list|()
block|{
name|super
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newRequestBuilder
specifier|public
name|SimulatePipelineRequestBuilder
name|newRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|)
block|{
return|return
operator|new
name|SimulatePipelineRequestBuilder
argument_list|(
name|client
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|public
name|SimulatePipelineResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|SimulatePipelineResponse
argument_list|()
return|;
block|}
block|}
end_class

end_unit

