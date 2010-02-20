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
name|inject
operator|.
name|Inject
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
name|queryParser
operator|.
name|ParseException
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
name|queryParser
operator|.
name|QueryParser
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
name|FuzzyQuery
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
name|analysis
operator|.
name|AnalysisService
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
name|query
operator|.
name|support
operator|.
name|MapperQueryParser
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

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|QueryStringJsonQueryParser
specifier|public
class|class
name|QueryStringJsonQueryParser
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
literal|"queryString"
decl_stmt|;
DECL|field|analysisService
specifier|private
specifier|final
name|AnalysisService
name|analysisService
decl_stmt|;
DECL|method|QueryStringJsonQueryParser
annotation|@
name|Inject
specifier|public
name|QueryStringJsonQueryParser
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|settings
parameter_list|,
name|AnalysisService
name|analysisService
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|analysisService
operator|=
name|analysisService
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
comment|// move to the field value
name|String
name|queryString
init|=
literal|null
decl_stmt|;
name|String
name|defaultField
init|=
literal|null
decl_stmt|;
name|MapperQueryParser
operator|.
name|Operator
name|defaultOperator
init|=
name|QueryParser
operator|.
name|Operator
operator|.
name|OR
decl_stmt|;
name|boolean
name|allowLeadingWildcard
init|=
literal|true
decl_stmt|;
name|boolean
name|lowercaseExpandedTerms
init|=
literal|true
decl_stmt|;
name|boolean
name|enablePositionIncrements
init|=
literal|true
decl_stmt|;
name|float
name|fuzzyMinSim
init|=
name|FuzzyQuery
operator|.
name|defaultMinSimilarity
decl_stmt|;
name|int
name|fuzzyPrefixLength
init|=
name|FuzzyQuery
operator|.
name|defaultPrefixLength
decl_stmt|;
name|int
name|phraseSlop
init|=
literal|0
decl_stmt|;
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
name|Analyzer
name|analyzer
init|=
literal|null
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|JsonToken
name|token
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
literal|"query"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|queryString
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
literal|"defaultField"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|defaultField
operator|=
name|parseContext
operator|.
name|indexName
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
literal|"defaultOperator"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|String
name|op
init|=
name|jp
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"or"
operator|.
name|equalsIgnoreCase
argument_list|(
name|op
argument_list|)
condition|)
block|{
name|defaultOperator
operator|=
name|QueryParser
operator|.
name|Operator
operator|.
name|OR
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"and"
operator|.
name|equalsIgnoreCase
argument_list|(
name|op
argument_list|)
condition|)
block|{
name|defaultOperator
operator|=
name|QueryParser
operator|.
name|Operator
operator|.
name|AND
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"Query default operator ["
operator|+
name|op
operator|+
literal|"] is not allowed"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"analyzer"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|analyzer
operator|=
name|analysisService
operator|.
name|analyzer
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
name|VALUE_FALSE
operator|||
name|token
operator|==
name|JsonToken
operator|.
name|VALUE_TRUE
condition|)
block|{
if|if
condition|(
literal|"allowLeadingWildcard"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|allowLeadingWildcard
operator|=
name|token
operator|==
name|JsonToken
operator|.
name|VALUE_TRUE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"lowercaseExpandedTerms"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|lowercaseExpandedTerms
operator|=
name|token
operator|==
name|JsonToken
operator|.
name|VALUE_TRUE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"enablePositionIncrements"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|enablePositionIncrements
operator|=
name|token
operator|==
name|JsonToken
operator|.
name|VALUE_TRUE
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
literal|"fuzzyMinSim"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|fuzzyMinSim
operator|=
name|jp
operator|.
name|getFloatValue
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
name|getFloatValue
argument_list|()
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
literal|"fuzzyPrefixLength"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|fuzzyPrefixLength
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
literal|"phraseSlop"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|phraseSlop
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
literal|"fuzzyMinSim"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|fuzzyMinSim
operator|=
name|jp
operator|.
name|getFloatValue
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
name|getFloatValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"allowLeadingWildcard"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|allowLeadingWildcard
operator|=
name|jp
operator|.
name|getIntValue
argument_list|()
operator|!=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"lowercaseExpandedTerms"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|lowercaseExpandedTerms
operator|=
name|jp
operator|.
name|getIntValue
argument_list|()
operator|!=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"enablePositionIncrements"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|enablePositionIncrements
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
block|}
if|if
condition|(
name|queryString
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
literal|"QueryString must be provided with a [query]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
name|analyzer
operator|=
name|parseContext
operator|.
name|mapperService
argument_list|()
operator|.
name|searchAnalyzer
argument_list|()
expr_stmt|;
block|}
name|MapperQueryParser
name|queryParser
init|=
operator|new
name|MapperQueryParser
argument_list|(
name|defaultField
argument_list|,
name|analyzer
argument_list|,
name|parseContext
operator|.
name|mapperService
argument_list|()
argument_list|,
name|parseContext
operator|.
name|filterCache
argument_list|()
argument_list|)
decl_stmt|;
name|queryParser
operator|.
name|setEnablePositionIncrements
argument_list|(
name|enablePositionIncrements
argument_list|)
expr_stmt|;
name|queryParser
operator|.
name|setLowercaseExpandedTerms
argument_list|(
name|lowercaseExpandedTerms
argument_list|)
expr_stmt|;
name|queryParser
operator|.
name|setAllowLeadingWildcard
argument_list|(
name|allowLeadingWildcard
argument_list|)
expr_stmt|;
name|queryParser
operator|.
name|setDefaultOperator
argument_list|(
name|defaultOperator
argument_list|)
expr_stmt|;
name|queryParser
operator|.
name|setFuzzyMinSim
argument_list|(
name|fuzzyMinSim
argument_list|)
expr_stmt|;
name|queryParser
operator|.
name|setFuzzyPrefixLength
argument_list|(
name|fuzzyPrefixLength
argument_list|)
expr_stmt|;
name|queryParser
operator|.
name|setPhraseSlop
argument_list|(
name|phraseSlop
argument_list|)
expr_stmt|;
try|try
block|{
name|Query
name|query
init|=
name|queryParser
operator|.
name|parse
argument_list|(
name|queryString
argument_list|)
decl_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|index
argument_list|,
literal|"Failed to parse query ["
operator|+
name|queryString
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

