begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.http
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|http
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|service
operator|.
name|ClusterService
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
name|breaker
operator|.
name|CircuitBreaker
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
name|BytesArray
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
name|component
operator|.
name|AbstractLifecycleComponent
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
name|ClusterSettings
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
name|transport
operator|.
name|LocalTransportAddress
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|breaker
operator|.
name|CircuitBreakerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|breaker
operator|.
name|HierarchyCircuitBreakerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|service
operator|.
name|NodeService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|AbstractRestChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|BytesRestResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestController
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
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
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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

begin_class
DECL|class|HttpServerTests
specifier|public
class|class
name|HttpServerTests
extends|extends
name|ESTestCase
block|{
DECL|field|BREAKER_LIMIT
specifier|private
specifier|static
specifier|final
name|ByteSizeValue
name|BREAKER_LIMIT
init|=
operator|new
name|ByteSizeValue
argument_list|(
literal|20
argument_list|)
decl_stmt|;
DECL|field|httpServer
specifier|private
name|HttpServer
name|httpServer
decl_stmt|;
DECL|field|inFlightRequestsBreaker
specifier|private
name|CircuitBreaker
name|inFlightRequestsBreaker
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|EMPTY
decl_stmt|;
name|CircuitBreakerService
name|circuitBreakerService
init|=
operator|new
name|HierarchyCircuitBreakerService
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|HierarchyCircuitBreakerService
operator|.
name|IN_FLIGHT_REQUESTS_CIRCUIT_BREAKER_LIMIT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|BREAKER_LIMIT
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
operator|new
name|ClusterSettings
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|ClusterSettings
operator|.
name|BUILT_IN_CLUSTER_SETTINGS
argument_list|)
argument_list|)
decl_stmt|;
comment|// we can do this here only because we know that we don't adjust breaker settings dynamically in the test
name|inFlightRequestsBreaker
operator|=
name|circuitBreakerService
operator|.
name|getBreaker
argument_list|(
name|CircuitBreaker
operator|.
name|IN_FLIGHT_REQUESTS
argument_list|)
expr_stmt|;
name|HttpServerTransport
name|httpServerTransport
init|=
operator|new
name|TestHttpServerTransport
argument_list|()
decl_stmt|;
name|RestController
name|restController
init|=
operator|new
name|RestController
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|restController
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|GET
argument_list|,
literal|"/"
argument_list|,
parameter_list|(
name|request
parameter_list|,
name|channel
parameter_list|,
name|client
parameter_list|)
lambda|->
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|BytesRestResponse
argument_list|(
name|RestStatus
operator|.
name|OK
argument_list|,
name|BytesRestResponse
operator|.
name|TEXT_CONTENT_TYPE
argument_list|,
name|BytesArray
operator|.
name|EMPTY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|restController
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|GET
argument_list|,
literal|"/error"
argument_list|,
parameter_list|(
name|request
parameter_list|,
name|channel
parameter_list|,
name|client
parameter_list|)
lambda|->
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"test error"
argument_list|)
throw|;
block|}
argument_list|)
expr_stmt|;
name|ClusterService
name|clusterService
init|=
operator|new
name|ClusterService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|ClusterSettings
argument_list|(
name|settings
argument_list|,
name|ClusterSettings
operator|.
name|BUILT_IN_CLUSTER_SETTINGS
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|NodeService
name|nodeService
init|=
operator|new
name|NodeService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|clusterService
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|httpServer
operator|=
operator|new
name|HttpServer
argument_list|(
name|settings
argument_list|,
name|httpServerTransport
argument_list|,
name|restController
argument_list|,
name|nodeService
argument_list|,
literal|null
argument_list|,
name|circuitBreakerService
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|testDispatchRequestAddsAndFreesBytesOnSuccess
specifier|public
name|void
name|testDispatchRequestAddsAndFreesBytesOnSuccess
parameter_list|()
block|{
name|int
name|contentLength
init|=
name|BREAKER_LIMIT
operator|.
name|bytesAsInt
argument_list|()
decl_stmt|;
name|String
name|content
init|=
name|randomAsciiOfLength
argument_list|(
name|contentLength
argument_list|)
decl_stmt|;
name|TestRestRequest
name|request
init|=
operator|new
name|TestRestRequest
argument_list|(
literal|"/"
argument_list|,
name|content
argument_list|)
decl_stmt|;
name|AssertingChannel
name|channel
init|=
operator|new
name|AssertingChannel
argument_list|(
name|request
argument_list|,
literal|true
argument_list|,
name|RestStatus
operator|.
name|OK
argument_list|)
decl_stmt|;
name|httpServer
operator|.
name|dispatchRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
operator|new
name|ThreadContext
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|inFlightRequestsBreaker
operator|.
name|getTrippedCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|inFlightRequestsBreaker
operator|.
name|getUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDispatchRequestAddsAndFreesBytesOnError
specifier|public
name|void
name|testDispatchRequestAddsAndFreesBytesOnError
parameter_list|()
block|{
name|int
name|contentLength
init|=
name|BREAKER_LIMIT
operator|.
name|bytesAsInt
argument_list|()
decl_stmt|;
name|String
name|content
init|=
name|randomAsciiOfLength
argument_list|(
name|contentLength
argument_list|)
decl_stmt|;
name|TestRestRequest
name|request
init|=
operator|new
name|TestRestRequest
argument_list|(
literal|"/error"
argument_list|,
name|content
argument_list|)
decl_stmt|;
name|AssertingChannel
name|channel
init|=
operator|new
name|AssertingChannel
argument_list|(
name|request
argument_list|,
literal|true
argument_list|,
name|RestStatus
operator|.
name|BAD_REQUEST
argument_list|)
decl_stmt|;
name|httpServer
operator|.
name|dispatchRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
operator|new
name|ThreadContext
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|inFlightRequestsBreaker
operator|.
name|getTrippedCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|inFlightRequestsBreaker
operator|.
name|getUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDispatchRequestAddsAndFreesBytesOnlyOnceOnError
specifier|public
name|void
name|testDispatchRequestAddsAndFreesBytesOnlyOnceOnError
parameter_list|()
block|{
name|int
name|contentLength
init|=
name|BREAKER_LIMIT
operator|.
name|bytesAsInt
argument_list|()
decl_stmt|;
name|String
name|content
init|=
name|randomAsciiOfLength
argument_list|(
name|contentLength
argument_list|)
decl_stmt|;
comment|// we will produce an error in the rest handler and one more when sending the error response
name|TestRestRequest
name|request
init|=
operator|new
name|TestRestRequest
argument_list|(
literal|"/error"
argument_list|,
name|content
argument_list|)
decl_stmt|;
name|ExceptionThrowingChannel
name|channel
init|=
operator|new
name|ExceptionThrowingChannel
argument_list|(
name|request
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|httpServer
operator|.
name|dispatchRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
operator|new
name|ThreadContext
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|inFlightRequestsBreaker
operator|.
name|getTrippedCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|inFlightRequestsBreaker
operator|.
name|getUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDispatchRequestLimitsBytes
specifier|public
name|void
name|testDispatchRequestLimitsBytes
parameter_list|()
block|{
name|int
name|contentLength
init|=
name|BREAKER_LIMIT
operator|.
name|bytesAsInt
argument_list|()
operator|+
literal|1
decl_stmt|;
name|String
name|content
init|=
name|randomAsciiOfLength
argument_list|(
name|contentLength
argument_list|)
decl_stmt|;
name|TestRestRequest
name|request
init|=
operator|new
name|TestRestRequest
argument_list|(
literal|"/"
argument_list|,
name|content
argument_list|)
decl_stmt|;
name|AssertingChannel
name|channel
init|=
operator|new
name|AssertingChannel
argument_list|(
name|request
argument_list|,
literal|true
argument_list|,
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|)
decl_stmt|;
name|httpServer
operator|.
name|dispatchRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
operator|new
name|ThreadContext
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|inFlightRequestsBreaker
operator|.
name|getTrippedCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|inFlightRequestsBreaker
operator|.
name|getUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|TestHttpServerTransport
specifier|private
specifier|static
specifier|final
class|class
name|TestHttpServerTransport
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|HttpServerTransport
argument_list|>
implements|implements
name|HttpServerTransport
block|{
DECL|method|TestHttpServerTransport
specifier|public
name|TestHttpServerTransport
parameter_list|()
block|{
name|super
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
block|{         }
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
block|{         }
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
block|{         }
annotation|@
name|Override
DECL|method|boundAddress
specifier|public
name|BoundTransportAddress
name|boundAddress
parameter_list|()
block|{
name|LocalTransportAddress
name|transportAddress
init|=
operator|new
name|LocalTransportAddress
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
return|return
operator|new
name|BoundTransportAddress
argument_list|(
operator|new
name|TransportAddress
index|[]
block|{
name|transportAddress
block|}
argument_list|,
name|transportAddress
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|info
specifier|public
name|HttpInfo
name|info
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|stats
specifier|public
name|HttpStats
name|stats
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|httpServerAdapter
specifier|public
name|void
name|httpServerAdapter
parameter_list|(
name|HttpServerAdapter
name|httpServerAdapter
parameter_list|)
block|{          }
block|}
DECL|class|AssertingChannel
specifier|private
specifier|static
specifier|final
class|class
name|AssertingChannel
extends|extends
name|AbstractRestChannel
block|{
DECL|field|expectedStatus
specifier|private
specifier|final
name|RestStatus
name|expectedStatus
decl_stmt|;
DECL|method|AssertingChannel
specifier|protected
name|AssertingChannel
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|boolean
name|detailedErrorsEnabled
parameter_list|,
name|RestStatus
name|expectedStatus
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|,
name|detailedErrorsEnabled
argument_list|)
expr_stmt|;
name|this
operator|.
name|expectedStatus
operator|=
name|expectedStatus
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendResponse
specifier|public
name|void
name|sendResponse
parameter_list|(
name|RestResponse
name|response
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedStatus
argument_list|,
name|response
operator|.
name|status
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ExceptionThrowingChannel
specifier|private
specifier|static
specifier|final
class|class
name|ExceptionThrowingChannel
extends|extends
name|AbstractRestChannel
block|{
DECL|method|ExceptionThrowingChannel
specifier|protected
name|ExceptionThrowingChannel
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|boolean
name|detailedErrorsEnabled
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|,
name|detailedErrorsEnabled
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendResponse
specifier|public
name|void
name|sendResponse
parameter_list|(
name|RestResponse
name|response
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"always throwing an exception for testing"
argument_list|)
throw|;
block|}
block|}
DECL|class|TestRestRequest
specifier|private
specifier|static
specifier|final
class|class
name|TestRestRequest
extends|extends
name|RestRequest
block|{
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|content
specifier|private
specifier|final
name|BytesReference
name|content
decl_stmt|;
DECL|method|TestRestRequest
specifier|private
name|TestRestRequest
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|content
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|content
operator|=
operator|new
name|BytesArray
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|method
specifier|public
name|Method
name|method
parameter_list|()
block|{
return|return
name|Method
operator|.
name|GET
return|;
block|}
annotation|@
name|Override
DECL|method|uri
specifier|public
name|String
name|uri
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|rawPath
specifier|public
name|String
name|rawPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
annotation|@
name|Override
DECL|method|hasContent
specifier|public
name|boolean
name|hasContent
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|content
specifier|public
name|BytesReference
name|content
parameter_list|()
block|{
return|return
name|content
return|;
block|}
annotation|@
name|Override
DECL|method|header
specifier|public
name|String
name|header
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|headers
specifier|public
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|headers
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hasParam
specifier|public
name|boolean
name|hasParam
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|param
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|param
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|params
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

