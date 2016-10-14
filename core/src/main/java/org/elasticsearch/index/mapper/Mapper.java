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
name|ParseFieldMatcher
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
name|ToXContent
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
name|IndexAnalyzers
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
name|SimilarityProvider
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
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_class
DECL|class|Mapper
specifier|public
specifier|abstract
class|class
name|Mapper
implements|implements
name|ToXContent
implements|,
name|Iterable
argument_list|<
name|Mapper
argument_list|>
block|{
DECL|class|BuilderContext
specifier|public
specifier|static
class|class
name|BuilderContext
block|{
DECL|field|indexSettings
specifier|private
specifier|final
name|Settings
name|indexSettings
decl_stmt|;
DECL|field|contentPath
specifier|private
specifier|final
name|ContentPath
name|contentPath
decl_stmt|;
DECL|method|BuilderContext
specifier|public
name|BuilderContext
parameter_list|(
name|Settings
name|indexSettings
parameter_list|,
name|ContentPath
name|contentPath
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|indexSettings
argument_list|,
literal|"indexSettings is required"
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentPath
operator|=
name|contentPath
expr_stmt|;
name|this
operator|.
name|indexSettings
operator|=
name|indexSettings
expr_stmt|;
block|}
DECL|method|path
specifier|public
name|ContentPath
name|path
parameter_list|()
block|{
return|return
name|this
operator|.
name|contentPath
return|;
block|}
DECL|method|indexSettings
specifier|public
name|Settings
name|indexSettings
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexSettings
return|;
block|}
DECL|method|indexCreatedVersion
specifier|public
name|Version
name|indexCreatedVersion
parameter_list|()
block|{
return|return
name|Version
operator|.
name|indexCreated
argument_list|(
name|indexSettings
argument_list|)
return|;
block|}
block|}
DECL|class|Builder
specifier|public
specifier|abstract
specifier|static
class|class
name|Builder
parameter_list|<
name|T
extends|extends
name|Builder
parameter_list|,
name|Y
extends|extends
name|Mapper
parameter_list|>
block|{
DECL|field|name
specifier|public
name|String
name|name
decl_stmt|;
DECL|field|builder
specifier|protected
name|T
name|builder
decl_stmt|;
DECL|method|Builder
specifier|protected
name|Builder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
comment|/** Returns a newly built mapper. */
DECL|method|build
specifier|public
specifier|abstract
name|Y
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
function_decl|;
block|}
DECL|interface|TypeParser
specifier|public
interface|interface
name|TypeParser
block|{
DECL|class|ParserContext
class|class
name|ParserContext
block|{
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|indexAnalyzers
specifier|private
specifier|final
name|IndexAnalyzers
name|indexAnalyzers
decl_stmt|;
DECL|field|similarityLookupService
specifier|private
specifier|final
name|Function
argument_list|<
name|String
argument_list|,
name|SimilarityProvider
argument_list|>
name|similarityLookupService
decl_stmt|;
DECL|field|mapperService
specifier|private
specifier|final
name|MapperService
name|mapperService
decl_stmt|;
DECL|field|typeParsers
specifier|private
specifier|final
name|Function
argument_list|<
name|String
argument_list|,
name|TypeParser
argument_list|>
name|typeParsers
decl_stmt|;
DECL|field|indexVersionCreated
specifier|private
specifier|final
name|Version
name|indexVersionCreated
decl_stmt|;
DECL|field|parseFieldMatcher
specifier|private
specifier|final
name|ParseFieldMatcher
name|parseFieldMatcher
decl_stmt|;
DECL|field|queryShardContext
specifier|private
specifier|final
name|QueryShardContext
name|queryShardContext
decl_stmt|;
DECL|method|ParserContext
specifier|public
name|ParserContext
parameter_list|(
name|String
name|type
parameter_list|,
name|IndexAnalyzers
name|indexAnalyzers
parameter_list|,
name|Function
argument_list|<
name|String
argument_list|,
name|SimilarityProvider
argument_list|>
name|similarityLookupService
parameter_list|,
name|MapperService
name|mapperService
parameter_list|,
name|Function
argument_list|<
name|String
argument_list|,
name|TypeParser
argument_list|>
name|typeParsers
parameter_list|,
name|Version
name|indexVersionCreated
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|,
name|QueryShardContext
name|queryShardContext
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|indexAnalyzers
operator|=
name|indexAnalyzers
expr_stmt|;
name|this
operator|.
name|similarityLookupService
operator|=
name|similarityLookupService
expr_stmt|;
name|this
operator|.
name|mapperService
operator|=
name|mapperService
expr_stmt|;
name|this
operator|.
name|typeParsers
operator|=
name|typeParsers
expr_stmt|;
name|this
operator|.
name|indexVersionCreated
operator|=
name|indexVersionCreated
expr_stmt|;
name|this
operator|.
name|parseFieldMatcher
operator|=
name|parseFieldMatcher
expr_stmt|;
name|this
operator|.
name|queryShardContext
operator|=
name|queryShardContext
expr_stmt|;
block|}
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getIndexAnalyzers
specifier|public
name|IndexAnalyzers
name|getIndexAnalyzers
parameter_list|()
block|{
return|return
name|indexAnalyzers
return|;
block|}
DECL|method|getSimilarity
specifier|public
name|SimilarityProvider
name|getSimilarity
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|similarityLookupService
operator|.
name|apply
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|mapperService
specifier|public
name|MapperService
name|mapperService
parameter_list|()
block|{
return|return
name|mapperService
return|;
block|}
DECL|method|typeParser
specifier|public
name|TypeParser
name|typeParser
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|typeParsers
operator|.
name|apply
argument_list|(
name|type
argument_list|)
return|;
block|}
DECL|method|indexVersionCreated
specifier|public
name|Version
name|indexVersionCreated
parameter_list|()
block|{
return|return
name|indexVersionCreated
return|;
block|}
DECL|method|parseFieldMatcher
specifier|public
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|()
block|{
return|return
name|parseFieldMatcher
return|;
block|}
DECL|method|queryShardContext
specifier|public
name|QueryShardContext
name|queryShardContext
parameter_list|()
block|{
return|return
name|queryShardContext
return|;
block|}
DECL|method|isWithinMultiField
specifier|public
name|boolean
name|isWithinMultiField
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|typeParsers
specifier|protected
name|Function
argument_list|<
name|String
argument_list|,
name|TypeParser
argument_list|>
name|typeParsers
parameter_list|()
block|{
return|return
name|typeParsers
return|;
block|}
DECL|method|similarityLookupService
specifier|protected
name|Function
argument_list|<
name|String
argument_list|,
name|SimilarityProvider
argument_list|>
name|similarityLookupService
parameter_list|()
block|{
return|return
name|similarityLookupService
return|;
block|}
DECL|method|createMultiFieldContext
specifier|public
name|ParserContext
name|createMultiFieldContext
parameter_list|(
name|ParserContext
name|in
parameter_list|)
block|{
return|return
operator|new
name|MultiFieldParserContext
argument_list|(
name|in
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isWithinMultiField
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
return|;
block|}
DECL|class|MultiFieldParserContext
specifier|static
class|class
name|MultiFieldParserContext
extends|extends
name|ParserContext
block|{
DECL|method|MultiFieldParserContext
name|MultiFieldParserContext
parameter_list|(
name|ParserContext
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
operator|.
name|type
argument_list|()
argument_list|,
name|in
operator|.
name|indexAnalyzers
argument_list|,
name|in
operator|.
name|similarityLookupService
argument_list|()
argument_list|,
name|in
operator|.
name|mapperService
argument_list|()
argument_list|,
name|in
operator|.
name|typeParsers
argument_list|()
argument_list|,
name|in
operator|.
name|indexVersionCreated
argument_list|()
argument_list|,
name|in
operator|.
name|parseFieldMatcher
argument_list|()
argument_list|,
name|in
operator|.
name|queryShardContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|parse
name|Mapper
operator|.
name|Builder
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
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
function_decl|;
block|}
DECL|field|simpleName
specifier|private
specifier|final
name|String
name|simpleName
decl_stmt|;
DECL|method|Mapper
specifier|public
name|Mapper
parameter_list|(
name|String
name|simpleName
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|simpleName
argument_list|)
expr_stmt|;
name|this
operator|.
name|simpleName
operator|=
name|simpleName
expr_stmt|;
block|}
comment|/** Returns the simple name, which identifies this mapper against other mappers at the same level in the mappers hierarchy      * TODO: make this protected once Mapper and FieldMapper are merged together */
DECL|method|simpleName
specifier|public
specifier|final
name|String
name|simpleName
parameter_list|()
block|{
return|return
name|simpleName
return|;
block|}
comment|/** Returns the canonical name which uniquely identifies the mapper against other mappers in a type. */
DECL|method|name
specifier|public
specifier|abstract
name|String
name|name
parameter_list|()
function_decl|;
comment|/** Return the merge of {@code mergeWith} into this.      *  Both {@code this} and {@code mergeWith} will be left unmodified. */
DECL|method|merge
specifier|public
specifier|abstract
name|Mapper
name|merge
parameter_list|(
name|Mapper
name|mergeWith
parameter_list|,
name|boolean
name|updateAllTypes
parameter_list|)
function_decl|;
comment|/**      * Update the field type of this mapper. This is necessary because some mapping updates      * can modify mappings across several types. This method must return a copy of the mapper      * so that the current mapper is not modified.      */
DECL|method|updateFieldType
specifier|public
specifier|abstract
name|Mapper
name|updateFieldType
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|MappedFieldType
argument_list|>
name|fullNameToFieldType
parameter_list|)
function_decl|;
block|}
end_class

end_unit

