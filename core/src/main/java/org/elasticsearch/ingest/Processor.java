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
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadContext
import|;
end_import

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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptService
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

begin_comment
comment|/**  * A processor implementation may modify the data belonging to a document.  * Whether changes are made and what exactly is modified is up to the implementation.  */
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
name|IngestDocument
name|ingestDocument
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Gets the type of a processor      */
DECL|method|getType
name|String
name|getType
parameter_list|()
function_decl|;
comment|/**      * Gets the tag of a processor.      */
DECL|method|getTag
name|String
name|getTag
parameter_list|()
function_decl|;
comment|/**      * A factory that knows how to construct a processor based on a map of maps.      */
DECL|interface|Factory
interface|interface
name|Factory
block|{
comment|/**          * Creates a processor based on the specified map of maps config.          *          * @param processorFactories Other processors which may be created inside this processor          * @param tag The tag for the processor          * @param config The configuration for the processor          *          *<b>Note:</b> Implementations are responsible for removing the used configuration keys, so that after          * creating a pipeline ingest can verify if all configurations settings have been used.          */
DECL|method|create
name|Processor
name|create
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Processor
operator|.
name|Factory
argument_list|>
name|processorFactories
parameter_list|,
name|String
name|tag
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
comment|/**      * Infrastructure class that holds services that can be used by processor factories to create processor instances      * and that gets passed around to all {@link org.elasticsearch.plugins.IngestPlugin}s.      */
DECL|class|Parameters
class|class
name|Parameters
block|{
comment|/**          * Useful to provide access to the node's environment like config directory to processor factories.          */
DECL|field|env
specifier|public
specifier|final
name|Environment
name|env
decl_stmt|;
comment|/**          * Provides processors script support.          */
DECL|field|scriptService
specifier|public
specifier|final
name|ScriptService
name|scriptService
decl_stmt|;
comment|/**          * Provides template support to pipeline settings.          */
DECL|field|templateService
specifier|public
specifier|final
name|TemplateService
name|templateService
decl_stmt|;
comment|/**          * Allows processors to read headers set by {@link org.elasticsearch.action.support.ActionFilter}          * instances that have run prior to in ingest.          */
DECL|field|threadContext
specifier|public
specifier|final
name|ThreadContext
name|threadContext
decl_stmt|;
DECL|method|Parameters
specifier|public
name|Parameters
parameter_list|(
name|Environment
name|env
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|,
name|TemplateService
name|templateService
parameter_list|,
name|ThreadContext
name|threadContext
parameter_list|)
block|{
name|this
operator|.
name|env
operator|=
name|env
expr_stmt|;
name|this
operator|.
name|scriptService
operator|=
name|scriptService
expr_stmt|;
name|this
operator|.
name|templateService
operator|=
name|templateService
expr_stmt|;
name|this
operator|.
name|threadContext
operator|=
name|threadContext
expr_stmt|;
block|}
block|}
block|}
end_interface

end_unit

