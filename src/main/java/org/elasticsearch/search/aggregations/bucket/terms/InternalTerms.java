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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cache
operator|.
name|recycler
operator|.
name|CacheRecycler
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
name|ToXContent
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
name|Aggregations
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
name|InternalAggregation
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
name|bucket
operator|.
name|terms
operator|.
name|support
operator|.
name|BucketPriorityQueue
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
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|InternalTerms
specifier|public
specifier|abstract
class|class
name|InternalTerms
extends|extends
name|InternalAggregation
implements|implements
name|Terms
implements|,
name|ToXContent
implements|,
name|Streamable
block|{
DECL|class|Bucket
specifier|public
specifier|static
specifier|abstract
class|class
name|Bucket
extends|extends
name|Terms
operator|.
name|Bucket
block|{
DECL|field|bucketOrd
name|long
name|bucketOrd
decl_stmt|;
DECL|field|docCount
specifier|protected
name|long
name|docCount
decl_stmt|;
DECL|field|aggregations
specifier|protected
name|InternalAggregations
name|aggregations
decl_stmt|;
DECL|method|Bucket
specifier|protected
name|Bucket
parameter_list|(
name|long
name|docCount
parameter_list|,
name|InternalAggregations
name|aggregations
parameter_list|)
block|{
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|aggregations
operator|=
name|aggregations
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
name|long
name|getDocCount
parameter_list|()
block|{
return|return
name|docCount
return|;
block|}
annotation|@
name|Override
DECL|method|getAggregations
specifier|public
name|Aggregations
name|getAggregations
parameter_list|()
block|{
return|return
name|aggregations
return|;
block|}
DECL|method|reduce
specifier|public
name|Bucket
name|reduce
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|Bucket
argument_list|>
name|buckets
parameter_list|,
name|CacheRecycler
name|cacheRecycler
parameter_list|)
block|{
if|if
condition|(
name|buckets
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|Bucket
name|bucket
init|=
name|buckets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|bucket
operator|.
name|aggregations
operator|.
name|reduce
argument_list|(
name|cacheRecycler
argument_list|)
expr_stmt|;
return|return
name|bucket
return|;
block|}
name|Bucket
name|reduced
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|InternalAggregations
argument_list|>
name|aggregationsList
init|=
operator|new
name|ArrayList
argument_list|<
name|InternalAggregations
argument_list|>
argument_list|(
name|buckets
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Bucket
name|bucket
range|:
name|buckets
control|)
block|{
if|if
condition|(
name|reduced
operator|==
literal|null
condition|)
block|{
name|reduced
operator|=
name|bucket
expr_stmt|;
block|}
else|else
block|{
name|reduced
operator|.
name|docCount
operator|+=
name|bucket
operator|.
name|docCount
expr_stmt|;
block|}
name|aggregationsList
operator|.
name|add
argument_list|(
name|bucket
operator|.
name|aggregations
argument_list|)
expr_stmt|;
block|}
name|reduced
operator|.
name|aggregations
operator|=
name|InternalAggregations
operator|.
name|reduce
argument_list|(
name|aggregationsList
argument_list|,
name|cacheRecycler
argument_list|)
expr_stmt|;
return|return
name|reduced
return|;
block|}
block|}
DECL|field|order
specifier|protected
name|InternalOrder
name|order
decl_stmt|;
DECL|field|requiredSize
specifier|protected
name|int
name|requiredSize
decl_stmt|;
DECL|field|minDocCount
specifier|protected
name|long
name|minDocCount
decl_stmt|;
DECL|field|buckets
specifier|protected
name|Collection
argument_list|<
name|Bucket
argument_list|>
name|buckets
decl_stmt|;
DECL|field|bucketMap
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Bucket
argument_list|>
name|bucketMap
decl_stmt|;
DECL|method|InternalTerms
specifier|protected
name|InternalTerms
parameter_list|()
block|{}
comment|// for serialization
DECL|method|InternalTerms
specifier|protected
name|InternalTerms
parameter_list|(
name|String
name|name
parameter_list|,
name|InternalOrder
name|order
parameter_list|,
name|int
name|requiredSize
parameter_list|,
name|long
name|minDocCount
parameter_list|,
name|Collection
argument_list|<
name|Bucket
argument_list|>
name|buckets
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
name|this
operator|.
name|requiredSize
operator|=
name|requiredSize
expr_stmt|;
name|this
operator|.
name|minDocCount
operator|=
name|minDocCount
expr_stmt|;
name|this
operator|.
name|buckets
operator|=
name|buckets
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBuckets
specifier|public
name|Collection
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
name|getBuckets
parameter_list|()
block|{
name|Object
name|o
init|=
name|buckets
decl_stmt|;
return|return
operator|(
name|Collection
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
operator|)
name|o
return|;
block|}
annotation|@
name|Override
DECL|method|getBucketByKey
specifier|public
name|Terms
operator|.
name|Bucket
name|getBucketByKey
parameter_list|(
name|String
name|term
parameter_list|)
block|{
if|if
condition|(
name|bucketMap
operator|==
literal|null
condition|)
block|{
name|bucketMap
operator|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
name|buckets
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Bucket
name|bucket
range|:
name|buckets
control|)
block|{
name|bucketMap
operator|.
name|put
argument_list|(
name|bucket
operator|.
name|getKey
argument_list|()
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|bucketMap
operator|.
name|get
argument_list|(
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|reduce
specifier|public
name|InternalTerms
name|reduce
parameter_list|(
name|ReduceContext
name|reduceContext
parameter_list|)
block|{
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggregations
init|=
name|reduceContext
operator|.
name|aggregations
argument_list|()
decl_stmt|;
if|if
condition|(
name|aggregations
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|InternalTerms
name|terms
init|=
operator|(
name|InternalTerms
operator|)
name|aggregations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|terms
operator|.
name|trimExcessEntries
argument_list|(
name|reduceContext
operator|.
name|cacheRecycler
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|terms
return|;
block|}
name|InternalTerms
name|reduced
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|Text
argument_list|,
name|List
argument_list|<
name|InternalTerms
operator|.
name|Bucket
argument_list|>
argument_list|>
name|buckets
init|=
literal|null
decl_stmt|;
for|for
control|(
name|InternalAggregation
name|aggregation
range|:
name|aggregations
control|)
block|{
name|InternalTerms
name|terms
init|=
operator|(
name|InternalTerms
operator|)
name|aggregation
decl_stmt|;
if|if
condition|(
name|terms
operator|instanceof
name|UnmappedTerms
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|reduced
operator|==
literal|null
condition|)
block|{
name|reduced
operator|=
name|terms
expr_stmt|;
block|}
if|if
condition|(
name|buckets
operator|==
literal|null
condition|)
block|{
name|buckets
operator|=
operator|new
name|HashMap
argument_list|<
name|Text
argument_list|,
name|List
argument_list|<
name|Bucket
argument_list|>
argument_list|>
argument_list|(
name|terms
operator|.
name|buckets
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Bucket
name|bucket
range|:
name|terms
operator|.
name|buckets
control|)
block|{
name|List
argument_list|<
name|Bucket
argument_list|>
name|existingBuckets
init|=
name|buckets
operator|.
name|get
argument_list|(
name|bucket
operator|.
name|getKeyAsText
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingBuckets
operator|==
literal|null
condition|)
block|{
name|existingBuckets
operator|=
operator|new
name|ArrayList
argument_list|<
name|Bucket
argument_list|>
argument_list|(
name|aggregations
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|buckets
operator|.
name|put
argument_list|(
name|bucket
operator|.
name|getKeyAsText
argument_list|()
argument_list|,
name|existingBuckets
argument_list|)
expr_stmt|;
block|}
name|existingBuckets
operator|.
name|add
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|reduced
operator|==
literal|null
condition|)
block|{
comment|// there are only unmapped terms, so we just return the first one (no need to reduce)
return|return
operator|(
name|UnmappedTerms
operator|)
name|aggregations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|final
name|int
name|size
init|=
name|Math
operator|.
name|min
argument_list|(
name|requiredSize
argument_list|,
name|buckets
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|BucketPriorityQueue
name|ordered
init|=
operator|new
name|BucketPriorityQueue
argument_list|(
name|size
argument_list|,
name|order
operator|.
name|comparator
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Text
argument_list|,
name|List
argument_list|<
name|Bucket
argument_list|>
argument_list|>
name|entry
range|:
name|buckets
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|Bucket
argument_list|>
name|sameTermBuckets
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
specifier|final
name|Bucket
name|b
init|=
name|sameTermBuckets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reduce
argument_list|(
name|sameTermBuckets
argument_list|,
name|reduceContext
operator|.
name|cacheRecycler
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|b
operator|.
name|docCount
operator|>=
name|minDocCount
condition|)
block|{
name|ordered
operator|.
name|insertWithOverflow
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
name|Bucket
index|[]
name|list
init|=
operator|new
name|Bucket
index|[
name|ordered
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|ordered
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|list
index|[
name|i
index|]
operator|=
operator|(
name|Bucket
operator|)
name|ordered
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
name|reduced
operator|.
name|buckets
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|list
argument_list|)
expr_stmt|;
return|return
name|reduced
return|;
block|}
DECL|method|trimExcessEntries
specifier|final
name|void
name|trimExcessEntries
parameter_list|(
name|CacheRecycler
name|cacheRecycler
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Bucket
argument_list|>
name|newBuckets
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Bucket
name|b
range|:
name|buckets
control|)
block|{
if|if
condition|(
name|newBuckets
operator|.
name|size
argument_list|()
operator|>=
name|requiredSize
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|b
operator|.
name|docCount
operator|>=
name|minDocCount
condition|)
block|{
name|newBuckets
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|b
operator|.
name|aggregations
operator|.
name|reduce
argument_list|(
name|cacheRecycler
argument_list|)
expr_stmt|;
block|}
block|}
name|buckets
operator|=
name|newBuckets
expr_stmt|;
block|}
comment|// 0 actually means unlimited
DECL|method|readSize
specifier|protected
specifier|static
name|int
name|readSize
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
return|return
name|size
operator|==
literal|0
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|size
return|;
block|}
DECL|method|writeSize
specifier|protected
specifier|static
name|void
name|writeSize
parameter_list|(
name|int
name|size
parameter_list|,
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|size
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|size
operator|=
literal|0
expr_stmt|;
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

