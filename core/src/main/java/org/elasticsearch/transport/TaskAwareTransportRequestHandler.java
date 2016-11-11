begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_comment
comment|/**  * Transport request handlers that is using task context  */
end_comment

begin_class
DECL|class|TaskAwareTransportRequestHandler
specifier|public
specifier|abstract
class|class
name|TaskAwareTransportRequestHandler
parameter_list|<
name|T
extends|extends
name|TransportRequest
parameter_list|>
implements|implements
name|TransportRequestHandler
argument_list|<
name|T
argument_list|>
block|{
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
specifier|final
name|void
name|messageReceived
parameter_list|(
name|T
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"the task parameter is required"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

