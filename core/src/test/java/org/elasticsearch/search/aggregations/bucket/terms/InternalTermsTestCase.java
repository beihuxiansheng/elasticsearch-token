begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.terms
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
name|terms
package|;
end_package

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
name|InternalAggregationTestCase
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_class
DECL|class|InternalTermsTestCase
specifier|public
specifier|abstract
class|class
name|InternalTermsTestCase
extends|extends
name|InternalAggregationTestCase
argument_list|<
name|InternalTerms
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createUnmappedInstance
specifier|protected
name|InternalTerms
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
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
name|InternalTerms
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|testInstance
init|=
name|createTestInstance
argument_list|(
name|name
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
decl_stmt|;
return|return
operator|new
name|UnmappedTerms
argument_list|(
name|name
argument_list|,
name|testInstance
operator|.
name|order
argument_list|,
name|testInstance
operator|.
name|requiredSize
argument_list|,
name|testInstance
operator|.
name|minDocCount
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|assertReduced
specifier|protected
name|void
name|assertReduced
parameter_list|(
name|InternalTerms
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|reduced
parameter_list|,
name|List
argument_list|<
name|InternalTerms
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|inputs
parameter_list|)
block|{
specifier|final
name|int
name|requiredSize
init|=
name|inputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|requiredSize
decl_stmt|;
name|Map
argument_list|<
name|Object
argument_list|,
name|Long
argument_list|>
name|reducedCounts
init|=
name|toCounts
argument_list|(
name|reduced
operator|.
name|getBuckets
argument_list|()
operator|.
name|stream
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Object
argument_list|,
name|Long
argument_list|>
name|totalCounts
init|=
name|toCounts
argument_list|(
name|inputs
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Terms
operator|::
name|getBuckets
argument_list|)
operator|.
name|flatMap
argument_list|(
name|List
operator|::
name|stream
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|reducedCounts
operator|.
name|size
argument_list|()
operator|==
name|requiredSize
argument_list|,
name|totalCounts
operator|.
name|size
argument_list|()
operator|>=
name|requiredSize
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Object
argument_list|,
name|Long
argument_list|>
name|expectedReducedCounts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|totalCounts
argument_list|)
decl_stmt|;
name|expectedReducedCounts
operator|.
name|keySet
argument_list|()
operator|.
name|retainAll
argument_list|(
name|reducedCounts
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedReducedCounts
argument_list|,
name|reducedCounts
argument_list|)
expr_stmt|;
specifier|final
name|long
name|minFinalcount
init|=
name|reduced
operator|.
name|getBuckets
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|?
operator|-
literal|1
else|:
name|reduced
operator|.
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
name|reduced
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Object
argument_list|,
name|Long
argument_list|>
name|evictedTerms
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|totalCounts
argument_list|)
decl_stmt|;
name|evictedTerms
operator|.
name|keySet
argument_list|()
operator|.
name|removeAll
argument_list|(
name|reducedCounts
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|Optional
argument_list|<
name|Entry
argument_list|<
name|Object
argument_list|,
name|Long
argument_list|>
argument_list|>
name|missingTerm
init|=
name|evictedTerms
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|e
lambda|->
name|e
operator|.
name|getValue
argument_list|()
operator|>
name|minFinalcount
argument_list|)
operator|.
name|findAny
argument_list|()
decl_stmt|;
if|if
condition|(
name|missingTerm
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"Missed term: "
operator|+
name|missingTerm
operator|+
literal|" from "
operator|+
name|reducedCounts
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|reducedTotalDocCount
init|=
name|reduced
operator|.
name|getSumOfOtherDocCounts
argument_list|()
operator|+
name|reduced
operator|.
name|getBuckets
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|mapToLong
argument_list|(
name|Terms
operator|.
name|Bucket
operator|::
name|getDocCount
argument_list|)
operator|.
name|sum
argument_list|()
decl_stmt|;
specifier|final
name|long
name|expectedTotalDocCount
init|=
name|inputs
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Terms
operator|::
name|getBuckets
argument_list|)
operator|.
name|flatMap
argument_list|(
name|List
operator|::
name|stream
argument_list|)
operator|.
name|mapToLong
argument_list|(
name|Terms
operator|.
name|Bucket
operator|::
name|getDocCount
argument_list|)
operator|.
name|sum
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedTotalDocCount
argument_list|,
name|reducedTotalDocCount
argument_list|)
expr_stmt|;
block|}
DECL|method|toCounts
specifier|private
specifier|static
name|Map
argument_list|<
name|Object
argument_list|,
name|Long
argument_list|>
name|toCounts
parameter_list|(
name|Stream
argument_list|<
name|?
extends|extends
name|Terms
operator|.
name|Bucket
argument_list|>
name|buckets
parameter_list|)
block|{
return|return
name|buckets
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toMap
argument_list|(
name|Terms
operator|.
name|Bucket
operator|::
name|getKey
argument_list|,
name|Terms
operator|.
name|Bucket
operator|::
name|getDocCount
argument_list|,
name|Long
operator|::
name|sum
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

