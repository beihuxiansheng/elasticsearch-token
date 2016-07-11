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
comment|/**  * A binding to a provider key. To resolve injections, the provider key is first resolved, then that  * provider's {@code get} method is invoked.  *  * @author jessewilson@google.com (Jesse Wilson)  * @since 2.0  */
end_comment

begin_interface
DECL|interface|ProviderKeyBinding
specifier|public
interface|interface
name|ProviderKeyBinding
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Binding
argument_list|<
name|T
argument_list|>
block|{
comment|/**      * Returns the key used to resolve the provider's binding.      */
DECL|method|getProviderKey
name|Key
argument_list|<
name|?
extends|extends
name|Provider
argument_list|<
name|?
extends|extends
name|T
argument_list|>
argument_list|>
name|getProviderKey
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

