begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|IngestPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
import|;
end_import

begin_comment
comment|/**  * Adds an ingest processor to be used in tests.  */
end_comment

begin_class
DECL|class|IngestTestPlugin
specifier|public
class|class
name|IngestTestPlugin
extends|extends
name|Plugin
implements|implements
name|IngestPlugin
block|{
annotation|@
name|Override
DECL|method|getProcessors
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Processor
operator|.
name|Factory
argument_list|>
name|getProcessors
parameter_list|(
name|Processor
operator|.
name|Parameters
name|parameters
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"test"
argument_list|,
parameter_list|(
name|factories
parameter_list|,
name|tag
parameter_list|,
name|config
parameter_list|)
lambda|->
operator|new
name|TestProcessor
argument_list|(
literal|"id"
argument_list|,
literal|"test"
argument_list|,
name|doc
lambda|->
block|{
name|doc
operator|.
name|setFieldValue
argument_list|(
literal|"processed"
argument_list|,
literal|true
argument_list|)
argument_list|;                 if
operator|(
name|doc
operator|.
name|hasField
argument_list|(
literal|"fail"
argument_list|)
operator|&&
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"fail"
argument_list|,
name|Boolean
operator|.
name|class
argument_list|)
operator|)
block|{
throw|throw
argument_list|new
name|IllegalArgumentException
argument_list|(
literal|"test processor failed"
argument_list|)
block|;                 }
block|}
block|)
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

unit|} }
end_unit

