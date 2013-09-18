begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
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
name|Filter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|inject
operator|.
name|Inject
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
name|XContentParser
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
name|ParsedFilter
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
name|SearchParseElement
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
name|SearchParseException
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
name|nested
operator|.
name|NestedFacetExecutor
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
name|internal
operator|.
name|SearchContext
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
name|List
import|;
end_import

begin_comment
comment|/**  *<pre>  * facets : {  *  facet1: {  *      query : { ... },  *      global : false  *  },  *  facet2: {  *      terms : {  *          name : "myfield",  *          size : 12  *      },  *      global : false  *  }  * }  *</pre>  */
end_comment

begin_class
DECL|class|FacetParseElement
specifier|public
class|class
name|FacetParseElement
implements|implements
name|SearchParseElement
block|{
DECL|field|facetParsers
specifier|private
specifier|final
name|FacetParsers
name|facetParsers
decl_stmt|;
annotation|@
name|Inject
DECL|method|FacetParseElement
specifier|public
name|FacetParseElement
parameter_list|(
name|FacetParsers
name|facetParsers
parameter_list|)
block|{
name|this
operator|.
name|facetParsers
operator|=
name|facetParsers
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|void
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|List
argument_list|<
name|SearchContextFacets
operator|.
name|Entry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<
name|SearchContextFacets
operator|.
name|Entry
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|facetName
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|facetName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|FacetExecutor
name|facetExecutor
init|=
literal|null
decl_stmt|;
name|boolean
name|global
init|=
literal|false
decl_stmt|;
name|FacetExecutor
operator|.
name|Mode
name|defaultMainMode
init|=
literal|null
decl_stmt|;
name|FacetExecutor
operator|.
name|Mode
name|defaultGlobalMode
init|=
literal|null
decl_stmt|;
name|FacetExecutor
operator|.
name|Mode
name|mode
init|=
literal|null
decl_stmt|;
name|Filter
name|filter
init|=
literal|null
decl_stmt|;
name|boolean
name|cacheFilter
init|=
literal|false
decl_stmt|;
name|String
name|nestedPath
init|=
literal|null
decl_stmt|;
name|String
name|fieldName
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|fieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
if|if
condition|(
literal|"facet_filter"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"facetFilter"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|ParsedFilter
name|parsedFilter
init|=
name|context
operator|.
name|queryParserService
argument_list|()
operator|.
name|parseInnerFilter
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|filter
operator|=
name|parsedFilter
operator|==
literal|null
condition|?
literal|null
else|:
name|parsedFilter
operator|.
name|filter
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|FacetParser
name|facetParser
init|=
name|facetParsers
operator|.
name|parser
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|facetParser
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"No facet type found for ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|facetExecutor
operator|=
name|facetParser
operator|.
name|parse
argument_list|(
name|facetName
argument_list|,
name|parser
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|defaultMainMode
operator|=
name|facetParser
operator|.
name|defaultMainMode
argument_list|()
expr_stmt|;
name|defaultGlobalMode
operator|=
name|facetParser
operator|.
name|defaultGlobalMode
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"global"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|global
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"mode"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|String
name|modeAsText
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"collector"
operator|.
name|equals
argument_list|(
name|modeAsText
argument_list|)
condition|)
block|{
name|mode
operator|=
name|FacetExecutor
operator|.
name|Mode
operator|.
name|COLLECTOR
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"post"
operator|.
name|equals
argument_list|(
name|modeAsText
argument_list|)
condition|)
block|{
name|mode
operator|=
name|FacetExecutor
operator|.
name|Mode
operator|.
name|POST
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"failed to parse facet mode ["
operator|+
name|modeAsText
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"scope"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"_scope"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"the [scope] support in facets have been removed"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
literal|"cache_filter"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"cacheFilter"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|cacheFilter
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"nested"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|nestedPath
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|cacheFilter
condition|)
block|{
name|filter
operator|=
name|context
operator|.
name|filterCache
argument_list|()
operator|.
name|cache
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|facetExecutor
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"no facet type found for facet named ["
operator|+
name|facetName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|nestedPath
operator|!=
literal|null
condition|)
block|{
name|facetExecutor
operator|=
operator|new
name|NestedFacetExecutor
argument_list|(
name|facetExecutor
argument_list|,
name|context
argument_list|,
name|nestedPath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mode
operator|==
literal|null
condition|)
block|{
name|mode
operator|=
name|global
condition|?
name|defaultGlobalMode
else|:
name|defaultMainMode
expr_stmt|;
block|}
name|entries
operator|.
name|add
argument_list|(
operator|new
name|SearchContextFacets
operator|.
name|Entry
argument_list|(
name|facetName
argument_list|,
name|mode
argument_list|,
name|facetExecutor
argument_list|,
name|global
argument_list|,
name|filter
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|context
operator|.
name|facets
argument_list|(
operator|new
name|SearchContextFacets
argument_list|(
name|entries
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

