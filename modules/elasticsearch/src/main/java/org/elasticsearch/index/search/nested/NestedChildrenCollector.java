begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search.nested
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|nested
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
name|IndexReader
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
name|DocSet
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
name|DocSets
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
name|FixedBitDocSet
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
name|FacetCollector
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
comment|/**  * A collector that accepts parent docs, and calls back the collect on child docs of that parent.  */
end_comment

begin_class
DECL|class|NestedChildrenCollector
specifier|public
class|class
name|NestedChildrenCollector
extends|extends
name|FacetCollector
block|{
DECL|field|collector
specifier|private
specifier|final
name|FacetCollector
name|collector
decl_stmt|;
DECL|field|parentFilter
specifier|private
specifier|final
name|Filter
name|parentFilter
decl_stmt|;
DECL|field|childFilter
specifier|private
specifier|final
name|Filter
name|childFilter
decl_stmt|;
DECL|field|childDocs
specifier|private
name|DocSet
name|childDocs
decl_stmt|;
DECL|field|parentDocs
specifier|private
name|FixedBitSet
name|parentDocs
decl_stmt|;
DECL|field|currentReader
specifier|private
name|IndexReader
name|currentReader
decl_stmt|;
DECL|method|NestedChildrenCollector
specifier|public
name|NestedChildrenCollector
parameter_list|(
name|FacetCollector
name|collector
parameter_list|,
name|Filter
name|parentFilter
parameter_list|,
name|Filter
name|childFilter
parameter_list|)
block|{
name|this
operator|.
name|collector
operator|=
name|collector
expr_stmt|;
name|this
operator|.
name|parentFilter
operator|=
name|parentFilter
expr_stmt|;
name|this
operator|.
name|childFilter
operator|=
name|childFilter
expr_stmt|;
block|}
DECL|method|facet
annotation|@
name|Override
specifier|public
name|Facet
name|facet
parameter_list|()
block|{
return|return
name|collector
operator|.
name|facet
argument_list|()
return|;
block|}
DECL|method|setFilter
annotation|@
name|Override
specifier|public
name|void
name|setFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
comment|// delegate the facet_filter to the children
name|collector
operator|.
name|setFilter
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
DECL|method|setScorer
annotation|@
name|Override
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
DECL|method|setNextReader
annotation|@
name|Override
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|,
name|docBase
argument_list|)
expr_stmt|;
name|currentReader
operator|=
name|reader
expr_stmt|;
name|childDocs
operator|=
name|DocSets
operator|.
name|convert
argument_list|(
name|reader
argument_list|,
name|childFilter
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
argument_list|)
expr_stmt|;
name|parentDocs
operator|=
operator|(
operator|(
name|FixedBitDocSet
operator|)
name|parentFilter
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
operator|)
operator|.
name|set
argument_list|()
expr_stmt|;
block|}
DECL|method|acceptsDocsOutOfOrder
annotation|@
name|Override
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
name|collector
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
return|;
block|}
DECL|method|collect
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|parentDoc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|parentDoc
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|int
name|prevParentDoc
init|=
name|parentDocs
operator|.
name|prevSetBit
argument_list|(
name|parentDoc
operator|-
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
operator|(
name|parentDoc
operator|-
literal|1
operator|)
init|;
name|i
operator|>
name|prevParentDoc
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
operator|!
name|currentReader
operator|.
name|isDeleted
argument_list|(
name|i
argument_list|)
operator|&&
name|childDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|collector
operator|.
name|collect
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

