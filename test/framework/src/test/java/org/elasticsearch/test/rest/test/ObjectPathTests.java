begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|test
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
name|rest
operator|.
name|ObjectPath
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
name|Stash
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|contains
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
name|containsString
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|notNullValue
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
name|nullValue
import|;
end_import

begin_class
DECL|class|ObjectPathTests
specifier|public
class|class
name|ObjectPathTests
extends|extends
name|ESTestCase
block|{
DECL|method|randomXContentBuilder
specifier|private
specifier|static
name|XContentBuilder
name|randomXContentBuilder
parameter_list|()
throws|throws
name|IOException
block|{
comment|//only string based formats are supported, no cbor nor smile
name|XContentType
name|xContentType
init|=
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|,
name|XContentType
operator|.
name|YAML
argument_list|)
decl_stmt|;
return|return
name|XContentBuilder
operator|.
name|builder
argument_list|(
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|xContentType
argument_list|)
argument_list|)
return|;
block|}
DECL|method|testEvaluateObjectPathEscape
specifier|public
name|void
name|testEvaluateObjectPathEscape
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|xContentBuilder
init|=
name|randomXContentBuilder
argument_list|()
decl_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|(
literal|"field1"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"field2.field3"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|ObjectPath
name|objectPath
init|=
name|ObjectPath
operator|.
name|createFromXContent
argument_list|(
name|xContentBuilder
operator|.
name|contentType
argument_list|()
operator|.
name|xContent
argument_list|()
argument_list|,
name|xContentBuilder
operator|.
name|string
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|object
init|=
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|"field1.field2\\.field3"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|instanceOf
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEvaluateObjectPathWithDots
specifier|public
name|void
name|testEvaluateObjectPathWithDots
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|xContentBuilder
init|=
name|randomXContentBuilder
argument_list|()
decl_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|(
literal|"field1"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|ObjectPath
name|objectPath
init|=
name|ObjectPath
operator|.
name|createFromXContent
argument_list|(
name|xContentBuilder
operator|.
name|contentType
argument_list|()
operator|.
name|xContent
argument_list|()
argument_list|,
name|xContentBuilder
operator|.
name|string
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|object
init|=
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|"field1..field2"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|instanceOf
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|object
operator|=
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|"field1.field2."
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|instanceOf
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|object
operator|=
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|"field1.field2"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|instanceOf
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEvaluateInteger
specifier|public
name|void
name|testEvaluateInteger
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|xContentBuilder
init|=
name|randomXContentBuilder
argument_list|()
decl_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|(
literal|"field1"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|333
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|ObjectPath
name|objectPath
init|=
name|ObjectPath
operator|.
name|createFromXContent
argument_list|(
name|xContentBuilder
operator|.
name|contentType
argument_list|()
operator|.
name|xContent
argument_list|()
argument_list|,
name|xContentBuilder
operator|.
name|string
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|object
init|=
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|"field1.field2"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|instanceOf
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|equalTo
argument_list|(
literal|333
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEvaluateDouble
specifier|public
name|void
name|testEvaluateDouble
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|xContentBuilder
init|=
name|randomXContentBuilder
argument_list|()
decl_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|(
literal|"field1"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|3.55
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|ObjectPath
name|objectPath
init|=
name|ObjectPath
operator|.
name|createFromXContent
argument_list|(
name|xContentBuilder
operator|.
name|contentType
argument_list|()
operator|.
name|xContent
argument_list|()
argument_list|,
name|xContentBuilder
operator|.
name|string
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|object
init|=
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|"field1.field2"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|instanceOf
argument_list|(
name|Double
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|equalTo
argument_list|(
literal|3.55
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEvaluateArray
specifier|public
name|void
name|testEvaluateArray
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|xContentBuilder
init|=
name|randomXContentBuilder
argument_list|()
decl_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|(
literal|"field1"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|array
argument_list|(
literal|"array1"
argument_list|,
literal|"value1"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|ObjectPath
name|objectPath
init|=
name|ObjectPath
operator|.
name|createFromXContent
argument_list|(
name|xContentBuilder
operator|.
name|contentType
argument_list|()
operator|.
name|xContent
argument_list|()
argument_list|,
name|xContentBuilder
operator|.
name|string
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|object
init|=
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|"field1.array1"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|object
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
name|list
init|=
operator|(
name|List
operator|)
name|object
decl_stmt|;
name|assertThat
argument_list|(
name|list
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
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|list
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|object
operator|=
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|"field1.array1.1"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|instanceOf
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testEvaluateArrayElementObject
specifier|public
name|void
name|testEvaluateArrayElementObject
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|xContentBuilder
init|=
name|randomXContentBuilder
argument_list|()
decl_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|(
literal|"field1"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|startArray
argument_list|(
literal|"array1"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"element"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"element"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|ObjectPath
name|objectPath
init|=
name|ObjectPath
operator|.
name|createFromXContent
argument_list|(
name|xContentBuilder
operator|.
name|contentType
argument_list|()
operator|.
name|xContent
argument_list|()
argument_list|,
name|xContentBuilder
operator|.
name|string
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|object
init|=
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|"field1.array1.1.element"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|instanceOf
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|object
operator|=
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|instanceOf
argument_list|(
name|Map
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|object
operator|)
operator|.
name|containsKey
argument_list|(
literal|"field1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|object
operator|=
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|"field1.array2.1.element"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testEvaluateObjectKeys
specifier|public
name|void
name|testEvaluateObjectKeys
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|xContentBuilder
init|=
name|randomXContentBuilder
argument_list|()
decl_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|(
literal|"metadata"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|(
literal|"templates"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|(
literal|"template_1"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|(
literal|"template_2"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|ObjectPath
name|objectPath
init|=
name|ObjectPath
operator|.
name|createFromXContent
argument_list|(
name|xContentBuilder
operator|.
name|contentType
argument_list|()
operator|.
name|xContent
argument_list|()
argument_list|,
name|xContentBuilder
operator|.
name|string
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|object
init|=
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|"metadata.templates"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|object
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
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|object
decl_stmt|;
name|assertThat
argument_list|(
name|map
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
name|Set
argument_list|<
name|String
argument_list|>
name|strings
init|=
name|map
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|strings
argument_list|,
name|contains
argument_list|(
literal|"template_1"
argument_list|,
literal|"template_2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEvaluateStashInPropertyName
specifier|public
name|void
name|testEvaluateStashInPropertyName
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|xContentBuilder
init|=
name|randomXContentBuilder
argument_list|()
decl_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|(
literal|"field1"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|(
literal|"elements"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"element1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|ObjectPath
name|objectPath
init|=
name|ObjectPath
operator|.
name|createFromXContent
argument_list|(
name|xContentBuilder
operator|.
name|contentType
argument_list|()
operator|.
name|xContent
argument_list|()
argument_list|,
name|xContentBuilder
operator|.
name|string
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|"field1.$placeholder.element1"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"evaluate should have failed due to unresolved placeholder"
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"stashed value not found for key [$placeholder]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Stash
name|stash
init|=
operator|new
name|Stash
argument_list|()
decl_stmt|;
name|stash
operator|.
name|stashValue
argument_list|(
literal|"placeholder"
argument_list|,
literal|"elements"
argument_list|)
expr_stmt|;
name|Object
name|object
init|=
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|"field1.$placeholder.element1"
argument_list|,
name|stash
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
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
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testEvaluateArrayAsRoot
specifier|public
name|void
name|testEvaluateArrayAsRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|xContentBuilder
init|=
name|randomXContentBuilder
argument_list|()
decl_stmt|;
name|xContentBuilder
operator|.
name|startArray
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"alias"
argument_list|,
literal|"test_alias1"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"test1"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"alias"
argument_list|,
literal|"test_alias2"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"test2"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|ObjectPath
name|objectPath
init|=
name|ObjectPath
operator|.
name|createFromXContent
argument_list|(
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|YAML
argument_list|)
argument_list|,
name|xContentBuilder
operator|.
name|string
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|object
init|=
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|instanceOf
argument_list|(
name|List
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|object
operator|)
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
name|object
operator|=
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|instanceOf
argument_list|(
name|Map
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|object
operator|)
operator|.
name|get
argument_list|(
literal|"alias"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"test_alias1"
argument_list|)
argument_list|)
expr_stmt|;
name|object
operator|=
name|objectPath
operator|.
name|evaluate
argument_list|(
literal|"1.index"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|instanceOf
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|object
argument_list|,
name|equalTo
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

