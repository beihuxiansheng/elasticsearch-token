begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2009 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
package|;
end_package

begin_comment
comment|/**  * Injects dependencies into the fields and methods on instances of type {@code T}. Ignores the  * presence or absence of an injectable constructor.  *  * @param<T> type to inject members of  * @author crazybob@google.com (Bob Lee)  * @author jessewilson@google.com (Jesse Wilson)  * @since 2.0  */
end_comment

begin_interface
DECL|interface|MembersInjector
specifier|public
interface|interface
name|MembersInjector
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**      * Injects dependencies into the fields and methods of {@code instance}. Ignores the presence or      * absence of an injectable constructor.      *      *<p>Whenever Guice creates an instance, it performs this injection automatically (after first      * performing constructor injection), so if you're able to let Guice create all your objects for      * you, you'll never need to use this method.      *      * @param instance to inject members on. May be {@code null}.      */
DECL|method|injectMembers
name|void
name|injectMembers
parameter_list|(
name|T
name|instance
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

