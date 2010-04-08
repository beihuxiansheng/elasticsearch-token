begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.yaml.snakeyaml.constructor
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
name|constructor
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
name|nodes
operator|.
name|YamlNode
import|;
end_import

begin_comment
comment|/**  * Provide a way to construct a Java instance out of the composed Node. Support  * recursive objects if it is required. (create Native Data Structure out of  * Node Graph)  *  * @see http://yaml.org/spec/1.1/#id859109  */
end_comment

begin_interface
DECL|interface|Construct
specifier|public
interface|interface
name|Construct
block|{
comment|/**      * Construct a Java instance with all the properties injected when it is      * possible.      *      * @param node composed Node      * @return a complete Java instance      */
DECL|method|construct
specifier|public
name|Object
name|construct
parameter_list|(
name|YamlNode
name|node
parameter_list|)
function_decl|;
comment|/**      * Apply the second step when constructing recursive structures. Because the      * instance is already created it can assign a reference to itself.      *      * @param node   composed Node      * @param object the instance constructed earlier by      *<code>construct(Node node)</code> for the provided Node      */
DECL|method|construct2ndStep
specifier|public
name|void
name|construct2ndStep
parameter_list|(
name|YamlNode
name|node
parameter_list|,
name|Object
name|object
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

