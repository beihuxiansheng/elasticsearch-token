begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util.concurrent.highscalelib
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|highscalelib
package|;
end_package

begin_import
import|import
name|sun
operator|.
name|misc
operator|.
name|Unsafe
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
name|Field
import|;
end_import

begin_comment
comment|/**  * Simple class to obtain access to the {@link Unsafe} object.  {@link Unsafe}  * is required to allow efficient CAS operations on arrays.  Note that the  * versions in {@link java.util.concurrent.atomic}, such as {@link  * java.util.concurrent.atomic.AtomicLongArray}, require extra memory ordering  * guarantees which are generally not needed in these algorithms and are also  * expensive on most processors.  */
end_comment

begin_class
DECL|class|UtilUnsafe
class|class
name|UtilUnsafe
block|{
DECL|method|UtilUnsafe
specifier|private
name|UtilUnsafe
parameter_list|()
block|{     }
comment|// dummy private constructor
comment|/**      * Fetch the Unsafe.  Use With Caution.      */
DECL|method|getUnsafe
specifier|public
specifier|static
name|Unsafe
name|getUnsafe
parameter_list|()
block|{
comment|// Not on bootclasspath
if|if
condition|(
name|UtilUnsafe
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|==
literal|null
condition|)
return|return
name|Unsafe
operator|.
name|getUnsafe
argument_list|()
return|;
try|try
block|{
specifier|final
name|Field
name|fld
init|=
name|Unsafe
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"theUnsafe"
argument_list|)
decl_stmt|;
name|fld
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|(
name|Unsafe
operator|)
name|fld
operator|.
name|get
argument_list|(
name|UtilUnsafe
operator|.
name|class
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
name|RuntimeException
argument_list|(
literal|"Could not obtain access to sun.misc.Unsafe"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

