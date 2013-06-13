begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.highlight.vectorhighlight
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|highlight
operator|.
name|vectorhighlight
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
name|search
operator|.
name|vectorhighlight
operator|.
name|FastVectorHighlighter
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
name|vectorhighlight
operator|.
name|FieldFragList
operator|.
name|WeightedFragInfo
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
name|vectorhighlight
operator|.
name|FieldFragList
operator|.
name|WeightedFragInfo
operator|.
name|SubInfo
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
name|vectorhighlight
operator|.
name|FragmentsBuilder
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
name|Version
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
name|mapper
operator|.
name|FieldMapper
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

begin_comment
comment|/**  * Simple helper class for {@link FastVectorHighlighter} {@link FragmentsBuilder} implemenations.  */
end_comment

begin_class
DECL|class|FragmentBuilderHelper
specifier|public
specifier|final
class|class
name|FragmentBuilderHelper
block|{
DECL|method|FragmentBuilderHelper
specifier|private
name|FragmentBuilderHelper
parameter_list|()
block|{
comment|// no instance
block|}
comment|/**      * Fixes problems with broken analysis chains if positions and offsets are messed up that can lead to      * {@link StringIndexOutOfBoundsException} in the {@link FastVectorHighlighter}      */
DECL|method|fixWeightedFragInfo
specifier|public
specifier|static
name|WeightedFragInfo
name|fixWeightedFragInfo
parameter_list|(
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|mapper
parameter_list|,
name|Field
index|[]
name|values
parameter_list|,
name|WeightedFragInfo
name|fragInfo
parameter_list|)
block|{
assert|assert
name|fragInfo
operator|!=
literal|null
operator|:
literal|"FragInfo must not be null"
assert|;
assert|assert
name|mapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
operator|.
name|equals
argument_list|(
name|values
index|[
literal|0
index|]
operator|.
name|name
argument_list|()
argument_list|)
operator|:
literal|"Expected FieldMapper for field "
operator|+
name|values
index|[
literal|0
index|]
operator|.
name|name
argument_list|()
assert|;
if|if
condition|(
operator|!
name|fragInfo
operator|.
name|getSubInfos
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|(
name|containsBrokenAnalysis
argument_list|(
name|mapper
operator|.
name|indexAnalyzer
argument_list|()
argument_list|)
operator|)
condition|)
block|{
comment|/* This is a special case where broken analysis like WDF is used for term-vector creation at index-time              * which can potentially mess up the offsets. To prevent a SAIIOBException we need to resort              * the fragments based on their offsets rather than using soley the positions as it is done in              * the FastVectorHighlighter. Yet, this is really a lucene problem and should be fixed in lucene rather              * than in this hack... aka. "we are are working on in!" */
specifier|final
name|List
argument_list|<
name|SubInfo
argument_list|>
name|subInfos
init|=
name|fragInfo
operator|.
name|getSubInfos
argument_list|()
decl_stmt|;
name|CollectionUtil
operator|.
name|quickSort
argument_list|(
name|subInfos
argument_list|,
operator|new
name|Comparator
argument_list|<
name|SubInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|SubInfo
name|o1
parameter_list|,
name|SubInfo
name|o2
parameter_list|)
block|{
name|int
name|startOffset
init|=
name|o1
operator|.
name|getTermsOffsets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStartOffset
argument_list|()
decl_stmt|;
name|int
name|startOffset2
init|=
name|o2
operator|.
name|getTermsOffsets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStartOffset
argument_list|()
decl_stmt|;
return|return
name|FragmentBuilderHelper
operator|.
name|compare
argument_list|(
name|startOffset
argument_list|,
name|startOffset2
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
operator|new
name|WeightedFragInfo
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|fragInfo
operator|.
name|getSubInfos
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTermsOffsets
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStartOffset
argument_list|()
argument_list|,
name|fragInfo
operator|.
name|getStartOffset
argument_list|()
argument_list|)
argument_list|,
name|fragInfo
operator|.
name|getEndOffset
argument_list|()
argument_list|,
name|subInfos
argument_list|,
name|fragInfo
operator|.
name|getTotalBoost
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|fragInfo
return|;
block|}
block|}
DECL|method|compare
specifier|private
specifier|static
name|int
name|compare
parameter_list|(
name|int
name|x
parameter_list|,
name|int
name|y
parameter_list|)
block|{
return|return
operator|(
name|x
operator|<
name|y
operator|)
condition|?
operator|-
literal|1
else|:
operator|(
operator|(
name|x
operator|==
name|y
operator|)
condition|?
literal|0
else|:
literal|1
operator|)
return|;
block|}
DECL|method|containsBrokenAnalysis
specifier|private
specifier|static
name|boolean
name|containsBrokenAnalysis
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
comment|// TODO maybe we need a getter on Namedanalyzer that tells if this uses broken Analysis
if|if
condition|(
name|analyzer
operator|instanceof
name|NamedAnalyzer
condition|)
block|{
name|analyzer
operator|=
operator|(
operator|(
name|NamedAnalyzer
operator|)
name|analyzer
operator|)
operator|.
name|analyzer
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|analyzer
operator|instanceof
name|CustomAnalyzer
condition|)
block|{
specifier|final
name|CustomAnalyzer
name|a
init|=
operator|(
name|CustomAnalyzer
operator|)
name|analyzer
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|tokenizerFactory
argument_list|()
operator|instanceof
name|EdgeNGramTokenizerFactory
operator|||
operator|(
name|a
operator|.
name|tokenizerFactory
argument_list|()
operator|instanceof
name|NGramTokenizerFactory
operator|&&
operator|!
operator|(
operator|(
name|NGramTokenizerFactory
operator|)
name|a
operator|.
name|tokenizerFactory
argument_list|()
operator|)
operator|.
name|version
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_42
argument_list|)
operator|)
condition|)
block|{
comment|// ngram tokenizer is broken before 4.2
return|return
literal|true
return|;
block|}
name|TokenFilterFactory
index|[]
name|tokenFilters
init|=
name|a
operator|.
name|tokenFilters
argument_list|()
decl_stmt|;
for|for
control|(
name|TokenFilterFactory
name|tokenFilterFactory
range|:
name|tokenFilters
control|)
block|{
if|if
condition|(
name|tokenFilterFactory
operator|instanceof
name|WordDelimiterTokenFilterFactory
operator|||
name|tokenFilterFactory
operator|instanceof
name|EdgeNGramTokenFilterFactory
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|tokenFilterFactory
operator|instanceof
name|NGramTokenFilterFactory
operator|&&
operator|!
operator|(
operator|(
name|NGramTokenFilterFactory
operator|)
name|tokenFilterFactory
operator|)
operator|.
name|version
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_42
argument_list|)
condition|)
block|{
comment|// ngram token filter is broken before 4.2
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

