begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.warmer.delete
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
name|warmer
operator|.
name|delete
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
name|IndicesRequest
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
name|IndicesOptions
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
name|AcknowledgedRequest
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
name|Nullable
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|CollectionUtils
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
comment|/**  * A request to delete an index warmer.  */
end_comment

begin_class
DECL|class|DeleteWarmerRequest
specifier|public
class|class
name|DeleteWarmerRequest
extends|extends
name|AcknowledgedRequest
argument_list|<
name|DeleteWarmerRequest
argument_list|>
implements|implements
name|IndicesRequest
block|{
DECL|field|names
specifier|private
name|String
index|[]
name|names
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|indicesOptions
specifier|private
name|IndicesOptions
name|indicesOptions
init|=
name|IndicesOptions
operator|.
name|fromOptions
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|indices
specifier|private
name|String
index|[]
name|indices
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|method|DeleteWarmerRequest
name|DeleteWarmerRequest
parameter_list|()
block|{     }
comment|/**      * Constructs a new delete warmer request for the specified name.      *      * @param names: the name (or wildcard expression) of the warmer to match, null to delete all.      */
DECL|method|DeleteWarmerRequest
specifier|public
name|DeleteWarmerRequest
parameter_list|(
name|String
modifier|...
name|names
parameter_list|)
block|{
name|names
argument_list|(
name|names
argument_list|)
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
name|CollectionUtils
operator|.
name|isEmpty
argument_list|(
name|names
argument_list|)
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"warmer names are missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|validationException
operator|=
name|checkForEmptyString
argument_list|(
name|validationException
argument_list|,
name|names
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|CollectionUtils
operator|.
name|isEmpty
argument_list|(
name|indices
argument_list|)
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"indices are missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|validationException
operator|=
name|checkForEmptyString
argument_list|(
name|validationException
argument_list|,
name|indices
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
return|;
block|}
DECL|method|checkForEmptyString
specifier|private
name|ActionRequestValidationException
name|checkForEmptyString
parameter_list|(
name|ActionRequestValidationException
name|validationException
parameter_list|,
name|String
index|[]
name|strings
parameter_list|)
block|{
name|boolean
name|containsEmptyString
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|string
range|:
name|strings
control|)
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|hasText
argument_list|(
name|string
argument_list|)
condition|)
block|{
name|containsEmptyString
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|containsEmptyString
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"types must not contain empty strings"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
return|;
block|}
comment|/**      * The name to delete.      */
annotation|@
name|Nullable
DECL|method|names
name|String
index|[]
name|names
parameter_list|()
block|{
return|return
name|names
return|;
block|}
comment|/**      * The name (or wildcard expression) of the index warmer to delete, or null      * to delete all warmers.      */
DECL|method|names
specifier|public
name|DeleteWarmerRequest
name|names
parameter_list|(
annotation|@
name|Nullable
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
comment|/**      * Sets the indices this put mapping operation will execute on.      */
DECL|method|indices
specifier|public
name|DeleteWarmerRequest
name|indices
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The indices the mappings will be put.      */
annotation|@
name|Override
DECL|method|indices
specifier|public
name|String
index|[]
name|indices
parameter_list|()
block|{
return|return
name|indices
return|;
block|}
annotation|@
name|Override
DECL|method|indicesOptions
specifier|public
name|IndicesOptions
name|indicesOptions
parameter_list|()
block|{
return|return
name|indicesOptions
return|;
block|}
DECL|method|indicesOptions
specifier|public
name|DeleteWarmerRequest
name|indicesOptions
parameter_list|(
name|IndicesOptions
name|indicesOptions
parameter_list|)
block|{
name|this
operator|.
name|indicesOptions
operator|=
name|indicesOptions
expr_stmt|;
return|return
name|this
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
name|indices
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|indicesOptions
operator|=
name|IndicesOptions
operator|.
name|readIndicesOptions
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|readTimeout
argument_list|(
name|in
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
name|writeStringArrayNullable
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArrayNullable
argument_list|(
name|indices
argument_list|)
expr_stmt|;
name|indicesOptions
operator|.
name|writeIndicesOptions
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|writeTimeout
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

