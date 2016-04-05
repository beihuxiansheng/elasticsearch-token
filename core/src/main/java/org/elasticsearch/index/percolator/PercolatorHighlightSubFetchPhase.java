begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.percolator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|percolator
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
name|LeafReaderContext
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
name|ReaderUtil
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
name|BooleanClause
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
name|BooleanQuery
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
name|BoostQuery
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
name|ConstantScoreQuery
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
name|IndexSearcher
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
name|common
operator|.
name|bytes
operator|.
name|BytesReference
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
name|text
operator|.
name|Text
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
name|ParsedQuery
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
name|PercolatorQuery
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
name|fetch
operator|.
name|FetchSubPhase
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
name|highlight
operator|.
name|HighlightPhase
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
name|highlight
operator|.
name|SearchContextHighlight
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
name|InternalSearchHit
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
name|internal
operator|.
name|SubSearchContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
comment|// Highlighting in the case of the percolator query is a bit different, because the PercolatorQuery itself doesn't get highlighted,
end_comment

begin_comment
comment|// but the source of the PercolatorQuery gets highlighted by each hit with type '.percolator' (percolator queries).
end_comment

begin_class
DECL|class|PercolatorHighlightSubFetchPhase
specifier|public
class|class
name|PercolatorHighlightSubFetchPhase
implements|implements
name|FetchSubPhase
block|{
DECL|field|highlightPhase
specifier|private
specifier|final
name|HighlightPhase
name|highlightPhase
decl_stmt|;
annotation|@
name|Inject
DECL|method|PercolatorHighlightSubFetchPhase
specifier|public
name|PercolatorHighlightSubFetchPhase
parameter_list|(
name|HighlightPhase
name|highlightPhase
parameter_list|)
block|{
name|this
operator|.
name|highlightPhase
operator|=
name|highlightPhase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hitsExecutionNeeded
specifier|public
name|boolean
name|hitsExecutionNeeded
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
return|return
name|context
operator|.
name|highlight
argument_list|()
operator|!=
literal|null
operator|&&
name|locatePercolatorQuery
argument_list|(
name|context
operator|.
name|query
argument_list|()
argument_list|)
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hitsExecute
specifier|public
name|void
name|hitsExecute
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|InternalSearchHit
index|[]
name|hits
parameter_list|)
block|{
name|PercolatorQuery
name|percolatorQuery
init|=
name|locatePercolatorQuery
argument_list|(
name|context
operator|.
name|query
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|percolatorQuery
operator|==
literal|null
condition|)
block|{
comment|// shouldn't happen as we checked for the existence of a percolator query in hitsExecutionNeeded(...)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"couldn't locate percolator query"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|ctxs
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
name|PercolatorQueryCache
name|queriesRegistry
init|=
name|context
operator|.
name|percolatorQueryCache
argument_list|()
decl_stmt|;
name|IndexSearcher
name|percolatorIndexSearcher
init|=
name|percolatorQuery
operator|.
name|getPercolatorIndexSearcher
argument_list|()
decl_stmt|;
name|LeafReaderContext
name|percolatorLeafReaderContext
init|=
name|percolatorIndexSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|FetchSubPhase
operator|.
name|HitContext
name|hitContext
init|=
operator|new
name|FetchSubPhase
operator|.
name|HitContext
argument_list|()
decl_stmt|;
name|SubSearchContext
name|subSearchContext
init|=
name|createSubSearchContext
argument_list|(
name|context
argument_list|,
name|percolatorLeafReaderContext
argument_list|,
name|percolatorQuery
operator|.
name|getDocumentSource
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|InternalSearchHit
name|hit
range|:
name|hits
control|)
block|{
name|LeafReaderContext
name|ctx
init|=
name|ctxs
operator|.
name|get
argument_list|(
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|hit
operator|.
name|docId
argument_list|()
argument_list|,
name|ctxs
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|segmentDocId
init|=
name|hit
operator|.
name|docId
argument_list|()
operator|-
name|ctx
operator|.
name|docBase
decl_stmt|;
name|Query
name|query
init|=
name|queriesRegistry
operator|.
name|getQueries
argument_list|(
name|ctx
argument_list|)
operator|.
name|getQuery
argument_list|(
name|segmentDocId
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|subSearchContext
operator|.
name|parsedQuery
argument_list|(
operator|new
name|ParsedQuery
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
name|hitContext
operator|.
name|reset
argument_list|(
operator|new
name|InternalSearchHit
argument_list|(
literal|0
argument_list|,
literal|"unknown"
argument_list|,
operator|new
name|Text
argument_list|(
name|percolatorQuery
operator|.
name|getDocumentType
argument_list|()
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
argument_list|,
name|percolatorLeafReaderContext
argument_list|,
literal|0
argument_list|,
name|percolatorIndexSearcher
argument_list|)
expr_stmt|;
name|hitContext
operator|.
name|cache
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|highlightPhase
operator|.
name|hitExecute
argument_list|(
name|subSearchContext
argument_list|,
name|hitContext
argument_list|)
expr_stmt|;
name|hit
operator|.
name|highlightFields
argument_list|()
operator|.
name|putAll
argument_list|(
name|hitContext
operator|.
name|hit
argument_list|()
operator|.
name|getHighlightFields
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hitExecutionNeeded
specifier|public
name|boolean
name|hitExecutionNeeded
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hitExecute
specifier|public
name|void
name|hitExecute
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|HitContext
name|hitContext
parameter_list|)
block|{     }
DECL|method|locatePercolatorQuery
specifier|static
name|PercolatorQuery
name|locatePercolatorQuery
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
if|if
condition|(
name|query
operator|instanceof
name|PercolatorQuery
condition|)
block|{
return|return
operator|(
name|PercolatorQuery
operator|)
name|query
return|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|BooleanQuery
condition|)
block|{
for|for
control|(
name|BooleanClause
name|clause
range|:
operator|(
operator|(
name|BooleanQuery
operator|)
name|query
operator|)
operator|.
name|clauses
argument_list|()
control|)
block|{
name|PercolatorQuery
name|result
init|=
name|locatePercolatorQuery
argument_list|(
name|clause
operator|.
name|getQuery
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|ConstantScoreQuery
condition|)
block|{
return|return
name|locatePercolatorQuery
argument_list|(
operator|(
operator|(
name|ConstantScoreQuery
operator|)
name|query
operator|)
operator|.
name|getQuery
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|query
operator|instanceof
name|BoostQuery
condition|)
block|{
return|return
name|locatePercolatorQuery
argument_list|(
operator|(
operator|(
name|BoostQuery
operator|)
name|query
operator|)
operator|.
name|getQuery
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|createSubSearchContext
specifier|private
name|SubSearchContext
name|createSubSearchContext
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|LeafReaderContext
name|leafReaderContext
parameter_list|,
name|BytesReference
name|source
parameter_list|)
block|{
name|SubSearchContext
name|subSearchContext
init|=
operator|new
name|SubSearchContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|subSearchContext
operator|.
name|highlight
argument_list|(
operator|new
name|SearchContextHighlight
argument_list|(
name|context
operator|.
name|highlight
argument_list|()
operator|.
name|fields
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Enforce highlighting by source, because MemoryIndex doesn't support stored fields.
name|subSearchContext
operator|.
name|highlight
argument_list|()
operator|.
name|globalForceSource
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|subSearchContext
operator|.
name|lookup
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|setSegmentAndDocument
argument_list|(
name|leafReaderContext
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|subSearchContext
operator|.
name|lookup
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|setSource
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|subSearchContext
return|;
block|}
block|}
end_class

end_unit

