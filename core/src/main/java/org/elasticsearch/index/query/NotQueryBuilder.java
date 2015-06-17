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
name|Objects
import|;
end_import

begin_comment
comment|/**  * A filter that matches documents matching boolean combinations of other filters.  */
end_comment

begin_class
DECL|class|NotQueryBuilder
specifier|public
class|class
name|NotQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|NotQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"not"
decl_stmt|;
DECL|field|filter
specifier|private
specifier|final
name|QueryBuilder
name|filter
decl_stmt|;
DECL|field|queryName
specifier|private
name|String
name|queryName
decl_stmt|;
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|NotQueryBuilder
name|PROTOTYPE
init|=
operator|new
name|NotQueryBuilder
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|method|NotQueryBuilder
specifier|public
name|NotQueryBuilder
parameter_list|(
name|QueryBuilder
name|filter
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
comment|/**      * @return the filter added to "not".      */
DECL|method|filter
specifier|public
name|QueryBuilder
name|filter
parameter_list|()
block|{
return|return
name|this
operator|.
name|filter
return|;
block|}
comment|/**      * Sets the filter name for the filter that can be used when searching for matched_filters per hit.      */
DECL|method|queryName
specifier|public
name|NotQueryBuilder
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
name|String
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
name|field
argument_list|(
literal|"query"
argument_list|)
expr_stmt|;
name|filter
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
name|filter
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Query
name|luceneQuery
init|=
name|filter
operator|.
name|toQuery
argument_list|(
name|parseContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|luceneQuery
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Query
name|notQuery
init|=
name|Queries
operator|.
name|not
argument_list|(
name|luceneQuery
argument_list|)
decl_stmt|;
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
name|notQuery
argument_list|)
expr_stmt|;
block|}
return|return
name|notQuery
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|QueryValidationException
name|validate
parameter_list|()
block|{
comment|// nothing to validate.
return|return
literal|null
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
name|filter
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
name|NotQueryBuilder
name|other
init|=
operator|(
name|NotQueryBuilder
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|filter
argument_list|,
name|other
operator|.
name|filter
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
name|NotQueryBuilder
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryBuilder
name|queryBuilder
init|=
name|in
operator|.
name|readNamedWriteable
argument_list|()
decl_stmt|;
name|NotQueryBuilder
name|notQueryBuilder
init|=
operator|new
name|NotQueryBuilder
argument_list|(
name|queryBuilder
argument_list|)
decl_stmt|;
name|notQueryBuilder
operator|.
name|queryName
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
return|return
name|notQueryBuilder
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
name|writeNamedWriteable
argument_list|(
name|filter
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
block|}
end_class

end_unit

