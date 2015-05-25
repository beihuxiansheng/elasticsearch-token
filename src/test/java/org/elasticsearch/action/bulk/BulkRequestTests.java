begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.bulk
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|bulk
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
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
name|Constants
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
name|ActionRequest
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
name|delete
operator|.
name|DeleteRequest
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
name|index
operator|.
name|IndexRequest
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
name|update
operator|.
name|UpdateRequest
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
name|Requests
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
name|test
operator|.
name|ElasticsearchTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|List
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
name|io
operator|.
name|Streams
operator|.
name|copyToStringFromClasspath
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
name|instanceOf
import|;
end_import

begin_class
DECL|class|BulkRequestTests
specifier|public
class|class
name|BulkRequestTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testSimpleBulk1
specifier|public
name|void
name|testSimpleBulk1
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|bulkAction
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/action/bulk/simple-bulk.json"
argument_list|)
decl_stmt|;
comment|// translate Windows line endings (\r\n) to standard ones (\n)
if|if
condition|(
name|Constants
operator|.
name|WINDOWS
condition|)
block|{
name|bulkAction
operator|=
name|Strings
operator|.
name|replace
argument_list|(
name|bulkAction
argument_list|,
literal|"\r\n"
argument_list|,
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|BulkRequest
name|bulkRequest
init|=
operator|new
name|BulkRequest
argument_list|()
decl_stmt|;
name|bulkRequest
operator|.
name|add
argument_list|(
name|bulkAction
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bulkAction
operator|.
name|length
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bulkRequest
operator|.
name|numberOfActions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|IndexRequest
operator|)
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|source
argument_list|()
operator|.
name|toBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|BytesArray
argument_list|(
literal|"{ \"field1\" : \"value1\" }"
argument_list|)
operator|.
name|toBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|DeleteRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|IndexRequest
operator|)
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|)
operator|.
name|source
argument_list|()
operator|.
name|toBytes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|BytesArray
argument_list|(
literal|"{ \"field1\" : \"value3\" }"
argument_list|)
operator|.
name|toBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleBulk2
specifier|public
name|void
name|testSimpleBulk2
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|bulkAction
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/action/bulk/simple-bulk2.json"
argument_list|)
decl_stmt|;
name|BulkRequest
name|bulkRequest
init|=
operator|new
name|BulkRequest
argument_list|()
decl_stmt|;
name|bulkRequest
operator|.
name|add
argument_list|(
name|bulkAction
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bulkAction
operator|.
name|length
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bulkRequest
operator|.
name|numberOfActions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleBulk3
specifier|public
name|void
name|testSimpleBulk3
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|bulkAction
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/action/bulk/simple-bulk3.json"
argument_list|)
decl_stmt|;
name|BulkRequest
name|bulkRequest
init|=
operator|new
name|BulkRequest
argument_list|()
decl_stmt|;
name|bulkRequest
operator|.
name|add
argument_list|(
name|bulkAction
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bulkAction
operator|.
name|length
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bulkRequest
operator|.
name|numberOfActions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleBulk4
specifier|public
name|void
name|testSimpleBulk4
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|bulkAction
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/action/bulk/simple-bulk4.json"
argument_list|)
decl_stmt|;
name|BulkRequest
name|bulkRequest
init|=
operator|new
name|BulkRequest
argument_list|()
decl_stmt|;
name|bulkRequest
operator|.
name|add
argument_list|(
name|bulkAction
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bulkAction
operator|.
name|length
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bulkRequest
operator|.
name|numberOfActions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|UpdateRequest
operator|)
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|UpdateRequest
operator|)
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|retryOnConflict
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|UpdateRequest
operator|)
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|doc
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|toUtf8
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\"field\":\"value\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|UpdateRequest
operator|)
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|UpdateRequest
operator|)
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|type
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"type1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|UpdateRequest
operator|)
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|index
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"index1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|UpdateRequest
operator|)
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|script
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"counter += param1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|UpdateRequest
operator|)
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|scriptLang
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"js"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|UpdateRequest
operator|)
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|scriptParams
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
call|(
name|Integer
call|)
argument_list|(
operator|(
name|UpdateRequest
operator|)
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|scriptParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"param1"
argument_list|)
operator|)
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|UpdateRequest
operator|)
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|upsertRequest
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|toUtf8
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\"counter\":1}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBulkAllowExplicitIndex
specifier|public
name|void
name|testBulkAllowExplicitIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|bulkAction
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/action/bulk/simple-bulk.json"
argument_list|)
decl_stmt|;
try|try
block|{
operator|new
name|BulkRequest
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|bulkAction
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{          }
name|bulkAction
operator|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/action/bulk/simple-bulk5.json"
argument_list|)
expr_stmt|;
operator|new
name|BulkRequest
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|bulkAction
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|,
literal|"test"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBulkAddIterable
specifier|public
name|void
name|testBulkAddIterable
parameter_list|()
block|{
name|BulkRequest
name|bulkRequest
init|=
name|Requests
operator|.
name|bulkRequest
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ActionRequest
argument_list|>
name|requests
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|requests
operator|.
name|add
argument_list|(
operator|new
name|IndexRequest
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"id"
argument_list|)
operator|.
name|source
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|requests
operator|.
name|add
argument_list|(
operator|new
name|UpdateRequest
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"id"
argument_list|)
operator|.
name|doc
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|requests
operator|.
name|add
argument_list|(
operator|new
name|DeleteRequest
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|bulkRequest
operator|.
name|add
argument_list|(
name|requests
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|IndexRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|UpdateRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bulkRequest
operator|.
name|requests
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|DeleteRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleBulk6
specifier|public
name|void
name|testSimpleBulk6
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|bulkAction
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/action/bulk/simple-bulk6.json"
argument_list|)
decl_stmt|;
name|BulkRequest
name|bulkRequest
init|=
operator|new
name|BulkRequest
argument_list|()
decl_stmt|;
try|try
block|{
name|bulkRequest
operator|.
name|add
argument_list|(
name|bulkAction
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bulkAction
operator|.
name|length
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have thrown an exception about the wrong format of line 1"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"message contains error about the wrong format of line 1: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Malformed action/metadata line [1], expected a simple value for field [_source] but found [START_OBJECT]"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSimpleBulk7
specifier|public
name|void
name|testSimpleBulk7
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|bulkAction
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/action/bulk/simple-bulk7.json"
argument_list|)
decl_stmt|;
name|BulkRequest
name|bulkRequest
init|=
operator|new
name|BulkRequest
argument_list|()
decl_stmt|;
try|try
block|{
name|bulkRequest
operator|.
name|add
argument_list|(
name|bulkAction
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bulkAction
operator|.
name|length
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have thrown an exception about the wrong format of line 5"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"message contains error about the wrong format of line 5: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Malformed action/metadata line [5], expected a simple value for field [_unkown] but found [START_ARRAY]"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSimpleBulk8
specifier|public
name|void
name|testSimpleBulk8
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|bulkAction
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/action/bulk/simple-bulk8.json"
argument_list|)
decl_stmt|;
name|BulkRequest
name|bulkRequest
init|=
operator|new
name|BulkRequest
argument_list|()
decl_stmt|;
try|try
block|{
name|bulkRequest
operator|.
name|add
argument_list|(
name|bulkAction
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bulkAction
operator|.
name|length
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have thrown an exception about the unknown paramater _foo"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"message contains error about the unknown paramater _foo: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Action/metadata line [3] contains an unknown parameter [_foo]"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSimpleBulk9
specifier|public
name|void
name|testSimpleBulk9
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|bulkAction
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/action/bulk/simple-bulk9.json"
argument_list|)
decl_stmt|;
name|BulkRequest
name|bulkRequest
init|=
operator|new
name|BulkRequest
argument_list|()
decl_stmt|;
try|try
block|{
name|bulkRequest
operator|.
name|add
argument_list|(
name|bulkAction
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bulkAction
operator|.
name|length
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have thrown an exception about the wrong format of line 3"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"message contains error about the wrong format of line 3: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Malformed action/metadata line [3], expected START_OBJECT or END_OBJECT but found [START_ARRAY]"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

