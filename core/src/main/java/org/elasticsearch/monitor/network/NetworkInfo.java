begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.network
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|network
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|NetworkInfo
specifier|public
class|class
name|NetworkInfo
implements|implements
name|Streamable
implements|,
name|ToXContent
block|{
DECL|field|NA_INTERFACE
specifier|public
specifier|static
specifier|final
name|Interface
name|NA_INTERFACE
init|=
operator|new
name|Interface
argument_list|()
decl_stmt|;
DECL|field|refreshInterval
name|long
name|refreshInterval
decl_stmt|;
DECL|field|primary
name|Interface
name|primary
init|=
name|NA_INTERFACE
decl_stmt|;
DECL|method|refreshInterval
specifier|public
name|long
name|refreshInterval
parameter_list|()
block|{
return|return
name|this
operator|.
name|refreshInterval
return|;
block|}
DECL|method|getRefreshInterval
specifier|public
name|long
name|getRefreshInterval
parameter_list|()
block|{
return|return
name|this
operator|.
name|refreshInterval
return|;
block|}
DECL|method|primaryInterface
specifier|public
name|Interface
name|primaryInterface
parameter_list|()
block|{
return|return
name|primary
return|;
block|}
DECL|method|getPrimaryInterface
specifier|public
name|Interface
name|getPrimaryInterface
parameter_list|()
block|{
return|return
name|primaryInterface
argument_list|()
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|NETWORK
specifier|static
specifier|final
name|XContentBuilderString
name|NETWORK
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"network"
argument_list|)
decl_stmt|;
DECL|field|REFRESH_INTERVAL
specifier|static
specifier|final
name|XContentBuilderString
name|REFRESH_INTERVAL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"refresh_interval"
argument_list|)
decl_stmt|;
DECL|field|REFRESH_INTERVAL_IN_MILLIS
specifier|static
specifier|final
name|XContentBuilderString
name|REFRESH_INTERVAL_IN_MILLIS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"refresh_interval_in_millis"
argument_list|)
decl_stmt|;
DECL|field|PRIMARY_INTERFACE
specifier|static
specifier|final
name|XContentBuilderString
name|PRIMARY_INTERFACE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"primary_interface"
argument_list|)
decl_stmt|;
DECL|field|ADDRESS
specifier|static
specifier|final
name|XContentBuilderString
name|ADDRESS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"address"
argument_list|)
decl_stmt|;
DECL|field|NAME
specifier|static
specifier|final
name|XContentBuilderString
name|NAME
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
DECL|field|MAC_ADDRESS
specifier|static
specifier|final
name|XContentBuilderString
name|MAC_ADDRESS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"mac_address"
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
name|NETWORK
argument_list|)
expr_stmt|;
name|builder
operator|.
name|timeValueField
argument_list|(
name|Fields
operator|.
name|REFRESH_INTERVAL_IN_MILLIS
argument_list|,
name|Fields
operator|.
name|REFRESH_INTERVAL
argument_list|,
name|refreshInterval
argument_list|)
expr_stmt|;
if|if
condition|(
name|primary
operator|!=
name|NA_INTERFACE
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|PRIMARY_INTERFACE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|ADDRESS
argument_list|,
name|primary
operator|.
name|address
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|NAME
argument_list|,
name|primary
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MAC_ADDRESS
argument_list|,
name|primary
operator|.
name|macAddress
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|readNetworkInfo
specifier|public
specifier|static
name|NetworkInfo
name|readNetworkInfo
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|NetworkInfo
name|info
init|=
operator|new
name|NetworkInfo
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
name|refreshInterval
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|primary
operator|=
name|Interface
operator|.
name|readNetworkInterface
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
name|out
operator|.
name|writeLong
argument_list|(
name|refreshInterval
argument_list|)
expr_stmt|;
name|primary
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|class|Interface
specifier|public
specifier|static
class|class
name|Interface
implements|implements
name|Streamable
block|{
DECL|field|name
specifier|private
name|String
name|name
init|=
literal|""
decl_stmt|;
DECL|field|address
specifier|private
name|String
name|address
init|=
literal|""
decl_stmt|;
DECL|field|macAddress
specifier|private
name|String
name|macAddress
init|=
literal|""
decl_stmt|;
DECL|method|Interface
specifier|private
name|Interface
parameter_list|()
block|{         }
DECL|method|Interface
specifier|public
name|Interface
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|address
parameter_list|,
name|String
name|macAddress
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|address
operator|=
name|address
expr_stmt|;
name|this
operator|.
name|macAddress
operator|=
name|macAddress
expr_stmt|;
block|}
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
argument_list|()
return|;
block|}
DECL|method|address
specifier|public
name|String
name|address
parameter_list|()
block|{
return|return
name|address
return|;
block|}
DECL|method|getAddress
specifier|public
name|String
name|getAddress
parameter_list|()
block|{
return|return
name|address
argument_list|()
return|;
block|}
DECL|method|macAddress
specifier|public
name|String
name|macAddress
parameter_list|()
block|{
return|return
name|macAddress
return|;
block|}
DECL|method|getMacAddress
specifier|public
name|String
name|getMacAddress
parameter_list|()
block|{
return|return
name|macAddress
argument_list|()
return|;
block|}
DECL|method|readNetworkInterface
specifier|public
specifier|static
name|Interface
name|readNetworkInterface
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Interface
name|inf
init|=
operator|new
name|Interface
argument_list|()
decl_stmt|;
name|inf
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|inf
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
name|name
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|address
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|macAddress
operator|=
name|in
operator|.
name|readString
argument_list|()
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
name|out
operator|.
name|writeString
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|address
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|macAddress
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

