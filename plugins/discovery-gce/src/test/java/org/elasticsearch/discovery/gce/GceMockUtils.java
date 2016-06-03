begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.gce
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|gce
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|api
operator|.
name|client
operator|.
name|http
operator|.
name|HttpTransport
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|api
operator|.
name|client
operator|.
name|http
operator|.
name|LowLevelHttpRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|api
operator|.
name|client
operator|.
name|http
operator|.
name|LowLevelHttpResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|api
operator|.
name|client
operator|.
name|json
operator|.
name|Json
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|api
operator|.
name|client
operator|.
name|testing
operator|.
name|http
operator|.
name|MockHttpTransport
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|api
operator|.
name|client
operator|.
name|testing
operator|.
name|http
operator|.
name|MockLowLevelHttpRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|api
operator|.
name|client
operator|.
name|testing
operator|.
name|http
operator|.
name|MockLowLevelHttpResponse
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
name|Streams
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
name|util
operator|.
name|Callback
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
name|URL
import|;
end_import

begin_class
DECL|class|GceMockUtils
specifier|public
class|class
name|GceMockUtils
block|{
DECL|field|logger
specifier|protected
specifier|final
specifier|static
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|GceMockUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|GCE_METADATA_URL
specifier|public
specifier|static
specifier|final
name|String
name|GCE_METADATA_URL
init|=
literal|"http://metadata.google.internal/computeMetadata/v1/instance"
decl_stmt|;
DECL|method|configureMock
specifier|protected
specifier|static
name|HttpTransport
name|configureMock
parameter_list|()
block|{
return|return
operator|new
name|MockHttpTransport
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|LowLevelHttpRequest
name|buildRequest
parameter_list|(
name|String
name|method
parameter_list|,
specifier|final
name|String
name|url
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|MockLowLevelHttpRequest
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|LowLevelHttpResponse
name|execute
parameter_list|()
throws|throws
name|IOException
block|{
name|MockLowLevelHttpResponse
name|response
init|=
operator|new
name|MockLowLevelHttpResponse
argument_list|()
decl_stmt|;
name|response
operator|.
name|setStatusCode
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
name|Json
operator|.
name|MEDIA_TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
name|GCE_METADATA_URL
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> Simulate GCE Auth/Metadata response for [{}]"
argument_list|,
name|url
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContent
argument_list|(
name|readGoogleInternalJsonResponse
argument_list|(
name|url
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> Simulate GCE API response for [{}]"
argument_list|,
name|url
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContent
argument_list|(
name|readGoogleApiJsonResponse
argument_list|(
name|url
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
DECL|method|readGoogleInternalJsonResponse
specifier|public
specifier|static
name|String
name|readGoogleInternalJsonResponse
parameter_list|(
name|String
name|url
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readJsonResponse
argument_list|(
name|url
argument_list|,
literal|"http://metadata.google.internal/"
argument_list|)
return|;
block|}
DECL|method|readGoogleApiJsonResponse
specifier|public
specifier|static
name|String
name|readGoogleApiJsonResponse
parameter_list|(
name|String
name|url
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readJsonResponse
argument_list|(
name|url
argument_list|,
literal|"https://www.googleapis.com/"
argument_list|)
return|;
block|}
DECL|method|readJsonResponse
specifier|private
specifier|static
name|String
name|readJsonResponse
parameter_list|(
name|String
name|url
parameter_list|,
name|String
name|urlRoot
parameter_list|)
throws|throws
name|IOException
block|{
comment|// We extract from the url the mock file path we want to use
name|String
name|mockFileName
init|=
name|Strings
operator|.
name|replace
argument_list|(
name|url
argument_list|,
name|urlRoot
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|URL
name|resource
init|=
name|GceInstancesServiceMock
operator|.
name|class
operator|.
name|getResource
argument_list|(
name|mockFileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"can't read ["
operator|+
name|url
operator|+
literal|"] in src/test/resources/org/elasticsearch/discovery/gce"
argument_list|)
throw|;
block|}
try|try
init|(
name|InputStream
name|is
init|=
name|resource
operator|.
name|openStream
argument_list|()
init|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Streams
operator|.
name|readAllLines
argument_list|(
name|is
argument_list|,
operator|new
name|Callback
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|String
name|response
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
name|response
return|;
block|}
block|}
block|}
end_class

end_unit

