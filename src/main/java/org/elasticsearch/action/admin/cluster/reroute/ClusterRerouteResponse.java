begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.reroute
package|package
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
name|reroute
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|AcknowledgedResponse
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
name|routing
operator|.
name|allocation
operator|.
name|RoutingExplanations
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
comment|/**  * Response returned after a cluster reroute request  */
end_comment

begin_class
DECL|class|ClusterRerouteResponse
specifier|public
class|class
name|ClusterRerouteResponse
extends|extends
name|AcknowledgedResponse
block|{
DECL|field|state
specifier|private
name|ClusterState
name|state
decl_stmt|;
DECL|field|explanations
specifier|private
name|RoutingExplanations
name|explanations
decl_stmt|;
DECL|method|ClusterRerouteResponse
name|ClusterRerouteResponse
parameter_list|()
block|{      }
DECL|method|ClusterRerouteResponse
name|ClusterRerouteResponse
parameter_list|(
name|boolean
name|acknowledged
parameter_list|,
name|ClusterState
name|state
parameter_list|,
name|RoutingExplanations
name|explanations
parameter_list|)
block|{
name|super
argument_list|(
name|acknowledged
argument_list|)
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|explanations
operator|=
name|explanations
expr_stmt|;
block|}
comment|/**      * Returns the cluster state resulted from the cluster reroute request execution      */
DECL|method|getState
specifier|public
name|ClusterState
name|getState
parameter_list|()
block|{
return|return
name|this
operator|.
name|state
return|;
block|}
DECL|method|getExplanations
specifier|public
name|RoutingExplanations
name|getExplanations
parameter_list|()
block|{
return|return
name|this
operator|.
name|explanations
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|state
operator|=
name|ClusterState
operator|.
name|Builder
operator|.
name|readFrom
argument_list|(
name|in
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|readAcknowledged
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_1_0
argument_list|)
condition|)
block|{
name|explanations
operator|=
name|RoutingExplanations
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|explanations
operator|=
operator|new
name|RoutingExplanations
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|ClusterState
operator|.
name|Builder
operator|.
name|writeTo
argument_list|(
name|state
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|writeAcknowledged
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_1_0
argument_list|)
condition|)
block|{
name|RoutingExplanations
operator|.
name|writeTo
argument_list|(
name|explanations
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

