begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.uid
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|uid
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
name|Bits
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
name|elasticsearch
operator|.
name|common
operator|.
name|Numbers
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
name|internal
operator|.
name|UidFieldMapper
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
name|internal
operator|.
name|VersionFieldMapper
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
name|List
import|;
end_import

begin_comment
comment|/** Utility class to resolve the Lucene doc ID and version for a given uid. */
end_comment

begin_class
DECL|class|Versions
specifier|public
class|class
name|Versions
block|{
DECL|field|MATCH_ANY
specifier|public
specifier|static
specifier|final
name|long
name|MATCH_ANY
init|=
literal|0L
decl_stmt|;
comment|// Version was not specified by the user
DECL|field|NOT_FOUND
specifier|public
specifier|static
specifier|final
name|long
name|NOT_FOUND
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|NOT_SET
specifier|public
specifier|static
specifier|final
name|long
name|NOT_SET
init|=
operator|-
literal|2L
decl_stmt|;
DECL|method|Versions
specifier|private
name|Versions
parameter_list|()
block|{}
comment|/** Wraps an {@link AtomicReaderContext}, a doc ID<b>relative to the context doc base</b> and a version. */
DECL|class|DocIdAndVersion
specifier|public
specifier|static
class|class
name|DocIdAndVersion
block|{
DECL|field|docId
specifier|public
specifier|final
name|int
name|docId
decl_stmt|;
DECL|field|version
specifier|public
specifier|final
name|long
name|version
decl_stmt|;
DECL|field|context
specifier|public
specifier|final
name|AtomicReaderContext
name|context
decl_stmt|;
DECL|method|DocIdAndVersion
specifier|public
name|DocIdAndVersion
parameter_list|(
name|int
name|docId
parameter_list|,
name|long
name|version
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|docId
operator|=
name|docId
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
block|}
comment|/**      * Load the internal doc ID and version for the uid from the reader, returning<ul>      *<li>null if the uid wasn't found,      *<li>a doc ID and a version otherwise, the version being potentially set to {@link #NOT_SET} if the uid has no associated version      *</ul>      */
DECL|method|loadDocIdAndVersion
specifier|public
specifier|static
name|DocIdAndVersion
name|loadDocIdAndVersion
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
comment|// iterate backwards to optimize for the frequently updated documents
comment|// which are likely to be in the last segments
specifier|final
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|leaves
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
operator|--
name|i
control|)
block|{
specifier|final
name|DocIdAndVersion
name|docIdAndVersion
init|=
name|loadDocIdAndVersion
argument_list|(
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|docIdAndVersion
operator|!=
literal|null
condition|)
block|{
assert|assert
name|docIdAndVersion
operator|.
name|version
operator|!=
name|NOT_FOUND
assert|;
return|return
name|docIdAndVersion
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Load the version for the uid from the reader, returning<ul>      *<li>{@link #NOT_FOUND} if no matching doc exists,      *<li>{@link #NOT_SET} if no version is available,      *<li>the version associated with the provided uid otherwise      *</ul>      */
DECL|method|loadVersion
specifier|public
specifier|static
name|long
name|loadVersion
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocIdAndVersion
name|docIdAndVersion
init|=
name|loadDocIdAndVersion
argument_list|(
name|reader
argument_list|,
name|term
argument_list|)
decl_stmt|;
return|return
name|docIdAndVersion
operator|==
literal|null
condition|?
name|NOT_FOUND
else|:
name|docIdAndVersion
operator|.
name|version
return|;
block|}
comment|/** Same as {@link #loadDocIdAndVersion(IndexReader, Term)} but operates directly on a reader context. */
DECL|method|loadDocIdAndVersion
specifier|public
specifier|static
name|DocIdAndVersion
name|loadDocIdAndVersion
parameter_list|(
name|AtomicReaderContext
name|readerContext
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|)
assert|;
specifier|final
name|AtomicReader
name|reader
init|=
name|readerContext
operator|.
name|reader
argument_list|()
decl_stmt|;
specifier|final
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
specifier|final
name|Terms
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|)
decl_stmt|;
assert|assert
name|terms
operator|!=
literal|null
operator|:
literal|"All segments must have a _uid field, but "
operator|+
name|reader
operator|+
literal|" doesn't"
assert|;
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
operator|!
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Versions are stored as doc values...
specifier|final
name|NumericDocValues
name|versions
init|=
name|reader
operator|.
name|getNumericDocValues
argument_list|(
name|VersionFieldMapper
operator|.
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|versions
operator|!=
literal|null
operator|||
operator|!
name|terms
operator|.
name|hasPayloads
argument_list|()
condition|)
block|{
comment|// only the last doc that matches the _uid is interesting here: if it is deleted, then there is
comment|// no match otherwise previous docs are necessarily either deleted or nested docs
specifier|final
name|DocsEnum
name|docs
init|=
name|termsEnum
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|int
name|docID
init|=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
decl_stmt|;
for|for
control|(
name|int
name|d
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
init|;
name|d
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|;
name|d
operator|=
name|docs
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|docID
operator|=
name|d
expr_stmt|;
block|}
assert|assert
name|docID
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
assert|;
comment|// would mean that the term exists but has no match at all
if|if
condition|(
name|liveDocs
operator|!=
literal|null
operator|&&
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|versions
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|DocIdAndVersion
argument_list|(
name|docID
argument_list|,
name|versions
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|,
name|readerContext
argument_list|)
return|;
block|}
else|else
block|{
comment|// _uid found, but no doc values and no payloads
return|return
operator|new
name|DocIdAndVersion
argument_list|(
name|docID
argument_list|,
name|NOT_SET
argument_list|,
name|readerContext
argument_list|)
return|;
block|}
block|}
comment|// ... but used to be stored as payloads
specifier|final
name|DocsAndPositionsEnum
name|dpe
init|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
name|liveDocs
argument_list|,
literal|null
argument_list|,
name|DocsAndPositionsEnum
operator|.
name|FLAG_PAYLOADS
argument_list|)
decl_stmt|;
assert|assert
name|dpe
operator|!=
literal|null
assert|;
comment|// terms has payloads
name|int
name|docID
init|=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
decl_stmt|;
for|for
control|(
name|int
name|d
init|=
name|dpe
operator|.
name|nextDoc
argument_list|()
init|;
name|d
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|;
name|d
operator|=
name|dpe
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|docID
operator|=
name|d
expr_stmt|;
name|dpe
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
specifier|final
name|BytesRef
name|payload
init|=
name|dpe
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
operator|&&
name|payload
operator|.
name|length
operator|==
literal|8
condition|)
block|{
return|return
operator|new
name|DocIdAndVersion
argument_list|(
name|d
argument_list|,
name|Numbers
operator|.
name|bytesToLong
argument_list|(
name|payload
argument_list|)
argument_list|,
name|readerContext
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|docID
operator|==
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|DocIdAndVersion
argument_list|(
name|docID
argument_list|,
name|NOT_SET
argument_list|,
name|readerContext
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

