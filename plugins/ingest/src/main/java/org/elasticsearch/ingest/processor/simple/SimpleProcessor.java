begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.processor.simple
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|processor
operator|.
name|simple
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|Data
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|processor
operator|.
name|Processor
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

begin_class
DECL|class|SimpleProcessor
specifier|public
specifier|final
class|class
name|SimpleProcessor
implements|implements
name|Processor
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"simple"
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|expectedValue
specifier|private
specifier|final
name|String
name|expectedValue
decl_stmt|;
DECL|field|addField
specifier|private
specifier|final
name|String
name|addField
decl_stmt|;
DECL|field|addFieldValue
specifier|private
specifier|final
name|String
name|addFieldValue
decl_stmt|;
DECL|method|SimpleProcessor
specifier|public
name|SimpleProcessor
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|expectedValue
parameter_list|,
name|String
name|addField
parameter_list|,
name|String
name|addFieldValue
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|expectedValue
operator|=
name|expectedValue
expr_stmt|;
name|this
operator|.
name|addField
operator|=
name|addField
expr_stmt|;
name|this
operator|.
name|addFieldValue
operator|=
name|addFieldValue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|Data
name|data
parameter_list|)
block|{
name|Object
name|value
init|=
name|data
operator|.
name|getProperty
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|value
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|expectedValue
argument_list|)
condition|)
block|{
name|data
operator|.
name|addField
argument_list|(
name|addField
argument_list|,
name|addFieldValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
implements|implements
name|Processor
operator|.
name|Builder
block|{
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|expectedValue
specifier|private
name|String
name|expectedValue
decl_stmt|;
DECL|field|addField
specifier|private
name|String
name|addField
decl_stmt|;
DECL|field|addFieldValue
specifier|private
name|String
name|addFieldValue
decl_stmt|;
DECL|method|setPath
specifier|public
name|void
name|setPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
DECL|method|setExpectedValue
specifier|public
name|void
name|setExpectedValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|expectedValue
operator|=
name|value
expr_stmt|;
block|}
DECL|method|setAddField
specifier|public
name|void
name|setAddField
parameter_list|(
name|String
name|addField
parameter_list|)
block|{
name|this
operator|.
name|addField
operator|=
name|addField
expr_stmt|;
block|}
DECL|method|setAddFieldValue
specifier|public
name|void
name|setAddFieldValue
parameter_list|(
name|String
name|addFieldValue
parameter_list|)
block|{
name|this
operator|.
name|addFieldValue
operator|=
name|addFieldValue
expr_stmt|;
block|}
DECL|method|fromMap
specifier|public
name|void
name|fromMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
expr_stmt|;
name|this
operator|.
name|expectedValue
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
literal|"expected_value"
argument_list|)
expr_stmt|;
name|this
operator|.
name|addField
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
literal|"add_field"
argument_list|)
expr_stmt|;
name|this
operator|.
name|addFieldValue
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|get
argument_list|(
literal|"add_field_value"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|Processor
name|build
parameter_list|()
block|{
return|return
operator|new
name|SimpleProcessor
argument_list|(
name|path
argument_list|,
name|expectedValue
argument_list|,
name|addField
argument_list|,
name|addFieldValue
argument_list|)
return|;
block|}
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
implements|implements
name|Processor
operator|.
name|Builder
operator|.
name|Factory
block|{
annotation|@
name|Override
DECL|method|create
specifier|public
name|Processor
operator|.
name|Builder
name|create
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

