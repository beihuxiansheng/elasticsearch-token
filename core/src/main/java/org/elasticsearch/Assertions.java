begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch
package|package
name|org
operator|.
name|elasticsearch
package|;
end_package

begin_comment
comment|/**  * Provides a static final field that can be used to check if assertions are enabled. Since this field might be used elsewhere to check if  * assertions are enabled, if you are running with assertions enabled for specific packages or classes, you should enable assertions on this  * class too (e.g., {@code -ea org.elasticsearch.Assertions -ea org.elasticsearch.cluster.service.MasterService}).  */
end_comment

begin_class
DECL|class|Assertions
specifier|public
specifier|final
class|class
name|Assertions
block|{
DECL|method|Assertions
specifier|private
name|Assertions
parameter_list|()
block|{      }
DECL|field|ENABLED
specifier|public
specifier|static
specifier|final
name|boolean
name|ENABLED
decl_stmt|;
static|static
block|{
name|boolean
name|enabled
init|=
literal|false
decl_stmt|;
comment|/*          * If assertions are enabled, the following line will be evaluated and enabled will have the value true, otherwise when assertions          * are disabled enabled will have the value false.          */
comment|// noinspection ConstantConditions,AssertWithSideEffects
assert|assert
name|enabled
operator|=
literal|true
assert|;
comment|// noinspection ConstantConditions
name|ENABLED
operator|=
name|enabled
expr_stmt|;
block|}
block|}
end_class

end_unit

