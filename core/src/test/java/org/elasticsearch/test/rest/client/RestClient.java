begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|client
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
name|RandomizedTest
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
name|config
operator|.
name|Registry
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
name|config
operator|.
name|RegistryBuilder
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
name|conn
operator|.
name|socket
operator|.
name|ConnectionSocketFactory
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
name|conn
operator|.
name|socket
operator|.
name|PlainConnectionSocketFactory
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
name|conn
operator|.
name|ssl
operator|.
name|SSLConnectionSocketFactory
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
name|conn
operator|.
name|ssl
operator|.
name|SSLContexts
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
name|impl
operator|.
name|client
operator|.
name|CloseableHttpClient
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
name|impl
operator|.
name|client
operator|.
name|HttpClients
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
name|impl
operator|.
name|conn
operator|.
name|PoolingHttpClientConnectionManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import

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
name|client
operator|.
name|support
operator|.
name|Headers
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
name|Strings
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
name|PathUtils
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|network
operator|.
name|NetworkAddress
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
name|util
operator|.
name|set
operator|.
name|Sets
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
name|rest
operator|.
name|client
operator|.
name|http
operator|.
name|HttpRequestBuilder
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
name|rest
operator|.
name|client
operator|.
name|http
operator|.
name|HttpResponse
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
name|rest
operator|.
name|spec
operator|.
name|RestApi
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
name|rest
operator|.
name|spec
operator|.
name|RestSpec
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|InputStream
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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyManagementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyStore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyStoreException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|CertificateException
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
name|HashMap
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

begin_comment
comment|/**  * REST client used to test the elasticsearch REST layer  * Holds the {@link RestSpec} used to translate api calls into REST calls  */
end_comment

