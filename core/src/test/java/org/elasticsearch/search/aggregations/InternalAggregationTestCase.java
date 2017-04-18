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
name|NamedWriteableRegistry
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
name|util
operator|.
name|MockBigArrays
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
name|ContextParser
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
name|indices
operator|.
name|breaker
operator|.
name|NoneCircuitBreakerService
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
name|script
operator|.
name|ScriptService
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
name|SearchModule
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
name|aggregations
operator|.
name|metrics
operator|.
name|cardinality
operator|.
name|CardinalityAggregationBuilder
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
name|aggregations
operator|.
name|metrics
operator|.
name|cardinality
operator|.
name|ParsedCardinality
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
name|aggregations
operator|.
name|metrics
operator|.
name|max
operator|.
name|MaxAggregationBuilder
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
name|aggregations
operator|.
name|metrics
operator|.
name|max
operator|.
name|ParsedMax
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
name|aggregations
operator|.
name|metrics
operator|.
name|min
operator|.
name|MinAggregationBuilder
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
name|aggregations
operator|.
name|metrics
operator|.
name|min
operator|.
name|ParsedMin
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
name|aggregations
operator|.
name|metrics
operator|.
name|percentiles
operator|.
name|hdr
operator|.
name|InternalHDRPercentileRanks
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
name|aggregations
operator|.
name|metrics
operator|.
name|percentiles
operator|.
name|hdr
operator|.
name|ParsedHDRPercentileRanks
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
name|aggregations
operator|.
name|metrics
operator|.
name|percentiles
operator|.
name|tdigest
operator|.
name|InternalTDigestPercentileRanks
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
name|aggregations
operator|.
name|metrics
operator|.
name|percentiles
operator|.
name|tdigest
operator|.
name|ParsedTDigestPercentileRanks
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
name|aggregations
operator|.
name|pipeline
operator|.
name|PipelineAggregator
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
name|AbstractWireSerializingTestCase
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
name|ArrayList
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
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|emptyList
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
name|XContentHelper
operator|.
name|toXContent
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

