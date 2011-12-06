begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.versioned
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|versioned
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadSafe
import|;
end_import

begin_comment
comment|/**  * A versioned map, allowing to put version numbers associated with specific  * keys.  *<p/>  *<p>Note. versions can be assumed to be>= 0.  *  *  */
end_comment

begin_interface
annotation|@
name|ThreadSafe
DECL|interface|VersionedMap
specifier|public
interface|interface
name|VersionedMap
block|{
comment|/**      * Returns<tt>true</tt> if the versionToCheck is smaller than the current version      * associated with the key. If there is no version associated with the key, then      * it should return<tt>true</tt> as well.      */
DECL|method|beforeVersion
name|boolean
name|beforeVersion
parameter_list|(
name|int
name|key
parameter_list|,
name|int
name|versionToCheck
parameter_list|)
function_decl|;
comment|/**      * Puts (and replaces if it exists) the current key with the provided version.      */
DECL|method|putVersion
name|void
name|putVersion
parameter_list|(
name|int
name|key
parameter_list|,
name|int
name|version
parameter_list|)
function_decl|;
comment|/**      * Puts the version with the key only if it is absent.      */
DECL|method|putVersionIfAbsent
name|void
name|putVersionIfAbsent
parameter_list|(
name|int
name|key
parameter_list|,
name|int
name|version
parameter_list|)
function_decl|;
comment|/**      * Clears the map.      */
DECL|method|clear
name|void
name|clear
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

