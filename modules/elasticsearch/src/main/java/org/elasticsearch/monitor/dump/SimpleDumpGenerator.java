begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.monitor.dump
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|dump
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
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
name|Nullable
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
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|SimpleDumpGenerator
specifier|public
class|class
name|SimpleDumpGenerator
implements|implements
name|DumpGenerator
block|{
DECL|field|dumpLocation
specifier|private
specifier|final
name|File
name|dumpLocation
decl_stmt|;
DECL|field|contributors
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|DumpContributor
argument_list|>
name|contributors
decl_stmt|;
DECL|method|SimpleDumpGenerator
specifier|public
name|SimpleDumpGenerator
parameter_list|(
name|File
name|dumpLocation
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|DumpContributor
argument_list|>
name|contributors
parameter_list|)
block|{
name|this
operator|.
name|dumpLocation
operator|=
name|dumpLocation
expr_stmt|;
name|this
operator|.
name|contributors
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|contributors
argument_list|)
expr_stmt|;
block|}
DECL|method|generateDump
specifier|public
name|Result
name|generateDump
parameter_list|(
name|String
name|cause
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|context
parameter_list|)
throws|throws
name|DumpGenerationFailedException
block|{
return|return
name|generateDump
argument_list|(
name|cause
argument_list|,
name|context
argument_list|,
name|contributors
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|contributors
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
DECL|method|generateDump
specifier|public
name|Result
name|generateDump
parameter_list|(
name|String
name|cause
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|context
parameter_list|,
name|String
modifier|...
name|contributors
parameter_list|)
throws|throws
name|DumpGenerationFailedException
block|{
name|long
name|timestamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|fileName
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|containsKey
argument_list|(
literal|"localNode"
argument_list|)
condition|)
block|{
name|DiscoveryNode
name|localNode
init|=
operator|(
name|DiscoveryNode
operator|)
name|context
operator|.
name|get
argument_list|(
literal|"localNode"
argument_list|)
decl_stmt|;
if|if
condition|(
name|localNode
operator|.
name|name
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|fileName
operator|+=
name|localNode
operator|.
name|name
argument_list|()
operator|+
literal|"-"
expr_stmt|;
block|}
name|fileName
operator|+=
name|localNode
operator|.
name|id
argument_list|()
operator|+
literal|"-"
expr_stmt|;
block|}
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|dumpLocation
argument_list|,
name|fileName
operator|+
name|cause
operator|+
literal|"-"
operator|+
name|timestamp
argument_list|)
decl_stmt|;
name|file
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|SimpleDump
name|dump
decl_stmt|;
try|try
block|{
name|dump
operator|=
operator|new
name|SimpleDump
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|cause
argument_list|,
name|context
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DumpGenerationFailedException
argument_list|(
literal|"Failed to generate dump"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|ArrayList
argument_list|<
name|DumpContributionFailedException
argument_list|>
name|failedContributors
init|=
operator|new
name|ArrayList
argument_list|<
name|DumpContributionFailedException
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|contributors
control|)
block|{
name|DumpContributor
name|contributor
init|=
name|this
operator|.
name|contributors
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|contributor
operator|==
literal|null
condition|)
block|{
name|failedContributors
operator|.
name|add
argument_list|(
operator|new
name|DumpContributionFailedException
argument_list|(
name|name
argument_list|,
literal|"No contributor"
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
try|try
block|{
name|contributor
operator|.
name|contribute
argument_list|(
name|dump
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DumpContributionFailedException
name|e
parameter_list|)
block|{
name|failedContributors
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|failedContributors
operator|.
name|add
argument_list|(
operator|new
name|DumpContributionFailedException
argument_list|(
name|contributor
operator|.
name|getName
argument_list|()
argument_list|,
literal|"Failed"
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|dump
operator|.
name|finish
argument_list|()
expr_stmt|;
return|return
operator|new
name|Result
argument_list|(
name|file
argument_list|,
name|failedContributors
argument_list|)
return|;
block|}
block|}
end_class

end_unit

