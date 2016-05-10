begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|CallSite
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandle
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
operator|.
name|Lookup
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MutableCallSite
import|;
end_import

begin_comment
comment|/**  * Painless invokedynamic call site.  *<p>  * Has 3 flavors (passed as static bootstrap parameters): dynamic method call,  * dynamic field load (getter), and dynamic field store (setter).  *<p>  * When a new type is encountered at the call site, we lookup from the appropriate  * whitelist, and cache with a guard. If we encounter too many types, we stop caching.  *<p>  * Based on the cascaded inlining cache from the JSR 292 cookbook   * (https://code.google.com/archive/p/jsr292-cookbook/, BSD license)  */
end_comment

begin_comment
comment|// NOTE: this class must be public, because generated painless classes are in a different package,
end_comment

begin_comment
comment|// and it needs to be accessible by that code.
end_comment

begin_class
DECL|class|DynamicCallSite
specifier|public
specifier|final
class|class
name|DynamicCallSite
block|{
comment|// NOTE: these must be primitive types, see https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.invokedynamic
comment|/** static bootstrap parameter indicating a dynamic method call, e.g. foo.bar(...) */
DECL|field|METHOD_CALL
specifier|static
specifier|final
name|int
name|METHOD_CALL
init|=
literal|0
decl_stmt|;
comment|/** static bootstrap parameter indicating a dynamic load (getter), e.g. baz = foo.bar */
DECL|field|LOAD
specifier|static
specifier|final
name|int
name|LOAD
init|=
literal|1
decl_stmt|;
comment|/** static bootstrap parameter indicating a dynamic store (setter), e.g. foo.bar = baz */
DECL|field|STORE
specifier|static
specifier|final
name|int
name|STORE
init|=
literal|2
decl_stmt|;
DECL|class|InliningCacheCallSite
specifier|static
class|class
name|InliningCacheCallSite
extends|extends
name|MutableCallSite
block|{
comment|/** maximum number of types before we go megamorphic */
DECL|field|MAX_DEPTH
specifier|static
specifier|final
name|int
name|MAX_DEPTH
init|=
literal|5
decl_stmt|;
DECL|field|lookup
specifier|final
name|Lookup
name|lookup
decl_stmt|;
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|flavor
specifier|final
name|int
name|flavor
decl_stmt|;
DECL|field|depth
name|int
name|depth
decl_stmt|;
DECL|method|InliningCacheCallSite
name|InliningCacheCallSite
parameter_list|(
name|Lookup
name|lookup
parameter_list|,
name|String
name|name
parameter_list|,
name|MethodType
name|type
parameter_list|,
name|int
name|flavor
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|lookup
operator|=
name|lookup
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|flavor
operator|=
name|flavor
expr_stmt|;
block|}
block|}
comment|/**       * invokeDynamic bootstrap method      *<p>      * In addition to ordinary parameters, we also take a static parameter {@code flavor} which      * tells us what type of dynamic call it is (and which part of whitelist to look at).      *<p>      * see https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html#jvms-6.5.invokedynamic      */
DECL|method|bootstrap
specifier|public
specifier|static
name|CallSite
name|bootstrap
parameter_list|(
name|Lookup
name|lookup
parameter_list|,
name|String
name|name
parameter_list|,
name|MethodType
name|type
parameter_list|,
name|int
name|flavor
parameter_list|)
block|{
name|InliningCacheCallSite
name|callSite
init|=
operator|new
name|InliningCacheCallSite
argument_list|(
name|lookup
argument_list|,
name|name
argument_list|,
name|type
argument_list|,
name|flavor
argument_list|)
decl_stmt|;
name|MethodHandle
name|fallback
init|=
name|FALLBACK
operator|.
name|bindTo
argument_list|(
name|callSite
argument_list|)
decl_stmt|;
name|fallback
operator|=
name|fallback
operator|.
name|asCollector
argument_list|(
name|Object
index|[]
operator|.
expr|class
argument_list|,
name|type
operator|.
name|parameterCount
argument_list|()
argument_list|)
expr_stmt|;
name|fallback
operator|=
name|fallback
operator|.
name|asType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|callSite
operator|.
name|setTarget
argument_list|(
name|fallback
argument_list|)
expr_stmt|;
return|return
name|callSite
return|;
block|}
comment|/**       * guard method for inline caching: checks the receiver's class is the same      * as the cached class      */
DECL|method|checkClass
specifier|static
name|boolean
name|checkClass
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|Object
name|receiver
parameter_list|)
block|{
return|return
name|receiver
operator|.
name|getClass
argument_list|()
operator|==
name|clazz
return|;
block|}
comment|/**      * Does a slow lookup against the whitelist.      */
DECL|method|lookup
specifier|private
specifier|static
name|MethodHandle
name|lookup
parameter_list|(
name|int
name|flavor
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|String
name|name
parameter_list|)
block|{
switch|switch
condition|(
name|flavor
condition|)
block|{
case|case
name|METHOD_CALL
case|:
return|return
name|Def
operator|.
name|lookupMethod
argument_list|(
name|clazz
argument_list|,
name|name
argument_list|,
name|Definition
operator|.
name|INSTANCE
argument_list|)
return|;
case|case
name|LOAD
case|:
return|return
name|Def
operator|.
name|lookupGetter
argument_list|(
name|clazz
argument_list|,
name|name
argument_list|,
name|Definition
operator|.
name|INSTANCE
argument_list|)
return|;
case|case
name|STORE
case|:
return|return
name|Def
operator|.
name|lookupSetter
argument_list|(
name|clazz
argument_list|,
name|name
argument_list|,
name|Definition
operator|.
name|INSTANCE
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
comment|/**      * Called when a new type is encountered (or, when we have encountered more than {@code MAX_DEPTH}      * types at this call site and given up on caching).       */
DECL|method|fallback
specifier|static
name|Object
name|fallback
parameter_list|(
name|InliningCacheCallSite
name|callSite
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|MethodType
name|type
init|=
name|callSite
operator|.
name|type
argument_list|()
decl_stmt|;
name|Object
name|receiver
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|receiverClass
init|=
name|receiver
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|MethodHandle
name|target
init|=
name|lookup
argument_list|(
name|callSite
operator|.
name|flavor
argument_list|,
name|receiverClass
argument_list|,
name|callSite
operator|.
name|name
argument_list|)
decl_stmt|;
name|target
operator|=
name|target
operator|.
name|asType
argument_list|(
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|callSite
operator|.
name|depth
operator|>=
name|InliningCacheCallSite
operator|.
name|MAX_DEPTH
condition|)
block|{
comment|// revert to a vtable call
name|callSite
operator|.
name|setTarget
argument_list|(
name|target
argument_list|)
expr_stmt|;
return|return
name|target
operator|.
name|invokeWithArguments
argument_list|(
name|args
argument_list|)
return|;
block|}
name|MethodHandle
name|test
init|=
name|CHECK_CLASS
operator|.
name|bindTo
argument_list|(
name|receiverClass
argument_list|)
decl_stmt|;
name|test
operator|=
name|test
operator|.
name|asType
argument_list|(
name|test
operator|.
name|type
argument_list|()
operator|.
name|changeParameterType
argument_list|(
literal|0
argument_list|,
name|type
operator|.
name|parameterType
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|MethodHandle
name|guard
init|=
name|MethodHandles
operator|.
name|guardWithTest
argument_list|(
name|test
argument_list|,
name|target
argument_list|,
name|callSite
operator|.
name|getTarget
argument_list|()
argument_list|)
decl_stmt|;
name|callSite
operator|.
name|depth
operator|++
expr_stmt|;
name|callSite
operator|.
name|setTarget
argument_list|(
name|guard
argument_list|)
expr_stmt|;
return|return
name|target
operator|.
name|invokeWithArguments
argument_list|(
name|args
argument_list|)
return|;
block|}
DECL|field|CHECK_CLASS
specifier|private
specifier|static
specifier|final
name|MethodHandle
name|CHECK_CLASS
decl_stmt|;
DECL|field|FALLBACK
specifier|private
specifier|static
specifier|final
name|MethodHandle
name|FALLBACK
decl_stmt|;
static|static
block|{
name|Lookup
name|lookup
init|=
name|MethodHandles
operator|.
name|lookup
argument_list|()
decl_stmt|;
try|try
block|{
name|CHECK_CLASS
operator|=
name|lookup
operator|.
name|findStatic
argument_list|(
name|DynamicCallSite
operator|.
name|class
argument_list|,
literal|"checkClass"
argument_list|,
name|MethodType
operator|.
name|methodType
argument_list|(
name|boolean
operator|.
name|class
argument_list|,
name|Class
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|FALLBACK
operator|=
name|lookup
operator|.
name|findStatic
argument_list|(
name|DynamicCallSite
operator|.
name|class
argument_list|,
literal|"fallback"
argument_list|,
name|MethodType
operator|.
name|methodType
argument_list|(
name|Object
operator|.
name|class
argument_list|,
name|InliningCacheCallSite
operator|.
name|class
argument_list|,
name|Object
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ReflectiveOperationException
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
block|}
block|}
block|}
end_class

end_unit

