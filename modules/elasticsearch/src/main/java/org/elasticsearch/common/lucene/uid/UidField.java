begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|analysis
operator|.
name|TokenFilter
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
name|PayloadAttribute
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
name|Payload
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
name|index
operator|.
name|TermPositions
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
name|Lucene
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|UidField
specifier|public
class|class
name|UidField
extends|extends
name|AbstractField
block|{
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
DECL|field|reader
specifier|public
specifier|final
name|IndexReader
name|reader
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
name|IndexReader
name|reader
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
name|reader
operator|=
name|reader
expr_stmt|;
block|}
block|}
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
block|{
name|int
name|docId
init|=
name|Lucene
operator|.
name|NO_DOC
decl_stmt|;
name|TermPositions
name|uid
init|=
literal|null
decl_stmt|;
try|try
block|{
name|uid
operator|=
name|reader
operator|.
name|termPositions
argument_list|(
name|term
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|uid
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
operator|new
name|DocIdAndVersion
argument_list|(
name|Lucene
operator|.
name|NO_DOC
argument_list|,
operator|-
literal|1
argument_list|,
name|reader
argument_list|)
return|;
block|}
name|docId
operator|=
name|uid
operator|.
name|doc
argument_list|()
expr_stmt|;
name|uid
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|uid
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
return|return
operator|new
name|DocIdAndVersion
argument_list|(
name|docId
argument_list|,
operator|-
literal|2
argument_list|,
name|reader
argument_list|)
return|;
block|}
if|if
condition|(
name|uid
operator|.
name|getPayloadLength
argument_list|()
operator|<
literal|8
condition|)
block|{
return|return
operator|new
name|DocIdAndVersion
argument_list|(
name|docId
argument_list|,
operator|-
literal|2
argument_list|,
name|reader
argument_list|)
return|;
block|}
name|byte
index|[]
name|payload
init|=
name|uid
operator|.
name|getPayload
argument_list|(
operator|new
name|byte
index|[
literal|8
index|]
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocIdAndVersion
argument_list|(
name|docId
argument_list|,
name|Numbers
operator|.
name|bytesToLong
argument_list|(
name|payload
argument_list|)
argument_list|,
name|reader
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
operator|new
name|DocIdAndVersion
argument_list|(
name|docId
argument_list|,
operator|-
literal|2
argument_list|,
name|reader
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|uid
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|uid
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
comment|// nothing to do here...
block|}
block|}
block|}
block|}
comment|/**      * Load the version for the uid from the reader, returning -1 if no doc exists, or -2 if      * no version is available (for backward comp.)      */
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
block|{
name|TermPositions
name|uid
init|=
literal|null
decl_stmt|;
try|try
block|{
name|uid
operator|=
name|reader
operator|.
name|termPositions
argument_list|(
name|term
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|uid
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|uid
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|uid
operator|.
name|isPayloadAvailable
argument_list|()
condition|)
block|{
return|return
operator|-
literal|2
return|;
block|}
if|if
condition|(
name|uid
operator|.
name|getPayloadLength
argument_list|()
operator|<
literal|8
condition|)
block|{
return|return
operator|-
literal|2
return|;
block|}
name|byte
index|[]
name|payload
init|=
name|uid
operator|.
name|getPayload
argument_list|(
operator|new
name|byte
index|[
literal|8
index|]
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
name|Numbers
operator|.
name|bytesToLong
argument_list|(
name|payload
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
operator|-
literal|2
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|uid
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|uid
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
comment|// nothing to do here...
block|}
block|}
block|}
block|}
DECL|field|uid
specifier|private
specifier|final
name|String
name|uid
decl_stmt|;
DECL|field|version
specifier|private
name|long
name|version
decl_stmt|;
DECL|method|UidField
specifier|public
name|UidField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|uid
parameter_list|,
name|long
name|version
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
expr_stmt|;
name|this
operator|.
name|uid
operator|=
name|uid
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|omitTermFreqAndPositions
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|setOmitTermFreqAndPositions
annotation|@
name|Override
specifier|public
name|void
name|setOmitTermFreqAndPositions
parameter_list|(
name|boolean
name|omitTermFreqAndPositions
parameter_list|)
block|{
comment|// never allow to set this, since we want payload!
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
name|uid
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
DECL|method|version
specifier|public
name|long
name|version
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
DECL|method|version
specifier|public
name|void
name|version
parameter_list|(
name|long
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
DECL|method|tokenStreamValue
annotation|@
name|Override
specifier|public
name|TokenStream
name|tokenStreamValue
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|UidPayloadTokenStream
argument_list|(
name|Lucene
operator|.
name|KEYWORD_ANALYZER
operator|.
name|reusableTokenStream
argument_list|(
literal|"_uid"
argument_list|,
operator|new
name|FastStringReader
argument_list|(
name|uid
argument_list|)
argument_list|)
argument_list|,
name|this
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"failed to create token stream"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|class|UidPayloadTokenStream
specifier|public
specifier|static
class|class
name|UidPayloadTokenStream
extends|extends
name|TokenFilter
block|{
DECL|field|payloadAttribute
specifier|private
specifier|final
name|PayloadAttribute
name|payloadAttribute
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|UidField
name|field
decl_stmt|;
DECL|method|UidPayloadTokenStream
specifier|public
name|UidPayloadTokenStream
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|UidField
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|payloadAttribute
operator|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
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
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|payloadAttribute
operator|.
name|setPayload
argument_list|(
operator|new
name|Payload
argument_list|(
name|Numbers
operator|.
name|longToBytes
argument_list|(
name|field
operator|.
name|version
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

