begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * A registry from String to some class implementation. Used to ensure implementations are registered only once.  */
end_comment

begin_class
DECL|class|NamedRegistry
specifier|public
class|class
name|NamedRegistry
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|registry
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|registry
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|targetName
specifier|private
specifier|final
name|String
name|targetName
decl_stmt|;
DECL|method|NamedRegistry
specifier|public
name|NamedRegistry
parameter_list|(
name|String
name|targetName
parameter_list|)
block|{
name|this
operator|.
name|targetName
operator|=
name|targetName
expr_stmt|;
block|}
DECL|method|getRegistry
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|getRegistry
parameter_list|()
block|{
return|return
name|registry
return|;
block|}
DECL|method|register
specifier|public
name|void
name|register
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|t
parameter_list|)
block|{
name|requireNonNull
argument_list|(
name|name
argument_list|,
literal|"name is required"
argument_list|)
expr_stmt|;
name|requireNonNull
argument_list|(
name|t
argument_list|,
name|targetName
operator|+
literal|" is required"
argument_list|)
expr_stmt|;
if|if
condition|(
name|registry
operator|.
name|putIfAbsent
argument_list|(
name|name
argument_list|,
name|t
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|targetName
operator|+
literal|" for name "
operator|+
name|name
operator|+
literal|" already registered"
argument_list|)
throw|;
block|}
block|}
DECL|method|extractAndRegister
specifier|public
parameter_list|<
name|P
parameter_list|>
name|void
name|extractAndRegister
parameter_list|(
name|List
argument_list|<
name|P
argument_list|>
name|plugins
parameter_list|,
name|Function
argument_list|<
name|P
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
argument_list|>
name|lookup
parameter_list|)
block|{
for|for
control|(
name|P
name|plugin
range|:
name|plugins
control|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|entry
range|:
name|lookup
operator|.
name|apply
argument_list|(
name|plugin
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|register
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

