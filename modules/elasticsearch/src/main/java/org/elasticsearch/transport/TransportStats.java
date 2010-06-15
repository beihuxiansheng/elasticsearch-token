begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|unit
operator|.
name|ByteSizeValue
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
name|builder
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
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TransportStats
specifier|public
class|class
name|TransportStats
implements|implements
name|Streamable
implements|,
name|Serializable
implements|,
name|ToXContent
block|{
DECL|field|rxCount
specifier|private
name|long
name|rxCount
decl_stmt|;
DECL|field|rxSize
specifier|private
name|long
name|rxSize
decl_stmt|;
DECL|field|txCount
specifier|private
name|long
name|txCount
decl_stmt|;
DECL|field|txSize
specifier|private
name|long
name|txSize
decl_stmt|;
DECL|method|TransportStats
name|TransportStats
parameter_list|()
block|{     }
DECL|method|TransportStats
specifier|public
name|TransportStats
parameter_list|(
name|long
name|rxCount
parameter_list|,
name|long
name|rxSize
parameter_list|,
name|long
name|txCount
parameter_list|,
name|long
name|txSize
parameter_list|)
block|{
name|this
operator|.
name|rxCount
operator|=
name|rxCount
expr_stmt|;
name|this
operator|.
name|rxSize
operator|=
name|rxSize
expr_stmt|;
name|this
operator|.
name|txCount
operator|=
name|txCount
expr_stmt|;
name|this
operator|.
name|txSize
operator|=
name|txSize
expr_stmt|;
block|}
DECL|method|toXContent
annotation|@
name|Override
specifier|public
name|void
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
literal|"transport"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"rx_count"
argument_list|,
name|rxCount
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"rx_size"
argument_list|,
name|rxSize
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
literal|"rx_size_in_bytes"
argument_list|,
name|rxSize
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"tx_count"
argument_list|,
name|txCount
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"tx_size"
argument_list|,
name|txSize
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
literal|"tx_size_in_bytes"
argument_list|,
name|txSize
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
DECL|method|readTransportStats
specifier|public
specifier|static
name|TransportStats
name|readTransportStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|TransportStats
name|stats
init|=
operator|new
name|TransportStats
argument_list|()
decl_stmt|;
name|stats
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|stats
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
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
name|rxCount
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|rxSize
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|txCount
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|txSize
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
block|}
DECL|method|writeTo
annotation|@
name|Override
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
name|writeVLong
argument_list|(
name|rxCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|rxSize
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|txCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|txSize
argument_list|)
expr_stmt|;
block|}
DECL|method|rxCount
specifier|public
name|long
name|rxCount
parameter_list|()
block|{
return|return
name|rxCount
return|;
block|}
DECL|method|getRxCount
specifier|public
name|long
name|getRxCount
parameter_list|()
block|{
return|return
name|rxCount
argument_list|()
return|;
block|}
DECL|method|rxSize
specifier|public
name|ByteSizeValue
name|rxSize
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|rxSize
argument_list|)
return|;
block|}
DECL|method|getRxSize
specifier|public
name|ByteSizeValue
name|getRxSize
parameter_list|()
block|{
return|return
name|rxSize
argument_list|()
return|;
block|}
DECL|method|txCount
specifier|public
name|long
name|txCount
parameter_list|()
block|{
return|return
name|txCount
return|;
block|}
DECL|method|getTxCount
specifier|public
name|long
name|getTxCount
parameter_list|()
block|{
return|return
name|txCount
argument_list|()
return|;
block|}
DECL|method|txSize
specifier|public
name|ByteSizeValue
name|txSize
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|txSize
argument_list|)
return|;
block|}
DECL|method|getTxSize
specifier|public
name|ByteSizeValue
name|getTxSize
parameter_list|()
block|{
return|return
name|txSize
argument_list|()
return|;
block|}
block|}
end_class

end_unit

