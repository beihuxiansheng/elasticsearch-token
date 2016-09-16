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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|analysis
operator|.
name|NamedAnalyzer
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
name|common
operator|.
name|xcontent
operator|.
name|support
operator|.
name|XContentMapValues
operator|.
name|nodeIntegerValue
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
name|TypeParsers
operator|.
name|parseField
import|;
end_import

begin_comment
comment|/**  * A {@link FieldMapper} that takes a string and writes a count of the tokens in that string  * to the index.  In most ways the mapper acts just like an {@link LegacyIntegerFieldMapper}.  */
end_comment

begin_class
DECL|class|TokenCountFieldMapper
specifier|public
class|class
name|TokenCountFieldMapper
extends|extends
name|FieldMapper
block|{
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"token_count"
decl_stmt|;
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
block|{
DECL|field|FIELD_TYPE
specifier|public
specifier|static
specifier|final
name|MappedFieldType
name|FIELD_TYPE
init|=
operator|new
name|NumberFieldMapper
operator|.
name|NumberFieldType
argument_list|(
name|NumberFieldMapper
operator|.
name|NumberType
operator|.
name|INTEGER
argument_list|)
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
name|TokenCountFieldMapper
argument_list|>
block|{
DECL|field|analyzer
specifier|private
name|NamedAnalyzer
name|analyzer
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|,
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|)
expr_stmt|;
name|builder
operator|=
name|this
expr_stmt|;
block|}
DECL|method|analyzer
specifier|public
name|Builder
name|analyzer
parameter_list|(
name|NamedAnalyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|analyzer
specifier|public
name|NamedAnalyzer
name|analyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|TokenCountFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|setupFieldType
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenCountFieldMapper
argument_list|(
name|name
argument_list|,
name|fieldType
argument_list|,
name|defaultFieldType
argument_list|,
name|context
operator|.
name|indexSettings
argument_list|()
argument_list|,
name|analyzer
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
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
if|if
condition|(
name|parserContext
operator|.
name|indexVersionCreated
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_5_0_0_alpha2
argument_list|)
condition|)
block|{
return|return
operator|new
name|LegacyTokenCountFieldMapper
operator|.
name|TypeParser
argument_list|()
operator|.
name|parse
argument_list|(
name|name
argument_list|,
name|node
argument_list|,
name|parserContext
argument_list|)
return|;
block|}
name|TokenCountFieldMapper
operator|.
name|Builder
name|builder
init|=
operator|new
name|TokenCountFieldMapper
operator|.
name|Builder
argument_list|(
name|name
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
name|propName
init|=
name|entry
operator|.
name|getKey
argument_list|()
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
name|nodeIntegerValue
argument_list|(
name|propNode
argument_list|)
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
name|propName
operator|.
name|equals
argument_list|(
literal|"analyzer"
argument_list|)
condition|)
block|{
name|NamedAnalyzer
name|analyzer
init|=
name|parserContext
operator|.
name|getIndexAnalyzers
argument_list|()
operator|.
name|get
argument_list|(
name|propNode
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Analyzer ["
operator|+
name|propNode
operator|.
name|toString
argument_list|()
operator|+
literal|"] not found for field ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|builder
operator|.
name|analyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
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
if|if
condition|(
name|builder
operator|.
name|analyzer
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Analyzer must be set for field ["
operator|+
name|name
operator|+
literal|"] but wasn't."
argument_list|)
throw|;
block|}
return|return
name|builder
return|;
block|}
block|}
DECL|field|analyzer
specifier|private
name|NamedAnalyzer
name|analyzer
decl_stmt|;
DECL|method|TokenCountFieldMapper
specifier|protected
name|TokenCountFieldMapper
parameter_list|(
name|String
name|simpleName
parameter_list|,
name|MappedFieldType
name|fieldType
parameter_list|,
name|MappedFieldType
name|defaultFieldType
parameter_list|,
name|Settings
name|indexSettings
parameter_list|,
name|NamedAnalyzer
name|analyzer
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
name|defaultFieldType
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
name|analyzer
operator|=
name|analyzer
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
specifier|final
name|String
name|value
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|externalValueSet
argument_list|()
condition|)
block|{
name|value
operator|=
name|context
operator|.
name|externalValue
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|context
operator|.
name|parser
argument_list|()
operator|.
name|textOrNull
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|tokenCount
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|tokenCount
operator|=
operator|(
name|Integer
operator|)
name|fieldType
argument_list|()
operator|.
name|nullValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|tokenCount
operator|=
name|countPositions
argument_list|(
name|analyzer
argument_list|,
name|name
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|boolean
name|indexed
init|=
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|NONE
decl_stmt|;
name|boolean
name|docValued
init|=
name|fieldType
argument_list|()
operator|.
name|hasDocValues
argument_list|()
decl_stmt|;
name|boolean
name|stored
init|=
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
decl_stmt|;
name|fields
operator|.
name|addAll
argument_list|(
name|NumberFieldMapper
operator|.
name|NumberType
operator|.
name|INTEGER
operator|.
name|createFields
argument_list|(
name|fieldType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|tokenCount
argument_list|,
name|indexed
argument_list|,
name|docValued
argument_list|,
name|stored
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Count position increments in a token stream.  Package private for testing.      * @param analyzer analyzer to create token stream      * @param fieldName field name to pass to analyzer      * @param fieldValue field value to pass to analyzer      * @return number of position increments in a token stream      * @throws IOException if tokenStream throws it      */
DECL|method|countPositions
specifier|static
name|int
name|countPositions
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|String
name|fieldValue
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|TokenStream
name|tokenStream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|fieldName
argument_list|,
name|fieldValue
argument_list|)
init|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|PositionIncrementAttribute
name|position
init|=
name|tokenStream
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|tokenStream
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|tokenStream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|count
operator|+=
name|position
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
name|tokenStream
operator|.
name|end
argument_list|()
expr_stmt|;
name|count
operator|+=
name|position
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
return|return
name|count
return|;
block|}
block|}
comment|/**      * Name of analyzer.      * @return name of analyzer      */
DECL|method|analyzer
specifier|public
name|String
name|analyzer
parameter_list|()
block|{
return|return
name|analyzer
operator|.
name|name
argument_list|()
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
name|this
operator|.
name|analyzer
operator|=
operator|(
operator|(
name|TokenCountFieldMapper
operator|)
name|mergeWith
operator|)
operator|.
name|analyzer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doXContentBody
specifier|protected
name|void
name|doXContentBody
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|boolean
name|includeDefaults
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|doXContentBody
argument_list|(
name|builder
argument_list|,
name|includeDefaults
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"analyzer"
argument_list|,
name|analyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

