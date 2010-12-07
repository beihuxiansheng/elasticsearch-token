begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|xcontent
package|;
end_package

begin_comment
comment|/**  * A static factory for simple "import static" usage.  *  * @author kimchy (shay.banon)  */
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
comment|/**      * A query that executes the query string against a field. It is a simplified      * version of {@link QueryStringQueryBuilder} that simply runs against      * a single field.      *      * @param name The name of the field      */
DECL|method|fieldQuery
specifier|public
specifier|static
name|FieldQueryBuilder
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
name|FieldQueryBuilder
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|/**      * A query that executes the query string against a field. It is a simplified      * version of {@link QueryStringQueryBuilder} that simply runs against      * a single field.      *      * @param name  The name of the field      * @param query The query string      */
DECL|method|fieldQuery
specifier|public
specifier|static
name|FieldQueryBuilder
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
name|FieldQueryBuilder
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|/**      * A query that executes the query string against a field. It is a simplified      * version of {@link QueryStringQueryBuilder} that simply runs against      * a single field.      *      * @param name  The name of the field      * @param query The query string      */
DECL|method|fieldQuery
specifier|public
specifier|static
name|FieldQueryBuilder
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
name|FieldQueryBuilder
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|/**      * A query that executes the query string against a field. It is a simplified      * version of {@link QueryStringQueryBuilder} that simply runs against      * a single field.      *      * @param name  The name of the field      * @param query The query string      */
DECL|method|fieldQuery
specifier|public
specifier|static
name|FieldQueryBuilder
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
name|FieldQueryBuilder
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|/**      * A query that executes the query string against a field. It is a simplified      * version of {@link QueryStringQueryBuilder} that simply runs against      * a single field.      *      * @param name  The name of the field      * @param query The query string      */
DECL|method|fieldQuery
specifier|public
specifier|static
name|FieldQueryBuilder
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
name|FieldQueryBuilder
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|/**      * A query that executes the query string against a field. It is a simplified      * version of {@link QueryStringQueryBuilder} that simply runs against      * a single field.      *      * @param name  The name of the field      * @param query The query string      */
DECL|method|fieldQuery
specifier|public
specifier|static
name|FieldQueryBuilder
name|fieldQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|query
parameter_list|)
block|{
return|return
operator|new
name|FieldQueryBuilder
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
return|;
block|}
comment|/**      * A query that executes the query string against a field. It is a simplified      * version of {@link QueryStringQueryBuilder} that simply runs against      * a single field.      *      * @param name  The name of the field      * @param query The query string      */
DECL|method|fieldQuery
specifier|public
specifier|static
name|FieldQueryBuilder
name|fieldQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|query
parameter_list|)
block|{
return|return
operator|new
name|FieldQueryBuilder
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
comment|/**      * A query that parses a query string and runs it. There are two modes that this operates. The first,      * when no field is added (using {@link QueryStringQueryBuilder#field(String)}, will run the query once and non prefixed fields      * will use the {@link QueryStringQueryBuilder#defaultField(String)} set. The second, when one or more fields are added      * (using {@link QueryStringQueryBuilder#field(String)}), will run the parsed query against the provided fields, and combine      * them either using DisMax or a plain boolean query (see {@link QueryStringQueryBuilder#useDisMax(boolean)}).      *      * @param queryString The query string to run      */
DECL|method|queryString
specifier|public
specifier|static
name|QueryStringQueryBuilder
name|queryString
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
name|XContentSpanQueryBuilder
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
DECL|method|fieldMaskingSpanQuery
specifier|public
specifier|static
name|FieldMaskingSpanQueryBuilder
name|fieldMaskingSpanQuery
parameter_list|(
name|XContentSpanQueryBuilder
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
comment|/**      * A query that applies a filter to the results of another query.      *      * @param queryBuilder  The query to apply the filter to      * @param filterBuilder The filter to apply on the query      * @deprecated Use filteredQuery instead (rename)      */
DECL|method|filtered
specifier|public
specifier|static
name|FilteredQueryBuilder
name|filtered
parameter_list|(
name|XContentQueryBuilder
name|queryBuilder
parameter_list|,
name|XContentFilterBuilder
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
comment|/**      * A query that applies a filter to the results of another query.      *      * @param queryBuilder  The query to apply the filter to      * @param filterBuilder The filter to apply on the query      */
DECL|method|filteredQuery
specifier|public
specifier|static
name|FilteredQueryBuilder
name|filteredQuery
parameter_list|(
name|XContentQueryBuilder
name|queryBuilder
parameter_list|,
name|XContentFilterBuilder
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
name|XContentFilterBuilder
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
comment|/**      * A query that simply applies the boost fact to the wrapped query (multiplies it).      *      * @param queryBuilder The query to apply the boost factor to.      */
DECL|method|customBoostFactorQuery
specifier|public
specifier|static
name|CustomBoostFactorQueryBuilder
name|customBoostFactorQuery
parameter_list|(
name|XContentQueryBuilder
name|queryBuilder
parameter_list|)
block|{
return|return
operator|new
name|CustomBoostFactorQueryBuilder
argument_list|(
name|queryBuilder
argument_list|)
return|;
block|}
comment|/**      * A query that allows to define a custom scoring script.      *      * @param queryBuilder The query to custom score      */
DECL|method|customScoreQuery
specifier|public
specifier|static
name|CustomScoreQueryBuilder
name|customScoreQuery
parameter_list|(
name|XContentQueryBuilder
name|queryBuilder
parameter_list|)
block|{
return|return
operator|new
name|CustomScoreQueryBuilder
argument_list|(
name|queryBuilder
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
comment|/**      * A fuzzy like this query that finds documents that are "like" the provided {@link FuzzyLikeThisQueryBuilder#likeText(String)}      * which is checked against the fields the query is constructed with.      *      * @param fields The fields to run the query against      */
DECL|method|fuzzyLikeThisQuery
specifier|public
specifier|static
name|FuzzyLikeThisQueryBuilder
name|fuzzyLikeThisQuery
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
return|return
operator|new
name|FuzzyLikeThisQueryBuilder
argument_list|(
name|fields
argument_list|)
return|;
block|}
comment|/**      * A fuzzy like this query that finds documents that are "like" the provided {@link FuzzyLikeThisQueryBuilder#likeText(String)}      * which is checked against the "_all" field.      */
DECL|method|fuzzyLikeThisQuery
specifier|public
specifier|static
name|FuzzyLikeThisQueryBuilder
name|fuzzyLikeThisQuery
parameter_list|()
block|{
return|return
operator|new
name|FuzzyLikeThisQueryBuilder
argument_list|()
return|;
block|}
comment|/**      * A fuzzy like this query that finds documents that are "like" the provided {@link FuzzyLikeThisFieldQueryBuilder#likeText(String)}.      */
DECL|method|fuzzyLikeThisFieldQuery
specifier|public
specifier|static
name|FuzzyLikeThisFieldQueryBuilder
name|fuzzyLikeThisFieldQuery
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|FuzzyLikeThisFieldQueryBuilder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * A more like this query that runs against a specific field.      *      * @param name The field name      */
DECL|method|moreLikeThisFieldQuery
specifier|public
specifier|static
name|MoreLikeThisFieldQueryBuilder
name|moreLikeThisFieldQuery
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|MoreLikeThisFieldQueryBuilder
argument_list|(
name|name
argument_list|)
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
name|XContentQueryBuilder
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
name|XContentQueryBuilder
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
DECL|method|QueryBuilders
specifier|private
name|QueryBuilders
parameter_list|()
block|{      }
block|}
end_class

end_unit

