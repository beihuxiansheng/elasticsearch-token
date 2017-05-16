begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.significant
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
operator|.
name|significant
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
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
name|Writeable
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
name|DocValueFormat
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
name|aggregations
operator|.
name|ParsedMultiBucketAggregation
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
name|heuristics
operator|.
name|ChiSquare
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
name|heuristics
operator|.
name|GND
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
name|heuristics
operator|.
name|JLHScore
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
name|heuristics
operator|.
name|MutualInformation
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
name|heuristics
operator|.
name|SignificanceHeuristic
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
name|HashSet
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

begin_class
DECL|class|SignificantStringTermsTests
specifier|public
class|class
name|SignificantStringTermsTests
extends|extends
name|InternalSignificantTermsTestCase
block|{
DECL|field|significanceHeuristic
specifier|private
name|SignificanceHeuristic
name|significanceHeuristic
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|significanceHeuristic
operator|=
name|randomSignificanceHeuristic
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createTestInstance
specifier|protected
name|InternalSignificantTerms
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
parameter_list|,
name|InternalAggregations
name|aggregations
parameter_list|)
block|{
name|DocValueFormat
name|format
init|=
name|DocValueFormat
operator|.
name|RAW
decl_stmt|;
name|int
name|requiredSize
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|int
name|shardSize
init|=
name|requiredSize
operator|+
literal|2
decl_stmt|;
specifier|final
name|int
name|numBuckets
init|=
name|randomInt
argument_list|(
name|shardSize
argument_list|)
decl_stmt|;
name|long
name|globalSubsetSize
init|=
literal|0
decl_stmt|;
name|long
name|globalSupersetSize
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|SignificantStringTerms
operator|.
name|Bucket
argument_list|>
name|buckets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numBuckets
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|terms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
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
name|numBuckets
condition|;
operator|++
name|i
control|)
block|{
name|BytesRef
name|term
init|=
name|randomValueOtherThanMany
argument_list|(
name|b
lambda|->
name|terms
operator|.
name|add
argument_list|(
name|b
argument_list|)
operator|==
literal|false
argument_list|,
parameter_list|()
lambda|->
operator|new
name|BytesRef
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|subsetDf
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|int
name|supersetDf
init|=
name|randomIntBetween
argument_list|(
name|subsetDf
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|int
name|supersetSize
init|=
name|randomIntBetween
argument_list|(
name|supersetDf
argument_list|,
literal|30
argument_list|)
decl_stmt|;
name|globalSubsetSize
operator|+=
name|subsetDf
expr_stmt|;
name|globalSupersetSize
operator|+=
name|supersetSize
expr_stmt|;
name|buckets
operator|.
name|add
argument_list|(
operator|new
name|SignificantStringTerms
operator|.
name|Bucket
argument_list|(
name|term
argument_list|,
name|subsetDf
argument_list|,
name|subsetDf
argument_list|,
name|supersetDf
argument_list|,
name|supersetSize
argument_list|,
name|aggregations
argument_list|,
name|format
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SignificantStringTerms
argument_list|(
name|name
argument_list|,
name|requiredSize
argument_list|,
literal|1L
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|,
name|format
argument_list|,
name|globalSubsetSize
argument_list|,
name|globalSupersetSize
argument_list|,
name|significanceHeuristic
argument_list|,
name|buckets
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|instanceReader
specifier|protected
name|Writeable
operator|.
name|Reader
argument_list|<
name|InternalSignificantTerms
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|instanceReader
parameter_list|()
block|{
return|return
name|SignificantStringTerms
operator|::
operator|new
return|;
block|}
annotation|@
name|Override
DECL|method|implementationClass
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|ParsedMultiBucketAggregation
argument_list|>
name|implementationClass
parameter_list|()
block|{
return|return
name|ParsedSignificantStringTerms
operator|.
name|class
return|;
block|}
DECL|method|randomSignificanceHeuristic
specifier|private
specifier|static
name|SignificanceHeuristic
name|randomSignificanceHeuristic
parameter_list|()
block|{
return|return
name|randomFrom
argument_list|(
operator|new
name|JLHScore
argument_list|()
argument_list|,
operator|new
name|MutualInformation
argument_list|(
name|randomBoolean
argument_list|()
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
argument_list|,
operator|new
name|GND
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ChiSquare
argument_list|(
name|randomBoolean
argument_list|()
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

