begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2006 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.guice.inject.binder
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
operator|.
name|binder
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
name|Scope
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Annotation
import|;
end_import

begin_comment
comment|/**  * See the EDSL examples at {@link org.elasticsearch.util.guice.inject.Binder}.  *  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_interface
DECL|interface|ScopedBindingBuilder
specifier|public
interface|interface
name|ScopedBindingBuilder
block|{
comment|/**    * See the EDSL examples at {@link org.elasticsearch.util.guice.inject.Binder}.    */
DECL|method|in
name|void
name|in
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|scopeAnnotation
parameter_list|)
function_decl|;
comment|/**    * See the EDSL examples at {@link org.elasticsearch.util.guice.inject.Binder}.    */
DECL|method|in
name|void
name|in
parameter_list|(
name|Scope
name|scope
parameter_list|)
function_decl|;
comment|/**    * Instructs the {@link org.elasticsearch.util.guice.inject.Injector} to eagerly initialize this    * singleton-scoped binding upon creation. Useful for application    * initialization logic.  See the EDSL examples at    * {@link org.elasticsearch.util.guice.inject.Binder}.    */
DECL|method|asEagerSingleton
name|void
name|asEagerSingleton
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

