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
name|spans
operator|.
name|SpanQuery
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

begin_class
DECL|class|SpanOrQueryBuilder
specifier|public
class|class
name|SpanOrQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|SpanOrQueryBuilder
argument_list|>
implements|implements
name|SpanQueryBuilder
argument_list|<
name|SpanOrQueryBuilder
argument_list|>
implements|,
name|BoostableQueryBuilder
argument_list|<
name|SpanOrQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"span_or"
decl_stmt|;
DECL|field|clauses
specifier|private
name|ArrayList
argument_list|<
name|SpanQueryBuilder
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<>
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
DECL|field|queryName
specifier|private
name|String
name|queryName
decl_stmt|;
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|SpanOrQueryBuilder
name|PROTOTYPE
init|=
operator|new
name|SpanOrQueryBuilder
argument_list|()
decl_stmt|;
DECL|method|clause
specifier|public
name|SpanOrQueryBuilder
name|clause
parameter_list|(
name|SpanQueryBuilder
name|clause
parameter_list|)
block|{
name|clauses
operator|.
name|add
argument_list|(
name|clause
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|boost
specifier|public
name|SpanOrQueryBuilder
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
name|SpanOrQueryBuilder
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
if|if
condition|(
name|clauses
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Must have at least one clause when building a spanOr query"
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
name|startArray
argument_list|(
literal|"clauses"
argument_list|)
expr_stmt|;
for|for
control|(
name|SpanQueryBuilder
name|clause
range|:
name|clauses
control|)
block|{
name|clause
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
name|SpanQuery
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
comment|//norelease just a temporary implementation, will go away once this query is refactored and properly overrides toQuery
return|return
operator|(
name|SpanQuery
operator|)
name|super
operator|.
name|toQuery
argument_list|(
name|parseContext
argument_list|)
return|;
block|}
block|}
end_class

end_unit

