begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch
package|package
name|org
operator|.
name|elasticsearch
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
name|SuppressForbidden
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
name|io
operator|.
name|PathUtils
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

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
name|jar
operator|.
name|JarInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|Manifest
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|Build
specifier|public
class|class
name|Build
block|{
DECL|field|CURRENT
specifier|public
specifier|static
specifier|final
name|Build
name|CURRENT
decl_stmt|;
static|static
block|{
specifier|final
name|String
name|shortHash
decl_stmt|;
specifier|final
name|String
name|date
decl_stmt|;
name|Path
name|path
init|=
name|getElasticsearchCodebase
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|toString
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
condition|)
block|{
try|try
init|(
name|JarInputStream
name|jar
init|=
operator|new
name|JarInputStream
argument_list|(
name|Files
operator|.
name|newInputStream
argument_list|(
name|path
argument_list|)
argument_list|)
init|)
block|{
name|Manifest
name|manifest
init|=
name|jar
operator|.
name|getManifest
argument_list|()
decl_stmt|;
name|shortHash
operator|=
name|manifest
operator|.
name|getMainAttributes
argument_list|()
operator|.
name|getValue
argument_list|(
literal|"Change"
argument_list|)
expr_stmt|;
name|date
operator|=
name|manifest
operator|.
name|getMainAttributes
argument_list|()
operator|.
name|getValue
argument_list|(
literal|"Build-Date"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// not running from a jar (unit tests, IDE)
name|shortHash
operator|=
literal|"Unknown"
expr_stmt|;
name|date
operator|=
literal|"Unknown"
expr_stmt|;
block|}
name|CURRENT
operator|=
operator|new
name|Build
argument_list|(
name|shortHash
argument_list|,
name|date
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns path to elasticsearch codebase path      */
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"looks up path of elasticsearch.jar directly"
argument_list|)
DECL|method|getElasticsearchCodebase
specifier|static
name|Path
name|getElasticsearchCodebase
parameter_list|()
block|{
name|URL
name|url
init|=
name|Build
operator|.
name|class
operator|.
name|getProtectionDomain
argument_list|()
operator|.
name|getCodeSource
argument_list|()
operator|.
name|getLocation
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|PathUtils
operator|.
name|get
argument_list|(
name|url
operator|.
name|toURI
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|bogus
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|bogus
argument_list|)
throw|;
block|}
block|}
DECL|field|shortHash
specifier|private
name|String
name|shortHash
decl_stmt|;
DECL|field|date
specifier|private
name|String
name|date
decl_stmt|;
DECL|method|Build
name|Build
parameter_list|(
name|String
name|shortHash
parameter_list|,
name|String
name|date
parameter_list|)
block|{
name|this
operator|.
name|shortHash
operator|=
name|shortHash
expr_stmt|;
name|this
operator|.
name|date
operator|=
name|date
expr_stmt|;
block|}
DECL|method|shortHash
specifier|public
name|String
name|shortHash
parameter_list|()
block|{
return|return
name|shortHash
return|;
block|}
DECL|method|date
specifier|public
name|String
name|date
parameter_list|()
block|{
return|return
name|date
return|;
block|}
DECL|method|readBuild
specifier|public
specifier|static
name|Build
name|readBuild
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|hash
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|String
name|date
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
return|return
operator|new
name|Build
argument_list|(
name|hash
argument_list|,
name|date
argument_list|)
return|;
block|}
DECL|method|writeBuild
specifier|public
specifier|static
name|void
name|writeBuild
parameter_list|(
name|Build
name|build
parameter_list|,
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeString
argument_list|(
name|build
operator|.
name|shortHash
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|build
operator|.
name|date
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"["
operator|+
name|shortHash
operator|+
literal|"]["
operator|+
name|date
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

