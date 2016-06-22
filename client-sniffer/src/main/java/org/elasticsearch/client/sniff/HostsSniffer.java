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
name|JsonParser
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
name|JsonToken
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|RestClient
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Class responsible for sniffing the http hosts from elasticsearch through the nodes info api and returning them back.  * Compatible with elasticsearch 5.x and 2.x.  */
end_comment

begin_class
DECL|class|HostsSniffer
specifier|public
class|class
name|HostsSniffer
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Log
name|logger
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|HostsSniffer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|restClient
specifier|private
specifier|final
name|RestClient
name|restClient
decl_stmt|;
DECL|field|sniffRequestParams
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|sniffRequestParams
decl_stmt|;
DECL|field|scheme
specifier|private
specifier|final
name|Scheme
name|scheme
decl_stmt|;
DECL|field|jsonFactory
specifier|private
specifier|final
name|JsonFactory
name|jsonFactory
init|=
operator|new
name|JsonFactory
argument_list|()
decl_stmt|;
DECL|method|HostsSniffer
specifier|protected
name|HostsSniffer
parameter_list|(
name|RestClient
name|restClient
parameter_list|,
name|long
name|sniffRequestTimeoutMillis
parameter_list|,
name|Scheme
name|scheme
parameter_list|)
block|{
name|this
operator|.
name|restClient
operator|=
name|restClient
expr_stmt|;
name|this
operator|.
name|sniffRequestParams
operator|=
name|Collections
operator|.
expr|<
name|String
operator|,
name|String
operator|>
name|singletonMap
argument_list|(
literal|"timeout"
argument_list|,
name|sniffRequestTimeoutMillis
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheme
operator|=
name|scheme
expr_stmt|;
block|}
comment|/**      * Calls the elasticsearch nodes info api, parses the response and returns all the found http hosts      */
DECL|method|sniffHosts
specifier|public
name|List
argument_list|<
name|HttpHost
argument_list|>
name|sniffHosts
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|Response
name|response
init|=
name|restClient
operator|.
name|performRequest
argument_list|(
literal|"get"
argument_list|,
literal|"/_nodes/http"
argument_list|,
name|sniffRequestParams
argument_list|,
literal|null
argument_list|)
init|)
block|{
return|return
name|readHosts
argument_list|(
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|readHosts
specifier|private
name|List
argument_list|<
name|HttpHost
argument_list|>
name|readHosts
parameter_list|(
name|HttpEntity
name|entity
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|InputStream
name|inputStream
init|=
name|entity
operator|.
name|getContent
argument_list|()
init|)
block|{
name|JsonParser
name|parser
init|=
name|jsonFactory
operator|.
name|createParser
argument_list|(
name|inputStream
argument_list|)
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|JsonToken
operator|.
name|START_OBJECT
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"expected data to start with an object"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|HttpHost
argument_list|>
name|hosts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|JsonToken
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|parser
operator|.
name|getCurrentToken
argument_list|()
operator|==
name|JsonToken
operator|.
name|START_OBJECT
condition|)
block|{
if|if
condition|(
literal|"nodes"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getCurrentName
argument_list|()
argument_list|)
condition|)
block|{
while|while
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|JsonToken
operator|.
name|END_OBJECT
condition|)
block|{
name|JsonToken
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
assert|assert
name|token
operator|==
name|JsonToken
operator|.
name|START_OBJECT
assert|;
name|String
name|nodeId
init|=
name|parser
operator|.
name|getCurrentName
argument_list|()
decl_stmt|;
name|HttpHost
name|sniffedHost
init|=
name|readHost
argument_list|(
name|nodeId
argument_list|,
name|parser
argument_list|,
name|this
operator|.
name|scheme
argument_list|)
decl_stmt|;
if|if
condition|(
name|sniffedHost
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"adding node ["
operator|+
name|nodeId
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|hosts
operator|.
name|add
argument_list|(
name|sniffedHost
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|parser
operator|.
name|skipChildren
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|hosts
return|;
block|}
block|}
DECL|method|readHost
specifier|private
specifier|static
name|HttpHost
name|readHost
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|JsonParser
name|parser
parameter_list|,
name|Scheme
name|scheme
parameter_list|)
throws|throws
name|IOException
block|{
name|HttpHost
name|httpHost
init|=
literal|null
decl_stmt|;
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|JsonToken
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|parser
operator|.
name|getCurrentToken
argument_list|()
operator|==
name|JsonToken
operator|.
name|FIELD_NAME
condition|)
block|{
name|fieldName
operator|=
name|parser
operator|.
name|getCurrentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parser
operator|.
name|getCurrentToken
argument_list|()
operator|==
name|JsonToken
operator|.
name|START_OBJECT
condition|)
block|{
if|if
condition|(
literal|"http"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
while|while
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|JsonToken
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|parser
operator|.
name|getCurrentToken
argument_list|()
operator|==
name|JsonToken
operator|.
name|VALUE_STRING
operator|&&
literal|"publish_address"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getCurrentName
argument_list|()
argument_list|)
condition|)
block|{
name|URI
name|boundAddressAsURI
init|=
name|URI
operator|.
name|create
argument_list|(
name|scheme
operator|+
literal|"://"
operator|+
name|parser
operator|.
name|getValueAsString
argument_list|()
argument_list|)
decl_stmt|;
name|httpHost
operator|=
operator|new
name|HttpHost
argument_list|(
name|boundAddressAsURI
operator|.
name|getHost
argument_list|()
argument_list|,
name|boundAddressAsURI
operator|.
name|getPort
argument_list|()
argument_list|,
name|boundAddressAsURI
operator|.
name|getScheme
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parser
operator|.
name|getCurrentToken
argument_list|()
operator|==
name|JsonToken
operator|.
name|START_OBJECT
condition|)
block|{
name|parser
operator|.
name|skipChildren
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|parser
operator|.
name|skipChildren
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|//http section is not present if http is not enabled on the node, ignore such nodes
if|if
condition|(
name|httpHost
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"skipping node ["
operator|+
name|nodeId
operator|+
literal|"] with http disabled"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|httpHost
return|;
block|}
comment|/**      * Returns a new {@link Builder} to help with {@link HostsSniffer} creation.      */
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|(
name|RestClient
name|restClient
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|restClient
argument_list|)
return|;
block|}
DECL|enum|Scheme
specifier|public
enum|enum
name|Scheme
block|{
DECL|enum constant|HTTP
DECL|enum constant|HTTPS
name|HTTP
argument_list|(
literal|"http"
argument_list|)
block|,
name|HTTPS
argument_list|(
literal|"https"
argument_list|)
block|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|Scheme
name|Scheme
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
comment|/**      * HostsSniffer builder. Helps creating a new {@link HostsSniffer}.      */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|DEFAULT_SNIFF_REQUEST_TIMEOUT
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_SNIFF_REQUEST_TIMEOUT
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|restClient
specifier|private
specifier|final
name|RestClient
name|restClient
decl_stmt|;
DECL|field|sniffRequestTimeoutMillis
specifier|private
name|long
name|sniffRequestTimeoutMillis
init|=
name|DEFAULT_SNIFF_REQUEST_TIMEOUT
decl_stmt|;
DECL|field|scheme
specifier|private
name|Scheme
name|scheme
decl_stmt|;
DECL|method|Builder
specifier|private
name|Builder
parameter_list|(
name|RestClient
name|restClient
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|restClient
argument_list|,
literal|"restClient cannot be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|restClient
operator|=
name|restClient
expr_stmt|;
block|}
comment|/**          * Sets the sniff request timeout (in milliseconds) to be passed in as a query string parameter to elasticsearch.          * Allows to halt the request without any failure, as only the nodes that have responded within this timeout will be returned.          */
DECL|method|setSniffRequestTimeoutMillis
specifier|public
name|Builder
name|setSniffRequestTimeoutMillis
parameter_list|(
name|int
name|sniffRequestTimeoutMillis
parameter_list|)
block|{
if|if
condition|(
name|sniffRequestTimeoutMillis
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"sniffRequestTimeoutMillis must be greater than 0"
argument_list|)
throw|;
block|}
name|this
operator|.
name|sniffRequestTimeoutMillis
operator|=
name|sniffRequestTimeoutMillis
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Sets the scheme to associate sniffed nodes with (as it is not returned by elasticsearch)          */
DECL|method|setScheme
specifier|public
name|Builder
name|setScheme
parameter_list|(
name|Scheme
name|scheme
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|scheme
argument_list|,
literal|"scheme cannot be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheme
operator|=
name|scheme
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Creates a new {@link HostsSniffer} instance given the provided configuration          */
DECL|method|build
specifier|public
name|HostsSniffer
name|build
parameter_list|()
block|{
return|return
operator|new
name|HostsSniffer
argument_list|(
name|restClient
argument_list|,
name|sniffRequestTimeoutMillis
argument_list|,
name|scheme
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

