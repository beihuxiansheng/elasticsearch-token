begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.metadata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
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
name|Property
import|;
end_import

begin_comment
comment|/**  * This class acts as a functional wrapper around the<tt>index.auto_expand_replicas</tt> setting.  * This setting or rather it's value is expanded into a min and max value which requires special handling  * based on the number of datanodes in the cluster. This class handles all the parsing and streamlines the access to these values.  */
end_comment

begin_class
DECL|class|AutoExpandReplicas
specifier|final
class|class
name|AutoExpandReplicas
block|{
comment|// the value we recognize in the "max" position to mean all the nodes
DECL|field|ALL_NODES_VALUE
specifier|private
specifier|static
specifier|final
name|String
name|ALL_NODES_VALUE
init|=
literal|"all"
decl_stmt|;
DECL|field|SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|AutoExpandReplicas
argument_list|>
name|SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_AUTO_EXPAND_REPLICAS
argument_list|,
literal|"false"
argument_list|,
parameter_list|(
name|value
parameter_list|)
lambda|->
block|{
specifier|final
name|int
name|min
decl_stmt|;
specifier|final
name|int
name|max
decl_stmt|;
if|if
condition|(
name|Booleans
operator|.
name|isFalse
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
operator|new
name|AutoExpandReplicas
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|final
name|int
name|dash
init|=
name|value
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
decl_stmt|;
if|if
condition|(
operator|-
literal|1
operator|==
name|dash
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"failed to parse ["
operator|+
name|IndexMetaData
operator|.
name|SETTING_AUTO_EXPAND_REPLICAS
operator|+
literal|"] from value: ["
operator|+
name|value
operator|+
literal|"] at index "
operator|+
name|dash
argument_list|)
throw|;
block|}
specifier|final
name|String
name|sMin
init|=
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dash
argument_list|)
decl_stmt|;
try|try
block|{
name|min
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|sMin
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"failed to parse ["
operator|+
name|IndexMetaData
operator|.
name|SETTING_AUTO_EXPAND_REPLICAS
operator|+
literal|"] from value: ["
operator|+
name|value
operator|+
literal|"] at index "
operator|+
name|dash
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|String
name|sMax
init|=
name|value
operator|.
name|substring
argument_list|(
name|dash
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|sMax
operator|.
name|equals
argument_list|(
name|ALL_NODES_VALUE
argument_list|)
condition|)
block|{
name|max
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|max
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|sMax
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"failed to parse ["
operator|+
name|IndexMetaData
operator|.
name|SETTING_AUTO_EXPAND_REPLICAS
operator|+
literal|"] from value: ["
operator|+
name|value
operator|+
literal|"] at index "
operator|+
name|dash
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|AutoExpandReplicas
argument_list|(
name|min
argument_list|,
name|max
argument_list|,
literal|true
argument_list|)
return|;
block|}
argument_list|,
name|Property
operator|.
name|Dynamic
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
DECL|field|minReplicas
specifier|private
specifier|final
name|int
name|minReplicas
decl_stmt|;
DECL|field|maxReplicas
specifier|private
specifier|final
name|int
name|maxReplicas
decl_stmt|;
DECL|field|enabled
specifier|private
specifier|final
name|boolean
name|enabled
decl_stmt|;
DECL|method|AutoExpandReplicas
specifier|private
name|AutoExpandReplicas
parameter_list|(
name|int
name|minReplicas
parameter_list|,
name|int
name|maxReplicas
parameter_list|,
name|boolean
name|enabled
parameter_list|)
block|{
if|if
condition|(
name|minReplicas
operator|>
name|maxReplicas
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"["
operator|+
name|IndexMetaData
operator|.
name|SETTING_AUTO_EXPAND_REPLICAS
operator|+
literal|"] minReplicas must be =< maxReplicas but wasn't "
operator|+
name|minReplicas
operator|+
literal|"> "
operator|+
name|maxReplicas
argument_list|)
throw|;
block|}
name|this
operator|.
name|minReplicas
operator|=
name|minReplicas
expr_stmt|;
name|this
operator|.
name|maxReplicas
operator|=
name|maxReplicas
expr_stmt|;
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
DECL|method|getMinReplicas
name|int
name|getMinReplicas
parameter_list|()
block|{
return|return
name|minReplicas
return|;
block|}
DECL|method|getMaxReplicas
name|int
name|getMaxReplicas
parameter_list|(
name|int
name|numDataNodes
parameter_list|)
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|maxReplicas
argument_list|,
name|numDataNodes
operator|-
literal|1
argument_list|)
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
name|enabled
condition|?
name|minReplicas
operator|+
literal|"-"
operator|+
name|maxReplicas
else|:
literal|"false"
return|;
block|}
DECL|method|isEnabled
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|enabled
return|;
block|}
block|}
end_class

end_unit

