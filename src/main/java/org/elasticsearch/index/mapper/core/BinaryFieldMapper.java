begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.core
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|core
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
name|document
operator|.
name|FieldType
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
name|ElasticSearchIllegalArgumentException
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
name|Base64
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
name|stream
operator|.
name|CachedStreamOutput
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
name|unit
operator|.
name|ByteSizeValue
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
name|XContentParser
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
name|codec
operator|.
name|postingsformat
operator|.
name|PostingsFormatProvider
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
name|*
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
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|support
operator|.
name|XContentMapValues
operator|.
name|nodeBooleanValue
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
name|mapper
operator|.
name|MapperBuilders
operator|.
name|binaryField
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
name|mapper
operator|.
name|core
operator|.
name|TypeParsers
operator|.
name|parseField
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|BinaryFieldMapper
specifier|public
class|class
name|BinaryFieldMapper
extends|extends
name|AbstractFieldMapper
argument_list|<
name|BytesReference
argument_list|>
block|{
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"binary"
decl_stmt|;
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
extends|extends
name|AbstractFieldMapper
operator|.
name|Defaults
block|{
DECL|field|COMPRESS_THRESHOLD
specifier|public
specifier|static
specifier|final
name|long
name|COMPRESS_THRESHOLD
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|FieldType
name|FIELD_TYPE
init|=
operator|new
name|FieldType
argument_list|(
name|AbstractFieldMapper
operator|.
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|)
decl_stmt|;
static|static
block|{
name|FIELD_TYPE
operator|.
name|setIndexed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|AbstractFieldMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|BinaryFieldMapper
argument_list|>
block|{
DECL|field|compress
specifier|private
name|Boolean
name|compress
init|=
literal|null
decl_stmt|;
DECL|field|compressThreshold
specifier|private
name|long
name|compressThreshold
init|=
name|Defaults
operator|.
name|COMPRESS_THRESHOLD
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
operator|new
name|FieldType
argument_list|(
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|=
name|this
expr_stmt|;
block|}
DECL|method|compress
specifier|public
name|Builder
name|compress
parameter_list|(
name|boolean
name|compress
parameter_list|)
block|{
name|this
operator|.
name|compress
operator|=
name|compress
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|compressThreshold
specifier|public
name|Builder
name|compressThreshold
parameter_list|(
name|long
name|compressThreshold
parameter_list|)
block|{
name|this
operator|.
name|compressThreshold
operator|=
name|compressThreshold
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|indexName
specifier|public
name|Builder
name|indexName
parameter_list|(
name|String
name|indexName
parameter_list|)
block|{
return|return
name|super
operator|.
name|indexName
argument_list|(
name|indexName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|BinaryFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|BinaryFieldMapper
argument_list|(
name|buildNames
argument_list|(
name|context
argument_list|)
argument_list|,
name|fieldType
argument_list|,
name|compress
argument_list|,
name|compressThreshold
argument_list|,
name|provider
argument_list|)
return|;
block|}
block|}
DECL|class|TypeParser
specifier|public
specifier|static
class|class
name|TypeParser
implements|implements
name|Mapper
operator|.
name|TypeParser
block|{
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Mapper
operator|.
name|Builder
name|parse
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|node
parameter_list|,
name|ParserContext
name|parserContext
parameter_list|)
throws|throws
name|MapperParsingException
block|{
name|BinaryFieldMapper
operator|.
name|Builder
name|builder
init|=
name|binaryField
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|parseField
argument_list|(
name|builder
argument_list|,
name|name
argument_list|,
name|node
argument_list|,
name|parserContext
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|node
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|fieldName
init|=
name|Strings
operator|.
name|toUnderscoreCase
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|fieldNode
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"compress"
argument_list|)
operator|&&
name|fieldNode
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|compress
argument_list|(
name|nodeBooleanValue
argument_list|(
name|fieldNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"compress_threshold"
argument_list|)
operator|&&
name|fieldNode
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fieldNode
operator|instanceof
name|Number
condition|)
block|{
name|builder
operator|.
name|compressThreshold
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|fieldNode
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|compress
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|compressThreshold
argument_list|(
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|compress
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|builder
return|;
block|}
block|}
DECL|field|compress
specifier|private
name|Boolean
name|compress
decl_stmt|;
DECL|field|compressThreshold
specifier|private
name|long
name|compressThreshold
decl_stmt|;
DECL|method|BinaryFieldMapper
specifier|protected
name|BinaryFieldMapper
parameter_list|(
name|Names
name|names
parameter_list|,
name|FieldType
name|fieldType
parameter_list|,
name|Boolean
name|compress
parameter_list|,
name|long
name|compressThreshold
parameter_list|,
name|PostingsFormatProvider
name|provider
parameter_list|)
block|{
name|super
argument_list|(
name|names
argument_list|,
literal|1.0f
argument_list|,
name|fieldType
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|provider
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|compress
operator|=
name|compress
expr_stmt|;
name|this
operator|.
name|compressThreshold
operator|=
name|compressThreshold
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|defaultFieldType
specifier|public
name|FieldType
name|defaultFieldType
parameter_list|()
block|{
return|return
name|Defaults
operator|.
name|FIELD_TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|fieldDataType2
specifier|public
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|FieldDataType
name|fieldDataType2
parameter_list|()
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"field data on binary field is not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|valueForSearch
specifier|public
name|Object
name|valueForSearch
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|value
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|BytesReference
name|value
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|BytesReference
name|bytes
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|BytesRef
condition|)
block|{
name|bytes
operator|=
operator|new
name|BytesArray
argument_list|(
operator|(
name|BytesRef
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|BytesReference
condition|)
block|{
name|bytes
operator|=
operator|(
name|BytesReference
operator|)
name|value
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|byte
index|[]
condition|)
block|{
name|bytes
operator|=
operator|new
name|BytesArray
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|bytes
operator|=
operator|new
name|BytesArray
argument_list|(
name|Base64
operator|.
name|decode
argument_list|(
name|value
operator|.
name|toString
argument_list|()
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
name|ElasticSearchParseException
argument_list|(
literal|"failed to convert bytes"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
try|try
block|{
return|return
name|CompressorFactory
operator|.
name|uncompressIfNeeded
argument_list|(
name|bytes
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
name|ElasticSearchParseException
argument_list|(
literal|"failed to decompress source"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|parseCreateField
specifier|protected
name|Field
name|parseCreateField
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|byte
index|[]
name|value
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|parser
argument_list|()
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NULL
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|value
operator|=
name|context
operator|.
name|parser
argument_list|()
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|compress
operator|!=
literal|null
operator|&&
name|compress
operator|&&
operator|!
name|CompressorFactory
operator|.
name|isCompressed
argument_list|(
name|value
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|)
condition|)
block|{
if|if
condition|(
name|compressThreshold
operator|==
operator|-
literal|1
operator|||
name|value
operator|.
name|length
operator|>
name|compressThreshold
condition|)
block|{
name|CachedStreamOutput
operator|.
name|Entry
name|cachedEntry
init|=
name|CachedStreamOutput
operator|.
name|popEntry
argument_list|()
decl_stmt|;
name|StreamOutput
name|streamOutput
init|=
name|cachedEntry
operator|.
name|bytes
argument_list|(
name|CompressorFactory
operator|.
name|defaultCompressor
argument_list|()
argument_list|)
decl_stmt|;
name|streamOutput
operator|.
name|writeBytes
argument_list|(
name|value
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
name|streamOutput
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// we copy over the byte array, since we need to push back the cached entry
comment|// TODO, we we had a handle into when we are done with parsing, then we push back then and not copy over bytes
name|value
operator|=
name|cachedEntry
operator|.
name|bytes
argument_list|()
operator|.
name|bytes
argument_list|()
operator|.
name|copyBytesArray
argument_list|()
operator|.
name|toBytes
argument_list|()
expr_stmt|;
name|CachedStreamOutput
operator|.
name|pushEntry
argument_list|(
name|cachedEntry
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|Field
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|value
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|contentType
specifier|protected
name|String
name|contentType
parameter_list|()
block|{
return|return
name|CONTENT_TYPE
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
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|names
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|contentType
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|names
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|names
operator|.
name|indexNameClean
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"index_name"
argument_list|,
name|names
operator|.
name|indexNameClean
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|compress
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"compress"
argument_list|,
name|compress
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|compressThreshold
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"compress_threshold"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|compressThreshold
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldType
operator|.
name|stored
argument_list|()
operator|!=
name|defaultFieldType
argument_list|()
operator|.
name|stored
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
name|fieldType
operator|.
name|stored
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|Mapper
name|mergeWith
parameter_list|,
name|MergeContext
name|mergeContext
parameter_list|)
throws|throws
name|MergeMappingException
block|{
name|BinaryFieldMapper
name|sourceMergeWith
init|=
operator|(
name|BinaryFieldMapper
operator|)
name|mergeWith
decl_stmt|;
if|if
condition|(
operator|!
name|mergeContext
operator|.
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|()
condition|)
block|{
if|if
condition|(
name|sourceMergeWith
operator|.
name|compress
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|compress
operator|=
name|sourceMergeWith
operator|.
name|compress
expr_stmt|;
block|}
if|if
condition|(
name|sourceMergeWith
operator|.
name|compressThreshold
operator|!=
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|compressThreshold
operator|=
name|sourceMergeWith
operator|.
name|compressThreshold
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

