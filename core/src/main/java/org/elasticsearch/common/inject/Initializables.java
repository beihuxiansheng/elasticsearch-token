begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2008 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|internal
operator|.
name|Errors
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|internal
operator|.
name|ErrorsException
import|;
end_import

begin_comment
comment|/**  * @author jessewilson@google.com (Jesse Wilson)  */
end_comment

begin_class
DECL|class|Initializables
class|class
name|Initializables
block|{
comment|/**      * Returns an initializable for an instance that requires no initialization.      */
DECL|method|of
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Initializable
argument_list|<
name|T
argument_list|>
name|of
parameter_list|(
specifier|final
name|T
name|instance
parameter_list|)
block|{
return|return
operator|new
name|Initializable
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|T
name|get
parameter_list|(
name|Errors
name|errors
parameter_list|)
throws|throws
name|ErrorsException
block|{
return|return
name|instance
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|instance
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

