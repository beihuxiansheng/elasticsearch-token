begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.terms.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|terms
operator|.
name|support
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
name|util
operator|.
name|PriorityQueue
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
name|terms
operator|.
name|TermsFacet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_class
DECL|class|EntryPriorityQueue
specifier|public
class|class
name|EntryPriorityQueue
extends|extends
name|PriorityQueue
argument_list|<
name|TermsFacet
operator|.
name|Entry
argument_list|>
block|{
DECL|field|LIMIT
specifier|public
specifier|static
specifier|final
name|int
name|LIMIT
init|=
literal|5000
decl_stmt|;
DECL|field|comparator
specifier|private
specifier|final
name|Comparator
argument_list|<
name|TermsFacet
operator|.
name|Entry
argument_list|>
name|comparator
decl_stmt|;
DECL|method|EntryPriorityQueue
specifier|public
name|EntryPriorityQueue
parameter_list|(
name|int
name|size
parameter_list|,
name|Comparator
argument_list|<
name|TermsFacet
operator|.
name|Entry
argument_list|>
name|comparator
parameter_list|)
block|{
name|initialize
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|TermsFacet
operator|.
name|Entry
name|a
parameter_list|,
name|TermsFacet
operator|.
name|Entry
name|b
parameter_list|)
block|{
return|return
name|comparator
operator|.
name|compare
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
operator|>
literal|0
return|;
comment|// reverse, since we reverse again when adding to a list
block|}
block|}
end_class

end_unit

