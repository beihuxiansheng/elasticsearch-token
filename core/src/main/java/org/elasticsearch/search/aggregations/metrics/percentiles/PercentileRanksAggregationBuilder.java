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
name|ParseField
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
name|index
operator|.
name|query
operator|.
name|QueryParseContext
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
name|AggregationBuilder
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
name|ValuesSourceAggregationBuilder
operator|.
name|LeafOnly
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
name|ValuesSourceParserHelper
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
DECL|class|PercentileRanksAggregationBuilder
specifier|public
class|class
name|PercentileRanksAggregationBuilder
extends|extends
name|LeafOnly
argument_list|<
name|ValuesSource
operator|.
name|Numeric
argument_list|,
name|PercentileRanksAggregationBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|PercentileRanks
operator|.
name|TYPE_NAME
decl_stmt|;
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|Type
name|TYPE
init|=
operator|new
name|Type
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
DECL|field|VALUES_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|VALUES_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"values"
argument_list|)
decl_stmt|;
DECL|class|TDigestOptions
specifier|private
specifier|static
class|class
name|TDigestOptions
block|{
DECL|field|compression
name|Double
name|compression
decl_stmt|;
block|}
DECL|field|TDIGEST_OPTIONS_PARSER
specifier|private
specifier|static
specifier|final
name|ObjectParser
argument_list|<
name|TDigestOptions
argument_list|,
name|QueryParseContext
argument_list|>
name|TDIGEST_OPTIONS_PARSER
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
name|PercentilesMethod
operator|.
name|TDIGEST
operator|.
name|getParseField
argument_list|()
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|TDigestOptions
operator|::
operator|new
argument_list|)
decl_stmt|;
static|static
block|{
name|TDIGEST_OPTIONS_PARSER
operator|.
name|declareDouble
argument_list|(
parameter_list|(
name|opts
parameter_list|,
name|compression
parameter_list|)
lambda|->
name|opts
operator|.
name|compression
operator|=
name|compression
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"compression"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|HDROptions
specifier|private
specifier|static
class|class
name|HDROptions
block|{
DECL|field|numberOfSigDigits
name|Integer
name|numberOfSigDigits
decl_stmt|;
block|}
DECL|field|HDR_OPTIONS_PARSER
specifier|private
specifier|static
specifier|final
name|ObjectParser
argument_list|<
name|HDROptions
argument_list|,
name|QueryParseContext
argument_list|>
name|HDR_OPTIONS_PARSER
init|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
name|PercentilesMethod
operator|.
name|HDR
operator|.
name|getParseField
argument_list|()
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|HDROptions
operator|::
operator|new
argument_list|)
decl_stmt|;
static|static
block|{
name|HDR_OPTIONS_PARSER
operator|.
name|declareInt
argument_list|(
parameter_list|(
name|opts
parameter_list|,
name|numberOfSigDigits
parameter_list|)
lambda|->
name|opts
operator|.
name|numberOfSigDigits
operator|=
name|numberOfSigDigits
argument_list|,
operator|new
name|ParseField
argument_list|(
literal|"number_of_significant_value_digits"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|PARSER
specifier|private
specifier|static
specifier|final
name|ObjectParser
argument_list|<
name|PercentileRanksAggregationBuilder
argument_list|,
name|QueryParseContext
argument_list|>
name|PARSER
decl_stmt|;
static|static
block|{
name|PARSER
operator|=
operator|new
name|ObjectParser
argument_list|<>
argument_list|(
name|PercentileRanksAggregationBuilder
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|ValuesSourceParserHelper
operator|.
name|declareNumericFields
argument_list|(
name|PARSER
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareDoubleArray
argument_list|(
parameter_list|(
name|b
parameter_list|,
name|v
parameter_list|)
lambda|->
name|b
operator|.
name|values
argument_list|(
name|v
operator|.
name|stream
argument_list|()
operator|.
name|mapToDouble
argument_list|(
name|Double
operator|::
name|doubleValue
argument_list|)
operator|.
name|toArray
argument_list|()
argument_list|)
argument_list|,
name|VALUES_FIELD
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareBoolean
argument_list|(
name|PercentileRanksAggregationBuilder
operator|::
name|keyed
argument_list|,
name|PercentilesAggregationBuilder
operator|.
name|KEYED_FIELD
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareField
argument_list|(
parameter_list|(
name|b
parameter_list|,
name|v
parameter_list|)
lambda|->
block|{
name|b
operator|.
name|method
argument_list|(
name|PercentilesMethod
operator|.
name|TDIGEST
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|.
name|compression
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|compression
argument_list|(
name|v
operator|.
name|compression
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|TDIGEST_OPTIONS_PARSER
operator|::
name|parse
argument_list|,
name|PercentilesMethod
operator|.
name|TDIGEST
operator|.
name|getParseField
argument_list|()
argument_list|,
name|ObjectParser
operator|.
name|ValueType
operator|.
name|OBJECT
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareField
argument_list|(
parameter_list|(
name|b
parameter_list|,
name|v
parameter_list|)
lambda|->
block|{
name|b
operator|.
name|method
argument_list|(
name|PercentilesMethod
operator|.
name|HDR
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|.
name|numberOfSigDigits
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|numberOfSignificantValueDigits
argument_list|(
name|v
operator|.
name|numberOfSigDigits
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|HDR_OPTIONS_PARSER
operator|::
name|parse
argument_list|,
name|PercentilesMethod
operator|.
name|HDR
operator|.
name|getParseField
argument_list|()
argument_list|,
name|ObjectParser
operator|.
name|ValueType
operator|.
name|OBJECT
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|public
specifier|static
name|AggregationBuilder
name|parse
parameter_list|(
name|String
name|aggregationName
parameter_list|,
name|QueryParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|PARSER
operator|.
name|parse
argument_list|(
name|context
operator|.
name|parser
argument_list|()
argument_list|,
operator|new
name|PercentileRanksAggregationBuilder
argument_list|(
name|aggregationName
argument_list|)
argument_list|,
name|context
argument_list|)
return|;
block|}
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
DECL|method|PercentileRanksAggregationBuilder
specifier|public
name|PercentileRanksAggregationBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
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
comment|/**      * Read from a stream.      */
DECL|method|PercentileRanksAggregationBuilder
specifier|public
name|PercentileRanksAggregationBuilder
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
argument_list|,
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
name|values
operator|=
name|in
operator|.
name|readDoubleArray
argument_list|()
expr_stmt|;
name|keyed
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|numberOfSignificantValueDigits
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|compression
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|method
operator|=
name|PercentilesMethod
operator|.
name|readFromStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
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
comment|/**      * Set the values to compute percentiles from.      */
DECL|method|values
specifier|public
name|PercentileRanksAggregationBuilder
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
name|PercentileRanksAggregationBuilder
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
name|PercentileRanksAggregationBuilder
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
name|PercentileRanksAggregationBuilder
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
name|PercentileRanksAggregationBuilder
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
operator|+
literal|"]"
argument_list|)
throw|;
block|}
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
name|array
argument_list|(
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
name|PercentilesAggregationBuilder
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
name|toString
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
name|PercentilesAggregationBuilder
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
name|PercentilesAggregationBuilder
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
name|PercentileRanksAggregationBuilder
name|other
init|=
operator|(
name|PercentileRanksAggregationBuilder
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
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
block|}
end_class

end_unit

