begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport.netty
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|netty
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|logging
operator|.
name|ESLogger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|logging
operator|.
name|AbstractInternalLogger
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|NettyInternalESLogger
specifier|public
class|class
name|NettyInternalESLogger
extends|extends
name|AbstractInternalLogger
block|{
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|method|NettyInternalESLogger
specifier|public
name|NettyInternalESLogger
parameter_list|(
name|ESLogger
name|logger
parameter_list|)
block|{
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
DECL|method|isDebugEnabled
annotation|@
name|Override
specifier|public
name|boolean
name|isDebugEnabled
parameter_list|()
block|{
return|return
name|logger
operator|.
name|isDebugEnabled
argument_list|()
return|;
block|}
DECL|method|isInfoEnabled
annotation|@
name|Override
specifier|public
name|boolean
name|isInfoEnabled
parameter_list|()
block|{
return|return
name|logger
operator|.
name|isInfoEnabled
argument_list|()
return|;
block|}
DECL|method|isWarnEnabled
annotation|@
name|Override
specifier|public
name|boolean
name|isWarnEnabled
parameter_list|()
block|{
return|return
name|logger
operator|.
name|isWarnEnabled
argument_list|()
return|;
block|}
DECL|method|isErrorEnabled
annotation|@
name|Override
specifier|public
name|boolean
name|isErrorEnabled
parameter_list|()
block|{
return|return
name|logger
operator|.
name|isErrorEnabled
argument_list|()
return|;
block|}
DECL|method|debug
annotation|@
name|Override
specifier|public
name|void
name|debug
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|debug
annotation|@
name|Override
specifier|public
name|void
name|debug
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|msg
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
DECL|method|info
annotation|@
name|Override
specifier|public
name|void
name|info
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|info
annotation|@
name|Override
specifier|public
name|void
name|info
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
name|msg
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
DECL|method|warn
annotation|@
name|Override
specifier|public
name|void
name|warn
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|warn
annotation|@
name|Override
specifier|public
name|void
name|warn
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|msg
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
DECL|method|error
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|error
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

