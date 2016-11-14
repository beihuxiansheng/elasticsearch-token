begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugins
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
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
name|action
operator|.
name|GenericAction
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
name|support
operator|.
name|ActionFilter
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
name|support
operator|.
name|TransportAction
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
name|support
operator|.
name|TransportActions
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
name|Strings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestHandler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * An additional extension point for {@link Plugin}s that extends Elasticsearch's scripting functionality. Implement it like this:  *<pre>{@code  *   {@literal @}Override  *   public List<ActionHandler<?, ?>> getActions() {  *       return Arrays.asList(new ActionHandler<>(ReindexAction.INSTANCE, TransportReindexAction.class),  *               new ActionHandler<>(UpdateByQueryAction.INSTANCE, TransportUpdateByQueryAction.class),  *               new ActionHandler<>(DeleteByQueryAction.INSTANCE, TransportDeleteByQueryAction.class),  *               new ActionHandler<>(RethrottleAction.INSTANCE, TransportRethrottleAction.class));  *   }  * }</pre>  */
end_comment

begin_interface
DECL|interface|ActionPlugin
specifier|public
interface|interface
name|ActionPlugin
block|{
comment|/**      * Actions added by this plugin.      */
DECL|method|getActions
specifier|default
name|List
argument_list|<
name|ActionHandler
argument_list|<
name|?
extends|extends
name|ActionRequest
argument_list|,
name|?
extends|extends
name|ActionResponse
argument_list|>
argument_list|>
name|getActions
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|/**      * Action filters added by this plugin.      */
DECL|method|getActionFilters
specifier|default
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|ActionFilter
argument_list|>
argument_list|>
name|getActionFilters
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|/**      * Rest handlers added by this plugin.      */
DECL|method|getRestHandlers
specifier|default
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RestHandler
argument_list|>
argument_list|>
name|getRestHandlers
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|/**      * Returns headers which should be copied through rest requests on to internal requests.      */
DECL|method|getRestHeaders
specifier|default
name|Collection
argument_list|<
name|String
argument_list|>
name|getRestHeaders
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
DECL|class|ActionHandler
specifier|final
class|class
name|ActionHandler
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
DECL|field|action
specifier|private
specifier|final
name|GenericAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
name|action
decl_stmt|;
DECL|field|transportAction
specifier|private
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|TransportAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
argument_list|>
name|transportAction
decl_stmt|;
DECL|field|supportTransportActions
specifier|private
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|supportTransportActions
decl_stmt|;
comment|/**          * Create a record of an action, the {@linkplain TransportAction} that handles it, and any supporting {@linkplain TransportActions}          * that are needed by that {@linkplain TransportAction}.          */
DECL|method|ActionHandler
specifier|public
name|ActionHandler
parameter_list|(
name|GenericAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
name|action
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|TransportAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
argument_list|>
name|transportAction
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
modifier|...
name|supportTransportActions
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
name|this
operator|.
name|transportAction
operator|=
name|transportAction
expr_stmt|;
name|this
operator|.
name|supportTransportActions
operator|=
name|supportTransportActions
expr_stmt|;
block|}
DECL|method|getAction
specifier|public
name|GenericAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
name|getAction
parameter_list|()
block|{
return|return
name|action
return|;
block|}
DECL|method|getTransportAction
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|TransportAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
argument_list|>
name|getTransportAction
parameter_list|()
block|{
return|return
name|transportAction
return|;
block|}
DECL|method|getSupportTransportActions
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|getSupportTransportActions
parameter_list|()
block|{
return|return
name|supportTransportActions
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|action
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" is handled by "
argument_list|)
operator|.
name|append
argument_list|(
name|transportAction
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|supportTransportActions
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
operator|.
name|append
argument_list|(
name|Strings
operator|.
name|arrayToCommaDelimitedString
argument_list|(
name|supportTransportActions
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
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
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|ActionHandler
operator|.
name|class
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ActionHandler
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|other
init|=
operator|(
name|ActionHandler
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|action
argument_list|,
name|other
operator|.
name|action
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|transportAction
argument_list|,
name|other
operator|.
name|transportAction
argument_list|)
operator|&&
name|Objects
operator|.
name|deepEquals
argument_list|(
name|supportTransportActions
argument_list|,
name|other
operator|.
name|supportTransportActions
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
name|Objects
operator|.
name|hash
argument_list|(
name|action
argument_list|,
name|transportAction
argument_list|,
name|supportTransportActions
argument_list|)
return|;
block|}
block|}
block|}
end_interface

end_unit

