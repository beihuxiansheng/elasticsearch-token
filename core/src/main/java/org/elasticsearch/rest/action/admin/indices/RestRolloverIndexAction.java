begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.admin.indices
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
name|rollover
operator|.
name|RolloverRequest
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
name|BaseRestHandler
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
name|RestRequest
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
name|RestToXContentListener
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RestRolloverIndexAction
specifier|public
class|class
name|RestRolloverIndexAction
extends|extends
name|BaseRestHandler
block|{
annotation|@
name|Inject
DECL|method|RestRolloverIndexAction
specifier|public
name|RestRolloverIndexAction
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
name|POST
argument_list|,
literal|"/{index}/_rollover"
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
name|RolloverRequest
name|rolloverIndexRequest
init|=
operator|new
name|RolloverRequest
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"index"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|hasContent
argument_list|()
condition|)
block|{
name|rolloverIndexRequest
operator|.
name|source
argument_list|(
name|request
operator|.
name|content
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rolloverIndexRequest
operator|.
name|dryRun
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"dry_run"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|rolloverIndexRequest
operator|.
name|timeout
argument_list|(
name|request
operator|.
name|paramAsTime
argument_list|(
literal|"timeout"
argument_list|,
name|rolloverIndexRequest
operator|.
name|timeout
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|rolloverIndexRequest
operator|.
name|masterNodeTimeout
argument_list|(
name|request
operator|.
name|paramAsTime
argument_list|(
literal|"master_timeout"
argument_list|,
name|rolloverIndexRequest
operator|.
name|masterNodeTimeout
argument_list|()
argument_list|)
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
name|rolloverIndex
argument_list|(
name|rolloverIndexRequest
argument_list|,
operator|new
name|RestToXContentListener
argument_list|<>
argument_list|(
name|channel
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

