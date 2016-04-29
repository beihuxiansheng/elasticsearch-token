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
name|TermContext
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
name|ConstantScoreQuery
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
name|MatchAllDocsQuery
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TypeFieldMapper
specifier|public
class|class
name|TypeFieldMapper
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
literal|"_type"
decl_stmt|;
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"_type"
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
name|TypeFieldMapper
operator|.
name|NAME
decl_stmt|;
DECL|field|FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|MappedFieldType
name|FIELD_TYPE
init|=
operator|new
name|TypeFieldType
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
name|TypeFieldMapper
argument_list|>
block|{
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
name|indexName
operator|=
name|Defaults
operator|.
name|NAME
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|TypeFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|fieldType
operator|.
name|setName
argument_list|(
name|buildFullName
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|TypeFieldMapper
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
throw|throw
operator|new
name|MapperParsingException
argument_list|(
name|NAME
operator|+
literal|" is not configurable"
argument_list|)
throw|;
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
name|TypeFieldMapper
argument_list|(
name|indexSettings
argument_list|,
name|fieldType
argument_list|)
return|;
block|}
block|}
DECL|class|TypeFieldType
specifier|static
specifier|final
class|class
name|TypeFieldType
extends|extends
name|MappedFieldType
block|{
DECL|method|TypeFieldType
specifier|public
name|TypeFieldType
parameter_list|()
block|{         }
DECL|method|TypeFieldType
specifier|protected
name|TypeFieldType
parameter_list|(
name|TypeFieldType
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
name|TypeFieldType
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
if|if
condition|(
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
return|return
operator|new
name|TypeQuery
argument_list|(
name|indexedValueForSearch
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|TypeQuery
specifier|public
specifier|static
class|class
name|TypeQuery
extends|extends
name|Query
block|{
DECL|field|type
specifier|private
specifier|final
name|BytesRef
name|type
decl_stmt|;
DECL|method|TypeQuery
specifier|public
name|TypeQuery
parameter_list|(
name|BytesRef
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
name|CONTENT_TYPE
argument_list|,
name|type
argument_list|)
decl_stmt|;
name|TermContext
name|context
init|=
name|TermContext
operator|.
name|build
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|docFreq
argument_list|()
operator|==
name|reader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
comment|// All docs have the same type.
comment|// Using a match_all query will help Lucene perform some optimizations
comment|// For instance, match_all queries as filter clauses are automatically removed
return|return
operator|new
name|MatchAllDocsQuery
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|,
name|context
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|TypeQuery
name|that
init|=
operator|(
name|TypeQuery
operator|)
name|obj
decl_stmt|;
return|return
name|type
operator|.
name|equals
argument_list|(
name|that
operator|.
name|type
argument_list|)
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
literal|31
operator|*
name|super
operator|.
name|hashCode
argument_list|()
operator|+
name|type
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"_type:"
operator|+
name|type
return|;
block|}
block|}
DECL|method|TypeFieldMapper
specifier|private
name|TypeFieldMapper
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
name|defaultFieldType
argument_list|(
name|indexSettings
argument_list|)
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
DECL|method|TypeFieldMapper
specifier|private
name|TypeFieldMapper
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
name|defaultFieldType
argument_list|(
name|indexSettings
argument_list|)
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
DECL|method|defaultFieldType
specifier|private
specifier|static
name|MappedFieldType
name|defaultFieldType
parameter_list|(
name|Settings
name|indexSettings
parameter_list|)
block|{
name|MappedFieldType
name|defaultFieldType
init|=
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|clone
argument_list|()
decl_stmt|;
name|Version
name|indexCreated
init|=
name|Version
operator|.
name|indexCreated
argument_list|(
name|indexSettings
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexCreated
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_2_1_0
argument_list|)
condition|)
block|{
name|defaultFieldType
operator|.
name|setHasDocValues
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|defaultFieldType
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
comment|// we parse in pre parse
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
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|==
name|IndexOptions
operator|.
name|NONE
operator|&&
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
name|fields
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|fieldType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|context
operator|.
name|type
argument_list|()
argument_list|,
name|fieldType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
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
name|type
argument_list|()
argument_list|)
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
comment|// do nothing here, no merging, but also no exception
block|}
block|}
end_class

end_unit

