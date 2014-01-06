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
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|DoubleArrayList
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
name|index
operator|.
name|fielddata
operator|.
name|IndexFieldData
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
name|mapper
operator|.
name|FieldMapper
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
name|SearchParseException
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
name|metrics
operator|.
name|percentiles
operator|.
name|tdigest
operator|.
name|TDigest
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
name|FieldContext
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
name|numeric
operator|.
name|NumericValuesSource
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
name|internal
operator|.
name|SearchContext
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
name|HashMap
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
DECL|class|PercentilesParser
specifier|public
class|class
name|PercentilesParser
implements|implements
name|Aggregator
operator|.
name|Parser
block|{
DECL|field|DEFAULT_PERCENTS
specifier|private
specifier|final
specifier|static
name|double
index|[]
name|DEFAULT_PERCENTS
init|=
operator|new
name|double
index|[]
block|{
literal|1
block|,
literal|5
block|,
literal|25
block|,
literal|50
block|,
literal|75
block|,
literal|95
block|,
literal|99
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|InternalPercentiles
operator|.
name|TYPE
operator|.
name|name
argument_list|()
return|;
block|}
comment|/**      * We must override the parse method because we need to allow custom parameters      * (execution_hint, etc) which is not possible otherwise      */
annotation|@
name|Override
DECL|method|parse
specifier|public
name|AggregatorFactory
name|parse
parameter_list|(
name|String
name|aggregationName
parameter_list|,
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ValuesSourceConfig
argument_list|<
name|NumericValuesSource
argument_list|>
name|config
init|=
operator|new
name|ValuesSourceConfig
argument_list|<
name|NumericValuesSource
argument_list|>
argument_list|(
name|NumericValuesSource
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|field
init|=
literal|null
decl_stmt|;
name|String
name|script
init|=
literal|null
decl_stmt|;
name|String
name|scriptLang
init|=
literal|null
decl_stmt|;
name|double
index|[]
name|percents
init|=
name|DEFAULT_PERCENTS
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|scriptParams
init|=
literal|null
decl_stmt|;
name|boolean
name|assumeSorted
init|=
literal|false
decl_stmt|;
name|boolean
name|keyed
init|=
literal|true
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|settings
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
if|if
condition|(
literal|"field"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|field
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"script"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|script
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"lang"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|scriptLang
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|settings
operator|==
literal|null
condition|)
block|{
name|settings
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|settings
operator|.
name|put
argument_list|(
name|currentFieldName
argument_list|,
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
if|if
condition|(
literal|"percents"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|DoubleArrayList
name|values
init|=
operator|new
name|DoubleArrayList
argument_list|(
literal|10
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|double
name|percent
init|=
name|parser
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|percent
argument_list|<
literal|0
operator|||
name|percent
argument_list|>
literal|100
condition|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"the percents in the percentiles aggregation ["
operator|+
name|aggregationName
operator|+
literal|"] must be in the [0, 100] range"
argument_list|)
throw|;
block|}
name|values
operator|.
name|add
argument_list|(
name|percent
argument_list|)
expr_stmt|;
block|}
name|percents
operator|=
name|values
operator|.
name|toArray
argument_list|()
expr_stmt|;
comment|// Some impls rely on the fact that percents are sorted
name|Arrays
operator|.
name|sort
argument_list|(
name|percents
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Unknown key for a "
operator|+
name|token
operator|+
literal|" in ["
operator|+
name|aggregationName
operator|+
literal|"]: ["
operator|+
name|currentFieldName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
if|if
condition|(
literal|"params"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|scriptParams
operator|=
name|parser
operator|.
name|map
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Unknown key for a "
operator|+
name|token
operator|+
literal|" in ["
operator|+
name|aggregationName
operator|+
literal|"]: ["
operator|+
name|currentFieldName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_BOOLEAN
condition|)
block|{
if|if
condition|(
literal|"script_values_sorted"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"scriptValuesSorted"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|assumeSorted
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|"keyed"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|keyed
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|settings
operator|==
literal|null
condition|)
block|{
name|settings
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|settings
operator|.
name|put
argument_list|(
name|currentFieldName
argument_list|,
name|parser
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NUMBER
condition|)
block|{
if|if
condition|(
name|settings
operator|==
literal|null
condition|)
block|{
name|settings
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|settings
operator|.
name|put
argument_list|(
name|currentFieldName
argument_list|,
name|parser
operator|.
name|numberValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Unexpected token "
operator|+
name|token
operator|+
literal|" in ["
operator|+
name|aggregationName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
name|PercentilesEstimator
operator|.
name|Factory
name|estimatorFactory
init|=
name|EstimatorType
operator|.
name|TDIGEST
operator|.
name|estimatorFactory
argument_list|(
name|settings
argument_list|)
decl_stmt|;
if|if
condition|(
name|script
operator|!=
literal|null
condition|)
block|{
name|config
operator|.
name|script
argument_list|(
name|context
operator|.
name|scriptService
argument_list|()
operator|.
name|search
argument_list|(
name|context
operator|.
name|lookup
argument_list|()
argument_list|,
name|scriptLang
argument_list|,
name|script
argument_list|,
name|scriptParams
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|assumeSorted
condition|)
block|{
name|config
operator|.
name|ensureSorted
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|PercentilesAggregator
operator|.
name|Factory
argument_list|(
name|aggregationName
argument_list|,
name|config
argument_list|,
name|percents
argument_list|,
name|estimatorFactory
argument_list|,
name|keyed
argument_list|)
return|;
block|}
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|mapper
init|=
name|context
operator|.
name|smartNameFieldMapper
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapper
operator|==
literal|null
condition|)
block|{
name|config
operator|.
name|unmapped
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|new
name|PercentilesAggregator
operator|.
name|Factory
argument_list|(
name|aggregationName
argument_list|,
name|config
argument_list|,
name|percents
argument_list|,
name|estimatorFactory
argument_list|,
name|keyed
argument_list|)
return|;
block|}
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|indexFieldData
init|=
name|context
operator|.
name|fieldData
argument_list|()
operator|.
name|getForField
argument_list|(
name|mapper
argument_list|)
decl_stmt|;
name|config
operator|.
name|fieldContext
argument_list|(
operator|new
name|FieldContext
argument_list|(
name|field
argument_list|,
name|indexFieldData
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|PercentilesAggregator
operator|.
name|Factory
argument_list|(
name|aggregationName
argument_list|,
name|config
argument_list|,
name|percents
argument_list|,
name|estimatorFactory
argument_list|,
name|keyed
argument_list|)
return|;
block|}
comment|/**      *      */
DECL|enum|EstimatorType
specifier|public
specifier|static
enum|enum
name|EstimatorType
block|{
DECL|method|TDIGEST
DECL|method|TDIGEST
name|TDIGEST
parameter_list|()
block|{
annotation|@
name|Override
specifier|public
name|PercentilesEstimator
operator|.
name|Factory
name|estimatorFactory
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|settings
parameter_list|)
block|{
return|return
operator|new
name|TDigest
operator|.
name|Factory
argument_list|(
name|settings
argument_list|)
return|;
block|}
block|}
block|;
DECL|method|estimatorFactory
specifier|public
specifier|abstract
name|PercentilesEstimator
operator|.
name|Factory
name|estimatorFactory
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|settings
parameter_list|)
function_decl|;
DECL|method|resolve
specifier|public
specifier|static
name|EstimatorType
name|resolve
parameter_list|(
name|String
name|name
parameter_list|,
name|SearchContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"tdigest"
argument_list|)
condition|)
block|{
return|return
name|TDIGEST
return|;
block|}
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Unknown percentile estimator ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

