begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.percentiles
package|package
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
name|metrics
operator|.
name|percentiles
operator|.
name|hdr
operator|.
name|HDRPercentileRanksAggregatorFactory
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
name|tdigest
operator|.
name|InternalTDigestPercentileRanks
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
name|tdigest
operator|.
name|TDigestPercentileRanksAggregatorFactory
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
name|ValuesSource
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
name|ValuesSourceAggregatorFactory
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
name|ValuesSourceConfig
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
name|ValuesSourceType
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
name|ValuesSource
operator|.
name|Numeric
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
name|ValuesSourceAggregatorBuilder
operator|.
name|LeafOnly
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
name|Arrays
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
DECL|class|PercentileRanksAggregatorBuilder
specifier|public
class|class
name|PercentileRanksAggregatorBuilder
extends|extends
name|LeafOnly
argument_list|<
name|ValuesSource
operator|.
name|Numeric
argument_list|,
name|PercentileRanksAggregatorBuilder
argument_list|>
block|{
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|PercentileRanksAggregatorBuilder
name|PROTOTYPE
init|=
operator|new
name|PercentileRanksAggregatorBuilder
argument_list|(
literal|""
argument_list|)
decl_stmt|;
DECL|field|values
specifier|private
name|double
index|[]
name|values
decl_stmt|;
DECL|field|method
specifier|private
name|PercentilesMethod
name|method
init|=
name|PercentilesMethod
operator|.
name|TDIGEST
decl_stmt|;
DECL|field|numberOfSignificantValueDigits
specifier|private
name|int
name|numberOfSignificantValueDigits
init|=
literal|3
decl_stmt|;
DECL|field|compression
specifier|private
name|double
name|compression
init|=
literal|100.0
decl_stmt|;
DECL|field|keyed
specifier|private
name|boolean
name|keyed
init|=
literal|true
decl_stmt|;
DECL|method|PercentileRanksAggregatorBuilder
specifier|public
name|PercentileRanksAggregatorBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|InternalTDigestPercentileRanks
operator|.
name|TYPE
argument_list|,
name|ValuesSourceType
operator|.
name|NUMERIC
argument_list|,
name|ValueType
operator|.
name|NUMERIC
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the values to compute percentiles from.      */
DECL|method|values
specifier|public
name|PercentileRanksAggregatorBuilder
name|values
parameter_list|(
name|double
modifier|...
name|values
parameter_list|)
block|{
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[values] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|double
index|[]
name|sortedValues
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|values
argument_list|,
name|values
operator|.
name|length
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|sortedValues
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|sortedValues
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the values to compute percentiles from.      */
DECL|method|values
specifier|public
name|double
index|[]
name|values
parameter_list|()
block|{
return|return
name|values
return|;
block|}
comment|/**      * Set whether the XContent response should be keyed      */
DECL|method|keyed
specifier|public
name|PercentileRanksAggregatorBuilder
name|keyed
parameter_list|(
name|boolean
name|keyed
parameter_list|)
block|{
name|this
operator|.
name|keyed
operator|=
name|keyed
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get whether the XContent response should be keyed      */
DECL|method|keyed
specifier|public
name|boolean
name|keyed
parameter_list|()
block|{
return|return
name|keyed
return|;
block|}
comment|/**      * Expert: set the number of significant digits in the values. Only relevant      * when using {@link PercentilesMethod#HDR}.      */
DECL|method|numberOfSignificantValueDigits
specifier|public
name|PercentileRanksAggregatorBuilder
name|numberOfSignificantValueDigits
parameter_list|(
name|int
name|numberOfSignificantValueDigits
parameter_list|)
block|{
if|if
condition|(
name|numberOfSignificantValueDigits
argument_list|<
literal|0
operator|||
name|numberOfSignificantValueDigits
argument_list|>
literal|5
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[numberOfSignificantValueDigits] must be between 0 and 5: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|numberOfSignificantValueDigits
operator|=
name|numberOfSignificantValueDigits
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Expert: get the number of significant digits in the values. Only relevant      * when using {@link PercentilesMethod#HDR}.      */
DECL|method|numberOfSignificantValueDigits
specifier|public
name|int
name|numberOfSignificantValueDigits
parameter_list|()
block|{
return|return
name|numberOfSignificantValueDigits
return|;
block|}
comment|/**      * Expert: set the compression. Higher values improve accuracy but also      * memory usage. Only relevant when using {@link PercentilesMethod#TDIGEST}.      */
DECL|method|compression
specifier|public
name|PercentileRanksAggregatorBuilder
name|compression
parameter_list|(
name|double
name|compression
parameter_list|)
block|{
if|if
condition|(
name|compression
operator|<
literal|0.0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[compression] must be greater than or equal to 0. Found ["
operator|+
name|compression
operator|+
literal|"] in ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|compression
operator|=
name|compression
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Expert: get the compression. Higher values improve accuracy but also      * memory usage. Only relevant when using {@link PercentilesMethod#TDIGEST}.      */
DECL|method|compression
specifier|public
name|double
name|compression
parameter_list|()
block|{
return|return
name|compression
return|;
block|}
DECL|method|method
specifier|public
name|PercentileRanksAggregatorBuilder
name|method
parameter_list|(
name|PercentilesMethod
name|method
parameter_list|)
block|{
if|if
condition|(
name|method
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[method] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|method
operator|=
name|method
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|method
specifier|public
name|PercentilesMethod
name|method
parameter_list|()
block|{
return|return
name|method
return|;
block|}
annotation|@
name|Override
DECL|method|innerBuild
specifier|protected
name|ValuesSourceAggregatorFactory
argument_list|<
name|Numeric
argument_list|,
name|?
argument_list|>
name|innerBuild
parameter_list|(
name|AggregationContext
name|context
parameter_list|,
name|ValuesSourceConfig
argument_list|<
name|Numeric
argument_list|>
name|config
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
switch|switch
condition|(
name|method
condition|)
block|{
case|case
name|TDIGEST
case|:
return|return
operator|new
name|TDigestPercentileRanksAggregatorFactory
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|config
argument_list|,
name|values
argument_list|,
name|compression
argument_list|,
name|keyed
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
case|case
name|HDR
case|:
return|return
operator|new
name|HDRPercentileRanksAggregatorFactory
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|config
argument_list|,
name|values
argument_list|,
name|numberOfSignificantValueDigits
argument_list|,
name|keyed
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
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Illegal method ["
operator|+
name|method
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|innerReadFrom
specifier|protected
name|PercentileRanksAggregatorBuilder
name|innerReadFrom
parameter_list|(
name|String
name|name
parameter_list|,
name|ValuesSourceType
name|valuesSourceType
parameter_list|,
name|ValueType
name|targetValueType
parameter_list|,
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|PercentileRanksAggregatorBuilder
name|factory
init|=
operator|new
name|PercentileRanksAggregatorBuilder
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|factory
operator|.
name|values
operator|=
name|in
operator|.
name|readDoubleArray
argument_list|()
expr_stmt|;
name|factory
operator|.
name|keyed
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|factory
operator|.
name|numberOfSignificantValueDigits
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|factory
operator|.
name|compression
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|factory
operator|.
name|method
operator|=
name|PercentilesMethod
operator|.
name|TDIGEST
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|factory
return|;
block|}
annotation|@
name|Override
DECL|method|innerWriteTo
specifier|protected
name|void
name|innerWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeDoubleArray
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|keyed
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|numberOfSignificantValueDigits
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|compression
argument_list|)
expr_stmt|;
name|method
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
specifier|protected
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
name|PercentileRanksParser
operator|.
name|VALUES_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|AbstractPercentilesParser
operator|.
name|KEYED_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|keyed
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|method
operator|==
name|PercentilesMethod
operator|.
name|TDIGEST
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|AbstractPercentilesParser
operator|.
name|COMPRESSION_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|compression
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|field
argument_list|(
name|AbstractPercentilesParser
operator|.
name|NUMBER_SIGNIFICANT_DIGITS_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|numberOfSignificantValueDigits
argument_list|)
expr_stmt|;
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
annotation|@
name|Override
DECL|method|innerEquals
specifier|protected
name|boolean
name|innerEquals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|PercentileRanksAggregatorBuilder
name|other
init|=
operator|(
name|PercentileRanksAggregatorBuilder
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|method
argument_list|,
name|other
operator|.
name|method
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|equalSettings
init|=
literal|false
decl_stmt|;
switch|switch
condition|(
name|method
condition|)
block|{
case|case
name|HDR
case|:
name|equalSettings
operator|=
name|Objects
operator|.
name|equals
argument_list|(
name|numberOfSignificantValueDigits
argument_list|,
name|other
operator|.
name|numberOfSignificantValueDigits
argument_list|)
expr_stmt|;
break|break;
case|case
name|TDIGEST
case|:
name|equalSettings
operator|=
name|Objects
operator|.
name|equals
argument_list|(
name|compression
argument_list|,
name|other
operator|.
name|compression
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Illegal method ["
operator|+
name|method
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|equalSettings
operator|&&
name|Objects
operator|.
name|deepEquals
argument_list|(
name|values
argument_list|,
name|other
operator|.
name|values
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
name|method
argument_list|,
name|other
operator|.
name|method
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|innerHashCode
specifier|protected
name|int
name|innerHashCode
parameter_list|()
block|{
switch|switch
condition|(
name|method
condition|)
block|{
case|case
name|HDR
case|:
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|Arrays
operator|.
name|hashCode
argument_list|(
name|values
argument_list|)
argument_list|,
name|keyed
argument_list|,
name|numberOfSignificantValueDigits
argument_list|,
name|method
argument_list|)
return|;
case|case
name|TDIGEST
case|:
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|Arrays
operator|.
name|hashCode
argument_list|(
name|values
argument_list|)
argument_list|,
name|keyed
argument_list|,
name|compression
argument_list|,
name|method
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Illegal method ["
operator|+
name|method
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

