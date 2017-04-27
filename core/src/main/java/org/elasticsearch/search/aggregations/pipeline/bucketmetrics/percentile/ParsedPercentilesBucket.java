begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.pipeline.bucketmetrics.percentile
package|package
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
name|bucketmetrics
operator|.
name|percentile
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
name|xcontent
operator|.
name|ObjectParser
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
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
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
name|metrics
operator|.
name|percentiles
operator|.
name|ParsedPercentiles
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
name|metrics
operator|.
name|percentiles
operator|.
name|Percentiles
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_class
DECL|class|ParsedPercentilesBucket
specifier|public
class|class
name|ParsedPercentilesBucket
extends|extends
name|ParsedPercentiles
implements|implements
name|Percentiles
block|{
annotation|@
name|Override
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|PercentilesBucketPipelineAggregationBuilder
operator|.
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|percentile
specifier|public
name|double
name|percentile
parameter_list|(
name|double
name|percent
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|Double
name|value
init|=
name|percentiles
operator|.
name|get
argument_list|(
name|percent
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Percent requested ["
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|percent
argument_list|)
operator|+
literal|"] was not"
operator|+
literal|" one of the computed percentiles. Available keys are: "
operator|+
name|percentiles
operator|.
name|keySet
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|percentileAsString
specifier|public
name|String
name|percentileAsString
parameter_list|(
name|double
name|percent
parameter_list|)
block|{
name|double
name|value
init|=
name|percentile
argument_list|(
name|percent
argument_list|)
decl_stmt|;
comment|// check availability as unformatted value
name|String
name|valueAsString
init|=
name|percentilesAsString
operator|.
name|get
argument_list|(
name|percent
argument_list|)
decl_stmt|;
if|if
condition|(
name|valueAsString
operator|!=
literal|null
condition|)
block|{
return|return
name|valueAsString
return|;
block|}
else|else
block|{
return|return
name|Double
operator|.
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
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
name|startObject
argument_list|(
literal|"values"
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Double
argument_list|,
name|Double
argument_list|>
name|percent
range|:
name|percentiles
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|double
name|value
init|=
name|percent
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|boolean
name|hasValue
init|=
operator|!
operator|(
name|Double
operator|.
name|isNaN
argument_list|(
name|value
argument_list|)
operator|)
decl_stmt|;
name|Double
name|key
init|=
name|percent
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|key
argument_list|)
argument_list|,
name|hasValue
condition|?
name|value
else|:
literal|null
argument_list|)
expr_stmt|;
name|String
name|valueAsString
init|=
name|percentilesAsString
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|hasValue
operator|&&
name|valueAsString
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|key
operator|+
literal|"_as_string"
argument_list|,
name|valueAsString
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|field|PARSER
specifier|private
specifier|static
name|ObjectParser
argument_list|<
name|ParsedPercentilesBucket
argument_list|,
name|Void
argument_list|>
name|PARSER
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
name|ParsedPercentilesBucket
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|true
argument_list|,
name|ParsedPercentilesBucket
operator|::
operator|new
argument_list|)
decl_stmt|;
static|static
block|{
name|ParsedPercentiles
operator|.
name|declarePercentilesFields
argument_list|(
name|PARSER
argument_list|)
expr_stmt|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|ParsedPercentilesBucket
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|ParsedPercentilesBucket
name|aggregation
init|=
name|PARSER
operator|.
name|parse
argument_list|(
name|parser
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|aggregation
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|aggregation
return|;
block|}
block|}
end_class

end_unit

