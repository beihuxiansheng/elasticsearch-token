begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.fetch.subphase
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
operator|.
name|subphase
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
name|search
operator|.
name|CollectionTerminatedException
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
name|ConjunctionDISI
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
name|DocIdSetIterator
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
name|LeafCollector
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Scorer
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
name|ScorerSupplier
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
name|TopDocs
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
name|Weight
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
name|search
operator|.
name|SearchHit
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Context used for inner hits retrieval  */
end_comment

begin_class
DECL|class|InnerHitsContext
specifier|public
specifier|final
class|class
name|InnerHitsContext
block|{
DECL|field|innerHits
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|InnerHitSubContext
argument_list|>
name|innerHits
decl_stmt|;
DECL|method|InnerHitsContext
specifier|public
name|InnerHitsContext
parameter_list|()
block|{
name|this
operator|.
name|innerHits
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|InnerHitsContext
name|InnerHitsContext
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|InnerHitSubContext
argument_list|>
name|innerHits
parameter_list|)
block|{
name|this
operator|.
name|innerHits
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|innerHits
argument_list|)
expr_stmt|;
block|}
DECL|method|getInnerHits
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|InnerHitSubContext
argument_list|>
name|getInnerHits
parameter_list|()
block|{
return|return
name|innerHits
return|;
block|}
DECL|method|addInnerHitDefinition
specifier|public
name|void
name|addInnerHitDefinition
parameter_list|(
name|InnerHitSubContext
name|innerHit
parameter_list|)
block|{
if|if
condition|(
name|innerHits
operator|.
name|containsKey
argument_list|(
name|innerHit
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"inner_hit definition with the name ["
operator|+
name|innerHit
operator|.
name|getName
argument_list|()
operator|+
literal|"] already exists. Use a different inner_hit name or define one explicitly"
argument_list|)
throw|;
block|}
name|innerHits
operator|.
name|put
argument_list|(
name|innerHit
operator|.
name|getName
argument_list|()
argument_list|,
name|innerHit
argument_list|)
expr_stmt|;
block|}
comment|/**      * A {@link SubSearchContext} that associates {@link TopDocs} to each {@link SearchHit}      * in the parent search context      */
DECL|class|InnerHitSubContext
specifier|public
specifier|abstract
specifier|static
class|class
name|InnerHitSubContext
extends|extends
name|SubSearchContext
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|context
specifier|protected
specifier|final
name|SearchContext
name|context
decl_stmt|;
DECL|field|childInnerHits
specifier|private
name|InnerHitsContext
name|childInnerHits
decl_stmt|;
DECL|method|InnerHitSubContext
specifier|protected
name|InnerHitSubContext
parameter_list|(
name|String
name|name
parameter_list|,
name|SearchContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
DECL|method|topDocs
specifier|public
specifier|abstract
name|TopDocs
index|[]
name|topDocs
parameter_list|(
name|SearchHit
index|[]
name|hits
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|innerHits
specifier|public
name|InnerHitsContext
name|innerHits
parameter_list|()
block|{
return|return
name|childInnerHits
return|;
block|}
DECL|method|setChildInnerHits
specifier|public
name|void
name|setChildInnerHits
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|InnerHitSubContext
argument_list|>
name|childInnerHits
parameter_list|)
block|{
name|this
operator|.
name|childInnerHits
operator|=
operator|new
name|InnerHitsContext
argument_list|(
name|childInnerHits
argument_list|)
expr_stmt|;
block|}
DECL|method|createInnerHitQueryWeight
specifier|protected
name|Weight
name|createInnerHitQueryWeight
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|boolean
name|needsScores
init|=
name|size
argument_list|()
operator|!=
literal|0
operator|&&
operator|(
name|sort
argument_list|()
operator|==
literal|null
operator|||
name|sort
argument_list|()
operator|.
name|sort
operator|.
name|needsScores
argument_list|()
operator|)
decl_stmt|;
return|return
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|()
argument_list|,
name|needsScores
argument_list|)
return|;
block|}
DECL|method|parentSearchContext
specifier|public
name|SearchContext
name|parentSearchContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
block|}
DECL|method|intersect
specifier|public
specifier|static
name|void
name|intersect
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Weight
name|innerHitQueryWeight
parameter_list|,
name|Collector
name|collector
parameter_list|,
name|LeafReaderContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|ScorerSupplier
name|scorerSupplier
init|=
name|weight
operator|.
name|scorerSupplier
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorerSupplier
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// use random access since this scorer will be consumed on a minority of documents
name|Scorer
name|scorer
init|=
name|scorerSupplier
operator|.
name|get
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|ScorerSupplier
name|innerHitQueryScorerSupplier
init|=
name|innerHitQueryWeight
operator|.
name|scorerSupplier
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerHitQueryScorerSupplier
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// use random access since this scorer will be consumed on a minority of documents
name|Scorer
name|innerHitQueryScorer
init|=
name|innerHitQueryScorerSupplier
operator|.
name|get
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|LeafCollector
name|leafCollector
decl_stmt|;
try|try
block|{
name|leafCollector
operator|=
name|collector
operator|.
name|getLeafCollector
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
comment|// Just setting the innerHitQueryScorer is ok, because that is the actual scoring part of the query
name|leafCollector
operator|.
name|setScorer
argument_list|(
name|innerHitQueryScorer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CollectionTerminatedException
name|e
parameter_list|)
block|{
return|return;
block|}
try|try
block|{
name|Bits
name|acceptDocs
init|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
name|DocIdSetIterator
name|iterator
init|=
name|ConjunctionDISI
operator|.
name|intersectIterators
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|innerHitQueryScorer
operator|.
name|iterator
argument_list|()
argument_list|,
name|scorer
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|docId
init|=
name|iterator
operator|.
name|nextDoc
argument_list|()
init|;
name|docId
operator|<
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|docId
operator|=
name|iterator
operator|.
name|nextDoc
argument_list|()
control|)
block|{
if|if
condition|(
name|acceptDocs
operator|==
literal|null
operator|||
name|acceptDocs
operator|.
name|get
argument_list|(
name|docId
argument_list|)
condition|)
block|{
name|leafCollector
operator|.
name|collect
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|CollectionTerminatedException
name|e
parameter_list|)
block|{
comment|// ignore and continue
block|}
block|}
block|}
end_class

end_unit

