begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2008 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.guice.inject.internal
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
name|internal
package|;
end_package

begin_comment
comment|/**  * Indicates that a result could not be returned while preparing or resolving a binding. The caller  * should {@link Errors#merge(Errors) merge} the errors from this exception with their existing  * errors.  *  * @author jessewilson@google.com (Jesse Wilson)  */
end_comment

begin_class
DECL|class|ErrorsException
specifier|public
class|class
name|ErrorsException
extends|extends
name|Exception
block|{
DECL|field|errors
specifier|private
specifier|final
name|Errors
name|errors
decl_stmt|;
DECL|method|ErrorsException
specifier|public
name|ErrorsException
parameter_list|(
name|Errors
name|errors
parameter_list|)
block|{
name|this
operator|.
name|errors
operator|=
name|errors
expr_stmt|;
block|}
DECL|method|getErrors
specifier|public
name|Errors
name|getErrors
parameter_list|()
block|{
return|return
name|errors
return|;
block|}
block|}
end_class

end_unit

