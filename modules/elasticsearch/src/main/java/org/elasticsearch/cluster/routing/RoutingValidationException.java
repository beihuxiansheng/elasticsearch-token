begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
package|;
end_package

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|RoutingValidationException
specifier|public
class|class
name|RoutingValidationException
extends|extends
name|RoutingException
block|{
DECL|field|validation
specifier|private
specifier|final
name|RoutingTableValidation
name|validation
decl_stmt|;
DECL|method|RoutingValidationException
specifier|public
name|RoutingValidationException
parameter_list|(
name|RoutingTableValidation
name|validation
parameter_list|)
block|{
name|super
argument_list|(
name|validation
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|validation
operator|=
name|validation
expr_stmt|;
block|}
DECL|method|validation
specifier|public
name|RoutingTableValidation
name|validation
parameter_list|()
block|{
return|return
name|this
operator|.
name|validation
return|;
block|}
block|}
end_class

end_unit

