begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.metadata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|operation
operator|.
name|hash
operator|.
name|djb
operator|.
name|DjbHashFunction
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
name|component
operator|.
name|AbstractComponent
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
name|common
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|MetaDataService
specifier|public
class|class
name|MetaDataService
extends|extends
name|AbstractComponent
block|{
DECL|field|indexMdLocks
specifier|private
specifier|final
name|MdLock
index|[]
name|indexMdLocks
decl_stmt|;
DECL|method|MetaDataService
annotation|@
name|Inject
specifier|public
name|MetaDataService
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|indexMdLocks
operator|=
operator|new
name|MdLock
index|[
literal|500
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
name|indexMdLocks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indexMdLocks
index|[
name|i
index|]
operator|=
operator|new
name|MdLock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|indexMetaDataLock
specifier|public
name|MdLock
name|indexMetaDataLock
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
name|indexMdLocks
index|[
name|Math
operator|.
name|abs
argument_list|(
name|DjbHashFunction
operator|.
name|DJB_HASH
argument_list|(
name|index
argument_list|)
operator|%
name|indexMdLocks
operator|.
name|length
argument_list|)
index|]
return|;
block|}
DECL|class|MdLock
specifier|public
class|class
name|MdLock
block|{
DECL|field|isLocked
specifier|private
name|boolean
name|isLocked
init|=
literal|false
decl_stmt|;
DECL|method|lock
specifier|public
specifier|synchronized
name|void
name|lock
parameter_list|()
throws|throws
name|InterruptedException
block|{
while|while
condition|(
name|isLocked
condition|)
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
name|isLocked
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|unlock
specifier|public
specifier|synchronized
name|void
name|unlock
parameter_list|()
block|{
name|isLocked
operator|=
literal|false
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

