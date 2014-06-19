begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cloud.aws
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|aws
package|;
end_package

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|AmazonS3
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
name|component
operator|.
name|LifecycleComponent
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|AwsS3Service
specifier|public
interface|interface
name|AwsS3Service
extends|extends
name|LifecycleComponent
argument_list|<
name|AwsS3Service
argument_list|>
block|{
DECL|method|client
name|AmazonS3
name|client
parameter_list|()
function_decl|;
DECL|method|client
name|AmazonS3
name|client
parameter_list|(
name|String
name|region
parameter_list|,
name|String
name|account
parameter_list|,
name|String
name|key
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

