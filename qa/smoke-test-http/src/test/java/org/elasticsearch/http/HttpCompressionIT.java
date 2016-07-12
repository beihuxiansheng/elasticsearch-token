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
name|apache
operator|.
name|http
operator|.
name|HttpHeaders
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
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESIntegTestCase
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

begin_class
DECL|class|HttpCompressionIT
specifier|public
class|class
name|HttpCompressionIT
extends|extends
name|ESIntegTestCase
block|{
DECL|field|GZIP_ENCODING
specifier|private
specifier|static
specifier|final
name|String
name|GZIP_ENCODING
init|=
literal|"gzip"
decl_stmt|;
DECL|field|SAMPLE_DOCUMENT
specifier|private
specifier|static
specifier|final
name|StringEntity
name|SAMPLE_DOCUMENT
init|=
operator|new
name|StringEntity
argument_list|(
literal|"{\n"
operator|+
literal|"   \"name\": {\n"
operator|+
literal|"      \"first name\": \"Steve\",\n"
operator|+
literal|"      \"last name\": \"Jobs\"\n"
operator|+
literal|"   }\n"
operator|+
literal|"}"
argument_list|,
name|RestClient
operator|.
name|JSON_CONTENT_TYPE
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|ignoreExternalCluster
specifier|protected
name|boolean
name|ignoreExternalCluster
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|testCompressesResponseIfRequested
specifier|public
name|void
name|testCompressesResponseIfRequested
parameter_list|()
throws|throws
name|Exception
block|{
name|ensureGreen
argument_list|()
expr_stmt|;
try|try
init|(
name|RestClient
name|client
init|=
name|getRestClient
argument_list|()
init|)
block|{
name|Response
name|response
init|=
name|client
operator|.
name|performRequest
argument_list|(
literal|"GET"
argument_list|,
literal|"/"
argument_list|,
operator|new
name|BasicHeader
argument_list|(
name|HttpHeaders
operator|.
name|ACCEPT_ENCODING
argument_list|,
name|GZIP_ENCODING
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|GZIP_ENCODING
argument_list|,
name|response
operator|.
name|getHeader
argument_list|(
name|HttpHeaders
operator|.
name|CONTENT_ENCODING
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testUncompressedResponseByDefault
specifier|public
name|void
name|testUncompressedResponseByDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|ensureGreen
argument_list|()
expr_stmt|;
try|try
init|(
name|RestClient
name|client
init|=
name|getRestClient
argument_list|()
init|)
block|{
name|Response
name|response
init|=
name|client
operator|.
name|performRequest
argument_list|(
literal|"GET"
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|response
operator|.
name|getHeader
argument_list|(
name|HttpHeaders
operator|.
name|CONTENT_ENCODING
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
operator|.
name|performRequest
argument_list|(
literal|"POST"
argument_list|,
literal|"/company/employees/1"
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|SAMPLE_DOCUMENT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|201
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|response
operator|.
name|getHeader
argument_list|(
name|HttpHeaders
operator|.
name|CONTENT_ENCODING
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

