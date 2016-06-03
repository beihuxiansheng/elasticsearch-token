begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|StoredField
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
name|IndexOptions
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
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
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
name|MappedFieldType
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
name|Mapper
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
name|MapperParsingException
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
name|MetadataFieldMapper
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
name|ParseContext
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
name|QueryShardContext
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
name|QueryShardException
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
name|Arrays
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
name|lenientNodeBooleanValue
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
name|MetadataFieldMapper
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
DECL|field|FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|MappedFieldType
name|FIELD_TYPE
init|=
operator|new
name|SourceFieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|FIELD_TYPE
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|NONE
argument_list|)
expr_stmt|;
comment|// not indexed
name|FIELD_TYPE
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setIndexAnalyzer
argument_list|(
name|Lucene
operator|.
name|KEYWORD_ANALYZER
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setSearchAnalyzer
argument_list|(
name|Lucene
operator|.
name|KEYWORD_ANALYZER
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setName
argument_list|(
name|NAME
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
name|MetadataFieldMapper
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
DECL|field|includes
specifier|private
name|String
index|[]
name|includes
init|=
literal|null
decl_stmt|;
DECL|field|excludes
specifier|private
name|String
index|[]
name|excludes
init|=
literal|null
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
argument_list|,
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|,
name|Defaults
operator|.
name|FIELD_TYPE
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
name|enabled
argument_list|,
name|includes
argument_list|,
name|excludes
argument_list|,
name|context
operator|.
name|indexSettings
argument_list|()
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
name|MetadataFieldMapper
operator|.
name|TypeParser
block|{
annotation|@
name|Override
DECL|method|parse
specifier|public
name|MetadataFieldMapper
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
name|Builder
name|builder
init|=
operator|new
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|iterator
init|=
name|node
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
name|entry
operator|.
name|getKey
argument_list|()
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
name|lenientNodeBooleanValue
argument_list|(
name|fieldNode
argument_list|)
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
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
operator|&&
name|parserContext
operator|.
name|indexVersionCreated
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_5_0_0_alpha1
argument_list|)
condition|)
block|{
comment|// ignore on old indices, reject on and after 5.0
name|iterator
operator|.
name|remove
argument_list|()
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
name|iterator
operator|.
name|remove
argument_list|()
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
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|getDefault
specifier|public
name|MetadataFieldMapper
name|getDefault
parameter_list|(
name|Settings
name|indexSettings
parameter_list|,
name|MappedFieldType
name|fieldType
parameter_list|,
name|String
name|typeName
parameter_list|)
block|{
return|return
operator|new
name|SourceFieldMapper
argument_list|(
name|indexSettings
argument_list|)
return|;
block|}
block|}
DECL|class|SourceFieldType
specifier|static
specifier|final
class|class
name|SourceFieldType
extends|extends
name|MappedFieldType
block|{
DECL|method|SourceFieldType
specifier|public
name|SourceFieldType
parameter_list|()
block|{}
DECL|method|SourceFieldType
specifier|protected
name|SourceFieldType
parameter_list|(
name|SourceFieldType
name|ref
parameter_list|)
block|{
name|super
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|MappedFieldType
name|clone
parameter_list|()
block|{
return|return
operator|new
name|SourceFieldType
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|typeName
specifier|public
name|String
name|typeName
parameter_list|()
block|{
return|return
name|CONTENT_TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|termQuery
specifier|public
name|Query
name|termQuery
parameter_list|(
name|Object
name|value
parameter_list|,
name|QueryShardContext
name|context
parameter_list|)
block|{
throw|throw
operator|new
name|QueryShardException
argument_list|(
name|context
argument_list|,
literal|"The _source field is not searchable"
argument_list|)
throw|;
block|}
block|}
DECL|field|enabled
specifier|private
specifier|final
name|boolean
name|enabled
decl_stmt|;
comment|/** indicates whether the source will always exist and be complete, for use by features like the update API */
DECL|field|complete
specifier|private
specifier|final
name|boolean
name|complete
decl_stmt|;
DECL|field|includes
specifier|private
specifier|final
name|String
index|[]
name|includes
decl_stmt|;
DECL|field|excludes
specifier|private
specifier|final
name|String
index|[]
name|excludes
decl_stmt|;
DECL|method|SourceFieldMapper
specifier|private
name|SourceFieldMapper
parameter_list|(
name|Settings
name|indexSettings
parameter_list|)
block|{
name|this
argument_list|(
name|Defaults
operator|.
name|ENABLED
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
DECL|method|SourceFieldMapper
specifier|private
name|SourceFieldMapper
parameter_list|(
name|boolean
name|enabled
parameter_list|,
name|String
index|[]
name|includes
parameter_list|,
name|String
index|[]
name|excludes
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|clone
argument_list|()
argument_list|,
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
comment|// Only stored.
name|this
operator|.
name|enabled
operator|=
name|enabled
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
name|complete
operator|=
name|enabled
operator|&&
name|includes
operator|==
literal|null
operator|&&
name|excludes
operator|==
literal|null
expr_stmt|;
block|}
DECL|method|enabled
specifier|public
name|boolean
name|enabled
parameter_list|()
block|{
return|return
name|enabled
return|;
block|}
DECL|method|excludes
specifier|public
name|String
index|[]
name|excludes
parameter_list|()
block|{
return|return
name|this
operator|.
name|excludes
operator|!=
literal|null
condition|?
name|this
operator|.
name|excludes
else|:
name|Strings
operator|.
name|EMPTY_ARRAY
return|;
block|}
DECL|method|includes
specifier|public
name|String
index|[]
name|includes
parameter_list|()
block|{
return|return
name|this
operator|.
name|includes
operator|!=
literal|null
condition|?
name|this
operator|.
name|includes
else|:
name|Strings
operator|.
name|EMPTY_ARRAY
return|;
block|}
DECL|method|isComplete
specifier|public
name|boolean
name|isComplete
parameter_list|()
block|{
return|return
name|complete
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
name|Mapper
name|parse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nothing to do here, we will call it in pre parse
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|parseCreateField
specifier|protected
name|void
name|parseCreateField
parameter_list|(
name|ParseContext
name|context
parameter_list|,
name|List
argument_list|<
name|Field
argument_list|>
name|fields
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
return|return;
block|}
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
return|return;
block|}
name|BytesReference
name|source
init|=
name|context
operator|.
name|sourceToParse
argument_list|()
operator|.
name|source
argument_list|()
decl_stmt|;
comment|// Percolate and tv APIs may not set the source and that is ok, because these APIs will not index any data
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|boolean
name|filtered
init|=
operator|(
name|includes
operator|!=
literal|null
operator|&&
name|includes
operator|.
name|length
operator|>
literal|0
operator|)
operator|||
operator|(
name|excludes
operator|!=
literal|null
operator|&&
name|excludes
operator|.
name|length
operator|>
literal|0
operator|)
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
name|BytesStreamOutput
name|bStream
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|XContentType
name|contentType
init|=
name|mapTuple
operator|.
name|v1
argument_list|()
decl_stmt|;
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|contentType
argument_list|,
name|bStream
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
name|bStream
operator|.
name|bytes
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|source
operator|.
name|hasArray
argument_list|()
condition|)
block|{
name|source
operator|=
name|source
operator|.
name|toBytesArray
argument_list|()
expr_stmt|;
block|}
name|fields
operator|.
name|add
argument_list|(
operator|new
name|StoredField
argument_list|(
name|fieldType
argument_list|()
operator|.
name|name
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
argument_list|)
expr_stmt|;
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
name|boolean
name|includeDefaults
init|=
name|params
operator|.
name|paramAsBoolean
argument_list|(
literal|"include_defaults"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// all are defaults, no need to write it at all
if|if
condition|(
operator|!
name|includeDefaults
operator|&&
name|enabled
operator|==
name|Defaults
operator|.
name|ENABLED
operator|&&
name|includes
operator|==
literal|null
operator|&&
name|excludes
operator|==
literal|null
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
name|includeDefaults
operator|||
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
name|includes
operator|!=
literal|null
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
elseif|else
if|if
condition|(
name|includeDefaults
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"includes"
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|excludes
operator|!=
literal|null
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
elseif|else
if|if
condition|(
name|includeDefaults
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"excludes"
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
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
DECL|method|doMerge
specifier|protected
name|void
name|doMerge
parameter_list|(
name|Mapper
name|mergeWith
parameter_list|,
name|boolean
name|updateAllTypes
parameter_list|)
block|{
name|SourceFieldMapper
name|sourceMergeWith
init|=
operator|(
name|SourceFieldMapper
operator|)
name|mergeWith
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|conflicts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|enabled
operator|!=
name|sourceMergeWith
operator|.
name|enabled
condition|)
block|{
name|conflicts
operator|.
name|add
argument_list|(
literal|"Cannot update enabled setting for [_source]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Arrays
operator|.
name|equals
argument_list|(
name|includes
argument_list|()
argument_list|,
name|sourceMergeWith
operator|.
name|includes
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
name|conflicts
operator|.
name|add
argument_list|(
literal|"Cannot update includes setting for [_source]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Arrays
operator|.
name|equals
argument_list|(
name|excludes
argument_list|()
argument_list|,
name|sourceMergeWith
operator|.
name|excludes
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
name|conflicts
operator|.
name|add
argument_list|(
literal|"Cannot update excludes setting for [_source]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|conflicts
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't merge because of conflicts: "
operator|+
name|conflicts
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

