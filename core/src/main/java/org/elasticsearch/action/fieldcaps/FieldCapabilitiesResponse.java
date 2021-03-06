begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.fieldcaps
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|fieldcaps
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
name|ActionResponse
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
name|xcontent
operator|.
name|ToXContent
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
name|xcontent
operator|.
name|XContentBuilder
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
name|Collections
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Response for {@link FieldCapabilitiesRequest} requests.  */
end_comment

begin_class
DECL|class|FieldCapabilitiesResponse
specifier|public
class|class
name|FieldCapabilitiesResponse
extends|extends
name|ActionResponse
implements|implements
name|ToXContent
block|{
DECL|field|responseMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FieldCapabilities
argument_list|>
argument_list|>
name|responseMap
decl_stmt|;
DECL|field|indexResponses
specifier|private
name|List
argument_list|<
name|FieldCapabilitiesIndexResponse
argument_list|>
name|indexResponses
decl_stmt|;
DECL|method|FieldCapabilitiesResponse
name|FieldCapabilitiesResponse
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FieldCapabilities
argument_list|>
argument_list|>
name|responseMap
parameter_list|)
block|{
name|this
argument_list|(
name|responseMap
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|FieldCapabilitiesResponse
name|FieldCapabilitiesResponse
parameter_list|(
name|List
argument_list|<
name|FieldCapabilitiesIndexResponse
argument_list|>
name|indexResponses
parameter_list|)
block|{
name|this
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|indexResponses
argument_list|)
expr_stmt|;
block|}
DECL|method|FieldCapabilitiesResponse
specifier|private
name|FieldCapabilitiesResponse
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FieldCapabilities
argument_list|>
argument_list|>
name|responseMap
parameter_list|,
name|List
argument_list|<
name|FieldCapabilitiesIndexResponse
argument_list|>
name|indexResponses
parameter_list|)
block|{
name|this
operator|.
name|responseMap
operator|=
name|responseMap
expr_stmt|;
name|this
operator|.
name|indexResponses
operator|=
name|indexResponses
expr_stmt|;
block|}
comment|/**      * Used for serialization      */
DECL|method|FieldCapabilitiesResponse
name|FieldCapabilitiesResponse
parameter_list|()
block|{
name|this
operator|.
name|responseMap
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
comment|/**      * Get the field capabilities map.      */
DECL|method|get
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FieldCapabilities
argument_list|>
argument_list|>
name|get
parameter_list|()
block|{
return|return
name|responseMap
return|;
block|}
comment|/**      * Returns the actual per-index field caps responses      */
DECL|method|getIndexResponses
name|List
argument_list|<
name|FieldCapabilitiesIndexResponse
argument_list|>
name|getIndexResponses
parameter_list|()
block|{
return|return
name|indexResponses
return|;
block|}
comment|/**      *      * Get the field capabilities per type for the provided {@code field}.      */
DECL|method|getField
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|FieldCapabilities
argument_list|>
name|getField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|responseMap
operator|.
name|get
argument_list|(
name|field
argument_list|)
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
name|this
operator|.
name|responseMap
operator|=
name|in
operator|.
name|readMap
argument_list|(
name|StreamInput
operator|::
name|readString
argument_list|,
name|FieldCapabilitiesResponse
operator|::
name|readField
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_5_5_0
argument_list|)
condition|)
block|{
name|indexResponses
operator|=
name|in
operator|.
name|readList
argument_list|(
name|FieldCapabilitiesIndexResponse
operator|::
operator|new
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|indexResponses
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|readField
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|FieldCapabilities
argument_list|>
name|readField
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readMap
argument_list|(
name|StreamInput
operator|::
name|readString
argument_list|,
name|FieldCapabilities
operator|::
operator|new
argument_list|)
return|;
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
name|writeMap
argument_list|(
name|responseMap
argument_list|,
name|StreamOutput
operator|::
name|writeString
argument_list|,
name|FieldCapabilitiesResponse
operator|::
name|writeField
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_5_5_0
argument_list|)
condition|)
block|{
name|out
operator|.
name|writeList
argument_list|(
name|indexResponses
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeField
specifier|private
specifier|static
name|void
name|writeField
parameter_list|(
name|StreamOutput
name|out
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FieldCapabilities
argument_list|>
name|map
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeMap
argument_list|(
name|map
argument_list|,
name|StreamOutput
operator|::
name|writeString
argument_list|,
parameter_list|(
name|valueOut
parameter_list|,
name|fc
parameter_list|)
lambda|->
name|fc
operator|.
name|writeTo
argument_list|(
name|valueOut
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"fields"
argument_list|,
name|responseMap
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|FieldCapabilitiesResponse
name|that
init|=
operator|(
name|FieldCapabilitiesResponse
operator|)
name|o
decl_stmt|;
return|return
name|responseMap
operator|.
name|equals
argument_list|(
name|that
operator|.
name|responseMap
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|responseMap
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

