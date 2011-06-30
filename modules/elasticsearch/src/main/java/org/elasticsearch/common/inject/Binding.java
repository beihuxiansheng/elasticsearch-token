begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2008 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|spi
operator|.
name|BindingScopingVisitor
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
name|inject
operator|.
name|spi
operator|.
name|BindingTargetVisitor
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
name|inject
operator|.
name|spi
operator|.
name|Element
import|;
end_import

begin_comment
comment|/**  * A mapping from a key (type and optional annotation) to the strategy for getting instances of the  * type. This interface is part of the introspection API and is intended primarily for use by  * tools.  *  *<p>Bindings are created in several ways:  *<ul>  *<li>Explicitly in a module, via {@code bind()} and {@code bindConstant()}  * statements:  *<pre>  *     bind(Service.class).annotatedWith(Red.class).to(ServiceImpl.class);  *     bindConstant().annotatedWith(ServerHost.class).to(args[0]);</pre></li>  *<li>Implicitly by the Injector by following a type's {@link ImplementedBy  * pointer} {@link ProvidedBy annotations} or by using its {@link Inject annotated} or  * default constructor.</li>  *<li>By converting a bound instance to a different type.</li>  *<li>For {@link Provider providers}, by delegating to the binding for the provided type.</li>  *</ul>  *  *  *<p>They exist on both modules and on injectors, and their behaviour is different for each:  *<ul>  *<li><strong>Module bindings</strong> are incomplete and cannot be used to provide instances.  * This is because the applicable scopes and interceptors may not be known until an injector  * is created. From a tool's perspective, module bindings are like the injector's source  * code. They can be inspected or rewritten, but this analysis must be done statically.</li>  *<li><strong>Injector bindings</strong> are complete and valid and can be used to provide  * instances. From a tools' perspective, injector bindings are like reflection for an  * injector. They have full runtime information, including the complete graph of injections  * necessary to satisfy a binding.</li>  *</ul>  *  * @param<T> the bound type. The injected is always assignable to this type.  * @author crazybob@google.com (Bob Lee)  * @author jessewilson@google.com (Jesse Wilson)  */
end_comment

begin_interface
DECL|interface|Binding
specifier|public
interface|interface
name|Binding
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Element
block|{
comment|/**      * Returns the key for this binding.      */
DECL|method|getKey
name|Key
argument_list|<
name|T
argument_list|>
name|getKey
parameter_list|()
function_decl|;
comment|/**      * Returns the scoped provider guice uses to fulfill requests for this      * binding.      *      * @throws UnsupportedOperationException when invoked on a {@link Binding}      *                                       created via {@link org.elasticsearch.common.inject.spi.Elements#getElements}. This      *                                       method is only supported on {@link Binding}s returned from an injector.      */
DECL|method|getProvider
name|Provider
argument_list|<
name|T
argument_list|>
name|getProvider
parameter_list|()
function_decl|;
comment|/**      * Accepts a target visitor. Invokes the visitor method specific to this binding's target.      *      * @param visitor to call back on      * @since 2.0      */
DECL|method|acceptTargetVisitor
parameter_list|<
name|V
parameter_list|>
name|V
name|acceptTargetVisitor
parameter_list|(
name|BindingTargetVisitor
argument_list|<
name|?
super|super
name|T
argument_list|,
name|V
argument_list|>
name|visitor
parameter_list|)
function_decl|;
comment|/**      * Accepts a scoping visitor. Invokes the visitor method specific to this binding's scoping.      *      * @param visitor to call back on      * @since 2.0      */
DECL|method|acceptScopingVisitor
parameter_list|<
name|V
parameter_list|>
name|V
name|acceptScopingVisitor
parameter_list|(
name|BindingScopingVisitor
argument_list|<
name|V
argument_list|>
name|visitor
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

