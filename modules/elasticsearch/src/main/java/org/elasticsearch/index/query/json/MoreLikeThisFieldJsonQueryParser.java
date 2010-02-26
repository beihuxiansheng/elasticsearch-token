begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.json
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|json
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|search
operator|.
name|Query
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
name|JsonParser
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
name|JsonToken
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
name|AbstractIndexComponent
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
name|Index
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
name|MapperService
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
name|query
operator|.
name|QueryParsingException
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
name|settings
operator|.
name|IndexSettings
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
name|lucene
operator|.
name|search
operator|.
name|MoreLikeThisQuery
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
name|settings
operator|.
name|Settings
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
name|Set
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
name|query
operator|.
name|support
operator|.
name|QueryParsers
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|MoreLikeThisFieldJsonQueryParser
specifier|public
class|class
name|MoreLikeThisFieldJsonQueryParser
extends|extends
name|AbstractIndexComponent
implements|implements
name|JsonQueryParser
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"moreLikeThisField"
decl_stmt|;
DECL|method|MoreLikeThisFieldJsonQueryParser
specifier|public
name|MoreLikeThisFieldJsonQueryParser
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
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
name|NAME
return|;
block|}
DECL|method|parse
annotation|@
name|Override
specifier|public
name|Query
name|parse
parameter_list|(
name|JsonQueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|JsonParser
name|jp
init|=
name|parseContext
operator|.
name|jp
argument_list|()
decl_stmt|;
name|JsonToken
name|token
init|=
name|jp
operator|.
name|nextToken
argument_list|()
decl_stmt|;
assert|assert
name|token
operator|==
name|JsonToken
operator|.
name|FIELD_NAME
assert|;
name|String
name|fieldName
init|=
name|jp
operator|.
name|getCurrentName
argument_list|()
decl_stmt|;
comment|// now, we move after the field name, which starts the object
name|token
operator|=
name|jp
operator|.
name|nextToken
argument_list|()
expr_stmt|;
assert|assert
name|token
operator|==
name|JsonToken
operator|.
name|START_OBJECT
assert|;
name|MoreLikeThisQuery
name|mltQuery
init|=
operator|new
name|MoreLikeThisQuery
argument_list|()
decl_stmt|;
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
name|jp
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|JsonToken
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|JsonToken
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|jp
operator|.
name|getCurrentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|JsonToken
operator|.
name|VALUE_STRING
condition|)
block|{
if|if
condition|(
literal|"likeText"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|mltQuery
operator|.
name|setLikeText
argument_list|(
name|jp
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|JsonToken
operator|.
name|VALUE_NUMBER_INT
condition|)
block|{
if|if
condition|(
literal|"minTermFrequency"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|mltQuery
operator|.
name|setMinTermFrequency
argument_list|(
name|jp
operator|.
name|getIntValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"maxQueryTerms"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|mltQuery
operator|.
name|setMaxQueryTerms
argument_list|(
name|jp
operator|.
name|getIntValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"minDocFreq"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|mltQuery
operator|.
name|setMinDocFreq
argument_list|(
name|jp
operator|.
name|getIntValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"maxDocFreq"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|mltQuery
operator|.
name|setMaxDocFreq
argument_list|(
name|jp
operator|.
name|getIntValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"minWordLen"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|mltQuery
operator|.
name|setMinWordLen
argument_list|(
name|jp
operator|.
name|getIntValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"maxWordLen"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|mltQuery
operator|.
name|setMaxWordLen
argument_list|(
name|jp
operator|.
name|getIntValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"boostTerms"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|mltQuery
operator|.
name|setBoostTerms
argument_list|(
name|jp
operator|.
name|getIntValue
argument_list|()
operator|!=
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"boostTermsFactor"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|mltQuery
operator|.
name|setBoostTermsFactor
argument_list|(
name|jp
operator|.
name|getIntValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|JsonToken
operator|.
name|VALUE_NUMBER_FLOAT
condition|)
block|{
if|if
condition|(
literal|"boostTermsFactor"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|mltQuery
operator|.
name|setBoostTermsFactor
argument_list|(
name|jp
operator|.
name|getFloatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|JsonToken
operator|.
name|START_ARRAY
condition|)
block|{
if|if
condition|(
literal|"stopWords"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|stopWords
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|jp
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|JsonToken
operator|.
name|END_ARRAY
condition|)
block|{
name|stopWords
operator|.
name|add
argument_list|(
name|jp
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|mltQuery
operator|.
name|setStopWords
argument_list|(
name|stopWords
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|mltQuery
operator|.
name|getLikeText
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"moreLikeThisField requires 'likeText' to be specified"
argument_list|)
throw|;
block|}
comment|// move to the next end object, to close the field name
name|token
operator|=
name|jp
operator|.
name|nextToken
argument_list|()
expr_stmt|;
assert|assert
name|token
operator|==
name|JsonToken
operator|.
name|END_OBJECT
assert|;
name|MapperService
operator|.
name|SmartNameFieldMappers
name|smartNameFieldMappers
init|=
name|parseContext
operator|.
name|smartFieldMappers
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|smartNameFieldMappers
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|smartNameFieldMappers
operator|.
name|hasMapper
argument_list|()
condition|)
block|{
name|fieldName
operator|=
name|smartNameFieldMappers
operator|.
name|mapper
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
expr_stmt|;
name|mltQuery
operator|.
name|setAnalyzer
argument_list|(
name|smartNameFieldMappers
operator|.
name|mapper
argument_list|()
operator|.
name|searchAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|mltQuery
operator|.
name|getAnalyzer
argument_list|()
operator|==
literal|null
condition|)
block|{
name|mltQuery
operator|.
name|setAnalyzer
argument_list|(
name|parseContext
operator|.
name|mapperService
argument_list|()
operator|.
name|searchAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|mltQuery
operator|.
name|setMoreLikeFields
argument_list|(
operator|new
name|String
index|[]
block|{
name|fieldName
block|}
argument_list|)
expr_stmt|;
return|return
name|wrapSmartNameQuery
argument_list|(
name|mltQuery
argument_list|,
name|smartNameFieldMappers
argument_list|,
name|parseContext
operator|.
name|filterCache
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

