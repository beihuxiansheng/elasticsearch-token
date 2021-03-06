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
name|Provider
import|;
end_import

begin_comment
comment|/**  * A provider with dependencies on other injected types. If a {@link Provider} has dependencies that  * aren't specified in injections, this interface should be used to expose all dependencies.  *  * @since 2.0  */
end_comment

begin_interface
DECL|interface|ProviderWithDependencies
specifier|public
interface|interface
name|ProviderWithDependencies
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Provider
argument_list|<
name|T
argument_list|>
extends|,
name|HasDependencies
block|{ }
end_interface

end_unit

