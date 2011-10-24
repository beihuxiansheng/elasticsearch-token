begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|support
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
name|XContentType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|*
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
name|*
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
annotation|@
name|Test
DECL|class|XContentMapValuesTests
specifier|public
class|class
name|XContentMapValuesTests
block|{
DECL|method|testFilter
annotation|@
name|Test
specifier|public
name|void
name|testFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|builder
init|=
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
literal|"test1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test2"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|)
operator|.
name|mapAndClose
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|filter
init|=
name|XContentMapValues
operator|.
name|filter
argument_list|(
name|source
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test1"
block|}
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|filter
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
name|filter
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
name|XContentMapValues
operator|.
name|filter
argument_list|(
name|source
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test*"
block|}
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|size
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
name|filter
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|get
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
name|XContentMapValues
operator|.
name|filter
argument_list|(
name|source
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test1"
block|}
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
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
name|filter
operator|.
name|get
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// more complex object...
name|builder
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"path1"
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"path2"
argument_list|)
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|source
operator|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|)
operator|.
name|mapAndClose
argument_list|()
expr_stmt|;
name|filter
operator|=
name|XContentMapValues
operator|.
name|filter
argument_list|(
name|source
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"path1"
block|}
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
name|XContentMapValues
operator|.
name|filter
argument_list|(
name|source
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"path1*"
block|}
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|get
argument_list|(
literal|"path1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|source
operator|.
name|get
argument_list|(
literal|"path1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|containsKey
argument_list|(
literal|"test1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|=
name|XContentMapValues
operator|.
name|filter
argument_list|(
name|source
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test1*"
block|}
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|source
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|containsKey
argument_list|(
literal|"path1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testExtractValue
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testExtractValue
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|builder
init|=
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
literal|"test"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|)
operator|.
name|mapAndClose
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
literal|"test"
argument_list|,
name|map
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
literal|"test.me"
argument_list|,
name|map
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
literal|"something.else.2"
argument_list|,
name|map
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"path1"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"path2"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|map
operator|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|)
operator|.
name|mapAndClose
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
literal|"path1.path2.test"
argument_list|,
name|map
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
literal|"path1.path2.test_me"
argument_list|,
name|map
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
literal|"path1.non_path2.test"
argument_list|,
name|map
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|extValue
init|=
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
literal|"path1.path2"
argument_list|,
name|map
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|extValue
argument_list|,
name|instanceOf
argument_list|(
name|Map
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|extMapValue
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|extValue
decl_stmt|;
name|assertThat
argument_list|(
name|extMapValue
argument_list|,
name|hasEntry
argument_list|(
literal|"test"
argument_list|,
operator|(
name|Object
operator|)
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|extValue
operator|=
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
literal|"path1"
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|extValue
argument_list|,
name|instanceOf
argument_list|(
name|Map
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|extMapValue
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|extValue
expr_stmt|;
name|assertThat
argument_list|(
name|extMapValue
operator|.
name|containsKey
argument_list|(
literal|"path2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// lists
name|builder
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"path1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value1"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|map
operator|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|)
operator|.
name|mapAndClose
argument_list|()
expr_stmt|;
name|extValue
operator|=
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
literal|"path1.test"
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|extValue
argument_list|,
name|instanceOf
argument_list|(
name|List
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|List
name|extListValue
init|=
operator|(
name|List
operator|)
name|extValue
decl_stmt|;
name|assertThat
argument_list|(
name|extListValue
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"path1"
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"path2"
argument_list|)
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|map
operator|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|)
operator|.
name|mapAndClose
argument_list|()
expr_stmt|;
name|extValue
operator|=
name|XContentMapValues
operator|.
name|extractValue
argument_list|(
literal|"path1.path2.test"
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|extValue
argument_list|,
name|instanceOf
argument_list|(
name|List
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|extListValue
operator|=
operator|(
name|List
operator|)
name|extValue
expr_stmt|;
name|assertThat
argument_list|(
name|extListValue
operator|.
name|size
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
name|extListValue
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|extListValue
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

