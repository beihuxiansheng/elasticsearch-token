begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.builder
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|builder
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
name|common
operator|.
name|ParseFieldMatcher
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
name|ParsingException
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
name|BytesReference
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
name|BytesStreamOutput
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
name|NamedWriteableAwareStreamInput
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
name|StreamInput
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
name|ToXContent
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
name|XContentParser
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
name|common
operator|.
name|xcontent
operator|.
name|json
operator|.
name|JsonXContent
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
name|query
operator|.
name|QueryBuilders
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
name|query
operator|.
name|QueryParseContext
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
name|query
operator|.
name|RandomQueryBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|AbstractSearchTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|rescore
operator|.
name|QueryRescorerBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
operator|.
name|FieldSortBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
operator|.
name|ScoreSortBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
operator|.
name|SortOrder
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
name|EqualsHashCodeTestUtils
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
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
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
name|hasToString
import|;
end_import

begin_class
DECL|class|SearchSourceBuilderTests
specifier|public
class|class
name|SearchSourceBuilderTests
extends|extends
name|AbstractSearchTestCase
block|{
DECL|method|testFromXContent
specifier|public
name|void
name|testFromXContent
parameter_list|()
throws|throws
name|IOException
block|{
name|SearchSourceBuilder
name|testSearchSourceBuilder
init|=
name|createSearchSourceBuilder
argument_list|()
decl_stmt|;
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builder
operator|.
name|prettyPrint
argument_list|()
expr_stmt|;
block|}
name|testSearchSourceBuilder
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|assertParseSearchSource
argument_list|(
name|testSearchSourceBuilder
argument_list|,
name|createParser
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertParseSearchSource
specifier|private
name|void
name|assertParseSearchSource
parameter_list|(
name|SearchSourceBuilder
name|testBuilder
parameter_list|,
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|assertParseSearchSource
argument_list|(
name|testBuilder
argument_list|,
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
expr_stmt|;
block|}
DECL|method|assertParseSearchSource
specifier|private
name|void
name|assertParseSearchSource
parameter_list|(
name|SearchSourceBuilder
name|testBuilder
parameter_list|,
name|XContentParser
name|parser
parameter_list|,
name|ParseFieldMatcher
name|pfm
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryParseContext
name|parseContext
init|=
operator|new
name|QueryParseContext
argument_list|(
name|parser
argument_list|,
name|pfm
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
comment|// sometimes we move it on the START_OBJECT to
comment|// test the embedded case
block|}
name|SearchSourceBuilder
name|newBuilder
init|=
name|SearchSourceBuilder
operator|.
name|fromXContent
argument_list|(
name|parseContext
argument_list|,
name|searchRequestParsers
operator|.
name|aggParsers
argument_list|,
name|searchRequestParsers
operator|.
name|suggesters
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testBuilder
argument_list|,
name|newBuilder
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testBuilder
operator|.
name|hashCode
argument_list|()
argument_list|,
name|newBuilder
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createParseContext
specifier|private
name|QueryParseContext
name|createParseContext
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
block|{
return|return
operator|new
name|QueryParseContext
argument_list|(
name|parser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
return|;
block|}
DECL|method|testSerialization
specifier|public
name|void
name|testSerialization
parameter_list|()
throws|throws
name|IOException
block|{
name|SearchSourceBuilder
name|testBuilder
init|=
name|createSearchSourceBuilder
argument_list|()
decl_stmt|;
try|try
init|(
name|BytesStreamOutput
name|output
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|testBuilder
operator|.
name|writeTo
argument_list|(
name|output
argument_list|)
expr_stmt|;
try|try
init|(
name|StreamInput
name|in
init|=
operator|new
name|NamedWriteableAwareStreamInput
argument_list|(
name|output
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
argument_list|,
name|namedWriteableRegistry
argument_list|)
init|)
block|{
name|SearchSourceBuilder
name|deserializedBuilder
init|=
operator|new
name|SearchSourceBuilder
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|deserializedBuilder
argument_list|,
name|testBuilder
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|deserializedBuilder
operator|.
name|hashCode
argument_list|()
argument_list|,
name|testBuilder
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|deserializedBuilder
argument_list|,
name|testBuilder
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testEqualsAndHashcode
specifier|public
name|void
name|testEqualsAndHashcode
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO add test checking that changing any member of this class produces an object that is not equal to the original
name|EqualsHashCodeTestUtils
operator|.
name|checkEqualsAndHashCode
argument_list|(
name|createSearchSourceBuilder
argument_list|()
argument_list|,
name|this
operator|::
name|copyBuilder
argument_list|)
expr_stmt|;
block|}
comment|//we use the streaming infra to create a copy of the builder provided as argument
DECL|method|copyBuilder
specifier|private
name|SearchSourceBuilder
name|copyBuilder
parameter_list|(
name|SearchSourceBuilder
name|original
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ESTestCase
operator|.
name|copyWriteable
argument_list|(
name|original
argument_list|,
name|namedWriteableRegistry
argument_list|,
name|SearchSourceBuilder
operator|::
operator|new
argument_list|)
return|;
block|}
DECL|method|testParseIncludeExclude
specifier|public
name|void
name|testParseIncludeExclude
parameter_list|()
throws|throws
name|IOException
block|{
block|{
name|String
name|restContent
init|=
literal|" { \"_source\": { \"includes\": \"include\", \"excludes\": \"*.field2\"}}"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|restContent
argument_list|)
init|)
block|{
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
name|SearchSourceBuilder
operator|.
name|fromXContent
argument_list|(
name|createParseContext
argument_list|(
name|parser
argument_list|)
argument_list|,
name|searchRequestParsers
operator|.
name|aggParsers
argument_list|,
name|searchRequestParsers
operator|.
name|suggesters
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"*.field2"
block|}
argument_list|,
name|searchSourceBuilder
operator|.
name|fetchSource
argument_list|()
operator|.
name|excludes
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"include"
block|}
argument_list|,
name|searchSourceBuilder
operator|.
name|fetchSource
argument_list|()
operator|.
name|includes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|{
name|String
name|restContent
init|=
literal|" { \"_source\": false}"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|restContent
argument_list|)
init|)
block|{
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
name|SearchSourceBuilder
operator|.
name|fromXContent
argument_list|(
name|createParseContext
argument_list|(
name|parser
argument_list|)
argument_list|,
name|searchRequestParsers
operator|.
name|aggParsers
argument_list|,
name|searchRequestParsers
operator|.
name|suggesters
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|,
name|searchSourceBuilder
operator|.
name|fetchSource
argument_list|()
operator|.
name|excludes
argument_list|()
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|,
name|searchSourceBuilder
operator|.
name|fetchSource
argument_list|()
operator|.
name|includes
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|searchSourceBuilder
operator|.
name|fetchSource
argument_list|()
operator|.
name|fetchSource
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testMultipleQueryObjectsAreRejected
specifier|public
name|void
name|testMultipleQueryObjectsAreRejected
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|restContent
init|=
literal|" { \"query\": {\n"
operator|+
literal|"    \"multi_match\": {\n"
operator|+
literal|"      \"query\": \"workd\",\n"
operator|+
literal|"      \"fields\": [\"title^5\", \"plain_body\"]\n"
operator|+
literal|"    },\n"
operator|+
literal|"    \"filters\": {\n"
operator|+
literal|"      \"terms\": {\n"
operator|+
literal|"        \"status\": [ 3 ]\n"
operator|+
literal|"      }\n"
operator|+
literal|"    }\n"
operator|+
literal|"  } }"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|restContent
argument_list|)
init|)
block|{
name|ParsingException
name|e
init|=
name|expectThrows
argument_list|(
name|ParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|SearchSourceBuilder
operator|.
name|fromXContent
argument_list|(
name|createParseContext
argument_list|(
name|parser
argument_list|)
argument_list|,
name|searchRequestParsers
operator|.
name|aggParsers
argument_list|,
name|searchRequestParsers
operator|.
name|suggesters
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[multi_match] malformed query, expected [END_OBJECT] but found [FIELD_NAME]"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParseSort
specifier|public
name|void
name|testParseSort
parameter_list|()
throws|throws
name|IOException
block|{
block|{
name|String
name|restContent
init|=
literal|" { \"sort\": \"foo\"}"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|restContent
argument_list|)
init|)
block|{
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
name|SearchSourceBuilder
operator|.
name|fromXContent
argument_list|(
name|createParseContext
argument_list|(
name|parser
argument_list|)
argument_list|,
name|searchRequestParsers
operator|.
name|aggParsers
argument_list|,
name|searchRequestParsers
operator|.
name|suggesters
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searchSourceBuilder
operator|.
name|sorts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|FieldSortBuilder
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|searchSourceBuilder
operator|.
name|sorts
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|{
name|String
name|restContent
init|=
literal|"{\"sort\" : [\n"
operator|+
literal|"        { \"post_date\" : {\"order\" : \"asc\"}},\n"
operator|+
literal|"        \"user\",\n"
operator|+
literal|"        { \"name\" : \"desc\" },\n"
operator|+
literal|"        { \"age\" : \"desc\" },\n"
operator|+
literal|"        \"_score\"\n"
operator|+
literal|"    ]}"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|restContent
argument_list|)
init|)
block|{
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
name|SearchSourceBuilder
operator|.
name|fromXContent
argument_list|(
name|createParseContext
argument_list|(
name|parser
argument_list|)
argument_list|,
name|searchRequestParsers
operator|.
name|aggParsers
argument_list|,
name|searchRequestParsers
operator|.
name|suggesters
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|searchSourceBuilder
operator|.
name|sorts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|FieldSortBuilder
argument_list|(
literal|"post_date"
argument_list|)
argument_list|,
name|searchSourceBuilder
operator|.
name|sorts
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|FieldSortBuilder
argument_list|(
literal|"user"
argument_list|)
argument_list|,
name|searchSourceBuilder
operator|.
name|sorts
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|FieldSortBuilder
argument_list|(
literal|"name"
argument_list|)
operator|.
name|order
argument_list|(
name|SortOrder
operator|.
name|DESC
argument_list|)
argument_list|,
name|searchSourceBuilder
operator|.
name|sorts
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|FieldSortBuilder
argument_list|(
literal|"age"
argument_list|)
operator|.
name|order
argument_list|(
name|SortOrder
operator|.
name|DESC
argument_list|)
argument_list|,
name|searchSourceBuilder
operator|.
name|sorts
argument_list|()
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|ScoreSortBuilder
argument_list|()
argument_list|,
name|searchSourceBuilder
operator|.
name|sorts
argument_list|()
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testAggsParsing
specifier|public
name|void
name|testAggsParsing
parameter_list|()
throws|throws
name|IOException
block|{
block|{
name|String
name|restContent
init|=
literal|"{\n"
operator|+
literal|"    "
operator|+
literal|"\"aggs\": {"
operator|+
literal|"        \"test_agg\": {\n"
operator|+
literal|"            "
operator|+
literal|"\"terms\" : {\n"
operator|+
literal|"                \"field\": \"foo\"\n"
operator|+
literal|"            }\n"
operator|+
literal|"        }\n"
operator|+
literal|"    }\n"
operator|+
literal|"}\n"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|restContent
argument_list|)
init|)
block|{
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
name|SearchSourceBuilder
operator|.
name|fromXContent
argument_list|(
name|createParseContext
argument_list|(
name|parser
argument_list|)
argument_list|,
name|searchRequestParsers
operator|.
name|aggParsers
argument_list|,
name|searchRequestParsers
operator|.
name|suggesters
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searchSourceBuilder
operator|.
name|aggregations
argument_list|()
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|{
name|String
name|restContent
init|=
literal|"{\n"
operator|+
literal|"    \"aggregations\": {"
operator|+
literal|"        \"test_agg\": {\n"
operator|+
literal|"            \"terms\" : {\n"
operator|+
literal|"                \"field\": \"foo\"\n"
operator|+
literal|"            }\n"
operator|+
literal|"        }\n"
operator|+
literal|"    }\n"
operator|+
literal|"}\n"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|restContent
argument_list|)
init|)
block|{
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
name|SearchSourceBuilder
operator|.
name|fromXContent
argument_list|(
name|createParseContext
argument_list|(
name|parser
argument_list|)
argument_list|,
name|searchRequestParsers
operator|.
name|aggParsers
argument_list|,
name|searchRequestParsers
operator|.
name|suggesters
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searchSourceBuilder
operator|.
name|aggregations
argument_list|()
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * test that we can parse the `rescore` element either as single object or as array      */
DECL|method|testParseRescore
specifier|public
name|void
name|testParseRescore
parameter_list|()
throws|throws
name|IOException
block|{
block|{
name|String
name|restContent
init|=
literal|"{\n"
operator|+
literal|"    \"query\" : {\n"
operator|+
literal|"        \"match\": { \"content\": { \"query\": \"foo bar\" }}\n"
operator|+
literal|"     },\n"
operator|+
literal|"    \"rescore\": {"
operator|+
literal|"        \"window_size\": 50,\n"
operator|+
literal|"        \"query\": {\n"
operator|+
literal|"            \"rescore_query\" : {\n"
operator|+
literal|"                \"match\": { \"content\": { \"query\": \"baz\" } }\n"
operator|+
literal|"            }\n"
operator|+
literal|"        }\n"
operator|+
literal|"    }\n"
operator|+
literal|"}\n"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|restContent
argument_list|)
init|)
block|{
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
name|SearchSourceBuilder
operator|.
name|fromXContent
argument_list|(
name|createParseContext
argument_list|(
name|parser
argument_list|)
argument_list|,
name|searchRequestParsers
operator|.
name|aggParsers
argument_list|,
name|searchRequestParsers
operator|.
name|suggesters
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searchSourceBuilder
operator|.
name|rescores
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|QueryRescorerBuilder
argument_list|(
name|QueryBuilders
operator|.
name|matchQuery
argument_list|(
literal|"content"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
operator|.
name|windowSize
argument_list|(
literal|50
argument_list|)
argument_list|,
name|searchSourceBuilder
operator|.
name|rescores
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|{
name|String
name|restContent
init|=
literal|"{\n"
operator|+
literal|"    \"query\" : {\n"
operator|+
literal|"        \"match\": { \"content\": { \"query\": \"foo bar\" }}\n"
operator|+
literal|"     },\n"
operator|+
literal|"    \"rescore\": [ {"
operator|+
literal|"        \"window_size\": 50,\n"
operator|+
literal|"        \"query\": {\n"
operator|+
literal|"            \"rescore_query\" : {\n"
operator|+
literal|"                \"match\": { \"content\": { \"query\": \"baz\" } }\n"
operator|+
literal|"            }\n"
operator|+
literal|"        }\n"
operator|+
literal|"    } ]\n"
operator|+
literal|"}\n"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|restContent
argument_list|)
init|)
block|{
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
name|SearchSourceBuilder
operator|.
name|fromXContent
argument_list|(
name|createParseContext
argument_list|(
name|parser
argument_list|)
argument_list|,
name|searchRequestParsers
operator|.
name|aggParsers
argument_list|,
name|searchRequestParsers
operator|.
name|suggesters
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searchSourceBuilder
operator|.
name|rescores
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|QueryRescorerBuilder
argument_list|(
name|QueryBuilders
operator|.
name|matchQuery
argument_list|(
literal|"content"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
operator|.
name|windowSize
argument_list|(
literal|50
argument_list|)
argument_list|,
name|searchSourceBuilder
operator|.
name|rescores
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testTimeoutWithUnits
specifier|public
name|void
name|testTimeoutWithUnits
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|timeout
init|=
name|randomTimeValue
argument_list|()
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"{ \"query\": { \"match_all\": {}}, \"timeout\": \""
operator|+
name|timeout
operator|+
literal|"\"}"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|query
argument_list|)
init|)
block|{
specifier|final
name|SearchSourceBuilder
name|builder
init|=
name|SearchSourceBuilder
operator|.
name|fromXContent
argument_list|(
name|createParseContext
argument_list|(
name|parser
argument_list|)
argument_list|,
name|searchRequestParsers
operator|.
name|aggParsers
argument_list|,
name|searchRequestParsers
operator|.
name|suggesters
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|builder
operator|.
name|timeout
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|timeout
argument_list|,
literal|null
argument_list|,
literal|"timeout"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testTimeoutWithoutUnits
specifier|public
name|void
name|testTimeoutWithoutUnits
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|timeout
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"{ \"query\": { \"match_all\": {}}, \"timeout\": \""
operator|+
name|timeout
operator|+
literal|"\"}"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|query
argument_list|)
init|)
block|{
specifier|final
name|ElasticsearchParseException
name|e
init|=
name|expectThrows
argument_list|(
name|ElasticsearchParseException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|SearchSourceBuilder
operator|.
name|fromXContent
argument_list|(
name|createParseContext
argument_list|(
name|parser
argument_list|)
argument_list|,
name|searchRequestParsers
operator|.
name|aggParsers
argument_list|,
name|searchRequestParsers
operator|.
name|suggesters
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
argument_list|,
name|hasToString
argument_list|(
name|containsString
argument_list|(
literal|"unit is missing or unrecognized"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testToXContent
specifier|public
name|void
name|testToXContent
parameter_list|()
throws|throws
name|IOException
block|{
comment|//verify that only what is set gets printed out through toXContent
name|XContentType
name|xContentType
init|=
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
block|{
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
operator|new
name|SearchSourceBuilder
argument_list|()
decl_stmt|;
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|xContentType
argument_list|)
decl_stmt|;
name|searchSourceBuilder
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|BytesReference
name|source
init|=
name|builder
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sourceAsMap
init|=
name|XContentHelper
operator|.
name|convertToMap
argument_list|(
name|source
argument_list|,
literal|false
argument_list|)
operator|.
name|v2
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|sourceAsMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
operator|new
name|SearchSourceBuilder
argument_list|()
decl_stmt|;
name|searchSourceBuilder
operator|.
name|query
argument_list|(
name|RandomQueryBuilder
operator|.
name|createQuery
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|xContentType
argument_list|)
decl_stmt|;
name|searchSourceBuilder
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|BytesReference
name|source
init|=
name|builder
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sourceAsMap
init|=
name|XContentHelper
operator|.
name|convertToMap
argument_list|(
name|source
argument_list|,
literal|false
argument_list|)
operator|.
name|v2
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|sourceAsMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"query"
argument_list|,
name|sourceAsMap
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParseIndicesBoost
specifier|public
name|void
name|testParseIndicesBoost
parameter_list|()
throws|throws
name|IOException
block|{
block|{
name|String
name|restContent
init|=
literal|" { \"indices_boost\": {\"foo\": 1.0, \"bar\": 2.0}}"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|restContent
argument_list|)
init|)
block|{
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
name|SearchSourceBuilder
operator|.
name|fromXContent
argument_list|(
name|createParseContext
argument_list|(
name|parser
argument_list|)
argument_list|,
name|searchRequestParsers
operator|.
name|aggParsers
argument_list|,
name|searchRequestParsers
operator|.
name|suggesters
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|searchSourceBuilder
operator|.
name|indexBoosts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|SearchSourceBuilder
operator|.
name|IndexBoost
argument_list|(
literal|"foo"
argument_list|,
literal|1.0f
argument_list|)
argument_list|,
name|searchSourceBuilder
operator|.
name|indexBoosts
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|SearchSourceBuilder
operator|.
name|IndexBoost
argument_list|(
literal|"bar"
argument_list|,
literal|2.0f
argument_list|)
argument_list|,
name|searchSourceBuilder
operator|.
name|indexBoosts
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertWarnings
argument_list|(
literal|"Object format in indices_boost is deprecated, please use array format instead"
argument_list|)
expr_stmt|;
block|}
block|}
block|{
name|String
name|restContent
init|=
literal|"{"
operator|+
literal|"    \"indices_boost\" : [\n"
operator|+
literal|"        { \"foo\" : 1.0 },\n"
operator|+
literal|"        { \"bar\" : 2.0 },\n"
operator|+
literal|"        { \"baz\" : 3.0 }\n"
operator|+
literal|"    ]}"
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|restContent
argument_list|)
init|)
block|{
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
name|SearchSourceBuilder
operator|.
name|fromXContent
argument_list|(
name|createParseContext
argument_list|(
name|parser
argument_list|)
argument_list|,
name|searchRequestParsers
operator|.
name|aggParsers
argument_list|,
name|searchRequestParsers
operator|.
name|suggesters
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|searchSourceBuilder
operator|.
name|indexBoosts
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|SearchSourceBuilder
operator|.
name|IndexBoost
argument_list|(
literal|"foo"
argument_list|,
literal|1.0f
argument_list|)
argument_list|,
name|searchSourceBuilder
operator|.
name|indexBoosts
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|SearchSourceBuilder
operator|.
name|IndexBoost
argument_list|(
literal|"bar"
argument_list|,
literal|2.0f
argument_list|)
argument_list|,
name|searchSourceBuilder
operator|.
name|indexBoosts
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|SearchSourceBuilder
operator|.
name|IndexBoost
argument_list|(
literal|"baz"
argument_list|,
literal|3.0f
argument_list|)
argument_list|,
name|searchSourceBuilder
operator|.
name|indexBoosts
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|{
name|String
name|restContent
init|=
literal|"{"
operator|+
literal|"    \"indices_boost\" : [\n"
operator|+
literal|"        { \"foo\" : 1.0, \"bar\": 2.0}\n"
operator|+
comment|// invalid format
literal|"    ]}"
decl_stmt|;
name|assertIndicesBoostParseErrorMessage
argument_list|(
name|restContent
argument_list|,
literal|"Expected [END_OBJECT] in [indices_boost] but found [FIELD_NAME]"
argument_list|)
expr_stmt|;
block|}
block|{
name|String
name|restContent
init|=
literal|"{"
operator|+
literal|"    \"indices_boost\" : [\n"
operator|+
literal|"        {}\n"
operator|+
comment|// invalid format
literal|"    ]}"
decl_stmt|;
name|assertIndicesBoostParseErrorMessage
argument_list|(
name|restContent
argument_list|,
literal|"Expected [FIELD_NAME] in [indices_boost] but found [END_OBJECT]"
argument_list|)
expr_stmt|;
block|}
block|{
name|String
name|restContent
init|=
literal|"{"
operator|+
literal|"    \"indices_boost\" : [\n"
operator|+
literal|"        { \"foo\" : \"bar\"}\n"
operator|+
comment|// invalid format
literal|"    ]}"
decl_stmt|;
name|assertIndicesBoostParseErrorMessage
argument_list|(
name|restContent
argument_list|,
literal|"Expected [VALUE_NUMBER] in [indices_boost] but found [VALUE_STRING]"
argument_list|)
expr_stmt|;
block|}
block|{
name|String
name|restContent
init|=
literal|"{"
operator|+
literal|"    \"indices_boost\" : [\n"
operator|+
literal|"        { \"foo\" : {\"bar\": 1}}\n"
operator|+
comment|// invalid format
literal|"    ]}"
decl_stmt|;
name|assertIndicesBoostParseErrorMessage
argument_list|(
name|restContent
argument_list|,
literal|"Expected [VALUE_NUMBER] in [indices_boost] but found [START_OBJECT]"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertIndicesBoostParseErrorMessage
specifier|private
name|void
name|assertIndicesBoostParseErrorMessage
parameter_list|(
name|String
name|restContent
parameter_list|,
name|String
name|expectedErrorMessage
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|JsonXContent
operator|.
name|jsonXContent
argument_list|,
name|restContent
argument_list|)
init|)
block|{
name|ParsingException
name|e
init|=
name|expectThrows
argument_list|(
name|ParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|SearchSourceBuilder
operator|.
name|fromXContent
argument_list|(
name|createParseContext
argument_list|(
name|parser
argument_list|)
argument_list|,
name|searchRequestParsers
operator|.
name|aggParsers
argument_list|,
name|searchRequestParsers
operator|.
name|suggesters
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedErrorMessage
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

