begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.admin.indices.template.put
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|template
operator|.
name|put
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
name|admin
operator|.
name|indices
operator|.
name|template
operator|.
name|put
operator|.
name|PutIndexTemplateRequest
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
name|admin
operator|.
name|indices
operator|.
name|template
operator|.
name|put
operator|.
name|PutIndexTemplateResponse
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
name|rest
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|support
operator|.
name|AcknowledgedRestListener
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RestPutIndexTemplateAction
specifier|public
class|class
name|RestPutIndexTemplateAction
extends|extends
name|BaseRestHandler
block|{
annotation|@
name|Inject
DECL|method|RestPutIndexTemplateAction
specifier|public
name|RestPutIndexTemplateAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
parameter_list|,
name|Client
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|controller
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|PUT
argument_list|,
literal|"/_template/{name}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|POST
argument_list|,
literal|"/_template/{name}"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
annotation|@
name|Override
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|,
specifier|final
name|RestChannel
name|channel
parameter_list|,
specifier|final
name|Client
name|client
parameter_list|)
block|{
name|PutIndexTemplateRequest
name|putRequest
init|=
operator|new
name|PutIndexTemplateRequest
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"name"
argument_list|)
argument_list|)
decl_stmt|;
name|putRequest
operator|.
name|listenerThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|putRequest
operator|.
name|template
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"template"
argument_list|,
name|putRequest
operator|.
name|template
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|putRequest
operator|.
name|order
argument_list|(
name|request
operator|.
name|paramAsInt
argument_list|(
literal|"order"
argument_list|,
name|putRequest
operator|.
name|order
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|putRequest
operator|.
name|masterNodeTimeout
argument_list|(
name|request
operator|.
name|paramAsTime
argument_list|(
literal|"master_timeout"
argument_list|,
name|putRequest
operator|.
name|masterNodeTimeout
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|putRequest
operator|.
name|create
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"create"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|putRequest
operator|.
name|cause
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"cause"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|putRequest
operator|.
name|source
argument_list|(
name|request
operator|.
name|content
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|putTemplate
argument_list|(
name|putRequest
argument_list|,
operator|new
name|AcknowledgedRestListener
argument_list|<
name|PutIndexTemplateResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

