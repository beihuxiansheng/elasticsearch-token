begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.routing.hash.simple
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|routing
operator|.
name|hash
operator|.
name|simple
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|routing
operator|.
name|hash
operator|.
name|HashFunction
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|SimpleHashFunction
specifier|public
class|class
name|SimpleHashFunction
implements|implements
name|HashFunction
block|{
DECL|method|hash
annotation|@
name|Override
specifier|public
name|int
name|hash
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
return|return
name|type
operator|.
name|hashCode
argument_list|()
operator|+
literal|31
operator|*
name|id
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

