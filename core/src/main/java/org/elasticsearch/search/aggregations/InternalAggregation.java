begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
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
name|DelegatingHasContextAndHeaders
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
name|HasContextAndHeaders
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
name|bytes
operator|.
name|BytesArray
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
name|bytes
operator|.
name|BytesReference
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|util
operator|.
name|BigArrays
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
name|ToXContent
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
name|XContentBuilderString
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
name|ScriptService
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
name|pipeline
operator|.
name|PipelineAggregator
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
name|pipeline
operator|.
name|PipelineAggregatorStreams
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
name|AggregationPath
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
comment|/**  * An internal implementation of {@link Aggregation}. Serves as a base class for all aggregation implementations.  */
end_comment

begin_class
DECL|class|InternalAggregation
specifier|public
specifier|abstract
class|class
name|InternalAggregation
implements|implements
name|Aggregation
implements|,
name|ToXContent
implements|,
name|Streamable
block|{
comment|/**      * The aggregation type that holds all the string types that are associated with an aggregation:      *<ul>      *<li>name - used as the parser type</li>      *<li>stream - used as the stream type</li>      *</ul>      */
DECL|class|Type
specifier|public
specifier|static
class|class
name|Type
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|stream
specifier|private
name|BytesReference
name|stream
decl_stmt|;
DECL|method|Type
specifier|public
name|Type
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
operator|new
name|BytesArray
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|Type
specifier|public
name|Type
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|stream
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
operator|new
name|BytesArray
argument_list|(
name|stream
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|Type
specifier|public
name|Type
parameter_list|(
name|String
name|name
parameter_list|,
name|BytesReference
name|stream
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|stream
operator|=
name|stream
expr_stmt|;
block|}
comment|/**          * @return The name of the type (mainly used for registering the parser for the aggregator (see {@link org.elasticsearch.search.aggregations.Aggregator.Parser#type()}).          */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**          * @return  The name of the stream type (used for registering the aggregation stream          *          (see {@link AggregationStreams#registerStream(AggregationStreams.Stream, org.elasticsearch.common.bytes.BytesReference...)}).          */
DECL|method|stream
specifier|public
name|BytesReference
name|stream
parameter_list|()
block|{
return|return
name|stream
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
name|name
return|;
block|}
block|}
DECL|class|ReduceContext
specifier|public
specifier|static
class|class
name|ReduceContext
extends|extends
name|DelegatingHasContextAndHeaders
block|{
DECL|field|bigArrays
specifier|private
specifier|final
name|BigArrays
name|bigArrays
decl_stmt|;
DECL|field|scriptService
specifier|private
name|ScriptService
name|scriptService
decl_stmt|;
DECL|method|ReduceContext
specifier|public
name|ReduceContext
parameter_list|(
name|BigArrays
name|bigArrays
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|,
name|HasContextAndHeaders
name|headersContext
parameter_list|)
block|{
name|super
argument_list|(
name|headersContext
argument_list|)
expr_stmt|;
name|this
operator|.
name|bigArrays
operator|=
name|bigArrays
expr_stmt|;
name|this
operator|.
name|scriptService
operator|=
name|scriptService
expr_stmt|;
block|}
DECL|method|bigArrays
specifier|public
name|BigArrays
name|bigArrays
parameter_list|()
block|{
return|return
name|bigArrays
return|;
block|}
DECL|method|scriptService
specifier|public
name|ScriptService
name|scriptService
parameter_list|()
block|{
return|return
name|scriptService
return|;
block|}
block|}
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|field|metaData
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
decl_stmt|;
DECL|field|pipelineAggregators
specifier|private
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
decl_stmt|;
comment|/** Constructs an un initialized addAggregation (used for serialization) **/
DECL|method|InternalAggregation
specifier|protected
name|InternalAggregation
parameter_list|()
block|{}
comment|/**      * Constructs an get with a given name.      *      * @param name The name of the get.      */
DECL|method|InternalAggregation
specifier|protected
name|InternalAggregation
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
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
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|pipelineAggregators
operator|=
name|pipelineAggregators
expr_stmt|;
name|this
operator|.
name|metaData
operator|=
name|metaData
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * @return The {@link Type} of this aggregation      */
DECL|method|type
specifier|public
specifier|abstract
name|Type
name|type
parameter_list|()
function_decl|;
comment|/**      * Reduces the given addAggregation to a single one and returns it. In<b>most</b> cases, the assumption will be the all given      * addAggregation are of the same type (the same type as this aggregation). For best efficiency, when implementing,      * try reusing an existing get instance (typically the first in the given list) to save on redundant object      * construction.      */
DECL|method|reduce
specifier|public
specifier|final
name|InternalAggregation
name|reduce
parameter_list|(
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggregations
parameter_list|,
name|ReduceContext
name|reduceContext
parameter_list|)
block|{
name|InternalAggregation
name|aggResult
init|=
name|doReduce
argument_list|(
name|aggregations
argument_list|,
name|reduceContext
argument_list|)
decl_stmt|;
for|for
control|(
name|PipelineAggregator
name|pipelineAggregator
range|:
name|pipelineAggregators
control|)
block|{
name|aggResult
operator|=
name|pipelineAggregator
operator|.
name|reduce
argument_list|(
name|aggResult
argument_list|,
name|reduceContext
argument_list|)
expr_stmt|;
block|}
return|return
name|aggResult
return|;
block|}
DECL|method|doReduce
specifier|public
specifier|abstract
name|InternalAggregation
name|doReduce
parameter_list|(
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggregations
parameter_list|,
name|ReduceContext
name|reduceContext
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|getProperty
specifier|public
name|Object
name|getProperty
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|AggregationPath
name|aggPath
init|=
name|AggregationPath
operator|.
name|parse
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|getProperty
argument_list|(
name|aggPath
operator|.
name|getPathElementsAsStringList
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getProperty
specifier|public
specifier|abstract
name|Object
name|getProperty
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|)
function_decl|;
comment|/**      * Read a size under the assumption that a value of 0 means unlimited.      */
DECL|method|readSize
specifier|protected
specifier|static
name|int
name|readSize
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
return|return
name|size
operator|==
literal|0
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|size
return|;
block|}
comment|/**      * Write a size under the assumption that a value of 0 means unlimited.      */
DECL|method|writeSize
specifier|protected
specifier|static
name|void
name|writeSize
parameter_list|(
name|int
name|size
parameter_list|,
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|size
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|size
operator|=
literal|0
expr_stmt|;
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMetaData
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getMetaData
parameter_list|()
block|{
return|return
name|metaData
return|;
block|}
DECL|method|pipelineAggregators
specifier|public
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
parameter_list|()
block|{
return|return
name|pipelineAggregators
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
specifier|final
name|XContentBuilder
name|toXContent
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
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|metaData
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|META
argument_list|)
expr_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|this
operator|.
name|metaData
argument_list|)
expr_stmt|;
block|}
name|doXContentBody
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|doXContentBody
specifier|public
specifier|abstract
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
function_decl|;
annotation|@
name|Override
DECL|method|writeTo
specifier|public
specifier|final
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeString
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeGenericValue
argument_list|(
name|metaData
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|pipelineAggregators
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|PipelineAggregator
name|pipelineAggregator
range|:
name|pipelineAggregators
control|)
block|{
name|out
operator|.
name|writeBytesReference
argument_list|(
name|pipelineAggregator
operator|.
name|type
argument_list|()
operator|.
name|stream
argument_list|()
argument_list|)
expr_stmt|;
name|pipelineAggregator
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|doWriteTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|doWriteTo
specifier|protected
specifier|abstract
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|readFrom
specifier|public
specifier|final
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|name
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|metaData
operator|=
name|in
operator|.
name|readMap
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
name|pipelineAggregators
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|pipelineAggregators
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|size
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|BytesReference
name|type
init|=
name|in
operator|.
name|readBytesReference
argument_list|()
decl_stmt|;
name|PipelineAggregator
name|pipelineAggregator
init|=
name|PipelineAggregatorStreams
operator|.
name|stream
argument_list|(
name|type
argument_list|)
operator|.
name|readResult
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|pipelineAggregators
operator|.
name|add
argument_list|(
name|pipelineAggregator
argument_list|)
expr_stmt|;
block|}
block|}
name|doReadFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|doReadFrom
specifier|protected
specifier|abstract
name|void
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Common xcontent fields that are shared among addAggregation      */
DECL|class|CommonFields
specifier|public
specifier|static
specifier|final
class|class
name|CommonFields
block|{
DECL|field|META
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|META
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"meta"
argument_list|)
decl_stmt|;
DECL|field|BUCKETS
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|BUCKETS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"buckets"
argument_list|)
decl_stmt|;
DECL|field|VALUE
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|VALUE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
DECL|field|VALUES
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|VALUES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"values"
argument_list|)
decl_stmt|;
DECL|field|VALUE_AS_STRING
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|VALUE_AS_STRING
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"value_as_string"
argument_list|)
decl_stmt|;
DECL|field|DOC_COUNT
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|DOC_COUNT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"doc_count"
argument_list|)
decl_stmt|;
DECL|field|KEY
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|KEY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"key"
argument_list|)
decl_stmt|;
DECL|field|KEY_AS_STRING
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|KEY_AS_STRING
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"key_as_string"
argument_list|)
decl_stmt|;
DECL|field|FROM
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|FROM
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"from"
argument_list|)
decl_stmt|;
DECL|field|FROM_AS_STRING
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|FROM_AS_STRING
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"from_as_string"
argument_list|)
decl_stmt|;
DECL|field|TO
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|TO
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"to"
argument_list|)
decl_stmt|;
DECL|field|TO_AS_STRING
specifier|public
specifier|static
specifier|final
name|XContentBuilderString
name|TO_AS_STRING
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"to_as_string"
argument_list|)
decl_stmt|;
block|}
block|}
end_class

end_unit
