begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|ActionRequestValidationException
specifier|public
class|class
name|ActionRequestValidationException
extends|extends
name|ElasticSearchException
block|{
DECL|field|validationErrors
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|validationErrors
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ActionRequestValidationException
specifier|public
name|ActionRequestValidationException
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|addValidationError
specifier|public
name|void
name|addValidationError
parameter_list|(
name|String
name|error
parameter_list|)
block|{
name|validationErrors
operator|.
name|add
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
DECL|method|addValidationErrors
specifier|public
name|void
name|addValidationErrors
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|errors
parameter_list|)
block|{
for|for
control|(
name|String
name|error
range|:
name|errors
control|)
block|{
name|validationErrors
operator|.
name|add
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|validationErrors
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|validationErrors
parameter_list|()
block|{
return|return
name|validationErrors
return|;
block|}
DECL|method|getMessage
annotation|@
name|Override
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Validation Failed: "
argument_list|)
expr_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|error
range|:
name|validationErrors
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
operator|++
name|index
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

