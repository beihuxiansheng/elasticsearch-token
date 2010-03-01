begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.yaml.snakeyaml.events
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|events
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|error
operator|.
name|Mark
import|;
end_import

begin_comment
comment|/**  * Base class for the end events of the collection nodes.  */
end_comment

begin_class
DECL|class|CollectionEndEvent
specifier|public
specifier|abstract
class|class
name|CollectionEndEvent
extends|extends
name|Event
block|{
DECL|method|CollectionEndEvent
specifier|public
name|CollectionEndEvent
parameter_list|(
name|Mark
name|startMark
parameter_list|,
name|Mark
name|endMark
parameter_list|)
block|{
name|super
argument_list|(
name|startMark
argument_list|,
name|endMark
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

