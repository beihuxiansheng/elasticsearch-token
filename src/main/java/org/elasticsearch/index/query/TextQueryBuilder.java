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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilder
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
comment|/**  * Text query is a query that analyzes the text and constructs a query as the result of the analysis. It  * can construct different queries based on the type provided.  */
end_comment

begin_class
DECL|class|TextQueryBuilder
specifier|public
class|class
name|TextQueryBuilder
extends|extends
name|BaseQueryBuilder
block|{
DECL|enum|Operator
specifier|public
specifier|static
enum|enum
name|Operator
block|{
DECL|enum constant|OR
name|OR
block|,
DECL|enum constant|AND
name|AND
block|}
DECL|enum|Type
specifier|public
specifier|static
enum|enum
name|Type
block|{
comment|/**          * The text is analyzed and terms are added to a boolean query.          */
DECL|enum constant|BOOLEAN
name|BOOLEAN
block|,
comment|/**          * The text is analyzed and used as a phrase query.          */
DECL|enum constant|PHRASE
name|PHRASE
block|,
comment|/**          * The text is analyzed and used in a phrase query, with the last term acting as a prefix.          */
DECL|enum constant|PHRASE_PREFIX
name|PHRASE_PREFIX
block|}
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|text
specifier|private
specifier|final
name|Object
name|text
decl_stmt|;
DECL|field|type
specifier|private
name|Type
name|type
decl_stmt|;
DECL|field|operator
specifier|private
name|Operator
name|operator
decl_stmt|;
DECL|field|analyzer
specifier|private
name|String
name|analyzer
decl_stmt|;
DECL|field|boost
specifier|private
name|Float
name|boost
decl_stmt|;
DECL|field|slop
specifier|private
name|Integer
name|slop
decl_stmt|;
DECL|field|fuzziness
specifier|private
name|String
name|fuzziness
decl_stmt|;
DECL|field|prefixLength
specifier|private
name|Integer
name|prefixLength
decl_stmt|;
DECL|field|maxExpansions
specifier|private
name|Integer
name|maxExpansions
decl_stmt|;
DECL|field|minimumShouldMatch
specifier|private
name|String
name|minimumShouldMatch
decl_stmt|;
DECL|field|rewrite
specifier|private
name|String
name|rewrite
init|=
literal|null
decl_stmt|;
DECL|field|fuzzyRewrite
specifier|private
name|String
name|fuzzyRewrite
init|=
literal|null
decl_stmt|;
comment|/**      * Constructs a new text query.      */
DECL|method|TextQueryBuilder
specifier|public
name|TextQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|text
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
comment|/**      * Sets the type of the text query.      */
DECL|method|type
specifier|public
name|TextQueryBuilder
name|type
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the operator to use when using a boolean query. Defaults to<tt>OR</tt>.      */
DECL|method|operator
specifier|public
name|TextQueryBuilder
name|operator
parameter_list|(
name|Operator
name|operator
parameter_list|)
block|{
name|this
operator|.
name|operator
operator|=
name|operator
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Explicitly set the analyzer to use. Defaults to use explicit mapping config for the field, or, if not      * set, the default search analyzer.      */
DECL|method|analyzer
specifier|public
name|TextQueryBuilder
name|analyzer
parameter_list|(
name|String
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the boost to apply to the query.      */
DECL|method|boost
specifier|public
name|TextQueryBuilder
name|boost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the phrase slop if evaluated to a phrase query type.      */
DECL|method|slop
specifier|public
name|TextQueryBuilder
name|slop
parameter_list|(
name|int
name|slop
parameter_list|)
block|{
name|this
operator|.
name|slop
operator|=
name|slop
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the minimum similarity used when evaluated to a fuzzy query type. Defaults to "0.5".      */
DECL|method|fuzziness
specifier|public
name|TextQueryBuilder
name|fuzziness
parameter_list|(
name|Object
name|fuzziness
parameter_list|)
block|{
name|this
operator|.
name|fuzziness
operator|=
name|fuzziness
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|prefixLength
specifier|public
name|TextQueryBuilder
name|prefixLength
parameter_list|(
name|int
name|prefixLength
parameter_list|)
block|{
name|this
operator|.
name|prefixLength
operator|=
name|prefixLength
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * When using fuzzy or prefix type query, the number of term expansions to use. Defaults to unbounded      * so its recommended to set it to a reasonable value for faster execution.      */
DECL|method|maxExpansions
specifier|public
name|TextQueryBuilder
name|maxExpansions
parameter_list|(
name|int
name|maxExpansions
parameter_list|)
block|{
name|this
operator|.
name|maxExpansions
operator|=
name|maxExpansions
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|minimumShouldMatch
specifier|public
name|TextQueryBuilder
name|minimumShouldMatch
parameter_list|(
name|String
name|minimumShouldMatch
parameter_list|)
block|{
name|this
operator|.
name|minimumShouldMatch
operator|=
name|minimumShouldMatch
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|rewrite
specifier|public
name|TextQueryBuilder
name|rewrite
parameter_list|(
name|String
name|rewrite
parameter_list|)
block|{
name|this
operator|.
name|rewrite
operator|=
name|rewrite
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fuzzyRewrite
specifier|public
name|TextQueryBuilder
name|fuzzyRewrite
parameter_list|(
name|String
name|fuzzyRewrite
parameter_list|)
block|{
name|this
operator|.
name|fuzzyRewrite
operator|=
name|fuzzyRewrite
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|doXContent
specifier|public
name|void
name|doXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|TextQueryParser
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|text
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|type
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|operator
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"operator"
argument_list|,
name|operator
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|analyzer
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"analyzer"
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|boost
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|slop
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"slop"
argument_list|,
name|slop
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fuzziness
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"fuzziness"
argument_list|,
name|fuzziness
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|prefixLength
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"prefix_length"
argument_list|,
name|prefixLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxExpansions
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"max_expansions"
argument_list|,
name|maxExpansions
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minimumShouldMatch
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"minimum_should_match"
argument_list|,
name|minimumShouldMatch
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rewrite
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"rewrite"
argument_list|,
name|rewrite
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fuzzyRewrite
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"fuzzy_rewrite"
argument_list|,
name|fuzzyRewrite
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

