begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.logging
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|logging
package|;
end_package

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_interface
DECL|interface|ESLogger
specifier|public
interface|interface
name|ESLogger
block|{
DECL|method|getPrefix
name|String
name|getPrefix
parameter_list|()
function_decl|;
DECL|method|getName
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|setLevel
name|void
name|setLevel
parameter_list|(
name|String
name|level
parameter_list|)
function_decl|;
comment|/**      * Returns {@code true} if a TRACE level message is logged.      */
DECL|method|isTraceEnabled
name|boolean
name|isTraceEnabled
parameter_list|()
function_decl|;
comment|/**      * Returns {@code true} if a DEBUG level message is logged.      */
DECL|method|isDebugEnabled
name|boolean
name|isDebugEnabled
parameter_list|()
function_decl|;
comment|/**      * Returns {@code true} if an INFO level message is logged.      */
DECL|method|isInfoEnabled
name|boolean
name|isInfoEnabled
parameter_list|()
function_decl|;
comment|/**      * Returns {@code true} if a WARN level message is logged.      */
DECL|method|isWarnEnabled
name|boolean
name|isWarnEnabled
parameter_list|()
function_decl|;
comment|/**      * Returns {@code true} if an ERROR level message is logged.      */
DECL|method|isErrorEnabled
name|boolean
name|isErrorEnabled
parameter_list|()
function_decl|;
comment|/**      * Logs a DEBUG level message.      */
DECL|method|trace
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
function_decl|;
comment|/**      * Logs a DEBUG level message.      */
DECL|method|trace
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
function_decl|;
comment|/**      * Logs a DEBUG level message.      */
DECL|method|debug
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
function_decl|;
comment|/**      * Logs a DEBUG level message.      */
DECL|method|debug
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
function_decl|;
comment|/**      * Logs an INFO level message.      */
DECL|method|info
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
function_decl|;
comment|/**      * Logs an INFO level message.      */
DECL|method|info
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
function_decl|;
comment|/**      * Logs a WARN level message.      */
DECL|method|warn
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
function_decl|;
comment|/**      * Logs a WARN level message.      */
DECL|method|warn
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
function_decl|;
comment|/**      * Logs an ERROR level message.      */
DECL|method|error
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
function_decl|;
comment|/**      * Logs an ERROR level message.      */
DECL|method|error
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
function_decl|;
block|}
end_interface

end_unit

