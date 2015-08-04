begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
package|;
end_package

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
name|IndexRequestBuilder
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
name|search
operator|.
name|SearchResponse
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
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|filter
operator|.
name|FilterAggregationBuilder
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
name|bucket
operator|.
name|filter
operator|.
name|InternalFilter
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
name|bucket
operator|.
name|significant
operator|.
name|SignificantTerms
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
name|bucket
operator|.
name|significant
operator|.
name|SignificantTermsAggregatorFactory
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
name|bucket
operator|.
name|significant
operator|.
name|SignificantTermsBuilder
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
name|bucket
operator|.
name|terms
operator|.
name|Terms
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
name|bucket
operator|.
name|terms
operator|.
name|TermsBuilder
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
name|ESIntegTestCase
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
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
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
name|assertAcked
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
name|assertSearchResponse
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TermsShardMinDocCountIT
specifier|public
class|class
name|TermsShardMinDocCountIT
extends|extends
name|ESIntegTestCase
block|{
DECL|field|index
specifier|private
specifier|static
specifier|final
name|String
name|index
init|=
literal|"someindex"
decl_stmt|;
DECL|field|type
specifier|private
specifier|static
specifier|final
name|String
name|type
init|=
literal|"testtype"
decl_stmt|;
DECL|method|randomExecutionHint
specifier|public
name|String
name|randomExecutionHint
parameter_list|()
block|{
return|return
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
name|randomFrom
argument_list|(
name|SignificantTermsAggregatorFactory
operator|.
name|ExecutionMode
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// see https://github.com/elasticsearch/elasticsearch/issues/5998
annotation|@
name|Test
DECL|method|shardMinDocCountSignificantTermsTest
specifier|public
name|void
name|shardMinDocCountSignificantTermsTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|termtype
init|=
literal|"string"
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|termtype
operator|=
literal|"long"
expr_stmt|;
block|}
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
name|index
argument_list|)
operator|.
name|setSettings
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|1
argument_list|,
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|0
argument_list|)
operator|.
name|addMapping
argument_list|(
name|type
argument_list|,
literal|"{\"properties\":{\"text\": {\"type\": \""
operator|+
name|termtype
operator|+
literal|"\"}}}"
argument_list|)
argument_list|)
expr_stmt|;
name|ensureYellow
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|indexBuilders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|addTermsDocs
argument_list|(
literal|"1"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
name|indexBuilders
argument_list|)
expr_stmt|;
comment|//high score but low doc freq
name|addTermsDocs
argument_list|(
literal|"2"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
name|indexBuilders
argument_list|)
expr_stmt|;
name|addTermsDocs
argument_list|(
literal|"3"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
name|indexBuilders
argument_list|)
expr_stmt|;
name|addTermsDocs
argument_list|(
literal|"4"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
name|indexBuilders
argument_list|)
expr_stmt|;
name|addTermsDocs
argument_list|(
literal|"5"
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
name|indexBuilders
argument_list|)
expr_stmt|;
comment|//low score but high doc freq
name|addTermsDocs
argument_list|(
literal|"6"
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
name|indexBuilders
argument_list|)
expr_stmt|;
name|addTermsDocs
argument_list|(
literal|"7"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
name|indexBuilders
argument_list|)
expr_stmt|;
comment|// make sure the terms all get score> 0 except for this one
name|indexRandom
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
name|indexBuilders
argument_list|)
expr_stmt|;
comment|// first, check that indeed when not setting the shardMinDocCount parameter 0 terms are returned
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
name|index
argument_list|)
operator|.
name|addAggregation
argument_list|(
operator|(
operator|new
name|FilterAggregationBuilder
argument_list|(
literal|"inclass"
argument_list|)
operator|.
name|filter
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"class"
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|)
operator|.
name|subAggregation
argument_list|(
operator|new
name|SignificantTermsBuilder
argument_list|(
literal|"mySignificantTerms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"text"
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|2
argument_list|)
operator|.
name|size
argument_list|(
literal|2
argument_list|)
operator|.
name|executionHint
argument_list|(
name|randomExecutionHint
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|InternalFilter
name|filteredBucket
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"inclass"
argument_list|)
decl_stmt|;
name|SignificantTerms
name|sigterms
init|=
name|filteredBucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"mySignificantTerms"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|sigterms
operator|.
name|getBuckets
argument_list|()
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
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
name|index
argument_list|)
operator|.
name|addAggregation
argument_list|(
operator|(
operator|new
name|FilterAggregationBuilder
argument_list|(
literal|"inclass"
argument_list|)
operator|.
name|filter
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"class"
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|)
operator|.
name|subAggregation
argument_list|(
operator|new
name|SignificantTermsBuilder
argument_list|(
literal|"mySignificantTerms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"text"
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|2
argument_list|)
operator|.
name|shardMinDocCount
argument_list|(
literal|2
argument_list|)
operator|.
name|size
argument_list|(
literal|2
argument_list|)
operator|.
name|executionHint
argument_list|(
name|randomExecutionHint
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertSearchResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|filteredBucket
operator|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"inclass"
argument_list|)
expr_stmt|;
name|sigterms
operator|=
name|filteredBucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"mySignificantTerms"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sigterms
operator|.
name|getBuckets
argument_list|()
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
block|}
DECL|method|addTermsDocs
specifier|private
name|void
name|addTermsDocs
parameter_list|(
name|String
name|term
parameter_list|,
name|int
name|numInClass
parameter_list|,
name|int
name|numNotInClass
parameter_list|,
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|builders
parameter_list|)
block|{
name|String
name|sourceClass
init|=
literal|"{\"text\": \""
operator|+
name|term
operator|+
literal|"\", \"class\":"
operator|+
literal|"true"
operator|+
literal|"}"
decl_stmt|;
name|String
name|sourceNotClass
init|=
literal|"{\"text\": \""
operator|+
name|term
operator|+
literal|"\", \"class\":"
operator|+
literal|"false"
operator|+
literal|"}"
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
name|numInClass
condition|;
name|i
operator|++
control|)
block|{
name|builders
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
name|index
argument_list|,
name|type
argument_list|)
operator|.
name|setSource
argument_list|(
name|sourceClass
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numNotInClass
condition|;
name|i
operator|++
control|)
block|{
name|builders
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
name|index
argument_list|,
name|type
argument_list|)
operator|.
name|setSource
argument_list|(
name|sourceNotClass
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// see https://github.com/elasticsearch/elasticsearch/issues/5998
annotation|@
name|Test
DECL|method|shardMinDocCountTermsTest
specifier|public
name|void
name|shardMinDocCountTermsTest
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
index|[]
name|termTypes
init|=
block|{
literal|"string"
block|,
literal|"long"
block|,
literal|"integer"
block|,
literal|"float"
block|,
literal|"double"
block|}
decl_stmt|;
name|String
name|termtype
init|=
name|termTypes
index|[
name|randomInt
argument_list|(
name|termTypes
operator|.
name|length
operator|-
literal|1
argument_list|)
index|]
decl_stmt|;
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
name|index
argument_list|)
operator|.
name|setSettings
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|1
argument_list|,
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|0
argument_list|)
operator|.
name|addMapping
argument_list|(
name|type
argument_list|,
literal|"{\"properties\":{\"text\": {\"type\": \""
operator|+
name|termtype
operator|+
literal|"\"}}}"
argument_list|)
argument_list|)
expr_stmt|;
name|ensureYellow
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|indexBuilders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|addTermsDocs
argument_list|(
literal|"1"
argument_list|,
literal|1
argument_list|,
name|indexBuilders
argument_list|)
expr_stmt|;
comment|//low doc freq but high score
name|addTermsDocs
argument_list|(
literal|"2"
argument_list|,
literal|1
argument_list|,
name|indexBuilders
argument_list|)
expr_stmt|;
name|addTermsDocs
argument_list|(
literal|"3"
argument_list|,
literal|1
argument_list|,
name|indexBuilders
argument_list|)
expr_stmt|;
name|addTermsDocs
argument_list|(
literal|"4"
argument_list|,
literal|1
argument_list|,
name|indexBuilders
argument_list|)
expr_stmt|;
name|addTermsDocs
argument_list|(
literal|"5"
argument_list|,
literal|3
argument_list|,
name|indexBuilders
argument_list|)
expr_stmt|;
comment|//low score but high doc freq
name|addTermsDocs
argument_list|(
literal|"6"
argument_list|,
literal|3
argument_list|,
name|indexBuilders
argument_list|)
expr_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
name|indexBuilders
argument_list|)
expr_stmt|;
comment|// first, check that indeed when not setting the shardMinDocCount parameter 0 terms are returned
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
name|index
argument_list|)
operator|.
name|addAggregation
argument_list|(
operator|new
name|TermsBuilder
argument_list|(
literal|"myTerms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"text"
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|2
argument_list|)
operator|.
name|size
argument_list|(
literal|2
argument_list|)
operator|.
name|executionHint
argument_list|(
name|randomExecutionHint
argument_list|()
argument_list|)
operator|.
name|order
argument_list|(
name|Terms
operator|.
name|Order
operator|.
name|term
argument_list|(
literal|true
argument_list|)
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Terms
name|sigterms
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"myTerms"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|sigterms
operator|.
name|getBuckets
argument_list|()
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
name|response
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
name|index
argument_list|)
operator|.
name|addAggregation
argument_list|(
operator|new
name|TermsBuilder
argument_list|(
literal|"myTerms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"text"
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|2
argument_list|)
operator|.
name|shardMinDocCount
argument_list|(
literal|2
argument_list|)
operator|.
name|size
argument_list|(
literal|2
argument_list|)
operator|.
name|executionHint
argument_list|(
name|randomExecutionHint
argument_list|()
argument_list|)
operator|.
name|order
argument_list|(
name|Terms
operator|.
name|Order
operator|.
name|term
argument_list|(
literal|true
argument_list|)
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertSearchResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|sigterms
operator|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"myTerms"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sigterms
operator|.
name|getBuckets
argument_list|()
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
block|}
DECL|method|addTermsDocs
specifier|private
name|void
name|addTermsDocs
parameter_list|(
name|String
name|term
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|builders
parameter_list|)
block|{
name|String
name|sourceClass
init|=
literal|"{\"text\": \""
operator|+
name|term
operator|+
literal|"\"}"
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|builders
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
name|index
argument_list|,
name|type
argument_list|)
operator|.
name|setSource
argument_list|(
name|sourceClass
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

