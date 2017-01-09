begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.admin.cluster
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
name|cluster
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
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
name|cluster
operator|.
name|allocation
operator|.
name|ClusterAllocationExplainRequest
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
name|cluster
operator|.
name|allocation
operator|.
name|ClusterAllocationExplainResponse
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
name|node
operator|.
name|NodeClient
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
name|bytes
operator|.
name|BytesArray
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
name|common
operator|.
name|xcontent
operator|.
name|ToXContent
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentParser
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
name|BytesRestResponse
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
name|RestResponse
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
name|RestStatus
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
name|RestBuilderListener
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Class handling cluster allocation explanation at the REST level  */
end_comment

begin_class
DECL|class|RestClusterAllocationExplainAction
specifier|public
class|class
name|RestClusterAllocationExplainAction
extends|extends
name|BaseRestHandler
block|{
annotation|@
name|Inject
DECL|method|RestClusterAllocationExplainAction
specifier|public
name|RestClusterAllocationExplainAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
parameter_list|)
block|{
name|super
argument_list|(
name|settings
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
name|GET
argument_list|,
literal|"/_cluster/allocation/explain"
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
literal|"/_cluster/allocation/explain"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareRequest
specifier|public
name|RestChannelConsumer
name|prepareRequest
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|,
specifier|final
name|NodeClient
name|client
parameter_list|)
throws|throws
name|IOException
block|{
name|ClusterAllocationExplainRequest
name|req
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|hasContentOrSourceParam
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// Empty request signals "explain the first unassigned shard you find"
name|req
operator|=
operator|new
name|ClusterAllocationExplainRequest
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
init|(
name|XContentParser
name|parser
init|=
name|request
operator|.
name|contentOrSourceParamParser
argument_list|()
init|)
block|{
name|req
operator|=
name|ClusterAllocationExplainRequest
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to parse allocation explain request"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|channel
lambda|->
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|BytesRestResponse
argument_list|(
name|ExceptionsHelper
operator|.
name|status
argument_list|(
name|e
argument_list|)
argument_list|,
name|BytesRestResponse
operator|.
name|TEXT_CONTENT_TYPE
argument_list|,
name|BytesArray
operator|.
name|EMPTY
argument_list|)
argument_list|)
return|;
block|}
block|}
try|try
block|{
name|req
operator|.
name|includeYesDecisions
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"include_yes_decisions"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|includeDiskInfo
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"include_disk_info"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|humanReadable
init|=
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"human"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|channel
lambda|->
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|allocationExplain
argument_list|(
name|req
argument_list|,
operator|new
name|RestBuilderListener
argument_list|<
name|ClusterAllocationExplainResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
block|{
block|@Override                 public RestResponse buildResponse(ClusterAllocationExplainResponse response
operator|,
name|XContentBuilder
name|builder
block|)
throws|throws
name|Exception
block|{
name|builder
operator|.
name|humanReadable
argument_list|(
name|humanReadable
argument_list|)
empty_stmt|;
name|response
operator|.
name|getExplanation
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
return|return
operator|new
name|BytesRestResponse
argument_list|(
name|RestStatus
operator|.
name|OK
argument_list|,
name|builder
argument_list|)
return|;
block|}
block|}
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
unit|} catch
operator|(
name|Exception
name|e
operator|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"failed to explain allocation"
argument_list|,
name|e
argument_list|)
block|;
return|return
name|channel
lambda|->
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|BytesRestResponse
argument_list|(
name|ExceptionsHelper
operator|.
name|status
argument_list|(
name|e
argument_list|)
argument_list|,
name|BytesRestResponse
operator|.
name|TEXT_CONTENT_TYPE
argument_list|,
name|BytesArray
operator|.
name|EMPTY
argument_list|)
argument_list|)
return|;
block|}
end_expr_stmt

unit|} }
end_unit

