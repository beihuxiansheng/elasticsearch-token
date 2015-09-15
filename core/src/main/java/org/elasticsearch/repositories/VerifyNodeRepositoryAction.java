begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.repositories
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectContainer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectCursor
import|;
end_import

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
name|ActionListener
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
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNodes
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
name|index
operator|.
name|snapshots
operator|.
name|IndexShardRepository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|RepositoriesService
operator|.
name|VerifyResponse
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
name|EmptyTransportResponseHandler
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
name|TransportChannel
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
name|TransportException
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
name|TransportRequest
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
name|TransportRequestHandler
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
name|TransportResponse
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
name|List
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
name|CopyOnWriteArrayList
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
name|AtomicInteger
import|;
end_import

begin_class
DECL|class|VerifyNodeRepositoryAction
specifier|public
class|class
name|VerifyNodeRepositoryAction
extends|extends
name|AbstractComponent
block|{
DECL|field|ACTION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ACTION_NAME
init|=
literal|"internal:admin/repository/verify"
decl_stmt|;
DECL|field|transportService
specifier|private
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|repositoriesService
specifier|private
specifier|final
name|RepositoriesService
name|repositoriesService
decl_stmt|;
DECL|method|VerifyNodeRepositoryAction
specifier|public
name|VerifyNodeRepositoryAction
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
name|RepositoriesService
name|repositoriesService
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
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|repositoriesService
operator|=
name|repositoriesService
expr_stmt|;
name|transportService
operator|.
name|registerRequestHandler
argument_list|(
name|ACTION_NAME
argument_list|,
name|VerifyNodeRepositoryRequest
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|,
operator|new
name|VerifyNodeRepositoryRequestHandler
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
name|ACTION_NAME
argument_list|)
expr_stmt|;
block|}
DECL|method|verify
specifier|public
name|void
name|verify
parameter_list|(
name|String
name|repository
parameter_list|,
name|String
name|verificationToken
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|VerifyResponse
argument_list|>
name|listener
parameter_list|)
block|{
specifier|final
name|DiscoveryNodes
name|discoNodes
init|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
decl_stmt|;
specifier|final
name|DiscoveryNode
name|localNode
init|=
name|discoNodes
operator|.
name|localNode
argument_list|()
decl_stmt|;
specifier|final
name|ObjectContainer
argument_list|<
name|DiscoveryNode
argument_list|>
name|masterAndDataNodes
init|=
name|discoNodes
operator|.
name|masterAndDataNodes
argument_list|()
operator|.
name|values
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ObjectCursor
argument_list|<
name|DiscoveryNode
argument_list|>
name|cursor
range|:
name|masterAndDataNodes
control|)
block|{
name|DiscoveryNode
name|node
init|=
name|cursor
operator|.
name|value
decl_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|VerificationFailure
argument_list|>
name|errors
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|(
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|DiscoveryNode
name|node
range|:
name|nodes
control|)
block|{
if|if
condition|(
name|node
operator|.
name|equals
argument_list|(
name|localNode
argument_list|)
condition|)
block|{
try|try
block|{
name|doVerify
argument_list|(
name|repository
argument_list|,
name|verificationToken
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[{}] failed to verify repository"
argument_list|,
name|t
argument_list|,
name|repository
argument_list|)
expr_stmt|;
name|errors
operator|.
name|add
argument_list|(
operator|new
name|VerificationFailure
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|,
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|counter
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|finishVerification
argument_list|(
name|listener
argument_list|,
name|nodes
argument_list|,
name|errors
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|ACTION_NAME
argument_list|,
operator|new
name|VerifyNodeRepositoryRequest
argument_list|(
name|repository
argument_list|,
name|verificationToken
argument_list|)
argument_list|,
operator|new
name|EmptyTransportResponseHandler
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|handleResponse
parameter_list|(
name|TransportResponse
operator|.
name|Empty
name|response
parameter_list|)
block|{
if|if
condition|(
name|counter
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|finishVerification
argument_list|(
name|listener
argument_list|,
name|nodes
argument_list|,
name|errors
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|TransportException
name|exp
parameter_list|)
block|{
name|errors
operator|.
name|add
argument_list|(
operator|new
name|VerificationFailure
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|,
name|exp
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|counter
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|finishVerification
argument_list|(
name|listener
argument_list|,
name|nodes
argument_list|,
name|errors
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|finishVerification
specifier|public
name|void
name|finishVerification
parameter_list|(
name|ActionListener
argument_list|<
name|VerifyResponse
argument_list|>
name|listener
parameter_list|,
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodes
parameter_list|,
name|CopyOnWriteArrayList
argument_list|<
name|VerificationFailure
argument_list|>
name|errors
parameter_list|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|RepositoriesService
operator|.
name|VerifyResponse
argument_list|(
name|nodes
operator|.
name|toArray
argument_list|(
operator|new
name|DiscoveryNode
index|[
name|nodes
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|errors
operator|.
name|toArray
argument_list|(
operator|new
name|VerificationFailure
index|[
name|errors
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doVerify
specifier|private
name|void
name|doVerify
parameter_list|(
name|String
name|repository
parameter_list|,
name|String
name|verificationToken
parameter_list|)
block|{
name|IndexShardRepository
name|blobStoreIndexShardRepository
init|=
name|repositoriesService
operator|.
name|indexShardRepository
argument_list|(
name|repository
argument_list|)
decl_stmt|;
name|blobStoreIndexShardRepository
operator|.
name|verify
argument_list|(
name|verificationToken
argument_list|)
expr_stmt|;
block|}
DECL|class|VerifyNodeRepositoryRequest
specifier|public
specifier|static
class|class
name|VerifyNodeRepositoryRequest
extends|extends
name|TransportRequest
block|{
DECL|field|repository
specifier|private
name|String
name|repository
decl_stmt|;
DECL|field|verificationToken
specifier|private
name|String
name|verificationToken
decl_stmt|;
DECL|method|VerifyNodeRepositoryRequest
specifier|public
name|VerifyNodeRepositoryRequest
parameter_list|()
block|{         }
DECL|method|VerifyNodeRepositoryRequest
name|VerifyNodeRepositoryRequest
parameter_list|(
name|String
name|repository
parameter_list|,
name|String
name|verificationToken
parameter_list|)
block|{
name|this
operator|.
name|repository
operator|=
name|repository
expr_stmt|;
name|this
operator|.
name|verificationToken
operator|=
name|verificationToken
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
name|repository
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|verificationToken
operator|=
name|in
operator|.
name|readString
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|repository
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|verificationToken
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|VerifyNodeRepositoryRequestHandler
class|class
name|VerifyNodeRepositoryRequestHandler
implements|implements
name|TransportRequestHandler
argument_list|<
name|VerifyNodeRepositoryRequest
argument_list|>
block|{
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
name|VerifyNodeRepositoryRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|doVerify
argument_list|(
name|request
operator|.
name|repository
argument_list|,
name|request
operator|.
name|verificationToken
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[{}] failed to verify repository"
argument_list|,
name|ex
argument_list|,
name|request
operator|.
name|repository
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
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

