begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2006 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|common
operator|.
name|inject
operator|.
name|spi
operator|.
name|InjectionPoint
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

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
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
name|Modifier
import|;
end_import

begin_comment
comment|/**  * Produces construction proxies that invoke the class constructor.  *  * @author crazybob@google.com (Bob Lee)  */
end_comment

begin_class
DECL|class|DefaultConstructionProxyFactory
class|class
name|DefaultConstructionProxyFactory
parameter_list|<
name|T
parameter_list|>
implements|implements
name|ConstructionProxyFactory
argument_list|<
name|T
argument_list|>
block|{
DECL|field|injectionPoint
specifier|private
specifier|final
name|InjectionPoint
name|injectionPoint
decl_stmt|;
comment|/**      * @param injectionPoint an injection point whose member is a constructor of {@code T}.      */
DECL|method|DefaultConstructionProxyFactory
name|DefaultConstructionProxyFactory
parameter_list|(
name|InjectionPoint
name|injectionPoint
parameter_list|)
block|{
name|this
operator|.
name|injectionPoint
operator|=
name|injectionPoint
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|ConstructionProxy
argument_list|<
name|T
argument_list|>
name|create
parameter_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// the injection point is for a constructor of T
specifier|final
name|Constructor
argument_list|<
name|T
argument_list|>
name|constructor
init|=
operator|(
name|Constructor
argument_list|<
name|T
argument_list|>
operator|)
name|injectionPoint
operator|.
name|getMember
argument_list|()
decl_stmt|;
comment|// Use FastConstructor if the constructor is public.
if|if
condition|(
name|Modifier
operator|.
name|isPublic
argument_list|(
name|constructor
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{         }
else|else
block|{
name|constructor
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ConstructionProxy
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|T
name|newInstance
parameter_list|(
name|Object
modifier|...
name|arguments
parameter_list|)
throws|throws
name|InvocationTargetException
block|{
try|try
block|{
return|return
name|constructor
operator|.
name|newInstance
argument_list|(
name|arguments
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|e
argument_list|)
throw|;
comment|// shouldn't happen, we know this is a concrete type
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|e
argument_list|)
throw|;
comment|// a security manager is blocking us, we're hosed
block|}
block|}
annotation|@
name|Override
specifier|public
name|InjectionPoint
name|getInjectionPoint
parameter_list|()
block|{
return|return
name|injectionPoint
return|;
block|}
annotation|@
name|Override
specifier|public
name|Constructor
argument_list|<
name|T
argument_list|>
name|getConstructor
parameter_list|()
block|{
return|return
name|constructor
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

