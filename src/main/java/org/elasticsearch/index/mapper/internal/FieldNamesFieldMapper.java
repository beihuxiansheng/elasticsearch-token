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
name|document
operator|.
name|SortedSetDocValuesField
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
name|ElasticsearchIllegalArgumentException
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
name|Nullable
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
name|index
operator|.
name|codec
operator|.
name|docvaluesformat
operator|.
name|DocValuesFormatProvider
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
name|fieldNames
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
name|AbstractFieldMapper
argument_list|<
name|String
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
name|FieldNamesFieldMapper
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
name|FieldNamesFieldMapper
operator|.
name|NAME
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
DECL|field|FIELD_TYPE_PRE_1_3_0
specifier|public
specifier|static
specifier|final
name|FieldType
name|FIELD_TYPE_PRE_1_3_0
decl_stmt|;
static|static
block|{
name|FIELD_TYPE
operator|.
name|setIndexed
argument_list|(
literal|true
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
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|FIELD_TYPE_PRE_1_3_0
operator|=
operator|new
name|FieldType
argument_list|(
name|FIELD_TYPE
argument_list|)
expr_stmt|;
name|FIELD_TYPE_PRE_1_3_0
operator|.
name|setIndexed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|FIELD_TYPE_PRE_1_3_0
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
name|FieldNamesFieldMapper
argument_list|>
block|{
DECL|field|indexIsExplicit
specifier|private
name|boolean
name|indexIsExplicit
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
operator|new
name|FieldType
argument_list|(
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|indexName
operator|=
name|Defaults
operator|.
name|INDEX_NAME
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|index
specifier|public
name|Builder
name|index
parameter_list|(
name|boolean
name|index
parameter_list|)
block|{
name|indexIsExplicit
operator|=
literal|true
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
if|if
condition|(
operator|(
name|context
operator|.
name|indexCreatedVersion
argument_list|()
operator|==
literal|null
operator|||
name|context
operator|.
name|indexCreatedVersion
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_1_3_0
argument_list|)
operator|)
operator|&&
operator|!
name|indexIsExplicit
condition|)
block|{
name|fieldType
operator|.
name|setIndexed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FieldNamesFieldMapper
argument_list|(
name|name
argument_list|,
name|indexName
argument_list|,
name|boost
argument_list|,
name|fieldType
argument_list|,
name|postingsProvider
argument_list|,
name|docValuesProvider
argument_list|,
name|fieldDataSettings
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
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_3_0
argument_list|)
condition|)
block|{
name|FieldNamesFieldMapper
operator|.
name|Builder
name|builder
init|=
name|fieldNames
argument_list|()
decl_stmt|;
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
return|return
name|builder
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"type="
operator|+
name|CONTENT_TYPE
operator|+
literal|" is not supported on indices created before version 1.3.0 is your cluster running multiple datanode versions?"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|defaultFieldType
specifier|private
specifier|final
name|FieldType
name|defaultFieldType
decl_stmt|;
DECL|method|defaultFieldType
specifier|private
specifier|static
name|FieldType
name|defaultFieldType
parameter_list|(
name|Settings
name|indexSettings
parameter_list|)
block|{
return|return
name|indexSettings
operator|!=
literal|null
operator|&&
name|Version
operator|.
name|indexCreated
argument_list|(
name|indexSettings
argument_list|)
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_3_0
argument_list|)
condition|?
name|Defaults
operator|.
name|FIELD_TYPE
else|:
name|Defaults
operator|.
name|FIELD_TYPE_PRE_1_3_0
return|;
block|}
DECL|method|FieldNamesFieldMapper
specifier|public
name|FieldNamesFieldMapper
parameter_list|(
name|Settings
name|indexSettings
parameter_list|)
block|{
name|this
argument_list|(
name|Defaults
operator|.
name|NAME
argument_list|,
name|Defaults
operator|.
name|INDEX_NAME
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
DECL|method|FieldNamesFieldMapper
specifier|protected
name|FieldNamesFieldMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|indexName
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|indexName
argument_list|,
name|Defaults
operator|.
name|BOOST
argument_list|,
operator|new
name|FieldType
argument_list|(
name|defaultFieldType
argument_list|(
name|indexSettings
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
DECL|method|FieldNamesFieldMapper
specifier|public
name|FieldNamesFieldMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|indexName
parameter_list|,
name|float
name|boost
parameter_list|,
name|FieldType
name|fieldType
parameter_list|,
name|PostingsFormatProvider
name|postingsProvider
parameter_list|,
name|DocValuesFormatProvider
name|docValuesProvider
parameter_list|,
annotation|@
name|Nullable
name|Settings
name|fieldDataSettings
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|Names
argument_list|(
name|name
argument_list|,
name|indexName
argument_list|,
name|indexName
argument_list|,
name|name
argument_list|)
argument_list|,
name|boost
argument_list|,
name|fieldType
argument_list|,
literal|null
argument_list|,
name|Lucene
operator|.
name|KEYWORD_ANALYZER
argument_list|,
name|Lucene
operator|.
name|KEYWORD_ANALYZER
argument_list|,
name|postingsProvider
argument_list|,
name|docValuesProvider
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|fieldDataSettings
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultFieldType
operator|=
name|defaultFieldType
argument_list|(
name|indexSettings
argument_list|)
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
name|defaultFieldType
return|;
block|}
annotation|@
name|Override
DECL|method|defaultFieldDataType
specifier|public
name|FieldDataType
name|defaultFieldDataType
parameter_list|()
block|{
return|return
operator|new
name|FieldDataType
argument_list|(
literal|"string"
argument_list|)
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
name|void
name|parse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we parse in post parse
block|}
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
operator|!
name|fieldType
operator|.
name|indexed
argument_list|()
operator|&&
operator|!
name|fieldType
operator|.
name|stored
argument_list|()
operator|&&
operator|!
name|hasDocValues
argument_list|()
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
operator|.
name|indexed
argument_list|()
operator|||
name|fieldType
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
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|fieldName
argument_list|,
name|fieldType
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hasDocValues
argument_list|()
condition|)
block|{
name|document
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|fieldName
argument_list|)
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
name|XContentBuilder
name|json
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
name|super
operator|.
name|toXContent
argument_list|(
name|json
argument_list|,
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|json
operator|.
name|string
argument_list|()
operator|.
name|equals
argument_list|(
literal|"\""
operator|+
name|NAME
operator|+
literal|"\"{\"type\":\""
operator|+
name|CONTENT_TYPE
operator|+
literal|"\"}"
argument_list|)
condition|)
block|{
return|return
name|builder
return|;
block|}
return|return
name|super
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
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

