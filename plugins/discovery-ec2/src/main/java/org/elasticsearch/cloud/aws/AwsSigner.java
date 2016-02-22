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
name|ClientConfiguration
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|auth
operator|.
name|SignerFactory
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

begin_class
DECL|class|AwsSigner
specifier|public
class|class
name|AwsSigner
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|AwsSigner
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|AwsSigner
specifier|private
name|AwsSigner
parameter_list|()
block|{      }
DECL|method|validateSignerType
specifier|protected
specifier|static
name|void
name|validateSignerType
parameter_list|(
name|String
name|signer
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|signer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[null] signer set"
argument_list|)
throw|;
block|}
try|try
block|{
comment|// We check this signer actually exists in AWS SDK
comment|// It throws a IllegalArgumentException if not found
name|SignerFactory
operator|.
name|getSignerByTypeAndService
argument_list|(
name|signer
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"wrong signer set ["
operator|+
name|signer
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Add a AWS API Signer.      * @param signer Signer to use      * @param configuration AWS Client configuration      */
DECL|method|configureSigner
specifier|public
specifier|static
name|void
name|configureSigner
parameter_list|(
name|String
name|signer
parameter_list|,
name|ClientConfiguration
name|configuration
parameter_list|)
block|{
try|try
block|{
name|validateSignerType
argument_list|(
name|signer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"{}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|configuration
operator|.
name|setSignerOverride
argument_list|(
name|signer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

