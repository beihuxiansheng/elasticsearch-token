begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.bootstrap
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
package|;
end_package

begin_comment
comment|/**   * Exposes system startup information   */
end_comment

begin_class
DECL|class|BootstrapInfo
specifier|public
specifier|final
class|class
name|BootstrapInfo
block|{
comment|/** no instantiation */
DECL|method|BootstrapInfo
specifier|private
name|BootstrapInfo
parameter_list|()
block|{}
comment|/**       * Returns true if we successfully loaded native libraries.      *<p>      * If this returns false, then native operations such as locking      * memory did not work.      */
DECL|method|isNativesAvailable
specifier|public
specifier|static
name|boolean
name|isNativesAvailable
parameter_list|()
block|{
return|return
name|Natives
operator|.
name|JNA_AVAILABLE
return|;
block|}
comment|/**       * Returns true if we were able to lock the process's address space.      */
DECL|method|isMemoryLocked
specifier|public
specifier|static
name|boolean
name|isMemoryLocked
parameter_list|()
block|{
return|return
name|Natives
operator|.
name|isMemoryLocked
argument_list|()
return|;
block|}
comment|/**      * Returns true if secure computing mode is enabled (linux/amd64 only)      */
DECL|method|isSeccompInstalled
specifier|public
specifier|static
name|boolean
name|isSeccompInstalled
parameter_list|()
block|{
return|return
name|Natives
operator|.
name|isSeccompInstalled
argument_list|()
return|;
block|}
block|}
end_class

end_unit

