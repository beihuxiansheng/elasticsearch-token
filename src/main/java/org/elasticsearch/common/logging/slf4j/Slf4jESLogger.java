begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.logging.slf4j
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|logging
operator|.
name|slf4j
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
name|logging
operator|.
name|support
operator|.
name|AbstractESLogger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|Slf4jESLogger
specifier|public
class|class
name|Slf4jESLogger
extends|extends
name|AbstractESLogger
block|{
DECL|field|logger
specifier|private
specifier|final
name|Logger
name|logger
decl_stmt|;
DECL|method|Slf4jESLogger
specifier|public
name|Slf4jESLogger
parameter_list|(
name|String
name|prefix
parameter_list|,
name|Logger
name|logger
parameter_list|)
block|{
name|super
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setLevel
specifier|public
name|void
name|setLevel
parameter_list|(
name|String
name|level
parameter_list|)
block|{
comment|// can't set it in slf4j...
block|}
annotation|@
name|Override
DECL|method|getLevel
specifier|public
name|String
name|getLevel
parameter_list|()
block|{
comment|// can't get it in slf4j...
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|logger
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isTraceEnabled
specifier|public
name|boolean
name|isTraceEnabled
parameter_list|()
block|{
return|return
name|logger
operator|.
name|isTraceEnabled
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isDebugEnabled
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
annotation|@
name|Override
DECL|method|isInfoEnabled
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
annotation|@
name|Override
DECL|method|isWarnEnabled
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
annotation|@
name|Override
DECL|method|isErrorEnabled
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
annotation|@
name|Override
DECL|method|internalTrace
specifier|protected
name|void
name|internalTrace
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|internalTrace
specifier|protected
name|void
name|internalTrace
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
name|trace
argument_list|(
name|msg
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|internalDebug
specifier|protected
name|void
name|internalDebug
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
annotation|@
name|Override
DECL|method|internalDebug
specifier|protected
name|void
name|internalDebug
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
annotation|@
name|Override
DECL|method|internalInfo
specifier|protected
name|void
name|internalInfo
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
annotation|@
name|Override
DECL|method|internalInfo
specifier|protected
name|void
name|internalInfo
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
annotation|@
name|Override
DECL|method|internalWarn
specifier|protected
name|void
name|internalWarn
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
annotation|@
name|Override
DECL|method|internalWarn
specifier|protected
name|void
name|internalWarn
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
annotation|@
name|Override
DECL|method|internalError
specifier|protected
name|void
name|internalError
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
annotation|@
name|Override
DECL|method|internalError
specifier|protected
name|void
name|internalError
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

