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
name|tika
operator|.
name|exception
operator|.
name|TikaException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Metadata
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
name|io
operator|.
name|FastByteArrayInputStream
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
name|FieldMapperListener
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|mapper
operator|.
name|attachments
operator|.
name|tika
operator|.
name|TikaInstance
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *<pre>  *      field1 : "..."  *</pre>  *<p>Or:  *<pre>  * {  *      file1 : {  *          _content_type : "application/pdf",  *          _name : "..../something.pdf",  *          content : ""  *      }  * }  *</pre>  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|XContentAttachmentMapper
specifier|public
class|class
name|XContentAttachmentMapper
implements|implements
name|XContentMapper
block|{
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"attachment"
decl_stmt|;
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
block|{
DECL|field|PATH_TYPE
specifier|public
specifier|static
specifier|final
name|ContentPath
operator|.
name|Type
name|PATH_TYPE
init|=
name|ContentPath
operator|.
name|Type
operator|.
name|FULL
decl_stmt|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|XContentMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|XContentAttachmentMapper
argument_list|>
block|{
DECL|field|pathType
specifier|private
name|ContentPath
operator|.
name|Type
name|pathType
init|=
name|Defaults
operator|.
name|PATH_TYPE
decl_stmt|;
DECL|field|contentBuilder
specifier|private
name|XContentStringFieldMapper
operator|.
name|Builder
name|contentBuilder
decl_stmt|;
DECL|field|titleBuilder
specifier|private
name|XContentStringFieldMapper
operator|.
name|Builder
name|titleBuilder
init|=
name|stringField
argument_list|(
literal|"title"
argument_list|)
decl_stmt|;
DECL|field|authorBuilder
specifier|private
name|XContentStringFieldMapper
operator|.
name|Builder
name|authorBuilder
init|=
name|stringField
argument_list|(
literal|"author"
argument_list|)
decl_stmt|;
DECL|field|keywordsBuilder
specifier|private
name|XContentStringFieldMapper
operator|.
name|Builder
name|keywordsBuilder
init|=
name|stringField
argument_list|(
literal|"keywords"
argument_list|)
decl_stmt|;
DECL|field|dateBuilder
specifier|private
name|XContentDateFieldMapper
operator|.
name|Builder
name|dateBuilder
init|=
name|dateField
argument_list|(
literal|"date"
argument_list|)
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
name|builder
operator|=
name|this
expr_stmt|;
name|this
operator|.
name|contentBuilder
operator|=
name|stringField
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|pathType
specifier|public
name|Builder
name|pathType
parameter_list|(
name|ContentPath
operator|.
name|Type
name|pathType
parameter_list|)
block|{
name|this
operator|.
name|pathType
operator|=
name|pathType
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|content
specifier|public
name|Builder
name|content
parameter_list|(
name|XContentStringFieldMapper
operator|.
name|Builder
name|content
parameter_list|)
block|{
name|this
operator|.
name|contentBuilder
operator|=
name|content
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|date
specifier|public
name|Builder
name|date
parameter_list|(
name|XContentDateFieldMapper
operator|.
name|Builder
name|date
parameter_list|)
block|{
name|this
operator|.
name|dateBuilder
operator|=
name|date
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|author
specifier|public
name|Builder
name|author
parameter_list|(
name|XContentStringFieldMapper
operator|.
name|Builder
name|author
parameter_list|)
block|{
name|this
operator|.
name|authorBuilder
operator|=
name|author
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|title
specifier|public
name|Builder
name|title
parameter_list|(
name|XContentStringFieldMapper
operator|.
name|Builder
name|title
parameter_list|)
block|{
name|this
operator|.
name|titleBuilder
operator|=
name|title
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|keywords
specifier|public
name|Builder
name|keywords
parameter_list|(
name|XContentStringFieldMapper
operator|.
name|Builder
name|keywords
parameter_list|)
block|{
name|this
operator|.
name|keywordsBuilder
operator|=
name|keywords
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
annotation|@
name|Override
specifier|public
name|XContentAttachmentMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|ContentPath
operator|.
name|Type
name|origPathType
init|=
name|context
operator|.
name|path
argument_list|()
operator|.
name|pathType
argument_list|()
decl_stmt|;
name|context
operator|.
name|path
argument_list|()
operator|.
name|pathType
argument_list|(
name|pathType
argument_list|)
expr_stmt|;
comment|// create the content mapper under the actual name
name|XContentStringFieldMapper
name|contentMapper
init|=
name|contentBuilder
operator|.
name|build
argument_list|(
name|context
argument_list|)
decl_stmt|;
comment|// create the DC one under the name
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
name|XContentDateFieldMapper
name|dateMapper
init|=
name|dateBuilder
operator|.
name|build
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|XContentStringFieldMapper
name|authorMapper
init|=
name|authorBuilder
operator|.
name|build
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|XContentStringFieldMapper
name|titleMapper
init|=
name|titleBuilder
operator|.
name|build
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|XContentStringFieldMapper
name|keywordsMapper
init|=
name|keywordsBuilder
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
name|context
operator|.
name|path
argument_list|()
operator|.
name|pathType
argument_list|(
name|origPathType
argument_list|)
expr_stmt|;
return|return
operator|new
name|XContentAttachmentMapper
argument_list|(
name|name
argument_list|,
name|pathType
argument_list|,
name|contentMapper
argument_list|,
name|dateMapper
argument_list|,
name|titleMapper
argument_list|,
name|authorMapper
argument_list|,
name|keywordsMapper
argument_list|)
return|;
block|}
block|}
comment|/**      *<pre>      *  field1 : { type : "attachment" }      *</pre>      * Or:      *<pre>      *  field1 : {      *      type : "attachment",      *      fields : {      *          field1 : {type : "binary"},      *          title : {store : "yes"},      *          date : {store : "yes"}      *      }      * }      *</pre>      *      * @author kimchy (shay.banon)      */
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
name|XContentAttachmentMapper
operator|.
name|Builder
name|builder
init|=
operator|new
name|XContentAttachmentMapper
operator|.
name|Builder
argument_list|(
name|name
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
name|node
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|fieldName
init|=
name|entry
operator|.
name|getKey
argument_list|()
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
name|pathType
argument_list|(
name|parsePathType
argument_list|(
name|name
argument_list|,
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
name|fieldName
operator|.
name|equals
argument_list|(
literal|"fields"
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fieldsNode
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
name|entry1
range|:
name|fieldsNode
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|propName
init|=
name|entry1
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|propNode
init|=
name|entry1
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|propName
argument_list|)
condition|)
block|{
comment|// that is the content
name|builder
operator|.
name|content
argument_list|(
operator|(
name|XContentStringFieldMapper
operator|.
name|Builder
operator|)
name|parserContext
operator|.
name|typeParser
argument_list|(
literal|"string"
argument_list|)
operator|.
name|parse
argument_list|(
name|name
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|propNode
argument_list|,
name|parserContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"date"
operator|.
name|equals
argument_list|(
name|propName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|date
argument_list|(
operator|(
name|XContentDateFieldMapper
operator|.
name|Builder
operator|)
name|parserContext
operator|.
name|typeParser
argument_list|(
literal|"date"
argument_list|)
operator|.
name|parse
argument_list|(
literal|"date"
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|propNode
argument_list|,
name|parserContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"title"
operator|.
name|equals
argument_list|(
name|propName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|title
argument_list|(
operator|(
name|XContentStringFieldMapper
operator|.
name|Builder
operator|)
name|parserContext
operator|.
name|typeParser
argument_list|(
literal|"string"
argument_list|)
operator|.
name|parse
argument_list|(
literal|"title"
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|propNode
argument_list|,
name|parserContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"author"
operator|.
name|equals
argument_list|(
name|propName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|author
argument_list|(
operator|(
name|XContentStringFieldMapper
operator|.
name|Builder
operator|)
name|parserContext
operator|.
name|typeParser
argument_list|(
literal|"string"
argument_list|)
operator|.
name|parse
argument_list|(
literal|"author"
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|propNode
argument_list|,
name|parserContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"keywords"
operator|.
name|equals
argument_list|(
name|propName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|keywords
argument_list|(
operator|(
name|XContentStringFieldMapper
operator|.
name|Builder
operator|)
name|parserContext
operator|.
name|typeParser
argument_list|(
literal|"string"
argument_list|)
operator|.
name|parse
argument_list|(
literal|"keywords"
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|propNode
argument_list|,
name|parserContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|builder
return|;
block|}
block|}
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|pathType
specifier|private
specifier|final
name|ContentPath
operator|.
name|Type
name|pathType
decl_stmt|;
DECL|field|contentMapper
specifier|private
specifier|final
name|XContentStringFieldMapper
name|contentMapper
decl_stmt|;
DECL|field|dateMapper
specifier|private
specifier|final
name|XContentDateFieldMapper
name|dateMapper
decl_stmt|;
DECL|field|authorMapper
specifier|private
specifier|final
name|XContentStringFieldMapper
name|authorMapper
decl_stmt|;
DECL|field|titleMapper
specifier|private
specifier|final
name|XContentStringFieldMapper
name|titleMapper
decl_stmt|;
DECL|field|keywordsMapper
specifier|private
specifier|final
name|XContentStringFieldMapper
name|keywordsMapper
decl_stmt|;
DECL|method|XContentAttachmentMapper
specifier|public
name|XContentAttachmentMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|ContentPath
operator|.
name|Type
name|pathType
parameter_list|,
name|XContentStringFieldMapper
name|contentMapper
parameter_list|,
name|XContentDateFieldMapper
name|dateMapper
parameter_list|,
name|XContentStringFieldMapper
name|titleMapper
parameter_list|,
name|XContentStringFieldMapper
name|authorMapper
parameter_list|,
name|XContentStringFieldMapper
name|keywordsMapper
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|pathType
operator|=
name|pathType
expr_stmt|;
name|this
operator|.
name|contentMapper
operator|=
name|contentMapper
expr_stmt|;
name|this
operator|.
name|dateMapper
operator|=
name|dateMapper
expr_stmt|;
name|this
operator|.
name|titleMapper
operator|=
name|titleMapper
expr_stmt|;
name|this
operator|.
name|authorMapper
operator|=
name|authorMapper
expr_stmt|;
name|this
operator|.
name|keywordsMapper
operator|=
name|keywordsMapper
expr_stmt|;
block|}
DECL|method|name
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|parse
annotation|@
name|Override
specifier|public
name|void
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
name|content
init|=
literal|null
decl_stmt|;
name|String
name|contentType
init|=
literal|null
decl_stmt|;
name|String
name|name
init|=
literal|null
decl_stmt|;
name|XContentParser
name|parser
init|=
name|context
operator|.
name|parser
argument_list|()
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|currentToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
name|content
operator|=
name|parser
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
if|if
condition|(
literal|"content"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|content
operator|=
name|parser
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"_content_type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|contentType
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"_name"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|name
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
if|if
condition|(
name|contentType
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|contentType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
name|String
name|parsedContent
decl_stmt|;
try|try
block|{
name|parsedContent
operator|=
name|tika
argument_list|()
operator|.
name|parseToString
argument_list|(
operator|new
name|FastByteArrayInputStream
argument_list|(
name|content
argument_list|)
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Failed to extract text for ["
operator|+
name|name
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|context
operator|.
name|externalValue
argument_list|(
name|parsedContent
argument_list|)
expr_stmt|;
name|contentMapper
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|context
operator|.
name|externalValue
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|DATE
argument_list|)
argument_list|)
expr_stmt|;
name|dateMapper
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|context
operator|.
name|externalValue
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|titleMapper
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|context
operator|.
name|externalValue
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|AUTHOR
argument_list|)
argument_list|)
expr_stmt|;
name|authorMapper
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|context
operator|.
name|externalValue
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|KEYWORDS
argument_list|)
argument_list|)
expr_stmt|;
name|keywordsMapper
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|merge
annotation|@
name|Override
specifier|public
name|void
name|merge
parameter_list|(
name|XContentMapper
name|mergeWith
parameter_list|,
name|MergeContext
name|mergeContext
parameter_list|)
throws|throws
name|MergeMappingException
block|{
comment|// ignore this for now
block|}
DECL|method|traverse
annotation|@
name|Override
specifier|public
name|void
name|traverse
parameter_list|(
name|FieldMapperListener
name|fieldMapperListener
parameter_list|)
block|{
name|contentMapper
operator|.
name|traverse
argument_list|(
name|fieldMapperListener
argument_list|)
expr_stmt|;
name|dateMapper
operator|.
name|traverse
argument_list|(
name|fieldMapperListener
argument_list|)
expr_stmt|;
name|titleMapper
operator|.
name|traverse
argument_list|(
name|fieldMapperListener
argument_list|)
expr_stmt|;
name|authorMapper
operator|.
name|traverse
argument_list|(
name|fieldMapperListener
argument_list|)
expr_stmt|;
name|keywordsMapper
operator|.
name|traverse
argument_list|(
name|fieldMapperListener
argument_list|)
expr_stmt|;
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
name|builder
operator|.
name|startObject
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|CONTENT_TYPE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"path"
argument_list|,
name|pathType
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"fields"
argument_list|)
expr_stmt|;
name|contentMapper
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|authorMapper
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|titleMapper
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|dateMapper
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|keywordsMapper
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
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

