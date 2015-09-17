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
name|Query
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
name|Strings
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|lucene
operator|.
name|search
operator|.
name|Queries
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
name|regex
operator|.
name|Regex
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|MappedFieldType
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
name|SimpleQueryParser
operator|.
name|Settings
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_comment
comment|/**  * SimpleQuery is a query parser that acts similar to a query_string query, but  * won't throw exceptions for any weird string syntax.  *  * For more detailed explanation of the query string syntax see also the<a  * href=  * "https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-simple-query-string-query.html"  *> online documentation</a>.  */
end_comment

begin_class
DECL|class|SimpleQueryStringBuilder
specifier|public
class|class
name|SimpleQueryStringBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|SimpleQueryStringBuilder
argument_list|>
block|{
comment|/** Default locale used for parsing.*/
DECL|field|DEFAULT_LOCALE
specifier|public
specifier|static
specifier|final
name|Locale
name|DEFAULT_LOCALE
init|=
name|Locale
operator|.
name|ROOT
decl_stmt|;
comment|/** Default for lowercasing parsed terms.*/
DECL|field|DEFAULT_LOWERCASE_EXPANDED_TERMS
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_LOWERCASE_EXPANDED_TERMS
init|=
literal|true
decl_stmt|;
comment|/** Default for using lenient query parsing.*/
DECL|field|DEFAULT_LENIENT
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_LENIENT
init|=
literal|false
decl_stmt|;
comment|/** Default for wildcard analysis.*/
DECL|field|DEFAULT_ANALYZE_WILDCARD
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_ANALYZE_WILDCARD
init|=
literal|false
decl_stmt|;
comment|/** Default for default operator to use for linking boolean clauses.*/
DECL|field|DEFAULT_OPERATOR
specifier|public
specifier|static
specifier|final
name|Operator
name|DEFAULT_OPERATOR
init|=
name|Operator
operator|.
name|OR
decl_stmt|;
comment|/** Default for search flags to use. */
DECL|field|DEFAULT_FLAGS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_FLAGS
init|=
name|SimpleQueryStringFlag
operator|.
name|ALL
operator|.
name|value
decl_stmt|;
comment|/** Name for (de-)serialization. */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"simple_query_string"
decl_stmt|;
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|SimpleQueryStringBuilder
name|PROTOTYPE
init|=
operator|new
name|SimpleQueryStringBuilder
argument_list|(
literal|""
argument_list|)
decl_stmt|;
comment|/** Query text to parse. */
DECL|field|queryText
specifier|private
specifier|final
name|String
name|queryText
decl_stmt|;
comment|/**      * Fields to query against. If left empty will query default field,      * currently _ALL. Uses a TreeMap to hold the fields so boolean clauses are      * always sorted in same order for generated Lucene query for easier      * testing.      *      * Can be changed back to HashMap once https://issues.apache.org/jira/browse/LUCENE-6305 is fixed.      */
DECL|field|fieldsAndWeights
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|fieldsAndWeights
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** If specified, analyzer to use to parse the query text, defaults to registered default in toQuery. */
DECL|field|analyzer
specifier|private
name|String
name|analyzer
decl_stmt|;
comment|/** Default operator to use for linking boolean clauses. Defaults to OR according to docs. */
DECL|field|defaultOperator
specifier|private
name|Operator
name|defaultOperator
init|=
name|DEFAULT_OPERATOR
decl_stmt|;
comment|/** If result is a boolean query, minimumShouldMatch parameter to apply. Ignored otherwise. */
DECL|field|minimumShouldMatch
specifier|private
name|String
name|minimumShouldMatch
decl_stmt|;
comment|/** Any search flags to be used, ALL by default. */
DECL|field|flags
specifier|private
name|int
name|flags
init|=
name|DEFAULT_FLAGS
decl_stmt|;
comment|/** Further search settings needed by the ES specific query string parser only. */
DECL|field|settings
specifier|private
name|Settings
name|settings
init|=
operator|new
name|Settings
argument_list|()
decl_stmt|;
comment|/** Construct a new simple query with this query string. */
DECL|method|SimpleQueryStringBuilder
specifier|public
name|SimpleQueryStringBuilder
parameter_list|(
name|String
name|queryText
parameter_list|)
block|{
if|if
condition|(
name|queryText
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"query text missing"
argument_list|)
throw|;
block|}
name|this
operator|.
name|queryText
operator|=
name|queryText
expr_stmt|;
block|}
comment|/** Returns the text to parse the query from. */
DECL|method|value
specifier|public
name|String
name|value
parameter_list|()
block|{
return|return
name|this
operator|.
name|queryText
return|;
block|}
comment|/** Add a field to run the query against. */
DECL|method|field
specifier|public
name|SimpleQueryStringBuilder
name|field
parameter_list|(
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isEmpty
argument_list|(
name|field
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"supplied field is null or empty."
argument_list|)
throw|;
block|}
name|this
operator|.
name|fieldsAndWeights
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Add a field to run the query against with a specific boost. */
DECL|method|field
specifier|public
name|SimpleQueryStringBuilder
name|field
parameter_list|(
name|String
name|field
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isEmpty
argument_list|(
name|field
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"supplied field is null or empty."
argument_list|)
throw|;
block|}
name|this
operator|.
name|fieldsAndWeights
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|boost
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Add several fields to run the query against with a specific boost. */
DECL|method|fields
specifier|public
name|SimpleQueryStringBuilder
name|fields
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fieldsAndWeights
operator|.
name|putAll
argument_list|(
name|fields
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns the fields including their respective boosts to run the query against. */
DECL|method|fields
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|fields
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldsAndWeights
return|;
block|}
comment|/** Specify an analyzer to use for the query. */
DECL|method|analyzer
specifier|public
name|SimpleQueryStringBuilder
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
comment|/** Returns the analyzer to use for the query. */
DECL|method|analyzer
specifier|public
name|String
name|analyzer
parameter_list|()
block|{
return|return
name|this
operator|.
name|analyzer
return|;
block|}
comment|/**      * Specify the default operator for the query. Defaults to "OR" if no      * operator is specified.      */
DECL|method|defaultOperator
specifier|public
name|SimpleQueryStringBuilder
name|defaultOperator
parameter_list|(
name|Operator
name|defaultOperator
parameter_list|)
block|{
name|this
operator|.
name|defaultOperator
operator|=
operator|(
name|defaultOperator
operator|!=
literal|null
operator|)
condition|?
name|defaultOperator
else|:
name|DEFAULT_OPERATOR
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns the default operator for the query. */
DECL|method|defaultOperator
specifier|public
name|Operator
name|defaultOperator
parameter_list|()
block|{
return|return
name|this
operator|.
name|defaultOperator
return|;
block|}
comment|/**      * Specify the enabled features of the SimpleQueryString. Defaults to ALL if      * none are specified.      */
DECL|method|flags
specifier|public
name|SimpleQueryStringBuilder
name|flags
parameter_list|(
name|SimpleQueryStringFlag
modifier|...
name|flags
parameter_list|)
block|{
if|if
condition|(
name|flags
operator|!=
literal|null
operator|&&
name|flags
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|int
name|value
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SimpleQueryStringFlag
name|flag
range|:
name|flags
control|)
block|{
name|value
operator||=
name|flag
operator|.
name|value
expr_stmt|;
block|}
name|this
operator|.
name|flags
operator|=
name|value
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|flags
operator|=
name|DEFAULT_FLAGS
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/** For testing and serialisation only. */
DECL|method|flags
name|SimpleQueryStringBuilder
name|flags
parameter_list|(
name|int
name|flags
parameter_list|)
block|{
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** For testing only: Return the flags set for this query. */
DECL|method|flags
name|int
name|flags
parameter_list|()
block|{
return|return
name|this
operator|.
name|flags
return|;
block|}
comment|/**      * Specifies whether parsed terms for this query should be lower-cased.      * Defaults to true if not set.      */
DECL|method|lowercaseExpandedTerms
specifier|public
name|SimpleQueryStringBuilder
name|lowercaseExpandedTerms
parameter_list|(
name|boolean
name|lowercaseExpandedTerms
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|.
name|lowercaseExpandedTerms
argument_list|(
name|lowercaseExpandedTerms
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns whether parsed terms should be lower cased for this query. */
DECL|method|lowercaseExpandedTerms
specifier|public
name|boolean
name|lowercaseExpandedTerms
parameter_list|()
block|{
return|return
name|this
operator|.
name|settings
operator|.
name|lowercaseExpandedTerms
argument_list|()
return|;
block|}
comment|/** Specifies the locale for parsing terms. Defaults to ROOT if none is set. */
DECL|method|locale
specifier|public
name|SimpleQueryStringBuilder
name|locale
parameter_list|(
name|Locale
name|locale
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|.
name|locale
argument_list|(
name|locale
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns the locale for parsing terms for this query. */
DECL|method|locale
specifier|public
name|Locale
name|locale
parameter_list|()
block|{
return|return
name|this
operator|.
name|settings
operator|.
name|locale
argument_list|()
return|;
block|}
comment|/** Specifies whether query parsing should be lenient. Defaults to false. */
DECL|method|lenient
specifier|public
name|SimpleQueryStringBuilder
name|lenient
parameter_list|(
name|boolean
name|lenient
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|.
name|lenient
argument_list|(
name|lenient
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns whether query parsing should be lenient. */
DECL|method|lenient
specifier|public
name|boolean
name|lenient
parameter_list|()
block|{
return|return
name|this
operator|.
name|settings
operator|.
name|lenient
argument_list|()
return|;
block|}
comment|/** Specifies whether wildcards should be analyzed. Defaults to false. */
DECL|method|analyzeWildcard
specifier|public
name|SimpleQueryStringBuilder
name|analyzeWildcard
parameter_list|(
name|boolean
name|analyzeWildcard
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|.
name|analyzeWildcard
argument_list|(
name|analyzeWildcard
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns whether wildcards should by analyzed. */
DECL|method|analyzeWildcard
specifier|public
name|boolean
name|analyzeWildcard
parameter_list|()
block|{
return|return
name|this
operator|.
name|settings
operator|.
name|analyzeWildcard
argument_list|()
return|;
block|}
comment|/**      * Specifies the minimumShouldMatch to apply to the resulting query should      * that be a Boolean query.      */
DECL|method|minimumShouldMatch
specifier|public
name|SimpleQueryStringBuilder
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
comment|/**      * Returns the minimumShouldMatch to apply to the resulting query should      * that be a Boolean query.      */
DECL|method|minimumShouldMatch
specifier|public
name|String
name|minimumShouldMatch
parameter_list|()
block|{
return|return
name|minimumShouldMatch
return|;
block|}
annotation|@
name|Override
DECL|method|doToQuery
specifier|protected
name|Query
name|doToQuery
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// field names in builder can have wildcards etc, need to resolve them here
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|resolvedFieldsAndWeights
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Use the default field if no fields specified
if|if
condition|(
name|fieldsAndWeights
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|resolvedFieldsAndWeights
operator|.
name|put
argument_list|(
name|resolveIndexName
argument_list|(
name|context
operator|.
name|defaultField
argument_list|()
argument_list|,
name|context
argument_list|)
argument_list|,
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|fieldEntry
range|:
name|fieldsAndWeights
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|Regex
operator|.
name|isSimpleMatchPattern
argument_list|(
name|fieldEntry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|fieldName
range|:
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|simpleMatchToIndexNames
argument_list|(
name|fieldEntry
operator|.
name|getKey
argument_list|()
argument_list|)
control|)
block|{
name|resolvedFieldsAndWeights
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|fieldEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|resolvedFieldsAndWeights
operator|.
name|put
argument_list|(
name|resolveIndexName
argument_list|(
name|fieldEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|context
argument_list|)
argument_list|,
name|fieldEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Use standard analyzer by default if none specified
name|Analyzer
name|luceneAnalyzer
decl_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
name|luceneAnalyzer
operator|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|searchAnalyzer
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|luceneAnalyzer
operator|=
name|context
operator|.
name|analysisService
argument_list|()
operator|.
name|analyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
if|if
condition|(
name|luceneAnalyzer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryShardException
argument_list|(
name|context
argument_list|,
literal|"["
operator|+
name|SimpleQueryStringBuilder
operator|.
name|NAME
operator|+
literal|"] analyzer ["
operator|+
name|analyzer
operator|+
literal|"] not found"
argument_list|)
throw|;
block|}
block|}
name|SimpleQueryParser
name|sqp
init|=
operator|new
name|SimpleQueryParser
argument_list|(
name|luceneAnalyzer
argument_list|,
name|resolvedFieldsAndWeights
argument_list|,
name|flags
argument_list|,
name|settings
argument_list|)
decl_stmt|;
name|sqp
operator|.
name|setDefaultOperator
argument_list|(
name|defaultOperator
operator|.
name|toBooleanClauseOccur
argument_list|()
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|sqp
operator|.
name|parse
argument_list|(
name|queryText
argument_list|)
decl_stmt|;
if|if
condition|(
name|minimumShouldMatch
operator|!=
literal|null
operator|&&
name|query
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|query
operator|=
name|Queries
operator|.
name|applyMinimumShouldMatch
argument_list|(
operator|(
name|BooleanQuery
operator|)
name|query
argument_list|,
name|minimumShouldMatch
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
DECL|method|resolveIndexName
specifier|private
specifier|static
name|String
name|resolveIndexName
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|QueryShardContext
name|context
parameter_list|)
block|{
name|MappedFieldType
name|fieldType
init|=
name|context
operator|.
name|fieldMapper
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|!=
literal|null
condition|)
block|{
return|return
name|fieldType
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
return|;
block|}
return|return
name|fieldName
return|;
block|}
annotation|@
name|Override
DECL|method|setFinalBoost
specifier|protected
name|void
name|setFinalBoost
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|query
operator|.
name|setBoost
argument_list|(
name|boost
operator|*
name|query
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doXContent
specifier|protected
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
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|queryText
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldsAndWeights
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
literal|"fields"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|entry
range|:
name|fieldsAndWeights
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"^"
operator|+
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
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
name|builder
operator|.
name|field
argument_list|(
literal|"flags"
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"default_operator"
argument_list|,
name|defaultOperator
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"lowercase_expanded_terms"
argument_list|,
name|settings
operator|.
name|lowercaseExpandedTerms
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"lenient"
argument_list|,
name|settings
operator|.
name|lenient
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"analyze_wildcard"
argument_list|,
name|settings
operator|.
name|analyzeWildcard
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"locale"
argument_list|,
operator|(
name|settings
operator|.
name|locale
argument_list|()
operator|.
name|toLanguageTag
argument_list|()
operator|)
argument_list|)
expr_stmt|;
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
name|printBoostAndQueryName
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|doReadFrom
specifier|protected
name|SimpleQueryStringBuilder
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|SimpleQueryStringBuilder
name|result
init|=
operator|new
name|SimpleQueryStringBuilder
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|field
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|Float
name|weight
init|=
name|in
operator|.
name|readFloat
argument_list|()
decl_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|weight
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|fieldsAndWeights
operator|.
name|putAll
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|result
operator|.
name|flags
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|result
operator|.
name|analyzer
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|result
operator|.
name|defaultOperator
operator|=
name|Operator
operator|.
name|readOperatorFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|result
operator|.
name|settings
operator|.
name|lowercaseExpandedTerms
argument_list|(
name|in
operator|.
name|readBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|settings
operator|.
name|lenient
argument_list|(
name|in
operator|.
name|readBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|settings
operator|.
name|analyzeWildcard
argument_list|(
name|in
operator|.
name|readBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|localeStr
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|result
operator|.
name|settings
operator|.
name|locale
argument_list|(
name|Locale
operator|.
name|forLanguageTag
argument_list|(
name|localeStr
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|minimumShouldMatch
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|protected
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeString
argument_list|(
name|queryText
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|fieldsAndWeights
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|entry
range|:
name|fieldsAndWeights
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeInt
argument_list|(
name|flags
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|defaultOperator
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|settings
operator|.
name|lowercaseExpandedTerms
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|settings
operator|.
name|lenient
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|settings
operator|.
name|analyzeWildcard
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|settings
operator|.
name|locale
argument_list|()
operator|.
name|toLanguageTag
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|minimumShouldMatch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doHashCode
specifier|protected
name|int
name|doHashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|fieldsAndWeights
argument_list|,
name|analyzer
argument_list|,
name|defaultOperator
argument_list|,
name|queryText
argument_list|,
name|minimumShouldMatch
argument_list|,
name|settings
argument_list|,
name|flags
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|SimpleQueryStringBuilder
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|fieldsAndWeights
argument_list|,
name|other
operator|.
name|fieldsAndWeights
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|analyzer
argument_list|,
name|other
operator|.
name|analyzer
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|defaultOperator
argument_list|,
name|other
operator|.
name|defaultOperator
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|queryText
argument_list|,
name|other
operator|.
name|queryText
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|minimumShouldMatch
argument_list|,
name|other
operator|.
name|minimumShouldMatch
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|settings
argument_list|,
name|other
operator|.
name|settings
argument_list|)
operator|&&
operator|(
name|flags
operator|==
name|other
operator|.
name|flags
operator|)
return|;
block|}
block|}
end_class

end_unit

