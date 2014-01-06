begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cache.recycler
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cache
operator|.
name|recycler
package|;
end_package

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
name|recycler
operator|.
name|Recycler
operator|.
name|V
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|TestCluster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_class
DECL|class|MockPageCacheRecycler
specifier|public
class|class
name|MockPageCacheRecycler
extends|extends
name|PageCacheRecycler
block|{
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
annotation|@
name|Inject
DECL|method|MockPageCacheRecycler
specifier|public
name|MockPageCacheRecycler
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|)
expr_stmt|;
specifier|final
name|long
name|seed
init|=
name|settings
operator|.
name|getAsLong
argument_list|(
name|TestCluster
operator|.
name|SETTING_CLUSTER_NODE_SEED
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
name|random
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bytePage
specifier|public
name|V
argument_list|<
name|byte
index|[]
argument_list|>
name|bytePage
parameter_list|(
name|boolean
name|clear
parameter_list|)
block|{
specifier|final
name|V
argument_list|<
name|byte
index|[]
argument_list|>
name|page
init|=
name|super
operator|.
name|bytePage
argument_list|(
name|clear
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|clear
condition|)
block|{
name|random
operator|.
name|nextBytes
argument_list|(
name|page
operator|.
name|v
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|page
return|;
block|}
annotation|@
name|Override
DECL|method|intPage
specifier|public
name|V
argument_list|<
name|int
index|[]
argument_list|>
name|intPage
parameter_list|(
name|boolean
name|clear
parameter_list|)
block|{
specifier|final
name|V
argument_list|<
name|int
index|[]
argument_list|>
name|page
init|=
name|super
operator|.
name|intPage
argument_list|(
name|clear
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|clear
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|page
operator|.
name|v
argument_list|()
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|page
operator|.
name|v
argument_list|()
index|[
name|i
index|]
operator|=
name|random
operator|.
name|nextInt
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|page
return|;
block|}
annotation|@
name|Override
DECL|method|longPage
specifier|public
name|V
argument_list|<
name|long
index|[]
argument_list|>
name|longPage
parameter_list|(
name|boolean
name|clear
parameter_list|)
block|{
specifier|final
name|V
argument_list|<
name|long
index|[]
argument_list|>
name|page
init|=
name|super
operator|.
name|longPage
argument_list|(
name|clear
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|clear
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|page
operator|.
name|v
argument_list|()
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|page
operator|.
name|v
argument_list|()
index|[
name|i
index|]
operator|=
name|random
operator|.
name|nextLong
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|page
return|;
block|}
annotation|@
name|Override
DECL|method|doublePage
specifier|public
name|V
argument_list|<
name|double
index|[]
argument_list|>
name|doublePage
parameter_list|(
name|boolean
name|clear
parameter_list|)
block|{
specifier|final
name|V
argument_list|<
name|double
index|[]
argument_list|>
name|page
init|=
name|super
operator|.
name|doublePage
argument_list|(
name|clear
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|clear
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|page
operator|.
name|v
argument_list|()
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|page
operator|.
name|v
argument_list|()
index|[
name|i
index|]
operator|=
name|random
operator|.
name|nextDouble
argument_list|()
operator|-
literal|0.5
expr_stmt|;
block|}
block|}
return|return
name|page
return|;
block|}
block|}
end_class

end_unit

