begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|xcontent
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
name|BooleanClause
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

begin_comment
comment|/**  * A filter that matches documents matching boolean combinations of other filters.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|BoolFilterBuilder
specifier|public
class|class
name|BoolFilterBuilder
extends|extends
name|BaseFilterBuilder
block|{
DECL|field|clauses
specifier|private
name|ArrayList
argument_list|<
name|Clause
argument_list|>
name|clauses
init|=
operator|new
name|ArrayList
argument_list|<
name|Clause
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|filterName
specifier|private
name|String
name|filterName
decl_stmt|;
comment|/**      * Adds a filter that<b>must</b> appear in the matching documents.      */
DECL|method|must
specifier|public
name|BoolFilterBuilder
name|must
parameter_list|(
name|XContentFilterBuilder
name|filterBuilder
parameter_list|)
block|{
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|Clause
argument_list|(
name|filterBuilder
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a filter that<b>must not</b> appear in the matching documents.      */
DECL|method|mustNot
specifier|public
name|BoolFilterBuilder
name|mustNot
parameter_list|(
name|XContentFilterBuilder
name|filterBuilder
parameter_list|)
block|{
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|Clause
argument_list|(
name|filterBuilder
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a filter that<i>should</i> appear in the matching documents. For a boolean filter      * with no<tt>MUST</tt> clauses one or more<code>SHOULD</code> clauses must match a document      * for the BooleanQuery to match.      */
DECL|method|should
specifier|public
name|BoolFilterBuilder
name|should
parameter_list|(
name|XContentFilterBuilder
name|filterBuilder
parameter_list|)
block|{
name|clauses
operator|.
name|add
argument_list|(
operator|new
name|Clause
argument_list|(
name|filterBuilder
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the filter name for the filter that can be used when searching for matched_filters per hit.      */
DECL|method|filterName
specifier|public
name|BoolFilterBuilder
name|filterName
parameter_list|(
name|String
name|filterName
parameter_list|)
block|{
name|this
operator|.
name|filterName
operator|=
name|filterName
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|doXContent
annotation|@
name|Override
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
literal|"bool"
argument_list|)
expr_stmt|;
for|for
control|(
name|Clause
name|clause
range|:
name|clauses
control|)
block|{
if|if
condition|(
name|clause
operator|.
name|occur
operator|==
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"must"
argument_list|)
expr_stmt|;
name|clause
operator|.
name|filterBuilder
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
name|clause
operator|.
name|occur
operator|==
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"must_not"
argument_list|)
expr_stmt|;
name|clause
operator|.
name|filterBuilder
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
name|clause
operator|.
name|occur
operator|==
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"should"
argument_list|)
expr_stmt|;
name|clause
operator|.
name|filterBuilder
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|filterName
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
name|filterName
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
DECL|class|Clause
specifier|private
specifier|static
class|class
name|Clause
block|{
DECL|field|filterBuilder
specifier|final
name|XContentFilterBuilder
name|filterBuilder
decl_stmt|;
DECL|field|occur
specifier|final
name|BooleanClause
operator|.
name|Occur
name|occur
decl_stmt|;
DECL|method|Clause
specifier|private
name|Clause
parameter_list|(
name|XContentFilterBuilder
name|filterBuilder
parameter_list|,
name|BooleanClause
operator|.
name|Occur
name|occur
parameter_list|)
block|{
name|this
operator|.
name|filterBuilder
operator|=
name|filterBuilder
expr_stmt|;
name|this
operator|.
name|occur
operator|=
name|occur
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

