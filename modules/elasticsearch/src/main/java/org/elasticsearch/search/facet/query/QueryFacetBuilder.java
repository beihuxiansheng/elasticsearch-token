begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|xcontent
operator|.
name|XContentFilterBuilder
import|;
end_import

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
name|xcontent
operator|.
name|XContentQueryBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|builder
operator|.
name|SearchSourceBuilderException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|AbstractFacetBuilder
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|QueryFacetBuilder
specifier|public
class|class
name|QueryFacetBuilder
extends|extends
name|AbstractFacetBuilder
block|{
DECL|field|query
specifier|private
name|XContentQueryBuilder
name|query
decl_stmt|;
DECL|method|QueryFacetBuilder
specifier|public
name|QueryFacetBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * Marks the facet to run in a global scope, not bounded by any query.      */
DECL|method|global
annotation|@
name|Override
specifier|public
name|QueryFacetBuilder
name|global
parameter_list|(
name|boolean
name|global
parameter_list|)
block|{
name|super
operator|.
name|global
argument_list|(
name|global
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Marks the facet to run in a specific scope.      */
DECL|method|scope
annotation|@
name|Override
specifier|public
name|QueryFacetBuilder
name|scope
parameter_list|(
name|String
name|scope
parameter_list|)
block|{
name|super
operator|.
name|scope
argument_list|(
name|scope
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|facetFilter
specifier|public
name|QueryFacetBuilder
name|facetFilter
parameter_list|(
name|XContentFilterBuilder
name|filter
parameter_list|)
block|{
name|this
operator|.
name|facetFilter
operator|=
name|filter
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|query
specifier|public
name|QueryFacetBuilder
name|query
parameter_list|(
name|XContentQueryBuilder
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|toXContent
annotation|@
name|Override
specifier|public
name|void
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
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchSourceBuilderException
argument_list|(
literal|"query must be set on query facet for facet ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
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
name|QueryFacetCollectorParser
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|query
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|addFilterFacetAndGlobal
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
block|}
block|}
end_class

end_unit

