begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.settings
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|settings
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchGenerationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequestValidationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|master
operator|.
name|AcknowledgedRequest
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|settings
operator|.
name|Settings
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentFactory
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
name|xcontent
operator|.
name|XContentType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ValidateActions
operator|.
name|addValidationError
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
operator|.
name|readSettingsFromStream
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
operator|.
name|writeSettingsToStream
import|;
end_import

begin_comment
comment|/**  * Request for an update cluster settings action  */
end_comment

begin_class
DECL|class|ClusterUpdateSettingsRequest
specifier|public
class|class
name|ClusterUpdateSettingsRequest
extends|extends
name|AcknowledgedRequest
argument_list|<
name|ClusterUpdateSettingsRequest
argument_list|>
block|{
DECL|field|transientSettings
specifier|private
name|Settings
name|transientSettings
init|=
name|EMPTY_SETTINGS
decl_stmt|;
DECL|field|persistentSettings
specifier|private
name|Settings
name|persistentSettings
init|=
name|EMPTY_SETTINGS
decl_stmt|;
DECL|method|ClusterUpdateSettingsRequest
specifier|public
name|ClusterUpdateSettingsRequest
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|validationException
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|transientSettings
operator|.
name|getAsMap
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
name|persistentSettings
operator|.
name|getAsMap
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"no settings to update"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
return|;
block|}
DECL|method|transientSettings
specifier|public
name|Settings
name|transientSettings
parameter_list|()
block|{
return|return
name|transientSettings
return|;
block|}
DECL|method|persistentSettings
specifier|public
name|Settings
name|persistentSettings
parameter_list|()
block|{
return|return
name|persistentSettings
return|;
block|}
comment|/**      * Sets the transient settings to be updated. They will not survive a full cluster restart      */
DECL|method|transientSettings
specifier|public
name|ClusterUpdateSettingsRequest
name|transientSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|transientSettings
operator|=
name|settings
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the transient settings to be updated. They will not survive a full cluster restart      */
DECL|method|transientSettings
specifier|public
name|ClusterUpdateSettingsRequest
name|transientSettings
parameter_list|(
name|Settings
operator|.
name|Builder
name|settings
parameter_list|)
block|{
name|this
operator|.
name|transientSettings
operator|=
name|settings
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the source containing the transient settings to be updated. They will not survive a full cluster restart      */
DECL|method|transientSettings
specifier|public
name|ClusterUpdateSettingsRequest
name|transientSettings
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|this
operator|.
name|transientSettings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|loadFromSource
argument_list|(
name|source
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the transient settings to be updated. They will not survive a full cluster restart      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|transientSettings
specifier|public
name|ClusterUpdateSettingsRequest
name|transientSettings
parameter_list|(
name|Map
name|source
parameter_list|)
block|{
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|transientSettings
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchGenerationException
argument_list|(
literal|"Failed to generate ["
operator|+
name|source
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|this
return|;
block|}
comment|/**      * Sets the persistent settings to be updated. They will get applied cross restarts      */
DECL|method|persistentSettings
specifier|public
name|ClusterUpdateSettingsRequest
name|persistentSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|persistentSettings
operator|=
name|settings
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the persistent settings to be updated. They will get applied cross restarts      */
DECL|method|persistentSettings
specifier|public
name|ClusterUpdateSettingsRequest
name|persistentSettings
parameter_list|(
name|Settings
operator|.
name|Builder
name|settings
parameter_list|)
block|{
name|this
operator|.
name|persistentSettings
operator|=
name|settings
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the source containing the persistent settings to be updated. They will get applied cross restarts      */
DECL|method|persistentSettings
specifier|public
name|ClusterUpdateSettingsRequest
name|persistentSettings
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|this
operator|.
name|persistentSettings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|loadFromSource
argument_list|(
name|source
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the persistent settings to be updated. They will get applied cross restarts      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|persistentSettings
specifier|public
name|ClusterUpdateSettingsRequest
name|persistentSettings
parameter_list|(
name|Map
name|source
parameter_list|)
block|{
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|persistentSettings
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchGenerationException
argument_list|(
literal|"Failed to generate ["
operator|+
name|source
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|transientSettings
operator|=
name|readSettingsFromStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|persistentSettings
operator|=
name|readSettingsFromStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|readTimeout
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|writeSettingsToStream
argument_list|(
name|transientSettings
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|writeSettingsToStream
argument_list|(
name|persistentSettings
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|writeTimeout
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

