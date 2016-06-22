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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Abstraction for the ingest template engine used to decouple {@link IngestDocument} from {@link org.elasticsearch.script.ScriptService}.  * Allows to compile a template into an ingest {@link Template} object.  * A compiled template can be executed by calling its {@link Template#execute(Map)} method.  */
end_comment

begin_interface
DECL|interface|TemplateService
specifier|public
interface|interface
name|TemplateService
block|{
DECL|method|compile
name|Template
name|compile
parameter_list|(
name|String
name|template
parameter_list|)
function_decl|;
DECL|interface|Template
interface|interface
name|Template
block|{
DECL|method|execute
name|String
name|execute
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|model
parameter_list|)
function_decl|;
DECL|method|getKey
name|String
name|getKey
parameter_list|()
function_decl|;
block|}
block|}
end_interface

end_unit

