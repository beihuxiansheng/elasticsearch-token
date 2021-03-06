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
name|aggregations
operator|.
name|bucket
operator|.
name|MultiBucketsAggregation
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

begin_class
DECL|class|InternalMultiBucketAggregation
specifier|public
specifier|abstract
class|class
name|InternalMultiBucketAggregation
parameter_list|<
name|A
extends|extends
name|InternalMultiBucketAggregation
parameter_list|,
name|B
extends|extends
name|InternalMultiBucketAggregation
operator|.
name|InternalBucket
parameter_list|>
extends|extends
name|InternalAggregation
implements|implements
name|MultiBucketsAggregation
block|{
DECL|method|InternalMultiBucketAggregation
specifier|public
name|InternalMultiBucketAggregation
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
name|super
argument_list|(
name|name
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|InternalMultiBucketAggregation
specifier|protected
name|InternalMultiBucketAggregation
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new copy of this {@link Aggregation} with the same settings as      * this {@link Aggregation} and contains the provided buckets.      *      * @param buckets      *            the buckets to use in the new {@link Aggregation}      * @return the new {@link Aggregation}      */
DECL|method|create
specifier|public
specifier|abstract
name|A
name|create
parameter_list|(
name|List
argument_list|<
name|B
argument_list|>
name|buckets
parameter_list|)
function_decl|;
comment|/**      * Create a new {@link InternalBucket} using the provided prototype bucket      * and aggregations.      *      * @param aggregations      *            the aggregations for the new bucket      * @param prototype      *            the bucket to use as a prototype      * @return the new bucket      */
DECL|method|createBucket
specifier|public
specifier|abstract
name|B
name|createBucket
parameter_list|(
name|InternalAggregations
name|aggregations
parameter_list|,
name|B
name|prototype
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|getBuckets
specifier|public
specifier|abstract
name|List
argument_list|<
name|?
extends|extends
name|InternalBucket
argument_list|>
name|getBuckets
parameter_list|()
function_decl|;
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
elseif|else
if|if
condition|(
name|path
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
literal|"_bucket_count"
argument_list|)
condition|)
block|{
return|return
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
else|else
block|{
name|List
argument_list|<
name|?
extends|extends
name|InternalBucket
argument_list|>
name|buckets
init|=
name|getBuckets
argument_list|()
decl_stmt|;
name|Object
index|[]
name|propertyArray
init|=
operator|new
name|Object
index|[
name|buckets
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
literal|0
init|;
name|i
operator|<
name|buckets
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|propertyArray
index|[
name|i
index|]
operator|=
name|buckets
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getProperty
argument_list|(
name|getName
argument_list|()
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|propertyArray
return|;
block|}
block|}
DECL|class|InternalBucket
specifier|public
specifier|abstract
specifier|static
class|class
name|InternalBucket
implements|implements
name|Bucket
implements|,
name|Writeable
block|{
DECL|method|getProperty
specifier|public
name|Object
name|getProperty
parameter_list|(
name|String
name|containingAggName
parameter_list|,
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
name|Aggregations
name|aggregations
init|=
name|getAggregations
argument_list|()
decl_stmt|;
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
name|InvalidAggregationPathException
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
elseif|else
if|if
condition|(
name|aggName
operator|.
name|equals
argument_list|(
literal|"_key"
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
name|InvalidAggregationPathException
argument_list|(
literal|"_key must be the last element in the path"
argument_list|)
throw|;
block|}
return|return
name|getKey
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
name|InvalidAggregationPathException
argument_list|(
literal|"Cannot find an aggregation named ["
operator|+
name|aggName
operator|+
literal|"] in ["
operator|+
name|containingAggName
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
block|}
end_class

end_unit

