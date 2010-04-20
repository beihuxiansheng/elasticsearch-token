begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.component
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|component
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|logging
operator|.
name|ESLogger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|logging
operator|.
name|Loggers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|AbstractComponent
specifier|public
class|class
name|AbstractComponent
block|{
DECL|field|logger
specifier|protected
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|field|settings
specifier|protected
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|field|componentSettings
specifier|protected
specifier|final
name|Settings
name|componentSettings
decl_stmt|;
DECL|method|AbstractComponent
specifier|public
name|AbstractComponent
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|logger
operator|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|this
operator|.
name|componentSettings
operator|=
name|settings
operator|.
name|getComponentSettings
argument_list|(
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|AbstractComponent
specifier|public
name|AbstractComponent
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|String
name|prefixSettings
parameter_list|)
block|{
name|this
operator|.
name|logger
operator|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|this
operator|.
name|componentSettings
operator|=
name|settings
operator|.
name|getComponentSettings
argument_list|(
name|prefixSettings
argument_list|,
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|AbstractComponent
specifier|public
name|AbstractComponent
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Class
name|customClass
parameter_list|)
block|{
name|this
operator|.
name|logger
operator|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|customClass
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|this
operator|.
name|componentSettings
operator|=
name|settings
operator|.
name|getComponentSettings
argument_list|(
name|customClass
argument_list|)
expr_stmt|;
block|}
DECL|method|AbstractComponent
specifier|public
name|AbstractComponent
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|String
name|prefixSettings
parameter_list|,
name|Class
name|customClass
parameter_list|)
block|{
name|this
operator|.
name|logger
operator|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|customClass
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|this
operator|.
name|componentSettings
operator|=
name|settings
operator|.
name|getComponentSettings
argument_list|(
name|prefixSettings
argument_list|,
name|customClass
argument_list|)
expr_stmt|;
block|}
DECL|method|AbstractComponent
specifier|public
name|AbstractComponent
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Class
name|loggerClass
parameter_list|,
name|Class
name|componentClass
parameter_list|)
block|{
name|this
operator|.
name|logger
operator|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|loggerClass
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|this
operator|.
name|componentSettings
operator|=
name|settings
operator|.
name|getComponentSettings
argument_list|(
name|componentClass
argument_list|)
expr_stmt|;
block|}
DECL|method|AbstractComponent
specifier|public
name|AbstractComponent
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|String
name|prefixSettings
parameter_list|,
name|Class
name|loggerClass
parameter_list|,
name|Class
name|componentClass
parameter_list|)
block|{
name|this
operator|.
name|logger
operator|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|loggerClass
argument_list|,
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|this
operator|.
name|componentSettings
operator|=
name|settings
operator|.
name|getComponentSettings
argument_list|(
name|prefixSettings
argument_list|,
name|componentClass
argument_list|)
expr_stmt|;
block|}
DECL|method|nodeName
specifier|public
name|String
name|nodeName
parameter_list|()
block|{
return|return
name|settings
operator|.
name|get
argument_list|(
literal|"name"
argument_list|,
literal|""
argument_list|)
return|;
block|}
block|}
end_class

end_unit

