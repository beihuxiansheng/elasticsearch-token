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
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|BoostingQuery
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * The BoostingQuery class can be used to effectively demote results that match a given query.  * Unlike the "NOT" clause, this still selects documents that contain undesirable terms,  * but reduces their overall score:  *<p/>  * Query balancedQuery = new BoostingQuery(positiveQuery, negativeQuery, 0.01f);  * In this scenario the positiveQuery contains the mandatory, desirable criteria which is used to  * select all matching documents, and the negativeQuery contains the undesirable elements which  * are simply used to lessen the scores. Documents that match the negativeQuery have their score  * multiplied by the supplied "boost" parameter, so this should be less than 1 to achieve a  * demoting effect  */
end_comment

begin_class
DECL|class|BoostingQueryBuilder
specifier|public
class|class
name|BoostingQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|BoostingQueryBuilder
argument_list|>
implements|implements
name|BoostableQueryBuilder
argument_list|<
name|BoostingQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"boosting"
decl_stmt|;
DECL|field|positiveQuery
specifier|private
name|QueryBuilder
name|positiveQuery
decl_stmt|;
DECL|field|negativeQuery
specifier|private
name|QueryBuilder
name|negativeQuery
decl_stmt|;
DECL|field|negativeBoost
specifier|private
name|float
name|negativeBoost
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|boost
specifier|private
name|float
name|boost
init|=
literal|1.0f
decl_stmt|;
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|BoostingQueryBuilder
name|PROTOTYPE
init|=
operator|new
name|BoostingQueryBuilder
argument_list|()
decl_stmt|;
DECL|method|BoostingQueryBuilder
specifier|public
name|BoostingQueryBuilder
parameter_list|()
block|{     }
comment|/**      * Add the positive query for this boosting query.      */
DECL|method|positive
specifier|public
name|BoostingQueryBuilder
name|positive
parameter_list|(
name|QueryBuilder
name|positiveQuery
parameter_list|)
block|{
name|this
operator|.
name|positiveQuery
operator|=
name|positiveQuery
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the positive query for this boosting query.      */
DECL|method|positive
specifier|public
name|QueryBuilder
name|positive
parameter_list|()
block|{
return|return
name|this
operator|.
name|positiveQuery
return|;
block|}
comment|/**      * Add the negative query for this boosting query.      */
DECL|method|negative
specifier|public
name|BoostingQueryBuilder
name|negative
parameter_list|(
name|QueryBuilder
name|negativeQuery
parameter_list|)
block|{
name|this
operator|.
name|negativeQuery
operator|=
name|negativeQuery
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the negative query for this boosting query.      */
DECL|method|negative
specifier|public
name|QueryBuilder
name|negative
parameter_list|()
block|{
return|return
name|this
operator|.
name|negativeQuery
return|;
block|}
comment|/**      * Set the negative boost factor.      */
DECL|method|negativeBoost
specifier|public
name|BoostingQueryBuilder
name|negativeBoost
parameter_list|(
name|float
name|negativeBoost
parameter_list|)
block|{
name|this
operator|.
name|negativeBoost
operator|=
name|negativeBoost
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the negative boost factor.      */
DECL|method|negativeBoost
specifier|public
name|float
name|negativeBoost
parameter_list|()
block|{
return|return
name|this
operator|.
name|negativeBoost
return|;
block|}
comment|/**      * Set the boost factor.      */
annotation|@
name|Override
DECL|method|boost
specifier|public
name|BoostingQueryBuilder
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
comment|/**      * Get the boost factor.      */
DECL|method|boost
specifier|public
name|float
name|boost
parameter_list|()
block|{
return|return
name|this
operator|.
name|boost
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
if|if
condition|(
name|positiveQuery
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"boosting query requires positive query to be set"
argument_list|)
throw|;
block|}
if|if
condition|(
name|negativeQuery
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"boosting query requires negative query to be set"
argument_list|)
throw|;
block|}
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
literal|"positive"
argument_list|)
expr_stmt|;
name|positiveQuery
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"negative"
argument_list|)
expr_stmt|;
name|negativeQuery
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"negative_boost"
argument_list|,
name|negativeBoost
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
name|boost
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
DECL|method|validate
specifier|public
name|QueryValidationException
name|validate
parameter_list|()
block|{
name|QueryValidationException
name|validationException
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|negativeBoost
operator|<
literal|0
condition|)
block|{
name|validationException
operator|=
name|QueryValidationException
operator|.
name|addValidationError
argument_list|(
literal|"[boosting] query requires negativeBoost to be set to positive value"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
return|;
block|}
empty_stmt|;
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
comment|// make upstream queries ignore this query by returning `null`
comment|// if either inner query builder is null or returns null-Query
if|if
condition|(
name|positiveQuery
operator|==
literal|null
operator|||
name|negativeQuery
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Query
name|positive
init|=
name|positiveQuery
operator|.
name|toQuery
argument_list|(
name|parseContext
argument_list|)
decl_stmt|;
name|Query
name|negative
init|=
name|negativeQuery
operator|.
name|toQuery
argument_list|(
name|parseContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|positive
operator|==
literal|null
operator|||
name|negative
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|BoostingQuery
name|boostingQuery
init|=
operator|new
name|BoostingQuery
argument_list|(
name|positive
argument_list|,
name|negative
argument_list|,
name|negativeBoost
argument_list|)
decl_stmt|;
name|boostingQuery
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|boostingQuery
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
name|this
operator|.
name|boost
argument_list|,
name|this
operator|.
name|negativeBoost
argument_list|,
name|this
operator|.
name|positiveQuery
argument_list|,
name|this
operator|.
name|negativeQuery
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
name|BoostingQueryBuilder
name|other
init|=
operator|(
name|BoostingQueryBuilder
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|boost
argument_list|,
name|other
operator|.
name|boost
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|negativeBoost
argument_list|,
name|other
operator|.
name|negativeBoost
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|positiveQuery
argument_list|,
name|other
operator|.
name|positiveQuery
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|negativeQuery
argument_list|,
name|other
operator|.
name|negativeQuery
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|BoostingQueryBuilder
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryBuilder
name|positiveQuery
init|=
name|in
operator|.
name|readNamedWriteable
argument_list|()
decl_stmt|;
name|QueryBuilder
name|negativeQuery
init|=
name|in
operator|.
name|readNamedWriteable
argument_list|()
decl_stmt|;
name|BoostingQueryBuilder
name|boostingQuery
init|=
operator|new
name|BoostingQueryBuilder
argument_list|()
decl_stmt|;
name|boostingQuery
operator|.
name|positive
argument_list|(
name|positiveQuery
argument_list|)
expr_stmt|;
name|boostingQuery
operator|.
name|negative
argument_list|(
name|negativeQuery
argument_list|)
expr_stmt|;
name|boostingQuery
operator|.
name|boost
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
name|boostingQuery
operator|.
name|negativeBoost
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
return|return
name|boostingQuery
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
name|this
operator|.
name|positiveQuery
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeNamedWriteable
argument_list|(
name|this
operator|.
name|negativeQuery
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|this
operator|.
name|boost
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|this
operator|.
name|negativeBoost
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

