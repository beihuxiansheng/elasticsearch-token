begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch
package|package
name|org
operator|.
name|elasticsearch
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|BasicPermission
import|;
end_import

begin_comment
comment|/**  * Elasticsearch-specific permission to check before entering  * {@code AccessController.doPrivileged()} blocks.  *<p>  * We try to avoid these blocks in our code and keep security simple,  * but we need them for a few special places to contain hacks for third  * party code, or dangerous things used by scripting engines.  *<p>  * All normal code has this permission, but checking this before truncating the stack  * prevents unprivileged code (e.g. scripts), which do not have it, from gaining elevated  * privileges.  *<p>  * In other words, don't do this:  *<br>  *<pre><code>  *   // throw away all information about caller and run with our own privs  *   AccessController.doPrivileged(  *    ...  *   );  *</code></pre>  *<br>  * Instead do this;  *<br>  *<pre><code>  *   // check caller first, to see if they should be allowed to do this  *   SecurityManager sm = System.getSecurityManager();  *   if (sm != null) {  *     sm.checkPermission(new SpecialPermission());  *   }  *   // throw away all information about caller and run with our own privs  *   AccessController.doPrivileged(  *    ...  *   );  *</code></pre>  */
end_comment

begin_class
DECL|class|SpecialPermission
specifier|public
specifier|final
class|class
name|SpecialPermission
extends|extends
name|BasicPermission
block|{
comment|/**      * Creates a new SpecialPermision object.      */
DECL|method|SpecialPermission
specifier|public
name|SpecialPermission
parameter_list|()
block|{
comment|// TODO: if we really need we can break out name (e.g. "hack" or "scriptEngineService" or whatever).
comment|// but let's just keep it simple if we can.
name|super
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new SpecialPermission object.      * This constructor exists for use by the {@code Policy} object to instantiate new Permission objects.      *      * @param name ignored      * @param actions ignored      */
DECL|method|SpecialPermission
specifier|public
name|SpecialPermission
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|actions
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

