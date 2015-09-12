begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.zen.membership
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
operator|.
name|membership
package|;
end_package

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
name|node
operator|.
name|DiscoveryNode
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
name|component
operator|.
name|AbstractComponent
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
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
operator|.
name|DiscoveryNodesProvider
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
name|*
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|MembershipAction
specifier|public
class|class
name|MembershipAction
extends|extends
name|AbstractComponent
block|{
DECL|field|DISCOVERY_JOIN_ACTION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DISCOVERY_JOIN_ACTION_NAME
init|=
literal|"internal:discovery/zen/join"
decl_stmt|;
DECL|field|DISCOVERY_JOIN_VALIDATE_ACTION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DISCOVERY_JOIN_VALIDATE_ACTION_NAME
init|=
literal|"internal:discovery/zen/join/validate"
decl_stmt|;
DECL|field|DISCOVERY_LEAVE_ACTION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|DISCOVERY_LEAVE_ACTION_NAME
init|=
literal|"internal:discovery/zen/leave"
decl_stmt|;
DECL|interface|JoinCallback
specifier|public
specifier|static
interface|interface
name|JoinCallback
block|{
DECL|method|onSuccess
name|void
name|onSuccess
parameter_list|()
function_decl|;
DECL|method|onFailure
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
function_decl|;
block|}
DECL|interface|MembershipListener
specifier|public
specifier|static
interface|interface
name|MembershipListener
block|{
DECL|method|onJoin
name|void
name|onJoin
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|JoinCallback
name|callback
parameter_list|)
function_decl|;
DECL|method|onLeave
name|void
name|onLeave
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
function_decl|;
block|}
DECL|field|transportService
specifier|private
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|nodesProvider
specifier|private
specifier|final
name|DiscoveryNodesProvider
name|nodesProvider
decl_stmt|;
DECL|field|listener
specifier|private
specifier|final
name|MembershipListener
name|listener
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|method|MembershipAction
specifier|public
name|MembershipAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|DiscoveryNodesProvider
name|nodesProvider
parameter_list|,
name|MembershipListener
name|listener
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|nodesProvider
operator|=
name|nodesProvider
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|transportService
operator|.
name|registerRequestHandler
argument_list|(
name|DISCOVERY_JOIN_ACTION_NAME
argument_list|,
name|JoinRequest
operator|.
name|class
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|GENERIC
argument_list|,
operator|new
name|JoinRequestRequestHandler
argument_list|()
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerRequestHandler
argument_list|(
name|DISCOVERY_JOIN_VALIDATE_ACTION_NAME
argument_list|,
name|ValidateJoinRequest
operator|.
name|class
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|GENERIC
argument_list|,
operator|new
name|ValidateJoinRequestRequestHandler
argument_list|()
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerRequestHandler
argument_list|(
name|DISCOVERY_LEAVE_ACTION_NAME
argument_list|,
name|LeaveRequest
operator|.
name|class
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|GENERIC
argument_list|,
operator|new
name|LeaveRequestRequestHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|transportService
operator|.
name|removeHandler
argument_list|(
name|DISCOVERY_JOIN_ACTION_NAME
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|removeHandler
argument_list|(
name|DISCOVERY_JOIN_VALIDATE_ACTION_NAME
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|removeHandler
argument_list|(
name|DISCOVERY_LEAVE_ACTION_NAME
argument_list|)
expr_stmt|;
block|}
DECL|method|sendLeaveRequest
specifier|public
name|void
name|sendLeaveRequest
parameter_list|(
name|DiscoveryNode
name|masterNode
parameter_list|,
name|DiscoveryNode
name|node
parameter_list|)
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|DISCOVERY_LEAVE_ACTION_NAME
argument_list|,
operator|new
name|LeaveRequest
argument_list|(
name|masterNode
argument_list|)
argument_list|,
name|EmptyTransportResponseHandler
operator|.
name|INSTANCE_SAME
argument_list|)
expr_stmt|;
block|}
DECL|method|sendLeaveRequestBlocking
specifier|public
name|void
name|sendLeaveRequestBlocking
parameter_list|(
name|DiscoveryNode
name|masterNode
parameter_list|,
name|DiscoveryNode
name|node
parameter_list|,
name|TimeValue
name|timeout
parameter_list|)
block|{
name|transportService
operator|.
name|submitRequest
argument_list|(
name|masterNode
argument_list|,
name|DISCOVERY_LEAVE_ACTION_NAME
argument_list|,
operator|new
name|LeaveRequest
argument_list|(
name|node
argument_list|)
argument_list|,
name|EmptyTransportResponseHandler
operator|.
name|INSTANCE_SAME
argument_list|)
operator|.
name|txGet
argument_list|(
name|timeout
operator|.
name|millis
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
DECL|method|sendJoinRequest
specifier|public
name|void
name|sendJoinRequest
parameter_list|(
name|DiscoveryNode
name|masterNode
parameter_list|,
name|DiscoveryNode
name|node
parameter_list|)
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|masterNode
argument_list|,
name|DISCOVERY_JOIN_ACTION_NAME
argument_list|,
operator|new
name|JoinRequest
argument_list|(
name|node
argument_list|)
argument_list|,
name|EmptyTransportResponseHandler
operator|.
name|INSTANCE_SAME
argument_list|)
expr_stmt|;
block|}
DECL|method|sendJoinRequestBlocking
specifier|public
name|void
name|sendJoinRequestBlocking
parameter_list|(
name|DiscoveryNode
name|masterNode
parameter_list|,
name|DiscoveryNode
name|node
parameter_list|,
name|TimeValue
name|timeout
parameter_list|)
block|{
name|transportService
operator|.
name|submitRequest
argument_list|(
name|masterNode
argument_list|,
name|DISCOVERY_JOIN_ACTION_NAME
argument_list|,
operator|new
name|JoinRequest
argument_list|(
name|node
argument_list|)
argument_list|,
name|EmptyTransportResponseHandler
operator|.
name|INSTANCE_SAME
argument_list|)
operator|.
name|txGet
argument_list|(
name|timeout
operator|.
name|millis
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
comment|/**      * Validates the join request, throwing a failure if it failed.      */
DECL|method|sendValidateJoinRequestBlocking
specifier|public
name|void
name|sendValidateJoinRequestBlocking
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|TimeValue
name|timeout
parameter_list|)
block|{
name|transportService
operator|.
name|submitRequest
argument_list|(
name|node
argument_list|,
name|DISCOVERY_JOIN_VALIDATE_ACTION_NAME
argument_list|,
operator|new
name|ValidateJoinRequest
argument_list|()
argument_list|,
name|EmptyTransportResponseHandler
operator|.
name|INSTANCE_SAME
argument_list|)
operator|.
name|txGet
argument_list|(
name|timeout
operator|.
name|millis
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
DECL|class|JoinRequest
specifier|public
specifier|static
class|class
name|JoinRequest
extends|extends
name|TransportRequest
block|{
DECL|field|node
name|DiscoveryNode
name|node
decl_stmt|;
DECL|method|JoinRequest
specifier|public
name|JoinRequest
parameter_list|()
block|{         }
DECL|method|JoinRequest
specifier|private
name|JoinRequest
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
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
name|node
operator|=
name|DiscoveryNode
operator|.
name|readNode
argument_list|(
name|in
argument_list|)
expr_stmt|;
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
name|node
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|JoinRequestRequestHandler
specifier|private
class|class
name|JoinRequestRequestHandler
implements|implements
name|TransportRequestHandler
argument_list|<
name|JoinRequest
argument_list|>
block|{
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
specifier|final
name|JoinRequest
name|request
parameter_list|,
specifier|final
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|listener
operator|.
name|onJoin
argument_list|(
name|request
operator|.
name|node
argument_list|,
operator|new
name|JoinCallback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|()
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|TransportResponse
operator|.
name|Empty
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|onFailure
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to send back failure on join request"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ValidateJoinRequest
specifier|public
specifier|static
class|class
name|ValidateJoinRequest
extends|extends
name|TransportRequest
block|{
DECL|method|ValidateJoinRequest
specifier|public
name|ValidateJoinRequest
parameter_list|()
block|{         }
block|}
DECL|class|ValidateJoinRequestRequestHandler
class|class
name|ValidateJoinRequestRequestHandler
implements|implements
name|TransportRequestHandler
argument_list|<
name|ValidateJoinRequest
argument_list|>
block|{
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
name|ValidateJoinRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
comment|// for now, the mere fact that we can serialize the cluster state acts as validation....
name|channel
operator|.
name|sendResponse
argument_list|(
name|TransportResponse
operator|.
name|Empty
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|LeaveRequest
specifier|public
specifier|static
class|class
name|LeaveRequest
extends|extends
name|TransportRequest
block|{
DECL|field|node
specifier|private
name|DiscoveryNode
name|node
decl_stmt|;
DECL|method|LeaveRequest
specifier|public
name|LeaveRequest
parameter_list|()
block|{         }
DECL|method|LeaveRequest
specifier|private
name|LeaveRequest
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
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
name|node
operator|=
name|DiscoveryNode
operator|.
name|readNode
argument_list|(
name|in
argument_list|)
expr_stmt|;
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
name|node
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|LeaveRequestRequestHandler
specifier|private
class|class
name|LeaveRequestRequestHandler
implements|implements
name|TransportRequestHandler
argument_list|<
name|LeaveRequest
argument_list|>
block|{
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
name|LeaveRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|listener
operator|.
name|onLeave
argument_list|(
name|request
operator|.
name|node
argument_list|)
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|TransportResponse
operator|.
name|Empty
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

