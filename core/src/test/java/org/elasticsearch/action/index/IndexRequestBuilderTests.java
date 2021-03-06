begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|index
package|;
end_package

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
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
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
name|XContentHelper
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
name|XContentType
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
name|elasticsearch
operator|.
name|test
operator|.
name|client
operator|.
name|NoOpClient
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
name|ByteArrayOutputStream
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
name|Map
import|;
end_import

begin_class
DECL|class|IndexRequestBuilderTests
specifier|public
class|class
name|IndexRequestBuilderTests
extends|extends
name|ESTestCase
block|{
DECL|field|EXPECTED_SOURCE
specifier|private
specifier|static
specifier|final
name|String
name|EXPECTED_SOURCE
init|=
literal|"{\"SomeKey\":\"SomeValue\"}"
decl_stmt|;
DECL|field|testClient
specifier|private
name|NoOpClient
name|testClient
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|this
operator|.
name|testClient
operator|=
operator|new
name|NoOpClient
argument_list|(
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|testClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/**      * test setting the source for the request with different available setters      */
DECL|method|testSetSource
specifier|public
name|void
name|testSetSource
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexRequestBuilder
name|indexRequestBuilder
init|=
operator|new
name|IndexRequestBuilder
argument_list|(
name|this
operator|.
name|testClient
argument_list|,
name|IndexAction
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|source
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|source
operator|.
name|put
argument_list|(
literal|"SomeKey"
argument_list|,
literal|"SomeValue"
argument_list|)
expr_stmt|;
name|indexRequestBuilder
operator|.
name|setSource
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXPECTED_SOURCE
argument_list|,
name|XContentHelper
operator|.
name|convertToJson
argument_list|(
name|indexRequestBuilder
operator|.
name|request
argument_list|()
operator|.
name|source
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|indexRequestBuilder
operator|.
name|setSource
argument_list|(
name|source
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXPECTED_SOURCE
argument_list|,
name|XContentHelper
operator|.
name|convertToJson
argument_list|(
name|indexRequestBuilder
operator|.
name|request
argument_list|()
operator|.
name|source
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|indexRequestBuilder
operator|.
name|setSource
argument_list|(
literal|"SomeKey"
argument_list|,
literal|"SomeValue"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXPECTED_SOURCE
argument_list|,
name|XContentHelper
operator|.
name|convertToJson
argument_list|(
name|indexRequestBuilder
operator|.
name|request
argument_list|()
operator|.
name|source
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// force the Object... setter
name|indexRequestBuilder
operator|.
name|setSource
argument_list|(
operator|(
name|Object
operator|)
literal|"SomeKey"
argument_list|,
literal|"SomeValue"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXPECTED_SOURCE
argument_list|,
name|XContentHelper
operator|.
name|convertToJson
argument_list|(
name|indexRequestBuilder
operator|.
name|request
argument_list|()
operator|.
name|source
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|docOut
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|XContentBuilder
name|doc
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|(
name|docOut
argument_list|)
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"SomeKey"
argument_list|,
literal|"SomeValue"
argument_list|)
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|doc
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexRequestBuilder
operator|.
name|setSource
argument_list|(
name|docOut
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXPECTED_SOURCE
argument_list|,
name|XContentHelper
operator|.
name|convertToJson
argument_list|(
name|indexRequestBuilder
operator|.
name|request
argument_list|()
operator|.
name|source
argument_list|()
argument_list|,
literal|true
argument_list|,
name|indexRequestBuilder
operator|.
name|request
argument_list|()
operator|.
name|getContentType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"SomeKey"
argument_list|,
literal|"SomeValue"
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|doc
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexRequestBuilder
operator|.
name|setSource
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXPECTED_SOURCE
argument_list|,
name|XContentHelper
operator|.
name|convertToJson
argument_list|(
name|indexRequestBuilder
operator|.
name|request
argument_list|()
operator|.
name|source
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

