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
name|support
operator|.
name|master
operator|.
name|MasterNodeReadOperationRequestBuilder
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
DECL|class|GetPipelineRequestBuilder
specifier|public
class|class
name|GetPipelineRequestBuilder
extends|extends
name|MasterNodeReadOperationRequestBuilder
argument_list|<
name|GetPipelineRequest
argument_list|,
name|GetPipelineResponse
argument_list|,
name|GetPipelineRequestBuilder
argument_list|>
block|{
DECL|method|GetPipelineRequestBuilder
specifier|public
name|GetPipelineRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|,
name|GetPipelineAction
name|action
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
name|action
argument_list|,
operator|new
name|GetPipelineRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|GetPipelineRequestBuilder
specifier|public
name|GetPipelineRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|,
name|GetPipelineAction
name|action
parameter_list|,
name|String
index|[]
name|ids
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
name|action
argument_list|,
operator|new
name|GetPipelineRequest
argument_list|(
name|ids
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

