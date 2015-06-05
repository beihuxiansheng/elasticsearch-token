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
name|Binding
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A binding to the constructor of a concrete clss. To resolve injections, an instance is  * instantiated by invoking the constructor.  *  * @author jessewilson@google.com (Jesse Wilson)  * @since 2.0  */
end_comment

begin_interface
DECL|interface|ConstructorBinding
specifier|public
interface|interface
name|ConstructorBinding
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Binding
argument_list|<
name|T
argument_list|>
extends|,
name|HasDependencies
block|{
comment|/**      * Gets the constructor this binding injects.      */
DECL|method|getConstructor
name|InjectionPoint
name|getConstructor
parameter_list|()
function_decl|;
comment|/**      * Returns all instance method and field injection points on {@code type}.      *      * @return a possibly empty set of injection points. The set has a specified iteration order. All      *         fields are returned and then all methods. Within the fields, supertype fields are returned      *         before subtype fields. Similarly, supertype methods are returned before subtype methods.      */
DECL|method|getInjectableMembers
name|Set
argument_list|<
name|InjectionPoint
argument_list|>
name|getInjectableMembers
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

