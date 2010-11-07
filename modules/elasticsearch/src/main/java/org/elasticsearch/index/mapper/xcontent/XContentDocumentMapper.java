begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|xcontent
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
name|analysis
operator|.
name|Analyzer
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
name|search
operator|.
name|Filter
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
name|Preconditions
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
name|ImmutableMap
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
name|CompressedString
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
name|thread
operator|.
name|ThreadLocals
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
name|mapper
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|XContentDocumentMapper
specifier|public
class|class
name|XContentDocumentMapper
implements|implements
name|DocumentMapper
implements|,
name|ToXContent
block|{
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|uidFieldMapper
specifier|private
name|UidFieldMapper
name|uidFieldMapper
init|=
operator|new
name|UidFieldMapper
argument_list|()
decl_stmt|;
DECL|field|idFieldMapper
specifier|private
name|IdFieldMapper
name|idFieldMapper
init|=
operator|new
name|IdFieldMapper
argument_list|()
decl_stmt|;
DECL|field|typeFieldMapper
specifier|private
name|TypeFieldMapper
name|typeFieldMapper
init|=
operator|new
name|TypeFieldMapper
argument_list|()
decl_stmt|;
DECL|field|indexFieldMapper
specifier|private
name|IndexFieldMapper
name|indexFieldMapper
init|=
operator|new
name|IndexFieldMapper
argument_list|()
decl_stmt|;
DECL|field|sourceFieldMapper
specifier|private
name|SourceFieldMapper
name|sourceFieldMapper
init|=
operator|new
name|SourceFieldMapper
argument_list|()
decl_stmt|;
DECL|field|boostFieldMapper
specifier|private
name|BoostFieldMapper
name|boostFieldMapper
init|=
operator|new
name|BoostFieldMapper
argument_list|()
decl_stmt|;
DECL|field|allFieldMapper
specifier|private
name|AllFieldMapper
name|allFieldMapper
init|=
operator|new
name|AllFieldMapper
argument_list|()
decl_stmt|;
DECL|field|indexAnalyzer
specifier|private
name|NamedAnalyzer
name|indexAnalyzer
decl_stmt|;
DECL|field|searchAnalyzer
specifier|private
name|NamedAnalyzer
name|searchAnalyzer
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|String
name|index
decl_stmt|;
DECL|field|rootObjectMapper
specifier|private
specifier|final
name|RootObjectMapper
name|rootObjectMapper
decl_stmt|;
DECL|field|attributes
specifier|private
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|field|builderContext
specifier|private
name|XContentMapper
operator|.
name|BuilderContext
name|builderContext
init|=
operator|new
name|XContentMapper
operator|.
name|BuilderContext
argument_list|(
operator|new
name|ContentPath
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|index
parameter_list|,
name|RootObjectMapper
operator|.
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|rootObjectMapper
operator|=
name|builder
operator|.
name|build
argument_list|(
name|builderContext
argument_list|)
expr_stmt|;
block|}
DECL|method|attributes
specifier|public
name|Builder
name|attributes
parameter_list|(
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
parameter_list|)
block|{
name|this
operator|.
name|attributes
operator|=
name|attributes
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|sourceField
specifier|public
name|Builder
name|sourceField
parameter_list|(
name|SourceFieldMapper
operator|.
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|sourceFieldMapper
operator|=
name|builder
operator|.
name|build
argument_list|(
name|builderContext
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|idField
specifier|public
name|Builder
name|idField
parameter_list|(
name|IdFieldMapper
operator|.
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|idFieldMapper
operator|=
name|builder
operator|.
name|build
argument_list|(
name|builderContext
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|uidField
specifier|public
name|Builder
name|uidField
parameter_list|(
name|UidFieldMapper
operator|.
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|uidFieldMapper
operator|=
name|builder
operator|.
name|build
argument_list|(
name|builderContext
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|typeField
specifier|public
name|Builder
name|typeField
parameter_list|(
name|TypeFieldMapper
operator|.
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|typeFieldMapper
operator|=
name|builder
operator|.
name|build
argument_list|(
name|builderContext
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|indexField
specifier|public
name|Builder
name|indexField
parameter_list|(
name|IndexFieldMapper
operator|.
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|indexFieldMapper
operator|=
name|builder
operator|.
name|build
argument_list|(
name|builderContext
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|boostField
specifier|public
name|Builder
name|boostField
parameter_list|(
name|BoostFieldMapper
operator|.
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|boostFieldMapper
operator|=
name|builder
operator|.
name|build
argument_list|(
name|builderContext
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|allField
specifier|public
name|Builder
name|allField
parameter_list|(
name|AllFieldMapper
operator|.
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|allFieldMapper
operator|=
name|builder
operator|.
name|build
argument_list|(
name|builderContext
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|indexAnalyzer
specifier|public
name|Builder
name|indexAnalyzer
parameter_list|(
name|NamedAnalyzer
name|indexAnalyzer
parameter_list|)
block|{
name|this
operator|.
name|indexAnalyzer
operator|=
name|indexAnalyzer
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|hasIndexAnalyzer
specifier|public
name|boolean
name|hasIndexAnalyzer
parameter_list|()
block|{
return|return
name|indexAnalyzer
operator|!=
literal|null
return|;
block|}
DECL|method|searchAnalyzer
specifier|public
name|Builder
name|searchAnalyzer
parameter_list|(
name|NamedAnalyzer
name|searchAnalyzer
parameter_list|)
block|{
name|this
operator|.
name|searchAnalyzer
operator|=
name|searchAnalyzer
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|hasSearchAnalyzer
specifier|public
name|boolean
name|hasSearchAnalyzer
parameter_list|()
block|{
return|return
name|searchAnalyzer
operator|!=
literal|null
return|;
block|}
DECL|method|build
specifier|public
name|XContentDocumentMapper
name|build
parameter_list|(
name|XContentDocumentMapperParser
name|docMapperParser
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|rootObjectMapper
argument_list|,
literal|"Mapper builder must have the root object mapper set"
argument_list|)
expr_stmt|;
return|return
operator|new
name|XContentDocumentMapper
argument_list|(
name|index
argument_list|,
name|docMapperParser
argument_list|,
name|rootObjectMapper
argument_list|,
name|attributes
argument_list|,
name|uidFieldMapper
argument_list|,
name|idFieldMapper
argument_list|,
name|typeFieldMapper
argument_list|,
name|indexFieldMapper
argument_list|,
name|sourceFieldMapper
argument_list|,
name|allFieldMapper
argument_list|,
name|indexAnalyzer
argument_list|,
name|searchAnalyzer
argument_list|,
name|boostFieldMapper
argument_list|)
return|;
block|}
block|}
DECL|field|cache
specifier|private
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|ParseContext
argument_list|>
argument_list|>
name|cache
init|=
operator|new
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|ParseContext
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|ParseContext
argument_list|>
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|ParseContext
argument_list|>
argument_list|(
operator|new
name|ParseContext
argument_list|(
name|index
argument_list|,
name|docMapperParser
argument_list|,
name|XContentDocumentMapper
operator|.
name|this
argument_list|,
operator|new
name|ContentPath
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|String
name|index
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|docMapperParser
specifier|private
specifier|final
name|XContentDocumentMapperParser
name|docMapperParser
decl_stmt|;
DECL|field|attributes
specifier|private
specifier|volatile
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
decl_stmt|;
DECL|field|mappingSource
specifier|private
specifier|volatile
name|CompressedString
name|mappingSource
decl_stmt|;
DECL|field|uidFieldMapper
specifier|private
specifier|final
name|UidFieldMapper
name|uidFieldMapper
decl_stmt|;
DECL|field|idFieldMapper
specifier|private
specifier|final
name|IdFieldMapper
name|idFieldMapper
decl_stmt|;
DECL|field|typeFieldMapper
specifier|private
specifier|final
name|TypeFieldMapper
name|typeFieldMapper
decl_stmt|;
DECL|field|indexFieldMapper
specifier|private
specifier|final
name|IndexFieldMapper
name|indexFieldMapper
decl_stmt|;
DECL|field|sourceFieldMapper
specifier|private
specifier|final
name|SourceFieldMapper
name|sourceFieldMapper
decl_stmt|;
DECL|field|boostFieldMapper
specifier|private
specifier|final
name|BoostFieldMapper
name|boostFieldMapper
decl_stmt|;
DECL|field|allFieldMapper
specifier|private
specifier|final
name|AllFieldMapper
name|allFieldMapper
decl_stmt|;
DECL|field|rootObjectMapper
specifier|private
specifier|final
name|RootObjectMapper
name|rootObjectMapper
decl_stmt|;
DECL|field|indexAnalyzer
specifier|private
specifier|final
name|NamedAnalyzer
name|indexAnalyzer
decl_stmt|;
DECL|field|searchAnalyzer
specifier|private
specifier|final
name|NamedAnalyzer
name|searchAnalyzer
decl_stmt|;
DECL|field|fieldMappers
specifier|private
specifier|volatile
name|DocumentFieldMappers
name|fieldMappers
decl_stmt|;
DECL|field|fieldMapperListeners
specifier|private
specifier|final
name|List
argument_list|<
name|FieldMapperListener
argument_list|>
name|fieldMapperListeners
init|=
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|typeFilter
specifier|private
specifier|final
name|Filter
name|typeFilter
decl_stmt|;
DECL|field|mutex
specifier|private
specifier|final
name|Object
name|mutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|method|XContentDocumentMapper
specifier|public
name|XContentDocumentMapper
parameter_list|(
name|String
name|index
parameter_list|,
name|XContentDocumentMapperParser
name|docMapperParser
parameter_list|,
name|RootObjectMapper
name|rootObjectMapper
parameter_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
parameter_list|,
name|UidFieldMapper
name|uidFieldMapper
parameter_list|,
name|IdFieldMapper
name|idFieldMapper
parameter_list|,
name|TypeFieldMapper
name|typeFieldMapper
parameter_list|,
name|IndexFieldMapper
name|indexFieldMapper
parameter_list|,
name|SourceFieldMapper
name|sourceFieldMapper
parameter_list|,
name|AllFieldMapper
name|allFieldMapper
parameter_list|,
name|NamedAnalyzer
name|indexAnalyzer
parameter_list|,
name|NamedAnalyzer
name|searchAnalyzer
parameter_list|,
annotation|@
name|Nullable
name|BoostFieldMapper
name|boostFieldMapper
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|rootObjectMapper
operator|.
name|name
argument_list|()
expr_stmt|;
name|this
operator|.
name|docMapperParser
operator|=
name|docMapperParser
expr_stmt|;
name|this
operator|.
name|attributes
operator|=
name|attributes
expr_stmt|;
name|this
operator|.
name|rootObjectMapper
operator|=
name|rootObjectMapper
expr_stmt|;
name|this
operator|.
name|uidFieldMapper
operator|=
name|uidFieldMapper
expr_stmt|;
name|this
operator|.
name|idFieldMapper
operator|=
name|idFieldMapper
expr_stmt|;
name|this
operator|.
name|typeFieldMapper
operator|=
name|typeFieldMapper
expr_stmt|;
name|this
operator|.
name|indexFieldMapper
operator|=
name|indexFieldMapper
expr_stmt|;
name|this
operator|.
name|sourceFieldMapper
operator|=
name|sourceFieldMapper
expr_stmt|;
name|this
operator|.
name|allFieldMapper
operator|=
name|allFieldMapper
expr_stmt|;
name|this
operator|.
name|boostFieldMapper
operator|=
name|boostFieldMapper
expr_stmt|;
name|this
operator|.
name|indexAnalyzer
operator|=
name|indexAnalyzer
expr_stmt|;
name|this
operator|.
name|searchAnalyzer
operator|=
name|searchAnalyzer
expr_stmt|;
name|this
operator|.
name|typeFilter
operator|=
operator|new
name|TermFilter
argument_list|(
name|typeMapper
argument_list|()
operator|.
name|term
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
comment|// if we are not enabling all, set it to false on the root object, (and on all the rest...)
if|if
condition|(
operator|!
name|allFieldMapper
operator|.
name|enabled
argument_list|()
condition|)
block|{
name|this
operator|.
name|rootObjectMapper
operator|.
name|includeInAll
argument_list|(
name|allFieldMapper
operator|.
name|enabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rootObjectMapper
operator|.
name|putMapper
argument_list|(
name|idFieldMapper
argument_list|)
expr_stmt|;
if|if
condition|(
name|boostFieldMapper
operator|!=
literal|null
condition|)
block|{
name|rootObjectMapper
operator|.
name|putMapper
argument_list|(
name|boostFieldMapper
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|FieldMapper
argument_list|>
name|tempFieldMappers
init|=
name|newArrayList
argument_list|()
decl_stmt|;
comment|// add the basic ones
if|if
condition|(
name|indexFieldMapper
operator|.
name|enabled
argument_list|()
condition|)
block|{
name|tempFieldMappers
operator|.
name|add
argument_list|(
name|indexFieldMapper
argument_list|)
expr_stmt|;
block|}
name|tempFieldMappers
operator|.
name|add
argument_list|(
name|typeFieldMapper
argument_list|)
expr_stmt|;
name|tempFieldMappers
operator|.
name|add
argument_list|(
name|sourceFieldMapper
argument_list|)
expr_stmt|;
name|tempFieldMappers
operator|.
name|add
argument_list|(
name|uidFieldMapper
argument_list|)
expr_stmt|;
name|tempFieldMappers
operator|.
name|add
argument_list|(
name|allFieldMapper
argument_list|)
expr_stmt|;
if|if
condition|(
name|boostFieldMapper
operator|!=
literal|null
condition|)
block|{
name|tempFieldMappers
operator|.
name|add
argument_list|(
name|boostFieldMapper
argument_list|)
expr_stmt|;
block|}
comment|// now traverse and get all the statically defined ones
name|rootObjectMapper
operator|.
name|traverse
argument_list|(
operator|new
name|FieldMapperListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|fieldMapper
parameter_list|(
name|FieldMapper
name|fieldMapper
parameter_list|)
block|{
name|tempFieldMappers
operator|.
name|add
argument_list|(
name|fieldMapper
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldMappers
operator|=
operator|new
name|DocumentFieldMappers
argument_list|(
name|this
argument_list|,
name|tempFieldMappers
argument_list|)
expr_stmt|;
name|refreshSource
argument_list|()
expr_stmt|;
block|}
DECL|method|type
annotation|@
name|Override
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
return|;
block|}
DECL|method|attributes
annotation|@
name|Override
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
parameter_list|()
block|{
return|return
name|this
operator|.
name|attributes
return|;
block|}
DECL|method|mappingSource
annotation|@
name|Override
specifier|public
name|CompressedString
name|mappingSource
parameter_list|()
block|{
return|return
name|this
operator|.
name|mappingSource
return|;
block|}
DECL|method|root
specifier|public
name|RootObjectMapper
name|root
parameter_list|()
block|{
return|return
name|this
operator|.
name|rootObjectMapper
return|;
block|}
DECL|method|uidMapper
annotation|@
name|Override
specifier|public
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|UidFieldMapper
name|uidMapper
parameter_list|()
block|{
return|return
name|this
operator|.
name|uidFieldMapper
return|;
block|}
DECL|method|idMapper
annotation|@
name|Override
specifier|public
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|IdFieldMapper
name|idMapper
parameter_list|()
block|{
return|return
name|this
operator|.
name|idFieldMapper
return|;
block|}
DECL|method|indexMapper
annotation|@
name|Override
specifier|public
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|IndexFieldMapper
name|indexMapper
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexFieldMapper
return|;
block|}
DECL|method|typeMapper
annotation|@
name|Override
specifier|public
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|TypeFieldMapper
name|typeMapper
parameter_list|()
block|{
return|return
name|this
operator|.
name|typeFieldMapper
return|;
block|}
DECL|method|sourceMapper
annotation|@
name|Override
specifier|public
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|SourceFieldMapper
name|sourceMapper
parameter_list|()
block|{
return|return
name|this
operator|.
name|sourceFieldMapper
return|;
block|}
DECL|method|boostMapper
annotation|@
name|Override
specifier|public
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|BoostFieldMapper
name|boostMapper
parameter_list|()
block|{
return|return
name|this
operator|.
name|boostFieldMapper
return|;
block|}
DECL|method|allFieldMapper
annotation|@
name|Override
specifier|public
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|AllFieldMapper
name|allFieldMapper
parameter_list|()
block|{
return|return
name|this
operator|.
name|allFieldMapper
return|;
block|}
DECL|method|indexAnalyzer
annotation|@
name|Override
specifier|public
name|Analyzer
name|indexAnalyzer
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexAnalyzer
return|;
block|}
DECL|method|searchAnalyzer
annotation|@
name|Override
specifier|public
name|Analyzer
name|searchAnalyzer
parameter_list|()
block|{
return|return
name|this
operator|.
name|searchAnalyzer
return|;
block|}
DECL|method|typeFilter
annotation|@
name|Override
specifier|public
name|Filter
name|typeFilter
parameter_list|()
block|{
return|return
name|this
operator|.
name|typeFilter
return|;
block|}
DECL|method|mappers
annotation|@
name|Override
specifier|public
name|DocumentFieldMappers
name|mappers
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldMappers
return|;
block|}
DECL|method|parse
annotation|@
name|Override
specifier|public
name|ParsedDocument
name|parse
parameter_list|(
name|byte
index|[]
name|source
parameter_list|)
block|{
return|return
name|parse
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|source
argument_list|)
return|;
block|}
DECL|method|parse
annotation|@
name|Override
specifier|public
name|ParsedDocument
name|parse
parameter_list|(
annotation|@
name|Nullable
name|String
name|type
parameter_list|,
annotation|@
name|Nullable
name|String
name|id
parameter_list|,
name|byte
index|[]
name|source
parameter_list|)
throws|throws
name|MapperParsingException
block|{
return|return
name|parse
argument_list|(
name|type
argument_list|,
name|id
argument_list|,
name|source
argument_list|,
name|ParseListener
operator|.
name|EMPTY
argument_list|)
return|;
block|}
DECL|method|parse
annotation|@
name|Override
specifier|public
name|ParsedDocument
name|parse
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|byte
index|[]
name|source
parameter_list|,
name|ParseListener
name|listener
parameter_list|)
block|{
name|ParseContext
name|context
init|=
name|cache
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
operator|&&
operator|!
name|type
operator|.
name|equals
argument_list|(
name|this
operator|.
name|type
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Type mismatch, provide type ["
operator|+
name|type
operator|+
literal|"] but mapper is of type ["
operator|+
name|this
operator|.
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|type
operator|=
name|this
operator|.
name|type
expr_stmt|;
name|XContentParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|source
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|context
operator|.
name|reset
argument_list|(
name|parser
argument_list|,
operator|new
name|Document
argument_list|()
argument_list|,
name|type
argument_list|,
name|source
argument_list|,
name|listener
argument_list|)
expr_stmt|;
comment|// will result in START_OBJECT
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
throw|throw
operator|new
name|MapperException
argument_list|(
literal|"Malformed content, must start with an object"
argument_list|)
throw|;
block|}
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
name|MapperException
argument_list|(
literal|"Malformed content, after first object, either the type field or the actual properties should exist"
argument_list|)
throw|;
block|}
if|if
condition|(
name|parser
operator|.
name|currentName
argument_list|()
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// first field is the same as the type, this might be because the type is provided, and the object exists within it
comment|// or because there is a valid field that by chance is named as the type
comment|// Note, in this case, we only handle plain value types, an object type will be analyzed as if it was the type itself
comment|// and other same level fields will be ignored
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
comment|// commented out, allow for same type with START_OBJECT, we do our best to handle it except for the above corner case
comment|//                if (token != XContentParser.Token.START_OBJECT) {
comment|//                    throw new MapperException("Malformed content, a field with the same name as the type must be an object with the properties/fields within it");
comment|//                }
block|}
if|if
condition|(
name|sourceFieldMapper
operator|.
name|enabled
argument_list|()
condition|)
block|{
name|sourceFieldMapper
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|// set the id if we have it so we can validate it later on, also, add the uid if we can
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|id
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|uidFieldMapper
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|typeFieldMapper
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|indexFieldMapper
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|rootObjectMapper
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// if we did not get the id, we need to parse the uid into the document now, after it was added
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
name|uidFieldMapper
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|.
name|parsedIdState
argument_list|()
operator|!=
name|ParseContext
operator|.
name|ParsedIdState
operator|.
name|PARSED
condition|)
block|{
comment|// mark it as external, so we can parse it
name|context
operator|.
name|parsedId
argument_list|(
name|ParseContext
operator|.
name|ParsedIdState
operator|.
name|EXTERNAL
argument_list|)
expr_stmt|;
name|idFieldMapper
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|allFieldMapper
operator|.
name|parse
argument_list|(
name|context
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
name|MapperParsingException
argument_list|(
literal|"Failed to parse"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ParsedDocument
argument_list|(
name|context
operator|.
name|uid
argument_list|()
argument_list|,
name|context
operator|.
name|id
argument_list|()
argument_list|,
name|context
operator|.
name|type
argument_list|()
argument_list|,
name|context
operator|.
name|doc
argument_list|()
argument_list|,
name|source
argument_list|,
name|context
operator|.
name|mappersAdded
argument_list|()
argument_list|)
return|;
block|}
DECL|method|addFieldMapper
name|void
name|addFieldMapper
parameter_list|(
name|FieldMapper
name|fieldMapper
parameter_list|)
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|fieldMappers
operator|=
name|fieldMappers
operator|.
name|concat
argument_list|(
name|this
argument_list|,
name|fieldMapper
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldMapperListener
name|listener
range|:
name|fieldMapperListeners
control|)
block|{
name|listener
operator|.
name|fieldMapper
argument_list|(
name|fieldMapper
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addFieldMapperListener
annotation|@
name|Override
specifier|public
name|void
name|addFieldMapperListener
parameter_list|(
name|FieldMapperListener
name|fieldMapperListener
parameter_list|,
name|boolean
name|includeExisting
parameter_list|)
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|fieldMapperListeners
operator|.
name|add
argument_list|(
name|fieldMapperListener
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeExisting
condition|)
block|{
if|if
condition|(
name|indexFieldMapper
operator|.
name|enabled
argument_list|()
condition|)
block|{
name|fieldMapperListener
operator|.
name|fieldMapper
argument_list|(
name|indexFieldMapper
argument_list|)
expr_stmt|;
block|}
name|fieldMapperListener
operator|.
name|fieldMapper
argument_list|(
name|sourceFieldMapper
argument_list|)
expr_stmt|;
name|fieldMapperListener
operator|.
name|fieldMapper
argument_list|(
name|typeFieldMapper
argument_list|)
expr_stmt|;
name|fieldMapperListener
operator|.
name|fieldMapper
argument_list|(
name|idFieldMapper
argument_list|)
expr_stmt|;
name|fieldMapperListener
operator|.
name|fieldMapper
argument_list|(
name|uidFieldMapper
argument_list|)
expr_stmt|;
name|fieldMapperListener
operator|.
name|fieldMapper
argument_list|(
name|allFieldMapper
argument_list|)
expr_stmt|;
name|rootObjectMapper
operator|.
name|traverse
argument_list|(
name|fieldMapperListener
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|merge
annotation|@
name|Override
specifier|public
specifier|synchronized
name|MergeResult
name|merge
parameter_list|(
name|DocumentMapper
name|mergeWith
parameter_list|,
name|MergeFlags
name|mergeFlags
parameter_list|)
block|{
name|XContentDocumentMapper
name|xContentMergeWith
init|=
operator|(
name|XContentDocumentMapper
operator|)
name|mergeWith
decl_stmt|;
name|MergeContext
name|mergeContext
init|=
operator|new
name|MergeContext
argument_list|(
name|this
argument_list|,
name|mergeFlags
argument_list|)
decl_stmt|;
name|rootObjectMapper
operator|.
name|merge
argument_list|(
name|xContentMergeWith
operator|.
name|rootObjectMapper
argument_list|,
name|mergeContext
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|mergeFlags
operator|.
name|simulate
argument_list|()
condition|)
block|{
comment|// let the merge with attributes to override the attributes
name|attributes
operator|=
name|mergeWith
operator|.
name|attributes
argument_list|()
expr_stmt|;
comment|// update the source of the merged one
name|refreshSource
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|MergeResult
argument_list|(
name|mergeContext
operator|.
name|buildConflicts
argument_list|()
argument_list|)
return|;
block|}
DECL|method|refreshSource
annotation|@
name|Override
specifier|public
name|void
name|refreshSource
parameter_list|()
throws|throws
name|FailedToGenerateSourceMapperException
block|{
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|this
operator|.
name|mappingSource
operator|=
operator|new
name|CompressedString
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|FailedToGenerateSourceMapperException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|toXContent
annotation|@
name|Override
specifier|public
name|void
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
name|rootObjectMapper
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|,
operator|new
name|ToXContent
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
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
name|indexAnalyzer
operator|!=
literal|null
operator|&&
name|searchAnalyzer
operator|!=
literal|null
operator|&&
name|indexAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|searchAnalyzer
operator|.
name|name
argument_list|()
argument_list|)
operator|&&
operator|!
name|indexAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|indexAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
literal|"default"
argument_list|)
condition|)
block|{
comment|// same analyzers, output it once
name|builder
operator|.
name|field
argument_list|(
literal|"analyzer"
argument_list|,
name|indexAnalyzer
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|indexAnalyzer
operator|!=
literal|null
operator|&&
operator|!
name|indexAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|indexAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
literal|"default"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"index_analyzer"
argument_list|,
name|indexAnalyzer
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|searchAnalyzer
operator|!=
literal|null
operator|&&
operator|!
name|searchAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|searchAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
literal|"default"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"search_analyzer"
argument_list|,
name|searchAnalyzer
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|attributes
operator|!=
literal|null
operator|&&
operator|!
name|attributes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_attributes"
argument_list|,
name|attributes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// no need to pass here id and boost, since they are added to the root object mapper
comment|// in the constructor
block|}
argument_list|,
name|indexFieldMapper
argument_list|,
name|typeFieldMapper
argument_list|,
name|allFieldMapper
argument_list|,
name|sourceFieldMapper
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

