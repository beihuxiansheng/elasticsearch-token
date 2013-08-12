begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.template.get
package|package
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
name|get
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|ActionListener
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
name|support
operator|.
name|master
operator|.
name|TransportMasterNodeOperationAction
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
name|ClusterService
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
name|ClusterState
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
name|IndexTemplateMetaData
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
name|regex
operator|.
name|Regex
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
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportService
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TransportGetIndexTemplatesAction
specifier|public
class|class
name|TransportGetIndexTemplatesAction
extends|extends
name|TransportMasterNodeOperationAction
argument_list|<
name|GetIndexTemplatesRequest
argument_list|,
name|GetIndexTemplatesResponse
argument_list|>
block|{
annotation|@
name|Inject
DECL|method|TransportGetIndexTemplatesAction
specifier|public
name|TransportGetIndexTemplatesAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|transportAction
specifier|protected
name|String
name|transportAction
parameter_list|()
block|{
return|return
name|GetIndexTemplatesAction
operator|.
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|protected
name|String
name|executor
parameter_list|()
block|{
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
return|;
block|}
annotation|@
name|Override
DECL|method|newRequest
specifier|protected
name|GetIndexTemplatesRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|GetIndexTemplatesRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|GetIndexTemplatesResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|GetIndexTemplatesResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|masterOperation
specifier|protected
name|void
name|masterOperation
parameter_list|(
name|GetIndexTemplatesRequest
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|,
name|ActionListener
argument_list|<
name|GetIndexTemplatesResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|List
argument_list|<
name|IndexTemplateMetaData
argument_list|>
name|results
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|Regex
operator|.
name|isSimpleMatchPattern
argument_list|(
name|request
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|IndexTemplateMetaData
argument_list|>
name|entry
range|:
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|templates
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|request
operator|.
name|name
argument_list|()
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|templates
argument_list|()
operator|.
name|containsKey
argument_list|(
name|request
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|templates
argument_list|()
operator|.
name|get
argument_list|(
name|request
operator|.
name|name
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
name|GetIndexTemplatesResponse
argument_list|(
name|results
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

