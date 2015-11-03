begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.completion
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
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
name|search
operator|.
name|suggest
operator|.
name|document
operator|.
name|CompletionQuery
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
name|unit
operator|.
name|Fuzziness
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
name|IndexFieldDataService
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
name|mapper
operator|.
name|core
operator|.
name|CompletionFieldMapper
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
name|Suggester
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion
operator|.
name|context
operator|.
name|CategoryQueryContext
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
name|completion
operator|.
name|context
operator|.
name|ContextMappings
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|CompletionSuggestionContext
specifier|public
class|class
name|CompletionSuggestionContext
extends|extends
name|SuggestionSearchContext
operator|.
name|SuggestionContext
block|{
DECL|field|fieldType
specifier|private
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
name|fieldType
decl_stmt|;
DECL|field|fuzzyOptionsBuilder
specifier|private
name|CompletionSuggestionBuilder
operator|.
name|FuzzyOptionsBuilder
name|fuzzyOptionsBuilder
decl_stmt|;
DECL|field|regexOptionsBuilder
specifier|private
name|CompletionSuggestionBuilder
operator|.
name|RegexOptionsBuilder
name|regexOptionsBuilder
decl_stmt|;
DECL|field|queryContexts
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CategoryQueryContext
argument_list|>
argument_list|>
name|queryContexts
decl_stmt|;
DECL|field|mapperService
specifier|private
name|MapperService
name|mapperService
decl_stmt|;
DECL|field|fieldData
specifier|private
name|IndexFieldDataService
name|fieldData
decl_stmt|;
DECL|field|payloadFields
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|payloadFields
decl_stmt|;
DECL|method|CompletionSuggestionContext
name|CompletionSuggestionContext
parameter_list|(
name|Suggester
name|suggester
parameter_list|)
block|{
name|super
argument_list|(
name|suggester
argument_list|)
expr_stmt|;
block|}
DECL|method|getFieldType
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
name|getFieldType
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldType
return|;
block|}
DECL|method|setFieldType
name|void
name|setFieldType
parameter_list|(
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
name|fieldType
parameter_list|)
block|{
name|this
operator|.
name|fieldType
operator|=
name|fieldType
expr_stmt|;
block|}
DECL|method|setRegexOptionsBuilder
name|void
name|setRegexOptionsBuilder
parameter_list|(
name|CompletionSuggestionBuilder
operator|.
name|RegexOptionsBuilder
name|regexOptionsBuilder
parameter_list|)
block|{
name|this
operator|.
name|regexOptionsBuilder
operator|=
name|regexOptionsBuilder
expr_stmt|;
block|}
DECL|method|setFuzzyOptionsBuilder
name|void
name|setFuzzyOptionsBuilder
parameter_list|(
name|CompletionSuggestionBuilder
operator|.
name|FuzzyOptionsBuilder
name|fuzzyOptionsBuilder
parameter_list|)
block|{
name|this
operator|.
name|fuzzyOptionsBuilder
operator|=
name|fuzzyOptionsBuilder
expr_stmt|;
block|}
DECL|method|setQueryContexts
name|void
name|setQueryContexts
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|CategoryQueryContext
argument_list|>
argument_list|>
name|queryContexts
parameter_list|)
block|{
name|this
operator|.
name|queryContexts
operator|=
name|queryContexts
expr_stmt|;
block|}
DECL|method|setMapperService
name|void
name|setMapperService
parameter_list|(
name|MapperService
name|mapperService
parameter_list|)
block|{
name|this
operator|.
name|mapperService
operator|=
name|mapperService
expr_stmt|;
block|}
DECL|method|getMapperService
name|MapperService
name|getMapperService
parameter_list|()
block|{
return|return
name|mapperService
return|;
block|}
DECL|method|setFieldData
name|void
name|setFieldData
parameter_list|(
name|IndexFieldDataService
name|fieldData
parameter_list|)
block|{
name|this
operator|.
name|fieldData
operator|=
name|fieldData
expr_stmt|;
block|}
DECL|method|getFieldData
name|IndexFieldDataService
name|getFieldData
parameter_list|()
block|{
return|return
name|fieldData
return|;
block|}
DECL|method|setPayloadFields
name|void
name|setPayloadFields
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|)
block|{
name|this
operator|.
name|payloadFields
operator|=
name|fields
expr_stmt|;
block|}
DECL|method|getPayloadFields
name|Set
argument_list|<
name|String
argument_list|>
name|getPayloadFields
parameter_list|()
block|{
return|return
name|payloadFields
return|;
block|}
DECL|method|toQuery
name|CompletionQuery
name|toQuery
parameter_list|()
block|{
name|CompletionFieldMapper
operator|.
name|CompletionFieldType
name|fieldType
init|=
name|getFieldType
argument_list|()
decl_stmt|;
specifier|final
name|CompletionQuery
name|query
decl_stmt|;
if|if
condition|(
name|getPrefix
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fuzzyOptionsBuilder
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
name|fieldType
operator|.
name|fuzzyQuery
argument_list|(
name|getPrefix
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|Fuzziness
operator|.
name|fromEdits
argument_list|(
name|fuzzyOptionsBuilder
operator|.
name|getEditDistance
argument_list|()
argument_list|)
argument_list|,
name|fuzzyOptionsBuilder
operator|.
name|getFuzzyPrefixLength
argument_list|()
argument_list|,
name|fuzzyOptionsBuilder
operator|.
name|getFuzzyMinLength
argument_list|()
argument_list|,
name|fuzzyOptionsBuilder
operator|.
name|getMaxDeterminizedStates
argument_list|()
argument_list|,
name|fuzzyOptionsBuilder
operator|.
name|isTranspositions
argument_list|()
argument_list|,
name|fuzzyOptionsBuilder
operator|.
name|isUnicodeAware
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
name|fieldType
operator|.
name|prefixQuery
argument_list|(
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|getRegex
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fuzzyOptionsBuilder
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"can not use 'fuzzy' options with 'regex"
argument_list|)
throw|;
block|}
if|if
condition|(
name|regexOptionsBuilder
operator|==
literal|null
condition|)
block|{
name|regexOptionsBuilder
operator|=
operator|new
name|CompletionSuggestionBuilder
operator|.
name|RegexOptionsBuilder
argument_list|()
expr_stmt|;
block|}
name|query
operator|=
name|fieldType
operator|.
name|regexpQuery
argument_list|(
name|getRegex
argument_list|()
argument_list|,
name|regexOptionsBuilder
operator|.
name|getFlagsValue
argument_list|()
argument_list|,
name|regexOptionsBuilder
operator|.
name|getMaxDeterminizedStates
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
literal|"'prefix' or 'regex' must be defined"
argument_list|)
throw|;
block|}
if|if
condition|(
name|fieldType
operator|.
name|hasContextMappings
argument_list|()
condition|)
block|{
name|ContextMappings
name|contextMappings
init|=
name|fieldType
operator|.
name|getContextMappings
argument_list|()
decl_stmt|;
return|return
name|contextMappings
operator|.
name|toContextQuery
argument_list|(
name|query
argument_list|,
name|queryContexts
argument_list|)
return|;
block|}
return|return
name|query
return|;
block|}
block|}
end_class

end_unit

