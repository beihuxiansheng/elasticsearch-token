begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2006 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.guice.inject
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|guice
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
name|util
operator|.
name|guice
operator|.
name|inject
operator|.
name|spi
operator|.
name|InjectionPoint
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_comment
comment|/**  * Proxies calls to a {@link java.lang.reflect.Constructor} for a class  * {@code T}.  *  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_interface
DECL|interface|ConstructionProxy
interface|interface
name|ConstructionProxy
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Constructs an instance of {@code T} for the given arguments.    */
DECL|method|newInstance
name|T
name|newInstance
parameter_list|(
name|Object
modifier|...
name|arguments
parameter_list|)
throws|throws
name|InvocationTargetException
function_decl|;
comment|/**    * Returns the injection point for this constructor.    */
DECL|method|getInjectionPoint
name|InjectionPoint
name|getInjectionPoint
parameter_list|()
function_decl|;
comment|/**    * Returns the injected constructor. If the injected constructor is synthetic (such as generated    * code for method interception), the natural constructor is returned.    */
DECL|method|getConstructor
name|Constructor
argument_list|<
name|T
argument_list|>
name|getConstructor
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

