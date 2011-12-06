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

begin_comment
comment|/**  * An exception indicating that a transport action was not found.  *  *  */
end_comment

begin_class
DECL|class|ActionNotFoundTransportException
specifier|public
class|class
name|ActionNotFoundTransportException
extends|extends
name|TransportException
block|{
DECL|field|action
specifier|private
specifier|final
name|String
name|action
decl_stmt|;
DECL|method|ActionNotFoundTransportException
specifier|public
name|ActionNotFoundTransportException
parameter_list|(
name|String
name|action
parameter_list|)
block|{
name|super
argument_list|(
literal|"No handler for action ["
operator|+
name|action
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
block|}
DECL|method|action
specifier|public
name|String
name|action
parameter_list|()
block|{
return|return
name|this
operator|.
name|action
return|;
block|}
block|}
end_class

end_unit

