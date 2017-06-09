begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
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
name|text
operator|.
name|Text
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
name|search
operator|.
name|SearchHit
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
name|SearchHits
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
name|SearchHitsTests
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
name|AggregationsTests
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
name|InternalAggregations
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
name|internal
operator|.
name|InternalSearchResponse
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
name|profile
operator|.
name|SearchProfileShardResults
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
name|profile
operator|.
name|SearchProfileShardResultsTests
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
name|suggest
operator|.
name|Suggest
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
name|suggest
operator|.
name|SuggestTests
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
name|InternalAggregationTestCase
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
name|List
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
name|test
operator|.
name|XContentTestUtils
operator|.
name|insertRandomFields
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
DECL|class|SearchResponseTests
specifier|public
class|class
name|SearchResponseTests
extends|extends
name|ESTestCase
block|{
DECL|field|xContentRegistry
specifier|private
specifier|static
specifier|final
name|NamedXContentRegistry
name|xContentRegistry
decl_stmt|;
static|static
block|{
name|List
argument_list|<
name|NamedXContentRegistry
operator|.
name|Entry
argument_list|>
name|namedXContents
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|InternalAggregationTestCase
operator|.
name|getDefaultNamedXContents
argument_list|()
argument_list|)
decl_stmt|;
name|namedXContents
operator|.
name|addAll
argument_list|(
name|SuggestTests
operator|.
name|getDefaultNamedXContents
argument_list|()
argument_list|)
expr_stmt|;
name|xContentRegistry
operator|=
operator|new
name|NamedXContentRegistry
argument_list|(
name|namedXContents
argument_list|)
expr_stmt|;
block|}
DECL|field|aggregationsTests
specifier|private
name|AggregationsTests
name|aggregationsTests
init|=
operator|new
name|AggregationsTests
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|aggregationsTests
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanUp
specifier|public
name|void
name|cleanUp
parameter_list|()
throws|throws
name|Exception
block|{
name|aggregationsTests
operator|.
name|cleanUp
argument_list|()
expr_stmt|;
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
name|xContentRegistry
return|;
block|}
DECL|method|createTestItem
specifier|private
name|SearchResponse
name|createTestItem
parameter_list|(
name|ShardSearchFailure
modifier|...
name|shardSearchFailures
parameter_list|)
block|{
return|return
name|createTestItem
argument_list|(
literal|false
argument_list|,
name|shardSearchFailures
argument_list|)
return|;
block|}
comment|/**      * This SearchResponse doesn't include SearchHits, Aggregations, Suggestions, ShardSearchFailures, SearchProfileShardResults      * to make it possible to only test properties of the SearchResponse itself      */
DECL|method|createMinimalTestItem
specifier|private
name|SearchResponse
name|createMinimalTestItem
parameter_list|()
block|{
return|return
name|createTestItem
argument_list|(
literal|true
argument_list|)
return|;
block|}
comment|/**      * if minimal is set, don't include search hits, aggregations, suggest etc... to make test simpler      */
DECL|method|createTestItem
specifier|private
name|SearchResponse
name|createTestItem
parameter_list|(
name|boolean
name|minimal
parameter_list|,
name|ShardSearchFailure
modifier|...
name|shardSearchFailures
parameter_list|)
block|{
name|boolean
name|timedOut
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|Boolean
name|terminatedEarly
init|=
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
name|randomBoolean
argument_list|()
decl_stmt|;
name|int
name|numReducePhases
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|long
name|tookInMillis
init|=
name|randomNonNegativeLong
argument_list|()
decl_stmt|;
name|int
name|successfulShards
init|=
name|randomInt
argument_list|()
decl_stmt|;
name|int
name|totalShards
init|=
name|randomInt
argument_list|()
decl_stmt|;
name|InternalSearchResponse
name|internalSearchResponse
decl_stmt|;
if|if
condition|(
name|minimal
operator|==
literal|false
condition|)
block|{
name|SearchHits
name|hits
init|=
name|SearchHitsTests
operator|.
name|createTestItem
argument_list|()
decl_stmt|;
name|InternalAggregations
name|aggregations
init|=
name|aggregationsTests
operator|.
name|createTestInstance
argument_list|()
decl_stmt|;
name|Suggest
name|suggest
init|=
name|SuggestTests
operator|.
name|createTestItem
argument_list|()
decl_stmt|;
name|SearchProfileShardResults
name|profileShardResults
init|=
name|SearchProfileShardResultsTests
operator|.
name|createTestItem
argument_list|()
decl_stmt|;
name|internalSearchResponse
operator|=
operator|new
name|InternalSearchResponse
argument_list|(
name|hits
argument_list|,
name|aggregations
argument_list|,
name|suggest
argument_list|,
name|profileShardResults
argument_list|,
name|timedOut
argument_list|,
name|terminatedEarly
argument_list|,
name|numReducePhases
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|internalSearchResponse
operator|=
name|InternalSearchResponse
operator|.
name|empty
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|SearchResponse
argument_list|(
name|internalSearchResponse
argument_list|,
literal|null
argument_list|,
name|totalShards
argument_list|,
name|successfulShards
argument_list|,
name|tookInMillis
argument_list|,
name|shardSearchFailures
argument_list|)
return|;
block|}
comment|/**      * the "_shard/total/failures" section makes it impossible to directly      * compare xContent, so we omit it here      */
DECL|method|testFromXContent
specifier|public
name|void
name|testFromXContent
parameter_list|()
throws|throws
name|IOException
block|{
name|doFromXContentTestWithRandomFields
argument_list|(
name|createTestItem
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * This test adds random fields and objects to the xContent rendered out to      * ensure we can parse it back to be forward compatible with additions to      * the xContent. We test this with a "minimal" SearchResponse, adding random      * fields to SearchHits, Aggregations etc... is tested in their own tests      */
DECL|method|testFromXContentWithRandomFields
specifier|public
name|void
name|testFromXContentWithRandomFields
parameter_list|()
throws|throws
name|IOException
block|{
name|doFromXContentTestWithRandomFields
argument_list|(
name|createMinimalTestItem
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|doFromXContentTestWithRandomFields
specifier|private
name|void
name|doFromXContentTestWithRandomFields
parameter_list|(
name|SearchResponse
name|response
parameter_list|,
name|boolean
name|addRandomFields
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentType
name|xcontentType
init|=
name|randomFrom
argument_list|(
name|XContentType
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|humanReadable
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
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
name|BytesReference
name|originalBytes
init|=
name|toShuffledXContent
argument_list|(
name|response
argument_list|,
name|xcontentType
argument_list|,
name|params
argument_list|,
name|humanReadable
argument_list|)
decl_stmt|;
name|BytesReference
name|mutated
decl_stmt|;
if|if
condition|(
name|addRandomFields
condition|)
block|{
name|mutated
operator|=
name|insertRandomFields
argument_list|(
name|xcontentType
argument_list|,
name|originalBytes
argument_list|,
literal|null
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mutated
operator|=
name|originalBytes
expr_stmt|;
block|}
try|try
init|(
name|XContentParser
name|parser
init|=
name|createParser
argument_list|(
name|xcontentType
operator|.
name|xContent
argument_list|()
argument_list|,
name|mutated
argument_list|)
init|)
block|{
name|SearchResponse
name|parsed
init|=
name|SearchResponse
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|assertToXContentEquivalent
argument_list|(
name|originalBytes
argument_list|,
name|XContentHelper
operator|.
name|toXContent
argument_list|(
name|parsed
argument_list|,
name|xcontentType
argument_list|,
name|params
argument_list|,
name|humanReadable
argument_list|)
argument_list|,
name|xcontentType
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
name|assertNull
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * The "_shard/total/failures" section makes if impossible to directly compare xContent, because      * the failures in the parsed SearchResponse are wrapped in an extra ElasticSearchException on the client side.      * Because of this, in this special test case we compare the "top level" fields for equality      * and the subsections xContent equivalence independently      */
DECL|method|testFromXContentWithFailures
specifier|public
name|void
name|testFromXContentWithFailures
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|numFailures
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|ShardSearchFailure
index|[]
name|failures
init|=
operator|new
name|ShardSearchFailure
index|[
name|numFailures
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
name|failures
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|failures
index|[
name|i
index|]
operator|=
name|ShardSearchFailureTests
operator|.
name|createTestItem
argument_list|()
expr_stmt|;
block|}
name|SearchResponse
name|response
init|=
name|createTestItem
argument_list|(
name|failures
argument_list|)
decl_stmt|;
name|XContentType
name|xcontentType
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
name|BytesReference
name|originalBytes
init|=
name|toShuffledXContent
argument_list|(
name|response
argument_list|,
name|xcontentType
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
name|xcontentType
operator|.
name|xContent
argument_list|()
argument_list|,
name|originalBytes
argument_list|)
init|)
block|{
name|SearchResponse
name|parsed
init|=
name|SearchResponse
operator|.
name|fromXContent
argument_list|(
name|parser
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
name|parsed
operator|.
name|getShardFailures
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ShardSearchFailure
name|parsedFailure
init|=
name|parsed
operator|.
name|getShardFailures
argument_list|()
index|[
name|i
index|]
decl_stmt|;
name|ShardSearchFailure
name|originalFailure
init|=
name|failures
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|originalFailure
operator|.
name|index
argument_list|()
argument_list|,
name|parsedFailure
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|originalFailure
operator|.
name|shard
argument_list|()
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|parsedFailure
operator|.
name|shard
argument_list|()
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|originalFailure
operator|.
name|shardId
argument_list|()
argument_list|,
name|parsedFailure
operator|.
name|shardId
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|originalMsg
init|=
name|originalFailure
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|parsedFailure
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Elasticsearch exception [type=parsing_exception, reason="
operator|+
name|originalMsg
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|String
name|nestedMsg
init|=
name|originalFailure
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|parsedFailure
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Elasticsearch exception [type=illegal_argument_exception, reason="
operator|+
name|nestedMsg
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
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
name|assertNull
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testToXContent
specifier|public
name|void
name|testToXContent
parameter_list|()
block|{
name|SearchHit
name|hit
init|=
operator|new
name|SearchHit
argument_list|(
literal|1
argument_list|,
literal|"id1"
argument_list|,
operator|new
name|Text
argument_list|(
literal|"type"
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
decl_stmt|;
name|hit
operator|.
name|score
argument_list|(
literal|2.0f
argument_list|)
expr_stmt|;
name|SearchHit
index|[]
name|hits
init|=
operator|new
name|SearchHit
index|[]
block|{
name|hit
block|}
decl_stmt|;
name|SearchResponse
name|response
init|=
operator|new
name|SearchResponse
argument_list|(
operator|new
name|InternalSearchResponse
argument_list|(
operator|new
name|SearchHits
argument_list|(
name|hits
argument_list|,
literal|100
argument_list|,
literal|1.5f
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|new
name|ShardSearchFailure
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|StringBuilder
name|expectedString
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|expectedString
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
block|{
name|expectedString
operator|.
name|append
argument_list|(
literal|"\"took\":0,"
argument_list|)
expr_stmt|;
name|expectedString
operator|.
name|append
argument_list|(
literal|"\"timed_out\":false,"
argument_list|)
expr_stmt|;
name|expectedString
operator|.
name|append
argument_list|(
literal|"\"_shards\":"
argument_list|)
expr_stmt|;
block|{
name|expectedString
operator|.
name|append
argument_list|(
literal|"{\"total\":0,"
argument_list|)
expr_stmt|;
name|expectedString
operator|.
name|append
argument_list|(
literal|"\"successful\":0,"
argument_list|)
expr_stmt|;
name|expectedString
operator|.
name|append
argument_list|(
literal|"\"failed\":0},"
argument_list|)
expr_stmt|;
block|}
name|expectedString
operator|.
name|append
argument_list|(
literal|"\"hits\":"
argument_list|)
expr_stmt|;
block|{
name|expectedString
operator|.
name|append
argument_list|(
literal|"{\"total\":100,"
argument_list|)
expr_stmt|;
name|expectedString
operator|.
name|append
argument_list|(
literal|"\"max_score\":1.5,"
argument_list|)
expr_stmt|;
name|expectedString
operator|.
name|append
argument_list|(
literal|"\"hits\":[{\"_type\":\"type\",\"_id\":\"id1\",\"_score\":2.0}]}"
argument_list|)
expr_stmt|;
block|}
block|}
name|expectedString
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedString
operator|.
name|toString
argument_list|()
argument_list|,
name|Strings
operator|.
name|toString
argument_list|(
name|response
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

