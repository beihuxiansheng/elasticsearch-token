begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|Classes
specifier|public
class|class
name|Classes
block|{
comment|/**      * The package separator character '.'      */
DECL|field|PACKAGE_SEPARATOR
specifier|private
specifier|static
specifier|final
name|char
name|PACKAGE_SEPARATOR
init|=
literal|'.'
decl_stmt|;
comment|/**      * Return the default ClassLoader to use: typically the thread context      * ClassLoader, if available; the ClassLoader that loaded the ClassUtils      * class will be used as fallback.      *<p/>      *<p>Call this method if you intend to use the thread context ClassLoader      * in a scenario where you absolutely need a non-null ClassLoader reference:      * for example, for class path resource loading (but not necessarily for      *<code>Class.forName</code>, which accepts a<code>null</code> ClassLoader      * reference as well).      *      * @return the default ClassLoader (never<code>null</code>)      * @see java.lang.Thread#getContextClassLoader()      */
DECL|method|getDefaultClassLoader
specifier|public
specifier|static
name|ClassLoader
name|getDefaultClassLoader
parameter_list|()
block|{
name|ClassLoader
name|cl
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cl
operator|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
comment|// Cannot access thread context ClassLoader - falling back to system class loader...
block|}
if|if
condition|(
name|cl
operator|==
literal|null
condition|)
block|{
comment|// No thread context class loader -> use class loader of this class.
name|cl
operator|=
name|Classes
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
expr_stmt|;
block|}
return|return
name|cl
return|;
block|}
comment|/**      * Determine the name of the package of the given class:      * e.g. "java.lang" for the<code>java.lang.String</code> class.      *      * @param clazz the class      * @return the package name, or the empty String if the class      *         is defined in the default package      */
DECL|method|getPackageName
specifier|public
specifier|static
name|String
name|getPackageName
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
name|String
name|className
init|=
name|clazz
operator|.
name|getName
argument_list|()
decl_stmt|;
name|int
name|lastDotIndex
init|=
name|className
operator|.
name|lastIndexOf
argument_list|(
name|PACKAGE_SEPARATOR
argument_list|)
decl_stmt|;
return|return
operator|(
name|lastDotIndex
operator|!=
operator|-
literal|1
condition|?
name|className
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lastDotIndex
argument_list|)
else|:
literal|""
operator|)
return|;
block|}
DECL|method|getPackageNameNoDomain
specifier|public
specifier|static
name|String
name|getPackageNameNoDomain
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
name|String
name|fullPackage
init|=
name|getPackageName
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|fullPackage
operator|.
name|startsWith
argument_list|(
literal|"org."
argument_list|)
operator|||
name|fullPackage
operator|.
name|startsWith
argument_list|(
literal|"com."
argument_list|)
operator|||
name|fullPackage
operator|.
name|startsWith
argument_list|(
literal|"net."
argument_list|)
condition|)
block|{
return|return
name|fullPackage
operator|.
name|substring
argument_list|(
literal|4
argument_list|)
return|;
block|}
return|return
name|fullPackage
return|;
block|}
DECL|method|isInnerClass
specifier|public
specifier|static
name|boolean
name|isInnerClass
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
operator|!
name|Modifier
operator|.
name|isStatic
argument_list|(
name|clazz
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
name|clazz
operator|.
name|getEnclosingClass
argument_list|()
operator|!=
literal|null
return|;
block|}
DECL|method|isConcrete
specifier|public
specifier|static
name|boolean
name|isConcrete
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
name|int
name|modifiers
init|=
name|clazz
operator|.
name|getModifiers
argument_list|()
decl_stmt|;
return|return
operator|!
name|clazz
operator|.
name|isInterface
argument_list|()
operator|&&
operator|!
name|Modifier
operator|.
name|isAbstract
argument_list|(
name|modifiers
argument_list|)
return|;
block|}
DECL|method|Classes
specifier|private
name|Classes
parameter_list|()
block|{      }
block|}
end_class

end_unit

