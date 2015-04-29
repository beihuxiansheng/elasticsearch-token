begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
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
name|BooleanQuery
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
name|similarities
operator|.
name|Similarity
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
comment|/**  * CommonTermsQuery query is a query that executes high-frequency terms in a  * optional sub-query to prevent slow queries due to "common" terms like  * stopwords. This query basically builds 2 queries off the {@link #add(Term)  * added} terms where low-frequency terms are added to a required boolean clause  * and high-frequency terms are added to an optional boolean clause. The  * optional clause is only executed if the required "low-frequency' clause  * matches. Scores produced by this query will be slightly different to plain  * {@link BooleanQuery} scorer mainly due to differences in the  * {@link Similarity#coord(int,int) number of leave queries} in the required  * boolean clause. In the most cases high-frequency terms are unlikely to  * significantly contribute to the document score unless at least one of the  * low-frequency terms are matched such that this query can improve query  * execution times significantly if applicable.  *<p>  */
end_comment

begin_class
DECL|class|CommonTermsQueryBuilder
specifier|public
class|class
name|CommonTermsQueryBuilder
extends|extends
name|BaseQueryBuilder
implements|implements
name|BoostableQueryBuilder
argument_list|<
name|CommonTermsQueryBuilder
argument_list|>
block|{
DECL|enum|Operator
specifier|public
specifier|static
enum|enum
name|Operator
block|{
DECL|enum constant|OR
DECL|enum constant|AND
name|OR
block|,
name|AND
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
DECL|field|highFreqOperator
specifier|private
name|Operator
name|highFreqOperator
init|=
literal|null
decl_stmt|;
DECL|field|lowFreqOperator
specifier|private
name|Operator
name|lowFreqOperator
init|=
literal|null
decl_stmt|;
DECL|field|analyzer
specifier|private
name|String
name|analyzer
init|=
literal|null
decl_stmt|;
DECL|field|boost
specifier|private
name|Float
name|boost
init|=
literal|null
decl_stmt|;
DECL|field|lowFreqMinimumShouldMatch
specifier|private
name|String
name|lowFreqMinimumShouldMatch
init|=
literal|null
decl_stmt|;
DECL|field|highFreqMinimumShouldMatch
specifier|private
name|String
name|highFreqMinimumShouldMatch
init|=
literal|null
decl_stmt|;
DECL|field|disableCoords
specifier|private
name|Boolean
name|disableCoords
init|=
literal|null
decl_stmt|;
DECL|field|cutoffFrequency
specifier|private
name|Float
name|cutoffFrequency
init|=
literal|null
decl_stmt|;
DECL|field|queryName
specifier|private
name|String
name|queryName
decl_stmt|;
comment|/**      * Constructs a new common terms query.      */
DECL|method|CommonTermsQueryBuilder
specifier|public
name|CommonTermsQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|text
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Field name must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|text
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Query must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * Sets the operator to use for terms with a high document frequency      * (greater than or equal to {@link #cutoffFrequency(float)}. Defaults to      *<tt>AND</tt>.      */
DECL|method|highFreqOperator
specifier|public
name|CommonTermsQueryBuilder
name|highFreqOperator
parameter_list|(
name|Operator
name|operator
parameter_list|)
block|{
name|this
operator|.
name|highFreqOperator
operator|=
name|operator
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the operator to use for terms with a low document frequency (less      * than {@link #cutoffFrequency(float)}. Defaults to<tt>AND</tt>.      */
DECL|method|lowFreqOperator
specifier|public
name|CommonTermsQueryBuilder
name|lowFreqOperator
parameter_list|(
name|Operator
name|operator
parameter_list|)
block|{
name|this
operator|.
name|lowFreqOperator
operator|=
name|operator
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Explicitly set the analyzer to use. Defaults to use explicit mapping      * config for the field, or, if not set, the default search analyzer.      */
DECL|method|analyzer
specifier|public
name|CommonTermsQueryBuilder
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
annotation|@
name|Override
DECL|method|boost
specifier|public
name|CommonTermsQueryBuilder
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
comment|/**      * Sets the cutoff document frequency for high / low frequent terms. A value      * in [0..1] (or absolute number>=1) representing the maximum threshold of      * a terms document frequency to be considered a low frequency term.      * Defaults to      *<tt>{@value CommonTermsQueryParser#DEFAULT_MAX_TERM_DOC_FREQ}</tt>      */
DECL|method|cutoffFrequency
specifier|public
name|CommonTermsQueryBuilder
name|cutoffFrequency
parameter_list|(
name|float
name|cutoffFrequency
parameter_list|)
block|{
name|this
operator|.
name|cutoffFrequency
operator|=
name|cutoffFrequency
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the minimum number of high frequent query terms that need to match in order to      * produce a hit when there are no low frequen terms.      */
DECL|method|highFreqMinimumShouldMatch
specifier|public
name|CommonTermsQueryBuilder
name|highFreqMinimumShouldMatch
parameter_list|(
name|String
name|highFreqMinimumShouldMatch
parameter_list|)
block|{
name|this
operator|.
name|highFreqMinimumShouldMatch
operator|=
name|highFreqMinimumShouldMatch
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the minimum number of low frequent query terms that need to match in order to      * produce a hit.      */
DECL|method|lowFreqMinimumShouldMatch
specifier|public
name|CommonTermsQueryBuilder
name|lowFreqMinimumShouldMatch
parameter_list|(
name|String
name|lowFreqMinimumShouldMatch
parameter_list|)
block|{
name|this
operator|.
name|lowFreqMinimumShouldMatch
operator|=
name|lowFreqMinimumShouldMatch
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the query name for the filter that can be used when searching for matched_filters per hit.      */
DECL|method|queryName
specifier|public
name|CommonTermsQueryBuilder
name|queryName
parameter_list|(
name|String
name|queryName
parameter_list|)
block|{
name|this
operator|.
name|queryName
operator|=
name|queryName
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
name|CommonTermsQueryParser
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
name|disableCoords
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"disable_coords"
argument_list|,
name|disableCoords
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|highFreqOperator
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"high_freq_operator"
argument_list|,
name|highFreqOperator
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lowFreqOperator
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"low_freq_operator"
argument_list|,
name|lowFreqOperator
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
name|cutoffFrequency
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"cutoff_frequency"
argument_list|,
name|cutoffFrequency
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lowFreqMinimumShouldMatch
operator|!=
literal|null
operator|||
name|highFreqMinimumShouldMatch
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"minimum_should_match"
argument_list|)
expr_stmt|;
if|if
condition|(
name|lowFreqMinimumShouldMatch
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"low_freq"
argument_list|,
name|lowFreqMinimumShouldMatch
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|highFreqMinimumShouldMatch
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"high_freq"
argument_list|,
name|highFreqMinimumShouldMatch
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|queryName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_name"
argument_list|,
name|queryName
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

