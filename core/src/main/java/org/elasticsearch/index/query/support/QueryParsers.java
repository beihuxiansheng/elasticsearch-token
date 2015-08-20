begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|support
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
name|MultiTermQuery
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
name|Nullable
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
name|ParseField
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
name|ParseFieldMatcher
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|QueryParsers
specifier|public
specifier|final
class|class
name|QueryParsers
block|{
DECL|field|CONSTANT_SCORE
specifier|public
specifier|static
specifier|final
name|ParseField
name|CONSTANT_SCORE
init|=
operator|new
name|ParseField
argument_list|(
literal|"constant_score"
argument_list|,
literal|"constant_score_auto"
argument_list|,
literal|"constant_score_filter"
argument_list|)
decl_stmt|;
DECL|field|SCORING_BOOLEAN
specifier|public
specifier|static
specifier|final
name|ParseField
name|SCORING_BOOLEAN
init|=
operator|new
name|ParseField
argument_list|(
literal|"scoring_boolean"
argument_list|)
decl_stmt|;
DECL|field|CONSTANT_SCORE_BOOLEAN
specifier|public
specifier|static
specifier|final
name|ParseField
name|CONSTANT_SCORE_BOOLEAN
init|=
operator|new
name|ParseField
argument_list|(
literal|"constant_score_boolean"
argument_list|)
decl_stmt|;
DECL|field|TOP_TERMS
specifier|public
specifier|static
specifier|final
name|ParseField
name|TOP_TERMS
init|=
operator|new
name|ParseField
argument_list|(
literal|"top_terms_"
argument_list|)
decl_stmt|;
DECL|field|TOP_TERMS_BOOST
specifier|public
specifier|static
specifier|final
name|ParseField
name|TOP_TERMS_BOOST
init|=
operator|new
name|ParseField
argument_list|(
literal|"top_terms_boost_"
argument_list|)
decl_stmt|;
DECL|field|TOP_TERMS_BLENDED_FREQS
specifier|public
specifier|static
specifier|final
name|ParseField
name|TOP_TERMS_BLENDED_FREQS
init|=
operator|new
name|ParseField
argument_list|(
literal|"top_terms_blended_freqs_"
argument_list|)
decl_stmt|;
DECL|method|QueryParsers
specifier|private
name|QueryParsers
parameter_list|()
block|{      }
DECL|method|setRewriteMethod
specifier|public
specifier|static
name|void
name|setRewriteMethod
parameter_list|(
name|MultiTermQuery
name|query
parameter_list|,
annotation|@
name|Nullable
name|MultiTermQuery
operator|.
name|RewriteMethod
name|rewriteMethod
parameter_list|)
block|{
if|if
condition|(
name|rewriteMethod
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|query
operator|.
name|setRewriteMethod
argument_list|(
name|rewriteMethod
argument_list|)
expr_stmt|;
block|}
DECL|method|setRewriteMethod
specifier|public
specifier|static
name|void
name|setRewriteMethod
parameter_list|(
name|MultiTermQuery
name|query
parameter_list|,
name|ParseFieldMatcher
name|matcher
parameter_list|,
annotation|@
name|Nullable
name|String
name|rewriteMethod
parameter_list|)
block|{
if|if
condition|(
name|rewriteMethod
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|query
operator|.
name|setRewriteMethod
argument_list|(
name|parseRewriteMethod
argument_list|(
name|matcher
argument_list|,
name|rewriteMethod
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|parseRewriteMethod
specifier|public
specifier|static
name|MultiTermQuery
operator|.
name|RewriteMethod
name|parseRewriteMethod
parameter_list|(
name|ParseFieldMatcher
name|matcher
parameter_list|,
annotation|@
name|Nullable
name|String
name|rewriteMethod
parameter_list|)
block|{
return|return
name|parseRewriteMethod
argument_list|(
name|matcher
argument_list|,
name|rewriteMethod
argument_list|,
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_REWRITE
argument_list|)
return|;
block|}
DECL|method|parseRewriteMethod
specifier|public
specifier|static
name|MultiTermQuery
operator|.
name|RewriteMethod
name|parseRewriteMethod
parameter_list|(
name|ParseFieldMatcher
name|matcher
parameter_list|,
annotation|@
name|Nullable
name|String
name|rewriteMethod
parameter_list|,
annotation|@
name|Nullable
name|MultiTermQuery
operator|.
name|RewriteMethod
name|defaultRewriteMethod
parameter_list|)
block|{
if|if
condition|(
name|rewriteMethod
operator|==
literal|null
condition|)
block|{
return|return
name|defaultRewriteMethod
return|;
block|}
if|if
condition|(
name|matcher
operator|.
name|match
argument_list|(
name|rewriteMethod
argument_list|,
name|CONSTANT_SCORE
argument_list|)
condition|)
block|{
return|return
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_REWRITE
return|;
block|}
if|if
condition|(
name|matcher
operator|.
name|match
argument_list|(
name|rewriteMethod
argument_list|,
name|SCORING_BOOLEAN
argument_list|)
condition|)
block|{
return|return
name|MultiTermQuery
operator|.
name|SCORING_BOOLEAN_REWRITE
return|;
block|}
if|if
condition|(
name|matcher
operator|.
name|match
argument_list|(
name|rewriteMethod
argument_list|,
name|CONSTANT_SCORE_BOOLEAN
argument_list|)
condition|)
block|{
return|return
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_BOOLEAN_REWRITE
return|;
block|}
name|int
name|firstDigit
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rewriteMethod
operator|.
name|length
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|Character
operator|.
name|isDigit
argument_list|(
name|rewriteMethod
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|firstDigit
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|firstDigit
operator|>=
literal|0
condition|)
block|{
specifier|final
name|int
name|size
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|rewriteMethod
operator|.
name|substring
argument_list|(
name|firstDigit
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|rewriteMethodName
init|=
name|rewriteMethod
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|firstDigit
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|match
argument_list|(
name|rewriteMethodName
argument_list|,
name|TOP_TERMS
argument_list|)
condition|)
block|{
return|return
operator|new
name|MultiTermQuery
operator|.
name|TopTermsScoringBooleanQueryRewrite
argument_list|(
name|size
argument_list|)
return|;
block|}
if|if
condition|(
name|matcher
operator|.
name|match
argument_list|(
name|rewriteMethodName
argument_list|,
name|TOP_TERMS_BOOST
argument_list|)
condition|)
block|{
return|return
operator|new
name|MultiTermQuery
operator|.
name|TopTermsBoostOnlyBooleanQueryRewrite
argument_list|(
name|size
argument_list|)
return|;
block|}
if|if
condition|(
name|matcher
operator|.
name|match
argument_list|(
name|rewriteMethodName
argument_list|,
name|TOP_TERMS_BLENDED_FREQS
argument_list|)
condition|)
block|{
return|return
operator|new
name|MultiTermQuery
operator|.
name|TopTermsBlendedFreqScoringRewrite
argument_list|(
name|size
argument_list|)
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Failed to parse rewrite_method ["
operator|+
name|rewriteMethod
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit
