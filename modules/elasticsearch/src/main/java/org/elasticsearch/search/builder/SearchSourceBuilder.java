begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.builder
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|builder
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
name|common
operator|.
name|trove
operator|.
name|TObjectFloatHashMap
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
name|trove
operator|.
name|TObjectFloatIterator
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
name|ToXContent
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
name|XContentFactory
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
name|XContentType
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
name|builder
operator|.
name|BinaryXContentBuilder
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
name|builder
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
name|query
operator|.
name|xcontent
operator|.
name|XContentQueryBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|FastByteArrayOutputStream
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A search source builder allowing to easily build search source. Simple construction  * using {@link org.elasticsearch.search.builder.SearchSourceBuilder#searchSource()}.  *  * @author kimchy (shay.banon)  * @see org.elasticsearch.action.search.SearchRequest#source(SearchSourceBuilder)  */
end_comment

begin_class
DECL|class|SearchSourceBuilder
specifier|public
class|class
name|SearchSourceBuilder
implements|implements
name|ToXContent
block|{
DECL|enum|Order
specifier|public
specifier|static
enum|enum
name|Order
block|{
DECL|enum constant|ASC
name|ASC
block|,
DECL|enum constant|DESC
name|DESC
block|}
comment|/**      * A static factory method to construct a new search source.      */
DECL|method|searchSource
specifier|public
specifier|static
name|SearchSourceBuilder
name|searchSource
parameter_list|()
block|{
return|return
operator|new
name|SearchSourceBuilder
argument_list|()
return|;
block|}
comment|/**      * A static factory method to construct new search facets.      */
DECL|method|facets
specifier|public
specifier|static
name|SearchSourceFacetsBuilder
name|facets
parameter_list|()
block|{
return|return
operator|new
name|SearchSourceFacetsBuilder
argument_list|()
return|;
block|}
comment|/**      * A static factory method to construct new search highlights.      */
DECL|method|highlight
specifier|public
specifier|static
name|SearchSourceHighlightBuilder
name|highlight
parameter_list|()
block|{
return|return
operator|new
name|SearchSourceHighlightBuilder
argument_list|()
return|;
block|}
DECL|field|queryBuilder
specifier|private
name|XContentQueryBuilder
name|queryBuilder
decl_stmt|;
DECL|field|from
specifier|private
name|int
name|from
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|queryParserName
specifier|private
name|String
name|queryParserName
decl_stmt|;
DECL|field|explain
specifier|private
name|Boolean
name|explain
decl_stmt|;
DECL|field|sortFields
specifier|private
name|List
argument_list|<
name|SortTuple
argument_list|>
name|sortFields
decl_stmt|;
DECL|field|fieldNames
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
decl_stmt|;
DECL|field|scriptFields
specifier|private
name|List
argument_list|<
name|ScriptField
argument_list|>
name|scriptFields
decl_stmt|;
DECL|field|facetsBuilder
specifier|private
name|SearchSourceFacetsBuilder
name|facetsBuilder
decl_stmt|;
DECL|field|highlightBuilder
specifier|private
name|SearchSourceHighlightBuilder
name|highlightBuilder
decl_stmt|;
DECL|field|indexBoost
specifier|private
name|TObjectFloatHashMap
argument_list|<
name|String
argument_list|>
name|indexBoost
init|=
literal|null
decl_stmt|;
comment|/**      * Constructs a new search source builder.      */
DECL|method|SearchSourceBuilder
specifier|public
name|SearchSourceBuilder
parameter_list|()
block|{     }
comment|/**      * Constructs a new search source builder with a search query.      *      * @see org.elasticsearch.index.query.xcontent.QueryBuilders      */
DECL|method|query
specifier|public
name|SearchSourceBuilder
name|query
parameter_list|(
name|XContentQueryBuilder
name|query
parameter_list|)
block|{
name|this
operator|.
name|queryBuilder
operator|=
name|query
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * From index to start the search from. Defaults to<tt>0</tt>.      */
DECL|method|from
specifier|public
name|SearchSourceBuilder
name|from
parameter_list|(
name|int
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The number of search hits to return. Defaults to<tt>10</tt>.      */
DECL|method|size
specifier|public
name|SearchSourceBuilder
name|size
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * An optional query parser name to use.      */
DECL|method|queryParserName
specifier|public
name|SearchSourceBuilder
name|queryParserName
parameter_list|(
name|String
name|queryParserName
parameter_list|)
block|{
name|this
operator|.
name|queryParserName
operator|=
name|queryParserName
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should each {@link org.elasticsearch.search.SearchHit} be returned with an      * explanation of the hit (ranking).      */
DECL|method|explain
specifier|public
name|SearchSourceBuilder
name|explain
parameter_list|(
name|Boolean
name|explain
parameter_list|)
block|{
name|this
operator|.
name|explain
operator|=
name|explain
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a sort against the given field name and the sort ordering.      *      * @param name  The name of the field      * @param order The sort ordering      */
DECL|method|sort
specifier|public
name|SearchSourceBuilder
name|sort
parameter_list|(
name|String
name|name
parameter_list|,
name|Order
name|order
parameter_list|)
block|{
name|boolean
name|reverse
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"score"
argument_list|)
condition|)
block|{
if|if
condition|(
name|order
operator|==
name|Order
operator|.
name|ASC
condition|)
block|{
name|reverse
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|order
operator|==
name|Order
operator|.
name|DESC
condition|)
block|{
name|reverse
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|sort
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
name|reverse
argument_list|)
return|;
block|}
comment|/**      * Add a sort against the given field name and if it should be revered or not.      *      * @param name    The name of the field to sort by      * @param reverse Should be soring be reversed or not      */
DECL|method|sort
specifier|public
name|SearchSourceBuilder
name|sort
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
return|return
name|sort
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
name|reverse
argument_list|)
return|;
block|}
comment|/**      * Add a sort against the given field name.      *      * @param name The name of the field to sort by      */
DECL|method|sort
specifier|public
name|SearchSourceBuilder
name|sort
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|sort
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Add a sort against the given field name of the given type.      *      * @param name The name of the field to sort by      * @param type The type of sort to perform      */
DECL|method|sort
specifier|public
name|SearchSourceBuilder
name|sort
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|type
parameter_list|)
block|{
return|return
name|sort
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Add a sort against the given field name and if it should be revered or not.      *      * @param name    The name of the field to sort by      * @param type    The type of the sort to perform      * @param reverse Should the sort be reversed or not      */
DECL|method|sort
specifier|public
name|SearchSourceBuilder
name|sort
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|type
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
if|if
condition|(
name|sortFields
operator|==
literal|null
condition|)
block|{
name|sortFields
operator|=
name|newArrayListWithCapacity
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|sortFields
operator|.
name|add
argument_list|(
operator|new
name|SortTuple
argument_list|(
name|name
argument_list|,
name|reverse
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds facets to perform as part of the search.      */
DECL|method|facets
specifier|public
name|SearchSourceBuilder
name|facets
parameter_list|(
name|SearchSourceFacetsBuilder
name|facetsBuilder
parameter_list|)
block|{
name|this
operator|.
name|facetsBuilder
operator|=
name|facetsBuilder
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds highlight to perform as part of the search.      */
DECL|method|highlight
specifier|public
name|SearchSourceBuilder
name|highlight
parameter_list|(
name|SearchSourceHighlightBuilder
name|highlightBuilder
parameter_list|)
block|{
name|this
operator|.
name|highlightBuilder
operator|=
name|highlightBuilder
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the fields to load and return as part of the search request. If none are specified,      * the source of the document will be returned.      */
DECL|method|fields
specifier|public
name|SearchSourceBuilder
name|fields
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fieldNames
operator|=
name|fields
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds the fields to load and return as part of the search request. If none are specified,      * the source of the document will be returned.      */
DECL|method|fields
specifier|public
name|SearchSourceBuilder
name|fields
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
if|if
condition|(
name|fieldNames
operator|==
literal|null
condition|)
block|{
name|fieldNames
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|fieldNames
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Adds a field to load and return (note, it must be stored) as part of the search request.      * If none are specified, the source of the document will be return.      */
DECL|method|field
specifier|public
name|SearchSourceBuilder
name|field
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|fieldNames
operator|==
literal|null
condition|)
block|{
name|fieldNames
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|fieldNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|scriptField
specifier|public
name|SearchSourceBuilder
name|scriptField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|script
parameter_list|)
block|{
return|return
name|scriptField
argument_list|(
name|name
argument_list|,
name|script
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|scriptField
specifier|public
name|SearchSourceBuilder
name|scriptField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|script
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
if|if
condition|(
name|scriptFields
operator|==
literal|null
condition|)
block|{
name|scriptFields
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
block|}
name|scriptFields
operator|.
name|add
argument_list|(
operator|new
name|ScriptField
argument_list|(
name|name
argument_list|,
name|script
argument_list|,
name|params
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the boost a specific index will receive when the query is executeed against it.      *      * @param index      The index to apply the boost against      * @param indexBoost The boost to apply to the index      */
DECL|method|indexBoost
specifier|public
name|SearchSourceBuilder
name|indexBoost
parameter_list|(
name|String
name|index
parameter_list|,
name|float
name|indexBoost
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|indexBoost
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|indexBoost
operator|=
operator|new
name|TObjectFloatHashMap
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|indexBoost
operator|.
name|put
argument_list|(
name|index
argument_list|,
name|indexBoost
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|buildAsUnsafeBytes
specifier|public
name|FastByteArrayOutputStream
name|buildAsUnsafeBytes
parameter_list|()
throws|throws
name|SearchSourceBuilderException
block|{
return|return
name|buildAsUnsafeBytes
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
return|;
block|}
DECL|method|buildAsUnsafeBytes
specifier|public
name|FastByteArrayOutputStream
name|buildAsUnsafeBytes
parameter_list|(
name|XContentType
name|contentType
parameter_list|)
throws|throws
name|SearchSourceBuilderException
block|{
try|try
block|{
name|BinaryXContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBinaryBuilder
argument_list|(
name|contentType
argument_list|)
decl_stmt|;
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|unsafeStream
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SearchSourceBuilderException
argument_list|(
literal|"Failed to build search source"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|buildAsBytes
specifier|public
name|byte
index|[]
name|buildAsBytes
parameter_list|()
throws|throws
name|SearchSourceBuilderException
block|{
return|return
name|buildAsBytes
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
return|;
block|}
DECL|method|buildAsBytes
specifier|public
name|byte
index|[]
name|buildAsBytes
parameter_list|(
name|XContentType
name|contentType
parameter_list|)
throws|throws
name|SearchSourceBuilderException
block|{
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBinaryBuilder
argument_list|(
name|contentType
argument_list|)
decl_stmt|;
name|toXContent
argument_list|(
name|builder
argument_list|,
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|copiedBytes
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SearchSourceBuilderException
argument_list|(
literal|"Failed to build search source"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|toXContent
annotation|@
name|Override
specifier|public
name|void
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
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|from
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"from"
argument_list|,
name|from
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|size
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"size"
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryParserName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"query_parser_name"
argument_list|,
name|queryParserName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryBuilder
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"query"
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|explain
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"explain"
argument_list|,
name|explain
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldNames
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fieldNames
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
literal|"fields"
argument_list|,
name|fieldNames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
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
name|fieldName
range|:
name|fieldNames
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|scriptFields
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"script_fields"
argument_list|)
expr_stmt|;
for|for
control|(
name|ScriptField
name|scriptField
range|:
name|scriptFields
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|scriptField
operator|.
name|fieldName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"script"
argument_list|,
name|scriptField
operator|.
name|script
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|scriptField
operator|.
name|params
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"params"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|scriptField
operator|.
name|params
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
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
name|sortFields
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"sort"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
for|for
control|(
name|SortTuple
name|sortTuple
range|:
name|sortFields
control|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|sortTuple
operator|.
name|fieldName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|sortTuple
operator|.
name|reverse
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"reverse"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sortTuple
operator|.
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
name|sortTuple
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
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
name|indexBoost
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"indices_boost"
argument_list|)
expr_stmt|;
for|for
control|(
name|TObjectFloatIterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|indexBoost
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
name|it
operator|.
name|advance
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|it
operator|.
name|key
argument_list|()
argument_list|,
name|it
operator|.
name|value
argument_list|()
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
name|facetsBuilder
operator|!=
literal|null
condition|)
block|{
name|facetsBuilder
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|highlightBuilder
operator|!=
literal|null
condition|)
block|{
name|highlightBuilder
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
DECL|class|ScriptField
specifier|private
specifier|static
class|class
name|ScriptField
block|{
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|script
specifier|private
specifier|final
name|String
name|script
decl_stmt|;
DECL|field|params
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
decl_stmt|;
DECL|method|ScriptField
specifier|private
name|ScriptField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|script
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
DECL|method|fieldName
specifier|public
name|String
name|fieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
DECL|method|script
specifier|public
name|String
name|script
parameter_list|()
block|{
return|return
name|script
return|;
block|}
DECL|method|params
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|()
block|{
return|return
name|params
return|;
block|}
block|}
DECL|class|SortTuple
specifier|private
specifier|static
class|class
name|SortTuple
block|{
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|reverse
specifier|private
specifier|final
name|boolean
name|reverse
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|method|SortTuple
specifier|private
name|SortTuple
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|boolean
name|reverse
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|reverse
operator|=
name|reverse
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|fieldName
specifier|public
name|String
name|fieldName
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
DECL|method|reverse
specifier|public
name|boolean
name|reverse
parameter_list|()
block|{
return|return
name|reverse
return|;
block|}
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
block|}
block|}
end_class

end_unit

