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
name|Fieldable
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
name|Booleans
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
name|common
operator|.
name|xcontent
operator|.
name|builder
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
name|MapperParsingException
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

begin_comment
comment|// TODO this can be made better, maybe storing a byte for it?
end_comment

begin_class
DECL|class|XContentBooleanFieldMapper
specifier|public
class|class
name|XContentBooleanFieldMapper
extends|extends
name|XContentFieldMapper
argument_list|<
name|Boolean
argument_list|>
block|{
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"boolean"
decl_stmt|;
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
extends|extends
name|XContentFieldMapper
operator|.
name|Defaults
block|{
DECL|field|OMIT_NORMS
specifier|public
specifier|static
specifier|final
name|boolean
name|OMIT_NORMS
init|=
literal|true
decl_stmt|;
DECL|field|NULL_VALUE
specifier|public
specifier|static
specifier|final
name|Boolean
name|NULL_VALUE
init|=
literal|null
decl_stmt|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|XContentFieldMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|XContentBooleanFieldMapper
argument_list|>
block|{
DECL|field|nullValue
specifier|private
name|Boolean
name|nullValue
init|=
name|Defaults
operator|.
name|NULL_VALUE
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
name|Defaults
operator|.
name|OMIT_NORMS
expr_stmt|;
name|this
operator|.
name|builder
operator|=
name|this
expr_stmt|;
block|}
DECL|method|nullValue
specifier|public
name|Builder
name|nullValue
parameter_list|(
name|boolean
name|nullValue
parameter_list|)
block|{
name|this
operator|.
name|nullValue
operator|=
name|nullValue
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|index
annotation|@
name|Override
specifier|public
name|Builder
name|index
parameter_list|(
name|Field
operator|.
name|Index
name|index
parameter_list|)
block|{
return|return
name|super
operator|.
name|index
argument_list|(
name|index
argument_list|)
return|;
block|}
DECL|method|store
annotation|@
name|Override
specifier|public
name|Builder
name|store
parameter_list|(
name|Field
operator|.
name|Store
name|store
parameter_list|)
block|{
return|return
name|super
operator|.
name|store
argument_list|(
name|store
argument_list|)
return|;
block|}
DECL|method|termVector
annotation|@
name|Override
specifier|public
name|Builder
name|termVector
parameter_list|(
name|Field
operator|.
name|TermVector
name|termVector
parameter_list|)
block|{
return|return
name|super
operator|.
name|termVector
argument_list|(
name|termVector
argument_list|)
return|;
block|}
DECL|method|boost
annotation|@
name|Override
specifier|public
name|Builder
name|boost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
return|return
name|super
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
return|;
block|}
DECL|method|indexName
annotation|@
name|Override
specifier|public
name|Builder
name|indexName
parameter_list|(
name|String
name|indexName
parameter_list|)
block|{
return|return
name|super
operator|.
name|indexName
argument_list|(
name|indexName
argument_list|)
return|;
block|}
DECL|method|omitTermFreqAndPositions
annotation|@
name|Override
specifier|public
name|Builder
name|omitTermFreqAndPositions
parameter_list|(
name|boolean
name|omitTermFreqAndPositions
parameter_list|)
block|{
return|return
name|super
operator|.
name|omitTermFreqAndPositions
argument_list|(
name|omitTermFreqAndPositions
argument_list|)
return|;
block|}
DECL|method|build
annotation|@
name|Override
specifier|public
name|XContentBooleanFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|XContentBooleanFieldMapper
argument_list|(
name|buildNames
argument_list|(
name|context
argument_list|)
argument_list|,
name|index
argument_list|,
name|store
argument_list|,
name|termVector
argument_list|,
name|boost
argument_list|,
name|omitNorms
argument_list|,
name|omitTermFreqAndPositions
argument_list|,
name|nullValue
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
name|XContentTypeParser
block|{
DECL|method|parse
annotation|@
name|Override
specifier|public
name|XContentMapper
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
name|XContentBooleanFieldMapper
operator|.
name|Builder
name|builder
init|=
name|booleanField
argument_list|(
name|name
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
name|node
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
name|nodeBooleanValue
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
block|}
DECL|field|nullValue
specifier|private
name|Boolean
name|nullValue
decl_stmt|;
DECL|method|XContentBooleanFieldMapper
specifier|protected
name|XContentBooleanFieldMapper
parameter_list|(
name|Names
name|names
parameter_list|,
name|Field
operator|.
name|Index
name|index
parameter_list|,
name|Field
operator|.
name|Store
name|store
parameter_list|,
name|Field
operator|.
name|TermVector
name|termVector
parameter_list|,
name|float
name|boost
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|omitTermFreqAndPositions
parameter_list|,
name|Boolean
name|nullValue
parameter_list|)
block|{
name|super
argument_list|(
name|names
argument_list|,
name|index
argument_list|,
name|store
argument_list|,
name|termVector
argument_list|,
name|boost
argument_list|,
name|omitNorms
argument_list|,
name|omitTermFreqAndPositions
argument_list|,
name|Lucene
operator|.
name|KEYWORD_ANALYZER
argument_list|,
name|Lucene
operator|.
name|KEYWORD_ANALYZER
argument_list|)
expr_stmt|;
name|this
operator|.
name|nullValue
operator|=
name|nullValue
expr_stmt|;
block|}
DECL|method|value
annotation|@
name|Override
specifier|public
name|Boolean
name|value
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
return|return
name|field
operator|.
name|stringValue
argument_list|()
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'T'
condition|?
name|Boolean
operator|.
name|TRUE
else|:
name|Boolean
operator|.
name|FALSE
return|;
block|}
DECL|method|valueAsString
annotation|@
name|Override
specifier|public
name|String
name|valueAsString
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
return|return
name|field
operator|.
name|stringValue
argument_list|()
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'T'
condition|?
literal|"true"
else|:
literal|"false"
return|;
block|}
DECL|method|indexedValue
annotation|@
name|Override
specifier|public
name|String
name|indexedValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
operator|||
name|value
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|"F"
return|;
block|}
if|if
condition|(
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|value
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return
literal|"T"
return|;
block|}
return|return
literal|"F"
return|;
block|}
DECL|method|parseCreateField
annotation|@
name|Override
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
name|XContentParser
operator|.
name|Token
name|token
init|=
name|context
operator|.
name|parser
argument_list|()
operator|.
name|currentToken
argument_list|()
decl_stmt|;
name|String
name|value
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_NULL
condition|)
block|{
if|if
condition|(
name|nullValue
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|nullValue
condition|?
literal|"T"
else|:
literal|"F"
expr_stmt|;
block|}
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
name|booleanValue
argument_list|()
condition|?
literal|"T"
else|:
literal|"F"
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
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
name|value
argument_list|,
name|store
argument_list|,
name|index
argument_list|,
name|termVector
argument_list|)
return|;
block|}
DECL|method|contentType
annotation|@
name|Override
specifier|protected
name|String
name|contentType
parameter_list|()
block|{
return|return
name|CONTENT_TYPE
return|;
block|}
DECL|method|doXContentBody
annotation|@
name|Override
specifier|protected
name|void
name|doXContentBody
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|doXContentBody
argument_list|(
name|builder
argument_list|)
expr_stmt|;
if|if
condition|(
name|nullValue
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"null_value"
argument_list|,
name|nullValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

