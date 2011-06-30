begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2008 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject.spi
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|spi
package|;
end_package

begin_comment
comment|/**  * Visits each of the strategies used to find an instance to satisfy an injection.  *  * @param<V> any type to be returned by the visit method. Use {@link Void} with  *            {@code return null} if no return type is needed.  * @since 2.0  */
end_comment

begin_interface
DECL|interface|BindingTargetVisitor
specifier|public
interface|interface
name|BindingTargetVisitor
parameter_list|<
name|T
parameter_list|,
name|V
parameter_list|>
block|{
comment|/**      * Visit a instance binding. The same instance is returned for every injection. This target is      * found in both module and injector bindings.      */
DECL|method|visit
name|V
name|visit
parameter_list|(
name|InstanceBinding
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|binding
parameter_list|)
function_decl|;
comment|/**      * Visit a provider instance binding. The provider's {@code get} method is invoked to resolve      * injections. This target is found in both module and injector bindings.      */
DECL|method|visit
name|V
name|visit
parameter_list|(
name|ProviderInstanceBinding
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|binding
parameter_list|)
function_decl|;
comment|/**      * Visit a provider key binding. To resolve injections, the provider key is first resolved, then      * that provider's {@code get} method is invoked. This target is found in both module and injector      * bindings.      */
DECL|method|visit
name|V
name|visit
parameter_list|(
name|ProviderKeyBinding
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|binding
parameter_list|)
function_decl|;
comment|/**      * Visit a linked key binding. The other key's binding is used to resolve injections. This      * target is found in both module and injector bindings.      */
DECL|method|visit
name|V
name|visit
parameter_list|(
name|LinkedKeyBinding
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|binding
parameter_list|)
function_decl|;
comment|/**      * Visit a binding to a key exposed from an enclosed private environment. This target is only      * found in injector bindings.      */
DECL|method|visit
name|V
name|visit
parameter_list|(
name|ExposedBinding
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|binding
parameter_list|)
function_decl|;
comment|/**      * Visit an untargetted binding. This target is found only on module bindings. It indicates      * that the injector should use its implicit binding strategies to resolve injections.      */
DECL|method|visit
name|V
name|visit
parameter_list|(
name|UntargettedBinding
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|binding
parameter_list|)
function_decl|;
comment|/**      * Visit a constructor binding. To resolve injections, an instance is instantiated by invoking      * {@code constructor}. This target is found only on injector bindings.      */
DECL|method|visit
name|V
name|visit
parameter_list|(
name|ConstructorBinding
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|binding
parameter_list|)
function_decl|;
comment|/**      * Visit a binding created from converting a bound instance to a new type. The source binding      * has the same binding annotation but a different type. This target is found only on injector      * bindings.      */
DECL|method|visit
name|V
name|visit
parameter_list|(
name|ConvertedConstantBinding
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|binding
parameter_list|)
function_decl|;
comment|/**      * Visit a binding to a {@link org.elasticsearch.common.inject.Provider} that delegates to the binding for the      * provided type. This target is found only on injector bindings.      */
DECL|method|visit
name|V
name|visit
parameter_list|(
name|ProviderBinding
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|binding
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

