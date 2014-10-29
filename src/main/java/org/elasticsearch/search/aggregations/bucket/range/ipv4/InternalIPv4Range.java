begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.range.ipv4
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
name|range
operator|.
name|ipv4
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
name|inject
operator|.
name|internal
operator|.
name|Nullable
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
name|search
operator|.
name|aggregations
operator|.
name|AggregationStreams
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
name|BucketStreamContext
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
name|BucketStreams
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
name|range
operator|.
name|InternalRange
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
name|format
operator|.
name|ValueFormatter
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|InternalIPv4Range
specifier|public
class|class
name|InternalIPv4Range
extends|extends
name|InternalRange
argument_list|<
name|InternalIPv4Range
operator|.
name|Bucket
argument_list|>
implements|implements
name|IPv4Range
block|{
DECL|field|MAX_IP
specifier|public
specifier|static
specifier|final
name|long
name|MAX_IP
init|=
literal|4294967296l
decl_stmt|;
DECL|field|TYPE
specifier|public
specifier|final
specifier|static
name|Type
name|TYPE
init|=
operator|new
name|Type
argument_list|(
literal|"ip_range"
argument_list|,
literal|"iprange"
argument_list|)
decl_stmt|;
DECL|field|STREAM
specifier|private
specifier|final
specifier|static
name|AggregationStreams
operator|.
name|Stream
name|STREAM
init|=
operator|new
name|AggregationStreams
operator|.
name|Stream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InternalIPv4Range
name|readResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|InternalIPv4Range
name|range
init|=
operator|new
name|InternalIPv4Range
argument_list|()
decl_stmt|;
name|range
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|range
return|;
block|}
block|}
decl_stmt|;
DECL|field|BUCKET_STREAM
specifier|private
specifier|final
specifier|static
name|BucketStreams
operator|.
name|Stream
argument_list|<
name|Bucket
argument_list|>
name|BUCKET_STREAM
init|=
operator|new
name|BucketStreams
operator|.
name|Stream
argument_list|<
name|Bucket
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Bucket
name|readResult
parameter_list|(
name|StreamInput
name|in
parameter_list|,
name|BucketStreamContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Bucket
name|buckets
init|=
operator|new
name|Bucket
argument_list|(
name|context
operator|.
name|keyed
argument_list|()
argument_list|)
decl_stmt|;
name|buckets
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|buckets
return|;
block|}
annotation|@
name|Override
specifier|public
name|BucketStreamContext
name|getBucketStreamContext
parameter_list|(
name|Bucket
name|bucket
parameter_list|)
block|{
name|BucketStreamContext
name|context
init|=
operator|new
name|BucketStreamContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|keyed
argument_list|(
name|bucket
operator|.
name|keyed
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|context
return|;
block|}
block|}
decl_stmt|;
DECL|method|registerStream
specifier|public
specifier|static
name|void
name|registerStream
parameter_list|()
block|{
name|AggregationStreams
operator|.
name|registerStream
argument_list|(
name|STREAM
argument_list|,
name|TYPE
operator|.
name|stream
argument_list|()
argument_list|)
expr_stmt|;
name|BucketStreams
operator|.
name|registerStream
argument_list|(
name|BUCKET_STREAM
argument_list|,
name|TYPE
operator|.
name|stream
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|FACTORY
specifier|public
specifier|static
specifier|final
name|Factory
name|FACTORY
init|=
operator|new
name|Factory
argument_list|()
decl_stmt|;
DECL|class|Bucket
specifier|public
specifier|static
class|class
name|Bucket
extends|extends
name|InternalRange
operator|.
name|Bucket
implements|implements
name|IPv4Range
operator|.
name|Bucket
block|{
DECL|method|Bucket
specifier|public
name|Bucket
parameter_list|(
name|boolean
name|keyed
parameter_list|)
block|{
name|super
argument_list|(
name|keyed
argument_list|,
name|ValueFormatter
operator|.
name|IPv4
argument_list|)
expr_stmt|;
block|}
DECL|method|Bucket
specifier|public
name|Bucket
parameter_list|(
name|String
name|key
parameter_list|,
name|double
name|from
parameter_list|,
name|double
name|to
parameter_list|,
name|long
name|docCount
parameter_list|,
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggregations
parameter_list|,
name|boolean
name|keyed
parameter_list|)
block|{
name|super
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
name|docCount
argument_list|,
operator|new
name|InternalAggregations
argument_list|(
name|aggregations
argument_list|)
argument_list|,
name|keyed
argument_list|,
name|ValueFormatter
operator|.
name|IPv4
argument_list|)
expr_stmt|;
block|}
DECL|method|Bucket
specifier|public
name|Bucket
parameter_list|(
name|String
name|key
parameter_list|,
name|double
name|from
parameter_list|,
name|double
name|to
parameter_list|,
name|long
name|docCount
parameter_list|,
name|InternalAggregations
name|aggregations
parameter_list|,
name|boolean
name|keyed
parameter_list|)
block|{
name|super
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
name|docCount
argument_list|,
name|aggregations
argument_list|,
name|keyed
argument_list|,
name|ValueFormatter
operator|.
name|IPv4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFromAsString
specifier|public
name|String
name|getFromAsString
parameter_list|()
block|{
name|double
name|from
init|=
name|getFrom
argument_list|()
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
return|return
name|Double
operator|.
name|isInfinite
argument_list|(
name|from
argument_list|)
condition|?
literal|null
else|:
name|from
operator|==
literal|0
condition|?
literal|null
else|:
name|ValueFormatter
operator|.
name|IPv4
operator|.
name|format
argument_list|(
name|from
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getToAsString
specifier|public
name|String
name|getToAsString
parameter_list|()
block|{
name|double
name|to
init|=
name|getTo
argument_list|()
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
return|return
name|Double
operator|.
name|isInfinite
argument_list|(
name|to
argument_list|)
condition|?
literal|null
else|:
name|MAX_IP
operator|==
name|to
condition|?
literal|null
else|:
name|ValueFormatter
operator|.
name|IPv4
operator|.
name|format
argument_list|(
name|to
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFactory
specifier|protected
name|InternalRange
operator|.
name|Factory
argument_list|<
name|Bucket
argument_list|,
name|?
argument_list|>
name|getFactory
parameter_list|()
block|{
return|return
name|FACTORY
return|;
block|}
DECL|method|keyed
name|boolean
name|keyed
parameter_list|()
block|{
return|return
name|keyed
return|;
block|}
block|}
DECL|class|Factory
specifier|private
specifier|static
class|class
name|Factory
extends|extends
name|InternalRange
operator|.
name|Factory
argument_list|<
name|InternalIPv4Range
operator|.
name|Bucket
argument_list|,
name|InternalIPv4Range
argument_list|>
block|{
annotation|@
name|Override
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|TYPE
operator|.
name|name
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|InternalIPv4Range
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Bucket
argument_list|>
name|ranges
parameter_list|,
annotation|@
name|Nullable
name|ValueFormatter
name|formatter
parameter_list|,
name|boolean
name|keyed
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
return|return
operator|new
name|InternalIPv4Range
argument_list|(
name|name
argument_list|,
name|ranges
argument_list|,
name|keyed
argument_list|,
name|metaData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createBucket
specifier|public
name|Bucket
name|createBucket
parameter_list|(
name|String
name|key
parameter_list|,
name|double
name|from
parameter_list|,
name|double
name|to
parameter_list|,
name|long
name|docCount
parameter_list|,
name|InternalAggregations
name|aggregations
parameter_list|,
name|boolean
name|keyed
parameter_list|,
annotation|@
name|Nullable
name|ValueFormatter
name|formatter
parameter_list|)
block|{
return|return
operator|new
name|Bucket
argument_list|(
name|key
argument_list|,
name|from
argument_list|,
name|to
argument_list|,
name|docCount
argument_list|,
name|aggregations
argument_list|,
name|keyed
argument_list|)
return|;
block|}
block|}
DECL|method|InternalIPv4Range
specifier|public
name|InternalIPv4Range
parameter_list|()
block|{}
comment|// for serialization
DECL|method|InternalIPv4Range
specifier|public
name|InternalIPv4Range
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|InternalIPv4Range
operator|.
name|Bucket
argument_list|>
name|ranges
parameter_list|,
name|boolean
name|keyed
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
name|ranges
argument_list|,
name|ValueFormatter
operator|.
name|IPv4
argument_list|,
name|keyed
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|getFactory
specifier|protected
name|InternalRange
operator|.
name|Factory
argument_list|<
name|Bucket
argument_list|,
name|?
argument_list|>
name|getFactory
parameter_list|()
block|{
return|return
name|FACTORY
return|;
block|}
block|}
end_class

end_unit

