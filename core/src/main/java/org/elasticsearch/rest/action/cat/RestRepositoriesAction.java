begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.cat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|cat
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
name|cluster
operator|.
name|repositories
operator|.
name|get
operator|.
name|GetRepositoriesRequest
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
name|repositories
operator|.
name|get
operator|.
name|GetRepositoriesResponse
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
name|cluster
operator|.
name|metadata
operator|.
name|RepositoryMetaData
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
name|Table
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
name|action
operator|.
name|support
operator|.
name|RestResponseListener
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
name|RestTable
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|GET
import|;
end_import

begin_comment
comment|/**  * Cat API class to display information about snapshot repositories  */
end_comment

begin_class
DECL|class|RestRepositoriesAction
specifier|public
class|class
name|RestRepositoriesAction
extends|extends
name|AbstractCatAction
block|{
annotation|@
name|Inject
DECL|method|RestRepositoriesAction
specifier|public
name|RestRepositoriesAction
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
name|GET
argument_list|,
literal|"/_cat/repositories"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doRequest
specifier|protected
name|void
name|doRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|,
name|NodeClient
name|client
parameter_list|)
block|{
name|GetRepositoriesRequest
name|getRepositoriesRequest
init|=
operator|new
name|GetRepositoriesRequest
argument_list|()
decl_stmt|;
name|getRepositoriesRequest
operator|.
name|local
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"local"
argument_list|,
name|getRepositoriesRequest
operator|.
name|local
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|getRepositoriesRequest
operator|.
name|masterNodeTimeout
argument_list|(
name|request
operator|.
name|paramAsTime
argument_list|(
literal|"master_timeout"
argument_list|,
name|getRepositoriesRequest
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
name|cluster
argument_list|()
operator|.
name|getRepositories
argument_list|(
name|getRepositoriesRequest
argument_list|,
operator|new
name|RestResponseListener
argument_list|<
name|GetRepositoriesResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|RestResponse
name|buildResponse
parameter_list|(
name|GetRepositoriesResponse
name|getRepositoriesResponse
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|RestTable
operator|.
name|buildResponse
argument_list|(
name|buildTable
argument_list|(
name|request
argument_list|,
name|getRepositoriesResponse
argument_list|)
argument_list|,
name|channel
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|documentation
specifier|protected
name|void
name|documentation
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"/_cat/repositories\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTableWithHeader
specifier|protected
name|Table
name|getTableWithHeader
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
return|return
operator|new
name|Table
argument_list|()
operator|.
name|startHeaders
argument_list|()
operator|.
name|addCell
argument_list|(
literal|"id"
argument_list|,
literal|"alias:id,repoId;desc:unique repository id"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"type"
argument_list|,
literal|"alias:t,type;text-align:right;desc:repository type"
argument_list|)
operator|.
name|endHeaders
argument_list|()
return|;
block|}
DECL|method|buildTable
specifier|private
name|Table
name|buildTable
parameter_list|(
name|RestRequest
name|req
parameter_list|,
name|GetRepositoriesResponse
name|getRepositoriesResponse
parameter_list|)
block|{
name|Table
name|table
init|=
name|getTableWithHeader
argument_list|(
name|req
argument_list|)
decl_stmt|;
for|for
control|(
name|RepositoryMetaData
name|repositoryMetaData
range|:
name|getRepositoriesResponse
operator|.
name|repositories
argument_list|()
control|)
block|{
name|table
operator|.
name|startRow
argument_list|()
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|repositoryMetaData
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|repositoryMetaData
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|endRow
argument_list|()
expr_stmt|;
block|}
return|return
name|table
return|;
block|}
block|}
end_class

end_unit

