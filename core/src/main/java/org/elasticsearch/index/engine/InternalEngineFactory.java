begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.engine
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
package|;
end_package

begin_class
DECL|class|InternalEngineFactory
specifier|public
class|class
name|InternalEngineFactory
implements|implements
name|EngineFactory
block|{
annotation|@
name|Override
DECL|method|newReadWriteEngine
specifier|public
name|Engine
name|newReadWriteEngine
parameter_list|(
name|EngineConfig
name|config
parameter_list|)
block|{
return|return
operator|new
name|InternalEngine
argument_list|(
name|config
argument_list|)
return|;
block|}
block|}
end_class

end_unit

