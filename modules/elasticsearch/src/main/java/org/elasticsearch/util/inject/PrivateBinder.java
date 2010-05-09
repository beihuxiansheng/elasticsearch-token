begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2008 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|binder
operator|.
name|AnnotatedElementBuilder
import|;
end_import

begin_comment
comment|/**  * Returns a binder whose configuration information is hidden from its environment by default. See  * {@link org.elasticsearch.util.inject.PrivateModule PrivateModule} for details.  *   * @author jessewilson@google.com (Jesse Wilson)  * @since 2.0  */
end_comment

begin_interface
DECL|interface|PrivateBinder
specifier|public
interface|interface
name|PrivateBinder
extends|extends
name|Binder
block|{
comment|/** Makes the binding for {@code key} available to the enclosing environment */
DECL|method|expose
name|void
name|expose
parameter_list|(
name|Key
argument_list|<
name|?
argument_list|>
name|key
parameter_list|)
function_decl|;
comment|/**    * Makes a binding for {@code type} available to the enclosing environment. Use {@link    * org.elasticsearch.util.inject.binder.AnnotatedElementBuilder#annotatedWith(Class) annotatedWith()} to expose {@code type} with a    * binding annotation.    */
DECL|method|expose
name|AnnotatedElementBuilder
name|expose
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
function_decl|;
comment|/**    * Makes a binding for {@code type} available to the enclosing environment. Use {@link    * AnnotatedElementBuilder#annotatedWith(Class) annotatedWith()} to expose {@code type} with a    * binding annotation.    */
DECL|method|expose
name|AnnotatedElementBuilder
name|expose
parameter_list|(
name|TypeLiteral
argument_list|<
name|?
argument_list|>
name|type
parameter_list|)
function_decl|;
DECL|method|withSource
name|PrivateBinder
name|withSource
parameter_list|(
name|Object
name|source
parameter_list|)
function_decl|;
DECL|method|skipSources
name|PrivateBinder
name|skipSources
parameter_list|(
name|Class
modifier|...
name|classesToSkip
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

