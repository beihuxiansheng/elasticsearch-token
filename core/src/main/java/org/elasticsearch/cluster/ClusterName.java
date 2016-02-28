begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|Setting
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
name|Setting
operator|.
name|SettingsProperty
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ClusterName
specifier|public
class|class
name|ClusterName
implements|implements
name|Streamable
block|{
DECL|field|CLUSTER_NAME_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|String
argument_list|>
name|CLUSTER_NAME_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"cluster.name"
argument_list|,
literal|"elasticsearch"
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|s
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[cluster.name] must not be empty"
argument_list|)
throw|;
block|}
return|return
name|s
return|;
block|}
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|ClusterName
name|DEFAULT
init|=
operator|new
name|ClusterName
argument_list|(
name|CLUSTER_NAME_SETTING
operator|.
name|getDefault
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
operator|.
name|intern
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|value
specifier|private
name|String
name|value
decl_stmt|;
DECL|method|clusterNameFromSettings
specifier|public
specifier|static
name|ClusterName
name|clusterNameFromSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
return|return
operator|new
name|ClusterName
argument_list|(
name|CLUSTER_NAME_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|)
return|;
block|}
DECL|method|ClusterName
specifier|private
name|ClusterName
parameter_list|()
block|{     }
DECL|method|ClusterName
specifier|public
name|ClusterName
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
operator|.
name|intern
argument_list|()
expr_stmt|;
block|}
DECL|method|value
specifier|public
name|String
name|value
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
DECL|method|readClusterName
specifier|public
specifier|static
name|ClusterName
name|readClusterName
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ClusterName
name|clusterName
init|=
operator|new
name|ClusterName
argument_list|()
decl_stmt|;
name|clusterName
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|clusterName
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
name|value
operator|=
name|in
operator|.
name|readString
argument_list|()
operator|.
name|intern
argument_list|()
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
name|out
operator|.
name|writeString
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|ClusterName
name|that
init|=
operator|(
name|ClusterName
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|?
operator|!
name|value
operator|.
name|equals
argument_list|(
name|that
operator|.
name|value
argument_list|)
else|:
name|that
operator|.
name|value
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|value
operator|!=
literal|null
condition|?
name|value
operator|.
name|hashCode
argument_list|()
else|:
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Cluster ["
operator|+
name|value
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

