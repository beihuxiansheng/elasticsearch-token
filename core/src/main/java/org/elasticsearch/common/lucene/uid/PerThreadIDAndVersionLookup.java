begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

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
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Fields
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
name|LeafReaderContext
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
name|NumericDocValues
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
name|PostingsEnum
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
name|index
operator|.
name|TermsEnum
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
name|DocIdSetIterator
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
name|common
operator|.
name|lucene
operator|.
name|uid
operator|.
name|Versions
operator|.
name|DocIdAndVersion
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

begin_comment
comment|/** Utility class to do efficient primary-key (only 1 doc contains the  *  given term) lookups by segment, re-using the enums.  This class is  *  not thread safe, so it is the caller's job to create and use one  *  instance of this per thread.  Do not use this if a term may appear  *  in more than one document!  It will only return the first one it  *  finds. */
end_comment

begin_class
DECL|class|PerThreadIDAndVersionLookup
specifier|final
class|class
name|PerThreadIDAndVersionLookup
block|{
DECL|field|readerContexts
specifier|private
specifier|final
name|LeafReaderContext
index|[]
name|readerContexts
decl_stmt|;
DECL|field|termsEnums
specifier|private
specifier|final
name|TermsEnum
index|[]
name|termsEnums
decl_stmt|;
DECL|field|docsEnums
specifier|private
specifier|final
name|PostingsEnum
index|[]
name|docsEnums
decl_stmt|;
comment|// Only used for back compat, to lookup a version from payload:
DECL|field|posEnums
specifier|private
specifier|final
name|PostingsEnum
index|[]
name|posEnums
decl_stmt|;
DECL|field|liveDocs
specifier|private
specifier|final
name|Bits
index|[]
name|liveDocs
decl_stmt|;
DECL|field|versions
specifier|private
specifier|final
name|NumericDocValues
index|[]
name|versions
decl_stmt|;
DECL|field|numSegs
specifier|private
specifier|final
name|int
name|numSegs
decl_stmt|;
DECL|field|hasDeletions
specifier|private
specifier|final
name|boolean
name|hasDeletions
decl_stmt|;
DECL|field|hasPayloads
specifier|private
specifier|final
name|boolean
index|[]
name|hasPayloads
decl_stmt|;
DECL|method|PerThreadIDAndVersionLookup
specifier|public
name|PerThreadIDAndVersionLookup
parameter_list|(
name|IndexReader
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|r
operator|.
name|leaves
argument_list|()
argument_list|)
decl_stmt|;
name|readerContexts
operator|=
name|leaves
operator|.
name|toArray
argument_list|(
operator|new
name|LeafReaderContext
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|termsEnums
operator|=
operator|new
name|TermsEnum
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|docsEnums
operator|=
operator|new
name|PostingsEnum
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|posEnums
operator|=
operator|new
name|PostingsEnum
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|liveDocs
operator|=
operator|new
name|Bits
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|versions
operator|=
operator|new
name|NumericDocValues
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|hasPayloads
operator|=
operator|new
name|boolean
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|int
name|numSegs
init|=
literal|0
decl_stmt|;
name|boolean
name|hasDeletions
init|=
literal|false
decl_stmt|;
comment|// iterate backwards to optimize for the frequently updated documents
comment|// which are likely to be in the last segments
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
name|i
operator|--
control|)
block|{
name|LeafReaderContext
name|readerContext
init|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Fields
name|fields
init|=
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|readerContexts
index|[
name|numSegs
index|]
operator|=
name|readerContext
expr_stmt|;
name|hasPayloads
index|[
name|numSegs
index|]
operator|=
name|terms
operator|.
name|hasPayloads
argument_list|()
expr_stmt|;
name|termsEnums
index|[
name|numSegs
index|]
operator|=
name|terms
operator|.
name|iterator
argument_list|()
expr_stmt|;
assert|assert
name|termsEnums
index|[
name|numSegs
index|]
operator|!=
literal|null
assert|;
name|liveDocs
index|[
name|numSegs
index|]
operator|=
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
name|hasDeletions
operator||=
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|hasDeletions
argument_list|()
expr_stmt|;
name|versions
index|[
name|numSegs
index|]
operator|=
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|getNumericDocValues
argument_list|(
name|VersionFieldMapper
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|numSegs
operator|++
expr_stmt|;
block|}
block|}
block|}
name|this
operator|.
name|numSegs
operator|=
name|numSegs
expr_stmt|;
name|this
operator|.
name|hasDeletions
operator|=
name|hasDeletions
expr_stmt|;
block|}
comment|/** Return null if id is not found. */
DECL|method|lookup
specifier|public
name|DocIdAndVersion
name|lookup
parameter_list|(
name|BytesRef
name|id
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|seg
init|=
literal|0
init|;
name|seg
operator|<
name|numSegs
condition|;
name|seg
operator|++
control|)
block|{
if|if
condition|(
name|termsEnums
index|[
name|seg
index|]
operator|.
name|seekExact
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|NumericDocValues
name|segVersions
init|=
name|versions
index|[
name|seg
index|]
decl_stmt|;
if|if
condition|(
name|segVersions
operator|!=
literal|null
operator|||
name|hasPayloads
index|[
name|seg
index|]
operator|==
literal|false
condition|)
block|{
comment|// Use NDV to retrieve the version, in which case we only need PostingsEnum:
comment|// there may be more than one matching docID, in the case of nested docs, so we want the last one:
name|PostingsEnum
name|docs
init|=
name|docsEnums
index|[
name|seg
index|]
operator|=
name|termsEnums
index|[
name|seg
index|]
operator|.
name|postings
argument_list|(
name|docsEnums
index|[
name|seg
index|]
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Bits
name|liveDocs
init|=
name|this
operator|.
name|liveDocs
index|[
name|seg
index|]
decl_stmt|;
name|int
name|docID
init|=
name|DocIdSetIterator
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
name|DocIdSetIterator
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
if|if
condition|(
name|liveDocs
operator|!=
literal|null
operator|&&
name|liveDocs
operator|.
name|get
argument_list|(
name|d
argument_list|)
operator|==
literal|false
condition|)
block|{
continue|continue;
block|}
name|docID
operator|=
name|d
expr_stmt|;
block|}
if|if
condition|(
name|docID
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|segVersions
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
name|segVersions
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|,
name|readerContexts
index|[
name|seg
index|]
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
name|Versions
operator|.
name|NOT_SET
argument_list|,
name|readerContexts
index|[
name|seg
index|]
argument_list|)
return|;
block|}
block|}
else|else
block|{
assert|assert
name|hasDeletions
assert|;
continue|continue;
block|}
block|}
comment|// ... but used to be stored as payloads; in this case we must use PostingsEnum
name|PostingsEnum
name|dpe
init|=
name|posEnums
index|[
name|seg
index|]
operator|=
name|termsEnums
index|[
name|seg
index|]
operator|.
name|postings
argument_list|(
name|posEnums
index|[
name|seg
index|]
argument_list|,
name|PostingsEnum
operator|.
name|PAYLOADS
argument_list|)
decl_stmt|;
assert|assert
name|dpe
operator|!=
literal|null
assert|;
comment|// terms has payloads
specifier|final
name|Bits
name|liveDocs
init|=
name|this
operator|.
name|liveDocs
index|[
name|seg
index|]
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
name|DocIdSetIterator
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
if|if
condition|(
name|liveDocs
operator|!=
literal|null
operator|&&
name|liveDocs
operator|.
name|get
argument_list|(
name|d
argument_list|)
operator|==
literal|false
condition|)
block|{
continue|continue;
block|}
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
comment|// TODO: does this break the nested docs case?  we are not returning the last matching docID here?
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
name|readerContexts
index|[
name|seg
index|]
argument_list|)
return|;
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|// TODO: add reopen method to carry over re-used enums...?
block|}
end_class

end_unit

