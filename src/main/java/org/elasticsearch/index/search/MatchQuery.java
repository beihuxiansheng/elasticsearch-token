begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
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
name|CachingTokenFilter
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
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
name|Term
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
name|UnicodeUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalStateException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchParseException
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
name|io
operator|.
name|FastStringReader
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
name|lucene
operator|.
name|search
operator|.
name|MatchNoDocsQuery
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
name|lucene
operator|.
name|search
operator|.
name|MultiPhrasePrefixQuery
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
name|MapperService
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
name|index
operator|.
name|query
operator|.
name|support
operator|.
name|QueryParsers
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
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|support
operator|.
name|QueryParsers
operator|.
name|wrapSmartNameQuery
import|;
end_import

begin_class
DECL|class|MatchQuery
specifier|public
class|class
name|MatchQuery
block|{
DECL|enum|Type
specifier|public
specifier|static
enum|enum
name|Type
block|{
DECL|enum constant|BOOLEAN
name|BOOLEAN
block|,
DECL|enum constant|PHRASE
name|PHRASE
block|,
DECL|enum constant|PHRASE_PREFIX
name|PHRASE_PREFIX
block|}
DECL|field|parseContext
specifier|protected
specifier|final
name|QueryParseContext
name|parseContext
decl_stmt|;
DECL|field|analyzer
specifier|protected
name|String
name|analyzer
decl_stmt|;
DECL|field|occur
specifier|protected
name|BooleanClause
operator|.
name|Occur
name|occur
init|=
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
decl_stmt|;
DECL|field|enablePositionIncrements
specifier|protected
name|boolean
name|enablePositionIncrements
init|=
literal|true
decl_stmt|;
DECL|field|phraseSlop
specifier|protected
name|int
name|phraseSlop
init|=
literal|0
decl_stmt|;
DECL|field|fuzziness
specifier|protected
name|String
name|fuzziness
init|=
literal|null
decl_stmt|;
DECL|field|fuzzyPrefixLength
specifier|protected
name|int
name|fuzzyPrefixLength
init|=
name|FuzzyQuery
operator|.
name|defaultPrefixLength
decl_stmt|;
DECL|field|maxExpansions
specifier|protected
name|int
name|maxExpansions
init|=
name|FuzzyQuery
operator|.
name|defaultMaxExpansions
decl_stmt|;
comment|//LUCENE 4 UPGRADE we need a default value for this!
DECL|field|transpositions
specifier|protected
name|boolean
name|transpositions
init|=
literal|false
decl_stmt|;
DECL|field|rewriteMethod
specifier|protected
name|MultiTermQuery
operator|.
name|RewriteMethod
name|rewriteMethod
decl_stmt|;
DECL|field|fuzzyRewriteMethod
specifier|protected
name|MultiTermQuery
operator|.
name|RewriteMethod
name|fuzzyRewriteMethod
decl_stmt|;
DECL|field|lenient
specifier|protected
name|boolean
name|lenient
decl_stmt|;
DECL|method|MatchQuery
specifier|public
name|MatchQuery
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
block|{
name|this
operator|.
name|parseContext
operator|=
name|parseContext
expr_stmt|;
block|}
DECL|method|setAnalyzer
specifier|public
name|void
name|setAnalyzer
parameter_list|(
name|String
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
block|}
DECL|method|setOccur
specifier|public
name|void
name|setOccur
parameter_list|(
name|BooleanClause
operator|.
name|Occur
name|occur
parameter_list|)
block|{
name|this
operator|.
name|occur
operator|=
name|occur
expr_stmt|;
block|}
DECL|method|setEnablePositionIncrements
specifier|public
name|void
name|setEnablePositionIncrements
parameter_list|(
name|boolean
name|enablePositionIncrements
parameter_list|)
block|{
name|this
operator|.
name|enablePositionIncrements
operator|=
name|enablePositionIncrements
expr_stmt|;
block|}
DECL|method|setPhraseSlop
specifier|public
name|void
name|setPhraseSlop
parameter_list|(
name|int
name|phraseSlop
parameter_list|)
block|{
name|this
operator|.
name|phraseSlop
operator|=
name|phraseSlop
expr_stmt|;
block|}
DECL|method|setFuzziness
specifier|public
name|void
name|setFuzziness
parameter_list|(
name|String
name|fuzziness
parameter_list|)
block|{
name|this
operator|.
name|fuzziness
operator|=
name|fuzziness
expr_stmt|;
block|}
DECL|method|setFuzzyPrefixLength
specifier|public
name|void
name|setFuzzyPrefixLength
parameter_list|(
name|int
name|fuzzyPrefixLength
parameter_list|)
block|{
name|this
operator|.
name|fuzzyPrefixLength
operator|=
name|fuzzyPrefixLength
expr_stmt|;
block|}
DECL|method|setMaxExpansions
specifier|public
name|void
name|setMaxExpansions
parameter_list|(
name|int
name|maxExpansions
parameter_list|)
block|{
name|this
operator|.
name|maxExpansions
operator|=
name|maxExpansions
expr_stmt|;
block|}
DECL|method|setTranspositions
specifier|public
name|void
name|setTranspositions
parameter_list|(
name|boolean
name|transpositions
parameter_list|)
block|{
name|this
operator|.
name|transpositions
operator|=
name|transpositions
expr_stmt|;
block|}
DECL|method|setRewriteMethod
specifier|public
name|void
name|setRewriteMethod
parameter_list|(
name|MultiTermQuery
operator|.
name|RewriteMethod
name|rewriteMethod
parameter_list|)
block|{
name|this
operator|.
name|rewriteMethod
operator|=
name|rewriteMethod
expr_stmt|;
block|}
DECL|method|setFuzzyRewriteMethod
specifier|public
name|void
name|setFuzzyRewriteMethod
parameter_list|(
name|MultiTermQuery
operator|.
name|RewriteMethod
name|fuzzyRewriteMethod
parameter_list|)
block|{
name|this
operator|.
name|fuzzyRewriteMethod
operator|=
name|fuzzyRewriteMethod
expr_stmt|;
block|}
DECL|method|setLenient
specifier|public
name|void
name|setLenient
parameter_list|(
name|boolean
name|lenient
parameter_list|)
block|{
name|this
operator|.
name|lenient
operator|=
name|lenient
expr_stmt|;
block|}
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|(
name|Type
name|type
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|String
name|text
parameter_list|)
block|{
name|FieldMapper
name|mapper
init|=
literal|null
decl_stmt|;
specifier|final
name|String
name|field
decl_stmt|;
name|MapperService
operator|.
name|SmartNameFieldMappers
name|smartNameFieldMappers
init|=
name|parseContext
operator|.
name|smartFieldMappers
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|smartNameFieldMappers
operator|!=
literal|null
operator|&&
name|smartNameFieldMappers
operator|.
name|hasMapper
argument_list|()
condition|)
block|{
name|mapper
operator|=
name|smartNameFieldMappers
operator|.
name|mapper
argument_list|()
expr_stmt|;
name|field
operator|=
name|mapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|field
operator|=
name|fieldName
expr_stmt|;
block|}
if|if
condition|(
name|mapper
operator|!=
literal|null
operator|&&
name|mapper
operator|.
name|useFieldQueryWithQueryString
argument_list|()
condition|)
block|{
if|if
condition|(
name|smartNameFieldMappers
operator|.
name|explicitTypeInNameWithDocMapper
argument_list|()
condition|)
block|{
name|String
index|[]
name|previousTypes
init|=
name|QueryParseContext
operator|.
name|setTypesWithPrevious
argument_list|(
operator|new
name|String
index|[]
block|{
name|smartNameFieldMappers
operator|.
name|docMapper
argument_list|()
operator|.
name|type
argument_list|()
block|}
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|wrapSmartNameQuery
argument_list|(
name|mapper
operator|.
name|fieldQuery
argument_list|(
name|text
argument_list|,
name|parseContext
argument_list|)
argument_list|,
name|smartNameFieldMappers
argument_list|,
name|parseContext
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
if|if
condition|(
name|lenient
condition|)
block|{
return|return
literal|null
return|;
block|}
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|QueryParseContext
operator|.
name|setTypes
argument_list|(
name|previousTypes
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
block|{
return|return
name|wrapSmartNameQuery
argument_list|(
name|mapper
operator|.
name|fieldQuery
argument_list|(
name|text
argument_list|,
name|parseContext
argument_list|)
argument_list|,
name|smartNameFieldMappers
argument_list|,
name|parseContext
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
if|if
condition|(
name|lenient
condition|)
block|{
return|return
literal|null
return|;
block|}
throw|throw
name|e
throw|;
block|}
block|}
block|}
name|Analyzer
name|analyzer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|analyzer
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|mapper
operator|!=
literal|null
condition|)
block|{
name|analyzer
operator|=
name|mapper
operator|.
name|searchAnalyzer
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|analyzer
operator|==
literal|null
operator|&&
name|smartNameFieldMappers
operator|!=
literal|null
condition|)
block|{
name|analyzer
operator|=
name|smartNameFieldMappers
operator|.
name|searchAnalyzer
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
name|analyzer
operator|=
name|parseContext
operator|.
name|mapperService
argument_list|()
operator|.
name|searchAnalyzer
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|analyzer
operator|=
name|parseContext
operator|.
name|mapperService
argument_list|()
operator|.
name|analysisService
argument_list|()
operator|.
name|analyzer
argument_list|(
name|this
operator|.
name|analyzer
argument_list|)
expr_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"No analyzer found for ["
operator|+
name|this
operator|.
name|analyzer
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|// Logic similar to QueryParser#getFieldQuery
specifier|final
name|TokenStream
name|source
decl_stmt|;
try|try
block|{
name|source
operator|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
operator|new
name|FastStringReader
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
name|source
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|//LUCENE 4 UPGRADE not sure what todo here really lucene 3.6 had a tokenStream that didn't throw an exc.
throw|throw
operator|new
name|ElasticSearchParseException
argument_list|(
literal|"failed to process query"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
name|CachingTokenFilter
name|buffer
init|=
operator|new
name|CachingTokenFilter
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
literal|null
decl_stmt|;
name|PositionIncrementAttribute
name|posIncrAtt
init|=
literal|null
decl_stmt|;
name|int
name|numTokens
init|=
literal|0
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
if|if
condition|(
name|buffer
operator|.
name|hasAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|termAtt
operator|=
name|buffer
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|buffer
operator|.
name|hasAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|posIncrAtt
operator|=
name|buffer
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|positionCount
init|=
literal|0
decl_stmt|;
name|boolean
name|severalTokensAtSamePosition
init|=
literal|false
decl_stmt|;
name|boolean
name|hasMoreTokens
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|termAtt
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|hasMoreTokens
operator|=
name|buffer
operator|.
name|incrementToken
argument_list|()
expr_stmt|;
while|while
condition|(
name|hasMoreTokens
condition|)
block|{
name|numTokens
operator|++
expr_stmt|;
name|int
name|positionIncrement
init|=
operator|(
name|posIncrAtt
operator|!=
literal|null
operator|)
condition|?
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
else|:
literal|1
decl_stmt|;
if|if
condition|(
name|positionIncrement
operator|!=
literal|0
condition|)
block|{
name|positionCount
operator|+=
name|positionIncrement
expr_stmt|;
block|}
else|else
block|{
name|severalTokensAtSamePosition
operator|=
literal|true
expr_stmt|;
block|}
name|hasMoreTokens
operator|=
name|buffer
operator|.
name|incrementToken
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
try|try
block|{
comment|// rewind the buffer stream
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// close original stream - all tokens buffered
name|source
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
if|if
condition|(
name|numTokens
operator|==
literal|0
condition|)
block|{
return|return
name|MatchNoDocsQuery
operator|.
name|INSTANCE
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|BOOLEAN
condition|)
block|{
if|if
condition|(
name|numTokens
operator|==
literal|1
condition|)
block|{
try|try
block|{
name|boolean
name|hasNext
init|=
name|buffer
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
assert|assert
name|hasNext
operator|==
literal|true
assert|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// safe to ignore, because we know the number of tokens
block|}
comment|//LUCENE 4 UPGRADE instead of string term we can convert directly from utf-16 to utf-8
name|Query
name|q
init|=
name|newTermQuery
argument_list|(
name|mapper
argument_list|,
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|termToByteRef
argument_list|(
name|termAtt
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|wrapSmartNameQuery
argument_list|(
name|q
argument_list|,
name|smartNameFieldMappers
argument_list|,
name|parseContext
argument_list|)
return|;
block|}
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|(
name|positionCount
operator|==
literal|1
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
name|numTokens
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|boolean
name|hasNext
init|=
name|buffer
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
assert|assert
name|hasNext
operator|==
literal|true
assert|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// safe to ignore, because we know the number of tokens
block|}
comment|//LUCENE 4 UPGRADE instead of string term we can convert directly from utf-16 to utf-8
name|Query
name|currentQuery
init|=
name|newTermQuery
argument_list|(
name|mapper
argument_list|,
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|termToByteRef
argument_list|(
name|termAtt
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|currentQuery
argument_list|,
name|occur
argument_list|)
expr_stmt|;
block|}
return|return
name|wrapSmartNameQuery
argument_list|(
name|q
argument_list|,
name|smartNameFieldMappers
argument_list|,
name|parseContext
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|PHRASE
condition|)
block|{
if|if
condition|(
name|severalTokensAtSamePosition
condition|)
block|{
name|MultiPhraseQuery
name|mpq
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|mpq
operator|.
name|setSlop
argument_list|(
name|phraseSlop
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Term
argument_list|>
name|multiTerms
init|=
operator|new
name|ArrayList
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|position
init|=
operator|-
literal|1
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
name|numTokens
condition|;
name|i
operator|++
control|)
block|{
name|int
name|positionIncrement
init|=
literal|1
decl_stmt|;
try|try
block|{
name|boolean
name|hasNext
init|=
name|buffer
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
assert|assert
name|hasNext
operator|==
literal|true
assert|;
if|if
condition|(
name|posIncrAtt
operator|!=
literal|null
condition|)
block|{
name|positionIncrement
operator|=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// safe to ignore, because we know the number of tokens
block|}
if|if
condition|(
name|positionIncrement
operator|>
literal|0
operator|&&
name|multiTerms
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|enablePositionIncrements
condition|)
block|{
name|mpq
operator|.
name|add
argument_list|(
name|multiTerms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|multiTerms
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mpq
operator|.
name|add
argument_list|(
name|multiTerms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|multiTerms
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|multiTerms
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|position
operator|+=
name|positionIncrement
expr_stmt|;
comment|//LUCENE 4 UPGRADE instead of string term we can convert directly from utf-16 to utf-8
name|multiTerms
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|termToByteRef
argument_list|(
name|termAtt
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|enablePositionIncrements
condition|)
block|{
name|mpq
operator|.
name|add
argument_list|(
name|multiTerms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|multiTerms
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mpq
operator|.
name|add
argument_list|(
name|multiTerms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|multiTerms
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|wrapSmartNameQuery
argument_list|(
name|mpq
argument_list|,
name|smartNameFieldMappers
argument_list|,
name|parseContext
argument_list|)
return|;
block|}
else|else
block|{
name|PhraseQuery
name|pq
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|pq
operator|.
name|setSlop
argument_list|(
name|phraseSlop
argument_list|)
expr_stmt|;
name|int
name|position
init|=
operator|-
literal|1
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
name|numTokens
condition|;
name|i
operator|++
control|)
block|{
name|int
name|positionIncrement
init|=
literal|1
decl_stmt|;
try|try
block|{
name|boolean
name|hasNext
init|=
name|buffer
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
assert|assert
name|hasNext
operator|==
literal|true
assert|;
if|if
condition|(
name|posIncrAtt
operator|!=
literal|null
condition|)
block|{
name|positionIncrement
operator|=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// safe to ignore, because we know the number of tokens
block|}
if|if
condition|(
name|enablePositionIncrements
condition|)
block|{
name|position
operator|+=
name|positionIncrement
expr_stmt|;
comment|//LUCENE 4 UPGRADE instead of string term we can convert directly from utf-16 to utf-8
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|termToByteRef
argument_list|(
name|termAtt
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|termToByteRef
argument_list|(
name|termAtt
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|wrapSmartNameQuery
argument_list|(
name|pq
argument_list|,
name|smartNameFieldMappers
argument_list|,
name|parseContext
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|PHRASE_PREFIX
condition|)
block|{
name|MultiPhrasePrefixQuery
name|mpq
init|=
operator|new
name|MultiPhrasePrefixQuery
argument_list|()
decl_stmt|;
name|mpq
operator|.
name|setSlop
argument_list|(
name|phraseSlop
argument_list|)
expr_stmt|;
name|mpq
operator|.
name|setMaxExpansions
argument_list|(
name|maxExpansions
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Term
argument_list|>
name|multiTerms
init|=
operator|new
name|ArrayList
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|position
init|=
operator|-
literal|1
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
name|numTokens
condition|;
name|i
operator|++
control|)
block|{
name|int
name|positionIncrement
init|=
literal|1
decl_stmt|;
try|try
block|{
name|boolean
name|hasNext
init|=
name|buffer
operator|.
name|incrementToken
argument_list|()
decl_stmt|;
assert|assert
name|hasNext
operator|==
literal|true
assert|;
if|if
condition|(
name|posIncrAtt
operator|!=
literal|null
condition|)
block|{
name|positionIncrement
operator|=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// safe to ignore, because we know the number of tokens
block|}
if|if
condition|(
name|positionIncrement
operator|>
literal|0
operator|&&
name|multiTerms
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|enablePositionIncrements
condition|)
block|{
name|mpq
operator|.
name|add
argument_list|(
name|multiTerms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|multiTerms
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mpq
operator|.
name|add
argument_list|(
name|multiTerms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|multiTerms
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|multiTerms
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|position
operator|+=
name|positionIncrement
expr_stmt|;
comment|//LUCENE 4 UPGRADE instead of string term we can convert directly from utf-16 to utf-8
name|multiTerms
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|termToByteRef
argument_list|(
name|termAtt
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|enablePositionIncrements
condition|)
block|{
name|mpq
operator|.
name|add
argument_list|(
name|multiTerms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|multiTerms
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mpq
operator|.
name|add
argument_list|(
name|multiTerms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|multiTerms
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|wrapSmartNameQuery
argument_list|(
name|mpq
argument_list|,
name|smartNameFieldMappers
argument_list|,
name|parseContext
argument_list|)
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"No type found for ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
DECL|method|newTermQuery
specifier|private
name|Query
name|newTermQuery
parameter_list|(
annotation|@
name|Nullable
name|FieldMapper
name|mapper
parameter_list|,
name|Term
name|term
parameter_list|)
block|{
if|if
condition|(
name|fuzziness
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|mapper
operator|!=
literal|null
condition|)
block|{
name|Query
name|query
init|=
name|mapper
operator|.
name|fuzzyQuery
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|,
name|fuzziness
argument_list|,
name|fuzzyPrefixLength
argument_list|,
name|maxExpansions
argument_list|,
name|transpositions
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|instanceof
name|FuzzyQuery
condition|)
block|{
name|QueryParsers
operator|.
name|setRewriteMethod
argument_list|(
operator|(
name|FuzzyQuery
operator|)
name|query
argument_list|,
name|fuzzyRewriteMethod
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|text
init|=
name|term
operator|.
name|text
argument_list|()
decl_stmt|;
comment|//LUCENE 4 UPGRADE we need to document that this should now be an int rather than a float
name|int
name|edits
init|=
name|FuzzyQuery
operator|.
name|floatToEdits
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|fuzziness
argument_list|)
argument_list|,
name|text
operator|.
name|codePointCount
argument_list|(
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|FuzzyQuery
name|query
init|=
operator|new
name|FuzzyQuery
argument_list|(
name|term
argument_list|,
name|edits
argument_list|,
name|fuzzyPrefixLength
argument_list|,
name|maxExpansions
argument_list|,
name|transpositions
argument_list|)
decl_stmt|;
name|QueryParsers
operator|.
name|setRewriteMethod
argument_list|(
name|query
argument_list|,
name|rewriteMethod
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
if|if
condition|(
name|mapper
operator|!=
literal|null
condition|)
block|{
name|Query
name|termQuery
init|=
name|mapper
operator|.
name|queryStringTermQuery
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|termQuery
operator|!=
literal|null
condition|)
block|{
return|return
name|termQuery
return|;
block|}
block|}
return|return
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
return|;
block|}
DECL|method|termToByteRef
specifier|private
specifier|static
name|BytesRef
name|termToByteRef
parameter_list|(
name|CharTermAttribute
name|attr
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
block|{
name|UnicodeUtil
operator|.
name|UTF16toUTF8WithHash
argument_list|(
name|attr
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|attr
operator|.
name|length
argument_list|()
argument_list|,
name|ref
argument_list|)
expr_stmt|;
return|return
name|ref
return|;
block|}
block|}
end_class

end_unit

