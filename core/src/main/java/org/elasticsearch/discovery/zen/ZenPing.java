begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.zen
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
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
name|ClusterName
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|lease
operator|.
name|Releasable
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|GatewayService
operator|.
name|STATE_NOT_RECOVERED_BLOCK
import|;
end_import

begin_interface
DECL|interface|ZenPing
specifier|public
interface|interface
name|ZenPing
extends|extends
name|Releasable
block|{
DECL|method|start
name|void
name|start
parameter_list|()
function_decl|;
DECL|method|ping
name|void
name|ping
parameter_list|(
name|Consumer
argument_list|<
name|PingCollection
argument_list|>
name|resultsConsumer
parameter_list|,
name|TimeValue
name|timeout
parameter_list|)
function_decl|;
DECL|class|PingResponse
class|class
name|PingResponse
implements|implements
name|Streamable
block|{
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|PingResponse
index|[]
name|EMPTY
init|=
operator|new
name|PingResponse
index|[
literal|0
index|]
decl_stmt|;
DECL|field|idGenerator
specifier|private
specifier|static
specifier|final
name|AtomicLong
name|idGenerator
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|// an always increasing unique identifier for this ping response.
comment|// lower values means older pings.
DECL|field|id
specifier|private
name|long
name|id
decl_stmt|;
DECL|field|clusterName
specifier|private
name|ClusterName
name|clusterName
decl_stmt|;
DECL|field|node
specifier|private
name|DiscoveryNode
name|node
decl_stmt|;
DECL|field|master
specifier|private
name|DiscoveryNode
name|master
decl_stmt|;
DECL|field|clusterStateVersion
specifier|private
name|long
name|clusterStateVersion
decl_stmt|;
DECL|method|PingResponse
specifier|private
name|PingResponse
parameter_list|()
block|{         }
comment|/**          * @param node                the node which this ping describes          * @param master              the current master of the node          * @param clusterName         the cluster name of the node          * @param clusterStateVersion the current cluster state version of that node          *                            ({@link ElectMasterService.MasterCandidate#UNRECOVERED_CLUSTER_VERSION} for not recovered)          */
DECL|method|PingResponse
name|PingResponse
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|DiscoveryNode
name|master
parameter_list|,
name|ClusterName
name|clusterName
parameter_list|,
name|long
name|clusterStateVersion
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|idGenerator
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|master
operator|=
name|master
expr_stmt|;
name|this
operator|.
name|clusterName
operator|=
name|clusterName
expr_stmt|;
name|this
operator|.
name|clusterStateVersion
operator|=
name|clusterStateVersion
expr_stmt|;
block|}
DECL|method|PingResponse
specifier|public
name|PingResponse
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|DiscoveryNode
name|master
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
name|this
argument_list|(
name|node
argument_list|,
name|master
argument_list|,
name|state
operator|.
name|getClusterName
argument_list|()
argument_list|,
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|hasGlobalBlock
argument_list|(
name|STATE_NOT_RECOVERED_BLOCK
argument_list|)
condition|?
name|ElectMasterService
operator|.
name|MasterCandidate
operator|.
name|UNRECOVERED_CLUSTER_VERSION
else|:
name|state
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**              * an always increasing unique identifier for this ping response.              * lower values means older pings.              */
DECL|method|id
specifier|public
name|long
name|id
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
DECL|method|clusterName
specifier|public
name|ClusterName
name|clusterName
parameter_list|()
block|{
return|return
name|this
operator|.
name|clusterName
return|;
block|}
comment|/** the node which this ping describes */
DECL|method|node
specifier|public
name|DiscoveryNode
name|node
parameter_list|()
block|{
return|return
name|node
return|;
block|}
comment|/** the current master of the node */
DECL|method|master
specifier|public
name|DiscoveryNode
name|master
parameter_list|()
block|{
return|return
name|master
return|;
block|}
comment|/**          * the current cluster state version of that node ({@link ElectMasterService.MasterCandidate#UNRECOVERED_CLUSTER_VERSION}          * for not recovered) */
DECL|method|getClusterStateVersion
specifier|public
name|long
name|getClusterStateVersion
parameter_list|()
block|{
return|return
name|clusterStateVersion
return|;
block|}
DECL|method|readPingResponse
specifier|public
specifier|static
name|PingResponse
name|readPingResponse
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|PingResponse
name|response
init|=
operator|new
name|PingResponse
argument_list|()
decl_stmt|;
name|response
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|response
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
name|clusterName
operator|=
operator|new
name|ClusterName
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|node
operator|=
operator|new
name|DiscoveryNode
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|master
operator|=
operator|new
name|DiscoveryNode
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|clusterStateVersion
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|in
operator|.
name|readLong
argument_list|()
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
name|clusterName
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
if|if
condition|(
name|master
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|master
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeLong
argument_list|(
name|clusterStateVersion
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ping_response{node ["
operator|+
name|node
operator|+
literal|"], id["
operator|+
name|id
operator|+
literal|"], master ["
operator|+
name|master
operator|+
literal|"],"
operator|+
literal|"cluster_state_version ["
operator|+
name|clusterStateVersion
operator|+
literal|"], cluster_name["
operator|+
name|clusterName
operator|.
name|value
argument_list|()
operator|+
literal|"]}"
return|;
block|}
block|}
comment|/**      * a utility collection of pings where only the most recent ping is stored per node      */
DECL|class|PingCollection
class|class
name|PingCollection
block|{
DECL|field|pings
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|PingResponse
argument_list|>
name|pings
decl_stmt|;
DECL|method|PingCollection
specifier|public
name|PingCollection
parameter_list|()
block|{
name|pings
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**          * adds a ping if newer than previous pings from the same node          *          * @return true if added, false o.w.          */
DECL|method|addPing
specifier|public
specifier|synchronized
name|boolean
name|addPing
parameter_list|(
name|PingResponse
name|ping
parameter_list|)
block|{
name|PingResponse
name|existingResponse
init|=
name|pings
operator|.
name|get
argument_list|(
name|ping
operator|.
name|node
argument_list|()
argument_list|)
decl_stmt|;
comment|// in case both existing and new ping have the same id (probably because they come
comment|// from nodes from version<1.4.0) we prefer to use the last added one.
if|if
condition|(
name|existingResponse
operator|==
literal|null
operator|||
name|existingResponse
operator|.
name|id
argument_list|()
operator|<=
name|ping
operator|.
name|id
argument_list|()
condition|)
block|{
name|pings
operator|.
name|put
argument_list|(
name|ping
operator|.
name|node
argument_list|()
argument_list|,
name|ping
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** serialize current pings to a list. It is guaranteed that the list contains one ping response per node */
DECL|method|toList
specifier|public
specifier|synchronized
name|List
argument_list|<
name|PingResponse
argument_list|>
name|toList
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|pings
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
comment|/** the number of nodes for which there are known pings */
DECL|method|size
specifier|public
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
return|return
name|pings
operator|.
name|size
argument_list|()
return|;
block|}
block|}
block|}
end_interface

end_unit

