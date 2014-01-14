begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.template.get
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|template
operator|.
name|get
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|action
operator|.
name|support
operator|.
name|master
operator|.
name|MasterNodeReadOperationRequest
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
name|Strings
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

begin_comment
comment|/**  * Request that allows to retrieve index templates  */
end_comment

begin_class
DECL|class|GetIndexTemplatesRequest
specifier|public
class|class
name|GetIndexTemplatesRequest
extends|extends
name|MasterNodeReadOperationRequest
argument_list|<
name|GetIndexTemplatesRequest
argument_list|>
block|{
DECL|field|names
specifier|private
name|String
index|[]
name|names
decl_stmt|;
DECL|method|GetIndexTemplatesRequest
specifier|public
name|GetIndexTemplatesRequest
parameter_list|()
block|{     }
DECL|method|GetIndexTemplatesRequest
specifier|public
name|GetIndexTemplatesRequest
parameter_list|(
name|String
modifier|...
name|names
parameter_list|)
block|{
name|this
operator|.
name|names
operator|=
name|names
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
name|names
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"names is null or empty"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
operator|||
operator|!
name|Strings
operator|.
name|hasText
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"name is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|validationException
return|;
block|}
comment|/**      * Sets the names of the index templates.      */
DECL|method|names
specifier|public
name|GetIndexTemplatesRequest
name|names
parameter_list|(
name|String
modifier|...
name|names
parameter_list|)
block|{
name|this
operator|.
name|names
operator|=
name|names
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The names of the index templates.      */
DECL|method|names
specifier|public
name|String
index|[]
name|names
parameter_list|()
block|{
return|return
name|this
operator|.
name|names
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
name|names
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|readLocal
argument_list|(
name|in
argument_list|,
name|Version
operator|.
name|V_1_0_0_RC2
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
name|out
operator|.
name|writeStringArray
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|writeLocal
argument_list|(
name|out
argument_list|,
name|Version
operator|.
name|V_1_0_0_RC2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

