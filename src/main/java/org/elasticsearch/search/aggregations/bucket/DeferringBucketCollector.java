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
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|Aggregator
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
name|BucketCollector
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
name|LeafBucketCollector
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

begin_comment
comment|/**  * A {@link BucketCollector} that records collected doc IDs and buckets and  * allows to replay a subset of the collected buckets.  */
end_comment

begin_class
DECL|class|DeferringBucketCollector
specifier|public
specifier|abstract
class|class
name|DeferringBucketCollector
extends|extends
name|BucketCollector
block|{
DECL|field|collector
specifier|private
name|BucketCollector
name|collector
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|DeferringBucketCollector
specifier|public
name|DeferringBucketCollector
parameter_list|()
block|{}
comment|/** Set the deferred collectors. */
DECL|method|setDeferredCollector
specifier|public
name|void
name|setDeferredCollector
parameter_list|(
name|Iterable
argument_list|<
name|BucketCollector
argument_list|>
name|deferredCollectors
parameter_list|)
block|{
name|this
operator|.
name|collector
operator|=
name|BucketCollector
operator|.
name|wrap
argument_list|(
name|deferredCollectors
argument_list|)
expr_stmt|;
block|}
DECL|method|replay
specifier|public
specifier|final
name|void
name|replay
parameter_list|(
name|long
modifier|...
name|selectedBuckets
parameter_list|)
throws|throws
name|IOException
block|{
name|prepareSelectedBuckets
argument_list|(
name|selectedBuckets
argument_list|)
expr_stmt|;
block|}
DECL|method|prepareSelectedBuckets
specifier|public
specifier|abstract
name|void
name|prepareSelectedBuckets
parameter_list|(
name|long
modifier|...
name|selectedBuckets
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Wrap the provided aggregator so that it behaves (almost) as if it had      * been collected directly.      */
DECL|method|wrap
specifier|public
name|Aggregator
name|wrap
parameter_list|(
specifier|final
name|Aggregator
name|in
parameter_list|)
block|{
return|return
operator|new
name|WrappedAggregator
argument_list|(
name|in
argument_list|)
return|;
block|}
DECL|class|WrappedAggregator
specifier|protected
class|class
name|WrappedAggregator
extends|extends
name|Aggregator
block|{
DECL|field|in
specifier|private
name|Aggregator
name|in
decl_stmt|;
DECL|method|WrappedAggregator
name|WrappedAggregator
parameter_list|(
name|Aggregator
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
name|in
operator|.
name|needsScores
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|in
operator|.
name|name
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parent
specifier|public
name|Aggregator
name|parent
parameter_list|()
block|{
return|return
name|in
operator|.
name|parent
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|context
specifier|public
name|AggregationContext
name|context
parameter_list|()
block|{
return|return
name|in
operator|.
name|context
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|subAggregator
specifier|public
name|Aggregator
name|subAggregator
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|in
operator|.
name|subAggregator
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|buildAggregation
specifier|public
name|InternalAggregation
name|buildAggregation
parameter_list|(
name|long
name|bucket
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|buildAggregation
argument_list|(
name|bucket
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|buildEmptyAggregation
specifier|public
name|InternalAggregation
name|buildEmptyAggregation
parameter_list|()
block|{
return|return
name|in
operator|.
name|buildEmptyAggregation
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafBucketCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Deferred collectors cannot be collected directly. They must be collected through the recording wrapper."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|preCollection
specifier|public
name|void
name|preCollection
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Deferred collectors cannot be collected directly. They must be collected through the recording wrapper."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|postCollection
specifier|public
name|void
name|postCollection
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Deferred collectors cannot be collected directly. They must be collected through the recording wrapper."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

