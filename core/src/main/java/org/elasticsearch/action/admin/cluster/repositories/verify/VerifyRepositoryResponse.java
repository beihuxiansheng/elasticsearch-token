begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.repositories.verify
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|repositories
operator|.
name|verify
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|xcontent
operator|.
name|ToXContent
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentBuilderString
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
name|xcontent
operator|.
name|XContentHelper
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
comment|/**  * Unregister repository response  */
end_comment

begin_class
DECL|class|VerifyRepositoryResponse
specifier|public
class|class
name|VerifyRepositoryResponse
extends|extends
name|ActionResponse
implements|implements
name|ToXContent
block|{
DECL|field|nodes
specifier|private
name|DiscoveryNode
index|[]
name|nodes
decl_stmt|;
DECL|field|clusterName
specifier|private
name|ClusterName
name|clusterName
decl_stmt|;
DECL|method|VerifyRepositoryResponse
name|VerifyRepositoryResponse
parameter_list|()
block|{     }
DECL|method|VerifyRepositoryResponse
specifier|public
name|VerifyRepositoryResponse
parameter_list|(
name|ClusterName
name|clusterName
parameter_list|,
name|DiscoveryNode
index|[]
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|clusterName
operator|=
name|clusterName
expr_stmt|;
name|this
operator|.
name|nodes
operator|=
name|nodes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|clusterName
operator|=
name|ClusterName
operator|.
name|readClusterName
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|nodes
operator|=
operator|new
name|DiscoveryNode
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|nodes
index|[
name|i
index|]
operator|=
operator|new
name|DiscoveryNode
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|clusterName
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|nodes
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|nodes
control|)
block|{
name|node
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getNodes
specifier|public
name|DiscoveryNode
index|[]
name|getNodes
parameter_list|()
block|{
return|return
name|nodes
return|;
block|}
DECL|method|getClusterName
specifier|public
name|ClusterName
name|getClusterName
parameter_list|()
block|{
return|return
name|clusterName
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|NODES
specifier|static
specifier|final
name|XContentBuilderString
name|NODES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"nodes"
argument_list|)
decl_stmt|;
DECL|field|NAME
specifier|static
specifier|final
name|XContentBuilderString
name|NAME
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|NODES
argument_list|)
expr_stmt|;
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|nodes
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|node
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|NAME
argument_list|,
name|node
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|XContentHelper
operator|.
name|toString
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

