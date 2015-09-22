begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2006 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
package|;
end_package

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
name|Map
import|;
end_import

begin_comment
comment|/**  * Builds the graphs of objects that make up your application. The injector tracks the dependencies  * for each type and uses bindings to inject them. This is the core of Guice, although you rarely  * interact with it directly. This "behind-the-scenes" operation is what distinguishes dependency  * injection from its cousin, the service locator pattern.  *<p>  * Contains several default bindings:  *<ul>  *<li>This {@link Injector} instance itself  *<li>A {@code Provider<T>} for each binding of type {@code T}  *<li>The {@link java.util.logging.Logger} for the class being injected  *<li>The {@link Stage} in which the Injector was created  *</ul>  *<p>  * Injectors are created using the facade class {@link Guice}.  *<p>  * An injector can also {@link #injectMembers(Object) inject the dependencies} of  * already-constructed instances. This can be used to interoperate with objects created by other  * frameworks or services.  *<p>  * Injectors can be {@link #createChildInjector(Iterable) hierarchical}. Child injectors inherit  * the configuration of their parent injectors, but the converse does not hold.  *<p>  * The injector's {@link #getBindings() internal bindings} are available for introspection. This  * enables tools and extensions to operate on an injector reflectively.  *  * @author crazybob@google.com (Bob Lee)  * @author jessewilson@google.com (Jesse Wilson)  */
end_comment

