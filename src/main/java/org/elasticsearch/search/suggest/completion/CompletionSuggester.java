begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.completion
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|index
operator|.
name|AtomicReader
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
name|index
operator|.
name|AtomicReaderContext
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|Terms
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
name|suggest
operator|.
name|Lookup
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
name|CharsRef
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
name|CollectionUtil
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
name|UnicodeUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|text
operator|.
name|StringText
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
name|CompletionFieldMapper
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
name|suggest
operator|.
name|Suggest
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
name|suggest
operator|.
name|SuggestContextParser
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
name|suggest
operator|.
name|Suggester
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
name|suggest
operator|.
name|completion
operator|.
name|CompletionSuggestion
operator|.
name|Entry
operator|.
name|Option
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
name|Comparator
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

begin_class
DECL|class|CompletionSuggester
specifier|public
class|class
name|CompletionSuggester
extends|extends
name|Suggester
argument_list|<
name|CompletionSuggestionContext
argument_list|>
block|{
DECL|field|scoreComparator
specifier|private
specifier|static
specifier|final
name|ScoreComparator
name|scoreComparator
init|=
operator|new
name|ScoreComparator
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|innerExecute
specifier|protected
name|Suggest
operator|.
name|Suggestion
argument_list|<
name|?
extends|extends
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
argument_list|<
name|?
extends|extends
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|>
argument_list|>
name|innerExecute
parameter_list|(
name|String
name|name
parameter_list|,
name|CompletionSuggestionContext
name|suggestionContext
parameter_list|,
name|IndexReader
name|indexReader
parameter_list|,
name|CharsRef
name|spare
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|suggestionContext
operator|.
name|mapper
argument_list|()
operator|==
literal|null
operator|||
operator|!
operator|(
name|suggestionContext
operator|.
name|mapper
argument_list|()
operator|instanceof
name|CompletionFieldMapper
operator|)
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchException
argument_list|(
literal|"Field ["
operator|+
name|suggestionContext
operator|.
name|getField
argument_list|()
operator|+
literal|"] is not a completion suggest field"
argument_list|)
throw|;
block|}
name|CompletionSuggestion
name|completionSuggestion
init|=
operator|new
name|CompletionSuggestion
argument_list|(
name|name
argument_list|,
name|suggestionContext
operator|.
name|getSize
argument_list|()
argument_list|)
decl_stmt|;
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|suggestionContext
operator|.
name|getText
argument_list|()
argument_list|,
name|spare
argument_list|)
expr_stmt|;
name|CompletionSuggestion
operator|.
name|Entry
name|completionSuggestEntry
init|=
operator|new
name|CompletionSuggestion
operator|.
name|Entry
argument_list|(
operator|new
name|StringText
argument_list|(
name|spare
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|,
name|spare
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|completionSuggestion
operator|.
name|addTerm
argument_list|(
name|completionSuggestEntry
argument_list|)
expr_stmt|;
name|String
name|fieldName
init|=
name|suggestionContext
operator|.
name|getField
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|CompletionSuggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|>
name|results
init|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
operator|*
name|suggestionContext
operator|.
name|getSize
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|atomicReaderContext
range|:
name|indexReader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|AtomicReader
name|atomicReader
init|=
name|atomicReaderContext
operator|.
name|reader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|atomicReader
operator|.
name|fields
argument_list|()
operator|.
name|terms
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|instanceof
name|Completion090PostingsFormat
operator|.
name|CompletionTerms
condition|)
block|{
specifier|final
name|Completion090PostingsFormat
operator|.
name|CompletionTerms
name|lookupTerms
init|=
operator|(
name|Completion090PostingsFormat
operator|.
name|CompletionTerms
operator|)
name|terms
decl_stmt|;
specifier|final
name|Lookup
name|lookup
init|=
name|lookupTerms
operator|.
name|getLookup
argument_list|(
name|suggestionContext
operator|.
name|mapper
argument_list|()
argument_list|,
name|suggestionContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|lookup
operator|==
literal|null
condition|)
block|{
comment|// we don't have a lookup for this segment.. this might be possible if a merge dropped all
comment|// docs from the segment that had a value in this segment.
continue|continue;
block|}
name|List
argument_list|<
name|Lookup
operator|.
name|LookupResult
argument_list|>
name|lookupResults
init|=
name|lookup
operator|.
name|lookup
argument_list|(
name|spare
argument_list|,
literal|false
argument_list|,
name|suggestionContext
operator|.
name|getSize
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Lookup
operator|.
name|LookupResult
name|res
range|:
name|lookupResults
control|)
block|{
specifier|final
name|String
name|key
init|=
name|res
operator|.
name|key
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|float
name|score
init|=
name|res
operator|.
name|value
decl_stmt|;
specifier|final
name|Option
name|value
init|=
name|results
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
specifier|final
name|Option
name|option
init|=
operator|new
name|CompletionSuggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|(
operator|new
name|StringText
argument_list|(
name|key
argument_list|)
argument_list|,
name|score
argument_list|,
name|res
operator|.
name|payload
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|BytesArray
argument_list|(
name|res
operator|.
name|payload
argument_list|)
argument_list|)
decl_stmt|;
name|results
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|option
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|getScore
argument_list|()
operator|<
name|score
condition|)
block|{
name|value
operator|.
name|setScore
argument_list|(
name|score
argument_list|)
expr_stmt|;
name|value
operator|.
name|setPayload
argument_list|(
name|res
operator|.
name|payload
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|BytesArray
argument_list|(
name|res
operator|.
name|payload
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|final
name|List
argument_list|<
name|CompletionSuggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|>
name|options
init|=
operator|new
name|ArrayList
argument_list|<
name|CompletionSuggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|>
argument_list|(
name|results
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|CollectionUtil
operator|.
name|introSort
argument_list|(
name|options
argument_list|,
name|scoreComparator
argument_list|)
expr_stmt|;
name|int
name|optionCount
init|=
name|Math
operator|.
name|min
argument_list|(
name|suggestionContext
operator|.
name|getSize
argument_list|()
argument_list|,
name|options
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|optionCount
condition|;
name|i
operator|++
control|)
block|{
name|completionSuggestEntry
operator|.
name|addOption
argument_list|(
name|options
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|completionSuggestion
return|;
block|}
annotation|@
name|Override
DECL|method|names
specifier|public
name|String
index|[]
name|names
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"completion"
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getContextParser
specifier|public
name|SuggestContextParser
name|getContextParser
parameter_list|()
block|{
return|return
operator|new
name|CompletionSuggestParser
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|class|ScoreComparator
specifier|public
specifier|static
class|class
name|ScoreComparator
implements|implements
name|Comparator
argument_list|<
name|CompletionSuggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Option
name|o1
parameter_list|,
name|Option
name|o2
parameter_list|)
block|{
return|return
name|Float
operator|.
name|compare
argument_list|(
name|o2
operator|.
name|getScore
argument_list|()
argument_list|,
name|o1
operator|.
name|getScore
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

