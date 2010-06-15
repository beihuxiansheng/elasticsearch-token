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
name|util
operator|.
name|Strings
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|XContentTypeParsers
specifier|public
class|class
name|XContentTypeParsers
block|{
DECL|method|parseNumberField
specifier|public
specifier|static
name|void
name|parseNumberField
parameter_list|(
name|XContentNumberFieldMapper
operator|.
name|Builder
name|builder
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|numberNode
parameter_list|,
name|XContentTypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
block|{
name|parseField
argument_list|(
name|builder
argument_list|,
name|name
argument_list|,
name|numberNode
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
name|numberNode
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
literal|"precision_step"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|precisionStep
argument_list|(
name|nodeIntegerValue
argument_list|(
name|propNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|parseField
specifier|public
specifier|static
name|void
name|parseField
parameter_list|(
name|XContentFieldMapper
operator|.
name|Builder
name|builder
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fieldNode
parameter_list|,
name|XContentTypeParser
operator|.
name|ParserContext
name|parserContext
parameter_list|)
block|{
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
name|fieldNode
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
literal|"index_name"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|indexName
argument_list|(
name|propNode
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|propName
operator|.
name|equals
argument_list|(
literal|"store"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|store
argument_list|(
name|parseStore
argument_list|(
name|name
argument_list|,
name|propNode
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
name|propName
operator|.
name|equals
argument_list|(
literal|"index"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|index
argument_list|(
name|parseIndex
argument_list|(
name|name
argument_list|,
name|propNode
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
name|propName
operator|.
name|equals
argument_list|(
literal|"term_vector"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|termVector
argument_list|(
name|parseTermVector
argument_list|(
name|name
argument_list|,
name|propNode
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
name|propName
operator|.
name|equals
argument_list|(
literal|"boost"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|boost
argument_list|(
name|nodeFloatValue
argument_list|(
name|propNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|propName
operator|.
name|equals
argument_list|(
literal|"omit_norms"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|omitNorms
argument_list|(
name|nodeBooleanValue
argument_list|(
name|propNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|propName
operator|.
name|equals
argument_list|(
literal|"omit_term_freq_and_positions"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|omitTermFreqAndPositions
argument_list|(
name|nodeBooleanValue
argument_list|(
name|propNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|propName
operator|.
name|equals
argument_list|(
literal|"index_analyzer"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|indexAnalyzer
argument_list|(
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
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|propName
operator|.
name|equals
argument_list|(
literal|"search_analyzer"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|searchAnalyzer
argument_list|(
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
argument_list|)
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
name|builder
operator|.
name|indexAnalyzer
argument_list|(
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
argument_list|)
expr_stmt|;
name|builder
operator|.
name|searchAnalyzer
argument_list|(
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
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|propName
operator|.
name|equals
argument_list|(
literal|"include_in_all"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|includeInAll
argument_list|(
name|nodeBooleanValue
argument_list|(
name|propNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|parseDateTimeFormatter
specifier|public
specifier|static
name|FormatDateTimeFormatter
name|parseDateTimeFormatter
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Object
name|node
parameter_list|)
block|{
return|return
name|Joda
operator|.
name|forPattern
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|parseTermVector
specifier|public
specifier|static
name|Field
operator|.
name|TermVector
name|parseTermVector
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|termVector
parameter_list|)
throws|throws
name|MapperParsingException
block|{
name|termVector
operator|=
name|Strings
operator|.
name|toUnderscoreCase
argument_list|(
name|termVector
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"no"
operator|.
name|equals
argument_list|(
name|termVector
argument_list|)
condition|)
block|{
return|return
name|Field
operator|.
name|TermVector
operator|.
name|NO
return|;
block|}
elseif|else
if|if
condition|(
literal|"yes"
operator|.
name|equals
argument_list|(
name|termVector
argument_list|)
condition|)
block|{
return|return
name|Field
operator|.
name|TermVector
operator|.
name|YES
return|;
block|}
elseif|else
if|if
condition|(
literal|"with_offsets"
operator|.
name|equals
argument_list|(
name|termVector
argument_list|)
condition|)
block|{
return|return
name|Field
operator|.
name|TermVector
operator|.
name|WITH_OFFSETS
return|;
block|}
elseif|else
if|if
condition|(
literal|"with_positions"
operator|.
name|equals
argument_list|(
name|termVector
argument_list|)
condition|)
block|{
return|return
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS
return|;
block|}
elseif|else
if|if
condition|(
literal|"with_positions_offsets"
operator|.
name|equals
argument_list|(
name|termVector
argument_list|)
condition|)
block|{
return|return
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Wrong value for termVector ["
operator|+
name|termVector
operator|+
literal|"] for field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|parseIndex
specifier|public
specifier|static
name|Field
operator|.
name|Index
name|parseIndex
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|index
parameter_list|)
throws|throws
name|MapperParsingException
block|{
name|index
operator|=
name|Strings
operator|.
name|toUnderscoreCase
argument_list|(
name|index
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"no"
operator|.
name|equals
argument_list|(
name|index
argument_list|)
condition|)
block|{
return|return
name|Field
operator|.
name|Index
operator|.
name|NO
return|;
block|}
elseif|else
if|if
condition|(
literal|"not_analyzed"
operator|.
name|equals
argument_list|(
name|index
argument_list|)
condition|)
block|{
return|return
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
return|;
block|}
elseif|else
if|if
condition|(
literal|"analyzed"
operator|.
name|equals
argument_list|(
name|index
argument_list|)
condition|)
block|{
return|return
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Wrong value for index ["
operator|+
name|index
operator|+
literal|"] for field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|parseStore
specifier|public
specifier|static
name|Field
operator|.
name|Store
name|parseStore
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|store
parameter_list|)
throws|throws
name|MapperParsingException
block|{
if|if
condition|(
literal|"no"
operator|.
name|equals
argument_list|(
name|store
argument_list|)
condition|)
block|{
return|return
name|Field
operator|.
name|Store
operator|.
name|NO
return|;
block|}
elseif|else
if|if
condition|(
literal|"yes"
operator|.
name|equals
argument_list|(
name|store
argument_list|)
condition|)
block|{
return|return
name|Field
operator|.
name|Store
operator|.
name|YES
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Wrong value for store ["
operator|+
name|store
operator|+
literal|"] for field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|parsePathType
specifier|public
specifier|static
name|ContentPath
operator|.
name|Type
name|parsePathType
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|MapperParsingException
block|{
name|path
operator|=
name|Strings
operator|.
name|toUnderscoreCase
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"just_name"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
name|ContentPath
operator|.
name|Type
operator|.
name|JUST_NAME
return|;
block|}
elseif|else
if|if
condition|(
literal|"full"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
name|ContentPath
operator|.
name|Type
operator|.
name|FULL
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Wrong value for pathType ["
operator|+
name|path
operator|+
literal|"] for objet ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

