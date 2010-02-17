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
name|index
operator|.
name|query
operator|.
name|QueryBuilderException
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

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|SpanNearJsonQueryBuilder
specifier|public
class|class
name|SpanNearJsonQueryBuilder
extends|extends
name|BaseJsonQueryBuilder
implements|implements
name|JsonSpanQueryBuilder
block|{
DECL|field|clauses
specifier|private
name|ArrayList
argument_list|<
name|JsonSpanQueryBuilder
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<
name|JsonSpanQueryBuilder
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|slop
specifier|private
name|int
name|slop
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|inOrder
specifier|private
name|Boolean
name|inOrder
decl_stmt|;
DECL|field|collectPayloads
specifier|private
name|Boolean
name|collectPayloads
decl_stmt|;
DECL|field|boost
specifier|private
name|float
name|boost
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|clause
specifier|public
name|SpanNearJsonQueryBuilder
name|clause
parameter_list|(
name|JsonSpanQueryBuilder
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
DECL|method|slop
specifier|public
name|SpanNearJsonQueryBuilder
name|slop
parameter_list|(
name|int
name|slop
parameter_list|)
block|{
name|this
operator|.
name|slop
operator|=
name|slop
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|inOrder
specifier|public
name|SpanNearJsonQueryBuilder
name|inOrder
parameter_list|(
name|boolean
name|inOrder
parameter_list|)
block|{
name|this
operator|.
name|inOrder
operator|=
name|inOrder
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|collectPayloads
specifier|public
name|SpanNearJsonQueryBuilder
name|collectPayloads
parameter_list|(
name|boolean
name|collectPayloads
parameter_list|)
block|{
name|this
operator|.
name|collectPayloads
operator|=
name|collectPayloads
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|boost
specifier|public
name|SpanNearJsonQueryBuilder
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
name|QueryBuilderException
argument_list|(
literal|"Must have at least one clause when building a spanNear query"
argument_list|)
throw|;
block|}
if|if
condition|(
name|slop
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|QueryBuilderException
argument_list|(
literal|"Must set the slop when building a spanNear query"
argument_list|)
throw|;
block|}
name|builder
operator|.
name|startObject
argument_list|(
name|SpanNearJsonQueryParser
operator|.
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
name|JsonSpanQueryBuilder
name|clause
range|:
name|clauses
control|)
block|{
name|clause
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
name|field
argument_list|(
literal|"slop"
argument_list|,
name|slop
argument_list|)
expr_stmt|;
if|if
condition|(
name|inOrder
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"inOrder"
argument_list|,
name|inOrder
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|collectPayloads
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"collectPayloads"
argument_list|,
name|collectPayloads
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
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

