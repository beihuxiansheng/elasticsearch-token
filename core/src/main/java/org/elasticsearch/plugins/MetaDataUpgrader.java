begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugins
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
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
name|metadata
operator|.
name|MetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|UnaryOperator
import|;
end_import

begin_comment
comment|/**  * Upgrades {@link MetaData} on startup on behalf of installed {@link Plugin}s  */
end_comment

begin_class
DECL|class|MetaDataUpgrader
specifier|public
class|class
name|MetaDataUpgrader
block|{
DECL|field|customMetaDataUpgraders
specifier|public
specifier|final
name|UnaryOperator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|MetaData
operator|.
name|Custom
argument_list|>
argument_list|>
name|customMetaDataUpgraders
decl_stmt|;
DECL|method|MetaDataUpgrader
specifier|public
name|MetaDataUpgrader
parameter_list|(
name|Collection
argument_list|<
name|UnaryOperator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|MetaData
operator|.
name|Custom
argument_list|>
argument_list|>
argument_list|>
name|customMetaDataUpgraders
parameter_list|)
block|{
name|this
operator|.
name|customMetaDataUpgraders
operator|=
name|customs
lambda|->
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|MetaData
operator|.
name|Custom
argument_list|>
name|upgradedCustoms
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|customs
argument_list|)
decl_stmt|;
for|for
control|(
name|UnaryOperator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|MetaData
operator|.
name|Custom
argument_list|>
argument_list|>
name|customMetaDataUpgrader
range|:
name|customMetaDataUpgraders
control|)
block|{
name|upgradedCustoms
operator|=
name|customMetaDataUpgrader
operator|.
name|apply
argument_list|(
name|upgradedCustoms
argument_list|)
expr_stmt|;
block|}
return|return
name|upgradedCustoms
return|;
block|}
expr_stmt|;
block|}
block|}
end_class

end_unit

