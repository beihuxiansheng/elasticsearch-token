begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
package|;
end_package

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
name|VoidStreamable
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|VoidTransportResponseHandler
specifier|public
class|class
name|VoidTransportResponseHandler
implements|implements
name|TransportResponseHandler
argument_list|<
name|VoidStreamable
argument_list|>
block|{
DECL|field|INSTANCE_SAME
specifier|public
specifier|static
specifier|final
name|VoidTransportResponseHandler
name|INSTANCE_SAME
init|=
operator|new
name|VoidTransportResponseHandler
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|)
decl_stmt|;
DECL|field|INSTANCE_CACHED
specifier|public
specifier|static
specifier|final
name|VoidTransportResponseHandler
name|INSTANCE_CACHED
init|=
operator|new
name|VoidTransportResponseHandler
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|CACHED
argument_list|)
decl_stmt|;
DECL|field|executor
specifier|private
specifier|final
name|String
name|executor
decl_stmt|;
DECL|method|VoidTransportResponseHandler
specifier|public
name|VoidTransportResponseHandler
parameter_list|(
name|String
name|executor
parameter_list|)
block|{
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newInstance
specifier|public
name|VoidStreamable
name|newInstance
parameter_list|()
block|{
return|return
name|VoidStreamable
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Override
DECL|method|handleResponse
specifier|public
name|void
name|handleResponse
parameter_list|(
name|VoidStreamable
name|response
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|handleException
specifier|public
name|void
name|handleException
parameter_list|(
name|TransportException
name|exp
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|executor
specifier|public
name|String
name|executor
parameter_list|()
block|{
return|return
name|executor
return|;
block|}
block|}
end_class

end_unit

