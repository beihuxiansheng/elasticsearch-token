begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|internal
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
name|InternalMapper
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
name|MergeContext
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
name|RootMapper
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
name|LongFieldMapper
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
name|NumberFieldMapper
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|MapperBuilders
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
name|core
operator|.
name|TypeParsers
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author paikan (benjamin.deveze)  */
end_comment

begin_class
DECL|class|TimestampFieldMapper
specifier|public
class|class
name|TimestampFieldMapper
extends|extends
name|DateFieldMapper
implements|implements
name|InternalMapper
implements|,
name|RootMapper
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"_timestamp"
decl_stmt|;
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"_timestamp"
decl_stmt|;
DECL|field|DEFAULT_DATE_TIME_FORMAT
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DATE_TIME_FORMAT
init|=
literal|"dateOptionalTime"
decl_stmt|;
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
extends|extends
name|DateFieldMapper
operator|.
name|Defaults
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"_timestamp"
decl_stmt|;
DECL|field|STORE
specifier|public
specifier|static
specifier|final
name|Field
operator|.
name|Store
name|STORE
init|=
name|Field
operator|.
name|Store
operator|.
name|NO
decl_stmt|;
DECL|field|INDEX
specifier|public
specifier|static
specifier|final
name|Field
operator|.
name|Index
name|INDEX
init|=
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
decl_stmt|;
DECL|field|ENABLED
specifier|public
specifier|static
specifier|final
name|boolean
name|ENABLED
init|=
literal|false
decl_stmt|;
DECL|field|PATH
specifier|public
specifier|static
specifier|final
name|String
name|PATH
init|=
literal|null
decl_stmt|;
DECL|field|DATE_TIME_FORMATTER
specifier|public
specifier|static
specifier|final
name|FormatDateTimeFormatter
name|DATE_TIME_FORMATTER
init|=
name|Joda
operator|.
name|forPattern
argument_list|(
name|DEFAULT_DATE_TIME_FORMAT
argument_list|)
decl_stmt|;
block|}
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
name|TimestampFieldMapper
argument_list|>
block|{
DECL|field|enabled
specifier|private
name|boolean
name|enabled
init|=
name|Defaults
operator|.
name|ENABLED
decl_stmt|;
DECL|field|path
specifier|private
name|String
name|path
init|=
name|Defaults
operator|.
name|PATH
decl_stmt|;
DECL|field|dateTimeFormatter
specifier|private
name|FormatDateTimeFormatter
name|dateTimeFormatter
init|=
name|Defaults
operator|.
name|DATE_TIME_FORMATTER
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|()
block|{
name|super
argument_list|(
name|Defaults
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|store
operator|=
name|Defaults
operator|.
name|STORE
expr_stmt|;
name|index
operator|=
name|Defaults
operator|.
name|INDEX
expr_stmt|;
block|}
DECL|method|enabled
specifier|public
name|Builder
name|enabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|path
specifier|public
name|Builder
name|path
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|dateTimeFormatter
specifier|public
name|Builder
name|dateTimeFormatter
parameter_list|(
name|FormatDateTimeFormatter
name|dateTimeFormatter
parameter_list|)
block|{
name|this
operator|.
name|dateTimeFormatter
operator|=
name|dateTimeFormatter
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|build
annotation|@
name|Override
specifier|public
name|TimestampFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|boolean
name|parseUpperInclusive
init|=
name|Defaults
operator|.
name|PARSE_UPPER_INCLUSIVE
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|indexSettings
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|parseUpperInclusive
operator|=
name|context
operator|.
name|indexSettings
argument_list|()
operator|.
name|getAsBoolean
argument_list|(
literal|"index.mapping.date.parse_upper_inclusive"
argument_list|,
name|Defaults
operator|.
name|PARSE_UPPER_INCLUSIVE
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TimestampFieldMapper
argument_list|(
name|store
argument_list|,
name|index
argument_list|,
name|enabled
argument_list|,
name|path
argument_list|,
name|dateTimeFormatter
argument_list|,
name|parseUpperInclusive
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
DECL|method|parse
annotation|@
name|Override
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
name|TimestampFieldMapper
operator|.
name|Builder
name|builder
init|=
name|timestamp
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
elseif|else
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
name|path
argument_list|(
name|fieldNode
operator|.
name|toString
argument_list|()
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
literal|"format"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|dateTimeFormatter
argument_list|(
name|parseDateTimeFormatter
argument_list|(
name|builder
operator|.
name|name
argument_list|()
argument_list|,
name|fieldNode
operator|.
name|toString
argument_list|()
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
DECL|field|enabled
specifier|private
name|boolean
name|enabled
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|method|TimestampFieldMapper
specifier|public
name|TimestampFieldMapper
parameter_list|()
block|{
name|this
argument_list|(
name|Defaults
operator|.
name|STORE
argument_list|,
name|Defaults
operator|.
name|INDEX
argument_list|,
name|Defaults
operator|.
name|ENABLED
argument_list|,
name|Defaults
operator|.
name|PATH
argument_list|,
name|Defaults
operator|.
name|DATE_TIME_FORMATTER
argument_list|,
name|Defaults
operator|.
name|PARSE_UPPER_INCLUSIVE
argument_list|)
expr_stmt|;
block|}
DECL|method|TimestampFieldMapper
specifier|protected
name|TimestampFieldMapper
parameter_list|(
name|Field
operator|.
name|Store
name|store
parameter_list|,
name|Field
operator|.
name|Index
name|index
parameter_list|,
name|boolean
name|enabled
parameter_list|,
name|String
name|path
parameter_list|,
name|FormatDateTimeFormatter
name|dateTimeFormatter
parameter_list|,
name|boolean
name|parseUpperInclusive
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|Names
argument_list|(
name|Defaults
operator|.
name|NAME
argument_list|,
name|Defaults
operator|.
name|NAME
argument_list|,
name|Defaults
operator|.
name|NAME
argument_list|,
name|Defaults
operator|.
name|NAME
argument_list|)
argument_list|,
name|dateTimeFormatter
argument_list|,
name|Defaults
operator|.
name|PRECISION_STEP
argument_list|,
name|Defaults
operator|.
name|FUZZY_FACTOR
argument_list|,
name|index
argument_list|,
name|store
argument_list|,
name|Defaults
operator|.
name|BOOST
argument_list|,
name|Defaults
operator|.
name|OMIT_NORMS
argument_list|,
name|Defaults
operator|.
name|OMIT_TERM_FREQ_AND_POSITIONS
argument_list|,
name|Defaults
operator|.
name|NULL_VALUE
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
comment|/*always milliseconds*/
argument_list|,
name|parseUpperInclusive
argument_list|)
expr_stmt|;
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
DECL|method|enabled
specifier|public
name|boolean
name|enabled
parameter_list|()
block|{
return|return
name|this
operator|.
name|enabled
return|;
block|}
DECL|method|path
specifier|public
name|String
name|path
parameter_list|()
block|{
return|return
name|this
operator|.
name|path
return|;
block|}
DECL|method|dateTimeFormatter
specifier|public
name|FormatDateTimeFormatter
name|dateTimeFormatter
parameter_list|()
block|{
return|return
name|this
operator|.
name|dateTimeFormatter
return|;
block|}
comment|/**      * Override the default behavior to return a timestamp      */
DECL|method|valueForSearch
annotation|@
name|Override
specifier|public
name|Object
name|valueForSearch
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
return|return
name|value
argument_list|(
name|field
argument_list|)
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
name|Long
name|value
init|=
name|value
argument_list|(
name|field
argument_list|)
decl_stmt|;
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
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|validate
annotation|@
name|Override
specifier|public
name|void
name|validate
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|MapperParsingException
block|{     }
DECL|method|preParse
annotation|@
name|Override
specifier|public
name|void
name|preParse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|parse
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|postParse
annotation|@
name|Override
specifier|public
name|void
name|postParse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{     }
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
comment|// nothing to do here, we call the parent in preParse
block|}
DECL|method|includeInObject
annotation|@
name|Override
specifier|public
name|boolean
name|includeInObject
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|parseCreateField
annotation|@
name|Override
specifier|protected
name|Fieldable
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
name|enabled
condition|)
block|{
name|long
name|timestamp
init|=
name|context
operator|.
name|sourceToParse
argument_list|()
operator|.
name|timestamp
argument_list|()
decl_stmt|;
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
name|context
operator|.
name|ignoredValue
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|timestamp
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
operator|new
name|LongFieldMapper
operator|.
name|CustomLongNumericField
argument_list|(
name|this
argument_list|,
name|timestamp
argument_list|)
return|;
block|}
return|return
literal|null
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
DECL|method|toXContent
annotation|@
name|Override
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
comment|// if all are defaults, no sense to write it at all
if|if
condition|(
name|index
operator|==
name|Defaults
operator|.
name|INDEX
operator|&&
name|store
operator|==
name|Defaults
operator|.
name|STORE
operator|&&
name|enabled
operator|==
name|Defaults
operator|.
name|ENABLED
operator|&&
name|path
operator|==
name|Defaults
operator|.
name|PATH
operator|&&
name|dateTimeFormatter
operator|.
name|format
argument_list|()
operator|.
name|equals
argument_list|(
name|Defaults
operator|.
name|DATE_TIME_FORMATTER
operator|.
name|format
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|builder
return|;
block|}
name|builder
operator|.
name|startObject
argument_list|(
name|CONTENT_TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|!=
name|Defaults
operator|.
name|INDEX
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
name|index
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|store
operator|!=
name|Defaults
operator|.
name|STORE
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
name|store
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|enabled
operator|!=
name|Defaults
operator|.
name|ENABLED
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
name|enabled
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|path
operator|!=
name|Defaults
operator|.
name|PATH
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"path"
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|dateTimeFormatter
operator|.
name|format
argument_list|()
operator|.
name|equals
argument_list|(
name|Defaults
operator|.
name|DATE_TIME_FORMATTER
operator|.
name|format
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
name|dateTimeFormatter
operator|.
name|format
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|merge
annotation|@
name|Override
specifier|public
name|void
name|merge
parameter_list|(
name|Mapper
name|mergeWith
parameter_list|,
name|MergeContext
name|mergeContext
parameter_list|)
throws|throws
name|MergeMappingException
block|{
comment|// do nothing here, no merging, but also no exception
block|}
block|}
end_class

end_unit

