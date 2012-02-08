begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.node
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|node
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
name|ImmutableMap
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
name|*
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
name|ClusterAction
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
name|TransportAction
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
name|internal
operator|.
name|InternalClusterAdminClient
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
name|support
operator|.
name|AbstractClusterAdminClient
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
name|collect
operator|.
name|MapBuilder
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
name|threadpool
operator|.
name|ThreadPool
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
DECL|class|NodeClusterAdminClient
specifier|public
class|class
name|NodeClusterAdminClient
extends|extends
name|AbstractClusterAdminClient
implements|implements
name|InternalClusterAdminClient
block|{
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|actions
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|ClusterAction
argument_list|,
name|TransportAction
argument_list|>
name|actions
decl_stmt|;
annotation|@
name|Inject
DECL|method|NodeClusterAdminClient
specifier|public
name|NodeClusterAdminClient
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|Map
argument_list|<
name|GenericAction
argument_list|,
name|TransportAction
argument_list|>
name|actions
parameter_list|)
block|{
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|MapBuilder
argument_list|<
name|ClusterAction
argument_list|,
name|TransportAction
argument_list|>
name|actionsBuilder
init|=
operator|new
name|MapBuilder
argument_list|<
name|ClusterAction
argument_list|,
name|TransportAction
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|GenericAction
argument_list|,
name|TransportAction
argument_list|>
name|entry
range|:
name|actions
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|instanceof
name|ClusterAction
condition|)
block|{
name|actionsBuilder
operator|.
name|put
argument_list|(
operator|(
name|ClusterAction
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|actions
operator|=
name|actionsBuilder
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|threadPool
specifier|public
name|ThreadPool
name|threadPool
parameter_list|()
block|{
return|return
name|this
operator|.
name|threadPool
return|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|,
name|RequestBuilder
extends|extends
name|ActionRequestBuilder
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
parameter_list|>
name|ActionFuture
argument_list|<
name|Response
argument_list|>
name|execute
parameter_list|(
name|ClusterAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|>
name|action
parameter_list|,
name|Request
name|request
parameter_list|)
block|{
name|TransportAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
name|transportAction
init|=
name|actions
operator|.
name|get
argument_list|(
name|action
argument_list|)
decl_stmt|;
return|return
name|transportAction
operator|.
name|execute
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|,
name|RequestBuilder
extends|extends
name|ActionRequestBuilder
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
parameter_list|>
name|void
name|execute
parameter_list|(
name|ClusterAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|>
name|action
parameter_list|,
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
name|TransportAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
name|transportAction
init|=
name|actions
operator|.
name|get
argument_list|(
name|action
argument_list|)
decl_stmt|;
name|transportAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