begin_interface
DECL|interface|Injector
specifier|public
interface|interface
name|Injector
block|{
comment|/**      * Injects dependencies into the fields and methods of {@code instance}. Ignores the presence or      * absence of an injectable constructor.      *<p>      * Whenever Guice creates an instance, it performs this injection automatically (after first      * performing constructor injection), so if you're able to let Guice create all your objects for      * you, you'll never need to use this method.      *      * @param instance to inject members on      * @see Binder#getMembersInjector(Class) for a preferred alternative that supports checks before      *      run time      */
DECL|method|injectMembers
name|void
name|injectMembers
parameter_list|(
name|Object
name|instance
parameter_list|)
function_decl|;
comment|/**      * Returns the members injector used to inject dependencies into methods and fields on instances      * of the given type {@code T}.      *      * @param typeLiteral type to get members injector for      * @see Binder#getMembersInjector(TypeLiteral) for an alternative that offers up front error      *      detection      * @since 2.0      */
DECL|method|getMembersInjector
parameter_list|<
name|T
parameter_list|>
name|MembersInjector
argument_list|<
name|T
argument_list|>
name|getMembersInjector
parameter_list|(
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|typeLiteral
parameter_list|)
function_decl|;
comment|/**      * Returns the members injector used to inject dependencies into methods and fields on instances      * of the given type {@code T}. When feasible, use {@link Binder#getMembersInjector(TypeLiteral)}      * instead to get increased up front error detection.      *      * @param type type to get members injector for      * @see Binder#getMembersInjector(Class) for an alternative that offers up front error      *      detection      * @since 2.0      */
DECL|method|getMembersInjector
parameter_list|<
name|T
parameter_list|>
name|MembersInjector
argument_list|<
name|T
argument_list|>
name|getMembersInjector
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
function_decl|;
comment|/**      * Returns all explicit bindings.      *<p>      * The returned map does not include bindings inherited from a {@link #getParent() parent      * injector}, should one exist. The returned map is guaranteed to iterate (for example, with      * its {@link java.util.Map#entrySet()} iterator) in the order of insertion. In other words,      * the order in which bindings appear in user Modules.      *<p>      * This method is part of the Guice SPI and is intended for use by tools and extensions.      */
DECL|method|getBindings
name|Map
argument_list|<
name|Key
argument_list|<
name|?
argument_list|>
argument_list|,
name|Binding
argument_list|<
name|?
argument_list|>
argument_list|>
name|getBindings
parameter_list|()
function_decl|;
comment|/**      * Returns the binding for the given injection key. This will be an explicit bindings if the key      * was bound explicitly by a module, or an implicit binding otherwise. The implicit binding will      * be created if necessary.      *<p>      * This method is part of the Guice SPI and is intended for use by tools and extensions.      *      * @throws ConfigurationException if this injector cannot find or create the binding.      */
DECL|method|getBinding
parameter_list|<
name|T
parameter_list|>
name|Binding
argument_list|<
name|T
argument_list|>
name|getBinding
parameter_list|(
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|)
function_decl|;
comment|/**      * Returns the binding for the given type. This will be an explicit bindings if the injection key      * was bound explicitly by a module, or an implicit binding otherwise. The implicit binding will      * be created if necessary.      *<p>      * This method is part of the Guice SPI and is intended for use by tools and extensions.      *      * @throws ConfigurationException if this injector cannot find or create the binding.      * @since 2.0      */
DECL|method|getBinding
parameter_list|<
name|T
parameter_list|>
name|Binding
argument_list|<
name|T
argument_list|>
name|getBinding
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
function_decl|;
comment|/**      * Returns all explicit bindings for {@code type}.      *<p>      * This method is part of the Guice SPI and is intended for use by tools and extensions.      */
DECL|method|findBindingsByType
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|Binding
argument_list|<
name|T
argument_list|>
argument_list|>
name|findBindingsByType
parameter_list|(
name|TypeLiteral
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
function_decl|;
comment|/**      * Returns the provider used to obtain instances for the given injection key. When feasible, avoid      * using this method, in favor of having Guice inject your dependencies ahead of time.      *      * @throws ConfigurationException if this injector cannot find or create the provider.      * @see Binder#getProvider(Key) for an alternative that offers up front error detection      */
DECL|method|getProvider
parameter_list|<
name|T
parameter_list|>
name|Provider
argument_list|<
name|T
argument_list|>
name|getProvider
parameter_list|(
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|)
function_decl|;
comment|/**      * Returns the provider used to obtain instances for the given type. When feasible, avoid      * using this method, in favor of having Guice inject your dependencies ahead of time.      *      * @throws ConfigurationException if this injector cannot find or create the provider.      * @see Binder#getProvider(Class) for an alternative that offers up front error detection      */
DECL|method|getProvider
parameter_list|<
name|T
parameter_list|>
name|Provider
argument_list|<
name|T
argument_list|>
name|getProvider
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
function_decl|;
comment|/**      * Returns the appropriate instance for the given injection key; equivalent to {@code      * getProvider(key).get()}. When feasible, avoid using this method, in favor of having Guice      * inject your dependencies ahead of time.      *      * @throws ConfigurationException if this injector cannot find or create the provider.      * @throws ProvisionException     if there was a runtime failure while providing an instance.      */
DECL|method|getInstance
parameter_list|<
name|T
parameter_list|>
name|T
name|getInstance
parameter_list|(
name|Key
argument_list|<
name|T
argument_list|>
name|key
parameter_list|)
function_decl|;
comment|/**      * Returns the appropriate instance for the given injection type; equivalent to {@code      * getProvider(type).get()}. When feasible, avoid using this method, in favor of having Guice      * inject your dependencies ahead of time.      *      * @throws ConfigurationException if this injector cannot find or create the provider.      * @throws ProvisionException     if there was a runtime failure while providing an instance.      */
DECL|method|getInstance
parameter_list|<
name|T
parameter_list|>
name|T
name|getInstance
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
function_decl|;
comment|/**      * Returns this injector's parent, or {@code null} if this is a top-level injector.      *      * @since 2.0      */
DECL|method|getParent
name|Injector
name|getParent
parameter_list|()
function_decl|;
comment|/**      * Returns a new injector that inherits all state from this injector. All bindings, scopes,      * interceptors and type converters are inherited -- they are visible to the child injector.      * Elements of the child injector are not visible to its parent.      *<p>      * Just-in-time bindings created for child injectors will be created in an ancestor injector      * whenever possible. This allows for scoped instances to be shared between injectors. Use      * explicit bindings to prevent bindings from being shared with the parent injector.      *<p>      * No key may be bound by both an injector and one of its ancestors. This includes just-in-time      * bindings. The lone exception is the key for {@code Injector.class}, which is bound by each      * injector to itself.      *      * @since 2.0      */
DECL|method|createChildInjector
name|Injector
name|createChildInjector
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|modules
parameter_list|)
function_decl|;
comment|/**      * Returns a new injector that inherits all state from this injector. All bindings, scopes,      * interceptors and type converters are inherited -- they are visible to the child injector.      * Elements of the child injector are not visible to its parent.      *<p>      * Just-in-time bindings created for child injectors will be created in an ancestor injector      * whenever possible. This allows for scoped instances to be shared between injectors. Use      * explicit bindings to prevent bindings from being shared with the parent injector.      *<p>      * No key may be bound by both an injector and one of its ancestors. This includes just-in-time      * bindings. The lone exception is the key for {@code Injector.class}, which is bound by each      * injector to itself.      *      * @since 2.0      */
DECL|method|createChildInjector
name|Injector
name|createChildInjector
parameter_list|(
name|Module
modifier|...
name|modules
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

