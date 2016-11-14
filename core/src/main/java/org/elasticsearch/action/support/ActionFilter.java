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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|component
operator|.
name|AbstractComponent
import|;
end_import

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
name|tasks
operator|.
name|Task
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
comment|/**      * The position of the filter in the chain. Execution is done from lowest order to highest.      */
DECL|method|order
name|int
name|order
parameter_list|()
function_decl|;
comment|/**      * Enables filtering the execution of an action on the request side, either by sending a response through the      * {@link ActionListener} or by continuing the execution through the given {@link ActionFilterChain chain}      */
DECL|method|apply
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|>
name|void
name|apply
parameter_list|(
name|Task
name|task
parameter_list|,
name|String
name|action
parameter_list|,
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|,
name|ActionFilterChain
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
name|chain
parameter_list|)
function_decl|;
comment|/**      * Enables filtering the execution of an action on the response side, either by sending a response through the      * {@link ActionListener} or by continuing the execution through the given {@link ActionFilterChain chain}      */
DECL|method|apply
parameter_list|<
name|Response
extends|extends
name|ActionResponse
parameter_list|>
name|void
name|apply
parameter_list|(
name|String
name|action
parameter_list|,
name|Response
name|response
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|,
name|ActionFilterChain
argument_list|<
name|?
argument_list|,
name|Response
argument_list|>
name|chain
parameter_list|)
function_decl|;
comment|/**      * A simple base class for injectable action filters that spares the implementation from handling the      * filter chain. This base class should serve any action filter implementations that doesn't require      * to apply async filtering logic.      */
DECL|class|Simple
specifier|public
specifier|abstract
specifier|static
class|class
name|Simple
extends|extends
name|AbstractComponent
implements|implements
name|ActionFilter
block|{
DECL|method|Simple
specifier|protected
name|Simple
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply
specifier|public
specifier|final
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|>
name|void
name|apply
parameter_list|(
name|Task
name|task
parameter_list|,
name|String
name|action
parameter_list|,
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|,
name|ActionFilterChain
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
name|chain
parameter_list|)
block|{
if|if
condition|(
name|apply
argument_list|(
name|action
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
condition|)
block|{
name|chain
operator|.
name|proceed
argument_list|(
name|task
argument_list|,
name|action
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**          * Applies this filter and returns {@code true} if the execution chain should proceed, or {@code false}          * if it should be aborted since the filter already handled the request and called the given listener.          */
DECL|method|apply
specifier|protected
specifier|abstract
name|boolean
name|apply
parameter_list|(
name|String
name|action
parameter_list|,
name|ActionRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|?
argument_list|>
name|listener
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|apply
specifier|public
specifier|final
parameter_list|<
name|Response
extends|extends
name|ActionResponse
parameter_list|>
name|void
name|apply
parameter_list|(
name|String
name|action
parameter_list|,
name|Response
name|response
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|,
name|ActionFilterChain
argument_list|<
name|?
argument_list|,
name|Response
argument_list|>
name|chain
parameter_list|)
block|{
if|if
condition|(
name|apply
argument_list|(
name|action
argument_list|,
name|response
argument_list|,
name|listener
argument_list|)
condition|)
block|{
name|chain
operator|.
name|proceed
argument_list|(
name|action
argument_list|,
name|response
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**          * Applies this filter and returns {@code true} if the execution chain should proceed, or {@code false}          * if it should be aborted since the filter already handled the response by calling the given listener.          */
DECL|method|apply
specifier|protected
specifier|abstract
name|boolean
name|apply
parameter_list|(
name|String
name|action
parameter_list|,
name|ActionResponse
name|response
parameter_list|,
name|ActionListener
argument_list|<
name|?
argument_list|>
name|listener
parameter_list|)
function_decl|;
block|}
block|}
end_interface

end_unit

