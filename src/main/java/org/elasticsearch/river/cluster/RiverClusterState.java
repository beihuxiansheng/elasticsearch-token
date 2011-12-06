begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.river.cluster
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|river
operator|.
name|cluster
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
name|river
operator|.
name|routing
operator|.
name|RiversRouting
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
DECL|class|RiverClusterState
specifier|public
class|class
name|RiverClusterState
block|{
DECL|field|version
specifier|private
specifier|final
name|long
name|version
decl_stmt|;
DECL|field|routing
specifier|private
specifier|final
name|RiversRouting
name|routing
decl_stmt|;
DECL|method|RiverClusterState
specifier|public
name|RiverClusterState
parameter_list|(
name|long
name|version
parameter_list|,
name|RiverClusterState
name|state
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|routing
operator|=
name|state
operator|.
name|routing
argument_list|()
expr_stmt|;
block|}
DECL|method|RiverClusterState
name|RiverClusterState
parameter_list|(
name|long
name|version
parameter_list|,
name|RiversRouting
name|routing
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|routing
operator|=
name|routing
expr_stmt|;
block|}
DECL|method|version
specifier|public
name|long
name|version
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
DECL|method|routing
specifier|public
name|RiversRouting
name|routing
parameter_list|()
block|{
return|return
name|routing
return|;
block|}
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|version
specifier|private
name|long
name|version
init|=
literal|0
decl_stmt|;
DECL|field|routing
specifier|private
name|RiversRouting
name|routing
init|=
name|RiversRouting
operator|.
name|EMPTY
decl_stmt|;
DECL|method|state
specifier|public
name|Builder
name|state
parameter_list|(
name|RiverClusterState
name|state
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|state
operator|.
name|version
argument_list|()
expr_stmt|;
name|this
operator|.
name|routing
operator|=
name|state
operator|.
name|routing
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|routing
specifier|public
name|Builder
name|routing
parameter_list|(
name|RiversRouting
operator|.
name|Builder
name|builder
parameter_list|)
block|{
return|return
name|routing
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|routing
specifier|public
name|Builder
name|routing
parameter_list|(
name|RiversRouting
name|routing
parameter_list|)
block|{
name|this
operator|.
name|routing
operator|=
name|routing
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|RiverClusterState
name|build
parameter_list|()
block|{
return|return
operator|new
name|RiverClusterState
argument_list|(
name|version
argument_list|,
name|routing
argument_list|)
return|;
block|}
DECL|method|readFrom
specifier|public
specifier|static
name|RiverClusterState
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Builder
name|builder
init|=
operator|new
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|version
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|builder
operator|.
name|routing
operator|=
name|RiversRouting
operator|.
name|Builder
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|writeTo
specifier|public
specifier|static
name|void
name|writeTo
parameter_list|(
name|RiverClusterState
name|clusterState
parameter_list|,
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVLong
argument_list|(
name|clusterState
operator|.
name|version
argument_list|)
expr_stmt|;
name|RiversRouting
operator|.
name|Builder
operator|.
name|writeTo
argument_list|(
name|clusterState
operator|.
name|routing
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

