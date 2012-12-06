begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|google
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
name|com
operator|.
name|google
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Collector
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
name|Filter
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
name|MultiCollector
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
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|lucene
operator|.
name|search
operator|.
name|Queries
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
name|search
operator|.
name|XConstantScoreQuery
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
name|search
operator|.
name|XFilteredQuery
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
name|BlockJoinQuery
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
name|SearchPhase
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|query
operator|.
name|QueryPhaseExecutionException
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
name|List
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
comment|/**  *  */
end_comment

begin_class
DECL|class|FacetPhase
specifier|public
class|class
name|FacetPhase
implements|implements
name|SearchPhase
block|{
DECL|field|facetParseElement
specifier|private
specifier|final
name|FacetParseElement
name|facetParseElement
decl_stmt|;
DECL|field|facetBinaryParseElement
specifier|private
specifier|final
name|FacetBinaryParseElement
name|facetBinaryParseElement
decl_stmt|;
annotation|@
name|Inject
DECL|method|FacetPhase
specifier|public
name|FacetPhase
parameter_list|(
name|FacetParseElement
name|facetParseElement
parameter_list|,
name|FacetBinaryParseElement
name|facetBinaryParseElement
parameter_list|)
block|{
name|this
operator|.
name|facetParseElement
operator|=
name|facetParseElement
expr_stmt|;
name|this
operator|.
name|facetBinaryParseElement
operator|=
name|facetBinaryParseElement
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseElements
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|SearchParseElement
argument_list|>
name|parseElements
parameter_list|()
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"facets"
argument_list|,
name|facetParseElement
argument_list|,
literal|"facets_binary"
argument_list|,
name|facetBinaryParseElement
argument_list|,
literal|"facetsBinary"
argument_list|,
name|facetBinaryParseElement
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|preProcess
specifier|public
name|void
name|preProcess
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
comment|// add specific facets to nested queries...
if|if
condition|(
name|context
operator|.
name|nestedQueries
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|BlockJoinQuery
argument_list|>
name|entry
range|:
name|context
operator|.
name|nestedQueries
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|Collector
argument_list|>
name|collectors
init|=
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|removeCollectors
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|collectors
operator|!=
literal|null
operator|&&
operator|!
name|collectors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|collectors
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|setCollector
argument_list|(
name|collectors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|setCollector
argument_list|(
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|collectors
operator|.
name|toArray
argument_list|(
operator|new
name|Collector
index|[
name|collectors
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|SearchContext
name|context
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
name|context
operator|.
name|facets
argument_list|()
operator|==
literal|null
operator|||
name|context
operator|.
name|facets
argument_list|()
operator|.
name|facetCollectors
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|context
operator|.
name|queryResult
argument_list|()
operator|.
name|facets
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// no need to compute the facets twice, they should be computed on a per context basis
return|return;
block|}
comment|// optimize global facet execution, based on filters (don't iterate over all docs), and check
comment|// if we have special facets that can be optimized for all execution, do it
name|List
argument_list|<
name|Collector
argument_list|>
name|collectors
init|=
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|removeCollectors
argument_list|(
name|ContextIndexSearcher
operator|.
name|Scopes
operator|.
name|GLOBAL
argument_list|)
decl_stmt|;
if|if
condition|(
name|collectors
operator|!=
literal|null
operator|&&
operator|!
name|collectors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Map
argument_list|<
name|Filter
argument_list|,
name|List
argument_list|<
name|Collector
argument_list|>
argument_list|>
name|filtersByCollector
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Collector
name|collector
range|:
name|collectors
control|)
block|{
if|if
condition|(
name|collector
operator|instanceof
name|OptimizeGlobalFacetCollector
condition|)
block|{
try|try
block|{
operator|(
operator|(
name|OptimizeGlobalFacetCollector
operator|)
name|collector
operator|)
operator|.
name|optimizedGlobalExecution
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryPhaseExecutionException
argument_list|(
name|context
argument_list|,
literal|"Failed to execute global facets"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|Filter
name|filter
init|=
name|Queries
operator|.
name|MATCH_ALL_FILTER
decl_stmt|;
if|if
condition|(
name|collector
operator|instanceof
name|AbstractFacetCollector
condition|)
block|{
name|AbstractFacetCollector
name|facetCollector
init|=
operator|(
name|AbstractFacetCollector
operator|)
name|collector
decl_stmt|;
if|if
condition|(
name|facetCollector
operator|.
name|getFilter
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// we can clear the filter, since we are anyhow going to iterate over it
comment|// so no need to double check it...
name|filter
operator|=
name|facetCollector
operator|.
name|getAndClearFilter
argument_list|()
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|Collector
argument_list|>
name|list
init|=
name|filtersByCollector
operator|.
name|get
argument_list|(
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|<
name|Collector
argument_list|>
argument_list|()
expr_stmt|;
name|filtersByCollector
operator|.
name|put
argument_list|(
name|filter
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|collector
argument_list|)
expr_stmt|;
block|}
block|}
comment|// now, go and execute the filters->collector ones
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Filter
argument_list|,
name|List
argument_list|<
name|Collector
argument_list|>
argument_list|>
name|entry
range|:
name|filtersByCollector
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Filter
name|filter
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|XConstantScoreQuery
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|Filter
name|searchFilter
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|searchFilter
argument_list|(
name|context
operator|.
name|types
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|searchFilter
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
operator|new
name|XFilteredQuery
argument_list|(
name|query
argument_list|,
name|context
operator|.
name|filterCache
argument_list|()
operator|.
name|cache
argument_list|(
name|searchFilter
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|Collector
index|[
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryPhaseExecutionException
argument_list|(
name|context
argument_list|,
literal|"Failed to execute global facets"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
name|SearchContextFacets
name|contextFacets
init|=
name|context
operator|.
name|facets
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Facet
argument_list|>
name|facets
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|contextFacets
operator|.
name|facetCollectors
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|FacetCollector
name|facetCollector
range|:
name|contextFacets
operator|.
name|facetCollectors
argument_list|()
control|)
block|{
name|facets
operator|.
name|add
argument_list|(
name|facetCollector
operator|.
name|facet
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|context
operator|.
name|queryResult
argument_list|()
operator|.
name|facets
argument_list|(
operator|new
name|InternalFacets
argument_list|(
name|facets
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

