begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2008 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.inject.spi
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|inject
operator|.
name|spi
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|inject
operator|.
name|Binding
import|;
end_import

begin_comment
comment|/**  * Visit elements.  *  * @param<V> any type to be returned by the visit method. Use {@link Void} with  *     {@code return null} if no return type is needed.  *   * @since 2.0  */
end_comment

begin_interface
DECL|interface|ElementVisitor
specifier|public
interface|interface
name|ElementVisitor
parameter_list|<
name|V
parameter_list|>
block|{
comment|/**    * Visit a mapping from a key (type and optional annotation) to the strategy for getting    * instances of the type.    */
DECL|method|visit
parameter_list|<
name|T
parameter_list|>
name|V
name|visit
parameter_list|(
name|Binding
argument_list|<
name|T
argument_list|>
name|binding
parameter_list|)
function_decl|;
comment|/**    * Visit a registration of a scope annotation with the scope that implements it.    */
DECL|method|visit
name|V
name|visit
parameter_list|(
name|ScopeBinding
name|binding
parameter_list|)
function_decl|;
comment|/**    * Visit a registration of type converters for matching target types.    */
DECL|method|visit
name|V
name|visit
parameter_list|(
name|TypeConverterBinding
name|binding
parameter_list|)
function_decl|;
comment|/**    * Visit a request to inject the instance fields and methods of an instance.    */
DECL|method|visit
name|V
name|visit
parameter_list|(
name|InjectionRequest
name|request
parameter_list|)
function_decl|;
comment|/**    * Visit a request to inject the static fields and methods of type.    */
DECL|method|visit
name|V
name|visit
parameter_list|(
name|StaticInjectionRequest
name|request
parameter_list|)
function_decl|;
comment|/**    * Visit a lookup of the provider for a type.    */
DECL|method|visit
parameter_list|<
name|T
parameter_list|>
name|V
name|visit
parameter_list|(
name|ProviderLookup
argument_list|<
name|T
argument_list|>
name|lookup
parameter_list|)
function_decl|;
comment|/**    * Visit a lookup of the members injector.    */
DECL|method|visit
parameter_list|<
name|T
parameter_list|>
name|V
name|visit
parameter_list|(
name|MembersInjectorLookup
argument_list|<
name|T
argument_list|>
name|lookup
parameter_list|)
function_decl|;
comment|/**    * Visit an error message and the context in which it occured.    */
DECL|method|visit
name|V
name|visit
parameter_list|(
name|Message
name|message
parameter_list|)
function_decl|;
comment|/**    * Visit a collection of configuration elements for a {@linkplain org.elasticsearch.util.inject.PrivateBinder    * private binder}.    */
DECL|method|visit
name|V
name|visit
parameter_list|(
name|PrivateElements
name|elements
parameter_list|)
function_decl|;
comment|/**    * Visit an injectable type listener binding.    */
DECL|method|visit
name|V
name|visit
parameter_list|(
name|TypeListenerBinding
name|binding
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

