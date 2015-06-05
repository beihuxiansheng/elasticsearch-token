begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectObjectAssociativeContainer
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
name|collect
operator|.
name|ImmutableOpenMap
import|;
end_import

begin_interface
DECL|interface|HasContext
specifier|public
interface|interface
name|HasContext
block|{
comment|/**      * Attaches the given value to the context.      *      * @return  The previous value that was associated with the given key in the context, or      *          {@code null} if there was none.      */
DECL|method|putInContext
parameter_list|<
name|V
parameter_list|>
name|V
name|putInContext
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
function_decl|;
comment|/**      * Attaches the given values to the context      */
DECL|method|putAllInContext
name|void
name|putAllInContext
parameter_list|(
name|ObjectObjectAssociativeContainer
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
function_decl|;
comment|/**      * @return  The context value that is associated with the given key      *      * @see     #putInContext(Object, Object)      */
DECL|method|getFromContext
parameter_list|<
name|V
parameter_list|>
name|V
name|getFromContext
parameter_list|(
name|Object
name|key
parameter_list|)
function_decl|;
comment|/**      * @param defaultValue  The default value that should be returned for the given key, if no      *                      value is currently associated with it.      *      * @return  The value that is associated with the given key in the context      *      * @see     #putInContext(Object, Object)      */
DECL|method|getFromContext
parameter_list|<
name|V
parameter_list|>
name|V
name|getFromContext
parameter_list|(
name|Object
name|key
parameter_list|,
name|V
name|defaultValue
parameter_list|)
function_decl|;
comment|/**      * Checks if the context contains an entry with the given key      */
DECL|method|hasInContext
name|boolean
name|hasInContext
parameter_list|(
name|Object
name|key
parameter_list|)
function_decl|;
comment|/**      * @return  The number of values attached in the context.      */
DECL|method|contextSize
name|int
name|contextSize
parameter_list|()
function_decl|;
comment|/**      * Checks if the context is empty.      */
DECL|method|isContextEmpty
name|boolean
name|isContextEmpty
parameter_list|()
function_decl|;
comment|/**      * @return  A safe immutable copy of the current context.      */
DECL|method|getContext
name|ImmutableOpenMap
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|getContext
parameter_list|()
function_decl|;
comment|/**      * Copies the context from the given context holder to this context holder. Any shared keys between      * the two context will be overridden by the given context holder.      */
DECL|method|copyContextFrom
name|void
name|copyContextFrom
parameter_list|(
name|HasContext
name|other
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

