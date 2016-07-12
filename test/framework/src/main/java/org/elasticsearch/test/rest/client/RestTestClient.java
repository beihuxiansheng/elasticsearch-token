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
name|Header
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
name|HttpEntity
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
name|entity
operator|.
name|StringEntity
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
name|message
operator|.
name|BasicHeader
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
name|nio
operator|.
name|conn
operator|.
name|ssl
operator|.
name|SSLIOSessionStrategy
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Objects
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

begin_comment
comment|/**  * REST client used to test the elasticsearch REST layer.  * Wraps a {@link RestClient} instance used to send the REST requests.  * Holds the {@link RestSpec} used to translate api calls into REST calls  */
end_comment

begin_class
DECL|class|RestTestClient
specifier|public
class|class
name|RestTestClient
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
name|RestTestClient
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
DECL|field|restSpec
specifier|private
specifier|final
name|RestSpec
name|restSpec
decl_stmt|;
DECL|field|restClient
specifier|private
specifier|final
name|RestClient
name|restClient
decl_stmt|;
DECL|field|esVersion
specifier|private
specifier|final
name|Version
name|esVersion
decl_stmt|;
DECL|method|RestTestClient
specifier|public
name|RestTestClient
parameter_list|(
name|RestSpec
name|restSpec
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|URL
index|[]
name|urls
parameter_list|)
throws|throws
name|Exception
block|{
assert|assert
name|urls
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
name|restClient
operator|=
name|createRestClient
argument_list|(
name|urls
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|esVersion
operator|=
name|readAndCheckVersion
argument_list|(
name|urls
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"REST client initialized {}, elasticsearch version: [{}]"
argument_list|,
name|urls
argument_list|,
name|esVersion
argument_list|)
expr_stmt|;
block|}
DECL|method|readAndCheckVersion
specifier|private
name|Version
name|readAndCheckVersion
parameter_list|(
name|URL
index|[]
name|urls
parameter_list|)
throws|throws
name|Exception
block|{
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
name|URL
name|ignored
range|:
name|urls
control|)
block|{
comment|//we don't really use the urls here, we rely on the client doing round-robin to touch all the nodes in the cluster
name|String
name|method
init|=
name|restApi
operator|.
name|getMethods
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|endpoint
init|=
name|restApi
operator|.
name|getPaths
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Response
name|response
init|=
name|restClient
operator|.
name|performRequest
argument_list|(
name|method
argument_list|,
name|endpoint
argument_list|)
decl_stmt|;
name|RestTestResponse
name|restTestResponse
init|=
operator|new
name|RestTestResponse
argument_list|(
name|response
argument_list|)
decl_stmt|;
name|Object
name|latestVersion
init|=
name|restTestResponse
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
comment|/**      * Calls an api with the provided parameters and body      */
DECL|method|callApi
specifier|public
name|RestTestResponse
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
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
literal|"raw"
operator|.
name|equals
argument_list|(
name|apiName
argument_list|)
condition|)
block|{
comment|// Raw requests are bit simpler....
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|queryStringParams
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|String
name|method
init|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|queryStringParams
operator|.
name|remove
argument_list|(
literal|"method"
argument_list|)
argument_list|,
literal|"Method must be set to use raw request"
argument_list|)
decl_stmt|;
name|String
name|path
init|=
literal|"/"
operator|+
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|queryStringParams
operator|.
name|remove
argument_list|(
literal|"path"
argument_list|)
argument_list|,
literal|"Path must be set to use raw request"
argument_list|)
decl_stmt|;
name|HttpEntity
name|entity
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|body
operator|!=
literal|null
operator|&&
name|body
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|entity
operator|=
operator|new
name|StringEntity
argument_list|(
name|body
argument_list|,
name|RestClient
operator|.
name|JSON_CONTENT_TYPE
argument_list|)
expr_stmt|;
block|}
comment|// And everything else is a url parameter!
name|Response
name|response
init|=
name|restClient
operator|.
name|performRequest
argument_list|(
name|method
argument_list|,
name|path
argument_list|,
name|queryStringParams
argument_list|,
name|entity
argument_list|)
decl_stmt|;
return|return
operator|new
name|RestTestResponse
argument_list|(
name|response
argument_list|)
return|;
block|}
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
decl_stmt|;
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
name|requestParams
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|requestParams
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
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
name|ignoreString
operator|!=
literal|null
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
block|}
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
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|queryStringParams
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
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
name|requestParams
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
name|queryStringParams
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
literal|"] "
operator|+
literal|"api"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|indexCreateApi
condition|)
block|{
name|queryStringParams
operator|.
name|put
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
name|String
name|requestMethod
decl_stmt|;
name|StringEntity
name|requestBody
init|=
literal|null
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
comment|//randomly test the GET with source param instead of GET/POST with body
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
name|queryStringParams
operator|.
name|put
argument_list|(
literal|"source"
argument_list|,
name|body
argument_list|)
expr_stmt|;
name|requestMethod
operator|=
literal|"GET"
expr_stmt|;
block|}
else|else
block|{
name|requestMethod
operator|=
name|RandomizedTest
operator|.
name|randomFrom
argument_list|(
name|supportedMethods
argument_list|)
expr_stmt|;
name|requestBody
operator|=
operator|new
name|StringEntity
argument_list|(
name|body
argument_list|,
name|RestClient
operator|.
name|JSON_CONTENT_TYPE
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
name|requestMethod
operator|=
name|RandomizedTest
operator|.
name|randomFrom
argument_list|(
name|supportedMethods
argument_list|)
expr_stmt|;
block|}
comment|//the rest path to use is randomized out of the matching ones (if more than one)
name|RestPath
name|restPath
init|=
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
decl_stmt|;
comment|//Encode rules for path and query string parameters are different. We use URI to encode the path.
comment|//We need to encode each path part separately, as each one might contain slashes that need to be escaped, which needs to
comment|//be done manually.
name|String
name|requestPath
decl_stmt|;
if|if
condition|(
name|restPath
operator|.
name|getPathParts
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|requestPath
operator|=
literal|"/"
expr_stmt|;
block|}
else|else
block|{
name|StringBuilder
name|finalPath
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|pathPart
range|:
name|restPath
operator|.
name|getPathParts
argument_list|()
control|)
block|{
try|try
block|{
name|finalPath
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
comment|// We append "/" to the path part to handle parts that start with - or other invalid characters
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
literal|"/"
operator|+
name|pathPart
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|//manually escape any slash that each part may contain
name|finalPath
operator|.
name|append
argument_list|(
name|uri
operator|.
name|getRawPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"/"
argument_list|,
literal|"%2F"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unable to build uri"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|requestPath
operator|=
name|finalPath
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|Header
index|[]
name|requestHeaders
init|=
operator|new
name|Header
index|[
name|headers
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
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
name|header
range|:
name|headers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Adding header {}\n with value {}"
argument_list|,
name|header
operator|.
name|getKey
argument_list|()
argument_list|,
name|header
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|requestHeaders
index|[
name|index
operator|++
index|]
operator|=
operator|new
name|BasicHeader
argument_list|(
name|header
operator|.
name|getKey
argument_list|()
argument_list|,
name|header
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"calling api [{}]"
argument_list|,
name|apiName
argument_list|)
expr_stmt|;
try|try
block|{
name|Response
name|response
init|=
name|restClient
operator|.
name|performRequest
argument_list|(
name|requestMethod
argument_list|,
name|requestPath
argument_list|,
name|queryStringParams
argument_list|,
name|requestBody
argument_list|,
name|requestHeaders
argument_list|)
decl_stmt|;
return|return
operator|new
name|RestTestResponse
argument_list|(
name|response
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ResponseException
name|e
parameter_list|)
block|{
if|if
condition|(
name|ignores
operator|.
name|contains
argument_list|(
name|e
operator|.
name|getResponse
argument_list|()
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|RestTestResponse
argument_list|(
name|e
operator|.
name|getResponse
argument_list|()
argument_list|)
return|;
block|}
throw|throw
name|e
throw|;
block|}
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
DECL|method|createRestClient
specifier|private
specifier|static
name|RestClient
name|createRestClient
parameter_list|(
name|URL
index|[]
name|urls
parameter_list|,
name|Settings
name|settings
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|protocol
init|=
name|settings
operator|.
name|get
argument_list|(
name|PROTOCOL
argument_list|,
literal|"http"
argument_list|)
decl_stmt|;
name|HttpHost
index|[]
name|hosts
init|=
operator|new
name|HttpHost
index|[
name|urls
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hosts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|URL
name|url
init|=
name|urls
index|[
name|i
index|]
decl_stmt|;
name|hosts
index|[
name|i
index|]
operator|=
operator|new
name|HttpHost
argument_list|(
name|url
operator|.
name|getHost
argument_list|()
argument_list|,
name|url
operator|.
name|getPort
argument_list|()
argument_list|,
name|protocol
argument_list|)
expr_stmt|;
block|}
name|RestClient
operator|.
name|Builder
name|builder
init|=
name|RestClient
operator|.
name|builder
argument_list|(
name|hosts
argument_list|)
operator|.
name|setMaxRetryTimeoutMillis
argument_list|(
literal|30000
argument_list|)
operator|.
name|setRequestConfigCallback
argument_list|(
name|requestConfigBuilder
lambda|->
name|requestConfigBuilder
operator|.
name|setSocketTimeout
argument_list|(
literal|30000
argument_list|)
argument_list|)
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
name|SSLIOSessionStrategy
name|sessionStrategy
init|=
operator|new
name|SSLIOSessionStrategy
argument_list|(
name|sslcontext
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setHttpClientConfigCallback
argument_list|(
name|httpClientBuilder
lambda|->
name|httpClientBuilder
operator|.
name|setSSLStrategy
argument_list|(
name|sessionStrategy
argument_list|)
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
try|try
init|(
name|ThreadContext
name|threadContext
init|=
operator|new
name|ThreadContext
argument_list|(
name|settings
argument_list|)
init|)
block|{
name|Header
index|[]
name|defaultHeaders
init|=
operator|new
name|Header
index|[
name|threadContext
operator|.
name|getHeaders
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
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
name|threadContext
operator|.
name|getHeaders
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|defaultHeaders
index|[
name|i
operator|++
index|]
operator|=
operator|new
name|BasicHeader
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
name|builder
operator|.
name|setDefaultHeaders
argument_list|(
name|defaultHeaders
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
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
name|restClient
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

