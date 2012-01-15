begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
package|;
end_package

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ValidateActions
specifier|public
class|class
name|ValidateActions
block|{
DECL|method|addValidationError
specifier|public
specifier|static
name|ActionRequestValidationException
name|addValidationError
parameter_list|(
name|String
name|error
parameter_list|,
name|ActionRequestValidationException
name|validationException
parameter_list|)
block|{
if|if
condition|(
name|validationException
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
operator|new
name|ActionRequestValidationException
argument_list|()
expr_stmt|;
block|}
name|validationException
operator|.
name|addValidationError
argument_list|(
name|error
argument_list|)
expr_stmt|;
return|return
name|validationException
return|;
block|}
block|}
end_class

end_unit

