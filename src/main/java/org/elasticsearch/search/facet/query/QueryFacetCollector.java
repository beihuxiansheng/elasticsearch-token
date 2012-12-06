begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Bits
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
name|DocIdSets
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
name|cache
operator|.
name|filter
operator|.
name|FilterCache
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
name|AbstractFacetCollector
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
name|Facet
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
name|OptimizeGlobalFacetCollector
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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|QueryFacetCollector
specifier|public
class|class
name|QueryFacetCollector
extends|extends
name|AbstractFacetCollector
implements|implements
name|OptimizeGlobalFacetCollector
block|{
DECL|field|query
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
DECL|field|filter
specifier|private
specifier|final
name|Filter
name|filter
decl_stmt|;
DECL|field|bits
specifier|private
name|Bits
name|bits
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
init|=
literal|0
decl_stmt|;
DECL|method|QueryFacetCollector
specifier|public
name|QueryFacetCollector
parameter_list|(
name|String
name|facetName
parameter_list|,
name|Query
name|query
parameter_list|,
name|FilterCache
name|filterCache
parameter_list|)
block|{
name|super
argument_list|(
name|facetName
argument_list|)
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|Filter
name|possibleFilter
init|=
name|extractFilterIfApplicable
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|possibleFilter
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|filter
operator|=
name|possibleFilter
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|filter
operator|=
operator|new
name|QueryWrapperFilter
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|bits
operator|=
name|DocIdSets
operator|.
name|toSafeBits
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doCollect
specifier|protected
name|void
name|doCollect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|optimizedGlobalExecution
specifier|public
name|void
name|optimizedGlobalExecution
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|query
init|=
name|this
operator|.
name|query
decl_stmt|;
if|if
condition|(
name|super
operator|.
name|filter
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
name|super
operator|.
name|filter
argument_list|)
expr_stmt|;
block|}
name|Filter
name|searchFilter
init|=
name|searchContext
operator|.
name|mapperService
argument_list|()
operator|.
name|searchFilter
argument_list|(
name|searchContext
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
name|searchContext
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
name|TotalHitCountCollector
name|collector
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
name|searchContext
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|count
operator|=
name|collector
operator|.
name|getTotalHits
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|facet
specifier|public
name|Facet
name|facet
parameter_list|()
block|{
return|return
operator|new
name|InternalQueryFacet
argument_list|(
name|facetName
argument_list|,
name|count
argument_list|)
return|;
block|}
comment|/**      * If its a filtered query with a match all, then we just need the inner filter.      */
DECL|method|extractFilterIfApplicable
specifier|private
name|Filter
name|extractFilterIfApplicable
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
if|if
condition|(
name|query
operator|instanceof
name|XFilteredQuery
condition|)
block|{
name|XFilteredQuery
name|fQuery
init|=
operator|(
name|XFilteredQuery
operator|)
name|query
decl_stmt|;
if|if
condition|(
name|Queries
operator|.
name|isConstantMatchAllQuery
argument_list|(
name|fQuery
operator|.
name|getQuery
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|fQuery
operator|.
name|getFilter
argument_list|()
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|XConstantScoreQuery
condition|)
block|{
return|return
operator|(
operator|(
name|XConstantScoreQuery
operator|)
name|query
operator|)
operator|.
name|getFilter
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|ConstantScoreQuery
condition|)
block|{
name|ConstantScoreQuery
name|constantScoreQuery
init|=
operator|(
name|ConstantScoreQuery
operator|)
name|query
decl_stmt|;
if|if
condition|(
name|constantScoreQuery
operator|.
name|getFilter
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|constantScoreQuery
operator|.
name|getFilter
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

