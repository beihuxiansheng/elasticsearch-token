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
comment|/**  * A query that will execute the wrapped query only for the specified indices, and "match_all" when  * it does not match those indices (by default).  */
end_comment

begin_class
DECL|class|IndicesQueryBuilder
specifier|public
class|class
name|IndicesQueryBuilder
extends|extends
name|QueryBuilder
block|{
DECL|field|queryBuilder
specifier|private
specifier|final
name|QueryBuilder
name|queryBuilder
decl_stmt|;
DECL|field|indices
specifier|private
specifier|final
name|String
index|[]
name|indices
decl_stmt|;
DECL|field|sNoMatchQuery
specifier|private
name|String
name|sNoMatchQuery
decl_stmt|;
DECL|field|noMatchQuery
specifier|private
name|QueryBuilder
name|noMatchQuery
decl_stmt|;
DECL|field|queryName
specifier|private
name|String
name|queryName
decl_stmt|;
DECL|method|IndicesQueryBuilder
specifier|public
name|IndicesQueryBuilder
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|,
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|this
operator|.
name|queryBuilder
operator|=
name|queryBuilder
expr_stmt|;
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
block|}
comment|/**      * Sets the no match query, can either be<tt>all</tt> or<tt>none</tt>.      */
DECL|method|noMatchQuery
specifier|public
name|IndicesQueryBuilder
name|noMatchQuery
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|sNoMatchQuery
operator|=
name|type
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the query to use when it executes on an index that does not match the indices provided.      */
DECL|method|noMatchQuery
specifier|public
name|IndicesQueryBuilder
name|noMatchQuery
parameter_list|(
name|QueryBuilder
name|noMatchQuery
parameter_list|)
block|{
name|this
operator|.
name|noMatchQuery
operator|=
name|noMatchQuery
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the query name for the filter that can be used when searching for matched_filters per hit.      */
DECL|method|queryName
specifier|public
name|IndicesQueryBuilder
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
name|builder
operator|.
name|startObject
argument_list|(
name|IndicesQueryParser
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"indices"
argument_list|,
name|indices
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|noMatchQuery
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"no_match_query"
argument_list|)
expr_stmt|;
name|noMatchQuery
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sNoMatchQuery
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"no_match_query"
argument_list|,
name|sNoMatchQuery
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
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

