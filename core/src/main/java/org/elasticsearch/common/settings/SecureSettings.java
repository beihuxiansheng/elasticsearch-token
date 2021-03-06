begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.settings
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|GeneralSecurityException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * An accessor for settings which are securely stored. See {@link SecureSetting}.  */
end_comment

begin_interface
DECL|interface|SecureSettings
specifier|public
interface|interface
name|SecureSettings
extends|extends
name|Closeable
block|{
comment|/** Returns true iff the settings are loaded and retrievable. */
DECL|method|isLoaded
name|boolean
name|isLoaded
parameter_list|()
function_decl|;
comment|/** Returns the names of all secure settings available. */
DECL|method|getSettingNames
name|Set
argument_list|<
name|String
argument_list|>
name|getSettingNames
parameter_list|()
function_decl|;
comment|/** Return a string setting. The {@link SecureString} should be closed once it is used. */
DECL|method|getString
name|SecureString
name|getString
parameter_list|(
name|String
name|setting
parameter_list|)
throws|throws
name|GeneralSecurityException
function_decl|;
comment|/** Return a file setting. The {@link InputStream} should be closed once it is used. */
DECL|method|getFile
name|InputStream
name|getFile
parameter_list|(
name|String
name|setting
parameter_list|)
throws|throws
name|GeneralSecurityException
function_decl|;
block|}
end_interface

end_unit

