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

begin_comment
comment|/**  * An Abstract Processor that holds a processorTag field to be used  * by other processors.  */
end_comment

begin_class
DECL|class|AbstractProcessor
specifier|public
specifier|abstract
class|class
name|AbstractProcessor
implements|implements
name|Processor
block|{
DECL|field|tag
specifier|protected
specifier|final
name|String
name|tag
decl_stmt|;
DECL|method|AbstractProcessor
specifier|protected
name|AbstractProcessor
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
name|this
operator|.
name|tag
operator|=
name|tag
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTag
specifier|public
name|String
name|getTag
parameter_list|()
block|{
return|return
name|tag
return|;
block|}
block|}
end_class

end_unit

