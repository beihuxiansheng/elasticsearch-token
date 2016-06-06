begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.snapshots
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|snapshots
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchCorruptionException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
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
name|ParseFieldMatcher
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
name|blobstore
operator|.
name|BlobContainer
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
name|blobstore
operator|.
name|BlobMetaData
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
name|blobstore
operator|.
name|BlobPath
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
name|blobstore
operator|.
name|BlobStore
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
name|blobstore
operator|.
name|fs
operator|.
name|FsBlobStore
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
name|bytes
operator|.
name|BytesReference
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
name|compress
operator|.
name|CompressorFactory
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
name|Streams
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
name|stream
operator|.
name|BytesStreamOutput
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
name|stream
operator|.
name|StreamOutput
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
name|common
operator|.
name|xcontent
operator|.
name|FromXContentBuilder
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
name|xcontent
operator|.
name|ToXContent
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentFactory
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
name|xcontent
operator|.
name|XContentParser
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
name|xcontent
operator|.
name|XContentType
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
name|translog
operator|.
name|BufferedChecksumStreamOutput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|blobstore
operator|.
name|ChecksumBlobStoreFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|blobstore
operator|.
name|LegacyBlobStoreFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|InputStream
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
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|greaterThan
import|;
end_import

begin_class
DECL|class|BlobStoreFormatIT
specifier|public
class|class
name|BlobStoreFormatIT
extends|extends
name|AbstractSnapshotIntegTestCase
block|{
DECL|field|parseFieldMatcher
specifier|private
specifier|static
specifier|final
name|ParseFieldMatcher
name|parseFieldMatcher
init|=
operator|new
name|ParseFieldMatcher
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
DECL|field|BLOB_CODEC
specifier|public
specifier|static
specifier|final
name|String
name|BLOB_CODEC
init|=
literal|"blob"
decl_stmt|;
DECL|class|BlobObj
specifier|private
specifier|static
class|class
name|BlobObj
implements|implements
name|ToXContent
implements|,
name|FromXContentBuilder
argument_list|<
name|BlobObj
argument_list|>
block|{
DECL|field|PROTO
specifier|public
specifier|static
specifier|final
name|BlobObj
name|PROTO
init|=
operator|new
name|BlobObj
argument_list|(
literal|""
argument_list|)
decl_stmt|;
DECL|field|text
specifier|private
specifier|final
name|String
name|text
decl_stmt|;
DECL|method|BlobObj
specifier|public
name|BlobObj
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
DECL|method|getText
specifier|public
name|String
name|getText
parameter_list|()
block|{
return|return
name|text
return|;
block|}
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
name|BlobObj
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|text
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|currentToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"unexpected token [{}]"
argument_list|,
name|token
argument_list|)
throw|;
block|}
name|String
name|currentFieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"text"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|text
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"unexpected field [{}]"
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"unexpected token [{}]"
argument_list|,
name|token
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|text
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"missing mandatory parameter text"
argument_list|)
throw|;
block|}
return|return
operator|new
name|BlobObj
argument_list|(
name|text
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|ToXContent
operator|.
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"text"
argument_list|,
name|getText
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
comment|/**      * Extends legacy format with writing functionality. It's used to simulate legacy file formats in tests.      */
DECL|class|LegacyEmulationBlobStoreFormat
specifier|private
specifier|static
specifier|final
class|class
name|LegacyEmulationBlobStoreFormat
parameter_list|<
name|T
extends|extends
name|ToXContent
parameter_list|>
extends|extends
name|LegacyBlobStoreFormat
argument_list|<
name|T
argument_list|>
block|{
DECL|field|xContentType
specifier|protected
specifier|final
name|XContentType
name|xContentType
decl_stmt|;
DECL|field|compress
specifier|protected
specifier|final
name|boolean
name|compress
decl_stmt|;
DECL|method|LegacyEmulationBlobStoreFormat
specifier|public
name|LegacyEmulationBlobStoreFormat
parameter_list|(
name|String
name|blobNameFormat
parameter_list|,
name|FromXContentBuilder
argument_list|<
name|T
argument_list|>
name|reader
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|,
name|boolean
name|compress
parameter_list|,
name|XContentType
name|xContentType
parameter_list|)
block|{
name|super
argument_list|(
name|blobNameFormat
argument_list|,
name|reader
argument_list|,
name|parseFieldMatcher
argument_list|)
expr_stmt|;
name|this
operator|.
name|xContentType
operator|=
name|xContentType
expr_stmt|;
name|this
operator|.
name|compress
operator|=
name|compress
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|T
name|obj
parameter_list|,
name|BlobContainer
name|blobContainer
parameter_list|,
name|String
name|blobName
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesReference
name|bytes
init|=
name|write
argument_list|(
name|obj
argument_list|)
decl_stmt|;
name|blobContainer
operator|.
name|writeBlob
argument_list|(
name|blobName
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|private
name|BytesReference
name|write
parameter_list|(
name|T
name|obj
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|BytesStreamOutput
name|bytesStreamOutput
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
if|if
condition|(
name|compress
condition|)
block|{
try|try
init|(
name|StreamOutput
name|compressedStreamOutput
init|=
name|CompressorFactory
operator|.
name|defaultCompressor
argument_list|()
operator|.
name|streamOutput
argument_list|(
name|bytesStreamOutput
argument_list|)
init|)
block|{
name|write
argument_list|(
name|obj
argument_list|,
name|compressedStreamOutput
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|write
argument_list|(
name|obj
argument_list|,
name|bytesStreamOutput
argument_list|)
expr_stmt|;
block|}
return|return
name|bytesStreamOutput
operator|.
name|bytes
argument_list|()
return|;
block|}
block|}
DECL|method|write
specifier|private
name|void
name|write
parameter_list|(
name|T
name|obj
parameter_list|,
name|StreamOutput
name|streamOutput
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|xContentType
argument_list|,
name|streamOutput
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|obj
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|SNAPSHOT_ONLY_FORMAT_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testBlobStoreOperations
specifier|public
name|void
name|testBlobStoreOperations
parameter_list|()
throws|throws
name|IOException
block|{
name|BlobStore
name|blobStore
init|=
name|createTestBlobStore
argument_list|()
decl_stmt|;
name|BlobContainer
name|blobContainer
init|=
name|blobStore
operator|.
name|blobContainer
argument_list|(
name|BlobPath
operator|.
name|cleanPath
argument_list|()
argument_list|)
decl_stmt|;
name|ChecksumBlobStoreFormat
argument_list|<
name|BlobObj
argument_list|>
name|checksumJSON
init|=
operator|new
name|ChecksumBlobStoreFormat
argument_list|<>
argument_list|(
name|BLOB_CODEC
argument_list|,
literal|"%s"
argument_list|,
name|BlobObj
operator|.
name|PROTO
argument_list|,
name|parseFieldMatcher
argument_list|,
literal|false
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|ChecksumBlobStoreFormat
argument_list|<
name|BlobObj
argument_list|>
name|checksumSMILE
init|=
operator|new
name|ChecksumBlobStoreFormat
argument_list|<>
argument_list|(
name|BLOB_CODEC
argument_list|,
literal|"%s"
argument_list|,
name|BlobObj
operator|.
name|PROTO
argument_list|,
name|parseFieldMatcher
argument_list|,
literal|false
argument_list|,
name|XContentType
operator|.
name|SMILE
argument_list|)
decl_stmt|;
name|ChecksumBlobStoreFormat
argument_list|<
name|BlobObj
argument_list|>
name|checksumSMILECompressed
init|=
operator|new
name|ChecksumBlobStoreFormat
argument_list|<>
argument_list|(
name|BLOB_CODEC
argument_list|,
literal|"%s"
argument_list|,
name|BlobObj
operator|.
name|PROTO
argument_list|,
name|parseFieldMatcher
argument_list|,
literal|true
argument_list|,
name|XContentType
operator|.
name|SMILE
argument_list|)
decl_stmt|;
name|LegacyEmulationBlobStoreFormat
argument_list|<
name|BlobObj
argument_list|>
name|legacyJSON
init|=
operator|new
name|LegacyEmulationBlobStoreFormat
argument_list|<>
argument_list|(
literal|"%s"
argument_list|,
name|BlobObj
operator|.
name|PROTO
argument_list|,
name|parseFieldMatcher
argument_list|,
literal|false
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|LegacyEmulationBlobStoreFormat
argument_list|<
name|BlobObj
argument_list|>
name|legacySMILE
init|=
operator|new
name|LegacyEmulationBlobStoreFormat
argument_list|<>
argument_list|(
literal|"%s"
argument_list|,
name|BlobObj
operator|.
name|PROTO
argument_list|,
name|parseFieldMatcher
argument_list|,
literal|false
argument_list|,
name|XContentType
operator|.
name|SMILE
argument_list|)
decl_stmt|;
name|LegacyEmulationBlobStoreFormat
argument_list|<
name|BlobObj
argument_list|>
name|legacySMILECompressed
init|=
operator|new
name|LegacyEmulationBlobStoreFormat
argument_list|<>
argument_list|(
literal|"%s"
argument_list|,
name|BlobObj
operator|.
name|PROTO
argument_list|,
name|parseFieldMatcher
argument_list|,
literal|true
argument_list|,
name|XContentType
operator|.
name|SMILE
argument_list|)
decl_stmt|;
comment|// Write blobs in different formats
name|checksumJSON
operator|.
name|write
argument_list|(
operator|new
name|BlobObj
argument_list|(
literal|"checksum json"
argument_list|)
argument_list|,
name|blobContainer
argument_list|,
literal|"check-json"
argument_list|)
expr_stmt|;
name|checksumSMILE
operator|.
name|write
argument_list|(
operator|new
name|BlobObj
argument_list|(
literal|"checksum smile"
argument_list|)
argument_list|,
name|blobContainer
argument_list|,
literal|"check-smile"
argument_list|)
expr_stmt|;
name|checksumSMILECompressed
operator|.
name|write
argument_list|(
operator|new
name|BlobObj
argument_list|(
literal|"checksum smile compressed"
argument_list|)
argument_list|,
name|blobContainer
argument_list|,
literal|"check-smile-comp"
argument_list|)
expr_stmt|;
name|legacyJSON
operator|.
name|write
argument_list|(
operator|new
name|BlobObj
argument_list|(
literal|"legacy json"
argument_list|)
argument_list|,
name|blobContainer
argument_list|,
literal|"legacy-json"
argument_list|)
expr_stmt|;
name|legacySMILE
operator|.
name|write
argument_list|(
operator|new
name|BlobObj
argument_list|(
literal|"legacy smile"
argument_list|)
argument_list|,
name|blobContainer
argument_list|,
literal|"legacy-smile"
argument_list|)
expr_stmt|;
name|legacySMILECompressed
operator|.
name|write
argument_list|(
operator|new
name|BlobObj
argument_list|(
literal|"legacy smile compressed"
argument_list|)
argument_list|,
name|blobContainer
argument_list|,
literal|"legacy-smile-comp"
argument_list|)
expr_stmt|;
comment|// Assert that all checksum blobs can be read by all formats
name|assertEquals
argument_list|(
name|checksumJSON
operator|.
name|read
argument_list|(
name|blobContainer
argument_list|,
literal|"check-json"
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|,
literal|"checksum json"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|checksumSMILE
operator|.
name|read
argument_list|(
name|blobContainer
argument_list|,
literal|"check-json"
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|,
literal|"checksum json"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|checksumJSON
operator|.
name|read
argument_list|(
name|blobContainer
argument_list|,
literal|"check-smile"
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|,
literal|"checksum smile"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|checksumSMILE
operator|.
name|read
argument_list|(
name|blobContainer
argument_list|,
literal|"check-smile"
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|,
literal|"checksum smile"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|checksumJSON
operator|.
name|read
argument_list|(
name|blobContainer
argument_list|,
literal|"check-smile-comp"
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|,
literal|"checksum smile compressed"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|checksumSMILE
operator|.
name|read
argument_list|(
name|blobContainer
argument_list|,
literal|"check-smile-comp"
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|,
literal|"checksum smile compressed"
argument_list|)
expr_stmt|;
comment|// Assert that all legacy blobs can be read be all formats
name|assertEquals
argument_list|(
name|legacyJSON
operator|.
name|read
argument_list|(
name|blobContainer
argument_list|,
literal|"legacy-json"
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|,
literal|"legacy json"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|legacySMILE
operator|.
name|read
argument_list|(
name|blobContainer
argument_list|,
literal|"legacy-json"
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|,
literal|"legacy json"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|legacyJSON
operator|.
name|read
argument_list|(
name|blobContainer
argument_list|,
literal|"legacy-smile"
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|,
literal|"legacy smile"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|legacySMILE
operator|.
name|read
argument_list|(
name|blobContainer
argument_list|,
literal|"legacy-smile"
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|,
literal|"legacy smile"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|legacyJSON
operator|.
name|read
argument_list|(
name|blobContainer
argument_list|,
literal|"legacy-smile-comp"
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|,
literal|"legacy smile compressed"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|legacySMILE
operator|.
name|read
argument_list|(
name|blobContainer
argument_list|,
literal|"legacy-smile-comp"
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|,
literal|"legacy smile compressed"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCompressionIsApplied
specifier|public
name|void
name|testCompressionIsApplied
parameter_list|()
throws|throws
name|IOException
block|{
name|BlobStore
name|blobStore
init|=
name|createTestBlobStore
argument_list|()
decl_stmt|;
name|BlobContainer
name|blobContainer
init|=
name|blobStore
operator|.
name|blobContainer
argument_list|(
name|BlobPath
operator|.
name|cleanPath
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuilder
name|veryRedundantText
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|randomIntBetween
argument_list|(
literal|100
argument_list|,
literal|300
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|veryRedundantText
operator|.
name|append
argument_list|(
literal|"Blah "
argument_list|)
expr_stmt|;
block|}
name|ChecksumBlobStoreFormat
argument_list|<
name|BlobObj
argument_list|>
name|checksumFormat
init|=
operator|new
name|ChecksumBlobStoreFormat
argument_list|<>
argument_list|(
name|BLOB_CODEC
argument_list|,
literal|"%s"
argument_list|,
name|BlobObj
operator|.
name|PROTO
argument_list|,
name|parseFieldMatcher
argument_list|,
literal|false
argument_list|,
name|randomBoolean
argument_list|()
condition|?
name|XContentType
operator|.
name|SMILE
else|:
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|ChecksumBlobStoreFormat
argument_list|<
name|BlobObj
argument_list|>
name|checksumFormatComp
init|=
operator|new
name|ChecksumBlobStoreFormat
argument_list|<>
argument_list|(
name|BLOB_CODEC
argument_list|,
literal|"%s"
argument_list|,
name|BlobObj
operator|.
name|PROTO
argument_list|,
name|parseFieldMatcher
argument_list|,
literal|true
argument_list|,
name|randomBoolean
argument_list|()
condition|?
name|XContentType
operator|.
name|SMILE
else|:
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|BlobObj
name|blobObj
init|=
operator|new
name|BlobObj
argument_list|(
name|veryRedundantText
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|checksumFormatComp
operator|.
name|write
argument_list|(
name|blobObj
argument_list|,
name|blobContainer
argument_list|,
literal|"blob-comp"
argument_list|)
expr_stmt|;
name|checksumFormat
operator|.
name|write
argument_list|(
name|blobObj
argument_list|,
name|blobContainer
argument_list|,
literal|"blob-not-comp"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|BlobMetaData
argument_list|>
name|blobs
init|=
name|blobContainer
operator|.
name|listBlobsByPrefix
argument_list|(
literal|"blob-"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|blobs
operator|.
name|size
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|blobs
operator|.
name|get
argument_list|(
literal|"blob-not-comp"
argument_list|)
operator|.
name|length
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
name|blobs
operator|.
name|get
argument_list|(
literal|"blob-comp"
argument_list|)
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBlobCorruption
specifier|public
name|void
name|testBlobCorruption
parameter_list|()
throws|throws
name|IOException
block|{
name|BlobStore
name|blobStore
init|=
name|createTestBlobStore
argument_list|()
decl_stmt|;
name|BlobContainer
name|blobContainer
init|=
name|blobStore
operator|.
name|blobContainer
argument_list|(
name|BlobPath
operator|.
name|cleanPath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|testString
init|=
name|randomAsciiOfLength
argument_list|(
name|randomInt
argument_list|(
literal|10000
argument_list|)
argument_list|)
decl_stmt|;
name|BlobObj
name|blobObj
init|=
operator|new
name|BlobObj
argument_list|(
name|testString
argument_list|)
decl_stmt|;
name|ChecksumBlobStoreFormat
argument_list|<
name|BlobObj
argument_list|>
name|checksumFormat
init|=
operator|new
name|ChecksumBlobStoreFormat
argument_list|<>
argument_list|(
name|BLOB_CODEC
argument_list|,
literal|"%s"
argument_list|,
name|BlobObj
operator|.
name|PROTO
argument_list|,
name|parseFieldMatcher
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|randomBoolean
argument_list|()
condition|?
name|XContentType
operator|.
name|SMILE
else|:
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|checksumFormat
operator|.
name|write
argument_list|(
name|blobObj
argument_list|,
name|blobContainer
argument_list|,
literal|"test-path"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|checksumFormat
operator|.
name|read
argument_list|(
name|blobContainer
argument_list|,
literal|"test-path"
argument_list|)
operator|.
name|getText
argument_list|()
argument_list|,
name|testString
argument_list|)
expr_stmt|;
name|randomCorruption
argument_list|(
name|blobContainer
argument_list|,
literal|"test-path"
argument_list|)
expr_stmt|;
try|try
block|{
name|checksumFormat
operator|.
name|read
argument_list|(
name|blobContainer
argument_list|,
literal|"test-path"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have failed due to corruption"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchCorruptionException
name|ex
parameter_list|)
block|{
name|assertThat
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"test-path"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|ex
parameter_list|)
block|{
comment|// This can happen if corrupt the byte length
block|}
block|}
DECL|method|testAtomicWrite
specifier|public
name|void
name|testAtomicWrite
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|BlobStore
name|blobStore
init|=
name|createTestBlobStore
argument_list|()
decl_stmt|;
specifier|final
name|BlobContainer
name|blobContainer
init|=
name|blobStore
operator|.
name|blobContainer
argument_list|(
name|BlobPath
operator|.
name|cleanPath
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|testString
init|=
name|randomAsciiOfLength
argument_list|(
name|randomInt
argument_list|(
literal|10000
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|block
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|unblock
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|BlobObj
name|blobObj
init|=
operator|new
name|BlobObj
argument_list|(
name|testString
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|ToXContent
operator|.
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
comment|// Block before finishing writing
try|try
block|{
name|block
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|unblock
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|ChecksumBlobStoreFormat
argument_list|<
name|BlobObj
argument_list|>
name|checksumFormat
init|=
operator|new
name|ChecksumBlobStoreFormat
argument_list|<>
argument_list|(
name|BLOB_CODEC
argument_list|,
literal|"%s"
argument_list|,
name|BlobObj
operator|.
name|PROTO
argument_list|,
name|parseFieldMatcher
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|randomBoolean
argument_list|()
condition|?
name|XContentType
operator|.
name|SMILE
else|:
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|ExecutorService
name|threadPool
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
name|Future
argument_list|<
name|Void
argument_list|>
name|future
init|=
name|threadPool
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|checksumFormat
operator|.
name|writeAtomic
argument_list|(
name|blobObj
argument_list|,
name|blobContainer
argument_list|,
literal|"test-blob"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|block
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|blobContainer
operator|.
name|blobExists
argument_list|(
literal|"test-blob"
argument_list|)
argument_list|)
expr_stmt|;
name|unblock
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|blobContainer
operator|.
name|blobExists
argument_list|(
literal|"test-blob"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|threadPool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createTestBlobStore
specifier|protected
name|BlobStore
name|createTestBlobStore
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
operator|new
name|FsBlobStore
argument_list|(
name|settings
argument_list|,
name|randomRepoPath
argument_list|()
argument_list|)
return|;
block|}
DECL|method|randomCorruption
specifier|protected
name|void
name|randomCorruption
parameter_list|(
name|BlobContainer
name|blobContainer
parameter_list|,
name|String
name|blobName
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|blobContainer
operator|.
name|listBlobsByPrefix
argument_list|(
name|blobName
argument_list|)
operator|.
name|get
argument_list|(
name|blobName
argument_list|)
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|long
name|originalChecksum
init|=
name|checksum
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|inputStream
init|=
name|blobContainer
operator|.
name|readBlob
argument_list|(
name|blobName
argument_list|)
init|)
block|{
name|Streams
operator|.
name|readFully
argument_list|(
name|inputStream
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
do|do
block|{
name|int
name|location
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|buffer
operator|.
name|length
operator|-
literal|1
argument_list|)
decl_stmt|;
name|buffer
index|[
name|location
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|buffer
index|[
name|location
index|]
operator|^
literal|42
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|originalChecksum
operator|==
name|checksum
argument_list|(
name|buffer
argument_list|)
condition|)
do|;
name|blobContainer
operator|.
name|deleteBlob
argument_list|(
name|blobName
argument_list|)
expr_stmt|;
comment|// delete original before writing new blob
name|blobContainer
operator|.
name|writeBlob
argument_list|(
name|blobName
argument_list|,
operator|new
name|BytesArray
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checksum
specifier|private
name|long
name|checksum
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|BytesStreamOutput
name|streamOutput
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
try|try
init|(
name|BufferedChecksumStreamOutput
name|checksumOutput
init|=
operator|new
name|BufferedChecksumStreamOutput
argument_list|(
name|streamOutput
argument_list|)
init|)
block|{
name|checksumOutput
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
return|return
name|checksumOutput
operator|.
name|getChecksum
argument_list|()
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

