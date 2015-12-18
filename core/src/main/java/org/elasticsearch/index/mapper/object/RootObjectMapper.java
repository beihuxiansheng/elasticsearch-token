begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.object
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|object
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
name|joda
operator|.
name|FormatDateTimeFormatter
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
name|joda
operator|.
name|Joda
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
name|ToXContent
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
name|DateFieldMapper
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
name|ArrayList
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
name|HashSet
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|nodeBooleanValue
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
name|parseDateTimeFormatter
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RootObjectMapper
specifier|public
class|class
name|RootObjectMapper
extends|extends
name|ObjectMapper
block|{
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
block|{
DECL|field|DYNAMIC_DATE_TIME_FORMATTERS
specifier|public
specifier|static
specifier|final
name|FormatDateTimeFormatter
index|[]
name|DYNAMIC_DATE_TIME_FORMATTERS
init|=
operator|new
name|FormatDateTimeFormatter
index|[]
block|{
name|DateFieldMapper
operator|.
name|Defaults
operator|.
name|DATE_TIME_FORMATTER
block|,
name|Joda
operator|.
name|getStrictStandardDateFormatter
argument_list|()
block|}
decl_stmt|;
DECL|field|DATE_DETECTION
specifier|public
specifier|static
specifier|final
name|boolean
name|DATE_DETECTION
init|=
literal|true
decl_stmt|;
DECL|field|NUMERIC_DETECTION
specifier|public
specifier|static
specifier|final
name|boolean
name|NUMERIC_DETECTION
init|=
literal|false
decl_stmt|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|ObjectMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|RootObjectMapper
argument_list|>
block|{
DECL|field|dynamicTemplates
specifier|protected
specifier|final
name|List
argument_list|<
name|DynamicTemplate
argument_list|>
name|dynamicTemplates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// we use this to filter out seen date formats, because we might get duplicates during merging
DECL|field|seenDateFormats
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|seenDateFormats
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|dynamicDateTimeFormatters
specifier|protected
name|List
argument_list|<
name|FormatDateTimeFormatter
argument_list|>
name|dynamicDateTimeFormatters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|dateDetection
specifier|protected
name|boolean
name|dateDetection
init|=
name|Defaults
operator|.
name|DATE_DETECTION
decl_stmt|;
DECL|field|numericDetection
specifier|protected
name|boolean
name|numericDetection
init|=
name|Defaults
operator|.
name|NUMERIC_DETECTION
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
block|}
DECL|method|noDynamicDateTimeFormatter
specifier|public
name|Builder
name|noDynamicDateTimeFormatter
parameter_list|()
block|{
name|this
operator|.
name|dynamicDateTimeFormatters
operator|=
literal|null
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|dynamicDateTimeFormatter
specifier|public
name|Builder
name|dynamicDateTimeFormatter
parameter_list|(
name|Iterable
argument_list|<
name|FormatDateTimeFormatter
argument_list|>
name|dateTimeFormatters
parameter_list|)
block|{
for|for
control|(
name|FormatDateTimeFormatter
name|dateTimeFormatter
range|:
name|dateTimeFormatters
control|)
block|{
if|if
condition|(
operator|!
name|seenDateFormats
operator|.
name|contains
argument_list|(
name|dateTimeFormatter
operator|.
name|format
argument_list|()
argument_list|)
condition|)
block|{
name|seenDateFormats
operator|.
name|add
argument_list|(
name|dateTimeFormatter
operator|.
name|format
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|dynamicDateTimeFormatters
operator|.
name|add
argument_list|(
name|dateTimeFormatter
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
return|;
block|}
DECL|method|add
specifier|public
name|Builder
name|add
parameter_list|(
name|DynamicTemplate
name|dynamicTemplate
parameter_list|)
block|{
name|this
operator|.
name|dynamicTemplates
operator|.
name|add
argument_list|(
name|dynamicTemplate
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|add
specifier|public
name|Builder
name|add
parameter_list|(
name|DynamicTemplate
modifier|...
name|dynamicTemplate
parameter_list|)
block|{
for|for
control|(
name|DynamicTemplate
name|template
range|:
name|dynamicTemplate
control|)
block|{
name|this
operator|.
name|dynamicTemplates
operator|.
name|add
argument_list|(
name|template
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|createMapper
specifier|protected
name|ObjectMapper
name|createMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|fullPath
parameter_list|,
name|boolean
name|enabled
parameter_list|,
name|Nested
name|nested
parameter_list|,
name|Dynamic
name|dynamic
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Mapper
argument_list|>
name|mappers
parameter_list|,
annotation|@
name|Nullable
name|Settings
name|settings
parameter_list|)
block|{
assert|assert
operator|!
name|nested
operator|.
name|isNested
argument_list|()
assert|;
name|FormatDateTimeFormatter
index|[]
name|dates
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|dynamicDateTimeFormatters
operator|==
literal|null
condition|)
block|{
name|dates
operator|=
operator|new
name|FormatDateTimeFormatter
index|[
literal|0
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dynamicDateTimeFormatters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// add the default one
name|dates
operator|=
name|Defaults
operator|.
name|DYNAMIC_DATE_TIME_FORMATTERS
expr_stmt|;
block|}
else|else
block|{
name|dates
operator|=
name|dynamicDateTimeFormatters
operator|.
name|toArray
argument_list|(
operator|new
name|FormatDateTimeFormatter
index|[
name|dynamicDateTimeFormatters
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|RootObjectMapper
argument_list|(
name|name
argument_list|,
name|enabled
argument_list|,
name|dynamic
argument_list|,
name|mappers
argument_list|,
name|dates
argument_list|,
name|dynamicTemplates
operator|.
name|toArray
argument_list|(
operator|new
name|DynamicTemplate
index|[
name|dynamicTemplates
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|dateDetection
argument_list|,
name|numericDetection
argument_list|)
return|;
block|}
block|}
DECL|class|TypeParser
specifier|public
specifier|static
class|class
name|TypeParser
extends|extends
name|ObjectMapper
operator|.
name|TypeParser
block|{
annotation|@
name|Override
DECL|method|createBuilder
specifier|protected
name|ObjectMapper
operator|.
name|Builder
name|createBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|name
argument_list|)
return|;
block|}
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
name|ObjectMapper
operator|.
name|Builder
name|builder
init|=
name|createBuilder
argument_list|(
name|name
argument_list|)
decl_stmt|;
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
decl_stmt|;
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
name|parseObjectOrDocumentTypeProperties
argument_list|(
name|fieldName
argument_list|,
name|fieldNode
argument_list|,
name|parserContext
argument_list|,
name|builder
argument_list|)
operator|||
name|processField
argument_list|(
name|builder
argument_list|,
name|fieldName
argument_list|,
name|fieldNode
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
DECL|method|processField
specifier|protected
name|boolean
name|processField
parameter_list|(
name|ObjectMapper
operator|.
name|Builder
name|builder
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|Object
name|fieldNode
parameter_list|)
block|{
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"date_formats"
argument_list|)
operator|||
name|fieldName
operator|.
name|equals
argument_list|(
literal|"dynamic_date_formats"
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|FormatDateTimeFormatter
argument_list|>
name|dateTimeFormatters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
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
name|node1
range|:
operator|(
name|List
operator|)
name|fieldNode
control|)
block|{
if|if
condition|(
name|node1
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"epoch_"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Epoch ["
operator|+
name|node1
operator|.
name|toString
argument_list|()
operator|+
literal|"] is not supported as dynamic date format"
argument_list|)
throw|;
block|}
name|dateTimeFormatters
operator|.
name|add
argument_list|(
name|parseDateTimeFormatter
argument_list|(
name|node1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"none"
operator|.
name|equals
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|dateTimeFormatters
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|dateTimeFormatters
operator|.
name|add
argument_list|(
name|parseDateTimeFormatter
argument_list|(
name|fieldNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dateTimeFormatters
operator|==
literal|null
condition|)
block|{
operator|(
operator|(
name|Builder
operator|)
name|builder
operator|)
operator|.
name|noDynamicDateTimeFormatter
argument_list|()
expr_stmt|;
block|}
else|else
block|{
operator|(
operator|(
name|Builder
operator|)
name|builder
operator|)
operator|.
name|dynamicDateTimeFormatter
argument_list|(
name|dateTimeFormatters
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"dynamic_templates"
argument_list|)
condition|)
block|{
comment|//  "dynamic_templates" : [
comment|//      {
comment|//          "template_1" : {
comment|//              "match" : "*_test",
comment|//              "match_mapping_type" : "string",
comment|//              "mapping" : { "type" : "string", "store" : "yes" }
comment|//          }
comment|//      }
comment|//  ]
name|List
name|tmplNodes
init|=
operator|(
name|List
operator|)
name|fieldNode
decl_stmt|;
for|for
control|(
name|Object
name|tmplNode
range|:
name|tmplNodes
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|tmpl
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|tmplNode
decl_stmt|;
if|if
condition|(
name|tmpl
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
literal|"A dynamic template must be defined with a name"
argument_list|)
throw|;
block|}
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
name|tmpl
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
operator|(
operator|(
name|Builder
operator|)
name|builder
operator|)
operator|.
name|add
argument_list|(
name|DynamicTemplate
operator|.
name|parse
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"date_detection"
argument_list|)
condition|)
block|{
operator|(
operator|(
name|Builder
operator|)
name|builder
operator|)
operator|.
name|dateDetection
operator|=
name|nodeBooleanValue
argument_list|(
name|fieldNode
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|fieldName
operator|.
name|equals
argument_list|(
literal|"numeric_detection"
argument_list|)
condition|)
block|{
operator|(
operator|(
name|Builder
operator|)
name|builder
operator|)
operator|.
name|numericDetection
operator|=
name|nodeBooleanValue
argument_list|(
name|fieldNode
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
DECL|field|dynamicDateTimeFormatters
specifier|private
specifier|final
name|FormatDateTimeFormatter
index|[]
name|dynamicDateTimeFormatters
decl_stmt|;
DECL|field|dateDetection
specifier|private
specifier|final
name|boolean
name|dateDetection
decl_stmt|;
DECL|field|numericDetection
specifier|private
specifier|final
name|boolean
name|numericDetection
decl_stmt|;
DECL|field|dynamicTemplates
specifier|private
specifier|volatile
name|DynamicTemplate
name|dynamicTemplates
index|[]
decl_stmt|;
DECL|method|RootObjectMapper
name|RootObjectMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|enabled
parameter_list|,
name|Dynamic
name|dynamic
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Mapper
argument_list|>
name|mappers
parameter_list|,
name|FormatDateTimeFormatter
index|[]
name|dynamicDateTimeFormatters
parameter_list|,
name|DynamicTemplate
name|dynamicTemplates
index|[]
parameter_list|,
name|boolean
name|dateDetection
parameter_list|,
name|boolean
name|numericDetection
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|name
argument_list|,
name|enabled
argument_list|,
name|Nested
operator|.
name|NO
argument_list|,
name|dynamic
argument_list|,
name|mappers
argument_list|)
expr_stmt|;
name|this
operator|.
name|dynamicTemplates
operator|=
name|dynamicTemplates
expr_stmt|;
name|this
operator|.
name|dynamicDateTimeFormatters
operator|=
name|dynamicDateTimeFormatters
expr_stmt|;
name|this
operator|.
name|dateDetection
operator|=
name|dateDetection
expr_stmt|;
name|this
operator|.
name|numericDetection
operator|=
name|numericDetection
expr_stmt|;
block|}
comment|/** Return a copy of this mapper that has the given {@code mapper} as a      *  sub mapper. */
DECL|method|copyAndPutMapper
specifier|public
name|RootObjectMapper
name|copyAndPutMapper
parameter_list|(
name|Mapper
name|mapper
parameter_list|)
block|{
name|RootObjectMapper
name|clone
init|=
operator|(
name|RootObjectMapper
operator|)
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|putMapper
argument_list|(
name|mapper
argument_list|)
expr_stmt|;
return|return
name|clone
return|;
block|}
annotation|@
name|Override
DECL|method|mappingUpdate
specifier|public
name|ObjectMapper
name|mappingUpdate
parameter_list|(
name|Mapper
name|mapper
parameter_list|)
block|{
name|RootObjectMapper
name|update
init|=
operator|(
name|RootObjectMapper
operator|)
name|super
operator|.
name|mappingUpdate
argument_list|(
name|mapper
argument_list|)
decl_stmt|;
comment|// dynamic templates are irrelevant for dynamic mappings updates
name|update
operator|.
name|dynamicTemplates
operator|=
operator|new
name|DynamicTemplate
index|[
literal|0
index|]
expr_stmt|;
return|return
name|update
return|;
block|}
DECL|method|dateDetection
specifier|public
name|boolean
name|dateDetection
parameter_list|()
block|{
return|return
name|this
operator|.
name|dateDetection
return|;
block|}
DECL|method|numericDetection
specifier|public
name|boolean
name|numericDetection
parameter_list|()
block|{
return|return
name|this
operator|.
name|numericDetection
return|;
block|}
DECL|method|dynamicDateTimeFormatters
specifier|public
name|FormatDateTimeFormatter
index|[]
name|dynamicDateTimeFormatters
parameter_list|()
block|{
return|return
name|dynamicDateTimeFormatters
return|;
block|}
DECL|method|findTemplateBuilder
specifier|public
name|Mapper
operator|.
name|Builder
name|findTemplateBuilder
parameter_list|(
name|ParseContext
name|context
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|dynamicType
parameter_list|)
block|{
return|return
name|findTemplateBuilder
argument_list|(
name|context
argument_list|,
name|name
argument_list|,
name|dynamicType
argument_list|,
name|dynamicType
argument_list|)
return|;
block|}
DECL|method|findTemplateBuilder
specifier|public
name|Mapper
operator|.
name|Builder
name|findTemplateBuilder
parameter_list|(
name|ParseContext
name|context
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|dynamicType
parameter_list|,
name|String
name|matchType
parameter_list|)
block|{
name|DynamicTemplate
name|dynamicTemplate
init|=
name|findTemplate
argument_list|(
name|context
operator|.
name|path
argument_list|()
argument_list|,
name|name
argument_list|,
name|matchType
argument_list|)
decl_stmt|;
if|if
condition|(
name|dynamicTemplate
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Mapper
operator|.
name|TypeParser
operator|.
name|ParserContext
name|parserContext
init|=
name|context
operator|.
name|docMapperParser
argument_list|()
operator|.
name|parserContext
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|mappingType
init|=
name|dynamicTemplate
operator|.
name|mappingType
argument_list|(
name|dynamicType
argument_list|)
decl_stmt|;
name|Mapper
operator|.
name|TypeParser
name|typeParser
init|=
name|parserContext
operator|.
name|typeParser
argument_list|(
name|mappingType
argument_list|)
decl_stmt|;
if|if
condition|(
name|typeParser
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"failed to find type parsed ["
operator|+
name|mappingType
operator|+
literal|"] for ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|typeParser
operator|.
name|parse
argument_list|(
name|name
argument_list|,
name|dynamicTemplate
operator|.
name|mappingForName
argument_list|(
name|name
argument_list|,
name|dynamicType
argument_list|)
argument_list|,
name|parserContext
argument_list|)
return|;
block|}
DECL|method|findTemplate
specifier|public
name|DynamicTemplate
name|findTemplate
parameter_list|(
name|ContentPath
name|path
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|matchType
parameter_list|)
block|{
for|for
control|(
name|DynamicTemplate
name|dynamicTemplate
range|:
name|dynamicTemplates
control|)
block|{
if|if
condition|(
name|dynamicTemplate
operator|.
name|match
argument_list|(
name|path
argument_list|,
name|name
argument_list|,
name|matchType
argument_list|)
condition|)
block|{
return|return
name|dynamicTemplate
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|RootObjectMapper
name|merge
parameter_list|(
name|Mapper
name|mergeWith
parameter_list|,
name|boolean
name|updateAllTypes
parameter_list|)
block|{
return|return
operator|(
name|RootObjectMapper
operator|)
name|super
operator|.
name|merge
argument_list|(
name|mergeWith
argument_list|,
name|updateAllTypes
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doMerge
specifier|protected
name|void
name|doMerge
parameter_list|(
name|ObjectMapper
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
name|RootObjectMapper
name|mergeWithObject
init|=
operator|(
name|RootObjectMapper
operator|)
name|mergeWith
decl_stmt|;
comment|// merge them
name|List
argument_list|<
name|DynamicTemplate
argument_list|>
name|mergedTemplates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|this
operator|.
name|dynamicTemplates
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|DynamicTemplate
name|template
range|:
name|mergeWithObject
operator|.
name|dynamicTemplates
control|)
block|{
name|boolean
name|replaced
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|mergedTemplates
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|mergedTemplates
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|template
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|mergedTemplates
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|template
argument_list|)
expr_stmt|;
name|replaced
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|replaced
condition|)
block|{
name|mergedTemplates
operator|.
name|add
argument_list|(
name|template
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|dynamicTemplates
operator|=
name|mergedTemplates
operator|.
name|toArray
argument_list|(
operator|new
name|DynamicTemplate
index|[
name|mergedTemplates
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doXContent
specifier|protected
name|void
name|doXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|ToXContent
operator|.
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dynamicDateTimeFormatters
operator|!=
name|Defaults
operator|.
name|DYNAMIC_DATE_TIME_FORMATTERS
condition|)
block|{
if|if
condition|(
name|dynamicDateTimeFormatters
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
literal|"dynamic_date_formats"
argument_list|)
expr_stmt|;
for|for
control|(
name|FormatDateTimeFormatter
name|dateTimeFormatter
range|:
name|dynamicDateTimeFormatters
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|dateTimeFormatter
operator|.
name|format
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|dynamicTemplates
operator|!=
literal|null
operator|&&
name|dynamicTemplates
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
literal|"dynamic_templates"
argument_list|)
expr_stmt|;
for|for
control|(
name|DynamicTemplate
name|dynamicTemplate
range|:
name|dynamicTemplates
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|dynamicTemplate
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|dynamicTemplate
operator|.
name|conf
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|dateDetection
operator|!=
name|Defaults
operator|.
name|DATE_DETECTION
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"date_detection"
argument_list|,
name|dateDetection
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numericDetection
operator|!=
name|Defaults
operator|.
name|NUMERIC_DETECTION
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"numeric_detection"
argument_list|,
name|numericDetection
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

