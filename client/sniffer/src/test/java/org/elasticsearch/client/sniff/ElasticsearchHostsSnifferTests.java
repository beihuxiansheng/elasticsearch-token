begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.sniff
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|sniff
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomInts
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomPicks
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomStrings
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonGenerator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|net
operator|.
name|httpserver
operator|.
name|HttpExchange
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|net
operator|.
name|httpserver
operator|.
name|HttpHandler
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|net
operator|.
name|httpserver
operator|.
name|HttpServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|Consts
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpHost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpGet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|mojo
operator|.
name|animal_sniffer
operator|.
name|IgnoreJRERequirement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|ResponseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|RestClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|RestClientTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
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
name|ArrayList
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|containsString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|//animal-sniffer doesn't like our usage of com.sun.net.httpserver.* classes
end_comment

begin_class
annotation|@
name|IgnoreJRERequirement
DECL|class|ElasticsearchHostsSnifferTests
specifier|public
class|class
name|ElasticsearchHostsSnifferTests
extends|extends
name|RestClientTestCase
block|{
DECL|field|sniffRequestTimeout
specifier|private
name|int
name|sniffRequestTimeout
decl_stmt|;
DECL|field|scheme
specifier|private
name|ElasticsearchHostsSniffer
operator|.
name|Scheme
name|scheme
decl_stmt|;
DECL|field|sniffResponse
specifier|private
name|SniffResponse
name|sniffResponse
decl_stmt|;
DECL|field|httpServer
specifier|private
name|HttpServer
name|httpServer
decl_stmt|;
annotation|@
name|Before
DECL|method|startHttpServer
specifier|public
name|void
name|startHttpServer
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|sniffRequestTimeout
operator|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|getRandom
argument_list|()
argument_list|,
literal|1000
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheme
operator|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|ElasticsearchHostsSniffer
operator|.
name|Scheme
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|this
operator|.
name|sniffResponse
operator|=
name|SniffResponse
operator|.
name|buildFailure
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|sniffResponse
operator|=
name|buildSniffResponse
argument_list|(
name|scheme
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|httpServer
operator|=
name|createHttpServer
argument_list|(
name|sniffResponse
argument_list|,
name|sniffRequestTimeout
argument_list|)
expr_stmt|;
name|this
operator|.
name|httpServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|stopHttpServer
specifier|public
name|void
name|stopHttpServer
parameter_list|()
throws|throws
name|IOException
block|{
name|httpServer
operator|.
name|stop
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testConstructorValidation
specifier|public
name|void
name|testConstructorValidation
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
operator|new
name|ElasticsearchHostsSniffer
argument_list|(
literal|null
argument_list|,
literal|1
argument_list|,
name|ElasticsearchHostsSniffer
operator|.
name|Scheme
operator|.
name|HTTP
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"restClient cannot be null"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|HttpHost
name|httpHost
init|=
operator|new
name|HttpHost
argument_list|(
name|httpServer
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostString
argument_list|()
argument_list|,
name|httpServer
operator|.
name|getAddress
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|RestClient
name|restClient
init|=
name|RestClient
operator|.
name|builder
argument_list|(
name|httpHost
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
try|try
block|{
operator|new
name|ElasticsearchHostsSniffer
argument_list|(
name|restClient
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"scheme cannot be null"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
operator|new
name|ElasticsearchHostsSniffer
argument_list|(
name|restClient
argument_list|,
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
literal|0
argument_list|)
argument_list|,
name|ElasticsearchHostsSniffer
operator|.
name|Scheme
operator|.
name|HTTP
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"sniffRequestTimeoutMillis must be greater than 0"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testSniffNodes
specifier|public
name|void
name|testSniffNodes
parameter_list|()
throws|throws
name|IOException
block|{
name|HttpHost
name|httpHost
init|=
operator|new
name|HttpHost
argument_list|(
name|httpServer
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostString
argument_list|()
argument_list|,
name|httpServer
operator|.
name|getAddress
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|RestClient
name|restClient
init|=
name|RestClient
operator|.
name|builder
argument_list|(
name|httpHost
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
name|ElasticsearchHostsSniffer
name|sniffer
init|=
operator|new
name|ElasticsearchHostsSniffer
argument_list|(
name|restClient
argument_list|,
name|sniffRequestTimeout
argument_list|,
name|scheme
argument_list|)
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|HttpHost
argument_list|>
name|sniffedHosts
init|=
name|sniffer
operator|.
name|sniffHosts
argument_list|()
decl_stmt|;
if|if
condition|(
name|sniffResponse
operator|.
name|isFailure
condition|)
block|{
name|fail
argument_list|(
literal|"sniffNodes should have failed"
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|sniffedHosts
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|sniffResponse
operator|.
name|hosts
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|HttpHost
argument_list|>
name|responseHostsIterator
init|=
name|sniffResponse
operator|.
name|hosts
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|HttpHost
name|sniffedHost
range|:
name|sniffedHosts
control|)
block|{
name|assertEquals
argument_list|(
name|sniffedHost
argument_list|,
name|responseHostsIterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ResponseException
name|e
parameter_list|)
block|{
name|Response
name|response
init|=
name|e
operator|.
name|getResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|sniffResponse
operator|.
name|isFailure
condition|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"GET "
operator|+
name|httpHost
operator|+
literal|"/_nodes/http?timeout="
operator|+
name|sniffRequestTimeout
operator|+
literal|"ms"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|sniffResponse
operator|.
name|nodesInfoResponseCode
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHost
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|httpHost
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|sniffResponse
operator|.
name|nodesInfoResponseCode
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getRequestLine
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"GET /_nodes/http?timeout="
operator|+
name|sniffRequestTimeout
operator|+
literal|"ms HTTP/1.1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"sniffNodes should have succeeded: "
operator|+
name|response
operator|.
name|getStatusLine
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|createHttpServer
specifier|private
specifier|static
name|HttpServer
name|createHttpServer
parameter_list|(
specifier|final
name|SniffResponse
name|sniffResponse
parameter_list|,
specifier|final
name|int
name|sniffTimeoutMillis
parameter_list|)
throws|throws
name|IOException
block|{
name|HttpServer
name|httpServer
init|=
name|HttpServer
operator|.
name|create
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|InetAddress
operator|.
name|getLoopbackAddress
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|httpServer
operator|.
name|createContext
argument_list|(
literal|"/_nodes/http"
argument_list|,
operator|new
name|ResponseHandler
argument_list|(
name|sniffTimeoutMillis
argument_list|,
name|sniffResponse
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|httpServer
return|;
block|}
comment|//animal-sniffer doesn't like our usage of com.sun.net.httpserver.* classes
annotation|@
name|IgnoreJRERequirement
DECL|class|ResponseHandler
specifier|private
specifier|static
class|class
name|ResponseHandler
implements|implements
name|HttpHandler
block|{
DECL|field|sniffTimeoutMillis
specifier|private
specifier|final
name|int
name|sniffTimeoutMillis
decl_stmt|;
DECL|field|sniffResponse
specifier|private
specifier|final
name|SniffResponse
name|sniffResponse
decl_stmt|;
DECL|method|ResponseHandler
name|ResponseHandler
parameter_list|(
name|int
name|sniffTimeoutMillis
parameter_list|,
name|SniffResponse
name|sniffResponse
parameter_list|)
block|{
name|this
operator|.
name|sniffTimeoutMillis
operator|=
name|sniffTimeoutMillis
expr_stmt|;
name|this
operator|.
name|sniffResponse
operator|=
name|sniffResponse
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle
specifier|public
name|void
name|handle
parameter_list|(
name|HttpExchange
name|httpExchange
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|httpExchange
operator|.
name|getRequestMethod
argument_list|()
operator|.
name|equals
argument_list|(
name|HttpGet
operator|.
name|METHOD_NAME
argument_list|)
condition|)
block|{
if|if
condition|(
name|httpExchange
operator|.
name|getRequestURI
argument_list|()
operator|.
name|getRawQuery
argument_list|()
operator|.
name|equals
argument_list|(
literal|"timeout="
operator|+
name|sniffTimeoutMillis
operator|+
literal|"ms"
argument_list|)
condition|)
block|{
name|String
name|nodesInfoBody
init|=
name|sniffResponse
operator|.
name|nodesInfoBody
decl_stmt|;
name|httpExchange
operator|.
name|sendResponseHeaders
argument_list|(
name|sniffResponse
operator|.
name|nodesInfoResponseCode
argument_list|,
name|nodesInfoBody
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|OutputStream
name|out
init|=
name|httpExchange
operator|.
name|getResponseBody
argument_list|()
init|)
block|{
name|out
operator|.
name|write
argument_list|(
name|nodesInfoBody
operator|.
name|getBytes
argument_list|(
name|Consts
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
name|httpExchange
operator|.
name|sendResponseHeaders
argument_list|(
literal|404
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|httpExchange
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|buildSniffResponse
specifier|private
specifier|static
name|SniffResponse
name|buildSniffResponse
parameter_list|(
name|ElasticsearchHostsSniffer
operator|.
name|Scheme
name|scheme
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numNodes
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|getRandom
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|HttpHost
argument_list|>
name|hosts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numNodes
argument_list|)
decl_stmt|;
name|JsonFactory
name|jsonFactory
init|=
operator|new
name|JsonFactory
argument_list|()
decl_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|JsonGenerator
name|generator
init|=
name|jsonFactory
operator|.
name|createGenerator
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|getRandom
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|generator
operator|.
name|writeStringField
argument_list|(
literal|"cluster_name"
argument_list|,
literal|"elasticsearch"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getRandom
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|generator
operator|.
name|writeObjectFieldStart
argument_list|(
literal|"bogus_object"
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
name|generator
operator|.
name|writeObjectFieldStart
argument_list|(
literal|"nodes"
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
name|numNodes
condition|;
name|i
operator|++
control|)
block|{
name|String
name|nodeId
init|=
name|RandomStrings
operator|.
name|randomAsciiOfLengthBetween
argument_list|(
name|getRandom
argument_list|()
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|generator
operator|.
name|writeObjectFieldStart
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
if|if
condition|(
name|getRandom
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|generator
operator|.
name|writeObjectFieldStart
argument_list|(
literal|"bogus_object"
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|getRandom
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|generator
operator|.
name|writeArrayFieldStart
argument_list|(
literal|"bogus_array"
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
block|}
name|boolean
name|isHttpEnabled
init|=
name|rarely
argument_list|()
operator|==
literal|false
decl_stmt|;
if|if
condition|(
name|isHttpEnabled
condition|)
block|{
name|String
name|host
init|=
literal|"host"
operator|+
name|i
decl_stmt|;
name|int
name|port
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|getRandom
argument_list|()
argument_list|,
literal|9200
argument_list|,
literal|9299
argument_list|)
decl_stmt|;
name|HttpHost
name|httpHost
init|=
operator|new
name|HttpHost
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|scheme
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|hosts
operator|.
name|add
argument_list|(
name|httpHost
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeObjectFieldStart
argument_list|(
literal|"http"
argument_list|)
expr_stmt|;
if|if
condition|(
name|getRandom
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|generator
operator|.
name|writeArrayFieldStart
argument_list|(
literal|"bound_address"
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeString
argument_list|(
literal|"[fe80::1]:"
operator|+
name|port
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeString
argument_list|(
literal|"[::1]:"
operator|+
name|port
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeString
argument_list|(
literal|"127.0.0.1:"
operator|+
name|port
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|getRandom
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|generator
operator|.
name|writeObjectFieldStart
argument_list|(
literal|"bogus_object"
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
name|generator
operator|.
name|writeStringField
argument_list|(
literal|"publish_address"
argument_list|,
name|httpHost
operator|.
name|toHostString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|getRandom
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|generator
operator|.
name|writeNumberField
argument_list|(
literal|"max_content_length_in_bytes"
argument_list|,
literal|104857600
argument_list|)
expr_stmt|;
block|}
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|getRandom
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|String
index|[]
name|roles
init|=
block|{
literal|"master"
block|,
literal|"data"
block|,
literal|"ingest"
block|}
decl_stmt|;
name|int
name|numRoles
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|getRandom
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nodeRoles
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|numRoles
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numRoles
condition|;
name|j
operator|++
control|)
block|{
name|String
name|role
decl_stmt|;
do|do
block|{
name|role
operator|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|getRandom
argument_list|()
argument_list|,
name|roles
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|nodeRoles
operator|.
name|add
argument_list|(
name|role
argument_list|)
operator|==
literal|false
condition|)
do|;
block|}
name|generator
operator|.
name|writeArrayFieldStart
argument_list|(
literal|"roles"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|nodeRole
range|:
name|nodeRoles
control|)
block|{
name|generator
operator|.
name|writeString
argument_list|(
name|nodeRole
argument_list|)
expr_stmt|;
block|}
name|generator
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
block|}
name|int
name|numAttributes
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|getRandom
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|numAttributes
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numAttributes
condition|;
name|j
operator|++
control|)
block|{
name|attributes
operator|.
name|put
argument_list|(
literal|"attr"
operator|+
name|j
argument_list|,
literal|"value"
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numAttributes
operator|>
literal|0
condition|)
block|{
name|generator
operator|.
name|writeObjectFieldStart
argument_list|(
literal|"attributes"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|attributes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|generator
operator|.
name|writeStringField
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numAttributes
operator|>
literal|0
condition|)
block|{
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|SniffResponse
operator|.
name|buildResponse
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|,
name|hosts
argument_list|)
return|;
block|}
DECL|class|SniffResponse
specifier|private
specifier|static
class|class
name|SniffResponse
block|{
DECL|field|nodesInfoBody
specifier|private
specifier|final
name|String
name|nodesInfoBody
decl_stmt|;
DECL|field|nodesInfoResponseCode
specifier|private
specifier|final
name|int
name|nodesInfoResponseCode
decl_stmt|;
DECL|field|hosts
specifier|private
specifier|final
name|List
argument_list|<
name|HttpHost
argument_list|>
name|hosts
decl_stmt|;
DECL|field|isFailure
specifier|private
specifier|final
name|boolean
name|isFailure
decl_stmt|;
DECL|method|SniffResponse
name|SniffResponse
parameter_list|(
name|String
name|nodesInfoBody
parameter_list|,
name|List
argument_list|<
name|HttpHost
argument_list|>
name|hosts
parameter_list|,
name|boolean
name|isFailure
parameter_list|)
block|{
name|this
operator|.
name|nodesInfoBody
operator|=
name|nodesInfoBody
expr_stmt|;
name|this
operator|.
name|hosts
operator|=
name|hosts
expr_stmt|;
name|this
operator|.
name|isFailure
operator|=
name|isFailure
expr_stmt|;
if|if
condition|(
name|isFailure
condition|)
block|{
name|this
operator|.
name|nodesInfoResponseCode
operator|=
name|randomErrorResponseCode
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|nodesInfoResponseCode
operator|=
literal|200
expr_stmt|;
block|}
block|}
DECL|method|buildFailure
specifier|static
name|SniffResponse
name|buildFailure
parameter_list|()
block|{
return|return
operator|new
name|SniffResponse
argument_list|(
literal|""
argument_list|,
name|Collections
operator|.
expr|<
name|HttpHost
operator|>
name|emptyList
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|buildResponse
specifier|static
name|SniffResponse
name|buildResponse
parameter_list|(
name|String
name|nodesInfoBody
parameter_list|,
name|List
argument_list|<
name|HttpHost
argument_list|>
name|hosts
parameter_list|)
block|{
return|return
operator|new
name|SniffResponse
argument_list|(
name|nodesInfoBody
argument_list|,
name|hosts
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
DECL|method|randomErrorResponseCode
specifier|private
specifier|static
name|int
name|randomErrorResponseCode
parameter_list|()
block|{
return|return
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|getRandom
argument_list|()
argument_list|,
literal|400
argument_list|,
literal|599
argument_list|)
return|;
block|}
block|}
end_class

end_unit
