begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.plain
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|plain
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
name|index
operator|.
name|*
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
name|BytesRef
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
name|CharsRefBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|Nullable
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
name|settings
operator|.
name|Settings
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
name|Index
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
name|*
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
operator|.
name|XFieldComparatorSource
operator|.
name|Nested
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
name|fieldcomparator
operator|.
name|BytesRefFieldComparatorSource
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
name|ordinals
operator|.
name|GlobalOrdinalsBuilder
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
name|MappedFieldType
operator|.
name|Names
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|breaker
operator|.
name|CircuitBreakerService
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
name|MultiValueMode
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
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

begin_class
DECL|class|AbstractIndexOrdinalsFieldData
specifier|public
specifier|abstract
class|class
name|AbstractIndexOrdinalsFieldData
extends|extends
name|AbstractIndexFieldData
argument_list|<
name|AtomicOrdinalsFieldData
argument_list|>
implements|implements
name|IndexOrdinalsFieldData
block|{
DECL|field|frequency
specifier|protected
name|Settings
name|frequency
decl_stmt|;
DECL|field|regex
specifier|protected
name|Settings
name|regex
decl_stmt|;
DECL|field|breakerService
specifier|protected
specifier|final
name|CircuitBreakerService
name|breakerService
decl_stmt|;
DECL|method|AbstractIndexOrdinalsFieldData
specifier|protected
name|AbstractIndexOrdinalsFieldData
parameter_list|(
name|Index
name|index
parameter_list|,
name|Settings
name|indexSettings
parameter_list|,
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|,
name|IndexFieldDataCache
name|cache
parameter_list|,
name|CircuitBreakerService
name|breakerService
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|fieldNames
argument_list|,
name|fieldDataType
argument_list|,
name|cache
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|groups
init|=
name|fieldDataType
operator|.
name|getSettings
argument_list|()
operator|.
name|getGroups
argument_list|(
literal|"filter"
argument_list|)
decl_stmt|;
name|frequency
operator|=
name|groups
operator|.
name|get
argument_list|(
literal|"frequency"
argument_list|)
expr_stmt|;
name|regex
operator|=
name|groups
operator|.
name|get
argument_list|(
literal|"regex"
argument_list|)
expr_stmt|;
name|this
operator|.
name|breakerService
operator|=
name|breakerService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|comparatorSource
specifier|public
name|XFieldComparatorSource
name|comparatorSource
parameter_list|(
annotation|@
name|Nullable
name|Object
name|missingValue
parameter_list|,
name|MultiValueMode
name|sortMode
parameter_list|,
name|Nested
name|nested
parameter_list|)
block|{
return|return
operator|new
name|BytesRefFieldComparatorSource
argument_list|(
name|this
argument_list|,
name|missingValue
argument_list|,
name|sortMode
argument_list|,
name|nested
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|loadGlobal
specifier|public
name|IndexOrdinalsFieldData
name|loadGlobal
parameter_list|(
name|DirectoryReader
name|indexReader
parameter_list|)
block|{
if|if
condition|(
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
operator|<=
literal|1
condition|)
block|{
comment|// ordinals are already global
return|return
name|this
return|;
block|}
try|try
block|{
return|return
name|cache
operator|.
name|load
argument_list|(
name|indexReader
argument_list|,
name|this
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|ElasticsearchException
condition|)
block|{
throw|throw
operator|(
name|ElasticsearchException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|localGlobalDirect
specifier|public
name|IndexOrdinalsFieldData
name|localGlobalDirect
parameter_list|(
name|DirectoryReader
name|indexReader
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|GlobalOrdinalsBuilder
operator|.
name|build
argument_list|(
name|indexReader
argument_list|,
name|this
argument_list|,
name|indexSettings
argument_list|,
name|breakerService
argument_list|,
name|logger
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|empty
specifier|protected
name|AtomicOrdinalsFieldData
name|empty
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
return|return
name|AbstractAtomicOrdinalsFieldData
operator|.
name|empty
argument_list|()
return|;
block|}
DECL|method|filter
specifier|protected
name|TermsEnum
name|filter
parameter_list|(
name|Terms
name|terms
parameter_list|,
name|LeafReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|TermsEnum
name|iterator
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|iterator
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|iterator
operator|!=
literal|null
operator|&&
name|frequency
operator|!=
literal|null
condition|)
block|{
name|iterator
operator|=
name|FrequencyFilter
operator|.
name|filter
argument_list|(
name|iterator
argument_list|,
name|terms
argument_list|,
name|reader
argument_list|,
name|frequency
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|iterator
operator|!=
literal|null
operator|&&
name|regex
operator|!=
literal|null
condition|)
block|{
name|iterator
operator|=
name|RegexFilter
operator|.
name|filter
argument_list|(
name|iterator
argument_list|,
name|terms
argument_list|,
name|reader
argument_list|,
name|regex
argument_list|)
expr_stmt|;
block|}
return|return
name|iterator
return|;
block|}
DECL|class|FrequencyFilter
specifier|private
specifier|static
specifier|final
class|class
name|FrequencyFilter
extends|extends
name|FilteredTermsEnum
block|{
DECL|field|minFreq
specifier|private
name|int
name|minFreq
decl_stmt|;
DECL|field|maxFreq
specifier|private
name|int
name|maxFreq
decl_stmt|;
DECL|method|FrequencyFilter
specifier|public
name|FrequencyFilter
parameter_list|(
name|TermsEnum
name|delegate
parameter_list|,
name|int
name|minFreq
parameter_list|,
name|int
name|maxFreq
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|minFreq
operator|=
name|minFreq
expr_stmt|;
name|this
operator|.
name|maxFreq
operator|=
name|maxFreq
expr_stmt|;
block|}
DECL|method|filter
specifier|public
specifier|static
name|TermsEnum
name|filter
parameter_list|(
name|TermsEnum
name|toFilter
parameter_list|,
name|Terms
name|terms
parameter_list|,
name|LeafReader
name|reader
parameter_list|,
name|Settings
name|settings
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|docCount
init|=
name|terms
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|docCount
operator|==
operator|-
literal|1
condition|)
block|{
name|docCount
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
specifier|final
name|double
name|minFrequency
init|=
name|settings
operator|.
name|getAsDouble
argument_list|(
literal|"min"
argument_list|,
literal|0d
argument_list|)
decl_stmt|;
specifier|final
name|double
name|maxFrequency
init|=
name|settings
operator|.
name|getAsDouble
argument_list|(
literal|"max"
argument_list|,
name|docCount
operator|+
literal|1d
argument_list|)
decl_stmt|;
specifier|final
name|double
name|minSegmentSize
init|=
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"min_segment_size"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|minSegmentSize
operator|<
name|docCount
condition|)
block|{
specifier|final
name|int
name|minFreq
init|=
name|minFrequency
operator|>
literal|1.0
condition|?
operator|(
name|int
operator|)
name|minFrequency
else|:
call|(
name|int
call|)
argument_list|(
name|docCount
operator|*
name|minFrequency
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxFreq
init|=
name|maxFrequency
operator|>
literal|1.0
condition|?
operator|(
name|int
operator|)
name|maxFrequency
else|:
call|(
name|int
call|)
argument_list|(
name|docCount
operator|*
name|maxFrequency
argument_list|)
decl_stmt|;
assert|assert
name|minFreq
operator|<
name|maxFreq
assert|;
return|return
operator|new
name|FrequencyFilter
argument_list|(
name|toFilter
argument_list|,
name|minFreq
argument_list|,
name|maxFreq
argument_list|)
return|;
block|}
return|return
name|toFilter
return|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|arg0
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|docFreq
init|=
name|docFreq
argument_list|()
decl_stmt|;
if|if
condition|(
name|docFreq
operator|>=
name|minFreq
operator|&&
name|docFreq
operator|<=
name|maxFreq
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
return|return
name|AcceptStatus
operator|.
name|NO
return|;
block|}
block|}
DECL|class|RegexFilter
specifier|private
specifier|static
specifier|final
class|class
name|RegexFilter
extends|extends
name|FilteredTermsEnum
block|{
DECL|field|matcher
specifier|private
specifier|final
name|Matcher
name|matcher
decl_stmt|;
DECL|field|spare
specifier|private
specifier|final
name|CharsRefBuilder
name|spare
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
DECL|method|RegexFilter
specifier|public
name|RegexFilter
parameter_list|(
name|TermsEnum
name|delegate
parameter_list|,
name|Matcher
name|matcher
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|matcher
operator|=
name|matcher
expr_stmt|;
block|}
DECL|method|filter
specifier|public
specifier|static
name|TermsEnum
name|filter
parameter_list|(
name|TermsEnum
name|iterator
parameter_list|,
name|Terms
name|terms
parameter_list|,
name|LeafReader
name|reader
parameter_list|,
name|Settings
name|regex
parameter_list|)
block|{
name|String
name|pattern
init|=
name|regex
operator|.
name|get
argument_list|(
literal|"pattern"
argument_list|)
decl_stmt|;
if|if
condition|(
name|pattern
operator|==
literal|null
condition|)
block|{
return|return
name|iterator
return|;
block|}
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|pattern
argument_list|)
decl_stmt|;
return|return
operator|new
name|RegexFilter
argument_list|(
name|iterator
argument_list|,
name|p
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|arg0
parameter_list|)
throws|throws
name|IOException
block|{
name|spare
operator|.
name|copyUTF8Bytes
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
name|matcher
operator|.
name|reset
argument_list|(
name|spare
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
return|return
name|AcceptStatus
operator|.
name|NO
return|;
block|}
block|}
block|}
end_class

end_unit

