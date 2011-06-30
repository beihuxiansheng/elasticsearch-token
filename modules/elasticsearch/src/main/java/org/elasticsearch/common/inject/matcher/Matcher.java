begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2006 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject.matcher
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|matcher
package|;
end_package

begin_comment
comment|/**  * Returns {@code true} or {@code false} for a given input.  *  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_interface
DECL|interface|Matcher
specifier|public
interface|interface
name|Matcher
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**      * Returns {@code true} if this matches {@code t}, {@code false} otherwise.      */
DECL|method|matches
name|boolean
name|matches
parameter_list|(
name|T
name|t
parameter_list|)
function_decl|;
comment|/**      * Returns a new matcher which returns {@code true} if both this and the      * given matcher return {@code true}.      */
DECL|method|and
name|Matcher
argument_list|<
name|T
argument_list|>
name|and
parameter_list|(
name|Matcher
argument_list|<
name|?
super|super
name|T
argument_list|>
name|other
parameter_list|)
function_decl|;
comment|/**      * Returns a new matcher which returns {@code true} if either this or the      * given matcher return {@code true}.      */
DECL|method|or
name|Matcher
argument_list|<
name|T
argument_list|>
name|or
parameter_list|(
name|Matcher
argument_list|<
name|?
super|super
name|T
argument_list|>
name|other
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

