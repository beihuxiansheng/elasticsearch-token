begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.node.stats
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
name|node
operator|.
name|stats
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
name|support
operator|.
name|nodes
operator|.
name|BaseNodeResponse
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
name|Nullable
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
name|http
operator|.
name|HttpStats
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|NodeIndicesStats
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|breaker
operator|.
name|AllCircuitBreakerStats
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|fs
operator|.
name|FsInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
operator|.
name|JvmStats
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|os
operator|.
name|OsStats
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|process
operator|.
name|ProcessStats
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
name|ThreadPoolStats
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
name|TransportStats
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Node statistics (dynamic, changes depending on when created).  */
end_comment

begin_class
DECL|class|NodeStats
specifier|public
class|class
name|NodeStats
extends|extends
name|BaseNodeResponse
implements|implements
name|ToXContent
block|{
DECL|field|timestamp
specifier|private
name|long
name|timestamp
decl_stmt|;
annotation|@
name|Nullable
DECL|field|indices
specifier|private
name|NodeIndicesStats
name|indices
decl_stmt|;
annotation|@
name|Nullable
DECL|field|os
specifier|private
name|OsStats
name|os
decl_stmt|;
annotation|@
name|Nullable
DECL|field|process
specifier|private
name|ProcessStats
name|process
decl_stmt|;
annotation|@
name|Nullable
DECL|field|jvm
specifier|private
name|JvmStats
name|jvm
decl_stmt|;
annotation|@
name|Nullable
DECL|field|threadPool
specifier|private
name|ThreadPoolStats
name|threadPool
decl_stmt|;
annotation|@
name|Nullable
DECL|field|fs
specifier|private
name|FsInfo
name|fs
decl_stmt|;
annotation|@
name|Nullable
DECL|field|transport
specifier|private
name|TransportStats
name|transport
decl_stmt|;
annotation|@
name|Nullable
DECL|field|http
specifier|private
name|HttpStats
name|http
decl_stmt|;
annotation|@
name|Nullable
DECL|field|breaker
specifier|private
name|AllCircuitBreakerStats
name|breaker
decl_stmt|;
DECL|method|NodeStats
name|NodeStats
parameter_list|()
block|{     }
DECL|method|NodeStats
specifier|public
name|NodeStats
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|long
name|timestamp
parameter_list|,
annotation|@
name|Nullable
name|NodeIndicesStats
name|indices
parameter_list|,
annotation|@
name|Nullable
name|OsStats
name|os
parameter_list|,
annotation|@
name|Nullable
name|ProcessStats
name|process
parameter_list|,
annotation|@
name|Nullable
name|JvmStats
name|jvm
parameter_list|,
annotation|@
name|Nullable
name|ThreadPoolStats
name|threadPool
parameter_list|,
annotation|@
name|Nullable
name|FsInfo
name|fs
parameter_list|,
annotation|@
name|Nullable
name|TransportStats
name|transport
parameter_list|,
annotation|@
name|Nullable
name|HttpStats
name|http
parameter_list|,
annotation|@
name|Nullable
name|AllCircuitBreakerStats
name|breaker
parameter_list|)
block|{
name|super
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
name|this
operator|.
name|os
operator|=
name|os
expr_stmt|;
name|this
operator|.
name|process
operator|=
name|process
expr_stmt|;
name|this
operator|.
name|jvm
operator|=
name|jvm
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
name|this
operator|.
name|http
operator|=
name|http
expr_stmt|;
name|this
operator|.
name|breaker
operator|=
name|breaker
expr_stmt|;
block|}
DECL|method|getTimestamp
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|this
operator|.
name|timestamp
return|;
block|}
annotation|@
name|Nullable
DECL|method|getHostname
specifier|public
name|String
name|getHostname
parameter_list|()
block|{
return|return
name|getNode
argument_list|()
operator|.
name|getHostName
argument_list|()
return|;
block|}
comment|/**      * Indices level stats.      */
annotation|@
name|Nullable
DECL|method|getIndices
specifier|public
name|NodeIndicesStats
name|getIndices
parameter_list|()
block|{
return|return
name|this
operator|.
name|indices
return|;
block|}
comment|/**      * Operating System level statistics.      */
annotation|@
name|Nullable
DECL|method|getOs
specifier|public
name|OsStats
name|getOs
parameter_list|()
block|{
return|return
name|this
operator|.
name|os
return|;
block|}
comment|/**      * Process level statistics.      */
annotation|@
name|Nullable
DECL|method|getProcess
specifier|public
name|ProcessStats
name|getProcess
parameter_list|()
block|{
return|return
name|process
return|;
block|}
comment|/**      * JVM level statistics.      */
annotation|@
name|Nullable
DECL|method|getJvm
specifier|public
name|JvmStats
name|getJvm
parameter_list|()
block|{
return|return
name|jvm
return|;
block|}
comment|/**      * Thread Pool level statistics.      */
annotation|@
name|Nullable
DECL|method|getThreadPool
specifier|public
name|ThreadPoolStats
name|getThreadPool
parameter_list|()
block|{
return|return
name|this
operator|.
name|threadPool
return|;
block|}
comment|/**      * File system level stats.      */
annotation|@
name|Nullable
DECL|method|getFs
specifier|public
name|FsInfo
name|getFs
parameter_list|()
block|{
return|return
name|fs
return|;
block|}
annotation|@
name|Nullable
DECL|method|getTransport
specifier|public
name|TransportStats
name|getTransport
parameter_list|()
block|{
return|return
name|this
operator|.
name|transport
return|;
block|}
annotation|@
name|Nullable
DECL|method|getHttp
specifier|public
name|HttpStats
name|getHttp
parameter_list|()
block|{
return|return
name|this
operator|.
name|http
return|;
block|}
annotation|@
name|Nullable
DECL|method|getBreaker
specifier|public
name|AllCircuitBreakerStats
name|getBreaker
parameter_list|()
block|{
return|return
name|this
operator|.
name|breaker
return|;
block|}
DECL|method|readNodeStats
specifier|public
specifier|static
name|NodeStats
name|readNodeStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|NodeStats
name|nodeInfo
init|=
operator|new
name|NodeStats
argument_list|()
decl_stmt|;
name|nodeInfo
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|nodeInfo
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
name|timestamp
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|indices
operator|=
name|NodeIndicesStats
operator|.
name|readIndicesStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|os
operator|=
name|OsStats
operator|.
name|readOsStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|process
operator|=
name|ProcessStats
operator|.
name|readProcessStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|jvm
operator|=
name|JvmStats
operator|.
name|readJvmStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|threadPool
operator|=
name|ThreadPoolStats
operator|.
name|readThreadPoolStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|fs
operator|=
name|FsInfo
operator|.
name|readFsInfo
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|transport
operator|=
name|TransportStats
operator|.
name|readTransportStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|http
operator|=
name|HttpStats
operator|.
name|readHttpStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|breaker
operator|=
name|AllCircuitBreakerStats
operator|.
name|readOptionalAllCircuitBreakerStats
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
name|out
operator|.
name|writeVLong
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
if|if
condition|(
name|indices
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
name|indices
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|os
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
name|os
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|process
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
name|process
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|jvm
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
name|jvm
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|threadPool
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
name|threadPool
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fs
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
name|fs
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|transport
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
name|transport
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|http
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
name|http
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeOptionalStreamable
argument_list|(
name|breaker
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|params
operator|.
name|param
argument_list|(
literal|"node_info_format"
argument_list|,
literal|"default"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"none"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
name|getNode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"transport_address"
argument_list|,
name|getNode
argument_list|()
operator|.
name|address
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"host"
argument_list|,
name|getNode
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|,
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"ip"
argument_list|,
name|getNode
argument_list|()
operator|.
name|getAddress
argument_list|()
argument_list|,
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|NONE
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|getNode
argument_list|()
operator|.
name|attributes
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"attributes"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attr
range|:
name|getNode
argument_list|()
operator|.
name|attributes
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|attr
operator|.
name|getKey
argument_list|()
argument_list|,
name|attr
operator|.
name|getValue
argument_list|()
argument_list|,
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|getIndices
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getIndices
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getOs
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getOs
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getProcess
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getProcess
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getJvm
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getJvm
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getThreadPool
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getThreadPool
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getFs
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getFs
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getTransport
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getTransport
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getHttp
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getHttp
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getBreaker
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getBreaker
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

