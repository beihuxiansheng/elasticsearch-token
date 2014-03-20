begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.functionscore.fieldvaluefactor
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|functionscore
operator|.
name|fieldvaluefactor
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|search
operator|.
name|function
operator|.
name|FieldValueFactorFunction
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
name|search
operator|.
name|function
operator|.
name|ScoreFunction
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
name|index
operator|.
name|fielddata
operator|.
name|IndexNumericFieldData
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
name|query
operator|.
name|QueryParseContext
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
name|functionscore
operator|.
name|ScoreFunctionParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SearchContext
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
name|Locale
import|;
end_import

begin_comment
comment|/**  * Parses out a function_score function that looks like:  *  *<pre>  *     {  *         "field_value_factor": {  *             "field": "myfield",  *             "factor": 1.5,  *             "modifier": "square"  *         }  *     }  *</pre>  */
end_comment

begin_class
DECL|class|FieldValueFactorFunctionParser
specifier|public
class|class
name|FieldValueFactorFunctionParser
implements|implements
name|ScoreFunctionParser
block|{
DECL|field|NAMES
specifier|public
specifier|static
name|String
index|[]
name|NAMES
init|=
block|{
literal|"field_value_factor"
block|,
literal|"fieldValueFactor"
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|parse
specifier|public
name|ScoreFunction
name|parse
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|,
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|String
name|field
init|=
literal|null
decl_stmt|;
name|float
name|boostFactor
init|=
literal|1
decl_stmt|;
name|FieldValueFactorFunction
operator|.
name|Modifier
name|modifier
init|=
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|NONE
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
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
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"field"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|field
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
literal|"factor"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|boostFactor
operator|=
name|parser
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"modifier"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|modifier
operator|=
name|FieldValueFactorFunction
operator|.
name|Modifier
operator|.
name|valueOf
argument_list|(
name|parser
operator|.
name|text
argument_list|()
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
name|NAMES
index|[
literal|0
index|]
operator|+
literal|" query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"["
operator|+
name|NAMES
index|[
literal|0
index|]
operator|+
literal|"] required field 'field' missing"
argument_list|)
throw|;
block|}
name|SearchContext
name|searchContext
init|=
name|SearchContext
operator|.
name|current
argument_list|()
decl_stmt|;
name|FieldMapper
name|mapper
init|=
name|searchContext
operator|.
name|mapperService
argument_list|()
operator|.
name|smartNameFieldMapper
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapper
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Unable to find a field mapper for field ["
operator|+
name|field
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
operator|new
name|FieldValueFactorFunction
argument_list|(
name|field
argument_list|,
name|boostFactor
argument_list|,
name|modifier
argument_list|,
operator|(
name|IndexNumericFieldData
operator|)
name|searchContext
operator|.
name|fieldData
argument_list|()
operator|.
name|getForField
argument_list|(
name|mapper
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNames
specifier|public
name|String
index|[]
name|getNames
parameter_list|()
block|{
return|return
name|NAMES
return|;
block|}
block|}
end_class

end_unit

