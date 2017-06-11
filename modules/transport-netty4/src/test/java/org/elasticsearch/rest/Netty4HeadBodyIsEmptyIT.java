begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
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
name|entity
operator|.
name|ContentType
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
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilder
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
name|ESRestTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
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
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
operator|.
name|NOT_FOUND
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
operator|.
name|OK
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|greaterThan
import|;
end_import

begin_class
DECL|class|Netty4HeadBodyIsEmptyIT
specifier|public
class|class
name|Netty4HeadBodyIsEmptyIT
extends|extends
name|ESRestTestCase
block|{
DECL|method|testHeadRoot
specifier|public
name|void
name|testHeadRoot
parameter_list|()
throws|throws
name|IOException
block|{
name|headTestCase
argument_list|(
literal|"/"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/"
argument_list|,
name|singletonMap
argument_list|(
literal|"pretty"
argument_list|,
literal|""
argument_list|)
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/"
argument_list|,
name|singletonMap
argument_list|(
literal|"pretty"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createTestDoc
specifier|private
name|void
name|createTestDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|createTestDoc
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
DECL|method|createTestDoc
specifier|private
name|void
name|createTestDoc
parameter_list|(
specifier|final
name|String
name|indexName
parameter_list|,
specifier|final
name|String
name|typeName
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|XContentBuilder
name|builder
init|=
name|jsonBuilder
argument_list|()
init|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"PUT"
argument_list|,
literal|"/"
operator|+
name|indexName
operator|+
literal|"/"
operator|+
name|typeName
operator|+
literal|"/"
operator|+
literal|"1"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
operator|new
name|StringEntity
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDocumentExists
specifier|public
name|void
name|testDocumentExists
parameter_list|()
throws|throws
name|IOException
block|{
name|createTestDoc
argument_list|()
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/test/test/1"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/test/test/1"
argument_list|,
name|singletonMap
argument_list|(
literal|"pretty"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/test/test/2"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|NOT_FOUND
operator|.
name|getStatus
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIndexExists
specifier|public
name|void
name|testIndexExists
parameter_list|()
throws|throws
name|IOException
block|{
name|createTestDoc
argument_list|()
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/test"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/test"
argument_list|,
name|singletonMap
argument_list|(
literal|"pretty"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTypeExists
specifier|public
name|void
name|testTypeExists
parameter_list|()
throws|throws
name|IOException
block|{
name|createTestDoc
argument_list|()
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/test/test"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/test/test"
argument_list|,
name|singletonMap
argument_list|(
literal|"pretty"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAliasExists
specifier|public
name|void
name|testAliasExists
parameter_list|()
throws|throws
name|IOException
block|{
name|createTestDoc
argument_list|()
expr_stmt|;
try|try
init|(
name|XContentBuilder
name|builder
init|=
name|jsonBuilder
argument_list|()
init|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
block|{
name|builder
operator|.
name|startArray
argument_list|(
literal|"actions"
argument_list|)
expr_stmt|;
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"add"
argument_list|)
expr_stmt|;
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"alias"
argument_list|,
literal|"test_alias"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"POST"
argument_list|,
literal|"_aliases"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
operator|new
name|StringEntity
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/_alias/test_alias"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/test/_alias/test_alias"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAliasDoesNotExist
specifier|public
name|void
name|testAliasDoesNotExist
parameter_list|()
throws|throws
name|IOException
block|{
name|createTestDoc
argument_list|()
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/_alias/test_alias"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|NOT_FOUND
operator|.
name|getStatus
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/test/_alias/test_alias"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|NOT_FOUND
operator|.
name|getStatus
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTemplateExists
specifier|public
name|void
name|testTemplateExists
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|XContentBuilder
name|builder
init|=
name|jsonBuilder
argument_list|()
init|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
block|{
name|builder
operator|.
name|array
argument_list|(
literal|"index_patterns"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"settings"
argument_list|)
expr_stmt|;
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"number_of_replicas"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"PUT"
argument_list|,
literal|"/_template/template"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
operator|new
name|StringEntity
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/_template/template"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testGetSourceAction
specifier|public
name|void
name|testGetSourceAction
parameter_list|()
throws|throws
name|IOException
block|{
name|createTestDoc
argument_list|()
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/test/test/1/_source"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/test/test/2/_source"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|NOT_FOUND
operator|.
name|getStatus
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|XContentBuilder
name|builder
init|=
name|jsonBuilder
argument_list|()
init|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"mappings"
argument_list|)
expr_stmt|;
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"test-no-source"
argument_list|)
expr_stmt|;
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"_source"
argument_list|)
expr_stmt|;
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"PUT"
argument_list|,
literal|"/test-no-source"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
operator|new
name|StringEntity
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|,
name|ContentType
operator|.
name|APPLICATION_JSON
argument_list|)
argument_list|)
expr_stmt|;
name|createTestDoc
argument_list|(
literal|"test-no-source"
argument_list|,
literal|"test-no-source"
argument_list|)
expr_stmt|;
name|headTestCase
argument_list|(
literal|"/test-no-source/test-no-source/1/_source"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|NOT_FOUND
operator|.
name|getStatus
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testException
specifier|public
name|void
name|testException
parameter_list|()
throws|throws
name|IOException
block|{
comment|/*          * This will throw an index not found exception which will be sent on the channel; previously when handling HEAD requests that would          * throw an exception, the content was swallowed and a content length header of zero was returned. Instead of swallowing the content          * we now let it rise up to the upstream channel so that it can compute the content length that would be returned. This test case is          * a test for this situation.          */
name|headTestCase
argument_list|(
literal|"/index-not-found-exception"
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|NOT_FOUND
operator|.
name|getStatus
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|headTestCase
specifier|private
name|void
name|headTestCase
parameter_list|(
specifier|final
name|String
name|url
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|,
specifier|final
name|Matcher
argument_list|<
name|Integer
argument_list|>
name|matcher
parameter_list|)
throws|throws
name|IOException
block|{
name|headTestCase
argument_list|(
name|url
argument_list|,
name|params
argument_list|,
name|OK
operator|.
name|getStatus
argument_list|()
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
DECL|method|headTestCase
specifier|private
name|void
name|headTestCase
parameter_list|(
specifier|final
name|String
name|url
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|,
specifier|final
name|int
name|expectedStatusCode
parameter_list|,
specifier|final
name|Matcher
argument_list|<
name|Integer
argument_list|>
name|matcher
parameter_list|)
throws|throws
name|IOException
block|{
name|Response
name|response
init|=
name|client
argument_list|()
operator|.
name|performRequest
argument_list|(
literal|"HEAD"
argument_list|,
name|url
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedStatusCode
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
name|assertThat
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|response
operator|.
name|getHeader
argument_list|(
literal|"Content-Length"
argument_list|)
argument_list|)
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"HEAD requests shouldn't have a response body but "
operator|+
name|url
operator|+
literal|" did"
argument_list|,
name|response
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

