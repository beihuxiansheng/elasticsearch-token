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
name|common
operator|.
name|bytes
operator|.
name|BytesReference
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

begin_comment
comment|/**  * A specialized, bytes only request, that can potentially be optimized on the network  * layer, specifically for teh same large buffer send to several nodes.  */
end_comment

begin_class
DECL|class|BytesTransportRequest
specifier|public
class|class
name|BytesTransportRequest
extends|extends
name|TransportRequest
block|{
DECL|field|bytes
name|BytesReference
name|bytes
decl_stmt|;
DECL|field|version
name|Version
name|version
decl_stmt|;
DECL|method|BytesTransportRequest
specifier|public
name|BytesTransportRequest
parameter_list|()
block|{      }
DECL|method|BytesTransportRequest
specifier|public
name|BytesTransportRequest
parameter_list|(
name|BytesReference
name|bytes
parameter_list|,
name|Version
name|version
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
DECL|method|version
specifier|public
name|Version
name|version
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
DECL|method|bytes
specifier|public
name|BytesReference
name|bytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|bytes
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
name|bytes
operator|=
name|in
operator|.
name|readBytesReference
argument_list|()
expr_stmt|;
name|version
operator|=
name|in
operator|.
name|getVersion
argument_list|()
expr_stmt|;
block|}
comment|/**      * Writes the data in a "thin" manner, without the actual bytes, assumes      * the actual bytes will be appended right after this content.      */
DECL|method|writeThin
specifier|public
name|void
name|writeThin
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
name|writeVInt
argument_list|(
name|bytes
operator|.
name|length
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
name|out
operator|.
name|writeBytesReference
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

