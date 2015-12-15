begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.externalvalues
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|externalvalues
package|;
end_package

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Point
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
name|Iterators
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
name|GeoPoint
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
name|builders
operator|.
name|ShapeBuilders
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
name|ContentPath
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
name|FieldMapper
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
name|mapper
operator|.
name|core
operator|.
name|BinaryFieldMapper
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
name|BooleanFieldMapper
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
name|BaseGeoPointFieldMapper
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
name|GeoPointFieldMapperLegacy
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|stringField
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
name|parseMultiField
import|;
end_import

begin_comment
comment|/**  * This mapper add a new sub fields  * .bin Binary type  * .bool Boolean type  * .point GeoPoint type  * .shape GeoShape type  */
end_comment

begin_class
DECL|class|ExternalMapper
specifier|public
class|class
name|ExternalMapper
extends|extends
name|FieldMapper
block|{
DECL|class|Names
specifier|public
specifier|static
class|class
name|Names
block|{
DECL|field|FIELD_BIN
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_BIN
init|=
literal|"bin"
decl_stmt|;
DECL|field|FIELD_BOOL
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_BOOL
init|=
literal|"bool"
decl_stmt|;
DECL|field|FIELD_POINT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_POINT
init|=
literal|"point"
decl_stmt|;
DECL|field|FIELD_SHAPE
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_SHAPE
init|=
literal|"shape"
decl_stmt|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|FieldMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|ExternalMapper
argument_list|>
block|{
DECL|field|binBuilder
specifier|private
name|BinaryFieldMapper
operator|.
name|Builder
name|binBuilder
init|=
operator|new
name|BinaryFieldMapper
operator|.
name|Builder
argument_list|(
name|Names
operator|.
name|FIELD_BIN
argument_list|)
decl_stmt|;
DECL|field|boolBuilder
specifier|private
name|BooleanFieldMapper
operator|.
name|Builder
name|boolBuilder
init|=
operator|new
name|BooleanFieldMapper
operator|.
name|Builder
argument_list|(
name|Names
operator|.
name|FIELD_BOOL
argument_list|)
decl_stmt|;
DECL|field|pointBuilder
specifier|private
name|GeoPointFieldMapper
operator|.
name|Builder
name|pointBuilder
init|=
operator|new
name|GeoPointFieldMapper
operator|.
name|Builder
argument_list|(
name|Names
operator|.
name|FIELD_POINT
argument_list|)
decl_stmt|;
DECL|field|legacyPointBuilder
specifier|private
name|GeoPointFieldMapperLegacy
operator|.
name|Builder
name|legacyPointBuilder
init|=
operator|new
name|GeoPointFieldMapperLegacy
operator|.
name|Builder
argument_list|(
name|Names
operator|.
name|FIELD_POINT
argument_list|)
decl_stmt|;
DECL|field|shapeBuilder
specifier|private
name|GeoShapeFieldMapper
operator|.
name|Builder
name|shapeBuilder
init|=
operator|new
name|GeoShapeFieldMapper
operator|.
name|Builder
argument_list|(
name|Names
operator|.
name|FIELD_SHAPE
argument_list|)
decl_stmt|;
DECL|field|stringBuilder
specifier|private
name|Mapper
operator|.
name|Builder
name|stringBuilder
decl_stmt|;
DECL|field|generatedValue
specifier|private
name|String
name|generatedValue
decl_stmt|;
DECL|field|mapperName
specifier|private
name|String
name|mapperName
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|generatedValue
parameter_list|,
name|String
name|mapperName
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
operator|new
name|ExternalFieldType
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|this
expr_stmt|;
name|this
operator|.
name|stringBuilder
operator|=
name|stringField
argument_list|(
name|name
argument_list|)
operator|.
name|store
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|generatedValue
operator|=
name|generatedValue
expr_stmt|;
name|this
operator|.
name|mapperName
operator|=
name|mapperName
expr_stmt|;
block|}
DECL|method|string
specifier|public
name|Builder
name|string
parameter_list|(
name|Mapper
operator|.
name|Builder
name|content
parameter_list|)
block|{
name|this
operator|.
name|stringBuilder
operator|=
name|content
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|ExternalMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|context
operator|.
name|path
argument_list|()
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|BinaryFieldMapper
name|binMapper
init|=
name|binBuilder
operator|.
name|build
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|BooleanFieldMapper
name|boolMapper
init|=
name|boolBuilder
operator|.
name|build
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|BaseGeoPointFieldMapper
name|pointMapper
init|=
operator|(
name|context
operator|.
name|indexCreatedVersion
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_2_2_0
argument_list|)
operator|)
condition|?
name|legacyPointBuilder
operator|.
name|build
argument_list|(
name|context
argument_list|)
else|:
name|pointBuilder
operator|.
name|build
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|GeoShapeFieldMapper
name|shapeMapper
init|=
name|shapeBuilder
operator|.
name|build
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|FieldMapper
name|stringMapper
init|=
operator|(
name|FieldMapper
operator|)
name|stringBuilder
operator|.
name|build
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|context
operator|.
name|path
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
name|setupFieldType
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
operator|new
name|ExternalMapper
argument_list|(
name|name
argument_list|,
name|fieldType
argument_list|,
name|generatedValue
argument_list|,
name|mapperName
argument_list|,
name|binMapper
argument_list|,
name|boolMapper
argument_list|,
name|pointMapper
argument_list|,
name|shapeMapper
argument_list|,
name|stringMapper
argument_list|,
name|context
operator|.
name|indexSettings
argument_list|()
argument_list|,
name|multiFieldsBuilder
operator|.
name|build
argument_list|(
name|this
argument_list|,
name|context
argument_list|)
argument_list|,
name|copyTo
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
DECL|field|generatedValue
specifier|private
name|String
name|generatedValue
decl_stmt|;
DECL|field|mapperName
specifier|private
name|String
name|mapperName
decl_stmt|;
DECL|method|TypeParser
name|TypeParser
parameter_list|(
name|String
name|mapperName
parameter_list|,
name|String
name|generatedValue
parameter_list|)
block|{
name|this
operator|.
name|mapperName
operator|=
name|mapperName
expr_stmt|;
name|this
operator|.
name|generatedValue
operator|=
name|generatedValue
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
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
name|ExternalMapper
operator|.
name|Builder
name|builder
init|=
operator|new
name|ExternalMapper
operator|.
name|Builder
argument_list|(
name|name
argument_list|,
name|generatedValue
argument_list|,
name|mapperName
argument_list|)
decl_stmt|;
name|parseField
argument_list|(
name|builder
argument_list|,
name|name
argument_list|,
name|node
argument_list|,
name|parserContext
argument_list|)
expr_stmt|;
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
name|parseMultiField
argument_list|(
name|builder
argument_list|,
name|name
argument_list|,
name|parserContext
argument_list|,
name|propName
argument_list|,
name|propNode
argument_list|)
condition|)
block|{
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
block|}
DECL|class|ExternalFieldType
specifier|static
class|class
name|ExternalFieldType
extends|extends
name|MappedFieldType
block|{
DECL|method|ExternalFieldType
specifier|public
name|ExternalFieldType
parameter_list|()
block|{}
DECL|method|ExternalFieldType
specifier|protected
name|ExternalFieldType
parameter_list|(
name|ExternalFieldType
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
name|ExternalFieldType
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
literal|"faketype"
return|;
block|}
block|}
DECL|field|generatedValue
specifier|private
specifier|final
name|String
name|generatedValue
decl_stmt|;
DECL|field|mapperName
specifier|private
specifier|final
name|String
name|mapperName
decl_stmt|;
DECL|field|binMapper
specifier|private
specifier|final
name|BinaryFieldMapper
name|binMapper
decl_stmt|;
DECL|field|boolMapper
specifier|private
specifier|final
name|BooleanFieldMapper
name|boolMapper
decl_stmt|;
DECL|field|pointMapper
specifier|private
specifier|final
name|BaseGeoPointFieldMapper
name|pointMapper
decl_stmt|;
DECL|field|shapeMapper
specifier|private
specifier|final
name|GeoShapeFieldMapper
name|shapeMapper
decl_stmt|;
DECL|field|stringMapper
specifier|private
specifier|final
name|FieldMapper
name|stringMapper
decl_stmt|;
DECL|method|ExternalMapper
specifier|public
name|ExternalMapper
parameter_list|(
name|String
name|simpleName
parameter_list|,
name|MappedFieldType
name|fieldType
parameter_list|,
name|String
name|generatedValue
parameter_list|,
name|String
name|mapperName
parameter_list|,
name|BinaryFieldMapper
name|binMapper
parameter_list|,
name|BooleanFieldMapper
name|boolMapper
parameter_list|,
name|BaseGeoPointFieldMapper
name|pointMapper
parameter_list|,
name|GeoShapeFieldMapper
name|shapeMapper
parameter_list|,
name|FieldMapper
name|stringMapper
parameter_list|,
name|Settings
name|indexSettings
parameter_list|,
name|MultiFields
name|multiFields
parameter_list|,
name|CopyTo
name|copyTo
parameter_list|)
block|{
name|super
argument_list|(
name|simpleName
argument_list|,
name|fieldType
argument_list|,
operator|new
name|ExternalFieldType
argument_list|()
argument_list|,
name|indexSettings
argument_list|,
name|multiFields
argument_list|,
name|copyTo
argument_list|)
expr_stmt|;
name|this
operator|.
name|generatedValue
operator|=
name|generatedValue
expr_stmt|;
name|this
operator|.
name|mapperName
operator|=
name|mapperName
expr_stmt|;
name|this
operator|.
name|binMapper
operator|=
name|binMapper
expr_stmt|;
name|this
operator|.
name|boolMapper
operator|=
name|boolMapper
expr_stmt|;
name|this
operator|.
name|pointMapper
operator|=
name|pointMapper
expr_stmt|;
name|this
operator|.
name|shapeMapper
operator|=
name|shapeMapper
expr_stmt|;
name|this
operator|.
name|stringMapper
operator|=
name|stringMapper
expr_stmt|;
block|}
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
name|byte
index|[]
name|bytes
init|=
literal|"Hello world"
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|defaultCharset
argument_list|()
argument_list|)
decl_stmt|;
name|binMapper
operator|.
name|parse
argument_list|(
name|context
operator|.
name|createExternalValueContext
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|boolMapper
operator|.
name|parse
argument_list|(
name|context
operator|.
name|createExternalValueContext
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Let's add a Dummy Point
name|Double
name|lat
init|=
literal|42.0
decl_stmt|;
name|Double
name|lng
init|=
literal|51.0
decl_stmt|;
name|GeoPoint
name|point
init|=
operator|new
name|GeoPoint
argument_list|(
name|lat
argument_list|,
name|lng
argument_list|)
decl_stmt|;
name|pointMapper
operator|.
name|parse
argument_list|(
name|context
operator|.
name|createExternalValueContext
argument_list|(
name|point
argument_list|)
argument_list|)
expr_stmt|;
comment|// Let's add a Dummy Shape
name|Point
name|shape
init|=
name|ShapeBuilders
operator|.
name|newPoint
argument_list|(
operator|-
literal|100
argument_list|,
literal|45
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|shapeMapper
operator|.
name|parse
argument_list|(
name|context
operator|.
name|createExternalValueContext
argument_list|(
name|shape
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|=
name|context
operator|.
name|createExternalValueContext
argument_list|(
name|generatedValue
argument_list|)
expr_stmt|;
comment|// Let's add a Original String
name|stringMapper
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|multiFields
operator|.
name|parse
argument_list|(
name|this
argument_list|,
name|context
argument_list|)
expr_stmt|;
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
comment|// ignore this for now
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
name|Iterators
operator|.
name|concat
argument_list|(
name|super
operator|.
name|iterator
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|binMapper
argument_list|,
name|boolMapper
argument_list|,
name|pointMapper
argument_list|,
name|shapeMapper
argument_list|,
name|stringMapper
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
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
name|builder
operator|.
name|startObject
argument_list|(
name|simpleName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|mapperName
argument_list|)
expr_stmt|;
name|multiFields
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
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
DECL|method|contentType
specifier|protected
name|String
name|contentType
parameter_list|()
block|{
return|return
name|mapperName
return|;
block|}
block|}
end_class

end_unit

