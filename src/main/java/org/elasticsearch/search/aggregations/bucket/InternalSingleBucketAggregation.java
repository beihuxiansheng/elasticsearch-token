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
name|reducers
operator|.
name|Reducer
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

begin_comment
comment|/**  * A base class for all the single bucket aggregations.  */
end_comment

begin_class
DECL|class|InternalSingleBucketAggregation
specifier|public
specifier|abstract
class|class
name|InternalSingleBucketAggregation
extends|extends
name|InternalAggregation
implements|implements
name|SingleBucketAggregation
block|{
DECL|field|docCount
specifier|private
name|long
name|docCount
decl_stmt|;
DECL|field|aggregations
specifier|private
name|InternalAggregations
name|aggregations
decl_stmt|;
DECL|method|InternalSingleBucketAggregation
specifier|protected
name|InternalSingleBucketAggregation
parameter_list|()
block|{}
comment|// for serialization
comment|/**      * Creates a single bucket aggregation.      *      * @param name          The aggregation name.      * @param docCount      The document count in the single bucket.      * @param aggregations  The already built sub-aggregations that are associated with the bucket.      */
DECL|method|InternalSingleBucketAggregation
specifier|protected
name|InternalSingleBucketAggregation
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|docCount
parameter_list|,
name|InternalAggregations
name|aggregations
parameter_list|,
name|List
argument_list|<
name|Reducer
argument_list|>
name|reducers
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
name|reducers
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
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
name|InternalAggregations
name|getAggregations
parameter_list|()
block|{
return|return
name|aggregations
return|;
block|}
comment|/**      * Create a<b>new</b> empty sub aggregation. This must be a new instance on each call.      */
DECL|method|newAggregation
specifier|protected
specifier|abstract
name|InternalSingleBucketAggregation
name|newAggregation
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|docCount
parameter_list|,
name|InternalAggregations
name|subAggregations
parameter_list|)
function_decl|;
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
name|long
name|docCount
init|=
literal|0L
decl_stmt|;
name|List
argument_list|<
name|InternalAggregations
argument_list|>
name|subAggregationsList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|aggregations
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|InternalAggregation
name|aggregation
range|:
name|aggregations
control|)
block|{
assert|assert
name|aggregation
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|getName
argument_list|()
argument_list|)
assert|;
name|docCount
operator|+=
operator|(
operator|(
name|InternalSingleBucketAggregation
operator|)
name|aggregation
operator|)
operator|.
name|docCount
expr_stmt|;
name|subAggregationsList
operator|.
name|add
argument_list|(
operator|(
operator|(
name|InternalSingleBucketAggregation
operator|)
name|aggregation
operator|)
operator|.
name|aggregations
argument_list|)
expr_stmt|;
block|}
specifier|final
name|InternalAggregations
name|aggs
init|=
name|InternalAggregations
operator|.
name|reduce
argument_list|(
name|subAggregationsList
argument_list|,
name|reduceContext
argument_list|)
decl_stmt|;
return|return
name|newAggregation
argument_list|(
name|getName
argument_list|()
argument_list|,
name|docCount
argument_list|,
name|aggs
argument_list|)
return|;
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
if|if
condition|(
name|path
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
else|else
block|{
name|String
name|aggName
init|=
name|path
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggName
operator|.
name|equals
argument_list|(
literal|"_count"
argument_list|)
condition|)
block|{
if|if
condition|(
name|path
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"_count must be the last element in the path"
argument_list|)
throw|;
block|}
return|return
name|getDocCount
argument_list|()
return|;
block|}
name|InternalAggregation
name|aggregation
init|=
name|aggregations
operator|.
name|get
argument_list|(
name|aggName
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggregation
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot find an aggregation named ["
operator|+
name|aggName
operator|+
literal|"] in ["
operator|+
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|aggregation
operator|.
name|getProperty
argument_list|(
name|path
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|path
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|doReadFrom
specifier|protected
name|void
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|docCount
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|aggregations
operator|=
name|InternalAggregations
operator|.
name|readAggregations
argument_list|(
name|in
argument_list|)
expr_stmt|;
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
name|writeVLong
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
name|aggregations
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
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
name|builder
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|DOC_COUNT
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
name|aggregations
operator|.
name|toXContentInternal
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

