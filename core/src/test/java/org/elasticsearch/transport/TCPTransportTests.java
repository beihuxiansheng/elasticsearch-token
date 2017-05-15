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
name|action
operator|.
name|ActionListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
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
name|compress
operator|.
name|CompressorFactory
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
name|settings
operator|.
name|Settings
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
name|TransportAddress
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
name|TimeValue
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
name|BigArrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|TestThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
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
name|net
operator|.
name|InetSocketAddress
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_comment
comment|/** Unit tests for TCPTransport */
end_comment

begin_class
DECL|class|TCPTransportTests
specifier|public
class|class
name|TCPTransportTests
extends|extends
name|ESTestCase
block|{
comment|/** Test ipv4 host with a default port works */
DECL|method|testParseV4DefaultPort
specifier|public
name|void
name|testParseV4DefaultPort
parameter_list|()
throws|throws
name|Exception
block|{
name|TransportAddress
index|[]
name|addresses
init|=
name|TcpTransport
operator|.
name|parse
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|"1234"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|addresses
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1234
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Test ipv4 host with a default port range works */
DECL|method|testParseV4DefaultRange
specifier|public
name|void
name|testParseV4DefaultRange
parameter_list|()
throws|throws
name|Exception
block|{
name|TransportAddress
index|[]
name|addresses
init|=
name|TcpTransport
operator|.
name|parse
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|"1234-1235"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|addresses
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1234
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|addresses
index|[
literal|1
index|]
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1235
argument_list|,
name|addresses
index|[
literal|1
index|]
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Test ipv4 host with port works */
DECL|method|testParseV4WithPort
specifier|public
name|void
name|testParseV4WithPort
parameter_list|()
throws|throws
name|Exception
block|{
name|TransportAddress
index|[]
name|addresses
init|=
name|TcpTransport
operator|.
name|parse
argument_list|(
literal|"127.0.0.1:2345"
argument_list|,
literal|"1234"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|addresses
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2345
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Test ipv4 host with port range works */
DECL|method|testParseV4WithPortRange
specifier|public
name|void
name|testParseV4WithPortRange
parameter_list|()
throws|throws
name|Exception
block|{
name|TransportAddress
index|[]
name|addresses
init|=
name|TcpTransport
operator|.
name|parse
argument_list|(
literal|"127.0.0.1:2345-2346"
argument_list|,
literal|"1234"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|addresses
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2345
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|addresses
index|[
literal|1
index|]
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2346
argument_list|,
name|addresses
index|[
literal|1
index|]
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Test unbracketed ipv6 hosts in configuration fail. Leave no ambiguity */
DECL|method|testParseV6UnBracketed
specifier|public
name|void
name|testParseV6UnBracketed
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|TcpTransport
operator|.
name|parse
argument_list|(
literal|"::1"
argument_list|,
literal|"1234"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have gotten exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"must be bracketed"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test ipv6 host with a default port works */
DECL|method|testParseV6DefaultPort
specifier|public
name|void
name|testParseV6DefaultPort
parameter_list|()
throws|throws
name|Exception
block|{
name|TransportAddress
index|[]
name|addresses
init|=
name|TcpTransport
operator|.
name|parse
argument_list|(
literal|"[::1]"
argument_list|,
literal|"1234"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|addresses
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"::1"
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1234
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Test ipv6 host with a default port range works */
DECL|method|testParseV6DefaultRange
specifier|public
name|void
name|testParseV6DefaultRange
parameter_list|()
throws|throws
name|Exception
block|{
name|TransportAddress
index|[]
name|addresses
init|=
name|TcpTransport
operator|.
name|parse
argument_list|(
literal|"[::1]"
argument_list|,
literal|"1234-1235"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|addresses
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"::1"
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1234
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"::1"
argument_list|,
name|addresses
index|[
literal|1
index|]
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1235
argument_list|,
name|addresses
index|[
literal|1
index|]
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Test ipv6 host with port works */
DECL|method|testParseV6WithPort
specifier|public
name|void
name|testParseV6WithPort
parameter_list|()
throws|throws
name|Exception
block|{
name|TransportAddress
index|[]
name|addresses
init|=
name|TcpTransport
operator|.
name|parse
argument_list|(
literal|"[::1]:2345"
argument_list|,
literal|"1234"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|addresses
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"::1"
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2345
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Test ipv6 host with port range works */
DECL|method|testParseV6WithPortRange
specifier|public
name|void
name|testParseV6WithPortRange
parameter_list|()
throws|throws
name|Exception
block|{
name|TransportAddress
index|[]
name|addresses
init|=
name|TcpTransport
operator|.
name|parse
argument_list|(
literal|"[::1]:2345-2346"
argument_list|,
literal|"1234"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|addresses
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"::1"
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2345
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"::1"
argument_list|,
name|addresses
index|[
literal|1
index|]
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2346
argument_list|,
name|addresses
index|[
literal|1
index|]
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Test per-address limit */
DECL|method|testAddressLimit
specifier|public
name|void
name|testAddressLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|TransportAddress
index|[]
name|addresses
init|=
name|TcpTransport
operator|.
name|parse
argument_list|(
literal|"[::1]:100-200"
argument_list|,
literal|"1000"
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|addresses
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|addresses
index|[
literal|0
index|]
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|101
argument_list|,
name|addresses
index|[
literal|1
index|]
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|102
argument_list|,
name|addresses
index|[
literal|2
index|]
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCompressRequest
specifier|public
name|void
name|testCompressRequest
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|boolean
name|compressed
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
specifier|final
name|AtomicBoolean
name|called
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Req
name|request
init|=
operator|new
name|Req
argument_list|(
name|randomRealisticUnicodeOfLengthBetween
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
argument_list|)
decl_stmt|;
name|ThreadPool
name|threadPool
init|=
operator|new
name|TestThreadPool
argument_list|(
name|TCPTransportTests
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|AtomicReference
argument_list|<
name|IOException
argument_list|>
name|exceptionReference
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|TcpTransport
name|transport
init|=
operator|new
name|TcpTransport
argument_list|(
literal|"test"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"transport.tcp.compress"
argument_list|,
name|compressed
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|threadPool
argument_list|,
operator|new
name|BigArrays
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|InetSocketAddress
name|getLocalAddress
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Object
name|bind
parameter_list|(
name|String
name|name
parameter_list|,
name|InetSocketAddress
name|address
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|closeChannels
parameter_list|(
name|List
name|channel
parameter_list|)
throws|throws
name|IOException
block|{                  }
annotation|@
name|Override
specifier|protected
name|void
name|sendMessage
parameter_list|(
name|Object
name|o
parameter_list|,
name|BytesReference
name|reference
parameter_list|,
name|ActionListener
name|listener
parameter_list|)
block|{
try|try
block|{
name|StreamInput
name|streamIn
init|=
name|reference
operator|.
name|streamInput
argument_list|()
decl_stmt|;
name|streamIn
operator|.
name|skip
argument_list|(
name|TcpHeader
operator|.
name|MARKER_BYTES_SIZE
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|streamIn
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|long
name|requestId
init|=
name|streamIn
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|42
argument_list|,
name|requestId
argument_list|)
expr_stmt|;
name|byte
name|status
init|=
name|streamIn
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|Version
name|version
init|=
name|Version
operator|.
name|fromId
argument_list|(
name|streamIn
operator|.
name|readInt
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|compressed
argument_list|,
name|TransportStatus
operator|.
name|isCompress
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
name|called
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|compressed
condition|)
block|{
specifier|final
name|int
name|bytesConsumed
init|=
name|TcpHeader
operator|.
name|HEADER_SIZE
decl_stmt|;
name|streamIn
operator|=
name|CompressorFactory
operator|.
name|compressor
argument_list|(
name|reference
operator|.
name|slice
argument_list|(
name|bytesConsumed
argument_list|,
name|reference
operator|.
name|length
argument_list|()
operator|-
name|bytesConsumed
argument_list|)
argument_list|)
operator|.
name|streamInput
argument_list|(
name|streamIn
argument_list|)
expr_stmt|;
block|}
name|threadPool
operator|.
name|getThreadContext
argument_list|()
operator|.
name|readHeaders
argument_list|(
name|streamIn
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foobar"
argument_list|,
name|streamIn
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|Req
name|readReq
init|=
operator|new
name|Req
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|readReq
operator|.
name|readFrom
argument_list|(
name|streamIn
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|request
operator|.
name|value
argument_list|,
name|readReq
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|exceptionReference
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|NodeChannels
name|connectToChannels
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ConnectionProfile
name|profile
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|NodeChannels
argument_list|(
name|node
argument_list|,
operator|new
name|Object
index|[
name|profile
operator|.
name|getNumConnections
argument_list|()
index|]
argument_list|,
name|profile
argument_list|,
name|c
lambda|->
block|{}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isOpen
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|serverOpen
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeChannels
name|getConnection
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{
return|return
operator|new
name|NodeChannels
argument_list|(
name|node
argument_list|,
operator|new
name|Object
index|[
name|MockTcpTransport
operator|.
name|LIGHT_PROFILE
operator|.
name|getNumConnections
argument_list|()
index|]
argument_list|,
name|MockTcpTransport
operator|.
name|LIGHT_PROFILE
argument_list|,
name|c
lambda|->
block|{}
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|DiscoveryNode
name|node
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"foo"
argument_list|,
name|buildNewFakeTransportAddress
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|Transport
operator|.
name|Connection
name|connection
init|=
name|transport
operator|.
name|getConnection
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|connection
operator|.
name|sendRequest
argument_list|(
literal|42
argument_list|,
literal|"foobar"
argument_list|,
name|request
argument_list|,
name|TransportRequestOptions
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|called
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"IOException while sending message."
argument_list|,
name|exceptionReference
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ThreadPool
operator|.
name|terminate
argument_list|(
name|threadPool
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Req
specifier|private
specifier|static
specifier|final
class|class
name|Req
extends|extends
name|TransportRequest
block|{
DECL|field|value
specifier|public
name|String
name|value
decl_stmt|;
DECL|method|Req
specifier|private
name|Req
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
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
name|value
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
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testConnectionProfileResolve
specifier|public
name|void
name|testConnectionProfileResolve
parameter_list|()
block|{
specifier|final
name|ConnectionProfile
name|defaultProfile
init|=
name|TcpTransport
operator|.
name|buildDefaultConnectionProfile
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|defaultProfile
argument_list|,
name|TcpTransport
operator|.
name|resolveConnectionProfile
argument_list|(
literal|null
argument_list|,
name|defaultProfile
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|ConnectionProfile
operator|.
name|Builder
name|builder
init|=
operator|new
name|ConnectionProfile
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|addConnections
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|,
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|BULK
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addConnections
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|,
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|RECOVERY
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addConnections
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|,
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|REG
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addConnections
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|,
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|STATE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addConnections
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|,
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|PING
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|connectionTimeoutSet
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionTimeoutSet
condition|)
block|{
name|builder
operator|.
name|setConnectTimeout
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|randomNonNegativeLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|boolean
name|connectionHandshakeSet
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionHandshakeSet
condition|)
block|{
name|builder
operator|.
name|setHandshakeTimeout
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|randomNonNegativeLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ConnectionProfile
name|profile
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|ConnectionProfile
name|resolved
init|=
name|TcpTransport
operator|.
name|resolveConnectionProfile
argument_list|(
name|profile
argument_list|,
name|defaultProfile
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
name|resolved
argument_list|,
name|defaultProfile
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolved
operator|.
name|getNumConnections
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|profile
operator|.
name|getNumConnections
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolved
operator|.
name|getHandles
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|profile
operator|.
name|getHandles
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolved
operator|.
name|getConnectTimeout
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|connectionTimeoutSet
condition|?
name|profile
operator|.
name|getConnectTimeout
argument_list|()
else|:
name|defaultProfile
operator|.
name|getConnectTimeout
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resolved
operator|.
name|getHandshakeTimeout
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|connectionHandshakeSet
condition|?
name|profile
operator|.
name|getHandshakeTimeout
argument_list|()
else|:
name|defaultProfile
operator|.
name|getHandshakeTimeout
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultConnectionProfile
specifier|public
name|void
name|testDefaultConnectionProfile
parameter_list|()
block|{
name|ConnectionProfile
name|profile
init|=
name|TcpTransport
operator|.
name|buildDefaultConnectionProfile
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|13
argument_list|,
name|profile
operator|.
name|getNumConnections
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|PING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|REG
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|STATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|RECOVERY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|BULK
argument_list|)
argument_list|)
expr_stmt|;
name|profile
operator|=
name|TcpTransport
operator|.
name|buildDefaultConnectionProfile
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.master"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|profile
operator|.
name|getNumConnections
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|PING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|REG
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|STATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|RECOVERY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|BULK
argument_list|)
argument_list|)
expr_stmt|;
name|profile
operator|=
name|TcpTransport
operator|.
name|buildDefaultConnectionProfile
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.data"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|profile
operator|.
name|getNumConnections
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|PING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|REG
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|STATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|RECOVERY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|BULK
argument_list|)
argument_list|)
expr_stmt|;
name|profile
operator|=
name|TcpTransport
operator|.
name|buildDefaultConnectionProfile
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.data"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.master"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|profile
operator|.
name|getNumConnections
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|PING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|REG
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|STATE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|RECOVERY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|profile
operator|.
name|getNumConnectionsPerType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|BULK
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

