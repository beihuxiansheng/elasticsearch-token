begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.attachments.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|attachments
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
name|codehaus
operator|.
name|jackson
operator|.
name|JsonNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|node
operator|.
name|ObjectNode
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
name|json
operator|.
name|JsonDateFieldMapper
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
name|json
operator|.
name|JsonMapper
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
name|json
operator|.
name|JsonStringFieldMapper
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
name|json
operator|.
name|JsonTypeParser
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
name|json
operator|.
name|JsonTypeParsers
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *<pre>  *  field1 : { type : "attachment" }  *</pre>  * Or:  *<pre>  *  field1 : {  *      type : "attachment",  *      fields : {  *          field1 : {type : "binary"},  *          title : {store : "yes"},  *          date : {store : "yes"}  *      }  * }  *</pre>  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|JsonAttachmentTypeParser
specifier|public
class|class
name|JsonAttachmentTypeParser
implements|implements
name|JsonTypeParser
block|{
DECL|method|parse
annotation|@
name|Override
specifier|public
name|JsonMapper
operator|.
name|Builder
name|parse
parameter_list|(
name|String
name|name
parameter_list|,
name|JsonNode
name|node
parameter_list|,
name|ParserContext
name|parserContext
parameter_list|)
throws|throws
name|MapperParsingException
block|{
name|ObjectNode
name|attachmentNode
init|=
operator|(
name|ObjectNode
operator|)
name|node
decl_stmt|;
name|JsonAttachmentMapper
operator|.
name|Builder
name|builder
init|=
operator|new
name|JsonAttachmentMapper
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
name|JsonNode
argument_list|>
argument_list|>
name|fieldsIt
init|=
name|attachmentNode
operator|.
name|getFields
argument_list|()
init|;
name|fieldsIt
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
name|JsonNode
argument_list|>
name|entry
init|=
name|fieldsIt
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|JsonNode
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
literal|"pathType"
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
name|getValueAsText
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
name|ObjectNode
name|fieldsNode
init|=
operator|(
name|ObjectNode
operator|)
name|fieldNode
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
name|JsonNode
argument_list|>
argument_list|>
name|propsIt
init|=
name|fieldsNode
operator|.
name|getFields
argument_list|()
init|;
name|propsIt
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
name|JsonNode
argument_list|>
name|entry1
init|=
name|propsIt
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|propName
init|=
name|entry1
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|JsonNode
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
name|JsonStringFieldMapper
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
name|JsonDateFieldMapper
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
name|JsonStringFieldMapper
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
name|JsonStringFieldMapper
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
name|JsonStringFieldMapper
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
end_class

end_unit