begin_class
DECL|class|InternalAggregationTestCase
specifier|public
specifier|abstract
class|class
name|InternalAggregationTestCase
parameter_list|<
name|T
extends|extends
name|InternalAggregation
parameter_list|>
extends|extends
name|AbstractWireSerializingTestCase
argument_list|<
name|T
argument_list|>
block|{
DECL|field|namedWriteableRegistry
specifier|private
specifier|final
name|NamedWriteableRegistry
name|namedWriteableRegistry
init|=
operator|new
name|NamedWriteableRegistry
argument_list|(
operator|new
name|SearchModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|false
argument_list|,
name|emptyList
argument_list|()
argument_list|)
operator|.
name|getNamedWriteables
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|namedXContentRegistry
specifier|private
specifier|final
name|NamedXContentRegistry
name|namedXContentRegistry
init|=
operator|new
name|NamedXContentRegistry
argument_list|(
name|getNamedXContents
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|getNamedXContents
specifier|static
name|List
argument_list|<
name|NamedXContentRegistry
operator|.
name|Entry
argument_list|>
name|getNamedXContents
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ContextParser
argument_list|<
name|Object
argument_list|,
name|?
extends|extends
name|Aggregation
argument_list|>
argument_list|>
name|namedXContents
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|namedXContents
operator|.
name|put
argument_list|(
name|CardinalityAggregationBuilder
operator|.
name|NAME
argument_list|,
parameter_list|(
name|p
parameter_list|,
name|c
parameter_list|)
lambda|->
name|ParsedCardinality
operator|.
name|fromXContent
argument_list|(
name|p
argument_list|,
operator|(
name|String
operator|)
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|namedXContents
operator|.
name|put
argument_list|(
name|InternalHDRPercentileRanks
operator|.
name|NAME
argument_list|,
parameter_list|(
name|p
parameter_list|,
name|c
parameter_list|)
lambda|->
name|ParsedHDRPercentileRanks
operator|.
name|fromXContent
argument_list|(
name|p
argument_list|,
operator|(
name|String
operator|)
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|namedXContents
operator|.
name|put
argument_list|(
name|InternalTDigestPercentileRanks
operator|.
name|NAME
argument_list|,
parameter_list|(
name|p
parameter_list|,
name|c
parameter_list|)
lambda|->
name|ParsedTDigestPercentileRanks
operator|.
name|fromXContent
argument_list|(
name|p
argument_list|,
operator|(
name|String
operator|)
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|namedXContents
operator|.
name|put
argument_list|(
name|MinAggregationBuilder
operator|.
name|NAME
argument_list|,
parameter_list|(
name|p
parameter_list|,
name|c
parameter_list|)
lambda|->
name|ParsedMin
operator|.
name|fromXContent
argument_list|(
name|p
argument_list|,
operator|(
name|String
operator|)
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|namedXContents
operator|.
name|put
argument_list|(
name|MaxAggregationBuilder
operator|.
name|NAME
argument_list|,
parameter_list|(
name|p
parameter_list|,
name|c
parameter_list|)
lambda|->
name|ParsedMax
operator|.
name|fromXContent
argument_list|(
name|p
argument_list|,
operator|(
name|String
operator|)
name|c
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|namedXContents
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|entry
lambda|->
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
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createTestInstance
specifier|protected
specifier|abstract
name|T
name|createTestInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
function_decl|;
comment|/** Return an instance on an unmapped field. */
DECL|method|createUnmappedInstance
specifier|protected
name|T
name|createUnmappedInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
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
comment|// For most impls, we use the same instance in the unmapped case and in the mapped case
return|return
name|createTestInstance
argument_list|(
name|name
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
return|;
block|}
DECL|method|testReduceRandom
specifier|public
name|void
name|testReduceRandom
parameter_list|()
block|{
name|String
name|name
init|=
name|randomAlphaOfLength
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|T
argument_list|>
name|inputs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|toReduce
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|toReduceSize
init|=
name|between
argument_list|(
literal|1
argument_list|,
literal|200
argument_list|)
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
name|toReduceSize
condition|;
name|i
operator|++
control|)
block|{
name|T
name|t
init|=
name|randomBoolean
argument_list|()
condition|?
name|createUnmappedInstance
argument_list|(
name|name
argument_list|)
else|:
name|createTestInstance
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|inputs
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|toReduce
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|ScriptService
name|mockScriptService
init|=
name|mockScriptService
argument_list|()
decl_stmt|;
name|MockBigArrays
name|bigArrays
init|=
operator|new
name|MockBigArrays
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|NoneCircuitBreakerService
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
operator|&&
name|toReduce
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
comment|// sometimes do an incremental reduce
name|Collections
operator|.
name|shuffle
argument_list|(
name|toReduce
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|r
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|toReduceSize
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|internalAggregations
init|=
name|toReduce
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|r
argument_list|)
decl_stmt|;
name|InternalAggregation
operator|.
name|ReduceContext
name|context
init|=
operator|new
name|InternalAggregation
operator|.
name|ReduceContext
argument_list|(
name|bigArrays
argument_list|,
name|mockScriptService
argument_list|,
literal|false
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|T
name|reduced
init|=
operator|(
name|T
operator|)
name|inputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reduce
argument_list|(
name|internalAggregations
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|toReduce
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|toReduce
operator|.
name|subList
argument_list|(
name|r
argument_list|,
name|toReduceSize
argument_list|)
argument_list|)
expr_stmt|;
name|toReduce
operator|.
name|add
argument_list|(
name|reduced
argument_list|)
expr_stmt|;
block|}
name|InternalAggregation
operator|.
name|ReduceContext
name|context
init|=
operator|new
name|InternalAggregation
operator|.
name|ReduceContext
argument_list|(
name|bigArrays
argument_list|,
name|mockScriptService
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|T
name|reduced
init|=
operator|(
name|T
operator|)
name|inputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reduce
argument_list|(
name|toReduce
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|assertReduced
argument_list|(
name|reduced
argument_list|,
name|inputs
argument_list|)
expr_stmt|;
block|}
comment|/**      * overwrite in tests that need it      */
DECL|method|mockScriptService
specifier|protected
name|ScriptService
name|mockScriptService
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|assertReduced
specifier|protected
specifier|abstract
name|void
name|assertReduced
parameter_list|(
name|T
name|reduced
parameter_list|,
name|List
argument_list|<
name|T
argument_list|>
name|inputs
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|createTestInstance
specifier|protected
specifier|final
name|T
name|createTestInstance
parameter_list|()
block|{
return|return
name|createTestInstance
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|5
argument_list|)
argument_list|)
return|;
block|}
DECL|method|createTestInstance
specifier|private
name|T
name|createTestInstance
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// TODO populate pipelineAggregators
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|metaData
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|int
name|metaDataCount
init|=
name|between
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
while|while
condition|(
name|metaData
operator|.
name|size
argument_list|()
operator|<
name|metaDataCount
condition|)
block|{
name|metaData
operator|.
name|put
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|5
argument_list|)
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|createTestInstance
argument_list|(
name|name
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
return|;
block|}
comment|/** Return an instance on an unmapped field. */
DECL|method|createUnmappedInstance
specifier|protected
specifier|final
name|T
name|createUnmappedInstance
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// TODO populate pipelineAggregators
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|metaDataCount
init|=
name|randomBoolean
argument_list|()
condition|?
literal|0
else|:
name|between
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
while|while
condition|(
name|metaData
operator|.
name|size
argument_list|()
operator|<
name|metaDataCount
condition|)
block|{
name|metaData
operator|.
name|put
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|5
argument_list|)
argument_list|,
name|randomAlphaOfLength
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|createUnmappedInstance
argument_list|(
name|name
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNamedWriteableRegistry
specifier|protected
name|NamedWriteableRegistry
name|getNamedWriteableRegistry
parameter_list|()
block|{
return|return
name|namedWriteableRegistry
return|;
block|}
annotation|@
name|Override
DECL|method|xContentRegistry
specifier|protected
name|NamedXContentRegistry
name|xContentRegistry
parameter_list|()
block|{
return|return
name|namedXContentRegistry
return|;
block|}
DECL|method|testFromXContent
specifier|public
specifier|final
name|void
name|testFromXContent
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|NamedXContentRegistry
name|xContentRegistry
init|=
name|xContentRegistry
argument_list|()
decl_stmt|;
specifier|final
name|T
name|aggregation
init|=
name|createTestInstance
argument_list|()
decl_stmt|;
comment|//norelease Remove this assumption when all aggregations can be parsed back.
name|assumeTrue
argument_list|(
literal|"This test does not support the aggregation type yet"
argument_list|,
name|getNamedXContents
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|entry
lambda|->
name|entry
operator|.
name|name
operator|.
name|match
argument_list|(
name|aggregation
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
operator|.
name|count
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|ToXContent
operator|.
name|Params
name|params
init|=
operator|new
name|ToXContent
operator|.
name|MapParams
argument_list|(
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
specifier|final
name|boolean
name|humanReadable
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
specifier|final
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
specifier|final
name|BytesReference
name|originalBytes
init|=
name|toShuffledXContent
argument_list|(
name|aggregation
argument_list|,
name|xContentType
argument_list|,
name|params
argument_list|,
name|humanReadable
argument_list|)
decl_stmt|;
name|Aggregation
name|parsedAggregation
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|xContentType
operator|.
name|xContent
argument_list|()
operator|.
name|createParser
argument_list|(
name|xContentRegistry
argument_list|,
name|originalBytes
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
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
name|parsedAggregation
operator|=
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
expr_stmt|;
name|assertEquals
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|,
name|parser
operator|.
name|currentToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
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
name|aggregation
operator|.
name|getName
argument_list|()
argument_list|,
name|parsedAggregation
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aggregation
operator|.
name|getMetaData
argument_list|()
argument_list|,
name|parsedAggregation
operator|.
name|getMetaData
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|parsedAggregation
operator|instanceof
name|ParsedAggregation
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|aggregation
operator|.
name|getType
argument_list|()
argument_list|,
operator|(
operator|(
name|ParsedAggregation
operator|)
name|parsedAggregation
operator|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|BytesReference
name|parsedBytes
init|=
name|toXContent
argument_list|(
operator|(
name|ToXContent
operator|)
name|parsedAggregation
argument_list|,
name|xContentType
argument_list|,
name|params
argument_list|,
name|humanReadable
argument_list|)
decl_stmt|;
name|assertToXContentEquivalent
argument_list|(
name|originalBytes
argument_list|,
name|parsedBytes
argument_list|,
name|xContentType
argument_list|)
expr_stmt|;
name|assertFromXContent
argument_list|(
name|aggregation
argument_list|,
operator|(
name|ParsedAggregation
operator|)
name|parsedAggregation
argument_list|)
expr_stmt|;
block|}
block|}
comment|//norelease TODO make abstract
DECL|method|assertFromXContent
specifier|protected
name|void
name|assertFromXContent
parameter_list|(
name|T
name|aggregation
parameter_list|,
name|ParsedAggregation
name|parsedAggregation
parameter_list|)
block|{     }
block|}
end_class

end_unit

