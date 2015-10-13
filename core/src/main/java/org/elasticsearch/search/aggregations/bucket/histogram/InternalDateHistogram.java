begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.histogram
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
name|histogram
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
name|AggregationExecutionException
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
operator|.
name|Type
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
name|support
operator|.
name|ValueType
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
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTime
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTimeZone
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|InternalDateHistogram
specifier|public
class|class
name|InternalDateHistogram
block|{
DECL|field|HISTOGRAM_FACTORY
specifier|public
specifier|static
specifier|final
name|Factory
name|HISTOGRAM_FACTORY
init|=
operator|new
name|Factory
argument_list|()
decl_stmt|;
DECL|field|TYPE
specifier|final
specifier|static
name|Type
name|TYPE
init|=
operator|new
name|Type
argument_list|(
literal|"date_histogram"
argument_list|,
literal|"dhisto"
argument_list|)
decl_stmt|;
DECL|class|Bucket
specifier|static
class|class
name|Bucket
extends|extends
name|InternalHistogram
operator|.
name|Bucket
block|{
DECL|method|Bucket
name|Bucket
parameter_list|(
name|boolean
name|keyed
parameter_list|,
name|ValueFormatter
name|formatter
parameter_list|,
name|InternalHistogram
operator|.
name|Factory
argument_list|<
name|Bucket
argument_list|>
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|keyed
argument_list|,
name|formatter
argument_list|,
name|factory
argument_list|)
expr_stmt|;
block|}
DECL|method|Bucket
name|Bucket
parameter_list|(
name|long
name|key
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
name|ValueFormatter
name|formatter
parameter_list|,
name|InternalHistogram
operator|.
name|Factory
argument_list|<
name|Bucket
argument_list|>
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|key
argument_list|,
name|docCount
argument_list|,
name|keyed
argument_list|,
name|formatter
argument_list|,
name|factory
argument_list|,
name|aggregations
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getKeyAsString
specifier|public
name|String
name|getKeyAsString
parameter_list|()
block|{
return|return
name|formatter
operator|!=
literal|null
condition|?
name|formatter
operator|.
name|format
argument_list|(
name|key
argument_list|)
else|:
name|ValueFormatter
operator|.
name|DateTime
operator|.
name|DEFAULT
operator|.
name|format
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getKey
specifier|public
name|DateTime
name|getKey
parameter_list|()
block|{
return|return
operator|new
name|DateTime
argument_list|(
name|key
argument_list|,
name|DateTimeZone
operator|.
name|UTC
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getKeyAsString
argument_list|()
return|;
block|}
block|}
DECL|class|Factory
specifier|static
class|class
name|Factory
extends|extends
name|InternalHistogram
operator|.
name|Factory
argument_list|<
name|InternalDateHistogram
operator|.
name|Bucket
argument_list|>
block|{
DECL|method|Factory
name|Factory
parameter_list|()
block|{         }
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
DECL|method|valueType
specifier|public
name|ValueType
name|valueType
parameter_list|()
block|{
return|return
name|ValueType
operator|.
name|DATE
return|;
block|}
annotation|@
name|Override
DECL|method|createBucket
specifier|public
name|InternalDateHistogram
operator|.
name|Bucket
name|createBucket
parameter_list|(
name|InternalAggregations
name|aggregations
parameter_list|,
name|InternalDateHistogram
operator|.
name|Bucket
name|prototype
parameter_list|)
block|{
return|return
operator|new
name|Bucket
argument_list|(
name|prototype
operator|.
name|key
argument_list|,
name|prototype
operator|.
name|docCount
argument_list|,
name|aggregations
argument_list|,
name|prototype
operator|.
name|getKeyed
argument_list|()
argument_list|,
name|prototype
operator|.
name|formatter
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createBucket
specifier|public
name|InternalDateHistogram
operator|.
name|Bucket
name|createBucket
parameter_list|(
name|Object
name|key
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
name|ValueFormatter
name|formatter
parameter_list|)
block|{
if|if
condition|(
name|key
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|new
name|Bucket
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|key
operator|)
operator|.
name|longValue
argument_list|()
argument_list|,
name|docCount
argument_list|,
name|aggregations
argument_list|,
name|keyed
argument_list|,
name|formatter
argument_list|,
name|this
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|key
operator|instanceof
name|DateTime
condition|)
block|{
return|return
operator|new
name|Bucket
argument_list|(
operator|(
operator|(
name|DateTime
operator|)
name|key
operator|)
operator|.
name|getMillis
argument_list|()
argument_list|,
name|docCount
argument_list|,
name|aggregations
argument_list|,
name|keyed
argument_list|,
name|formatter
argument_list|,
name|this
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Expected key of type Number or DateTime but got ["
operator|+
name|key
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|createEmptyBucket
specifier|protected
name|InternalDateHistogram
operator|.
name|Bucket
name|createEmptyBucket
parameter_list|(
name|boolean
name|keyed
parameter_list|,
name|ValueFormatter
name|formatter
parameter_list|)
block|{
return|return
operator|new
name|Bucket
argument_list|(
name|keyed
argument_list|,
name|formatter
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
DECL|method|InternalDateHistogram
specifier|private
name|InternalDateHistogram
parameter_list|()
block|{}
block|}
end_class

end_unit

