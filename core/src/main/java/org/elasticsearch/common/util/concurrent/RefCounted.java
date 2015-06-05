begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util.concurrent
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
package|;
end_package

begin_comment
comment|/**  *  An interface for objects that need to be notified when all reference  *  to itself are not in user anymore. This implements basic reference counting  *  for instance if async operations holding on to services that are close concurrently  *  but should be functional until all async operations have joined  *  Classes implementing this interface should ref counted at any time ie. if an object is used it's reference count should  *  be increased before using it by calling #incRef and a corresponding #decRef must be called in a try/finally  *  block to release the object again ie.:  *<pre>  *      inst.incRef();  *      try {  *        // use the inst...  *  *      } finally {  *          inst.decRef();  *      }  *</pre>  */
end_comment

begin_interface
DECL|interface|RefCounted
specifier|public
interface|interface
name|RefCounted
block|{
comment|/**      * Increments the refCount of this instance.      *      * @see #decRef      * @see #tryIncRef()      * @throws org.apache.lucene.store.AlreadyClosedException iff the reference counter can not be incremented.      */
DECL|method|incRef
name|void
name|incRef
parameter_list|()
function_decl|;
comment|/**      * Tries to increment the refCount of this instance. This method will return<tt>true</tt> iff the refCount was      *      * @see #decRef()      * @see #incRef()      */
DECL|method|tryIncRef
name|boolean
name|tryIncRef
parameter_list|()
function_decl|;
comment|/**      * Decreases the refCount of this  instance. If the refCount drops to 0, then this      * instance is considered as closed and should not be used anymore.      *      * @see #incRef      */
DECL|method|decRef
name|void
name|decRef
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

