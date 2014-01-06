begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
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
name|common
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
name|index
operator|.
name|settings
operator|.
name|IndexSettings
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AbstractIndexComponent
specifier|public
specifier|abstract
class|class
name|AbstractIndexComponent
implements|implements
name|IndexComponent
block|{
DECL|field|logger
specifier|protected
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|field|index
specifier|protected
specifier|final
name|Index
name|index
decl_stmt|;
DECL|field|indexSettings
specifier|protected
specifier|final
name|Settings
name|indexSettings
decl_stmt|;
DECL|field|componentSettings
specifier|protected
specifier|final
name|Settings
name|componentSettings
decl_stmt|;
comment|/**      * Constructs a new index component, with the index name and its settings.      *      * @param index         The index name      * @param indexSettings The index settings      */
DECL|method|AbstractIndexComponent
specifier|protected
name|AbstractIndexComponent
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|indexSettings
operator|=
name|indexSettings
expr_stmt|;
name|this
operator|.
name|componentSettings
operator|=
name|indexSettings
operator|.
name|getComponentSettings
argument_list|(
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
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
name|indexSettings
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new index component, with the index name and its settings, as well as settings prefix.      *      * @param index          The index name      * @param indexSettings  The index settings      * @param prefixSettings A settings prefix (like "com.mycompany") to simplify extracting the component settings      */
DECL|method|AbstractIndexComponent
specifier|protected
name|AbstractIndexComponent
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|String
name|prefixSettings
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|indexSettings
operator|=
name|indexSettings
expr_stmt|;
name|this
operator|.
name|componentSettings
operator|=
name|indexSettings
operator|.
name|getComponentSettings
argument_list|(
name|prefixSettings
argument_list|,
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
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
name|indexSettings
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|index
specifier|public
name|Index
name|index
parameter_list|()
block|{
return|return
name|this
operator|.
name|index
return|;
block|}
DECL|method|nodeName
specifier|public
name|String
name|nodeName
parameter_list|()
block|{
return|return
name|indexSettings
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

