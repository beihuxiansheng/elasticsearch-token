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
name|common
operator|.
name|bytes
operator|.
name|BytesArray
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
name|index
operator|.
name|IndexService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndicesModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|mapper
operator|.
name|MapperRegistry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESSingleNodeTestCase
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
name|Arrays
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
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_class
DECL|class|FieldNamesFieldMapperTests
specifier|public
class|class
name|FieldNamesFieldMapperTests
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|method|extract
specifier|private
specifier|static
name|SortedSet
argument_list|<
name|String
argument_list|>
name|extract
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|SortedSet
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|FieldNamesFieldMapper
operator|.
name|extractFieldNames
argument_list|(
name|path
argument_list|)
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
DECL|method|set
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|SortedSet
argument_list|<
name|T
argument_list|>
name|set
parameter_list|(
name|T
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
argument_list|)
return|;
block|}
DECL|method|assertFieldNames
name|void
name|assertFieldNames
parameter_list|(
name|SortedSet
argument_list|<
name|String
argument_list|>
name|expected
parameter_list|,
name|ParsedDocument
name|doc
parameter_list|)
block|{
name|String
index|[]
name|got
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getValues
argument_list|(
literal|"_field_names"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|set
argument_list|(
name|got
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testExtractFieldNames
specifier|public
name|void
name|testExtractFieldNames
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|set
argument_list|(
literal|"abc"
argument_list|)
argument_list|,
name|extract
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|set
argument_list|(
literal|"a"
argument_list|,
literal|"a.b"
argument_list|)
argument_list|,
name|extract
argument_list|(
literal|"a.b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|set
argument_list|(
literal|"a"
argument_list|,
literal|"a.b"
argument_list|,
literal|"a.b.c"
argument_list|)
argument_list|,
name|extract
argument_list|(
literal|"a.b.c"
argument_list|)
argument_list|)
expr_stmt|;
comment|// and now corner cases
name|assertEquals
argument_list|(
name|set
argument_list|(
literal|""
argument_list|,
literal|".a"
argument_list|)
argument_list|,
name|extract
argument_list|(
literal|".a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|set
argument_list|(
literal|"a"
argument_list|,
literal|"a."
argument_list|)
argument_list|,
name|extract
argument_list|(
literal|"a."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|set
argument_list|(
literal|""
argument_list|,
literal|"."
argument_list|,
literal|".."
argument_list|)
argument_list|,
name|extract
argument_list|(
literal|".."
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldType
specifier|public
name|void
name|testFieldType
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_field_names"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
decl_stmt|;
name|FieldNamesFieldMapper
name|fieldNamesMapper
init|=
name|docMapper
operator|.
name|metadataMapper
argument_list|(
name|FieldNamesFieldMapper
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|fieldNamesMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|hasDocValues
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|,
name|fieldNamesMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fieldNamesMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|tokenized
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fieldNamesMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fieldNamesMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testInjectIntoDocDuringParsing
specifier|public
name|void
name|testInjectIntoDocDuringParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|defaultMapper
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|defaultMapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"a"
argument_list|,
literal|"100"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"b"
argument_list|)
operator|.
name|field
argument_list|(
literal|"c"
argument_list|,
literal|42
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertFieldNames
argument_list|(
name|set
argument_list|(
literal|"a"
argument_list|,
literal|"a.keyword"
argument_list|,
literal|"b"
argument_list|,
literal|"b.c"
argument_list|,
literal|"_uid"
argument_list|,
literal|"_type"
argument_list|,
literal|"_version"
argument_list|,
literal|"_seq_no"
argument_list|,
literal|"_source"
argument_list|,
literal|"_all"
argument_list|)
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|testExplicitEnabled
specifier|public
name|void
name|testExplicitEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_field_names"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
decl_stmt|;
name|FieldNamesFieldMapper
name|fieldNamesMapper
init|=
name|docMapper
operator|.
name|metadataMapper
argument_list|(
name|FieldNamesFieldMapper
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|fieldNamesMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|isEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|ParsedDocument
name|doc
init|=
name|docMapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertFieldNames
argument_list|(
name|set
argument_list|(
literal|"field"
argument_list|,
literal|"field.keyword"
argument_list|,
literal|"_uid"
argument_list|,
literal|"_type"
argument_list|,
literal|"_version"
argument_list|,
literal|"_seq_no"
argument_list|,
literal|"_source"
argument_list|,
literal|"_all"
argument_list|)
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|testDisabled
specifier|public
name|void
name|testDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_field_names"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|false
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
decl_stmt|;
name|FieldNamesFieldMapper
name|fieldNamesMapper
init|=
name|docMapper
operator|.
name|metadataMapper
argument_list|(
name|FieldNamesFieldMapper
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|fieldNamesMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|isEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|ParsedDocument
name|doc
init|=
name|docMapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|get
argument_list|(
literal|"_field_names"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMergingMappings
specifier|public
name|void
name|testMergingMappings
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|enabledMapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_field_names"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|String
name|disabledMapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_field_names"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|false
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|MapperService
name|mapperService
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|DocumentMapper
name|mapperEnabled
init|=
name|mapperService
operator|.
name|merge
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|enabledMapping
argument_list|)
argument_list|,
name|MapperService
operator|.
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|DocumentMapper
name|mapperDisabled
init|=
name|mapperService
operator|.
name|merge
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|disabledMapping
argument_list|)
argument_list|,
name|MapperService
operator|.
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|mapperDisabled
operator|.
name|metadataMapper
argument_list|(
name|FieldNamesFieldMapper
operator|.
name|class
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|isEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|mapperEnabled
operator|=
name|mapperService
operator|.
name|merge
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|enabledMapping
argument_list|)
argument_list|,
name|MapperService
operator|.
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mapperEnabled
operator|.
name|metadataMapper
argument_list|(
name|FieldNamesFieldMapper
operator|.
name|class
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|isEnabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|DummyMetadataFieldMapper
specifier|private
specifier|static
class|class
name|DummyMetadataFieldMapper
extends|extends
name|MetadataFieldMapper
block|{
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
block|{
return|return
operator|new
name|MetadataFieldMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|DummyMetadataFieldMapper
argument_list|>
argument_list|(
literal|"_dummy"
argument_list|,
name|FIELD_TYPE
argument_list|,
name|FIELD_TYPE
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|DummyMetadataFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|DummyMetadataFieldMapper
argument_list|(
name|context
operator|.
name|indexSettings
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
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
name|DummyMetadataFieldMapper
argument_list|(
name|indexSettings
argument_list|)
return|;
block|}
block|}
DECL|class|DummyFieldType
specifier|private
specifier|static
class|class
name|DummyFieldType
extends|extends
name|TermBasedFieldType
block|{
DECL|method|DummyFieldType
specifier|public
name|DummyFieldType
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|DummyFieldType
specifier|private
name|DummyFieldType
parameter_list|(
name|MappedFieldType
name|other
parameter_list|)
block|{
name|super
argument_list|(
name|other
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
name|DummyFieldType
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
literal|"_dummy"
return|;
block|}
block|}
DECL|field|FIELD_TYPE
specifier|private
specifier|static
specifier|final
name|MappedFieldType
name|FIELD_TYPE
init|=
operator|new
name|DummyFieldType
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
name|DOCS
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setName
argument_list|(
literal|"_dummy"
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
DECL|method|DummyMetadataFieldMapper
specifier|protected
name|DummyMetadataFieldMapper
parameter_list|(
name|Settings
name|indexSettings
parameter_list|)
block|{
name|super
argument_list|(
literal|"_dummy"
argument_list|,
name|FIELD_TYPE
argument_list|,
name|FIELD_TYPE
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
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
block|{         }
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
name|context
operator|.
name|doc
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"_dummy"
argument_list|,
literal|"dummy"
argument_list|,
name|FIELD_TYPE
argument_list|)
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
name|Field
argument_list|>
name|fields
parameter_list|)
throws|throws
name|IOException
block|{         }
annotation|@
name|Override
DECL|method|contentType
specifier|protected
name|String
name|contentType
parameter_list|()
block|{
return|return
literal|"_dummy"
return|;
block|}
block|}
DECL|method|testSeesFieldsFromPlugins
specifier|public
name|void
name|testSeesFieldsFromPlugins
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexService
name|indexService
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|IndicesModule
name|indicesModule
init|=
name|newTestIndicesModule
argument_list|(
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"_dummy"
argument_list|,
operator|new
name|DummyMetadataFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|MapperRegistry
name|mapperRegistry
init|=
name|indicesModule
operator|.
name|getMapperRegistry
argument_list|()
decl_stmt|;
name|MapperService
name|mapperService
init|=
operator|new
name|MapperService
argument_list|(
name|indexService
operator|.
name|getIndexSettings
argument_list|()
argument_list|,
name|indexService
operator|.
name|getIndexAnalyzers
argument_list|()
argument_list|,
name|indexService
operator|.
name|similarityService
argument_list|()
argument_list|,
name|mapperRegistry
argument_list|,
name|indexService
operator|::
name|newQueryShardContext
argument_list|)
decl_stmt|;
name|DocumentMapperParser
name|parser
init|=
operator|new
name|DocumentMapperParser
argument_list|(
name|indexService
operator|.
name|getIndexSettings
argument_list|()
argument_list|,
name|mapperService
argument_list|,
name|indexService
operator|.
name|getIndexAnalyzers
argument_list|()
argument_list|,
name|indexService
operator|.
name|similarityService
argument_list|()
argument_list|,
name|mapperRegistry
argument_list|,
name|indexService
operator|::
name|newQueryShardContext
argument_list|)
decl_stmt|;
name|String
name|mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|mapper
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
decl_stmt|;
name|ParsedDocument
name|parsedDocument
init|=
name|mapper
operator|.
name|parse
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{}"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexableField
index|[]
name|fields
init|=
name|parsedDocument
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
name|FieldNamesFieldMapper
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|IndexableField
name|f
range|:
name|fields
control|)
block|{
if|if
condition|(
literal|"_dummy"
operator|.
name|equals
argument_list|(
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Could not find the dummy field among "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|fields
argument_list|)
argument_list|,
name|found
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
