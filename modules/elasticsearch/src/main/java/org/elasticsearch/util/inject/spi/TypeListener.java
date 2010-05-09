begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2009 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.inject.spi
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
operator|.
name|inject
operator|.
name|TypeLiteral
import|;
end_import

begin_comment
comment|/**  * Listens for Guice to encounter injectable types. If a given type has its constructor injected in  * one situation but only its methods and fields injected in another, Guice will notify this  * listener once.  *  *<p>Useful for extra type checking, {@linkplain TypeEncounter#register(InjectionListener)  * registering injection listeners}, and {@linkplain TypeEncounter#bindInterceptor(  * org.elasticsearch.util.inject.matcher.Matcher, org.aopalliance.intercept.MethodInterceptor[])  * binding method interceptors}.  *   * @since 2.0  */
end_comment

begin_interface
DECL|interface|TypeListener
specifier|public
interface|interface
name|TypeListener
block|{
comment|/**    * Invoked when Guice encounters a new type eligible for constructor or members injection.    * Called during injector creation (or afterwords if Guice encounters a type at run time and    * creates a JIT binding).    *    * @param type encountered by Guice    * @param encounter context of this encounter, enables reporting errors, registering injection    *     listeners and binding method interceptors for {@code type}.    *    * @param<I> the injectable type    */
DECL|method|hear
parameter_list|<
name|I
parameter_list|>
name|void
name|hear
parameter_list|(
name|TypeLiteral
argument_list|<
name|I
argument_list|>
name|type
parameter_list|,
name|TypeEncounter
argument_list|<
name|I
argument_list|>
name|encounter
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

