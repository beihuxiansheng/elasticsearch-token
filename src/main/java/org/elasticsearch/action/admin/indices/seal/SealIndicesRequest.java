begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.seal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|seal
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|broadcast
operator|.
name|BroadcastRequest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  * A request to seal one or more indices.  */
end_comment

begin_class
DECL|class|SealIndicesRequest
specifier|public
class|class
name|SealIndicesRequest
extends|extends
name|BroadcastRequest
block|{
DECL|method|SealIndicesRequest
name|SealIndicesRequest
parameter_list|()
block|{     }
comment|/**      * Constructs a seal request against one or more indices. If nothing is provided, all indices will      * be sealed.      */
DECL|method|SealIndicesRequest
specifier|public
name|SealIndicesRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|super
argument_list|(
name|indices
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SealIndicesRequest{"
operator|+
literal|"indices="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|indices
argument_list|)
operator|+
literal|", indicesOptions="
operator|+
name|indicesOptions
argument_list|()
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

