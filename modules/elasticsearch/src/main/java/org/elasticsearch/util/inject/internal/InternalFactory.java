begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2006 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.inject.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|inject
operator|.
name|internal
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
name|spi
operator|.
name|Dependency
import|;
end_import

begin_comment
comment|/**  * Creates objects which will be injected.  *  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_interface
DECL|interface|InternalFactory
specifier|public
interface|interface
name|InternalFactory
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Creates an object to be injected.    *    * @param context of this injection    * @throws org.elasticsearch.util.inject.internal.ErrorsException if a value cannot be provided    * @return instance to be injected    */
DECL|method|get
name|T
name|get
parameter_list|(
name|Errors
name|errors
parameter_list|,
name|InternalContext
name|context
parameter_list|,
name|Dependency
argument_list|<
name|?
argument_list|>
name|dependency
parameter_list|)
throws|throws
name|ErrorsException
function_decl|;
block|}
end_interface

end_unit

