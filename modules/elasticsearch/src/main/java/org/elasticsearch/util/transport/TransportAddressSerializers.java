begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalStateException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|logging
operator|.
name|Loggers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|MapBuilder
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A global registry of all different types of {@link org.elasticsearch.util.transport.TransportAddress} allowing  * to perfrom serialization of them.  *<p/>  *<p>By defualt, adds {@link org.elasticsearch.util.transport.InetSocketTransportAddress}.  *  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|TransportAddressSerializers
specifier|public
specifier|abstract
class|class
name|TransportAddressSerializers
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|TransportAddressSerializers
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|addressConstructors
specifier|private
specifier|static
name|ImmutableMap
argument_list|<
name|Short
argument_list|,
name|Constructor
argument_list|<
name|?
extends|extends
name|TransportAddress
argument_list|>
argument_list|>
name|addressConstructors
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
static|static
block|{
try|try
block|{
name|addAddressType
argument_list|(
name|DummyTransportAddress
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|addAddressType
argument_list|(
operator|new
name|InetSocketTransportAddress
argument_list|()
argument_list|)
expr_stmt|;
name|addAddressType
argument_list|(
operator|new
name|LocalTransportAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to add InetSocketTransportAddress"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addAddressType
specifier|public
specifier|static
specifier|synchronized
name|void
name|addAddressType
parameter_list|(
name|TransportAddress
name|address
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|addressConstructors
operator|.
name|containsKey
argument_list|(
name|address
operator|.
name|uniqueAddressTypeId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"Address ["
operator|+
name|address
operator|.
name|uniqueAddressTypeId
argument_list|()
operator|+
literal|"] already bound"
argument_list|)
throw|;
block|}
name|Constructor
argument_list|<
name|?
extends|extends
name|TransportAddress
argument_list|>
name|constructor
init|=
name|address
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredConstructor
argument_list|()
decl_stmt|;
name|constructor
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|addressConstructors
operator|=
name|newMapBuilder
argument_list|(
name|addressConstructors
argument_list|)
operator|.
name|put
argument_list|(
name|address
operator|.
name|uniqueAddressTypeId
argument_list|()
argument_list|,
name|constructor
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
DECL|method|addressFromStream
specifier|public
specifier|static
name|TransportAddress
name|addressFromStream
parameter_list|(
name|DataInput
name|input
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|short
name|addressUniqueId
init|=
name|input
operator|.
name|readShort
argument_list|()
decl_stmt|;
name|Constructor
argument_list|<
name|?
extends|extends
name|TransportAddress
argument_list|>
name|constructor
init|=
name|addressConstructors
operator|.
name|get
argument_list|(
name|addressUniqueId
argument_list|)
decl_stmt|;
if|if
condition|(
name|constructor
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
name|TransportAddress
name|address
decl_stmt|;
try|try
block|{
name|address
operator|=
name|constructor
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to create class with constructor ["
operator|+
name|constructor
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|address
operator|.
name|readFrom
argument_list|(
name|input
argument_list|)
expr_stmt|;
return|return
name|address
return|;
block|}
DECL|method|addressToStream
specifier|public
specifier|static
name|void
name|addressToStream
parameter_list|(
name|DataOutput
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

