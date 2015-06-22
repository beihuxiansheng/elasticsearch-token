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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * A filter that matches documents matching boolean combinations of other filters.  * @deprecated Use {@link BoolQueryBuilder} instead  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|AndQueryBuilder
specifier|public
class|class
name|AndQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|AndQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"and"
decl_stmt|;
DECL|field|filters
specifier|private
name|ArrayList
argument_list|<
name|QueryBuilder
argument_list|>
name|filters
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|queryName
specifier|private
name|String
name|queryName
decl_stmt|;
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|AndQueryBuilder
name|PROTOTYPE
init|=
operator|new
name|AndQueryBuilder
argument_list|()
decl_stmt|;
DECL|method|AndQueryBuilder
specifier|public
name|AndQueryBuilder
parameter_list|(
name|QueryBuilder
modifier|...
name|filters
parameter_list|)
block|{
for|for
control|(
name|QueryBuilder
name|filter
range|:
name|filters
control|)
block|{
name|this
operator|.
name|filters
operator|.
name|add
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Adds a filter to the list of filters to "and".      */
DECL|method|add
specifier|public
name|AndQueryBuilder
name|add
parameter_list|(
name|QueryBuilder
name|filterBuilder
parameter_list|)
block|{
name|filters
operator|.
name|add
argument_list|(
name|filterBuilder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * @return the list of filters added to "and".      */
DECL|method|filters
specifier|public
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|filters
parameter_list|()
block|{
return|return
name|this
operator|.
name|filters
return|;
block|}
comment|/**      * Sets the filter name for the filter that can be used when searching for matched_filters per hit.      */
DECL|method|queryName
specifier|public
name|AndQueryBuilder
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
comment|/**      * @return the query name.      */
DECL|method|queryName
specifier|public
name|Object
name|queryName
parameter_list|()
block|{
return|return
name|this
operator|.
name|queryName
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
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
literal|"filters"
argument_list|)
expr_stmt|;
for|for
control|(
name|QueryBuilder
name|filter
range|:
name|filters
control|)
block|{
name|filter
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
name|endArray
argument_list|()
expr_stmt|;
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
annotation|@
name|Override
DECL|method|toQuery
specifier|public
name|Query
name|toQuery
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|QueryParsingException
throws|,
name|IOException
block|{
if|if
condition|(
name|filters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// no filters provided, this should be ignored upstream
return|return
literal|null
return|;
block|}
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|QueryBuilder
name|f
range|:
name|filters
control|)
block|{
name|query
operator|.
name|add
argument_list|(
name|f
operator|.
name|toQuery
argument_list|(
name|parseContext
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
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
name|parseContext
operator|.
name|addNamedQuery
argument_list|(
name|queryName
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|filters
argument_list|,
name|queryName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|AndQueryBuilder
name|other
init|=
operator|(
name|AndQueryBuilder
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|filters
argument_list|,
name|other
operator|.
name|filters
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|queryName
argument_list|,
name|other
operator|.
name|queryName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|AndQueryBuilder
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|AndQueryBuilder
name|andQueryBuilder
init|=
operator|new
name|AndQueryBuilder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|QueryBuilder
argument_list|>
name|queryBuilders
init|=
name|in
operator|.
name|readNamedWritableList
argument_list|()
decl_stmt|;
for|for
control|(
name|QueryBuilder
name|queryBuilder
range|:
name|queryBuilders
control|)
block|{
name|andQueryBuilder
operator|.
name|add
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
block|}
name|andQueryBuilder
operator|.
name|queryName
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
return|return
name|andQueryBuilder
return|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeNamedWritableList
argument_list|(
name|this
operator|.
name|filters
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|queryName
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

