begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.docset
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|docset
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
name|DocIdSetIterator
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
DECL|class|NotDocIdSet
specifier|public
class|class
name|NotDocIdSet
extends|extends
name|DocIdSet
block|{
DECL|field|set
specifier|private
specifier|final
name|DocIdSet
name|set
decl_stmt|;
DECL|field|max
specifier|private
specifier|final
name|int
name|max
decl_stmt|;
DECL|method|NotDocIdSet
specifier|public
name|NotDocIdSet
parameter_list|(
name|DocIdSet
name|set
parameter_list|,
name|int
name|max
parameter_list|)
block|{
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
name|this
operator|.
name|set
operator|=
name|set
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isCacheable
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
comment|// not cacheable, the reason is that by default, when constructing the filter, it is not cacheable,
comment|// so if someone wants it to be cacheable, we might as well construct a cached version of the result
return|return
literal|false
return|;
comment|//        return set.isCacheable();
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
name|DocIdSetIterator
name|it
init|=
name|set
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|it
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|AllDocSet
operator|.
name|AllDocIdSetIterator
argument_list|(
name|max
argument_list|)
return|;
block|}
return|return
operator|new
name|NotDocIdSetIterator
argument_list|(
name|max
argument_list|,
name|it
argument_list|)
return|;
block|}
DECL|class|NotDocIdSetIterator
specifier|public
specifier|static
class|class
name|NotDocIdSetIterator
extends|extends
name|DocIdSetIterator
block|{
DECL|field|max
specifier|private
specifier|final
name|int
name|max
decl_stmt|;
DECL|field|it1
specifier|private
name|DocIdSetIterator
name|it1
decl_stmt|;
DECL|field|lastReturn
name|int
name|lastReturn
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|innerDocid
specifier|private
name|int
name|innerDocid
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|NotDocIdSetIterator
name|NotDocIdSetIterator
parameter_list|(
name|int
name|max
parameter_list|,
name|DocIdSetIterator
name|it
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
name|this
operator|.
name|it1
operator|=
name|it
expr_stmt|;
if|if
condition|(
operator|(
name|innerDocid
operator|=
name|it1
operator|.
name|nextDoc
argument_list|()
operator|)
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
name|it1
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|lastReturn
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|advance
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|lastReturn
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
if|if
condition|(
name|target
operator|<=
name|lastReturn
condition|)
name|target
operator|=
name|lastReturn
operator|+
literal|1
expr_stmt|;
if|if
condition|(
name|it1
operator|!=
literal|null
operator|&&
name|innerDocid
operator|<
name|target
condition|)
block|{
if|if
condition|(
operator|(
name|innerDocid
operator|=
name|it1
operator|.
name|advance
argument_list|(
name|target
argument_list|)
operator|)
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|it1
operator|=
literal|null
expr_stmt|;
block|}
block|}
while|while
condition|(
name|it1
operator|!=
literal|null
operator|&&
name|innerDocid
operator|==
name|target
condition|)
block|{
name|target
operator|++
expr_stmt|;
if|if
condition|(
name|target
operator|>=
name|max
condition|)
block|{
return|return
operator|(
name|lastReturn
operator|=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
operator|)
return|;
block|}
if|if
condition|(
operator|(
name|innerDocid
operator|=
name|it1
operator|.
name|advance
argument_list|(
name|target
argument_list|)
operator|)
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|it1
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|// ADDED THIS, bug in original code
if|if
condition|(
name|target
operator|>=
name|max
condition|)
block|{
return|return
operator|(
name|lastReturn
operator|=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
operator|)
return|;
block|}
return|return
operator|(
name|lastReturn
operator|=
name|target
operator|)
return|;
block|}
block|}
block|}
end_class

end_unit

