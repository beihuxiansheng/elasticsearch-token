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
name|EnumSet
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Booleans
import|;
end_import

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
name|ArrayUtils
import|;
end_import

begin_comment
comment|/**  * A secure setting.  *  * This class allows access to settings from the Elasticsearch keystore.  */
end_comment

begin_class
DECL|class|SecureSetting
specifier|public
specifier|abstract
class|class
name|SecureSetting
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Setting
argument_list|<
name|T
argument_list|>
block|{
comment|/** Determines whether legacy settings with sensitive values should be allowed. */
DECL|field|ALLOW_INSECURE_SETTINGS
specifier|private
specifier|static
specifier|final
name|boolean
name|ALLOW_INSECURE_SETTINGS
init|=
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"es.allow_insecure_settings"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|ALLOWED_PROPERTIES
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Property
argument_list|>
name|ALLOWED_PROPERTIES
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|Property
operator|.
name|Deprecated
argument_list|)
decl_stmt|;
DECL|field|FIXED_PROPERTIES
specifier|private
specifier|static
specifier|final
name|Property
index|[]
name|FIXED_PROPERTIES
init|=
block|{
name|Property
operator|.
name|NodeScope
block|}
decl_stmt|;
DECL|method|SecureSetting
specifier|private
name|SecureSetting
parameter_list|(
name|String
name|key
parameter_list|,
name|Property
modifier|...
name|properties
parameter_list|)
block|{
name|super
argument_list|(
name|key
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|,
literal|null
argument_list|,
name|ArrayUtils
operator|.
name|concat
argument_list|(
name|properties
argument_list|,
name|FIXED_PROPERTIES
argument_list|,
name|Property
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|assertAllowedProperties
argument_list|(
name|properties
argument_list|)
assert|;
block|}
DECL|method|assertAllowedProperties
specifier|private
name|boolean
name|assertAllowedProperties
parameter_list|(
name|Setting
operator|.
name|Property
modifier|...
name|properties
parameter_list|)
block|{
for|for
control|(
name|Setting
operator|.
name|Property
name|property
range|:
name|properties
control|)
block|{
if|if
condition|(
name|ALLOWED_PROPERTIES
operator|.
name|contains
argument_list|(
name|property
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultRaw
specifier|public
name|String
name|getDefaultRaw
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"secure settings are not strings"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getDefault
specifier|public
name|T
name|getDefault
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"secure settings are not strings"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getRaw
specifier|public
name|String
name|getRaw
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"secure settings are not strings"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
specifier|final
name|SecureSettings
name|secureSettings
init|=
name|settings
operator|.
name|getSecureSettings
argument_list|()
decl_stmt|;
return|return
name|secureSettings
operator|!=
literal|null
operator|&&
name|secureSettings
operator|.
name|getSettingNames
argument_list|()
operator|.
name|contains
argument_list|(
name|getKey
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|T
name|get
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|checkDeprecation
argument_list|(
name|settings
argument_list|)
expr_stmt|;
specifier|final
name|SecureSettings
name|secureSettings
init|=
name|settings
operator|.
name|getSecureSettings
argument_list|()
decl_stmt|;
if|if
condition|(
name|secureSettings
operator|==
literal|null
operator|||
name|secureSettings
operator|.
name|getSettingNames
argument_list|()
operator|.
name|contains
argument_list|(
name|getKey
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|super
operator|.
name|exists
argument_list|(
name|settings
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Setting ["
operator|+
name|getKey
argument_list|()
operator|+
literal|"] is a secure setting"
operator|+
literal|" and must be stored inside the Elasticsearch keystore, but was found inside elasticsearch.yml"
argument_list|)
throw|;
block|}
return|return
name|getFallback
argument_list|(
name|settings
argument_list|)
return|;
block|}
try|try
block|{
return|return
name|getSecret
argument_list|(
name|secureSettings
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|GeneralSecurityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"failed to read secure setting "
operator|+
name|getKey
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the secret setting from the keyStoreReader store. */
DECL|method|getSecret
specifier|abstract
name|T
name|getSecret
parameter_list|(
name|SecureSettings
name|secureSettings
parameter_list|)
throws|throws
name|GeneralSecurityException
function_decl|;
comment|/** Returns the value from a fallback setting. Returns null if no fallback exists. */
DECL|method|getFallback
specifier|abstract
name|T
name|getFallback
parameter_list|(
name|Settings
name|settings
parameter_list|)
function_decl|;
comment|// TODO: override toXContent
comment|/**      * Overrides the diff operation to make this a no-op for secure settings as they shouldn't be returned in a diff      */
annotation|@
name|Override
DECL|method|diff
specifier|public
name|void
name|diff
parameter_list|(
name|Settings
operator|.
name|Builder
name|builder
parameter_list|,
name|Settings
name|source
parameter_list|,
name|Settings
name|defaultSettings
parameter_list|)
block|{     }
comment|/**      * A setting which contains a sensitive string.      *      * This may be any sensitive string, e.g. a username, a password, an auth token, etc.      */
DECL|method|secureString
specifier|public
specifier|static
name|Setting
argument_list|<
name|SecureString
argument_list|>
name|secureString
parameter_list|(
name|String
name|name
parameter_list|,
name|Setting
argument_list|<
name|SecureString
argument_list|>
name|fallback
parameter_list|,
name|Property
modifier|...
name|properties
parameter_list|)
block|{
return|return
operator|new
name|SecureSetting
argument_list|<
name|SecureString
argument_list|>
argument_list|(
name|name
argument_list|,
name|properties
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|SecureString
name|getSecret
parameter_list|(
name|SecureSettings
name|secureSettings
parameter_list|)
throws|throws
name|GeneralSecurityException
block|{
return|return
name|secureSettings
operator|.
name|getString
argument_list|(
name|getKey
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
name|SecureString
name|getFallback
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
if|if
condition|(
name|fallback
operator|!=
literal|null
condition|)
block|{
return|return
name|fallback
operator|.
name|get
argument_list|(
name|settings
argument_list|)
return|;
block|}
return|return
operator|new
name|SecureString
argument_list|(
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
return|;
comment|// this means "setting does not exist"
block|}
block|}
return|;
block|}
comment|/**      * A setting which contains a sensitive string, but which for legacy reasons must be found outside secure settings.      * @see #secureString(String, Setting, Property...)      */
DECL|method|insecureString
specifier|public
specifier|static
name|Setting
argument_list|<
name|SecureString
argument_list|>
name|insecureString
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Setting
argument_list|<
name|SecureString
argument_list|>
argument_list|(
name|name
argument_list|,
literal|""
argument_list|,
name|SecureString
operator|::
operator|new
argument_list|,
name|Property
operator|.
name|Deprecated
argument_list|,
name|Property
operator|.
name|Filtered
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SecureString
name|get
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
if|if
condition|(
name|ALLOW_INSECURE_SETTINGS
operator|==
literal|false
operator|&&
name|exists
argument_list|(
name|settings
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Setting ["
operator|+
name|name
operator|+
literal|"] is insecure, "
operator|+
literal|"but property [allow_insecure_settings] is not set"
argument_list|)
throw|;
block|}
return|return
name|super
operator|.
name|get
argument_list|(
name|settings
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**      * A setting which contains a file. Reading the setting opens an input stream to the file.      *      * This may be any sensitive file, e.g. a set of credentials normally in plaintext.      */
DECL|method|secureFile
specifier|public
specifier|static
name|Setting
argument_list|<
name|InputStream
argument_list|>
name|secureFile
parameter_list|(
name|String
name|name
parameter_list|,
name|Setting
argument_list|<
name|InputStream
argument_list|>
name|fallback
parameter_list|,
name|Property
modifier|...
name|properties
parameter_list|)
block|{
return|return
operator|new
name|SecureSetting
argument_list|<
name|InputStream
argument_list|>
argument_list|(
name|name
argument_list|,
name|properties
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|InputStream
name|getSecret
parameter_list|(
name|SecureSettings
name|secureSettings
parameter_list|)
throws|throws
name|GeneralSecurityException
block|{
return|return
name|secureSettings
operator|.
name|getFile
argument_list|(
name|getKey
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
name|InputStream
name|getFallback
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
if|if
condition|(
name|fallback
operator|!=
literal|null
condition|)
block|{
return|return
name|fallback
operator|.
name|get
argument_list|(
name|settings
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

