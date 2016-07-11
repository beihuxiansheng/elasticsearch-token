begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.update
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|update
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|unit
operator|.
name|TimeValue
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
name|index
operator|.
name|get
operator|.
name|GetResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|ShardId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Script
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptService
operator|.
name|ScriptType
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|arrayContaining
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
name|is
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
DECL|class|UpdateRequestTests
specifier|public
class|class
name|UpdateRequestTests
extends|extends
name|ESTestCase
block|{
DECL|method|testUpdateRequest
specifier|public
name|void
name|testUpdateRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|UpdateRequest
name|request
init|=
operator|new
name|UpdateRequest
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
decl_stmt|;
comment|// simple script
name|request
operator|.
name|source
argument_list|(
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
literal|"script"
argument_list|,
literal|"script1"
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
expr_stmt|;
name|Script
name|script
init|=
name|request
operator|.
name|script
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|script
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|script
operator|.
name|getScript
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"script1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|script
operator|.
name|getType
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|script
operator|.
name|getLang
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
name|script
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|params
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// script with params
name|request
operator|=
operator|new
name|UpdateRequest
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|request
operator|.
name|source
argument_list|(
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
literal|"script"
argument_list|)
operator|.
name|field
argument_list|(
literal|"inline"
argument_list|,
literal|"script1"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"params"
argument_list|)
operator|.
name|field
argument_list|(
literal|"param1"
argument_list|,
literal|"value1"
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
argument_list|)
expr_stmt|;
name|script
operator|=
name|request
operator|.
name|script
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|script
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|script
operator|.
name|getScript
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"script1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|script
operator|.
name|getType
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|script
operator|.
name|getLang
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|=
name|script
operator|.
name|getParams
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|params
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
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
name|params
operator|.
name|get
argument_list|(
literal|"param1"
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
name|request
operator|=
operator|new
name|UpdateRequest
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|request
operator|.
name|source
argument_list|(
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
literal|"script"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"params"
argument_list|)
operator|.
name|field
argument_list|(
literal|"param1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"inline"
argument_list|,
literal|"script1"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
expr_stmt|;
name|script
operator|=
name|request
operator|.
name|script
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|script
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|script
operator|.
name|getScript
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"script1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|script
operator|.
name|getType
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|script
operator|.
name|getLang
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|=
name|script
operator|.
name|getParams
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|params
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
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
name|params
operator|.
name|get
argument_list|(
literal|"param1"
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
comment|// script with params and upsert
name|request
operator|=
operator|new
name|UpdateRequest
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|request
operator|.
name|source
argument_list|(
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
literal|"script"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"params"
argument_list|)
operator|.
name|field
argument_list|(
literal|"param1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"inline"
argument_list|,
literal|"script1"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"upsert"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"compound"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"value2"
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
argument_list|)
expr_stmt|;
name|script
operator|=
name|request
operator|.
name|script
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|script
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|script
operator|.
name|getScript
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"script1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|script
operator|.
name|getType
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|script
operator|.
name|getLang
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|=
name|script
operator|.
name|getParams
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|params
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
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
name|params
operator|.
name|get
argument_list|(
literal|"param1"
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|upsertDoc
init|=
name|XContentHelper
operator|.
name|convertToMap
argument_list|(
name|request
operator|.
name|upsertRequest
argument_list|()
operator|.
name|source
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|v2
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|upsertDoc
operator|.
name|get
argument_list|(
literal|"field1"
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
operator|(
operator|(
name|Map
operator|)
name|upsertDoc
operator|.
name|get
argument_list|(
literal|"compound"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"field2"
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
name|request
operator|=
operator|new
name|UpdateRequest
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|request
operator|.
name|source
argument_list|(
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
literal|"upsert"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"compound"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"script"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"params"
argument_list|)
operator|.
name|field
argument_list|(
literal|"param1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"inline"
argument_list|,
literal|"script1"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
expr_stmt|;
name|script
operator|=
name|request
operator|.
name|script
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|script
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|script
operator|.
name|getScript
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"script1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|script
operator|.
name|getType
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|script
operator|.
name|getLang
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|=
name|script
operator|.
name|getParams
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|params
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|params
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
name|params
operator|.
name|get
argument_list|(
literal|"param1"
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
name|upsertDoc
operator|=
name|XContentHelper
operator|.
name|convertToMap
argument_list|(
name|request
operator|.
name|upsertRequest
argument_list|()
operator|.
name|source
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|v2
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|upsertDoc
operator|.
name|get
argument_list|(
literal|"field1"
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
operator|(
operator|(
name|Map
operator|)
name|upsertDoc
operator|.
name|get
argument_list|(
literal|"compound"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"field2"
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
comment|// script with doc
name|request
operator|=
operator|new
name|UpdateRequest
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|request
operator|.
name|source
argument_list|(
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
literal|"doc"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"compound"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"value2"
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
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|doc
init|=
name|request
operator|.
name|doc
argument_list|()
operator|.
name|sourceAsMap
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"field1"
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
operator|(
operator|(
name|Map
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"compound"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"field2"
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
comment|// Related to issue 3256
DECL|method|testUpdateRequestWithTTL
specifier|public
name|void
name|testUpdateRequestWithTTL
parameter_list|()
throws|throws
name|Exception
block|{
name|TimeValue
name|providedTTLValue
init|=
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|randomTimeValue
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"ttl"
argument_list|)
decl_stmt|;
name|Settings
name|settings
init|=
name|settings
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|UpdateHelper
name|updateHelper
init|=
operator|new
name|UpdateHelper
argument_list|(
name|settings
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// We just upsert one document with ttl
name|IndexRequest
name|indexRequest
init|=
operator|new
name|IndexRequest
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|source
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|ttl
argument_list|(
name|providedTTLValue
argument_list|)
decl_stmt|;
name|UpdateRequest
name|updateRequest
init|=
operator|new
name|UpdateRequest
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|doc
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"fooz"
argument_list|,
literal|"baz"
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|upsert
argument_list|(
name|indexRequest
argument_list|)
decl_stmt|;
comment|// We simulate that the document is not existing yet
name|GetResult
name|getResult
init|=
operator|new
name|GetResult
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|UpdateHelper
operator|.
name|Result
name|result
init|=
name|updateHelper
operator|.
name|prepare
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
literal|"_na_"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|updateRequest
argument_list|,
name|getResult
argument_list|)
decl_stmt|;
name|Streamable
name|action
init|=
name|result
operator|.
name|action
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|action
argument_list|,
name|instanceOf
argument_list|(
name|IndexRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|IndexRequest
name|indexAction
init|=
operator|(
name|IndexRequest
operator|)
name|action
decl_stmt|;
name|assertThat
argument_list|(
name|indexAction
operator|.
name|ttl
argument_list|()
argument_list|,
name|is
argument_list|(
name|providedTTLValue
argument_list|)
argument_list|)
expr_stmt|;
comment|// We just upsert one document with ttl using a script
name|indexRequest
operator|=
operator|new
name|IndexRequest
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|source
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|ttl
argument_list|(
name|providedTTLValue
argument_list|)
expr_stmt|;
name|updateRequest
operator|=
operator|new
name|UpdateRequest
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|upsert
argument_list|(
name|indexRequest
argument_list|)
operator|.
name|script
argument_list|(
operator|new
name|Script
argument_list|(
literal|";"
argument_list|)
argument_list|)
operator|.
name|scriptedUpsert
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// We simulate that the document is not existing yet
name|getResult
operator|=
operator|new
name|GetResult
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|result
operator|=
name|updateHelper
operator|.
name|prepare
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
literal|"_na_"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|updateRequest
argument_list|,
name|getResult
argument_list|)
expr_stmt|;
name|action
operator|=
name|result
operator|.
name|action
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|action
argument_list|,
name|instanceOf
argument_list|(
name|IndexRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|indexAction
operator|=
operator|(
name|IndexRequest
operator|)
name|action
expr_stmt|;
name|assertThat
argument_list|(
name|indexAction
operator|.
name|ttl
argument_list|()
argument_list|,
name|is
argument_list|(
name|providedTTLValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Related to issue #15822
DECL|method|testInvalidBodyThrowsParseException
specifier|public
name|void
name|testInvalidBodyThrowsParseException
parameter_list|()
throws|throws
name|Exception
block|{
name|UpdateRequest
name|request
init|=
operator|new
name|UpdateRequest
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
decl_stmt|;
try|try
block|{
name|request
operator|.
name|source
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|'"'
block|}
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown a ElasticsearchParseException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
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
name|equalTo
argument_list|(
literal|"Failed to derive xcontent"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Related to issue 15338
DECL|method|testFieldsParsing
specifier|public
name|void
name|testFieldsParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|UpdateRequest
name|request
init|=
operator|new
name|UpdateRequest
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|source
argument_list|(
operator|new
name|BytesArray
argument_list|(
literal|"{\"doc\": {\"field1\": \"value1\"}, \"fields\": \"_source\"}"
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|request
operator|.
name|doc
argument_list|()
operator|.
name|sourceAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"field1"
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
name|request
operator|.
name|fields
argument_list|()
argument_list|,
name|arrayContaining
argument_list|(
literal|"_source"
argument_list|)
argument_list|)
expr_stmt|;
name|request
operator|=
operator|new
name|UpdateRequest
argument_list|(
literal|"test"
argument_list|,
literal|"type2"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|source
argument_list|(
operator|new
name|BytesArray
argument_list|(
literal|"{\"doc\": {\"field2\": \"value2\"}, \"fields\": [\"field1\", \"field2\"]}"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|request
operator|.
name|doc
argument_list|()
operator|.
name|sourceAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"field2"
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
name|assertThat
argument_list|(
name|request
operator|.
name|fields
argument_list|()
argument_list|,
name|arrayContaining
argument_list|(
literal|"field1"
argument_list|,
literal|"field2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

