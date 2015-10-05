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
comment|/**  * An processor implementation may modify the data belonging to a document.  * If and what exactly is modified is upto the implementation.  */
end_comment

begin_interface
DECL|interface|Processor
specifier|public
interface|interface
name|Processor
block|{
comment|/**      * Introspect and potentially modify the incoming data.      */
DECL|method|execute
name|void
name|execute
parameter_list|(
name|Data
name|data
parameter_list|)
function_decl|;
comment|/**      * A builder to contruct a processor to be used in a pipeline.      */
DECL|interface|Builder
interface|interface
name|Builder
block|{
comment|/**          * A general way to set processor related settings based on the config map.          */
DECL|method|fromMap
name|void
name|fromMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|)
function_decl|;
comment|/**          * Builds the processor based on previous set settings.          */
DECL|method|build
name|Processor
name|build
parameter_list|()
function_decl|;
comment|/**          * A factory that creates a processor builder when processor instances for pipelines are being created.          */
DECL|interface|Factory
interface|interface
name|Factory
block|{
comment|/**              * Creates the builder.              */
DECL|method|create
name|Builder
name|create
parameter_list|()
function_decl|;
block|}
block|}
block|}
end_interface

end_unit

