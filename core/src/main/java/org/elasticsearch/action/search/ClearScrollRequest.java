begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequestValidationException
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Arrays
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ValidateActions
operator|.
name|addValidationError
import|;
end_import

begin_class
DECL|class|ClearScrollRequest
specifier|public
class|class
name|ClearScrollRequest
extends|extends
name|ActionRequest
argument_list|<
name|ClearScrollRequest
argument_list|>
block|{
DECL|field|scrollIds
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|scrollIds
decl_stmt|;
DECL|method|getScrollIds
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getScrollIds
parameter_list|()
block|{
return|return
name|scrollIds
return|;
block|}
DECL|method|setScrollIds
specifier|public
name|void
name|setScrollIds
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|scrollIds
parameter_list|)
block|{
name|this
operator|.
name|scrollIds
operator|=
name|scrollIds
expr_stmt|;
block|}
DECL|method|addScrollId
specifier|public
name|void
name|addScrollId
parameter_list|(
name|String
name|scrollId
parameter_list|)
block|{
if|if
condition|(
name|scrollIds
operator|==
literal|null
condition|)
block|{
name|scrollIds
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|scrollIds
operator|.
name|add
argument_list|(
name|scrollId
argument_list|)
expr_stmt|;
block|}
DECL|method|scrollIds
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|scrollIds
parameter_list|()
block|{
return|return
name|scrollIds
return|;
block|}
DECL|method|scrollIds
specifier|public
name|void
name|scrollIds
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|scrollIds
parameter_list|)
block|{
name|this
operator|.
name|scrollIds
operator|=
name|scrollIds
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|validationException
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|scrollIds
operator|==
literal|null
operator|||
name|scrollIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"no scroll ids specified"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|scrollIds
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|in
operator|.
name|readStringArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|scrollIds
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeStringArray
argument_list|(
name|scrollIds
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|scrollIds
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

