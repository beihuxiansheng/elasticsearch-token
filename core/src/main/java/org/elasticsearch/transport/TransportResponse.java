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

begin_class
DECL|class|TransportResponse
specifier|public
specifier|abstract
class|class
name|TransportResponse
extends|extends
name|TransportMessage
block|{
DECL|class|Empty
specifier|public
specifier|static
class|class
name|Empty
extends|extends
name|TransportResponse
block|{
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|Empty
name|INSTANCE
init|=
operator|new
name|Empty
argument_list|()
decl_stmt|;
block|}
block|}
end_class

end_unit

