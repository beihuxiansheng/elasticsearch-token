begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2006 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.inject.binder
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|inject
operator|.
name|binder
package|;
end_package

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
comment|/**  * See the EDSL examples at {@link org.elasticsearch.util.inject.Binder}.  *  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_interface
DECL|interface|AnnotatedBindingBuilder
specifier|public
interface|interface
name|AnnotatedBindingBuilder
parameter_list|<
name|T
parameter_list|>
extends|extends
name|LinkedBindingBuilder
argument_list|<
name|T
argument_list|>
block|{
comment|/**    * See the EDSL examples at {@link org.elasticsearch.util.inject.Binder}.    */
DECL|method|annotatedWith
name|LinkedBindingBuilder
argument_list|<
name|T
argument_list|>
name|annotatedWith
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|annotationType
parameter_list|)
function_decl|;
comment|/**    * See the EDSL examples at {@link org.elasticsearch.util.inject.Binder}.    */
DECL|method|annotatedWith
name|LinkedBindingBuilder
argument_list|<
name|T
argument_list|>
name|annotatedWith
parameter_list|(
name|Annotation
name|annotation
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

