begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.ingest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
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
name|ingest
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
name|rest
operator|.
name|RestChannel
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
name|RestController
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
name|RestFilter
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
name|RestFilterChain
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
name|RestRequest
import|;
end_import

begin_class
DECL|class|IngestRestFilter
specifier|public
class|class
name|IngestRestFilter
extends|extends
name|RestFilter
block|{
annotation|@
name|Inject
DECL|method|IngestRestFilter
specifier|public
name|IngestRestFilter
parameter_list|(
name|RestController
name|controller
parameter_list|)
block|{
name|controller
operator|.
name|registerFilter
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|,
name|RestFilterChain
name|filterChain
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|request
operator|.
name|hasParam
argument_list|(
name|IngestActionFilter
operator|.
name|PIPELINE_ID_PARAM
argument_list|)
condition|)
block|{
name|request
operator|.
name|putInContext
argument_list|(
name|IngestActionFilter
operator|.
name|PIPELINE_ID_PARAM_CONTEXT_KEY
argument_list|,
name|request
operator|.
name|param
argument_list|(
name|IngestActionFilter
operator|.
name|PIPELINE_ID_PARAM
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|filterChain
operator|.
name|continueProcessing
argument_list|(
name|request
argument_list|,
name|channel
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

