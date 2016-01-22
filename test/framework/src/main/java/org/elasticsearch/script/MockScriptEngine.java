begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
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
name|index
operator|.
name|LeafReaderContext
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
name|bytes
operator|.
name|BytesArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
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
name|SearchLookup
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
name|util
operator|.
name|Collections
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

begin_comment
comment|/**  * A dummy script engine used for testing. Scripts must be a number. Running the script  */
end_comment

begin_class
DECL|class|MockScriptEngine
specifier|public
class|class
name|MockScriptEngine
implements|implements
name|ScriptEngineService
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"mockscript"
decl_stmt|;
DECL|field|TYPES
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|TYPES
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
DECL|class|TestPlugin
specifier|public
specifier|static
class|class
name|TestPlugin
extends|extends
name|Plugin
block|{
DECL|method|TestPlugin
specifier|public
name|TestPlugin
parameter_list|()
block|{         }
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"Mock script engine for integration tests"
return|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|ScriptModule
name|module
parameter_list|)
block|{
name|module
operator|.
name|addScriptEngine
argument_list|(
operator|new
name|ScriptEngineRegistry
operator|.
name|ScriptEngineRegistration
argument_list|(
name|MockScriptEngine
operator|.
name|class
argument_list|,
name|MockScriptEngine
operator|.
name|TYPES
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|types
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|types
parameter_list|()
block|{
return|return
name|TYPES
return|;
block|}
annotation|@
name|Override
DECL|method|extensions
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|extensions
parameter_list|()
block|{
return|return
name|TYPES
return|;
block|}
annotation|@
name|Override
DECL|method|sandboxed
specifier|public
name|boolean
name|sandboxed
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|compile
specifier|public
name|Object
name|compile
parameter_list|(
name|String
name|script
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
block|{
return|return
name|script
return|;
block|}
annotation|@
name|Override
DECL|method|executable
specifier|public
name|ExecutableScript
name|executable
parameter_list|(
name|CompiledScript
name|compiledScript
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|)
block|{
return|return
operator|new
name|AbstractExecutableScript
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
block|{
return|return
operator|new
name|BytesArray
argument_list|(
operator|(
name|String
operator|)
name|compiledScript
operator|.
name|compiled
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|search
specifier|public
name|SearchScript
name|search
parameter_list|(
name|CompiledScript
name|compiledScript
parameter_list|,
name|SearchLookup
name|lookup
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|)
block|{
return|return
operator|new
name|SearchScript
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|LeafSearchScript
name|getLeafSearchScript
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|AbstractSearchScript
name|leafSearchScript
init|=
operator|new
name|AbstractSearchScript
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
block|{
return|return
name|compiledScript
operator|.
name|compiled
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|leafSearchScript
operator|.
name|setLookup
argument_list|(
name|lookup
operator|.
name|getLeafSearchLookup
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|leafSearchScript
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|scriptRemoved
specifier|public
name|void
name|scriptRemoved
parameter_list|(
annotation|@
name|Nullable
name|CompiledScript
name|script
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{     }
block|}
end_class

end_unit

