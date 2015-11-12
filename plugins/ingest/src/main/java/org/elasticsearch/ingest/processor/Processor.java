begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.processor
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|processor
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|Data
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|Data
name|data
parameter_list|)
function_decl|;
comment|/**      * Gets the type of a processor      */
DECL|method|getType
name|String
name|getType
parameter_list|()
function_decl|;
comment|/**      * A factory that knows how to construct a processor based on a map of maps.      */
DECL|interface|Factory
interface|interface
name|Factory
parameter_list|<
name|P
extends|extends
name|Processor
parameter_list|>
extends|extends
name|Closeable
block|{
comment|/**          * Creates a processor based on the specified map of maps config          */
DECL|method|create
name|P
name|create
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**          * Sets the configuration directory when needed to read additional config files          */
DECL|method|setConfigDirectory
specifier|default
name|void
name|setConfigDirectory
parameter_list|(
name|Path
name|configDirectory
parameter_list|)
block|{         }
annotation|@
name|Override
DECL|method|close
specifier|default
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{         }
block|}
block|}
end_interface

end_unit

