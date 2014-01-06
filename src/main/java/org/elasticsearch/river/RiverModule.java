begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.river
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|river
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|Strings
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
name|inject
operator|.
name|AbstractModule
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
name|inject
operator|.
name|Module
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
name|inject
operator|.
name|Modules
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
name|inject
operator|.
name|SpawnModules
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
name|NoClassSettingsException
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
name|util
operator|.
name|Locale
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Strings
operator|.
name|toCamelCase
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RiverModule
specifier|public
class|class
name|RiverModule
extends|extends
name|AbstractModule
implements|implements
name|SpawnModules
block|{
DECL|field|riverName
specifier|private
name|RiverName
name|riverName
decl_stmt|;
DECL|field|globalSettings
specifier|private
specifier|final
name|Settings
name|globalSettings
decl_stmt|;
DECL|field|settings
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|settings
decl_stmt|;
DECL|field|typesRegistry
specifier|private
specifier|final
name|RiversTypesRegistry
name|typesRegistry
decl_stmt|;
DECL|method|RiverModule
specifier|public
name|RiverModule
parameter_list|(
name|RiverName
name|riverName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|settings
parameter_list|,
name|Settings
name|globalSettings
parameter_list|,
name|RiversTypesRegistry
name|typesRegistry
parameter_list|)
block|{
name|this
operator|.
name|riverName
operator|=
name|riverName
expr_stmt|;
name|this
operator|.
name|globalSettings
operator|=
name|globalSettings
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|this
operator|.
name|typesRegistry
operator|=
name|typesRegistry
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|spawnModules
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|spawnModules
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|Modules
operator|.
name|createModule
argument_list|(
name|loadTypeModule
argument_list|(
name|riverName
operator|.
name|type
argument_list|()
argument_list|,
literal|"org.elasticsearch.river."
argument_list|,
literal|"RiverModule"
argument_list|)
argument_list|,
name|globalSettings
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|RiverSettings
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
operator|new
name|RiverSettings
argument_list|(
name|globalSettings
argument_list|,
name|settings
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|loadTypeModule
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|loadTypeModule
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|prefixPackage
parameter_list|,
name|String
name|suffixClassName
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|registered
init|=
name|typesRegistry
operator|.
name|type
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|registered
operator|!=
literal|null
condition|)
block|{
return|return
name|registered
return|;
block|}
name|String
name|fullClassName
init|=
name|type
decl_stmt|;
try|try
block|{
return|return
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
operator|)
name|globalSettings
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
name|fullClassName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|fullClassName
operator|=
name|prefixPackage
operator|+
name|Strings
operator|.
name|capitalize
argument_list|(
name|toCamelCase
argument_list|(
name|type
argument_list|)
argument_list|)
operator|+
name|suffixClassName
expr_stmt|;
try|try
block|{
return|return
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
operator|)
name|globalSettings
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
name|fullClassName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e1
parameter_list|)
block|{
name|fullClassName
operator|=
name|prefixPackage
operator|+
name|toCamelCase
argument_list|(
name|type
argument_list|)
operator|+
literal|"."
operator|+
name|Strings
operator|.
name|capitalize
argument_list|(
name|toCamelCase
argument_list|(
name|type
argument_list|)
argument_list|)
operator|+
name|suffixClassName
expr_stmt|;
try|try
block|{
return|return
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
operator|)
name|globalSettings
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
name|fullClassName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e2
parameter_list|)
block|{
name|fullClassName
operator|=
name|prefixPackage
operator|+
name|toCamelCase
argument_list|(
name|type
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|+
literal|"."
operator|+
name|Strings
operator|.
name|capitalize
argument_list|(
name|toCamelCase
argument_list|(
name|type
argument_list|)
argument_list|)
operator|+
name|suffixClassName
expr_stmt|;
try|try
block|{
return|return
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
operator|)
name|globalSettings
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
name|fullClassName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e3
parameter_list|)
block|{
throw|throw
operator|new
name|NoClassSettingsException
argument_list|(
literal|"Failed to load class with value ["
operator|+
name|type
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

