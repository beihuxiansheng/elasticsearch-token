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
comment|/**  * A binding to a single instance. The same instance is returned for every injection.  *  * @author jessewilson@google.com (Jesse Wilson)  * @since 2.0  */
end_comment

begin_interface
DECL|interface|InstanceBinding
specifier|public
interface|interface
name|InstanceBinding
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
comment|/**    * Returns the user-supplied instance.    */
DECL|method|getInstance
name|T
name|getInstance
parameter_list|()
function_decl|;
comment|/**    * Returns the field and method injection points of the instance, injected at injector-creation    * time only.    *    * @return a possibly empty set    */
DECL|method|getInjectionPoints
name|Set
argument_list|<
name|InjectionPoint
argument_list|>
name|getInjectionPoints
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

