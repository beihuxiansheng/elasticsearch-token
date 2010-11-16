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
name|collect
operator|.
name|MapBuilder
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
name|Maps
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
name|settings
operator|.
name|ImmutableSettings
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
name|XContentParser
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
name|AbstractIndexComponent
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
name|Index
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
name|AnalysisService
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
name|DocumentMapperParser
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
name|MapperService
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
name|xcontent
operator|.
name|geo
operator|.
name|GeoPointFieldMapper
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
name|xcontent
operator|.
name|ip
operator|.
name|IpFieldMapper
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
name|settings
operator|.
name|IndexSettings
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
name|*
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
name|xcontent
operator|.
name|XContentMapperBuilders
operator|.
name|*
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
name|xcontent
operator|.
name|XContentTypeParsers
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|XContentDocumentMapperParser
specifier|public
class|class
name|XContentDocumentMapperParser
extends|extends
name|AbstractIndexComponent
implements|implements
name|DocumentMapperParser
block|{
DECL|field|analysisService
specifier|final
name|AnalysisService
name|analysisService
decl_stmt|;
DECL|field|rootObjectTypeParser
specifier|private
specifier|final
name|RootObjectMapper
operator|.
name|TypeParser
name|rootObjectTypeParser
init|=
operator|new
name|RootObjectMapper
operator|.
name|TypeParser
argument_list|()
decl_stmt|;
DECL|field|typeParsersMutex
specifier|private
specifier|final
name|Object
name|typeParsersMutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|typeParsers
specifier|private
specifier|volatile
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|XContentMapper
operator|.
name|TypeParser
argument_list|>
name|typeParsers
decl_stmt|;
DECL|method|XContentDocumentMapperParser
specifier|public
name|XContentDocumentMapperParser
parameter_list|(
name|Index
name|index
parameter_list|,
name|AnalysisService
name|analysisService
parameter_list|)
block|{
name|this
argument_list|(
name|index
argument_list|,
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|,
name|analysisService
argument_list|)
expr_stmt|;
block|}
DECL|method|XContentDocumentMapperParser
specifier|public
name|XContentDocumentMapperParser
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|AnalysisService
name|analysisService
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|analysisService
operator|=
name|analysisService
expr_stmt|;
name|typeParsers
operator|=
operator|new
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|XContentMapper
operator|.
name|TypeParser
argument_list|>
argument_list|()
operator|.
name|put
argument_list|(
name|ShortFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|ShortFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|IntegerFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|IntegerFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|LongFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|LongFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|FloatFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|FloatFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|DoubleFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|DoubleFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|BooleanFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|BooleanFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|BinaryFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|BinaryFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|DateFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|DateFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|IpFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|IpFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|StringFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|StringFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|ObjectMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|ObjectMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|MultiFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|MultiFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|GeoPointFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|GeoPointFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
DECL|method|putTypeParser
specifier|public
name|void
name|putTypeParser
parameter_list|(
name|String
name|type
parameter_list|,
name|XContentMapper
operator|.
name|TypeParser
name|typeParser
parameter_list|)
block|{
synchronized|synchronized
init|(
name|typeParsersMutex
init|)
block|{
name|typeParsers
operator|=
operator|new
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|XContentMapper
operator|.
name|TypeParser
argument_list|>
argument_list|()
operator|.
name|putAll
argument_list|(
name|typeParsers
argument_list|)
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|typeParser
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|parserContext
specifier|public
name|XContentMapper
operator|.
name|TypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|()
block|{
return|return
operator|new
name|XContentMapper
operator|.
name|TypeParser
operator|.
name|ParserContext
argument_list|(
name|analysisService
argument_list|,
name|typeParsers
argument_list|)
return|;
block|}
DECL|method|parse
annotation|@
name|Override
specifier|public
name|XContentDocumentMapper
name|parse
parameter_list|(
name|String
name|source
parameter_list|)
throws|throws
name|MapperParsingException
block|{
return|return
name|parse
argument_list|(
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
name|XContentDocumentMapper
name|parse
parameter_list|(
annotation|@
name|Nullable
name|String
name|type
parameter_list|,
name|String
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
name|source
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|parse
annotation|@
name|Override
specifier|public
name|XContentDocumentMapper
name|parse
parameter_list|(
annotation|@
name|Nullable
name|String
name|type
parameter_list|,
name|String
name|source
parameter_list|,
name|String
name|defaultSource
parameter_list|)
throws|throws
name|MapperParsingException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mapping
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
name|Tuple
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|t
init|=
name|extractMapping
argument_list|(
name|type
argument_list|,
name|source
argument_list|)
decl_stmt|;
name|type
operator|=
name|t
operator|.
name|v1
argument_list|()
expr_stmt|;
name|mapping
operator|=
name|t
operator|.
name|v2
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|mapping
operator|==
literal|null
condition|)
block|{
name|mapping
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Failed to derive type"
argument_list|)
throw|;
block|}
if|if
condition|(
name|defaultSource
operator|!=
literal|null
condition|)
block|{
name|Tuple
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|t
init|=
name|extractMapping
argument_list|(
name|MapperService
operator|.
name|DEFAULT_MAPPING
argument_list|,
name|defaultSource
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|v2
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|XContentHelper
operator|.
name|mergeDefaults
argument_list|(
name|mapping
argument_list|,
name|t
operator|.
name|v2
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|XContentMapper
operator|.
name|TypeParser
operator|.
name|ParserContext
name|parserContext
init|=
operator|new
name|XContentMapper
operator|.
name|TypeParser
operator|.
name|ParserContext
argument_list|(
name|analysisService
argument_list|,
name|typeParsers
argument_list|)
decl_stmt|;
name|XContentDocumentMapper
operator|.
name|Builder
name|docBuilder
init|=
name|doc
argument_list|(
name|index
operator|.
name|name
argument_list|()
argument_list|,
operator|(
name|RootObjectMapper
operator|.
name|Builder
operator|)
name|rootObjectTypeParser
operator|.
name|parse
argument_list|(
name|type
argument_list|,
name|mapping
argument_list|,
name|parserContext
argument_list|)
argument_list|)
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
name|mapping
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
name|SourceFieldMapper
operator|.
name|CONTENT_TYPE
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"sourceField"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|docBuilder
operator|.
name|sourceField
argument_list|(
name|parseSourceField
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|fieldNode
argument_list|,
name|parserContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|IdFieldMapper
operator|.
name|CONTENT_TYPE
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"idField"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|docBuilder
operator|.
name|idField
argument_list|(
name|parseIdField
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|fieldNode
argument_list|,
name|parserContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|IndexFieldMapper
operator|.
name|CONTENT_TYPE
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"indexField"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|docBuilder
operator|.
name|indexField
argument_list|(
name|parseIndexField
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|fieldNode
argument_list|,
name|parserContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|TypeFieldMapper
operator|.
name|CONTENT_TYPE
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"typeField"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|docBuilder
operator|.
name|typeField
argument_list|(
name|parseTypeField
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|fieldNode
argument_list|,
name|parserContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|UidFieldMapper
operator|.
name|CONTENT_TYPE
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"uidField"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|docBuilder
operator|.
name|uidField
argument_list|(
name|parseUidField
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|fieldNode
argument_list|,
name|parserContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|RoutingFieldMapper
operator|.
name|CONTENT_TYPE
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|docBuilder
operator|.
name|routingField
argument_list|(
name|parseRoutingField
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|fieldNode
argument_list|,
name|parserContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|BoostFieldMapper
operator|.
name|CONTENT_TYPE
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"boostField"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|docBuilder
operator|.
name|boostField
argument_list|(
name|parseBoostField
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|fieldNode
argument_list|,
name|parserContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|AllFieldMapper
operator|.
name|CONTENT_TYPE
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"allField"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|docBuilder
operator|.
name|allField
argument_list|(
name|parseAllField
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|fieldNode
argument_list|,
name|parserContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|AnalyzerMapper
operator|.
name|CONTENT_TYPE
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|docBuilder
operator|.
name|analyzerField
argument_list|(
name|parseAnalyzerField
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|fieldNode
argument_list|,
name|parserContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"index_analyzer"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|docBuilder
operator|.
name|indexAnalyzer
argument_list|(
name|analysisService
operator|.
name|analyzer
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"search_analyzer"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|docBuilder
operator|.
name|searchAnalyzer
argument_list|(
name|analysisService
operator|.
name|analyzer
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"analyzer"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|docBuilder
operator|.
name|indexAnalyzer
argument_list|(
name|analysisService
operator|.
name|analyzer
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|docBuilder
operator|.
name|searchAnalyzer
argument_list|(
name|analysisService
operator|.
name|analyzer
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|docBuilder
operator|.
name|hasIndexAnalyzer
argument_list|()
condition|)
block|{
name|docBuilder
operator|.
name|indexAnalyzer
argument_list|(
name|analysisService
operator|.
name|defaultIndexAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|docBuilder
operator|.
name|hasSearchAnalyzer
argument_list|()
condition|)
block|{
name|docBuilder
operator|.
name|searchAnalyzer
argument_list|(
name|analysisService
operator|.
name|defaultSearchAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|mapping
operator|.
name|containsKey
argument_list|(
literal|"_meta"
argument_list|)
condition|)
block|{
name|attributes
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|mapping
operator|.
name|get
argument_list|(
literal|"_meta"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|docBuilder
operator|.
name|meta
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
name|XContentDocumentMapper
name|documentMapper
init|=
name|docBuilder
operator|.
name|build
argument_list|(
name|this
argument_list|)
decl_stmt|;
comment|// update the source with the generated one
name|documentMapper
operator|.
name|refreshSource
argument_list|()
expr_stmt|;
return|return
name|documentMapper
return|;
block|}
DECL|method|parseUidField
specifier|private
name|UidFieldMapper
operator|.
name|Builder
name|parseUidField
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|uidNode
parameter_list|,
name|XContentMapper
operator|.
name|TypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
block|{
name|UidFieldMapper
operator|.
name|Builder
name|builder
init|=
name|uid
argument_list|()
decl_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|parseBoostField
specifier|private
name|BoostFieldMapper
operator|.
name|Builder
name|parseBoostField
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|boostNode
parameter_list|,
name|XContentMapper
operator|.
name|TypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
block|{
name|String
name|name
init|=
name|boostNode
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
operator|==
literal|null
condition|?
name|BoostFieldMapper
operator|.
name|Defaults
operator|.
name|NAME
else|:
name|boostNode
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|BoostFieldMapper
operator|.
name|Builder
name|builder
init|=
name|boost
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|parseNumberField
argument_list|(
name|builder
argument_list|,
name|name
argument_list|,
name|boostNode
argument_list|,
name|parserContext
argument_list|)
expr_stmt|;
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
name|boostNode
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|propName
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
name|propNode
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|propName
operator|.
name|equals
argument_list|(
literal|"null_value"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|nullValue
argument_list|(
name|nodeFloatValue
argument_list|(
name|propNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
return|;
block|}
DECL|method|parseTypeField
specifier|private
name|TypeFieldMapper
operator|.
name|Builder
name|parseTypeField
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|typeNode
parameter_list|,
name|XContentMapper
operator|.
name|TypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
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
name|typeNode
argument_list|,
name|parserContext
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|parseIdField
specifier|private
name|IdFieldMapper
operator|.
name|Builder
name|parseIdField
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|idNode
parameter_list|,
name|XContentMapper
operator|.
name|TypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
block|{
name|IdFieldMapper
operator|.
name|Builder
name|builder
init|=
name|id
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
name|idNode
argument_list|,
name|parserContext
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|parseRoutingField
specifier|private
name|RoutingFieldMapper
operator|.
name|Builder
name|parseRoutingField
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|routingNode
parameter_list|,
name|XContentMapper
operator|.
name|TypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
block|{
name|RoutingFieldMapper
operator|.
name|Builder
name|builder
init|=
name|routing
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
name|routingNode
argument_list|,
name|parserContext
argument_list|)
expr_stmt|;
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
name|routingNode
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
literal|"required"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|required
argument_list|(
name|nodeBooleanValue
argument_list|(
name|fieldNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
return|;
block|}
DECL|method|parseAnalyzerField
specifier|private
name|AnalyzerMapper
operator|.
name|Builder
name|parseAnalyzerField
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|analyzerNode
parameter_list|,
name|XContentMapper
operator|.
name|TypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
block|{
name|AnalyzerMapper
operator|.
name|Builder
name|builder
init|=
name|analyzer
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
name|analyzerNode
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
literal|"path"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
return|;
block|}
DECL|method|parseAllField
specifier|private
name|AllFieldMapper
operator|.
name|Builder
name|parseAllField
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|allNode
parameter_list|,
name|XContentMapper
operator|.
name|TypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
block|{
name|AllFieldMapper
operator|.
name|Builder
name|builder
init|=
name|all
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
name|allNode
argument_list|,
name|parserContext
argument_list|)
expr_stmt|;
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
name|allNode
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
block|}
return|return
name|builder
return|;
block|}
DECL|method|parseSourceField
specifier|private
name|SourceFieldMapper
operator|.
name|Builder
name|parseSourceField
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sourceNode
parameter_list|,
name|XContentMapper
operator|.
name|TypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
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
name|sourceNode
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
block|}
return|return
name|builder
return|;
block|}
DECL|method|parseIndexField
specifier|private
name|IndexFieldMapper
operator|.
name|Builder
name|parseIndexField
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|indexNode
parameter_list|,
name|XContentMapper
operator|.
name|TypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
block|{
name|IndexFieldMapper
operator|.
name|Builder
name|builder
init|=
name|XContentMapperBuilders
operator|.
name|index
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
name|indexNode
argument_list|,
name|parserContext
argument_list|)
expr_stmt|;
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
name|indexNode
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
block|}
return|return
name|builder
return|;
block|}
DECL|method|extractMapping
specifier|private
name|Tuple
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|extractMapping
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|source
parameter_list|)
throws|throws
name|MapperParsingException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|root
decl_stmt|;
name|XContentParser
name|xContentParser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|xContentParser
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
name|root
operator|=
name|xContentParser
operator|.
name|map
argument_list|()
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
literal|"Failed to parse mapping definition"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|xContentParser
operator|!=
literal|null
condition|)
block|{
name|xContentParser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// we always assume the first and single key is the mapping type root
if|if
condition|(
name|root
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Mapping must have the `type` as the root object"
argument_list|)
throw|;
block|}
name|String
name|rootName
init|=
name|root
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|type
operator|=
name|rootName
expr_stmt|;
block|}
return|return
operator|new
name|Tuple
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|(
name|type
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|root
operator|.
name|get
argument_list|(
name|rootName
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

