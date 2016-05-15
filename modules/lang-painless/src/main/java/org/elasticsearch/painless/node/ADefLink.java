begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless.node
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|node
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|painless
operator|.
name|Definition
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * The superclass for all LDef* (link) nodes that store or return a DEF. (Internal only.)  */
end_comment

begin_class
DECL|class|ADefLink
specifier|abstract
class|class
name|ADefLink
extends|extends
name|ALink
block|{
comment|/**      * The type of the original type that was pushed on stack, set by {@link EChain} during analyze.      * This value is only used for writing the 'store' bytecode, otherwise ignored.      */
DECL|field|storeValueType
name|Type
name|storeValueType
init|=
literal|null
decl_stmt|;
DECL|method|ADefLink
name|ADefLink
parameter_list|(
specifier|final
name|int
name|line
parameter_list|,
specifier|final
name|String
name|location
parameter_list|,
specifier|final
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|line
argument_list|,
name|location
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

