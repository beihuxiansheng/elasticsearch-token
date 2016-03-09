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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * This class starts elasticsearch.  */
end_comment

begin_class
DECL|class|Elasticsearch
specifier|public
specifier|final
class|class
name|Elasticsearch
block|{
comment|/** no instantiation */
DECL|method|Elasticsearch
specifier|private
name|Elasticsearch
parameter_list|()
block|{}
comment|/**      * Main entry point for starting elasticsearch      */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|Bootstrap
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// format exceptions to the console in a special way
comment|// to avoid 2MB stacktraces from guice, etc.
throw|throw
operator|new
name|StartupError
argument_list|(
name|t
argument_list|)
throw|;
block|}
block|}
comment|/**      * Required method that's called by Apache Commons procrun when      * running as a service on Windows, when the service is stopped.      *      * http://commons.apache.org/proper/commons-daemon/procrun.html      *      * NOTE: If this method is renamed and/or moved, make sure to update service.bat!      */
DECL|method|close
specifier|static
name|void
name|close
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
block|{
name|Bootstrap
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

