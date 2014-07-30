begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
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
name|ActionListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequest
import|;
end_import

begin_comment
comment|/**  * A filter allowing to filter transport actions  */
end_comment

begin_interface
DECL|interface|ActionFilter
specifier|public
interface|interface
name|ActionFilter
block|{
comment|/**      * Filters the actual execution of the request by either sending a response through the {@link ActionListener}      * or continuing the filters execution through the {@link ActionFilterChain}      */
DECL|method|process
name|void
name|process
parameter_list|(
specifier|final
name|String
name|action
parameter_list|,
specifier|final
name|ActionRequest
name|actionRequest
parameter_list|,
specifier|final
name|ActionListener
name|actionListener
parameter_list|,
specifier|final
name|ActionFilterChain
name|actionFilterChain
parameter_list|)
function_decl|;
comment|/**      * The position of the filter in the chain. Execution is done from lowest order to highest.      */
DECL|method|order
name|int
name|order
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

