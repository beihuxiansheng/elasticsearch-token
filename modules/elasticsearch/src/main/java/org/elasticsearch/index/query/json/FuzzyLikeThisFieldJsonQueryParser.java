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
name|search
operator|.
name|FuzzyLikeThisQuery
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
name|Booleans
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
comment|/**  *<pre>  * {  *  fuzzyLikeThisField : {  *      field1 : {  *          maxNumTerms : 12,  *          boost : 1.1,  *          likeText : "..."  *      }  * }  *</pre>  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|FuzzyLikeThisFieldJsonQueryParser
specifier|public
class|class
name|FuzzyLikeThisFieldJsonQueryParser
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
literal|"fuzzyLikeThisField"
decl_stmt|;
DECL|method|FuzzyLikeThisFieldJsonQueryParser
specifier|public
name|FuzzyLikeThisFieldJsonQueryParser
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
name|int
name|maxNumTerms
init|=
literal|100
decl_stmt|;
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
name|String
name|likeText
init|=
literal|null
decl_stmt|;
name|float
name|minSimilarity
init|=
literal|0.5f
decl_stmt|;
name|int
name|prefixLength
init|=
literal|0
decl_stmt|;
name|boolean
name|ignoreTF
init|=
literal|false
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
name|likeText
operator|=
name|jp
operator|.
name|getText
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"maxNumTerms"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|maxNumTerms
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|jp
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"boost"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|boost
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|jp
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"ignoreTF"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|ignoreTF
operator|=
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|jp
operator|.
name|getText
argument_list|()
argument_list|,
literal|false
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
literal|"maxNumTerms"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|maxNumTerms
operator|=
name|jp
operator|.
name|getIntValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"boost"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|boost
operator|=
name|jp
operator|.
name|getIntValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"ignoreTF"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|ignoreTF
operator|=
name|jp
operator|.
name|getIntValue
argument_list|()
operator|!=
literal|0
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
name|VALUE_TRUE
condition|)
block|{
if|if
condition|(
literal|"ignoreTF"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|ignoreTF
operator|=
literal|true
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
literal|"boost"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|boost
operator|=
name|jp
operator|.
name|getFloatValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|likeText
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
literal|"fuzzyLikeThisField requires 'likeText' to be specified"
argument_list|)
throw|;
block|}
name|Analyzer
name|analyzer
init|=
name|parseContext
operator|.
name|mapperService
argument_list|()
operator|.
name|searchAnalyzer
argument_list|()
decl_stmt|;
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
name|analyzer
operator|=
name|smartNameFieldMappers
operator|.
name|mapper
argument_list|()
operator|.
name|searchAnalyzer
argument_list|()
expr_stmt|;
block|}
block|}
name|FuzzyLikeThisQuery
name|query
init|=
operator|new
name|FuzzyLikeThisQuery
argument_list|(
name|maxNumTerms
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|query
operator|.
name|addTerms
argument_list|(
name|likeText
argument_list|,
name|fieldName
argument_list|,
name|minSimilarity
argument_list|,
name|prefixLength
argument_list|)
expr_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|query
operator|.
name|setIgnoreTF
argument_list|(
name|ignoreTF
argument_list|)
expr_stmt|;
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
return|return
name|wrapSmartNameQuery
argument_list|(
name|query
argument_list|,
name|smartNameFieldMappers
argument_list|,
name|parseContext
operator|.
name|indexCache
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

