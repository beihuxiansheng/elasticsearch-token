begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.terms
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
name|terms
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
name|fielddata
operator|.
name|IndexNumericFieldData
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
name|index
operator|.
name|mapper
operator|.
name|core
operator|.
name|DateFieldMapper
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
name|ip
operator|.
name|IpFieldMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|SearchScript
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
name|bytes
operator|.
name|BytesValuesSource
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
name|aggregations
operator|.
name|support
operator|.
name|numeric
operator|.
name|ValueFormatter
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
name|ValueParser
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
name|Map
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TermsParser
specifier|public
class|class
name|TermsParser
implements|implements
name|Aggregator
operator|.
name|Parser
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
name|StringTerms
operator|.
name|TYPE
operator|.
name|name
argument_list|()
return|;
block|}
comment|// TODO add support for shard_size (vs. size) a la terms facets
comment|// TODO add support for term filtering (regexp/include/exclude) a la terms facets
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
name|Terms
operator|.
name|ValueType
name|valueType
init|=
literal|null
decl_stmt|;
name|int
name|requiredSize
init|=
literal|10
decl_stmt|;
name|String
name|orderKey
init|=
literal|"_count"
decl_stmt|;
name|boolean
name|orderAsc
init|=
literal|false
decl_stmt|;
name|String
name|format
init|=
literal|null
decl_stmt|;
name|boolean
name|assumeUnique
init|=
literal|false
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
literal|"script_lang"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"scriptLang"
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
elseif|else
if|if
condition|(
literal|"value_type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"valueType"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|valueType
operator|=
name|Terms
operator|.
name|ValueType
operator|.
name|resolveType
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"format"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|format
operator|=
name|parser
operator|.
name|text
argument_list|()
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
name|VALUE_BOOLEAN
condition|)
block|{
if|if
condition|(
literal|"script_values_unique"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|assumeUnique
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
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
literal|"size"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|requiredSize
operator|=
name|parser
operator|.
name|intValue
argument_list|()
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
elseif|else
if|if
condition|(
literal|"order"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
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
name|orderKey
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
name|String
name|dir
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
name|orderAsc
operator|=
literal|"asc"
operator|.
name|equalsIgnoreCase
argument_list|(
name|dir
argument_list|)
expr_stmt|;
comment|//TODO: do we want to throw a parse error if the alternative is not "desc"???
block|}
block|}
block|}
block|}
block|}
name|InternalOrder
name|order
init|=
name|resolveOrder
argument_list|(
name|orderKey
argument_list|,
name|orderAsc
argument_list|)
decl_stmt|;
name|SearchScript
name|searchScript
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|script
operator|!=
literal|null
condition|)
block|{
name|searchScript
operator|=
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
expr_stmt|;
block|}
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|ValuesSource
argument_list|>
name|valueSourceType
init|=
name|script
operator|==
literal|null
condition|?
name|ValuesSource
operator|.
name|class
else|:
comment|// unknown, will inherit whatever is in the context
name|valueType
operator|!=
literal|null
condition|?
name|valueType
operator|.
name|scriptValueType
operator|.
name|getValuesSourceType
argument_list|()
else|:
comment|// the user explicitly defined a value type
name|BytesValuesSource
operator|.
name|class
decl_stmt|;
comment|// defaulting to bytes
name|ValuesSourceConfig
argument_list|<
name|?
argument_list|>
name|config
init|=
operator|new
name|ValuesSourceConfig
argument_list|(
name|valueSourceType
argument_list|)
decl_stmt|;
if|if
condition|(
name|valueType
operator|!=
literal|null
condition|)
block|{
name|config
operator|.
name|scriptValueType
argument_list|(
name|valueType
operator|.
name|scriptValueType
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|script
argument_list|(
name|searchScript
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|assumeUnique
condition|)
block|{
name|config
operator|.
name|ensureUnique
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TermsAggregatorFactory
argument_list|(
name|aggregationName
argument_list|,
name|config
argument_list|,
name|order
argument_list|,
name|requiredSize
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
name|ValuesSourceConfig
argument_list|<
name|?
argument_list|>
name|config
init|=
operator|new
name|ValuesSourceConfig
argument_list|<
name|BytesValuesSource
argument_list|>
argument_list|(
name|BytesValuesSource
operator|.
name|class
argument_list|)
decl_stmt|;
name|config
operator|.
name|unmapped
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|new
name|TermsAggregatorFactory
argument_list|(
name|aggregationName
argument_list|,
name|config
argument_list|,
name|order
argument_list|,
name|requiredSize
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
name|ValuesSourceConfig
argument_list|<
name|?
argument_list|>
name|config
decl_stmt|;
if|if
condition|(
name|mapper
operator|instanceof
name|DateFieldMapper
condition|)
block|{
name|DateFieldMapper
name|dateMapper
init|=
operator|(
name|DateFieldMapper
operator|)
name|mapper
decl_stmt|;
name|ValueFormatter
name|formatter
init|=
name|format
operator|==
literal|null
condition|?
operator|new
name|ValueFormatter
operator|.
name|DateTime
argument_list|(
name|dateMapper
operator|.
name|dateTimeFormatter
argument_list|()
argument_list|)
else|:
operator|new
name|ValueFormatter
operator|.
name|DateTime
argument_list|(
name|format
argument_list|)
decl_stmt|;
name|config
operator|=
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
operator|.
name|formatter
argument_list|(
name|formatter
argument_list|)
operator|.
name|parser
argument_list|(
operator|new
name|ValueParser
operator|.
name|DateMath
argument_list|(
name|dateMapper
operator|.
name|dateMathParser
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mapper
operator|instanceof
name|IpFieldMapper
condition|)
block|{
name|config
operator|=
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
operator|.
name|formatter
argument_list|(
name|ValueFormatter
operator|.
name|IPv4
argument_list|)
operator|.
name|parser
argument_list|(
name|ValueParser
operator|.
name|IPv4
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|indexFieldData
operator|instanceof
name|IndexNumericFieldData
condition|)
block|{
name|config
operator|=
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
expr_stmt|;
if|if
condition|(
name|format
operator|!=
literal|null
condition|)
block|{
name|config
operator|.
name|formatter
argument_list|(
operator|new
name|ValueFormatter
operator|.
name|Number
operator|.
name|Pattern
argument_list|(
name|format
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|config
operator|=
operator|new
name|ValuesSourceConfig
argument_list|<
name|BytesValuesSource
argument_list|>
argument_list|(
name|BytesValuesSource
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// TODO: it will make sense to set false instead here if the aggregator factory uses
comment|// ordinals instead of hash tables
name|config
operator|.
name|needsHashes
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|script
argument_list|(
name|searchScript
argument_list|)
expr_stmt|;
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
comment|// We need values to be unique to be able to run terms aggs efficiently
if|if
condition|(
operator|!
name|assumeUnique
condition|)
block|{
name|config
operator|.
name|ensureUnique
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TermsAggregatorFactory
argument_list|(
name|aggregationName
argument_list|,
name|config
argument_list|,
name|order
argument_list|,
name|requiredSize
argument_list|)
return|;
block|}
DECL|method|resolveOrder
specifier|static
name|InternalOrder
name|resolveOrder
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|asc
parameter_list|)
block|{
if|if
condition|(
literal|"_term"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
call|(
name|InternalOrder
call|)
argument_list|(
name|asc
condition|?
name|InternalOrder
operator|.
name|TERM_ASC
else|:
name|InternalOrder
operator|.
name|TERM_DESC
argument_list|)
return|;
block|}
if|if
condition|(
literal|"_count"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
call|(
name|InternalOrder
call|)
argument_list|(
name|asc
condition|?
name|InternalOrder
operator|.
name|COUNT_ASC
else|:
name|InternalOrder
operator|.
name|COUNT_DESC
argument_list|)
return|;
block|}
name|int
name|i
init|=
name|key
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
condition|)
block|{
return|return
name|Terms
operator|.
name|Order
operator|.
name|aggregation
argument_list|(
name|key
argument_list|,
name|asc
argument_list|)
return|;
block|}
return|return
name|Terms
operator|.
name|Order
operator|.
name|aggregation
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|,
name|key
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|,
name|asc
argument_list|)
return|;
block|}
block|}
end_class

end_unit

