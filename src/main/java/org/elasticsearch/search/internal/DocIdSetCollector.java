begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
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
name|util
operator|.
name|FixedBitSet
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
name|XCollector
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
name|docset
operator|.
name|DocSetCache
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|DocIdSetCollector
specifier|public
class|class
name|DocIdSetCollector
extends|extends
name|XCollector
block|{
DECL|field|docSetCache
specifier|private
specifier|final
name|DocSetCache
name|docSetCache
decl_stmt|;
DECL|field|collector
specifier|private
specifier|final
name|Collector
name|collector
decl_stmt|;
DECL|field|docSets
specifier|private
specifier|final
name|List
argument_list|<
name|ContextDocIdSet
argument_list|>
name|docSets
decl_stmt|;
DECL|field|currentHasDocs
specifier|private
name|boolean
name|currentHasDocs
decl_stmt|;
DECL|field|currentContext
specifier|private
name|ContextDocIdSet
name|currentContext
decl_stmt|;
DECL|field|currentSet
specifier|private
name|FixedBitSet
name|currentSet
decl_stmt|;
DECL|method|DocIdSetCollector
specifier|public
name|DocIdSetCollector
parameter_list|(
name|DocSetCache
name|docSetCache
parameter_list|,
name|Collector
name|collector
parameter_list|)
block|{
name|this
operator|.
name|docSetCache
operator|=
name|docSetCache
expr_stmt|;
name|this
operator|.
name|collector
operator|=
name|collector
expr_stmt|;
name|this
operator|.
name|docSets
operator|=
operator|new
name|ArrayList
argument_list|<
name|ContextDocIdSet
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|docSets
specifier|public
name|List
argument_list|<
name|ContextDocIdSet
argument_list|>
name|docSets
parameter_list|()
block|{
return|return
name|docSets
return|;
block|}
DECL|method|release
specifier|public
name|void
name|release
parameter_list|()
block|{
for|for
control|(
name|ContextDocIdSet
name|docSet
range|:
name|docSets
control|)
block|{
name|docSetCache
operator|.
name|release
argument_list|(
name|docSet
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|currentHasDocs
operator|=
literal|true
expr_stmt|;
name|currentSet
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentContext
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|currentHasDocs
condition|)
block|{
name|docSets
operator|.
name|add
argument_list|(
name|currentContext
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|docSetCache
operator|.
name|release
argument_list|(
name|currentContext
argument_list|)
expr_stmt|;
block|}
block|}
name|currentContext
operator|=
name|docSetCache
operator|.
name|obtain
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|currentSet
operator|=
operator|(
name|FixedBitSet
operator|)
name|currentContext
operator|.
name|docSet
expr_stmt|;
name|currentHasDocs
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postCollection
specifier|public
name|void
name|postCollection
parameter_list|()
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
if|if
condition|(
name|currentContext
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|currentHasDocs
condition|)
block|{
name|docSets
operator|.
name|add
argument_list|(
name|currentContext
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|docSetCache
operator|.
name|release
argument_list|(
name|currentContext
argument_list|)
expr_stmt|;
block|}
name|currentContext
operator|=
literal|null
expr_stmt|;
name|currentSet
operator|=
literal|null
expr_stmt|;
name|currentHasDocs
operator|=
literal|false
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

