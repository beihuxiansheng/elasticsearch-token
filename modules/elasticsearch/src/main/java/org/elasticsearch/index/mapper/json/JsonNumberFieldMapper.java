begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.json
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|json
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
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|NumericTokenStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Fieldable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|NumericUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|gnu
operator|.
name|trove
operator|.
name|TIntObjectHashMap
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
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|JsonNumberFieldMapper
specifier|public
specifier|abstract
class|class
name|JsonNumberFieldMapper
parameter_list|<
name|T
extends|extends
name|Number
parameter_list|>
extends|extends
name|JsonFieldMapper
argument_list|<
name|T
argument_list|>
block|{
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
extends|extends
name|JsonFieldMapper
operator|.
name|Defaults
block|{
DECL|field|PRECISION_STEP
specifier|public
specifier|static
specifier|final
name|int
name|PRECISION_STEP
init|=
name|NumericUtils
operator|.
name|PRECISION_STEP_DEFAULT
decl_stmt|;
DECL|field|INDEX
specifier|public
specifier|static
specifier|final
name|Field
operator|.
name|Index
name|INDEX
init|=
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
decl_stmt|;
DECL|field|OMIT_NORMS
specifier|public
specifier|static
specifier|final
name|boolean
name|OMIT_NORMS
init|=
literal|true
decl_stmt|;
DECL|field|OMIT_TERM_FREQ_AND_POSITIONS
specifier|public
specifier|static
specifier|final
name|boolean
name|OMIT_TERM_FREQ_AND_POSITIONS
init|=
literal|true
decl_stmt|;
block|}
DECL|class|Builder
specifier|public
specifier|abstract
specifier|static
class|class
name|Builder
parameter_list|<
name|T
extends|extends
name|Builder
parameter_list|,
name|Y
extends|extends
name|JsonNumberFieldMapper
parameter_list|>
extends|extends
name|JsonFieldMapper
operator|.
name|Builder
argument_list|<
name|T
argument_list|,
name|Y
argument_list|>
block|{
DECL|field|precisionStep
specifier|protected
name|int
name|precisionStep
init|=
name|Defaults
operator|.
name|PRECISION_STEP
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|Defaults
operator|.
name|INDEX
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
name|Defaults
operator|.
name|OMIT_NORMS
expr_stmt|;
name|this
operator|.
name|omitTermFreqAndPositions
operator|=
name|Defaults
operator|.
name|OMIT_TERM_FREQ_AND_POSITIONS
expr_stmt|;
block|}
DECL|method|precisionStep
specifier|public
name|T
name|precisionStep
parameter_list|(
name|int
name|precisionStep
parameter_list|)
block|{
name|this
operator|.
name|precisionStep
operator|=
name|precisionStep
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
DECL|field|cachedStreams
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|TIntObjectHashMap
argument_list|<
name|Deque
argument_list|<
name|CachedNumericTokenStream
argument_list|>
argument_list|>
argument_list|>
name|cachedStreams
init|=
operator|new
name|ThreadLocal
argument_list|<
name|TIntObjectHashMap
argument_list|<
name|Deque
argument_list|<
name|CachedNumericTokenStream
argument_list|>
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TIntObjectHashMap
argument_list|<
name|Deque
argument_list|<
name|CachedNumericTokenStream
argument_list|>
argument_list|>
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|TIntObjectHashMap
argument_list|<
name|Deque
argument_list|<
name|CachedNumericTokenStream
argument_list|>
argument_list|>
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|precisionStep
specifier|protected
specifier|final
name|int
name|precisionStep
decl_stmt|;
DECL|method|JsonNumberFieldMapper
specifier|protected
name|JsonNumberFieldMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|indexName
parameter_list|,
name|String
name|fullName
parameter_list|,
name|int
name|precisionStep
parameter_list|,
name|Field
operator|.
name|Index
name|index
parameter_list|,
name|Field
operator|.
name|Store
name|store
parameter_list|,
name|float
name|boost
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|omitTermFreqAndPositions
parameter_list|,
name|Analyzer
name|indexAnalyzer
parameter_list|,
name|Analyzer
name|searchAnalyzer
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|indexName
argument_list|,
name|fullName
argument_list|,
name|index
argument_list|,
name|store
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|,
name|boost
argument_list|,
name|omitNorms
argument_list|,
name|omitTermFreqAndPositions
argument_list|,
name|indexAnalyzer
argument_list|,
name|searchAnalyzer
argument_list|)
expr_stmt|;
if|if
condition|(
name|precisionStep
operator|<=
literal|0
operator|||
name|precisionStep
operator|>=
name|maxPrecisionStep
argument_list|()
condition|)
block|{
name|this
operator|.
name|precisionStep
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|precisionStep
operator|=
name|precisionStep
expr_stmt|;
block|}
block|}
DECL|method|maxPrecisionStep
specifier|protected
specifier|abstract
name|int
name|maxPrecisionStep
parameter_list|()
function_decl|;
DECL|method|precisionStep
specifier|public
name|int
name|precisionStep
parameter_list|()
block|{
return|return
name|this
operator|.
name|precisionStep
return|;
block|}
comment|/**      * Override the default behavior (to return the string, and return the actual Number instance).      */
DECL|method|valueForSearch
annotation|@
name|Override
specifier|public
name|Object
name|valueForSearch
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
return|return
name|value
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|valueAsString
annotation|@
name|Override
specifier|public
name|String
name|valueAsString
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
return|return
name|value
argument_list|(
name|field
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|sortType
annotation|@
name|Override
specifier|public
specifier|abstract
name|int
name|sortType
parameter_list|()
function_decl|;
comment|/**      * Removes a cached numeric token stream. The stream will be returned to the cahed once it is used      * sicne it implements the end method.      */
DECL|method|popCachedStream
specifier|protected
name|CachedNumericTokenStream
name|popCachedStream
parameter_list|(
name|int
name|precisionStep
parameter_list|)
block|{
name|Deque
argument_list|<
name|CachedNumericTokenStream
argument_list|>
name|deque
init|=
name|cachedStreams
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
name|precisionStep
argument_list|)
decl_stmt|;
if|if
condition|(
name|deque
operator|==
literal|null
condition|)
block|{
name|deque
operator|=
operator|new
name|ArrayDeque
argument_list|<
name|CachedNumericTokenStream
argument_list|>
argument_list|()
expr_stmt|;
name|cachedStreams
operator|.
name|get
argument_list|()
operator|.
name|put
argument_list|(
name|precisionStep
argument_list|,
name|deque
argument_list|)
expr_stmt|;
name|deque
operator|.
name|add
argument_list|(
operator|new
name|CachedNumericTokenStream
argument_list|(
operator|new
name|NumericTokenStream
argument_list|(
name|precisionStep
argument_list|)
argument_list|,
name|precisionStep
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deque
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|deque
operator|.
name|add
argument_list|(
operator|new
name|CachedNumericTokenStream
argument_list|(
operator|new
name|NumericTokenStream
argument_list|(
name|precisionStep
argument_list|)
argument_list|,
name|precisionStep
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|deque
operator|.
name|pollFirst
argument_list|()
return|;
block|}
comment|/**      * A wrapper around a numeric stream allowing to reuse it by implementing the end method which returns      * this stream back to the thread local cache.      */
DECL|class|CachedNumericTokenStream
specifier|protected
specifier|static
specifier|final
class|class
name|CachedNumericTokenStream
extends|extends
name|TokenStream
block|{
DECL|field|precisionStep
specifier|private
specifier|final
name|int
name|precisionStep
decl_stmt|;
DECL|field|numericTokenStream
specifier|private
specifier|final
name|NumericTokenStream
name|numericTokenStream
decl_stmt|;
DECL|method|CachedNumericTokenStream
specifier|public
name|CachedNumericTokenStream
parameter_list|(
name|NumericTokenStream
name|numericTokenStream
parameter_list|,
name|int
name|precisionStep
parameter_list|)
block|{
name|super
argument_list|(
name|numericTokenStream
argument_list|)
expr_stmt|;
name|this
operator|.
name|numericTokenStream
operator|=
name|numericTokenStream
expr_stmt|;
name|this
operator|.
name|precisionStep
operator|=
name|precisionStep
expr_stmt|;
block|}
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
name|numericTokenStream
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
comment|/**          * Close the input TokenStream.          */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|numericTokenStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|cachedStreams
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|(
name|precisionStep
argument_list|)
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**          * Reset the filter as well as the input TokenStream.          */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|numericTokenStream
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|incrementToken
annotation|@
name|Override
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|numericTokenStream
operator|.
name|incrementToken
argument_list|()
return|;
block|}
DECL|method|setIntValue
specifier|public
name|CachedNumericTokenStream
name|setIntValue
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|numericTokenStream
operator|.
name|setIntValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setLongValue
specifier|public
name|CachedNumericTokenStream
name|setLongValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|numericTokenStream
operator|.
name|setLongValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setFloatValue
specifier|public
name|CachedNumericTokenStream
name|setFloatValue
parameter_list|(
name|float
name|value
parameter_list|)
block|{
name|numericTokenStream
operator|.
name|setFloatValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setDoubleValue
specifier|public
name|CachedNumericTokenStream
name|setDoubleValue
parameter_list|(
name|double
name|value
parameter_list|)
block|{
name|numericTokenStream
operator|.
name|setDoubleValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

