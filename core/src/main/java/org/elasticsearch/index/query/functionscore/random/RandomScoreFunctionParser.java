begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.functionscore.random
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
name|random
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
name|inject
operator|.
name|Inject
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
name|common
operator|.
name|ParsingException
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|RandomScoreFunctionParser
specifier|public
class|class
name|RandomScoreFunctionParser
implements|implements
name|ScoreFunctionParser
argument_list|<
name|RandomScoreFunctionBuilder
argument_list|>
block|{
DECL|field|NAMES
specifier|public
specifier|static
name|String
index|[]
name|NAMES
init|=
block|{
literal|"random_score"
block|,
literal|"randomScore"
block|}
decl_stmt|;
DECL|field|PROTOTYPE
specifier|private
specifier|static
name|RandomScoreFunctionBuilder
name|PROTOTYPE
init|=
operator|new
name|RandomScoreFunctionBuilder
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|RandomScoreFunctionParser
specifier|public
name|RandomScoreFunctionParser
parameter_list|()
block|{     }
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
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
name|RandomScoreFunctionBuilder
name|fromXContent
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
name|ParsingException
block|{
name|RandomScoreFunctionBuilder
name|randomScoreFunctionBuilder
init|=
operator|new
name|RandomScoreFunctionBuilder
argument_list|()
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
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
literal|"seed"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
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
name|VALUE_NUMBER
condition|)
block|{
if|if
condition|(
name|parser
operator|.
name|numberType
argument_list|()
operator|==
name|XContentParser
operator|.
name|NumberType
operator|.
name|INT
condition|)
block|{
name|randomScoreFunctionBuilder
operator|.
name|seed
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parser
operator|.
name|numberType
argument_list|()
operator|==
name|XContentParser
operator|.
name|NumberType
operator|.
name|LONG
condition|)
block|{
name|randomScoreFunctionBuilder
operator|.
name|seed
argument_list|(
name|parser
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"random_score seed must be an int, long or string, not '"
operator|+
name|token
operator|.
name|toString
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
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
name|randomScoreFunctionBuilder
operator|.
name|seed
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"random_score seed must be an int/long or string, not '"
operator|+
name|token
operator|.
name|toString
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
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
return|return
name|randomScoreFunctionBuilder
return|;
block|}
annotation|@
name|Override
DECL|method|getBuilderPrototype
specifier|public
name|RandomScoreFunctionBuilder
name|getBuilderPrototype
parameter_list|()
block|{
return|return
name|PROTOTYPE
return|;
block|}
block|}
end_class

end_unit

