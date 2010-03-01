begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.yaml.snakeyaml.introspector
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
name|introspector
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_class
DECL|class|Property
specifier|public
specifier|abstract
class|class
name|Property
implements|implements
name|Comparable
argument_list|<
name|Property
argument_list|>
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
name|type
decl_stmt|;
DECL|method|Property
specifier|public
name|Property
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
name|type
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|getType
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getActualTypeArguments
specifier|public
specifier|abstract
name|Type
index|[]
name|getActualTypeArguments
parameter_list|()
function_decl|;
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getName
argument_list|()
operator|+
literal|" of "
operator|+
name|getType
argument_list|()
return|;
block|}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Property
name|o
parameter_list|)
block|{
return|return
name|name
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|name
argument_list|)
return|;
block|}
DECL|method|set
specifier|abstract
specifier|public
name|void
name|set
parameter_list|(
name|Object
name|object
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|get
specifier|abstract
specifier|public
name|Object
name|get
parameter_list|(
name|Object
name|object
parameter_list|)
function_decl|;
block|}
end_class

end_unit

