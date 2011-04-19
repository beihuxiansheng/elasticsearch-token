begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
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
name|common
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
name|common
operator|.
name|lucene
operator|.
name|docset
operator|.
name|AndDocIdSet
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
name|AndDocSet
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
return|;
block|}
name|List
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
name|boolean
name|allAreDocSet
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Filter
name|filter
range|:
name|filters
control|)
block|{
name|DocIdSet
name|set
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
comment|// none matching for this filter, we AND, so return EMPTY
return|return
name|DocSet
operator|.
name|EMPTY_DOC_SET
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|set
operator|instanceof
name|DocSet
operator|)
condition|)
block|{
name|allAreDocSet
operator|=
literal|false
expr_stmt|;
block|}
name|sets
operator|.
name|add
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allAreDocSet
condition|)
block|{
return|return
operator|new
name|AndDocSet
argument_list|(
name|sets
argument_list|)
return|;
block|}
return|return
operator|new
name|AndDocIdSet
argument_list|(
name|sets
argument_list|)
return|;
block|}
DECL|method|hashCode
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
literal|7
decl_stmt|;
name|hash
operator|=
literal|31
operator|*
name|hash
operator|+
operator|(
literal|null
operator|==
name|filters
condition|?
literal|0
else|:
name|filters
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|hash
return|;
block|}
DECL|method|equals
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|(
name|obj
operator|==
literal|null
operator|)
operator|||
operator|(
name|obj
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
operator|)
condition|)
return|return
literal|false
return|;
name|AndFilter
name|other
init|=
operator|(
name|AndFilter
operator|)
name|obj
decl_stmt|;
return|return
name|equalFilters
argument_list|(
name|filters
argument_list|,
name|other
operator|.
name|filters
argument_list|)
return|;
block|}
DECL|method|equalFilters
specifier|private
name|boolean
name|equalFilters
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
name|filters1
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|Filter
argument_list|>
name|filters2
parameter_list|)
block|{
return|return
operator|(
name|filters1
operator|==
name|filters2
operator|)
operator|||
operator|(
operator|(
name|filters1
operator|!=
literal|null
operator|)
operator|&&
name|filters1
operator|.
name|equals
argument_list|(
name|filters2
argument_list|)
operator|)
return|;
block|}
block|}
end_class

end_unit

