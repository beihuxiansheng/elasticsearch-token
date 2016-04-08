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
name|BoostQuery
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
operator|.
name|SpanBoostQuery
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
name|spans
operator|.
name|SpanQuery
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
name|util
operator|.
name|BytesRef
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
name|support
operator|.
name|ToXContentToBytes
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
name|ParseField
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
name|BytesRefs
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
name|common
operator|.
name|xcontent
operator|.
name|XContentType
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
name|Collection
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
comment|/**  * Base class for all classes producing lucene queries.  * Supports conversion to BytesReference and creation of lucene Query objects.  */
end_comment

begin_class
DECL|class|AbstractQueryBuilder
specifier|public
specifier|abstract
class|class
name|AbstractQueryBuilder
parameter_list|<
name|QB
extends|extends
name|AbstractQueryBuilder
parameter_list|<
name|QB
parameter_list|>
parameter_list|>
extends|extends
name|ToXContentToBytes
implements|implements
name|QueryBuilder
argument_list|<
name|QB
argument_list|>
block|{
comment|/** Default for boost to apply to resulting Lucene query. Defaults to 1.0*/
DECL|field|DEFAULT_BOOST
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT_BOOST
init|=
literal|1.0f
decl_stmt|;
DECL|field|NAME_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|NAME_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"_name"
argument_list|)
decl_stmt|;
DECL|field|BOOST_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|BOOST_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"boost"
argument_list|)
decl_stmt|;
DECL|field|queryName
specifier|protected
name|String
name|queryName
decl_stmt|;
DECL|field|boost
specifier|protected
name|float
name|boost
init|=
name|DEFAULT_BOOST
decl_stmt|;
DECL|method|AbstractQueryBuilder
specifier|protected
name|AbstractQueryBuilder
parameter_list|()
block|{
name|super
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
expr_stmt|;
block|}
DECL|method|AbstractQueryBuilder
specifier|protected
name|AbstractQueryBuilder
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|boost
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
name|queryName
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
specifier|final
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
name|writeFloat
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|queryName
argument_list|)
expr_stmt|;
name|doWriteTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|doWriteTo
specifier|protected
specifier|abstract
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
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
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|doXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|doXContent
specifier|protected
specifier|abstract
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
function_decl|;
DECL|method|printBoostAndQueryName
specifier|protected
name|void
name|printBoostAndQueryName
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
name|BOOST_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|boost
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
name|NAME_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|queryName
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toQuery
specifier|public
specifier|final
name|Query
name|toQuery
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|query
init|=
name|doToQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|boost
operator|!=
name|DEFAULT_BOOST
condition|)
block|{
if|if
condition|(
name|query
operator|instanceof
name|SpanQuery
condition|)
block|{
name|query
operator|=
operator|new
name|SpanBoostQuery
argument_list|(
operator|(
name|SpanQuery
operator|)
name|query
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
operator|new
name|BoostQuery
argument_list|(
name|query
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|queryName
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|addNamedQuery
argument_list|(
name|queryName
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|toFilter
specifier|public
specifier|final
name|Query
name|toFilter
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|result
init|=
literal|null
decl_stmt|;
specifier|final
name|boolean
name|originalIsFilter
init|=
name|context
operator|.
name|isFilter
decl_stmt|;
try|try
block|{
name|context
operator|.
name|isFilter
operator|=
literal|true
expr_stmt|;
name|result
operator|=
name|toQuery
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|isFilter
operator|=
name|originalIsFilter
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|doToQuery
specifier|protected
specifier|abstract
name|Query
name|doToQuery
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Sets the query name for the query.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|queryName
specifier|public
specifier|final
name|QB
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
operator|(
name|QB
operator|)
name|this
return|;
block|}
comment|/**      * Returns the query name for the query.      */
annotation|@
name|Override
DECL|method|queryName
specifier|public
specifier|final
name|String
name|queryName
parameter_list|()
block|{
return|return
name|queryName
return|;
block|}
comment|/**      * Returns the boost for this query.      */
annotation|@
name|Override
DECL|method|boost
specifier|public
specifier|final
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
comment|/**      * Sets the boost for this query.  Documents matching this query will (in addition to the normal      * weightings) have their score multiplied by the boost provided.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|boost
specifier|public
specifier|final
name|QB
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
operator|(
name|QB
operator|)
name|this
return|;
block|}
DECL|method|addValidationError
specifier|protected
specifier|final
name|QueryValidationException
name|addValidationError
parameter_list|(
name|String
name|validationError
parameter_list|,
name|QueryValidationException
name|validationException
parameter_list|)
block|{
return|return
name|QueryValidationException
operator|.
name|addValidationError
argument_list|(
name|getName
argument_list|()
argument_list|,
name|validationError
argument_list|,
name|validationException
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
specifier|final
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|QB
name|other
init|=
operator|(
name|QB
operator|)
name|obj
decl_stmt|;
return|return
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
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|boost
argument_list|,
name|other
operator|.
name|boost
argument_list|)
operator|&&
name|doEquals
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/**      * Indicates whether some other {@link QueryBuilder} object of the same type is "equal to" this one.      */
DECL|method|doEquals
specifier|protected
specifier|abstract
name|boolean
name|doEquals
parameter_list|(
name|QB
name|other
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|getClass
argument_list|()
argument_list|,
name|queryName
argument_list|,
name|boost
argument_list|,
name|doHashCode
argument_list|()
argument_list|)
return|;
block|}
DECL|method|doHashCode
specifier|protected
specifier|abstract
name|int
name|doHashCode
parameter_list|()
function_decl|;
comment|/**      * This helper method checks if the object passed in is a string, if so it      * converts it to a {@link BytesRef}.      * @param obj the input object      * @return the same input object or a {@link BytesRef} representation if input was of type string      */
DECL|method|convertToBytesRefIfString
specifier|protected
specifier|static
name|Object
name|convertToBytesRefIfString
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|String
condition|)
block|{
return|return
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|obj
argument_list|)
return|;
block|}
return|return
name|obj
return|;
block|}
comment|/**      * This helper method checks if the object passed in is a {@link BytesRef}, if so it      * converts it to a utf8 string.      * @param obj the input object      * @return the same input object or a utf8 string if input was of type {@link BytesRef}      */
DECL|method|convertToStringIfBytesRef
specifier|protected
specifier|static
name|Object
name|convertToStringIfBytesRef
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|BytesRef
condition|)
block|{
return|return
operator|(
operator|(
name|BytesRef
operator|)
name|obj
operator|)
operator|.
name|utf8ToString
argument_list|()
return|;
block|}
return|return
name|obj
return|;
block|}
comment|/**      * Helper method to convert collection of {@link QueryBuilder} instances to lucene      * {@link Query} instances. {@link QueryBuilder} that return<tt>null</tt> calling      * their {@link QueryBuilder#toQuery(QueryShardContext)} method are not added to the      * resulting collection.      */
DECL|method|toQueries
specifier|protected
specifier|static
name|Collection
argument_list|<
name|Query
argument_list|>
name|toQueries
parameter_list|(
name|Collection
argument_list|<
name|QueryBuilder
argument_list|<
name|?
argument_list|>
argument_list|>
name|queryBuilders
parameter_list|,
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|QueryShardException
throws|,
name|IOException
block|{
name|List
argument_list|<
name|Query
argument_list|>
name|queries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|queryBuilders
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|queryBuilder
range|:
name|queryBuilders
control|)
block|{
name|Query
name|query
init|=
name|queryBuilder
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|queries
operator|.
name|add
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|queries
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
comment|//default impl returns the same as writeable name, but we keep the distinction between the two just to make sure
return|return
name|getWriteableName
argument_list|()
return|;
block|}
DECL|method|writeQueries
specifier|protected
specifier|final
name|void
name|writeQueries
parameter_list|(
name|StreamOutput
name|out
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|QueryBuilder
argument_list|<
name|?
argument_list|>
argument_list|>
name|queries
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|queries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|query
range|:
name|queries
control|)
block|{
name|out
operator|.
name|writeQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readQueries
specifier|protected
specifier|final
name|List
argument_list|<
name|QueryBuilder
argument_list|<
name|?
argument_list|>
argument_list|>
name|readQueries
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|QueryBuilder
argument_list|<
name|?
argument_list|>
argument_list|>
name|queries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
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
name|queries
operator|.
name|add
argument_list|(
name|in
operator|.
name|readQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|queries
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
specifier|final
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|rewrite
parameter_list|(
name|QueryRewriteContext
name|queryShardContext
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|rewritten
init|=
name|doRewrite
argument_list|(
name|queryShardContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewritten
operator|==
name|this
condition|)
block|{
return|return
name|rewritten
return|;
block|}
if|if
condition|(
name|queryName
argument_list|()
operator|!=
literal|null
operator|&&
name|rewritten
operator|.
name|queryName
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// we inherit the name
name|rewritten
operator|.
name|queryName
argument_list|(
name|queryName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|boost
argument_list|()
operator|!=
name|DEFAULT_BOOST
operator|&&
name|rewritten
operator|.
name|boost
argument_list|()
operator|==
name|DEFAULT_BOOST
condition|)
block|{
name|rewritten
operator|.
name|boost
argument_list|(
name|boost
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|rewritten
return|;
block|}
DECL|method|doRewrite
specifier|protected
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|doRewrite
parameter_list|(
name|QueryRewriteContext
name|queryShardContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

