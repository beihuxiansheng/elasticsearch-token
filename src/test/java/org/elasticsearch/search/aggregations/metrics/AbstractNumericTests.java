begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|metrics
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
name|common
operator|.
name|settings
operator|.
name|ImmutableSettings
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
name|test
operator|.
name|ElasticsearchIntegrationTest
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
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AbstractNumericTests
specifier|public
specifier|abstract
class|class
name|AbstractNumericTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Override
DECL|method|indexSettings
specifier|public
name|Settings
name|indexSettings
parameter_list|()
block|{
return|return
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
name|between
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_replicas"
argument_list|,
name|between
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|field|minValue
DECL|field|maxValue
DECL|field|minValues
DECL|field|maxValues
specifier|protected
name|long
name|minValue
decl_stmt|,
name|maxValue
decl_stmt|,
name|minValues
decl_stmt|,
name|maxValues
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
name|createIndex
argument_list|(
literal|"idx"
argument_list|)
expr_stmt|;
name|createIndex
argument_list|(
literal|"idx_unmapped"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|builders
init|=
operator|new
name|ArrayList
argument_list|<
name|IndexRequestBuilder
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
literal|10
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
comment|// TODO randomize the size and the params in here?
name|builders
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
name|i
operator|+
literal|1
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"values"
argument_list|)
operator|.
name|value
argument_list|(
name|i
operator|+
literal|2
argument_list|)
operator|.
name|value
argument_list|(
name|i
operator|+
literal|3
argument_list|)
operator|.
name|endArray
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|minValue
operator|=
literal|1
expr_stmt|;
name|minValues
operator|=
literal|2
expr_stmt|;
name|maxValue
operator|=
name|numDocs
expr_stmt|;
name|maxValues
operator|=
name|numDocs
operator|+
literal|2
expr_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|builders
argument_list|)
expr_stmt|;
comment|// creating an index to test the empty buckets functionality. The way it works is by indexing
comment|// two docs {value: 0} and {value : 2}, then building a histogram agg with interval 1 and with empty
comment|// buckets computed.. the empty bucket is the one associated with key "1". then each test will have
comment|// to check that this bucket exists with the appropriate sub aggregations.
name|prepareCreate
argument_list|(
literal|"empty_bucket_idx"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
literal|"value"
argument_list|,
literal|"type=integer"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|builders
operator|=
operator|new
name|ArrayList
argument_list|<
name|IndexRequestBuilder
argument_list|>
argument_list|()
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
literal|2
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
literal|"empty_bucket_idx"
argument_list|,
literal|"type"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
name|i
operator|*
literal|2
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|builders
argument_list|)
expr_stmt|;
name|ensureSearchable
argument_list|()
expr_stmt|;
block|}
DECL|method|testEmptyAggregation
specifier|public
specifier|abstract
name|void
name|testEmptyAggregation
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|testUnmapped
specifier|public
specifier|abstract
name|void
name|testUnmapped
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|testSingleValuedField
specifier|public
specifier|abstract
name|void
name|testSingleValuedField
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|testSingleValuedField_PartiallyUnmapped
specifier|public
specifier|abstract
name|void
name|testSingleValuedField_PartiallyUnmapped
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|testSingleValuedField_WithValueScript
specifier|public
specifier|abstract
name|void
name|testSingleValuedField_WithValueScript
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|testSingleValuedField_WithValueScript_WithParams
specifier|public
specifier|abstract
name|void
name|testSingleValuedField_WithValueScript_WithParams
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|testMultiValuedField
specifier|public
specifier|abstract
name|void
name|testMultiValuedField
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|testMultiValuedField_WithValueScript
specifier|public
specifier|abstract
name|void
name|testMultiValuedField_WithValueScript
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|testMultiValuedField_WithValueScript_WithParams
specifier|public
specifier|abstract
name|void
name|testMultiValuedField_WithValueScript_WithParams
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|testScript_SingleValued
specifier|public
specifier|abstract
name|void
name|testScript_SingleValued
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|testScript_SingleValued_WithParams
specifier|public
specifier|abstract
name|void
name|testScript_SingleValued_WithParams
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|testScript_ExplicitSingleValued_WithParams
specifier|public
specifier|abstract
name|void
name|testScript_ExplicitSingleValued_WithParams
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|testScript_MultiValued
specifier|public
specifier|abstract
name|void
name|testScript_MultiValued
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|testScript_ExplicitMultiValued
specifier|public
specifier|abstract
name|void
name|testScript_ExplicitMultiValued
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|testScript_MultiValued_WithParams
specifier|public
specifier|abstract
name|void
name|testScript_MultiValued_WithParams
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

