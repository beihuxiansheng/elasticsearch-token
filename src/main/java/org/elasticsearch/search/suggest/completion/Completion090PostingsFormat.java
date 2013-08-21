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
name|ImmutableMap
import|;
end_import

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
name|ImmutableMap
operator|.
name|Builder
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
name|codecs
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
name|index
operator|.
name|FilterAtomicReader
operator|.
name|FilterTerms
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
name|store
operator|.
name|IOContext
operator|.
name|Context
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
name|store
operator|.
name|IndexInput
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
name|store
operator|.
name|IndexOutput
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
name|store
operator|.
name|InputStreamDataInput
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
name|store
operator|.
name|OutputStreamDataOutput
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
name|IOUtils
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
name|common
operator|.
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|suggest
operator|.
name|completion
operator|.
name|CompletionTokenStream
operator|.
name|ToFiniteStrings
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|Comparator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * This {@link PostingsFormat} is basically a T-Sink for a default postings  * format that is used to store postings on disk fitting the lucene APIs and  * builds a suggest FST as an auxiliary data structure next to the actual  * postings format. It uses the delegate postings format for simplicity to  * handle all the merge operations. The auxiliary suggest FST data structure is  * only loaded if a FieldsProducer is requested for reading, for merging it uses  * the low memory delegate postings format.  */
end_comment

