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

begin_comment
comment|/**  * A static factory for simple "import static" usage.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|JsonQueryBuilders
specifier|public
specifier|abstract
class|class
name|JsonQueryBuilders
block|{
comment|/**      * A query that match on all documents.      */
DECL|method|matchAllQuery
specifier|public
specifier|static
name|MatchAllJsonQueryBuilder
name|matchAllQuery
parameter_list|()
block|{
return|return
operator|new
name|MatchAllJsonQueryBuilder
argument_list|()
return|;
block|}
comment|/**      * A query that generates the union of documents produced by its sub-queries, and that scores each document      * with the maximum score for that document as produced by any sub-query, plus a tie breaking increment for any      * additional matching sub-queries.      */
DECL|method|disMaxQuery
specifier|public
specifier|static
name|DisMaxJsonQueryBuilder
name|disMaxQuery
parameter_list|()
block|{
return|return
operator|new
name|DisMaxJsonQueryBuilder
argument_list|()
return|;
block|}
comment|/**      * A Query that matches documents containing a term.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|termQuery
specifier|public
specifier|static
name|TermJsonQueryBuilder
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
name|TermJsonQueryBuilder
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
name|TermJsonQueryBuilder
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
name|TermJsonQueryBuilder
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
name|TermJsonQueryBuilder
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
name|TermJsonQueryBuilder
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
name|TermJsonQueryBuilder
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
name|TermJsonQueryBuilder
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
name|TermJsonQueryBuilder
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
name|TermJsonQueryBuilder
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * A query that executes the query string against a field. It is a simplified      * version of {@link QueryStringJsonQueryBuilder} that simply runs against      * a single field.      *      * @param name The name of the field      */
DECL|method|fieldQuery
specifier|public
specifier|static
name|FieldJsonQueryBuilder
name|fieldQuery
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
name|FieldJsonQueryBuilder
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|/**      * A query that executes the query string against a field. It is a simplified      * version of {@link QueryStringJsonQueryBuilder} that simply runs against      * a single field.      *      * @param name  The name of the field      * @param query The query string      */
DECL|method|fieldQuery
specifier|public
specifier|static
name|FieldJsonQueryBuilder
name|fieldQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|query
parameter_list|)
block|{
return|return
operator|new
name|FieldJsonQueryBuilder
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|/**      * A query that executes the query string against a field. It is a simplified      * version of {@link QueryStringJsonQueryBuilder} that simply runs against      * a single field.      *      * @param name  The name of the field      * @param query The query string      */
DECL|method|fieldQuery
specifier|public
specifier|static
name|FieldJsonQueryBuilder
name|fieldQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|query
parameter_list|)
block|{
return|return
operator|new
name|FieldJsonQueryBuilder
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|/**      * A query that executes the query string against a field. It is a simplified      * version of {@link QueryStringJsonQueryBuilder} that simply runs against      * a single field.      *      * @param name  The name of the field      * @param query The query string      */
DECL|method|fieldQuery
specifier|public
specifier|static
name|FieldJsonQueryBuilder
name|fieldQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|query
parameter_list|)
block|{
return|return
operator|new
name|FieldJsonQueryBuilder
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|/**      * A query that executes the query string against a field. It is a simplified      * version of {@link QueryStringJsonQueryBuilder} that simply runs against      * a single field.      *      * @param name  The name of the field      * @param query The query string      */
DECL|method|fieldQuery
specifier|public
specifier|static
name|FieldJsonQueryBuilder
name|fieldQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|query
parameter_list|)
block|{
return|return
operator|new
name|FieldJsonQueryBuilder
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|/**      * A Query that matches documents containing terms with a specified prefix.      *      * @param name   The name of the field      * @param prefix The prefix query      */
DECL|method|prefixQuery
specifier|public
specifier|static
name|PrefixJsonQueryBuilder
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
name|PrefixJsonQueryBuilder
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
name|RangeJsonQueryBuilder
name|rangeQuery
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|RangeJsonQueryBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Implements the wildcard search query. Supported wildcards are<tt>*</tt>, which      * matches any character sequence (including the empty one), and<tt>?</tt>,      * which matches any single character. Note this query can be slow, as it      * needs to iterate over many terms. In order to prevent extremely slow WildcardQueries,      * a Wildcard term should not start with one of the wildcards<tt>*</tt> or      *<tt>?</tt>.      *      * @param name  The field name      * @param query The wildcard query string      */
DECL|method|wildcardQuery
specifier|public
specifier|static
name|WildcardJsonQueryBuilder
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
name|WildcardJsonQueryBuilder
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|/**      * A query that parses a query string and runs it. There are two modes that this operates. The first,      * when no field is added (using {@link QueryStringJsonQueryBuilder#field(String)}, will run the query once and non prefixed fields      * will use the {@link QueryStringJsonQueryBuilder#defaultField(String)} set. The second, when one or more fields are added      * (using {@link QueryStringJsonQueryBuilder#field(String)}), will run the parsed query against the provided fields, and combine      * them either using DisMax or a plain boolean query (see {@link QueryStringJsonQueryBuilder#useDisMax(boolean)}).      *      * @param queryString The query string to run      */
DECL|method|queryString
specifier|public
specifier|static
name|QueryStringJsonQueryBuilder
name|queryString
parameter_list|(
name|String
name|queryString
parameter_list|)
block|{
return|return
operator|new
name|QueryStringJsonQueryBuilder
argument_list|(
name|queryString
argument_list|)
return|;
block|}
comment|/**      * A Query that matches documents matching boolean combinations of other queries.      */
DECL|method|boolQuery
specifier|public
specifier|static
name|BoolJsonQueryBuilder
name|boolQuery
parameter_list|()
block|{
return|return
operator|new
name|BoolJsonQueryBuilder
argument_list|()
return|;
block|}
DECL|method|spanTermQuery
specifier|public
specifier|static
name|SpanTermJsonQueryBuilder
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
name|SpanTermJsonQueryBuilder
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
name|SpanTermJsonQueryBuilder
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
name|SpanTermJsonQueryBuilder
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
name|SpanTermJsonQueryBuilder
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
name|SpanTermJsonQueryBuilder
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
name|SpanTermJsonQueryBuilder
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
name|SpanTermJsonQueryBuilder
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
name|SpanTermJsonQueryBuilder
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
name|SpanTermJsonQueryBuilder
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
name|SpanFirstJsonQueryBuilder
name|spanFirstQuery
parameter_list|(
name|JsonSpanQueryBuilder
name|match
parameter_list|,
name|int
name|end
parameter_list|)
block|{
return|return
operator|new
name|SpanFirstJsonQueryBuilder
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
name|SpanNearJsonQueryBuilder
name|spanNearQuery
parameter_list|()
block|{
return|return
operator|new
name|SpanNearJsonQueryBuilder
argument_list|()
return|;
block|}
DECL|method|spanNotQuery
specifier|public
specifier|static
name|SpanNotJsonQueryBuilder
name|spanNotQuery
parameter_list|()
block|{
return|return
operator|new
name|SpanNotJsonQueryBuilder
argument_list|()
return|;
block|}
DECL|method|spanOrQuery
specifier|public
specifier|static
name|SpanOrJsonQueryBuilder
name|spanOrQuery
parameter_list|()
block|{
return|return
operator|new
name|SpanOrJsonQueryBuilder
argument_list|()
return|;
block|}
comment|/**      * A query that applies a filter to the results of another query.      *      * @param queryBuilder  The query to apply the filter to      * @param filterBuilder The filter to apply on the query      */
DECL|method|filtered
specifier|public
specifier|static
name|FilteredJsonQueryBuilder
name|filtered
parameter_list|(
name|JsonQueryBuilder
name|queryBuilder
parameter_list|,
name|JsonFilterBuilder
name|filterBuilder
parameter_list|)
block|{
return|return
operator|new
name|FilteredJsonQueryBuilder
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
name|ConstantScoreQueryJsonQueryBuilder
name|constantScoreQuery
parameter_list|(
name|JsonFilterBuilder
name|filterBuilder
parameter_list|)
block|{
return|return
operator|new
name|ConstantScoreQueryJsonQueryBuilder
argument_list|(
name|filterBuilder
argument_list|)
return|;
block|}
comment|/**      * A more like this query that finds documents that are "like" the provided {@link MoreLikeThisJsonQueryBuilder#likeText(String)}      * which is checked against the fields the query is constructed with.      *      * @param fields The fields to run the query against      */
DECL|method|moreLikeThisQuery
specifier|public
specifier|static
name|MoreLikeThisJsonQueryBuilder
name|moreLikeThisQuery
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
return|return
operator|new
name|MoreLikeThisJsonQueryBuilder
argument_list|(
name|fields
argument_list|)
return|;
block|}
comment|/**      * A more like this query that finds documents that are "like" the provided {@link MoreLikeThisJsonQueryBuilder#likeText(String)}      * which is checked against the "_all" field.      */
DECL|method|moreLikeThisQuery
specifier|public
specifier|static
name|MoreLikeThisJsonQueryBuilder
name|moreLikeThisQuery
parameter_list|()
block|{
return|return
operator|new
name|MoreLikeThisJsonQueryBuilder
argument_list|()
return|;
block|}
comment|/**      * A fuzzy like this query that finds documents that are "like" the provided {@link FuzzyLikeThisJsonQueryBuilder#likeText(String)}      * which is checked against the fields the query is constructed with.      *      * @param fields The fields to run the query against      */
DECL|method|fuzzyLikeThisQuery
specifier|public
specifier|static
name|FuzzyLikeThisJsonQueryBuilder
name|fuzzyLikeThisQuery
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
return|return
operator|new
name|FuzzyLikeThisJsonQueryBuilder
argument_list|(
name|fields
argument_list|)
return|;
block|}
comment|/**      * A fuzzy like this query that finds documents that are "like" the provided {@link FuzzyLikeThisJsonQueryBuilder#likeText(String)}      * which is checked against the "_all" field.      */
DECL|method|fuzzyLikeThisQuery
specifier|public
specifier|static
name|FuzzyLikeThisJsonQueryBuilder
name|fuzzyLikeThisQuery
parameter_list|()
block|{
return|return
operator|new
name|FuzzyLikeThisJsonQueryBuilder
argument_list|()
return|;
block|}
comment|/**      * A fuzzy like this query that finds documents that are "like" the provided {@link FuzzyLikeThisFieldJsonQueryBuilder#likeText(String)}.      */
DECL|method|fuzzyLikeThisFieldQuery
specifier|public
specifier|static
name|FuzzyLikeThisFieldJsonQueryBuilder
name|fuzzyLikeThisFieldQuery
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|FuzzyLikeThisFieldJsonQueryBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * A more like this query that runs against a specific field.      *      * @param name The field name      */
DECL|method|moreLikeThisFieldQuery
specifier|public
specifier|static
name|MoreLikeThisFieldJsonQueryBuilder
name|moreLikeThisFieldQuery
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|MoreLikeThisFieldJsonQueryBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|JsonQueryBuilders
specifier|private
name|JsonQueryBuilders
parameter_list|()
block|{      }
block|}
end_class

end_unit

