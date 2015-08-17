begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.dfs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|dfs
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectObjectHashMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectHashSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectCursor
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
name|ImmutableMap
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
name|IndexReaderContext
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
name|Term
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
name|TermContext
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
name|CollectionStatistics
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
name|TermStatistics
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
name|HppcMaps
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
name|rescore
operator|.
name|RescoreSearchContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
DECL|class|DfsPhase
specifier|public
class|class
name|DfsPhase
implements|implements
name|SearchPhase
block|{
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
argument_list|()
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
block|{     }
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
block|{
specifier|final
name|ObjectHashSet
argument_list|<
name|Term
argument_list|>
name|termsSet
init|=
operator|new
name|ObjectHashSet
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|createNormalizedWeight
argument_list|(
name|context
operator|.
name|query
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|extractTerms
argument_list|(
operator|new
name|DelegateSet
argument_list|(
name|termsSet
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|RescoreSearchContext
name|rescoreContext
range|:
name|context
operator|.
name|rescore
argument_list|()
control|)
block|{
name|rescoreContext
operator|.
name|rescorer
argument_list|()
operator|.
name|extractTerms
argument_list|(
name|context
argument_list|,
name|rescoreContext
argument_list|,
operator|new
name|DelegateSet
argument_list|(
name|termsSet
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Term
index|[]
name|terms
init|=
name|termsSet
operator|.
name|toArray
argument_list|(
name|Term
operator|.
name|class
argument_list|)
decl_stmt|;
name|TermStatistics
index|[]
name|termStatistics
init|=
operator|new
name|TermStatistics
index|[
name|terms
operator|.
name|length
index|]
decl_stmt|;
name|IndexReaderContext
name|indexReaderContext
init|=
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// LUCENE 4 UPGRADE: cache TermContext?
name|TermContext
name|termContext
init|=
name|TermContext
operator|.
name|build
argument_list|(
name|indexReaderContext
argument_list|,
name|terms
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|termStatistics
index|[
name|i
index|]
operator|=
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|termStatistics
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|,
name|termContext
argument_list|)
expr_stmt|;
block|}
name|ObjectObjectHashMap
argument_list|<
name|String
argument_list|,
name|CollectionStatistics
argument_list|>
name|fieldStatistics
init|=
name|HppcMaps
operator|.
name|newNoNullKeysMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|terms
control|)
block|{
assert|assert
name|term
operator|.
name|field
argument_list|()
operator|!=
literal|null
operator|:
literal|"field is null"
assert|;
if|if
condition|(
operator|!
name|fieldStatistics
operator|.
name|containsKey
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|CollectionStatistics
name|collectionStatistics
init|=
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|collectionStatistics
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
name|fieldStatistics
operator|.
name|put
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|,
name|collectionStatistics
argument_list|)
expr_stmt|;
block|}
block|}
name|context
operator|.
name|dfsResult
argument_list|()
operator|.
name|termsStatistics
argument_list|(
name|terms
argument_list|,
name|termStatistics
argument_list|)
operator|.
name|fieldStatistics
argument_list|(
name|fieldStatistics
argument_list|)
operator|.
name|maxDoc
argument_list|(
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
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
name|DfsPhaseExecutionException
argument_list|(
name|context
argument_list|,
literal|"Exception during dfs phase"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|termsSet
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// don't hold on to terms
block|}
block|}
comment|// We need to bridge to JCF world, b/c of Query#extractTerms
DECL|class|DelegateSet
specifier|private
specifier|static
class|class
name|DelegateSet
extends|extends
name|AbstractSet
argument_list|<
name|Term
argument_list|>
block|{
DECL|field|delegate
specifier|private
specifier|final
name|ObjectHashSet
argument_list|<
name|Term
argument_list|>
name|delegate
decl_stmt|;
DECL|method|DelegateSet
specifier|private
name|DelegateSet
parameter_list|(
name|ObjectHashSet
argument_list|<
name|Term
argument_list|>
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|add
argument_list|(
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|addAll
specifier|public
name|boolean
name|addAll
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|terms
control|)
block|{
name|result
operator|=
name|delegate
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Term
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|ObjectCursor
argument_list|<
name|Term
argument_list|>
argument_list|>
name|iterator
init|=
name|delegate
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|Term
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Term
name|next
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|next
argument_list|()
operator|.
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|size
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

