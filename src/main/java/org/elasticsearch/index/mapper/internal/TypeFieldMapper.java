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
name|DeletionAwareConstantScoreQuery
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
name|Filter
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
name|PrefixFilter
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
name|lucene
operator|.
name|search
operator|.
name|TermFilter
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryParseContext
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
name|index
operator|.
name|mapper
operator|.
name|MapperBuilders
operator|.
name|type
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
DECL|class|TypeFieldMapper
specifier|public
class|class
name|TypeFieldMapper
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
literal|"_type"
decl_stmt|;
DECL|field|TERM_FACTORY
specifier|public
specifier|static
specifier|final
name|Term
name|TERM_FACTORY
init|=
operator|new
name|Term
argument_list|(
name|NAME
argument_list|,
literal|""
argument_list|)
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
name|TypeFieldMapper
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
name|TypeFieldMapper
operator|.
name|NAME
decl_stmt|;
DECL|field|TYPE_FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE_FIELD_TYPE
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
name|TYPE_FIELD_TYPE
operator|.
name|setIndexed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_FIELD_TYPE
operator|.
name|setTokenized
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TYPE_FIELD_TYPE
operator|.
name|setStored
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|TYPE_FIELD_TYPE
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_FIELD_TYPE
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
expr_stmt|;
name|TYPE_FIELD_TYPE
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
name|TypeFieldMapper
argument_list|>
block|{
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
name|TYPE_FIELD_TYPE
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
DECL|method|build
specifier|public
name|TypeFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|TypeFieldMapper
argument_list|(
name|name
argument_list|,
name|indexName
argument_list|,
name|boost
argument_list|,
name|fieldType
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
name|TypeFieldMapper
operator|.
name|Builder
name|builder
init|=
name|type
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
block|}
DECL|method|TypeFieldMapper
specifier|public
name|TypeFieldMapper
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
name|INDEX_NAME
argument_list|)
expr_stmt|;
block|}
DECL|method|TypeFieldMapper
specifier|protected
name|TypeFieldMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|indexName
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
name|Defaults
operator|.
name|TYPE_FIELD_TYPE
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|TypeFieldMapper
specifier|public
name|TypeFieldMapper
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
name|provider
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
name|Lucene
operator|.
name|KEYWORD_ANALYZER
argument_list|,
name|Lucene
operator|.
name|KEYWORD_ANALYZER
argument_list|,
name|provider
argument_list|)
expr_stmt|;
block|}
DECL|method|value
specifier|public
name|String
name|value
parameter_list|(
name|Document
name|document
parameter_list|)
block|{
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|document
operator|.
name|getField
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
annotation|@
name|Override
DECL|method|value
specifier|public
name|String
name|value
parameter_list|(
name|Field
name|field
parameter_list|)
block|{
return|return
name|field
operator|.
name|stringValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|valueFromString
specifier|public
name|String
name|valueFromString
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
DECL|method|valueAsString
specifier|public
name|String
name|valueAsString
parameter_list|(
name|Field
name|field
parameter_list|)
block|{
return|return
name|value
argument_list|(
name|field
argument_list|)
return|;
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
DECL|method|term
specifier|public
name|Term
name|term
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|names
argument_list|()
operator|.
name|createIndexNameTerm
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldFilter
specifier|public
name|Filter
name|fieldFilter
parameter_list|(
name|String
name|value
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
block|{
if|if
condition|(
operator|!
name|indexed
argument_list|()
condition|)
block|{
return|return
operator|new
name|PrefixFilter
argument_list|(
operator|new
name|Term
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|,
name|Uid
operator|.
name|typePrefix
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|new
name|TermFilter
argument_list|(
name|names
argument_list|()
operator|.
name|createIndexNameTerm
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldQuery
specifier|public
name|Query
name|fieldQuery
parameter_list|(
name|String
name|value
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|DeletionAwareConstantScoreQuery
argument_list|(
name|context
operator|.
name|cacheFilter
argument_list|(
name|fieldFilter
argument_list|(
name|value
argument_list|,
name|context
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|useFieldQueryWithQueryString
specifier|public
name|boolean
name|useFieldQueryWithQueryString
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
comment|// we parse in pre parse
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
name|indexed
argument_list|()
operator|&&
operator|!
name|stored
argument_list|()
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
name|context
operator|.
name|type
argument_list|()
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
comment|// if all are defaults, no sense to write it at all
if|if
condition|(
name|stored
argument_list|()
operator|==
name|Defaults
operator|.
name|TYPE_FIELD_TYPE
operator|.
name|stored
argument_list|()
operator|&&
name|indexed
argument_list|()
operator|==
name|Defaults
operator|.
name|TYPE_FIELD_TYPE
operator|.
name|indexed
argument_list|()
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
name|CONTENT_TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|stored
argument_list|()
operator|!=
name|Defaults
operator|.
name|TYPE_FIELD_TYPE
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
name|stored
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexed
argument_list|()
operator|!=
name|Defaults
operator|.
name|TYPE_FIELD_TYPE
operator|.
name|indexed
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
name|indexed
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
comment|// do nothing here, no merging, but also no exception
block|}
block|}
end_class

end_unit

