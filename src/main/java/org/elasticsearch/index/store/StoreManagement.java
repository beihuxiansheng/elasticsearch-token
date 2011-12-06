begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
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
name|index
operator|.
name|shard
operator|.
name|AbstractIndexShardComponent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|jmx
operator|.
name|MBean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|jmx
operator|.
name|ManagedAttribute
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
annotation|@
name|MBean
argument_list|(
name|objectName
operator|=
literal|"shardType=store"
argument_list|,
name|description
operator|=
literal|"The storage of the index shard"
argument_list|)
DECL|class|StoreManagement
specifier|public
class|class
name|StoreManagement
extends|extends
name|AbstractIndexShardComponent
block|{
DECL|field|store
specifier|private
specifier|final
name|Store
name|store
decl_stmt|;
annotation|@
name|Inject
DECL|method|StoreManagement
specifier|public
name|StoreManagement
parameter_list|(
name|Store
name|store
parameter_list|)
block|{
name|super
argument_list|(
name|store
operator|.
name|shardId
argument_list|()
argument_list|,
name|store
operator|.
name|indexSettings
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Size in bytes"
argument_list|)
DECL|method|getSizeInBytes
specifier|public
name|long
name|getSizeInBytes
parameter_list|()
block|{
try|try
block|{
return|return
name|store
operator|.
name|estimateSize
argument_list|()
operator|.
name|bytes
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|ManagedAttribute
argument_list|(
name|description
operator|=
literal|"Size"
argument_list|)
DECL|method|getSize
specifier|public
name|String
name|getSize
parameter_list|()
block|{
try|try
block|{
return|return
name|store
operator|.
name|estimateSize
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|"NA"
return|;
block|}
block|}
block|}
end_class

end_unit

