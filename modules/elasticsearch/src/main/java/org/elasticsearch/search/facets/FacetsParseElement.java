begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facets
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facets
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
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|collect
operator|.
name|Lists
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
name|collect
operator|.
name|MapBuilder
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
name|xcontent
operator|.
name|XContentIndexQueryParser
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
name|facets
operator|.
name|collector
operator|.
name|FacetCollector
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
name|collector
operator|.
name|FacetCollectorParser
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
name|histogram
operator|.
name|HistogramFacetCollectorParser
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
name|query
operator|.
name|QueryFacetCollectorParser
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
name|statistical
operator|.
name|StatisticalFacetCollectorParser
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
name|terms
operator|.
name|TermsFacetCollectorParser
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
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|MapBuilder
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *<pre>  * facets : {  *  facet1: {  *      query : { ... },  *      global : false  *  },  *  facet2: {  *      terms : {  *          name : "myfield",  *          size : 12  *      },  *      global : false  *  }  * }  *</pre>  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|FacetsParseElement
specifier|public
class|class
name|FacetsParseElement
implements|implements
name|SearchParseElement
block|{
DECL|field|facetCollectorParsers
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|FacetCollectorParser
argument_list|>
name|facetCollectorParsers
decl_stmt|;
DECL|method|FacetsParseElement
specifier|public
name|FacetsParseElement
parameter_list|()
block|{
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|FacetCollectorParser
argument_list|>
name|builder
init|=
name|newMapBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|TermsFacetCollectorParser
operator|.
name|NAME
argument_list|,
operator|new
name|TermsFacetCollectorParser
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|QueryFacetCollectorParser
operator|.
name|NAME
argument_list|,
operator|new
name|QueryFacetCollectorParser
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|StatisticalFacetCollectorParser
operator|.
name|NAME
argument_list|,
operator|new
name|StatisticalFacetCollectorParser
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|HistogramFacetCollectorParser
operator|.
name|NAME
argument_list|,
operator|new
name|HistogramFacetCollectorParser
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|facetCollectorParsers
operator|=
name|builder
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
DECL|method|parse
annotation|@
name|Override
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
name|FacetCollector
argument_list|>
name|facetCollectors
init|=
literal|null
decl_stmt|;
name|String
name|topLevelFieldName
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
name|topLevelFieldName
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
name|FacetCollector
name|facet
init|=
literal|null
decl_stmt|;
name|boolean
name|global
init|=
literal|false
decl_stmt|;
name|String
name|facetFieldName
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
literal|true
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
name|facetFieldName
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
literal|"filter"
operator|.
name|equals
argument_list|(
name|facetFieldName
argument_list|)
condition|)
block|{
name|XContentIndexQueryParser
name|indexQueryParser
init|=
operator|(
name|XContentIndexQueryParser
operator|)
name|context
operator|.
name|queryParser
argument_list|()
decl_stmt|;
name|filter
operator|=
name|indexQueryParser
operator|.
name|parseInnerFilter
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FacetCollectorParser
name|facetCollectorParser
init|=
name|facetCollectorParsers
operator|.
name|get
argument_list|(
name|facetFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|facetCollectorParser
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
literal|"No facet type for ["
operator|+
name|facetFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|facet
operator|=
name|facetCollectorParser
operator|.
name|parser
argument_list|(
name|topLevelFieldName
argument_list|,
name|parser
argument_list|,
name|context
argument_list|)
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
name|facetFieldName
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
literal|"cache_filter"
operator|.
name|equals
argument_list|(
name|facetFieldName
argument_list|)
operator|||
literal|"cacheFilter"
operator|.
name|equals
argument_list|(
name|facetFieldName
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
name|facet
operator|.
name|setFilter
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|facetCollectors
operator|==
literal|null
condition|)
block|{
name|facetCollectors
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
block|}
name|facetCollectors
operator|.
name|add
argument_list|(
name|facet
argument_list|)
expr_stmt|;
if|if
condition|(
name|global
condition|)
block|{
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|addGlobalCollector
argument_list|(
name|facet
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|addCollector
argument_list|(
name|facet
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|context
operator|.
name|facets
argument_list|(
operator|new
name|SearchContextFacets
argument_list|(
name|facetCollectors
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

