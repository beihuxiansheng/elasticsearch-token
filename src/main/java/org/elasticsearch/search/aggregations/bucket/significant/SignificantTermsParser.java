begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.significant
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
name|significant
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
name|search
operator|.
name|Filter
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
name|regex
operator|.
name|Regex
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
name|bucket
operator|.
name|BucketUtils
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
name|terms
operator|.
name|support
operator|.
name|IncludeExclude
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
name|format
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
name|format
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
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SignificantTermsParser
specifier|public
class|class
name|SignificantTermsParser
implements|implements
name|Aggregator
operator|.
name|Parser
block|{
DECL|field|DEFAULT_REQUIRED_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_REQUIRED_SIZE
init|=
literal|10
decl_stmt|;
DECL|field|DEFAULT_SHARD_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SHARD_SIZE
init|=
literal|0
decl_stmt|;
comment|//Typically need more than one occurrence of something for it to be statistically significant
DECL|field|DEFAULT_MIN_DOC_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_DOC_COUNT
init|=
literal|3
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
name|SignificantStringTerms
operator|.
name|TYPE
operator|.
name|name
argument_list|()
return|;
block|}
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
name|Filter
name|filter
init|=
literal|null
decl_stmt|;
name|int
name|requiredSize
init|=
name|DEFAULT_REQUIRED_SIZE
decl_stmt|;
name|int
name|shardSize
init|=
name|DEFAULT_SHARD_SIZE
decl_stmt|;
name|String
name|format
init|=
literal|null
decl_stmt|;
name|String
name|include
init|=
literal|null
decl_stmt|;
name|int
name|includeFlags
init|=
literal|0
decl_stmt|;
comment|// 0 means no flags
name|String
name|exclude
init|=
literal|null
decl_stmt|;
name|int
name|excludeFlags
init|=
literal|0
decl_stmt|;
comment|// 0 means no flags
name|String
name|executionHint
init|=
literal|null
decl_stmt|;
name|long
name|minDocCount
init|=
name|DEFAULT_MIN_DOC_COUNT
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
elseif|else
if|if
condition|(
literal|"include"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|include
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
literal|"exclude"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|exclude
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
literal|"execution_hint"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"executionHint"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|executionHint
operator|=
name|parser
operator|.
name|text
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
elseif|else
if|if
condition|(
literal|"shard_size"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"shardSize"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|shardSize
operator|=
name|parser
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"min_doc_count"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"minDocCount"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|minDocCount
operator|=
name|parser
operator|.
name|intValue
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
name|START_OBJECT
condition|)
block|{
comment|// TODO not sure if code below is the best means to declare a filter for
comment|// defining an alternative background stats context.
comment|// In trial runs it becomes obvious that the choice of background does have to
comment|// be a strict superset of the foreground subset otherwise the significant terms algo
comment|// immediately singles out the odd terms that are in the foreground but not represented
comment|// in the background. So a better approach may be to use a designated parent agg as the
comment|// background because parent aggs are always guaranteed to be a superset whereas arbitrary
comment|// filters defined by end users and parsed below are not.
comment|//                if ("background_context".equals(currentFieldName)) {
comment|//                    filter = context.queryParserService().parseInnerFilter(parser).filter();
comment|//                } else
if|if
condition|(
literal|"include"
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
literal|"pattern"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|include
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
literal|"flags"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|includeFlags
operator|=
name|Regex
operator|.
name|flagsFromString
argument_list|(
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
name|VALUE_NUMBER
condition|)
block|{
if|if
condition|(
literal|"flags"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|includeFlags
operator|=
name|parser
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
literal|"exclude"
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
literal|"pattern"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|exclude
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
literal|"flags"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|excludeFlags
operator|=
name|Regex
operator|.
name|flagsFromString
argument_list|(
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
name|VALUE_NUMBER
condition|)
block|{
if|if
condition|(
literal|"flags"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|excludeFlags
operator|=
name|parser
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
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
if|if
condition|(
name|shardSize
operator|==
name|DEFAULT_SHARD_SIZE
condition|)
block|{
comment|//The user has not made a shardSize selection .
comment|//Use default heuristic to avoid any wrong-ranking caused by distributed counting
comment|//but request double the usual amount.
comment|//We typically need more than the number of "top" terms requested by other aggregations
comment|//as the significance algorithm is in less of a position to down-select at shard-level -
comment|//some of the things we want to find have only one occurrence on each shard and as
comment|// such are impossible to differentiate from non-significant terms at that early stage.
name|shardSize
operator|=
literal|2
operator|*
name|BucketUtils
operator|.
name|suggestShardSideQueueSize
argument_list|(
name|requiredSize
argument_list|,
name|context
operator|.
name|numberOfShards
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// shard_size cannot be smaller than size as we need to at least fetch<size> entries from every shards in order to return<size>
if|if
condition|(
name|shardSize
operator|<
name|requiredSize
condition|)
block|{
name|shardSize
operator|=
name|requiredSize
expr_stmt|;
block|}
name|IncludeExclude
name|includeExclude
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|include
operator|!=
literal|null
operator|||
name|exclude
operator|!=
literal|null
condition|)
block|{
name|Pattern
name|includePattern
init|=
name|include
operator|!=
literal|null
condition|?
name|Pattern
operator|.
name|compile
argument_list|(
name|include
argument_list|,
name|includeFlags
argument_list|)
else|:
literal|null
decl_stmt|;
name|Pattern
name|excludePattern
init|=
name|exclude
operator|!=
literal|null
condition|?
name|Pattern
operator|.
name|compile
argument_list|(
name|exclude
argument_list|,
name|excludeFlags
argument_list|)
else|:
literal|null
decl_stmt|;
name|includeExclude
operator|=
operator|new
name|IncludeExclude
argument_list|(
name|includePattern
argument_list|,
name|excludePattern
argument_list|)
expr_stmt|;
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
argument_list|<>
argument_list|(
name|ValuesSource
operator|.
name|Bytes
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
name|SignificantTermsAggregatorFactory
argument_list|(
name|aggregationName
argument_list|,
name|config
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|requiredSize
argument_list|,
name|shardSize
argument_list|,
name|minDocCount
argument_list|,
name|includeExclude
argument_list|,
name|executionHint
argument_list|,
name|filter
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
name|ValueFormatter
name|valueFormatter
init|=
literal|null
decl_stmt|;
name|ValueParser
name|valueParser
init|=
literal|null
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
name|config
operator|=
operator|new
name|ValuesSourceConfig
argument_list|<>
argument_list|(
name|ValuesSource
operator|.
name|Numeric
operator|.
name|class
argument_list|)
expr_stmt|;
name|valueFormatter
operator|=
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
expr_stmt|;
name|valueParser
operator|=
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
argument_list|<>
argument_list|(
name|ValuesSource
operator|.
name|Numeric
operator|.
name|class
argument_list|)
expr_stmt|;
name|valueFormatter
operator|=
name|ValueFormatter
operator|.
name|IPv4
expr_stmt|;
name|valueParser
operator|=
name|ValueParser
operator|.
name|IPv4
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
argument_list|<>
argument_list|(
name|ValuesSource
operator|.
name|Numeric
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
name|valueFormatter
operator|=
operator|new
name|ValueFormatter
operator|.
name|Number
operator|.
name|Pattern
argument_list|(
name|format
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
argument_list|<>
argument_list|(
name|ValuesSource
operator|.
name|Bytes
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
name|config
operator|.
name|ensureUnique
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|new
name|SignificantTermsAggregatorFactory
argument_list|(
name|aggregationName
argument_list|,
name|config
argument_list|,
name|valueFormatter
argument_list|,
name|valueParser
argument_list|,
name|requiredSize
argument_list|,
name|shardSize
argument_list|,
name|minDocCount
argument_list|,
name|includeExclude
argument_list|,
name|executionHint
argument_list|,
name|filter
argument_list|)
return|;
block|}
block|}
end_class

end_unit

