begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
package|;
end_package

begin_comment
comment|/**  * Simplest representation of a connection to an elasticsearch node.  * It doesn't have any mutable state. It holds the node that the connection points to.  * Allows the transport to deal with very simple connection objects that are immutable.  * Any change to the state of connections should be made through the connection pool  * which is aware of the connection object that it supports.  */
end_comment

begin_class
DECL|class|Connection
specifier|public
class|class
name|Connection
block|{
DECL|field|node
specifier|private
specifier|final
name|Node
name|node
decl_stmt|;
comment|/**      * Creates a new connection pointing to the provided {@link Node} argument      */
DECL|method|Connection
specifier|public
name|Connection
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
comment|/**      * Returns the {@link Node} that the connection points to      */
DECL|method|getNode
specifier|public
name|Node
name|getNode
parameter_list|()
block|{
return|return
name|node
return|;
block|}
block|}
end_class

end_unit

