begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|inject
operator|.
name|AbstractModule
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
name|inject
operator|.
name|Module
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
name|transport
operator|.
name|local
operator|.
name|LocalTransportModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|Classes
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|ModulesFactory
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|TransportModule
specifier|public
class|class
name|TransportModule
extends|extends
name|AbstractModule
block|{
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|method|TransportModule
specifier|public
name|TransportModule
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|TransportService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|TransportServiceManagement
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|defaultTransportModule
decl_stmt|;
if|if
condition|(
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"node.local"
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|defaultTransportModule
operator|=
name|LocalTransportModule
operator|.
name|class
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|Classes
operator|.
name|getDefaultClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
literal|"org.elasticsearch.transport.netty.NettyTransport"
argument_list|)
expr_stmt|;
name|defaultTransportModule
operator|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
operator|)
name|Classes
operator|.
name|getDefaultClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
literal|"org.elasticsearch.transport.netty.NettyTransportModule"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|defaultTransportModule
operator|=
name|LocalTransportModule
operator|.
name|class
expr_stmt|;
block|}
block|}
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|moduleClass
init|=
name|settings
operator|.
name|getAsClass
argument_list|(
literal|"transport.type"
argument_list|,
name|defaultTransportModule
argument_list|,
literal|"org.elasticsearch.transport."
argument_list|,
literal|"TransportModule"
argument_list|)
decl_stmt|;
name|createModule
argument_list|(
name|moduleClass
argument_list|,
name|settings
argument_list|)
operator|.
name|configure
argument_list|(
name|binder
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

