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
name|com
operator|.
name|google
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSortedMap
import|;
end_import

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
name|Maps
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
name|compress
operator|.
name|CompressedXContent
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
name|geo
operator|.
name|ShapesAvailability
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|core
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
name|geo
operator|.
name|GeoShapeFieldMapper
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
name|internal
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
name|mapper
operator|.
name|object
operator|.
name|ObjectMapper
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
name|object
operator|.
name|RootObjectMapper
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|similarity
operator|.
name|SimilarityLookupService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Script
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptService
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
name|doc
import|;
end_import

begin_class
DECL|class|DocumentMapperParser
specifier|public
class|class
name|DocumentMapperParser
block|{
DECL|field|indexSettings
specifier|private
specifier|final
name|Settings
name|indexSettings
decl_stmt|;
DECL|field|mapperService
specifier|final
name|MapperService
name|mapperService
decl_stmt|;
DECL|field|analysisService
specifier|final
name|AnalysisService
name|analysisService
decl_stmt|;
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|DocumentMapperParser
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|similarityLookupService
specifier|private
specifier|final
name|SimilarityLookupService
name|similarityLookupService
decl_stmt|;
DECL|field|scriptService
specifier|private
specifier|final
name|ScriptService
name|scriptService
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
DECL|field|typeParsers
specifier|private
specifier|volatile
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Mapper
operator|.
name|TypeParser
argument_list|>
name|typeParsers
decl_stmt|;
DECL|field|rootTypeParsers
specifier|private
specifier|volatile
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Mapper
operator|.
name|TypeParser
argument_list|>
name|rootTypeParsers
decl_stmt|;
DECL|field|additionalRootMappers
specifier|private
specifier|volatile
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Mapper
operator|.
name|TypeParser
argument_list|>
name|additionalRootMappers
decl_stmt|;
DECL|method|DocumentMapperParser
specifier|public
name|DocumentMapperParser
parameter_list|(
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|MapperService
name|mapperService
parameter_list|,
name|AnalysisService
name|analysisService
parameter_list|,
name|SimilarityLookupService
name|similarityLookupService
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|)
block|{
name|this
operator|.
name|indexSettings
operator|=
name|indexSettings
expr_stmt|;
name|this
operator|.
name|parseFieldMatcher
operator|=
operator|new
name|ParseFieldMatcher
argument_list|(
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|mapperService
operator|=
name|mapperService
expr_stmt|;
name|this
operator|.
name|analysisService
operator|=
name|analysisService
expr_stmt|;
name|this
operator|.
name|similarityLookupService
operator|=
name|similarityLookupService
expr_stmt|;
name|this
operator|.
name|scriptService
operator|=
name|scriptService
expr_stmt|;
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|Mapper
operator|.
name|TypeParser
argument_list|>
name|typeParsersBuilder
init|=
operator|new
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|Mapper
operator|.
name|TypeParser
argument_list|>
argument_list|()
operator|.
name|put
argument_list|(
name|ByteFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|ByteFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
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
name|TokenCountFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|TokenCountFieldMapper
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
name|ObjectMapper
operator|.
name|NESTED_CONTENT_TYPE
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
name|TypeParsers
operator|.
name|MULTI_FIELD_CONTENT_TYPE
argument_list|,
name|TypeParsers
operator|.
name|multiFieldConverterTypeParser
argument_list|)
operator|.
name|put
argument_list|(
name|CompletionFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|CompletionFieldMapper
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
decl_stmt|;
if|if
condition|(
name|ShapesAvailability
operator|.
name|JTS_AVAILABLE
condition|)
block|{
name|typeParsersBuilder
operator|.
name|put
argument_list|(
name|GeoShapeFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|GeoShapeFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|typeParsers
operator|=
name|typeParsersBuilder
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
name|rootTypeParsers
operator|=
operator|new
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|Mapper
operator|.
name|TypeParser
argument_list|>
argument_list|()
operator|.
name|put
argument_list|(
name|IndexFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|IndexFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|SourceFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|SourceFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|TypeFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|TypeFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|AllFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|AllFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|ParentFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|ParentFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|RoutingFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|RoutingFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|TimestampFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|TimestampFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|TTLFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|TTLFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|UidFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|VersionFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|VersionFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|IdFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|IdFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|FieldNamesFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|FieldNamesFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
name|additionalRootMappers
operator|=
name|ImmutableSortedMap
operator|.
expr|<
name|String
operator|,
name|Mapper
operator|.
name|TypeParser
operator|>
name|of
argument_list|()
expr_stmt|;
name|indexVersionCreated
operator|=
name|Version
operator|.
name|indexCreated
argument_list|(
name|indexSettings
argument_list|)
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
name|Mapper
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
argument_list|<>
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
DECL|method|putRootTypeParser
specifier|public
name|void
name|putRootTypeParser
parameter_list|(
name|String
name|type
parameter_list|,
name|Mapper
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
name|rootTypeParsers
operator|=
operator|new
name|MapBuilder
argument_list|<>
argument_list|(
name|rootTypeParsers
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
name|additionalRootMappers
operator|=
name|ImmutableSortedMap
operator|.
expr|<
name|String
operator|,
name|Mapper
operator|.
name|TypeParser
operator|>
name|naturalOrder
argument_list|()
operator|.
name|putAll
argument_list|(
name|additionalRootMappers
argument_list|)
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|typeParser
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|parserContext
specifier|public
name|Mapper
operator|.
name|TypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
operator|new
name|Mapper
operator|.
name|TypeParser
operator|.
name|ParserContext
argument_list|(
name|type
argument_list|,
name|analysisService
argument_list|,
name|similarityLookupService
argument_list|,
name|mapperService
argument_list|,
name|typeParsers
argument_list|,
name|indexVersionCreated
argument_list|,
name|parseFieldMatcher
argument_list|)
return|;
block|}
DECL|method|parse
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
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|parse
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
return|return
name|parse
argument_list|(
name|type
argument_list|,
name|mapping
argument_list|,
name|defaultSource
argument_list|)
return|;
block|}
DECL|method|parseCompressed
specifier|public
name|DocumentMapper
name|parseCompressed
parameter_list|(
annotation|@
name|Nullable
name|String
name|type
parameter_list|,
name|CompressedXContent
name|source
parameter_list|)
throws|throws
name|MapperParsingException
block|{
return|return
name|parseCompressed
argument_list|(
name|type
argument_list|,
name|source
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|parseCompressed
specifier|public
name|DocumentMapper
name|parseCompressed
parameter_list|(
annotation|@
name|Nullable
name|String
name|type
parameter_list|,
name|CompressedXContent
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|root
init|=
name|XContentHelper
operator|.
name|convertToMap
argument_list|(
name|source
operator|.
name|compressedReference
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|v2
argument_list|()
decl_stmt|;
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
name|root
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
return|return
name|parse
argument_list|(
name|type
argument_list|,
name|mapping
argument_list|,
name|defaultSource
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
DECL|method|parse
specifier|private
name|DocumentMapper
name|parse
parameter_list|(
name|String
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mapping
parameter_list|,
name|String
name|defaultSource
parameter_list|)
throws|throws
name|MapperParsingException
block|{
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
name|Mapper
operator|.
name|TypeParser
operator|.
name|ParserContext
name|parserContext
init|=
name|parserContext
argument_list|(
name|type
argument_list|)
decl_stmt|;
comment|// parse RootObjectMapper
name|DocumentMapper
operator|.
name|Builder
name|docBuilder
init|=
name|doc
argument_list|(
name|indexSettings
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
argument_list|,
name|mapperService
argument_list|)
decl_stmt|;
comment|// Add default mapping for the plugged-in meta mappers
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Mapper
operator|.
name|TypeParser
argument_list|>
name|entry
range|:
name|additionalRootMappers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|docBuilder
operator|.
name|put
argument_list|(
operator|(
name|MetadataFieldMapper
operator|.
name|Builder
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|parse
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|emptyMap
argument_list|()
argument_list|,
name|parserContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|mapping
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// parse DocumentMapper
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
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
literal|"transform"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
if|if
condition|(
name|fieldNode
operator|instanceof
name|Map
condition|)
block|{
name|parseTransform
argument_list|(
name|docBuilder
argument_list|,
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
operator|.
name|indexVersionCreated
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldNode
operator|instanceof
name|List
condition|)
block|{
for|for
control|(
name|Object
name|transformItem
range|:
operator|(
name|List
operator|)
name|fieldNode
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|transformItem
operator|instanceof
name|Map
operator|)
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Elements of transform list must be objects but one was:  "
operator|+
name|fieldNode
argument_list|)
throw|;
block|}
name|parseTransform
argument_list|(
name|docBuilder
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|transformItem
argument_list|,
name|parserContext
operator|.
name|indexVersionCreated
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Transform must be an object or an array but was:  "
operator|+
name|fieldNode
argument_list|)
throw|;
block|}
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Mapper
operator|.
name|TypeParser
name|typeParser
init|=
name|rootTypeParsers
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|typeParser
operator|!=
literal|null
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fieldNodeMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|fieldNode
decl_stmt|;
name|docBuilder
operator|.
name|put
argument_list|(
operator|(
name|MetadataFieldMapper
operator|.
name|Builder
operator|)
name|typeParser
operator|.
name|parse
argument_list|(
name|fieldName
argument_list|,
name|fieldNodeMap
argument_list|,
name|parserContext
argument_list|)
argument_list|)
expr_stmt|;
name|fieldNodeMap
operator|.
name|remove
argument_list|(
literal|"type"
argument_list|)
expr_stmt|;
name|checkNoRemainingFields
argument_list|(
name|fieldName
argument_list|,
name|fieldNodeMap
argument_list|,
name|parserContext
operator|.
name|indexVersionCreated
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|remove
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
name|checkNoRemainingFields
argument_list|(
name|mapping
argument_list|,
name|parserContext
operator|.
name|indexVersionCreated
argument_list|()
argument_list|,
literal|"Root mapping definition has unsupported parameters: "
argument_list|)
expr_stmt|;
return|return
name|docBuilder
operator|.
name|build
argument_list|(
name|mapperService
argument_list|,
name|this
argument_list|)
return|;
block|}
DECL|method|checkNoRemainingFields
specifier|public
specifier|static
name|void
name|checkNoRemainingFields
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fieldNodeMap
parameter_list|,
name|Version
name|indexVersionCreated
parameter_list|)
block|{
name|checkNoRemainingFields
argument_list|(
name|fieldNodeMap
argument_list|,
name|indexVersionCreated
argument_list|,
literal|"Mapping definition for ["
operator|+
name|fieldName
operator|+
literal|"] has unsupported parameters: "
argument_list|)
expr_stmt|;
block|}
DECL|method|checkNoRemainingFields
specifier|public
specifier|static
name|void
name|checkNoRemainingFields
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fieldNodeMap
parameter_list|,
name|Version
name|indexVersionCreated
parameter_list|,
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
operator|!
name|fieldNodeMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|indexVersionCreated
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_2_0_0_beta1
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
name|message
operator|+
name|getRemainingFields
argument_list|(
name|fieldNodeMap
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
name|message
operator|+
literal|"{}"
argument_list|,
name|getRemainingFields
argument_list|(
name|fieldNodeMap
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getRemainingFields
specifier|private
specifier|static
name|String
name|getRemainingFields
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|map
parameter_list|)
block|{
name|StringBuilder
name|remainingFields
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|map
operator|.
name|keySet
argument_list|()
control|)
block|{
name|remainingFields
operator|.
name|append
argument_list|(
literal|" ["
argument_list|)
operator|.
name|append
argument_list|(
name|key
argument_list|)
operator|.
name|append
argument_list|(
literal|" : "
argument_list|)
operator|.
name|append
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
return|return
name|remainingFields
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|parseTransform
specifier|private
name|void
name|parseTransform
parameter_list|(
name|DocumentMapper
operator|.
name|Builder
name|docBuilder
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transformConfig
parameter_list|,
name|Version
name|indexVersionCreated
parameter_list|)
block|{
name|Script
name|script
init|=
name|Script
operator|.
name|parse
argument_list|(
name|transformConfig
argument_list|,
literal|true
argument_list|,
name|parseFieldMatcher
argument_list|)
decl_stmt|;
if|if
condition|(
name|script
operator|!=
literal|null
condition|)
block|{
name|docBuilder
operator|.
name|transform
argument_list|(
name|scriptService
argument_list|,
name|script
argument_list|)
expr_stmt|;
block|}
name|checkNoRemainingFields
argument_list|(
name|transformConfig
argument_list|,
name|indexVersionCreated
argument_list|,
literal|"Transform config has unsupported parameters: "
argument_list|)
expr_stmt|;
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
try|try
init|(
name|XContentParser
name|parser
init|=
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
init|)
block|{
name|root
operator|=
name|parser
operator|.
name|mapOrdered
argument_list|()
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
name|MapperParsingException
argument_list|(
literal|"failed to parse mapping definition"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|extractMapping
argument_list|(
name|type
argument_list|,
name|root
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|root
parameter_list|)
throws|throws
name|MapperParsingException
block|{
if|if
condition|(
name|root
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// if we don't have any keys throw an exception
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"malformed mapping no root object found"
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
name|mapping
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
operator|||
name|type
operator|.
name|equals
argument_list|(
name|rootName
argument_list|)
condition|)
block|{
name|mapping
operator|=
operator|new
name|Tuple
argument_list|<>
argument_list|(
name|rootName
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
expr_stmt|;
block|}
else|else
block|{
name|mapping
operator|=
operator|new
name|Tuple
argument_list|<>
argument_list|(
name|type
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
return|return
name|mapping
return|;
block|}
block|}
end_class

end_unit
