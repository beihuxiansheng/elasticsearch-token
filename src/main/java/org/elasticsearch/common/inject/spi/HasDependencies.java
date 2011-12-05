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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Implemented by {@link org.elasticsearch.common.inject.Binding bindings}, {@link org.elasticsearch.common.inject.Provider  * providers} and instances that expose their dependencies explicitly.  *  * @author jessewilson@google.com (Jesse Wilson)  * @since 2.0  */
end_comment

begin_interface
DECL|interface|HasDependencies
specifier|public
interface|interface
name|HasDependencies
block|{
comment|/**      * Returns the known dependencies for this type. If this has dependencies whose values are not      * known statically, a dependency for the {@link org.elasticsearch.common.inject.Injector Injector} will be      * included in the returned set.      *      * @return a possibly empty set      */
DECL|method|getDependencies
name|Set
argument_list|<
name|Dependency
argument_list|<
name|?
argument_list|>
argument_list|>
name|getDependencies
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

