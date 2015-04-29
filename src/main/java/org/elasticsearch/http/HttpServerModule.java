begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.http
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|http
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|logging
operator|.
name|ESLogger
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
name|http
operator|.
name|netty
operator|.
name|NettyHttpServerTransport
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|HttpServerModule
specifier|public
class|class
name|HttpServerModule
extends|extends
name|AbstractModule
block|{
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|field|configuredHttpServerTransport
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|HttpServerTransport
argument_list|>
name|configuredHttpServerTransport
decl_stmt|;
DECL|field|configuredHttpServerTransportSource
specifier|private
name|String
name|configuredHttpServerTransportSource
decl_stmt|;
DECL|method|HttpServerModule
specifier|public
name|HttpServerModule
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
name|this
operator|.
name|logger
operator|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|,
name|settings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{
if|if
condition|(
name|configuredHttpServerTransport
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Using [{}] as http transport, overridden by [{}]"
argument_list|,
name|configuredHttpServerTransport
operator|.
name|getName
argument_list|()
argument_list|,
name|configuredHttpServerTransportSource
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|HttpServerTransport
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|configuredHttpServerTransport
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Class
argument_list|<
name|?
extends|extends
name|HttpServerTransport
argument_list|>
name|defaultHttpServerTransport
init|=
name|NettyHttpServerTransport
operator|.
name|class
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|HttpServerTransport
argument_list|>
name|httpServerTransport
init|=
name|settings
operator|.
name|getAsClass
argument_list|(
literal|"http.type"
argument_list|,
name|defaultHttpServerTransport
argument_list|,
literal|"org.elasticsearch.http."
argument_list|,
literal|"HttpServerTransport"
argument_list|)
decl_stmt|;
name|bind
argument_list|(
name|HttpServerTransport
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|httpServerTransport
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
name|bind
argument_list|(
name|HttpServer
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
DECL|method|setHttpServerTransport
specifier|public
name|void
name|setHttpServerTransport
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|HttpServerTransport
argument_list|>
name|httpServerTransport
parameter_list|,
name|String
name|source
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|httpServerTransport
argument_list|,
literal|"Configured http server transport may not be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|source
argument_list|,
literal|"Plugin, that changes transport may not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|configuredHttpServerTransport
operator|=
name|httpServerTransport
expr_stmt|;
name|this
operator|.
name|configuredHttpServerTransportSource
operator|=
name|source
expr_stmt|;
block|}
block|}
end_class

end_unit

