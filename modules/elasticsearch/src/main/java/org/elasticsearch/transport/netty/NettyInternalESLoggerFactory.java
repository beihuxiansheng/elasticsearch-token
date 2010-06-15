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
name|common
operator|.
name|logging
operator|.
name|Loggers
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
name|netty
operator|.
name|logging
operator|.
name|InternalLogger
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
name|netty
operator|.
name|logging
operator|.
name|InternalLoggerFactory
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|NettyInternalESLoggerFactory
specifier|public
class|class
name|NettyInternalESLoggerFactory
extends|extends
name|InternalLoggerFactory
block|{
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|InternalLogger
name|newInstance
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|NettyInternalESLogger
argument_list|(
name|Loggers
operator|.
name|getLogger
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

