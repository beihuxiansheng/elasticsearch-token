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
name|geo
operator|.
name|builders
operator|.
name|ShapeBuilder
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
name|FunctionScoreQueryBuilder
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
name|ScoreFunctionBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_comment
comment|/**  * A static factory for simple "import static" usage.  */
end_comment

begin_class
DECL|class|QueryBuilders
specifier|public
specifier|abstract
class|class
name|QueryBuilders
block|{
comment|/**      * A query that match on all documents.      */
DECL|method|matchAllQuery
specifier|public
specifier|static
name|MatchAllQueryBuilder
name|matchAllQuery
parameter_list|()
block|{
return|return
operator|new
name|MatchAllQueryBuilder
argument_list|()
return|;
block|}
comment|/**      * Creates a match query with type "BOOLEAN" for the provided field name and text.      *      * @param name The field name.      * @param text The query text (to be analyzed).      */
DECL|method|matchQuery
specifier|public
specifier|static
name|MatchQueryBuilder
name|matchQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|text
parameter_list|)
block|{
return|return
operator|new
name|MatchQueryBuilder
argument_list|(
name|name
argument_list|,
name|text
argument_list|)
operator|.
name|type
argument_list|(
name|MatchQueryBuilder
operator|.
name|Type
operator|.
name|BOOLEAN
argument_list|)
return|;
block|}
comment|/**      * Creates a common query for the provided field name and text.      *      * @param name The field name.      * @param text The query text (to be analyzed).      */
DECL|method|commonTermsQuery
specifier|public
specifier|static
name|CommonTermsQueryBuilder
name|commonTermsQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|text
parameter_list|)
block|{
return|return
operator|new
name|CommonTermsQueryBuilder
argument_list|(
name|name
argument_list|,
name|text
argument_list|)
return|;
block|}
comment|/**      * Creates a match query with type "BOOLEAN" for the provided field name and text.      *      * @param fieldNames The field names.      * @param text       The query text (to be analyzed).      */
DECL|method|multiMatchQuery
specifier|public
specifier|static
name|MultiMatchQueryBuilder
name|multiMatchQuery
parameter_list|(
name|Object
name|text
parameter_list|,
name|String
modifier|...
name|fieldNames
parameter_list|)
block|{
return|return
operator|new
name|MultiMatchQueryBuilder
argument_list|(
name|text
argument_list|,
name|fieldNames
argument_list|)
return|;
comment|// BOOLEAN is the default
block|}
comment|/**      * Creates a text query with type "PHRASE" for the provided field name and text.      *      * @param name The field name.      * @param text The query text (to be analyzed).      */
DECL|method|matchPhraseQuery
specifier|public
specifier|static
name|MatchQueryBuilder
name|matchPhraseQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|text
parameter_list|)
block|{
return|return
operator|new
name|MatchQueryBuilder
argument_list|(
name|name
argument_list|,
name|text
argument_list|)
operator|.
name|type
argument_list|(
name|MatchQueryBuilder
operator|.
name|Type
operator|.
name|PHRASE
argument_list|)
return|;
block|}
comment|/**      * Creates a match query with type "PHRASE_PREFIX" for the provided field name and text.      *      * @param name The field name.      * @param text The query text (to be analyzed).      */
DECL|method|matchPhrasePrefixQuery
specifier|public
specifier|static
name|MatchQueryBuilder
name|matchPhrasePrefixQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|text
parameter_list|)
block|{
return|return
operator|new
name|MatchQueryBuilder
argument_list|(
name|name
argument_list|,
name|text
argument_list|)
operator|.
name|type
argument_list|(
name|MatchQueryBuilder
operator|.
name|Type
operator|.
name|PHRASE_PREFIX
argument_list|)
return|;
block|}
comment|/**      * A query that generates the union of documents produced by its sub-queries, and that scores each document      * with the maximum score for that document as produced by any sub-query, plus a tie breaking increment for any      * additional matching sub-queries.      */
DECL|method|disMaxQuery
specifier|public
specifier|static
name|DisMaxQueryBuilder
name|disMaxQuery
parameter_list|()
block|{
return|return
operator|new
name|DisMaxQueryBuilder
argument_list|()
return|;
block|}
comment|/**      * Constructs a query that will match only specific ids within types.      *      * @param types The mapping/doc type      */
DECL|method|idsQuery
specifier|public
specifier|static
name|IdsQueryBuilder
name|idsQuery
parameter_list|(
annotation|@
name|Nullable
name|String
modifier|...
name|types
parameter_list|)
block|{
return|return
operator|new
name|IdsQueryBuilder
argument_list|(
name|types
argument_list|)
return|;
block|}
comment|/**      * A Query that matches documents containing a term.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|termQuery
specifier|public
specifier|static
name|TermQueryBuilder
name|termQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * A Query that matches documents containing a term.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|termQuery
specifier|public
specifier|static
name|TermQueryBuilder
name|termQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * A Query that matches documents containing a term.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|termQuery
specifier|public
specifier|static
name|TermQueryBuilder
name|termQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * A Query that matches documents containing a term.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|termQuery
specifier|public
specifier|static
name|TermQueryBuilder
name|termQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * A Query that matches documents containing a term.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|termQuery
specifier|public
specifier|static
name|TermQueryBuilder
name|termQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * A Query that matches documents containing a term.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|termQuery
specifier|public
specifier|static
name|TermQueryBuilder
name|termQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * A Query that matches documents containing a term.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|termQuery
specifier|public
specifier|static
name|TermQueryBuilder
name|termQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * A Query that matches documents using fuzzy query.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|fuzzyQuery
specifier|public
specifier|static
name|FuzzyQueryBuilder
name|fuzzyQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|FuzzyQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * A Query that matches documents using fuzzy query.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|fuzzyQuery
specifier|public
specifier|static
name|FuzzyQueryBuilder
name|fuzzyQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
return|return
operator|new
name|FuzzyQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * A Query that matches documents containing terms with a specified prefix.      *      * @param name   The name of the field      * @param prefix The prefix query      */
DECL|method|prefixQuery
specifier|public
specifier|static
name|PrefixQueryBuilder
name|prefixQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
return|return
operator|new
name|PrefixQueryBuilder
argument_list|(
name|name
argument_list|,
name|prefix
argument_list|)
return|;
block|}
comment|/**      * A Query that matches documents within an range of terms.      *      * @param name The field name      */
DECL|method|rangeQuery
specifier|public
specifier|static
name|RangeQueryBuilder
name|rangeQuery
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|RangeQueryBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Implements the wildcard search query. Supported wildcards are<tt>*</tt>, which      * matches any character sequence (including the empty one), and<tt>?</tt>,      * which matches any single character. Note this query can be slow, as it      * needs to iterate over many terms. In order to prevent extremely slow WildcardQueries,      * a Wildcard term should not start with one of the wildcards<tt>*</tt> or      *<tt>?</tt>.      *      * @param name  The field name      * @param query The wildcard query string      */
DECL|method|wildcardQuery
specifier|public
specifier|static
name|WildcardQueryBuilder
name|wildcardQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|query
parameter_list|)
block|{
return|return
operator|new
name|WildcardQueryBuilder
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|/**      * A Query that matches documents containing terms with a specified regular expression.      *      * @param name   The name of the field      * @param regexp The regular expression      */
DECL|method|regexpQuery
specifier|public
specifier|static
name|RegexpQueryBuilder
name|regexpQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|regexp
parameter_list|)
block|{
return|return
operator|new
name|RegexpQueryBuilder
argument_list|(
name|name
argument_list|,
name|regexp
argument_list|)
return|;
block|}
comment|/**      * A query that parses a query string and runs it. There are two modes that this operates. The first,      * when no field is added (using {@link QueryStringQueryBuilder#field(String)}, will run the query once and non prefixed fields      * will use the {@link QueryStringQueryBuilder#defaultField(String)} set. The second, when one or more fields are added      * (using {@link QueryStringQueryBuilder#field(String)}), will run the parsed query against the provided fields, and combine      * them either using DisMax or a plain boolean query (see {@link QueryStringQueryBuilder#useDisMax(boolean)}).      *      * @param queryString The query string to run      */
DECL|method|queryStringQuery
specifier|public
specifier|static
name|QueryStringQueryBuilder
name|queryStringQuery
parameter_list|(
name|String
name|queryString
parameter_list|)
block|{
return|return
operator|new
name|QueryStringQueryBuilder
argument_list|(
name|queryString
argument_list|)
return|;
block|}
comment|/**      * A query that acts similar to a query_string query, but won't throw      * exceptions for any weird string syntax. See      * {@link org.apache.lucene.queryparser.XSimpleQueryParser} for the full      * supported syntax.      */
DECL|method|simpleQueryStringQuery
specifier|public
specifier|static
name|SimpleQueryStringBuilder
name|simpleQueryStringQuery
parameter_list|(
name|String
name|queryString
parameter_list|)
block|{
return|return
operator|new
name|SimpleQueryStringBuilder
argument_list|(
name|queryString
argument_list|)
return|;
block|}
comment|/**      * The BoostingQuery class can be used to effectively demote results that match a given query.      * Unlike the "NOT" clause, this still selects documents that contain undesirable terms,      * but reduces their overall score:      */
DECL|method|boostingQuery
specifier|public
specifier|static
name|BoostingQueryBuilder
name|boostingQuery
parameter_list|()
block|{
return|return
operator|new
name|BoostingQueryBuilder
argument_list|()
return|;
block|}
comment|/**      * A Query that matches documents matching boolean combinations of other queries.      */
DECL|method|boolQuery
specifier|public
specifier|static
name|BoolQueryBuilder
name|boolQuery
parameter_list|()
block|{
return|return
operator|new
name|BoolQueryBuilder
argument_list|()
return|;
block|}
DECL|method|spanTermQuery
specifier|public
specifier|static
name|SpanTermQueryBuilder
name|spanTermQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|SpanTermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|spanTermQuery
specifier|public
specifier|static
name|SpanTermQueryBuilder
name|spanTermQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|value
parameter_list|)
block|{
return|return
operator|new
name|SpanTermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|spanTermQuery
specifier|public
specifier|static
name|SpanTermQueryBuilder
name|spanTermQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
return|return
operator|new
name|SpanTermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|spanTermQuery
specifier|public
specifier|static
name|SpanTermQueryBuilder
name|spanTermQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|value
parameter_list|)
block|{
return|return
operator|new
name|SpanTermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|spanTermQuery
specifier|public
specifier|static
name|SpanTermQueryBuilder
name|spanTermQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|value
parameter_list|)
block|{
return|return
operator|new
name|SpanTermQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|spanFirstQuery
specifier|public
specifier|static
name|SpanFirstQueryBuilder
name|spanFirstQuery
parameter_list|(
name|SpanQueryBuilder
name|match
parameter_list|,
name|int
name|end
parameter_list|)
block|{
return|return
operator|new
name|SpanFirstQueryBuilder
argument_list|(
name|match
argument_list|,
name|end
argument_list|)
return|;
block|}
DECL|method|spanNearQuery
specifier|public
specifier|static
name|SpanNearQueryBuilder
name|spanNearQuery
parameter_list|()
block|{
return|return
operator|new
name|SpanNearQueryBuilder
argument_list|()
return|;
block|}
DECL|method|spanNotQuery
specifier|public
specifier|static
name|SpanNotQueryBuilder
name|spanNotQuery
parameter_list|()
block|{
return|return
operator|new
name|SpanNotQueryBuilder
argument_list|()
return|;
block|}
DECL|method|spanOrQuery
specifier|public
specifier|static
name|SpanOrQueryBuilder
name|spanOrQuery
parameter_list|()
block|{
return|return
operator|new
name|SpanOrQueryBuilder
argument_list|()
return|;
block|}
comment|/**      * Creates a {@link SpanQueryBuilder} which allows having a sub query      * which implements {@link MultiTermQueryBuilder}. This is useful for      * having e.g. wildcard or fuzzy queries inside spans.      *      * @param multiTermQueryBuilder The {@link MultiTermQueryBuilder} that      *                              backs the created builder.      * @return      */
DECL|method|spanMultiTermQueryBuilder
specifier|public
specifier|static
name|SpanMultiTermQueryBuilder
name|spanMultiTermQueryBuilder
parameter_list|(
name|MultiTermQueryBuilder
name|multiTermQueryBuilder
parameter_list|)
block|{
return|return
operator|new
name|SpanMultiTermQueryBuilder
argument_list|(
name|multiTermQueryBuilder
argument_list|)
return|;
block|}
DECL|method|fieldMaskingSpanQuery
specifier|public
specifier|static
name|FieldMaskingSpanQueryBuilder
name|fieldMaskingSpanQuery
parameter_list|(
name|SpanQueryBuilder
name|query
parameter_list|,
name|String
name|field
parameter_list|)
block|{
return|return
operator|new
name|FieldMaskingSpanQueryBuilder
argument_list|(
name|query
argument_list|,
name|field
argument_list|)
return|;
block|}
comment|/**      * A query that applies a filter to the results of another query.      *      * @param queryBuilder  The query to apply the filter to      * @param filterBuilder The filter to apply on the query      */
DECL|method|filteredQuery
specifier|public
specifier|static
name|FilteredQueryBuilder
name|filteredQuery
parameter_list|(
annotation|@
name|Nullable
name|QueryBuilder
name|queryBuilder
parameter_list|,
annotation|@
name|Nullable
name|FilterBuilder
name|filterBuilder
parameter_list|)
block|{
return|return
operator|new
name|FilteredQueryBuilder
argument_list|(
name|queryBuilder
argument_list|,
name|filterBuilder
argument_list|)
return|;
block|}
comment|/**      * A query that wraps a filter and simply returns a constant score equal to the      * query boost for every document in the filter.      *      * @param filterBuilder The filter to wrap in a constant score query      */
DECL|method|constantScoreQuery
specifier|public
specifier|static
name|ConstantScoreQueryBuilder
name|constantScoreQuery
parameter_list|(
name|FilterBuilder
name|filterBuilder
parameter_list|)
block|{
return|return
operator|new
name|ConstantScoreQueryBuilder
argument_list|(
name|filterBuilder
argument_list|)
return|;
block|}
comment|/**      * A query that wraps another query and simply returns a constant score equal to the      * query boost for every document in the query.      *      * @param queryBuilder The query to wrap in a constant score query      */
DECL|method|constantScoreQuery
specifier|public
specifier|static
name|ConstantScoreQueryBuilder
name|constantScoreQuery
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
return|return
operator|new
name|ConstantScoreQueryBuilder
argument_list|(
name|queryBuilder
argument_list|)
return|;
block|}
comment|/**      * A query that allows to define a custom scoring function.      *      * @param queryBuilder The query to custom score      */
DECL|method|functionScoreQuery
specifier|public
specifier|static
name|FunctionScoreQueryBuilder
name|functionScoreQuery
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
return|return
operator|new
name|FunctionScoreQueryBuilder
argument_list|(
name|queryBuilder
argument_list|)
return|;
block|}
comment|/**      * A query that allows to define a custom scoring function.      */
DECL|method|functionScoreQuery
specifier|public
specifier|static
name|FunctionScoreQueryBuilder
name|functionScoreQuery
parameter_list|()
block|{
return|return
operator|new
name|FunctionScoreQueryBuilder
argument_list|()
return|;
block|}
comment|/**      * A query that allows to define a custom scoring function.      *      * @param function The function builder used to custom score      */
DECL|method|functionScoreQuery
specifier|public
specifier|static
name|FunctionScoreQueryBuilder
name|functionScoreQuery
parameter_list|(
name|ScoreFunctionBuilder
name|function
parameter_list|)
block|{
return|return
operator|new
name|FunctionScoreQueryBuilder
argument_list|(
name|function
argument_list|)
return|;
block|}
comment|/**      * A query that allows to define a custom scoring function.      *      * @param queryBuilder The query to custom score      * @param function     The function builder used to custom score      */
DECL|method|functionScoreQuery
specifier|public
specifier|static
name|FunctionScoreQueryBuilder
name|functionScoreQuery
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|,
name|ScoreFunctionBuilder
name|function
parameter_list|)
block|{
return|return
operator|(
operator|new
name|FunctionScoreQueryBuilder
argument_list|(
name|queryBuilder
argument_list|)
operator|)
operator|.
name|add
argument_list|(
name|function
argument_list|)
return|;
block|}
comment|/**      * A query that allows to define a custom scoring function.      *      * @param filterBuilder The query to custom score      * @param function      The function builder used to custom score      */
DECL|method|functionScoreQuery
specifier|public
specifier|static
name|FunctionScoreQueryBuilder
name|functionScoreQuery
parameter_list|(
name|FilterBuilder
name|filterBuilder
parameter_list|,
name|ScoreFunctionBuilder
name|function
parameter_list|)
block|{
return|return
operator|(
operator|new
name|FunctionScoreQueryBuilder
argument_list|(
name|filterBuilder
argument_list|)
operator|)
operator|.
name|add
argument_list|(
name|function
argument_list|)
return|;
block|}
comment|/**      * A query that allows to define a custom scoring function.      *      * @param filterBuilder The filterBuilder to custom score      */
DECL|method|functionScoreQuery
specifier|public
specifier|static
name|FunctionScoreQueryBuilder
name|functionScoreQuery
parameter_list|(
name|FilterBuilder
name|filterBuilder
parameter_list|)
block|{
return|return
operator|new
name|FunctionScoreQueryBuilder
argument_list|(
name|filterBuilder
argument_list|)
return|;
block|}
comment|/**      * A query that allows to define a custom scoring function.      *      * @param queryBuilder  The query to custom score      * @param filterBuilder The filterBuilder to custom score      */
DECL|method|functionScoreQuery
specifier|public
specifier|static
name|FunctionScoreQueryBuilder
name|functionScoreQuery
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|,
name|FilterBuilder
name|filterBuilder
parameter_list|)
block|{
return|return
operator|new
name|FunctionScoreQueryBuilder
argument_list|(
name|queryBuilder
argument_list|,
name|filterBuilder
argument_list|)
return|;
block|}
comment|/**      * A query that allows to define a custom scoring function.      *      * @param queryBuilder  The query to custom score      * @param filterBuilder The filterBuilder to custom score      */
DECL|method|functionScoreQuery
specifier|public
specifier|static
name|FunctionScoreQueryBuilder
name|functionScoreQuery
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|,
name|FilterBuilder
name|filterBuilder
parameter_list|,
name|ScoreFunctionBuilder
name|function
parameter_list|)
block|{
return|return
operator|(
operator|new
name|FunctionScoreQueryBuilder
argument_list|(
name|queryBuilder
argument_list|,
name|filterBuilder
argument_list|)
operator|)
operator|.
name|add
argument_list|(
name|function
argument_list|)
return|;
block|}
comment|/**      * A more like this query that finds documents that are "like" the provided {@link MoreLikeThisQueryBuilder#likeText(String)}      * which is checked against the fields the query is constructed with.      *      * @param fields The fields to run the query against      */
DECL|method|moreLikeThisQuery
specifier|public
specifier|static
name|MoreLikeThisQueryBuilder
name|moreLikeThisQuery
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
return|return
operator|new
name|MoreLikeThisQueryBuilder
argument_list|(
name|fields
argument_list|)
return|;
block|}
comment|/**      * A more like this query that finds documents that are "like" the provided {@link MoreLikeThisQueryBuilder#likeText(String)}      * which is checked against the "_all" field.      */
DECL|method|moreLikeThisQuery
specifier|public
specifier|static
name|MoreLikeThisQueryBuilder
name|moreLikeThisQuery
parameter_list|()
block|{
return|return
operator|new
name|MoreLikeThisQueryBuilder
argument_list|()
return|;
block|}
comment|/**      * Constructs a new scoring child query, with the child type and the query to run on the child documents. The      * results of this query are the parent docs that those child docs matched.      *      * @param type  The child type.      * @param query The query.      */
DECL|method|topChildrenQuery
specifier|public
specifier|static
name|TopChildrenQueryBuilder
name|topChildrenQuery
parameter_list|(
name|String
name|type
parameter_list|,
name|QueryBuilder
name|query
parameter_list|)
block|{
return|return
operator|new
name|TopChildrenQueryBuilder
argument_list|(
name|type
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|/**      * Constructs a new NON scoring child query, with the child type and the query to run on the child documents. The      * results of this query are the parent docs that those child docs matched.      *      * @param type  The child type.      * @param query The query.      */
DECL|method|hasChildQuery
specifier|public
specifier|static
name|HasChildQueryBuilder
name|hasChildQuery
parameter_list|(
name|String
name|type
parameter_list|,
name|QueryBuilder
name|query
parameter_list|)
block|{
return|return
operator|new
name|HasChildQueryBuilder
argument_list|(
name|type
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|/**      * Constructs a new NON scoring parent query, with the parent type and the query to run on the parent documents. The      * results of this query are the children docs that those parent docs matched.      *      * @param type  The parent type.      * @param query The query.      */
DECL|method|hasParentQuery
specifier|public
specifier|static
name|HasParentQueryBuilder
name|hasParentQuery
parameter_list|(
name|String
name|type
parameter_list|,
name|QueryBuilder
name|query
parameter_list|)
block|{
return|return
operator|new
name|HasParentQueryBuilder
argument_list|(
name|type
argument_list|,
name|query
argument_list|)
return|;
block|}
DECL|method|nestedQuery
specifier|public
specifier|static
name|NestedQueryBuilder
name|nestedQuery
parameter_list|(
name|String
name|path
parameter_list|,
name|QueryBuilder
name|query
parameter_list|)
block|{
return|return
operator|new
name|NestedQueryBuilder
argument_list|(
name|path
argument_list|,
name|query
argument_list|)
return|;
block|}
DECL|method|nestedQuery
specifier|public
specifier|static
name|NestedQueryBuilder
name|nestedQuery
parameter_list|(
name|String
name|path
parameter_list|,
name|FilterBuilder
name|filter
parameter_list|)
block|{
return|return
operator|new
name|NestedQueryBuilder
argument_list|(
name|path
argument_list|,
name|filter
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|termsQuery
specifier|public
specifier|static
name|TermsQueryBuilder
name|termsQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsQueryBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|termsQuery
specifier|public
specifier|static
name|TermsQueryBuilder
name|termsQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|int
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsQueryBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|termsQuery
specifier|public
specifier|static
name|TermsQueryBuilder
name|termsQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|long
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsQueryBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|termsQuery
specifier|public
specifier|static
name|TermsQueryBuilder
name|termsQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|float
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsQueryBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|termsQuery
specifier|public
specifier|static
name|TermsQueryBuilder
name|termsQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|double
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsQueryBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|termsQuery
specifier|public
specifier|static
name|TermsQueryBuilder
name|termsQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
modifier|...
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsQueryBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A filer for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|termsQuery
specifier|public
specifier|static
name|TermsQueryBuilder
name|termsQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|Collection
argument_list|<
name|?
argument_list|>
name|values
parameter_list|)
block|{
return|return
operator|new
name|TermsQueryBuilder
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
return|;
block|}
comment|/**      * A query that will execute the wrapped query only for the specified indices, and "match_all" when      * it does not match those indices.      */
DECL|method|indicesQuery
specifier|public
specifier|static
name|IndicesQueryBuilder
name|indicesQuery
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|,
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|IndicesQueryBuilder
argument_list|(
name|queryBuilder
argument_list|,
name|indices
argument_list|)
return|;
block|}
comment|/**      * A Query builder which allows building a query thanks to a JSON string or binary data.      */
DECL|method|wrapperQuery
specifier|public
specifier|static
name|WrapperQueryBuilder
name|wrapperQuery
parameter_list|(
name|String
name|source
parameter_list|)
block|{
return|return
operator|new
name|WrapperQueryBuilder
argument_list|(
name|source
argument_list|)
return|;
block|}
comment|/**      * A Query builder which allows building a query thanks to a JSON string or binary data.      */
DECL|method|wrapperQuery
specifier|public
specifier|static
name|WrapperQueryBuilder
name|wrapperQuery
parameter_list|(
name|byte
index|[]
name|source
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
operator|new
name|WrapperQueryBuilder
argument_list|(
name|source
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/**      * Query that matches Documents based on the relationship between the given shape and      * indexed shapes      *      * @param name  The shape field name      * @param shape Shape to use in the Query      */
DECL|method|geoShapeQuery
specifier|public
specifier|static
name|GeoShapeQueryBuilder
name|geoShapeQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|ShapeBuilder
name|shape
parameter_list|)
block|{
return|return
operator|new
name|GeoShapeQueryBuilder
argument_list|(
name|name
argument_list|,
name|shape
argument_list|)
return|;
block|}
DECL|method|geoShapeQuery
specifier|public
specifier|static
name|GeoShapeQueryBuilder
name|geoShapeQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|indexedShapeId
parameter_list|,
name|String
name|indexedShapeType
parameter_list|)
block|{
return|return
operator|new
name|GeoShapeQueryBuilder
argument_list|(
name|name
argument_list|,
name|indexedShapeId
argument_list|,
name|indexedShapeType
argument_list|)
return|;
block|}
comment|/**      * Facilitates creating template query requests using an inline script      */
DECL|method|templateQuery
specifier|public
specifier|static
name|TemplateQueryBuilder
name|templateQuery
parameter_list|(
name|String
name|template
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|)
block|{
return|return
operator|new
name|TemplateQueryBuilder
argument_list|(
name|template
argument_list|,
name|vars
argument_list|)
return|;
block|}
comment|/**      * Facilitates creating template query requests      */
DECL|method|templateQuery
specifier|public
specifier|static
name|TemplateQueryBuilder
name|templateQuery
parameter_list|(
name|String
name|template
parameter_list|,
name|ScriptService
operator|.
name|ScriptType
name|templateType
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|)
block|{
return|return
operator|new
name|TemplateQueryBuilder
argument_list|(
name|template
argument_list|,
name|templateType
argument_list|,
name|vars
argument_list|)
return|;
block|}
DECL|method|QueryBuilders
specifier|private
name|QueryBuilders
parameter_list|()
block|{      }
block|}
end_class

end_unit

