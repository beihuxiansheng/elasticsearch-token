begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.sampler
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
name|sampler
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
name|ValuesSourceAggregationBuilder
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
name|Objects
import|;
end_import

begin_class
DECL|class|DiversifiedAggregationBuilder
specifier|public
class|class
name|DiversifiedAggregationBuilder
extends|extends
name|ValuesSourceAggregationBuilder
argument_list|<
name|ValuesSource
argument_list|,
name|DiversifiedAggregationBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"diversified_sampler"
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
DECL|field|MAX_DOCS_PER_VALUE_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|MAX_DOCS_PER_VALUE_DEFAULT
init|=
literal|1
decl_stmt|;
DECL|field|PARSER
specifier|private
specifier|static
specifier|final
name|ObjectParser
argument_list|<
name|DiversifiedAggregationBuilder
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
name|DiversifiedAggregationBuilder
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|ValuesSourceParserHelper
operator|.
name|declareAnyFields
argument_list|(
name|PARSER
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareInt
argument_list|(
name|DiversifiedAggregationBuilder
operator|::
name|shardSize
argument_list|,
name|SamplerAggregator
operator|.
name|SHARD_SIZE_FIELD
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareInt
argument_list|(
name|DiversifiedAggregationBuilder
operator|::
name|maxDocsPerValue
argument_list|,
name|SamplerAggregator
operator|.
name|MAX_DOCS_PER_VALUE_FIELD
argument_list|)
expr_stmt|;
name|PARSER
operator|.
name|declareString
argument_list|(
name|DiversifiedAggregationBuilder
operator|::
name|executionHint
argument_list|,
name|SamplerAggregator
operator|.
name|EXECUTION_HINT_FIELD
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
name|DiversifiedAggregationBuilder
argument_list|(
name|aggregationName
argument_list|)
argument_list|,
name|context
argument_list|)
return|;
block|}
DECL|field|shardSize
specifier|private
name|int
name|shardSize
init|=
name|SamplerAggregationBuilder
operator|.
name|DEFAULT_SHARD_SAMPLE_SIZE
decl_stmt|;
DECL|field|maxDocsPerValue
specifier|private
name|int
name|maxDocsPerValue
init|=
name|MAX_DOCS_PER_VALUE_DEFAULT
decl_stmt|;
DECL|field|executionHint
specifier|private
name|String
name|executionHint
init|=
literal|null
decl_stmt|;
DECL|method|DiversifiedAggregationBuilder
specifier|public
name|DiversifiedAggregationBuilder
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
name|ANY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|DiversifiedAggregationBuilder
specifier|public
name|DiversifiedAggregationBuilder
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
name|ANY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|shardSize
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|maxDocsPerValue
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|executionHint
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
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
name|writeVInt
argument_list|(
name|shardSize
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|maxDocsPerValue
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|executionHint
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the max num docs to be returned from each shard.      */
DECL|method|shardSize
specifier|public
name|DiversifiedAggregationBuilder
name|shardSize
parameter_list|(
name|int
name|shardSize
parameter_list|)
block|{
if|if
condition|(
name|shardSize
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[shardSize] must be greater than or equal to 0. Found ["
operator|+
name|shardSize
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
name|shardSize
operator|=
name|shardSize
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the max num docs to be returned from each shard.      */
DECL|method|shardSize
specifier|public
name|int
name|shardSize
parameter_list|()
block|{
return|return
name|shardSize
return|;
block|}
comment|/**      * Set the max num docs to be returned per value.      */
DECL|method|maxDocsPerValue
specifier|public
name|DiversifiedAggregationBuilder
name|maxDocsPerValue
parameter_list|(
name|int
name|maxDocsPerValue
parameter_list|)
block|{
if|if
condition|(
name|maxDocsPerValue
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[maxDocsPerValue] must be greater than or equal to 0. Found ["
operator|+
name|maxDocsPerValue
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
name|maxDocsPerValue
operator|=
name|maxDocsPerValue
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the max num docs to be returned per value.      */
DECL|method|maxDocsPerValue
specifier|public
name|int
name|maxDocsPerValue
parameter_list|()
block|{
return|return
name|maxDocsPerValue
return|;
block|}
comment|/**      * Set the execution hint.      */
DECL|method|executionHint
specifier|public
name|DiversifiedAggregationBuilder
name|executionHint
parameter_list|(
name|String
name|executionHint
parameter_list|)
block|{
name|this
operator|.
name|executionHint
operator|=
name|executionHint
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the execution hint.      */
DECL|method|executionHint
specifier|public
name|String
name|executionHint
parameter_list|()
block|{
return|return
name|executionHint
return|;
block|}
annotation|@
name|Override
DECL|method|innerBuild
specifier|protected
name|ValuesSourceAggregatorFactory
argument_list|<
name|ValuesSource
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
name|ValuesSource
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
return|return
operator|new
name|DiversifiedAggregatorFactory
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|,
name|config
argument_list|,
name|shardSize
argument_list|,
name|maxDocsPerValue
argument_list|,
name|executionHint
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
name|SamplerAggregator
operator|.
name|SHARD_SIZE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|shardSize
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|SamplerAggregator
operator|.
name|MAX_DOCS_PER_VALUE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|maxDocsPerValue
argument_list|)
expr_stmt|;
if|if
condition|(
name|executionHint
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|SamplerAggregator
operator|.
name|EXECUTION_HINT_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|executionHint
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
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
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|shardSize
argument_list|,
name|maxDocsPerValue
argument_list|,
name|executionHint
argument_list|)
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
name|DiversifiedAggregationBuilder
name|other
init|=
operator|(
name|DiversifiedAggregationBuilder
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|shardSize
argument_list|,
name|other
operator|.
name|shardSize
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|maxDocsPerValue
argument_list|,
name|other
operator|.
name|maxDocsPerValue
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|executionHint
argument_list|,
name|other
operator|.
name|executionHint
argument_list|)
return|;
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

