begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|index
operator|.
name|AtomicReaderContext
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalStateException
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
name|docset
operator|.
name|AllDocIdSet
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
name|docset
operator|.
name|ContextDocIdSet
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
name|*
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
if|if
condition|(
name|context
operator|.
name|facets
argument_list|()
operator|!=
literal|null
operator|&&
name|context
operator|.
name|facets
argument_list|()
operator|.
name|hasQuery
argument_list|()
condition|)
block|{
for|for
control|(
name|SearchContextFacets
operator|.
name|Entry
name|entry
range|:
name|context
operator|.
name|facets
argument_list|()
operator|.
name|entries
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|isGlobal
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|entry
operator|.
name|getMode
argument_list|()
operator|==
name|FacetExecutor
operator|.
name|Mode
operator|.
name|COLLECTOR
condition|)
block|{
comment|// TODO: We can pass the filter as param to collector method, then this filter wrapper logic can
comment|// be moved to NestedFacetExecutor impl, the other implementations would just wrap it into
comment|// FilteredCollector.
name|Collector
name|collector
init|=
name|entry
operator|.
name|getFacetExecutor
argument_list|()
operator|.
name|collector
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getFilter
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|collector
operator|instanceof
name|NestedFacetExecutor
operator|.
name|Collector
condition|)
block|{
comment|// We get rootDoc ids as hits in the collect method, so we need to first translate from
comment|// rootDoc hit to nested doc hit and then apply filter.
name|collector
operator|=
operator|new
name|NestedFacetExecutor
operator|.
name|Collector
argument_list|(
operator|(
name|NestedFacetExecutor
operator|.
name|Collector
operator|)
name|collector
argument_list|,
name|entry
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
comment|// If we would first apply the filter on the rootDoc level and then translate it back to the
comment|// nested docs we ignore the facet filter and all nested docs are passed to facet collector
block|}
else|else
block|{
name|collector
operator|=
operator|new
name|FilteredCollector
argument_list|(
name|collector
argument_list|,
name|entry
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|addMainQueryCollector
argument_list|(
name|collector
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|entry
operator|.
name|getMode
argument_list|()
operator|==
name|FacetExecutor
operator|.
name|Mode
operator|.
name|POST
condition|)
block|{
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|enableMainDocIdSetCollector
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"what mode?"
argument_list|)
throw|;
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
name|ElasticsearchException
block|{
if|if
condition|(
name|context
operator|.
name|facets
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
literal|null
decl_stmt|;
name|List
argument_list|<
name|ContextDocIdSet
argument_list|>
name|globalDocSets
init|=
literal|null
decl_stmt|;
for|for
control|(
name|SearchContextFacets
operator|.
name|Entry
name|entry
range|:
name|context
operator|.
name|facets
argument_list|()
operator|.
name|entries
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|entry
operator|.
name|isGlobal
argument_list|()
condition|)
block|{
if|if
condition|(
name|entry
operator|.
name|getMode
argument_list|()
operator|==
name|FacetExecutor
operator|.
name|Mode
operator|.
name|POST
condition|)
block|{
name|FacetExecutor
operator|.
name|Post
name|post
init|=
name|entry
operator|.
name|getFacetExecutor
argument_list|()
operator|.
name|post
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getFilter
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|post
operator|instanceof
name|NestedFacetExecutor
operator|.
name|Post
condition|)
block|{
name|post
operator|=
operator|new
name|NestedFacetExecutor
operator|.
name|Post
argument_list|(
operator|(
name|NestedFacetExecutor
operator|.
name|Post
operator|)
name|post
argument_list|,
name|entry
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|post
operator|=
operator|new
name|FacetExecutor
operator|.
name|Post
operator|.
name|Filtered
argument_list|(
name|post
argument_list|,
name|entry
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|post
operator|.
name|executePost
argument_list|(
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|mainDocIdSetCollector
argument_list|()
operator|.
name|docSets
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryPhaseExecutionException
argument_list|(
name|context
argument_list|,
literal|"failed to execute facet ["
operator|+
name|entry
operator|.
name|getFacetName
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|entry
operator|.
name|getMode
argument_list|()
operator|==
name|FacetExecutor
operator|.
name|Mode
operator|.
name|POST
condition|)
block|{
if|if
condition|(
name|globalDocSets
operator|==
literal|null
condition|)
block|{
comment|// build global post entries, map a reader context to a live docs docIdSet
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
init|=
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|globalDocSets
operator|=
operator|new
name|ArrayList
argument_list|<
name|ContextDocIdSet
argument_list|>
argument_list|(
name|leaves
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AtomicReaderContext
name|leaf
range|:
name|leaves
control|)
block|{
name|globalDocSets
operator|.
name|add
argument_list|(
operator|new
name|ContextDocIdSet
argument_list|(
name|leaf
argument_list|,
name|BitsFilteredDocIdSet
operator|.
name|wrap
argument_list|(
operator|new
name|AllDocIdSet
argument_list|(
name|leaf
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
argument_list|,
name|leaf
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
argument_list|)
comment|// need to only include live docs
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|FacetExecutor
operator|.
name|Post
name|post
init|=
name|entry
operator|.
name|getFacetExecutor
argument_list|()
operator|.
name|post
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getFilter
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|post
operator|instanceof
name|NestedFacetExecutor
operator|.
name|Post
condition|)
block|{
name|post
operator|=
operator|new
name|NestedFacetExecutor
operator|.
name|Post
argument_list|(
operator|(
name|NestedFacetExecutor
operator|.
name|Post
operator|)
name|post
argument_list|,
name|entry
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|post
operator|=
operator|new
name|FacetExecutor
operator|.
name|Post
operator|.
name|Filtered
argument_list|(
name|post
argument_list|,
name|entry
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|post
operator|.
name|executePost
argument_list|(
name|globalDocSets
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryPhaseExecutionException
argument_list|(
name|context
argument_list|,
literal|"Failed to execute facet ["
operator|+
name|entry
operator|.
name|getFacetName
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|entry
operator|.
name|getMode
argument_list|()
operator|==
name|FacetExecutor
operator|.
name|Mode
operator|.
name|COLLECTOR
condition|)
block|{
name|Filter
name|filter
init|=
name|Queries
operator|.
name|MATCH_ALL_FILTER
decl_stmt|;
name|Collector
name|collector
init|=
name|entry
operator|.
name|getFacetExecutor
argument_list|()
operator|.
name|collector
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getFilter
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|collector
operator|instanceof
name|NestedFacetExecutor
operator|.
name|Collector
condition|)
block|{
name|collector
operator|=
operator|new
name|NestedFacetExecutor
operator|.
name|Collector
argument_list|(
operator|(
name|NestedFacetExecutor
operator|.
name|Collector
operator|)
name|collector
argument_list|,
name|entry
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collector
operator|=
operator|new
name|FilteredCollector
argument_list|(
name|collector
argument_list|,
name|entry
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|filtersByCollector
operator|==
literal|null
condition|)
block|{
name|filtersByCollector
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
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
block|}
comment|// optimize the global collector based execution
if|if
condition|(
name|filtersByCollector
operator|!=
literal|null
condition|)
block|{
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
name|searchFilter
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
name|Exception
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
for|for
control|(
name|Collector
name|collector
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
if|if
condition|(
name|collector
operator|instanceof
name|XCollector
condition|)
block|{
operator|(
operator|(
name|XCollector
operator|)
name|collector
operator|)
operator|.
name|postCollection
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
name|List
argument_list|<
name|Facet
argument_list|>
name|facets
init|=
operator|new
name|ArrayList
argument_list|<
name|Facet
argument_list|>
argument_list|(
name|context
operator|.
name|facets
argument_list|()
operator|.
name|entries
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SearchContextFacets
operator|.
name|Entry
name|entry
range|:
name|context
operator|.
name|facets
argument_list|()
operator|.
name|entries
argument_list|()
control|)
block|{
name|facets
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getFacetExecutor
argument_list|()
operator|.
name|buildFacet
argument_list|(
name|entry
operator|.
name|getFacetName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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

