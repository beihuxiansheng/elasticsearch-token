begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.terms
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|terms
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
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
name|FilterBuilder
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
name|FacetBuilder
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
name|Locale
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
comment|/**  * Term facets allow to collect frequency of terms within one (or more) field.  */
end_comment

begin_class
DECL|class|TermsFacetBuilder
specifier|public
class|class
name|TermsFacetBuilder
extends|extends
name|FacetBuilder
block|{
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
DECL|field|fieldsNames
specifier|private
name|String
index|[]
name|fieldsNames
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
init|=
literal|10
decl_stmt|;
DECL|field|allTerms
specifier|private
name|Boolean
name|allTerms
decl_stmt|;
DECL|field|exclude
specifier|private
name|Object
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
DECL|field|lang
specifier|private
name|String
name|lang
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
DECL|field|executionHint
name|String
name|executionHint
decl_stmt|;
comment|/**      * Construct a new term facet with the provided facet name.      *      * @param name The facet name.      */
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
comment|/**      * Should the fact run in global mode (not bounded by the search query) or not. Defaults      * to<tt>false</tt>.      */
DECL|method|global
specifier|public
name|TermsFacetBuilder
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
comment|/**      * An additional facet filter that will further filter the documents the facet will be      * executed on.      */
DECL|method|facetFilter
specifier|public
name|TermsFacetBuilder
name|facetFilter
parameter_list|(
name|FilterBuilder
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
comment|/**      * Sets the nested path the facet will execute on. A match (root object) will then cause all the      * nested objects matching the path to be computed into the facet.      */
DECL|method|nested
specifier|public
name|TermsFacetBuilder
name|nested
parameter_list|(
name|String
name|nested
parameter_list|)
block|{
name|this
operator|.
name|nested
operator|=
name|nested
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The field the terms will be collected from.      */
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
comment|/**      * The fields the terms will be collected from.      */
DECL|method|fields
specifier|public
name|TermsFacetBuilder
name|fields
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fieldsNames
operator|=
name|fields
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Define a script field that will control the terms that will be used (and not filtered, as is the      * case when the script is provided on top of field / fields).      */
DECL|method|scriptField
specifier|public
name|TermsFacetBuilder
name|scriptField
parameter_list|(
name|String
name|scriptField
parameter_list|)
block|{
name|this
operator|.
name|script
operator|=
name|scriptField
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * A set of terms that will be excluded.      */
DECL|method|exclude
specifier|public
name|TermsFacetBuilder
name|exclude
parameter_list|(
name|Object
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
comment|/**      * The number of terms (and frequencies) to return. Defaults to 10.      */
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
comment|/**      * A regular expression to use in order to further filter terms.      */
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
comment|/**      * A regular expression (with flags) to use in order to further filter terms.      */
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
comment|/**      * The order by which to return the facets by. Defaults to {@link TermsFacet.ComparatorType#COUNT}.      */
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
comment|/**      * A script allowing to either modify or ignore a provided term (can be accessed using<tt>term</tt> var).      */
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
comment|/**      * The language of the script.      */
DECL|method|lang
specifier|public
name|TermsFacetBuilder
name|lang
parameter_list|(
name|String
name|lang
parameter_list|)
block|{
name|this
operator|.
name|lang
operator|=
name|lang
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * An execution hint to how the facet is computed.      */
DECL|method|executionHint
specifier|public
name|TermsFacetBuilder
name|executionHint
parameter_list|(
name|String
name|executionHint
parameter_list|)
block|{
name|this
operator|.
name|executionHint
operator|=
name|executionHint
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * A parameter that will be passed to the script.      *      * @param name  The name of the script parameter.      * @param value The value of the script parameter.      */
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
comment|/**      * Sets all possible terms to be loaded, even ones with 0 count. Note, this *should not* be used      * with a field that has many possible terms.      */
DECL|method|allTerms
specifier|public
name|TermsFacetBuilder
name|allTerms
parameter_list|(
name|boolean
name|allTerms
parameter_list|)
block|{
name|this
operator|.
name|allTerms
operator|=
name|allTerms
expr_stmt|;
return|return
name|this
return|;
block|}
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
if|if
condition|(
name|fieldName
operator|==
literal|null
operator|&&
name|fieldsNames
operator|==
literal|null
operator|&&
name|script
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchSourceBuilderException
argument_list|(
literal|"field/fields/script must be set on terms facet for facet ["
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
name|TermsFacet
operator|.
name|TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldsNames
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fieldsNames
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
name|fieldsNames
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"fields"
argument_list|,
name|fieldsNames
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|fieldName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
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
name|Object
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
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allTerms
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"all_terms"
argument_list|,
name|allTerms
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
name|lang
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"lang"
argument_list|,
name|lang
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|executionHint
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"execution_hint"
argument_list|,
name|executionHint
argument_list|)
expr_stmt|;
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
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

