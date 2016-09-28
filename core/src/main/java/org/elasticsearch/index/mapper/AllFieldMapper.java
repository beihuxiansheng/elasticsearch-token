begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
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
name|Query
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
name|all
operator|.
name|AllEntries
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
name|all
operator|.
name|AllField
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
name|all
operator|.
name|AllTermQuery
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
name|index
operator|.
name|analysis
operator|.
name|NamedAnalyzer
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
name|similarity
operator|.
name|SimilarityService
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
name|nodeMapValue
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
name|TypeParsers
operator|.
name|parseTextField
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AllFieldMapper
specifier|public
class|class
name|AllFieldMapper
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
literal|"_all"
decl_stmt|;
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"_all"
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
name|AllFieldMapper
operator|.
name|NAME
decl_stmt|;
DECL|field|INDEX_NAME
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_NAME
init|=
name|AllFieldMapper
operator|.
name|NAME
decl_stmt|;
DECL|field|ENABLED
specifier|public
specifier|static
specifier|final
name|EnabledAttributeMapper
name|ENABLED
init|=
name|EnabledAttributeMapper
operator|.
name|UNSET_ENABLED
decl_stmt|;
DECL|field|POSITION_INCREMENT_GAP
specifier|public
specifier|static
specifier|final
name|int
name|POSITION_INCREMENT_GAP
init|=
literal|100
decl_stmt|;
DECL|field|FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|MappedFieldType
name|FIELD_TYPE
init|=
operator|new
name|AllFieldType
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
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setTokenized
argument_list|(
literal|true
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
name|AllFieldMapper
argument_list|>
block|{
DECL|field|enabled
specifier|private
name|EnabledAttributeMapper
name|enabled
init|=
name|Defaults
operator|.
name|ENABLED
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|MappedFieldType
name|existing
parameter_list|)
block|{
name|super
argument_list|(
name|Defaults
operator|.
name|NAME
argument_list|,
name|existing
operator|==
literal|null
condition|?
name|Defaults
operator|.
name|FIELD_TYPE
else|:
name|existing
argument_list|,
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|)
expr_stmt|;
name|builder
operator|=
name|this
expr_stmt|;
block|}
DECL|method|enabled
specifier|public
name|Builder
name|enabled
parameter_list|(
name|EnabledAttributeMapper
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
annotation|@
name|Override
DECL|method|build
specifier|public
name|AllFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
comment|// In case the mapping overrides these
comment|// TODO: this should be an exception! it doesnt make sense to not index this field
if|if
condition|(
name|fieldType
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
name|fieldType
operator|.
name|setIndexOptions
argument_list|(
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|indexOptions
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fieldType
operator|.
name|setIndexAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
name|fieldType
operator|.
name|indexAnalyzer
argument_list|()
argument_list|,
name|Defaults
operator|.
name|POSITION_INCREMENT_GAP
argument_list|)
argument_list|)
expr_stmt|;
name|fieldType
operator|.
name|setSearchAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
name|fieldType
operator|.
name|searchAnalyzer
argument_list|()
argument_list|,
name|Defaults
operator|.
name|POSITION_INCREMENT_GAP
argument_list|)
argument_list|)
expr_stmt|;
name|fieldType
operator|.
name|setSearchQuoteAnalyzer
argument_list|(
operator|new
name|NamedAnalyzer
argument_list|(
name|fieldType
operator|.
name|searchQuoteAnalyzer
argument_list|()
argument_list|,
name|Defaults
operator|.
name|POSITION_INCREMENT_GAP
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fieldType
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|new
name|AllFieldMapper
argument_list|(
name|fieldType
argument_list|,
name|enabled
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
argument_list|(
name|parserContext
operator|.
name|mapperService
argument_list|()
operator|.
name|fullName
argument_list|(
name|NAME
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|fieldType
argument_list|()
operator|.
name|setIndexAnalyzer
argument_list|(
name|parserContext
operator|.
name|getIndexAnalyzers
argument_list|()
operator|.
name|getDefaultIndexAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|fieldType
argument_list|()
operator|.
name|setSearchAnalyzer
argument_list|(
name|parserContext
operator|.
name|getIndexAnalyzers
argument_list|()
operator|.
name|getDefaultSearchAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|fieldType
argument_list|()
operator|.
name|setSearchQuoteAnalyzer
argument_list|(
name|parserContext
operator|.
name|getIndexAnalyzers
argument_list|()
operator|.
name|getDefaultSearchQuoteAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
comment|// parseField below will happily parse the doc_values setting, but it is then never passed to
comment|// the AllFieldMapper ctor in the builder since it is not valid. Here we validate
comment|// the doc values settings (old and new) are rejected
name|Object
name|docValues
init|=
name|node
operator|.
name|get
argument_list|(
literal|"doc_values"
argument_list|)
decl_stmt|;
if|if
condition|(
name|docValues
operator|!=
literal|null
operator|&&
name|lenientNodeBooleanValue
argument_list|(
name|docValues
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Field ["
operator|+
name|name
operator|+
literal|"] is always tokenized and cannot have doc values"
argument_list|)
throw|;
block|}
comment|// convoluted way of specifying doc values
name|Object
name|fielddata
init|=
name|node
operator|.
name|get
argument_list|(
literal|"fielddata"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fielddata
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fielddataMap
init|=
name|nodeMapValue
argument_list|(
name|fielddata
argument_list|,
literal|"fielddata"
argument_list|)
decl_stmt|;
name|Object
name|format
init|=
name|fielddataMap
operator|.
name|get
argument_list|(
literal|"format"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"doc_values"
operator|.
name|equals
argument_list|(
name|format
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Field ["
operator|+
name|name
operator|+
literal|"] is always tokenized and cannot have doc values"
argument_list|)
throw|;
block|}
block|}
name|parseTextField
argument_list|(
name|builder
argument_list|,
name|builder
operator|.
name|name
argument_list|,
name|node
argument_list|,
name|parserContext
argument_list|)
expr_stmt|;
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
condition|?
name|EnabledAttributeMapper
operator|.
name|ENABLED
else|:
name|EnabledAttributeMapper
operator|.
name|DISABLED
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
name|AllFieldMapper
argument_list|(
name|indexSettings
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
block|}
DECL|class|AllFieldType
specifier|static
specifier|final
class|class
name|AllFieldType
extends|extends
name|StringFieldType
block|{
DECL|method|AllFieldType
specifier|public
name|AllFieldType
parameter_list|()
block|{         }
DECL|method|AllFieldType
specifier|protected
name|AllFieldType
parameter_list|(
name|AllFieldType
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
name|AllFieldType
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
DECL|method|queryStringTermQuery
specifier|public
name|Query
name|queryStringTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
return|return
operator|new
name|AllTermQuery
argument_list|(
name|term
argument_list|)
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
return|return
name|queryStringTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|name
argument_list|()
argument_list|,
name|indexedValueForSearch
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|field|enabledState
specifier|private
name|EnabledAttributeMapper
name|enabledState
decl_stmt|;
DECL|method|AllFieldMapper
specifier|private
name|AllFieldMapper
parameter_list|(
name|Settings
name|indexSettings
parameter_list|,
name|MappedFieldType
name|existing
parameter_list|)
block|{
name|this
argument_list|(
name|existing
operator|==
literal|null
condition|?
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|clone
argument_list|()
else|:
name|existing
operator|.
name|clone
argument_list|()
argument_list|,
name|Defaults
operator|.
name|ENABLED
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
DECL|method|AllFieldMapper
specifier|private
name|AllFieldMapper
parameter_list|(
name|MappedFieldType
name|fieldType
parameter_list|,
name|EnabledAttributeMapper
name|enabled
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|fieldType
argument_list|,
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|enabledState
operator|=
name|enabled
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
name|enabledState
operator|.
name|enabled
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
block|{     }
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
comment|// we parse in post parse
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
name|enabledState
operator|.
name|enabled
condition|)
block|{
return|return;
block|}
for|for
control|(
name|AllEntries
operator|.
name|Entry
name|entry
range|:
name|context
operator|.
name|allEntries
argument_list|()
operator|.
name|entries
argument_list|()
control|)
block|{
name|fields
operator|.
name|add
argument_list|(
operator|new
name|AllField
argument_list|(
name|fieldType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|entry
operator|.
name|value
argument_list|()
argument_list|,
name|entry
operator|.
name|boost
argument_list|()
argument_list|,
name|fieldType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
operator|!
name|includeDefaults
condition|)
block|{
comment|// simulate the generation to make sure we don't add unnecessary content if all is default
comment|// if all are defaults, no need to write it at all - generating is twice is ok though
name|BytesStreamOutput
name|bytesStreamOutput
init|=
operator|new
name|BytesStreamOutput
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|XContentBuilder
name|b
init|=
operator|new
name|XContentBuilder
argument_list|(
name|builder
operator|.
name|contentType
argument_list|()
operator|.
name|xContent
argument_list|()
argument_list|,
name|bytesStreamOutput
argument_list|)
decl_stmt|;
name|b
operator|.
name|startObject
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
name|long
name|pos
init|=
name|bytesStreamOutput
operator|.
name|position
argument_list|()
decl_stmt|;
name|innerToXContent
argument_list|(
name|b
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|b
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|pos
operator|==
name|bytesStreamOutput
operator|.
name|position
argument_list|()
condition|)
block|{
return|return
name|builder
return|;
block|}
block|}
name|builder
operator|.
name|startObject
argument_list|(
name|CONTENT_TYPE
argument_list|)
expr_stmt|;
name|innerToXContent
argument_list|(
name|builder
argument_list|,
name|includeDefaults
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|innerToXContent
specifier|private
name|void
name|innerToXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|boolean
name|includeDefaults
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|includeDefaults
operator|||
name|enabledState
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
name|enabledState
operator|.
name|enabled
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELD_TYPE
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
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|storeTermVectors
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"store_term_vectors"
argument_list|,
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|storeTermVectorOffsets
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"store_term_vector_offsets"
argument_list|,
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|storeTermVectorPositions
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"store_term_vector_positions"
argument_list|,
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|storeTermVectorPayloads
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"store_term_vector_payloads"
argument_list|,
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
operator|!=
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|omitNorms
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"norms"
argument_list|,
operator|!
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|doXContentAnalyzers
argument_list|(
name|builder
argument_list|,
name|includeDefaults
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|similarity
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"similarity"
argument_list|,
name|fieldType
argument_list|()
operator|.
name|similarity
argument_list|()
operator|.
name|name
argument_list|()
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
literal|"similarity"
argument_list|,
name|SimilarityService
operator|.
name|DEFAULT_SIMILARITY
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
operator|(
operator|(
name|AllFieldMapper
operator|)
name|mergeWith
operator|)
operator|.
name|enabled
argument_list|()
operator|!=
name|this
operator|.
name|enabled
argument_list|()
operator|&&
operator|(
operator|(
name|AllFieldMapper
operator|)
name|mergeWith
operator|)
operator|.
name|enabledState
operator|!=
name|Defaults
operator|.
name|ENABLED
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"mapper ["
operator|+
name|fieldType
argument_list|()
operator|.
name|name
argument_list|()
operator|+
literal|"] enabled is "
operator|+
name|this
operator|.
name|enabled
argument_list|()
operator|+
literal|" now encountering "
operator|+
operator|(
operator|(
name|AllFieldMapper
operator|)
name|mergeWith
operator|)
operator|.
name|enabled
argument_list|()
argument_list|)
throw|;
block|}
name|super
operator|.
name|doMerge
argument_list|(
name|mergeWith
argument_list|,
name|updateAllTypes
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
