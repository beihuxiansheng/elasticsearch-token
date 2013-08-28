begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|Lists
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
name|sandbox
operator|.
name|queries
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
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|analysis
operator|.
name|Analysis
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

begin_comment
comment|/**  *<pre>  * {  *  fuzzy_like_this : {  *      maxNumTerms : 12,  *      boost : 1.1,  *      fields : ["field1", "field2"]  *      likeText : "..."  *  }  * }  *</pre>  */
end_comment

begin_class
DECL|class|FuzzyLikeThisQueryParser
specifier|public
class|class
name|FuzzyLikeThisQueryParser
implements|implements
name|QueryParser
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"flt"
decl_stmt|;
annotation|@
name|Inject
DECL|method|FuzzyLikeThisQueryParser
specifier|public
name|FuzzyLikeThisQueryParser
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|names
specifier|public
name|String
index|[]
name|names
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
name|NAME
block|,
literal|"fuzzy_like_this"
block|,
literal|"fuzzyLikeThis"
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|int
name|maxNumTerms
init|=
literal|25
decl_stmt|;
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fields
init|=
literal|null
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
name|Analyzer
name|analyzer
init|=
literal|null
decl_stmt|;
name|boolean
name|failOnUnsupportedField
init|=
literal|true
decl_stmt|;
name|String
name|queryName
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
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
literal|"like_text"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
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
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"max_query_terms"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"maxQueryTerms"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|maxNumTerms
operator|=
name|parser
operator|.
name|intValue
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
name|parser
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"ignore_tf"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
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
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"min_similarity"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"minSimilarity"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|minSimilarity
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
literal|"prefix_length"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"prefixLength"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|prefixLength
operator|=
name|parser
operator|.
name|intValue
argument_list|()
expr_stmt|;
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
name|parseContext
operator|.
name|analysisService
argument_list|()
operator|.
name|analyzer
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"fail_on_unsupported_field"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"failOnUnsupportedField"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|failOnUnsupportedField
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"_name"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|queryName
operator|=
name|parser
operator|.
name|text
argument_list|()
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
literal|"[flt] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
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
name|START_ARRAY
condition|)
block|{
if|if
condition|(
literal|"fields"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|fields
operator|=
name|Lists
operator|.
name|newLinkedList
argument_list|()
expr_stmt|;
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
name|END_ARRAY
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|parseContext
operator|.
name|indexName
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"[flt] query does not support ["
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
name|likeText
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
literal|"fuzzy_like_this requires 'like_text' to be specified"
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
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|parseContext
operator|.
name|defaultField
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fields
operator|.
name|isEmpty
argument_list|()
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
literal|"fuzzy_like_this requires 'fields' to be non-empty"
argument_list|)
throw|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|fields
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|String
name|fieldName
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Analysis
operator|.
name|generatesCharacterTokenStream
argument_list|(
name|analyzer
argument_list|,
name|fieldName
argument_list|)
condition|)
block|{
if|if
condition|(
name|failOnUnsupportedField
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"more_like_this doesn't support binary/numeric fields: ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
else|else
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|fields
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|query
operator|.
name|addTerms
argument_list|(
name|likeText
argument_list|,
name|field
argument_list|,
name|minSimilarity
argument_list|,
name|prefixLength
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|queryName
operator|!=
literal|null
condition|)
block|{
name|parseContext
operator|.
name|addNamedQuery
argument_list|(
name|queryName
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
block|}
end_class

end_unit

