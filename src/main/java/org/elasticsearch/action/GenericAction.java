begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
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
name|TransportRequestOptions
import|;
end_import

begin_comment
comment|/**  * A generic action. Should strive to make it a singleton.  */
end_comment

begin_class
DECL|class|GenericAction
specifier|public
specifier|abstract
class|class
name|GenericAction
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|>
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**      * @param name The name of the action, must be unique across actions.      */
DECL|method|GenericAction
specifier|protected
name|GenericAction
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * The name of the action. Must be unique across actions.      */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
comment|/**      * Creates a new response instance.      */
DECL|method|newResponse
specifier|public
specifier|abstract
name|Response
name|newResponse
parameter_list|()
function_decl|;
comment|/**      * Optional request options for the action.      */
DECL|method|transportOptions
specifier|public
name|TransportRequestOptions
name|transportOptions
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
return|return
name|TransportRequestOptions
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|name
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|GenericAction
operator|)
name|o
operator|)
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|name
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

