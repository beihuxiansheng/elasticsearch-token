begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
comment|/**  * A Query that matches documents containing a term.  *  *  */
end_comment

begin_class
DECL|class|TermQueryBuilder
specifier|public
class|class
name|TermQueryBuilder
extends|extends
name|BaseQueryBuilder
implements|implements
name|BoostableQueryBuilder
argument_list|<
name|TermQueryBuilder
argument_list|>
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|Object
name|value
decl_stmt|;
DECL|field|boost
specifier|private
name|float
name|boost
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|queryName
specifier|private
name|String
name|queryName
decl_stmt|;
comment|/**      * Constructs a new term query.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|TermQueryBuilder
specifier|public
name|TermQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
operator|(
name|Object
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new term query.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|TermQueryBuilder
specifier|public
name|TermQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
operator|(
name|Object
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new term query.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|TermQueryBuilder
specifier|public
name|TermQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
operator|(
name|Object
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new term query.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|TermQueryBuilder
specifier|public
name|TermQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
operator|(
name|Object
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new term query.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|TermQueryBuilder
specifier|public
name|TermQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
operator|(
name|Object
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new term query.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|TermQueryBuilder
specifier|public
name|TermQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
operator|(
name|Object
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new term query.      *      * @param name  The name of the field      * @param value The value of the term      */
DECL|method|TermQueryBuilder
specifier|public
name|TermQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Sets the boost for this query.  Documents matching this query will (in addition to the normal      * weightings) have their score multiplied by the boost provided.      */
DECL|method|boost
specifier|public
name|TermQueryBuilder
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
comment|/**      * Sets the query name for the filter that can be used when searching for matched_filters per hit.      */
DECL|method|queryName
specifier|public
name|TermQueryBuilder
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
name|TermQueryParser
operator|.
name|NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|boost
operator|==
operator|-
literal|1
operator|&&
name|queryName
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
literal|"value"
argument_list|,
name|value
argument_list|)
expr_stmt|;
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
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

