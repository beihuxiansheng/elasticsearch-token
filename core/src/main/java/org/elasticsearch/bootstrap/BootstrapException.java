begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.bootstrap
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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

begin_comment
comment|/**  * Wrapper exception for checked exceptions thrown during the bootstrap process. Methods invoked  * during bootstrap should explicitly declare the checked exceptions that they can throw, rather  * than declaring the top-level checked exception {@link Exception}. This exception exists to wrap  * these checked exceptions so that {@link Bootstrap#init(boolean, Path, Map)} does not have to  * declare all of these checked exceptions.  */
end_comment

begin_class
DECL|class|BootstrapException
class|class
name|BootstrapException
extends|extends
name|Exception
block|{
comment|/**      * Wraps an existing exception.      *      * @param cause the underlying cause of bootstrap failing      */
DECL|method|BootstrapException
name|BootstrapException
parameter_list|(
specifier|final
name|Exception
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

