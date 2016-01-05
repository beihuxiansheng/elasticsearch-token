begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
import|;
end_import

begin_comment
comment|/**  * The ingest framework (pipeline, processor and processor factory) can't rely on ES specific code. However some  * processors rely on reading files from the config directory. We can't add Environment as a constructor parameter,  * so we need some code that provides the physical location of the configuration directory to the processor factories  * that need this and this is what this processor factory provider does.  */
end_comment

begin_comment
comment|//TODO this abstraction could be removed once ingest-core is part of es core?
end_comment

begin_interface
annotation|@
name|FunctionalInterface
DECL|interface|ProcessorFactoryProvider
specifier|public
interface|interface
name|ProcessorFactoryProvider
block|{
DECL|method|get
name|Processor
operator|.
name|Factory
name|get
parameter_list|(
name|Environment
name|environment
parameter_list|,
name|TemplateService
name|templateService
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

