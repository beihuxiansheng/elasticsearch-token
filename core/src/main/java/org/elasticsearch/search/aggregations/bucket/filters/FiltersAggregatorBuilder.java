begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.filters
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
name|filters
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
name|index
operator|.
name|query
operator|.
name|MatchAllQueryBuilder
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
name|QueryBuilder
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
name|AggregatorBuilder
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
name|AggregatorFactories
operator|.
name|Builder
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
name|AggregatorFactory
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
name|filters
operator|.
name|FiltersAggregator
operator|.
name|KeyedFilter
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
name|support
operator|.
name|AggregationContext
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
name|Arrays
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_class
DECL|class|FiltersAggregatorBuilder
specifier|public
class|class
name|FiltersAggregatorBuilder
extends|extends
name|AggregatorBuilder
argument_list|<
name|FiltersAggregatorBuilder
argument_list|>
block|{
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|FiltersAggregatorBuilder
name|PROTOTYPE
init|=
operator|new
name|FiltersAggregatorBuilder
argument_list|(
literal|""
argument_list|,
operator|new
name|MatchAllQueryBuilder
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|filters
specifier|private
specifier|final
name|List
argument_list|<
name|KeyedFilter
argument_list|>
name|filters
decl_stmt|;
DECL|field|keyed
specifier|private
specifier|final
name|boolean
name|keyed
decl_stmt|;
DECL|field|otherBucket
specifier|private
name|boolean
name|otherBucket
init|=
literal|false
decl_stmt|;
DECL|field|otherBucketKey
specifier|private
name|String
name|otherBucketKey
init|=
literal|"_other_"
decl_stmt|;
comment|/**      * @param name      *            the name of this aggregation      * @param filters      *            the KeyedFilters to use with this aggregation.      */
DECL|method|FiltersAggregatorBuilder
specifier|public
name|FiltersAggregatorBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|KeyedFilter
modifier|...
name|filters
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|filters
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|FiltersAggregatorBuilder
specifier|private
name|FiltersAggregatorBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|KeyedFilter
argument_list|>
name|filters
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|InternalFilters
operator|.
name|TYPE
argument_list|)
expr_stmt|;
comment|// internally we want to have a fixed order of filters, regardless of the order of the filters in the request
name|this
operator|.
name|filters
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|filters
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|this
operator|.
name|filters
argument_list|,
parameter_list|(
name|KeyedFilter
name|kf1
parameter_list|,
name|KeyedFilter
name|kf2
parameter_list|)
lambda|->
name|kf1
operator|.
name|key
argument_list|()
operator|.
name|compareTo
argument_list|(
name|kf2
operator|.
name|key
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyed
operator|=
literal|true
expr_stmt|;
block|}
comment|/**      * @param name      *            the name of this aggregation      * @param filters      *            the filters to use with this aggregation      */
DECL|method|FiltersAggregatorBuilder
specifier|public
name|FiltersAggregatorBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|QueryBuilder
argument_list|<
name|?
argument_list|>
modifier|...
name|filters
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|InternalFilters
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|KeyedFilter
argument_list|>
name|keyedFilters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|filters
operator|.
name|length
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
name|filters
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|keyedFilters
operator|.
name|add
argument_list|(
operator|new
name|KeyedFilter
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
name|filters
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|filters
operator|=
name|keyedFilters
expr_stmt|;
name|this
operator|.
name|keyed
operator|=
literal|false
expr_stmt|;
block|}
comment|/**      * Set whether to include a bucket for documents not matching any filter      */
DECL|method|otherBucket
specifier|public
name|FiltersAggregatorBuilder
name|otherBucket
parameter_list|(
name|boolean
name|otherBucket
parameter_list|)
block|{
name|this
operator|.
name|otherBucket
operator|=
name|otherBucket
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get whether to include a bucket for documents not matching any filter      */
DECL|method|otherBucket
specifier|public
name|boolean
name|otherBucket
parameter_list|()
block|{
return|return
name|otherBucket
return|;
block|}
comment|/**      * Get the filters. This will be an unmodifiable list      */
DECL|method|filters
specifier|public
name|List
argument_list|<
name|KeyedFilter
argument_list|>
name|filters
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|this
operator|.
name|filters
argument_list|)
return|;
block|}
comment|/**      * Set the key to use for the bucket for documents not matching any      * filter.      */
DECL|method|otherBucketKey
specifier|public
name|FiltersAggregatorBuilder
name|otherBucketKey
parameter_list|(
name|String
name|otherBucketKey
parameter_list|)
block|{
if|if
condition|(
name|otherBucketKey
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[otherBucketKey] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|otherBucketKey
operator|=
name|otherBucketKey
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the key to use for the bucket for documents not matching any      * filter.      */
DECL|method|otherBucketKey
specifier|public
name|String
name|otherBucketKey
parameter_list|()
block|{
return|return
name|otherBucketKey
return|;
block|}
annotation|@
name|Override
DECL|method|doBuild
specifier|protected
name|AggregatorFactory
argument_list|<
name|?
argument_list|>
name|doBuild
parameter_list|(
name|AggregationContext
name|context
parameter_list|,
name|AggregatorFactory
argument_list|<
name|?
argument_list|>
name|parent
parameter_list|,
name|Builder
name|subFactoriesBuilder
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FiltersAggregatorFactory
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|filters
argument_list|,
name|keyed
argument_list|,
name|otherBucket
argument_list|,
name|otherBucketKey
argument_list|,
name|context
argument_list|,
name|parent
argument_list|,
name|subFactoriesBuilder
argument_list|,
name|metaData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|internalXContent
specifier|protected
name|XContentBuilder
name|internalXContent
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
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|keyed
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|FiltersAggregator
operator|.
name|FILTERS_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|KeyedFilter
name|keyedFilter
range|:
name|filters
control|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|keyedFilter
operator|.
name|key
argument_list|()
argument_list|,
name|keyedFilter
operator|.
name|filter
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|FiltersAggregator
operator|.
name|FILTERS_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|KeyedFilter
name|keyedFilter
range|:
name|filters
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|keyedFilter
operator|.
name|filter
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
name|FiltersAggregator
operator|.
name|OTHER_BUCKET_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|otherBucket
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|FiltersAggregator
operator|.
name|OTHER_BUCKET_KEY_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|otherBucketKey
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|doReadFrom
specifier|protected
name|FiltersAggregatorBuilder
name|doReadFrom
parameter_list|(
name|String
name|name
parameter_list|,
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|FiltersAggregatorBuilder
name|factory
decl_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|KeyedFilter
argument_list|>
name|filters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|filters
operator|.
name|add
argument_list|(
name|KeyedFilter
operator|.
name|PROTOTYPE
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|factory
operator|=
operator|new
name|FiltersAggregatorBuilder
argument_list|(
name|name
argument_list|,
name|filters
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|QueryBuilder
argument_list|<
name|?
argument_list|>
index|[]
name|filters
init|=
operator|new
name|QueryBuilder
argument_list|<
name|?
argument_list|>
index|[
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|filters
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readQuery
argument_list|()
expr_stmt|;
block|}
name|factory
operator|=
operator|new
name|FiltersAggregatorBuilder
argument_list|(
name|name
argument_list|,
name|filters
argument_list|)
expr_stmt|;
block|}
name|factory
operator|.
name|otherBucket
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|factory
operator|.
name|otherBucketKey
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
return|return
name|factory
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
name|out
operator|.
name|writeBoolean
argument_list|(
name|keyed
argument_list|)
expr_stmt|;
if|if
condition|(
name|keyed
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|filters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|KeyedFilter
name|keyedFilter
range|:
name|filters
control|)
block|{
name|keyedFilter
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|filters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|KeyedFilter
name|keyedFilter
range|:
name|filters
control|)
block|{
name|out
operator|.
name|writeQuery
argument_list|(
name|keyedFilter
operator|.
name|filter
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|otherBucket
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|otherBucketKey
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doHashCode
specifier|protected
name|int
name|doHashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|filters
argument_list|,
name|keyed
argument_list|,
name|otherBucket
argument_list|,
name|otherBucketKey
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|FiltersAggregatorBuilder
name|other
init|=
operator|(
name|FiltersAggregatorBuilder
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|filters
argument_list|,
name|other
operator|.
name|filters
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|keyed
argument_list|,
name|other
operator|.
name|keyed
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|otherBucket
argument_list|,
name|other
operator|.
name|otherBucket
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|otherBucketKey
argument_list|,
name|other
operator|.
name|otherBucketKey
argument_list|)
return|;
block|}
block|}
end_class

end_unit

