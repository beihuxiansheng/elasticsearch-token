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
name|DocumentMapper
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
implements|implements
name|DocumentMapperParser
block|{
DECL|field|analysisService
specifier|private
specifier|final
name|AnalysisService
name|analysisService
decl_stmt|;
DECL|field|rootObjectTypeParser
specifier|private
specifier|final
name|XContentObjectMapper
operator|.
name|TypeParser
name|rootObjectTypeParser
init|=
operator|new
name|XContentObjectMapper
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
name|XContentTypeParser
argument_list|>
name|typeParsers
decl_stmt|;
DECL|method|XContentDocumentMapperParser
specifier|public
name|XContentDocumentMapperParser
parameter_list|(
name|AnalysisService
name|analysisService
parameter_list|)
block|{
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
name|XContentTypeParser
argument_list|>
argument_list|()
operator|.
name|put
argument_list|(
name|XContentShortFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|XContentShortFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|XContentIntegerFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|XContentIntegerFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|XContentLongFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|XContentLongFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|XContentFloatFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|XContentFloatFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|XContentDoubleFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|XContentDoubleFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|XContentBooleanFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|XContentBooleanFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|XContentBinaryFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|XContentBinaryFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|XContentDateFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|XContentDateFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|XContentStringFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|XContentStringFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|XContentObjectMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|XContentObjectMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|XContentMultiFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|XContentMultiFieldMapper
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
name|XContentTypeParser
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
name|XContentTypeParser
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
DECL|method|parse
annotation|@
name|Override
specifier|public
name|DocumentMapper
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
name|DocumentMapper
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|rootObj
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
comment|// we have no type, we assume the first node is the type
name|rootObj
operator|=
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
expr_stmt|;
name|type
operator|=
name|rootName
expr_stmt|;
block|}
else|else
block|{
comment|// we have a type, check if the top level one is the type as well
comment|// if it is, then the root is that node, if not then the root is the master node
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|rootName
argument_list|)
condition|)
block|{
name|Object
name|tmpNode
init|=
name|root
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|tmpNode
operator|instanceof
name|Map
operator|)
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Expected root node name ["
operator|+
name|rootName
operator|+
literal|"] to be of object type, but its not"
argument_list|)
throw|;
block|}
name|rootObj
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|tmpNode
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rootName
operator|.
name|equals
argument_list|(
literal|"_default_"
argument_list|)
condition|)
block|{
name|Object
name|tmpNode
init|=
name|root
operator|.
name|get
argument_list|(
literal|"_default_"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|tmpNode
operator|instanceof
name|Map
operator|)
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"_default_ mappings must have an inner object representing the actual mappings for the type"
argument_list|)
throw|;
block|}
name|rootObj
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|tmpNode
expr_stmt|;
block|}
else|else
block|{
name|rootObj
operator|=
name|root
expr_stmt|;
block|}
block|}
name|XContentTypeParser
operator|.
name|ParserContext
name|parserContext
init|=
operator|new
name|XContentTypeParser
operator|.
name|ParserContext
argument_list|(
name|rootObj
argument_list|,
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
operator|(
name|XContentObjectMapper
operator|.
name|Builder
operator|)
name|rootObjectTypeParser
operator|.
name|parse
argument_list|(
name|type
argument_list|,
name|rootObj
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
name|rootObj
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
name|XContentSourceFieldMapper
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
name|XContentIdFieldMapper
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
name|XContentTypeFieldMapper
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
name|XContentUidFieldMapper
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
name|XContentBoostFieldMapper
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
name|XContentAllFieldMapper
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
name|rootObj
operator|.
name|containsKey
argument_list|(
literal|"_attributes"
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
name|rootObj
operator|.
name|get
argument_list|(
literal|"_attributes"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|docBuilder
operator|.
name|attributes
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
name|docBuilder
operator|.
name|mappingSource
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|XContentDocumentMapper
name|documentMapper
init|=
name|docBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// update the source with the generated one
name|documentMapper
operator|.
name|mappingSource
argument_list|(
name|documentMapper
operator|.
name|buildSource
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|documentMapper
return|;
block|}
DECL|method|parseUidField
specifier|private
name|XContentUidFieldMapper
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
name|XContentTypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
block|{
name|XContentUidFieldMapper
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
name|XContentBoostFieldMapper
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
name|XContentTypeParser
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
name|XContentBoostFieldMapper
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
name|XContentBoostFieldMapper
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
name|XContentTypeFieldMapper
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
name|XContentTypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
block|{
name|XContentTypeFieldMapper
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
name|XContentIdFieldMapper
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
name|XContentTypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
block|{
name|XContentIdFieldMapper
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
DECL|method|parseAllField
specifier|private
name|XContentAllFieldMapper
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
name|XContentTypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
block|{
name|XContentAllFieldMapper
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
name|XContentSourceFieldMapper
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
name|XContentTypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
block|{
name|XContentSourceFieldMapper
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
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

