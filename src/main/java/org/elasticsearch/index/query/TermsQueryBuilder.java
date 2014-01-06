begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TermsQueryBuilder
specifier|public
class|class
name|TermsQueryBuilder
extends|extends
name|BaseQueryBuilder
implements|implements
name|BoostableQueryBuilder
argument_list|<
name|TermsQueryBuilder
argument_list|>
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|Object
index|[]
name|values
decl_stmt|;
DECL|field|minimumShouldMatch
specifier|private
name|String
name|minimumShouldMatch
decl_stmt|;
DECL|field|disableCoord
specifier|private
name|Boolean
name|disableCoord
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
comment|/**      * A query for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|TermsQueryBuilder
specifier|public
name|TermsQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|values
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|values
argument_list|)
expr_stmt|;
block|}
comment|/**      * A query for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|TermsQueryBuilder
specifier|public
name|TermsQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|int
modifier|...
name|values
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
name|values
operator|=
operator|new
name|Integer
index|[
name|values
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|values
index|[
name|i
index|]
operator|=
name|values
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
comment|/**      * A query for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|TermsQueryBuilder
specifier|public
name|TermsQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|long
modifier|...
name|values
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
name|values
operator|=
operator|new
name|Long
index|[
name|values
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|values
index|[
name|i
index|]
operator|=
name|values
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
comment|/**      * A query for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|TermsQueryBuilder
specifier|public
name|TermsQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|float
modifier|...
name|values
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
name|values
operator|=
operator|new
name|Float
index|[
name|values
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|values
index|[
name|i
index|]
operator|=
name|values
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
comment|/**      * A query for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|TermsQueryBuilder
specifier|public
name|TermsQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|double
modifier|...
name|values
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
name|values
operator|=
operator|new
name|Double
index|[
name|values
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|values
index|[
name|i
index|]
operator|=
name|values
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
comment|/**      * A query for a field based on several terms matching on any of them.      *      * @param name   The field name      * @param values The terms      */
DECL|method|TermsQueryBuilder
specifier|public
name|TermsQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
modifier|...
name|values
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
name|values
operator|=
name|values
expr_stmt|;
block|}
comment|/**    * A query for a field based on several terms matching on any of them.    *    * @param name    The field name    * @param values  The terms    */
DECL|method|TermsQueryBuilder
specifier|public
name|TermsQueryBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|Collection
name|values
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|values
operator|.
name|toArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the minimum number of matches across the provided terms. Defaults to<tt>1</tt>.      */
DECL|method|minimumMatch
specifier|public
name|TermsQueryBuilder
name|minimumMatch
parameter_list|(
name|int
name|minimumMatch
parameter_list|)
block|{
name|this
operator|.
name|minimumShouldMatch
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|minimumMatch
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|minimumShouldMatch
specifier|public
name|TermsQueryBuilder
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
comment|/**      * Sets the boost for this query.  Documents matching this query will (in addition to the normal      * weightings) have their score multiplied by the boost provided.      */
DECL|method|boost
specifier|public
name|TermsQueryBuilder
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
comment|/**      * Disables<tt>Similarity#coord(int,int)</tt> in scoring. Defualts to<tt>false</tt>.      */
DECL|method|disableCoord
specifier|public
name|TermsQueryBuilder
name|disableCoord
parameter_list|(
name|boolean
name|disableCoord
parameter_list|)
block|{
name|this
operator|.
name|disableCoord
operator|=
name|disableCoord
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the query name for the filter that can be used when searching for matched_filters per hit.      */
DECL|method|queryName
specifier|public
name|TermsQueryBuilder
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
name|TermsQueryParser
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
name|name
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|value
range|:
name|values
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|value
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
if|if
condition|(
name|disableCoord
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"disable_coord"
argument_list|,
name|disableCoord
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

