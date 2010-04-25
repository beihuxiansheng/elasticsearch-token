begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2007 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * Binds to a constant value.  */
end_comment

begin_interface
DECL|interface|ConstantBindingBuilder
specifier|public
interface|interface
name|ConstantBindingBuilder
block|{
comment|/**    * Binds constant to the given value.    */
DECL|method|to
name|void
name|to
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
comment|/**    * Binds constant to the given value.    */
DECL|method|to
name|void
name|to
parameter_list|(
name|int
name|value
parameter_list|)
function_decl|;
comment|/**    * Binds constant to the given value.    */
DECL|method|to
name|void
name|to
parameter_list|(
name|long
name|value
parameter_list|)
function_decl|;
comment|/**    * Binds constant to the given value.    */
DECL|method|to
name|void
name|to
parameter_list|(
name|boolean
name|value
parameter_list|)
function_decl|;
comment|/**    * Binds constant to the given value.    */
DECL|method|to
name|void
name|to
parameter_list|(
name|double
name|value
parameter_list|)
function_decl|;
comment|/**    * Binds constant to the given value.    */
DECL|method|to
name|void
name|to
parameter_list|(
name|float
name|value
parameter_list|)
function_decl|;
comment|/**    * Binds constant to the given value.    */
DECL|method|to
name|void
name|to
parameter_list|(
name|short
name|value
parameter_list|)
function_decl|;
comment|/**    * Binds constant to the given value.    */
DECL|method|to
name|void
name|to
parameter_list|(
name|char
name|value
parameter_list|)
function_decl|;
comment|/**    * Binds constant to the given value.    */
DECL|method|to
name|void
name|to
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|value
parameter_list|)
function_decl|;
comment|/**    * Binds constant to the given value.    */
DECL|method|to
parameter_list|<
name|E
extends|extends
name|Enum
argument_list|<
name|E
argument_list|>
parameter_list|>
name|void
name|to
parameter_list|(
name|E
name|value
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

