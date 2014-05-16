begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.disruption
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|disruption
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|InternalTestCluster
import|;
end_import

begin_class
DECL|class|NoOpDisruptionScheme
specifier|public
class|class
name|NoOpDisruptionScheme
implements|implements
name|ServiceDisruptionScheme
block|{
annotation|@
name|Override
DECL|method|applyToCluster
specifier|public
name|void
name|applyToCluster
parameter_list|(
name|InternalTestCluster
name|cluster
parameter_list|)
block|{      }
annotation|@
name|Override
DECL|method|removeFromCluster
specifier|public
name|void
name|removeFromCluster
parameter_list|(
name|InternalTestCluster
name|cluster
parameter_list|)
block|{      }
annotation|@
name|Override
DECL|method|applyToNode
specifier|public
name|void
name|applyToNode
parameter_list|(
name|String
name|node
parameter_list|,
name|InternalTestCluster
name|cluster
parameter_list|)
block|{      }
annotation|@
name|Override
DECL|method|removeFromNode
specifier|public
name|void
name|removeFromNode
parameter_list|(
name|String
name|node
parameter_list|,
name|InternalTestCluster
name|cluster
parameter_list|)
block|{      }
annotation|@
name|Override
DECL|method|startDisrupting
specifier|public
name|void
name|startDisrupting
parameter_list|()
block|{      }
annotation|@
name|Override
DECL|method|stopDisrupting
specifier|public
name|void
name|stopDisrupting
parameter_list|()
block|{      }
annotation|@
name|Override
DECL|method|testClusterClosed
specifier|public
name|void
name|testClusterClosed
parameter_list|()
block|{      }
block|}
end_class

end_unit

