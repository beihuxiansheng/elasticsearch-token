begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.lookup
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|lookup
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
name|search
operator|.
name|TermStatistics
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
name|lucene
operator|.
name|search
operator|.
name|EmptyScorer
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
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Holds all information on a particular term in a field.  * */
end_comment

begin_class
DECL|class|IndexFieldTerm
specifier|public
class|class
name|IndexFieldTerm
implements|implements
name|Iterable
argument_list|<
name|TermPosition
argument_list|>
block|{
comment|// The posting list for this term. Is null if the term or field does not
comment|// exist. Can be DocsEnum or DocsAndPositionsEnum.
DECL|field|docsEnum
name|DocsEnum
name|docsEnum
decl_stmt|;
comment|// Stores if positions, offsets and payloads are requested.
DECL|field|flags
specifier|private
specifier|final
name|int
name|flags
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|term
specifier|private
specifier|final
name|String
name|term
decl_stmt|;
DECL|field|iterator
specifier|private
specifier|final
name|PositionIterator
name|iterator
decl_stmt|;
comment|// for lucene calls
DECL|field|identifier
specifier|private
specifier|final
name|Term
name|identifier
decl_stmt|;
DECL|field|termStats
specifier|private
specifier|final
name|TermStatistics
name|termStats
decl_stmt|;
DECL|field|EMPTY_DOCS_ENUM
specifier|static
specifier|private
name|EmptyScorer
name|EMPTY_DOCS_ENUM
init|=
operator|new
name|EmptyScorer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
comment|// get the document frequency of the term
DECL|method|df
specifier|public
name|long
name|df
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|termStats
operator|.
name|docFreq
argument_list|()
return|;
block|}
comment|// get the total term frequency of the term, that is, how often does the
comment|// term appear in any document?
DECL|method|ttf
specifier|public
name|long
name|ttf
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|termStats
operator|.
name|totalTermFreq
argument_list|()
return|;
block|}
comment|// when the reader changes, we have to get the posting list for this term
comment|// and reader
DECL|method|setNextReader
name|void
name|setNextReader
parameter_list|(
name|AtomicReader
name|reader
parameter_list|)
block|{
try|try
block|{
comment|// Get the posting list for a specific term. Depending on the flags,
comment|// this
comment|// will either get a DocsEnum or a DocsAndPositionsEnum if
comment|// available.
comment|// get lucene frequency flag
name|int
name|luceneFrequencyFlag
init|=
name|getLuceneFrequencyFlag
argument_list|(
name|flags
argument_list|)
decl_stmt|;
if|if
condition|(
name|shouldRetrieveFrequenciesOnly
argument_list|()
condition|)
block|{
name|docsEnum
operator|=
name|getOnlyDocsEnum
argument_list|(
name|luceneFrequencyFlag
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|lucenePositionsFlags
init|=
name|getLucenePositionsFlags
argument_list|(
name|flags
argument_list|)
decl_stmt|;
name|docsEnum
operator|=
name|getDocsAndPosEnum
argument_list|(
name|lucenePositionsFlags
argument_list|,
name|reader
argument_list|)
expr_stmt|;
if|if
condition|(
name|docsEnum
operator|==
literal|null
condition|)
block|{
comment|// no pos available
name|docsEnum
operator|=
name|getOnlyDocsEnum
argument_list|(
name|luceneFrequencyFlag
argument_list|,
name|reader
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
name|ElasticsearchException
argument_list|(
literal|"Unable to get posting list for field "
operator|+
name|fieldName
operator|+
literal|" and term "
operator|+
name|term
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|shouldRetrieveFrequenciesOnly
specifier|private
name|boolean
name|shouldRetrieveFrequenciesOnly
parameter_list|()
block|{
return|return
operator|(
name|flags
operator|&
operator|~
name|IndexLookup
operator|.
name|FLAG_FREQUENCIES
operator|)
operator|==
literal|0
return|;
block|}
DECL|method|getLuceneFrequencyFlag
specifier|private
name|int
name|getLuceneFrequencyFlag
parameter_list|(
name|int
name|flags
parameter_list|)
block|{
return|return
operator|(
name|flags
operator|&
name|IndexLookup
operator|.
name|FLAG_FREQUENCIES
operator|)
operator|>
literal|0
condition|?
name|DocsEnum
operator|.
name|FLAG_FREQS
else|:
name|DocsEnum
operator|.
name|FLAG_NONE
return|;
block|}
DECL|method|getLucenePositionsFlags
specifier|private
name|int
name|getLucenePositionsFlags
parameter_list|(
name|int
name|flags
parameter_list|)
block|{
name|int
name|lucenePositionsFlags
init|=
operator|(
name|flags
operator|&
name|IndexLookup
operator|.
name|FLAG_PAYLOADS
operator|)
operator|>
literal|0
condition|?
name|DocsAndPositionsEnum
operator|.
name|FLAG_PAYLOADS
else|:
literal|0x0
decl_stmt|;
name|lucenePositionsFlags
operator||=
operator|(
name|flags
operator|&
name|IndexLookup
operator|.
name|FLAG_OFFSETS
operator|)
operator|>
literal|0
condition|?
name|DocsAndPositionsEnum
operator|.
name|FLAG_OFFSETS
else|:
literal|0x0
expr_stmt|;
return|return
name|lucenePositionsFlags
return|;
block|}
comment|// get the DocsAndPositionsEnum from the reader.
DECL|method|getDocsAndPosEnum
specifier|private
name|DocsEnum
name|getDocsAndPosEnum
parameter_list|(
name|int
name|luceneFlags
parameter_list|,
name|AtomicReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|identifier
operator|.
name|field
argument_list|()
operator|!=
literal|null
assert|;
assert|assert
name|identifier
operator|.
name|bytes
argument_list|()
operator|!=
literal|null
assert|;
specifier|final
name|Fields
name|fields
init|=
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
name|DocsEnum
name|newDocsEnum
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|identifier
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|terms
operator|.
name|hasPositions
argument_list|()
condition|)
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|identifier
operator|.
name|bytes
argument_list|()
argument_list|)
condition|)
block|{
name|newDocsEnum
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|,
name|docsEnum
operator|instanceof
name|DocsAndPositionsEnum
condition|?
operator|(
name|DocsAndPositionsEnum
operator|)
name|docsEnum
else|:
literal|null
argument_list|,
name|luceneFlags
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|newDocsEnum
return|;
block|}
comment|// get the DocsEnum from the reader.
DECL|method|getOnlyDocsEnum
specifier|private
name|DocsEnum
name|getOnlyDocsEnum
parameter_list|(
name|int
name|luceneFlags
parameter_list|,
name|AtomicReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|identifier
operator|.
name|field
argument_list|()
operator|!=
literal|null
assert|;
assert|assert
name|identifier
operator|.
name|bytes
argument_list|()
operator|!=
literal|null
assert|;
specifier|final
name|Fields
name|fields
init|=
name|reader
operator|.
name|fields
argument_list|()
decl_stmt|;
name|DocsEnum
name|newDocsEnum
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|identifier
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|identifier
operator|.
name|bytes
argument_list|()
argument_list|)
condition|)
block|{
name|newDocsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|,
name|docsEnum
argument_list|,
name|luceneFlags
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|newDocsEnum
operator|==
literal|null
condition|)
block|{
name|newDocsEnum
operator|=
name|EMPTY_DOCS_ENUM
expr_stmt|;
block|}
return|return
name|newDocsEnum
return|;
block|}
DECL|field|freq
specifier|private
name|int
name|freq
init|=
literal|0
decl_stmt|;
DECL|method|setNextDoc
specifier|public
name|void
name|setNextDoc
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
assert|assert
operator|(
name|docsEnum
operator|!=
literal|null
operator|)
assert|;
try|try
block|{
comment|// we try to advance to the current document.
name|int
name|currentDocPos
init|=
name|docsEnum
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentDocPos
operator|<
name|docId
condition|)
block|{
name|currentDocPos
operator|=
name|docsEnum
operator|.
name|advance
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|currentDocPos
operator|==
name|docId
condition|)
block|{
name|freq
operator|=
name|docsEnum
operator|.
name|freq
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|freq
operator|=
literal|0
expr_stmt|;
block|}
name|iterator
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"While trying to initialize term positions in IndexFieldTerm.setNextDoc() "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|IndexFieldTerm
specifier|public
name|IndexFieldTerm
parameter_list|(
name|String
name|term
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|IndexLookup
name|indexLookup
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
assert|assert
name|fieldName
operator|!=
literal|null
assert|;
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
assert|assert
name|term
operator|!=
literal|null
assert|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
assert|assert
name|indexLookup
operator|!=
literal|null
assert|;
name|identifier
operator|=
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
operator|(
name|String
operator|)
name|term
argument_list|)
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
name|boolean
name|doRecord
init|=
operator|(
operator|(
name|flags
operator|&
name|IndexLookup
operator|.
name|FLAG_CACHE
operator|)
operator|>
literal|0
operator|)
decl_stmt|;
if|if
condition|(
name|withPositions
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|doRecord
condition|)
block|{
name|iterator
operator|=
operator|new
name|PositionIterator
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|iterator
operator|=
operator|new
name|CachedPositionIterator
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|iterator
operator|=
operator|new
name|PositionIterator
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|setNextReader
argument_list|(
name|indexLookup
operator|.
name|getReader
argument_list|()
argument_list|)
expr_stmt|;
name|setNextDoc
argument_list|(
name|indexLookup
operator|.
name|getDocId
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|termStats
operator|=
name|indexLookup
operator|.
name|getIndexSearcher
argument_list|()
operator|.
name|termStatistics
argument_list|(
name|identifier
argument_list|,
name|TermContext
operator|.
name|build
argument_list|(
name|indexLookup
operator|.
name|getReaderContext
argument_list|()
argument_list|,
name|identifier
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Cannot get term statistics: "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|withPositions
specifier|private
name|boolean
name|withPositions
parameter_list|()
block|{
return|return
name|shouldRetrievePositions
argument_list|()
operator|||
name|shouldRetrieveOffsets
argument_list|()
operator|||
name|shouldRetrievePayloads
argument_list|()
return|;
block|}
DECL|method|shouldRetrievePositions
specifier|protected
name|boolean
name|shouldRetrievePositions
parameter_list|()
block|{
return|return
operator|(
name|flags
operator|&
name|IndexLookup
operator|.
name|FLAG_POSITIONS
operator|)
operator|>
literal|0
return|;
block|}
DECL|method|shouldRetrieveOffsets
specifier|protected
name|boolean
name|shouldRetrieveOffsets
parameter_list|()
block|{
return|return
operator|(
name|flags
operator|&
name|IndexLookup
operator|.
name|FLAG_OFFSETS
operator|)
operator|>
literal|0
return|;
block|}
DECL|method|shouldRetrievePayloads
specifier|protected
name|boolean
name|shouldRetrievePayloads
parameter_list|()
block|{
return|return
operator|(
name|flags
operator|&
name|IndexLookup
operator|.
name|FLAG_PAYLOADS
operator|)
operator|>
literal|0
return|;
block|}
DECL|method|tf
specifier|public
name|int
name|tf
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|freq
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|TermPosition
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|reset
argument_list|()
return|;
block|}
comment|/*      * A user might decide inside a script to call get with _POSITIONS and then      * a second time with _PAYLOADS. If the positions were recorded but the      * payloads were not, the user will not have access to them. Therfore, throw      * exception here explaining how to call get().      */
DECL|method|validateFlags
specifier|public
name|void
name|validateFlags
parameter_list|(
name|int
name|flags2
parameter_list|)
block|{
if|if
condition|(
operator|(
name|this
operator|.
name|flags
operator|&
name|flags2
operator|)
operator|<
name|flags2
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"You must call get with all required flags! Instead of "
operator|+
name|getCalledStatement
argument_list|(
name|flags2
argument_list|)
operator|+
literal|"call "
operator|+
name|getCallStatement
argument_list|(
name|flags2
operator||
name|this
operator|.
name|flags
argument_list|)
operator|+
literal|" once"
argument_list|)
throw|;
block|}
block|}
DECL|method|getCalledStatement
specifier|private
name|String
name|getCalledStatement
parameter_list|(
name|int
name|flags2
parameter_list|)
block|{
name|String
name|calledFlagsCall1
init|=
name|getFlagsString
argument_list|(
name|flags
argument_list|)
decl_stmt|;
name|String
name|calledFlagsCall2
init|=
name|getFlagsString
argument_list|(
name|flags2
argument_list|)
decl_stmt|;
name|String
name|callStatement1
init|=
name|getCallStatement
argument_list|(
name|calledFlagsCall1
argument_list|)
decl_stmt|;
name|String
name|callStatement2
init|=
name|getCallStatement
argument_list|(
name|calledFlagsCall2
argument_list|)
decl_stmt|;
return|return
literal|" "
operator|+
name|callStatement1
operator|+
literal|" and "
operator|+
name|callStatement2
operator|+
literal|" "
return|;
block|}
DECL|method|getCallStatement
specifier|private
name|String
name|getCallStatement
parameter_list|(
name|String
name|calledFlags
parameter_list|)
block|{
return|return
literal|"_index['"
operator|+
name|this
operator|.
name|fieldName
operator|+
literal|"'].get('"
operator|+
name|this
operator|.
name|term
operator|+
literal|"', "
operator|+
name|calledFlags
operator|+
literal|")"
return|;
block|}
DECL|method|getFlagsString
specifier|private
name|String
name|getFlagsString
parameter_list|(
name|int
name|flags2
parameter_list|)
block|{
name|String
name|flagsString
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|(
name|flags2
operator|&
name|IndexLookup
operator|.
name|FLAG_FREQUENCIES
operator|)
operator|!=
literal|0
condition|)
block|{
name|flagsString
operator|=
name|anddToFlagsString
argument_list|(
name|flagsString
argument_list|,
literal|"_FREQUENCIES"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|flags2
operator|&
name|IndexLookup
operator|.
name|FLAG_POSITIONS
operator|)
operator|!=
literal|0
condition|)
block|{
name|flagsString
operator|=
name|anddToFlagsString
argument_list|(
name|flagsString
argument_list|,
literal|"_POSITIONS"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|flags2
operator|&
name|IndexLookup
operator|.
name|FLAG_OFFSETS
operator|)
operator|!=
literal|0
condition|)
block|{
name|flagsString
operator|=
name|anddToFlagsString
argument_list|(
name|flagsString
argument_list|,
literal|"_OFFSETS"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|flags2
operator|&
name|IndexLookup
operator|.
name|FLAG_PAYLOADS
operator|)
operator|!=
literal|0
condition|)
block|{
name|flagsString
operator|=
name|anddToFlagsString
argument_list|(
name|flagsString
argument_list|,
literal|"_PAYLOADS"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|flags2
operator|&
name|IndexLookup
operator|.
name|FLAG_CACHE
operator|)
operator|!=
literal|0
condition|)
block|{
name|flagsString
operator|=
name|anddToFlagsString
argument_list|(
name|flagsString
argument_list|,
literal|"_CACHE"
argument_list|)
expr_stmt|;
block|}
return|return
name|flagsString
return|;
block|}
DECL|method|anddToFlagsString
specifier|private
name|String
name|anddToFlagsString
parameter_list|(
name|String
name|flagsString
parameter_list|,
name|String
name|flag
parameter_list|)
block|{
if|if
condition|(
name|flagsString
operator|!=
literal|null
condition|)
block|{
name|flagsString
operator|+=
literal|" | "
expr_stmt|;
block|}
else|else
block|{
name|flagsString
operator|=
literal|""
expr_stmt|;
block|}
name|flagsString
operator|+=
name|flag
expr_stmt|;
return|return
name|flagsString
return|;
block|}
DECL|method|getCallStatement
specifier|private
name|String
name|getCallStatement
parameter_list|(
name|int
name|flags2
parameter_list|)
block|{
name|String
name|calledFlags
init|=
name|getFlagsString
argument_list|(
name|flags2
argument_list|)
decl_stmt|;
name|String
name|callStatement
init|=
name|getCallStatement
argument_list|(
name|calledFlags
argument_list|)
decl_stmt|;
return|return
literal|" "
operator|+
name|callStatement
operator|+
literal|" "
return|;
block|}
block|}
end_class

end_unit

