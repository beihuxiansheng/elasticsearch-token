begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2006 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.inject
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|inject
package|;
end_package

begin_comment
comment|/**  * Creates {@link ConstructionProxy} instances.  *  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_interface
DECL|interface|ConstructionProxyFactory
interface|interface
name|ConstructionProxyFactory
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Gets a construction proxy for the given constructor.    */
DECL|method|create
name|ConstructionProxy
argument_list|<
name|T
argument_list|>
name|create
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

