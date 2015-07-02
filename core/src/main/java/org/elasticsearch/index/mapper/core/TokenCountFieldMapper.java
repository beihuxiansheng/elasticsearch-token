begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.core
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|core
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
name|elasticsearch
operator|.
name|common
operator|.
name|Explicit
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|analysis
operator|.
name|NumericIntegerAnalyzer
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
name|MergeMappingException
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
name|MergeResult
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
name|StringFieldMapper
operator|.
name|ValueAndBoost
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
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexOptions
operator|.
name|NONE
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
name|MapperBuilders
operator|.
name|tokenCountField
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
name|parseNumberField
import|;
end_import

begin_comment
comment|/**  * A {@link FieldMapper} that takes a string and writes a count of the tokens in that string  * to the index.  In most ways the mapper acts just like an {@link IntegerFieldMapper}.  */
end_comment

begin_class
DECL|class|TokenCountFieldMapper
specifier|public
class|class
name|TokenCountFieldMapper
extends|extends
name|IntegerFieldMapper
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
extends|extends
name|IntegerFieldMapper
operator|.
name|Defaults
block|{      }
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|NumberFieldMapper
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
name|PRECISION_STEP_32_BIT
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
name|TokenCountFieldMapper
name|fieldMapper
init|=
operator|new
name|TokenCountFieldMapper
argument_list|(
name|name
argument_list|,
name|fieldType
argument_list|,
name|docValues
argument_list|,
name|ignoreMalformed
argument_list|(
name|context
argument_list|)
argument_list|,
name|coerce
argument_list|(
name|context
argument_list|)
argument_list|,
name|fieldDataSettings
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
decl_stmt|;
name|fieldMapper
operator|.
name|includeInAll
argument_list|(
name|includeInAll
argument_list|)
expr_stmt|;
return|return
name|fieldMapper
return|;
block|}
annotation|@
name|Override
DECL|method|makeNumberAnalyzer
specifier|protected
name|NamedAnalyzer
name|makeNumberAnalyzer
parameter_list|(
name|int
name|precisionStep
parameter_list|)
block|{
return|return
name|NumericIntegerAnalyzer
operator|.
name|buildNamedAnalyzer
argument_list|(
name|precisionStep
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|maxPrecisionStep
specifier|protected
name|int
name|maxPrecisionStep
parameter_list|()
block|{
return|return
literal|32
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
name|TokenCountFieldMapper
operator|.
name|Builder
name|builder
init|=
name|tokenCountField
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
name|analysisService
argument_list|()
operator|.
name|analyzer
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
name|parseNumberField
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
name|Boolean
name|docValues
parameter_list|,
name|Explicit
argument_list|<
name|Boolean
argument_list|>
name|ignoreMalformed
parameter_list|,
name|Explicit
argument_list|<
name|Boolean
argument_list|>
name|coerce
parameter_list|,
name|Settings
name|fieldDataSettings
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
name|docValues
argument_list|,
name|ignoreMalformed
argument_list|,
name|coerce
argument_list|,
name|fieldDataSettings
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
name|Field
argument_list|>
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
name|ValueAndBoost
name|valueAndBoost
init|=
name|StringFieldMapper
operator|.
name|parseCreateFieldForString
argument_list|(
name|context
argument_list|,
literal|null
comment|/* Out null value is an int so we convert*/
argument_list|,
name|fieldType
argument_list|()
operator|.
name|boost
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|valueAndBoost
operator|.
name|value
argument_list|()
operator|==
literal|null
operator|&&
name|fieldType
argument_list|()
operator|.
name|nullValue
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
operator|!=
name|NONE
operator|||
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
operator|||
name|fieldType
argument_list|()
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
name|int
name|count
decl_stmt|;
if|if
condition|(
name|valueAndBoost
operator|.
name|value
argument_list|()
operator|==
literal|null
condition|)
block|{
name|count
operator|=
name|fieldType
argument_list|()
operator|.
name|nullValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|count
operator|=
name|countPositions
argument_list|(
name|analyzer
operator|.
name|analyzer
argument_list|()
operator|.
name|tokenStream
argument_list|(
name|simpleName
argument_list|()
argument_list|,
name|valueAndBoost
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|addIntegerFields
argument_list|(
name|context
argument_list|,
name|fields
argument_list|,
name|count
argument_list|,
name|valueAndBoost
operator|.
name|boost
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|context
operator|.
name|ignoredValue
argument_list|(
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|valueAndBoost
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Count position increments in a token stream.  Package private for testing.      * @param tokenStream token stream to count      * @return number of position increments in a token stream      * @throws IOException if tokenStream throws it      */
DECL|method|countPositions
specifier|static
name|int
name|countPositions
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
throws|throws
name|IOException
block|{
try|try
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
finally|finally
block|{
name|tokenStream
operator|.
name|close
argument_list|()
expr_stmt|;
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
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|Mapper
name|mergeWith
parameter_list|,
name|MergeResult
name|mergeResult
parameter_list|)
throws|throws
name|MergeMappingException
block|{
name|super
operator|.
name|merge
argument_list|(
name|mergeWith
argument_list|,
name|mergeResult
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|mergeWith
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|mergeResult
operator|.
name|simulate
argument_list|()
condition|)
block|{
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
annotation|@
name|Override
DECL|method|isGenerated
specifier|public
name|boolean
name|isGenerated
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

