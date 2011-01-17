begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|Modules
specifier|public
class|class
name|Modules
block|{
DECL|method|createModule
specifier|public
specifier|static
name|Module
name|createModule
parameter_list|(
name|String
name|moduleClass
parameter_list|,
name|Settings
name|settings
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
return|return
name|createModule
argument_list|(
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
operator|)
name|settings
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
name|moduleClass
argument_list|)
argument_list|,
name|settings
argument_list|)
return|;
block|}
DECL|method|createModule
specifier|public
specifier|static
name|Module
name|createModule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|moduleClass
parameter_list|,
annotation|@
name|Nullable
name|Settings
name|settings
parameter_list|)
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|constructor
decl_stmt|;
try|try
block|{
name|constructor
operator|=
name|moduleClass
operator|.
name|getConstructor
argument_list|(
name|Settings
operator|.
name|class
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|constructor
operator|.
name|newInstance
argument_list|(
name|settings
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchException
argument_list|(
literal|"Failed to create module ["
operator|+
name|moduleClass
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
try|try
block|{
name|constructor
operator|=
name|moduleClass
operator|.
name|getConstructor
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|constructor
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchException
argument_list|(
literal|"Failed to create module ["
operator|+
name|moduleClass
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchException
argument_list|(
literal|"No constructor for ["
operator|+
name|moduleClass
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|processModules
specifier|public
specifier|static
name|void
name|processModules
parameter_list|(
name|Iterable
argument_list|<
name|Module
argument_list|>
name|modules
parameter_list|)
block|{
for|for
control|(
name|Module
name|module
range|:
name|modules
control|)
block|{
if|if
condition|(
name|module
operator|instanceof
name|PreProcessModule
condition|)
block|{
for|for
control|(
name|Module
name|module1
range|:
name|modules
control|)
block|{
operator|(
operator|(
name|PreProcessModule
operator|)
name|module
operator|)
operator|.
name|processModule
argument_list|(
name|module1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

