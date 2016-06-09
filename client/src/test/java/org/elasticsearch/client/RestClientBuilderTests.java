begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client
package|package
name|org
operator|.
name|elasticsearch
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
name|generators
operator|.
name|RandomInts
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
name|impl
operator|.
name|client
operator|.
name|HttpClientBuilder
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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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

begin_class
DECL|class|RestClientBuilderTests
specifier|public
class|class
name|RestClientBuilderTests
extends|extends
name|LuceneTestCase
block|{
DECL|method|testBuild
specifier|public
name|void
name|testBuild
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|RestClient
operator|.
name|builder
argument_list|(
operator|(
name|HttpHost
index|[]
operator|)
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
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"no hosts provided"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|RestClient
operator|.
name|builder
argument_list|()
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
literal|"no hosts provided"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|RestClient
operator|.
name|builder
argument_list|(
operator|new
name|HttpHost
index|[]
block|{
operator|new
name|HttpHost
argument_list|(
literal|"localhost"
argument_list|,
literal|9200
argument_list|)
block|,
literal|null
block|}
argument_list|)
operator|.
name|build
argument_list|()
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
literal|"host cannot be null"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|RestClient
operator|.
name|builder
argument_list|(
operator|new
name|HttpHost
argument_list|(
literal|"localhost"
argument_list|,
literal|9200
argument_list|)
argument_list|)
operator|.
name|setMaxRetryTimeoutMillis
argument_list|(
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|Integer
operator|.
name|MIN_VALUE
argument_list|,
literal|0
argument_list|)
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
literal|"maxRetryTimeoutMillis must be greater than 0"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|RestClient
operator|.
name|builder
argument_list|(
operator|new
name|HttpHost
argument_list|(
literal|"localhost"
argument_list|,
literal|9200
argument_list|)
argument_list|)
operator|.
name|setDefaultHeaders
argument_list|(
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
literal|"default headers must not be null"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|RestClient
operator|.
name|builder
argument_list|(
operator|new
name|HttpHost
argument_list|(
literal|"localhost"
argument_list|,
literal|9200
argument_list|)
argument_list|)
operator|.
name|setDefaultHeaders
argument_list|(
operator|new
name|Header
index|[]
block|{
literal|null
block|}
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
literal|"default header must not be null"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|numNodes
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|HttpHost
index|[]
name|hosts
init|=
operator|new
name|HttpHost
index|[
name|numNodes
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
name|numNodes
condition|;
name|i
operator|++
control|)
block|{
name|hosts
index|[
name|i
index|]
operator|=
operator|new
name|HttpHost
argument_list|(
literal|"localhost"
argument_list|,
literal|9200
operator|+
name|i
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
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|setHttpClient
argument_list|(
name|HttpClientBuilder
operator|.
name|create
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|int
name|numHeaders
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|Header
index|[]
name|headers
init|=
operator|new
name|Header
index|[
name|numHeaders
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
name|numHeaders
condition|;
name|i
operator|++
control|)
block|{
name|headers
index|[
name|i
index|]
operator|=
operator|new
name|BasicHeader
argument_list|(
literal|"header"
operator|+
name|i
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setDefaultHeaders
argument_list|(
name|headers
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|setMaxRetryTimeoutMillis
argument_list|(
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|RestClient
name|restClient
init|=
name|builder
operator|.
name|build
argument_list|()
init|)
block|{
name|assertNotNull
argument_list|(
name|restClient
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