begin_class
DECL|class|RestClient
specifier|public
class|class
name|RestClient
implements|implements
name|Closeable
block|{
DECL|field|PROTOCOL
specifier|public
specifier|static
specifier|final
name|String
name|PROTOCOL
init|=
literal|"protocol"
decl_stmt|;
DECL|field|TRUSTSTORE_PATH
specifier|public
specifier|static
specifier|final
name|String
name|TRUSTSTORE_PATH
init|=
literal|"truststore.path"
decl_stmt|;
DECL|field|TRUSTSTORE_PASSWORD
specifier|public
specifier|static
specifier|final
name|String
name|TRUSTSTORE_PASSWORD
init|=
literal|"truststore.password"
decl_stmt|;
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|RestClient
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//query_string params that don't need to be declared in the spec, thay are supported by default
DECL|field|ALWAYS_ACCEPTED_QUERY_STRING_PARAMS
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|ALWAYS_ACCEPTED_QUERY_STRING_PARAMS
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"pretty"
argument_list|,
literal|"source"
argument_list|,
literal|"filter_path"
argument_list|)
decl_stmt|;
DECL|field|protocol
specifier|private
specifier|final
name|String
name|protocol
decl_stmt|;
DECL|field|restSpec
specifier|private
specifier|final
name|RestSpec
name|restSpec
decl_stmt|;
DECL|field|httpClient
specifier|private
specifier|final
name|CloseableHttpClient
name|httpClient
decl_stmt|;
DECL|field|headers
specifier|private
specifier|final
name|Headers
name|headers
decl_stmt|;
DECL|field|addresses
specifier|private
specifier|final
name|InetSocketAddress
index|[]
name|addresses
decl_stmt|;
DECL|field|esVersion
specifier|private
specifier|final
name|Version
name|esVersion
decl_stmt|;
DECL|method|RestClient
specifier|public
name|RestClient
parameter_list|(
name|RestSpec
name|restSpec
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|InetSocketAddress
index|[]
name|addresses
parameter_list|)
throws|throws
name|IOException
throws|,
name|RestException
block|{
assert|assert
name|addresses
operator|.
name|length
operator|>
literal|0
assert|;
name|this
operator|.
name|restSpec
operator|=
name|restSpec
expr_stmt|;
name|this
operator|.
name|headers
operator|=
operator|new
name|Headers
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|protocol
operator|=
name|settings
operator|.
name|get
argument_list|(
name|PROTOCOL
argument_list|,
literal|"http"
argument_list|)
expr_stmt|;
name|this
operator|.
name|httpClient
operator|=
name|createHttpClient
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|addresses
operator|=
name|addresses
expr_stmt|;
name|this
operator|.
name|esVersion
operator|=
name|readAndCheckVersion
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"REST client initialized {}, elasticsearch version: [{}]"
argument_list|,
name|addresses
argument_list|,
name|esVersion
argument_list|)
expr_stmt|;
block|}
DECL|method|readAndCheckVersion
specifier|private
name|Version
name|readAndCheckVersion
parameter_list|()
throws|throws
name|IOException
throws|,
name|RestException
block|{
comment|//we make a manual call here without using callApi method, mainly because we are initializing
comment|//and the randomized context doesn't exist for the current thread (would be used to choose the method otherwise)
name|RestApi
name|restApi
init|=
name|restApi
argument_list|(
literal|"info"
argument_list|)
decl_stmt|;
assert|assert
name|restApi
operator|.
name|getPaths
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
assert|assert
name|restApi
operator|.
name|getMethods
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
name|String
name|version
init|=
literal|null
decl_stmt|;
for|for
control|(
name|InetSocketAddress
name|address
range|:
name|addresses
control|)
block|{
name|RestResponse
name|restResponse
init|=
operator|new
name|RestResponse
argument_list|(
name|httpRequestBuilder
argument_list|(
name|address
argument_list|)
operator|.
name|path
argument_list|(
name|restApi
operator|.
name|getPaths
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|method
argument_list|(
name|restApi
operator|.
name|getMethods
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
argument_list|)
decl_stmt|;
name|checkStatusCode
argument_list|(
name|restResponse
argument_list|)
expr_stmt|;
name|Object
name|latestVersion
init|=
name|restResponse
operator|.
name|evaluate
argument_list|(
literal|"version.number"
argument_list|)
decl_stmt|;
if|if
condition|(
name|latestVersion
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"elasticsearch version not found in the response"
argument_list|)
throw|;
block|}
if|if
condition|(
name|version
operator|==
literal|null
condition|)
block|{
name|version
operator|=
name|latestVersion
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|latestVersion
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"provided nodes addresses run different elasticsearch versions"
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|Version
operator|.
name|fromString
argument_list|(
name|version
argument_list|)
return|;
block|}
DECL|method|getEsVersion
specifier|public
name|Version
name|getEsVersion
parameter_list|()
block|{
return|return
name|esVersion
return|;
block|}
comment|/**      * Calls an api with the provided parameters and body      * @throws RestException if the obtained status code is non ok, unless the specific error code needs to be ignored      * according to the ignore parameter received as input (which won't get sent to elasticsearch)      */
DECL|method|callApi
specifier|public
name|RestResponse
name|callApi
parameter_list|(
name|String
name|apiName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|,
name|String
name|body
parameter_list|)
throws|throws
name|IOException
throws|,
name|RestException
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|ignores
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|requestParams
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
comment|//makes a copy of the parameters before modifying them for this specific request
name|requestParams
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|params
argument_list|)
expr_stmt|;
comment|//ignore is a special parameter supported by the clients, shouldn't be sent to es
name|String
name|ignoreString
init|=
name|requestParams
operator|.
name|remove
argument_list|(
literal|"ignore"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|ignoreString
argument_list|)
condition|)
block|{
try|try
block|{
name|ignores
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|ignoreString
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ignore value should be a number, found ["
operator|+
name|ignoreString
operator|+
literal|"] instead"
argument_list|)
throw|;
block|}
block|}
block|}
name|HttpRequestBuilder
name|httpRequestBuilder
init|=
name|callApiBuilder
argument_list|(
name|apiName
argument_list|,
name|requestParams
argument_list|,
name|body
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"calling api [{}]"
argument_list|,
name|apiName
argument_list|)
expr_stmt|;
name|HttpResponse
name|httpResponse
init|=
name|httpRequestBuilder
operator|.
name|execute
argument_list|()
decl_stmt|;
comment|// http HEAD doesn't support response body
comment|// For the few api (exists class of api) that use it we need to accept 404 too
if|if
condition|(
operator|!
name|httpResponse
operator|.
name|supportsBody
argument_list|()
condition|)
block|{
name|ignores
operator|.
name|add
argument_list|(
literal|404
argument_list|)
expr_stmt|;
block|}
name|RestResponse
name|restResponse
init|=
operator|new
name|RestResponse
argument_list|(
name|httpResponse
argument_list|)
decl_stmt|;
name|checkStatusCode
argument_list|(
name|restResponse
argument_list|,
name|ignores
argument_list|)
expr_stmt|;
return|return
name|restResponse
return|;
block|}
DECL|method|checkStatusCode
specifier|private
name|void
name|checkStatusCode
parameter_list|(
name|RestResponse
name|restResponse
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|ignores
parameter_list|)
throws|throws
name|RestException
block|{
comment|//ignore is a catch within the client, to prevent the client from throwing error if it gets non ok codes back
if|if
condition|(
name|ignores
operator|.
name|contains
argument_list|(
name|restResponse
operator|.
name|getStatusCode
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"ignored non ok status codes {} as requested"
argument_list|,
name|ignores
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|checkStatusCode
argument_list|(
name|restResponse
argument_list|)
expr_stmt|;
block|}
DECL|method|checkStatusCode
specifier|private
name|void
name|checkStatusCode
parameter_list|(
name|RestResponse
name|restResponse
parameter_list|)
throws|throws
name|RestException
block|{
if|if
condition|(
name|restResponse
operator|.
name|isError
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RestException
argument_list|(
literal|"non ok status code ["
operator|+
name|restResponse
operator|.
name|getStatusCode
argument_list|()
operator|+
literal|"] returned"
argument_list|,
name|restResponse
argument_list|)
throw|;
block|}
block|}
DECL|method|callApiBuilder
specifier|private
name|HttpRequestBuilder
name|callApiBuilder
parameter_list|(
name|String
name|apiName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|,
name|String
name|body
parameter_list|)
block|{
comment|//create doesn't exist in the spec but is supported in the clients (index with op_type=create)
name|boolean
name|indexCreateApi
init|=
literal|"create"
operator|.
name|equals
argument_list|(
name|apiName
argument_list|)
decl_stmt|;
name|String
name|api
init|=
name|indexCreateApi
condition|?
literal|"index"
else|:
name|apiName
decl_stmt|;
name|RestApi
name|restApi
init|=
name|restApi
argument_list|(
name|api
argument_list|)
decl_stmt|;
name|HttpRequestBuilder
name|httpRequestBuilder
init|=
name|httpRequestBuilder
argument_list|()
decl_stmt|;
comment|//divide params between ones that go within query string and ones that go within path
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|pathParts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
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
name|String
argument_list|>
name|entry
range|:
name|params
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|restApi
operator|.
name|getPathParts
argument_list|()
operator|.
name|contains
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|pathParts
operator|.
name|put
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
else|else
block|{
if|if
condition|(
name|restApi
operator|.
name|getParams
argument_list|()
operator|.
name|contains
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|||
name|ALWAYS_ACCEPTED_QUERY_STRING_PARAMS
operator|.
name|contains
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|httpRequestBuilder
operator|.
name|addParam
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
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"param ["
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"] not supported in ["
operator|+
name|restApi
operator|.
name|getName
argument_list|()
operator|+
literal|"] api"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|indexCreateApi
condition|)
block|{
name|httpRequestBuilder
operator|.
name|addParam
argument_list|(
literal|"op_type"
argument_list|,
literal|"create"
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|supportedMethods
init|=
name|restApi
operator|.
name|getSupportedMethods
argument_list|(
name|pathParts
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|body
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|restApi
operator|.
name|isBodySupported
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"body is not supported by ["
operator|+
name|restApi
operator|.
name|getName
argument_list|()
operator|+
literal|"] api"
argument_list|)
throw|;
block|}
comment|//test the GET with source param instead of GET/POST with body
if|if
condition|(
name|supportedMethods
operator|.
name|contains
argument_list|(
literal|"GET"
argument_list|)
operator|&&
name|RandomizedTest
operator|.
name|rarely
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"sending the request body as source param with GET method"
argument_list|)
expr_stmt|;
name|httpRequestBuilder
operator|.
name|addParam
argument_list|(
literal|"source"
argument_list|,
name|body
argument_list|)
operator|.
name|method
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|httpRequestBuilder
operator|.
name|body
argument_list|(
name|body
argument_list|)
operator|.
name|method
argument_list|(
name|RandomizedTest
operator|.
name|randomFrom
argument_list|(
name|supportedMethods
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|restApi
operator|.
name|isBodyRequired
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"body is required by ["
operator|+
name|restApi
operator|.
name|getName
argument_list|()
operator|+
literal|"] api"
argument_list|)
throw|;
block|}
name|httpRequestBuilder
operator|.
name|method
argument_list|(
name|RandomizedTest
operator|.
name|randomFrom
argument_list|(
name|supportedMethods
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//the http method is randomized (out of the available ones with the chosen api)
return|return
name|httpRequestBuilder
operator|.
name|path
argument_list|(
name|RandomizedTest
operator|.
name|randomFrom
argument_list|(
name|restApi
operator|.
name|getFinalPaths
argument_list|(
name|pathParts
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|restApi
specifier|private
name|RestApi
name|restApi
parameter_list|(
name|String
name|apiName
parameter_list|)
block|{
name|RestApi
name|restApi
init|=
name|restSpec
operator|.
name|getApi
argument_list|(
name|apiName
argument_list|)
decl_stmt|;
if|if
condition|(
name|restApi
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"rest api ["
operator|+
name|apiName
operator|+
literal|"] doesn't exist in the rest spec"
argument_list|)
throw|;
block|}
return|return
name|restApi
return|;
block|}
DECL|method|httpRequestBuilder
specifier|protected
name|HttpRequestBuilder
name|httpRequestBuilder
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|)
block|{
return|return
operator|new
name|HttpRequestBuilder
argument_list|(
name|httpClient
argument_list|)
operator|.
name|addHeaders
argument_list|(
name|headers
argument_list|)
operator|.
name|protocol
argument_list|(
name|protocol
argument_list|)
operator|.
name|host
argument_list|(
name|NetworkAddress
operator|.
name|formatAddress
argument_list|(
name|address
operator|.
name|getAddress
argument_list|()
argument_list|)
argument_list|)
operator|.
name|port
argument_list|(
name|address
operator|.
name|getPort
argument_list|()
argument_list|)
return|;
block|}
DECL|method|httpRequestBuilder
specifier|protected
name|HttpRequestBuilder
name|httpRequestBuilder
parameter_list|()
block|{
comment|//the address used is randomized between the available ones
name|InetSocketAddress
name|address
init|=
name|RandomizedTest
operator|.
name|randomFrom
argument_list|(
name|addresses
argument_list|)
decl_stmt|;
return|return
name|httpRequestBuilder
argument_list|(
name|address
argument_list|)
return|;
block|}
DECL|method|createHttpClient
specifier|protected
name|CloseableHttpClient
name|createHttpClient
parameter_list|(
name|Settings
name|settings
parameter_list|)
throws|throws
name|IOException
block|{
name|SSLConnectionSocketFactory
name|sslsf
decl_stmt|;
name|String
name|keystorePath
init|=
name|settings
operator|.
name|get
argument_list|(
name|TRUSTSTORE_PATH
argument_list|)
decl_stmt|;
if|if
condition|(
name|keystorePath
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|keystorePass
init|=
name|settings
operator|.
name|get
argument_list|(
name|TRUSTSTORE_PASSWORD
argument_list|)
decl_stmt|;
if|if
condition|(
name|keystorePass
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|TRUSTSTORE_PATH
operator|+
literal|" is provided but not "
operator|+
name|TRUSTSTORE_PASSWORD
argument_list|)
throw|;
block|}
name|Path
name|path
init|=
name|PathUtils
operator|.
name|get
argument_list|(
name|keystorePath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|TRUSTSTORE_PATH
operator|+
literal|" is set but points to a non-existing file"
argument_list|)
throw|;
block|}
try|try
block|{
name|KeyStore
name|keyStore
init|=
name|KeyStore
operator|.
name|getInstance
argument_list|(
literal|"jks"
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|path
argument_list|)
init|)
block|{
name|keyStore
operator|.
name|load
argument_list|(
name|is
argument_list|,
name|keystorePass
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|SSLContext
name|sslcontext
init|=
name|SSLContexts
operator|.
name|custom
argument_list|()
operator|.
name|loadTrustMaterial
argument_list|(
name|keyStore
argument_list|,
literal|null
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|sslsf
operator|=
operator|new
name|SSLConnectionSocketFactory
argument_list|(
name|sslcontext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeyStoreException
decl||
name|NoSuchAlgorithmException
decl||
name|KeyManagementException
decl||
name|CertificateException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|sslsf
operator|=
name|SSLConnectionSocketFactory
operator|.
name|getSocketFactory
argument_list|()
expr_stmt|;
block|}
name|Registry
argument_list|<
name|ConnectionSocketFactory
argument_list|>
name|socketFactoryRegistry
init|=
name|RegistryBuilder
operator|.
expr|<
name|ConnectionSocketFactory
operator|>
name|create
argument_list|()
operator|.
name|register
argument_list|(
literal|"http"
argument_list|,
name|PlainConnectionSocketFactory
operator|.
name|getSocketFactory
argument_list|()
argument_list|)
operator|.
name|register
argument_list|(
literal|"https"
argument_list|,
name|sslsf
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|HttpClients
operator|.
name|createMinimal
argument_list|(
operator|new
name|PoolingHttpClientConnectionManager
argument_list|(
name|socketFactoryRegistry
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|15
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Closes the REST client and the underlying http client      */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|httpClient
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

