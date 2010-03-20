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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|json
operator|.
name|JsonBuilder
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
import|import static
name|com
operator|.
name|google
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
comment|/**  * A query that generates the union of documents produced by its sub-queries, and that scores each document  * with the maximum score for that document as produced by any sub-query, plus a tie breaking increment for any  * additional matching sub-queries.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|DisMaxJsonQueryBuilder
specifier|public
class|class
name|DisMaxJsonQueryBuilder
extends|extends
name|BaseJsonQueryBuilder
block|{
DECL|field|queries
specifier|private
name|ArrayList
argument_list|<
name|JsonQueryBuilder
argument_list|>
name|queries
init|=
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|boost
specifier|private
name|float
name|boost
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|tieBreaker
specifier|private
name|float
name|tieBreaker
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Add a sub-query to this disjunction.      */
DECL|method|add
specifier|public
name|DisMaxJsonQueryBuilder
name|add
parameter_list|(
name|JsonQueryBuilder
name|queryBuilder
parameter_list|)
block|{
name|queries
operator|.
name|add
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the boost for this query.  Documents matching this query will (in addition to the normal      * weightings) have their score multiplied by the boost provided.      */
DECL|method|boost
specifier|public
name|DisMaxJsonQueryBuilder
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
comment|/**      * The score of each non-maximum disjunct for a document is multiplied by this weight      * and added into the final score.  If non-zero, the value should be small, on the order of 0.1, which says that      * 10 occurrences of word in a lower-scored field that is also in a higher scored field is just as good as a unique      * word in the lower scored field (i.e., one that is not in any higher scored field.      */
DECL|method|tieBreaker
specifier|public
name|DisMaxJsonQueryBuilder
name|tieBreaker
parameter_list|(
name|float
name|tieBreaker
parameter_list|)
block|{
name|this
operator|.
name|tieBreaker
operator|=
name|tieBreaker
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|doJson
annotation|@
name|Override
specifier|protected
name|void
name|doJson
parameter_list|(
name|JsonBuilder
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
literal|"disMax"
argument_list|)
expr_stmt|;
if|if
condition|(
name|tieBreaker
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"tieBreaker"
argument_list|,
name|tieBreaker
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
name|builder
operator|.
name|startArray
argument_list|(
literal|"queries"
argument_list|)
expr_stmt|;
for|for
control|(
name|JsonQueryBuilder
name|queryBuilder
range|:
name|queries
control|)
block|{
name|queryBuilder
operator|.
name|toJson
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
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

