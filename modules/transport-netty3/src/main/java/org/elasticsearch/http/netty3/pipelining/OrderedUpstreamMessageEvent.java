begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.http.netty3.pipelining
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|netty3
operator|.
name|pipelining
package|;
end_package

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_comment
comment|// this file is from netty-http-pipelining, under apache 2.0 license
end_comment

begin_comment
comment|// see github.com/typesafehub/netty-http-pipelining
end_comment

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|Channel
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
name|channel
operator|.
name|UpstreamMessageEvent
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
import|;
end_import

begin_comment
comment|/**  * Permits upstream message events to be ordered.  *  * @author Christopher Hunt  */
end_comment

begin_class
DECL|class|OrderedUpstreamMessageEvent
specifier|public
class|class
name|OrderedUpstreamMessageEvent
extends|extends
name|UpstreamMessageEvent
block|{
DECL|field|sequence
specifier|final
name|int
name|sequence
decl_stmt|;
DECL|method|OrderedUpstreamMessageEvent
specifier|public
name|OrderedUpstreamMessageEvent
parameter_list|(
specifier|final
name|int
name|sequence
parameter_list|,
specifier|final
name|Channel
name|channel
parameter_list|,
specifier|final
name|Object
name|msg
parameter_list|,
specifier|final
name|SocketAddress
name|remoteAddress
parameter_list|)
block|{
name|super
argument_list|(
name|channel
argument_list|,
name|msg
argument_list|,
name|remoteAddress
argument_list|)
expr_stmt|;
name|this
operator|.
name|sequence
operator|=
name|sequence
expr_stmt|;
block|}
DECL|method|getSequence
specifier|public
name|int
name|getSequence
parameter_list|()
block|{
return|return
name|sequence
return|;
block|}
block|}
end_class

end_unit

