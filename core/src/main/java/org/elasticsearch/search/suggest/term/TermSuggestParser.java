begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.term
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|term
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
name|ParseFieldMatcher
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
name|QueryShardContext
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
name|suggest
operator|.
name|DirectSpellcheckerSettings
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
name|suggest
operator|.
name|SuggestContextParser
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
name|suggest
operator|.
name|SuggestUtils
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
name|suggest
operator|.
name|SuggestionSearchContext
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
DECL|class|TermSuggestParser
specifier|public
specifier|final
class|class
name|TermSuggestParser
implements|implements
name|SuggestContextParser
block|{
DECL|field|suggester
specifier|private
name|TermSuggester
name|suggester
decl_stmt|;
DECL|method|TermSuggestParser
specifier|public
name|TermSuggestParser
parameter_list|(
name|TermSuggester
name|suggester
parameter_list|)
block|{
name|this
operator|.
name|suggester
operator|=
name|suggester
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|SuggestionSearchContext
operator|.
name|SuggestionContext
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|QueryShardContext
name|shardContext
parameter_list|)
throws|throws
name|IOException
block|{
name|MapperService
name|mapperService
init|=
name|shardContext
operator|.
name|getMapperService
argument_list|()
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
name|TermSuggestionContext
name|suggestion
init|=
operator|new
name|TermSuggestionContext
argument_list|(
name|suggester
argument_list|)
decl_stmt|;
name|DirectSpellcheckerSettings
name|settings
init|=
name|suggestion
operator|.
name|getDirectSpellCheckerSettings
argument_list|()
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
name|fieldName
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
name|parseTokenValue
argument_list|(
name|parser
argument_list|,
name|mapperService
argument_list|,
name|fieldName
argument_list|,
name|suggestion
argument_list|,
name|settings
argument_list|,
name|mapperService
operator|.
name|getIndexSettings
argument_list|()
operator|.
name|getParseFieldMatcher
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"suggester[term]  doesn't support field ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
return|return
name|suggestion
return|;
block|}
DECL|method|parseTokenValue
specifier|private
name|void
name|parseTokenValue
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|MapperService
name|mapperService
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|TermSuggestionContext
name|suggestion
parameter_list|,
name|DirectSpellcheckerSettings
name|settings
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
name|SuggestUtils
operator|.
name|parseSuggestContext
argument_list|(
name|parser
argument_list|,
name|mapperService
argument_list|,
name|fieldName
argument_list|,
name|suggestion
argument_list|,
name|parseFieldMatcher
argument_list|)
operator|||
name|SuggestUtils
operator|.
name|parseDirectSpellcheckerSettings
argument_list|(
name|parser
argument_list|,
name|fieldName
argument_list|,
name|settings
argument_list|,
name|parseFieldMatcher
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"suggester[term] doesn't support ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

