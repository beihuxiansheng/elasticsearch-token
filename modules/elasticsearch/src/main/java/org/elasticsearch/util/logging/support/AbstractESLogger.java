begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.logging.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|logging
operator|.
name|support
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|AbstractESLogger
specifier|public
specifier|abstract
class|class
name|AbstractESLogger
implements|implements
name|ESLogger
block|{
DECL|field|prefix
specifier|private
specifier|final
name|String
name|prefix
decl_stmt|;
DECL|method|AbstractESLogger
specifier|protected
name|AbstractESLogger
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
DECL|method|getPrefix
annotation|@
name|Override
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|this
operator|.
name|prefix
return|;
block|}
DECL|method|trace
annotation|@
name|Override
specifier|public
name|void
name|trace
parameter_list|(
name|String
name|msg
parameter_list|,
name|Object
modifier|...
name|params
parameter_list|)
block|{
if|if
condition|(
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|internalTrace
argument_list|(
name|LoggerMessageFormat
operator|.
name|format
argument_list|(
name|prefix
argument_list|,
name|msg
argument_list|,
name|params
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|internalTrace
specifier|protected
specifier|abstract
name|void
name|internalTrace
parameter_list|(
name|String
name|msg
parameter_list|)
function_decl|;
DECL|method|trace
annotation|@
name|Override
specifier|public
name|void
name|trace
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|cause
parameter_list|,
name|Object
modifier|...
name|params
parameter_list|)
block|{
if|if
condition|(
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|internalTrace
argument_list|(
name|LoggerMessageFormat
operator|.
name|format
argument_list|(
name|prefix
argument_list|,
name|msg
argument_list|,
name|params
argument_list|)
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|internalTrace
specifier|protected
specifier|abstract
name|void
name|internalTrace
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|cause
parameter_list|)
function_decl|;
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
name|Object
modifier|...
name|params
parameter_list|)
block|{
if|if
condition|(
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|internalDebug
argument_list|(
name|LoggerMessageFormat
operator|.
name|format
argument_list|(
name|prefix
argument_list|,
name|msg
argument_list|,
name|params
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|internalDebug
specifier|protected
specifier|abstract
name|void
name|internalDebug
parameter_list|(
name|String
name|msg
parameter_list|)
function_decl|;
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
parameter_list|,
name|Object
modifier|...
name|params
parameter_list|)
block|{
if|if
condition|(
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|internalDebug
argument_list|(
name|LoggerMessageFormat
operator|.
name|format
argument_list|(
name|prefix
argument_list|,
name|msg
argument_list|,
name|params
argument_list|)
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|internalDebug
specifier|protected
specifier|abstract
name|void
name|internalDebug
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|cause
parameter_list|)
function_decl|;
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
name|Object
modifier|...
name|params
parameter_list|)
block|{
if|if
condition|(
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|internalInfo
argument_list|(
name|LoggerMessageFormat
operator|.
name|format
argument_list|(
name|prefix
argument_list|,
name|msg
argument_list|,
name|params
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|internalInfo
specifier|protected
specifier|abstract
name|void
name|internalInfo
parameter_list|(
name|String
name|msg
parameter_list|)
function_decl|;
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
parameter_list|,
name|Object
modifier|...
name|params
parameter_list|)
block|{
if|if
condition|(
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|internalInfo
argument_list|(
name|LoggerMessageFormat
operator|.
name|format
argument_list|(
name|prefix
argument_list|,
name|msg
argument_list|,
name|params
argument_list|)
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|internalInfo
specifier|protected
specifier|abstract
name|void
name|internalInfo
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|cause
parameter_list|)
function_decl|;
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
name|Object
modifier|...
name|params
parameter_list|)
block|{
if|if
condition|(
name|isWarnEnabled
argument_list|()
condition|)
block|{
name|internalWarn
argument_list|(
name|LoggerMessageFormat
operator|.
name|format
argument_list|(
name|prefix
argument_list|,
name|msg
argument_list|,
name|params
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|internalWarn
specifier|protected
specifier|abstract
name|void
name|internalWarn
parameter_list|(
name|String
name|msg
parameter_list|)
function_decl|;
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
parameter_list|,
name|Object
modifier|...
name|params
parameter_list|)
block|{
if|if
condition|(
name|isWarnEnabled
argument_list|()
condition|)
block|{
name|internalWarn
argument_list|(
name|LoggerMessageFormat
operator|.
name|format
argument_list|(
name|prefix
argument_list|,
name|msg
argument_list|,
name|params
argument_list|)
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|internalWarn
specifier|protected
specifier|abstract
name|void
name|internalWarn
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|cause
parameter_list|)
function_decl|;
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
name|Object
modifier|...
name|params
parameter_list|)
block|{
if|if
condition|(
name|isErrorEnabled
argument_list|()
condition|)
block|{
name|internalError
argument_list|(
name|LoggerMessageFormat
operator|.
name|format
argument_list|(
name|prefix
argument_list|,
name|msg
argument_list|,
name|params
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|internalError
specifier|protected
specifier|abstract
name|void
name|internalError
parameter_list|(
name|String
name|msg
parameter_list|)
function_decl|;
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
parameter_list|,
name|Object
modifier|...
name|params
parameter_list|)
block|{
if|if
condition|(
name|isErrorEnabled
argument_list|()
condition|)
block|{
name|internalError
argument_list|(
name|LoggerMessageFormat
operator|.
name|format
argument_list|(
name|prefix
argument_list|,
name|msg
argument_list|,
name|params
argument_list|)
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|internalError
specifier|protected
specifier|abstract
name|void
name|internalError
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|cause
parameter_list|)
function_decl|;
block|}
end_class

end_unit

