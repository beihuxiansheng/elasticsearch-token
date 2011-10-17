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
name|mapper
operator|.
name|MapperService
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
name|mapper
operator|.
name|object
operator|.
name|ObjectMapper
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
name|search
operator|.
name|nested
operator|.
name|NestedChildrenCollector
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
name|search
operator|.
name|nested
operator|.
name|NonNestedDocsFilter
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
name|internal
operator|.
name|ContextIndexSearcher
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

begin_comment
comment|/**  *<pre>  * facets : {  *  facet1: {  *      query : { ... },  *      global : false  *  },  *  facet2: {  *      terms : {  *          name : "myfield",  *          size : 12  *      },  *      global : false  *  }  * }  *</pre>  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|FacetParseElement
specifier|public
class|class
name|FacetParseElement
implements|implements
name|SearchParseElement
block|{
DECL|field|facetProcessors
specifier|private
specifier|final
name|FacetProcessors
name|facetProcessors
decl_stmt|;
DECL|method|FacetParseElement
annotation|@
name|Inject
specifier|public
name|FacetParseElement
parameter_list|(
name|FacetProcessors
name|facetProcessors
parameter_list|)
block|{
name|this
operator|.
name|facetProcessors
operator|=
name|facetProcessors
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
name|String
name|scope
init|=
name|ContextIndexSearcher
operator|.
name|Scopes
operator|.
name|MAIN
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
name|String
name|nestedPath
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
literal|"facet_filter"
operator|.
name|equals
argument_list|(
name|facetFieldName
argument_list|)
operator|||
literal|"facetFilter"
operator|.
name|equals
argument_list|(
name|facetFieldName
argument_list|)
condition|)
block|{
name|filter
operator|=
name|context
operator|.
name|queryParserService
argument_list|()
operator|.
name|parseInnerFilter
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FacetProcessor
name|facetProcessor
init|=
name|facetProcessors
operator|.
name|processor
argument_list|(
name|facetFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|facetProcessor
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
name|facetFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|facet
operator|=
name|facetProcessor
operator|.
name|parse
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
if|if
condition|(
name|parser
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
name|scope
operator|=
name|ContextIndexSearcher
operator|.
name|Scopes
operator|.
name|GLOBAL
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"scope"
operator|.
name|equals
argument_list|(
name|facetFieldName
argument_list|)
operator|||
literal|"_scope"
operator|.
name|equals
argument_list|(
name|facetFieldName
argument_list|)
condition|)
block|{
name|scope
operator|=
name|parser
operator|.
name|text
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
elseif|else
if|if
condition|(
literal|"nested"
operator|.
name|equals
argument_list|(
name|facetFieldName
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
name|nestedPath
operator|!=
literal|null
condition|)
block|{
comment|// its a nested facet, wrap the collector with a facet one...
name|MapperService
operator|.
name|SmartNameObjectMapper
name|mapper
init|=
name|context
operator|.
name|smartNameObjectMapper
argument_list|(
name|nestedPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapper
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
literal|"facet nested path ["
operator|+
name|nestedPath
operator|+
literal|"] not found"
argument_list|)
throw|;
block|}
name|ObjectMapper
name|objectMapper
init|=
name|mapper
operator|.
name|mapper
argument_list|()
decl_stmt|;
if|if
condition|(
name|objectMapper
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
literal|"facet nested path ["
operator|+
name|nestedPath
operator|+
literal|"] not found"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|objectMapper
operator|.
name|nested
argument_list|()
operator|.
name|isNested
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"facet nested path ["
operator|+
name|nestedPath
operator|+
literal|"] is not nested"
argument_list|)
throw|;
block|}
name|facet
operator|=
operator|new
name|NestedChildrenCollector
argument_list|(
name|facet
argument_list|,
name|context
operator|.
name|filterCache
argument_list|()
operator|.
name|cache
argument_list|(
name|NonNestedDocsFilter
operator|.
name|INSTANCE
argument_list|)
argument_list|,
name|context
operator|.
name|filterCache
argument_list|()
operator|.
name|cache
argument_list|(
name|objectMapper
operator|.
name|nestedTypeFilter
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|facet
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
name|topLevelFieldName
operator|+
literal|"]"
argument_list|)
throw|;
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
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|addCollector
argument_list|(
name|scope
argument_list|,
name|facet
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
name|facetCollectors
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

