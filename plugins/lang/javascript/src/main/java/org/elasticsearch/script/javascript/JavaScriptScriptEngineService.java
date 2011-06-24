begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script.javascript
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|javascript
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
name|IndexReader
import|;
end_import

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
name|component
operator|.
name|AbstractComponent
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
name|Inject
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
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ExecutableScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptEngineService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|SearchScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|javascript
operator|.
name|support
operator|.
name|NativeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|javascript
operator|.
name|support
operator|.
name|NativeMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|javascript
operator|.
name|support
operator|.
name|ScriptValueConverter
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
name|org
operator|.
name|mozilla
operator|.
name|javascript
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mozilla
operator|.
name|javascript
operator|.
name|Script
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mozilla
operator|.
name|javascript
operator|.
name|Scriptable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mozilla
operator|.
name|javascript
operator|.
name|ScriptableObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mozilla
operator|.
name|javascript
operator|.
name|WrapFactory
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|JavaScriptScriptEngineService
specifier|public
class|class
name|JavaScriptScriptEngineService
extends|extends
name|AbstractComponent
implements|implements
name|ScriptEngineService
block|{
DECL|field|counter
specifier|private
specifier|final
name|AtomicLong
name|counter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|wrapFactory
specifier|private
specifier|static
name|WrapFactory
name|wrapFactory
init|=
operator|new
name|CustomWrapFactory
argument_list|()
decl_stmt|;
DECL|field|optimizationLevel
specifier|private
specifier|final
name|int
name|optimizationLevel
decl_stmt|;
DECL|field|globalScope
specifier|private
name|Scriptable
name|globalScope
decl_stmt|;
DECL|method|JavaScriptScriptEngineService
annotation|@
name|Inject
specifier|public
name|JavaScriptScriptEngineService
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|optimizationLevel
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"optimization_level"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Context
name|ctx
init|=
name|Context
operator|.
name|enter
argument_list|()
decl_stmt|;
try|try
block|{
name|ctx
operator|.
name|setWrapFactory
argument_list|(
name|wrapFactory
argument_list|)
expr_stmt|;
name|globalScope
operator|=
name|ctx
operator|.
name|initStandardObjects
argument_list|(
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Context
operator|.
name|exit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{      }
DECL|method|types
annotation|@
name|Override
specifier|public
name|String
index|[]
name|types
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"js"
block|,
literal|"javascript"
block|}
return|;
block|}
DECL|method|extensions
annotation|@
name|Override
specifier|public
name|String
index|[]
name|extensions
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"js"
block|}
return|;
block|}
DECL|method|compile
annotation|@
name|Override
specifier|public
name|Object
name|compile
parameter_list|(
name|String
name|script
parameter_list|)
block|{
name|Context
name|ctx
init|=
name|Context
operator|.
name|enter
argument_list|()
decl_stmt|;
try|try
block|{
name|ctx
operator|.
name|setWrapFactory
argument_list|(
name|wrapFactory
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|setOptimizationLevel
argument_list|(
name|optimizationLevel
argument_list|)
expr_stmt|;
return|return
name|ctx
operator|.
name|compileString
argument_list|(
name|script
argument_list|,
name|generateScriptName
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
return|;
block|}
finally|finally
block|{
name|Context
operator|.
name|exit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|executable
annotation|@
name|Override
specifier|public
name|ExecutableScript
name|executable
parameter_list|(
name|Object
name|compiledScript
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|)
block|{
name|Context
name|ctx
init|=
name|Context
operator|.
name|enter
argument_list|()
decl_stmt|;
try|try
block|{
name|ctx
operator|.
name|setWrapFactory
argument_list|(
name|wrapFactory
argument_list|)
expr_stmt|;
name|Scriptable
name|scope
init|=
name|ctx
operator|.
name|newObject
argument_list|(
name|globalScope
argument_list|)
decl_stmt|;
name|scope
operator|.
name|setPrototype
argument_list|(
name|globalScope
argument_list|)
expr_stmt|;
name|scope
operator|.
name|setParentScope
argument_list|(
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|vars
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ScriptableObject
operator|.
name|putProperty
argument_list|(
name|scope
argument_list|,
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
return|return
operator|new
name|JavaScriptExecutableScript
argument_list|(
operator|(
name|Script
operator|)
name|compiledScript
argument_list|,
name|scope
argument_list|)
return|;
block|}
finally|finally
block|{
name|Context
operator|.
name|exit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|search
annotation|@
name|Override
specifier|public
name|SearchScript
name|search
parameter_list|(
name|Object
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
name|Context
name|ctx
init|=
name|Context
operator|.
name|enter
argument_list|()
decl_stmt|;
try|try
block|{
name|ctx
operator|.
name|setWrapFactory
argument_list|(
name|wrapFactory
argument_list|)
expr_stmt|;
name|Scriptable
name|scope
init|=
name|ctx
operator|.
name|newObject
argument_list|(
name|globalScope
argument_list|)
decl_stmt|;
name|scope
operator|.
name|setPrototype
argument_list|(
name|globalScope
argument_list|)
expr_stmt|;
name|scope
operator|.
name|setParentScope
argument_list|(
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|lookup
operator|.
name|asMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ScriptableObject
operator|.
name|putProperty
argument_list|(
name|scope
argument_list|,
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
if|if
condition|(
name|vars
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|vars
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ScriptableObject
operator|.
name|putProperty
argument_list|(
name|scope
argument_list|,
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
return|return
operator|new
name|JavaScriptSearchScript
argument_list|(
operator|(
name|Script
operator|)
name|compiledScript
argument_list|,
name|scope
argument_list|,
name|lookup
argument_list|)
return|;
block|}
finally|finally
block|{
name|Context
operator|.
name|exit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|execute
annotation|@
name|Override
specifier|public
name|Object
name|execute
parameter_list|(
name|Object
name|compiledScript
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|vars
parameter_list|)
block|{
name|Context
name|ctx
init|=
name|Context
operator|.
name|enter
argument_list|()
decl_stmt|;
name|ctx
operator|.
name|setWrapFactory
argument_list|(
name|wrapFactory
argument_list|)
expr_stmt|;
try|try
block|{
name|Script
name|script
init|=
operator|(
name|Script
operator|)
name|compiledScript
decl_stmt|;
name|Scriptable
name|scope
init|=
name|ctx
operator|.
name|newObject
argument_list|(
name|globalScope
argument_list|)
decl_stmt|;
name|scope
operator|.
name|setPrototype
argument_list|(
name|globalScope
argument_list|)
expr_stmt|;
name|scope
operator|.
name|setParentScope
argument_list|(
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|vars
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ScriptableObject
operator|.
name|putProperty
argument_list|(
name|scope
argument_list|,
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
name|Object
name|ret
init|=
name|script
operator|.
name|exec
argument_list|(
name|ctx
argument_list|,
name|scope
argument_list|)
decl_stmt|;
return|return
name|ScriptValueConverter
operator|.
name|unwrapValue
argument_list|(
name|ret
argument_list|)
return|;
block|}
finally|finally
block|{
name|Context
operator|.
name|exit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|unwrap
annotation|@
name|Override
specifier|public
name|Object
name|unwrap
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|ScriptValueConverter
operator|.
name|unwrapValue
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|generateScriptName
specifier|private
name|String
name|generateScriptName
parameter_list|()
block|{
return|return
literal|"Script"
operator|+
name|counter
operator|.
name|incrementAndGet
argument_list|()
operator|+
literal|".js"
return|;
block|}
DECL|class|JavaScriptExecutableScript
specifier|public
specifier|static
class|class
name|JavaScriptExecutableScript
implements|implements
name|ExecutableScript
block|{
DECL|field|script
specifier|private
specifier|final
name|Script
name|script
decl_stmt|;
DECL|field|scope
specifier|private
specifier|final
name|Scriptable
name|scope
decl_stmt|;
DECL|method|JavaScriptExecutableScript
specifier|public
name|JavaScriptExecutableScript
parameter_list|(
name|Script
name|script
parameter_list|,
name|Scriptable
name|scope
parameter_list|)
block|{
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
block|}
DECL|method|run
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
block|{
name|Context
name|ctx
init|=
name|Context
operator|.
name|enter
argument_list|()
decl_stmt|;
try|try
block|{
name|ctx
operator|.
name|setWrapFactory
argument_list|(
name|wrapFactory
argument_list|)
expr_stmt|;
return|return
name|ScriptValueConverter
operator|.
name|unwrapValue
argument_list|(
name|script
operator|.
name|exec
argument_list|(
name|ctx
argument_list|,
name|scope
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|Context
operator|.
name|exit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setNextVar
annotation|@
name|Override
specifier|public
name|void
name|setNextVar
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|ScriptableObject
operator|.
name|putProperty
argument_list|(
name|scope
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|unwrap
annotation|@
name|Override
specifier|public
name|Object
name|unwrap
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|ScriptValueConverter
operator|.
name|unwrapValue
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
DECL|class|JavaScriptSearchScript
specifier|public
specifier|static
class|class
name|JavaScriptSearchScript
implements|implements
name|SearchScript
block|{
DECL|field|script
specifier|private
specifier|final
name|Script
name|script
decl_stmt|;
DECL|field|scope
specifier|private
specifier|final
name|Scriptable
name|scope
decl_stmt|;
DECL|field|lookup
specifier|private
specifier|final
name|SearchLookup
name|lookup
decl_stmt|;
DECL|method|JavaScriptSearchScript
specifier|public
name|JavaScriptSearchScript
parameter_list|(
name|Script
name|script
parameter_list|,
name|Scriptable
name|scope
parameter_list|,
name|SearchLookup
name|lookup
parameter_list|)
block|{
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
name|this
operator|.
name|lookup
operator|=
name|lookup
expr_stmt|;
block|}
DECL|method|setScorer
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|lookup
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
DECL|method|setNextReader
annotation|@
name|Override
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|lookup
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|setNextDocId
annotation|@
name|Override
specifier|public
name|void
name|setNextDocId
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|lookup
operator|.
name|setNextDocId
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|setNextScore
annotation|@
name|Override
specifier|public
name|void
name|setNextScore
parameter_list|(
name|float
name|score
parameter_list|)
block|{
name|ScriptableObject
operator|.
name|putProperty
argument_list|(
name|scope
argument_list|,
literal|"_score"
argument_list|,
name|score
argument_list|)
expr_stmt|;
block|}
DECL|method|setNextVar
annotation|@
name|Override
specifier|public
name|void
name|setNextVar
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|ScriptableObject
operator|.
name|putProperty
argument_list|(
name|scope
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|setNextSource
annotation|@
name|Override
specifier|public
name|void
name|setNextSource
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
parameter_list|)
block|{
name|lookup
operator|.
name|source
argument_list|()
operator|.
name|setNextSource
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
DECL|method|run
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
block|{
name|Context
name|ctx
init|=
name|Context
operator|.
name|enter
argument_list|()
decl_stmt|;
try|try
block|{
name|ctx
operator|.
name|setWrapFactory
argument_list|(
name|wrapFactory
argument_list|)
expr_stmt|;
return|return
name|ScriptValueConverter
operator|.
name|unwrapValue
argument_list|(
name|script
operator|.
name|exec
argument_list|(
name|ctx
argument_list|,
name|scope
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|Context
operator|.
name|exit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|runAsFloat
annotation|@
name|Override
specifier|public
name|float
name|runAsFloat
parameter_list|()
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|run
argument_list|()
operator|)
operator|.
name|floatValue
argument_list|()
return|;
block|}
DECL|method|runAsLong
annotation|@
name|Override
specifier|public
name|long
name|runAsLong
parameter_list|()
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|run
argument_list|()
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
DECL|method|runAsDouble
annotation|@
name|Override
specifier|public
name|double
name|runAsDouble
parameter_list|()
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|run
argument_list|()
operator|)
operator|.
name|doubleValue
argument_list|()
return|;
block|}
DECL|method|unwrap
annotation|@
name|Override
specifier|public
name|Object
name|unwrap
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|ScriptValueConverter
operator|.
name|unwrapValue
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
comment|/**      * Wrap Factory for Rhino Script Engine      */
DECL|class|CustomWrapFactory
specifier|public
specifier|static
class|class
name|CustomWrapFactory
extends|extends
name|WrapFactory
block|{
DECL|method|CustomWrapFactory
specifier|public
name|CustomWrapFactory
parameter_list|()
block|{
name|setJavaPrimitiveWrap
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// RingoJS does that..., claims its annoying...
block|}
DECL|method|wrapAsJavaObject
specifier|public
name|Scriptable
name|wrapAsJavaObject
parameter_list|(
name|Context
name|cx
parameter_list|,
name|Scriptable
name|scope
parameter_list|,
name|Object
name|javaObject
parameter_list|,
name|Class
name|staticType
parameter_list|)
block|{
if|if
condition|(
name|javaObject
operator|instanceof
name|Map
condition|)
block|{
return|return
operator|new
name|NativeMap
argument_list|(
name|scope
argument_list|,
operator|(
name|Map
operator|)
name|javaObject
argument_list|)
return|;
block|}
if|if
condition|(
name|javaObject
operator|instanceof
name|List
condition|)
block|{
return|return
operator|new
name|NativeList
argument_list|(
name|scope
argument_list|,
operator|(
name|List
operator|)
name|javaObject
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|wrapAsJavaObject
argument_list|(
name|cx
argument_list|,
name|scope
argument_list|,
name|javaObject
argument_list|,
name|staticType
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

