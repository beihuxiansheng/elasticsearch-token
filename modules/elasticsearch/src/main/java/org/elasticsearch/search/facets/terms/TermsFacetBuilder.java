begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facets.terms
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facets
operator|.
name|terms
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
name|collect
operator|.
name|Maps
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
name|regex
operator|.
name|Regex
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
name|builder
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
name|facets
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TermsFacetBuilder
specifier|public
class|class
name|TermsFacetBuilder
extends|extends
name|AbstractFacetBuilder
block|{
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
init|=
literal|10
decl_stmt|;
DECL|field|exclude
specifier|private
name|String
index|[]
name|exclude
decl_stmt|;
DECL|field|regex
specifier|private
name|String
name|regex
decl_stmt|;
DECL|field|regexFlags
specifier|private
name|int
name|regexFlags
init|=
literal|0
decl_stmt|;
DECL|field|comparatorType
specifier|private
name|TermsFacet
operator|.
name|ComparatorType
name|comparatorType
decl_stmt|;
DECL|field|script
specifier|private
name|String
name|script
decl_stmt|;
DECL|field|params
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
decl_stmt|;
DECL|method|TermsFacetBuilder
specifier|public
name|TermsFacetBuilder
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
DECL|method|global
specifier|public
name|TermsFacetBuilder
name|global
parameter_list|(
name|boolean
name|global
parameter_list|)
block|{
name|this
operator|.
name|global
operator|=
name|global
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|facetFilter
specifier|public
name|TermsFacetBuilder
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
DECL|method|field
specifier|public
name|TermsFacetBuilder
name|field
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|field
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|exclude
specifier|public
name|TermsFacetBuilder
name|exclude
parameter_list|(
name|String
modifier|...
name|exclude
parameter_list|)
block|{
name|this
operator|.
name|exclude
operator|=
name|exclude
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|size
specifier|public
name|TermsFacetBuilder
name|size
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|regex
specifier|public
name|TermsFacetBuilder
name|regex
parameter_list|(
name|String
name|regex
parameter_list|)
block|{
return|return
name|regex
argument_list|(
name|regex
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|regex
specifier|public
name|TermsFacetBuilder
name|regex
parameter_list|(
name|String
name|regex
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|this
operator|.
name|regex
operator|=
name|regex
expr_stmt|;
name|this
operator|.
name|regexFlags
operator|=
name|flags
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|order
specifier|public
name|TermsFacetBuilder
name|order
parameter_list|(
name|TermsFacet
operator|.
name|ComparatorType
name|comparatorType
parameter_list|)
block|{
name|this
operator|.
name|comparatorType
operator|=
name|comparatorType
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|script
specifier|public
name|TermsFacetBuilder
name|script
parameter_list|(
name|String
name|script
parameter_list|)
block|{
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|param
specifier|public
name|TermsFacetBuilder
name|param
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
name|params
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
block|}
name|params
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
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
name|fieldName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchSourceBuilderException
argument_list|(
literal|"field must be set on terms facet for facet ["
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
name|startObject
argument_list|(
name|TermsFacetCollectorParser
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"size"
argument_list|,
name|size
argument_list|)
expr_stmt|;
if|if
condition|(
name|exclude
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
literal|"exclude"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|ex
range|:
name|exclude
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|regex
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"regex"
argument_list|,
name|regex
argument_list|)
expr_stmt|;
if|if
condition|(
name|regexFlags
operator|!=
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"regex_flags"
argument_list|,
name|Regex
operator|.
name|flagsToString
argument_list|(
name|regexFlags
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|comparatorType
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"order"
argument_list|,
name|comparatorType
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|script
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"script"
argument_list|,
name|script
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|params
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"params"
argument_list|,
name|this
operator|.
name|params
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endObject
argument_list|()
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

