begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
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
name|ParseField
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
name|StreamOutput
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
name|NamedXContentRegistry
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
name|ObjectParser
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
name|rest
operator|.
name|action
operator|.
name|search
operator|.
name|RestSearchAction
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
name|Collections
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
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertToXContentEquivalent
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
name|instanceOf
import|;
end_import

begin_class
DECL|class|ParsedAggregationTests
specifier|public
class|class
name|ParsedAggregationTests
extends|extends
name|ESTestCase
block|{
comment|//TODO maybe this test will no longer be needed once we have real tests for ParsedAggregation subclasses
DECL|method|testParse
specifier|public
name|void
name|testParse
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|name
init|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|meta
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|int
name|numMetas
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|meta
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|numMetas
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numMetas
condition|;
name|i
operator|++
control|)
block|{
name|meta
operator|.
name|put
argument_list|(
name|randomAlphaOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomAlphaOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|TestInternalAggregation
name|testAgg
init|=
operator|new
name|TestInternalAggregation
argument_list|(
name|name
argument_list|,
name|meta
argument_list|)
decl_stmt|;
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
name|ToXContent
operator|.
name|MapParams
name|params
init|=
operator|new
name|ToXContent
operator|.
name|MapParams
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
name|RestSearchAction
operator|.
name|TYPED_KEYS_PARAM
argument_list|,
literal|"true"
argument_list|)
argument_list|)
decl_stmt|;
name|BytesReference
name|bytesAgg
init|=
name|XContentHelper
operator|.
name|toXContent
argument_list|(
name|testAgg
argument_list|,
name|xContentType
argument_list|,
name|params
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|xContentType
operator|.
name|xContent
argument_list|()
argument_list|,
name|bytesAgg
argument_list|)
init|)
block|{
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
assert|assert
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
assert|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
assert|assert
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
assert|;
name|String
name|currentName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|currentName
operator|.
name|indexOf
argument_list|(
name|InternalAggregation
operator|.
name|TYPED_KEYS_DELIMITER
argument_list|)
decl_stmt|;
name|String
name|aggType
init|=
name|currentName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|String
name|aggName
init|=
name|currentName
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|Aggregation
name|parsedAgg
init|=
name|parser
operator|.
name|namedObject
argument_list|(
name|Aggregation
operator|.
name|class
argument_list|,
name|aggType
argument_list|,
name|aggName
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|parsedAgg
argument_list|,
name|instanceOf
argument_list|(
name|TestParsedAggregation
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testAgg
operator|.
name|getName
argument_list|()
argument_list|,
name|parsedAgg
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testAgg
operator|.
name|getMetaData
argument_list|()
argument_list|,
name|parsedAgg
operator|.
name|getMetaData
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|meta
operator|!=
literal|null
condition|)
block|{
name|expectThrows
argument_list|(
name|UnsupportedOperationException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|parsedAgg
operator|.
name|getMetaData
argument_list|()
operator|.
name|put
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BytesReference
name|finalAgg
init|=
name|XContentHelper
operator|.
name|toXContent
argument_list|(
operator|(
name|ToXContent
operator|)
name|parsedAgg
argument_list|,
name|xContentType
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
decl_stmt|;
name|assertToXContentEquivalent
argument_list|(
name|bytesAgg
argument_list|,
name|finalAgg
argument_list|,
name|xContentType
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|xContentRegistry
specifier|protected
name|NamedXContentRegistry
name|xContentRegistry
parameter_list|()
block|{
name|NamedXContentRegistry
operator|.
name|Entry
name|entry
init|=
operator|new
name|NamedXContentRegistry
operator|.
name|Entry
argument_list|(
name|Aggregation
operator|.
name|class
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"type"
argument_list|)
argument_list|,
parameter_list|(
name|parser
parameter_list|,
name|name
parameter_list|)
lambda|->
name|TestParsedAggregation
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|,
operator|(
name|String
operator|)
name|name
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|NamedXContentRegistry
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|entry
argument_list|)
argument_list|)
return|;
block|}
DECL|class|TestParsedAggregation
specifier|private
specifier|static
class|class
name|TestParsedAggregation
extends|extends
name|ParsedAggregation
block|{
DECL|field|PARSER
specifier|private
specifier|static
name|ObjectParser
argument_list|<
name|TestParsedAggregation
argument_list|,
name|Void
argument_list|>
name|PARSER
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
literal|"testAggParser"
argument_list|,
name|TestParsedAggregation
operator|::
operator|new
argument_list|)
decl_stmt|;
static|static
block|{
name|ParsedAggregation
operator|.
name|declareAggregationFields
argument_list|(
name|PARSER
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
literal|"type"
return|;
block|}
annotation|@
name|Override
DECL|method|doXContentBody
specifier|protected
name|XContentBuilder
name|doXContentBody
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|builder
return|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|TestParsedAggregation
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|TestParsedAggregation
name|parsedAgg
init|=
name|PARSER
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|parsedAgg
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|parsedAgg
return|;
block|}
block|}
DECL|class|TestInternalAggregation
specifier|private
specifier|static
class|class
name|TestInternalAggregation
extends|extends
name|InternalAggregation
block|{
DECL|method|TestInternalAggregation
specifier|private
name|TestInternalAggregation
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
literal|"type"
return|;
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|protected
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|doReduce
specifier|public
name|InternalAggregation
name|doReduce
parameter_list|(
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggregations
parameter_list|,
name|ReduceContext
name|reduceContext
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getProperty
specifier|public
name|Object
name|getProperty
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|doXContentBody
specifier|public
name|XContentBuilder
name|doXContentBody
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|builder
return|;
block|}
block|}
block|}
end_class

end_unit

