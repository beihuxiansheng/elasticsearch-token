begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Scorer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|lookup
operator|.
name|LeafDocLookup
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

begin_class
DECL|class|Executable
specifier|public
specifier|abstract
class|class
name|Executable
block|{
DECL|field|definition
specifier|protected
specifier|final
name|Definition
name|definition
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|String
name|source
decl_stmt|;
DECL|method|Executable
specifier|public
name|Executable
parameter_list|(
specifier|final
name|Definition
name|definition
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|source
parameter_list|)
block|{
name|this
operator|.
name|definition
operator|=
name|definition
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
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
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
name|source
return|;
block|}
DECL|method|getDefinition
specifier|public
name|Definition
name|getDefinition
parameter_list|()
block|{
return|return
name|definition
return|;
block|}
DECL|method|execute
specifier|public
specifier|abstract
name|Object
name|execute
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|input
parameter_list|,
name|Scorer
name|scorer
parameter_list|,
name|LeafDocLookup
name|doc
parameter_list|,
name|Object
name|value
parameter_list|)
function_decl|;
block|}
end_class

end_unit

