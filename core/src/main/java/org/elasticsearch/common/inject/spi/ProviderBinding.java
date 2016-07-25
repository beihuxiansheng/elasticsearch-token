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
name|Binding
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
name|Key
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
name|Provider
import|;
end_import

begin_comment
comment|/**  * A binding to a {@link Provider} that delegates to the binding for the provided type. This binding  * is used whenever a {@code Provider<T>} is injected (as opposed to injecting {@code T} directly).  *  * @author jessewilson@google.com (Jesse Wilson)  * @since 2.0  */
end_comment

begin_interface
DECL|interface|ProviderBinding
specifier|public
interface|interface
name|ProviderBinding
parameter_list|<
name|T
extends|extends
name|Provider
parameter_list|<
name|?
parameter_list|>
parameter_list|>
extends|extends
name|Binding
argument_list|<
name|T
argument_list|>
block|{
comment|/**      * Returns the key whose binding is used to {@link Provider#get provide instances}.      */
DECL|method|getProvidedKey
name|Key
argument_list|<
name|?
argument_list|>
name|getProvidedKey
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

