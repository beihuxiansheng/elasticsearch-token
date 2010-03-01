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
name|error
operator|.
name|YAMLException
import|;
end_import

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
name|Node
import|;
end_import

begin_comment
comment|/**  * Because recursive structures are not very common we provide a way to save  * some typing when extending a constructor  */
end_comment

begin_class
DECL|class|AbstractConstruct
specifier|public
specifier|abstract
class|class
name|AbstractConstruct
implements|implements
name|Construct
block|{
comment|/**      * Fail with a reminder to provide the seconds step for a recursive      * structure      *      * @see org.elasticsearch.util.yaml.snakeyaml.constructor.Construct#construct2ndStep(org.elasticsearch.util.yaml.snakeyaml.nodes.Node,      *      java.lang.Object)      */
DECL|method|construct2ndStep
specifier|public
name|void
name|construct2ndStep
parameter_list|(
name|Node
name|node
parameter_list|,
name|Object
name|data
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|isTwoStepsConstruction
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Not Implemented in "
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|YAMLException
argument_list|(
literal|"Unexpected recursive structure for Node: "
operator|+
name|node
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

