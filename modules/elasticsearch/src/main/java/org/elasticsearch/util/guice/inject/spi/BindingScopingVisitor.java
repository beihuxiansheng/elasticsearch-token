begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2008 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.guice.inject.spi
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|guice
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
name|guice
operator|.
name|inject
operator|.
name|Scope
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Annotation
import|;
end_import

begin_comment
comment|/**  * Visits each of the strategies used to scope an injection.  *  * @param<V> any type to be returned by the visit method. Use {@link Void} with  *     {@code return null} if no return type is needed.  * @since 2.0  */
end_comment

begin_interface
DECL|interface|BindingScopingVisitor
specifier|public
interface|interface
name|BindingScopingVisitor
parameter_list|<
name|V
parameter_list|>
block|{
comment|/**    * Visit an eager singleton or single instance. This scope strategy is found on both module and    * injector bindings.    */
DECL|method|visitEagerSingleton
name|V
name|visitEagerSingleton
parameter_list|()
function_decl|;
comment|/**    * Visit a scope instance. This scope strategy is found on both module and injector bindings.    */
DECL|method|visitScope
name|V
name|visitScope
parameter_list|(
name|Scope
name|scope
parameter_list|)
function_decl|;
comment|/**    * Visit a scope annotation. This scope strategy is found only on module bindings. The instance    * that implements this scope is registered by {@link org.elasticsearch.util.guice.inject.Binder#bindScope(Class,    * Scope) Binder.bindScope()}.    */
DECL|method|visitScopeAnnotation
name|V
name|visitScopeAnnotation
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|scopeAnnotation
parameter_list|)
function_decl|;
comment|/**    * Visit an unspecified or unscoped strategy. On a module, this strategy indicates that the    * injector should use scoping annotations to find a scope. On an injector, it indicates that    * no scope is applied to the binding. An unscoped binding will behave like a scoped one when it    * is linked to a scoped binding.    */
DECL|method|visitNoScoping
name|V
name|visitNoScoping
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

