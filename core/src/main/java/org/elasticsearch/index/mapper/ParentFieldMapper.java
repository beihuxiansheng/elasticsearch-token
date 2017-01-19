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
name|SortedDocValuesField
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
name|DocValuesType
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
name|BooleanClause
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
name|BooleanQuery
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
name|DocValuesTermsQuery
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TermQuery
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
name|ParseField
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
name|BytesRefs
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
name|settings
operator|.
name|loader
operator|.
name|SettingsLoader
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
name|fielddata
operator|.
name|IndexFieldData
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
name|plain
operator|.
name|ParentChildIndexFieldData
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
name|Collections
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
name|nodeMapValue
import|;
end_import

begin_class
DECL|class|ParentFieldMapper
specifier|public
class|class
name|ParentFieldMapper
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
literal|"_parent"
decl_stmt|;
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"_parent"
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
name|ParentFieldMapper
operator|.
name|NAME
decl_stmt|;
DECL|field|FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|ParentFieldType
name|FIELD_TYPE
init|=
operator|new
name|ParentFieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|FIELD_TYPE
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setHasDocValues
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setDocValuesType
argument_list|(
name|DocValuesType
operator|.
name|SORTED
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
name|ParentFieldMapper
argument_list|>
block|{
DECL|field|parentType
specifier|private
name|String
name|parentType
decl_stmt|;
DECL|field|documentType
specifier|private
specifier|final
name|String
name|documentType
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|documentType
parameter_list|)
block|{
name|super
argument_list|(
name|Defaults
operator|.
name|NAME
argument_list|,
operator|new
name|ParentFieldType
argument_list|(
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|,
name|documentType
argument_list|)
argument_list|,
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|)
expr_stmt|;
name|this
operator|.
name|documentType
operator|=
name|documentType
expr_stmt|;
name|builder
operator|=
name|this
expr_stmt|;
block|}
DECL|method|type
specifier|public
name|Builder
name|type
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|parentType
operator|=
name|type
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|eagerGlobalOrdinals
specifier|public
name|Builder
name|eagerGlobalOrdinals
parameter_list|(
name|boolean
name|eagerGlobalOrdinals
parameter_list|)
block|{
name|fieldType
argument_list|()
operator|.
name|setEagerGlobalOrdinals
argument_list|(
name|eagerGlobalOrdinals
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|ParentFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|parentType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"[_parent] field mapping must contain the [type] option"
argument_list|)
throw|;
block|}
name|name
operator|=
name|joinField
argument_list|(
name|parentType
argument_list|)
expr_stmt|;
name|setupFieldType
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
operator|new
name|ParentFieldMapper
argument_list|(
name|createParentJoinFieldMapper
argument_list|(
name|documentType
argument_list|,
name|context
argument_list|)
argument_list|,
name|fieldType
argument_list|,
name|parentType
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
DECL|field|FIELDDATA
specifier|private
specifier|static
specifier|final
name|ParseField
name|FIELDDATA
init|=
operator|new
name|ParseField
argument_list|(
literal|"fielddata"
argument_list|)
operator|.
name|withAllDeprecated
argument_list|(
literal|"eager_global_ordinals"
argument_list|)
decl_stmt|;
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
name|type
argument_list|()
argument_list|)
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
literal|"type"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|type
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
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
name|FIELDDATA
operator|.
name|match
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
comment|// for bw compat only
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fieldDataSettings
init|=
name|SettingsLoader
operator|.
name|Helper
operator|.
name|loadNestedFromMap
argument_list|(
name|nodeMapValue
argument_list|(
name|fieldNode
argument_list|,
literal|"fielddata"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldDataSettings
operator|.
name|containsKey
argument_list|(
literal|"loading"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|eagerGlobalOrdinals
argument_list|(
literal|"eager_global_ordinals"
operator|.
name|equals
argument_list|(
name|fieldDataSettings
operator|.
name|get
argument_list|(
literal|"loading"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"eager_global_ordinals"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|eagerGlobalOrdinals
argument_list|(
name|XContentMapValues
operator|.
name|nodeBooleanValue
argument_list|(
name|fieldNode
argument_list|,
literal|"eager_global_ordinals"
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
annotation|@
name|Override
DECL|method|getDefault
specifier|public
name|MetadataFieldMapper
name|getDefault
parameter_list|(
name|MappedFieldType
name|fieldType
parameter_list|,
name|ParserContext
name|context
parameter_list|)
block|{
specifier|final
name|Settings
name|indexSettings
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|getIndexSettings
argument_list|()
operator|.
name|getSettings
argument_list|()
decl_stmt|;
specifier|final
name|String
name|typeName
init|=
name|context
operator|.
name|type
argument_list|()
decl_stmt|;
name|KeywordFieldMapper
name|parentJoinField
init|=
name|createParentJoinFieldMapper
argument_list|(
name|typeName
argument_list|,
operator|new
name|BuilderContext
argument_list|(
name|indexSettings
argument_list|,
operator|new
name|ContentPath
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|MappedFieldType
name|childJoinFieldType
init|=
operator|new
name|ParentFieldType
argument_list|(
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|,
name|typeName
argument_list|)
decl_stmt|;
name|childJoinFieldType
operator|.
name|setName
argument_list|(
name|ParentFieldMapper
operator|.
name|NAME
argument_list|)
expr_stmt|;
return|return
operator|new
name|ParentFieldMapper
argument_list|(
name|parentJoinField
argument_list|,
name|childJoinFieldType
argument_list|,
literal|null
argument_list|,
name|indexSettings
argument_list|)
return|;
block|}
block|}
DECL|method|createParentJoinFieldMapper
specifier|static
name|KeywordFieldMapper
name|createParentJoinFieldMapper
parameter_list|(
name|String
name|docType
parameter_list|,
name|BuilderContext
name|context
parameter_list|)
block|{
name|KeywordFieldMapper
operator|.
name|Builder
name|parentJoinField
init|=
operator|new
name|KeywordFieldMapper
operator|.
name|Builder
argument_list|(
name|joinField
argument_list|(
name|docType
argument_list|)
argument_list|)
decl_stmt|;
name|parentJoinField
operator|.
name|indexOptions
argument_list|(
name|IndexOptions
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|parentJoinField
operator|.
name|docValues
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|parentJoinField
operator|.
name|fieldType
argument_list|()
operator|.
name|setDocValuesType
argument_list|(
name|DocValuesType
operator|.
name|SORTED
argument_list|)
expr_stmt|;
return|return
name|parentJoinField
operator|.
name|build
argument_list|(
name|context
argument_list|)
return|;
block|}
DECL|class|ParentFieldType
specifier|static
specifier|final
class|class
name|ParentFieldType
extends|extends
name|MappedFieldType
block|{
DECL|field|documentType
specifier|final
name|String
name|documentType
decl_stmt|;
DECL|method|ParentFieldType
specifier|public
name|ParentFieldType
parameter_list|()
block|{
name|documentType
operator|=
literal|null
expr_stmt|;
name|setEagerGlobalOrdinals
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|ParentFieldType
name|ParentFieldType
parameter_list|(
name|ParentFieldType
name|ref
parameter_list|,
name|String
name|documentType
parameter_list|)
block|{
name|super
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|this
operator|.
name|documentType
operator|=
name|documentType
expr_stmt|;
block|}
DECL|method|ParentFieldType
specifier|private
name|ParentFieldType
parameter_list|(
name|ParentFieldType
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
name|documentType
operator|=
name|ref
operator|.
name|documentType
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
name|ParentFieldType
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
annotation|@
name|Nullable
name|QueryShardContext
name|context
parameter_list|)
block|{
return|return
name|termsQuery
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|value
argument_list|)
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|termsQuery
specifier|public
name|Query
name|termsQuery
parameter_list|(
name|List
name|values
parameter_list|,
annotation|@
name|Nullable
name|QueryShardContext
name|context
parameter_list|)
block|{
name|BytesRef
index|[]
name|ids
init|=
operator|new
name|BytesRef
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
name|ids
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ids
index|[
name|i
index|]
operator|=
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BooleanQuery
operator|.
name|Builder
name|query
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|DocValuesTermsQuery
argument_list|(
name|name
argument_list|()
argument_list|,
name|ids
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|TypeFieldMapper
operator|.
name|NAME
argument_list|,
name|documentType
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|FILTER
argument_list|)
expr_stmt|;
return|return
name|query
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fielddataBuilder
specifier|public
name|IndexFieldData
operator|.
name|Builder
name|fielddataBuilder
parameter_list|()
block|{
return|return
operator|new
name|ParentChildIndexFieldData
operator|.
name|Builder
argument_list|()
return|;
block|}
block|}
DECL|field|parentType
specifier|private
specifier|final
name|String
name|parentType
decl_stmt|;
comment|// has no impact of field data settings, is just here for creating a join field,
comment|// the parent field mapper in the child type pointing to this type determines the field data settings for this join field
DECL|field|parentJoinField
specifier|private
specifier|final
name|KeywordFieldMapper
name|parentJoinField
decl_stmt|;
DECL|method|ParentFieldMapper
specifier|private
name|ParentFieldMapper
parameter_list|(
name|KeywordFieldMapper
name|parentJoinField
parameter_list|,
name|MappedFieldType
name|childJoinFieldType
parameter_list|,
name|String
name|parentType
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|super
argument_list|(
name|NAME
argument_list|,
name|childJoinFieldType
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
name|parentType
operator|=
name|parentType
expr_stmt|;
name|this
operator|.
name|parentJoinField
operator|=
name|parentJoinField
expr_stmt|;
block|}
DECL|method|getParentJoinFieldType
specifier|public
name|MappedFieldType
name|getParentJoinFieldType
parameter_list|()
block|{
return|return
name|parentJoinField
operator|.
name|fieldType
argument_list|()
return|;
block|}
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|parentType
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
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
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
name|IndexableField
argument_list|>
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|parent
init|=
name|context
operator|.
name|docMapper
argument_list|()
operator|.
name|isParent
argument_list|(
name|context
operator|.
name|sourceToParse
argument_list|()
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
name|parentJoinField
operator|.
name|fieldType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|context
operator|.
name|sourceToParse
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|active
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|context
operator|.
name|parser
argument_list|()
operator|.
name|currentName
argument_list|()
operator|!=
literal|null
operator|&&
name|context
operator|.
name|parser
argument_list|()
operator|.
name|currentName
argument_list|()
operator|.
name|equals
argument_list|(
name|Defaults
operator|.
name|NAME
argument_list|)
condition|)
block|{
comment|// we are in the parsing of _parent phase
name|String
name|parentId
init|=
name|context
operator|.
name|parser
argument_list|()
operator|.
name|text
argument_list|()
decl_stmt|;
name|context
operator|.
name|sourceToParse
argument_list|()
operator|.
name|parent
argument_list|(
name|parentId
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
name|fieldType
operator|.
name|name
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|parentId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// otherwise, we are running it post processing of the xcontent
name|String
name|parsedParentId
init|=
name|context
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
name|Defaults
operator|.
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|sourceToParse
argument_list|()
operator|.
name|parent
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|parentId
init|=
name|context
operator|.
name|sourceToParse
argument_list|()
operator|.
name|parent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parsedParentId
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|parentId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"No parent id provided, not within the document, and not externally"
argument_list|)
throw|;
block|}
comment|// we did not add it in the parsing phase, add it now
name|fields
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
name|fieldType
operator|.
name|name
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|parentId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parentId
operator|!=
literal|null
operator|&&
operator|!
name|parsedParentId
operator|.
name|equals
argument_list|(
name|Uid
operator|.
name|createUid
argument_list|(
name|parentType
argument_list|,
name|parentId
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Parent id mismatch, document value is ["
operator|+
name|Uid
operator|.
name|createUid
argument_list|(
name|parsedParentId
argument_list|)
operator|.
name|id
argument_list|()
operator|+
literal|"], while external value is ["
operator|+
name|parentId
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
comment|// we have parent mapping, yet no value was set, ignore it...
block|}
DECL|method|joinField
specifier|public
specifier|static
name|String
name|joinField
parameter_list|(
name|String
name|parentType
parameter_list|)
block|{
return|return
name|ParentFieldMapper
operator|.
name|NAME
operator|+
literal|"#"
operator|+
name|parentType
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
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Mapper
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Collections
operator|.
expr|<
name|Mapper
operator|>
name|singleton
argument_list|(
name|parentJoinField
argument_list|)
operator|.
name|iterator
argument_list|()
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
operator|!
name|active
argument_list|()
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
name|builder
operator|.
name|startObject
argument_list|(
name|CONTENT_TYPE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|parentType
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeDefaults
operator|||
name|fieldType
argument_list|()
operator|.
name|eagerGlobalOrdinals
argument_list|()
operator|!=
name|defaultFieldType
operator|.
name|eagerGlobalOrdinals
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"eager_global_ordinals"
argument_list|,
name|fieldType
argument_list|()
operator|.
name|eagerGlobalOrdinals
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
name|super
operator|.
name|doMerge
argument_list|(
name|mergeWith
argument_list|,
name|updateAllTypes
argument_list|)
expr_stmt|;
name|ParentFieldMapper
name|fieldMergeWith
init|=
operator|(
name|ParentFieldMapper
operator|)
name|mergeWith
decl_stmt|;
if|if
condition|(
name|Objects
operator|.
name|equals
argument_list|(
name|parentType
argument_list|,
name|fieldMergeWith
operator|.
name|parentType
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The _parent field's type option can't be changed: ["
operator|+
name|parentType
operator|+
literal|"]->["
operator|+
name|fieldMergeWith
operator|.
name|parentType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
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
name|fieldType
argument_list|()
operator|.
name|checkCompatibility
argument_list|(
name|fieldMergeWith
operator|.
name|fieldType
argument_list|,
name|conflicts
argument_list|,
literal|true
argument_list|)
expr_stmt|;
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
literal|"Merge conflicts: "
operator|+
name|conflicts
argument_list|)
throw|;
block|}
if|if
condition|(
name|active
argument_list|()
condition|)
block|{
name|fieldType
operator|=
name|fieldMergeWith
operator|.
name|fieldType
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return Whether the _parent field is actually configured.      */
DECL|method|active
specifier|public
name|boolean
name|active
parameter_list|()
block|{
return|return
name|parentType
operator|!=
literal|null
return|;
block|}
block|}
end_class

end_unit

