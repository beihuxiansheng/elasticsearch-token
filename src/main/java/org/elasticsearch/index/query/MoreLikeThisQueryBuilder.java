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
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|get
operator|.
name|MultiGetRequest
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
name|bytes
operator|.
name|BytesReference
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
name|uid
operator|.
name|Versions
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
name|*
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
name|VersionType
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
name|fetch
operator|.
name|source
operator|.
name|FetchSourceContext
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Locale
import|;
end_import

begin_comment
comment|/**  * A more like this query that finds documents that are "like" the provided {@link #likeText(String)}  * which is checked against the fields the query is constructed with.  */
end_comment

begin_class
DECL|class|MoreLikeThisQueryBuilder
specifier|public
class|class
name|MoreLikeThisQueryBuilder
extends|extends
name|BaseQueryBuilder
implements|implements
name|BoostableQueryBuilder
argument_list|<
name|MoreLikeThisQueryBuilder
argument_list|>
block|{
comment|/**      * A single get item. Pure delegate to multi get.      */
DECL|class|Item
specifier|public
specifier|static
specifier|final
class|class
name|Item
extends|extends
name|MultiGetRequest
operator|.
name|Item
implements|implements
name|ToXContent
block|{
DECL|field|doc
specifier|private
name|BytesReference
name|doc
decl_stmt|;
DECL|field|likeText
specifier|private
name|String
name|likeText
decl_stmt|;
DECL|method|Item
specifier|public
name|Item
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|Item
specifier|public
name|Item
parameter_list|(
name|String
name|index
parameter_list|,
annotation|@
name|Nullable
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
DECL|method|Item
specifier|public
name|Item
parameter_list|(
name|String
name|likeText
parameter_list|)
block|{
name|this
operator|.
name|likeText
operator|=
name|likeText
expr_stmt|;
block|}
DECL|method|doc
specifier|public
name|BytesReference
name|doc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
DECL|method|doc
specifier|public
name|Item
name|doc
parameter_list|(
name|XContentBuilder
name|doc
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
name|doc
operator|.
name|bytes
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
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
if|if
condition|(
name|this
operator|.
name|likeText
operator|!=
literal|null
condition|)
block|{
return|return
name|builder
operator|.
name|value
argument_list|(
name|this
operator|.
name|likeText
argument_list|)
return|;
block|}
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|index
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_index"
argument_list|,
name|this
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|type
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_type"
argument_list|,
name|this
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|id
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_id"
argument_list|,
name|this
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|doc
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|XContentType
name|contentType
init|=
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentType
operator|==
name|builder
operator|.
name|contentType
argument_list|()
condition|)
block|{
name|builder
operator|.
name|rawField
argument_list|(
literal|"doc"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|contentType
argument_list|)
operator|.
name|createParser
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"doc"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|copyCurrentStructure
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|this
operator|.
name|fields
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|array
argument_list|(
literal|"fields"
argument_list|,
name|this
operator|.
name|fields
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|routing
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_routing"
argument_list|,
name|this
operator|.
name|routing
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|fetchSourceContext
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|FetchSourceContext
name|source
init|=
name|this
operator|.
name|fetchSourceContext
argument_list|()
decl_stmt|;
name|String
index|[]
name|includes
init|=
name|source
operator|.
name|includes
argument_list|()
decl_stmt|;
name|String
index|[]
name|excludes
init|=
name|source
operator|.
name|excludes
argument_list|()
decl_stmt|;
if|if
condition|(
name|includes
operator|.
name|length
operator|==
literal|0
operator|&&
name|excludes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_source"
argument_list|,
name|source
operator|.
name|fetchSource
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|includes
operator|.
name|length
operator|>
literal|0
operator|&&
name|excludes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|array
argument_list|(
literal|"_source"
argument_list|,
name|source
operator|.
name|includes
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|excludes
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"_source"
argument_list|)
expr_stmt|;
if|if
condition|(
name|includes
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|array
argument_list|(
literal|"includes"
argument_list|,
name|source
operator|.
name|includes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|array
argument_list|(
literal|"excludes"
argument_list|,
name|source
operator|.
name|excludes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|this
operator|.
name|version
argument_list|()
operator|!=
name|Versions
operator|.
name|MATCH_ANY
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_version"
argument_list|,
name|this
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|versionType
argument_list|()
operator|!=
name|VersionType
operator|.
name|INTERNAL
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_version_type"
argument_list|,
name|this
operator|.
name|versionType
argument_list|()
operator|.
name|toString
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
block|}
return|return
name|builder
operator|.
name|endObject
argument_list|()
return|;
block|}
block|}
DECL|field|fields
specifier|private
specifier|final
name|String
index|[]
name|fields
decl_stmt|;
DECL|field|docs
specifier|private
name|List
argument_list|<
name|Item
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|include
specifier|private
name|Boolean
name|include
init|=
literal|null
decl_stmt|;
DECL|field|minimumShouldMatch
specifier|private
name|String
name|minimumShouldMatch
init|=
literal|null
decl_stmt|;
DECL|field|minTermFreq
specifier|private
name|int
name|minTermFreq
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|maxQueryTerms
specifier|private
name|int
name|maxQueryTerms
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|stopWords
specifier|private
name|String
index|[]
name|stopWords
init|=
literal|null
decl_stmt|;
DECL|field|minDocFreq
specifier|private
name|int
name|minDocFreq
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|maxDocFreq
specifier|private
name|int
name|maxDocFreq
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|minWordLength
specifier|private
name|int
name|minWordLength
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|maxWordLength
specifier|private
name|int
name|maxWordLength
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|boostTerms
specifier|private
name|float
name|boostTerms
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|boost
specifier|private
name|float
name|boost
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|analyzer
specifier|private
name|String
name|analyzer
decl_stmt|;
DECL|field|failOnUnsupportedField
specifier|private
name|Boolean
name|failOnUnsupportedField
decl_stmt|;
DECL|field|queryName
specifier|private
name|String
name|queryName
decl_stmt|;
comment|/**      * Constructs a new more like this query which uses the "_all" field.      */
DECL|method|MoreLikeThisQueryBuilder
specifier|public
name|MoreLikeThisQueryBuilder
parameter_list|()
block|{
name|this
operator|.
name|fields
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Sets the field names that will be used when generating the 'More Like This' query.      *      * @param fields the field names that will be used when generating the 'More Like This' query.      */
DECL|method|MoreLikeThisQueryBuilder
specifier|public
name|MoreLikeThisQueryBuilder
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
block|}
DECL|method|like
specifier|public
name|MoreLikeThisQueryBuilder
name|like
parameter_list|(
name|Item
modifier|...
name|docs
parameter_list|)
block|{
name|this
operator|.
name|docs
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|docs
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|like
specifier|public
name|MoreLikeThisQueryBuilder
name|like
parameter_list|(
name|String
modifier|...
name|likeText
parameter_list|)
block|{
name|this
operator|.
name|docs
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|text
range|:
name|likeText
control|)
block|{
name|this
operator|.
name|docs
operator|.
name|add
argument_list|(
operator|new
name|Item
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|addItem
specifier|public
name|MoreLikeThisQueryBuilder
name|addItem
parameter_list|(
name|Item
name|item
parameter_list|)
block|{
name|this
operator|.
name|docs
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addLikeText
specifier|public
name|MoreLikeThisQueryBuilder
name|addLikeText
parameter_list|(
name|String
name|likeText
parameter_list|)
block|{
name|this
operator|.
name|docs
operator|.
name|add
argument_list|(
operator|new
name|Item
argument_list|(
name|likeText
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The text to use in order to find documents that are "like" this.      */
annotation|@
name|Deprecated
DECL|method|likeText
specifier|public
name|MoreLikeThisQueryBuilder
name|likeText
parameter_list|(
name|String
name|likeText
parameter_list|)
block|{
return|return
name|like
argument_list|(
name|likeText
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
DECL|method|ids
specifier|public
name|MoreLikeThisQueryBuilder
name|ids
parameter_list|(
name|String
modifier|...
name|ids
parameter_list|)
block|{
name|Item
index|[]
name|items
init|=
operator|new
name|Item
index|[
name|ids
operator|.
name|length
index|]
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
name|items
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|items
index|[
name|i
index|]
operator|=
operator|new
name|Item
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|ids
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|like
argument_list|(
name|items
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
DECL|method|docs
specifier|public
name|MoreLikeThisQueryBuilder
name|docs
parameter_list|(
name|Item
modifier|...
name|docs
parameter_list|)
block|{
return|return
name|like
argument_list|(
name|docs
argument_list|)
return|;
block|}
DECL|method|include
specifier|public
name|MoreLikeThisQueryBuilder
name|include
parameter_list|(
name|boolean
name|include
parameter_list|)
block|{
name|this
operator|.
name|include
operator|=
name|include
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Number of terms that must match the generated query expressed in the      * common syntax for minimum should match. Defaults to<tt>30%</tt>.      *      * @see    org.elasticsearch.common.lucene.search.Queries#calculateMinShouldMatch(int, String)      */
DECL|method|minimumShouldMatch
specifier|public
name|MoreLikeThisQueryBuilder
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
comment|/**      * The percentage of terms to match. Defaults to<tt>0.3</tt>.      */
annotation|@
name|Deprecated
DECL|method|percentTermsToMatch
specifier|public
name|MoreLikeThisQueryBuilder
name|percentTermsToMatch
parameter_list|(
name|float
name|percentTermsToMatch
parameter_list|)
block|{
return|return
name|minimumShouldMatch
argument_list|(
name|Math
operator|.
name|round
argument_list|(
name|percentTermsToMatch
operator|*
literal|100
argument_list|)
operator|+
literal|"%"
argument_list|)
return|;
block|}
comment|/**      * The frequency below which terms will be ignored in the source doc. The default      * frequency is<tt>2</tt>.      */
DECL|method|minTermFreq
specifier|public
name|MoreLikeThisQueryBuilder
name|minTermFreq
parameter_list|(
name|int
name|minTermFreq
parameter_list|)
block|{
name|this
operator|.
name|minTermFreq
operator|=
name|minTermFreq
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the maximum number of query terms that will be included in any generated query.      * Defaults to<tt>25</tt>.      */
DECL|method|maxQueryTerms
specifier|public
name|MoreLikeThisQueryBuilder
name|maxQueryTerms
parameter_list|(
name|int
name|maxQueryTerms
parameter_list|)
block|{
name|this
operator|.
name|maxQueryTerms
operator|=
name|maxQueryTerms
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the set of stopwords.      *<p/>      *<p>Any word in this set is considered "uninteresting" and ignored. Even if your Analyzer allows stopwords, you      * might want to tell the MoreLikeThis code to ignore them, as for the purposes of document similarity it seems      * reasonable to assume that "a stop word is never interesting".      */
DECL|method|stopWords
specifier|public
name|MoreLikeThisQueryBuilder
name|stopWords
parameter_list|(
name|String
modifier|...
name|stopWords
parameter_list|)
block|{
name|this
operator|.
name|stopWords
operator|=
name|stopWords
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the frequency at which words will be ignored which do not occur in at least this      * many docs. Defaults to<tt>5</tt>.      */
DECL|method|minDocFreq
specifier|public
name|MoreLikeThisQueryBuilder
name|minDocFreq
parameter_list|(
name|int
name|minDocFreq
parameter_list|)
block|{
name|this
operator|.
name|minDocFreq
operator|=
name|minDocFreq
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the maximum frequency in which words may still appear. Words that appear      * in more than this many docs will be ignored. Defaults to unbounded.      */
DECL|method|maxDocFreq
specifier|public
name|MoreLikeThisQueryBuilder
name|maxDocFreq
parameter_list|(
name|int
name|maxDocFreq
parameter_list|)
block|{
name|this
operator|.
name|maxDocFreq
operator|=
name|maxDocFreq
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the minimum word length below which words will be ignored. Defaults      * to<tt>0</tt>.      */
DECL|method|minWordLength
specifier|public
name|MoreLikeThisQueryBuilder
name|minWordLength
parameter_list|(
name|int
name|minWordLength
parameter_list|)
block|{
name|this
operator|.
name|minWordLength
operator|=
name|minWordLength
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the maximum word length above which words will be ignored. Defaults to      * unbounded (<tt>0</tt>).      */
DECL|method|maxWordLength
specifier|public
name|MoreLikeThisQueryBuilder
name|maxWordLength
parameter_list|(
name|int
name|maxWordLength
parameter_list|)
block|{
name|this
operator|.
name|maxWordLength
operator|=
name|maxWordLength
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the boost factor to use when boosting terms. Defaults to<tt>1</tt>.      */
DECL|method|boostTerms
specifier|public
name|MoreLikeThisQueryBuilder
name|boostTerms
parameter_list|(
name|float
name|boostTerms
parameter_list|)
block|{
name|this
operator|.
name|boostTerms
operator|=
name|boostTerms
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The analyzer that will be used to analyze the text. Defaults to the analyzer associated with the fied.      */
DECL|method|analyzer
specifier|public
name|MoreLikeThisQueryBuilder
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
DECL|method|boost
specifier|public
name|MoreLikeThisQueryBuilder
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
comment|/**      * Whether to fail or return no result when this query is run against a field which is not supported such as binary/numeric fields.      */
DECL|method|failOnUnsupportedField
specifier|public
name|MoreLikeThisQueryBuilder
name|failOnUnsupportedField
parameter_list|(
name|boolean
name|fail
parameter_list|)
block|{
name|failOnUnsupportedField
operator|=
name|fail
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the query name for the filter that can be used when searching for matched_filters per hit.      */
DECL|method|queryName
specifier|public
name|MoreLikeThisQueryBuilder
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
name|String
name|likeFieldName
init|=
name|MoreLikeThisQueryParser
operator|.
name|Fields
operator|.
name|LIKE
operator|.
name|getPreferredName
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|MoreLikeThisQueryParser
operator|.
name|NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
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
name|String
name|field
range|:
name|fields
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|field
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
name|this
operator|.
name|docs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"more_like_this requires '"
operator|+
name|likeFieldName
operator|+
literal|"' to be provided"
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
name|docs
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|likeFieldName
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|array
argument_list|(
name|likeFieldName
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
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
name|MoreLikeThisQueryParser
operator|.
name|Fields
operator|.
name|MINIMUM_SHOULD_MATCH
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|minimumShouldMatch
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minTermFreq
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|MoreLikeThisQueryParser
operator|.
name|Fields
operator|.
name|MIN_TERM_FREQ
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|minTermFreq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxQueryTerms
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|MoreLikeThisQueryParser
operator|.
name|Fields
operator|.
name|MAX_QUERY_TERMS
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|maxQueryTerms
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|stopWords
operator|!=
literal|null
operator|&&
name|stopWords
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|MoreLikeThisQueryParser
operator|.
name|Fields
operator|.
name|STOP_WORDS
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|stopWord
range|:
name|stopWords
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|stopWord
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
name|minDocFreq
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|MoreLikeThisQueryParser
operator|.
name|Fields
operator|.
name|MIN_DOC_FREQ
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|minDocFreq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxDocFreq
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|MoreLikeThisQueryParser
operator|.
name|Fields
operator|.
name|MAX_DOC_FREQ
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|maxDocFreq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minWordLength
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|MoreLikeThisQueryParser
operator|.
name|Fields
operator|.
name|MIN_WORD_LENGTH
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|minWordLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxWordLength
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|MoreLikeThisQueryParser
operator|.
name|Fields
operator|.
name|MAX_WORD_LENGTH
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|maxWordLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|boostTerms
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|MoreLikeThisQueryParser
operator|.
name|Fields
operator|.
name|BOOST_TERMS
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|boostTerms
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|boost
operator|!=
operator|-
literal|1
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
name|failOnUnsupportedField
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|MoreLikeThisQueryParser
operator|.
name|Fields
operator|.
name|FAIL_ON_UNSUPPORTED_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|failOnUnsupportedField
argument_list|)
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
if|if
condition|(
name|include
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"include"
argument_list|,
name|include
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

