begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|internal
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
name|base
operator|.
name|Objects
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
name|Document
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
name|document
operator|.
name|Fieldable
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
name|FieldInfo
operator|.
name|IndexOptions
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
name|collect
operator|.
name|Tuple
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
name|CompressedStreamInput
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
name|Compressor
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
name|lucene
operator|.
name|Lucene
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
name|document
operator|.
name|ResetFieldSelector
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
name|XContentHelper
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
name|common
operator|.
name|xcontent
operator|.
name|support
operator|.
name|XContentMapValues
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
name|AbstractFieldMapper
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
name|common
operator|.
name|xcontent
operator|.
name|support
operator|.
name|XContentMapValues
operator|.
name|nodeStringValue
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
name|source
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SourceFieldMapper
specifier|public
class|class
name|SourceFieldMapper
extends|extends
name|AbstractFieldMapper
argument_list|<
name|byte
index|[]
argument_list|>
implements|implements
name|InternalMapper
implements|,
name|RootMapper
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"_source"
decl_stmt|;
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"_source"
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
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|SourceFieldMapper
operator|.
name|NAME
decl_stmt|;
DECL|field|ENABLED
specifier|public
specifier|static
specifier|final
name|boolean
name|ENABLED
init|=
literal|true
decl_stmt|;
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
DECL|field|FORMAT
specifier|public
specifier|static
specifier|final
name|String
name|FORMAT
init|=
literal|null
decl_stmt|;
comment|// default format is to use the one provided
DECL|field|INDEX
specifier|public
specifier|static
specifier|final
name|Field
operator|.
name|Index
name|INDEX
init|=
name|Field
operator|.
name|Index
operator|.
name|NO
decl_stmt|;
DECL|field|STORE
specifier|public
specifier|static
specifier|final
name|Field
operator|.
name|Store
name|STORE
init|=
name|Field
operator|.
name|Store
operator|.
name|YES
decl_stmt|;
DECL|field|OMIT_NORMS
specifier|public
specifier|static
specifier|final
name|boolean
name|OMIT_NORMS
init|=
literal|true
decl_stmt|;
DECL|field|INDEX_OPTIONS
specifier|public
specifier|static
specifier|final
name|IndexOptions
name|INDEX_OPTIONS
init|=
name|IndexOptions
operator|.
name|DOCS_ONLY
decl_stmt|;
DECL|field|INCLUDES
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|INCLUDES
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|EXCLUDES
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|EXCLUDES
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|Mapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|SourceFieldMapper
argument_list|>
block|{
DECL|field|enabled
specifier|private
name|boolean
name|enabled
init|=
name|Defaults
operator|.
name|ENABLED
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
DECL|field|compress
specifier|private
name|Boolean
name|compress
init|=
literal|null
decl_stmt|;
DECL|field|format
specifier|private
name|String
name|format
init|=
name|Defaults
operator|.
name|FORMAT
decl_stmt|;
DECL|field|includes
specifier|private
name|String
index|[]
name|includes
init|=
name|Defaults
operator|.
name|INCLUDES
decl_stmt|;
DECL|field|excludes
specifier|private
name|String
index|[]
name|excludes
init|=
name|Defaults
operator|.
name|EXCLUDES
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|()
block|{
name|super
argument_list|(
name|Defaults
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
DECL|method|enabled
specifier|public
name|Builder
name|enabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
return|return
name|this
return|;
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
DECL|method|format
specifier|public
name|Builder
name|format
parameter_list|(
name|String
name|format
parameter_list|)
block|{
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|includes
specifier|public
name|Builder
name|includes
parameter_list|(
name|String
index|[]
name|includes
parameter_list|)
block|{
name|this
operator|.
name|includes
operator|=
name|includes
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|excludes
specifier|public
name|Builder
name|excludes
parameter_list|(
name|String
index|[]
name|excludes
parameter_list|)
block|{
name|this
operator|.
name|excludes
operator|=
name|excludes
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|SourceFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|SourceFieldMapper
argument_list|(
name|name
argument_list|,
name|enabled
argument_list|,
name|format
argument_list|,
name|compress
argument_list|,
name|compressThreshold
argument_list|,
name|includes
argument_list|,
name|excludes
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
name|SourceFieldMapper
operator|.
name|Builder
name|builder
init|=
name|source
argument_list|()
decl_stmt|;
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
literal|"enabled"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|enabled
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
elseif|else
if|if
condition|(
literal|"format"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|format
argument_list|(
name|nodeStringValue
argument_list|(
name|fieldNode
argument_list|,
literal|null
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
literal|"includes"
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|values
init|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|fieldNode
decl_stmt|;
name|String
index|[]
name|includes
init|=
operator|new
name|String
index|[
name|values
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
name|includes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|includes
index|[
name|i
index|]
operator|=
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|includes
argument_list|(
name|includes
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
literal|"excludes"
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|values
init|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|fieldNode
decl_stmt|;
name|String
index|[]
name|excludes
init|=
operator|new
name|String
index|[
name|values
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
name|excludes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|excludes
index|[
name|i
index|]
operator|=
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|excludes
argument_list|(
name|excludes
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
return|;
block|}
block|}
DECL|field|enabled
specifier|private
specifier|final
name|boolean
name|enabled
decl_stmt|;
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
DECL|field|includes
specifier|private
name|String
index|[]
name|includes
decl_stmt|;
DECL|field|excludes
specifier|private
name|String
index|[]
name|excludes
decl_stmt|;
DECL|field|format
specifier|private
name|String
name|format
decl_stmt|;
DECL|field|formatContentType
specifier|private
name|XContentType
name|formatContentType
decl_stmt|;
DECL|method|SourceFieldMapper
specifier|public
name|SourceFieldMapper
parameter_list|()
block|{
name|this
argument_list|(
name|Defaults
operator|.
name|NAME
argument_list|,
name|Defaults
operator|.
name|ENABLED
argument_list|,
name|Defaults
operator|.
name|FORMAT
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
name|Defaults
operator|.
name|INCLUDES
argument_list|,
name|Defaults
operator|.
name|EXCLUDES
argument_list|)
expr_stmt|;
block|}
DECL|method|SourceFieldMapper
specifier|protected
name|SourceFieldMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|enabled
parameter_list|,
name|String
name|format
parameter_list|,
name|Boolean
name|compress
parameter_list|,
name|long
name|compressThreshold
parameter_list|,
name|String
index|[]
name|includes
parameter_list|,
name|String
index|[]
name|excludes
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|Names
argument_list|(
name|name
argument_list|,
name|name
argument_list|,
name|name
argument_list|,
name|name
argument_list|)
argument_list|,
name|Defaults
operator|.
name|INDEX
argument_list|,
name|Defaults
operator|.
name|STORE
argument_list|,
name|Defaults
operator|.
name|TERM_VECTOR
argument_list|,
name|Defaults
operator|.
name|BOOST
argument_list|,
name|Defaults
operator|.
name|OMIT_NORMS
argument_list|,
name|Defaults
operator|.
name|INDEX_OPTIONS
argument_list|,
name|Lucene
operator|.
name|KEYWORD_ANALYZER
argument_list|,
name|Lucene
operator|.
name|KEYWORD_ANALYZER
argument_list|)
expr_stmt|;
name|this
operator|.
name|enabled
operator|=
name|enabled
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
name|this
operator|.
name|includes
operator|=
name|includes
expr_stmt|;
name|this
operator|.
name|excludes
operator|=
name|excludes
expr_stmt|;
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
name|this
operator|.
name|formatContentType
operator|=
name|format
operator|==
literal|null
condition|?
literal|null
else|:
name|XContentType
operator|.
name|fromRestContentType
argument_list|(
name|format
argument_list|)
expr_stmt|;
block|}
DECL|method|enabled
specifier|public
name|boolean
name|enabled
parameter_list|()
block|{
return|return
name|this
operator|.
name|enabled
return|;
block|}
DECL|method|fieldSelector
specifier|public
name|ResetFieldSelector
name|fieldSelector
parameter_list|()
block|{
return|return
name|SourceFieldSelector
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Override
DECL|method|preParse
specifier|public
name|void
name|preParse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postParse
specifier|public
name|void
name|postParse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|parse
specifier|public
name|void
name|parse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nothing to do here, we will call it in pre parse
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|void
name|validate
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|MapperParsingException
block|{     }
annotation|@
name|Override
DECL|method|includeInObject
specifier|public
name|boolean
name|includeInObject
parameter_list|()
block|{
return|return
literal|false
return|;
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
name|enabled
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|store
operator|==
name|Field
operator|.
name|Store
operator|.
name|NO
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|context
operator|.
name|flyweight
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|BytesReference
name|source
init|=
name|context
operator|.
name|source
argument_list|()
decl_stmt|;
name|boolean
name|filtered
init|=
name|includes
operator|.
name|length
operator|>
literal|0
operator|||
name|excludes
operator|.
name|length
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|filtered
condition|)
block|{
comment|// we don't update the context source if we filter, we want to keep it as is...
name|Tuple
argument_list|<
name|XContentType
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|mapTuple
init|=
name|XContentHelper
operator|.
name|convertToMap
argument_list|(
name|source
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|filteredSource
init|=
name|XContentMapValues
operator|.
name|filter
argument_list|(
name|mapTuple
operator|.
name|v2
argument_list|()
argument_list|,
name|includes
argument_list|,
name|excludes
argument_list|)
decl_stmt|;
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
decl_stmt|;
if|if
condition|(
name|compress
operator|!=
literal|null
operator|&&
name|compress
operator|&&
operator|(
name|compressThreshold
operator|==
operator|-
literal|1
operator|||
name|source
operator|.
name|length
argument_list|()
operator|>
name|compressThreshold
operator|)
condition|)
block|{
name|streamOutput
operator|=
name|cachedEntry
operator|.
name|bytes
argument_list|(
name|CompressorFactory
operator|.
name|defaultCompressor
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|streamOutput
operator|=
name|cachedEntry
operator|.
name|bytes
argument_list|()
expr_stmt|;
block|}
name|XContentType
name|contentType
init|=
name|formatContentType
decl_stmt|;
if|if
condition|(
name|contentType
operator|==
literal|null
condition|)
block|{
name|contentType
operator|=
name|mapTuple
operator|.
name|v1
argument_list|()
expr_stmt|;
block|}
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|contentType
argument_list|,
name|streamOutput
argument_list|)
operator|.
name|map
argument_list|(
name|filteredSource
argument_list|)
decl_stmt|;
name|builder
operator|.
name|close
argument_list|()
expr_stmt|;
name|source
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
expr_stmt|;
name|CachedStreamOutput
operator|.
name|pushEntry
argument_list|(
name|cachedEntry
argument_list|)
expr_stmt|;
block|}
elseif|else
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
name|source
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
name|source
operator|.
name|length
argument_list|()
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
try|try
block|{
name|XContentType
name|contentType
init|=
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|formatContentType
operator|!=
literal|null
operator|&&
name|formatContentType
operator|!=
name|contentType
condition|)
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|formatContentType
argument_list|,
name|cachedEntry
operator|.
name|bytes
argument_list|(
name|CompressorFactory
operator|.
name|defaultCompressor
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|copyCurrentStructure
argument_list|(
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|contentType
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
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
name|source
operator|.
name|writeTo
argument_list|(
name|streamOutput
argument_list|)
expr_stmt|;
name|streamOutput
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// we copy over the byte array, since we need to push back the cached entry
comment|// TODO, we we had a handle into when we are done with parsing, then we push back then and not copy over bytes
name|source
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
expr_stmt|;
comment|// update the data in the context, so it can be compressed and stored compressed outside...
name|context
operator|.
name|source
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
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
elseif|else
if|if
condition|(
name|formatContentType
operator|!=
literal|null
condition|)
block|{
comment|// see if we need to convert the content type
name|Compressor
name|compressor
init|=
name|CompressorFactory
operator|.
name|compressor
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|compressor
operator|!=
literal|null
condition|)
block|{
name|CompressedStreamInput
name|compressedStreamInput
init|=
name|compressor
operator|.
name|streamInput
argument_list|(
name|source
operator|.
name|streamInput
argument_list|()
argument_list|)
decl_stmt|;
name|XContentType
name|contentType
init|=
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|compressedStreamInput
argument_list|)
decl_stmt|;
name|compressedStreamInput
operator|.
name|resetToBufferStart
argument_list|()
expr_stmt|;
if|if
condition|(
name|contentType
operator|!=
name|formatContentType
condition|)
block|{
comment|// we need to reread and store back, compressed....
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
try|try
block|{
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
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|formatContentType
argument_list|,
name|streamOutput
argument_list|)
decl_stmt|;
name|builder
operator|.
name|copyCurrentStructure
argument_list|(
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|contentType
argument_list|)
operator|.
name|createParser
argument_list|(
name|compressedStreamInput
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|close
argument_list|()
expr_stmt|;
name|source
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
expr_stmt|;
comment|// update the data in the context, so we store it in the translog in this format
name|context
operator|.
name|source
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|CachedStreamOutput
operator|.
name|pushEntry
argument_list|(
name|cachedEntry
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|compressedStreamInput
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|XContentType
name|contentType
init|=
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentType
operator|!=
name|formatContentType
condition|)
block|{
comment|// we need to reread and store back
comment|// we need to reread and store back, compressed....
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
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|formatContentType
argument_list|,
name|cachedEntry
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|copyCurrentStructure
argument_list|(
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|contentType
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|close
argument_list|()
expr_stmt|;
name|source
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
expr_stmt|;
comment|// update the data in the context, so we store it in the translog in this format
name|context
operator|.
name|source
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
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
block|}
assert|assert
name|source
operator|.
name|hasArray
argument_list|()
assert|;
return|return
operator|new
name|Field
argument_list|(
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|source
operator|.
name|array
argument_list|()
argument_list|,
name|source
operator|.
name|arrayOffset
argument_list|()
argument_list|,
name|source
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
DECL|method|value
specifier|public
name|byte
index|[]
name|value
parameter_list|(
name|Document
name|document
parameter_list|)
block|{
name|Fieldable
name|field
init|=
name|document
operator|.
name|getFieldable
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|field
operator|==
literal|null
condition|?
literal|null
else|:
name|value
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|nativeValue
specifier|public
name|byte
index|[]
name|nativeValue
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
return|return
name|field
operator|.
name|getBinaryValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|byte
index|[]
name|value
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
name|byte
index|[]
name|value
init|=
name|field
operator|.
name|getBinaryValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|value
return|;
block|}
try|try
block|{
return|return
name|CompressorFactory
operator|.
name|uncompressIfNeeded
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|value
argument_list|)
argument_list|)
operator|.
name|toBytes
argument_list|()
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
DECL|method|valueFromString
specifier|public
name|byte
index|[]
name|valueFromString
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|valueAsString
specifier|public
name|String
name|valueAsString
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|indexedValue
specifier|public
name|String
name|indexedValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|value
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
comment|// all are defaults, no need to write it at all
if|if
condition|(
name|enabled
operator|==
name|Defaults
operator|.
name|ENABLED
operator|&&
name|compress
operator|==
literal|null
operator|&&
name|compressThreshold
operator|==
operator|-
literal|1
operator|&&
name|includes
operator|.
name|length
operator|==
literal|0
operator|&&
name|excludes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|builder
return|;
block|}
name|builder
operator|.
name|startObject
argument_list|(
name|contentType
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|enabled
operator|!=
name|Defaults
operator|.
name|ENABLED
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
name|enabled
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Objects
operator|.
name|equal
argument_list|(
name|format
argument_list|,
name|Defaults
operator|.
name|FORMAT
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
name|format
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
name|includes
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"includes"
argument_list|,
name|includes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|excludes
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"excludes"
argument_list|,
name|excludes
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
name|SourceFieldMapper
name|sourceMergeWith
init|=
operator|(
name|SourceFieldMapper
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

