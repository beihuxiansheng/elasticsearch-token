begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2008 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Binder
import|;
end_import

begin_comment
comment|/**  * A core component of a module or injector.  *<p>  * The elements of a module can be inspected, validated and rewritten. Use {@link  * Elements#getElements(org.elasticsearch.common.inject.Module[]) Elements.getElements()} to read the elements  * from a module, and {@link Elements#getModule(Iterable) Elements.getModule()} to rewrite them.  * This can be used for static analysis and generation of Guice modules.  *  * @author jessewilson@google.com (Jesse Wilson)  * @author crazybob@google.com (Bob Lee)  * @since 2.0  */
end_comment

begin_interface
DECL|interface|Element
specifier|public
interface|interface
name|Element
block|{
comment|/**      * Returns an arbitrary object containing information about the "place" where this element was      * configured. Used by Guice in the production of descriptive error messages.      *<p>      * Tools might specially handle types they know about; {@code StackTraceElement} is a good      * example. Tools should simply call {@code toString()} on the source object if the type is      * unfamiliar.      */
DECL|method|getSource
name|Object
name|getSource
parameter_list|()
function_decl|;
comment|/**      * Accepts an element visitor. Invokes the visitor method specific to this element's type.      *      * @param visitor to call back on      */
DECL|method|acceptVisitor
parameter_list|<
name|T
parameter_list|>
name|T
name|acceptVisitor
parameter_list|(
name|ElementVisitor
argument_list|<
name|T
argument_list|>
name|visitor
parameter_list|)
function_decl|;
comment|/**      * Writes this module element to the given binder (optional operation).      *      * @param binder to apply configuration element to      * @throws UnsupportedOperationException if the {@code applyTo} method is not supported by this      *                                       element.      */
DECL|method|applyTo
name|void
name|applyTo
parameter_list|(
name|Binder
name|binder
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

