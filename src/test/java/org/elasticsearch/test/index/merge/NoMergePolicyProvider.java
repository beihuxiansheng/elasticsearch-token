begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.index.merge
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|index
operator|.
name|merge
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
name|MergePolicy
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
name|NoMergePolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|index
operator|.
name|merge
operator|.
name|policy
operator|.
name|AbstractMergePolicyProvider
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
name|store
operator|.
name|Store
import|;
end_import

begin_comment
comment|/**  * {@link org.elasticsearch.index.merge.policy.MergePolicyProvider} for lucenes {@link org.apache.lucene.index.NoMergePolicy}  */
end_comment

begin_class
DECL|class|NoMergePolicyProvider
specifier|public
class|class
name|NoMergePolicyProvider
extends|extends
name|AbstractMergePolicyProvider
argument_list|<
name|MergePolicy
argument_list|>
block|{
annotation|@
name|Inject
DECL|method|NoMergePolicyProvider
specifier|public
name|NoMergePolicyProvider
parameter_list|(
name|Store
name|store
parameter_list|)
block|{
name|super
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMergePolicy
specifier|public
name|MergePolicy
name|getMergePolicy
parameter_list|()
block|{
return|return
name|NoMergePolicy
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|ElasticsearchException
block|{}
block|}
end_class

end_unit

