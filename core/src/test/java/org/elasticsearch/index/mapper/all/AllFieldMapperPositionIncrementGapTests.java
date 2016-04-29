begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.all
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|all
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
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
name|MatchPhraseQueryBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
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
name|ESSingleNodeTestCase
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
name|InternalSettingsPlugin
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
name|Collection
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
name|assertHitCount
import|;
end_import

begin_comment
comment|/**  * Tests that position_increment_gap is read from the mapper and applies as  * expected in queries.  */
end_comment

begin_class
DECL|class|AllFieldMapperPositionIncrementGapTests
specifier|public
class|class
name|AllFieldMapperPositionIncrementGapTests
extends|extends
name|ESSingleNodeTestCase
block|{
annotation|@
name|Override
DECL|method|getPlugins
specifier|protected
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|getPlugins
parameter_list|()
block|{
return|return
name|pluginList
argument_list|(
name|InternalSettingsPlugin
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**      * The default position_increment_gap should be large enough that most      * "sensible" queries phrase slops won't match across values.      */
DECL|method|testDefault
specifier|public
name|void
name|testDefault
parameter_list|()
throws|throws
name|IOException
block|{
name|assertGapIsOneHundred
argument_list|(
name|client
argument_list|()
argument_list|,
literal|"test"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Asserts that the post-2.0 default is being applied.      */
DECL|method|assertGapIsOneHundred
specifier|public
specifier|static
name|void
name|assertGapIsOneHundred
parameter_list|(
name|Client
name|client
parameter_list|,
name|String
name|indexName
parameter_list|,
name|String
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|testGap
argument_list|(
name|client
argument_list|,
name|indexName
argument_list|,
name|type
argument_list|,
literal|100
argument_list|)
expr_stmt|;
comment|// No match across gap using default slop with default positionIncrementGap
name|assertHitCount
argument_list|(
name|client
operator|.
name|prepareSearch
argument_list|(
name|indexName
argument_list|)
operator|.
name|setQuery
argument_list|(
operator|new
name|MatchPhraseQueryBuilder
argument_list|(
literal|"_all"
argument_list|,
literal|"one two"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Nor with small-ish values
name|assertHitCount
argument_list|(
name|client
operator|.
name|prepareSearch
argument_list|(
name|indexName
argument_list|)
operator|.
name|setQuery
argument_list|(
operator|new
name|MatchPhraseQueryBuilder
argument_list|(
literal|"_all"
argument_list|,
literal|"one two"
argument_list|)
operator|.
name|slop
argument_list|(
literal|5
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|client
operator|.
name|prepareSearch
argument_list|(
name|indexName
argument_list|)
operator|.
name|setQuery
argument_list|(
operator|new
name|MatchPhraseQueryBuilder
argument_list|(
literal|"_all"
argument_list|,
literal|"one two"
argument_list|)
operator|.
name|slop
argument_list|(
literal|50
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// But huge-ish values still match
name|assertHitCount
argument_list|(
name|client
operator|.
name|prepareSearch
argument_list|(
name|indexName
argument_list|)
operator|.
name|setQuery
argument_list|(
operator|new
name|MatchPhraseQueryBuilder
argument_list|(
literal|"_all"
argument_list|,
literal|"one two"
argument_list|)
operator|.
name|slop
argument_list|(
literal|500
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**      * Asserts that the pre-2.0 default has been applied or explicitly      * configured.      */
DECL|method|assertGapIsZero
specifier|public
specifier|static
name|void
name|assertGapIsZero
parameter_list|(
name|Client
name|client
parameter_list|,
name|String
name|indexName
parameter_list|,
name|String
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|testGap
argument_list|(
name|client
argument_list|,
name|indexName
argument_list|,
name|type
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|/*          * Phrases match across different values using default slop with pre-2.0 default          * position_increment_gap.          */
name|assertHitCount
argument_list|(
name|client
operator|.
name|prepareSearch
argument_list|(
name|indexName
argument_list|)
operator|.
name|setQuery
argument_list|(
operator|new
name|MatchPhraseQueryBuilder
argument_list|(
literal|"string"
argument_list|,
literal|"one two"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testGap
specifier|private
specifier|static
name|void
name|testGap
parameter_list|(
name|Client
name|client
parameter_list|,
name|String
name|indexName
parameter_list|,
name|String
name|type
parameter_list|,
name|int
name|positionIncrementGap
parameter_list|)
throws|throws
name|IOException
block|{
name|client
operator|.
name|prepareIndex
argument_list|(
name|indexName
argument_list|,
name|type
argument_list|,
literal|"position_gap_test"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"string1"
argument_list|,
literal|"one"
argument_list|,
literal|"string2"
argument_list|,
literal|"two three"
argument_list|)
operator|.
name|setRefresh
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// Baseline - phrase query finds matches in the same field value
name|assertHitCount
argument_list|(
name|client
operator|.
name|prepareSearch
argument_list|(
name|indexName
argument_list|)
operator|.
name|setQuery
argument_list|(
operator|new
name|MatchPhraseQueryBuilder
argument_list|(
literal|"_all"
argument_list|,
literal|"two three"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|positionIncrementGap
operator|>
literal|0
condition|)
block|{
comment|// No match across gaps when slop< position gap
name|assertHitCount
argument_list|(
name|client
operator|.
name|prepareSearch
argument_list|(
name|indexName
argument_list|)
operator|.
name|setQuery
argument_list|(
operator|new
name|MatchPhraseQueryBuilder
argument_list|(
literal|"_all"
argument_list|,
literal|"one two"
argument_list|)
operator|.
name|slop
argument_list|(
name|positionIncrementGap
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// Match across gaps when slop>= position gap
name|assertHitCount
argument_list|(
name|client
operator|.
name|prepareSearch
argument_list|(
name|indexName
argument_list|)
operator|.
name|setQuery
argument_list|(
operator|new
name|MatchPhraseQueryBuilder
argument_list|(
literal|"_all"
argument_list|,
literal|"one two"
argument_list|)
operator|.
name|slop
argument_list|(
name|positionIncrementGap
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|client
operator|.
name|prepareSearch
argument_list|(
name|indexName
argument_list|)
operator|.
name|setQuery
argument_list|(
operator|new
name|MatchPhraseQueryBuilder
argument_list|(
literal|"_all"
argument_list|,
literal|"one two"
argument_list|)
operator|.
name|slop
argument_list|(
name|positionIncrementGap
operator|+
literal|1
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

