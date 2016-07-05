begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
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
name|Writeable
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
name|HashMap
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableMap
import|;
end_import

begin_comment
comment|/**  * A global registry of all supported types of {@link TransportAddress}s. This registry is not open for modification by plugins.  */
end_comment

begin_class
DECL|class|TransportAddressSerializers
specifier|public
specifier|abstract
class|class
name|TransportAddressSerializers
block|{
DECL|field|ADDRESS_REGISTRY
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Short
argument_list|,
name|Writeable
operator|.
name|Reader
argument_list|<
name|TransportAddress
argument_list|>
argument_list|>
name|ADDRESS_REGISTRY
decl_stmt|;
static|static
block|{
name|Map
argument_list|<
name|Short
argument_list|,
name|Writeable
operator|.
name|Reader
argument_list|<
name|TransportAddress
argument_list|>
argument_list|>
name|registry
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|addAddressType
argument_list|(
name|registry
argument_list|,
name|InetSocketTransportAddress
operator|.
name|TYPE_ID
argument_list|,
name|InetSocketTransportAddress
operator|::
operator|new
argument_list|)
expr_stmt|;
name|addAddressType
argument_list|(
name|registry
argument_list|,
name|LocalTransportAddress
operator|.
name|TYPE_ID
argument_list|,
name|LocalTransportAddress
operator|::
operator|new
argument_list|)
expr_stmt|;
name|ADDRESS_REGISTRY
operator|=
name|unmodifiableMap
argument_list|(
name|registry
argument_list|)
expr_stmt|;
block|}
DECL|method|addAddressType
specifier|private
specifier|static
name|void
name|addAddressType
parameter_list|(
name|Map
argument_list|<
name|Short
argument_list|,
name|Writeable
operator|.
name|Reader
argument_list|<
name|TransportAddress
argument_list|>
argument_list|>
name|registry
parameter_list|,
name|short
name|uniqueAddressTypeId
parameter_list|,
name|Writeable
operator|.
name|Reader
argument_list|<
name|TransportAddress
argument_list|>
name|address
parameter_list|)
block|{
if|if
condition|(
name|registry
operator|.
name|containsKey
argument_list|(
name|uniqueAddressTypeId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Address ["
operator|+
name|uniqueAddressTypeId
operator|+
literal|"] already bound"
argument_list|)
throw|;
block|}
name|registry
operator|.
name|put
argument_list|(
name|uniqueAddressTypeId
argument_list|,
name|address
argument_list|)
expr_stmt|;
block|}
DECL|method|addressFromStream
specifier|public
specifier|static
name|TransportAddress
name|addressFromStream
parameter_list|(
name|StreamInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO why don't we just use named writeables here?
name|short
name|addressUniqueId
init|=
name|input
operator|.
name|readShort
argument_list|()
decl_stmt|;
name|Writeable
operator|.
name|Reader
argument_list|<
name|TransportAddress
argument_list|>
name|addressType
init|=
name|ADDRESS_REGISTRY
operator|.
name|get
argument_list|(
name|addressUniqueId
argument_list|)
decl_stmt|;
if|if
condition|(
name|addressType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No transport address mapped to ["
operator|+
name|addressUniqueId
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|addressType
operator|.
name|read
argument_list|(
name|input
argument_list|)
return|;
block|}
DECL|method|addressToStream
specifier|public
specifier|static
name|void
name|addressToStream
parameter_list|(
name|StreamOutput
name|out
parameter_list|,
name|TransportAddress
name|address
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeShort
argument_list|(
name|address
operator|.
name|uniqueAddressTypeId
argument_list|()
argument_list|)
expr_stmt|;
name|address
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

