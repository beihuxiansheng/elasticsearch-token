begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.highlight
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|highlight
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
name|index
operator|.
name|IndexOptions
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
name|IndexSearcher
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
name|highlight
operator|.
name|Encoder
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
name|postingshighlight
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
name|CollectionUtil
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
name|Strings
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
name|fetch
operator|.
name|FetchPhaseExecutionException
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
name|fetch
operator|.
name|FetchSubPhase
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
name|text
operator|.
name|BreakIterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_class
DECL|class|PostingsHighlighter
specifier|public
class|class
name|PostingsHighlighter
implements|implements
name|Highlighter
block|{
DECL|field|CACHE_KEY
specifier|private
specifier|static
specifier|final
name|String
name|CACHE_KEY
init|=
literal|"highlight-postings"
decl_stmt|;
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
literal|"postings"
block|,
literal|"postings-highlighter"
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|highlight
specifier|public
name|HighlightField
name|highlight
parameter_list|(
name|HighlighterContext
name|highlighterContext
parameter_list|)
block|{
name|FieldMapper
name|fieldMapper
init|=
name|highlighterContext
operator|.
name|mapper
decl_stmt|;
name|SearchContextHighlight
operator|.
name|Field
name|field
init|=
name|highlighterContext
operator|.
name|field
decl_stmt|;
if|if
condition|(
name|fieldMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"the field ["
operator|+
name|highlighterContext
operator|.
name|fieldName
operator|+
literal|"] should be indexed with positions and offsets in the postings list to be used with postings highlighter"
argument_list|)
throw|;
block|}
name|SearchContext
name|context
init|=
name|highlighterContext
operator|.
name|context
decl_stmt|;
name|FetchSubPhase
operator|.
name|HitContext
name|hitContext
init|=
name|highlighterContext
operator|.
name|hitContext
decl_stmt|;
if|if
condition|(
operator|!
name|hitContext
operator|.
name|cache
argument_list|()
operator|.
name|containsKey
argument_list|(
name|CACHE_KEY
argument_list|)
condition|)
block|{
name|hitContext
operator|.
name|cache
argument_list|()
operator|.
name|put
argument_list|(
name|CACHE_KEY
argument_list|,
operator|new
name|HighlighterEntry
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|HighlighterEntry
name|highlighterEntry
init|=
operator|(
name|HighlighterEntry
operator|)
name|hitContext
operator|.
name|cache
argument_list|()
operator|.
name|get
argument_list|(
name|CACHE_KEY
argument_list|)
decl_stmt|;
name|MapperHighlighterEntry
name|mapperHighlighterEntry
init|=
name|highlighterEntry
operator|.
name|mappers
operator|.
name|get
argument_list|(
name|fieldMapper
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapperHighlighterEntry
operator|==
literal|null
condition|)
block|{
name|Encoder
name|encoder
init|=
name|field
operator|.
name|fieldOptions
argument_list|()
operator|.
name|encoder
argument_list|()
operator|.
name|equals
argument_list|(
literal|"html"
argument_list|)
condition|?
name|HighlightUtils
operator|.
name|Encoders
operator|.
name|HTML
else|:
name|HighlightUtils
operator|.
name|Encoders
operator|.
name|DEFAULT
decl_stmt|;
name|CustomPassageFormatter
name|passageFormatter
init|=
operator|new
name|CustomPassageFormatter
argument_list|(
name|field
operator|.
name|fieldOptions
argument_list|()
operator|.
name|preTags
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|field
operator|.
name|fieldOptions
argument_list|()
operator|.
name|postTags
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|encoder
argument_list|)
decl_stmt|;
name|mapperHighlighterEntry
operator|=
operator|new
name|MapperHighlighterEntry
argument_list|(
name|passageFormatter
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Snippet
argument_list|>
name|snippets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numberOfFragments
decl_stmt|;
try|try
block|{
name|Analyzer
name|analyzer
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|hitContext
operator|.
name|hit
argument_list|()
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|mappers
argument_list|()
operator|.
name|indexAnalyzer
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|fieldValues
init|=
name|HighlightUtils
operator|.
name|loadFieldValues
argument_list|(
name|field
argument_list|,
name|fieldMapper
argument_list|,
name|context
argument_list|,
name|hitContext
argument_list|)
decl_stmt|;
name|CustomPostingsHighlighter
name|highlighter
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|fieldOptions
argument_list|()
operator|.
name|numberOfFragments
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|//we use a control char to separate values, which is the only char that the custom break iterator breaks the text on,
comment|//so we don't lose the distinction between the different values of a field and we get back a snippet per value
name|String
name|fieldValue
init|=
name|mergeFieldValues
argument_list|(
name|fieldValues
argument_list|,
name|HighlightUtils
operator|.
name|NULL_SEPARATOR
argument_list|)
decl_stmt|;
name|CustomSeparatorBreakIterator
name|breakIterator
init|=
operator|new
name|CustomSeparatorBreakIterator
argument_list|(
name|HighlightUtils
operator|.
name|NULL_SEPARATOR
argument_list|)
decl_stmt|;
name|highlighter
operator|=
operator|new
name|CustomPostingsHighlighter
argument_list|(
name|analyzer
argument_list|,
name|mapperHighlighterEntry
operator|.
name|passageFormatter
argument_list|,
name|breakIterator
argument_list|,
name|fieldValue
argument_list|,
name|field
operator|.
name|fieldOptions
argument_list|()
operator|.
name|noMatchSize
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|numberOfFragments
operator|=
name|fieldValues
operator|.
name|size
argument_list|()
expr_stmt|;
comment|//we are highlighting the whole content, one snippet per value
block|}
else|else
block|{
comment|//using paragraph separator we make sure that each field value holds a discrete passage for highlighting
name|String
name|fieldValue
init|=
name|mergeFieldValues
argument_list|(
name|fieldValues
argument_list|,
name|HighlightUtils
operator|.
name|PARAGRAPH_SEPARATOR
argument_list|)
decl_stmt|;
name|highlighter
operator|=
operator|new
name|CustomPostingsHighlighter
argument_list|(
name|analyzer
argument_list|,
name|mapperHighlighterEntry
operator|.
name|passageFormatter
argument_list|,
name|fieldValue
argument_list|,
name|field
operator|.
name|fieldOptions
argument_list|()
operator|.
name|noMatchSize
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|numberOfFragments
operator|=
name|field
operator|.
name|fieldOptions
argument_list|()
operator|.
name|numberOfFragments
argument_list|()
expr_stmt|;
block|}
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|hitContext
operator|.
name|reader
argument_list|()
argument_list|)
decl_stmt|;
name|Snippet
index|[]
name|fieldSnippets
init|=
name|highlighter
operator|.
name|highlightField
argument_list|(
name|fieldMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|highlighterContext
operator|.
name|query
argument_list|,
name|searcher
argument_list|,
name|hitContext
operator|.
name|docId
argument_list|()
argument_list|,
name|numberOfFragments
argument_list|)
decl_stmt|;
for|for
control|(
name|Snippet
name|fieldSnippet
range|:
name|fieldSnippets
control|)
block|{
if|if
condition|(
name|Strings
operator|.
name|hasText
argument_list|(
name|fieldSnippet
operator|.
name|getText
argument_list|()
argument_list|)
condition|)
block|{
name|snippets
operator|.
name|add
argument_list|(
name|fieldSnippet
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|FetchPhaseExecutionException
argument_list|(
name|context
argument_list|,
literal|"Failed to highlight field ["
operator|+
name|highlighterContext
operator|.
name|fieldName
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|snippets
operator|=
name|filterSnippets
argument_list|(
name|snippets
argument_list|,
name|field
operator|.
name|fieldOptions
argument_list|()
operator|.
name|numberOfFragments
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|.
name|fieldOptions
argument_list|()
operator|.
name|scoreOrdered
argument_list|()
condition|)
block|{
comment|//let's sort the snippets by score if needed
name|CollectionUtil
operator|.
name|introSort
argument_list|(
name|snippets
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Snippet
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Snippet
name|o1
parameter_list|,
name|Snippet
name|o2
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|signum
argument_list|(
name|o2
operator|.
name|getScore
argument_list|()
operator|-
name|o1
operator|.
name|getScore
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|fragments
init|=
operator|new
name|String
index|[
name|snippets
operator|.
name|size
argument_list|()
index|]
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
name|fragments
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|fragments
index|[
name|i
index|]
operator|=
name|snippets
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getText
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|fragments
operator|.
name|length
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|HighlightField
argument_list|(
name|highlighterContext
operator|.
name|fieldName
argument_list|,
name|StringText
operator|.
name|convertFromStringArray
argument_list|(
name|fragments
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|mergeFieldValues
specifier|private
specifier|static
name|String
name|mergeFieldValues
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|fieldValues
parameter_list|,
name|char
name|valuesSeparator
parameter_list|)
block|{
comment|//postings highlighter accepts all values in a single string, as offsets etc. need to match with content
comment|//loaded from stored fields, we merge all values using a proper separator
name|String
name|rawValue
init|=
name|Strings
operator|.
name|collectionToDelimitedString
argument_list|(
name|fieldValues
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|valuesSeparator
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|rawValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|rawValue
operator|.
name|length
argument_list|()
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|1
argument_list|)
argument_list|)
return|;
block|}
DECL|method|filterSnippets
specifier|private
specifier|static
name|List
argument_list|<
name|Snippet
argument_list|>
name|filterSnippets
parameter_list|(
name|List
argument_list|<
name|Snippet
argument_list|>
name|snippets
parameter_list|,
name|int
name|numberOfFragments
parameter_list|)
block|{
comment|//We need to filter the snippets as due to no_match_size we could have
comment|//either highlighted snippets or non highlighted ones and we don't want to mix those up
name|List
argument_list|<
name|Snippet
argument_list|>
name|filteredSnippets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|snippets
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Snippet
name|snippet
range|:
name|snippets
control|)
block|{
if|if
condition|(
name|snippet
operator|.
name|isHighlighted
argument_list|()
condition|)
block|{
name|filteredSnippets
operator|.
name|add
argument_list|(
name|snippet
argument_list|)
expr_stmt|;
block|}
block|}
comment|//if there's at least one highlighted snippet, we return all the highlighted ones
comment|//otherwise we return the first non highlighted one if available
if|if
condition|(
name|filteredSnippets
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|snippets
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Snippet
name|snippet
init|=
name|snippets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//if we tried highlighting the whole content using whole break iterator (as number_of_fragments was 0)
comment|//we need to return the first sentence of the content rather than the whole content
if|if
condition|(
name|numberOfFragments
operator|==
literal|0
condition|)
block|{
name|BreakIterator
name|bi
init|=
name|BreakIterator
operator|.
name|getSentenceInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|snippet
operator|.
name|getText
argument_list|()
decl_stmt|;
name|bi
operator|.
name|setText
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|int
name|next
init|=
name|bi
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|!=
name|BreakIterator
operator|.
name|DONE
condition|)
block|{
name|String
name|newText
init|=
name|text
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|next
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|snippet
operator|=
operator|new
name|Snippet
argument_list|(
name|newText
argument_list|,
name|snippet
operator|.
name|getScore
argument_list|()
argument_list|,
name|snippet
operator|.
name|isHighlighted
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|filteredSnippets
operator|.
name|add
argument_list|(
name|snippet
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|filteredSnippets
return|;
block|}
DECL|class|HighlighterEntry
specifier|private
specifier|static
class|class
name|HighlighterEntry
block|{
DECL|field|mappers
name|Map
argument_list|<
name|FieldMapper
argument_list|,
name|MapperHighlighterEntry
argument_list|>
name|mappers
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
block|}
DECL|class|MapperHighlighterEntry
specifier|private
specifier|static
class|class
name|MapperHighlighterEntry
block|{
DECL|field|passageFormatter
specifier|final
name|CustomPassageFormatter
name|passageFormatter
decl_stmt|;
DECL|method|MapperHighlighterEntry
specifier|private
name|MapperHighlighterEntry
parameter_list|(
name|CustomPassageFormatter
name|passageFormatter
parameter_list|)
block|{
name|this
operator|.
name|passageFormatter
operator|=
name|passageFormatter
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

