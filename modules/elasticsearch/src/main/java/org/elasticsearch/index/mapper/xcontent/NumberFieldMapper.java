begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|xcontent
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
name|AbstractField
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
name|search
operator|.
name|Filter
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
name|search
operator|.
name|Query
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|StringHelper
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
name|thread
operator|.
name|ThreadLocals
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
name|trove
operator|.
name|TIntObjectHashMap
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
name|analysis
operator|.
name|NamedAnalyzer
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
name|cache
operator|.
name|field
operator|.
name|data
operator|.
name|FieldDataCache
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
name|field
operator|.
name|data
operator|.
name|FieldDataType
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
name|MergeMappingException
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
name|io
operator|.
name|Reader
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|NumberFieldMapper
specifier|public
specifier|abstract
class|class
name|NumberFieldMapper
parameter_list|<
name|T
extends|extends
name|Number
parameter_list|>
extends|extends
name|AbstractFieldMapper
argument_list|<
name|T
argument_list|>
implements|implements
name|IncludeInAllMapper
block|{
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
extends|extends
name|AbstractFieldMapper
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
name|NumberFieldMapper
parameter_list|>
extends|extends
name|AbstractFieldMapper
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
DECL|method|store
annotation|@
name|Override
specifier|public
name|T
name|store
parameter_list|(
name|Field
operator|.
name|Store
name|store
parameter_list|)
block|{
return|return
name|super
operator|.
name|store
argument_list|(
name|store
argument_list|)
return|;
block|}
DECL|method|boost
annotation|@
name|Override
specifier|public
name|T
name|boost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
return|return
name|super
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
return|;
block|}
DECL|method|indexName
annotation|@
name|Override
specifier|public
name|T
name|indexName
parameter_list|(
name|String
name|indexName
parameter_list|)
block|{
return|return
name|super
operator|.
name|indexName
argument_list|(
name|indexName
argument_list|)
return|;
block|}
DECL|method|includeInAll
annotation|@
name|Override
specifier|public
name|T
name|includeInAll
parameter_list|(
name|Boolean
name|includeInAll
parameter_list|)
block|{
return|return
name|super
operator|.
name|includeInAll
argument_list|(
name|includeInAll
argument_list|)
return|;
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
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|TIntObjectHashMap
argument_list|<
name|Deque
argument_list|<
name|CachedNumericTokenStream
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|cachedStreams
init|=
operator|new
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|TIntObjectHashMap
argument_list|<
name|Deque
argument_list|<
name|CachedNumericTokenStream
argument_list|>
argument_list|>
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|TIntObjectHashMap
argument_list|<
name|Deque
argument_list|<
name|CachedNumericTokenStream
argument_list|>
argument_list|>
argument_list|>
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|TIntObjectHashMap
argument_list|<
name|Deque
argument_list|<
name|CachedNumericTokenStream
argument_list|>
argument_list|>
argument_list|>
argument_list|(
operator|new
name|TIntObjectHashMap
argument_list|<
name|Deque
argument_list|<
name|CachedNumericTokenStream
argument_list|>
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|precisionStep
specifier|protected
name|int
name|precisionStep
decl_stmt|;
DECL|field|includeInAll
specifier|protected
name|Boolean
name|includeInAll
decl_stmt|;
DECL|method|NumberFieldMapper
specifier|protected
name|NumberFieldMapper
parameter_list|(
name|Names
name|names
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
name|NamedAnalyzer
name|indexAnalyzer
parameter_list|,
name|NamedAnalyzer
name|searchAnalyzer
parameter_list|)
block|{
name|super
argument_list|(
name|names
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
DECL|method|includeInAll
annotation|@
name|Override
specifier|public
name|void
name|includeInAll
parameter_list|(
name|Boolean
name|includeInAll
parameter_list|)
block|{
if|if
condition|(
name|includeInAll
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|includeInAll
operator|=
name|includeInAll
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
comment|/**      * Use the field query created here when matching on numbers.      */
DECL|method|useFieldQueryWithQueryString
annotation|@
name|Override
specifier|public
name|boolean
name|useFieldQueryWithQueryString
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * Numeric field level query are basically range queries with same value and included. That's the recommended      * way to execute it.      */
DECL|method|fieldQuery
annotation|@
name|Override
specifier|public
name|Query
name|fieldQuery
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|rangeQuery
argument_list|(
name|value
argument_list|,
name|value
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**      * Numeric field level filter are basically range queries with same value and included. That's the recommended      * way to execute it.      */
DECL|method|fieldFilter
annotation|@
name|Override
specifier|public
name|Filter
name|fieldFilter
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|rangeFilter
argument_list|(
name|value
argument_list|,
name|value
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|rangeQuery
annotation|@
name|Override
specifier|public
specifier|abstract
name|Query
name|rangeQuery
parameter_list|(
name|String
name|lowerTerm
parameter_list|,
name|String
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
function_decl|;
DECL|method|rangeFilter
annotation|@
name|Override
specifier|public
specifier|abstract
name|Filter
name|rangeFilter
parameter_list|(
name|String
name|lowerTerm
parameter_list|,
name|String
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
function_decl|;
comment|/**      * A range filter based on the field data cache.      */
DECL|method|rangeFilter
specifier|public
specifier|abstract
name|Filter
name|rangeFilter
parameter_list|(
name|FieldDataCache
name|fieldDataCache
parameter_list|,
name|String
name|lowerTerm
parameter_list|,
name|String
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
function_decl|;
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
name|Number
name|num
init|=
name|value
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|num
operator|==
literal|null
condition|?
literal|null
else|:
name|num
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|merge
annotation|@
name|Override
specifier|public
name|void
name|merge
parameter_list|(
name|XContentMapper
name|mergeWith
parameter_list|,
name|MergeContext
name|mergeContext
parameter_list|)
throws|throws
name|MergeMappingException
block|{
name|super
operator|.
name|merge
argument_list|(
name|mergeWith
argument_list|,
name|mergeContext
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|mergeWith
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|mergeContext
operator|.
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|()
condition|)
block|{
name|this
operator|.
name|precisionStep
operator|=
operator|(
operator|(
name|NumberFieldMapper
operator|)
name|mergeWith
operator|)
operator|.
name|precisionStep
expr_stmt|;
name|this
operator|.
name|includeInAll
operator|=
operator|(
operator|(
name|NumberFieldMapper
operator|)
name|mergeWith
operator|)
operator|.
name|includeInAll
expr_stmt|;
block|}
block|}
DECL|method|fieldDataType
annotation|@
name|Override
specifier|public
specifier|abstract
name|FieldDataType
name|fieldDataType
parameter_list|()
function_decl|;
comment|/**      * Removes a cached numeric token stream. The stream will be returned to the cached once it is used      * since it implements the end method.      */
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
name|TIntObjectHashMap
argument_list|<
name|Deque
argument_list|<
name|CachedNumericTokenStream
argument_list|>
argument_list|>
name|cached
init|=
name|cachedStreams
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|cached
operator|!=
literal|null
condition|)
block|{
name|Deque
argument_list|<
name|CachedNumericTokenStream
argument_list|>
name|cachedDeque
init|=
name|cached
operator|.
name|get
argument_list|(
name|precisionStep
argument_list|)
decl_stmt|;
if|if
condition|(
name|cachedDeque
operator|!=
literal|null
condition|)
block|{
name|cachedDeque
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|// used to we can use a numeric field in a document that is then parsed twice!
DECL|class|CustomNumericField
specifier|protected
specifier|abstract
specifier|static
class|class
name|CustomNumericField
extends|extends
name|AbstractField
block|{
DECL|method|CustomNumericField
specifier|public
name|CustomNumericField
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|indexed
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|StringHelper
operator|.
name|intern
argument_list|(
name|name
argument_list|)
expr_stmt|;
comment|// field names are interned
name|fieldsData
operator|=
name|value
expr_stmt|;
name|isIndexed
operator|=
name|indexed
expr_stmt|;
name|isTokenized
operator|=
name|indexed
expr_stmt|;
name|omitTermFreqAndPositions
operator|=
literal|true
expr_stmt|;
name|omitNorms
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|isStored
operator|=
literal|true
expr_stmt|;
name|isBinary
operator|=
literal|true
expr_stmt|;
name|binaryLength
operator|=
name|value
operator|.
name|length
expr_stmt|;
name|binaryOffset
operator|=
literal|0
expr_stmt|;
block|}
name|setStoreTermVector
argument_list|(
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
DECL|method|stringValue
annotation|@
name|Override
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|readerValue
annotation|@
name|Override
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

