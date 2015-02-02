begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|transport
operator|.
name|BoundTransportAddress
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilderString
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
name|io
operator|.
name|Serializable
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
comment|/**  *  */
end_comment

begin_class
DECL|class|TransportInfo
specifier|public
class|class
name|TransportInfo
implements|implements
name|Streamable
implements|,
name|Serializable
implements|,
name|ToXContent
block|{
DECL|field|address
specifier|private
name|BoundTransportAddress
name|address
decl_stmt|;
DECL|field|profileAddresses
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|BoundTransportAddress
argument_list|>
name|profileAddresses
decl_stmt|;
DECL|method|TransportInfo
name|TransportInfo
parameter_list|()
block|{     }
DECL|method|TransportInfo
specifier|public
name|TransportInfo
parameter_list|(
name|BoundTransportAddress
name|address
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|BoundTransportAddress
argument_list|>
name|profileAddresses
parameter_list|)
block|{
name|this
operator|.
name|address
operator|=
name|address
expr_stmt|;
name|this
operator|.
name|profileAddresses
operator|=
name|profileAddresses
expr_stmt|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|TRANSPORT
specifier|static
specifier|final
name|XContentBuilderString
name|TRANSPORT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"transport"
argument_list|)
decl_stmt|;
DECL|field|BOUND_ADDRESS
specifier|static
specifier|final
name|XContentBuilderString
name|BOUND_ADDRESS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"bound_address"
argument_list|)
decl_stmt|;
DECL|field|PUBLISH_ADDRESS
specifier|static
specifier|final
name|XContentBuilderString
name|PUBLISH_ADDRESS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"publish_address"
argument_list|)
decl_stmt|;
DECL|field|PROFILES
specifier|static
specifier|final
name|XContentBuilderString
name|PROFILES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"profiles"
argument_list|)
decl_stmt|;
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
name|startObject
argument_list|(
name|Fields
operator|.
name|TRANSPORT
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|BOUND_ADDRESS
argument_list|,
name|address
operator|.
name|boundAddress
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|PUBLISH_ADDRESS
argument_list|,
name|address
operator|.
name|publishAddress
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|PROFILES
argument_list|)
expr_stmt|;
if|if
condition|(
name|profileAddresses
operator|!=
literal|null
operator|&&
name|profileAddresses
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|BoundTransportAddress
argument_list|>
name|entry
range|:
name|profileAddresses
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|BOUND_ADDRESS
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|boundAddress
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|PUBLISH_ADDRESS
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|publishAddress
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|readTransportInfo
specifier|public
specifier|static
name|TransportInfo
name|readTransportInfo
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|TransportInfo
name|info
init|=
operator|new
name|TransportInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|info
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
name|address
operator|=
name|BoundTransportAddress
operator|.
name|readBoundTransportAddress
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|profileAddresses
operator|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|BoundTransportAddress
name|value
init|=
name|BoundTransportAddress
operator|.
name|readBoundTransportAddress
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|profileAddresses
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
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
name|address
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|profileAddresses
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|profileAddresses
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|profileAddresses
operator|!=
literal|null
operator|&&
name|profileAddresses
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|BoundTransportAddress
argument_list|>
name|entry
range|:
name|profileAddresses
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|address
specifier|public
name|BoundTransportAddress
name|address
parameter_list|()
block|{
return|return
name|address
return|;
block|}
DECL|method|getAddress
specifier|public
name|BoundTransportAddress
name|getAddress
parameter_list|()
block|{
return|return
name|address
argument_list|()
return|;
block|}
DECL|method|getProfileAddresses
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|BoundTransportAddress
argument_list|>
name|getProfileAddresses
parameter_list|()
block|{
return|return
name|profileAddresses
argument_list|()
return|;
block|}
DECL|method|profileAddresses
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|BoundTransportAddress
argument_list|>
name|profileAddresses
parameter_list|()
block|{
return|return
name|profileAddresses
return|;
block|}
block|}
end_class

end_unit

