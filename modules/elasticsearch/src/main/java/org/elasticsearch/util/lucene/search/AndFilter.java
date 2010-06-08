begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.lucene.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|lucene
operator|.
name|search
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
name|DocIdSet
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
name|elasticsearch
operator|.
name|util
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
name|util
operator|.
name|lucene
operator|.
name|docset
operator|.
name|AndDocSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
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
name|List
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|AndFilter
specifier|public
class|class
name|AndFilter
extends|extends
name|Filter
block|{
DECL|field|filters
specifier|private
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
name|filters
decl_stmt|;
DECL|method|AndFilter
specifier|public
name|AndFilter
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
name|filters
parameter_list|)
block|{
name|this
operator|.
name|filters
operator|=
name|filters
expr_stmt|;
block|}
DECL|method|filters
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
name|filters
parameter_list|()
block|{
return|return
name|filters
return|;
block|}
DECL|method|getDocIdSet
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|filters
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|DocSets
operator|.
name|convert
argument_list|(
name|reader
argument_list|,
name|filters
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
argument_list|)
return|;
block|}
name|List
argument_list|<
name|DocSet
argument_list|>
name|sets
init|=
name|Lists
operator|.
name|newArrayListWithExpectedSize
argument_list|(
name|filters
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Filter
name|filter
range|:
name|filters
control|)
block|{
name|sets
operator|.
name|add
argument_list|(
name|DocSets
operator|.
name|convert
argument_list|(
name|reader
argument_list|,
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|AndDocSet
argument_list|(
name|sets
argument_list|)
return|;
block|}
block|}
end_class

end_unit