begin_class
DECL|class|Completion090PostingsFormat
specifier|public
class|class
name|Completion090PostingsFormat
extends|extends
name|PostingsFormat
block|{
DECL|field|CODEC_NAME
specifier|public
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"completion090"
decl_stmt|;
DECL|field|SUGGEST_CODEC_VERSION
specifier|public
specifier|static
specifier|final
name|int
name|SUGGEST_CODEC_VERSION
init|=
literal|1
decl_stmt|;
DECL|field|EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|EXTENSION
init|=
literal|"cmp"
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
specifier|static
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Completion090PostingsFormat
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|delegatePostingsFormat
specifier|private
name|PostingsFormat
name|delegatePostingsFormat
decl_stmt|;
DECL|field|providers
specifier|private
specifier|final
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|CompletionLookupProvider
argument_list|>
name|providers
decl_stmt|;
DECL|field|writeProvider
specifier|private
name|CompletionLookupProvider
name|writeProvider
decl_stmt|;
static|static
block|{
specifier|final
name|CompletionLookupProvider
name|provider
init|=
operator|new
name|AnalyzingCompletionLookupProvider
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|Builder
argument_list|<
name|String
argument_list|,
name|CompletionLookupProvider
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|providers
operator|=
name|builder
operator|.
name|put
argument_list|(
name|provider
operator|.
name|getName
argument_list|()
argument_list|,
name|provider
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|Completion090PostingsFormat
specifier|public
name|Completion090PostingsFormat
parameter_list|(
name|PostingsFormat
name|delegatePostingsFormat
parameter_list|,
name|CompletionLookupProvider
name|provider
parameter_list|)
block|{
name|super
argument_list|(
name|CODEC_NAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegatePostingsFormat
operator|=
name|delegatePostingsFormat
expr_stmt|;
name|this
operator|.
name|writeProvider
operator|=
name|provider
expr_stmt|;
assert|assert
name|delegatePostingsFormat
operator|!=
literal|null
operator|&&
name|writeProvider
operator|!=
literal|null
assert|;
block|}
comment|/*      * Used only by core Lucene at read-time via Service Provider instantiation      * do not use at Write-time in application code.      */
DECL|method|Completion090PostingsFormat
specifier|public
name|Completion090PostingsFormat
parameter_list|()
block|{
name|super
argument_list|(
name|CODEC_NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|CompletionFieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|delegatePostingsFormat
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Error - "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" has been constructed without a choice of PostingsFormat"
argument_list|)
throw|;
block|}
assert|assert
name|writeProvider
operator|!=
literal|null
assert|;
return|return
operator|new
name|CompletionFieldsConsumer
argument_list|(
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|CompletionFieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CompletionFieldsProducer
argument_list|(
name|state
argument_list|)
return|;
block|}
DECL|class|CompletionFieldsConsumer
specifier|private
class|class
name|CompletionFieldsConsumer
extends|extends
name|FieldsConsumer
block|{
DECL|field|delegatesFieldsConsumer
specifier|private
name|FieldsConsumer
name|delegatesFieldsConsumer
decl_stmt|;
DECL|field|suggestFieldsConsumer
specifier|private
name|FieldsConsumer
name|suggestFieldsConsumer
decl_stmt|;
DECL|method|CompletionFieldsConsumer
specifier|public
name|CompletionFieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|delegatesFieldsConsumer
operator|=
name|delegatePostingsFormat
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|String
name|suggestFSTFile
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|EXTENSION
argument_list|)
decl_stmt|;
name|IndexOutput
name|output
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|output
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|suggestFSTFile
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
name|CODEC_NAME
argument_list|,
name|SUGGEST_CODEC_VERSION
argument_list|)
expr_stmt|;
comment|/*                  * we write the delegate postings format name so we can load it                  * without getting an instance in the ctor                  */
name|output
operator|.
name|writeString
argument_list|(
name|delegatePostingsFormat
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeString
argument_list|(
name|writeProvider
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|suggestFieldsConsumer
operator|=
name|writeProvider
operator|.
name|consumer
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|TermsConsumer
name|addField
parameter_list|(
specifier|final
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|TermsConsumer
name|delegateConsumer
init|=
name|delegatesFieldsConsumer
operator|.
name|addField
argument_list|(
name|field
argument_list|)
decl_stmt|;
specifier|final
name|TermsConsumer
name|suggestTermConsumer
init|=
name|suggestFieldsConsumer
operator|.
name|addField
argument_list|(
name|field
argument_list|)
decl_stmt|;
specifier|final
name|GroupedPostingsConsumer
name|groupedPostingsConsumer
init|=
operator|new
name|GroupedPostingsConsumer
argument_list|(
name|delegateConsumer
argument_list|,
name|suggestTermConsumer
argument_list|)
decl_stmt|;
return|return
operator|new
name|TermsConsumer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PostingsConsumer
name|startTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|groupedPostingsConsumer
operator|.
name|startTerm
argument_list|(
name|text
argument_list|)
expr_stmt|;
return|return
name|groupedPostingsConsumer
return|;
block|}
annotation|@
name|Override
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegateConsumer
operator|.
name|getComparator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|finishTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|TermStats
name|stats
parameter_list|)
throws|throws
name|IOException
block|{
name|suggestTermConsumer
operator|.
name|finishTerm
argument_list|(
name|text
argument_list|,
name|stats
argument_list|)
expr_stmt|;
name|delegateConsumer
operator|.
name|finishTerm
argument_list|(
name|text
argument_list|,
name|stats
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|finish
parameter_list|(
name|long
name|sumTotalTermFreq
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
name|suggestTermConsumer
operator|.
name|finish
argument_list|(
name|sumTotalTermFreq
argument_list|,
name|sumDocFreq
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
name|delegateConsumer
operator|.
name|finish
argument_list|(
name|sumTotalTermFreq
argument_list|,
name|sumDocFreq
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|delegatesFieldsConsumer
argument_list|,
name|suggestFieldsConsumer
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|GroupedPostingsConsumer
specifier|private
class|class
name|GroupedPostingsConsumer
extends|extends
name|PostingsConsumer
block|{
DECL|field|termsConsumers
specifier|private
name|TermsConsumer
index|[]
name|termsConsumers
decl_stmt|;
DECL|field|postingsConsumers
specifier|private
name|PostingsConsumer
index|[]
name|postingsConsumers
decl_stmt|;
DECL|method|GroupedPostingsConsumer
specifier|public
name|GroupedPostingsConsumer
parameter_list|(
name|TermsConsumer
modifier|...
name|termsConsumersArgs
parameter_list|)
block|{
name|termsConsumers
operator|=
name|termsConsumersArgs
expr_stmt|;
name|postingsConsumers
operator|=
operator|new
name|PostingsConsumer
index|[
name|termsConsumersArgs
operator|.
name|length
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startDoc
specifier|public
name|void
name|startDoc
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|freq
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|PostingsConsumer
name|postingsConsumer
range|:
name|postingsConsumers
control|)
block|{
name|postingsConsumer
operator|.
name|startDoc
argument_list|(
name|docID
argument_list|,
name|freq
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addPosition
specifier|public
name|void
name|addPosition
parameter_list|(
name|int
name|position
parameter_list|,
name|BytesRef
name|payload
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|PostingsConsumer
name|postingsConsumer
range|:
name|postingsConsumers
control|)
block|{
name|postingsConsumer
operator|.
name|addPosition
argument_list|(
name|position
argument_list|,
name|payload
argument_list|,
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finishDoc
specifier|public
name|void
name|finishDoc
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|PostingsConsumer
name|postingsConsumer
range|:
name|postingsConsumers
control|)
block|{
name|postingsConsumer
operator|.
name|finishDoc
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|startTerm
specifier|public
name|void
name|startTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|termsConsumers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|postingsConsumers
index|[
name|i
index|]
operator|=
name|termsConsumers
index|[
name|i
index|]
operator|.
name|startTerm
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|CompletionFieldsProducer
specifier|private
class|class
name|CompletionFieldsProducer
extends|extends
name|FieldsProducer
block|{
DECL|field|delegateProducer
specifier|private
name|FieldsProducer
name|delegateProducer
decl_stmt|;
DECL|field|lookupFactory
specifier|private
name|LookupFactory
name|lookupFactory
decl_stmt|;
DECL|method|CompletionFieldsProducer
specifier|public
name|CompletionFieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|suggestFSTFile
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|EXTENSION
argument_list|)
decl_stmt|;
name|IndexInput
name|input
init|=
name|state
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|suggestFSTFile
argument_list|,
name|state
operator|.
name|context
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|input
argument_list|,
name|CODEC_NAME
argument_list|,
name|SUGGEST_CODEC_VERSION
argument_list|,
name|SUGGEST_CODEC_VERSION
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|PostingsFormat
name|delegatePostingsFormat
init|=
name|PostingsFormat
operator|.
name|forName
argument_list|(
name|input
operator|.
name|readString
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|providerName
init|=
name|input
operator|.
name|readString
argument_list|()
decl_stmt|;
name|CompletionLookupProvider
name|completionLookupProvider
init|=
name|providers
operator|.
name|get
argument_list|(
name|providerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|completionLookupProvider
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"no provider with name ["
operator|+
name|providerName
operator|+
literal|"] registered"
argument_list|)
throw|;
block|}
comment|// TODO: we could clone the ReadState and make it always forward IOContext.MERGE to prevent unecessary heap usage?
name|this
operator|.
name|delegateProducer
operator|=
name|delegatePostingsFormat
operator|.
name|fieldsProducer
argument_list|(
name|state
argument_list|)
expr_stmt|;
comment|/*                  * If we are merging we don't load the FSTs at all such that we                  * don't consume so much memory during merge                  */
if|if
condition|(
name|state
operator|.
name|context
operator|.
name|context
operator|!=
name|Context
operator|.
name|MERGE
condition|)
block|{
comment|// TODO: maybe we can do this in a fully lazy fashion based on some configuration
comment|// eventually we should have some kind of curciut breaker that prevents us from going OOM here
comment|// with some configuration
name|this
operator|.
name|lookupFactory
operator|=
name|completionLookupProvider
operator|.
name|load
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|delegateProducer
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|delegateProducer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|delegateProducer
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|Terms
name|terms
init|=
name|delegateProducer
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
name|terms
return|;
block|}
return|return
operator|new
name|CompletionTerms
argument_list|(
name|terms
argument_list|,
name|this
operator|.
name|lookupFactory
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|delegateProducer
operator|.
name|size
argument_list|()
return|;
block|}
block|}
DECL|class|CompletionTerms
specifier|public
specifier|static
specifier|final
class|class
name|CompletionTerms
extends|extends
name|FilterTerms
block|{
DECL|field|lookup
specifier|private
specifier|final
name|LookupFactory
name|lookup
decl_stmt|;
DECL|method|CompletionTerms
specifier|public
name|CompletionTerms
parameter_list|(
name|Terms
name|delegate
parameter_list|,
name|LookupFactory
name|lookup
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
name|this
operator|.
name|lookup
operator|=
name|lookup
expr_stmt|;
block|}
DECL|method|getLookup
specifier|public
name|Lookup
name|getLookup
parameter_list|(
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|mapper
parameter_list|,
name|CompletionSuggestionContext
name|suggestionContext
parameter_list|)
block|{
return|return
name|lookup
operator|.
name|getLookup
argument_list|(
name|mapper
argument_list|,
name|suggestionContext
argument_list|)
return|;
block|}
DECL|method|stats
specifier|public
name|CompletionStats
name|stats
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
return|return
name|lookup
operator|.
name|stats
argument_list|(
name|fields
argument_list|)
return|;
block|}
block|}
DECL|class|CompletionLookupProvider
specifier|public
specifier|static
specifier|abstract
class|class
name|CompletionLookupProvider
implements|implements
name|PayloadProcessor
implements|,
name|ToFiniteStrings
block|{
DECL|field|UNIT_SEPARATOR
specifier|public
specifier|static
specifier|final
name|char
name|UNIT_SEPARATOR
init|=
literal|'\u001f'
decl_stmt|;
DECL|method|consumer
specifier|public
specifier|abstract
name|FieldsConsumer
name|consumer
parameter_list|(
name|IndexOutput
name|output
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getName
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|load
specifier|public
specifier|abstract
name|LookupFactory
name|load
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|buildPayload
specifier|public
name|BytesRef
name|buildPayload
parameter_list|(
name|BytesRef
name|surfaceForm
parameter_list|,
name|long
name|weight
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|weight
argument_list|<
operator|-
literal|1
operator|||
name|weight
argument_list|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"weight must be>= -1&&<= Integer.MAX_VALUE"
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|surfaceForm
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|surfaceForm
operator|.
name|bytes
index|[
name|i
index|]
operator|==
name|UNIT_SEPARATOR
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"surface form cannot contain unit separator character U+001F; this character is reserved"
argument_list|)
throw|;
block|}
block|}
name|ByteArrayOutputStream
name|byteArrayOutputStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|OutputStreamDataOutput
name|output
init|=
operator|new
name|OutputStreamDataOutput
argument_list|(
name|byteArrayOutputStream
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeVLong
argument_list|(
name|weight
operator|+
literal|1
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeVInt
argument_list|(
name|surfaceForm
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|surfaceForm
operator|.
name|bytes
argument_list|,
name|surfaceForm
operator|.
name|offset
argument_list|,
name|surfaceForm
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeVInt
argument_list|(
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|BytesRef
argument_list|(
name|byteArrayOutputStream
operator|.
name|toByteArray
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parsePayload
specifier|public
name|void
name|parsePayload
parameter_list|(
name|BytesRef
name|payload
parameter_list|,
name|SuggestPayload
name|ref
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayInputStream
name|byteArrayInputStream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
name|payload
operator|.
name|offset
argument_list|,
name|payload
operator|.
name|length
argument_list|)
decl_stmt|;
name|InputStreamDataInput
name|input
init|=
operator|new
name|InputStreamDataInput
argument_list|(
name|byteArrayInputStream
argument_list|)
decl_stmt|;
name|ref
operator|.
name|weight
operator|=
name|input
operator|.
name|readVLong
argument_list|()
operator|-
literal|1
expr_stmt|;
name|int
name|len
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|ref
operator|.
name|surfaceForm
operator|.
name|grow
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|ref
operator|.
name|surfaceForm
operator|.
name|length
operator|=
name|len
expr_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|ref
operator|.
name|surfaceForm
operator|.
name|bytes
argument_list|,
name|ref
operator|.
name|surfaceForm
operator|.
name|offset
argument_list|,
name|ref
operator|.
name|surfaceForm
operator|.
name|length
argument_list|)
expr_stmt|;
name|len
operator|=
name|input
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|ref
operator|.
name|payload
operator|.
name|grow
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|ref
operator|.
name|payload
operator|.
name|length
operator|=
name|len
expr_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|ref
operator|.
name|payload
operator|.
name|bytes
argument_list|,
name|ref
operator|.
name|payload
operator|.
name|offset
argument_list|,
name|ref
operator|.
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|completionStats
specifier|public
name|CompletionStats
name|completionStats
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|,
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|CompletionStats
name|completionStats
init|=
operator|new
name|CompletionStats
argument_list|()
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
try|try
block|{
for|for
control|(
name|String
name|fieldName
range|:
name|atomicReader
operator|.
name|fields
argument_list|()
control|)
block|{
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
name|CompletionTerms
condition|)
block|{
name|CompletionTerms
name|completionTerms
init|=
operator|(
name|CompletionTerms
operator|)
name|terms
decl_stmt|;
name|completionStats
operator|.
name|add
argument_list|(
name|completionTerms
operator|.
name|stats
argument_list|(
name|fields
argument_list|)
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
name|logger
operator|.
name|error
argument_list|(
literal|"Could not get completion stats: {}"
argument_list|,
name|e
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|completionStats
return|;
block|}
DECL|class|LookupFactory
specifier|public
specifier|static
specifier|abstract
class|class
name|LookupFactory
block|{
DECL|method|getLookup
specifier|public
specifier|abstract
name|Lookup
name|getLookup
parameter_list|(
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|mapper
parameter_list|,
name|CompletionSuggestionContext
name|suggestionContext
parameter_list|)
function_decl|;
DECL|method|stats
specifier|public
specifier|abstract
name|CompletionStats
name|stats
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

