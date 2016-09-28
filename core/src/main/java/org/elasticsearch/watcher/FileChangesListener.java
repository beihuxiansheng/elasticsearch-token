begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.watcher
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|watcher
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_comment
comment|/**  * Callback interface that file changes File Watcher is using to notify listeners about changes.  */
end_comment

begin_interface
DECL|interface|FileChangesListener
specifier|public
interface|interface
name|FileChangesListener
block|{
comment|/**      * Called for every file found in the watched directory during initialization      */
DECL|method|onFileInit
specifier|default
name|void
name|onFileInit
parameter_list|(
name|Path
name|file
parameter_list|)
block|{}
comment|/**      * Called for every subdirectory found in the watched directory during initialization      */
DECL|method|onDirectoryInit
specifier|default
name|void
name|onDirectoryInit
parameter_list|(
name|Path
name|file
parameter_list|)
block|{}
comment|/**      * Called for every new file found in the watched directory      */
DECL|method|onFileCreated
specifier|default
name|void
name|onFileCreated
parameter_list|(
name|Path
name|file
parameter_list|)
block|{}
comment|/**      * Called for every file that disappeared in the watched directory      */
DECL|method|onFileDeleted
specifier|default
name|void
name|onFileDeleted
parameter_list|(
name|Path
name|file
parameter_list|)
block|{}
comment|/**      * Called for every file that was changed in the watched directory      */
DECL|method|onFileChanged
specifier|default
name|void
name|onFileChanged
parameter_list|(
name|Path
name|file
parameter_list|)
block|{}
comment|/**      * Called for every new subdirectory found in the watched directory      */
DECL|method|onDirectoryCreated
specifier|default
name|void
name|onDirectoryCreated
parameter_list|(
name|Path
name|file
parameter_list|)
block|{}
comment|/**      * Called for every file that disappeared in the watched directory      */
DECL|method|onDirectoryDeleted
specifier|default
name|void
name|onDirectoryDeleted
parameter_list|(
name|Path
name|file
parameter_list|)
block|{}
block|}
end_interface

end_unit

