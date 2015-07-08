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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|UnmodifiableIterator
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
name|IndexableField
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
name|index
operator|.
name|fielddata
operator|.
name|FieldDataType
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|core
operator|.
name|TypeParsers
operator|.
name|parseField
import|;
end_import

begin_comment
comment|/**  * A mapper that indexes the field names of a document under<code>_field_names</code>. This mapper is typically useful in order  * to have fast<code>exists</code> and<code>missing</code> queries/filters.  *  * Added in Elasticsearch 1.3.  */
end_comment

begin_class
DECL|class|FieldNamesFieldMapper
specifier|public
class|class
name|FieldNamesFieldMapper
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
literal|"_field_names"
decl_stmt|;
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"_field_names"
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
name|FieldNamesFieldMapper
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
name|FieldNamesFieldType
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
name|DOCS
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setStored
argument_list|(
literal|false
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
name|setNames
argument_list|(
operator|new
name|MappedFieldType
operator|.
name|Names
argument_list|(
name|NAME
argument_list|)
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
name|FieldNamesFieldMapper
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
argument_list|)
expr_stmt|;
name|indexName
operator|=
name|Defaults
operator|.
name|NAME
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Deprecated
DECL|method|index
specifier|public
name|Builder
name|index
parameter_list|(
name|boolean
name|index
parameter_list|)
block|{
name|enabled
argument_list|(
name|index
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|index
argument_list|(
name|index
argument_list|)
return|;
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
annotation|@
name|Override
DECL|method|build
specifier|public
name|FieldNamesFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|setupFieldType
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|fieldType
operator|.
name|setHasDocValues
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|FieldNamesFieldType
name|fieldNamesFieldType
init|=
operator|(
name|FieldNamesFieldType
operator|)
name|fieldType
decl_stmt|;
name|fieldNamesFieldType
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
return|return
operator|new
name|FieldNamesFieldMapper
argument_list|(
name|fieldType
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
if|if
condition|(
name|parserContext
operator|.
name|indexVersionCreated
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_1_3_0
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"type="
operator|+
name|CONTENT_TYPE
operator|+
literal|" is not supported on indices created before version 1.3.0. Is your cluster running multiple datanode versions?"
argument_list|)
throw|;
block|}
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
if|if
condition|(
name|parserContext
operator|.
name|indexVersionCreated
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_2_0_0
argument_list|)
condition|)
block|{
name|parseField
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
block|}
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
block|}
DECL|class|FieldNamesFieldType
specifier|public
specifier|static
specifier|final
class|class
name|FieldNamesFieldType
extends|extends
name|MappedFieldType
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
DECL|method|FieldNamesFieldType
specifier|public
name|FieldNamesFieldType
parameter_list|()
block|{
name|setFieldDataType
argument_list|(
operator|new
name|FieldDataType
argument_list|(
literal|"string"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|FieldNamesFieldType
specifier|protected
name|FieldNamesFieldType
parameter_list|(
name|FieldNamesFieldType
name|ref
parameter_list|)
block|{
name|super
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|this
operator|.
name|enabled
operator|=
name|ref
operator|.
name|enabled
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|FieldNamesFieldType
name|clone
parameter_list|()
block|{
return|return
operator|new
name|FieldNamesFieldType
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|FieldNamesFieldType
name|that
init|=
operator|(
name|FieldNamesFieldType
operator|)
name|o
decl_stmt|;
return|return
name|enabled
operator|==
name|that
operator|.
name|enabled
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|super
operator|.
name|hashCode
argument_list|()
argument_list|,
name|enabled
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
DECL|method|checkCompatibility
specifier|public
name|void
name|checkCompatibility
parameter_list|(
name|MappedFieldType
name|fieldType
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|conflicts
parameter_list|,
name|boolean
name|strict
parameter_list|)
block|{
if|if
condition|(
name|strict
condition|)
block|{
name|FieldNamesFieldType
name|other
init|=
operator|(
name|FieldNamesFieldType
operator|)
name|fieldType
decl_stmt|;
if|if
condition|(
name|isEnabled
argument_list|()
operator|!=
name|other
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|conflicts
operator|.
name|add
argument_list|(
literal|"mapper ["
operator|+
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
operator|+
literal|"] is used by multiple types. Set update_all_types to true to update [enabled] across all types."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setEnabled
specifier|public
name|void
name|setEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
DECL|method|isEnabled
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|enabled
return|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|String
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
return|return
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|useTermQueryWithQueryString
specifier|public
name|boolean
name|useTermQueryWithQueryString
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|field|pre13Index
specifier|private
specifier|final
name|boolean
name|pre13Index
decl_stmt|;
comment|// if the index was created before 1.3, _field_names is always disabled
DECL|method|FieldNamesFieldMapper
specifier|public
name|FieldNamesFieldMapper
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
name|indexSettings
argument_list|)
expr_stmt|;
block|}
DECL|method|FieldNamesFieldMapper
specifier|public
name|FieldNamesFieldMapper
parameter_list|(
name|MappedFieldType
name|fieldType
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
name|pre13Index
operator|=
name|Version
operator|.
name|indexCreated
argument_list|(
name|indexSettings
argument_list|)
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_1_3_0
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|pre13Index
condition|)
block|{
name|FieldNamesFieldType
name|newFieldType
init|=
name|fieldType
argument_list|()
operator|.
name|clone
argument_list|()
decl_stmt|;
name|newFieldType
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|newFieldType
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|fieldTypeRef
operator|.
name|set
argument_list|(
name|newFieldType
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|fieldType
specifier|public
name|FieldNamesFieldType
name|fieldType
parameter_list|()
block|{
return|return
operator|(
name|FieldNamesFieldType
operator|)
name|super
operator|.
name|fieldType
argument_list|()
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
DECL|method|extractFieldNames
specifier|static
name|Iterable
argument_list|<
name|String
argument_list|>
name|extractFieldNames
parameter_list|(
specifier|final
name|String
name|fullPath
parameter_list|)
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|UnmodifiableIterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
name|int
name|endIndex
init|=
name|nextEndIndex
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|int
name|nextEndIndex
parameter_list|(
name|int
name|index
parameter_list|)
block|{
while|while
condition|(
name|index
operator|<
name|fullPath
operator|.
name|length
argument_list|()
operator|&&
name|fullPath
operator|.
name|charAt
argument_list|(
name|index
argument_list|)
operator|!=
literal|'.'
condition|)
block|{
name|index
operator|+=
literal|1
expr_stmt|;
block|}
return|return
name|index
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|endIndex
operator|<=
name|fullPath
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
specifier|final
name|String
name|result
init|=
name|fullPath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|endIndex
argument_list|)
decl_stmt|;
name|endIndex
operator|=
name|nextEndIndex
argument_list|(
name|endIndex
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
return|;
block|}
block|}
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
name|fieldType
argument_list|()
operator|.
name|isEnabled
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return;
block|}
for|for
control|(
name|ParseContext
operator|.
name|Document
name|document
range|:
name|context
operator|.
name|docs
argument_list|()
control|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexableField
name|field
range|:
name|document
operator|.
name|getFields
argument_list|()
control|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
for|for
control|(
name|String
name|fieldName
range|:
name|extractFieldNames
argument_list|(
name|path
argument_list|)
control|)
block|{
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|NONE
operator|||
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
condition|)
block|{
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|fieldName
argument_list|,
name|fieldType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
if|if
condition|(
name|pre13Index
condition|)
block|{
return|return
name|builder
return|;
block|}
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
name|includeDefaults
operator|==
literal|false
operator|&&
name|fieldType
argument_list|()
operator|.
name|isEnabled
argument_list|()
operator|==
name|Defaults
operator|.
name|ENABLED
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
name|NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|isEnabled
argument_list|()
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
name|fieldType
argument_list|()
operator|.
name|isEnabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexCreatedBefore2x
operator|&&
operator|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|equals
argument_list|(
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|)
operator|==
literal|false
operator|)
condition|)
block|{
name|super
operator|.
name|doXContentBody
argument_list|(
name|builder
argument_list|,
name|includeDefaults
argument_list|,
name|params
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
DECL|method|isGenerated
specifier|public
name|boolean
name|isGenerated
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

